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

import cloudnet.elasticity.DowntimeVmMigrationPolicy;
import cloudnet.workloads.prediction.PessimisticSimplePredictionStrategy;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class Evaluations2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ScenarioEvaluator evaluator = new ScenarioEvaluator();
        evaluator.evaluate(
                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.1)),
                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.2)),
                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.3)),
                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.4)),
                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.5)),
                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.1)),
                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.2)),
                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.3)),
                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.4)),
                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.5)),
                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.1)),
                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.2)),
                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.3)),
                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.4)),
                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.5)),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.1)),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.2)),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.3)),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.4)),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.5)),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.1)),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.2)),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.3)),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.4)),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.5)),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.1)),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.2)),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.3)),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.4)),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new DowntimeVmMigrationPolicy(), new PessimisticSimplePredictionStrategy(.5))
        );

        // exit in order to stop R-Environment if it is used.
        System.exit(0);
    }

}
