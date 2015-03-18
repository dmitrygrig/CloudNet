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

import cloudnet.core.Size;
import cloudnet.monitoring.model.PmHistory;
import cloudnet.monitoring.model.VmHistory;
import cloudnet.monitoring.model.DatacenterHistory;
import cloudnet.monitoring.model.CloudHistory;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class CsvHistoryWriter implements HistoryWriter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvHistoryWriter.class);

    private static final String EOS = "\r\n";
    private static final String Missing = "NA";
    private final String cloudHistoryFilename;
    private final String dcHistoryFilename;
    private final String pmHistoryFilename;
    private final String vmHistoryFilename;
    private static final String CloudHeader = "Timestamp,DateTime,EnergyConsumption,EnergyCosts,SlaViolationCosts,ShortViolations,Violations,Migrations";
    private static final String VmHeader = "Id,TimeStamp,DateTime,State,Pm,Dc,PmMigrTo,DcMigrTo,ShortDowntime,Downtime,"
            + "CummViol,CummViolRate,OverallViol,OverallViolRate,"
            + "CpuSpecs,RamSpecs,BwSpecs,SizeSpecs,"
            + "CpuProv,RamProv,BwProv,SizeProv,"
            + "CpuReq,RamReq,BwReq,SizeReq,"
            + "RuntimeProv,RuntimeReq";
    private static final String PmHeader = "Id,TimeStamp,DateTime,Dc,State,EnergyConsumption,VmsCount,MigratedVmsCount,CpuSpecs,RamSpecs,BwSpecs,SizeSpecs,CpuProv,RamProv,BwProv,SizeProv,CpuReq,RamReq,BwReq,SizeReq";
    private static final String DcHeader = "Id,TimeStamp,DateTime,Location,Temperature,CoolingMode,PPue,EnergyCosts,"
            + "EnergyUsageRate,EnergyOverallUsage,EnergyPrice,DayOrNight,"
            + "PmsCount,PmsSwitchedOnCount,PowerOutageDuration,VmsCount";
    private final List<String> cloudData = new ArrayList<>();
    private final List<String> dcData = new ArrayList<>();
    private final List<String> pmData = new ArrayList<>();
    private final List<String> vmData = new ArrayList<>();

    private int batchOffset = 1000;

    /**
     * Creates new instance of CsvHistoryWriter using specified filenames.
     *
     * @param cloudHistoryFilename
     * @param dcHistoryFilename
     * @param pmHistoryFilename
     * @param vmHistoryFilename
     * @param batchOffset
     * @param append If false, deletes all specified output files on start.
     */
    public CsvHistoryWriter(String cloudHistoryFilename, String dcHistoryFilename, String pmHistoryFilename, String vmHistoryFilename, int batchOffset, boolean append) {
        this.cloudHistoryFilename = cloudHistoryFilename;
        this.dcHistoryFilename = dcHistoryFilename;
        this.pmHistoryFilename = pmHistoryFilename;
        this.vmHistoryFilename = vmHistoryFilename;
        this.batchOffset = batchOffset;

        if (!append) {
            deleteFileIfExists(cloudHistoryFilename);
            deleteFileIfExists(dcHistoryFilename);
            deleteFileIfExists(pmHistoryFilename);
            deleteFileIfExists(vmHistoryFilename);
        }
    }

    public int getBatchOffset() {
        return batchOffset;
    }

    public void setBatchOffset(int batchOffset) {
        this.batchOffset = batchOffset;
    }

    private void deleteFileIfExists(final String filename) {
        File f = new File(filename);
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public void saveCloudHistory(CloudHistory history) {
        // http://stackoverflow.com/questions/5236056/force-decimal-point-as-seperator-in-java
        String str = String.format(new Locale("en", "US"), "%d,%s,%.4f,%.4f,%.4f,%d,%d,%d",
                history.getTimestamp(),
                history.getDateTime(),
                history.getEnergyConsumption(),
                history.getEnergyCosts(),
                history.getSlaViolationCosts(),
                history.getShortViolationCount(),
                history.getViolationCount(),
                history.getVmMigrationCount());
        cloudData.add(str);
        flushData(cloudHistoryFilename, CloudHeader, cloudData);
    }

    @Override
    public void saveDcHistory(DatacenterHistory history) {
        String str = String.format(new Locale("en", "US"), "%d,%d,%s,%s,%.2f,%s,%.2f,%.4f,%.4f,%.4f,%.4f,%b,%d,%d,%d,%d",
                history.getId(),
                history.getTimestamp(),
                history.getDateTime(),
                history.getLocation(),
                history.getTemperature(),
                history.getCoolingMode(),
                history.getPpue(),
                history.getEnergyCosts(),
                history.getEnergyUsageRate(),
                history.getEnergyOverallUsage(),
                history.getEnergyPrice(),
                history.isDayOrNight(),
                history.getPmsCount(),
                history.getPmsSwitchedOnCount(),
                history.getPowerOutageDuration(),
                history.getVmsCount()
        );
        dcData.add(str);
        flushData(dcHistoryFilename, DcHeader, dcData);
    }

    @Override
    public void savePmHistory(PmHistory history) {
        String str = String.format(new Locale("en", "US"), "%d,%d,%s,%d,%s,%.4f,%d,%d,%d,%s,%s,%s,%d,%s,%s,%s,%d,%s,%s,%s",
                history.getId(),
                history.getTimestamp(),
                history.getDateTime(),
                history.getDc(),
                history.getState(),
                history.getEnergyConsumption(),
                history.getVmsCount(),
                history.getMigratedVmsCount(),
                history.getCpuSpecs(),
                Size.toMBRawString(history.getRamSpecs(), 0),
                Size.toMBitPerSecRawString(history.getBwSpecs(), 0),
                Size.toMBRawString(history.getSizeSpecs(), 0),
                history.getCpuProvisioned(),
                Size.toMBRawString(history.getRamProvisioned(), 0),
                Size.toMBitPerSecRawString(history.getBwProvisioned(), 0),
                Size.toMBRawString(history.getSizeProvisioned(), 0),
                history.getCpuReq(),
                Size.toMBRawString(history.getRamReq(), 0),
                Size.toMBitPerSecRawString(history.getBwReq(), 0),
                Size.toMBRawString(history.getSizeReq(), 0)
        );
        pmData.add(str);
        flushData(pmHistoryFilename, PmHeader, pmData);
    }

    @Override
    public void saveVmHistory(VmHistory history) {
        String str = String.format(new Locale("en", "US"), "%d,%d,%s,%s,%s,%s,%s,%s,%b,%b,%d,%.4f,%d,%.4f,%d,%s,%s,%s,%d,%s,%s,%s,%d,%s,%s,%s,%d,%d",
                history.getId(),
                history.getTimestamp(),
                history.getDateTime(),
                history.getState(),
                history.getPm() == null ? Missing : history.getPm(),
                history.getDc() == null ? Missing : history.getDc(),
                history.getPmMigrTo() == null ? Missing : history.getPmMigrTo(),
                history.getDcMigrTo() == null ? Missing : history.getDcMigrTo(),
                history.isShortDowntime(),
                history.isDowntime(),
                history.getCummViol(),
                history.getCummViolRate(),
                history.getOverallViol(),
                history.getOverallViolRate(),
                history.getCpuSpecs(),
                Size.toMBRawString(history.getRamSpecs(), 0),
                Size.toMBitPerSecRawString(history.getBwSpecs(), 0),
                Size.toMBRawString(history.getSizeSpecs(), 0),
                history.getCpuProvisioned(),
                Size.toMBRawString(history.getRamProvisioned(), 0),
                Size.toMBitPerSecRawString(history.getBwProvisioned(), 0),
                Size.toMBRawString(history.getSizeProvisioned(), 0),
                history.getCpuReq(),
                Size.toMBRawString(history.getRamReq(), 0),
                Size.toMBitPerSecRawString(history.getBwReq(), 0),
                Size.toMBRawString(history.getSizeReq(), 0),
                history.getRunningTimeProvisioned(),
                history.getRunningTimeReq()
        );
        vmData.add(str);
        flushData(vmHistoryFilename, VmHeader, vmData);
    }

    @Override
    public void flush() {
        flushData(cloudHistoryFilename, CloudHeader, cloudData, true);
        flushData(dcHistoryFilename, DcHeader, dcData, true);
        flushData(pmHistoryFilename, PmHeader, pmData, true);
        flushData(vmHistoryFilename, VmHeader, vmData, true);
    }

    private void flushData(String filename, String header, List<String> data) {
        flushData(filename, header, data, false);
    }

    private synchronized void flushData(String filename, String header, List<String> data, boolean force) {
        if (force || data.size() >= batchOffset) {
            writeHistoryIntoCsvFile(filename, header, data);
            data.clear();
        }
    }

    protected void writeHistoryIntoCsvFile(String filename, String header, List<String> data) {

        createFileIfNotExists(filename, header);

        try (FileWriter writer = new FileWriter(filename, true)) {
            for (String dataItem : data) {
                writer.append(dataItem + EOS);
            }
        } catch (Exception e) {
            LOGGER.error("writeHistory", e);
        }
    }

    protected synchronized void createFileIfNotExists(String filename, String header) {
        File file = new File(filename);
        boolean exists = file.exists();
        if (!exists) {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(filename, true)) {
                writer.append(header + EOS);
            } catch (Exception e) {
                LOGGER.error("writeHistory", e);
            }
        }
    }

}
