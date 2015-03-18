/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudnet.weather.data.forecastio;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class WeatherDataFethApp {

    private final static String URL_STRING = "https://api.forecast.io/forecast/{your_token}/";

    public static void main(String[] args) {

        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        start.set(2013, Calendar.JANUARY, 1, 0, 0, 0);

        Calendar end = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        end.set(2014, Calendar.JANUARY, 1, 0, 0, 0);

        final String viennaLocation = "48.2000,16.3667";
        final String vienna = "vienna";

        WeatherDataFetcher fether = new WeatherDataFetcher(URL_STRING);
        fether.fetchData(viennaLocation, vienna, start, end);
    }
}
