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
public class DistrHelperTest {

    public DistrHelperTest() {
    }

    private void testForNullValues(Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            assertNotNull(arr[i]);
        }
    }

    @Test
    public void testTwoWorkloadsSumDistributionLength() {
        System.out.println("TwoWorkloadsSumDistributionLength");
        int expResult = 1100;
        Integer[] result = DistrHelper.TwoWorkloadsSumDistribution();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testTwoWorkloadsSumDistributionNoNullValues() {
        System.out.println("testTwoWorkloadsSumDistributionNoNullValues");
        testForNullValues(DistrHelper.TwoWorkloadsSumDistribution());
    }

    @Test
    public void testTwoWorkloadsSumDistributionValues() {
        System.out.println("TwoWorkloadsSumDistributionValues");
        int expected = 50;
        Integer[] result = DistrHelper.TwoWorkloadsSumDistribution();
        assertEquals(expected, (int) result[0]);
        assertEquals(expected, (int) result[12]);
    }

    @Test
    public void testGetTwoWorkloadsSumDistributionLength() {
        System.out.println("getTwoWorkloadsSumDistributionLength");
        int expResult = 27;
        Integer[] result = DistrHelper.getTwoWorkloadsSumDistribution(3, 3, 3);
        assertEquals(expResult, result.length);
    }

    @Test
    public void testGetTwoWorkloadsSumDistributionNoNullValues() {
        System.out.println("testTwoWorkloadsSumDistributionNoNullValues");
        testForNullValues(DistrHelper.getTwoWorkloadsSumDistribution(3, 3, 3));
    }

    @Test
    public void testTwoWorkloadsAndDistributionLength() {
        System.out.println("TwoWorkloadsAndDistributionLength");
        int expResult = WorkloadLevel.AllWithOverusage().length
                * WorkloadLevel.AllWithOverusage().length
                * WorkloadLevel.AllWithOverusage().length;
        Integer[] result = DistrHelper.TwoWorkloadsAndDistribution();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testTwoWorkloadsAndDistributionNoNullValues() {
        System.out.println("testTwoWorkloadsAndDistributionNoNullValues");
        testForNullValues(DistrHelper.TwoWorkloadsAndDistribution());
    }

    @Test
    public void testTwoWorkloadsAndDistributionValues() {
        System.out.println("TwoWorkloadsAndDistributionValues");
        int expected = 100;
        Integer[] result = DistrHelper.TwoWorkloadsAndDistribution();
        assertEquals(expected, (int) result[0]);
        assertEquals(expected, (int) result[12]);
    }

    @Test
    public void testMigrationTimeDistributionLength() {
        System.out.println("MigrationTimeDistributionLength");
        int expResult = 11 * BwLevel.All().length * VmSizeLevel.All().length * DirtyPageLevel.All().length;
        Double[] result = DistrHelper.MigrationTimeDistribution();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testMigrationTimeDistributionNoNullValues() {
        System.out.println("testMigrationTimeDistributionNoNullValues");
        testForNullValues(DistrHelper.MigrationTimeDistribution());
    }

    @Test
    public void testVmDowntimeDistributionLength() {
        System.out.println("VmDowntimeDistributionLength");
        int expResult = BooleanLevel.All().length * 11 * 11;
        Integer[] result = DistrHelper.VmDowntimeDistribution();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testVmDowntimeDistributionNoNullValues() {
        System.out.println("testVmDowntimeDistributionNoNullValues");
        testForNullValues(DistrHelper.VmDowntimeDistribution());
    }

    @Test
    public void testGetMigrationTimeUB() {
        System.out.println("getMigrationTimeUB");
        long vmSize = Size.GB;
        long bw = Size.GBit;
        int dirtyPageRate = 5;
        long expResult = 40000L; // 40000 ms
        long result = DistrHelper.getMigrationTimeUB(vmSize, bw, dirtyPageRate);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetMigrationTimeUBLessThanSecond() {
        System.out.println("getMigrationTimeUBLessThanSecond");
        long vmSize = Size.GB;
        long bw = 100 * Size.GBit;
        int dirtyPageRate = 5;
        long expResult = 400L; // 40000 ms
        long result = DistrHelper.getMigrationTimeUB(vmSize, bw, dirtyPageRate);
        assertEquals(expResult, result);
    }

    @Test
    public void testPowerOutageDistrLength() {
        System.out.println("PowerOutageDistrLength");
        int expResult = CountryLevel.All().length * 2;
        Double[] result = DistrHelper.PowerOutageDistr();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testPowerOutageDistrNoNullValues() {
        System.out.println("testPowerOutageDistrNoNullValues");
        testForNullValues(DistrHelper.PowerOutageDistr());
    }

    @Test
    public void testPowerOutageDurationDistrLength() {
        System.out.println("PowerOutageDurationDistrLength");
        int expResult = CountryLevel.All().length * WorkloadLevel.AllWithOverusage().length * BooleanLevel.All().length;
        Double[] result = DistrHelper.PowerOutageDurationDistr();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testPowerOutageDurationDistrNoNullValues() {
        System.out.println("testPowerOutageDurationDistrNoNullValues");
        testForNullValues(DistrHelper.PowerOutageDurationDistr());
    }

    @Test
    public void testWorkloadToOverusageDistributionLength() {
        System.out.println("WorkloadToOverusageDistributionLength");
        int expResult = WorkloadLevel.AllWithOverusage().length * BooleanLevel.All().length;
        Integer[] result = DistrHelper.WorkloadToOverusageDistribution();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testWorkloadToOverusageDistributionNoNullValues() {
        System.out.println("testWorkloadToOverusageDistributionNoNullValues");
        testForNullValues(DistrHelper.WorkloadToOverusageDistribution());
    }

    @Test
    public void testWorkloadToDirtyPageDistrLength() {
        System.out.println("WorkloadToDirtyPageDistrLength");
        int expResult = WorkloadLevel.All().length * BooleanLevel.All().length * DirtyPageLevel.All().length;
        Integer[] result = DistrHelper.WorkloadToDirtyPageDistr();
        assertEquals(expResult, result.length);
    }

    @Test
    public void testWorkloadToDirtyPageDistrNoNullValues() {
        System.out.println("testWorkloadToDirtyPageDistrNoNullValues");
        testForNullValues(DistrHelper.WorkloadToDirtyPageDistr());
    }

}
