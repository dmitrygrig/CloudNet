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
package cloudnet.iaas;

import cloudnet.core.SlaLevel;
import cloudnet.core.TimeFrame;
import cloudnet.core.Vm;
import cloudnet.core.VmSpec;
import cloudnet.sim.SimClock;
import cloudnet.sla.VmAvailabilityBasedSla;
import cloudnet.sla.VmSlaCummulativeDowntime;
import cloudnet.sla.VmSlaOverallDowntime;
import cloudnet.workloads.ContinuouslyChangingWorkloadModel;
import cloudnet.workloads.DayNightWorkloadModel;
import cloudnet.workloads.PeriodicWorkloadModel;
import cloudnet.workloads.StaticWorkloadModel;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmGeneratorVariousVmsOnce implements VmGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(VmGeneratorVariousVmsOnce.class);

    private boolean generated = false;
    private final static int startSEED = 7;
    private final int idleVmNumber;
    private final VmSpec idleVmSpec;
    private final int webVmNumber;
    private final VmSpec webVmSpec;
    private final int hpcVmNumber;
    private final VmSpec hpcVmSpec;

    public VmGeneratorVariousVmsOnce(int idleVmNumber, VmSpec idleVmSpec, int webVmNumber, VmSpec webVmSpec, int hpcVmNumber, VmSpec hpcVmSpec) {
        this.idleVmNumber = idleVmNumber;
        this.idleVmSpec = idleVmSpec;
        this.webVmNumber = webVmNumber;
        this.webVmSpec = webVmSpec;
        this.hpcVmNumber = hpcVmNumber;
        this.hpcVmSpec = hpcVmSpec;
    }

    private Vm createIdleVm(int id, SimClock clock, VmAvailabilityBasedSla sla) {
        int currSeed = startSEED + id;
        Vm vm = new Vm(id, clock);
        vm.setSpec(idleVmSpec);
        vm.setBwWorkloadModel(new StaticWorkloadModel(.1, .05, currSeed));
        vm.setCpuWorkloadModel(new StaticWorkloadModel(.1, .05, currSeed));
        vm.setRamWorkloadModel(new StaticWorkloadModel(.1, .05, currSeed));
        vm.setSizeWorkloadModel(new ContinuouslyChangingWorkloadModel(0.2, 0.3, 0.01, TimeFrame.Day, 0));
        vm.setSla(sla);
        sla.setVm(vm);
        vm.run();
        return vm;
    }

    private Vm createWebVm(int id, SimClock clock, VmAvailabilityBasedSla sla) {
        int currSeed = startSEED + id;
        Vm vm = new Vm(id, clock);
        vm.setSpec(webVmSpec);
        vm.setBwWorkloadModel(new DayNightWorkloadModel(
                new PeriodicWorkloadModel(.7, .95, .05, TimeFrame.Day, currSeed),
                new PeriodicWorkloadModel(.3, .7, .05, TimeFrame.Day, currSeed),
                ZoneId.systemDefault(), 8, 20));
        vm.setCpuWorkloadModel(new DayNightWorkloadModel(
                new PeriodicWorkloadModel(.1, .3, .05, TimeFrame.Day, currSeed),
                new PeriodicWorkloadModel(.05, .1, .05, TimeFrame.Day, currSeed),
                ZoneId.systemDefault(), 8, 20));
        vm.setRamWorkloadModel(new DayNightWorkloadModel(
                new PeriodicWorkloadModel(.4, .6, .05, TimeFrame.Day, currSeed),
                new PeriodicWorkloadModel(.1, .4, .05, TimeFrame.Day, currSeed),
                ZoneId.systemDefault(), 8, 20));
        vm.setSizeWorkloadModel(new ContinuouslyChangingWorkloadModel(0.2, 0.8, 0.01, TimeFrame.Day, 0));
        vm.setSla(sla);
        sla.setVm(vm);
        vm.run();
        return vm;
    }

    private Vm createHPCVm(int id, SimClock clock, VmAvailabilityBasedSla sla) {
        int currSeed = startSEED + id;
        Vm vm = new Vm(id, clock);
        vm.setSpec(hpcVmSpec);
        vm.setBwWorkloadModel(new StaticWorkloadModel(.2, .05, currSeed));
        vm.setCpuWorkloadModel(new DayNightWorkloadModel(
                new PeriodicWorkloadModel(.7, .9, .05, TimeFrame.Day, currSeed),
                new StaticWorkloadModel(.2, .05, currSeed),
                ZoneId.systemDefault(), 7, 23));
        vm.setRamWorkloadModel(new DayNightWorkloadModel(
                new PeriodicWorkloadModel(.7, .9, .05, TimeFrame.Day, currSeed),
                new StaticWorkloadModel(.1, .05, currSeed),
                ZoneId.systemDefault(), 7, 23));
        vm.setSizeWorkloadModel(new StaticWorkloadModel(.3, .05, currSeed));
        vm.setSla(sla);
        sla.setVm(vm);
        vm.run();
        return vm;
    }

    private VmAvailabilityBasedSla createSla(SlaLevel level) {
        VmSlaOverallDowntime overallSla = new VmSlaOverallDowntime(level, 0.01, 25, TimeFrame.Month);
        VmSlaCummulativeDowntime cummSla = new VmSlaCummulativeDowntime(level, 5, TimeFrame.Hour);
        VmAvailabilityBasedSla sla = new VmAvailabilityBasedSla(overallSla, cummSla);
        return sla;
    }

    @Override
    public List<Vm> generate(SimClock clock) {

        List<Vm> vms = new ArrayList<>();
        if (!generated) {
            int counter = 1;
            for (int i = 0; i < idleVmNumber; i++) {
                VmAvailabilityBasedSla sla = createSla(SlaLevel.Bronze);
                Vm vm = createIdleVm(counter++, clock, sla);
                LOGGER.info(String.format("Idle %s was generated.", vm));
                vms.add(vm);
            }
            for (int i = 0; i < webVmNumber; i++) {
                VmAvailabilityBasedSla sla = createSla(SlaLevel.Bronze);
                Vm vm = createWebVm(counter++, clock, sla);
                LOGGER.info(String.format("Web %s was generated.", vm));
                vms.add(vm);
            }

            for (int i = 0; i < hpcVmNumber; i++) {
                VmAvailabilityBasedSla sla = createSla(SlaLevel.Bronze);
                Vm vm = createHPCVm(counter++, clock, sla);
                LOGGER.info(String.format("HPC %s was generated.", vm));
                vms.add(vm);
            }

            generated = true;
        }
        return vms;
    }

}
