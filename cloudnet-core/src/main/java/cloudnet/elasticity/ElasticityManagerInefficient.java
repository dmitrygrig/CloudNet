/*
 * Copyright (C) 2014 Dmytro
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
package cloudnet.elasticity;

import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.core.Pm;
import cloudnet.core.PmState;
import cloudnet.core.Vm;
import cloudnet.messaging.PmStartMessage;
import cloudnet.messaging.VmAllocationMessage;
import cloudnet.messaging.VmSchedulingMessage;
import cloudnet.util.Ensure;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro
 */
public class ElasticityManagerInefficient extends ElasticityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticityManagerInefficient.class);
    
    private final Map<Integer, Long> consumedMipsPerPm = new HashMap<>();

    public ElasticityManagerInefficient() {
    }

    @Override
    public void manage(Cloud cloud) {

        Ensure.NotNull(cloud, "cloud");

        consumedMipsPerPm.clear();

        managePmSwitchOn(cloud);
        manageAllocations(cloud);
    }

    private void managePmSwitchOn(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        cloud.getDatacenters().forEach(dc -> {
            dc.getPms().stream().filter(x -> x.getPmState() == PmState.PowerOff).forEach(pm -> {
                cloud.getMessageBus().push(new PmStartMessage(pm));
            });
        });
    }

    private void manageAllocations(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        List<VmSchedulingMessage> vmAllocMessages = cloud.getMessageBus().getMessagesByType(VmSchedulingMessage.class);
        List<Vm> vms = vmAllocMessages.stream().map(x -> x.getVm())
                .sorted((vm1, vm2) -> vm1.getSla().getLevel().compareTo(vm2.getSla().getLevel()))
                .collect(Collectors.toList());
        for (Vm vm : vms) {
            Pm targetPm = findTargetPmForAllocation(cloud, vm);
            if (targetPm != null) {
                cloud.getMessageBus().push(new VmAllocationMessage(vm, targetPm));
                updatedAvailablePmResources(targetPm, vm, true);
                LOGGER.info(String.format("%s was decided to allocate to %s.", vm.toShortString(), targetPm.toShortString()));
            } else {
                LOGGER.warn(String.format("%s was not allocated to any pm.", vm.toShortString()));
            }
        }

    }

    private Pm findTargetPmForAllocation(Cloud cloud, Vm vm) {
        Ensure.NotNull(cloud, "cloud");
        Ensure.NotNull(vm, "vm");
        Ensure.IsNull(vm.getServer(), "Vm is already allocated.");

        LOGGER.trace(String.format("Find target pm for allocation of %s...", vm.toShortString()));

        for (Datacenter dc : cloud.getDatacenters()) {
            for (Pm pm : dc.getPms()) {
                if (isPmSuitableForVm(pm, vm)) {
                    return pm;
                }
            }
        }

        return null;
    }

    private boolean isPmSuitableForVm(Pm pm, Vm vm) {
        Ensure.NotNull(pm, "pm");
        Ensure.NotNull(vm, "vm");

        return pm.getSpec().getMips() - getComsumedMipsForPm(pm) >= vm.getSpec().getMips();
    }

    private long getComsumedMipsForPm(Pm pm) {
        Ensure.NotNull(pm, "pm");
        Long res = consumedMipsPerPm.get(pm.getId());
        if (res == null) {
            res = pm.getVms().stream().mapToLong(vm -> vm.getSpec().getMips()).sum();
            consumedMipsPerPm.put(pm.getId(), res);
        }
        return res;
    }

    private void updatedAvailablePmResources(Pm pm, Vm vm, boolean isVmAdded) {
        Ensure.NotNull(pm, "pm");
        Ensure.NotNull(vm, "vm");

        Long mips = getComsumedMipsForPm(pm);
        Long vmMips = vm.getSpec().getMips();
        if (isVmAdded) {
            mips += vmMips;
            LOGGER.info(String.format("Consumed mips by %s after allocation of %s: mips=%d,vmMips=%d",
                    pm.toShortString(), vm.toShortString(), mips, vmMips));
        } else {
            mips -= vmMips;
            LOGGER.info(String.format("Consumed mips by %s after deallocation of %s: mips=%d,vmMips=%d",
                    pm.toShortString(), vm.toShortString(), mips, vmMips));
        }

        consumedMipsPerPm.put(pm.getId(), mips);
    }

}
