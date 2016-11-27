import org.encog.ConsoleStatusReportable;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.obj.SerializeObject;
import org.encog.util.simple.EncogUtility;
import scr.SensorModel;

import java.io.*;

public class NeuralNetwork implements Serializable {

    private static final long serialVersionUID = -88L;

//    acceleration
//            brake
//            steering
    NormalizationHelper helper;
    MLRegression bestMethod;

    NeuralNetwork() {

        helper = loadHelper();
        bestMethod = loadGenome();

    }

    public Double[] getOutput(SensorModel a) {
//        System.out.println(a);
//        return 0.5;
        String[] line = new String[22];
        MLData input = helper.allocateInputVector();

        line[0] = String.valueOf(a.getSpeed());
        line[1] = String.valueOf(a.getTrackPosition());
        line[2] = String.valueOf(a.getAngleToTrackAxis());
        for (int i = 0; i < 19; i++) {
            line[i+3] = String.valueOf(a.getTrackEdgeSensors()[i]);
        }
        helper.normalizeInputVector(line, input.getData(), false);
        MLData output = bestMethod.compute(input);
        String acceleration = helper.denormalizeOutputVectorToString(output)[0];
        String brake = helper.denormalizeOutputVectorToString(output)[1];
        String steering = helper.denormalizeOutputVectorToString(output)[2];
        Double[] outputs = new Double[3];
        outputs[0] = Double.parseDouble(acceleration);
        outputs[1] = Double.parseDouble(brake);
        outputs[2] = Double.parseDouble(steering);
        System.out.println(outputs[0]);
        return outputs;

    }

    public static void trainModel() {
        VersatileMLDataSet data = getData();
        ColumnDefinition[] outputColumns = getColumns(data);
        EncogModel model = getModel(data, outputColumns);
        train(model, data);
        System.out.println("model has been updated");
    }

    private static VersatileMLDataSet getData() {
        File csvfile = new File("/Users/diederusticus/Downloads/torcs_template/train_data/All-ahura.csv");
//        CSVFormat format = new CSVFormat(';');
        VersatileDataSource source = new CSVDataSource(csvfile, true, CSVFormat.DECIMAL_POINT);
        VersatileMLDataSet data = new VersatileMLDataSet(source);
        return data;
    }

    private static ColumnDefinition[] getColumns(VersatileMLDataSet data) {
        //MLDataSet trainingset = TrainingSetUtil.loadCSVTOMemory(CSVFormat.ENGLISH, "/home/iris/Documents/MasterAI/ComputationalIntelligence/RaceProject/src/train_data/aalborg.csv", false, 2, 1);
        ColumnDefinition columnAcceleration = data.defineSourceColumn("ACCELERATION", ColumnType.continuous);
        ColumnDefinition columnBrake = data.defineSourceColumn("BRAKE", ColumnType.continuous);
        ColumnDefinition columnSteering = data.defineSourceColumn("STEERING", 2, ColumnType.continuous);

        ColumnDefinition[] outputColumns = new ColumnDefinition[3];
        outputColumns[0] = columnAcceleration;
        outputColumns[1] = columnBrake;
        outputColumns[2] = columnSteering;
        for (int i = 0; i < 22; i++) {
            String name = "Sensor_" + Integer.toString(i);
            data.defineSourceColumn(name, i + 3, ColumnType.continuous);
        }
        data.analyze();
        return outputColumns;
    }

    public static EncogModel getModel(VersatileMLDataSet data, ColumnDefinition[] outputColumns) {
        data.defineMultipleOutputsOthersInput(outputColumns);
        EncogModel model = new EncogModel(data);
        model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

        // Send any output to the console.
        model.setReport(new ConsoleStatusReportable());

        // Now normalize the data.  Encog will automatically determine the correct normalization
        // type based on the model you chose in the last step.
        data.normalize();

        return model;
    }

    public static MLRegression train(EncogModel model, VersatileMLDataSet data) {

        // Hold back some data for a final validation.
        // Shuffle the data into a random ordering.
        // Use a seed of 1001 so that we always use the same holdback and will get more consistent results.
        model.holdBackValidation(0.1, true, 1001);

        // Choose whatever is the default training type for this model.
        model.selectTrainingType(data);

        // Use a 5-fold cross-validated train.  Return the best method found.
        MLRegression bestMethod = (MLRegression) model.crossvalidate(5, true);

        // Display the training and validation errors.
        System.out.println("Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
        System.out.println("Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));

        // Display our normalization parameters.
        NormalizationHelper helper = data.getNormHelper();
        System.out.println(helper.toString());

        // Save the final model
        System.out.println("Final model: " + bestMethod);
        try {
            SerializeObject.save(new File("/Users/diederusticus/Downloads/torcs_template/src/memory/resulthelper-ff.mem"), helper);
            SerializeObject.save(new File("/Users/diederusticus/Downloads/torcs_template/src/memory/resultmodel-ff.mem"), bestMethod);
        } catch (Exception e) {
            System.err.print("Error msg: " + e);
        }

        return bestMethod;
    }



//    ReadCSV csv = new ReadCSV("/Users/diederusticus/Downloads/torcs_template/train_data/alpine-1.csv", true, CSVFormat.DECIMAL_POINT);
//
//    String line[] = new String[22];
//    MLData input = helper.allocateInputVector();
//        while(csv.next()){
//        StringBuilder result = new StringBuilder();
//        for(int i = 0; i<22; i++) {
//            line[i] = csv.get(i + 3);
//        }
////
//        String correct1 = csv.get(0);
//        String correct2 = csv.get(1);
//        String correct3 = csv.get(2);
//
//        helper.normalizeInputVector(line, input.getData(), false);
//        MLData output = bestMethod.compute(input);
//        String acceleration = helper.denormalizeOutputVectorToString(output)[0];
//        String brake = helper.denormalizeOutputVectorToString(output)[1];
//        String steering = helper.denormalizeOutputVectorToString(output)[2];
////            result.append(Arrays.toString(line));
//        result.append("-> predicted: ");
//        result.append(acceleration+" ");
//        result.append("-> correct: ");
//        result.append(correct1+" ");
//        result.append("-> predicted: ");
//        result.append(steering+" ");
//        result.append("-> correct: ");
//        result.append(correct2+" ");
//        result.append("-> predicted: ");
//        result.append(brake+" ");
//        result.append("-> correct: ");
//        result.append(correct3+" ");
//
//        System.out.println(result.toString());
//    }

//    //Store the state of this neural network
//    public void storeGenome() {
//        ObjectOutputStream out = null;
//        try {
//            //create the memory folder manually
//            out = new ObjectOutputStream(new FileOutputStream("memory/resultmodel.mem"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (out != null) {
//                out.writeObject(this);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // Load a neural network from memory
    public MLRegression loadGenome() { // MLRegression was NeuralNetwork

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream("/Users/diederusticus/Downloads/torcs_template/src/memory/resultmodel-ff.mem");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read object using ObjectInputStream
        ObjectInputStream obj_in = null;
        try {
            obj_in = new ObjectInputStream(f_in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read an object
        try {
            if (obj_in != null) {
                return (MLRegression) obj_in.readObject(); // MLRegression was NeuralNetwork
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Load a helper from memory
    public NormalizationHelper loadHelper() { // MLRegression was NeuralNetwork

        // Read from disk using FileInputStream
        FileInputStream f_in = null;
        try {
            f_in = new FileInputStream("/Users/diederusticus/Downloads/torcs_template/src/memory/resulthelper-ff.mem");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Read object using ObjectInputStream
        ObjectInputStream obj_in = null;
        try {
            obj_in = new ObjectInputStream(f_in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read an object
        try {
            if (obj_in != null) {
                return (NormalizationHelper) obj_in.readObject(); // MLRegression was NeuralNetwork
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
