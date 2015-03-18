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
package cloudnet.examples.bn;

import cloudnet.util.Ensure;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class UtilityHelper {

    public static final double MaxPower = 250.0;
    public static final double MaxEnergyPrice = .3;

    private UtilityHelper() {
    }

    /**
     * Returns utility value for the specified workload assuming that lower
     * workload is better.
     *
     * @param workload Workload value
     * @return utility value
     */
    public static Double getDescBenefitWorkloadUtility(double workload) {
        String level = WorkloadLevel.getLevel(workload);
        return getDescBenefitWorkloadUtility(level);
    }

    /**
     * Returns utility value for the specified workload assuming that lower
     * workload is better.
     *
     * @param level Workload level
     * @return utility value
     */
    public static Double getDescBenefitWorkloadUtility(String level) {

        int index = Arrays.asList(WorkloadLevel.AllWithOverusage()).indexOf(level);

        if (index == -1) {
            throw new IllegalArgumentException("Unexisted level specified.");
        }

        double step = 1.0 / WorkloadLevel.All().length;
        return 1 - step * index;
    }

    /**
     * Returns utility value for the specified workload assuming that higher
     * workload is better.
     *
     * @param workload Workload value
     * @return utility value. Null values means "reject"
     */
    public static Double getAscBenefitWorkloadUtility(double workload) {
        String level = WorkloadLevel.getLevel(workload);
        return getAscBenefitWorkloadUtility(level);
    }

    /**
     * Returns utility value for the specified workload assuming that higher
     * workload is better.
     *
     * @param level Workload level
     * @return utility value. Null values means "reject"
     */
    public static Double getAscBenefitWorkloadUtility(String level) {

        int index = Arrays.asList(WorkloadLevel.AllWithOverusage()).indexOf(level);

        if (index == -1) {
            throw new IllegalArgumentException("Unexisted level specified.");
        }

        if (index == WorkloadLevel.AllWithOverusage().length - 1) {
            return 0.0; // reject
        }

        double step = 1.0 / WorkloadLevel.All().length;
        return step * (index + 1);
    }

    /**
     * Returns utility value for the specified workload assuming that the
     * specified workload is better.
     *
     * @param workload Workload value
     * @param bestWorkload The best workload value
     * @return utility value. Null values means "reject"
     */
    public static Double getCombinedBenefitWorkloadUtility(double workload, double bestWorkload) {
        String level = WorkloadLevel.getLevel(workload);
        String bestLevel = WorkloadLevel.getLevel(bestWorkload);
        return getCombinedBenefitWorkloadUtility(level, bestLevel);
    }

    /**
     * Returns utility value for the specified workload assuming that higher
     * workload is better.
     *
     * @param level Workload level
     * @param bestLevel The best workload level
     * @return utility value. Null values means "reject"
     */
    public static Double getCombinedBenefitWorkloadUtility(String level, String bestLevel) {

        List<String> levels = Arrays.asList(WorkloadLevel.AllWithOverusage());
        int index = levels.indexOf(level);

        if (index == -1) {
            throw new IllegalArgumentException("Unexisted level specified.");
        }

        if (index == WorkloadLevel.AllWithOverusage().length - 1) {
            return 0.0; // reject
        }

        int indexMax = levels.indexOf(bestLevel);

        Ensure.GreaterThan(indexMax, 0, "indexMax");

        if (index == indexMax) {
            return 1.0;
        } else if (index < indexMax) {
            double step = 1.0 / (indexMax + 1);
            return step * (index + 1);
        } else {
            double step = 1.0 / (WorkloadLevel.All().length - indexMax);
            return 1.0 - step * (index - indexMax);
        }
    }

    /**
     * Returns utility for energy price. Lower values are better.
     *
     * @param price Energy price
     * @return utility value
     */
    public static double getEnergyPriceUtility(double price) {
        return (MaxEnergyPrice - price) / MaxEnergyPrice;
    }

    /**
     * Returns utility value for the pm power consumption (usually including
     * cooling power consumption). Lower values are better.
     *
     * @param powerConsumption Pm power consumption
     * @return utility value
     */
    public static double getPmPowerUtility(double powerConsumption) {
        return (MaxPower - powerConsumption) / MaxPower;
    }

}
