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
package cloudnet.sim;

import cloudnet.core.Cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of SimEngine interface that simulates the predetermined number
 * of steps
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class SimEngineSimple implements SimEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimEngineSimple.class);

    private final SimClock clock;
    private final Scheduler scheduler;
    private final Cloud cloud;
    private final long stepCount;
    private long simStart; // time of the simulation start
    private long elapsedSteps;

    public SimEngineSimple(SimClock clock, Scheduler scheduler, Cloud cloud, long stepCount) {
        this.clock = clock;
        this.scheduler = scheduler;
        this.cloud = cloud;
        this.stepCount = stepCount;
    }

    @Override
    public void start() {
        simStart = System.currentTimeMillis();
        LOGGER.info("Simulation starting...");

        for (long i = 0; i < stepCount; i++) {

            LOGGER.info(String.format("Step %d...", i));

            long currentTime = System.currentTimeMillis();

            // schedule 
            scheduler.schedule(cloud, clock);

            // inc clock
            clock.add();

            // simulate cloud execution
            cloud.simulateExecution();

            // save current value for clock
            clock.sync();

            LOGGER.info(String.format("Consumed time for step: %d ms",
                    System.currentTimeMillis() - currentTime));

            // Force GC
            System.gc();

            elapsedSteps++;
        }
    }

    @Override
    public void stop() {
        cloud.stopExecution();
        LOGGER.info(String.format("Simulation stopeped. Consumed time: %d sec",
                (System.currentTimeMillis() - simStart) / 1000L));
    }

    @Override
    public long getElapsedSteps() {
        return elapsedSteps;
    }
}
