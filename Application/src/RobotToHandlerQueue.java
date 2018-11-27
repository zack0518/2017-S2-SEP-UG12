import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayDeque;

/**
 * A wrapper around a thread-safe queue that communicates current state and
 * sensor data from the Robot module to the Handler module.
 * @author jkortman
 */
public class RobotToHandlerQueue {
    /**
     * Construct an empty queue.
     * @param concurrent    whether the queue should be multithread-friendly.
     */
    public RobotToHandlerQueue(boolean concurrent) {
        if (concurrent) {
            queue = new ConcurrentLinkedQueue<Message>();
        } else {
            queue = new ArrayDeque<Message>();
        }
    }
    
    /**
     * Construct an empty, non-concurrent queue.
     */
    public RobotToHandlerQueue() {
        queue = new ArrayDeque<Message>();
    }
    
    /**
     * Add a message object to the queue.
     * @param message
     */
    public void add(Message message) {
        queue.add(message);
    }
    
    /**
     * Remove a message from the top of the queue.
     * Returns null if no messages are currently in the queue.
     * @return The message removed from the top of the queue.
     */
    public Message receive() {
        return queue.poll();
    }
    
    /**
     * Check if there are messages currently in the queue.
     * @return true if there are any messages in the queue.
     */
    public boolean hasNewMessages() {
        return queue.peek() != null;
    }
    
    /**
     * A message communicating sensor data and robot state from the Robot to
     * the Handler.
     */
    public static class Message {
        // Sensor data.
        public RGBColor sensorRGB;
        public float sensorDistanceMetres;
        // Robot state.
        // The current estimated position and bearing of the robot.
        public Point position;
        public float angleDegrees;
        public Point colorSensorPosition;
        // Job recording data
        public int currentJob;
        public int lastJobCompleted;
        public int lastJobRecieved;
        public boolean interupted;
        public int interuptedJob;
    }
    
    /**
     * Get the current size of the queue.
     */
    public int size() {
        return queue.size();
    }

    Queue<Message> queue;
}
