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

import cloudnet.poweroutage.PowerOutageModel;
import cloudnet.weather.TemperatureModel;
import cloudnet.cooling.CoolingModel;
import cloudnet.locations.Location;
import cloudnet.util.Ensure;
import cloudnet.sim.SimClock;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Datacenter extends CloudEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Datacenter.class);

    protected List<Pm> pms;
    protected EnergyPriceModel energyPrice;
    protected TemperatureModel temperature;
    protected CoolingModel coolingModel;
    protected Location location;
    protected Cloud cloud;
    private double energyOverallUsage = 0d;
    protected double energyCosts = 0d;
    private PowerOutageModel powerOutage;

    public Datacenter(int id, SimClock clock) {
        super(id, clock);
    }

    public static Datacenter forLocation(int id, SimClock clock, Location location) {
        Datacenter dc = new Datacenter(id, clock);
        dc.setEnergyPrice(location.getEnergyPrice());
        dc.setTemperature(location.getTemperature());
        dc.setPowerOutage(location.getPowerOutage());
        dc.setLocation(location);
        return dc;
    }

    public List<Pm> getPms() {
        return pms != null ? pms : (pms = new ArrayList<>());
    }

    public void setPms(List<Pm> pms) {
        this.pms = pms;
    }

    public EnergyPriceModel getEnergyPrice() {
        return energyPrice;
    }

    public void setEnergyPrice(EnergyPriceModel energyPrice) {
        this.energyPrice = energyPrice;
    }

    public TemperatureModel getTemperature() {
        return temperature;
    }

    public void setTemperature(TemperatureModel temperature) {
        this.temperature = temperature;
    }

    public CoolingModel getCoolingModel() {
        return coolingModel;
    }

    public void setCoolingModel(CoolingModel coolingModel) {
        this.coolingModel = coolingModel;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getEnergyCosts() {
        return energyCosts;
    }

    public PowerOutageModel getPowerOutage() {
        return powerOutage;
    }

    public void setPowerOutage(PowerOutageModel powerOutage) {
        this.powerOutage = powerOutage;
    }

    public void addPm(Pm pm) {
        Ensure.NotNull(pm, "pm");
        pm.setDatacenter(this);
        getPms().add(pm);
        LOGGER.info("%s added to %s.", pm.toShortString(), toShortString());
    }

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    @Override
    public void simulateExecutionWork() {
    }

    public Pm getPmById(int id) {
        Pm result = getPms()
                .stream()
                .filter(pm -> pm.getId() == id)
                .findFirst()
                .get();
        return result;
    }

    /**
     * Returns utilized power by DC (by all PMs located in the DC) in KWatt/h.
     *
     * @return utilized power rate
     */
    public double getUtilizedPower() {
        double power = getPms().stream().mapToDouble(pm -> pm.getUtilizedPower()).sum();
        return power;
    }

    /**
     * Returns utilized power by DC considering power consumed for cooling in
     * KWatt/h.
     *
     * @return utilized power rate
     */
    public double getUtilizedPowerWithPue() {
        double currTemp = getTemperature().getTemperature(getClock().now());
        double pPue = getCoolingModel().getpPUE(currTemp);
        double power = getUtilizedPower();
        double powerWithPPue = power * (1 + pPue);
        return powerWithPPue;
    }

    public double getEnergyOverallUsage() {
        return energyOverallUsage;
    }

    /**
     * Computes energy costs for the last simulation step and updates data about
     * energyOverallUsage and energyCosts.
     */
    public void computeEnergyCostsForLastStep() {
        double price = getEnergyPrice().getPrice(getClock().now());
        LOGGER.trace(String.format("Energy price for %s is %.4f.", toShortString(), price));
        LOGGER.trace(String.format("Temperature for %s is %.2f.", toShortString(), getTemperature().getTemperature(clock.now())));

        double powerWithPPue = getUtilizedPowerWithPue();
        long powerOutageDuration = getPowerOutageDuration();
        if (powerOutageDuration > 0L) {
            LOGGER.info("Power outage duration of %s: %d.", this.toShortString(), powerOutageDuration);
        }
        double executionTime = TimeFrame.msToHour(getClock().diff() - powerOutageDuration);
        double powerWithPPueDiff = powerWithPPue * executionTime;
        energyOverallUsage += powerWithPPueDiff;
        LOGGER.trace(String.format("Utilized energy rate for %s is %.4f kWh.", toShortString(), powerWithPPue));
        LOGGER.trace(String.format("Energy usage for %s increased on %.4f kW to %.4f kW.",
                toShortString(), powerWithPPueDiff, energyOverallUsage));

        energyCosts += price * powerWithPPueDiff;
        LOGGER.trace(String.format("Energy costs for dc %s increased to %.4f.", toShortString(), energyCosts));
    }

    /**
     * Returns duration of poweroutage for the last simulation step.
     *
     * @return duration of poweroutage
     */
    public long getPowerOutageDuration() {
        return getPowerOutage().getDuration(getClock().previous(), getClock().now());
    }

    @Override
    public String toShortString() {
        return String.format("DC %d (%s)", getId(), getLocation().getDescription());
    }
}
