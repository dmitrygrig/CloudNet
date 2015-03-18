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
import cloudnet.util.Ensure;
import cloudnet.workloads.WorkloadHistory;
import cloudnet.workloads.WorkloadModel;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Vm extends CloudEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Vm.class);

    /**
     * The id of the user Vm belongs to.
     */
    private int userId;

    /**
     * Vm Specifications.
     */
    private VmSpec spec;

    /**
     * The server where VM is allocated.
     */
    private Pm server;

    /**
     * The workload model of the allocated size.
     */
    private WorkloadModel sizeWorkloadModel;

    /**
     * The workload model of the allocated ram.
     */
    private WorkloadModel ramWorkloadModel;

    /**
     * The workload model of the allocated bw.
     */
    private WorkloadModel bwWorkloadModel;

    /**
     * The workload model of the allocated cpu.
     */
    private WorkloadModel cpuWorkloadModel;

    private final WorkloadHistory sizeWorkloadHistory = new WorkloadHistory();
    private final WorkloadHistory ramWorkloadHistory = new WorkloadHistory();
    private final WorkloadHistory bwWorkloadHistory = new WorkloadHistory();
    private final WorkloadHistory cpuWorkloadHistory = new WorkloadHistory();

    /**
     * Current state of the Vm.
     */
    private VmState vmState = VmState.PowerOff;

    /**
     * Is Vm in downtime.
     */
    private boolean inDowntime;

    /**
     * Is Vm in short downtime.
     */
    private boolean inShortDowntime;

    /**
     * The overall time when VM is in the running state.
     */
    private long runningTime;

    /**
     * The time of vm creation.
     */
    private final long createTime;

    /**
     * The time whem vm is deleted.
     */
    private long deleteTime;

    /**
     * Sla for vm.
     */
    private Sla sla;

    /**
     * Already migrated size of the Vm to another server.
     */
    private long migratedSize;

    /**
     * The server where VM is being migrated.
     */
    private Pm migratedToServer;

    /**
     * The time between current time and last time where there was enough
     * resources for vm.
     */
    private long cummulativeDowntime;

    public Vm(int id, SimClock clock) {
        super(id, clock);
        this.createTime = clock.now();
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public VmSpec getSpec() {
        return spec;
    }

    public void setSpec(VmSpec spec) {
        this.spec = spec;
    }

    public Pm getServer() {
        return server;
    }

    private void setServer(Pm server) {
        this.server = server;
    }

    public Pm getMigratedToServer() {
        return migratedToServer;
    }

    private void setMigratedToServer(Pm migratedToServer) {
        this.migratedToServer = migratedToServer;
    }

    public WorkloadModel getSizeWorkloadModel() {
        return sizeWorkloadModel;
    }

    public void setSizeWorkloadModel(WorkloadModel sizeWorkloadModel) {
        this.sizeWorkloadModel = sizeWorkloadModel;
    }

    public WorkloadModel getRamWorkloadModel() {
        return ramWorkloadModel;
    }

    public void setRamWorkloadModel(WorkloadModel ramWorkloadModel) {
        this.ramWorkloadModel = ramWorkloadModel;
    }

    public WorkloadModel getBwWorkloadModel() {
        return bwWorkloadModel;
    }

    public void setBwWorkloadModel(WorkloadModel bwWorkloadModel) {
        this.bwWorkloadModel = bwWorkloadModel;
    }

    public WorkloadModel getCpuWorkloadModel() {
        return cpuWorkloadModel;
    }

    public void setCpuWorkloadModel(WorkloadModel cpuWorkloadModel) {
        this.cpuWorkloadModel = cpuWorkloadModel;
    }

    public WorkloadHistory getSizeWorkloadHistory() {
        return sizeWorkloadHistory;
    }

    public WorkloadHistory getRamWorkloadHistory() {
        return ramWorkloadHistory;
    }

    public WorkloadHistory getBwWorkloadHistory() {
        return bwWorkloadHistory;
    }

    public WorkloadHistory getCpuWorkloadHistory() {
        return cpuWorkloadHistory;
    }

    public VmState getVmState() {
        return vmState;
    }

    public void setVmState(VmState vmState) {
        this.vmState = vmState;
    }

    public long getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(long overallRunningTime) {
        this.runningTime = overallRunningTime;
    }

    public long getCummulativeDowntime() {
        return cummulativeDowntime;
    }

    public long getRequestedRunningTime() {
        return lastSimulationTime - createTime;
    }

    public Sla getSla() {
        return sla;
    }

    public void setSla(Sla sla) {
        this.sla = sla;
    }

    public boolean isInDowntime() {
        return inDowntime;
    }

    private void setInDowntime(boolean inDowntime) {
        this.inDowntime = inDowntime;
    }

    public boolean isInShortDowntime() {
        return inShortDowntime;
    }

    public long getMigratedSize() {
        return migratedSize;
    }

    public void setMigratedSize(long migratedSize) {
        this.migratedSize = migratedSize;
    }

    public void reduceMigratedSize(long migratedSize) {
        this.migratedSize = FastMath.max(this.migratedSize - migratedSize, 0L);
    }

    /**
     * Returns true, if vm has been already allocated to some server, otherwise
     * false.
     *
     * @return True, if vm has been already allocated to some server, otherwise
     * false
     */
    public boolean isAllocated() {
        return getServer() != null;
    }

    /**
     * Returns true, if vm is migrate to some server, otherwise false.
     *
     * @return True, if vm is migrate to some server, otherwise false.
     */
    public boolean isInMigration() {
        return getMigratedToServer() != null;
    }

    /**
     * Adds this vm to the specified server.
     *
     * @param server
     * @throws IllegalStateException If vm was already allocated
     */
    public void allocateTo(Pm server) {
        if (isAllocated()) {
            throw new IllegalStateException(String.format("Vm %d was already allocated to Pm %d", this.getId(), getServer().getId()));
        }

        LOGGER.trace("Vm %d is being allocated to Pm %d.", this.getId(), server.getId());

        server.allocateVm(this);
        setServer(server);
    }

    /**
     * Migrates this vm to the specified server and deallocates it from current.
     *
     * @param migrateToServer
     * @throws IllegalStateException If vm has not been allocated yet
     */
    public void migrateTo(Pm migrateToServer) {
        if (!isAllocated()) {
            throw new IllegalStateException(String.format("Vm %d has not been allocated yet", this.getId()));
        }

        LOGGER.trace("Immediate migration of Vm %d from Pm %d to Pm %d.", this.getId(), getServer().getId(), migrateToServer.getId());

        getServer().deallocateVm(this);
        migrateToServer.allocateVm(this);
        setServer(migrateToServer);
        migratedSize = 0;
    }

    public void beginMigrationTo(Pm migrateToServer) {
        if (!isAllocated()) {
            throw new IllegalStateException(String.format("Vm %d has not been allocated yet.", this.getId()));
        }

        if (isInMigration()) {
            throw new IllegalStateException(String.format("Migration of Vm %d has already been started.", this.getId()));
        }

        LOGGER.info("Continious migration of Vm %d from Pm %d to Pm %d is started.",
                this.getId(), getServer().getId(), migrateToServer.getId());

        migrateToServer.beginVmMigration(this);
        setMigratedToServer(migrateToServer);
        long requestedRam = getRequestedRam();
        migratedSize = (long) (requestedRam * PageDirtyRate.getRate(requestedRam / (double) getSpec().getRam()));

        LOGGER.trace("Estimated migrated size for %s is %d.", this.toString(), migratedSize);

        Ensure.GreaterThan(migratedSize, 0, "migratedSize");
    }

    public void finishMigration() {
        if (!isInMigration()) {
            throw new IllegalStateException(String.format("Migration of Vm %d has not been started.", this.getId()));
        }

        LOGGER.info("Continious migration of Vm %d from Pm %d to Pm %d is finished.",
                this.getId(), getServer().getId(), getMigratedToServer().getId());

        deallocate();
        getMigratedToServer().finishVmMigration(this);
        allocateTo(getMigratedToServer());
        setMigratedToServer(null);
        migratedSize = 0;
    }

    public void deallocate() {
        if (!isAllocated()) {
            throw new IllegalStateException(String.format("Vm %d has not been allocated yet", this.getId()));
        }

        LOGGER.trace("Vm %d is being deallocated from Pm %d.", this.getId(), getServer().getId());

        getServer().deallocateVm(this);
        setServer(null);
    }

    public void increaseRunningTime(long diff) {
        this.runningTime += diff;
    }

    public long getDowntime() {
        return getRequestedRunningTime() - getRunningTime();
    }

    public long getRequestedRam(long timestamp) {
        return FastMath.round(getRamWorkloadModel().getWorkload(timestamp) * getSpec().getRam());
    }

    public long getRequestedSize(long timestamp) {
        return FastMath.round(getSizeWorkloadModel().getWorkload(timestamp) * getSpec().getSize());
    }

    public long getRequestedBw(long timestamp) {
        return FastMath.round(getBwWorkloadModel().getWorkload(timestamp) * getSpec().getBw());
    }

    public long getRequestedMips(long timestamp) {
        return FastMath.round(getCpuWorkloadModel().getWorkload(timestamp) * getSpec().getMips());
    }

    public long getProvisionedRam(long timestamp) {
        return getRamWorkloadHistory().getWorkload(timestamp);
    }

    public long getProvisionedSize(long timestamp) {
        return getSizeWorkloadHistory().getWorkload(timestamp);
    }

    public long getProvisionedBw(long timestamp) {
        return getBwWorkloadHistory().getWorkload(timestamp);
    }

    public long getProvisionedMips(long timestamp) {
        return getCpuWorkloadHistory().getWorkload(timestamp);
    }

    public long getProvisionedRamOrDefault(long timestamp) {
        return getRamWorkloadHistory().getWorkloadOrDefault(timestamp);
    }

    public long getProvisionedSizeOrDefault(long timestamp) {
        return getSizeWorkloadHistory().getWorkloadOrDefault(timestamp);
    }

    public long getProvisionedBwOrDefault(long timestamp) {
        return getBwWorkloadHistory().getWorkloadOrDefault(timestamp);
    }

    public long getProvisionedMipsOrDefault(long timestamp) {
        return getCpuWorkloadHistory().getWorkloadOrDefault(timestamp);
    }

    public long getRequestedRam() {
        return FastMath.round(getRamWorkloadModel().getWorkload(clock.now()) * getSpec().getRam());
    }

    public long getRequestedSize() {
        return FastMath.round(getSizeWorkloadModel().getWorkload(clock.now()) * getSpec().getSize());
    }

    public long getRequestedBw() {
        return FastMath.round(getBwWorkloadModel().getWorkload(clock.now()) * getSpec().getBw());
    }

    public long getRequestedMips() {
        return FastMath.round(getCpuWorkloadModel().getWorkload(clock.now()) * getSpec().getMips());
    }

    public long getProvisionedRam() {
        return getRamWorkloadHistory().getWorkloadOrDefault(clock.now());
    }

    public long getProvisionedSize() {
        return getSizeWorkloadHistory().getWorkloadOrDefault(clock.now());
    }

    public long getProvisionedBw() {
        return getBwWorkloadHistory().getWorkloadOrDefault(clock.now());
    }

    public long getProvisionedMips() {
        return getCpuWorkloadHistory().getWorkloadOrDefault(clock.now());
    }

    public void setProvisionedRam(long value) {
        getRamWorkloadHistory().saveWorkload(clock.now(), value);
    }

    public void setProvisionedSize(long value) {
        getSizeWorkloadHistory().saveWorkload(clock.now(), value);
    }

    public void setProvisionedBw(long value) {
        getBwWorkloadHistory().saveWorkload(clock.now(), value);
    }

    public void setProvisionedMips(long value) {
        getCpuWorkloadHistory().saveWorkload(clock.now(), value);
    }

    public void powerOff() {
        Ensure.AreNotEquals(getVmState(), VmState.PowerOff, "vmState");
        setVmState(VmState.PowerOff);
    }

    public void run() {
        Ensure.AreEquals(getVmState(), VmState.PowerOff, "vmState");
        setVmState(VmState.Running);
    }

    public void suspend() {
        Ensure.AreNotEquals(getVmState(), VmState.PowerOff, "vmState");
        setVmState(VmState.Suspended);
    }

    public boolean isEnoughResources() {
        // ToDo impl resource consumption consideration for all resources
        boolean result = true;

        // cpu
        long requested = getRequestedMips();
        long provisioned = getProvisionedMips();
        if (requested > provisioned) {
            LOGGER.warn("There is not enough mips for %s, requested=%d,provisioned=%d",
                    this.toShortString(), requested, provisioned);
            result = false;
        }

        // bw
//        requested = getRequestedBw();
//        provisioned = getRequestedBw();
//        if (requested > provisioned) {
//            LOGGER.warn("There is not enough bw for %s, requested=%d,provisioned=%d",
//                    this.toShortString(), requested, provisioned);
//        }
//
//        //ram
//        requested = getRequestedRam();
//        provisioned = getProvisionedRam();
//        if (requested > provisioned) {
//            LOGGER.warn("There is not enough ram for %s, requested=%d,provisioned=%d",
//                    this.toShortString(), requested, provisioned);
//        }
//
//        // size
//        requested = getRequestedSize();
//        provisioned = getProvisionedSize();
//        if (requested > provisioned) {
//            LOGGER.warn("There is not enough size for %s, requested=%d,provisioned=%d",
//                    this.toShortString(), requested, provisioned);
//        }
//        
        return result;
    }

    @Override
    public void simulateExecutionWork() {
        if (!isAllocated()) {
            setInDowntime(true);
            this.inShortDowntime = true;
            this.cummulativeDowntime += getClock().diff();
        } else if (!isEnoughResources()) {
            LOGGER.warn("%s is allocated on %s, but there is not enough resources for execution that results in downtime.",
                    toShortString(), getServer().toShortString());
            setInDowntime(true);
            this.cummulativeDowntime += getClock().diff();
            this.inShortDowntime = true;
        } else {
            // obtain duration of poweroutage for the last step
            long shortTermDowntime = getServer().getDatacenter().getPowerOutageDuration();
            this.inShortDowntime = shortTermDowntime != 0L;
            // if it equals diff, set downtime to true
            if (shortTermDowntime == getClock().diff()) {
                setInDowntime(true);
                this.cummulativeDowntime += getClock().diff();
            } else {
                setInDowntime(false);
                this.increaseRunningTime(getClock().diff() - shortTermDowntime);
                this.cummulativeDowntime = 0L;
            }
        }
    }

    @Override
    public String toString() {
        long requestedMips = getRequestedMips();
        long requestedRam = getRequestedRam();
        long requestedSize = getRequestedSize();
        long requestedBw = getRequestedBw();
        long requestedMipsPerc = (long) (requestedMips / (double) getSpec().getMips() * 100);
        long requestedRamPerc = (long) (requestedRam / (double) getSpec().getRam() * 100);
        long requestedSizePerc = (long) (requestedSize / (double) getSpec().getSize() * 100);
        long requestedBwPerc = (long) (requestedBw / (double) getSpec().getBw() * 100);
        return String.format("Vm [id=%d, vmstate=%s, pm=%d, migrTo=%d, runTime=%d, mips=%d, ram=%s, size=%s, bw=%s, "
                + "reqRunTime=%d, reqMips=%d(%d%%), reqRam=%s(%d%%), reqSize=%s(%d%%), reqBw=%s(%d%%)]",
                getId(),
                getVmState().toString(),
                getServer() == null ? null : getServer().getId(),
                getMigratedToServer() == null ? null : getMigratedToServer().getId(),
                getRunningTime(),
                getProvisionedMips(),
                Size.toMBString(getProvisionedRam(), 0),
                Size.toMBString(getProvisionedSize(), 0),
                Size.toMBitPerSecString(getProvisionedBw(), 0),
                getRequestedRunningTime(),
                requestedMips, requestedMipsPerc,
                Size.toMBString(requestedRam, 0), requestedRamPerc,
                Size.toMBString(requestedSize, 0), requestedSizePerc,
                Size.toMBitPerSecString(requestedBw, 0), requestedBwPerc);
    }
}
