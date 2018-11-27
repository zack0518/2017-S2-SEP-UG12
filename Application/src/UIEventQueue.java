import java.util.Queue;
import java.util.ArrayDeque;

/**
 * A wrapper around a thread-safe queue used to pass messages from a UI module
 * to a Handler module.
 * @author jkortman
 */
public class UIEventQueue {
    /**
     * Construct an empty queue.
     */
    public UIEventQueue() {
        queue = new ArrayDeque<UIEvent>();
    }
    
    /**
     * Add a UI event to the message queue given an event type.
     * @param eventType the type of the UI event to add to the queue
     */
    public void add(EventType eventType) {
        queue.add(new UIEvent(eventType));
    }
    
    /**
     * Take an event from the event queue. Returns null if no messages are
     * present in the queue.
     */
    public UIEvent receive() {
        return queue.poll();
    }
    
    /**
     * Check if any messages are present in the queue.
     */
    public boolean hasNewEvents() {
        return queue.peek() != null;
    }
    
    /**
     * Check if a particular event has occurred
     * @author cyrusvillacampa
     * @since Added on 25/8/2017
     */
    public boolean checkEvent(UIEvent event) {
    		return queue.contains(event);
    }
    
    /**
     * Remove all stored events in the queue
     * @author cyrusvillacampa
     * @since Added on 25/8/2017
     */
    public void clear() {
    		queue.clear();
    }
    
    /**
     * Added by Ziang Chen
     * receive the destination created by user
     * @return
     */
    public void receiveDestination(Map.GridLocation destinationPoint){
    	this.destinationPoint = new Map.GridLocation(destinationPoint.row, destinationPoint.col);
    }
    
    /**
     * receive the start point of the shape of no go zone
     * @param noGoZoneStart
     */
    public void receiveNoGoZoneStart(Map.GridLocation noGoZoneStart){
    	this.noGoZoneStart = new Map.GridLocation(noGoZoneStart.row, noGoZoneStart.col);
    }
    
    /**
     * receive the end point of the shape of no go zone
     * @param noGoZoneEnd
     */
    public void receiveNoGoZoneEnd(Map.GridLocation noGoZoneEnd){
    	this.noGoZoneEnd = new Map.GridLocation(noGoZoneEnd.row, noGoZoneEnd.col);
    }
    
    /**
     * get the start point of the shape of no go zone
     */
    public Map.GridLocation getNoGoZoneStart(){
    	return noGoZoneStart;
    }
    
    /**
     * get the start point of the shape of no go zone
     */
    public Map.GridLocation getNoGoZoneEnd(){
    	return noGoZoneEnd;
    }
    /**
     * Added by Ziang Chen
     * get the destination created by user
     */
    public Map.GridLocation getDestination(){

    		return this.destinationPoint;
    }
    
    public enum EventType {
        NONE,
        FORWARD_PRESSED, FORWARD_RELEASED,
        BACKWARD_PRESSED, BACKWARD_RELEASED,
        TURN_CLOCKWISE_PRESSED, TURN_CLOCKWISE_RELEASED,
        TURN_ANTICLOCKWISE_PRESSED, TURN_ANTICLOCKWISE_RELEASED,
        STOP_PRESSED, STOP_RELEASED,
        EMERGENCY_STOP_PRESSED, EMERGENCY_STOP_RELEASED,
        SENSOR_LEFT_PRESSED,   SENSOR_LEFT_RELEASED,
        SENSOR_RIGHT_PRESSED,   SENSOR_RIGHT_RELEASED,
        DESTINATION_CREATED,
        MANUAL, AUTO, 
        CLOSE,
        ADD_NO_GO_ZONES,
    }
    
    /**
     * A message sent to the handler by the UI communicating that a particular
     * event has occurred.
     * @author jkortman
     */
    public class UIEvent {
        public UIEvent(EventType eventType) {
            this.eventType = eventType;
        }
        
        @Override
        public boolean equals(Object o) {
        		if (o == null) {
        			return false;
        		}
        		
        		if (this == o) {
        			return true;
        		}
        		
        		if (!(o instanceof UIEvent)) {
        			return false;
        		}
        		
        		UIEvent event = (UIEvent)o;
        		return this.eventType == event.eventType;
        }
        
        public final EventType eventType;
    }
    
    public Map.GridLocation destinationPoint;
    public Map.GridLocation noGoZoneStart;
    public Map.GridLocation noGoZoneEnd;
    private Queue<UIEvent> queue;
}
