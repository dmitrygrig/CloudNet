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

import cloudnet.core.Cloud;
import cloudnet.messaging.VmSchedulingMessage;
import cloudnet.sim.SimClock;
import cloudnet.sim.Scheduler;

/**
 * VmScheduler interface should be implemented in order to schedule VMs during
 * simulation.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class IaaSScheduler implements Scheduler {

    private final VmGenerator vmGenerator;

    public IaaSScheduler(VmGenerator vmGenerator) {
        this.vmGenerator = vmGenerator;
    }

    /**
     * Get the value of vmGenerator
     *
     * @return the value of vmGenerator
     */
    public VmGenerator getVmGenerator() {
        return vmGenerator;
    }

    @Override
    public void schedule(Cloud cloud, SimClock clock) {
        vmGenerator.generate(clock)
                .stream()
                .forEach(vm -> cloud.getMessageBus().push(new VmSchedulingMessage(vm)));
    }
}
