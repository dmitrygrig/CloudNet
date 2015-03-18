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
package cloudnet.elasticity;

import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.core.Pm;
import cloudnet.core.PmState;
import cloudnet.core.Vm;
import cloudnet.messaging.PmStartMessage;
import cloudnet.messaging.VmAllocationMessage;
import cloudnet.messaging.VmMigrationMessage;
import cloudnet.messaging.VmSchedulingMessage;
import cloudnet.util.Ensure;
import java.util.ArrayList;
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
public class ElasticityManagerUnefficient implements ElasticityManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticityManagerUnefficient.class);

    private final Map<Integer, Long> consumedMipsPerPm = new HashMap<>();
    private final List<Integer> usedPms = new ArrayList<>();
    private final VmMigrationPolicy migrationPolicy;

    public ElasticityManagerUnefficient(VmMigrationPolicy migrationPolicy) {
        this.migrationPolicy = migrationPolicy;
    }

    @Override
    public void manage(Cloud cloud) {

        Ensure.NotNull(cloud, "cloud");

        consumedMipsPerPm.clear();

        managePmSwitchOn(cloud);
        manageAllocations(cloud);
        manageMigrations(cloud);
    }

    private void managePmSwitchOn(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        cloud.getDatacenters().forEach(dc -> {
            dc.getPms().parallelStream().filter(x -> x.getPmState() == PmState.PowerOff).forEach(pm -> {
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
                LOGGER.info("%s was decided to allocate to %s.", vm.toShortString(), targetPm.toShortString());
            } else {
                LOGGER.warn("%s was not allocated to any pm.", vm.toShortString());
            }
        }

    }

    private void manageMigrations(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");
        cloud.getDatacenters().forEach(dc
                -> dc.getPms().forEach(pm
                        -> pm.getVms().stream().filter(vm -> migrationPolicy.shouldBeMigrated(vm)).forEach(vm
                                -> {
                    Pm targetPm = findTargetPmForMigration(cloud, vm);
                    if (targetPm != null && targetPm != vm.getServer()) {
                        cloud.getMessageBus().push(new VmMigrationMessage(vm, targetPm));
                        updatedAvailablePmResources(vm.getServer(), vm, false);
                        updatedAvailablePmResources(targetPm, vm, true);
                        LOGGER.info("%s was decided to migrate to %s.", vm.toShortString(), targetPm.toShortString());
                    }
                })));
    }

    private Pm findTargetPmForAllocation(Cloud cloud, Vm vm) {
        Ensure.NotNull(cloud, "cloud");
        Ensure.NotNull(vm, "vm");
        Ensure.IsNull(vm.getServer(), "Vm is already allocated.");

        LOGGER.trace("Find target pm for allocation of %s...", vm.toShortString());

        for (Datacenter dc : cloud.getDatacenters()) {
            for (Pm pm : dc.getPms()) {
                if (isPmSuitableForVm(pm, vm)) {
                    return pm;
                }
            }
        }

        return null;
    }

    private Pm findTargetPmForMigration(Cloud cloud, Vm vm) {
        Ensure.NotNull(cloud, "cloud");
        Ensure.NotNull(vm.getServer(), "vm.Server");
        Ensure.IsFalse(vm.isInMigration(), "During live migration vm cannot be migrated to another pm.");

        LOGGER.trace("Find target pm for migration of %s...", vm.toShortString());

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
            LOGGER.info("Consumed mips by %s after allocation of %s: mips=%d,vmMips=%d",
                    pm.toShortString(), vm.toShortString(), mips, vmMips);
        } else {
            mips -= vmMips;
            LOGGER.info("Consumed mips by %s after deallocation of %s: mips=%d,vmMips=%d",
                    pm.toShortString(), vm.toShortString(), mips, vmMips);
        }

        consumedMipsPerPm.put(pm.getId(), mips);
    }

}
