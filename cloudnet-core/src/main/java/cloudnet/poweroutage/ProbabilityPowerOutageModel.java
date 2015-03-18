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
package cloudnet.poweroutage;

import cloudnet.core.TimeFrame;
import cloudnet.util.Ensure;
import org.apache.commons.math3.util.FastMath;

/**
 * ProbabilityPowerOutageModel implements PowerOutageModel using probability of
 * power outage and avg duration of each power outage.
 * <p>
 * It divides the whole time line into intervals, where duration of each
 * interval can be computed as follows:
 * <pre>
 * {@code
 * periodDuration =Year / timesPerYear;
 * }
 * </pre> When duration for the specific period is requested it checks what
 * periods are captured by requested interval and then compute occurence of
 * interval in each power outage interval. Power otuage start and finish time
 * for each interval are computed using function sin(x) as follows:
 * * <pre>
 * {@code
 * poFrom = (long) ((FastMath.sin(periodIndex) + 1d) / 2) * (periodDuration - * avgDuration);
 * poTill = poFrom + avgDuration;
 * }
 * </pre>
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class ProbabilityPowerOutageModel implements PowerOutageModel {

    private final long avgDuration;
    private final long periodDuration;

    public ProbabilityPowerOutageModel(long avgDuration, long timesPerYear) {
        this(avgDuration, timesPerYear, TimeFrame.Year);
    }

    public ProbabilityPowerOutageModel(long avgDuration, long timePerBasePeriod, long baseTimeFrame) {
        Ensure.GreaterThanOrEquals(avgDuration, 0d, "avgDuration");
        Ensure.GreaterThanOrEquals(timePerBasePeriod, 0d, "timesPerYear");
        Ensure.GreaterThan(baseTimeFrame, 0d, "baseTimeFrame");

        this.avgDuration = avgDuration;
        this.periodDuration = (long) (baseTimeFrame / (double) timePerBasePeriod);
    }

    @Override
    public long getDuration(final long from, final long till) {
        Ensure.GreaterThanOrEquals(from, 0L, "from");
        Ensure.GreaterThanOrEquals(till, from, "Till should be greater than from");

        long periodFrom = from / periodDuration;
        long periodTill = till / periodDuration;
        long result = 0L;
        for (long i = periodFrom; i < periodTill + 1; i++) {
            // compute po duration starttime for this period
            double sinx = FastMath.sin(i);
            final long poFrom = (long) ((sinx + 1.0) / 2.0) * (periodDuration - avgDuration) + i * periodDuration;
            final long poTill = poFrom + avgDuration;
            // a=poFrom, b=poTill, c=from, d=till
            if (from >= poTill) {//a,b,c,d
                // do nothing
            } else if (from < poTill && till >= poTill) {//a,c,b,d
                result += poTill - poFrom;
            } else if (from < poTill && from >= poFrom && till <= poTill) {//a,c,d,b
                result += till - from;
            } else if (from < poFrom && till >= poFrom) {//c,a,d,b
                result += till - poFrom;
            } else if (from <= poFrom && till >= poTill) {//c,a,b,d
                result += poTill - poFrom;
            } else if (till <= poFrom) {//c,d,a,b
                // do nothing
            } else {
                throw new IllegalStateException("Not all cases captured");
            }
        }
        return result;
    }

}
