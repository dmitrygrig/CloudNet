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
import cloudnet.iaas.IaaSCloud;
import cloudnet.core.Pm;
import cloudnet.core.TimeFrame;
import cloudnet.elasticity.AlwaysVmMigrationPolicy;
import cloudnet.elasticity.ElasticityManagerFirstFitOptimistic;
import cloudnet.sim.SimEngine;
import cloudnet.sim.SimEngineSimple;
import cloudnet.pm.PmSpecPowerHpProLiantMl110G3PentiumD930;
import cloudnet.iaas.IaaSScheduler;
import cloudnet.sim.Scheduler;
import cloudnet.iaas.VmGeneratorOnce;
import cloudnet.locations.Location;
import cloudnet.examples.locations.Oslo;
import cloudnet.examples.locations.RioDeJaneiro;
import cloudnet.examples.locations.Tokyo;
import cloudnet.examples.locations.Toronto;
import cloudnet.examples.locations.Vienna;
import cloudnet.monitoring.CsvHistoryWriter;
import cloudnet.monitoring.PassiveMonitoringSystem;
import cloudnet.provisioners.GreedyProvisioner;
import cloudnet.sim.SimClock;
import cloudnet.vm.azure.VmSpecAzureA1;
import cloudnet.weather.RealTemperatureModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example shows simulation of a cloud.
 *
 * Electricity prices source: http://en.wikipedia.org/wiki/Electricity_pricing
 * Day/night switch time:
 * http://www.airtricity.com/ie/home/help-centre-ie/understanding-your-bill/rates-and-charges/what-is-the-price-per-unit-for-day-and-night-rates/
 *
 */
public class AllocationApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(AllocationApp.class);

    public static void main(String[] args) {

        // Create clock
        SimClock clock = new SimClock(TimeFrame.Sec);

        // Create cloud
        Cloud cloud = new IaaSCloud(1, clock, new ElasticityManagerFirstFitOptimistic(new AlwaysVmMigrationPolicy()));
        cloud.setMonitor(new PassiveMonitoringSystem(new CsvHistoryWriter("resources/cloud.csv", "out/dcs.csv", "out/pms.csv", "out/vms.csv", 1000, false)));

        // Create datacenters
        cloud.addDatacenter(createDatacenter(clock, new RioDeJaneiro(), new MechanicalCoolingModel()));
        cloud.addDatacenter(createDatacenter(clock, new Oslo(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel())));
        cloud.addDatacenter(createDatacenter(clock, new Tokyo(), new MechanicalCoolingModel()));
        cloud.addDatacenter(createDatacenter(clock, new Vienna(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel())));
        cloud.addDatacenter(createDatacenter(clock, new Toronto(), new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel())));

        // Create cloud simulator
        Scheduler scheduler = new IaaSScheduler(new VmGeneratorOnce(new VmSpecAzureA1(), 2));
        SimEngine engine = new SimEngineSimple(clock, scheduler, cloud, 3600);

        // Perform simulation
        engine.start();
        engine.stop();

        // print results
        LOGGER.info("Total Costs: %.2f", cloud.getCosts());

        // exit in order to stop R-Environment if it is used.
        System.exit(0);
    }

    private static int pmIdCounter = 1;
    private static int dcIdCounter = 1;

    private static Datacenter createDatacenter(SimClock clock, Location location, CoolingModel model) {
        Datacenter dc = Datacenter.forLocation(dcIdCounter, clock, location);
        dc.setCoolingModel(model);
        createPmsForDc(dc, clock);
        dcIdCounter++;
        LOGGER.trace("%s created in %s.", dc, location.getDescription());
        return dc;
    }

    private static void createPmsForDc(Datacenter dc, SimClock clock) {
        for (int i = 0; i < 5; i++) {
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
