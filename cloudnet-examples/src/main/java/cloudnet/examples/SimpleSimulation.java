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
import cloudnet.cooling.MechanicalCoolingModel;
import cloudnet.cooling.MixedCoolingModel;
import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.iaas.IaaSCloud;
import cloudnet.core.Pm;
import cloudnet.core.TimeFrame;
import cloudnet.elasticity.ElasticityManagerInefficient;
import cloudnet.sim.SimEngine;
import cloudnet.sim.SimEngineSimple;
import cloudnet.pm.PmSpecPowerHpProLiantMl110G3PentiumD930;
import cloudnet.iaas.IaaSScheduler;
import cloudnet.sim.Scheduler;
import cloudnet.iaas.VmGeneratorOnce;
import cloudnet.locations.Oslo;
import cloudnet.monitoring.CsvHistoryWriter;
import cloudnet.monitoring.PassiveMonitoringSystem;
import cloudnet.provisioners.GreedyProvisioner;
import cloudnet.sim.SimClock;
import cloudnet.vm.azure.VmSpecAzureA1;
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
public class SimpleSimulation {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleSimulation.class);

    public static void main(String[] args) {

        // Create clock
        SimClock clock = new SimClock(TimeFrame.Hour);

        // Create cloud
        Cloud cloud = new IaaSCloud(1, clock);
        
        // attack em
        cloud.attachPlugin(new ElasticityManagerInefficient());
        
        // attach monitor
        cloud.attachPlugin(new PassiveMonitoringSystem(new CsvHistoryWriter("resources/cloud.csv", "out/dcs.csv", "out/pms.csv", "out/vms.csv", 1000, false)));

        // Create datacenter (DC)
        Datacenter dc = Datacenter.forLocation(1, clock, new Oslo());
        dc.setCoolingModel(new MixedCoolingModel(10.0, 18.0, new AirCoolingModel(), new MechanicalCoolingModel()));
        
        // add one PM to the DC
        Pm pm = new Pm(1, clock, new PmSpecPowerHpProLiantMl110G3PentiumD930());
        pm.setMipsProvisioner(new GreedyProvisioner());
        pm.setRamProvisioner(new GreedyProvisioner());
        pm.setSizeProvisioner(new GreedyProvisioner());
        pm.setBwProvisioner(new GreedyProvisioner());
        dc.addPm(pm);
        
        // add DC to the cloud
        cloud.addDatacenter(dc);

        // Create cloud simulator
        Scheduler scheduler = new IaaSScheduler(new VmGeneratorOnce(new VmSpecAzureA1(), 2));
        SimEngine engine = new SimEngineSimple(clock, scheduler, cloud, 10);

        // Perform simulation
        engine.start();
        engine.stop();

        // print results
        LOGGER.info(String.format("Total Costs: %.2f", cloud.getCosts()));
    }
}
