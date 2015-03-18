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

import cloudnet.elasticity.ShortDowntimeVmMigrationPolicy;
import cloudnet.elasticity.AlwaysVmMigrationPolicy;
import cloudnet.workloads.prediction.PessimisticSimplePredictionStrategy;
import cloudnet.workloads.prediction.SimplePredictionStrategy;
import cloudnet.workloads.prediction.SimpleRegressionPredictionStrategy;
import cloudnet.workloads.prediction.TrendPredictionStrategy;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class Evaluations {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ScenarioEvaluator evaluator = new ScenarioEvaluator();
        evaluator.evaluate(
                // sc1
//                Scenario.createScenario1(ScenarioEvaluator.EM_UE, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario1(ScenarioEvaluator.EM_FFP, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario1(ScenarioEvaluator.EM_FFO, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimplePredictionStrategy()),
//                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new PessimisticSimplePredictionStrategy()),
//                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new TrendPredictionStrategy()),
//                Scenario.createScenario1(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimpleRegressionPredictionStrategy()),
//                // sc2
//                Scenario.createScenario2(ScenarioEvaluator.EM_UE, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario2(ScenarioEvaluator.EM_FFP, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario2(ScenarioEvaluator.EM_FFO, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimplePredictionStrategy()),
//                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new PessimisticSimplePredictionStrategy()),
//                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new TrendPredictionStrategy()),
//                Scenario.createScenario2(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimpleRegressionPredictionStrategy()),
//                // sc3
//                Scenario.createScenario3(ScenarioEvaluator.EM_UE, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario3(ScenarioEvaluator.EM_FFP, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario3(ScenarioEvaluator.EM_FFO, new ShortDowntimeVmMigrationPolicy(), null),
//                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimplePredictionStrategy()),
//                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new PessimisticSimplePredictionStrategy()),
//                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new TrendPredictionStrategy()),
//                Scenario.createScenario3(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimpleRegressionPredictionStrategy()),
                // sc4
                Scenario.createScenario4(ScenarioEvaluator.EM_UE, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario4(ScenarioEvaluator.EM_FFP, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario4(ScenarioEvaluator.EM_FFO, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimplePredictionStrategy()),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new PessimisticSimplePredictionStrategy()),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new TrendPredictionStrategy()),
                Scenario.createScenario4(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimpleRegressionPredictionStrategy()),
                // sc5
                Scenario.createScenario5(ScenarioEvaluator.EM_UE, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario5(ScenarioEvaluator.EM_FFP, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario5(ScenarioEvaluator.EM_FFO, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimplePredictionStrategy()),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new PessimisticSimplePredictionStrategy()),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new TrendPredictionStrategy()),
                Scenario.createScenario5(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimpleRegressionPredictionStrategy()),
                // sc6
                Scenario.createScenario6(ScenarioEvaluator.EM_UE, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario6(ScenarioEvaluator.EM_FFP, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario6(ScenarioEvaluator.EM_FFO, new ShortDowntimeVmMigrationPolicy(), null),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimplePredictionStrategy()),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new PessimisticSimplePredictionStrategy()),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new TrendPredictionStrategy()),
                Scenario.createScenario6(ScenarioEvaluator.EM_BN, new AlwaysVmMigrationPolicy(), new SimpleRegressionPredictionStrategy())
        );

        // exit in order to stop R-Environment if it is used.
        System.exit(0);
    }

}
