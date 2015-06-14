/*
 * Copyright (C) 2014 Dmytro
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
import cloudnet.core.Vm;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Dmytro
 */
public class VmSlaOverallDowntimeTest {

    /**
     * Test of getPenalty method, of class IaaSSla.
     */
    @Test
    public void testGetPenalty_NoRuntime() {
        System.out.println("getPenalty");
        Vm vm = mock(Vm.class);
        when(vm.getRequestedRunningTime()).thenReturn(0L);
        VmSlaOverallDowntime instance = new VmSlaOverallDowntime(SlaLevel.Bronze, 0.05, 0.1, 10000);
        instance.setVm(vm);
        double expResult = 0.0;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPenalty method, of class IaaSSla.
     */
    @Test
    public void testGetPenalty_NoDowntime() {
        System.out.println("getPenalty");
        Vm vm = mock(Vm.class);
        when(vm.getDowntime()).thenReturn(0L);
        when(vm.getRequestedRunningTime()).thenReturn(100L);
        VmSlaOverallDowntime instance = new VmSlaOverallDowntime(SlaLevel.Bronze, 0.05, 0.1, 1000);
        instance.setVm(vm);
        double expResult = 0.0;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPenalty method, of class IaaSSla.
     */
    @Test
    public void testGetPenalty_DowntimeAsThreshold() {
        System.out.println("getPenalty");
        Vm vm = mock(Vm.class);
        when(vm.getDowntime()).thenReturn(4L);
        when(vm.getRequestedRunningTime()).thenReturn(100L);
        VmSlaOverallDowntime instance = new VmSlaOverallDowntime(SlaLevel.Bronze, 0.05, 0.1, 100);
        instance.setVm(vm);
        double expResult = 0.0;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPenalty method, of class IaaSSla.
     */
    @Test
    public void testGetPenalty_ExceedOneTime() {
        System.out.println("getPenalty");
        Vm vm = mock(Vm.class);
        when(vm.getDowntime()).thenReturn(5L);
        when(vm.getRequestedRunningTime()).thenReturn(100L);
        VmSlaOverallDowntime instance = new VmSlaOverallDowntime(SlaLevel.Bronze, 0.05, 0.1, 100);
        instance.setVm(vm);
        double expResult = 0.1;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getPenalty method, of class IaaSSla.
     */
    @Test
    public void testGetPenalty_ExceedThreeTimes() {
        System.out.println("getPenalty");
        Vm vm = mock(Vm.class);
        when(vm.getDowntime()).thenReturn(15L);
        when(vm.getRequestedRunningTime()).thenReturn(100L);
        VmSlaOverallDowntime instance = new VmSlaOverallDowntime(SlaLevel.Bronze, 0.05, 0.1, 100);
        instance.setVm(vm);
        double expResult = 0.3;
        double result = instance.getPenalty();
        assertEquals(expResult, result, 0.0);
    }
}
