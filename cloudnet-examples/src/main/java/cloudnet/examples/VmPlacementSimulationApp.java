/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cloudnet.examples;

import cloudnet.cooling.AirCoolingModel;
import cloudnet.cooling.CoolingModel;
import cloudnet.cooling.MechanicalCoolingModel;
import cloudnet.cooling.MixedCoolingModel;
import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.core.Pm;
import cloudnet.core.TimeFrame;
import cloudnet.core.VmSpec;
import cloudnet.elasticity.AlwaysVmMigrationPolicy;
import cloudnet.elasticity.ElasticityManager;
import cloudnet.elasticity.ElasticityManagerFirstFitOptimistic;
import cloudnet.elasticity.ElasticityManagerFirstFitPesimistic;
import cloudnet.examples.bn.ElasiticityManagerMCDA;
import cloudnet.iaas.IaaSCloud;
import cloudnet.iaas.IaaSScheduler;
import cloudnet.iaas.VmGenerator;
import cloudnet.iaas.VmGeneratorOnce;
import cloudnet.locations.Location;
import cloudnet.examples.locations.Oslo;
import cloudnet.examples.locations.RioDeJaneiro;
import cloudnet.examples.locations.Tokyo;
import cloudnet.examples.locations.Toronto;
import cloudnet.examples.locations.Vienna;
import cloudnet.monitoring.CsvHistoryWriter;
import cloudnet.monitoring.PassiveAsyncMonitoringSystem;
import cloudnet.pm.PmSpecPowerHpProLiantMl110G3PentiumD930;
import cloudnet.provisioners.GreedyProvisioner;
import cloudnet.sim.Scheduler;
import cloudnet.sim.SimClock;
import cloudnet.sim.SimEngine;
import cloudnet.sim.SimEngineDates;
import cloudnet.vm.azure.VmSpecAzureA0;
import cloudnet.vm.azure.VmSpecAzureA1;
import cloudnet.vm.azure.VmSpecAzureA2;
import cloudnet.vm.azure.VmSpecAzureA3;
import cloudnet.vm.azure.VmSpecAzureA4;
import cloudnet.vm.azure.VmSpecAzureA5;
import cloudnet.vm.azure.VmSpecAzureA6;
import cloudnet.vm.azure.VmSpecAzureA7;
import cloudnet.vm.azure.VmSpecAzureA8;
import cloudnet.workloads.prediction.SimplePredictionStrategy;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmPlacementSimulationApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(VmPlacementSimulationApp.class);

    // options
    private static int vmNum = 10;
    private static int pmPerDcNum = 2;
    private static int iterNum = 1440;
    private static long simStep = 5 * TimeFrame.Minute;
    private static String em = "ff";
    private static VmSpec vmSpec = new VmSpecAzureA0();

    // helpers
    private static int pmIdCounter = 1;
    private static int dcIdCounter = 1;

    public static void main(String[] args) {

        System.out.println("Working Directory = "
                + System.getProperty("user.dir"));

        Options options = makeOptions();
        CommandLineParser parser = new DefaultParser();
        try {

            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("vmPlacementSimApp", options);
                return;
            }

            // apply options
            applyOptions(cmd);

            // run simulation
            runSimulation();

        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }

    }

    private static Options makeOptions() {
        Options options = new Options();
        options
                .addOption("em", "elasticityManager", true, "Elasticity manager: ff - first fit, bn - bayesian network (default ff)")
                .addOption("vm", "vmNum", true, "Number of scheduled vms (default 50)")
                .addOption("vs", "vmSpec", true, "Vm Specifications used by vm scheduler: a0, a1,..,a8  (default a1)")
                .addOption("pm", "pmPerDcNum", true, "Number of allocated pms per each datacenter (default 5)")
                .addOption("s", "simStep", true, "Simulation step: sec,min,hour,day (default min)")
                .addOption("i", "iterNum", true, "Number of iterations (default 1440)")
                .addOption("h", "help", false, "Prints option help.");
        return options;
    }

    private static void applyOptions(CommandLine cmd) {
        if (cmd.hasOption("vmNum")) {
            vmNum = Integer.parseInt(cmd.getOptionValue("vmNum"));
        }
        if (cmd.hasOption("pmPerDcNum")) {
            pmPerDcNum = Integer.parseInt(cmd.getOptionValue("pmPerDcNum"));
        }
        if (cmd.hasOption("iterNum")) {
            String str = cmd.getOptionValue("iterNum");
            iterNum = Integer.parseInt(str);
        }
        if (cmd.hasOption("simStep")) {
            String simStepString = cmd.getOptionValue("simStep");
            switch (simStepString) {
                case "sec":
                    simStep = TimeFrame.Sec;
                    break;
                case "min":
                    simStep = TimeFrame.Minute;
                    break;
                case "hour":
                    simStep = TimeFrame.Hour;
                    break;
                case "day":
                    simStep = TimeFrame.Day;
                    break;

                default:
                    throw new IllegalArgumentException("Wrong simulation step defined.");
            }
        }
        if (cmd.hasOption("em")) {
            em = cmd.getOptionValue("em");
        }
        if (cmd.hasOption("vmSpec")) {
            String vmSpecString = cmd.getOptionValue("vmSpec");
            switch (vmSpecString) {
                case "a0":
                    vmSpec = new VmSpecAzureA0();
                    break;
                case "a1":
                    vmSpec = new VmSpecAzureA1();
                    break;
                case "a2":
                    vmSpec = new VmSpecAzureA2();
                    break;
                case "a3":
                    vmSpec = new VmSpecAzureA3();
                    break;
                case "a4":
                    vmSpec = new VmSpecAzureA4();
                    break;
                case "a5":
                    vmSpec = new VmSpecAzureA5();
                    break;
                case "a6":
                    vmSpec = new VmSpecAzureA6();
                    break;
                case "a7":
                    vmSpec = new VmSpecAzureA7();
                    break;
                case "a8":
                    vmSpec = new VmSpecAzureA8();
                    break;
                default:
                    throw new IllegalArgumentException("Wrong vm spec configuration defined.");
            }
        }
    }

    private static void runSimulation() {

        // create output
        long currentTimestamp = System.currentTimeMillis();
        String outDir = "resources/out/sim/" + currentTimestamp + "_" + em + "/";

        System.out.println("VmPlacementSimulationApp starts...");
        System.out.printf("Output directory; '%s'\r\n", outDir);

        // Create clock
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        SimClock clock = new SimClock(simStep, calendar.getTimeInMillis());
        calendar.set(2014, Calendar.JANUARY, 7, 0, 0, 0);

        // Create em
        ElasticityManager elasticityManager = null;
        if (em == "ffo") {
            elasticityManager = new ElasticityManagerFirstFitOptimistic(new AlwaysVmMigrationPolicy());
        } else if (em == "ffp") {
            elasticityManager = new ElasticityManagerFirstFitPesimistic(new AlwaysVmMigrationPolicy());
        } else if (em == "bn") {
            elasticityManager = new ElasiticityManagerMCDA(clock,
                    new SimplePredictionStrategy(), new AlwaysVmMigrationPolicy());
        }

        // Create cloud
        Cloud cloud = new IaaSCloud(1, clock, elasticityManager);

        // Attach monitor
        cloud.setMonitor(new PassiveAsyncMonitoringSystem(new CsvHistoryWriter(
                outDir + "cloud.csv",
                outDir + "dcs.csv",
                outDir + "pms.csv",
                outDir + "vms.csv", 1000, false)));

        // Create datacenters
        cloud.addDatacenter(createDatacenter(clock, new RioDeJaneiro(), new MechanicalCoolingModel()));
        cloud.addDatacenter(createDatacenter(clock, new Oslo(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel())));
        cloud.addDatacenter(createDatacenter(clock, new Tokyo(), new MechanicalCoolingModel()));
        cloud.addDatacenter(createDatacenter(clock, new Vienna(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel())));
        cloud.addDatacenter(createDatacenter(clock, new Toronto(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel())));

        // Create cloud simulator
//        VmGenerator vmGenerator = new VmGeneratorVariousVmsOnce(
//                3, new VmSpecAzureA1(),
//                3, new VmSpecAzureA3(),
//                2, new VmSpecAzureA6());
        VmGenerator vmGenerator = new VmGeneratorOnce(vmSpec, vmNum);
        Scheduler scheduler = new IaaSScheduler(vmGenerator);
        SimEngine engine = new SimEngineDates(clock, scheduler, cloud, calendar.getTimeInMillis());

        // Perform simulation
        engine.start();
        engine.stop();

        // print results
        LOGGER.info("Total Costs: %.4f", cloud.getCosts());

        // exit in order to stop R-Environment if it is used.
        System.exit(0);
    }

    private static Datacenter createDatacenter(SimClock clock, Location location, CoolingModel model) {
        Datacenter dc = Datacenter.forLocation(dcIdCounter, clock, location);
        dc.setCoolingModel(model);
        createPmsForDc(dc, clock);
        dcIdCounter++;
        LOGGER.trace("%s created in %s.", dc, location.getDescription());
        return dc;
    }

    private static void createPmsForDc(Datacenter dc, SimClock clock) {
        for (int i = 0; i < pmPerDcNum; i++) {
            Pm pm = createDefaultPm(pmIdCounter, clock);
            dc.addPm(pm);
            LOGGER.trace("%s created for %s.", pm, dc);
            pmIdCounter++;
        }
    }

    private static Pm createDefaultPm(int id, SimClock clock) {
        Pm pm = new Pm(id, clock, new PmSpecPowerHpProLiantMl110G3PentiumD930());
        pm.setMipsProvisioner(new GreedyProvisioner());
        pm.setRamProvisioner(new GreedyProvisioner());
        pm.setSizeProvisioner(new GreedyProvisioner());
        pm.setBwProvisioner(new GreedyProvisioner());
        return pm;
    }

}
