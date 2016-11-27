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


        // checkpoint if training is still necessary
        // check if mem file exists if not:
//        NeuralNetwork.trainModel();


        //Set path to torcs.properties
        TorcsConfiguration.getInstance().initialize(new File("torcs.properties"));
        /*
		 *
		 * Start without arguments to run the algorithm
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 *
		 */
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

//        RecurrentNeuralNetwork algorithm = new RecurrentNeuralNetwork();
        //BasicNetwork ElmanNN = algorithm.createElmanNetwork();
//        File csvfile = new File("/Users/diederusticus/Downloads/torcs_template/train_data/aalborg.csv");
//        VersatileDataSource source = new CSVDataSource(csvfile, true, CSVFormat.DECIMAL_POINT);
//        VersatileMLDataSet data = new VersatileMLDataSet(source);
//        //MLDataSet trainingset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, "/home/iris/Documents/MasterAI/ComputationalIntelligence/RaceProject/src/train_data/aalborg.csv", false, 2, 1);
//        ColumnDefinition columnAcceleration = data.defineSourceColumn("ACCELERATION", ColumnType.continuous);
//        ColumnDefinition columnBrake = data.defineSourceColumn("BRAKE", ColumnType.continuous);
//        ColumnDefinition columnSteering = data.defineSourceColumn("STEERING", 2, ColumnType.continuous);
//
//        //source.rewind();
//        ColumnDefinition[] outputColumns = new ColumnDefinition[3];
//        outputColumns[0] = columnAcceleration;
//        outputColumns[1] = columnBrake;
//        outputColumns[2] = columnSteering;
//
//        //data.defineSourceColumn("SPEED", 3, ColumnType.continuous);
//        for(int i = 0; i<22; i++){
//            String name = "Sensor_" +  Integer.toString(i);
//            data.defineSourceColumn(name, i+3, ColumnType.continuous);
//        }
//
//        data.analyze();
//        data.defineMultipleOutputsOthersInput(outputColumns);
//        EncogModel model = new EncogModel(data) ;
//        model.selectMethod(data, MLMethodFactory.TYPE_NEAT);
//
//        // Send any output to the console.
//        model.setReport(new ConsoleStatusReportable());
//
//        // Now normalize the data.  Encog will automatically determine the correct normalization
//        // type based on the model you chose in the last step.
//        data.normalize();
//
//        // Hold back some data for a final validation.
//        // Shuffle the data into a random ordering.
//        // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
//        model.holdBackValidation(0.99, true, 1001);
//
//        // Choose whatever is the default training type for this model.
//        model.selectTrainingType(data);
//
//        // Use a 5-fold cross-validated train.  Return the best method found.
//        MLRegression bestMethod = (MLRegression)model.crossvalidate(5, true);
//
//        // Display the training and validation errors.
//        System.out.println( "Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
//        System.out.println( "Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));
//
//        // Display our normalization parameters.
//        NormalizationHelper helper = data.getNormHelper();
//        System.out.println(helper.toString());
//
////         Display the final model.
//        System.out.println("Final model: " + bestMethod);
//        try {
//            SerializeObject.save(new File("/resulthelper.mem"), helper);
//            SerializeObject.save(new File("/resultmodel.mem"), bestMethod);
//        }catch(Exception e){
//            System.err.print("Error msg: " + e);
//        }
//
//        ReadCSV csv = new ReadCSV("/Users/diederusticus/Downloads/torcs_template/train_data/alpine-1.csv", true, CSVFormat.DECIMAL_POINT);
//
//        String line[] = new String[22];
//        MLData input = helper.allocateInputVector();
//        while(csv.next()){
//            StringBuilder result = new StringBuilder();
//            for(int i = 0; i<22; i++) {
//                line[i] = csv.get(i + 3);
//            }
////
//            String correct1 = csv.get(0);
//            String correct2 = csv.get(1);
//            String correct3 = csv.get(2);
//
//            helper.normalizeInputVector(line, input.getData(), false);
//            MLData output = bestMethod.compute(input);
//            String acceleration = helper.denormalizeOutputVectorToString(output)[0];
//            String brake = helper.denormalizeOutputVectorToString(output)[1];
//            String steering = helper.denormalizeOutputVectorToString(output)[2];
////            result.append(Arrays.toString(line));
//            result.append("-> predicted: ");
//            result.append(acceleration+" ");
//            result.append("-> correct: ");
//            result.append(correct1+" ");
//            result.append("-> predicted: ");
//            result.append(steering+" ");
//            result.append("-> correct: ");
//            result.append(correct2+" ");
//            result.append("-> predicted: ");
//            result.append(brake+" ");
//            result.append("-> correct: ");
//            result.append(correct3+" ");
//
//            System.out.println(result.toString());
//        }

        //double error = trainNetwork("Elman", ElmanNN, data );
        //System.out.println(error);
    }

}