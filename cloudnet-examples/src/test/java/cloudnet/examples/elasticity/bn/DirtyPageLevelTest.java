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

import cloudnet.examples.elasticity.bn.DirtyPageLevel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DirtyPageLevelTest {
    
    public DirtyPageLevelTest() {
    }

    @Test
    public void testGetRateByLevel() {
        System.out.println("getRateByLevel");
        String level = DirtyPageLevel.High;
        int expResult = 5;
        int result = DirtyPageLevel.getRateByLevel(level);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetLevelNotSameDc() {
        System.out.println("getLevelNotSameDc");
        double ramWorkload = 0.6;
        boolean sameDC = false;
        String expResult = DirtyPageLevel.Middle;
        String result = DirtyPageLevel.getLevel(ramWorkload, sameDC);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetLevelSameDc() {
        System.out.println("getLevelSameDc");
        double ramWorkload = 0.6;
        boolean sameDC = true;
        String expResult = DirtyPageLevel.Low;
        String result = DirtyPageLevel.getLevel(ramWorkload, sameDC);
        assertEquals(expResult, result);
    }
    
}
