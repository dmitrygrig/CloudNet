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
package cloudnet.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DumperTest {

    public DumperTest() {
    }


    /**
     * Test of dump method, of class Dumper.
     */
    @Test
    public void testDump_TestClassWithVariable() {
        System.out.println("dump_TestClassWithVariable");
        TestClassWithVariable o = new TestClassWithVariable();
        o.setVariable1(5);
        String result = Dumper.dump(o);
        assertNotNull(result);
    }
    
    /**
     * Test of dump method, of class Dumper.
     */
    @Test
    public void testDump_EmptyTestClass() {
        System.out.println("dump_EmptyTestClass");
        EmptyTestClass o = new EmptyTestClass();
        String result = Dumper.dump(o);
        assertNotNull(result);
    }

    /**
     * Test of dump method, of class Dumper.
     */
    @Test
    public void testDump_TestClassWithVariable_4Args() {
        System.out.println("dump_TestClassWithVariable_4Args");
        TestClassWithVariable o = new TestClassWithVariable();
        o.setVariable1(5);
        String result = Dumper.dump(o, 1, 1, null);
        assertNotNull(result);
    }
    
    /**
     * Test of dump method, of class Dumper.
     */
    @Test
    public void testDump_EmptyTestClass_4Args() {
        System.out.println("dump_EmptyTestClass_4Args");
        EmptyTestClass o = new EmptyTestClass();
       String result = Dumper.dump(o, 1, 1, null);
        assertNotNull(result);
    }

}
