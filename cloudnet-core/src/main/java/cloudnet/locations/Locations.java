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
package cloudnet.locations;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Locations {

    private static final List<Location> locations = new ArrayList<>();

    private Locations() {

    }
    
    public static void registerDefault(){
        register(new Oslo());
        register(new Vienna());
        register(new Toronto());
        register(new Tokyo());
        register(new RioDeJaneiro());
    }
    
    static {
        registerDefault();
    }

    public static List<Location> get() {
        return new ArrayList<>(locations);
    }

    public static Location byCity(String city) {
        return locations.stream().filter(l -> l.getCity().equalsIgnoreCase(city)).findFirst().get();
    }

    public static Location byCountry(String country) {
        return locations.stream().filter(l -> l.getCountry().equalsIgnoreCase(country)).findFirst().get();
    }

    public static void register(Location location) {
        locations.add(location);
    }

    public static void unregister(Location location) {
        locations.remove(location);
    }

}
