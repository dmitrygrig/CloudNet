/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudnet.weather.data.forecastio;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class FormatConverterApp {

    public static void main(String[] args) {
        FormatConverter converter = new FormatConverter();
        converter.convert("resources/weather");
    }

}
