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

import cloudnet.util.Ensure;
import org.apache.commons.math3.util.FastMath;

/**
 * As a special case of Periodic Workload, the peaks of periodic utilization can
 * occur only once in a very long timeframe. Often, this peak is known in
 * advance as it correlates to a certain event or task.
 *
 * (http://www.cloudcomputingpatterns.org/Once-in-a-lifetime_Workload)
 *
 * Implementation uses f(x)=sin(x) without random variable.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class OnceInAlifetimeWorkloadModel extends ChangableWorkloadModelBase {

    private final double baseValue;
    private final double peekValue;
    private final long period;
    private final long startTime;

    public OnceInAlifetimeWorkloadModel(double baseValue, double peekValue, long period, long startTime) {
        Ensure.GreaterThan(peekValue, baseValue, "peekValue");
        Ensure.GreaterThanOrEquals(startTime, 0L, "startTime");

        this.baseValue = baseValue;
        this.peekValue = peekValue;
        this.startTime = startTime;
        this.period = period;
    }

    @Override
    protected double generateWorkload(long timestamp) {
        // workload = baseValue, if timestamp <= startTime or timestamp > startTime + period /2
        // otherwise workload = periodic workload modelled with sin(x)
        if (timestamp >= startTime && timestamp < startTime + period / 2) {
            double periodicValue = FastMath.sin((timestamp - startTime) * 2 * FastMath.PI / period);
            double workload = baseValue + (peekValue - baseValue) * periodicValue;
            return workload;
        } else {
            return baseValue;
        }
    }

}
