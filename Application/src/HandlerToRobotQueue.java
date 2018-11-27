import java.util.Queue;
import java.util.ArrayDeque;

/**
 * A wrapper around a thread-safe queue that communicates high-level actuation
 * information from the Handler module to the Robot module.
 * <p>
 * The receiver must interpret commands manually. Each command has an optional value
 * parameter associated with it. The commands are:
 * <ul>
 *  <li>SET_DEFAULT_SPEED:      Set the robot default speed to value (metres / second).
 *  <li>SET_DEFAULT_TURN_RATE:  Set the new default turn rate to value (degrees / second).
 *  <li>MANUAL_MOVE:            Move forward indefinitely (manual mode). Value is a multiplier for
 *                                the assigned default speed. value = 1.0 for forward, value = -1.0
 *                                for reverse.
 *  <li>MANUAL_TURN:            Turn indefinitely (manual mode). Value is a multiplier for the
 *                                assigned default turn rate. value =  1.0 for anti-clockwise,
 *                                value = -1.0 for clockwise.
 *  <li>MANUAL_STOP,            Stop the robot's currently-executing manual command. (value = N/A)
 *  <li>AUTO_MOVE_METRES,       Move the robot a fixed distance forward/backward.
 *                                value (metres) is negative for reverse movement.
 *  <li>AUTO_TURN_DEGREES,      Turn a fixed angle. value (degrees) is positive for anti-clockwise,
 *                                negative is clockwise.
 *  <li>EMERGENCY_STOP,         Clear the command queue completely except for the EMERGENCY_STOP
 *                                command and stop the robot. (value = N/A)
 * </ul>
 * @author jkortman
 */
public class HandlerToRobotQueue {
    // The ID of the next command id.
    private static int nextCommandID = 0;
    
    /**
     * Construct an empty queue.
     */
    public HandlerToRobotQueue() {
        queue = new ArrayDeque<Command>();
    }
    
    /**
     * Add a message to the queue telling the robot to set it's operating mode.
     * @param mode Auto is 1, Manual is 0.
     * @return                a handle for the sent command.
     */
    public int setAutoMode(float mode) {
        return sendCommand(CommandType.SET_AUTO_MODE, mode);
    }
    
    
    /**
     * Add a message to the queue telling the robot to set it's forward
     * speed. Can be provided with negative speeds to move backward.
     * @param metresPerSecond the speed at which the robot will move forward.
     * @return                a handle for the sent command.
     */
    public int setDefaultSpeed(float metresPerSecond) {
        return sendCommand(CommandType.SET_DEFAULT_SPEED, metresPerSecond);
    }
    
    /**
     * Add a message to the queue telling the robot to set it's turn rate.
     * Positive turn rate rotates the robot in a clockwise direction.
     * @param  degreesPerSecond the turn rate at which the robot will rotate.
     * @return                  a handle for the sent command.
     */
    public int setDefaultTurnRate(float degreesPerSecond) {
        return sendCommand(CommandType.SET_DEFAULT_TURN_RATE, degreesPerSecond);
    }
    /**
     * Tell the robot to move forward a fixed number of metres.
     * @param  metres the distance to move.
     * @return        a handle for the sent command.
     */
    public int moveForwardMetres(float metres) {
        return sendCommand(CommandType.AUTO_MOVE_METRES, metres);
    }
    
    /**
     * Tell the robot to rotate a fixed number of degrees.
     * Positive values rotates the robot in a clockwise direction.
     * @param  degrees the number of degrees to rotate clockwise.
     * @return         a handle for the sent command.
     */
    public int turnDegrees(float degrees) {
        return sendCommand(CommandType.AUTO_TURN_DEGREES, degrees);
    }
    
    /**
     * Send command to begin moving forward.
     * @return a handle for the sent command.
     */
    public int manualMoveForward() {
        return sendCommand(CommandType.MANUAL_MOVE, 1.0f);
    }

    /**
     * Send command to begin moving backward.
     * @return a handle for the sent command.
     */
    public int manualMoveBackward() {
        return sendCommand(CommandType.MANUAL_MOVE, -1.0f);
    }
    
    /**
     * Send command to begin rotating clockwise.
     * @return a handle for the sent command.
     */
    public int manualTurnClockwise() {
        return sendCommand(CommandType.MANUAL_TURN, -1.0f);
    }
    
    /**
     * Send command to begin rotating anti-clockwise.
     * @return a handle for the sent command.
     */
    public int manualTurnAntiClockwise() {
        return sendCommand(CommandType.MANUAL_TURN, 1.0f);
    }
    
    /**
     * Send command to rotate color mover arm clockwise.
     * @param  degrees the angle to rotate.
     * @return         a handle for the sent command.
     */
    public int manualTurnColorMoverClockwise(){
        return sendCommand(CommandType.MANUAL_TURN_COLOR_MOVER, 1.0f);
    }
    
    /**
     * Send command to rotate color mover arm anticlockwise.
     * @param  degrees the angle to rotate.
     * @return         a handle for the sent command.
     */
    public int manualTurnColorMoverAntiClockwise(){
        return sendCommand(CommandType.MANUAL_TURN_COLOR_MOVER, -1.0f);
    }
    
    /**
     * Tell the robot to stop moving if it is currently moving.
     * @return a handle for the sent command.
     */
    public int manualStop() {
        return sendCommand(CommandType.MANUAL_STOP, 0.0f);
    }
    
    /**
     * Tell the robot to make an emergency stop, purging the queue
     * in the process.
     * @return a handle for the sent command.
     */
    public int emergencyStop() {
        queue.clear();
        return sendCommand(CommandType.EMERGENCY_STOP, 0.0f);
    }
    
    /**
     * Take an message from the message queue. Returns null if no messages are
     * present in the queue.
     * @return the command at the end of the queue.
     */
    public Command receive() {
        return queue.poll();
    }
    
    /**
     * Check if any messages are present in the queue.
     */
    public boolean hasNewMessages() {
        return queue.peek() != null;
    }
    
    // Commands are sent with a value parameter. The meaning of the value parameter is explained
    // to the right of each listed command. 'N/A' is used when the value has no meaning for that
    // command.
    public enum CommandType {
        /* Configuration commands used to set the default speed and turn rate of the robot. */
        SET_DEFAULT_SPEED,      // value: the new default speed (metres / second).
        SET_DEFAULT_TURN_RATE,  // value: the new default turn rate (degrees / second).
        SET_AUTO_MODE,     // value: MANUAL or AUTO to tell the robot what mode it should operate in.
        /* Manual mode control commands. 'MANUAL_STOP' command should be used to cancel these commands.
         * While using these commands, any received autononomous mode commands will cause the vehicle
         * to immediately stop, send an 'interrupted' message for the manual command being executed
         * (when implemented), then execute the autonomous mode mode command. */
        MANUAL_MOVE,            // value: A multiplier. 1.0 for forward, -1.0 for reverse.
        MANUAL_TURN,            // value: A multiplier. 1.0 for anti-clockwise, -1.0 for clockwise.
        MANUAL_STOP,            // value: N/A. Stop the robot's currently-executing manual command.
        /* Autonomous mode commands. */
        AUTO_MOVE_METRES,       // value: the distance to move (metres). Negative for reverse.
        AUTO_TURN_DEGREES,      // value: the degrees to rotate. Positive is anti-clockwise, negative
                                // is clockwise.
        MANUAL_TURN_COLOR_MOVER,     // value: The angle to move the color sensor arm
        EMERGENCY_STOP,         // value: N/A. Stop the robot.
    }
    
    /**
     * A message sent from the Handler to the Robot, communicating a message
     * type and an optional value.
     * @author jkortman
     */
    public class Command {
        /**
         * Create a message with a message type and value.
         * Should use helper functions like setSpeed() rather than this directly.
         * @param m the message type
         * @param v the optional value of the message
         */
        public Command(CommandType commandType, float value, int id) {
            this.messageType = commandType;
            this.value = value;
            this.id = id;
        }
        public final CommandType messageType;
        public final float value;
        public final int id;
    }
    
    /**
     * Generate a new command id.
     */
    private void newCommandID() {
        nextCommandID += 1;
        if (nextCommandID < 0) nextCommandID = 1;
    }
    
    /**
     * Send a command, generating a new command id for it in the process.
     * @param  commandType The command type to send.
     * @param  value       The optional value to associate with the command.
     * @return             The id of the send command.
     */
    private int sendCommand(CommandType commandType, float value) {
        newCommandID();
        queue.add(new Command(commandType, value, nextCommandID));
        return nextCommandID;
    }
    
    // The internal queue of commands.
    private Queue<Command> queue;
}
