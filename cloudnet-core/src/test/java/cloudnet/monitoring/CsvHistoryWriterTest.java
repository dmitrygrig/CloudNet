/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cloudnet.monitoring;

import cloudnet.core.PmState;
import cloudnet.core.Size;
import cloudnet.core.VmState;
import cloudnet.monitoring.model.CloudHistory;
import cloudnet.monitoring.model.DatacenterHistory;
import cloudnet.monitoring.model.PmHistory;
import cloudnet.monitoring.model.VmHistory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class CsvHistoryWriterTest {

    public CsvHistoryWriterTest() {
    }

    private CsvHistoryWriterTestImpl getWriter() {
        return new CsvHistoryWriterTestImpl("cloud", "dcs", "pms", "vms");
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSaveCloudHistory() {
        System.out.println("saveCloudHistory");

        CloudHistory hist = new CloudHistory();
        hist.setTimestamp(1000L);
        hist.setEnergyCosts(1.0212);
        hist.setSlaViolationCosts(1.4512);
        hist.setEnergyConsumption(1.4536);
        hist.setDateTime("2012-01-01 10:00:00");
        hist.setShortViolationCount(10);
        hist.setViolationCount(5);
        hist.setVmMigrationCount(100);

        CsvHistoryWriterTestImpl instance = getWriter();
        instance.saveCloudHistory(hist);

        assertEquals("cloud", instance.getCalledFilename());
        assertEquals("1000,2012-01-01 10:00:00,1.4536,1.0212,1.4512,10,5,100", instance.getCalledData());
    }

    @Test
    public void testSaveDcHistory() {
        System.out.println("saveDcHistory");

        DatacenterHistory hist = new DatacenterHistory();
        hist.setId(1);
        hist.setTimestamp(1000L);
        hist.setDateTime("2014-01-01 01:00:00");
        hist.setLocation("Vienna");
        hist.setPowerOutageDuration(1000L);
        hist.setTemperature(14.72);
        hist.setCoolingMode("Air");
        hist.setPpue(1.0512);
        hist.setEnergyCosts(1.4512);
        hist.setEnergyPrice(1.2312);
        hist.setEnergyUsageRate(0.1212);
        hist.setEnergyOverallUsage(145.1212);
        hist.setDayOrNight(true);
        hist.setPmsCount(5);
        hist.setPmsSwitchedOnCount(2);
        hist.setVmsCount(10);

        CsvHistoryWriterTestImpl instance = getWriter();

        instance.saveDcHistory(hist);

        assertEquals("dcs", instance.getCalledFilename());
        assertEquals("1,1000,2014-01-01 01:00:00,Vienna,14.72,Air,1.05,1.4512,0.1212,145.1212,1.2312,true,5,2,1000,10", instance.getCalledData());
    }

    @Test
    public void testSavePmHistory() {
        System.out.println("savePmHistory");

        PmHistory hist = new PmHistory();
        hist.setDateTime("2012-01-01 10:00:00");
        hist.setId(1);
        hist.setTimestamp(1000L);
        hist.setState(PmState.Running);
        hist.setDc(1);
        hist.setEnergyConsumption(145.2312);
        hist.setVmsCount(5);
        hist.setMigratedVmsCount(2);
        hist.setCpuSpecs(1000L);
        hist.setRamSpecs(4000 * Size.MB);
        hist.setBwSpecs(100 * Size.MBit);
        hist.setSizeSpecs(100000 * Size.MB);
        hist.setCpuReq(800);
        hist.setRamReq(5000 * Size.MB);
        hist.setBwReq(20 * Size.MBit);
        hist.setSizeReq(250000 * Size.MB);
        hist.setCpuProvisioned(750);
        hist.setRamProvisioned(3000 * Size.MB);
        hist.setBwProvisioned(25 * Size.MBit);
        hist.setSizeProvisioned(200000 * Size.MB);

        CsvHistoryWriterTestImpl instance = getWriter();

        instance.savePmHistory(hist);

        assertEquals("pms", instance.getCalledFilename());
        assertEquals("1,1000,2012-01-01 10:00:00,1,Running,145.2312,5,2,1000,4000,100,100000,750,3000,25,200000,800,5000,20,250000", instance.getCalledData());
    }

    @Test
    public void testSaveVmHistory() {
        System.out.println("saveVmHistory");
        VmHistory hist = new VmHistory();
        hist.setDateTime("2012-01-01 10:00:00");
        hist.setId(1);
        hist.setPm(2);
        hist.setDc(3);
        hist.setPmMigrTo(3);
        hist.setDcMigrTo(1);
        hist.setTimestamp(1000L);
        hist.setState(VmState.Running);
        hist.setDowntime(true);
        hist.setShortDowntime(true);
        hist.setCummViol(30);
        hist.setCummViolRate(0.3121);
        hist.setOverallViol(400);
        hist.setOverallViolRate(0.5121);
        hist.setCpuSpecs(1000L);
        hist.setRamSpecs(4000 * Size.MB);
        hist.setBwSpecs(100 * Size.MBit);
        hist.setSizeSpecs(100000 * Size.MB);
        hist.setCpuReq(800);
        hist.setRamReq(5000 * Size.MB);
        hist.setBwReq(20 * Size.MBit);
        hist.setSizeReq(250000 * Size.MB);
        hist.setCpuProvisioned(750);
        hist.setRamProvisioned(3000 * Size.MB);
        hist.setBwProvisioned(25 * Size.MBit);
        hist.setSizeProvisioned(200000 * Size.MB);
        hist.setRunningTimeProvisioned(1000);
        hist.setRunningTimeReq(2000);

        CsvHistoryWriterTestImpl instance = getWriter();

        instance.saveVmHistory(hist);

        assertEquals("vms", instance.getCalledFilename());
        assertEquals("1,1000,2012-01-01 10:00:00,Running,2,3,3,1,true,true,30,0.3121,400,0.5121,1000,4000,100,100000,750,3000,25,200000,800,5000,20,250000,1000,2000", instance.getCalledData());
    }

    @Test
    public void testSaveVmHistory_notAllocated() {
        System.out.println("saveVmHistory_notAllocated");
        VmHistory hist = new VmHistory();
        hist.setId(1);
        hist.setDateTime("2012-01-01 10:00:00");
        hist.setPm(null);
        hist.setDc(null);
        hist.setPmMigrTo(null);
        hist.setDcMigrTo(null);
        hist.setTimestamp(1000L);
        hist.setState(VmState.Running);
        hist.setDowntime(true);
        hist.setShortDowntime(true);
        hist.setCummViol(30);
        hist.setCummViolRate(0.3121);
        hist.setOverallViol(400);
        hist.setOverallViolRate(0.5121);
        hist.setCpuSpecs(1000L);
        hist.setRamSpecs(4000 * Size.MB);
        hist.setBwSpecs(100 * Size.MBit);
        hist.setSizeSpecs(100000 * Size.MB);
        hist.setCpuReq(800);
        hist.setRamReq(5000 * Size.MB);
        hist.setBwReq(20 * Size.MBit);
        hist.setSizeReq(250000 * Size.MB);
        hist.setCpuProvisioned(750);
        hist.setRamProvisioned(3000 * Size.MB);
        hist.setBwProvisioned(25 * Size.MBit);
        hist.setSizeProvisioned(200000 * Size.MB);
        hist.setRunningTimeProvisioned(1000);
        hist.setRunningTimeReq(2000);

        CsvHistoryWriterTestImpl instance = getWriter();

        instance.saveVmHistory(hist);

        assertEquals("vms", instance.getCalledFilename());
        assertEquals("1,1000,2012-01-01 10:00:00,Running,NA,NA,NA,NA,true,true,30,0.3121,400,0.5121,1000,4000,100,100000,750,3000,25,200000,800,5000,20,250000,1000,2000", instance.getCalledData());
    }

}
