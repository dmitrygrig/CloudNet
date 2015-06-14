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
package cloudnet.examples.elasticity.bn;

import cloudnet.core.*;
import cloudnet.elasticity.ElasticityManager;
import cloudnet.elasticity.VmMigrationPolicy;
import cloudnet.messaging.PmStartMessage;
import cloudnet.messaging.PmStopMessage;
import cloudnet.messaging.VmAllocationMessage;
import cloudnet.messaging.VmMigrationMessage;
import cloudnet.messaging.VmSchedulingMessage;
import cloudnet.sim.SimClock;
import cloudnet.sla.VmAvailabilityBasedSla;
import cloudnet.util.Ensure;
import cloudnet.workloads.prediction.WorkloadPredictionStrategy;
import java.io.IOException;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import jbayes.core.BayesNet;
import jbayes.core.Node;
import jbayes.inference.IBayesInferer;
import jbayes.r.R;
import jbayes.r.inference.RAsyncBayesInferer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Elasticity manager loops over all vms allocated on the pms and decides
 * whether it is necessary to migrate VM to another pm.
 * <p/>
 * It uses MCDA. There are the following criterias: 1.	migration time a1 2.
 * power pm usage difference between target pm and pm where vm is allocated
 * right now a2 3.	sla violation of current machine a3 (true, false) 4.	possible
 * violations of other vms (if the pm gets overall overusage) a4
 * <p/>
 * Each variable has a weight: 1.	a1 - smth 2.	a2 - dP * (1 + cool_coef) *
 * energyPrice 3.	a3 - penalty price 4.	a4 - smth
 * <p/>
 * Input parameters are: 1.	Current VM CPU workload 2.	Current VM RAM workload
 * 3.	Current Target PM CPU workload 4.	Current CPU workload of VMs on the
 * target PM 5.	Country of DC location 6.	Is Target PM is placed in the same DC
 * as VM 7.	Current VM overal violation rate 8.	Current VM cummulative violation
 * rate (diff between current time and last allocation date)
 * <p/>
 * Algorithm complexity: Considering that we loop over N VNs and try to place on
 * each iteration on N PMs, upper bound of complexity is O(N^2).
 * <p/>
 * Internal ticket: BCN-55.
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class ElasiticityManagerMCDA implements ElasticityManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasiticityManagerMCDA.class);
    
    private final Map<Integer, Long> consumedMipsPerPm = new HashMap<>();
//    private final Map<Integer, Long> consumedRamPerPm = new HashMap<>();
//    private final Map<Integer, Long> consumedSizePerPm = new HashMap<>();
//    private final Map<Integer, Long> consumedBwPerPm = new HashMap<>();
    private final Map<Integer, Long> nextVmCpu = new HashMap<>();
    private final Map<Integer, Long> nextVmRam = new HashMap<>();
    private final List<Integer> usedPms = new ArrayList<>();
    private final Map<Integer, Integer> allocatedVmsPerPm = new HashMap<>();
    private R r = null; // r engine
    private boolean initialized = false;
    private final WorkloadPredictionStrategy predStrategy;
    private final SimClock clock;
    private final VmMigrationPolicy migrationPolicy;

    public ElasiticityManagerMCDA(SimClock clock, WorkloadPredictionStrategy predStrategy, VmMigrationPolicy migrationPolicy) {
        this.predStrategy = predStrategy;
        this.clock = clock;
        this.migrationPolicy = migrationPolicy;
    }

    private void initRIfNecessary() {
        if (r == null) {
            r = R.getInstance();
        }
    }

    public R getR() {
        return r;
    }

    public void setR(R r) {
        this.r = r;
    }

    private void initNetworkIfNecessary() {
        if (!initialized) {
            initRIfNecessary();
//            getNetwork().init();
            initialized = true;
        }
    }

    @Override
    public void manage(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        consumedMipsPerPm.clear();
        allocatedVmsPerPm.clear();
//        consumedBwPerPm.clear();
//        consumedRamPerPm.clear();
//        consumedSizePerPm.clear();
        nextVmCpu.clear();
        nextVmRam.clear();
        usedPms.clear();

        initNetworkIfNecessary();

        // firstly allocate all vms
        manageAllocations(cloud);

        // and then migrate already allocated
        manageMigrations(cloud);

        // after all swith off all unused pcs
        managePmSwitchOff(cloud);

    }

    private void managePmSwitchOff(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        cloud.getDatacenters().forEach(dc -> {
            dc.getPms().parallelStream().filter(x -> x.getPmState() != PmState.PowerOff).forEach(pm -> {
                // if pm has no vms, migrated vms and was not managed to start, switch it off
                if (pm.getVms().isEmpty() && pm.getMigratedVms().isEmpty() && !usedPms.contains(pm.getId())) {
                    cloud.getMessageBus().push(new PmStopMessage(pm));
                }
            });
        });
    }

    private void manageAllocations(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        List<VmSchedulingMessage> vmAllocMessages = cloud.getMessageBus().getMessagesByType(VmSchedulingMessage.class);
        List<Vm> vms = vmAllocMessages.stream().map(x -> x.getVm())
                .sorted((vm1, vm2) -> vm1.getSla().getLevel().compareTo(vm2.getSla().getLevel()))
                .collect(Collectors.toList());
        for (Vm vm : vms) {
            Pm targetPm = findTargetPmForAllocation(cloud, vm);
            if (targetPm != null) {
                runPmAndNotifyIfNecessary(cloud, targetPm);
                cloud.getMessageBus().push(new VmAllocationMessage(vm, targetPm));
                updatedAvailablePmResources(targetPm, vm, true);
                LOGGER.info("%s was decided to allocate to %s.", vm.toShortString(), targetPm.toShortString());
            } else {
                LOGGER.warn("%s was not allocated to any pm.", vm.toShortString());
            }
        }

    }

    private Pm findTargetPmForAllocation(Cloud cloud, Vm vm) {
        Ensure.NotNull(cloud, "cloud");
        Ensure.NotNull(vm, "vm");
        Ensure.IsNull(vm.getServer(), "Vm is already allocated.");

        LOGGER.debug("Find target pm for allocation of %s...", vm.toString());

        // submit parallel calculations of the most probable BN levels
        Map<Pm, Future<List<Integer>>> futureMap = new HashMap<>();
        cloud.getDatacenters()
                .forEach(dc -> dc.getPms()
                        .forEach(pm -> futureMap.put(pm, setEvidences(pm, vm))));

        // reduce results
        return reduceMostValuablePm(vm, futureMap);
    }

    private boolean shouldVmBeMigrated(Cloud cloud, Vm vm) {
        final Pm targetPm = findTargetPmForMigration(cloud, vm);
        if (vm.getServer() != targetPm) {
            LOGGER.trace("%s should be migrated from %s perhaps to %s.",
                    vm.toShortString(), vm.getServer().toShortString(), targetPm.toShortString());
            return true;
        } else {
            return false;
        }
    }

    private void manageMigrations(Cloud cloud) {
        Ensure.NotNull(cloud, "cloud");

        List<Vm> vmList = new ArrayList<>();
        for (Datacenter dc : cloud.getDatacenters()) {
            for (Pm pm : dc.getPms()) {
                LOGGER.trace("Find list of vms that should be migrated from %s...", pm.toShortString());
                for (Vm vm : pm.getVms()) {
                    if (migrationPolicy.shouldBeMigrated(vm) && shouldVmBeMigrated(cloud, vm)) {
                        vmList.add(vm);
                        vm.setShouldBeMigrated(true);
                    }
                }
            }
        }

        Collections.sort(vmList, new VmSlaComparator());

        for (Vm vm : vmList) {
            Pm targetPm = findTargetPmForMigration(cloud, vm);
            if (targetPm != null && targetPm != vm.getServer()) {
                runPmAndNotifyIfNecessary(cloud, targetPm);
                cloud.getMessageBus().push(new VmMigrationMessage(vm, targetPm));
                updatedAvailablePmResources(targetPm, vm, true);
                updatedAvailablePmResources(vm.getServer(), vm, false);
                LOGGER.info("%s was decided to migrate to %s.", vm.toShortString(), targetPm.toShortString());
            } else {
                runPmAndNotifyIfNecessary(cloud, vm.getServer());
            }
            vm.setShouldBeMigrated(false);
        }
    }

    private Pm findTargetPmForMigration(Cloud cloud, Vm vm) {
        Ensure.NotNull(cloud, "cloud");
        Ensure.NotNull(vm.getServer(), "vm.Server");
        Ensure.IsFalse(vm.isInMigration(), "During live migration vm cannot be migrated to another pm.");

        LOGGER.debug("Find target pm for migration of %s allocated to %s...", vm.toString(), vm.getServer().toShortString());

        // map parallel calculations of the most probable BN levels
        Map<Pm, Future<List<Integer>>> futureMap = new HashMap<>();
        Pm currentPm = vm.getServer();
        futureMap.put(currentPm, setEvidences(currentPm, vm));
        cloud.getDatacenters()
                .forEach(dc
                        -> dc.getPms()
                        .stream()
                        .filter(pm -> pm != currentPm).forEach(pm
                                -> futureMap.put(pm, setEvidences(pm, vm))
                        ));

        // reduce results
        return reduceMostValuablePm(vm, futureMap);
    }

    private Pm reduceMostValuablePm(Vm vm, Map<Pm, Future<List<Integer>>> futureMap) {
        final Pm currentPm = vm.getServer();
        // perform map reduce on the previously obtained results
        PmUtilityResult pmUtilityResult = futureMap.entrySet()
                .parallelStream()
                .map(entry -> {
                    try {
                        return new PmUtilityResult(entry.getKey(), computeUtilityForTargetPm(entry.getKey(), entry.getValue().get()));
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.error("Map for vm allocation", e);
                        throw new RuntimeException(e);
                    }
                })
                .reduce((max, next) -> {
                    if (max == null && next == null){
                        return null;
                    } else if (next == null || next.getUtility() == null){
                        return max;
                    } else if (max == null || max.getUtility() == null){
                        return next;
                    }

                    double maxUtil = max.getUtility();
                    double nextUtil = next.getUtility();

                    // decide what Pm to use regarding current Pm of Vm. More priority for current Pm
                    if (nextUtil == maxUtil) {
                        if (next.getPm() == currentPm) {
                            return next;
                        } else {
                            return max;
                        }
                    } else if (nextUtil > maxUtil) {
                        return next;
                    } else {
                        return max;
                    }
                }).orElse(null);
        Pm targetPm = pmUtilityResult == null || pmUtilityResult.getUtility() == null ? null : pmUtilityResult.getPm();
        return targetPm;
    }

    private Future<List<Integer>> setEvidences(Pm pm, Vm vm) {
        Ensure.NotNull(pm, "pm");
        Ensure.NotNull(vm, "vm");

        LOGGER.trace("-------------------------------------------");
        LOGGER.trace("set evidences for %s and %s...", pm.toShortString(), vm.toShortString());

        // build network
        BayesNet network = getNetwork();
        network.clearEvidences();

        // set evidences
        if (pm == vm.getServer()) {
            network.setEvidence(NodeName.MigrationTime, WorkloadLevel.getLevel(0.0));
        }

        final long vmNextCpu = predictNextCpuConsumption(vm);
        final double vmNextCpuWorkload = (double) vmNextCpu / pm.getSpec().getMips();
        final String vmNextCpuLevel = WorkloadLevel.getLevel(vmNextCpuWorkload);
        network.setEvidence(NodeName.VmNextCpu, vmNextCpuLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("vmNextCpu=%d,vmNextCpuWorkload=%.2f,vmNextCpuLevel=%s", vmNextCpu, vmNextCpuWorkload, vmNextCpuLevel));

//        final long sumOfOtherVmNextCpu = pm.getVms().stream().
//                filter(x -> x.getId() != vm.getId() && x.getSla().getLevel().moreImportantOrEqualsTo(vm.getSla().getLevel())).
//                mapToLong(this::predictNextCpuConsumption).sum();
        long sumOfOtherVmNextCpu = pm.getVms().stream().filter(x-> !x.shouldBeMigrated()).
                mapToLong(this::predictNextCpuConsumption).sum() + consumedMipsPerPm.getOrDefault(pm.getId(), 0L);
        long sumOfMigratedVmNextCpu = pm.getMigratedVms().stream().mapToLong(this::predictNextCpuConsumption).sum();
        sumOfOtherVmNextCpu += sumOfMigratedVmNextCpu;
        if (!vm.shouldBeMigrated() && pm == vm.getServer()) {
            sumOfOtherVmNextCpu -= predictNextCpuConsumption(vm);
        }
        final double sumOfOtherVmNextCpuWorkload = FastMath.min(1.0, (double) sumOfOtherVmNextCpu / pm.getSpec().getMips());
        final String sumOfOtherVmNextCpuLevel = WorkloadLevel.getLevel(sumOfOtherVmNextCpuWorkload);
        network.setEvidence(NodeName.OtherVmNextCpu, sumOfOtherVmNextCpuLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("sumOfOtherVmNextCpu=%d,sumOfOtherVmNextCpuWorkload=%.2f,sumOfOtherVmNextCpuLevel=%s",
                sumOfOtherVmNextCpu, sumOfOtherVmNextCpuWorkload, sumOfOtherVmNextCpuLevel));

        long overallPmCpu = pm.getVms().stream().filter(x-> !x.shouldBeMigrated()).
                mapToLong(this::predictNextCpuConsumption).sum() + consumedMipsPerPm.getOrDefault(pm.getId(), 0L);
        overallPmCpu += sumOfMigratedVmNextCpu;
        if (!vm.shouldBeMigrated() && pm == vm.getServer()) {
            overallPmCpu -= predictNextCpuConsumption(vm);
        }
        // if overusage will occur, pm won't provide resources for all pms
        final double overallPmCpuWorkload = FastMath.min(1.0, (double) overallPmCpu / pm.getSpec().getMips());
        final String overallPmCpuLevel = WorkloadLevel.getLevel(overallPmCpuWorkload);
        network.setEvidence(NodeName.PmOverallCpu, overallPmCpuLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("overallPmCpu=%d,overallPmCpuWorkload=%.2f,overallPmCpuLevel=%s",
                overallPmCpu, overallPmCpuWorkload, overallPmCpuLevel));

        final long vmNextRamWorkload = predictNextRamConsumption(vm);
        final double vmNextRamWorkloadPercent = (double) vmNextRamWorkload / vm.getSpec().getRam(); // percent from own specs
        final String vmNextRamLevel = WorkloadLevel.getLevel(vmNextRamWorkloadPercent);
        network.setEvidence(NodeName.VmNextRam, vmNextRamLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("vmNextRamWorkload=%d,vmNextRamWorkloadPercent=%.2f,vmNextRamLevel=%s",
                vmNextRamWorkload, vmNextRamWorkloadPercent, vmNextRamLevel));

        final long vmSpecRam = vm.getSpec().getRam();
        final String vmNextRamSizeLevel = VmSizeLevel.getLevel(vmSpecRam);
        network.setEvidence(NodeName.VmRamSize, vmNextRamSizeLevel);
        LOGGER.trace("vmSpecRam=%d,vmNextRamSizeLevel=%s", vmSpecRam, vmNextRamSizeLevel);

        network.setEvidence(NodeName.DatacenterBandwidth, BwLevel.Infini);

        final boolean isInDc = vm.getServer() != null && vm.getServer().getDatacenter().equals(pm.getDatacenter());
        final String isInDcLevel = BooleanLevel.getLevel(isInDc);
        network.setEvidence(NodeName.PmInDc, isInDcLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("isInDc=%b,isInDcLevel=%s", isInDc, isInDcLevel));

        final String dcCountry = pm.getDatacenter().getLocation().getCountry();
        final String dcCountryLevel = pm.getDatacenter().getLocation().getCountry();
        network.setEvidence(NodeName.Country, dcCountryLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("dcCountry=%s,dcCountryLevel=%s", dcCountry, dcCountryLevel));

        final double overallViolationRate = ((VmAvailabilityBasedSla) vm.getSla()).getOverallViolationRate();
        final String overallViolationRateLevel = WorkloadLevel.getLevel(overallViolationRate);
        network.setEvidence(NodeName.VmSlaViolationRate, overallViolationRateLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("overallViolationRate=%.2f,overallViolationRateLevel=%s", overallViolationRate, overallViolationRateLevel));

        final double cummViolationRate = ((VmAvailabilityBasedSla) vm.getSla()).getCummViolationRate();
        final String cummViolationRateLevel = WorkloadLevel.getLevel(cummViolationRate);
        network.setEvidence(NodeName.VmCummSlaViolationRate, cummViolationRateLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("cummViolationRate=%.2f,cummViolationRateLevel=%s", cummViolationRate, cummViolationRateLevel));

        try (RAsyncBayesInferer inferer = new RAsyncBayesInferer(getR(), network)){
            Future<List<Integer>> future = inferer.inferMostProbableLevelsAsync(
                NodeName.VmCummAdjSlaViolationRate,
                NodeName.MigrationTime,
                NodeName.PmNextOverallCpu,
                NodeName.HasOverallOverusage,
                NodeName.VmAdjSlaViolationRate);

            return future;
        } catch (IOException e){
            LOGGER.error("inferMostProbableLevelsAsync / " + e);
            throw new RuntimeException(e);
        }
    }

    private Double computeUtilityForTargetPm(Pm pm, List<Integer> mostProbableLevels) {

        BayesNet network = getNetwork();

        LOGGER.trace("-------------------------------------------");
        LOGGER.trace("Compute utility for Pm %d...", pm.getId());

        // compute slaCummViolation utility
        String slaCummViolationLevel = network.getNodeByName(NodeName.VmCummAdjSlaViolationRate).getLevels().get(mostProbableLevels.get(0));
        double slaCummViolationUtility = UtilityHelper.getDescBenefitWorkloadUtility(slaCummViolationLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("slaCummViolationLevel=%s,slaCummViolationUtility=%.2f", slaCummViolationLevel, slaCummViolationUtility));

        // compute slaOverallViolation utility
        String slaViolationLevel = network.getNodeByName(NodeName.VmAdjSlaViolationRate).getLevels().get(mostProbableLevels.get(4));
        double slaViolationUtility = UtilityHelper.getDescBenefitWorkloadUtility(slaViolationLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("slaViolationLevel=%s,slaViolationUtility=%.2f", slaViolationLevel, slaViolationUtility));

        // compute migration utility
        String migrTimeLevel = network.getNodeByName(NodeName.MigrationTime).getLevels().get(mostProbableLevels.get(1));
        double migrTimeUtility = UtilityHelper.getDescBenefitWorkloadUtility(migrTimeLevel);
        LOGGER.trace(pm.toShortString() + ":" + String.format("migrTimeLevel=%s,migrTimeUtility=%.2f", migrTimeLevel, migrTimeUtility));

        // compute pm cpu workload utility, higher is better, overusage - reject
        String pmNextCpuLevel = network.getNodeByName(NodeName.PmNextOverallCpu).getLevels().get(mostProbableLevels.get(2));
        double pmCpuWorkloadUtility = UtilityHelper.getCombinedBenefitWorkloadUtility(pmNextCpuLevel, WorkloadLevel.W80);
        LOGGER.trace(pm.toShortString() + ":" + String.format("pmNextCpuLevel=%s,pmCpuWorkloadUtility=%.2f", pmNextCpuLevel, pmCpuWorkloadUtility));

        if (pmCpuWorkloadUtility == 0.0) {
            LOGGER.trace(pm.toShortString() + ": Due to inappropriate pmCpuWorkloadUtility pm is rejected.");
            return null;
        }

        // compute energy price utility, lower is better
        double price = pm.getDatacenter().getEnergyPrice().getPrice(clock.now());
        double priceUtility = UtilityHelper.getEnergyPriceUtility(price);
        LOGGER.trace(pm.toShortString() + ":" + String.format("energyPrice=%.2f,priceUtility=%.2f", price, priceUtility));

        // compute pm power consumption utility, lower is better
        long overallPmCpu = pm.getVms().stream().
                mapToLong(this::predictNextCpuConsumption).sum() + consumedMipsPerPm.getOrDefault(pm.getId(), 0L);
        double currPlannedCpuUtilization = FastMath.min(1.0, overallPmCpu / (double) pm.getSpec().getMips());
        double pmCurrPowerUtility = getPmPowerUtility(pm, currPlannedCpuUtilization);
        double pmNextPowerUtility = getPmPowerUtility(pm, WorkloadLevel.getWorkload(pmNextCpuLevel));
        double pmPowerUtility = pmNextPowerUtility - pmCurrPowerUtility;
        LOGGER.trace(pm.toShortString() + ":" + String.format("overallPmCpu=%d", overallPmCpu));
        LOGGER.trace(pm.toShortString() + ":" + String.format("currPlannedCpuUtilization=%.2f", currPlannedCpuUtilization));
        LOGGER.trace(pm.toShortString() + ":" + String.format("pmCurrPowerUtility=%.2f", pmCurrPowerUtility));
        LOGGER.trace(pm.toShortString() + ":" + String.format("pmNextPowerUtility=%.2f", pmNextPowerUtility));
        LOGGER.trace(pm.toShortString() + ":" + String.format("pmPowerUtility=%.2f", pmPowerUtility));

        // compute other sla overusage utility
        String otherViolationLevel = network.getNodeByName(NodeName.HasOverallOverusage).getLevels().get(mostProbableLevels.get(3));
        boolean hasOtherViolation = BooleanLevel.getValue(otherViolationLevel);
        double otherViolationUtility = hasOtherViolation ? 0.0 : 1.0;
        LOGGER.trace(pm.toShortString() + ":" + String.format("otherViolationLevel=%s,priceUtility=%.2f", otherViolationLevel, otherViolationUtility));

        // 
//        double allocatedResourcesUtility = getAllocatedResourcesUtility(pm);
//        LOGGER.trace(pm.toShortString() + ":" + String.format("allocatedResourcesUtility=%.2f", allocatedResourcesUtility));

        // compute overall utility
//        double utility =
//                = 1 * slaCummViolationUtility // will cummulative downtime of the vm be violated
//                1 * slaViolationUtility // will overall downtime of the vm be violated
//                + 2 * pmPowerUtility // how much energy will be consumed
//                + 1 * migrTimeUtility // how long vms will be migrated
//                + 1 * otherViolationUtility // whether other vms can have violation
//                + 1 * pmCpuWorkloadUtility // how strong cpu will be loaded, more usage is better
//                + 1 * priceUtility // energy price
//                + 1 * allocatedResourcesUtility; 
        double utility = 
                  3 * slaViolationUtility // will overall downtime of the vm be violated
                + 3 * otherViolationUtility // whether other vms can have violation
                + 1 * migrTimeUtility // how long vms will be migrated
                + 2 * pmPowerUtility // how much energy will be consumed
                + 5 * pmCpuWorkloadUtility // how strong cpu will be loaded, more usage is better
                + 5 * priceUtility; // energy price


        LOGGER.trace(String.format(new Locale("en", "US"), "Utility for %s is %.2f [slaViolation=%.2f,slaCummViolation=%.2f,pmPower=%.2f,migrTime=%.2f,"
                + "otherViolation=%.2f, pmCpuWorkload=%.2f, price=%.2f,"
                + "slaCummViolationLevel=%s, slaViolationLevel=%s, pmCpuLevel=%s, migrTimeLevel=%s, otherViolationLevel=%s]", pm.toShortString(), utility,
                slaCummViolationUtility, slaCummViolationUtility, pmPowerUtility, migrTimeUtility,
                otherViolationUtility, pmCpuWorkloadUtility, priceUtility,
                slaCummViolationLevel,
                slaViolationLevel,
                pmNextCpuLevel,
                migrTimeLevel,
                otherViolationLevel));

        LOGGER.debug("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f",
                pm.toShortString(),
                slaCummViolationUtility,
                slaViolationUtility,
                pmPowerUtility,
                migrTimeUtility,
                otherViolationUtility,
                pmCpuWorkloadUtility,
                priceUtility,
                utility);

        return utility;
    }

    private double getPmPowerUtility(Pm pm, double pmCpuUtilization) {
        Ensure.NotNull(pm, "pm");
        Ensure.BetweenInclusive(pmCpuUtilization, 0.0, 1.0, "pmCpuUtilization");

        if (pm.getPmState() == PmState.PowerOff && pmCpuUtilization == 0) {
            return 1;
        }

        // compute pm power consumption utility, lower is better
        double temperature = pm.getDatacenter().getTemperature().getTemperature(clock.now());
        double pPue = pm.getDatacenter().getCoolingModel().getpPUE(temperature);
        double pmPower = pm.getSpec().getPower(pmCpuUtilization);
        double pmPowerWithPPue = pmPower * (1 + pPue);
        double pmPowerUtility = UtilityHelper.getPmPowerUtility(pmPowerWithPPue);

        return pmPowerUtility;
    }

    private double getAllocatedResourcesUtility(Pm pm) {
        long mips = pm.getVms().stream().
                mapToLong(this::predictNextCpuConsumption).sum() + consumedMipsPerPm.getOrDefault(pm.getId(), 0L);
        LOGGER.trace("Allocated mips for %s is %d.", pm.toShortString(), mips);
        if (mips < pm.getSpec().getMips()) {
            return 1.0;
        } else {
            return (double) pm.getSpec().getMips() / mips;
        }
    }

    private void runPmAndNotifyIfNecessary(Cloud cloud, Pm pm) {
        Ensure.NotNull(cloud, "cloud");
        Ensure.NotNull(pm, "pm");

        if (!usedPms.contains(pm.getId())) {
            usedPms.add(pm.getId());
            if (pm.getPmState() == PmState.PowerOff) {
                cloud.getMessageBus().push(new PmStartMessage(pm));
            }
        }
    }

    private long predictNextCpuConsumption(Vm vm) {
        Long value = nextVmCpu.get(vm.getId());
        if (value == null) {
            // predict value using history
            value = predStrategy.predictValue(clock.now(), clock.previous(), vm.getCpuWorkloadHistory());
            // if value is null, use value from specs
            if (value == null || value == 0.0) {
                value = vm.getSpec().getMips();
            }
            nextVmCpu.put(vm.getId(), value);
            LOGGER.trace("Next cpu consumption for %s will be %d.", vm.toShortString(), value);
        }
        return value;
    }

    private long predictNextBwConsumption(Vm vm) {
        // predict value using history
        Long value = predStrategy.predictValue(clock.now() + clock.getStep(), clock.now(), vm.getBwWorkloadHistory());
        // if value is null, use value from specs
        if (value == null) {
            value = vm.getSpec().getBw();
        }
        return value;
    }

    private long predictNextRamConsumption(Vm vm) {
        Long value = nextVmRam.get(vm.getId());
        if (value == null) {
            // predict value using history
            value = predStrategy.predictValue(clock.now() + clock.getStep(), clock.now(), vm.getRamWorkloadHistory());
            // if value is null, use value from specs
            if (value == null) {
                value = vm.getSpec().getRam();
            }
            nextVmRam.put(vm.getId(), value);
        }
        return value;
    }

    private long predictNextSizeConsumption(Vm vm) {
        // predict value using history
        Long value = predStrategy.predictValue(clock.now() + clock.getStep(), clock.now(), vm.getSizeWorkloadHistory());
        // if value is null, use value from specs
        if (value == null) {
            value = vm.getSpec().getSize();
        }
        return value;
    }

    private void updatedAvailablePmResources(Pm pm, Vm vm, boolean isVmAdded) {
        Ensure.NotNull(pm, "pm");
        Ensure.NotNull(vm, "vm");

        Long mips = consumedMipsPerPm.getOrDefault(pm.getId(), 0L);
        Long next = predictNextCpuConsumption(vm);
        mips = isVmAdded ? mips + next : mips - next;
//        if (isVmAdded) {
//            
//            mips += next;
//        } else {
//            mips -= vm.getProvisionedMips();
//        }
        consumedMipsPerPm.put(pm.getId(), mips);

        Integer allocated = allocatedVmsPerPm.getOrDefault(pm.getId(), 0);
        allocated += isVmAdded ? 1 : -1;
        allocatedVmsPerPm.put(pm.getId(), allocated);
    }

    public BayesNet getNetwork() {
        BayesNet network = new BayesNet("pm");

        Node otherVmNextCpu = new Node(NodeName.OtherVmNextCpu, WorkloadLevel.All());
        network.addNode(otherVmNextCpu);

        Node vmNextCpu = new Node(NodeName.VmNextCpu, WorkloadLevel.All());
        network.addNode(vmNextCpu);

        Node pmOverallCpu = new Node(NodeName.PmOverallCpu, WorkloadLevel.All());
        network.addNode(pmOverallCpu);

        Node pmNextOverallCpu = new Node(NodeName.PmNextOverallCpu,
                WorkloadLevel.AllWithOverusage(), DistrHelper.TwoWorkloadsSumDistribution());
        network.addNode(pmNextOverallCpu);
        network.addLink(pmOverallCpu, pmNextOverallCpu);
        network.addLink(vmNextCpu, pmNextOverallCpu);

        Node hasOverallOverusage = new Node(NodeName.HasOverallOverusage,
                BooleanLevel.All(), DistrHelper.WorkloadToOverusageDistribution());
        network.addNode(hasOverallOverusage);
        network.addLink(pmNextOverallCpu, hasOverallOverusage);

        Node pmSameSlaNextCpu = new Node(NodeName.PmSameSlaNextCpu,
                WorkloadLevel.AllWithOverusage(), DistrHelper.TwoWorkloadsSumDistribution());
        network.addNode(pmSameSlaNextCpu);
        network.addLink(otherVmNextCpu, pmSameSlaNextCpu);
        network.addLink(vmNextCpu, pmSameSlaNextCpu);

        Node country = new Node(NodeName.Country, CountryLevel.All());
        network.addNode(country);

        Node powerOutage = new Node(NodeName.PowerOutage, BooleanLevel.All(), DistrHelper.PowerOutageDistr());
        network.addNode(powerOutage);
        network.addLink(country, powerOutage);

        Node powerOutageDuration = new Node(NodeName.PowerOutageDuration,
                WorkloadLevel.AllWithOverusage(), DistrHelper.PowerOutageDurationDistr());
        network.addNode(powerOutageDuration);
        network.addLink(country, powerOutageDuration);
        network.addLink(powerOutage, powerOutageDuration);

        Node vmNextRam = new Node(NodeName.VmNextRam, WorkloadLevel.All());
        network.addNode(vmNextRam);

        Node pmInDc = new Node(NodeName.PmInDc, BooleanLevel.All());
        network.addNode(pmInDc);

        Node dirtyPageRate = new Node(NodeName.DirtyPageRate,
                DirtyPageLevel.All(), DistrHelper.WorkloadToDirtyPageDistr());
        network.addNode(dirtyPageRate);
        network.addLink(vmNextRam, dirtyPageRate);
        network.addLink(pmInDc, dirtyPageRate);

        Node vmRamSize = new Node(NodeName.VmRamSize, VmSizeLevel.All());
        network.addNode(vmRamSize);

        Node dcBw = new Node(NodeName.DatacenterBandwidth, BwLevel.All());
        network.addNode(dcBw);

        Node migrTime = new Node(NodeName.MigrationTime,
                WorkloadLevel.AllWithOverusage(), DistrHelper.MigrationTimeDistribution());
        network.addNode(migrTime);
        network.addLink(dirtyPageRate, migrTime);
        network.addLink(vmRamSize, migrTime);
        network.addLink(dcBw, migrTime);

        Node vmDowntime = new Node(NodeName.VmDowntime,
                BooleanLevel.All(), DistrHelper.WorkloadToOverusageDistribution());
        network.addNode(vmDowntime);
        network.addLink(pmSameSlaNextCpu, vmDowntime);

        Node vmDowntimeDuration = new Node(NodeName.VmDowntimeDuration,
                WorkloadLevel.AllWithOverusage(), DistrHelper.VmDowntimeDistribution());
        network.addNode(vmDowntimeDuration);
        network.addLink(migrTime, vmDowntimeDuration);
        network.addLink(vmDowntime, vmDowntimeDuration);

        Node vmAdjDowntimeDuration = new Node(NodeName.VmAdjDowntimeDuration,
                WorkloadLevel.AllWithOverusage(), DistrHelper.TwoWorkloadsAndDistribution());
        network.addNode(vmAdjDowntimeDuration);
        network.addLink(vmDowntimeDuration, vmAdjDowntimeDuration);
        network.addLink(powerOutageDuration, vmAdjDowntimeDuration);

        Node vmCummSlaViolationRate = new Node(NodeName.VmCummSlaViolationRate, WorkloadLevel.All());
        network.addNode(vmCummSlaViolationRate);

        Node vmSlaViolationRate = new Node(NodeName.VmSlaViolationRate, WorkloadLevel.All());
        network.addNode(vmSlaViolationRate);

        Node vmAdjSlaViolationRate = new Node(NodeName.VmAdjSlaViolationRate,
                WorkloadLevel.AllWithOverusage(),
                DistrHelper.getTwoWorkloadsSumDistribution(
                        WorkloadLevel.AllWithOverusage().length, // levels of vmAdjDowntimeDuration
                        WorkloadLevel.All().length, // levels of vmSlaViolationRate
                        WorkloadLevel.AllWithOverusage().length)); // levels of vmAdjSlaViolationRate
        network.addNode(vmAdjSlaViolationRate);
        network.addLink(vmSlaViolationRate, vmAdjSlaViolationRate);
        network.addLink(vmAdjDowntimeDuration, vmAdjSlaViolationRate);

        Node vmCummAdjSlaViolationRate = new Node(NodeName.VmCummAdjSlaViolationRate,
                WorkloadLevel.AllWithOverusage(),
                DistrHelper.getTwoWorkloadsSumDistribution(
                        WorkloadLevel.AllWithOverusage().length, // levels of vmAdjDowntimeDuration
                        WorkloadLevel.All().length, // levels of vmCummSlaViolationRate
                        WorkloadLevel.AllWithOverusage().length)); // levels of vmCummAdjSlaViolationRate
        network.addNode(vmCummAdjSlaViolationRate);
        network.addLink(vmCummSlaViolationRate, vmCummAdjSlaViolationRate);
        network.addLink(vmAdjDowntimeDuration, vmCummAdjSlaViolationRate);

        return network;
    }

    private static class NodeName {

        private static final String OtherVmNextCpu = "othervmcpu";
        private static final String VmNextCpu = "vmcpu";
        private static final String PmSameSlaNextCpu = "pmslacpunext";
        private static final String PmOverallCpu = "pmcpu";
        private static final String PmNextOverallCpu = "pmcpunext";
        private static final String HasOverallOverusage = "overusage";
        private static final String VmNextRam = "vmram";
        private static final String DirtyPageRate = "dirtypagerate";
        private static final String MigrationTime = "migrtime";
        private static final String VmRamSize = "vmramsize";
        private static final String PmInDc = "pmindc";
        private static final String Country = "country";
        private static final String PowerOutage = "powerout";
        private static final String PowerOutageDuration = "poweroutdur";
        private static final String VmDowntime = "vmdowntime";
        private static final String VmDowntimeDuration = "vmdowntimedur";
        private static final String VmAdjDowntimeDuration = "vmadjdowntimedur";
        private static final String VmSlaViolationRate = "vmslaviol";
        private static final String VmAdjSlaViolationRate = "vmadjslaviol";
        private static final String VmCummSlaViolationRate = "vmcummslaviol";
        private static final String VmCummAdjSlaViolationRate = "vmcummadjslaviol";
        private static final String DatacenterBandwidth = "dcbw";
    }

    private static final class PmUtilityResult {

        private final Pm pm;
        private final Double utility;

        public PmUtilityResult(Pm pm, Double utility) {
            this.pm = pm;
            this.utility = utility;
        }

        public Pm getPm() {
            return pm;
        }

        public Double getUtility() {
            return utility;
        }
    }

}
