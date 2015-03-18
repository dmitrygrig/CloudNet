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
package cloudnet.messaging;

import cloudnet.core.Pm;
import cloudnet.core.Vm;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmMigrationMessage extends Message {

    private Vm vm;
    private Pm pmTo;

    public VmMigrationMessage() {
    }

    public VmMigrationMessage(Vm vm, Pm pmTo) {
        this.vm = vm;
        this.pmTo = pmTo;
    }

    public Vm getVm() {
        return vm;
    }

    public void setVm(Vm vm) {
        this.vm = vm;
    }

    public Pm getPmTo() {
        return pmTo;
    }

    public void setPmTo(Pm pmTo) {
        this.pmTo = pmTo;
    }

    @Override
    public String toString() {
        return String.format("%s [pmToid=%d,vmid=%d]",
                getClass().getSimpleName(), getPmTo().getId(), getVm().getId());
    }

}
