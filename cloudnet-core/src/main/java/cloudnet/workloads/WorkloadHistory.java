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
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class WorkloadHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadHistory.class);

    private TreeMap<Long, Long> workloadHistory = new TreeMap<>();

    private int shrinkAfter = 100;
    private long shrinkTo = 10;

    public int getShrinkAfter() {
        return shrinkAfter;
    }

    public void setShrinkAfter(int shrinkAfter) {
        this.shrinkAfter = shrinkAfter;
    }

    public long getShrinkTo() {
        return shrinkTo;
    }

    public void setShrinkTo(long shrinkTo) {
        this.shrinkTo = shrinkTo;
    }

    public Long getWorkload(long timestamp) {
        return workloadHistory.get(timestamp);
    }

    public long getWorkloadOrDefault(long timestamp) {
        Long value = workloadHistory.get(timestamp);

        if (value != null) {
            return value;
        } else {
            Map.Entry<Long, Long> entry = workloadHistory.lowerEntry(timestamp);
            if (entry != null) {
                return entry.getValue();
            } else {
                entry = workloadHistory.higherEntry(timestamp);
                if (entry != null) {
                    return entry.getValue();
                }
            }
        }

        LOGGER.warn("Workload could not be obtaind. Zero workload will be returned.");
        return 0L;
    }

    public void saveWorkload(long timestamp, Long value) {
        workloadHistory.put(timestamp, value);
        if (shrinkAfter >= 0 && workloadHistory.size() > shrinkAfter) {
            schrinkHistory();
        }
    }

    private void schrinkHistory() {
        Ensure.GreaterThan(shrinkTo, 0, "shrinkTo");
        workloadHistory = workloadHistory.descendingMap().entrySet()
                .stream()
                .limit(this.shrinkTo)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (v1, v2) -> v1, TreeMap::new));
    }

    public Long getFirstWorkloadBefore(long timestamp) {
        Long key = workloadHistory.floorKey(timestamp);
        Long workload = key == null ? null : workloadHistory.get(key);
        return workload;
    }

    public TreeMap<Long, Long> getWorkloadHistory() {
        return workloadHistory;
    }
}
