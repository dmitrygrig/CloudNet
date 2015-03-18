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
package cloudnet.core;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class TimeFrame {

    private TimeFrame() {

    }

    public static final long Ms = 1;
    public static final long Sec = 1000 * Ms;
    public static final long Minute = 60 * Sec;
    public static final long Hour = 60 * Minute;
    public static final long Day = 24 * Hour;
    public static final long Month = (long) (365 / 12.0 * Day);
    public static final long Year = 365 * Day;

    public static double msToHour(long durationMs) {
        return durationMs / (double) Hour;
    }

    public static double msToSec(long durationMs) {
        return durationMs / (double) Sec;
    }

}
