import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.util.csv.CSVFormat;

import java.io.File;

/**
 * Created by diederusticus on 18/11/16.
 */
public class NeuralNetAlgorithm {



    public static void main(String[] args) {
        Readfile()
        File training_data = new File("/Users/diederusticus/Downloads/torcs_template/train_data/aalborg.csv");
//        CSVFormat format = new CSVFormat(',',' ');
        VersatileDataSource source = new CSVDataSource(training_data, true, CSVFormat.DECIMAL_POINT);
        VersatileMLDataSet data = new VersatileMLDataSet(source);
//        data.getNormHelper().setFormat(format);
        ColumnDefinition columnAcceleration = data.defineSourceColumn("ACCELERATION", ColumnType.continuous);
        ColumnDefinition columnBrake = data.defineSourceColumn("BRAKE", ColumnType.continuous);
        ColumnDefinition columnAngle = data.defineSourceColumn("ANGLE_TO_TRACK_AXIS", ColumnType.continuous);
        data.analyze();
        System.out.println(columnAngle.getMean());
    }
}
