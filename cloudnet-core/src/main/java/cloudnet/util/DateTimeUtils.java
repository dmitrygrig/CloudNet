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
package cloudnet.util;

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DateTimeUtils {

    private DateTimeUtils() {

    }

    public static String getDateTime(long timestamp, String zoneId) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));
        calendar.setTimeInMillis(timestamp);
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH) + 1, //http://stackoverflow.com/questions/344380/why-is-january-month-0-in-java-calendar
                calendar.get(GregorianCalendar.DAY_OF_MONTH),
                calendar.get(GregorianCalendar.HOUR_OF_DAY),
                calendar.get(GregorianCalendar.MINUTE),
                calendar.get(GregorianCalendar.SECOND)
        );
    }

    /**
     * Returns whether defined timestamp defines daytime or nighttime
     * considering specified time zone and day/night switch hours.
     *
     * @param timestamp
     * @param zoneId
     * @param nightToDaySwitchHour
     * @param dayToNightSwitchHour
     * @return True if it is day, otherwise False.
     */
    public static boolean isDayOrNight(long timestamp, String zoneId, int nightToDaySwitchHour, int dayToNightSwitchHour) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));
        calendar.setTimeInMillis(timestamp);
        int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        return hour >= nightToDaySwitchHour && hour < dayToNightSwitchHour;
    }
}
