/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig(at)gmail.com>
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
package cloudnet.sla;

import cloudnet.core.Sla;
import cloudnet.core.SlaLevel;
import cloudnet.core.Vm;
import cloudnet.util.Ensure;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmSlaOverallDowntime implements Sla {

    private Vm vm;
    /**
     * Percent = Downtime / Overall requested time
     */
    private final double downtimeViolationStep;
    /**
     * Penalty for exceeding 1 percent of downtime.
     */
    private final double downtimePercentPenalty;
    /**
     * Billing period.
     */
    private final long billingPeriod;

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

    public double getMaxDowntimePercent() {
        return downtimeViolationStep;
    }

    public double getDowntimePercentPenalty() {
        return downtimePercentPenalty;
    }

    public VmSlaOverallDowntime(SlaLevel level, double downtimeViolationStep,
            double downtimePercentPenalty, long billingPeriod) {
        this.downtimeViolationStep = downtimeViolationStep;
        this.downtimePercentPenalty = downtimePercentPenalty;
        this.level = level;
        this.billingPeriod = billingPeriod;
    }

    @Override
    public SlaLevel getLevel() {
        return level;
    }

    @Override
    public double getPenalty() {
        Ensure.NotNull(vm, "vm");
        if (vm.getRequestedRunningTime() == 0L) {
            return 0.0;
        }

        float percent = (float) vm.getDowntime() / billingPeriod;
        float violations = percent / (float) downtimeViolationStep;
        int violationsCount = (int) FastMath.floor(violations);
        return round(getDowntimePercentPenalty() * violationsCount, 2);
    }

    public double getViolationRate() {
        Ensure.NotNull(vm, "vm");
        double percent = (double) vm.getDowntime() / billingPeriod;
        int violationsCount = (int) FastMath.floor(percent / downtimeViolationStep);
        double result = (percent - violationsCount * downtimeViolationStep) / downtimeViolationStep;
        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
