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

import cloudnet.cooling.CoolingModel;
import cloudnet.core.Cloud;
import cloudnet.core.Datacenter;
import cloudnet.core.Pm;
import cloudnet.core.PmSpec;
import cloudnet.core.Size;
import cloudnet.core.Vm;
import cloudnet.core.VmSpec;
import cloudnet.elasticity.ElasticityManager;
import cloudnet.elasticity.VmMigrationPolicy;
import cloudnet.iaas.IaaSCloud;
import cloudnet.iaas.VmGenerator;
import cloudnet.iaas.VmGeneratorOnce;
import cloudnet.locations.Location;
import cloudnet.locations.Locations;
import cloudnet.locations.Oslo;
import cloudnet.locations.RioDeJaneiro;
import cloudnet.locations.Tokyo;
import cloudnet.locations.Toronto;
import cloudnet.locations.Vienna;
import cloudnet.messaging.PmStartMessage;
import cloudnet.messaging.VmMigrationMessage;
import cloudnet.provisioners.GreedyProvisioner;
import cloudnet.sim.SimClock;
import cloudnet.workloads.prediction.WorkloadPredictionStrategy;
import java.util.List;
import jbayes.core.BayesNet;
import jbayes.core.DiscreteDistribution;
import jbayes.core.Node;
import jbayes.r.R;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.mockito.Matchers.anyDouble;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class ElasiticityManagerMCDAViolationsTest {

    private static R r;

    @BeforeClass
    public static void setUpClass() {
        r = R.getInstance();
    }

    @Before
    public void setUp() {
        r.clearAll();
    }

    private Cloud getTestCloud(SimClock clock, ElasticityManager em) {
        PmSpec pmSpec = mock(PmSpec.class);
        when(pmSpec.getMips()).thenReturn(2000L);
        when(pmSpec.getBw()).thenReturn(Size.GBit);
        when(pmSpec.getRam()).thenReturn(4 * Size.GB);
        when(pmSpec.getSize()).thenReturn(100 * Size.GB);

        CoolingModel cm = mock(CoolingModel.class);
        when(cm.getpPUE(anyDouble())).thenReturn(1.05);

        Cloud cloud = new IaaSCloud(1, clock);
        cloud.attachPlugin(em);
        // cheaper dc at nicht
        Datacenter dc1 = createDatacenter(1, clock, new Oslo(), cm);
        // more expensive dc at night
        Datacenter dc2 = createDatacenter(2, clock, new Toronto(), cm);

        Pm pm1 = createPm(1, clock, pmSpec);
        Pm pm2 = createPm(2, clock, pmSpec);
        dc1.addPm(pm1);
        dc2.addPm(pm2);

        cloud.addDatacenter(dc1);
        cloud.addDatacenter(dc2);

        return cloud;
    }

    private static Datacenter createDatacenter(int id, SimClock clock, Location location, CoolingModel model) {
        Datacenter dc = Datacenter.forLocation(id, clock, location);
        dc.setCoolingModel(model);
        return dc;
    }

    private static Pm createPm(int id, SimClock clock, PmSpec pmSpec) {
        Pm pm = new Pm(id, clock, pmSpec);
        pm.setMipsProvisioner(new GreedyProvisioner());
        pm.setRamProvisioner(new GreedyProvisioner());
        pm.setSizeProvisioner(new GreedyProvisioner());
        pm.setBwProvisioner(new GreedyProvisioner());
        return pm;

    }

    private VmSpec getVmSpec() {
        VmSpec vmSpec = mock(VmSpec.class);
        when(vmSpec.getMips()).thenReturn(1000L);
        when(vmSpec.getBw()).thenReturn(100 * Size.MBit);
        when(vmSpec.getRam()).thenReturn(Size.GB);
        when(vmSpec.getSize()).thenReturn(10 * Size.GB);

        return vmSpec;
    }

    private void testForNullValues(List<Double> list) {
        for (int i = 0; i < list.size(); i++) {
            Double value = list.get(i);
            assertNotNull(value);
            assertNotEquals(value, Double.NaN);
        }
    }

    @Test
    public void testNetwork() {
        System.out.println("network");

        ElasiticityManagerMCDA instance = new ElasiticityManagerMCDA(new SimClock(), mock(WorkloadPredictionStrategy.class), mock(VmMigrationPolicy.class));
        instance.setR(r);

        BayesNet result = instance.getNetwork();
        assertNotNull(result);

        for (Node node : result.getNodes()) {
            DiscreteDistribution d = (DiscreteDistribution) node.getDistribution();
            testForNullValues(d.getCpt());
        }
    }

    @Test
    public void testManageMigrations() {
        System.out.println("manage");

        /**
         * The test checks that elasticity manager will migrate from one DC to
         * another one.
         */
        SimClock clock = new SimClock();
        WorkloadPredictionStrategy str = mock(WorkloadPredictionStrategy.class);
        when(str.predictValue(anyLong(), anyLong(), any())).thenReturn(0L);

        VmMigrationPolicy migrPolicy = mock(VmMigrationPolicy.class);
        when(migrPolicy.shouldBeMigrated(any())).thenReturn(Boolean.TRUE);

        ElasiticityManagerMCDA instance = new ElasiticityManagerMCDA(clock, str, migrPolicy);
        instance.setR(r);

        Cloud cloud = getTestCloud(clock, instance);

        VmGenerator vmGenerator = new VmGeneratorOnce(getVmSpec(), 3);
        List<Vm> vms = vmGenerator.generate(clock);
        Pm pm = findDatacenterById(cloud, 2).getPms().iterator().next();
        pm.run();
        for (Vm vm : vms) {
            vm.allocateTo(pm);
        }

        clock.add();

        instance.manage(cloud);

        // migration of three vms should be scheduled to another dc
        assertEquals(1, cloud.getMessageBus().getMessagesByType(PmStartMessage.class).size());
        assertEquals(2, cloud.getMessageBus().getMessagesByType(VmMigrationMessage.class).size());
    }

    private static Datacenter findDatacenterById(Cloud cloud, int id) {
        return cloud.getDatacenters().stream().filter(dc -> dc.getId() == id).findFirst().get();
    }

}
