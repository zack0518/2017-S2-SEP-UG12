import static org.junit.Assert.*;

import org.junit.Test;

public class RobotJobQueueTest {

    @Test
    public void test() {
        float[] testValues = {-2.0f, -0.1f, 0.0f, 0.001f, 0.1f, 0.5f, 1.0f, 5.0f, 100.0f};
        RobotJobQueue q = new RobotJobQueue();
        assertFalse(q.hasNewJob());
        assertEquals(-1,q.getLastFinished());
        assertEquals(-1,q.getLastJob());
        
        // Test varying-valued messages.
        HandlerToRobotQueue.CommandType[] varied_value_messages = {
                HandlerToRobotQueue.CommandType.AUTO_MOVE_METRES,
                HandlerToRobotQueue.CommandType.AUTO_TURN_DEGREES
        };
        
        HandlerToRobotQueue htrQueue = new HandlerToRobotQueue();        
        
        for (float expectedValue : testValues) {
            for (HandlerToRobotQueue.CommandType messageType : varied_value_messages) {
                String functionName = "None";
                switch (messageType) {
                case AUTO_MOVE_METRES:
                	htrQueue.moveForwardMetres(expectedValue);
                	q.add(htrQueue.receive());
                    functionName = "moveForwardMetres";
                    break;
                case AUTO_TURN_DEGREES:
                    htrQueue.turnDegrees(expectedValue);
                    q.add(htrQueue.receive());
                    functionName = "turnDegrees";
                    break;
                default:
                    throw new RuntimeException();
                }
                
                testReceiveValue(q, messageType, expectedValue, functionName);
            }
        }
        
        htrQueue.moveForwardMetres(0.1f);
        htrQueue.turnDegrees(20);
        htrQueue.moveForwardMetres(-0.2f);
        htrQueue.turnDegrees(-30);
        q.add(htrQueue.receive());
        q.add(htrQueue.receive());
        q.add(htrQueue.receive());
        q.add(htrQueue.receive());
        
        assertTrue(q.hasNewJob());
        assertEquals(4, q.size());
       
        q.clear();
        
        assertFalse(q.hasNewJob());
        assertEquals(0, q.size());
                
    }
   
    
    private void testReceiveValue(RobotJobQueue q, HandlerToRobotQueue.CommandType expectedType, float expectedValue, String function) {
        assertTrue(q.hasNewJob());
        assertEquals(1, q.size());
        RobotJobQueue.Job job = q.receive();
        HandlerToRobotQueue.Command command = job.command;
        assertEquals(String.format("%s: message type mismatch %s!=%s",
                                   function, expectedType, command.messageType),
                     command.messageType, expectedType);
        assertEquals(String.format("%s: value mismatch %f!=%f (type %s)",
                                   function, expectedValue, command.value, command.messageType),
                command.value, expectedValue, 0.001f);
        assertFalse(q.hasNewJob());
        assertEquals(0, q.size());
        q.setLastFinished(job.jobID);
    }

}
