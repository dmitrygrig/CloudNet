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
package cloudnet.pm;

import cloudnet.core.PmSpec;
import cloudnet.util.Ensure;
import org.apache.commons.math3.util.FastMath;

/**
 * PmSpecPower is the abstract base for each Pm Specification that based on data
 * from SPECpower benchmark: http://www.spec.org/power_ssj2008/
 *
 * Partly obtained from CloudSim project.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public abstract class PmSpecPower implements PmSpec {

    @Override
    public double getPower(double utilization) throws IllegalArgumentException {
        Ensure.BetweenInclusive(utilization, 0.0, 1.0, "utilization");
        if (utilization % 0.1 == 0) {
            return getPowerData((int) (utilization * 10));
        }
        int utilization1 = (int) FastMath.floor(utilization * 10);
        int utilization2 = (int) FastMath.ceil(utilization * 10);
        double power1 = getPowerData(utilization1);
        double power2 = getPowerData(utilization2);
        double delta = (power2 - power1) / 10;
        double power = power1 + delta * (utilization - (double) utilization1 / 10) * 100;
        return power;
    }

    /**
     * Gets the power data.
     *
     * @param index the index
     * @return the power data
     */
    protected abstract double getPowerData(int index);
}
