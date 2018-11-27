import static org.junit.Assert.*;

import org.junit.Test;

public class RobotToHandlerQueueTest {

    @Test
    public void test() {
    	
    	RobotToHandlerQueue q = new RobotToHandlerQueue();
    	
    	assertFalse(q.hasNewMessages());
    	assertEquals(0, q.size());
    	
    	RobotToHandlerQueue.Message message = new RobotToHandlerQueue.Message();
    	
    	message.angleDegrees = 23.3f;
    	message.colorSensorPosition = new Point(0.01f, 0.02f);
    	message.currentJob = 3;
    	message.interupted = false;
    	message.interuptedJob = 4;
    	message.lastJobCompleted = 5;
    	message.lastJobRecieved = 6;
    	message.position = new Point(0.07f,0.08f);
    	message.sensorDistanceMetres = 0.09f;
    	message.sensorRGB = new RGBColor(0.1f,0.11f,0.12f);
    	
    	q.add(message);
    	
    	assertTrue(q.hasNewMessages());
    	assertEquals(1, q.size());
    	
    	RobotToHandlerQueue.Message newMessage = q.receive();
    	assertEquals(message, newMessage);
    	assertFalse(q.hasNewMessages());
    	assertEquals(0, q.size());
    }
   
}