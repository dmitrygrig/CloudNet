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
package cloudnet.vm.azure;

import cloudnet.azure.Azure;
import cloudnet.vm.ConstantVmSpec;

/**
 * http://azure.microsoft.com/en-us/pricing/details/virtual-machines/
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmSpecAzureA1 extends ConstantVmSpec {

    public VmSpecAzureA1() {
        super(Azure.SmallHdd, Azure.SlowCpu, 1, Azure.RamA1, Azure.MediumBw);
    }

}