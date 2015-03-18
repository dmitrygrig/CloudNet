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

/**
 * Continuously Changing Workload is characterized by an ongoing continuous
 * growth or decline of the utilization.
 *
 * (http://www.cloudcomputingpatterns.org/Continuously_Changing_Workload)
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class ContinuouslyChangingWorkloadModel extends ChangableWorkloadModelBase {

    private final double startValue;
    private final double finishValue;
    private final double changeValue;
    private final long period;
    private final long startOfChangeTime;

    public ContinuouslyChangingWorkloadModel(double startValue, double finishValue, double changeValue, long period, long startOfChangeTime) {
        if (changeValue > 0.0) {
            Ensure.GreaterThan(finishValue, startValue, "finishValue");
        } else if (changeValue < 0.0) {
            Ensure.LowerThan(finishValue, startValue, "finishValue");
        }
        Ensure.GreaterThanOrEquals(startOfChangeTime, 0L, "startOfChangeTime");

        this.startValue = startValue;
        this.finishValue = finishValue;
        this.changeValue = changeValue;
        this.period = period;
        this.startOfChangeTime = startOfChangeTime;
    }

    @Override
    protected double generateWorkload(long timestamp) {
        // workload increases (decreases) after timestamp >= startOfChangeTime, until it reaches finishValue.
        if (timestamp >= startOfChangeTime) {
            double workload = startValue + changeValue * (timestamp - startOfChangeTime) / period;
            if ((changeValue > 0.0 && workload >= finishValue) || (changeValue < 0.0 && workload <= finishValue)) {
                workload = finishValue;
            }
            return workload;
        } else {
            return startValue;
        }
    }

}
