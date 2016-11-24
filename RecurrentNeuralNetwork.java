import org.encog.ConsoleStatusReportable;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.plugin.system.SystemActivationPlugin;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;
import org.encog.util.simple.TrainingSetUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by iris on 16-11-16.
 */
public class RecurrentNeuralNetwork extends EncogModel implements Serializable  {
    private static final long serialVersionUID = -88L;

    static BasicNetwork createElmanNetwork(){
        //construct a Elman type network
        ElmanPattern pattern = new ElmanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(30);
        pattern.addHiddenLayer(6);
        pattern.setOutputNeurons(1);
        return (BasicNetwork)pattern.generate();
    }

    public void storeGenome() {
        ObjectOutputStream out = null;
        try {
            //create the memory folder manually
            out = new ObjectOutputStream(new FileOutputStream("memory/mydriver.mem"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.writeObject(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double trainNetwork(final String what,
                                      final BasicNetwork network, final MLDataSet trainingSet){
        CalculateScore score = new TrainingSetScore(trainingSet);
        // Create main trained network via backpropagation.
        final MLTrain trainMain = new Backpropagation(network, trainingSet, 0.0000001, 0.0);
        // Create alternative via simulated annealing, to overcome local minima
        final MLTrain trainAlt = new NeuralSimulatedAnnealing(network, score, 10, 2, 50);
        final StopTrainingStrategy stop = new StopTrainingStrategy();
        trainMain.addStrategy(new Greedy());
        trainMain.addStrategy(new HybridStrategy(trainAlt));
        trainMain.addStrategy(stop);
        int epoch = 0;
        while (!stop.shouldStop()){
            trainMain.iteration();
            System.out.println("Training " + what + ", Epoch #" + epoch + " Error: " + trainMain.getError());
            epoch++;
        }
        return trainMain.getError();
    }

    public static void main(String [ ] args){
        //RecurrentNeuralNetwork algorithm = new RecurrentNeuralNetwork();
        //BasicNetwork ElmanNN = algorithm.createElmanNetwork();
        File csvfile = new File("/home/iris/Documents/MasterAI/ComputationalIntelligence/RaceProject/src/train_data/aalborg.csv");
        VersatileDataSource source = new CSVDataSource(csvfile, true, CSVFormat.DECIMAL_POINT);
        VersatileMLDataSet data = new VersatileMLDataSet(source);
        //MLDataSet trainingset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, "/home/iris/Documents/MasterAI/ComputationalIntelligence/RaceProject/src/train_data/aalborg.csv", false, 2, 1);
        ColumnDefinition columnAcceleration = data.defineSourceColumn("ACCELERATION", ColumnType.continuous);
        ColumnDefinition columnBrake = data.defineSourceColumn("BRAKE", ColumnType.continuous);
        ColumnDefinition columnSteering = data.defineSourceColumn("STEERING", 2, ColumnType.continuous);

        //source.rewind();
        ColumnDefinition[] outputColumns = new ColumnDefinition[3];
        outputColumns[0] = columnAcceleration;
        outputColumns[1] = columnBrake;
        outputColumns[2] = columnSteering;

        //data.defineSourceColumn("SPEED", 3, ColumnType.continuous);
        for(int i = 0; i<22; i++){
            String name = "Sensor_" +  Integer.toString(i);
            data.defineSourceColumn(name, i+3, ColumnType.continuous);
        }

        data.analyze();
        data.defineMultipleOutputsOthersInput(outputColumns);
        RecurrentNeuralNetwork model = new RecurrentNeuralNetwork(data) ;
        model.selectMethod(data, MLMethodFactory.TYPE_NEAT);

        // Send any output to the console.
        model.setReport(new ConsoleStatusReportable());

        // Now normalize the data.  Encog will automatically determine the correct normalization
        // type based on the model you chose in the last step.
        data.normalize();

        // Hold back some data for a final validation.
        // Shuffle the data into a random ordering.
        // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
        model.holdBackValidation(0.9, true, 1001);

        // Choose whatever is the default training type for this model.
        model.selectTrainingType(data);

        // Use a 5-fold cross-validated train.  Return the best method found.
        MLRegression bestMethod = (MLRegression)model.crossvalidate(5, true);

        // Display the training and validation errors.
        System.out.println( "Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
        System.out.println( "Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));

        // Display our normalization parameters.
        NormalizationHelper helper = data.getNormHelper();
        System.out.println(helper.toString());

        // Display the final model.
        System.out.println("Final model: " + bestMethod);

        System.out.println(model);
        System.out.println(model.getPredictedFeatures());
        System.out.println(model.getReport());
        model.storeGenome();



        //double error = trainNetwork("Elman", ElmanNN, data );
        //System.out.println(error);
    }

}
