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

import cloudnet.azure.Azure;
import cloudnet.util.Ensure;


/**
 * Bandwith capacity level
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class BwLevel {

    private BwLevel() {
    }
    public static final String Slow = "Slow";
    public static final String Medium = "Medium";
    public static final String Fast = "Fast";
    public static final String Infini = "Infini";

    private static final String[] All;

    static {
        All = new String[]{Slow, Medium, Fast, Infini};
    }

    public static String[] All() {
        return All;
    }

    public static final long getBwByLevel(String level) {
        switch (level) {
            case Slow:
                return Azure.SlowBw;

            case Medium:
                return Azure.MediumBw;

            case Fast:
                return Azure.FastBw;

            case Infini:
                return Azure.InfiniBw;
        }

        throw new IllegalArgumentException("Bw level not found");
    }

    public static final String getLevel(long bw) {
        Ensure.GreaterThanOrEquals(bw, 0L, "bw");
        if (bw < Azure.MediumBw) {
            return Slow;
        } else if (bw < Azure.FastBw) {
            return Medium;
        } else if (bw < Azure.InfiniBw) {
            return Fast;
        } else {
            return Infini;
        }
    }
}
