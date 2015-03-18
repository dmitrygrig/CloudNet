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
package cloudnet.examples;

import cloudnet.azure.Azure;
import cloudnet.core.Size;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class AzureDefaultValuesApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(Azure.RamA0);
        System.out.println(Azure.RamA1);
        System.out.println(Azure.RamA2);
        System.out.println(Size.MB);
        System.out.println(1750 * Size.MB);
    }

}
