package ahuraDriver;
//package com.jcg;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class DriverControllerE6 extends Controller{
//	private float clutch = 0;
	private static final String COMMA_DELIMITER = ",";

	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FILE_HEADER = "ACCELERATION,BRAKE,STEERING,SPEED,TRACK_POSITION,ANGLE_TO_TRACK_AXIS,TRACK_EDGE_0,TRACK_EDGE_1,TRACK_EDGE_2,TRACK_EDGE_3,TRACK_EDGE_4,TRACK_EDGE_5,TRACK_EDGE_6,TRACK_EDGE_7,TRACK_EDGE_8,TRACK_EDGE_9,TRACK_EDGE_10,TRACK_EDGE_11,TRACK_EDGE_12,TRACK_EDGE_13,TRACK_EDGE_14,TRACK_EDGE_15,TRACK_EDGE_16,TRACK_EDGE_17";
	SpeedControllerE6 speedController = new SpeedControllerE6();
	DirectionControllerE6 directionController = new DirectionControllerE6();
	List<SensorModel> sensorList = new ArrayList<SensorModel>();  
	double totalDistance = 0.0;
	double prevLapTime = 0.0;
	double totalTime = 0.0;
	double damage = 0.0;
	float clutch = DriverControllerHelperE6.clutchMax;
	int lapCounter = 1;
	boolean lastLapTimeCounted= false;
	MySensorModel noiseCan = new MySensorModel();
	Action action = new Action();
	
	double tempTime = 0.0;

	public void writeIntoCSV(){
		//nothing for now

	}
	@Override
	public Action control(SensorModel sensors) {
		if(myPara.getStage().compareTo(Stage.COMPLEXITYMEASURER)==0){
			if(tempTime+1.0 < sensors.getCurrentLapTime()){
				myPara.setTotalTime(tempTime+1.0);
				tempTime++;
				myPara.writeToResultsFile((float) DriverControllerHelperE6.maximumDistanceInfront(sensors.getTrackEdgeSensors()));
			}
		}
//		System.out.println(sensors.getZ() + " "+ sensors.getZSpeed());
		
//		totalDistance += sensors.getDistanceRaced();
		totalDistance = sensors.getDistanceFromStartLine();
		if(sensors.getDistanceFromStartLine() < 10.0f && sensors.getDistanceFromStartLine() > 0.0f && !lastLapTimeCounted){
			lastLapTimeCounted = true;
			prevLapTime += sensors.getLastLapTime();
			lapCounter++;
		}
		
		OpponentController2.zigzaggerPos = myPara.zigzaggerposition;
		
		if(sensors.getDistanceFromStartLine() > 10.0f)
			lastLapTimeCounted = false;
		
		totalTime = prevLapTime + sensors.getCurrentLapTime();
		
		StuckTypes isStuck = StuckHandler.isStuck(sensors);
		boolean isOut = StuckHandler.isOutTrack(sensors);
//		myPara.updatePenalty(isOut, sensors.getDamage(), lapCounter, sensors.getRacePosition(), sensors.getDistanceFromStartLine());
						
		speedController.setMyPara(myPara);
		directionController.setMyPara(myPara);
		
		sensorList.add(sensors);
		damage = sensors.getDamage();
		if(sensorList.size() > DriverControllerHelperE6.memorySensorLength){
			sensorList.remove(0);
		}
		
		noiseCan = NoiseCanceller.cancelNoise(sensorList);
				
		double estimatedTurn = DriverControllerHelperE6.turnDirectionCalculator(noiseCan, 9);
		speedController.setEstimatedTurn(estimatedTurn);
		directionController.setEstimatedTurn(estimatedTurn);
//		OpponentController1.relativeSpeedUpdater(noiseCan);
		OpponentController2.opponentsInfoUpdater(noiseCan, myPara);
		
		
		int gear = speedController.calculateGear(noiseCan, isStuck);
		double steer = directionController.calcSteer(noiseCan, isStuck, isOut);

		myPara.frictionUpdater(gear, noiseCan, steer, action.accelerate);
		myPara.dangerZoneUpdater(sensors);
		myPara.trackWidthUpdater(noiseCan, steer, isOut);
		
		float [] accelBrake = speedController.calcBrakeAndAccelPedals(noiseCan, steer, isStuck, isOut);
		if(gear == 1)
			clutch = DriverControllerHelperE6.clutchMax;
		if(clutch > 0.0)
			clutch = speedController.clutching(noiseCan, this.clutch);

//		Sensor data
//
//		sensors.getCurrentLapTime();
//		sensors.getDamage();
//		sensors.getCurrentLapTime();
//		sensors.getDistanceFromStartLine();
//		sensors.getDistanceRaced();
//		sensors.getFocusSensors();
//		sensors.getFuelLevel();
//		sensors.getWheelSpinVelocity();
		System.out.print("  ");
		System.out.print(sensors.getTrackPosition());
		System.out.print("  ");
		System.out.print(sensors.getAngleToTrackAxis());
		System.out.println("  ");
		System.out.print(sensors.getTrackEdgeSensors()[18]);
		System.out.print("  ");

		System.out.print(" Acceleration :");
		System.out.print(accelBrake[1]);
		System.out.print(" Brake :");
		System.out.print(accelBrake[0]);
		System.out.print(" steer :");
		System.out.print(steer);
		System.out.print(" Speed :");
		System.out.print(sensors.getSpeed());
		//CSV
		FileWriter fileWriter = null;
		//FileWriter

		try {

			fileWriter = new FileWriter("Fample2.csv", true);





			//Write the CSV file header

//			fileWriter.append(FILE_HEADER.toString());



			//Add a new line separator after the header




			//Write a new student object list to the CSV file

				fileWriter.append(String.valueOf(accelBrake[1]));

				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(String.valueOf(accelBrake[0]));

				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(String.valueOf(steer));

				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(String.valueOf(sensors.getSpeed()));

				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(String.valueOf(sensors.getTrackPosition()));

				fileWriter.append(COMMA_DELIMITER);

				fileWriter.append(String.valueOf(sensors.getAngleToTrackAxis()));

				int i=0;
				while (i<19){
					fileWriter.append(COMMA_DELIMITER);
					fileWriter.append(String.valueOf(sensors.getTrackEdgeSensors()[i]));
					i+=1;
				}



			fileWriter.append(NEW_LINE_SEPARATOR);









			System.out.println("CSV file was created successfully !!!");



		} catch (Exception e) {

			System.out.println("Error in CsvFileWriter !!!");

			e.printStackTrace();

		} finally {



			try {

				fileWriter.flush();

				fileWriter.close();

			} catch (IOException e) {

				System.out.println("Error while flushing/closing fileWriter !!!");

				e.printStackTrace();

			}



		}
// END OF CSV

//
// sensors.getRPM();
//		sensors.getRacePosition();
//		sensors.getOpponentSensors();
//		sensors.getLateralSpeed();
//		sensors.getLastLapTime();
//


        action.gear = gear;
        action.steering = steer;
        action.accelerate = accelBrake[1];
        action.brake = accelBrake[0];
        action.clutch = clutch;
//		System.out.print("gear :");
//		System.out.print(gear);

//		System.out.print(" clutch :");
//		System.out.println(clutch);

		return action;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		System.out.println("Restarting the race!");
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		myPara.setDamage(damage);
		myPara.setTotalTime(totalTime);
//		System.out.println(totalDistance);
	}
	
	public float[] initAngles()	{
		
		float[] angles = DriverControllerHelperE6.angles;
		
		/* set angles as {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90} */
		return angles;
	}

}
