import static org.junit.Assert.*;

import org.junit.Test;

public class HandlerToRobotQueueTest {

    @Test
    public void test() {
        float[] testValues = {-2.0f, -0.1f, 0.0f, 0.001f, 0.1f, 0.5f, 1.0f, 5.0f, 100.0f};
        HandlerToRobotQueue q = new HandlerToRobotQueue();
        assertFalse(q.hasNewMessages());
        
        // Test varying-valued messages.
        HandlerToRobotQueue.CommandType[] varied_value_messages = {
                HandlerToRobotQueue.CommandType.SET_DEFAULT_SPEED,
                HandlerToRobotQueue.CommandType.SET_DEFAULT_TURN_RATE,
                HandlerToRobotQueue.CommandType.AUTO_MOVE_METRES,
                HandlerToRobotQueue.CommandType.AUTO_TURN_DEGREES
        };
        for (float expectedValue : testValues) {
            for (HandlerToRobotQueue.CommandType messageType : varied_value_messages) {
                String functionName = "None";
                switch (messageType) {
                case SET_DEFAULT_SPEED:
                    q.setDefaultSpeed(expectedValue);
                    functionName = "setDefaultSpeed";
                    break;
                case SET_DEFAULT_TURN_RATE:
                    q.setDefaultTurnRate(expectedValue);
                    functionName = "setDefaultTurnRate";
                    break;
                case AUTO_MOVE_METRES:
                    q.moveForwardMetres(expectedValue);
                    functionName = "moveForwardMetres";
                    break;
                case AUTO_TURN_DEGREES:
                    q.turnDegrees(expectedValue);
                    functionName = "turnDegrees";
                    break;
                default:
                    throw new RuntimeException();
                }
                testReceiveValue(q, messageType, expectedValue, functionName);
            }
        }
        
        // Test specifically fixed-value messages.
        q.manualMoveForward();
        testReceiveValue(q, HandlerToRobotQueue.CommandType.MANUAL_MOVE, 1.0f, "manualMoveForward");
        q.manualMoveBackward();
        testReceiveValue(q, HandlerToRobotQueue.CommandType.MANUAL_MOVE, -1.0f, "manualMoveForward");
        q.manualTurnAntiClockwise();
        testReceiveValue(q, HandlerToRobotQueue.CommandType.MANUAL_TURN, 1.0f, "manualMoveForward");
        q.manualTurnClockwise();
        testReceiveValue(q, HandlerToRobotQueue.CommandType.MANUAL_TURN, -1.0f, "manualMoveForward");
    
        // Test value-ignoring messages.
        q.manualStop();
        testReceiveMessageType(q, HandlerToRobotQueue.CommandType.MANUAL_STOP, "manualStop");
        q.emergencyStop();
        testReceiveMessageType(q, HandlerToRobotQueue.CommandType.EMERGENCY_STOP, "emergencyStop");
        
    }
   
    private void testReceiveMessageType(HandlerToRobotQueue q, HandlerToRobotQueue.CommandType expectedType, String function) {
        assertTrue(q.hasNewMessages());
        HandlerToRobotQueue.Command command = q.receive();
        assertEquals(String.format("%s: message type mismatch %s!=%s",
                                   function, expectedType, command.messageType),
                     command.messageType, expectedType);
        assertFalse(q.hasNewMessages());
    }
    
    private void testReceiveValue(HandlerToRobotQueue q, HandlerToRobotQueue.CommandType expectedType, float expectedValue, String function) {
        assertTrue(q.hasNewMessages());
        HandlerToRobotQueue.Command command = q.receive();
        assertEquals(String.format("%s: message type mismatch %s!=%s",
                                   function, expectedType, command.messageType),
                     command.messageType, expectedType);
        assertEquals(String.format("%s: value mismatch %f!=%f (type %s)",
                                   function, expectedValue, command.value, command.messageType),
                command.value, expectedValue, 0.001f);
        assertFalse(q.hasNewMessages());
    }

}
