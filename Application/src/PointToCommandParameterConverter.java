/**
 * A class that converts a point on where the robot should move next into parameters of a set of commands 
 * that will be sent to the robot.
 * @author cyrusvillacampa
 *
 */

public class PointToCommandParameterConverter {
	public PointToCommandParameterConverter() { }
	
	/**
	 * This method determines the parameter to the commands to be sent to the robot in order to move it to 
	 * a particular point on the map.
	 * @param point - The destination point
	 * @param robotPosition - The robot position
	 * @param robotOrientation - The angle 
	 * @return - The turning angle and the distance to travel
	 */
	public AngleDistance convert(Point destinationPoint, Point robotPosition, float robotOrientation) {
		float xDistance = destinationPoint.xMetres - robotPosition.xMetres;
		float yDistance = destinationPoint.yMetres - robotPosition.yMetres;
//		float distanceToTravel = (float) Math.sqrt((double) Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
		float distanceToTravel = (float)Math.hypot(xDistance, yDistance);
		float angleRelativeToXAxis = convertRobotAngleRelativeToXAxis(robotOrientation);
		float turningAngle = 0.0f;
		
		turningAngle = calculateTurningAngle(destinationPoint, angleRelativeToXAxis, robotPosition);
		return new AngleDistance(turningAngle, distanceToTravel);
	}
	
	/**
	 * Converts the robot angle/orientation to make it relative to the positive x-axis
	 * @return - The robot angle but now relative to positive x-axis instead of positive y-axis
	 */
	private float convertRobotAngleRelativeToXAxis(float angle) {
		return 90.0f + angle;
	}
	
	/**
	 * This calculates the turning angle, that is the angle the robot needs to turn to for it to face
	 * the correct direction.
	 * @param point - The destination point
	 * @param angle - The angle/orientation of the robot relative to the positive x-axis
	 * @param robotPos - The robot position
	 * @return - The turning angle
	 */
	private float calculateTurningAngle(Point point, float angle, Point robotPos) {
		float turningAngle = 0.0f;
		Point robotVectorOrientation = angleToVector((double) angle);
		Point destinationVectorRelativeToRobotPos = normalize(calculateDestinationVector(point, robotPos));
		float result = (float) dotProduct(robotVectorOrientation, destinationVectorRelativeToRobotPos);
		turningAngle = (float) Math.toDegrees(Math.acos(result)) - 10.0f;
		
		return turningDirection(turningAngle, robotVectorOrientation, destinationVectorRelativeToRobotPos);
//		return turningDirection(turningAngle, robotPos, point);
	}
	
	/**
	 * Calculates the vector of the destination point relative to the "origin", where the "origin" is 
	 * the robot position.
	 * @param destPoint - The destination point
	 * @return - Destination point vector
	 */
	private Point calculateDestinationVector(Point destPoint, Point origin) {
		Point result = new Point();
		result.xMetres = destPoint.xMetres - origin.xMetres;
		result.yMetres = destPoint.yMetres - origin.yMetres;
		return result;
	}
	
	/**
	 * Convert an angle relative to the positive x-axis to a unit vector
	 * @param angle - The angle to convert
	 * @return - A unit vector
	 */
	private Point angleToVector(double angle) {
		Point result = new Point();
		result.xMetres = (float) Math.cos(Math.toRadians(angle));
		result.yMetres = (float) Math.sin(Math.toRadians(angle));
		return result;
	}
	
	/**
	 * Calculates the dot product of two vectors
	 * @param p1 - The first vector
	 * @param p2 - The second vector
	 * @return - Dot/Scalar product of two vectors
	 */
	private double dotProduct(Point p1, Point p2) {
		double result = 0.0;
		result = (p1.xMetres*p2.xMetres) + (p1.yMetres*p2.yMetres);
		return result;
	}
	
	/**
	 * Normalizes a vector
	 * @param p1 - The vector to be normalized
	 * @return - Normalized version of the vector
	 */
	private Point normalize(Point p1) {
		Point result = new Point();
		double magnitude = Math.sqrt((Math.pow(p1.xMetres, 2)) + (Math.pow(p1.yMetres, 2)));
//		if (Float.compare((float) (magnitude - Math.abs(p1.xMetres)), 0.001f) < 0) {
//			magnitude = Math.abs(p1.xMetres);
//		} else if (Float.compare((float)(magnitude - Math.abs(p1.yMetres)), 0.001f) < 0) {
//			magnitude = Math.abs(p1.yMetres);
//		}
		result.xMetres = (float) (p1.xMetres/magnitude);
		result.yMetres = (float) (p1.yMetres/magnitude);
		return result;
	}
	
	/**
	 * Determines which direction should the robot turn to(clockwise/anti-clockwise)
	 * @param angle - The turning angle
	 * @param robotPos - The robots position
	 * @param destinationPoint - The point of destination
	 * @return - A signed(positive/negative) angle that indicates direction to turn
	 */
	private float turningDirection(float angle, Point robotPos, Point destinationPoint) {
		float turningAngle = -angle;
		if (robotPos.xMetres <= 0.0f && destinationPoint.xMetres <= 0.0f) {
			if (robotPos.yMetres > destinationPoint.yMetres) {
				turningAngle = -turningAngle;
			}
		} else if (robotPos.xMetres >= 0.0f && destinationPoint.xMetres >= 0.0f) {
			if (robotPos.yMetres < destinationPoint.yMetres) {
				turningAngle = -turningAngle;
			}
		} else if (robotPos.xMetres > 0.0f && destinationPoint.xMetres < 0.0f) {
			turningAngle = -turningAngle;
		}
		return turningAngle;
	}
	
	/**
	 * The return value of the conversion from point to command parameters
	 * @author cyrusvillacampa
	 *
	 */
	public static class AngleDistance {
		public float angle;
		public float distance;
		
		public AngleDistance(float angle, float distance) {
			this.angle = angle;
			this.distance = distance;
		}
		
		@Override
		public String toString() {
			return "(angle, distance): (" + this.angle + ", " + this.distance + ")";
		}
	}
}
