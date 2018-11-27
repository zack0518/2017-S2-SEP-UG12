import static org.junit.Assert.*;

import org.junit.Test;

public class HandlerTest {
	UIEventQueue uiEventQueue;
	HandlerToRobotQueue handlerToRobotQueue;
	RobotToHandlerQueue robotToHandlerQueue;
	Handler handler;
	
	public HandlerTest() {
		/**
		 * Create queues
		 */
		uiEventQueue = new UIEventQueue();
		handlerToRobotQueue = new HandlerToRobotQueue();
		robotToHandlerQueue = new RobotToHandlerQueue();
		
		handler = new Handler(uiEventQueue, 
				handlerToRobotQueue, 
				robotToHandlerQueue);
	}

	@Test
	public void processUIEventTestForwardPressed() {
		pushUIEvent(UIEventQueue.EventType.FORWARD_PRESSED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.MANUAL_MOVE, command.messageType);
		assertEquals(1.0f, command.value, 0.001);
	}
	
	@Test
	public void processUIEventTestForwardReleased() {
		pushUIEvent(UIEventQueue.EventType.FORWARD_RELEASED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.MANUAL_STOP, command.messageType);
		assertEquals(0.0f, command.value, 0.001);
	}
	
	@Test
	public void processUIEventTestBackwardPressed() {
		pushUIEvent(UIEventQueue.EventType.BACKWARD_PRESSED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.MANUAL_MOVE, command.messageType);
		assertEquals(-1.0f, command.value, 0.001);
	}
	
	@Test
	public void processUIEventTestBackwardReleased() {
		pushUIEvent(UIEventQueue.EventType.BACKWARD_RELEASED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.MANUAL_STOP, command.messageType);
		assertEquals(0.0f, command.value, 0.001);
	}
	
	@Test
	public void processUIEventTestTurnAntiClockwisePressed() {
		pushUIEvent(UIEventQueue.EventType.TURN_ANTICLOCKWISE_PRESSED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.MANUAL_TURN, command.messageType);
		assertEquals(1.0f, command.value, 0.001);
	}
	
	@Test
	public void processUIEventTestTurnAntiClockwiseReleased() {
		pushUIEvent(UIEventQueue.EventType.TURN_ANTICLOCKWISE_RELEASED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.MANUAL_STOP, command.messageType);
		assertEquals(0.0f, command.value, 0.001f);
	}
	
	@Test
	public void processUIEventTestEmergencyStop() {
		pushUIEvent(UIEventQueue.EventType.EMERGENCY_STOP_PRESSED);
		handler.step();
		HandlerToRobotQueue.Command command = popRobotCommand();
		assertEquals(HandlerToRobotQueue.CommandType.EMERGENCY_STOP, command.messageType);
		assertEquals(0.0f, command.value, 0.01f);
	}
	
	public void pushUIEvent(UIEventQueue.EventType event) {
		UIEventQueue.EventType eventType = event;
		uiEventQueue.add(eventType);
	}
	
	public RobotToHandlerQueue.Message createRTHMessage(Point position,
													   float angleDegrees,
													   float sensorDistanceMetres) {
		RobotToHandlerQueue.Message message = new RobotToHandlerQueue.Message();
		message.position = position;
		message.angleDegrees = angleDegrees;
		message.sensorDistanceMetres = sensorDistanceMetres;
		return message;
	}
	
	public void pushToRobotToHandlerQueue(RobotToHandlerQueue.Message message) {
		robotToHandlerQueue.add(message);
	}
	
	public HandlerToRobotQueue.Command popRobotCommand() {
		return handlerToRobotQueue.receive();
	}

}
