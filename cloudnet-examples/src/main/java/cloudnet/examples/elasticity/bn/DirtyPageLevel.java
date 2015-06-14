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
package cloudnet.examples.elasticity.bn;

import cloudnet.util.Ensure;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class DirtyPageLevel {

    public static final String Low = "low"; // 1
    public static final String Middle = "middle"; //3
    public static final String High = "high"; //5

    private static final String[] All;

    static {
        All = new String[]{Low, Middle, High};
    }

    public static String[] All() {
        return All;
    }

    public static int getRateByLevel(String level) {
        switch (level) {
            case Low:
                return 1;

            case Middle:
                return 3;

            case High:
                return 5;
        }

        throw new IllegalArgumentException("Dirty page level not found");
    }
    
    public static String getLevel(double ramWorkload, boolean sameDC) {
        Ensure.BetweenInclusive(ramWorkload, 0.0, 1.0, "ramWorkload");
        if (sameDC || ramWorkload < 0.25) {
            return Low;
        } else if (ramWorkload < 0.75) {
            return Middle;
        } else {
            return High;
        }
    }
}
