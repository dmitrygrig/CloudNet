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

/**
 * PmSpec interface should be implemented in order to provide Pm Specifications.
 *
 * Partly obtained from CloudSim project.
 * 
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public interface PmSpec {

    /**
     * Get power consumption by the utilization percentage according to the
     * power model.
     *
     * @param utilization the utilization (0..1)
     * @return power consumption (Watt)
     * @throws IllegalArgumentException the illegal argument exception
     */
    double getPower(double utilization) throws IllegalArgumentException;

    /**
     * Get amount of all Pm MIPS as sum of mips of each separate cpu.
     *
     * @return Amount of mips (MIPS)
     */
    long getMips();
    
    /**
     * Gets overall bandwidth capacity.
     * 
     * @return Bandwidth capacity (bit/s)
     */
    long getBw();

    /**
     * Gets overall RAM capacity
     * 
     * @return RAM (bits)
     */
    long getRam();

    /**
     * Gets overall hdd capacity.
     * 
     * @return (bits)
     */
    long getSize();
}
