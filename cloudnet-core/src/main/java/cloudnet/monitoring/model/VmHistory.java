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

import cloudnet.core.VmState;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmHistory {

    private int id;
    private long timestamp;
    private String dateTime; // UTC
    private Integer pm;
    private Integer dc;
    private Integer pmMigrTo;
    private Integer dcMigrTo;
    private boolean downtime;
    private boolean shortDowntime;
    private VmState state;
    private long runningTimeReq;
    private long runningTimeProvisioned;
    private double cummViolRate;
    private long cummViol;
    private double overallViolRate;
    private long overallViol;

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

    public boolean isShortDowntime() {
        return shortDowntime;
    }

    public void setShortDowntime(boolean shortDowntime) {
        this.shortDowntime = shortDowntime;
    }

    public long getRunningTimeReq() {
        return runningTimeReq;
    }

    public void setRunningTimeReq(long runningTimeReq) {
        this.runningTimeReq = runningTimeReq;
    }

    public long getRunningTimeProvisioned() {
        return runningTimeProvisioned;
    }

    public void setRunningTimeProvisioned(long runningTimeProvisioned) {
        this.runningTimeProvisioned = runningTimeProvisioned;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPm() {
        return pm;
    }

    public void setPm(Integer pm) {
        this.pm = pm;
    }

    public Integer getDc() {
        return dc;
    }

    public void setDc(Integer dc) {
        this.dc = dc;
    }

    public Integer getPmMigrTo() {
        return pmMigrTo;
    }

    public void setPmMigrTo(Integer pmMigrTo) {
        this.pmMigrTo = pmMigrTo;
    }

    public Integer getDcMigrTo() {
        return dcMigrTo;
    }

    public void setDcMigrTo(Integer dcMigrTo) {
        this.dcMigrTo = dcMigrTo;
    }

    public boolean isDowntime() {
        return downtime;
    }

    public void setDowntime(boolean downtime) {
        this.downtime = downtime;
    }

    public VmState getState() {
        return state;
    }

    public void setState(VmState state) {
        this.state = state;
    }

    public double getCummViolRate() {
        return cummViolRate;
    }

    public void setCummViolRate(double cummViolRate) {
        this.cummViolRate = cummViolRate;
    }

    public long getCummViol() {
        return cummViol;
    }

    public void setCummViol(long cummViol) {
        this.cummViol = cummViol;
    }

    public double getOverallViolRate() {
        return overallViolRate;
    }

    public void setOverallViolRate(double overallViolRate) {
        this.overallViolRate = overallViolRate;
    }

    public long getOverallViol() {
        return overallViol;
    }

    public void setOverallViol(long overallViol) {
        this.overallViol = overallViol;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
