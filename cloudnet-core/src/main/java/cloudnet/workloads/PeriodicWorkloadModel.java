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
package cloudnet.workloads;

import java.util.Random;
import org.apache.commons.math3.util.FastMath;

/**
 * In our real lives periodic tasks and routines are very common. For example,
 * monthly paychecks, monthly telephone bills, yearly car checkups, weekly
 * status reports, or the daily use of public transport during rush hour, all
 * these tasks and routines occur in well-defined intervals.
 *
 * (http://www.cloudcomputingpatterns.org/Periodic_Workload)
 *
 * Implementation uses function f(x)=sin(x) in order simulate cyclicity.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class PeriodicWorkloadModel extends ChangableWorkloadModelBase {

    private final double lowerThreshold;
    private final double upperThreshold;
    private final long period; // in ms
    private final double deviation;
    private final Random random;

    public PeriodicWorkloadModel(double lowerThreshold, double upperThreshold,
            double deviation, long period, long seed) {

        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.deviation = deviation;
        this.period = period;
        this.random = new Random(seed);
    }

    @Override
    protected double generateWorkload(long timestamp) {
        // workload = lower + diff_upper_lower * ((sin(x) + 1)/2 + deviation)
        double periodicValue = (FastMath.sin(timestamp * 2 * FastMath.PI / period) + 1) / 2;
        double randomValue = (2 * (random.nextDouble() - 0.5) * deviation);
        double workload = lowerThreshold + (upperThreshold - lowerThreshold) * (periodicValue + randomValue);
        return workload;
    }

}
