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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class RealTemperatureModelTest {

    public RealTemperatureModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetTemperature() {
        System.out.println("getTemperature");
        long timestamp = 1356912000000L;
        RealTemperatureModel instance = new RealTemperatureModel("capetown");
        double expResult = 17.43;
        double result = instance.getTemperature(timestamp);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetTemperature_Before() {
        System.out.println("getTemperature_Before");
        long timestamp = 1356912000000L - 1;
        RealTemperatureModel instance = new RealTemperatureModel("capetown");
        double expResult = 18.89;
        double result = instance.getTemperature(timestamp);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetTemperature_After() {
        System.out.println("getTemperature_After");
        long timestamp = 1356912000000L + 1;
        RealTemperatureModel instance = new RealTemperatureModel("capetown");
        double expResult = 17.43;
        double result = instance.getTemperature(timestamp);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetTemperature_FromCalendar() {
        System.out.println("getTemperature_FromCalendar");
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Africa/Johannesburg"));
        calendar.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        long timestamp = calendar.getTimeInMillis();
        RealTemperatureModel instance = new RealTemperatureModel("capetown");
        double expResult = 22.88;
        double result = instance.getTemperature(timestamp);
        assertEquals(expResult, result, 0.0);
    }
    
     @Test
    public void testGetTemperature_FromCalendar_UTC() {
        System.out.println("getTemperature_FromCalendar");
        // Capetown has UTC +2 time.
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        long timestamp = calendar.getTimeInMillis();
        RealTemperatureModel instance = new RealTemperatureModel("capetown");
        double expResult = 22.78;
        double result = instance.getTemperature(timestamp);
        assertEquals(expResult, result, 0.0);
    }

}
