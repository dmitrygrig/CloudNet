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

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class TemperatureLevel {
    
    private TemperatureLevel(){
        
    }

    public static final String VeryLow = "<15";
    public static final String Low = "-15 - 0";
    public static final String Middle = "0 - 15";
    public static final String High = "15 - 30";
    public static final String VeryHigh = ">30";

    public static String getLevel(double temperature) {
        if (temperature < -15) {
            return VeryLow;
        } else if (temperature < 0) {
            return Low;
        } else if (temperature < 15) {
            return Middle;
        } else if (temperature < 30) {
            return High;
        } else {
            return VeryHigh;
        }
    }
    
    private static final String[] All;
    
    static {
        All = new String[]{
            TemperatureLevel.VeryLow,
            TemperatureLevel.Low,
            TemperatureLevel.Middle,
            TemperatureLevel.High,
            TemperatureLevel.VeryHigh
        };
    }

    public static String[] All() {
        return All;
    }
}
