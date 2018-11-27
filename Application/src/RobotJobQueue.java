import java.util.Queue;

import java.util.ArrayDeque;

/**
 * A wrapper around a thread-safe queue that keeps track of jobs received from the Handler.
 * Allows commands to be processed in order during autonomous mode.
 * @author jkortman, Jeremy Hughes
 */
public class RobotJobQueue {
  
    /**
     * Construct an empty, non-concurrent queue.
     */
    public RobotJobQueue() {
    	this.queue = new ArrayDeque<Job>();
        this.lastJob = -1;
        this.lastFinished = -1;
    }
		
    /**
     * Adds a new job to the queue, and updates the lastJob variable with the current ID.
     * @param command the command from the handlerToRobot queue
     */
    public void add(HandlerToRobotQueue.Command jobCommand) {
        queue.add(new Job(jobCommand, jobCommand.id));
        this.lastJob = jobCommand.id;
    }
    

	/**
	 * Remove a job from the top of the queue.
	 * Returns null if no messages are currently in the queue.
     * @return The next job to be run.
     */
    public Job receive() {
        return queue.poll();
    }
    
    /**
     * Checks if there any jobs waiting in the queue.
     * @return True if any jobs in the queue.
     */
    public boolean hasNewJob() {
        return queue.peek() != null;
    }
    /**
     * Empties queue of jobs.
     */
    public void clear() {
    	queue.clear();
    }
    
    public int getLastJob() {
    	return this.lastJob;
    }
    
    public int getLastFinished() {
    	return this.lastFinished;
    }
    
    public void setLastFinished(int ID) {
    	this.lastFinished = ID;
    }
    
    /**
     * Stores commands with IDs as jobs.
     * @see JobQueue
     */
    public class Job {
        /**
         * Create a job holding a command and a jobID.
         * @param command holds the command from the handlerToRobot queue
         * @param jobID holds the jobID for the command
         */
        public Job(HandlerToRobotQueue.Command jobCommand, int value) {
            	this.jobID = value;
            	this.command = jobCommand;
        }
        public final HandlerToRobotQueue.Command command;
        public final int jobID;
    }
    
    
    /**
     * Get the current size of the queue.
     */
    public int size() {
        return queue.size();
    }
    
    private int lastJob;
	private int lastFinished;
    Queue<Job> queue;
}






