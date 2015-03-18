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

import cloudnet.sla.VmSlaOverallDowntime;
import cloudnet.core.Sla;
import cloudnet.core.SlaLevel;
import cloudnet.core.TimeFrame;
import cloudnet.core.Vm;
import cloudnet.core.VmSpec;
import cloudnet.sim.SimClock;
import cloudnet.sla.VmAvailabilityBasedSla;
import cloudnet.sla.VmSlaCummulativeDowntime;
import cloudnet.workloads.ContinuouslyChangingWorkloadModel;
import cloudnet.workloads.PeriodicWorkloadModel;
import cloudnet.workloads.StaticWorkloadModel;
import cloudnet.workloads.WorkloadModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmGeneratorOnce implements VmGenerator {

    private final static int SEED = 7;
    private final VmSpec vmSpec;
    private boolean generated = false;
    private final int number;
    private WorkloadModel cpuWorkloadModel = new PeriodicWorkloadModel(.1, .8, .05, 2 * TimeFrame.Hour, SEED);
    private WorkloadModel bwWorkloadModel = new StaticWorkloadModel(.5, .05, SEED);
    private WorkloadModel ramWorkloadModel = new PeriodicWorkloadModel(.1, .6, .05, 2 * TimeFrame.Hour, SEED);
    private WorkloadModel sizeWorkloadModel = new ContinuouslyChangingWorkloadModel(0.2, 0.8, 0.01, TimeFrame.Day, 0);

    public VmGeneratorOnce(VmSpec vmSpec, int number) {
        this.vmSpec = vmSpec;
        this.number = number;
    }

    private Vm createVm(int i, SimClock clock) {

        Vm vm = new Vm(i, clock);
        vm.setSpec(vmSpec);
        vm.setCpuWorkloadModel(cpuWorkloadModel);
        vm.setBwWorkloadModel(bwWorkloadModel);
        vm.setRamWorkloadModel(ramWorkloadModel);
        vm.setSizeWorkloadModel(sizeWorkloadModel);
        vm.setSla(createSla(vm));
        vm.run();
        return vm;
    }

    private int slaLevelCounter;

    private Sla createSla(Vm vm) {
        VmSlaOverallDowntime overallSla = new VmSlaOverallDowntime(getSlaLevel(), 0.01, 5, TimeFrame.Month);
        VmSlaCummulativeDowntime cummSla = new VmSlaCummulativeDowntime(getSlaLevel(), 1, TimeFrame.Hour);
        VmAvailabilityBasedSla sla = new VmAvailabilityBasedSla(overallSla, cummSla);
        sla.setVm(vm);
        return sla;
    }

    private SlaLevel getSlaLevel() {
        SlaLevel level;
        if (slaLevelCounter == 0) {
            level = SlaLevel.Bronze;
            slaLevelCounter++;
        } else if (slaLevelCounter == 1) {
            level = SlaLevel.Silver;
            slaLevelCounter++;
        } else {
            level = SlaLevel.Gold;
            slaLevelCounter = 0;
        }
        return level;
    }

    @Override
    public List<Vm> generate(SimClock clock) {

        List<Vm> vms = new ArrayList<>();
        if (!generated) {
            generated = true;
            for (int i = 0; i < number; i++) {
                vms.add(createVm(i + 1, clock));
            }
        }
        return vms;
    }

    public WorkloadModel getCpuWorkloadModel() {
        return cpuWorkloadModel;
    }

    public void setCpuWorkloadModel(WorkloadModel cpuWorkloadModel) {
        this.cpuWorkloadModel = cpuWorkloadModel;
    }

    public WorkloadModel getBwWorkloadModel() {
        return bwWorkloadModel;
    }

    public void setBwWorkloadModel(WorkloadModel bwWorkloadModel) {
        this.bwWorkloadModel = bwWorkloadModel;
    }

    public WorkloadModel getRamWorkloadModel() {
        return ramWorkloadModel;
    }

    public void setRamWorkloadModel(WorkloadModel ramWorkloadModel) {
        this.ramWorkloadModel = ramWorkloadModel;
    }

    public WorkloadModel getSizeWorkloadModel() {
        return sizeWorkloadModel;
    }

    public void setSizeWorkloadModel(WorkloadModel sizeWorkloadModel) {
        this.sizeWorkloadModel = sizeWorkloadModel;
    }

}
