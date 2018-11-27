/**
 * Receives distance data from the Robot and stores it in a Map.
 * @author jkortman
 */
public class DistanceSensorInterpreter {
    /**
     * Create a DistanceSensorInterpreter.
     */
    public DistanceSensorInterpreter() {}
    
    public enum Interpretation {
        INFINITY, OUT_OF_BOUNDS, SUCCESS
    }
    
    /**
     * Given a robot state and the distance sensor reading, load the obstacle
     * data into the map.
     * @param distanceMetres        the metres from the robot position at
     *                              which an obstacle is detected.
     * @param map                   the map data structure to update.
     * @param robotLocation         the location of the robot (map
     *                              coordinate system).
     * @param robotDirectionDegrees the direction of the robot (degrees
     *                              anticlockwise from positive y-axis
     *                              in map coordinate system.
     * @return                      the interpreted result state (INFINITY, 
     *                              OUT_OF_BOUNDS, or SUCCESS).
     */
    public Interpretation interpret(float distanceMetres,
                                    Map map,
                                    Point robotLocation,
                                    float robotDirectionDegrees) {
        if (distanceMetres == Float.NEGATIVE_INFINITY
                || distanceMetres == Float.POSITIVE_INFINITY) {
            return Interpretation.INFINITY;
        }
        // Calculate the position of the detected obstacle.
        Point pos = Point.add(
            robotLocation,
            new Point(-1.0f * distanceMetres * (float)Math.sin(Math.toRadians(robotDirectionDegrees)),
                       1.0f * distanceMetres * (float)Math.cos(Math.toRadians(robotDirectionDegrees)))
        );
        // Load the obstacle position into the Map.
        try {
            //System.out.printf("pos = (%f, %f)\n", pos.xMetres, pos.yMetres);
            map.set(Map.Property.OBSTACLE, pos, 1.0f);
        } catch (Map.OutOfMapBoundsException e) {
            return Interpretation.OUT_OF_BOUNDS;
        }
        return Interpretation.SUCCESS;
    }
}
