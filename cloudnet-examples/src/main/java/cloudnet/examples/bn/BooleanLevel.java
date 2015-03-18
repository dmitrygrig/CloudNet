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
package cloudnet.examples.bn;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class BooleanLevel {

    public static final String Yes = "yes";
    public static final String No = "no";

    public static String getLevel(boolean value) {
        return value ? Yes : No;
    }
    
    public static boolean getValue(String level) {
        return Yes.equals(level);
    }

    public static String[] All() {
        return BoolLevels;
    }
    
    private static final String[] BoolLevels;

    static {
        BoolLevels = new String[]{BooleanLevel.Yes, BooleanLevel.No};
    }
}
