import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayDeque;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;

/**
 * A module that translate graphical user interface events(such as button press) to high-level
 * commands that are sent to a RobotMessenger interface. Handles the processing of data coming
 * from the robot's sensor and displays a real-time map on the graphical user interface.
 * It uses modules HandlerToRobotQueue and RobotToHandlerQueue to send high-level commands and
 * receive sensor data to/from the robot.
 * @author cyrusvillacampa jkortman
 */
public class Handler implements HandlerDisplayInterface {
    private final float DEFAULT_SPEED = 0.03f;	// metres per second
    private final float DEFAULT_TURN_RATE = 2.0f;	// degrees per second
    private final float DEFAULT_DISTANCE = 0.05f;	// metres
    private final float DEFAULT_TURN_ANGLE = 10.0f;	// degrees
    public Map map;
    public int rows,cols;
    public float GridSize;
    public ArrayList <Point> MapFeatures;
    private Point robotPosition;
    private Point colorSensorPosition;
    private float turnAngleDegrees;
    private ColorDetector colorDetector;
    private ColorInterpreter colorInterpreter;
    private DistanceSensorInterpreter distanceSensorInterpreter;
    private NoGoZonesMarker ngzMarker;
    private DecisionMaker decisionMaker;
    private PointToCommandParameterConverter ptcParameterConverter;
    private Deque<Integer> existingJobs;		// Stores the id's of the jobs/commands sent to robot that has not been executed by the robot
    // FOR DEBUGGING
    private List<PointToCommandParameterConverter.AngleDistance> angleDistanceListDebug;
    private List<Point> robotPositionList;
    /**
     * Store high-level commands that will be sent to the robot.
     */
    public HandlerToRobotQueue handlerToRobotQueue;
    /**
     * Store sensor data sent from the robot to be processed.
     */
    public RobotToHandlerQueue robotToHandlerQueue;
    /**
     * Store the robot commands translated from pressed buttons in
     * the graphical user interface.
     */
    public UIEventQueue uiEventQueue;
    /**
     * Store the current control mode(manual/autonomous) of the robot.
     */
    public UIEventQueue.EventType mode;
    /**
     * A flag that signals the application to terminate all running program
     */
    private Boolean exitFlag = false;
    
    /**
     * 
     * @param uiEventQueue
     * @param handlerToRobotQueue
     * @param robotToHandlerQueue
     */
    public Handler(UIEventQueue uiEventQueue,
                   HandlerToRobotQueue handlerToRobotQueue,
                   RobotToHandlerQueue robotToHandlerQueue) {
        this.init(uiEventQueue,
                  handlerToRobotQueue,
                  robotToHandlerQueue);
    }
    
    /**
     * Initialize the Handler. Groups shared code for constructors.
     */
    private void init(UIEventQueue uiEventQueue,
                      HandlerToRobotQueue handlerToRobotQueue,
                      RobotToHandlerQueue robotToHandlerQueue) {
        this.uiEventQueue = uiEventQueue;
        this.handlerToRobotQueue = handlerToRobotQueue;
        this.robotToHandlerQueue = robotToHandlerQueue;
        mode = UIEventQueue.EventType.MANUAL;	// Initialize robot to manual control
        rows = Settings.Map.rows;
        cols = Settings.Map.cols;
        GridSize = Settings.Map.gridSize;
        this.map = new Map(GridSize,     // 1cm grid size
        					rows,    // 119cm is the maximum edge length of an A0 sheet.
        					cols);
        this.robotPosition = new Point(0.0f, 0.0f);
        this.colorSensorPosition = new Point(0.0f, 0.0f);
        
        // Set up color detector.
        HashMap<ColorDetector.Color, RGBColor> colorDetectorReference = new HashMap<ColorDetector.Color, RGBColor>();
        colorDetectorReference.put(ColorDetector.Color.WHITE,  Settings.ReferenceColors.white);
        colorDetectorReference.put(ColorDetector.Color.BLACK,  Settings.ReferenceColors.black);
        colorDetectorReference.put(ColorDetector.Color.GREEN,  Settings.ReferenceColors.green);
        colorDetectorReference.put(ColorDetector.Color.PURPLE, Settings.ReferenceColors.purple);
        colorDetectorReference.put(ColorDetector.Color.BLUE,   Settings.ReferenceColors.blue);
        this.colorDetector = new ColorDetector(colorDetectorReference,
                                               ColorDetector.Color.WHITE,
                                               Settings.allowedColorMSE,
                                               Settings.allowedGrayscaleError);
        // Set up color interpreter.
        HashMap<ColorDetector.Color, Map.Property> colorInterpreterKey = new HashMap<ColorDetector.Color, Map.Property>();
        colorInterpreterKey.put(ColorDetector.Color.WHITE,  Map.Property.NONE);
        colorInterpreterKey.put(ColorDetector.Color.BLACK,  Map.Property.CRATER);
        colorInterpreterKey.put(ColorDetector.Color.GREEN,  Map.Property.RADIATION);
        colorInterpreterKey.put(ColorDetector.Color.PURPLE, Map.Property.TRACKS);
        colorInterpreterKey.put(ColorDetector.Color.BLUE,   Map.Property.BORDER);
        this.colorInterpreter = new ColorInterpreter(colorInterpreterKey);
        this.distanceSensorInterpreter = new DistanceSensorInterpreter();
        this.ngzMarker = new NoGoZonesMarker(this.map);
        this.decisionMaker = new DecisionMaker(this.map);
        this.ptcParameterConverter = new PointToCommandParameterConverter();
        this.existingJobs = new ArrayDeque<>();
        if (Settings.Debug.usePresetMap) {
            loadMapData();
        }
        // FOR DEBUGGING
        this.angleDistanceListDebug = new ArrayList<>();
        this.robotPositionList = new ArrayList<>();
    }
    
    /**
     * Set the mode(manual/autonomous) of the robot
     * @param mode - one of the values of the enum EventType
     */
    public void setMode(UIEventQueue.EventType mode) {
        this.mode = mode;
    }
    
    /**
     * This method is the implementation of the autonomous mode.
     */
    public void autonomousModeStep() {
    		boolean emergencyStopPressedFlag = false;
        // Check if the EMERGENCY_STOP or MANUAL button is pressed
        if (uiEventQueue.checkEvent(new UIEventQueue().new UIEvent(
                                                                   UIEventQueue.EventType.EMERGENCY_STOP_PRESSED)) ||
            uiEventQueue.checkEvent(new UIEventQueue().new UIEvent(
                                                                   UIEventQueue.EventType.MANUAL))) {
            handlerToRobotQueue.emergencyStop();
            // Set the robot mode back to MANUAL to prevent sending commands to robot until the 
            // operator decide to.
            setMode(UIEventQueue.EventType.MANUAL);
            handlerToRobotQueue.setAutoMode(0);
            // Clear the queue in case if the operator has pressed some buttons(forward, back, 
            // left/right rotate) on the UI while in AUTO mode.
            uiEventQueue.clear();
            emergencyStopPressedFlag = true;
        }
        
        // Process sensor data from the robot
        processSensorData();
        // Process event from GUI
        processUIEvent();
        // Make the robot move to the next position
        if (!emergencyStopPressedFlag) {
        		// FOR DEBUGGING PURPOSES
        		if (Settings.Debug.showCommandParameters) {
        			if (uiEventQueue.getDestination() != null) {
            			try {
    						map.set(Map.Property.TRACKS, uiEventQueue.getDestination(), 1.0f);
    					} catch (Map.OutOfMapBoundsException e) {
    						e.printStackTrace();
    					}
            		}
//            		System.out.println("Destination point: " + uiEventQueue.getDestination());
//            		System.out.println("Robot position: " + this.robotPosition.toString());
            		if (uiEventQueue.getDestination() != null) {
//            			Map.GridLocation loc = uiEventQueue.getDestination();
//                		System.out.printf("Destination grid loc(row,col): (%d,%d)%n", loc.row, loc.col);
//                		loc = map.getGridLocation(this.robotPosition);
//                		System.out.printf("Robot grid loc(row,col): (%d,%d)%n", loc.row, loc.col);
//            			decisionMaker.printFoundPath();
            		}
//            		System.out.print("Command parameters: ");
//            		for (PointToCommandParameterConverter.AngleDistance angleD: this.angleDistanceListDebug) {
//            			System.out.printf("%s", angleD);
//            		}
//            		System.out.println("");
//            		System.out.print("Robot positions: ");
//            		for (Point robotPos: this.robotPositionList) {
//            			System.out.printf("%s", robotPos.toString());
//            		}
//            		System.out.println("");
        		}
        		
        		if (existingJobs.isEmpty()) {			// There are no more existing jobs
        			moveToNextPosition();
        		}
        }
    }
    
    /**
     * Process an event that belongs to the set of auto UI event.
     * @param event - The event to be processed
     */
    private void processAutoUIEvent(UIEventQueue.UIEvent event) {
    		// Translate event to command and send command to robot
        switch (event.eventType) {
            case DESTINATION_CREATED:
            		existingJobs.add(handlerToRobotQueue.turnDegrees(360));
            		existingJobs.add(handlerToRobotQueue.turnDegrees(-360));
            		Map.GridLocation destinationPoint = uiEventQueue.getDestination();
                this.decisionMaker.findPath(robotPosition, destinationPoint);
                System.out.printf("DESTINATION CREATED%n");
                break;
            default:
            		System.err.printf("Invalid robot command: '%s'%n"
            						+ "Command not supported in auto mode.", 
            						event.eventType.name());
            		break;
        }
    }
    
    /**
     * Takes the top message(if it is not empty) in the handlerToRobotQueue and process it or invokes
     * a method and passes the event to process it.
     */
    private void processUIEvent() {
        if (uiEventQueue.hasNewEvents()) {
            // Remove head of queue
            UIEventQueue.UIEvent event = uiEventQueue.receive();
            // Translate event to command and send command to robot
            switch (event.eventType) {
                case MANUAL:
                    setMode(UIEventQueue.EventType.MANUAL);
                    handlerToRobotQueue.setAutoMode(0);
                    System.out.printf("MANUAL PRESSED%n");
                    break;
                case AUTO:
                    setMode(UIEventQueue.EventType.AUTO);
                    handlerToRobotQueue.emergencyStop();
                    handlerToRobotQueue.setAutoMode(1);
                    System.out.printf("AUTO PRESSED%n");
                    break;
                case CLOSE:
                    setExitFlag(true);
                    handlerToRobotQueue.emergencyStop();
                    System.out.printf("CLOSE PRESSED%n");
                    break;
                case ADD_NO_GO_ZONES:
                		Map.GridLocation startPoint = uiEventQueue.getNoGoZoneStart();
                		Map.GridLocation endPoint = uiEventQueue.getNoGoZoneEnd();
                		ngzMarker.mark(startPoint, endPoint);
                		System.out.printf("NO GO ZONE ADDED%n");
                		break;
                case NONE:
                    break;
                default:
                    if (this.mode == UIEventQueue.EventType.AUTO) {	// Robot is on auto mode
                    		processAutoUIEvent(event);
                    } else {											// Robot is on manual mode
                    		processManualUIEvent(event);
                    }
                    break;
            }
        }
    }
    
    /**
     * Moves the robot to the next position in the found path
     */
    private void moveToNextPosition() {
		if (!decisionMaker.hasReached()) {	// The destination point has not been reached
    			Point nextPos = decisionMaker.getNextPosition();
    			if (nextPos == null) { 			// A path does not exist
    				System.out.println("Destination is blocked");
    				return;
    			}
    			sendMoveCommand(nextPos);
    			return;
    		}
    }
    
    /**
     * Sends the commands to move to a given point
     * @param point	- A point on the map on where the robot would move.
     */
    private void sendMoveCommand(Point point) {
    		System.out.printf("Robot position given to PTC: %s%n", this.robotPosition);
    		PointToCommandParameterConverter.AngleDistance commandParameters = 
    				this.ptcParameterConverter.convert(point, this.robotPosition, this.turnAngleDegrees);
    		
    		// Add jobs into the queue of existing jobs
    		existingJobs.add(this.handlerToRobotQueue.turnDegrees(commandParameters.angle));
    		
    		System.out.printf("TURN %f DEGREES COMMAND SENT [%d]%n", commandParameters.angle, existingJobs.peek());
    		existingJobs.add(this.handlerToRobotQueue.moveForwardMetres(commandParameters.distance));
    		System.out.printf("MOVE %f M FORWARD COMMAND SENT [%d]%n", commandParameters.distance, existingJobs.peek());
    		
    		if (Settings.Debug.showCommandParameters) {
    			this.angleDistanceListDebug.add(commandParameters);
        		this.robotPositionList.add(this.robotPosition);
    		}
    }
    
    public Map map() {
        return this.map;
    }
    
    /**
     * This method is the implementation of the manual mode. It takes an event one
     * at a time from the head of the UI event queue and process it.
     */
    public void manualModeStep() {
        // Check if the EMERGENCY_STOP button is pressed
        if (uiEventQueue.checkEvent(new UIEventQueue().new UIEvent(
                                                                   UIEventQueue.EventType.EMERGENCY_STOP_PRESSED))) {
            System.out.println("STOP HERE");
            handlerToRobotQueue.emergencyStop();
            // Clear the queue in case if the operator has pressed multiple buttons(forward, back, 
            // left/right rotate) on the UI before pressing the emergency stop.
            	uiEventQueue.clear();
        }
        
        // Process sensor from the robot
        processSensorData();
        // Process UI event from the GUI
        processUIEvent();
    }
    
    /**
     * Process an event that belongs to the set of manual UI event.
     * @param event - The event to be processed
     */
    public void processManualUIEvent(UIEventQueue.UIEvent event) {
        int id = -1;
        // Translate event to command and send command to robot
        switch (event.eventType) {
            case FORWARD_PRESSED:
                id = handlerToRobotQueue.manualMoveForward();
                if (Settings.Debug.showManualCommands) System.out.printf("FORWARD PRESSED [%d]%n", id);
                break;
            case FORWARD_RELEASED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("FORWARD RELEASED [%d]%n", id);
                break;
            case BACKWARD_PRESSED:
                id = handlerToRobotQueue.manualMoveBackward();
                if (Settings.Debug.showManualCommands) System.out.printf("BACKWARD PRESSED [%d]%n", id);
                break;
            case BACKWARD_RELEASED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("BACKWARD RELEASED [%d]%n", id);
                break;
            case TURN_CLOCKWISE_PRESSED:
                id = handlerToRobotQueue.manualTurnClockwise();
                if (Settings.Debug.showManualCommands) System.out.printf("TURN CLOCKWISE PRESSED [%d]%n", id);
                break;
            case TURN_CLOCKWISE_RELEASED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("TURN CLOCKWISE RELEASED [%d]%n", id);
                break;
            case TURN_ANTICLOCKWISE_PRESSED:
                id = handlerToRobotQueue.manualTurnAntiClockwise();
                if (Settings.Debug.showManualCommands) System.out.printf("TURN ANTICLOCKWISE PRESSED [%d]%n", id);
                break;
            case TURN_ANTICLOCKWISE_RELEASED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("TURN ANTICLOCKWISE RELEASED [%d]%n", id);
                break;
            case SENSOR_LEFT_PRESSED:
                id = handlerToRobotQueue.manualTurnColorMoverAntiClockwise();
                if (Settings.Debug.showManualCommands) System.out.printf("SENSOR_LEFT_PRESSED [%d]%n", id);
                break;
            case SENSOR_LEFT_RELEASED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("SENSOR_LEFT_RELEASED [%d]%n", id);
                break;
            case SENSOR_RIGHT_PRESSED:
                id = handlerToRobotQueue.manualTurnColorMoverClockwise();
                if (Settings.Debug.showManualCommands) System.out.printf("SENSOR_RIGHT_PRESSED [%d]%n", id);
                break;
            case SENSOR_RIGHT_RELEASED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("SENSOR_RIGHT_RELEASED [%d]%n", id);
                break;
            case STOP_PRESSED:
                id = handlerToRobotQueue.manualStop();
                if (Settings.Debug.showManualCommands) System.out.printf("STOP PRESSED [%d]%n", id);
                break;
            default:
                System.err.printf("Invalid robot command: '%s'%n"
                				    + "Command not supported in manual mode.", 
                					event.eventType.name());
                break;
        }
    }
    
    /**
     * Take the top message(if it is not empty) in the robotToHandlerQueue and process it.
     */
    public void processSensorData() {
        if (robotToHandlerQueue.hasNewMessages()) {
            RobotToHandlerQueue.Message data = robotToHandlerQueue.receive();
            updateMap(data);
            updateExistingJobs(data);
        }
    }
    
    /**
     * Update the map using the received sensor data
     * @param data - sensor data
     */
    public void updateMap(RobotToHandlerQueue.Message data) {
        if (Settings.Debug.showColorSensor) {
            System.out.printf(
                "Received color sensor data: pos (%.3f, %.3f), RGB (%.3f, %.3f, %.3f)%n", 
                data.colorSensorPosition.xMetres, data.colorSensorPosition.yMetres,
                data.sensorRGB.r, data.sensorRGB.g, data.sensorRGB.b); 
        }
    	
        // Update robot position.
        updateRobotPosition(data.position);
        updateColorSensorPosition(data.colorSensorPosition);
        turnAngleDegrees=data.angleDegrees;
        
        // Interpret distance sensor.
        distanceSensorInterpreter.interpret(data.sensorDistanceMetres,
                                            map,
                                            robotPosition,
                                            data.angleDegrees);
        // Interpret color sensor.
        try {
            ColorDetector.Color color = colorDetector.detect(data.sensorRGB);
            colorInterpreter.interpret(color,
                                       map,
                                       colorSensorPosition);
        } catch (Map.OutOfMapBoundsException e) {
            // Pass.
        }
    }
    
    /**
     * Updates the existing jobs by removing jobs from the queue 
     * that has already been executed by the robot or if the command
     * was interrupted due to detected obstacle or crater.
     * @param data	- The message from the robot that contains 
     * 				  the information if a job has been executed
     */
    private void updateExistingJobs(RobotToHandlerQueue.Message data) {
    		if (!existingJobs.isEmpty() && data.currentJob == -1) {
    			existingJobs.pop();
    		}
    }
    
    /**
     * Set the value of the exitFlag. This would notify the application to terminate the program if exitFlag
     * is set to true, otherwise continue running the program.
     * @param val	- True if program wants to be terminated, false otherwise
     */
    public void setExitFlag(boolean val) {
        exitFlag = val;
    }
    
    /**
     * Return the value of the exitFlag
     * @return exitFlag
     */
    public boolean getExitFlag() {
        return exitFlag.booleanValue();
    }
    
    /**
     * This method checks the current mode(manual or autonomous) of the robot and invokes
     * a method that step through necessary actions to be performed.
     */
    public void step() {
        switch (mode) {
            case AUTO:
                autonomousModeStep();
                break;
            case MANUAL:
                manualModeStep();
                break;
            default:
                System.err.println("Invalid mode: " + mode);
                // Some Lunarian is controlling our robot!
                break;
        }
    }
    
    /**
     * Returns a reference to the ColorInterpreter.
     * Intended for use by robot
     */
    public ColorDetector getColorDetector() {
    	return colorDetector;
    }
    
    /**
     * Get the robot position the in Map coordinate system.
     * @return the position of the robot.
     */
    public Point getRobotPosition() {
        return robotPosition;
    }
    /**
     * Get the color sensor position the in Map coordinate system.
     * @return the position of the robot.
     */
    public Point getColorSensorPosition() {
        return colorSensorPosition;
    }
    
    /**
     * Get the angle degree
     * @param angleDegrees
     */
    public float getAngleDegree(){
        return turnAngleDegrees;
    }
    /**
     * Update the angle degree
     * @param angleDegrees
     */
    public void updateAngleDegrees(float newAngleDegree){
        turnAngleDegrees = newAngleDegree;
    }
    
    /**
     * Update the position of the robot.
     * @param newPosition the new position of the robot
     *                    in map coordinate system.
     */
    public void updateRobotPosition(Point newPosition) {
        if (Settings.Debug.showLocation) {
            System.out.printf(
                    "Updated robot position to (%.3f, %.3f, %f)%n", 
                    newPosition.xMetres, newPosition.yMetres, getAngleDegree()); 
        }
        robotPosition = newPosition;
    }
    
    /**
     * Update the color sensor position of the robot.
     * @param newColorSensorPosition
     */
    public void updateColorSensorPosition(Point newColorSensorPosition) {
        colorSensorPosition = newColorSensorPosition;
    }
    

    public void loadMapData(){
        try {
            final Point[] Obstacles = {
            	    new Point(0.01f, 0.02f),
            	  
                    
            };
            
            final Point[] Craters = {
            		new Point(-0.1f, -0.05f),
                    new Point(-0.17f, -0.05f),
                    new Point(-0.17f, -0.1f),
                    new Point(-0.14f, -0.13f),
                    new Point(-0.1f, -0.1f),
            };
            
            final Point[] FootSteps = {
            		new Point(-0.12f, 0.02f),
                    new Point(-0.09f, 0.15f),
                    new Point(-0.09f, 0.17f),
                    new Point(-0.11f, 0.14f),
                	new Point(0.12f, 0.02f),
                    new Point(0.09f, 0.15f),
                    new Point(0.09f, 0.17f),
                    new Point(0.11f, -0.14f),
                    new Point(0.09f, -0.15f),
                    new Point(0.09f, -0.17f),
                    new Point(0.11f, -0.14f),
            };
            final Point[] Tracks = {
            		new Point(0.13f, 0.12f),
                    new Point(0.12f, 0.17f),
                    new Point(0.15f, 0.15f),
                    new Point(0.03f, 0.12f),
            };
            final Point[] Radiations = {
            		new Point(-0.05f, 0.03f),
                    new Point(- 0.10f, 0.03f),
                    new Point(- 0.10f, 0.15f),
                    new Point(- 0.05f, 0.15f),
            };

            for (Point Obstacle : Obstacles) {
            	map.set(Map.Property.OBSTACLE, Obstacle, 1.0f);
            }
            for (Point FootStep :  FootSteps) {
            	map.set(Map.Property.TRACKS, FootStep, 1.0f);
            }
            for (Point Crater :  Craters) {
            	map.set(Map.Property.CRATER, Crater, 1.0f);
            }
            for (Point Track  :  Tracks ) {
            	map.set(Map.Property.TRACKS, Track, 1.0f);
            }
            for (Point Radiation  :  Radiations ) {
            	map.set(Map.Property.RADIATION, Radiation, 1.0f);
            }
    	} catch (Map.OutOfMapBoundsException e) {
    			System.out.println("out of bound");
    	}
       // addColor();
    }
}
