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

import cloudnet.provisioners.Provisioner;
import cloudnet.util.Ensure;
import cloudnet.sim.SimClock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class Pm extends CloudEntity {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Pm.class);

    /**
     * Datacenter where pm is placed.
     */
    private Datacenter datacenter;

    /**
     * Specifications
     */
    private final PmSpec spec;

    /**
     * Current state of the Vm.
     */
    private PmState pmState = PmState.PowerOff;

    /**
     * Mips provisioner for vms.
     */
    private Provisioner mipsProvisioner;

    /**
     * Ram provisioner for vms.
     */
    private Provisioner ramProvisioner;

    /**
     * Hdd provisioner for vms.
     */
    private Provisioner sizeProvisioner;

    /**
     * Bw provisioner for vms.
     */
    private Provisioner bwProvisioner;

    private List<Vm> vms;

    private List<Vm> migratedVms;

    public Pm(int id, SimClock clock, PmSpec spec) {
        super(id, clock);
        this.spec = spec;
    }

    public Datacenter getDatacenter() {
        return datacenter;
    }

    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
    }

    public PmSpec getSpec() {
        return spec;
    }

    public PmState getPmState() {
        return pmState;
    }

    public void setPmState(PmState pmState) {
        LOGGER.info(String.format("Pm %d new state:%s", getId(), pmState.toString()));
        this.pmState = pmState;
    }

    public Provisioner getMipsProvisioner() {
        return mipsProvisioner;
    }

    public void setMipsProvisioner(Provisioner mipsProvisioner) {
        this.mipsProvisioner = mipsProvisioner;
    }

    public Provisioner getRamProvisioner() {
        return ramProvisioner;
    }

    public void setRamProvisioner(Provisioner ramProvisioner) {
        this.ramProvisioner = ramProvisioner;
    }

    public Provisioner getSizeProvisioner() {
        return sizeProvisioner;
    }

    public void setSizeProvisioner(Provisioner sizeProvisioner) {
        this.sizeProvisioner = sizeProvisioner;
    }

    public Provisioner getBwProvisioner() {
        return bwProvisioner;
    }

    public void setBwProvisioner(Provisioner bwProvisioner) {
        this.bwProvisioner = bwProvisioner;
    }

    /**
     * Get the value of availableMips for the specified timestamp
     *
     * @param timestamp
     * @return the value of availableMips
     */
    public long getAvailableMips(long timestamp) {
        this.getVms().stream().map(x-> x);
        return getSpec().getMips() - getVms().stream().mapToLong(x -> x.getProvisionedMipsOrDefault(timestamp)).sum();
    }

    /**
     * Get the value of currently available Mips
     *
     * @return the value of availableMips
     */
    public long getAvailableMips() {
        return getSpec().getMips() - getConsumedMips();
    }

    /**
     * Returns requested mips by all vms allocated on the pm for the specified
     * timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getRequestedMips(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getRequestedMips(timestamp)).sum();
    }

    /**
     * Returns requested mips by all vms allocated on the pm for now.
     *
     * @return
     */
    public long getRequestedMips() {
        return getVms().stream().mapToLong(x -> x.getRequestedMips()).sum();
    }

    /**
     * Get the value of currently consumed Mips
     *
     * @return
     */
    public long getConsumedMips() {
        return getVms().stream().mapToLong(x -> x.getProvisionedMips()).sum();
    }

    /**
     * Get the value of consumed Mips for the specified timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getConsumedMips(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getProvisionedMips(timestamp)).sum();
    }

    /**
     * Get the value of currently available bw
     *
     * @return the value of availableMips
     */
    public long getAvailableBw() {
        return getSpec().getBw() - getConsumedBw();
    }

    /**
     * Get the value of currently available bw
     *
     * @param timestamp
     * @return the value of availableMips
     */
    public long getAvailableBw(long timestamp) {
        return getSpec().getBw() - getVms().stream().mapToLong(x -> x.getProvisionedBwOrDefault(timestamp)).sum();
    }

    /**
     * Returns requested bw by all vms allocated on the pm for the specified
     * timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getRequestedBw(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getRequestedBw(timestamp)).sum();
    }

    /**
     * Returns requested bw by all vms allocated on the pm for now.
     *
     * @return
     */
    public long getRequestedBw() {
        return getVms().stream().mapToLong(x -> x.getRequestedBw()).sum();
    }

    /**
     * Get the value of currently consumed Bw
     *
     * @return
     */
    public long getConsumedBw() {
        return getVms().stream().mapToLong(x -> x.getProvisionedBw()).sum();
    }

    /**
     * Get the value of consumed Bw for the specified timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getConsumedBw(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getProvisionedBw(timestamp)).sum();
    }

    /**
     * Get the value of currently available ram
     *
     * @return the value of availableMips
     */
    public long getAvailableRam() {
        return getSpec().getRam() - getConsumedRam();
    }

    /**
     * Get the value of currently available ram
     *
     * @param timestamp
     * @return the value of availableMips
     */
    public long getAvailableRam(long timestamp) {
        return getSpec().getRam() - getVms().stream().mapToLong(x -> x.getProvisionedRamOrDefault(timestamp)).sum();
    }

    /**
     * Returns requested ram by all vms allocated on the pm for the specified
     * timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getRequestedRam(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getRequestedRam(timestamp)).sum();
    }

    /**
     * Returns requested ram by all vms allocated on the pm for now.
     *
     * @return
     */
    public long getRequestedRam() {
        return getVms().stream().mapToLong(x -> x.getRequestedRam()).sum();
    }

    /**
     * Get the value of currently consumed Ram
     *
     * @return
     */
    public long getConsumedRam() {
        return getVms().stream().mapToLong(x -> x.getProvisionedRam()).sum();
    }

    /**
     * Get the value of consumed Ram for the specified timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getConsumedRam(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getProvisionedRam(timestamp)).sum();
    }

    /**
     * Get the value of currently available size
     *
     * @return the value of availableMips
     */
    public long getAvailableSize() {
        return getSpec().getSize() - getConsumedSize();
    }

    /**
     * Get the value of currently available size
     *
     * @param timestamp
     * @return the value of availableMips
     */
    public long getAvailableSize(long timestamp) {
        return getSpec().getSize() - getVms().stream().mapToLong(x -> x.getProvisionedSizeOrDefault(timestamp)).sum();
    }

    /**
     * Returns requested size by all vms allocated on the pm for the specified
     * timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getRequestedSize(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getRequestedSize(timestamp)).sum();
    }

    /**
     * Returns requested size by all vms allocated on the pm for now.
     *
     * @return
     */
    public long getRequestedSize() {
        return getVms().stream().mapToLong(x -> x.getRequestedSize()).sum();
    }

    /**
     * Get the value of currently consumed Size
     *
     * @return
     */
    public long getConsumedSize() {
        return getVms().stream().mapToLong(x -> x.getProvisionedSize()).sum();
    }

    /**
     * Get the value of consumed Size for the specified timestamp.
     *
     * @param timestamp
     * @return
     */
    public long getConsumedSize(long timestamp) {
        return getVms().stream().mapToLong(x -> x.getProvisionedSize(timestamp)).sum();
    }

    /**
     * Get vms
     *
     * @return the value of vms
     */
    public List<Vm> getVms() {
        return vms != null ? vms : (vms = new ArrayList<>());
    }

    public List<Vm> getMigratedVms() {
        return migratedVms != null ? migratedVms : (migratedVms = new ArrayList<>());
    }

    private List<Vm> getVmsSortedBySlaLevel() {
        List<Vm> sortedVms = getVms();
        Collections.sort(sortedVms, new VmSlaComparator());
        return sortedVms;
    }

    public void allocateVm(Vm vm) {
        Ensure.NotNull(vm, "vm");
        Ensure.AreNotEquals(pmState, PmState.PowerOff, "pmState");
        getVms().add(vm);
        LOGGER.info(String.format("Vm %d was allocated to Pm %d", vm.getId(), this.getId()));
    }

    public void beginVmMigration(Vm vm) {
        Ensure.NotNull(vm, "vm");
        Ensure.AreNotEquals(pmState, PmState.PowerOff, "pmState");
        getMigratedVms().add(vm);
    }

    public void finishVmMigration(Vm vm) {
        Ensure.NotNull(vm, "vm");
        Ensure.IsTrue(getMigratedVms().contains(vm), "Vm was not migrated to the pm");
        getMigratedVms().remove(vm);
    }

    public final void deallocateVm(Vm vm) {
        Ensure.NotNull(vm, "vm");
        getVms().remove(vm);
    }

    public final void deallocateAllVms() {
        while (!getVms().isEmpty()) {
            Vm vm = getVms().iterator().next();
            vm.deallocate();
        }
        LOGGER.info(String.format("Pm %d released resources.", this.getId()));

    }

    public void powerOff() {
        Ensure.AreNotEquals(getPmState(), PmState.PowerOff, "pmState");

        // deallocate all vms
        if (!getVms().isEmpty()) {
            LOGGER.warn(String.format("%s is powered off, but it still has vms allocated on it.", this));
            deallocateAllVms();
        }

        // power off
        setPmState(PmState.PowerOff);
    }

    public void run() {
        Ensure.AreEquals(getPmState(), PmState.PowerOff, "pmState");
        setPmState(PmState.Running);
    }

    public void suspend() {
        Ensure.AreNotEquals(getPmState(), PmState.PowerOff, "pmState");
        setPmState(PmState.Suspended);
    }

    @Override
    public void simulateExecutionWork() {
        provisionMipsForVms();
        provisionRamForVms();
        provisionBwForVms();
        provisionSizeForVms();
    }

    private void provisionMipsForVms() {
        long overallMips = getSpec().getMips();
        for (Vm vm : getVmsSortedBySlaLevel()) {
            long reqMips = vm.getRequestedMips();
            long provMips = mipsProvisioner.provisionResource(overallMips, reqMips);
            vm.setProvisionedMips(provMips);
            overallMips -= provMips;
        }

        Ensure.GreaterThanOrEquals(overallMips, 0.0, "overallMips");
    }

    private void provisionRamForVms() {
        long overallRam = getSpec().getRam();
        for (Vm vm : getVmsSortedBySlaLevel()) {
            long reqRam = vm.getRequestedRam();
            long provRam = ramProvisioner.provisionResource(overallRam, reqRam);
            vm.setProvisionedRam(provRam);
            overallRam -= provRam;
        }

        Ensure.GreaterThanOrEquals(overallRam, 0.0, "overallRam");
    }

    private void provisionSizeForVms() {
        long overallSize = getSpec().getSize();
        for (Vm vm : getVmsSortedBySlaLevel()) {
            long reqSize = vm.getRequestedSize();
            long provSize = sizeProvisioner.provisionResource(overallSize, reqSize);
            vm.setProvisionedSize(provSize);
            overallSize -= provSize;
        }

        Ensure.GreaterThanOrEquals(overallSize, 0.0, "overallSize");
    }

    private void provisionBwForVms() {
        long overallBw = getSpec().getBw();
        for (Vm vm : getVmsSortedBySlaLevel()) {
            long reqBw = vm.getRequestedBw();
            long provBw = bwProvisioner.provisionResource(overallBw, reqBw);
            vm.setProvisionedBw(provBw);
            overallBw -= provBw;
        }

        Ensure.GreaterThanOrEquals(overallBw, 0.0, "overallBw");
    }

    public Vm getPmById(int id) {
        Vm result = getVms()
                .stream()
                .filter(vm -> vm.getId() == id)
                .findFirst()
                .get();
        return result;
    }

    /**
     * Returns utilized power for last sim step in KWatt/h.
     *
     * @return Utilized power
     */
    public double getUtilizedPower() {
        if (pmState == PmState.PowerOff) {
            return 0.0;
        }

        double workload = (double) (getConsumedMips()) / getSpec().getMips();
        LOGGER.trace(String.format("Pm %d cpu workload is %.2f.", this.getId(), workload));
        double power = getSpec().getPower(workload) / 1000.0;
        return power;
    }

    /**
     * Returns utilized power for the specified timestamp in KWatt/h.
     *
     * Storing of pm states should be performed.
     *
     * @param timestamp
     * @return Utilized power
     */
    @Deprecated
    public double getUtilizedPower(long timestamp) {
        double workload = (double) (getConsumedMips(timestamp)) / getSpec().getMips();
        LOGGER.trace(String.format("Pm %d cpu workload is %.2f.", this.getId(), workload));
        double power = getSpec().getPower(workload) / 1000.0;
        return power;
    }

    @Override
    public String toString() {
        long consumedMips = getConsumedMips();
        long consumedRam = getConsumedRam();
        long consumedSize = getConsumedSize();
        long consumedBw = getConsumedBw();
        long consumedMipsPerc = (long) (consumedMips / (double) getSpec().getMips() * 100);
        long consumedRamPerc = (long) (consumedRam / (double) getSpec().getRam() * 100);
        long consumedSizePerc = (long) (consumedSize / (double) getSpec().getSize() * 100);
        long consumedBwPerc = (long) (consumedBw / (double) getSpec().getBw() * 100);
        return String.format("Pm [id=%d, state=%s, vms=%d, mips=%d(%d%%), ram=%s(%d%%), size=%s(%d%%), bw=%s(%d%%)]",
                getId(),
                getPmState().toString(),
                getVms().size(),
                consumedMips, consumedMipsPerc,
                Size.toMBString(consumedRam, 0), consumedRamPerc,
                Size.toMBString(consumedRam, 0), consumedSizePerc,
                Size.toMBitPerSecString(consumedBw, 0), consumedBwPerc);
    }

}
