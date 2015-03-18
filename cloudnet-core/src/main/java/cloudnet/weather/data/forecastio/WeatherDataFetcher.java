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
package cloudnet.weather.data.forecastio;

import cloudnet.weather.RealTemperatureModel;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class WeatherDataFetcher {

    private final static  Logger LOGGER = LoggerFactory.getLogger(RealTemperatureModel.class);

    private final String urlString;

    public WeatherDataFetcher(String urlString) {
        this.urlString = urlString;
    }

    public void fetchData(String locationData, String city, Calendar start, Calendar end) {

        try {
            URL url;
            while (start.before(end)) {
                url = new URL(urlString + locationData + "," + start.getTimeInMillis() / 1000 + "?units=si");
                File file = new File("resources/weather/" + city + "/" + start.getTimeInMillis() / 1000 + ".json");
                LOGGER.trace("downloading " + start.getTimeInMillis() / 1000);
                FileUtils.copyURLToFile(url, file);
                start.add(Calendar.HOUR, 12);
            }

        } catch (MalformedURLException e) {
            LOGGER.error("fetchData", e);
        } catch (IOException e) {
            LOGGER.error("fetchData", e);
        }
    }

}
