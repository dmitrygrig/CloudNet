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
package cloudnet.examples.elasticity.bn;

import cloudnet.core.Size;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class WorkloadLevelTest {

    public WorkloadLevelTest() {
    }

    @Test
    public void testGetLevel() {
        System.out.println("getLevel");
        double workload = 0.2;
        String expResult = WorkloadLevel.W20;
        String result = WorkloadLevel.getLevel(workload);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetLevelOverusage() {
        System.out.println("testGetLevelOverusage");
        double workload = 1.1;
        String expResult = WorkloadLevel.Overusage;
        String result = WorkloadLevel.getLevel(workload);
        assertEquals(expResult, result);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetLevelLessThanZero() {
        System.out.println("testGetLevelLessThanZero");
        double workload = -0.1;
        WorkloadLevel.getLevel(workload);
    }

}
