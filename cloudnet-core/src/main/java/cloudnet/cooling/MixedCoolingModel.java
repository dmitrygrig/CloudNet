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
package cloudnet.cooling;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class MixedCoolingModel implements CoolingModel {

    private final double lowerSwitchTemperature;
    private final double upperSwitchTemperature;
    private final CoolingModel lowerModel;
    private final CoolingModel upperModel;

    public MixedCoolingModel(double lowerSwitchTemperature, double upperSwitchTemperature, CoolingModel lowerModel, CoolingModel upperModel) {
        this.lowerSwitchTemperature = lowerSwitchTemperature;
        this.upperSwitchTemperature = upperSwitchTemperature;
        this.lowerModel = lowerModel;
        this.upperModel = upperModel;
    }

    @Override
    public double getpPUE(double temperature) {
        if (temperature < lowerSwitchTemperature) {
            return lowerModel.getpPUE(temperature);
        } else if (temperature > upperSwitchTemperature) {
            return upperModel.getpPUE(temperature);
        } else {
            return (lowerModel.getpPUE(temperature) + upperModel.getpPUE(temperature)) / 2;
        }
    }

    @Override
    public String getMode(double temperature) {
         if (temperature < lowerSwitchTemperature) {
            return lowerModel.getMode(temperature);
        } else if (temperature > upperSwitchTemperature) {
            return upperModel.getMode(temperature);
        } else {
            return "Mixed";
        }
    }

}
