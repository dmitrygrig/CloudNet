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
public class UtilityHelperTest {

    public UtilityHelperTest() {
    }

    public static final double DELTA = 1e-15;

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
    public void testGetDescBenefitWorkloadUtility_0() {
        System.out.println("getDescBenefitWorkloadUtility_0");
        Double result = UtilityHelper.getDescBenefitWorkloadUtility(0.0);
        assertEquals(1.0, result, DELTA);
    }

    @Test
    public void testGetDescBenefitWorkloadUtility_5() {
        System.out.println("getDescBenefitWorkloadUtility_5");
        Double result = UtilityHelper.getDescBenefitWorkloadUtility(0.5);
        assertEquals(.6, result, DELTA);
    }

    @Test
    public void testGetDescBenefitWorkloadUtility_1() {
        System.out.println("getDescBenefitWorkloadUtility_1");
        Double result = UtilityHelper.getDescBenefitWorkloadUtility(1.0);
        assertEquals(.1, result, DELTA);
    }

    @Test
    public void testGetDescBenefitWorkloadUtilityOverusage() {
        System.out.println("getDescBenefitWorkloadUtilityOverusage");
        Double result = UtilityHelper.getDescBenefitWorkloadUtility(1.1);
        assertEquals(.0, result, DELTA);
    }

    @Test
    public void testGetAscBenefitWorkloadUtility_0() {
        System.out.println("getAscBenefitWorkloadUtility_0");
        Double result = UtilityHelper.getAscBenefitWorkloadUtility(0.0);
        assertEquals(0.1, result, DELTA);
    }

    @Test
    public void testGetAscBenefitWorkloadUtility_5() {
        System.out.println("getAscBenefitWorkloadUtility_5");
        Double result = UtilityHelper.getAscBenefitWorkloadUtility(0.5);
        assertEquals(.5, result, DELTA);
    }

    @Test
    public void testGetCombinedBenefitWorkloadUtility_0() {
        System.out.println("testGetCombinedBenefitWorkloadUtility_0");
        Double result = UtilityHelper.getCombinedBenefitWorkloadUtility(0.0, 0.7);
        assertEquals(1.0 / 7, result, DELTA);
    }

    @Test
    public void testGetCombinedBenefitWorkloadUtility_5() {
        System.out.println("testGetCombinedBenefitWorkloadUtility_5");
        Double result = UtilityHelper.getCombinedBenefitWorkloadUtility(0.5, 0.7);
        assertEquals(5.0 / 7, result, DELTA);
    }

    @Test
    public void testGetCombinedBenefitWorkloadUtility_6() {
        System.out.println("testGetCombinedBenefitWorkloadUtility_6");
        Double result = UtilityHelper.getCombinedBenefitWorkloadUtility(0.7, 0.7);
        assertEquals(1.0, result, DELTA);
    }

    @Test
    public void testGetCombinedBenefitWorkloadUtility_9() {
        System.out.println("testGetCombinedBenefitWorkloadUtility_9");
        Double result = UtilityHelper.getCombinedBenefitWorkloadUtility(0.91, 0.7);
        assertEquals(0.25, result, DELTA);
    }

    @Test
    public void testGetCombinedBenefitWorkloadUtility_Overusage() {
        System.out.println("testGetCombinedBenefitWorkloadUtility_0");
        Double result = UtilityHelper.getCombinedBenefitWorkloadUtility(1.1, 0.7);
        assertEquals(0.0, result, DELTA);
    }

}
