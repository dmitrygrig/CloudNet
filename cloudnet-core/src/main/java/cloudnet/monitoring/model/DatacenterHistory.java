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
package cloudnet.monitoring.model;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class DatacenterHistory {

    private int id;
    private long timestamp;
    private String dateTime; // according to locale time zone
    private String location;
    private double temperature;
    private String coolingMode;
    private double ppue;
    private double energyCosts;
    private double energyUsageRate;
    private double energyOverallUsage;
    private double energyPrice;
    private boolean dayOrNight; // True = day, False = night
    private int pmsCount;
    private int pmsSwitchedOnCount;
    private long powerOutageDuration;
    private int vmsCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getCoolingMode() {
        return coolingMode;
    }

    public void setCoolingMode(String coolingMode) {
        this.coolingMode = coolingMode;
    }

    public double getEnergyCosts() {
        return energyCosts;
    }

    public void setEnergyCosts(double energyCosts) {
        this.energyCosts = energyCosts;
    }

    public double getEnergyPrice() {
        return energyPrice;
    }

    public void setEnergyPrice(double energyPrice) {
        this.energyPrice = energyPrice;
    }

    public boolean isDayOrNight() {
        return dayOrNight;
    }

    public void setDayOrNight(boolean dayOrNight) {
        this.dayOrNight = dayOrNight;
    }

    public double getEnergyUsageRate() {
        return energyUsageRate;
    }

    public void setEnergyUsageRate(double energyUsageRate) {
        this.energyUsageRate = energyUsageRate;
    }

    public double getEnergyOverallUsage() {
        return energyOverallUsage;
    }

    public void setEnergyOverallUsage(double energyOverallUsage) {
        this.energyOverallUsage = energyOverallUsage;
    }

    public double getPpue() {
        return ppue;
    }

    public void setPpue(double ppue) {
        this.ppue = ppue;
    }

    public int getPmsCount() {
        return pmsCount;
    }

    public void setPmsCount(int pmsCount) {
        this.pmsCount = pmsCount;
    }

    public int getPmsSwitchedOnCount() {
        return pmsSwitchedOnCount;
    }

    public void setPmsSwitchedOnCount(int pmsSwitchedOnCount) {
        this.pmsSwitchedOnCount = pmsSwitchedOnCount;
    }

    public long getPowerOutageDuration() {
        return powerOutageDuration;
    }

    public void setPowerOutageDuration(long powerOutageDuration) {
        this.powerOutageDuration = powerOutageDuration;
    }

    public int getVmsCount() {
        return vmsCount;
    }

    public void setVmsCount(int vmsCount) {
        this.vmsCount = vmsCount;
    }
    
    

}
