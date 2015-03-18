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

import java.util.HashMap;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public abstract class ChangableWorkloadModelBase extends WorkloadModelBase {

    private final HashMap<Long, Double> workloadHistory = new HashMap<>();

    private boolean adjustValues;

    @Override
    public double getWorkload(long timestamp) {
        Double entry = workloadHistory.get(timestamp);
        if (entry == null) {
            entry = generateWorkload(timestamp);
            if (adjustValues) {
                entry = adjustToBounds(entry);
            }
            workloadHistory.put(timestamp, entry);
        }
        return entry;
    }

    public boolean isAdjustValues() {
        return adjustValues;
    }

    public void setAdjustValues(boolean adjustValues) {
        this.adjustValues = adjustValues;
    }

    protected abstract double generateWorkload(long timestamp);
}
