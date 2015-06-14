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
public class StaticWorkloadModelTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {0}, {100}, {200}
        });
    }

    @Parameterized.Parameter
    public long inputTimestamp;

    /**
     * Test of getWorkload method, of class PediodicWorkloadModel.
     */
    @Test
    public void testGetWorkloadBetweenBoundaries() {
        System.out.printf("getWorkload (input = %d)\r\n", inputTimestamp);
        StaticWorkloadModel instance = new StaticWorkloadModel(0.2, WorkloadConstants.Deviation, WorkloadConstants.Seed);
        double result = instance.getWorkload(inputTimestamp);
        assertEquals(.2, result, WorkloadConstants.Deviation);
    }

}
