import java.io.File;

import cicontest.algorithm.abstracts.AbstractAlgorithm;
import cicontest.algorithm.abstracts.AbstractRace;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.Driver;
import cicontest.torcs.controller.Human;
import race.TorcsConfiguration;

public class DefaultDriverAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 654963126362653L;

    DefaultDriverGenome[] drivers = new DefaultDriverGenome[1];
    int[] results = new int[1];

    public Class<? extends Driver> getDriverClass() {
        return DefaultDriver.class;
    }

    public void run(boolean continue_from_checkpoint) {
        if (!continue_from_checkpoint) {
            //init NN
            DefaultDriverGenome genome = new DefaultDriverGenome();
            drivers[0] = genome;

            //Start a race
            DefaultRace race = new DefaultRace();
            race.setTrack("aalborg", "road");
            race.laps = 1;

            //for speedup set withGUI to false
            results = race.runRace(drivers, true);

            // Save genome/nn
            DriversUtils.storeGenome(drivers[0]);
        }
        // create a checkpoint this allows you to continue this run later
        DriversUtils.createCheckpoint(this);
        //DriversUtils.clearCheckpoint();
    }

//import org.encog.ConsoleStatusReportable;
//import org.encog.engine.network.activation.ActivationSigmoid;
//import org.encog.ml.CalculateScore;
//import org.encog.ml.MLRegression;
//import org.encog.ml.data.MLData;
//import org.encog.ml.data.MLDataSet;
//import org.encog.ml.data.basic.BasicMLDataSet;
//import org.encog.ml.data.versatile.NormalizationHelper;
//import org.encog.ml.data.versatile.VersatileMLDataSet;
//import org.encog.ml.data.versatile.columns.ColumnDefinition;
//import org.encog.ml.data.versatile.columns.ColumnType;
//import org.encog.ml.data.versatile.sources.CSVDataSource;
//import org.encog.ml.data.versatile.sources.VersatileDataSource;
//import org.encog.ml.factory.MLMethodFactory;
//import org.encog.ml.model.EncogModel;
//import org.encog.ml.train.MLTrain;
//import org.encog.ml.train.strategy.Greedy;
//import org.encog.ml.train.strategy.HybridStrategy;
//import org.encog.ml.train.strategy.StopTrainingStrategy;
//import org.encog.neural.networks.BasicNetwork;
//import org.encog.neural.networks.training.TrainingSetScore;
//import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
//import org.encog.neural.networks.training.propagation.back.Backpropagation;
//import org.encog.neural.pattern.ElmanPattern;
//import org.encog.plugin.system.SystemActivationPlugin;
//import org.encog.util.csv.CSVFormat;
//import org.encog.util.csv.ReadCSV;
//import org.encog.util.obj.SerializeObject;
//import org.encog.util.simple.EncogUtility;
//import org.encog.util.simple.TrainingSetUtil;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.Arrays;


    public static void main(String[] args) {

        // If you want to train the model, decomment below:
        // Please check the file names if you don't want to overwrite stuff

//        NeuralNetwork.trainModel();



        // If you want to perform the race with a model acquired after training, decomment below:
        // Please check if the right helper and model file are selected in class NeuralNetwork

        /*
		 *
		 * Start without arguments to run the algorithm
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 *
		 */

        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));

        DefaultDriverAlgorithm algorithm = new DefaultDriverAlgorithm();
        DriversUtils.registerMemory(algorithm.getDriverClass());
        if (args.length > 0 && args[0].equals("-show")) {
            new DefaultRace().showBest();
        } else if (args.length > 0 && args[0].equals("-show-race")) {
            new DefaultRace().showBestRace();
        } else if (args.length > 0 && args[0].equals("-human")) {
            new DefaultRace().raceBest();
        } else if (args.length > 0 && args[0].equals("-continue")) {
            if (DriversUtils.hasCheckpoint()) {
                DriversUtils.loadCheckpoint().run(true);
            } else {
                algorithm.run();
            }
        } else {
            algorithm.run();
        }

    }

}