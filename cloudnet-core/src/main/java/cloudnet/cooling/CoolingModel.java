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
package cloudnet.cooling;

/**
 * CoolingModel interface should be implemented in order to get power usage
 * effectiveness of a cooling system used in a datacenter.
 *
 * More information can be found in the article: Hong Xu, Chen Feng, Baochun Li.
 * "Temperature Aware Workload Management in Geo-distributed Datacenters",
 * SIGMETRICS'13,June 17–21, 2013, Pittsburgh, PA, USA.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public interface CoolingModel {

    /**
     * Returns the partial power usage effectiveness (pPUE) defined as the sum
     * of server power and cooling overhead divided by server power.
     *
     * @param temperature
     * @return
     */
    double getpPUE(double temperature);
    
    /**
     * Returns name of the using model considering temperature.
     * @param temperature
     * @return name of mode
     */
    String getMode(double temperature);
}
