import java.rmi.ConnectException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.DeviceException;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;


/**
 * This class is responsible for communicating instructions to the robot
 * and getting data back from it. It uses RobotToHandlerQueue.java
 * and HandlerToRobotQueue.java
 * Notes: Throws Remote Exception is needed for the motor calls
 * @author Adam, Jeremy
 */
public class Robot implements RobotInterface {
    RemoteEV3 ev3;
    // Motor variables. A=right motor, D=left motor, C=arm motor
    RMIRegulatedMotor leftMotor, rightMotor, armMotor;

    // Sensor Providers
    RMISampleProvider colorProvider, ultrasonicProvider, gyroProvider;

    // Global speed variables
    private int turnSpeed;
    private int driveSpeed = 50;
    private int colorMoverSpeed = 50;
    private float colorMoverCalibrationFactor = 3.9f;
    
    // Robot state variables
    private Point robotPosition;
    private float robotDirection;
    private Point colorSensorPosition;
    private float colorArmAngle;
    private Point colorSensorOffset;
    
    private float distanceRemaining;
    private float previousDrivingDistance = 0;
    
    private float turnRemaining;
    private float currentAngle;
    private float lastAngle;
    
    
    @SuppressWarnings("unused")
	private float turnGoal;
    // private boolean isStopping;
    // private float moveCooldown;
    private boolean isReacting;
    
    // private int previousColorMoverDistance = 0;
    
    // Robot dimensions (in metres)
    private final float headLength = 0.17f; // distance from center of robot to front of ultrasonic sensor 
    private final float wheelDiameter = 0.056f; 
    private final Point armPivotOffset = new Point(0.016f, 0.042f); // distance from centre of robot to arm pivot
    private final float armLength = 0.1f;
    private final Point armHeadOffset = new Point(-0.016f, 0.03f); // distance from end of arm pivot to sensor
    private final int armDriveCog = 16;
    private final int armMotionCog = 36;
    static public final float maximumRadiusFromCentre = 0.23f;
    
    private final float moveErr = (0.228f-0.250f)/(0.250f);
    
    // Conversion from drive motor degrees turned into distance travelled
    private final float rotationDistance = (float) (360.0f / (wheelDiameter * Math.PI));
    
    // Conversion from arm motor degrees turned into degrees turned around pivot
    // To replace colorMoverCalibrationFactor
    @SuppressWarnings("unused")
	private final float armRotationRatio = (float) (armDriveCog / armMotionCog);
    
    // Global message timing variables
    private long messageRate = 25; //in milliseconds
    
    private boolean autoMode = false; // Started in manual mode

    ColorDetector colorDetector;
    
    private RobotJobQueue queue;
    private RobotJobQueue.Job currentJob;
    private RobotJobQueue.Job idleJob;
    private boolean isInterupted;
    private int interuptedJobID;

    private long messageCooldown;
    
    // Number of samples to take for the sensor readings.
    private final int colorSampleRate = 5;
    private final int ultrasonicSampleRate = 2;
   
    /**
     * This function gets gyro sensor data
     * @return data A float
     * @author Adam, Jeremy
     */
    public float readGyroSensor() throws RemoteException {
        return gyroProvider.fetchSample()[0];
    }

    /**
     * This function gets ultrasonic sensor data
     * @return data Returns a float with ultrasonic distance information
     * @author Adam, Jeremy
     */
    private float readDistanceSensor() throws RemoteException {
        float sensedDistance = 0.0f;
        for (int i = 0; i < ultrasonicSampleRate; i += 1) {
            sensedDistance += ultrasonicProvider.fetchSample()[0];
        }
        return (sensedDistance / ultrasonicSampleRate) + headLength;
    }

    /**
     * This function gets color sensor data. If the color sensor is in RedMode
     * do not access elements 1 and 2.
     * @return data Returns colourSensor data in a float array
     * @author Adam, Jeremy
     */
    private RGBColor readColorSensor() throws RemoteException {
        float[] averageSensedColor = {0.0f, 0.0f, 0.0f};
        for (int i = 0; i < colorSampleRate; i += 1) {
            float[] sensedColor = colorProvider.fetchSample();
            for (int channel = 0; channel < 3; channel += 1) {
                averageSensedColor[channel] += sensedColor[channel];
            }
        }
        
        return new RGBColor(averageSensedColor[0] / colorSampleRate,
                            averageSensedColor[1] / colorSampleRate,
                            averageSensedColor[2] / colorSampleRate);
    }
    
    /**
     * This function is called at start and when drive/turn speed is changed to set the speed.
     * @throws RemoteException
     */
    private void setMotorSpeeds() throws RemoteException {
        leftMotor.setSpeed(driveSpeed*turnSpeed);
        rightMotor.setSpeed(driveSpeed*turnSpeed);
        armMotor.setSpeed(colorMoverSpeed);
    }
    
    /**
     * Checks if the robot is in motion.
     * @return True if either main motor in use.
     * @throws RemoteException
     * @see isDriving
     * @see isTurning
     */
    private boolean isMoving() throws RemoteException {
    	    return (leftMotor.isMoving()
    	            || rightMotor.isMoving()
    	            || leftMotor.getTachoCount() != 0
    	            || rightMotor.getTachoCount() !=0);
    }
    
    /**
     * Checks if the robot is driving turning.
     * Assumes that motor tachometers are reset for every movement.
     * @return True if turning.
     * @throws RemoteException
     * @see isMoving
     * @see isDriving
     */
    private boolean isTurning() throws RemoteException {
    	if (leftMotor.isMoving() && rightMotor.isMoving()) {
    		// Uses Exclusive Or - returns true only if one motor is moving forward and the other is not
    		return ((leftMotor.getTachoCount() > 0) ^ (rightMotor.getTachoCount() > 0));
    	}
    	return false;
    }
    
    
    /**
     * Checks if the robot is driving forwards or backwards.
     * Assumes that motor tachometers are reset for every movement.
     * @return True if driving forwards or backwards.
     * @throws RemoteException
     * @see isMoving
     * @see isTurning
     */
    private boolean isDriving() throws RemoteException {
        	if (leftMotor.isMoving() && rightMotor.isMoving()) {
        		// Uses Exclusive Not Or - returns true if 
        		return !((leftMotor.getTachoCount() > 0) ^ (rightMotor.getTachoCount() > 0));
        	}
        	return false;
    }
    
    /**
     * Returns true if the color arm motor is moving.
     * @return True or False
     * @throws RemoteException
     * @see colorMoverForever
     * @see colorMoverStop
     */
    private boolean isArmMoving() throws RemoteException {
    	if(armMotor.isMoving()) return true;
    	return false;
    }
    
    /**
     * Returns the distance traveled since the drive motor tachometers were last reset.
     * Tachometers should be reset whenever the robot stops a motion.
     * @return distance traveled (in metres)
     * @throws RemoteException
     */
    private float motorDistance() throws RemoteException {
    	int motorAngle = ((leftMotor.getTachoCount()+rightMotor.getTachoCount())/2);
    	float motorDistance = motorAngle / rotationDistance;
    	motorDistance *= (1+moveErr);
    	return motorDistance; 	
    }
       
    /**
     * This function updates the position of the robot and the color sensor.
     * The function is called every step, and updates positions as robot drives, turns, and moves color sensor.
     * @author Adam, Jeremy
     * @throws RemoteException
     */
    private void updatePosition() throws RemoteException {
    	if (Settings.Debug.showUpdatePositionOutput) {
        	System.out.printf("isMoving: %b; isDriving: %b; isTurning: %b; isArmMoving: %b%n", isMoving(), isDriving(), isTurning(), isArmMoving());
		}
        if (isDriving()){
            float currentDistance = motorDistance();
            float stepDistance = motorDistance() - previousDrivingDistance;
            robotPosition.xMetres -= (stepDistance) * Math.sin(Math.toRadians(robotDirection));
            robotPosition.yMetres += (stepDistance) * Math.cos(Math.toRadians(robotDirection));
            previousDrivingDistance = currentDistance;
            distanceRemaining -= Math.abs(stepDistance);
            if (Settings.Debug.showUpdatePositionOutput) {
            	System.out.printf("currentDistance: %f; stepDistance: %f;%n", currentDistance, stepDistance);
            	System.out.printf("previousDrivingDistance: %f; distanceRemaining: %f;%n", previousDrivingDistance, distanceRemaining);
    		}
        }
        if (isTurning()){
            	currentAngle = readGyroSensor();
            	turnRemaining -= Math.abs(currentAngle - lastAngle);
            	lastAngle = currentAngle;
            	if (Settings.Debug.showUpdatePositionOutput) {
                	System.out.printf("currentAngle: %f; turnRemaining: %f; lastAngle: %f;%n", currentAngle, turnRemaining, lastAngle);
        		}
        }

        if (isArmMoving()) {
        	colorArmAngle = armMotor.getTachoCount()/colorMoverCalibrationFactor;
            // colorSensorOffset gives a vector from centre of robot to head
            colorSensorOffset = Point.add(armPivotOffset, armHeadOffset);
            colorSensorOffset = Point.add(colorSensorOffset, 
	    					        	  new Point((float) (armLength * Math.sin(-colorArmAngle)),
	    					        			    (float) (armLength * Math.cos(colorArmAngle))));
            
        }
        float pivotOffset = armPivotOffset.yMetres + armHeadOffset.yMetres;

        colorSensorPosition.xMetres =
                (float) (robotPosition.xMetres 
                         + pivotOffset * Math.sin(-Math.toRadians(robotDirection))
                         + armLength * Math.sin(-Math.toRadians(robotDirection + colorArmAngle)));
        
        colorSensorPosition.yMetres =
                (float) (robotPosition.yMetres 
                         + pivotOffset * Math.cos(Math.toRadians(robotDirection))
                         + armLength * Math.cos(Math.toRadians(robotDirection + colorArmAngle)));
        if (Settings.Debug.showUpdatePositionOutput) {
        	System.out.printf("colorArmAngle: %f; colorSensorOffset.xMetres: %f; colorSensorOffset.yMetres: %f;%n", colorArmAngle, colorSensorOffset.xMetres, colorSensorOffset.yMetres);
        	System.out.printf("colorSensorPosition.xMetres: %f; colorSensorPosition.yMetres: %f;%n", colorSensorPosition.xMetres, colorSensorPosition.yMetres);
        }
    }

    /**
     * This function makes the robot drive forwards or backwards until stopDriving() is called. 
     * @author Adam, Jeremy
     * @see stopDriving
     */
    public void drive(float distance) throws RemoteException {
        if (autoMode || isReacting) {
            distanceRemaining = Math.abs(distance);
        }
    	
        leftMotor.setAcceleration(2000);
        rightMotor.setAcceleration(2000);
        if ( distance > 0 ){
            leftMotor.forward();
            rightMotor.forward();
        } else {
            leftMotor.backward();
            rightMotor.backward();
        }
    }

    /**
     * This function turns the robot left or right until stopDriving() is called.
     * @param degrees
     * @see stopDriving
     * @throws RemoteException
     */
    public void turn( float degrees ) throws RemoteException {
        	if (autoMode) {
        		turnGoal = robotDirection + degrees;
        		turnRemaining = Math.abs(degrees);
        		lastAngle = readGyroSensor();
        	}
    	
        leftMotor.setAcceleration(500);
        rightMotor.setAcceleration(500);
        // Turn left
        if ( degrees > 0 ) {
            leftMotor.backward();
            rightMotor.forward();
        // Turn right
        } else {
            leftMotor.forward();
            rightMotor.backward();
        }
    }
    
    /**
     * This function makes the robot stop moving.
     * @author Adam, Jeremy
     * @see drive
     * @see turn
     */
    private void stopDriving() throws RemoteException {
        leftMotor.stop(true);
        rightMotor.stop(true);
        //isStopping = true;
        //moveCooldown = System.currentTimeMillis() + 25;
        
        previousDrivingDistance = 0;
        turnGoal = 0;
        //previousColorMoverDistance = 0;
        leftMotor.resetTachoCount();
        rightMotor.resetTachoCount();
    } 

//    /**
//     * This rotates the color mover motor
//     * @param rotations the number of rotations you'd like (Positive is towards the right hand side)
//     * @author Adam
//     */
//    public void colorMover(double angle) throws RemoteException {
//        int angle2 = (int) (angle*colorMoverCalibrationFactor);
//        colorArmAngle += angle2;
//        armMotor.rotate(angle2);
//    }

    /**
     * This function rotates the color sensor arm motor until colorMoverStop() is called.
     * @author Adam
     * @see colorMoverStop
     */
    public void colorMoverForever( float direction ) throws RemoteException {
        if ( direction > 0 ){
            armMotor.forward();
        } else {
            armMotor.backward();
        }
    }

    /**
     * This stops any motion of the color sensor arm motor. 
     * @author Adam
     * @see colorMoverForever
     */
    private void colorMoverStop() throws RemoteException {
        armMotor.stop(true);
    }

    /**
     * Immediately sends a message that the robot as been interrupted.
     * isInterupted is set to false once a new auto job is started.
     * @throws RemoteException
     */
    private void interupt() throws RemoteException {
    	isInterupted = true;
        interuptedJobID = currentJob.jobID;
        sendMessage(true);
    }
    
    /**
     * Allows the robot to be stopped in automode without the emergency stop being called.
     * (Emergency stop cause robot to shift into manual mode).
     * @throws RemoteException
     */
    private void autoStop() throws RemoteException {
    	stopDriving();
//    	queue.clear();
        currentJob = idleJob;
    }
    
    /**
     * ObjectAvoid function is called when reflex detects an object or a black
     * line. It stops the robot and moves back 2 cm.
     * @throws RemoteException
     * @author Adam
     * @see reflex
     */
    @SuppressWarnings("unused")
	private void objectAvoid() throws RemoteException{
    	isReacting = true;
    	stopDriving();
    	drive(-0.02f);
    }

    /**
     * This is a reflex function for the robot. Warns the handler when it detects a crater or border,
     * and stops if about to hit an obstacle.
     * @throws RemoteException
     * @author Adam
     * @see objectAvoid
     */
    @SuppressWarnings("unused")
	private void reflex() throws RemoteException {
        float distance = readDistanceSensor() - headLength;
        //System.out.println(distance + " < " + 0.03);
        if ( isDriving() && distance < 0.06 ){
        	interupt();
        	autoStop();
            //objectAvoid();
        }
        RGBColor color = readColorSensor();
        ColorDetector.Color detectedColor = colorDetector.detect(color);
        if ( detectedColor == ColorDetector.Color.BLACK || detectedColor == ColorDetector.Color.BLUE) {
        	interupt();
        	isInterupted = false;
        	//objectAvoid();
        }
    }

    /**
     * This function sends a message to the Handler with an update on its current status. 
     * @param boolean to indicate if message is sent with priority.
     * @throws RemoteException
     * @author Adam, Jeremy
     */
    private void sendMessage(boolean isPriority) throws RemoteException {
        if (isPriority || System.currentTimeMillis() > messageCooldown) {
        	System.err.println("Size of robotToHandlerQueue: " + this.rthQueue.size());
    	    robotDirection = readGyroSensor();
            RobotToHandlerQueue.Message message = new RobotToHandlerQueue.Message();
            message.position = robotPosition;
            message.angleDegrees = robotDirection%360;
            message.sensorRGB = readColorSensor();
            message.sensorDistanceMetres = readDistanceSensor();
            message.colorSensorPosition = colorSensorPosition;
            message.currentJob = currentJob.jobID;
            message.lastJobCompleted = queue.getLastFinished();
            message.lastJobRecieved = queue.getLastJob();
            message.interupted = isInterupted;
            message.interuptedJob = interuptedJobID;
            
            rthQueue.add(message);
            messageCooldown = System.currentTimeMillis() + messageRate;
            if (Settings.Debug.showRobotJobStatus) {
                System.out.println("Current:"+message.currentJob+", lastCompleted:"+message.lastJobCompleted+", lastGot:"+message.lastJobRecieved+"interupted: "+message.interupted+"interuptedID: "+message.interuptedJob);
            }
            if (Settings.Debug.showRobotMessages) {
            	System.out.printf("isPriority: %b%n", isPriority);
            	System.out.printf("Position: (%f, %f); Angle: %f%n", message.position.xMetres, message.position.yMetres, message.angleDegrees);
            	System.out.printf("Colour: (%f,%f,%f);%n", message.sensorRGB.r, message.sensorRGB.g, message.sensorRGB.b);
            	System.out.printf("Object Distance: %f; colorSensorPosition: (%f, %f)%n", message.sensorDistanceMetres, message.colorSensorPosition.xMetres, message.colorSensorPosition.yMetres);
            	System.out.printf("Current Job: %i; Last Completed: %i; Last Recieved: %i%n", message.currentJob, message.lastJobCompleted, message.lastJobRecieved);
            	System.out.printf("Interupted?: %b; Iterupted ID: %i%n", message.interupted, message.interuptedJob);
            }
        }
    	}
    /**
     * This function sends a message to the Handler with an update on its current status. 
     * Has optional parameter isPriority, set to false by default.
     * @throws RemoteException
     * @author Adam, Jeremy
     */
    private void sendMessage() throws RemoteException {
    	sendMessage(false);
    }
    
    /**
     * This step function iterates one step over the behaviour of the robot.
     * Includes: reading messages, acting on messages, reflex, and sending messages
     * @throws NotBoundException
     * @throws MalformedURLException
     * @author Adam
     */
    @Override
    public void step() throws RemoteException, MalformedURLException, NotBoundException {
    	/* Step Order:
    	 * 1. Check for new commands
    	 * 2. Update robot position
    	 * 3. Autonomous mode
         * 	a. Checks status of current job
         * 	b. Checks status of movement stopping
         *  c. Checks for new jobs
         * 4. Sends message (if cool-down allows)
         */
        if ( htrQueue.hasNewMessages() ) {
            HandlerToRobotQueue.Command command = htrQueue.receive();
            switch (command.messageType) {
            case SET_AUTO_MODE:
            	if (command.value == 1) {
            		autoMode = true;
            		if (Settings.Debug.showRobotCommands) System.out.println("SET MODE - AUTO");
            	}
            	else if (command.value == 0) {
            		autoMode = false;
            		if (Settings.Debug.showRobotCommands) System.out.println("SET MODE - MANUAL");
            	}
            	else {
            		// We have a problem...
            	}
            	break;
            case SET_DEFAULT_SPEED:
                driveSpeed = (int)command.value;
                setMotorSpeeds();
                break;
            case SET_DEFAULT_TURN_RATE:
                turnSpeed = (int)command.value;
                setMotorSpeeds();
                break;
            case MANUAL_MOVE:
            	if (Settings.Debug.showRobotCommands) System.out.println("DRIVE ROBOT - MANUAL");
                drive(command.value);
                break;
            case MANUAL_TURN:
            	if (Settings.Debug.showRobotCommands) System.out.println("TURN ROBOT - MANUAL");
                turn(command.value);
                break;
            case MANUAL_TURN_COLOR_MOVER:
            	if (Settings.Debug.showRobotCommands) System.out.println("COLOR MOVER - MANUAL");
                colorMoverForever(command.value);
                break;
            case MANUAL_STOP:
            	if (Settings.Debug.showRobotCommands) System.out.println("MANUAL STOP ROBOT");
                stopDriving();
                colorMoverStop();
                break;
            case AUTO_MOVE_METRES:
            	if (Settings.Debug.showRobotCommands) System.out.println("DRIVE ROBOT - AUTO");
                queue.add(command);
                break;
            case AUTO_TURN_DEGREES:
            	if (Settings.Debug.showRobotCommands) System.out.println("TURN ROBOT - AUTO");
                queue.add(command);
                break;
            case EMERGENCY_STOP:
            	if (Settings.Debug.showRobotCommands) System.out.println("EMERGENCY STOP ROBOT");
                autoMode = false;
                stopDriving();
                colorMoverStop();
                
                interupt();
                break;
            default:
                break;
            }
        }
        updatePosition();
     
        if (autoMode)
        {
        	/* Check status of current job */
        	if (currentJob != idleJob) {
        		/* Check if goal reached. */
        		switch (currentJob.command.messageType) {
				case AUTO_MOVE_METRES:
					if (distanceRemaining <= 0)
					{
						stopDriving();
						queue.setLastFinished(currentJob.jobID);
						currentJob = idleJob;
						sendMessage(true);
					}
					break;
				case AUTO_TURN_DEGREES:
					if (turnRemaining <= 0) {
						stopDriving();
						queue.setLastFinished(currentJob.jobID);
						currentJob = idleJob;
						sendMessage(true);
					}
					break;
				default:
					break;
        		}
        	} 
        	else if (queue.hasNewJob()){
        		currentJob = queue.receive();
        		isInterupted = false;
        		switch (currentJob.command.messageType) {
				case AUTO_MOVE_METRES:
					System.out.println("Start Driving");
					drive(currentJob.command.value);
					break;
				case AUTO_TURN_DEGREES:
					System.out.println("Start Turning.");
					turn(currentJob.command.value);
					break;
				default:
					break;
        		}
        	}
//        	if (isStopping) {
//    		//System.out.println("Slowing...");
//    		if ( isDriving() && (previousDrivingDistance == motorDistance())) {
//    			previousDrivingDistance = 0;
//    			//System.out.println(leftMotor.getTachoCount());
//    	        //System.out.println(rightMotor.getTachoCount());
//    	        
//    	        leftMotor.resetTachoCount();
//    	        rightMotor.resetTachoCount();
//    	        isStopping = false;
//    	        //System.out.println("Stopped.");
//    		}
//    		if ( isTurning() && (lastAngle == readGyroSensor()) ) {
//    			//System.out.println(readGyroSensor());
//    			leftMotor.resetTachoCount();
//    	        rightMotor.resetTachoCount();
//    	        isStopping = false;
//    	        //System.out.println("Stopped.");
//    		}
//    	}
        }
        sendMessage();
//        reflex();
        if (isReacting) {
        	if (distanceRemaining <= 0)
			{
				stopDriving();
				isReacting = false;
			}
        }
    }
    
    
    /**
     * This is an initialiser to get a connection to the robot and
     * initialise the motor ports and the sensor ports.
     * Port A: Right motor
     * Port C: Arm motor
     * Port D: Left motor
     * Port 1: GyroSensor
     * Port 2: UltrasonicSensor
     * Port 3: TouchSensor
     * Port 4: ColorSensor
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    @Override
    public void connect(String IP, GUIConnectionSelect connectionWindow) throws RemoteException, MalformedURLException, NotBoundException, ConnectException {
        // Create the remote EV3
        ev3 = new RemoteEV3(IP);
        connectionWindow.setStatus("Connecting... Done!");

        // Get ports
        connectionWindow.setStatus("Setting up motors...");
        ev3.getPort("A");
        ev3.getPort("C");
        ev3.getPort("D");
        
        // Create motors
        if ( leftMotor == null ) {leftMotor = ev3.createRegulatedMotor("D", 'L'); }
        if ( rightMotor == null ) {rightMotor = ev3.createRegulatedMotor("A", 'L'); }
        if ( armMotor == null ) {armMotor = ev3.createRegulatedMotor("C", 'M'); }
        leftMotor.resetTachoCount();
        rightMotor.resetTachoCount();
        armMotor.resetTachoCount();
        connectionWindow.setStatus("Setting up motors... Done!");
        
        connectionWindow.setStatus("Setting up sensors...");
        try {
            String sensorString = "lejos.hardware.sensor.EV3";
            // Color Sensor
            connectionWindow.setStatus("Setting up colour sensor...");
            colorProvider = ev3.createSampleProvider("S1", sensorString + "ColorSensor", "RGB");
            connectionWindow.setStatus("Setting up colour sensor... Done!");
            
            // Gyro Sensor
            connectionWindow.setStatus("Setting up gyroscopic sensor... Keep robot stationary.");
            gyroProvider = ev3.createSampleProvider("S4", sensorString + "GyroSensor", "Angle");
            connectionWindow.setStatus("Setting up gyroscopic sensor... Done!");

            // Ultrasonic Sensor
            connectionWindow.setStatus("Setting up ultrasonic sensor...");
            try {
            	ultrasonicProvider = ev3.createSampleProvider("S2", sensorString + "UltrasonicSensor", "Distance");
            } catch (DeviceException e) 
            {
            	// Try again
            	System.err.println("Retrying...");
            	try {
            		ultrasonicProvider = ev3.createSampleProvider("S2", sensorString + "UltrasonicSensor", "Distance");
            	} catch (DeviceException e1)
            	{
            		connectionWindow.setStatus("Failed to create Ultrasonic Sample Provider. Closing...");
            		cleanUp();
            	}
            }
            
            connectionWindow.setStatus("Setting up ultrasonic sensor... Done!");
        } catch (Exception e) {
        		connectionWindow.setStatusError("Error:");
            e.printStackTrace();
            connectionWindow.setStatusError("Closing ports...");
            try {
            	    cleanUp();
            } catch (Exception err) {
            	    
            }
            
            connectionWindow.setStatusError("Closing ports... Done!");

            throw new RuntimeException("Robot failed to connect.");
        }
        connectionWindow.setStatus("Setting up sensors... Done!");
        setMotorSpeeds();
        
        messageCooldown = System.currentTimeMillis() - messageRate;
    }

    /**
     * A cleanup function. Closes all the motor and sensor ports.
     * @throws RemoteException
     * @author Adam, Jeremy
     */
    @Override
    public void cleanUp() throws RemoteException {
        leftMotor.close();
        rightMotor.close();
        armMotor.close();

        colorProvider.close();
        gyroProvider.close();
        ultrasonicProvider.close();
    }
    
    /**
     * Initialise the robot's state variables.
     * @param handlerColorDetector
     */
    private void init(ColorDetector handlerColorDetector)
    {
    	robotPosition = new Point(0,0);
        colorSensorPosition = new Point(0,0);
        colorSensorOffset = new Point(armPivotOffset.xMetres + armHeadOffset.xMetres,
        							  armPivotOffset.yMetres + armLength + armHeadOffset.yMetres);
        turnGoal = 0;
        distanceRemaining = 0;
        // moveCooldown = 0;
        isReacting = false;
        turnSpeed = 1;
        
        colorDetector = handlerColorDetector;
        
        queue = new RobotJobQueue();
        idleJob = queue.new Job(null, -1);
        currentJob = idleJob;
        isInterupted = false;
        interuptedJobID = -1;
    }
    
    /**
     * Constructor to initialise the motors as well as getting handles
     * for the queues. It also sets the IP address.
     * @author Adam
     */
    public Robot(HandlerToRobotQueue handlerToRobotQueue, RobotToHandlerQueue robotToHandlerQueue, ColorDetector colorDetector) throws RemoteException, MalformedURLException, NotBoundException {
        init(colorDetector);
        htrQueue = handlerToRobotQueue;
        rthQueue = robotToHandlerQueue;
    }

    private HandlerToRobotQueue htrQueue;
    private RobotToHandlerQueue rthQueue;
    
    
}