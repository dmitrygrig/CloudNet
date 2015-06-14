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
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class WorkloadLevel {

    public static final String W10 = "0-10";
    public static final String W20 = "10-20";
    public static final String W30 = "20-30";
    public static final String W40 = "30-40";
    public static final String W50 = "40-50";
    public static final String W60 = "50-60";
    public static final String W70 = "60-70";
    public static final String W80 = "70-80";
    public static final String W90 = "80-90";
    public static final String W100 = "90-100";
    public static final String Overusage = "overusage";

    public static String getLevel(double workload) {
        Ensure.GreaterThanOrEquals(workload, 0.0, "workload");

        if (workload <= 0.1) {
            return W10;
        } else if (workload <= 0.2) {
            return W20;
        } else if (workload <= 0.3) {
            return W30;
        } else if (workload <= 0.4) {
            return W40;
        } else if (workload <= 0.5) {
            return W50;
        } else if (workload <= 0.6) {
            return W60;
        } else if (workload <= 0.7) {
            return W70;
        } else if (workload <= 0.8) {
            return W80;
        } else if (workload <= 0.9) {
            return W90;
        } else if (workload <= 1.0) {
            return W100;
        } else {
            return Overusage;
        }
    }

    public static double getWorkload(String level) {
        Ensure.NotNullOrEmpty(level, "level");

        int index = Arrays.asList(WorkloadLevelsWithOverusage).indexOf(level);

        if (index == -1) {
            throw new IllegalArgumentException("Unexisted level specified.");
        }

        double workload = FastMath.min(1.0, 0.1 * (index + 1));
        return workload;
    }

    public static final String[] All() {
        return WorkloadLevels;
    }

    public static final String[] AllWithOverusage() {
        return WorkloadLevelsWithOverusage;
    }

    private static final String[] WorkloadLevels;
    private static final String[] WorkloadLevelsWithOverusage;

    static {
        WorkloadLevels = new String[]{WorkloadLevel.W10, WorkloadLevel.W20, WorkloadLevel.W30, WorkloadLevel.W40, WorkloadLevel.W50, WorkloadLevel.W60, WorkloadLevel.W70, WorkloadLevel.W80, WorkloadLevel.W90, WorkloadLevel.W100};
        WorkloadLevelsWithOverusage = new String[]{WorkloadLevel.W10, WorkloadLevel.W20, WorkloadLevel.W30, WorkloadLevel.W40, WorkloadLevel.W50, WorkloadLevel.W60, WorkloadLevel.W70, WorkloadLevel.W80, WorkloadLevel.W90, WorkloadLevel.W100, WorkloadLevel.Overusage};
    }
}
