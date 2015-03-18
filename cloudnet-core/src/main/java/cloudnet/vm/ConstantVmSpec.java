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
package cloudnet.vm;

import cloudnet.core.VmSpec;
import cloudnet.util.SimpleDumper;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class ConstantVmSpec implements VmSpec {

    /**
     * The size.
     */
    private final long size;

    /**
     * The MIPS of each cpu.
     */
    private final long mipsPerCore;

    /**
     * The number of Cpus.
     */
    private final int numberOfCores;

    /**
     * The ram.
     */
    private final long ram;

    /**
     * The bw.
     */
    private final long bw;

    public ConstantVmSpec(long size, long mipsPerCore, int numberOfCores, long ram, long bw) {
        this.size = size;
        this.mipsPerCore = mipsPerCore;
        this.numberOfCores = numberOfCores;
        this.ram = ram;
        this.bw = bw;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getMips() {
        return mipsPerCore * numberOfCores;
    }

    @Override
    public long getRam() {
        return ram;
    }

    @Override
    public long getBw() {
        return bw;
    }

    @Override
    public String toString() {
        return SimpleDumper.dump(this);
    }

}
