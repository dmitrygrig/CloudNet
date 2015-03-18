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
package cloudnet.locations;

import cloudnet.poweroutage.PowerOutageModel;
import cloudnet.weather.TemperatureModel;
import cloudnet.core.TimeFrame;
import cloudnet.poweroutage.ProbabilityPowerOutageModel;
import cloudnet.weather.RealTemperatureModel;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public abstract class BaseLocation implements Location {

    @Override
    public long getSAIFI() {
        return getSAIDI() / getCAIDI();
    }

    @Override
    public double getPowerOutageProbability() {
        return (double) getSAIDI() / TimeFrame.Year;
    }

    @Override
    public PowerOutageModel getPowerOutage() {
        return new ProbabilityPowerOutageModel(getCAIDI(), getSAIFI());
    }
    
    @Override
    public TemperatureModel getTemperature() {
        return new RealTemperatureModel(getCity());
    }
}
