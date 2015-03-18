/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cloudnet.core;

import cloudnet.util.Ensure;
import java.util.Comparator;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmSlaComparator implements Comparator<Vm> {

    @Override
    public int compare(Vm vm1, Vm vm2) {
        Ensure.NotNull(vm1, "vm1");
        Ensure.NotNull(vm1.getSla(), "vm1.sla");
        Ensure.NotNull(vm2, "vm2");
        Ensure.NotNull(vm2.getSla(), "vm2.sla");

        return vm1.getSla().getLevel().moreImportantThan(vm2.getSla().getLevel()) ? 1 : 0;
    }

}
