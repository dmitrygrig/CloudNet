/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cloudnet.weather;

import cloudnet.core.Vm;
import cloudnet.util.DateTimeUtils;
import cloudnet.util.Ensure;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@code TemperatureModel} that uses temperature data fetched
 * from http://forecast.io/ . Data should be saved in the local disk.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class RealTemperatureModel implements TemperatureModel {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Vm.class);

    private final String location;
    private final TreeMap<Long, Double> data = new TreeMap<>();

    public RealTemperatureModel(String location) {
        Ensure.NotNull(location, "location");
        this.location = location.toLowerCase();
        readData("resources/weather/" + location + ".csv");
    }

    private void readData(String filename) {
        boolean isHeader = true;
        LOGGER.trace("Reading temperature for location %s from %s...", location, filename);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            for (String line; (line = br.readLine()) != null;) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // format: Date,Timestamp,Temperature
                // 2012-12-30 23:00:00,1356904800,18.89
                String[] parts = line.split(",");
                Long timestamp = Long.parseLong(parts[1]);
                Double temp = Double.parseDouble(parts[2]);
                data.put(timestamp, temp);
            }
        } catch (Exception ex) {
            LOGGER.error("readTempData", ex);
        }
    }

    @Override
    public double getTemperature(long timestamp) {
        LOGGER.trace("Get temperature in %s at %s UTC", location, DateTimeUtils.getDateTime(timestamp, "UTC"));
        Double value = data.get(timestamp);

        if (value != null) {
            return value;
        } else {
            Entry<Long, Double> entry = data.lowerEntry(timestamp);
            if (entry != null) {
                return entry.getValue();
            } else {
                entry = data.higherEntry(timestamp);
                if (entry != null) {
                    return entry.getValue();
                }
            }
        }

        throw new IllegalArgumentException(String.format("Temperature for location %s for time %d was not found.", location, timestamp));
    }
}
