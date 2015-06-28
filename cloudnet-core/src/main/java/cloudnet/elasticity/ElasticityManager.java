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
package cloudnet.elasticity;

import cloudnet.core.Cloud;
import cloudnet.core.CloudEntityPlugin;

/**
 * The utilization of cloud resources on which application component instances
 * are deployed is monitored by ElasticityManager. This could be, for example,
 * the CPU load of a virtual server. This information is used to determine the
 * number of required instances.
 *
 * http://www.cloudcomputingpatterns.org/Elasticity_Manager
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public abstract class ElasticityManager implements CloudEntityPlugin<Cloud> {

    public abstract void manage(Cloud cloud);
    
    @Override
    public void executeBeforeExecution(Cloud entity) {
        manage(entity);
    }

    @Override
    public void executeAfterExecution(Cloud entity) {
    }
    
    @Override
    public void release(){
    }

}
