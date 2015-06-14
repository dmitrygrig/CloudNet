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

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
@RunWith(Parameterized.class)
public class OnceInAlifetimeWorkloadModelTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {0, .2, .2},
            {WorkloadConstants.Minute, .15, .25},
            {WorkloadConstants.Minute / 2 * 3, .15, .25},
            {WorkloadConstants.Minute / 4 * 5, .75, .85}
        });
    }

    @Parameterized.Parameter
    public long inputTimestamp;
    @Parameterized.Parameter(value = 1)
    public double lowerThreshold;
    @Parameterized.Parameter(value = 2)
    public double upperThreshold;

    /**
     * Test of getWorkload method, of class PediodicWorkloadModel.
     */
    @Test
    public void testGetWorkloadBetweenBoundaries() {
        System.out.printf("getWorkload (input = %d, lower = %f, upper=%f)\r\n", inputTimestamp, lowerThreshold, upperThreshold);
        OnceInAlifetimeWorkloadModel instance = new OnceInAlifetimeWorkloadModel(0.2, 0.8, WorkloadConstants.Minute, WorkloadConstants.Minute);
        double result = instance.getWorkload(inputTimestamp);
        assertTrue(result >= lowerThreshold);
        assertTrue(result <= upperThreshold);
    }

}
