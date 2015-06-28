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

import cloudnet.util.DateTimeUtils;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class FormatConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormatConverter.class);

    private final Map<String, TreeMap<Long, Double>> weatherData = new TreeMap<>();
    private final Map<String, String> locationTimeZone = new HashMap<>();

    public void convert(String inputDir) {
        aggregateData(inputDir);
        writeResults(inputDir);
    }

    private void aggregateData(String inputDir) {
        LOGGER.info("read weather data...");

        File path = new File(inputDir);
        JSONParser parser = new JSONParser();

        for (File cityDir : path.listFiles()) {

            // If not a dir; next file in data path
            if (!cityDir.isDirectory()) {
                continue;
            }

            LOGGER.trace(String.format("Processing %s...", cityDir.getAbsolutePath()));

            String[] extensions = {"json"};

            List<File> fileList = new ArrayList<>(FileUtils.listFiles(cityDir, extensions, false));

            for (File file : fileList) {

                try {
                    try (Reader reader = new BufferedReader(new FileReader(file))) {
                        JSONObject jsonObject = (JSONObject) parser.parse(reader);

                        String timezone = (String) jsonObject.get("timezone");
                        locationTimeZone.put(cityDir.getName(), timezone);

                        Number currentTemp = (Number) ((JSONObject) jsonObject.get("currently")).get("temperature");
                        Number currentTime = (Number) ((JSONObject) jsonObject.get("currently")).get("time");

                        JSONObject hourlyForecast = (JSONObject) jsonObject.get("hourly");
                        JSONArray data = (JSONArray) hourlyForecast.get("data");

                        if (currentTemp != null) {
                            addWeatherdata(currentTime.longValue(), cityDir.getName(), currentTemp.doubleValue());
                        }

                        for (Object o : data) {
                            JSONObject oo = (JSONObject) o;

                            Number time = (Number) oo.get("time");
                            Number temp = (Number) oo.get("temperature");

                            long timeDiff = time.longValue() - currentTime.longValue();

                            if (temp != null) {
                                addWeatherdata(currentTime.longValue() + timeDiff, cityDir.getName(), temp.doubleValue());
                            }
                        }
                    }

                } catch (IOException | ParseException e) {
                    LOGGER.error("aggregateData", e);
                }
            }
        }
    }

    private void addWeatherdata(long datetime, String location, double temperature) {
        TreeMap<Long, Double> map;
        if (weatherData.containsKey(location)) {
            map = weatherData.get(location);
        } else {
            map = new TreeMap<>();
            weatherData.put(location, map);
        }
        map.put(datetime, temperature);
    }

    private void writeResults(String inputDir) {
        LOGGER.info("Writing results...");

        for (Map.Entry<String, TreeMap<Long, Double>> entry : weatherData.entrySet()) {

            String location = entry.getKey();

            File outputFile = new File(inputDir + location + ".csv");
            try (Writer outputWriter = new BufferedWriter(new FileWriter(outputFile));) {

                outputWriter.write("Date,Timestamp,Temperature\r\n");
                String timezone = locationTimeZone.get(location);

                for (Map.Entry<Long, Double> item : entry.getValue().entrySet()) {
                    long currentTime = item.getKey();
                    String line = String.format(new Locale("en", "US"), "%s,%d,%.2f\r\n",
                            DateTimeUtils.getDateTime(currentTime * 1000, timezone),
                            currentTime * 1000,
                            weatherData.get(location).get(currentTime));
                    outputWriter.write(line);
                }

                LOGGER.info("Finished output to " + outputFile.getAbsolutePath());
            } catch (Exception e) {
                LOGGER.error("writeResults", e);
            }
        }

        LOGGER.info("Writing results finished.");
    }

}
