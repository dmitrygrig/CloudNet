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
import cloudnet.workloads.DayNightWorkloadModel;
import java.time.ZoneId;

/**
 * Implementation of Location interface for the city Oslo (Norway). Data about
 * electricity pricing: Day tariff
 * {@link https://www.wienenergie.at/eportal/ep/programView.do/pageTypeId/11986/programId/19402/channelId/-27929}
 * , Night tariff
 * {@link https://www.wienenergie.at/eportal/ep/programView.do/pageTypeId/11986/programId/19403/channelId/-27929}
 * Temperature data {@link  http://en.wikipedia.org/wiki/Oslo}
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Vienna extends BaseLocation implements Location {

    private static final String TimeZoneString = "Europe/Vienna";
    private static final String Country = "Austria";
    private static final ZoneId TimeZone = ZoneId.of(TimeZoneString);

        private static final double DayEnergyPrice = .2484; // .2484 USD/kWh
    private static final double NightEnergyPrice = .1678;

    private static final int NightToDayHour = 6; // 6 am
    private static final int DayToNightHour = 22; // 10 pm

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
        return new EnergyPriceModel(
                new DayNightWorkloadModel(
                        new ConstantWorkloadModel(DayEnergyPrice),
                        new ConstantWorkloadModel(NightEnergyPrice),
                        TimeZone,
                        NightToDayHour,
                        DayToNightHour));
    }

//    @Override
//    public TemperatureModel getTemperature() {
//        return new GeneratedTemperatureModel(new PeriodicWorkloadModel(6.7, 14.5, .05, TimeFrame.Day, 7));
//    }

    @Override
    public String getCity() {
        return "Vienna";
    }

    @Override
    public long getSAIDI() {
        // 2002-2008years: http://www.e-control.at/portal/pls/portal/portal.kb_folderitems_xml.redirectToItem?pMasterthingId=253211
        return (long) ((35.23 + 38.43 + 30.33 + 31.35 + 48.07 + 45.47 + 43.69) / 7 * TimeFrame.Minute);
    }

    @Override
    public long getCAIDI() {
        return 3 * TimeFrame.Minute;
    }
    
    

}
