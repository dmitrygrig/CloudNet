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

import cloudnet.core.EnergyPriceModel;
import cloudnet.poweroutage.PowerOutageModel;
import cloudnet.weather.TemperatureModel;
import java.time.ZoneId;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public interface Location {

    String getDescription();

    String getCountry();

    String getCity();

    ZoneId getTimeZone();

    int getDayToNightSwitchHour();

    int getNightToDaySwitchHour();

    EnergyPriceModel getEnergyPrice();

    TemperatureModel getTemperature();

    PowerOutageModel getPowerOutage();

    long getSAIDI(); // sum of all power outage durations 

    long getCAIDI(); // single power outage duration
    
    long getSAIFI(); // number of interruptions
    
    double getPowerOutageProbability();

}
