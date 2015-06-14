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
package cloudnet.workloads;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
@RunWith(Parameterized.class)
public class DayNightWorkloadModelTest {

    private static final double nightValue = .2;
    private static final double dayValue = .5;
    private static final String utcTimeZone = "Etc/UTC";
    private static final String laTimeZone = "America/Los_Angeles";
    private static final long laHourOffset = new GregorianCalendar(TimeZone.getTimeZone(laTimeZone)).get(GregorianCalendar.ZONE_OFFSET) / WorkloadConstants.Hour;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            // paris
            {WorkloadConstants.Hour * 0, utcTimeZone, nightValue},
            {WorkloadConstants.Hour * 7, utcTimeZone, nightValue},
            {WorkloadConstants.Hour * 8, utcTimeZone, dayValue},
            {WorkloadConstants.Hour * 9, utcTimeZone, dayValue},
            {WorkloadConstants.Hour * 19, utcTimeZone, dayValue},
            {WorkloadConstants.Hour * 20, utcTimeZone, nightValue},
            {WorkloadConstants.Hour * 21, utcTimeZone, nightValue},
            //la
            {WorkloadConstants.Hour * (0 - laHourOffset), laTimeZone, nightValue},
            {WorkloadConstants.Hour * (7 - laHourOffset), laTimeZone, nightValue},
            {WorkloadConstants.Hour * (8 - laHourOffset), laTimeZone, dayValue},
            {WorkloadConstants.Hour * (9 - laHourOffset), laTimeZone, dayValue},
            {WorkloadConstants.Hour * (19 - laHourOffset), laTimeZone, dayValue},
            {WorkloadConstants.Hour * (20 - laHourOffset), laTimeZone, nightValue},
            {WorkloadConstants.Hour * (21 - laHourOffset), laTimeZone, nightValue}});
    }

    @Parameterized.Parameter
    public long inputTimestamp;
    @Parameterized.Parameter(value = 1)
    public String timeZoneId;
    @Parameterized.Parameter(value = 2)
    public double expected;

    /**
     * Test of getWorkload method, of class DayNightWorkloadModel.
     */
    @Test
    public void testGetWorkload() {
        System.out.printf("getWorkload (input = %d, timeZone = %s, expected = %f)\r\n", inputTimestamp / WorkloadConstants.Hour, timeZoneId, expected);

        WorkloadModel dayModel = mock(WorkloadModel.class);
        WorkloadModel nightModel = mock(WorkloadModel.class);
        given(dayModel.getWorkload(anyLong())).willReturn(dayValue);
        given(nightModel.getWorkload(anyLong())).willReturn(nightValue);
        DayNightWorkloadModel instance = new DayNightWorkloadModel(dayModel, nightModel, ZoneId.of(timeZoneId), 8, 20);

        double result = instance.getWorkload(inputTimestamp);

        assertEquals(expected, result, 1e-15);
    }

}
