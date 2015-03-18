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

import cloudnet.core.TimeFrame;
import cloudnet.locations.Locations;
import cloudnet.util.Ensure;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DistrHelper {

    public static final Integer[] TwoWorkloadsSumDistribution() {
        return TwoWorkloadsSumDistribution;
    }

    public static final Integer[] TwoWorkloadsAndDistribution() {
        return TwoWorkloadsAndDistribution;
    }

    public static final Double[] MigrationTimeDistribution() {
        return MigrationTimeDistribution;
    }

    public static final Integer[] VmDowntimeDistribution() {
        return VmDowntimeDistribution;
    }

    public static Integer[] WorkloadToOverusageDistribution() {
        return WorkloadToOverusageDistribution;
    }

    public static Integer[] WorkloadToDirtyPageDistr() {
        return WorkloadToDirtyPageDistr;
    }

    public static Double[] PowerOutageDistr() {
        return PowerOutageDistr;
    }

    public static Double[] PowerOutageDurationDistr() {
        return PowerOutageDurationDistr;
    }

    private static final long MaxSlaViolationTime = (long) (0.001 * TimeFrame.Month); // 99.9% / per month
    private static final Integer[] TwoWorkloadsSumDistribution;
    private static final Integer[] TwoWorkloadsAndDistribution;
    private static final Double[] MigrationTimeDistribution;
    private static final Integer[] VmDowntimeDistribution;
    private static final Integer[] WorkloadToOverusageDistribution;
    private static final Integer[] WorkloadToDirtyPageDistr;
    private static final Double[] PowerOutageDistr;
    private static final Double[] PowerOutageDurationDistr;

    static {
        TwoWorkloadsSumDistribution = getTwoWorkloadsSumDistribution(
                WorkloadLevel.All().length, WorkloadLevel.All().length, WorkloadLevel.AllWithOverusage().length);
        TwoWorkloadsAndDistribution = getTwoWorkloadsAndDistribution();
        MigrationTimeDistribution = getMigrationTimeDistr();
        VmDowntimeDistribution = getVmDowntimeDistribution();
        WorkloadToOverusageDistribution = getWorkloadToOverusageDistribution();
        WorkloadToDirtyPageDistr = getWorkloadToDirtyPageDistr();
        PowerOutageDistr = getPowerOutageDistr();
        PowerOutageDurationDistr = getPowerOutageDurationDistr();
    }

    public static Integer[] getTwoWorkloadsSumDistribution(int src1Levels, int src2Levels, int targetLevels) {
        Integer[] result = new Integer[src1Levels * src2Levels * targetLevels];

        int index = 0;
        for (int i = 0; i < src1Levels; i++) {
            for (int j = 0; j < src2Levels; j++) {
                int counter = j + i;
                int sum = 100;
                for (int k = 0; k < targetLevels - 1; k++) {
                    if (k == counter || k == counter + 1) {
                        result[index++] = 50;
                        sum -= 50;
                    } else {
                        result[index++] = 0;
                    }
                }
                Ensure.BetweenInclusive(sum, 0, 100, "sum");
                result[index++] = sum;
            }
        }

        return result;
    }

    private static Integer[] getTwoWorkloadsAndDistribution() {
        Integer[] result = new Integer[WorkloadLevel.AllWithOverusage().length * WorkloadLevel.AllWithOverusage().length * WorkloadLevel.AllWithOverusage().length];

        int index = 0;
        for (int i = 0; i < WorkloadLevel.AllWithOverusage().length; i++) {
            for (int j = 0; j < WorkloadLevel.AllWithOverusage().length; j++) {
                int level = FastMath.max(i, j);
                for (int k = 0; k < WorkloadLevel.AllWithOverusage().length; k++) {
                    if (k == level) {
                        result[index++] = 100;
                    } else {
                        result[index++] = 0;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns dstribution for migrationTime node. It assumes the following
     * order of nodes: dirtyPageRate, dbBw, vmSize.
     *
     * @return distribution for node MigrationTime
     */
    private static Double[] getMigrationTimeDistr() {
        Double[] result = new Double[WorkloadLevel.AllWithOverusage().length
                * DirtyPageLevel.All().length
                * VmSizeLevel.All().length
                * BwLevel.All().length];

        double levelStep = (100.0 / WorkloadLevel.All().length);
        int index = 0;
        for (int i = 0; i < BwLevel.All().length; i++) {
            for (int j = 0; j < VmSizeLevel.All().length; j++) {
                for (int k = 0; k < DirtyPageLevel.All().length; k++) {

                    // obtain sizes
                    long vmSize = VmSizeLevel.getVmSizeByLevel(VmSizeLevel.All()[j]);
                    long bw = BwLevel.getBwByLevel(BwLevel.All()[i]);
                    int dpr = DirtyPageLevel.getRateByLevel(DirtyPageLevel.All()[k]);

                    // calc time
                    long migrTimeUB = getMigrationTimeUB(vmSize, bw, dpr);
                    // get distribution
                    double[] distr = getWorkloadDistrByAverageValue(migrTimeUB);
                    // inset distr
                    for (int l = 0; l < distr.length; l++) {
                        result[index++] = distr[l];
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns upper bound of migration time in seconds.
     *
     * @param vmSize
     * @param bw
     * @param dirtyPageRate
     * @return
     */
    public static long getMigrationTimeUB(long vmSize, long bw, int dirtyPageRate) {
        return (long) FastMath.ceil(1000 * dirtyPageRate * vmSize / (double) bw);
    }

    /**
     * Returns distr for the node 'CurrVmDowntimeDuration'
     *
     * @return
     */
    private static Integer[] getVmDowntimeDistribution() {
        Integer[] result = new Integer[WorkloadLevel.AllWithOverusage().length * WorkloadLevel.AllWithOverusage().length * 2];
        int index = 0;
        for (int i = 0; i < WorkloadLevel.AllWithOverusage().length; i++) {
            for (int j = 0; j < WorkloadLevel.AllWithOverusage().length; j++) {
                int value = 0;
                if (i == j) {
                    value = 1;
                }
                result[index++] = value;
            }
        }

        // fill another part of cpt by zero for the case if powerOutage = FALSE
        for (int i = 0; i < WorkloadLevel.AllWithOverusage().length; i++) {
            for (int j = 0; j < WorkloadLevel.AllWithOverusage().length; j++) {
                if (j == 0) {
                    result[index++] = 1;
                } else {
                    result[index++] = 0;
                }
            }
        }

        return result;
    }

    private static Integer[] getWorkloadToOverusageDistribution() {
        Integer[] result = new Integer[WorkloadLevel.AllWithOverusage().length * 2];

        int index = 0;
        for (int j = 0; j < WorkloadLevel.AllWithOverusage().length - 1; j++) {
            result[index++] = 0;
            result[index++] = 1;
        }
        result[index++] = 1;
        result[index++] = 0;

        return result;
    }

    private static Integer[] getWorkloadToDirtyPageDistr() {
        Integer[] result = new Integer[DirtyPageLevel.All().length * WorkloadLevel.All().length * 2];

        int index = 0;
        List<String> dirtyPageLevels = Arrays.asList(DirtyPageLevel.All());
        List<Boolean> boolLevels = Arrays.asList(true, false);
        for (Boolean pmInDc : boolLevels) {
            for (int j = 0; j < WorkloadLevel.All().length; j++) {
                double workload = j / 10.0;
                String level = DirtyPageLevel.getLevel(workload, pmInDc);
                for (String dpLevel : dirtyPageLevels) {
                    if (level.equalsIgnoreCase(dpLevel)) {
                        result[index++] = 1;
                    } else {
                        result[index++] = 0;
                    }
                }
            }
        }

        return result;
    }

    private static Double[] getPowerOutageDistr() {

        // boolean levels are used
        Double[] result = new Double[CountryLevel.All().length * 2];
        int index = 0;
        for (int i = 0; i < CountryLevel.All().length; i++) {
            double powerOutage = Locations.byCountry(CountryLevel.All()[i]).getPowerOutageProbability();
            result[index++] = powerOutage;
            result[index++] = 1.0 - powerOutage;
        }
        return result;
    }

    private static Double[] getPowerOutageDurationDistr() {

        Double[] result = new Double[CountryLevel.All().length * WorkloadLevel.AllWithOverusage().length * 2];
        int index = 0;

        for (int i = 0; i < CountryLevel.All().length; i++) {
            // calc caidi time
            double caidi = Locations.byCountry(CountryLevel.All()[i]).getCAIDI();
            // get distribution
            double[] distr = getWorkloadDistrByAverageValue(caidi);
            // inset distr
            for (int j = 0; j < distr.length; j++) {
                result[index++] = distr[j];
            }
        }

        // fill another part of cpt by zero for the case if powerOutage = FALSE
        for (int i = 0; i < CountryLevel.All().length; i++) {
            for (int j = 0; j < WorkloadLevel.AllWithOverusage().length; j++) {
                if (j == 0) {
                    result[index++] = 1.0;
                } else {
                    result[index++] = 0.0;
                }
            }
        }

        return result;
    }

    private static double[] getWorkloadDistrByAverageValue(double time) {

        double[] result = new double[WorkloadLevel.AllWithOverusage().length];
        int index = 0;
        double levelStep = (100.0 / WorkloadLevel.All().length);

        // get percent
        double percent = (100 * time) / (double) MaxSlaViolationTime;
        //specify distr
        NormalDistribution d = new NormalDistribution(percent / 2, percent / 6);

        // get probabilites
        double[] probabilities = new double[WorkloadLevel.AllWithOverusage().length];
        double maxp = 0.0d;
        for (int l = 0; l < WorkloadLevel.AllWithOverusage().length - 1; l++) {
            double p = d.probability(l * levelStep, (l + 1) * levelStep);
            probabilities[l] = p;
            maxp = FastMath.max(maxp, p);
        }
        probabilities[WorkloadLevel.AllWithOverusage().length - 1] = d.probability(WorkloadLevel.AllWithOverusage().length * levelStep, Double.POSITIVE_INFINITY);

        if (maxp != 0.0d) {

            // and insert their normalized values into result array 
            double sum = 1.0d;
            for (int l = 0; l < probabilities.length - 1; l++) {
                double newValue = FastMath.min(sum, probabilities[l] / maxp);
                result[index++] = newValue;
                sum -= newValue;
            }
            result[index++] = sum;
            Ensure.GreaterThanOrEquals(sum, 0d, "sum of probabilities");
        } else {
            // if no max probability found, just put 1.0 for the closest level
            // let say, if percent = 42 and there are 10 levels, levelIndex = would be 4
            int levelIndex = (int) FastMath.floor(percent / levelStep);
            if (levelIndex > WorkloadLevel.All().length) {
                levelIndex = WorkloadLevel.AllWithOverusage().length - 1;
            }
            Ensure.GreaterThanOrEquals(levelIndex, 0, "levelIndex");
            for (int l = 0; l < WorkloadLevel.AllWithOverusage().length; l++) {
                if (l == levelIndex) {
                    result[index++] = 1.0d;
                } else {
                    result[index++] = 0.0d;
                }
            }
        }

        return result;
    }

}
