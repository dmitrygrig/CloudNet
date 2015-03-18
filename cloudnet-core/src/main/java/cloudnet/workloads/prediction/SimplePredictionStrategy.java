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
package cloudnet.workloads.prediction;

import cloudnet.util.Ensure;
import cloudnet.workloads.WorkloadHistory;

/**
 * Implementation of WorkloadPredictionStrategy that searches first entry with
 * timestamp that equals or less than
 * @code currTimeStamp returns its value.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class SimplePredictionStrategy implements WorkloadPredictionStrategy {

    @Override
    public Long predictValue(long futureTimeStamp, long currTimeStamp, WorkloadHistory history) {
        Ensure.NotNull(history, "history");
        return history.getFirstWorkloadBefore(currTimeStamp);
    }
    
    @Override
    public String toString(){
        return "sps";
    }

}
