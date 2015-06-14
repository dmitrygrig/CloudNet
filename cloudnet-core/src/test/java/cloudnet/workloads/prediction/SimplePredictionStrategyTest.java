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
package cloudnet.workloads.prediction;

import cloudnet.workloads.WorkloadHistory;
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
public class SimplePredictionStrategyTest {

    public SimplePredictionStrategyTest() {
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
    public void testPredictValue_NoValues_Null() {
        System.out.println("testPredictValue_NoValues_Null");
        long futureTimeStamp = 0L;
        long currTimeStamp = 0L;
        WorkloadHistory history = new WorkloadHistory();
        SimplePredictionStrategy instance = new SimplePredictionStrategy();
        Long expResult = null;
        Long result = instance.predictValue(futureTimeStamp, currTimeStamp, history);
        assertEquals(expResult, result);
    }

    @Test
    public void testPredictValue_OnveValue_ReturnIt() {
        System.out.println("testPredictValue_OneValue_ReturnIt");
        long futureTimeStamp = 0L;
        long currTimeStamp = 0L;
        WorkloadHistory history = new WorkloadHistory();
        history.saveWorkload(0L, 1L);
        SimplePredictionStrategy instance = new SimplePredictionStrategy();
        Long expResult = 1L;
        Long result = instance.predictValue(futureTimeStamp, currTimeStamp, history);
        assertEquals(expResult, result);
    }

    @Test
    public void testPredictValue_OneValueBefore_Null() {
        System.out.println("testPredictValue_OneValueBefore_Null");
        long futureTimeStamp = 0L;
        long currTimeStamp = 1L;
        WorkloadHistory history = new WorkloadHistory();
        history.saveWorkload(2L, 1L);
        SimplePredictionStrategy instance = new SimplePredictionStrategy();
        Long expResult = null;
        Long result = instance.predictValue(futureTimeStamp, currTimeStamp, history);
        assertEquals(expResult, result);
    }

    @Test
    public void testPredictValue_OneValueAfter_ReturnIt() {
        System.out.println("testPredictValue_OneValueAfter_ReturnIt");
        long futureTimeStamp = 0L;
        long currTimeStamp = 1L;
        WorkloadHistory history = new WorkloadHistory();
        history.saveWorkload(0L, 1L);
        SimplePredictionStrategy instance = new SimplePredictionStrategy();
        Long expResult = 1L;
        Long result = instance.predictValue(futureTimeStamp, currTimeStamp, history);
        assertEquals(expResult, result);
    }

    @Test
    public void testPredictValue_TwoValues_ReturnLast() {
        System.out.println("testPredictValue_TwoValues_ReturnLast");
        long futureTimeStamp = 0L;
        long currTimeStamp = 1L;
        WorkloadHistory history = new WorkloadHistory();
        history.saveWorkload(0L, 1L);
        history.saveWorkload(1L, 2L);
        SimplePredictionStrategy instance = new SimplePredictionStrategy();
        Long expResult = 2L;
        Long result = instance.predictValue(futureTimeStamp, currTimeStamp, history);
        assertEquals(expResult, result);
    }

    @Test
    public void testPredictValue_TwoValuesFlipped_ReturnLast() {
        System.out.println("testPredictValue_TwoValuesFlipped_ReturnLast");
        long futureTimeStamp = 0L;
        long currTimeStamp = 1L;
        WorkloadHistory history = new WorkloadHistory();
        history.saveWorkload(1L, 2L);
        history.saveWorkload(0L, 1L);
        SimplePredictionStrategy instance = new SimplePredictionStrategy();
        Long expResult = 2L;
        Long result = instance.predictValue(futureTimeStamp, currTimeStamp, history);
        assertEquals(expResult, result);
    }

}
