/**
 * A read-only interface used to receive info from the Handler to display.
 * @author jkortman
 */
public interface HandlerDisplayInterface {
    /**
     * Get the robot position the in Map coordinate system.
     * @return the position of the robot.
     */
    public Point getRobotPosition();
    
    /**
     * Get the underlying map display interface.
     * @return
     */
    public Map map();
}
 