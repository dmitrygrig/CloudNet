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
package cloudnet.examples.locations;

import cloudnet.core.EnergyPriceModel;
import cloudnet.core.TimeFrame;
import cloudnet.locations.BaseLocation;
import cloudnet.locations.BaseLocation;
import cloudnet.locations.Location;
import cloudnet.locations.Location;
import cloudnet.workloads.ConstantWorkloadModel;
import java.time.ZoneId;

/**
 * Implementation of Location interface for the city Rio de Janeiro (Brazil).
 * Data about electricity pricing (
 * http://en.wikipedia.org/wiki/Electricity_pricing) Temperature data (
 * http://en.wikipedia.org/wiki/Rio_de_Janeiro)
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class RioDeJaneiro extends BaseLocation implements Location {

    private static final String TimeZoneString = "Brazil/East";
    private static final String Country = "Brazil";
    private static final ZoneId TimeZone = ZoneId.of(TimeZoneString);

    private static final double EnergyPrice = .162; // .12 USD/kWh

    private static final int NightToDayHour = 8; // 8 am
    private static final int DayToNightHour = 23; // 11 pm

    @Override
    public String getDescription() {
        return TimeZoneString;
    }

    @Override
    public String getCountry() {
        return Country;
    }

    @Override
    public ZoneId getTimeZone() {
        return TimeZone;
    }

    @Override
    public int getDayToNightSwitchHour() {
        return DayToNightHour;
    }

    @Override
    public int getNightToDaySwitchHour() {
        return NightToDayHour;
    }

    @Override
    public EnergyPriceModel getEnergyPrice() {
        return new EnergyPriceModel(new ConstantWorkloadModel(EnergyPrice));
    }

//    @Override
//    public TemperatureModel getTemperature() {
//        return new GeneratedTemperatureModel(new PeriodicWorkloadModel(21.2, 27.2, .05, TimeFrame.Day, 7));
//    }

    @Override
    public String getCity() {
        return "Rio";
    }

    @Override
    public long getSAIDI() {
        // 2013 year: http://earlywarn.blogspot.co.at/2013/05/international-power-outage-comparisons.html
        return (long) (1101.6 * TimeFrame.Minute);
    }

    @Override
    public long getCAIDI() {
        return 25 * TimeFrame.Minute;
    }

}
