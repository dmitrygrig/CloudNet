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
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class Size {

    public final static long Bit = 1;
    public final static long KBit = Bit * 1024;
    public final static long MBit = KBit * 1024;
    public final static long GBit = MBit * 1024;

    public final static long Byte = Bit * 8;
    public final static long KB = Byte * 1024;
    public final static long MB = KB * 1024;
    public final static long GB = MB * 1024;
    public final static long TB = GB * 1024;

    /**
     * Converts bits to bytes.
     *
     * @param bits Number of bits
     * @return Number of bytes rounded to lover long value.
     */
    public static long bitToByte(long bits) {
        return (long) (bits / 8.0);
    }

    /**
     * Converts bytes to bits.
     *
     * @param bytes Number of bytes
     *
     * @return Number of bits
     */
    public static long byteToBit(long bytes) {
        return bytes * 8;
    }

    public static String toMBString(long bits, int round) {
        return String.format("%." + round + "f MB", bits / (double) (8 * 1024 * 1024));
    }
    
    public static String toGBString(long bits, int round) {
        return String.format("%." + round + "f GB", bits / (double) (8 * 1024 * 1024 * 1024));
    }

    public static String toMBitPerSecString(long bits, int round) {
        return String.format("%." + round + "f MBit/s", bits / (double) (1024 * 1024));
    }
    
      public static String toMBRawString(long bits, int round) {
        return String.format("%." + round + "f", bits / (double) (8 * 1024 * 1024));
    }
    
    public static String toGBRawString(long bits, int round) {
        return String.format("%." + round + "f", bits / (double) (8 * 1024 * 1024 * 1024));
    }

    public static String toMBitPerSecRawString(long bits, int round) {
        return String.format("%." + round + "f", bits / (double) (1024 * 1024));
    }
}
