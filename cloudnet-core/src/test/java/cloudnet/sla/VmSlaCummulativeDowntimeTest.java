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
package cloudnet.sla;

import cloudnet.core.SlaLevel;
import cloudnet.core.TimeFrame;
import cloudnet.core.Vm;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmSlaCummulativeDowntimeTest {

    public VmSlaCummulativeDowntimeTest() {
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
    public void testGetPenalty_no() {
        System.out.println("getPenalty_no");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(0L);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0.0;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetPenalty_1time() {
        System.out.println("getPenalty_1time");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 10;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetPenalty_moreThanOne_LessThanTwo() {
        System.out.println("getPenalty_moreThanOne_LessThanTwo");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour + 1);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 10;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetPenalty_LessThanOne() {
        System.out.println("getPenalty_LessThanOne");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour - 1);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetPenalty_2times() {
        System.out.println("getPenalty_2times");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(2 * TimeFrame.Hour);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 20;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetViolationRate_no() {
        System.out.println("getViolationRate_no");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(0L);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0.0;
        double result = instance.getViolationRate();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetViolationRate_90Percent() {
        System.out.println("getViolationRate_90Percent");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour / 100 * 90);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0.9;
        double result = instance.getViolationRate();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetViolationRate_90PercentSecondTime() {
        System.out.println("getViolationRate_90PercentSecondTime");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour / 100 * 190);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0.9;
        double result = instance.getViolationRate();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetViolationRate_10Percent() {
        System.out.println("getViolationRate_10Percent");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour / 100 * 10);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0.1;
        double result = instance.getViolationRate();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetViolationRate_10PercentSecondTime() {
        System.out.println("getViolationRate_10PercentSecondTime");
        Vm vm = mock(Vm.class);
        when(vm.getCummulativeDowntime()).thenReturn(TimeFrame.Hour / 100 * 110);
        VmSlaCummulativeDowntime instance = new VmSlaCummulativeDowntime(SlaLevel.Bronze, 10, TimeFrame.Hour);
        instance.setVm(vm);
        double expResult = 0.1;
        double result = instance.getViolationRate();
        assertEquals(expResult, result, 0.0);
    }

}
