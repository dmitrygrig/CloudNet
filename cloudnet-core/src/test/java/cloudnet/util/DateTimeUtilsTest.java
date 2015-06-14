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
package cloudnet.util;

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
public class DateTimeUtilsTest {

    public DateTimeUtilsTest() {
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
    public void testGetDateTime() {
        System.out.println("getDateTime");

        String zoneId = "Europe/Vienna";
        String zoneId2 = "America/Toronto";
        GregorianCalendar cal = new GregorianCalendar(2014, 7, 20, 14, 12, 15);
        cal.setTimeZone(TimeZone.getTimeZone(zoneId));
        long timestamp = cal.getTimeInMillis();

        String expResult = "2014-08-20 14:12:15";
        String expResult2 = "2014-08-20 08:12:15";
        String result = DateTimeUtils.getDateTime(timestamp, zoneId);
        String result2 = DateTimeUtils.getDateTime(timestamp, zoneId2);

        assertEquals(expResult, result);
        assertEquals(expResult2, result2);
    }

    @Test
    public void testIsDayOrNight() {
        System.out.println("isDayOrNight");

        String zoneId = "Europe/Vienna";
        String zoneId2 = "America/Toronto";
        GregorianCalendar cal = new GregorianCalendar(2014, 7, 20, 14, 12, 15);
        cal.setTimeZone(TimeZone.getTimeZone(zoneId));
        long timestamp = cal.getTimeInMillis();

        boolean expResult = true;
        boolean expResult2 = false;
        boolean result = DateTimeUtils.isDayOrNight(timestamp, zoneId, 10, 22);
        boolean result2 = DateTimeUtils.isDayOrNight(timestamp, zoneId2, 10, 22);

        assertEquals(expResult, result);
        assertEquals(expResult2, result2);
    }

}
