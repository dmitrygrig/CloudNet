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

/**
 * Static Workloads are characterized by a more-or-less flat utilization profile
 * over time within certain boundaries.
 *
 * (http://www.cloudcomputingpatterns.org/Static_Workload)
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class StaticWorkloadModel extends ChangableWorkloadModelBase {

    private final double baseWorkload;
    private final double deviation;
    private final Random random;

    public StaticWorkloadModel(double baseWorkload, double deviation, long seed) {
        this.baseWorkload = baseWorkload;
        this.deviation = deviation;
        this.random = new Random(seed);
    }

    @Override
    protected double generateWorkload(long timestamp) {
        // workload = base + deviation
        double workload = baseWorkload + 2 * (random.nextDouble() - 0.5) * deviation;
        return workload;
    }

}
