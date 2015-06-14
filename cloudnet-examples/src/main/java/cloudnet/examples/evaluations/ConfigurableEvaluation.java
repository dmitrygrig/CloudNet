/*
 * Copyright (C) 2015 Dmytro
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
package cloudnet.examples.evaluations;

import cloudnet.elasticity.AlwaysVmMigrationPolicy;
import cloudnet.workloads.prediction.PessimisticSimplePredictionStrategy;
import cloudnet.workloads.prediction.SimplePredictionStrategy;
import cloudnet.workloads.prediction.SimpleRegressionPredictionStrategy;
import cloudnet.workloads.prediction.TrendPredictionStrategy;
import cloudnet.workloads.prediction.WorkloadPredictionStrategy;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Dmytro
 */
public class ConfigurableEvaluation {

    // options
    private static int vmNum = 25;
    private static int pmPerDcNum = 5;
    private static String em = "ffo";
    private static String ps = "sps";
    private static WorkloadPredictionStrategy predStrategy;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Options options = makeOptions();
        CommandLineParser parser = new DefaultParser();
        try {

            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("vmPlacementSimApp", options);
                return;
            }

            // apply options
            applyOptions(cmd);

            // run simulation
            runSimulation();

        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }

        // exit in order to stop R-Environment if it is used.
        System.exit(0);
    }

    private static Options makeOptions() {
        Options options = new Options();
        options
                .addOption("em", "elasticityManager", true, "Elasticity manager: ue, ffp, ffo, bn (default ffo)")
                .addOption("ps", "predictionStrategy", true, "Prediction Strategy: sps, psps, tps, srps (default sps)")
                .addOption("vm", "vmNum", true, "Number of scheduled vms (default 25)")
                .addOption("pm", "pmPerDcNum", true, "Number of allocated pms per each datacenter (default 5)")
                .addOption("h", "help", false, "Prints option help.");
        return options;
    }

    private static void applyOptions(CommandLine cmd) {

        if (cmd.hasOption("vmNum")) {
            vmNum = Integer.parseInt(cmd.getOptionValue("vmNum"));
        }
        if (cmd.hasOption("pmPerDcNum")) {
            pmPerDcNum = Integer.parseInt(cmd.getOptionValue("pmPerDcNum"));
        }
        if (cmd.hasOption("em")) {
            em = cmd.getOptionValue("em");
        }

        if (cmd.hasOption("ps")) {
            ps = cmd.getOptionValue("ps");
            switch (ps) {
                case "tps":
                    predStrategy = new TrendPredictionStrategy();
                    break;
                case "sps":
                    predStrategy = new SimplePredictionStrategy();
                    break;
                case "psps":
                    predStrategy = new PessimisticSimplePredictionStrategy();
                    break;
                case "srps":
                    predStrategy = new SimpleRegressionPredictionStrategy();
                    break;
                default:
                    throw new IllegalArgumentException("Prediction Strategy type not supported: " + ps);
            }
        }

        if (em.equals(ScenarioEvaluator.EM_BN) && predStrategy == null) {
            throw new IllegalArgumentException("Prediction strategy should be specified for bn.");
        }
    }

    private static void runSimulation() {
        Scenario sc = Scenario.createScenario4(em, new AlwaysVmMigrationPolicy(), predStrategy);
        sc.setVmNum(vmNum);
        sc.setPmPerDcNum(pmPerDcNum);

        ScenarioEvaluator evaluator = new ScenarioEvaluator();
        evaluator.evaluate(sc);
    }

}
