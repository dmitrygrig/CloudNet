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

import cloudnet.sim.SimClock;
import cloudnet.sim.Simulated;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public abstract class CloudEntity implements Simulated {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEntity.class);
    private List<CloudEntityPlugin> plugins;

    private final int id;
    protected final SimClock clock;
    protected long lastSimulationTime;

    public CloudEntity(int id, SimClock clock) {
        this.id = id;
        this.clock = clock;
        this.lastSimulationTime = clock.now();
    }

    public int getId() {
        return id;
    }

    public SimClock getClock() {
        return clock;
    }

    public long getLastSimulationTime() {
        return lastSimulationTime;
    }

    public void setLastSimulationTime(long lastSimulationTime) {
        this.lastSimulationTime = lastSimulationTime;
    }

    public List<CloudEntityPlugin> getPlugins() {
        return plugins != null ? plugins : (plugins = new ArrayList<>());
    }

    public void setPlugins(List<CloudEntityPlugin> plugins) {
        this.plugins = plugins;
    }
    
    public void attachPlugin(CloudEntityPlugin plugin){
        getPlugins().add(plugin);
    }

    public String toShortString() {
        return String.format("%s %d", getClass().getSimpleName(), getId());
    }

    @Override
    public void simulateExecution() {
        LOGGER.trace(String.format("Execution simulation on the %s...", this.toShortString()));
        executePluginsBefore();
        simulateExecutionWork();
        executePluginsAfter();
        setLastSimulationTime(clock.now());
        LOGGER.trace(String.format("Execution simulated on the %s.", this.toShortString()));
    }
    
    @Override
    public void stopExecution(){
        getPlugins().forEach(x-> x.release());
    }
    
     private void executePluginsBefore(){
        getPlugins().forEach(x-> x.executeBeforeExecution(this));
    }
    
    private void executePluginsAfter(){
        getPlugins().forEach(x-> x.executeAfterExecution(this));
    }

    public abstract void simulateExecutionWork();

}
