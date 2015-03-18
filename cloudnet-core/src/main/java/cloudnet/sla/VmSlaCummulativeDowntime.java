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
package cloudnet.sla;

import cloudnet.core.Sla;
import cloudnet.core.SlaLevel;
import cloudnet.core.Vm;

/**
 *
 * @author Dmytro
 */
public class VmSlaCummulativeDowntime implements Sla {

    private Vm vm;
    /**
     * Penalty for exceeding each cummulative period.
     */
    private final double downtimePenalty;
    /**
     * Cummulative downtime period.
     */
    private final long cummulativePeriod;

    private long violationsCount;
    private long lastViolationsCount;

    /**
     * Sla level
     */
    private final SlaLevel level;

    public Vm getVm() {
        return vm;
    }

    public void setVm(Vm vm) {
        this.vm = vm;
    }

    public double getDowntimePenalty() {
        return downtimePenalty;
    }

    public VmSlaCummulativeDowntime(SlaLevel level, double downtimePenalty, long cummulativePeriod) {
        this.downtimePenalty = downtimePenalty;
        this.level = level;
        this.cummulativePeriod = cummulativePeriod;
    }

    @Override
    public SlaLevel getLevel() {
        return level;
    }

    @Override
    public double getPenalty() {

        long count = vm.getCummulativeDowntime() / cummulativePeriod;
        if (count > lastViolationsCount) {
            violationsCount += count - lastViolationsCount;
            lastViolationsCount = count;
        } else if (count == 0) {
            lastViolationsCount = 0;
        }

        return getDowntimePenalty() * violationsCount;
    }

    public double getViolationRate() {
        long cummulativeDowntime = vm.getCummulativeDowntime();
        double result = (double) (cummulativeDowntime % cummulativePeriod) / cummulativePeriod;
        return result;
    }
}
