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
package cloudnet.core;

import cloudnet.sim.SimClock;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmTest {

    private final SimClock clock = new SimClock();

    private Vm getInstance() {
        Vm instance = new Vm(0, clock);
        Sla sla = mock(Sla.class);
        when(sla.getLevel()).thenReturn(SlaLevel.Silver);
        instance.setSla(sla);
        return instance;
    }

    /**
     * Test of isAllocated method, of class Vm.
     */
    @Test
    public void testIsAllocated_ServerNotSet_False() {
        System.out.println("isAllocated");
        Vm instance = getInstance();
        boolean expResult = false;
        boolean result = instance.isAllocated();
        assertEquals(expResult, result);
    }

    /**
     * Test of isAllocated method, of class Vm.
     */
    @Test
    public void testIsAllocated_ServerIsSet_True() {
        System.out.println("isAllocated");
        Vm instance = getInstance();
        instance.allocateTo(mock(Pm.class));
        boolean expResult = true;
        boolean result = instance.isAllocated();
        assertEquals(expResult, result);
    }

    /**
     * Test of allocateTo method, of class Vm.
     */
    @Test
    public void testAllocateTo_NotAllocated_IsAllocatedTrue() {
        System.out.println("allocateTo");

        Pm server = new Pm(0, clock, mock(PmSpec.class));
        server.run();
        Vm instance = getInstance();
        instance.allocateTo(server);

        boolean expResult = true;
        boolean result = instance.isAllocated();

        assertEquals(expResult, result);
    }

    /**
     * Test of allocateTo method, of class Vm.
     */
    @Test(expected = IllegalStateException.class)
    public void testAllocateTo_AllocateTwoTimes_Exception() {
        System.out.println("allocateTo");

        Pm server = new Pm(0, clock, mock(PmSpec.class));
        server.run();
        Vm instance = getInstance();

        instance.allocateTo(server);
        instance.allocateTo(server);
    }

    /**
     * Test of deallocate method, of class Vm.
     */
    @Test(expected = IllegalStateException.class)
    public void testDeallocate_ServerNotSet_Exception() {
        System.out.println("deallocate");
        Vm instance = getInstance();
        instance.deallocate();
    }

    /**
     * Test of deallocate method, of class Vm.
     */
    @Test
    public void testDeallocate_ServerIsSet_isAllocatedFalse() {
        System.out.println("deallocate");

        Pm server = new Pm(0, clock, mock(PmSpec.class));
        server.run();
        Vm instance = getInstance();
        instance.allocateTo(server);
        instance.deallocate();

        boolean expResult = false;
        boolean result = instance.isAllocated();

        assertEquals(expResult, result);
    }

    /**
     * Test of getDowntime method, of class Vm.
     */
    @Test
    public void testGetDowntime() {
        System.out.println("getDowntime");
        Vm instance = getInstance();
        instance.setLastSimulationTime(50L);
        clock.add(100L);
        long expResult = 50L;
        long result = instance.getDowntime();
        assertEquals(expResult, result);
    }

}
