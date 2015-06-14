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

import cloudnet.azure.Azure;
import cloudnet.util.Ensure;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class VmSizeLevel {

    private VmSizeLevel() {
    }

    public static final String A0 = "A0";
    public static final String A1 = "A1";
    public static final String A2 = "A2";
    public static final String A3 = "A3";
    public static final String A4 = "A4";
    public static final String A5 = "A5";
    public static final String A6 = "A6";
    public static final String A7 = "A7";
    public static final String A8 = "A8";
    public static final String A9 = "A9";

    private static final String[] All;

    static {
        All = new String[]{A0, A1, A2, A3, A4, A5, A6, A7, A8, A9};
    }

    public static String[] All() {
        return All;
    }

    public static final long getVmSizeByLevel(String level) {
        switch (level) {
            case A0:
                return Azure.RamA0;

            case A1:
                return Azure.RamA1;

            case A2:
                return Azure.RamA2;

            case A3:
                return Azure.RamA3;

            case A4:
                return Azure.RamA4;

            case A5:
                return Azure.RamA5;

            case A6:
                return Azure.RamA6;

            case A7:
                return Azure.RamA7;

            case A8:
                return Azure.RamA8;

            case A9:
                return Azure.RamA9;
        }

        throw new IllegalArgumentException("Vm Ram level not found");
    }

    public static final String getLevel(long ram) {
        Ensure.GreaterThanOrEquals(ram, 0L, "ram");
        if (ram < Azure.RamA1) {
            return A0;
        } else if (ram < Azure.RamA2) {
            return A1;
        } else if (ram < Azure.RamA3) {
            return A2;
        } else if (ram < Azure.RamA4) {
            return A3;
        } else if (ram < Azure.RamA5) {
            return A4;
        } else if (ram < Azure.RamA6) {
            return A5;
        } else if (ram < Azure.RamA7) {
            return A6;
        } else if (ram < Azure.RamA8) {
            return A7;
        } else if (ram < Azure.RamA9) {
            return A8;
        } else {
            return A9;
        }
    }

}
