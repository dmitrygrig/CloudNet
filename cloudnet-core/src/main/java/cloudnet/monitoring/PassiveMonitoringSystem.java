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
package cloudnet.monitoring;

import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.core.MonitoringSystem;
import cloudnet.core.Pm;
import cloudnet.core.PmState;
import cloudnet.core.Vm;
import cloudnet.monitoring.model.CloudHistory;
import cloudnet.monitoring.model.DatacenterHistory;
import cloudnet.monitoring.model.PmHistory;
import cloudnet.monitoring.model.VmHistory;
import cloudnet.sla.VmAvailabilityBasedSla;
import cloudnet.util.DateTimeUtils;
import cloudnet.util.Ensure;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class PassiveMonitoringSystem implements MonitoringSystem {

    protected final HistoryWriter writer;

    public PassiveMonitoringSystem(HistoryWriter writer) {
        this.writer = writer;
    }

    @Override
    public void monitor(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        CloudHistory cloudHistory = getCloudHistory(cloud);
        writeCloudHistory(cloudHistory);

        for (Datacenter dc : cloud.getDatacenters()) {
            DatacenterHistory dcHist = getDcHistory(dc);
            writeDcHistory(dcHist);

            for (Pm pm : dc.getPms()) {
                PmHistory pmHist = getPmHistory(pm);
                writePmHistory(pmHist);
            }
        }

        for (Vm vm : cloud.getVms()) {
            VmHistory pmHist = getVmHistory(vm);
            writeVmHistory(pmHist);
        }

    }

    protected void writeCloudHistory(CloudHistory hist) {
        writer.saveCloudHistory(hist);
    }

    protected CloudHistory getCloudHistory(Cloud cloud) {
        CloudHistory hist = new CloudHistory();
        hist.setTimestamp(cloud.getClock().now());
        hist.setEnergyCosts(cloud.getEnergyCosts());
        hist.setSlaViolationCosts(cloud.getSlaPenaltyCosts());
        hist.setEnergyConsumption(cloud.getUtilizedPowerWithPue());
        hist.setDateTime(cloud.getClock().toUtc());
        hist.setViolationCount(cloud.getViolationCount());
        hist.setShortViolationCount(cloud.getShortViolationCount());
        hist.setVmMigrationCount(cloud.getVmMigrationCount());
        return hist;
    }

    protected void writeDcHistory(DatacenterHistory hist) {
        writer.saveDcHistory(hist);
    }

    protected DatacenterHistory getDcHistory(Datacenter dc) {
        long now = dc.getClock().now();
        double temp = dc.getTemperature().getTemperature(now);
        String zoneId = dc.getLocation().getTimeZone().getId();

        DatacenterHistory hist = new DatacenterHistory();
        hist.setId(dc.getId());
        hist.setTimestamp(now);
        hist.setDateTime(DateTimeUtils.getDateTime(now, zoneId));
        hist.setPowerOutageDuration(dc.getPowerOutageDuration());
        hist.setLocation(dc.getLocation().getDescription());
        hist.setTemperature(temp);
        hist.setCoolingMode(dc.getCoolingModel().getMode(temp));
        hist.setPpue(dc.getCoolingModel().getpPUE(temp));
        hist.setEnergyCosts(dc.getEnergyCosts());
        hist.setEnergyPrice(dc.getEnergyPrice().getPrice(now));
        hist.setEnergyUsageRate(dc.getUtilizedPowerWithPue());
        hist.setEnergyOverallUsage(dc.getEnergyOverallUsage());
        hist.setDayOrNight(DateTimeUtils.isDayOrNight(now, zoneId, dc.getLocation().getNightToDaySwitchHour(), dc.getLocation().getDayToNightSwitchHour()));
        hist.setPmsCount(dc.getPms().size());
        hist.setVmsCount(dc.getPms().parallelStream().mapToInt(pm -> pm.getVms().size()).sum());
        hist.setPmsSwitchedOnCount((int) dc.getPms().stream().filter(x -> x.getPmState() != PmState.PowerOff).count());
        return hist;
    }

    protected void writePmHistory(PmHistory hist) {
        writer.savePmHistory(hist);
    }

    protected PmHistory getPmHistory(Pm pm) {
        String zoneId = pm.getDatacenter().getLocation().getTimeZone().getId();
        long now = pm.getClock().now();

        PmHistory hist = new PmHistory();
        hist.setId(pm.getId());
        hist.setTimestamp(now);
        hist.setDateTime(DateTimeUtils.getDateTime(now, zoneId));
        hist.setState(pm.getPmState());
        hist.setDc(pm.getDatacenter().getId());
        hist.setEnergyConsumption(pm.getUtilizedPower());
        hist.setVmsCount(pm.getVms().size());
        hist.setMigratedVmsCount(pm.getMigratedVms().size());
        hist.setCpuSpecs(pm.getSpec().getMips());
        hist.setRamSpecs(pm.getSpec().getRam());
        hist.setBwSpecs(pm.getSpec().getBw());
        hist.setSizeSpecs(pm.getSpec().getSize());
        hist.setCpuReq(pm.getRequestedMips());
        hist.setRamReq(pm.getRequestedRam());
        hist.setBwReq(pm.getRequestedBw());
        hist.setSizeReq(pm.getRequestedSize());
        hist.setCpuProvisioned(pm.getConsumedMips());
        hist.setRamProvisioned(pm.getConsumedRam());
        hist.setBwProvisioned(pm.getConsumedBw());
        hist.setSizeProvisioned(pm.getConsumedSize());
        return hist;
    }

    protected void writeVmHistory(VmHistory hist) {
        writer.saveVmHistory(hist);
    }

    protected VmHistory getVmHistory(Vm vm) {
        long now = vm.getClock().now();
        VmHistory hist = new VmHistory();
        hist.setId(vm.getId());
        if (vm.getServer() != null) {
            hist.setPm(vm.getServer().getId());
            hist.setDc(vm.getServer().getDatacenter().getId());
            hist.setCpuProvisioned(vm.getProvisionedMips());
            hist.setRamProvisioned(vm.getProvisionedRam());
            hist.setBwProvisioned(vm.getProvisionedBw());
            hist.setSizeProvisioned(vm.getProvisionedSize());
        }
        if (vm.getMigratedToServer() != null) {
            hist.setPmMigrTo(vm.getMigratedToServer().getId());
            hist.setDcMigrTo(vm.getMigratedToServer().getDatacenter().getId());
        }
        hist.setTimestamp(now);
        hist.setState(vm.getVmState());
        hist.setShortDowntime(vm.isInShortDowntime());
        hist.setDowntime(vm.isInDowntime());
        hist.setCummViol(vm.getCummulativeDowntime());
        hist.setCummViolRate(((VmAvailabilityBasedSla) vm.getSla()).getCummViolationRate());
        hist.setOverallViol(vm.getDowntime());
        hist.setOverallViolRate(((VmAvailabilityBasedSla) vm.getSla()).getOverallViolationRate());
        hist.setCpuSpecs(vm.getSpec().getMips());
        hist.setRamSpecs(vm.getSpec().getRam());
        hist.setBwSpecs(vm.getSpec().getBw());
        hist.setSizeSpecs(vm.getSpec().getSize());
        hist.setCpuReq(vm.getRequestedMips());
        hist.setRamReq(vm.getRequestedRam(now));
        hist.setBwReq(vm.getRequestedBw(now));
        hist.setSizeReq(vm.getRequestedSize(now));
        hist.setDateTime(vm.getClock().toUtc());
        hist.setRunningTimeProvisioned(vm.getRunningTime());
        hist.setRunningTimeReq(vm.getRequestedRunningTime());

        return hist;
    }

    @Override
    public void shutdown() {
        writer.flush();
    }

}
