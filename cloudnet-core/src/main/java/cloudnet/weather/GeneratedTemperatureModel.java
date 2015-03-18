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
package cloudnet.weather;

import cloudnet.workloads.WorkloadModel;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class GeneratedTemperatureModel implements TemperatureModel {

    private final WorkloadModel model;

    public GeneratedTemperatureModel(WorkloadModel model) {
        this.model = model;
    }

    @Override
    public double getTemperature(long timestamp) {
        return model.getWorkload(timestamp);
    }
//
//    @Override
//    public double getForecast(long timestamp, long currentTimestamp) {
//        return model.getWorkload(timestamp);
//    }
}
