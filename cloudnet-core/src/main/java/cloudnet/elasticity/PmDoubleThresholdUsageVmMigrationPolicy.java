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

import cloudnet.core.Vm;
import cloudnet.util.Ensure;

/**
 *
 * @author Dmytro
 */
public class PmDoubleThresholdUsageVmMigrationPolicy implements VmMigrationPolicy {

    private final double lowerThreshold;
    private final double upperThreshold;

    public PmDoubleThresholdUsageVmMigrationPolicy(double lowerThreshold, double upperThreshold) {
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    @Override
    public boolean shouldBeMigrated(Vm vm) {
        Ensure.NotNull(vm.getServer(), "vm.server");
        if (vm.isInMigration()) {
            return false;
        }
        if (vm.isInDowntime()) {
            return true;
        }
        double threshold = vm.getServer().getConsumedMips() / (double) vm.getServer().getSpec().getMips();
        return threshold < lowerThreshold && threshold > upperThreshold;
    }

    @Override
    public String toString() {
        return String.format("pm2thr_%d_%d", (int) (lowerThreshold * 100), (int) (upperThreshold * 100));
    }

}
