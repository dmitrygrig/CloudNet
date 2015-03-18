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
package cloudnet.pm;

import cloudnet.core.Size;

/**
 * The power model of an HP ProLiant ML110 G3 (1 x [Pentium D930 3000 MHz, 2
 * cores], 4GB).
 * http://www.spec.org/power_ssj2008/results/res2011q1/power_ssj2008-20110127-00342.html
 *
 * Partly obtained from CloudSim project.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class PmSpecPowerHpProLiantMl110G3PentiumD930 extends PmSpecPower {

    /**
     * The power.
     */
    private final double[] power = {105, 112, 118, 125, 131, 137, 147, 153, 157, 164, 169};

    /**
     * The mips.
     */
    private final long mips = 3000;

    private final long bw = 1 * Size.GBit;

    private final long ram = 4 * Size.GB;

    private final long size = 320 * Size.GB;

    @Override
    protected double getPowerData(int index) {
        return power[index];
    }

    @Override
    public long getMips() {
        return mips;
    }

    @Override
    public long getBw() {
        return bw;
    }

    @Override
    public long getRam() {
        return ram;
    }

    @Override
    public long getSize() {
        return size;
    }

}
