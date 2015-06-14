/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cloudnet.workloads.prediction;

import cloudnet.util.Ensure;
import cloudnet.workloads.WorkloadHistory;
import java.util.Map;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class SimpleRegressionPredictionStrategy implements WorkloadPredictionStrategy {

    @Override
    public Long predictValue(long futureTimeStamp, long currTimeStamp, WorkloadHistory history) {
        Ensure.NotNull(history, "history");

        SimpleRegression r = new SimpleRegression();
        for (Map.Entry<Long, Long> entry : history.getWorkloadHistory().entrySet()) {
            r.addData(entry.getKey(), entry.getValue());
        }

        double predicted = r.predict(futureTimeStamp);
        return predicted == Double.NaN || predicted < 0 ? null : (long) predicted;
    }

    @Override
    public String toString() {
        return "srps";
    }

}
