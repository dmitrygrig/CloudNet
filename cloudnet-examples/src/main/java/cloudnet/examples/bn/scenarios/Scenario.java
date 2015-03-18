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
package cloudnet.examples.bn.scenarios;

import cloudnet.core.TimeFrame;
import cloudnet.core.VmSpec;
import cloudnet.elasticity.DowntimeVmMigrationPolicy;
import cloudnet.elasticity.VmMigrationPolicy;
import cloudnet.util.DateTimeUtils;
import cloudnet.vm.azure.VmSpecAzureA0;
import cloudnet.workloads.ContinuouslyChangingWorkloadModel;
import cloudnet.workloads.OnceInAlifetimeWorkloadModel;
import cloudnet.workloads.PeriodicWorkloadModel;
import cloudnet.workloads.StaticWorkloadModel;
import cloudnet.workloads.UnpredictableWorkloadModel;
import cloudnet.workloads.WorkloadModel;
import cloudnet.workloads.prediction.WorkloadPredictionStrategy;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class Scenario {

    public static int SEED = 12;

    public static Scenario createDefaultScenario(String name) {
        Scenario sc = new Scenario();
        sc.setName(name);
        sc.setPredStrategy(null);
        sc.setMigrPolicy(new DowntimeVmMigrationPolicy());
        sc.setFrom(new GregorianCalendar(TimeZone.getTimeZone("UTC")));
        sc.getFrom().set(2013, Calendar.JANUARY, 1, 0, 0, 0);
        sc.setTill(new GregorianCalendar(TimeZone.getTimeZone("UTC")));
        sc.getTill().set(2013, Calendar.FEBRUARY, 1, 0, 0, 0);
        sc.setDcNum(1);
        sc.setVmNum(10);
        sc.setPmPerDcNum(3);
        sc.setSimStep(TimeFrame.Hour);
        sc.setVmSpec(new VmSpecAzureA0());
        sc.hasPowerOutage = false;
        sc.setThreadSafe(false);
        return sc;
    }

    public static Scenario createScenario1(String em, VmMigrationPolicy migrPolicy, WorkloadPredictionStrategy predStrategy) {
        Scenario sc = createDefaultScenario("sc1");
        sc.setEm(em);
        sc.setMigrPolicy(migrPolicy);
        sc.setPredStrategy(predStrategy);
        sc.setMigrPolicy(migrPolicy);
        sc.setDcNum(1);
        sc.setVmNum(10);
        sc.setPmPerDcNum(3);
        sc.hasPowerOutage = false;
        return sc;
    }

    public static Scenario createScenario2(String em, VmMigrationPolicy migrPolicy, WorkloadPredictionStrategy predStrategy) {
        Scenario sc = createDefaultScenario("sc2");
        sc.setEm(em);
        sc.setMigrPolicy(migrPolicy);
        sc.setPredStrategy(predStrategy);
        sc.setMigrPolicy(migrPolicy);
        sc.setDcNum(1);
        sc.setVmNum(10);
        sc.setPmPerDcNum(10);
        sc.hasPowerOutage = false;
        return sc;
    }

    public static Scenario createScenario3(String em, VmMigrationPolicy migrPolicy, WorkloadPredictionStrategy predStrategy) {
        Scenario sc = createDefaultScenario("sc3");
        sc.setEm(em);
        sc.setMigrPolicy(migrPolicy);
        sc.setPredStrategy(predStrategy);
        sc.setMigrPolicy(migrPolicy);
        sc.setDcNum(5);
        sc.setVmNum(10);
        sc.setPmPerDcNum(2);
        sc.hasPowerOutage = false;
        return sc;
    }

    public static Scenario createScenario4(String em, VmMigrationPolicy migrPolicy, WorkloadPredictionStrategy predStrategy) {
        Scenario sc = createDefaultScenario("sc4");
        sc.setEm(em);
        sc.setMigrPolicy(migrPolicy);
        sc.setPredStrategy(predStrategy);
        sc.setMigrPolicy(migrPolicy);
        sc.setDcNum(5);
        sc.setVmNum(10);
        sc.setPmPerDcNum(2);
        sc.setHasPowerOutage(true);
        sc.setInverseDatacenters(true);
        return sc;
    }

    public static Scenario createScenario5(String em, VmMigrationPolicy migrPolicy, WorkloadPredictionStrategy predStrategy) {
        Scenario sc = createDefaultScenario("sc5");
        sc.setEm(em);
        sc.setMigrPolicy(migrPolicy);
        sc.setPredStrategy(predStrategy);
        long startTime = sc.getFrom().getTimeInMillis() + TimeFrame.Hour * 12;
        sc.setCpuWorkloadModel(new OnceInAlifetimeWorkloadModel(0.1, 1.0, 12 * TimeFrame.Hour, startTime));
        sc.setPredStrategy(predStrategy);
        sc.setMigrPolicy(migrPolicy);
        sc.setDcNum(5);
        sc.setVmNum(10);
        sc.setPmPerDcNum(2);
        sc.setHasPowerOutage(false);
        return sc;
    }

    public static Scenario createScenario6(String em, VmMigrationPolicy migrPolicy, WorkloadPredictionStrategy predStrategy) {
        Scenario sc = createDefaultScenario("sc6");
        sc.setEm(em);
        sc.setMigrPolicy(migrPolicy);
        sc.setPredStrategy(predStrategy);
        sc.setCpuWorkloadModel(new UnpredictableWorkloadModel(TimeFrame.Minute * 30, SEED));
        sc.setPredStrategy(predStrategy);
        sc.setMigrPolicy(migrPolicy);
        sc.setDcNum(5);
        sc.setVmNum(10);
        sc.setPmPerDcNum(2);
        sc.hasPowerOutage = false;
        return sc;
    }

    private String name;
    private String em;
    private VmMigrationPolicy migrPolicy;
    private WorkloadPredictionStrategy predStrategy;
    private Calendar from;
    private Calendar till;
    private int dcNum;
    private int vmNum;
    private int pmPerDcNum;
    private long simStep;
    private VmSpec vmSpec;
    private boolean hasPowerOutage;
    private WorkloadModel cpuWorkloadModel = new PeriodicWorkloadModel(.1, .8, .05, 24 * TimeFrame.Hour, SEED);
    private WorkloadModel bwWorkloadModel = new StaticWorkloadModel(.5, .05, SEED);
    private WorkloadModel ramWorkloadModel = new PeriodicWorkloadModel(.1, .6, .05, 24 * TimeFrame.Hour, SEED);
    private WorkloadModel sizeWorkloadModel = new ContinuouslyChangingWorkloadModel(0.2, 0.8, 0.01, TimeFrame.Day, 0);
    private boolean threadSafe;
    private boolean inverseDatacenters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isThreadSafe() {
        return threadSafe;
    }

    public void setThreadSafe(boolean threadSafe) {
        this.threadSafe = threadSafe;
    }

    public WorkloadPredictionStrategy getPredStrategy() {
        return predStrategy;
    }

    public void setPredStrategy(WorkloadPredictionStrategy predStrategy) {
        this.predStrategy = predStrategy;
    }

    public VmMigrationPolicy getMigrPolicy() {
        return migrPolicy;
    }

    public void setMigrPolicy(VmMigrationPolicy migrPolicy) {
        this.migrPolicy = migrPolicy;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public Calendar getFrom() {
        return from;
    }

    public void setFrom(Calendar from) {
        this.from = from;
    }

    public Calendar getTill() {
        return till;
    }

    public void setTill(Calendar till) {
        this.till = till;
    }

    public int getDcNum() {
        return dcNum;
    }

    public void setDcNum(int dcNum) {
        this.dcNum = dcNum;
    }

    public int getVmNum() {
        return vmNum;
    }

    public void setVmNum(int vmNum) {
        this.vmNum = vmNum;
    }

    public int getPmPerDcNum() {
        return pmPerDcNum;
    }

    public void setPmPerDcNum(int pmPerDcNum) {
        this.pmPerDcNum = pmPerDcNum;
    }

    public long getSimStep() {
        return simStep;
    }

    public void setSimStep(long simStep) {
        this.simStep = simStep;
    }

    public VmSpec getVmSpec() {
        return vmSpec;
    }

    public void setVmSpec(VmSpec vmSpec) {
        this.vmSpec = vmSpec;
    }

    public boolean isHasPowerOutage() {
        return hasPowerOutage;
    }

    public void setHasPowerOutage(boolean hasPowerOutage) {
        this.hasPowerOutage = hasPowerOutage;
    }

    public WorkloadModel getCpuWorkloadModel() {
        return cpuWorkloadModel;
    }

    public void setCpuWorkloadModel(WorkloadModel cpuWorkloadModel) {
        this.cpuWorkloadModel = cpuWorkloadModel;
    }

    public WorkloadModel getBwWorkloadModel() {
        return bwWorkloadModel;
    }

    public void setBwWorkloadModel(WorkloadModel bwWorkloadModel) {
        this.bwWorkloadModel = bwWorkloadModel;
    }

    public WorkloadModel getRamWorkloadModel() {
        return ramWorkloadModel;
    }

    public void setRamWorkloadModel(WorkloadModel ramWorkloadModel) {
        this.ramWorkloadModel = ramWorkloadModel;
    }

    public WorkloadModel getSizeWorkloadModel() {
        return sizeWorkloadModel;
    }

    public void setSizeWorkloadModel(WorkloadModel sizeWorkloadModel) {
        this.sizeWorkloadModel = sizeWorkloadModel;
    }

    public boolean isInverseDatacenters() {
        return inverseDatacenters;
    }

    public void setInverseDatacenters(boolean inverseDatacenters) {
        this.inverseDatacenters = inverseDatacenters;
    }

    @Override
    public String toString() {
        return "em=" + toStringOrDefault(em) + "\r\n"
                + "migrPolicy=" + toStringOrDefault(migrPolicy) + "\r\n"
                + "predStrategy=" + toStringOrDefault(predStrategy) + "\r\n"
                + "from=" + calendarToStringOrDefault(from) + "\r\n"
                + "till=" + calendarToStringOrDefault(till) + "\r\n"
                + "dcNum=" + toStringOrDefault(dcNum) + "\r\n"
                + "vmNum=" + toStringOrDefault(vmNum) + "\r\n"
                + "pmPerDcNum=" + toStringOrDefault(pmPerDcNum) + "\r\n"
                + "simStep=" + toStringOrDefault(simStep) + "\r\n"
                + "vmSpec=" + toStringOrDefault(vmSpec) + "\r\n"
                + "hasPowerOutage=" + toStringOrDefault(hasPowerOutage) + "\r\n"
                + "cpuWorkloadModel=" + toStringOrDefault(cpuWorkloadModel) + "\r\n"
                + "bwWorkloadModel=" + toStringOrDefault(bwWorkloadModel) + "\r\n"
                + "ramWorkloadModel=" + toStringOrDefault(ramWorkloadModel) + "\r\n"
                + "sizeWorkloadModel=" + toStringOrDefault(sizeWorkloadModel);
    }

    private String toStringOrDefault(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    private String calendarToStringOrDefault(Calendar cal) {
        if (cal == null) {
            return "null";
        } else {
            return DateTimeUtils.getDateTime(cal.getTimeInMillis(), TimeZone.getDefault().getID());
        }
    }

}
