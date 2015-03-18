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
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DayNightWorkloadModel implements WorkloadModel {

    private final WorkloadModel dayWorkload;
    private final WorkloadModel nightWorkload;
    private final int nightToDaySwitchHour;
    private final int dayToNightSwitchHour;
    private final Calendar calendar;
    private final long zoneOffset;

    public DayNightWorkloadModel(WorkloadModel dayWorkload, WorkloadModel nightWorkload, ZoneId zoneId, int nightToDaySwitchHour, int dayToNightSwitchHour) {
        Ensure.NotNull(dayWorkload, "dayWorkload");
        Ensure.NotNull(nightWorkload, "nightWorkload");
        Ensure.NotNull(zoneId, "zoneId");

        this.dayWorkload = dayWorkload;
        this.nightWorkload = nightWorkload;
        this.nightToDaySwitchHour = nightToDaySwitchHour;
        this.dayToNightSwitchHour = dayToNightSwitchHour;
        this.calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));
        this.zoneOffset = calendar.get(GregorianCalendar.ZONE_OFFSET);
    }

    @Override
    public double getWorkload(long timestamp) {

        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);

        if (hour >= nightToDaySwitchHour && hour < dayToNightSwitchHour) {
            return dayWorkload.getWorkload(convertTimestamp(timestamp));
        } else {
            return nightWorkload.getWorkload(convertTimestamp(timestamp));
        }
    }

    private long convertTimestamp(long timestamp) {
        return timestamp + zoneOffset;
    }

}
