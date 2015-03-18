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
package cloudnet.azure;

import cloudnet.core.Size;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Azure {

    public static long SlowCpu = 1000; // 1Ghz
    public static long MediumCpu = 2000; // 2Ghz
    public static long FastCpu = 3000; // 3Ghz

    public static long SmallHdd = 64 * Size.GB; // 64GB
    public static long MediumHdd = 256 * Size.GB; // 256GB
    public static long BigHdd = Size.TB; // 1 TB

    public static long SlowBw = 20 * Size.MBit; // 20 Mbit/sec
    public static long MediumBw = 100 * Size.MBit; // 100 Mbit/sec
    public static long FastBw = Size.GBit; // 1 Gbit/sec
    public static long InfiniBw = 40 * Size.GBit; // 40 Gbit/sec
    
    public static long RamA0 = 768 * Size.MB; 
    public static long RamA1 = 1750 * Size.MB; 
    public static long RamA2 = 3500 * Size.MB; 
    public static long RamA3 = 7000 * Size.MB; 
    public static long RamA4 = 14000 * Size.MB; 
    public static long RamA5 = 14000 * Size.MB; 
    public static long RamA6 = 28000 * Size.MB; 
    public static long RamA7 = 56000 * Size.MB; 
    public static long RamA8 = 56000 * Size.MB; 
    public static long RamA9 = 112000 * Size.MB; 
}
