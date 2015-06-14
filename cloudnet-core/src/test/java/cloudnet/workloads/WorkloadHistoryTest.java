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
public class WorkloadHistoryTest {

    public WorkloadHistoryTest() {
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
    public void testGetWorkload_EmptyHistory_Null() {
        System.out.println("testGetWorkload_EmptyHistory_Null");
        long timestamp = 0L;
        WorkloadHistory instance = new WorkloadHistory();
        Long expResult = null;
        Long result = instance.getWorkload(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetWorkload_HasValues_Correct() {
        System.out.println("testGetWorkload_HasValues_Correct");
        long timestamp = 0L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(0L, 1L);
        Long expResult = 1L;
        Long result = instance.getWorkload(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetWorkloadOrDefault_EmptyHistory_0L() {
        System.out.println("testGetWorkloadOrDefault_EmptyHistory_0L");
        long timestamp = 0L;
        WorkloadHistory instance = new WorkloadHistory();
        long expResult = 0L;
        long result = instance.getWorkloadOrDefault(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetWorkloadOrDefault_HasValues_SameTimestamp_0L() {
        System.out.println("testGetWorkloadOrDefault_HasValues_SameTimestamp_0L");
        long timestamp = 0L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(0L, 1L);
        long expResult = 1L;
        long result = instance.getWorkloadOrDefault(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetWorkloadOrDefault_HasValues_AnotherTimestamp_LowerValue() {
        System.out.println("testGetWorkloadOrDefault_HasValues_AnotherTimestamp_LowerValue");
        long timestamp = 1L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(0L, 1L);
        long expResult = 1L;
        long result = instance.getWorkloadOrDefault(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetWorkloadOrDefault_HasValues_AnotherTimestamp_UpperValue() {
        System.out.println("testGetWorkloadOrDefault_HasValues_AnotherTimestamp_UpperValue");
        long timestamp = 1L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(2L, 1L);
        long expResult = 1L;
        long result = instance.getWorkloadOrDefault(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFirstWorkloadBefore_EmptyHistory_Null() {
        System.out.println("testGetFirstWorkloadBefore_EmptyHistory_Null");
        long timestamp = 0L;
        WorkloadHistory instance = new WorkloadHistory();
        Long expResult = null;
        Long result = instance.getFirstWorkloadBefore(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFirstWorkloadBefore_HasValuesBefore_Last() {
        System.out.println("testGetFirstWorkloadBefore_HasValuesBefore_Last");
        long timestamp = 3L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(1L, 3L);
        instance.saveWorkload(2L, 4L);
        Long expResult = 4L;
        Long result = instance.getFirstWorkloadBefore(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFirstWorkloadBefore_HasSameValue_It() {
        System.out.println("testGetFirstWorkloadBefore_HasSameValue_It");
        long timestamp = 1L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(1L, 2L);
        Long expResult = 2L;
        Long result = instance.getFirstWorkloadBefore(timestamp);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFirstWorkloadBefore_HasValuesAfter_Null() {
        System.out.println("testGetFirstWorkloadBefore_HasValuesAfter_Null");
        long timestamp = 0L;
        WorkloadHistory instance = new WorkloadHistory();
        instance.saveWorkload(1L, 2L);
        Long expResult = null;
        Long result = instance.getFirstWorkloadBefore(timestamp);
        assertEquals(expResult, result);
    }

    public void testSaveWorkload_MapSizeAsSchrinkAfter_NotSchrinked() {
        System.out.println("testSaveWorkload_MapSizeAsSchrinkAfter_NotSchrinked");
        WorkloadHistory instance = new WorkloadHistory();
        instance.setShrinkAfter(1);
        instance.setShrinkTo(1);
        instance.saveWorkload(1L, 2L);
        Long expResult = 2L;
        Long result = instance.getWorkload(1L);
        assertEquals(expResult, result);
    }

    public void testSaveWorkload_MapOverfilled_SchrinkedToLimit() {
        System.out.println("testSaveWorkload_MapOverfilled_SchrinkedToLimit");
        WorkloadHistory instance = new WorkloadHistory();
        instance.setShrinkAfter(1);
        instance.setShrinkTo(1);
        instance.saveWorkload(1L, 2L);
        instance.saveWorkload(2L, 3L);
        Long expResult = null;
        Long result = instance.getWorkload(1L);
        Long expResult2 = 3L;
        Long result2 = instance.getWorkload(2L);
        assertEquals(expResult, result);
        assertEquals(expResult2, result2);
    }
}
