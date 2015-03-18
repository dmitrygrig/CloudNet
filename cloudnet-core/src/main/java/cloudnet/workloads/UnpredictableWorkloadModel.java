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
 * Unpredicatable Workloads are characterized by a random and unforeseeable
 * utilization over time experience unpredictable workload.
 *
 * @url{http://www.cloudcomputingpatterns.org/Unpredictable_Workload}
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class UnpredictableWorkloadModel extends ChangableWorkloadModelBase {

    private final Random random;
    private final long changeDuration;
    private long lastChange;
    private double lastWorkload;

    public UnpredictableWorkloadModel(long changeDuration, long seed) {
        this.random = new Random(seed);
        this.changeDuration = changeDuration;
        lastWorkload = random.nextDouble();
        lastChange = 0;
    }

    @Override
    protected double generateWorkload(long timestamp) {

        if (timestamp - lastChange > changeDuration) {
            lastWorkload = random.nextDouble();
            lastChange = timestamp;
        }

        return lastWorkload;
    }

}
