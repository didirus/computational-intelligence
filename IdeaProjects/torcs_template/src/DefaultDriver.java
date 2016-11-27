import cicontest.algorithm.abstracts.AbstractDriver;
import cicontest.algorithm.abstracts.DriversUtils;
import cicontest.torcs.controller.extras.ABS;
import cicontest.torcs.controller.extras.AutomatedClutch;
import cicontest.torcs.controller.extras.AutomatedGearbox;
import cicontest.torcs.controller.extras.AutomatedRecovering;
import cicontest.torcs.genome.IGenome;
import org.encog.neural.pattern.ElmanPattern;
import scr.Action;
import scr.SensorModel;

public class DefaultDriver extends AbstractDriver {

    private NeuralNetwork neuralNetwork;
    private ElmanPattern elmanPattern;

    public DefaultDriver() {
        initialize();
        neuralNetwork = new NeuralNetwork(); // should maybe be 3 output nodes
//        neuralNetwork = neuralNetwork.loadGenome();
    }

//    private trainModel() {
//        createElmanNetwork
//    }

    private void initialize() {
        this.enableExtras(new AutomatedClutch());
        this.enableExtras(new AutomatedGearbox());
        this.enableExtras(new AutomatedRecovering());
        this.enableExtras(new ABS());
    }

    @Override
    public void loadGenome(IGenome genome) {
        if (genome instanceof DefaultDriverGenome) {
            DefaultDriverGenome myGenome = (DefaultDriverGenome) genome;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    @Override
    public double getAcceleration(SensorModel sensors) {
        double[] sensorArray = new double[22];
//        double[] output = new double[3];
        Double[] output = neuralNetwork.getOutput(sensors);
        return output[0]; // this should probably be 'output'
    }

    @Override
    public double getSteering(SensorModel sensors) {
        Double[] output = neuralNetwork.getOutput(sensors);
        return output[1]; // this should probably be 'output'
    }

//    @Override
    public double getBraking(SensorModel sensors) {
        Double[] output = neuralNetwork.getOutput(sensors);
        return output[2];
    }

    @Override
    public String getDriverName() {
        return "Example Controller";
    }

    @Override
    public Action controlWarmUp(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlQualification(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action controlRace(SensorModel sensors) {
        Action action = new Action();
        return defaultControl(action, sensors);
    }

    @Override
    public Action defaultControl(Action action, SensorModel sensors) {
        if (action == null) {
            action = new Action();
        }
//        action.steering = DriversUtils.alignToTrackAxis(sensors, 0.5);

        action.steering = getSteering(sensors);
        action.accelerate = getAcceleration(sensors);
        action.brake = getBraking(sensors);

//        if (sensors.getSpeed() > 100.0D) {
//            action.accelerate = 0.0D;
//            action.brake = 0.0D;
//        }
//
//        if (sensors.getSpeed() > 110.0D) {
//            action.accelerate = getAcceleration();
//            action.brake = -1.0D;
//        }
//
//        if (sensors.getSpeed() <= 100.0D) {
//            action.accelerate = (120.0D - sensors.getSpeed()) / 120.0D;
//            action.brake = 0.0D;
//        }
//
//        if (sensors.getSpeed() < 30.0D) {
//            action.accelerate = 100.0D;
//            action.brake = 0.0D;
//        }
        System.out.println("--------------" + getDriverName() + "--------------");
        System.out.println("Time: " + sensors.getTime());
        System.out.println("Steering: " + action.steering);
        System.out.println("Acceleration: " + action.accelerate);
        System.out.println("Brake: " + action.brake);
        System.out.println("Speed: " + sensors.getSpeed());
        System.out.println("-----------------------------------------------");
        return action;
    }
}