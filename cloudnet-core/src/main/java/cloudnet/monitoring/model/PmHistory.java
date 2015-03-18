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

import cloudnet.core.PmState;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class PmHistory {

    private int id;
    private long timestamp;
    private String dateTime; // according to locale time zone
    private int dc;
    private PmState state;
    private double energyConsumption; // kWh
    private int vmsCount;
    private int migratedVmsCount;

    private long cpuSpecs;
    private long ramSpecs;
    private long bwSpecs;
    private long sizeSpecs;

    private long cpuProvisioned;
    private long ramProvisioned;
    private long bwProvisioned;
    private long sizeProvisioned;

    private long cpuReq;
    private long ramReq;
    private long bwReq;
    private long sizeReq;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDc() {
        return dc;
    }

    public void setDc(int dc) {
        this.dc = dc;
    }

    public PmState getState() {
        return state;
    }

    public void setState(PmState state) {
        this.state = state;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public int getVmsCount() {
        return vmsCount;
    }

    public void setVmsCount(int vmsCount) {
        this.vmsCount = vmsCount;
    }

    public long getCpuSpecs() {
        return cpuSpecs;
    }

    public void setCpuSpecs(long cpuSpecs) {
        this.cpuSpecs = cpuSpecs;
    }

    public long getRamSpecs() {
        return ramSpecs;
    }

    public void setRamSpecs(long ramSpecs) {
        this.ramSpecs = ramSpecs;
    }

    public long getBwSpecs() {
        return bwSpecs;
    }

    public void setBwSpecs(long bwSpecs) {
        this.bwSpecs = bwSpecs;
    }

    public long getSizeSpecs() {
        return sizeSpecs;
    }

    public void setSizeSpecs(long sizeSpecs) {
        this.sizeSpecs = sizeSpecs;
    }

    public long getCpuProvisioned() {
        return cpuProvisioned;
    }

    public void setCpuProvisioned(long cpuProvisioned) {
        this.cpuProvisioned = cpuProvisioned;
    }

    public long getRamProvisioned() {
        return ramProvisioned;
    }

    public void setRamProvisioned(long ramProvisioned) {
        this.ramProvisioned = ramProvisioned;
    }

    public long getBwProvisioned() {
        return bwProvisioned;
    }

    public void setBwProvisioned(long bwProvisioned) {
        this.bwProvisioned = bwProvisioned;
    }

    public long getSizeProvisioned() {
        return sizeProvisioned;
    }

    public void setSizeProvisioned(long sizeProvisioned) {
        this.sizeProvisioned = sizeProvisioned;
    }

    public long getCpuReq() {
        return cpuReq;
    }

    public void setCpuReq(long cpuReq) {
        this.cpuReq = cpuReq;
    }

    public long getRamReq() {
        return ramReq;
    }

    public void setRamReq(long ramReq) {
        this.ramReq = ramReq;
    }

    public long getBwReq() {
        return bwReq;
    }

    public void setBwReq(long bwReq) {
        this.bwReq = bwReq;
    }

    public long getSizeReq() {
        return sizeReq;
    }

    public void setSizeReq(long sizeReq) {
        this.sizeReq = sizeReq;
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

    public int getMigratedVmsCount() {
        return migratedVmsCount;
    }

    public void setMigratedVmsCount(int migratedVmsCount) {
        this.migratedVmsCount = migratedVmsCount;
    }

}
