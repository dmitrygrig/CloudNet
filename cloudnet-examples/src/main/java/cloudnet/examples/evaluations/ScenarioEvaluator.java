/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cloudnet.examples.evaluations;

import cloudnet.cooling.AirCoolingModel;
import cloudnet.cooling.CoolingModel;
import cloudnet.cooling.MechanicalCoolingModel;
import cloudnet.cooling.MixedCoolingModel;
import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.core.Pm;
import cloudnet.core.TimeFrame;
import cloudnet.elasticity.ElasticityManager;
import cloudnet.elasticity.ElasticityManagerFirstFitOptimistic;
import cloudnet.elasticity.ElasticityManagerFirstFitPesimistic;
import cloudnet.elasticity.ElasticityManagerInefficient;
import cloudnet.examples.elasticity.bn.ElasiticityManagerMCDA;
import cloudnet.iaas.IaaSCloud;
import cloudnet.iaas.IaaSScheduler;
import cloudnet.iaas.VmGeneratorOnce;
import cloudnet.locations.Location;
import cloudnet.locations.Oslo;
import cloudnet.locations.RioDeJaneiro;
import cloudnet.locations.Tokyo;
import cloudnet.locations.Toronto;
import cloudnet.locations.Vienna;
import cloudnet.monitoring.CsvHistoryWriter;
import cloudnet.monitoring.PassiveMonitoringSystem;
import cloudnet.pm.PmSpecPowerHpProLiantMl110G3PentiumD930;
import cloudnet.poweroutage.NoPowerOutageModel;
import cloudnet.poweroutage.ProbabilityPowerOutageModel;
import cloudnet.provisioners.GreedyProvisioner;
import cloudnet.sim.Scheduler;
import cloudnet.sim.SimClock;
import cloudnet.sim.SimEngine;
import cloudnet.sim.SimEngineDates;
import cloudnet.util.Ensure;
import cloudnet.util.FileUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class ScenarioEvaluator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioEvaluator.class);

    public static final String EM_FFO = "ffo";
    public static final String EM_UE = "ue";
    public static final String EM_FFP = "ffp";
    public static final String EM_BN = "bn";

    public void evaluate(Scenario... scenarios) {
        long startTime = System.currentTimeMillis();
        saveResult("Scenario\tElasticityManager\tMigrationPolicy\tViolations\tMigrations\t"
                + "EnergyCosts\tPenaltyCosts\tTotalCosts\tAvg. VM Availability\tElapsedTime\tOutputDir", startTime);
        evaluateScenarios(Arrays.asList(scenarios), startTime);
    }

    protected void evaluateScenarios(List<Scenario> scenarios, long startTime) {
        for (Scenario scenario : scenarios) {
            System.gc();
            evaluateScenario(scenario, startTime);
        }
    }

    protected String evaluateScenario(Scenario sc, long startTime) {
        Ensure.NotNull(sc, "scenario");
        Ensure.NotNull(sc.getEm(), "Elasticity Manager");

        System.out.printf("Start %s...\r\n", sc.toString());

        // create output
        String outDir = "resources/out/sim/"
                + startTime + "/"
                + sc.getName() + "/"
                + System.currentTimeMillis() + "_"
                + sc.getEm()
                + "_" + sc.getMigrPolicy().toString()
                + "_" + (sc.getPredStrategy() == null ? "no" : sc.getPredStrategy().toString()) + "/";

        // save configuration
        FileUtil.writeToFile(outDir + "conf.dat", sc.toString(), false);

        // Create clock
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(sc.getFrom().get(Calendar.YEAR),
                sc.getFrom().get(Calendar.MONTH),
                sc.getFrom().get(Calendar.DAY_OF_MONTH),
                sc.getFrom().get(Calendar.HOUR_OF_DAY),
                sc.getFrom().get(Calendar.MINUTE),
                sc.getFrom().get(Calendar.SECOND));
        SimClock clock = new SimClock(sc.getSimStep(), calendar.getTimeInMillis());
        calendar.set(sc.getTill().get(Calendar.YEAR),
                sc.getTill().get(Calendar.MONTH),
                sc.getTill().get(Calendar.DAY_OF_MONTH),
                sc.getTill().get(Calendar.HOUR_OF_DAY),
                sc.getTill().get(Calendar.MINUTE),
                sc.getTill().get(Calendar.SECOND));

        // Create em
        ElasticityManager elasticityManager = null;
        switch (sc.getEm()) {
            case EM_FFO:
                elasticityManager = new ElasticityManagerFirstFitOptimistic(sc.getMigrPolicy());
                break;
            case EM_FFP:
                elasticityManager = new ElasticityManagerFirstFitPesimistic(sc.getMigrPolicy());
                break;
            case EM_BN:
                elasticityManager = new ElasiticityManagerMCDA(clock,
                        sc.getPredStrategy(), sc.getMigrPolicy());
                break;
            case EM_UE:
                elasticityManager = new ElasticityManagerInefficient();
                break;
                
            default: throw new IllegalArgumentException("EM Type not supported yet: " + sc.getEm());
        }

        // Create cloud
        Cloud cloud = new IaaSCloud(1, clock);
        
        // attach em
        cloud.attachPlugin(elasticityManager);

        // Attach monitor
        cloud.attachPlugin(new PassiveMonitoringSystem(new CsvHistoryWriter(
                outDir + "cloud.csv",
                outDir + "dcs.csv",
                outDir + "pms.csv",
                outDir + "vms.csv",
                1000,
                false)));

        // Create datacenters
        List<Datacenter> datacenters = getDatacenters(clock, sc.isHasPowerOutage(), sc.getPmPerDcNum());
        if (sc.isInverseDatacenters()) {
            for (int i = sc.getDcNum() - 1; i >= 0; i--) {
                cloud.addDatacenter(datacenters.get(i));
            }
        } else {
            for (int i = 0; i < sc.getDcNum(); i++) {
                cloud.addDatacenter(datacenters.get(i));
            }
        }

        // Create cloud simulator
        VmGeneratorOnce vmGenerator = new VmGeneratorOnce(sc.getVmSpec(), sc.getVmNum());
        vmGenerator.setBwWorkloadModel(sc.getBwWorkloadModel());
        vmGenerator.setCpuWorkloadModel(sc.getCpuWorkloadModel());
        vmGenerator.setRamWorkloadModel(sc.getRamWorkloadModel());
        vmGenerator.setSizeWorkloadModel(sc.getSizeWorkloadModel());
        Scheduler scheduler = new IaaSScheduler(vmGenerator);
        SimEngine engine = new SimEngineDates(clock, scheduler, cloud, calendar.getTimeInMillis());

        long currentTime = System.currentTimeMillis();

        // Perform simulation
        engine.start();
        engine.stop();

        // print results
        LOGGER.info(String.format("Violations: %d/%d", cloud.getViolationCount(), sc.getVmNum() * engine.getElapsedSteps()));
        LOGGER.info(String.format("Energy Costs: %.4f", cloud.getEnergyCosts()));
        LOGGER.info(String.format("Penalty Costs: %.4f", cloud.getSlaPenaltyCosts()));
        LOGGER.info(String.format("Total Costs: %.4f", cloud.getCosts()));

        double vmRequestedRunningTime = cloud.getVms().stream().mapToDouble(x -> x.getRequestedRunningTime()).sum();
        double vmRunningTime = cloud.getVms().stream().mapToDouble(x -> x.getRunningTime()).sum();
        double vmAvailability = 100.0 * vmRunningTime / vmRequestedRunningTime;

        String result = String.format("%s\t%s\t%s\t%d/%d/%d\t%d\t%.4f\t%.4f\t%.4f\t%.2f\t%d\t%s",
                sc.getName(),
                sc.getEm(),
                sc.getMigrPolicy().toString(),
                cloud.getShortViolationCount(), cloud.getViolationCount(), sc.getVmNum() * engine.getElapsedSteps(),
                cloud.getVmMigrationCount(),
                cloud.getEnergyCosts(),
                cloud.getSlaPenaltyCosts(),
                cloud.getCosts(),
                vmAvailability,
                System.currentTimeMillis() - currentTime,
                outDir);

        // print result
        System.out.println("Result:" + result);

        // save result
        saveResult(result, startTime);

        return result;
    }

    private List<Datacenter> getDatacenters(SimClock clock, boolean hasPowerOutages, int pmPerDc) {
        List<Datacenter> datacenters = new ArrayList<>();
        datacenters.add(createDatacenter(1, clock, new Tokyo(), new MechanicalCoolingModel(), hasPowerOutages, pmPerDc));
        datacenters.add(createDatacenter(2, clock, new Vienna(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel()), hasPowerOutages, pmPerDc));
        datacenters.add(createDatacenter(3, clock, new Oslo(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel()), hasPowerOutages, pmPerDc));
        datacenters.add(createDatacenter(4, clock, new Toronto(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel()), hasPowerOutages, pmPerDc));
        datacenters.add(createDatacenter(5, clock, new RioDeJaneiro(), new MechanicalCoolingModel(), hasPowerOutages, pmPerDc));
        return datacenters;
    }

    private Datacenter createDatacenter(int id, SimClock clock, Location location, CoolingModel model, boolean hasPowerOutages, int pmPerDc) {
        Datacenter dc = Datacenter.forLocation(id, clock, location);
        dc.setCoolingModel(model);
        if (hasPowerOutages) {
            dc.setPowerOutage(new ProbabilityPowerOutageModel(location.getCAIDI(), location.getSAIFI(), TimeFrame.Month));
        } else {
            dc.setPowerOutage(new NoPowerOutageModel());
        }
        createPmsForDc(dc, clock, pmPerDc);
        LOGGER.trace("%s created in %s.", dc, location.getDescription());
        return dc;
    }

    private void createPmsForDc(Datacenter dc, SimClock clock, int pmPerDc) {
        for (int i = 0; i < pmPerDc; i++) {
            Pm pm = createDefaultPm((dc.getId() - 1) * pmPerDc + i + 1, clock);
            dc.addPm(pm);
            LOGGER.trace("%s created for %s.", pm, dc);
        }
    }

    private Pm createDefaultPm(int id, SimClock clock) {
        Pm pm = new Pm(id, clock, new PmSpecPowerHpProLiantMl110G3PentiumD930());
        pm.setMipsProvisioner(new GreedyProvisioner());
        pm.setRamProvisioner(new GreedyProvisioner());
        pm.setSizeProvisioner(new GreedyProvisioner());
        pm.setBwProvisioner(new GreedyProvisioner());
        return pm;
    }

    public void saveResult(String res, long time) {
        String filename = "resources/out/sim/" + time + "/" + time + ".dat";
        FileUtil.writeToFile(filename, res, true);
    }
}
