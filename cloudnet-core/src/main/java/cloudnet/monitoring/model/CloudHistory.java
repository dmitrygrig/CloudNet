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
public class CloudHistory {

    private long timestamp;
    private String dateTime; // UTC
    private double energyCosts;
    private double slaViolationCosts;
    private double energyConsumption;
    private long violationCount;
    private long shortViolationCount;
    private long vmMigrationCount;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getEnergyCosts() {
        return energyCosts;
    }

    public void setEnergyCosts(double energyCosts) {
        this.energyCosts = energyCosts;
    }

    public double getSlaViolationCosts() {
        return slaViolationCosts;
    }

    public void setSlaViolationCosts(double slaViolationCosts) {
        this.slaViolationCosts = slaViolationCosts;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getViolationCount() {
        return violationCount;
    }

    public void setViolationCount(long violationCount) {
        this.violationCount = violationCount;
    }

    public long getShortViolationCount() {
        return shortViolationCount;
    }

    public void setShortViolationCount(long shortViolationCount) {
        this.shortViolationCount = shortViolationCount;
    }

    public long getVmMigrationCount() {
        return vmMigrationCount;
    }

    public void setVmMigrationCount(long vmMigrationCount) {
        this.vmMigrationCount = vmMigrationCount;
    }

}
