/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig@gmail.com>
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
package cloudnet.poweroutage;

import cloudnet.core.TimeFrame;
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
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class ProbabilityPowerOutageModelTest {

    public ProbabilityPowerOutageModelTest() {
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

    /**
     * Test of getDuration method, of class ProbabilityPowerOutageModel.
     */
    @Test
    public void testGetDuration_withCal() {
        System.out.println("getDuration_withCal");
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        long from = cal.getTimeInMillis();
        long till = from + TimeFrame.Day;
        ProbabilityPowerOutageModel instance = new ProbabilityPowerOutageModel(TimeFrame.Minute, 6, TimeFrame.Day);
        long expResult = 6 * TimeFrame.Minute;
        long result = instance.getDuration(from, till);
        assertEquals(expResult, result, TimeFrame.Minute);
    }

    /**
     * Test of getDuration method, of class ProbabilityPowerOutageModel.
     */
    @Test
    public void testGetDuration() {
        System.out.println("getDuration");
        long from = 0;
        long till = from + TimeFrame.Day;
        ProbabilityPowerOutageModel instance = new ProbabilityPowerOutageModel(20 * TimeFrame.Minute, 12, TimeFrame.Day);
        long expResult = 4 * TimeFrame.Hour;
        long result = instance.getDuration(from, till);
        assertEquals(expResult, result, 30 * TimeFrame.Minute);
    }

}
