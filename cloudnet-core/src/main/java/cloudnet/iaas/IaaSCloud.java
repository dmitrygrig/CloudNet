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
import cloudnet.core.Datacenter;
import cloudnet.core.Pm;
import cloudnet.core.Vm;
import cloudnet.sim.SimClock;
import cloudnet.elasticity.ElasticityManager;
import cloudnet.core.TimeFrame;
import cloudnet.messaging.PmStartMessage;
import cloudnet.messaging.PmStopMessage;
import cloudnet.messaging.VmAllocationMessage;
import cloudnet.messaging.VmMigrationMessage;
import cloudnet.messaging.VmSchedulingMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class IaaSCloud extends Cloud {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IaaSCloud.class);

    private final Map<Integer, Vm> vmPool = new HashMap<>();

    public IaaSCloud(int id, SimClock clock, ElasticityManager em) {
        super(id, clock, em);
    }

    @Override
    protected void performChanges() {
        // starting of pms
        List<PmStartMessage> pmStartMessages = getMessageBus().getMessagesByType(PmStartMessage.class);
        for (PmStartMessage m : pmStartMessages) {
            m.getPm().run();
        }
        getMessageBus().removeRange(pmStartMessages);

        // process live migrations
        for (Datacenter dc : datacenters) {
            for (Pm pm : dc.getPms()) {
                for (Vm vm : pm.getVms().stream().filter(x -> x.isInMigration()).collect(Collectors.toList())) {
                    long migratedSize = (long) (pm.getSpec().getBw()
                            * TimeFrame.msToSec(getClock().diff() - dc.getPowerOutageDuration()));
                    vm.reduceMigratedSize(migratedSize);
                    if (vm.getMigratedSize() == 0.0) {
                        vm.finishMigration();
                    }
                }
            }
        }

        // process messages
        // migrations
        List<VmMigrationMessage> vmMigrMessages = getMessageBus().getMessagesByType(VmMigrationMessage.class);
        this.vmMigrationCount += vmMigrMessages.size();
        for (VmMigrationMessage m : vmMigrMessages) {
            // ToDo: replace with live migration strategy
            m.getVm().beginMigrationTo(m.getPmTo());
        }
        getMessageBus().removeRange(vmMigrMessages);

        // allocations
        List<VmAllocationMessage> vmAllocMessages = getMessageBus().getMessagesByType(VmAllocationMessage.class);
        for (VmAllocationMessage m : vmAllocMessages) {
            m.getVm().allocateTo(m.getPm());
        }
        getMessageBus().removeRange(vmAllocMessages);

        // stoping of pms
        List<PmStopMessage> pmStopMessages = getMessageBus().getMessagesByType(PmStopMessage.class);
        for (PmStopMessage m : pmStopMessages) {
            m.getPm().powerOff();
        }
        getMessageBus().removeRange(pmStopMessages);

        // register all scheduling vms in the vm pool
        List<VmSchedulingMessage> vmScheduledMessages = getMessageBus().getMessagesByType(VmSchedulingMessage.class);
        List<VmSchedulingMessage> messagesToDelete = new ArrayList<>();
        for (VmSchedulingMessage m : vmScheduledMessages) {
            if (!vmPool.containsKey(m.getVm().getId())) {
                vmPool.put(m.getVm().getId(), m.getVm());
            }
            if (m.getVm().isAllocated()) {
                messagesToDelete.add(m);
            }
        }
        getMessageBus().removeRange(messagesToDelete);

    }

    @Override
    protected void computeOverallSlaCosts() {
        slaPenaltyCosts = 0.0;

        // update sla violations
        for (Map.Entry<Integer, Vm> entry : vmPool.entrySet()) {
            double penalty = entry.getValue().getSla().getPenalty();
            slaPenaltyCosts += penalty;
            LOGGER.trace("Penalty costs for Vm %d is %.4f.", entry.getKey(), penalty);
        }
        LOGGER.trace("Overall penalty costs increased to %.2f.", slaPenaltyCosts);
    }

    @Override
    public Collection<Vm> getVms() {
        return vmPool.values();
    }

    @Override
    protected void simulateCustomEntities() {
        for (Vm vm : getVms()) {
            vm.simulateExecution();
        }
    }

    @Override
    protected void computeViolationCount() {
        long currViolations = getVms().stream().filter(x -> x.isInDowntime()).count();
        if (currViolations > 0) {
            violationCount += currViolations;
            LOGGER.info("Number of violations: %d", violationCount);
        }
        
        long currShortViolations = getVms().stream().filter(x -> x.isInShortDowntime()).count();
        if (currShortViolations > 0) {
            shortViolationCount += currShortViolations;
            LOGGER.info("Number of short violations: %d", currShortViolations);
        }
    }

}
