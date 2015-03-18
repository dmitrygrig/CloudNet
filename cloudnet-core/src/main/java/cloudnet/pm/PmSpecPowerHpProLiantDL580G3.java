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
 * The power model of an HP ProLiant DL580 G3 (4 x [Intel Xeon Processor 7020,
 * 2660 MHz, 8 cores], 16GB).
 * http://www.spec.org/power_ssj200results/res2011q1/power_ssj2008-20110124-00336.html
 *
 * Partly obtained from CloudSim project.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class PmSpecPowerHpProLiantDL580G3 extends PmSpecPower {

    /**
     * The power.
     */
    private final double[] power = {520, 551, 587, 646, 705, 740, 766, 787, 803, 818, 833};

    /**
     * The mips.
     */
    private final long mips = 4 * 2660;

    private final long bw = 10 * Size.GBit;

    private final long ram = 16 * Size.GB;

    private final long size = 1 * Size.TB;

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
