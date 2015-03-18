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

import cloudnet.core.TimeFrame;
import cloudnet.util.Ensure;
import cloudnet.util.DateTimeUtils;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulation clock shows the actual simulation time. The actual simulation time
 * can be polled by any object but can only be set by the simulation engine
 * responsible for this.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public final class SimClock {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimClock.class);

    private final AtomicLong previousTime;
    private final AtomicLong currentTime;
    private final long step;

    public SimClock() {
        this(TimeFrame.Sec);
    }

    public SimClock(long step) {
        this(step, 0L);
    }

    public SimClock(long step, long initial) {
        this.step = step;
        currentTime = new AtomicLong(initial);
        previousTime = new AtomicLong(currentTime.get());

        LOGGER.info(String.format("Time init: %d", now()));
    }

    /**
     * Adds step to current time.
     *
     */
    public final void add() {
        add(step);
    }

    /**
     * Adds defined delta to current time.
     *
     * @param delta
     */
    public final void add(long delta) {
        this.currentTime.addAndGet(delta);
        LOGGER.info(String.format("CurrentTime increased: %d (%s UTC)",
                this.currentTime.get(),
                DateTimeUtils.getDateTime(this.currentTime.get(), "UTC")));
    }

    /**
     * Syncronizes previous time with current one
     */
    public final void sync() {
        previousTime.set(currentTime.get());
        LOGGER.trace("TimeSync");
    }

    /**
     * Returns current time.
     *
     * @return Current timestamp
     */
    public final long now() {
        return currentTime.get();
    }

    /**
     * Returns previous time.
     *
     * @return Previous timestamp
     */
    public final long previous() {
        return previousTime.get();
    }

    /**
     * Returns diff between current and previous time.
     *
     * @return Difference between current and previous time
     */
    public final long diff() {
        long result = currentTime.get() - previousTime.get();
        Ensure.GreaterThanOrEquals(result, 0, "Current time should be always greater or equals than previous one.");
        return result;
    }

    public final long getStep() {
        return step;
    }

    public final double diffSeconds() {
        return diff() / (double) TimeFrame.Sec;
    }

    public final double diffHours() {
        return diff() / (double) TimeFrame.Hour;
    }

    public String toUtc() {
        return DateTimeUtils.getDateTime(now(), "UTC");
    }
}
