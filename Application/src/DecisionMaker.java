import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class finds the shortest path between two points in the map by utilizing the A star algorithm
 * to find such path. Additionally it also makes decisions whether or not if the next point that it will
 * give to the handler(for it to send to the robot which the robot will move to) is not inside any impassable
 * area, if it is then this class will find another path that around the impassable area.
 * @author cyrusvillacampa
 *
 */
public class DecisionMaker {
	private Map map;
	private PathFinding pathFinding;		// The pathfinding algorithm
	private List<PathNode> foundPath;	// The found path
	private int nextPointCounter;		// Points to the next point in the found path
	private PathNode origin;				// The origin point of the robot
	
	public DecisionMaker(Map map) {
		this.map = map;
		this.pathFinding = new PathFinding(this.map);
		this.nextPointCounter = 0;
		init();
	}
	
	private void init() {
		pathFinding.setDistanceFromPoint(10);
	}
	
	/**
	 * Provides the next position in the found path. If the next position is not traversable then it
	 * finds a new path and returns the next position on the new found path. If a path could not be found
	 * or if the next position is out of bounds or if the destination point has been reached, it returns 
	 * null.
	 * @return  - The next position/point in metres
	 */
	public Point getNextPosition() {
		PathNode nextPosNode = new PathNode();
		if (nextPointCounter < foundPath.size()) {
			try {
				nextPosNode = foundPath.get(nextPointCounter);
				if (!isTraversable(nextPosNode) ||
						!pathFinding.checkSurroundings(nextPosNode)) {		// Robot sensor has detected that the next point is inside an impassable area
					if (!findAnotherPath()) { 			// If a path could not be found.
						return null;
					}
					nextPosNode = foundPath.get(nextPointCounter);
				}
				if (nextPointCounter + 5 < foundPath.size()) {
					nextPointCounter += 5;
				} else {
					nextPointCounter += 1;
				}
				return convertGridLocationToPoint(nextPosNode);
			} catch (Map.OutOfMapBoundsException e) {
				System.err.printf("The grid location %s is out of bounds%n", nextPosNode.toString());
			}
		}
		System.out.println("There are no more points or the path is blocked");
		return null;
	}
	
	/**
	 * This method finds a new path from the robot's current position to the destination point
	 * @return - True if a path is found, otherwise false
	 */
	private boolean findAnotherPath() {
		PathNode startPoint, endPoint = foundPath.get(foundPath.size()-1);
		// NOTE: This is possible if the robot has not sensed it's surroundings yet.
		if (nextPointCounter == 0) {			// If robot hasn't yet move from it's origin and it has detected that the next position is not traversable.
			startPoint = origin;
		} else {								// If robot has moved from it's origin
			startPoint = foundPath.get(nextPointCounter-1);
		}
		return findPathHelper(startPoint, adjustNode(endPoint));
	}
	
	/**
	 * It converts the robot and destination to grid location and invokes a function that finds the 
	 * shortest path.
	 * @param robotPos - The robot's current position on the map in metres
	 * @param destPos  - A point on the map in terms of cartesian coordinate
	 * @return		   - True if a path is found, false otherwise
	 */
	public boolean findPath(Point robotPos, Map.GridLocation destPos) {
		PathNode robotPosNode = convertPointToGridLocation(robotPos);
		PathNode destPosNode = adjustNode(new PathNode(destPos.row, destPos.col));
//		PathNode destPosNode = new PathNode(destPos.row, destPos.col);
//		PathNode destPosNode = new PathNode(destPos.xMetres, destPos.yMetres);
		origin = robotPosNode;
		return findPathHelper(robotPosNode, destPosNode);
	}
	
	/**
	 * A helper function that finds a shortest path from the robot's current position to a destination 
	 * point. And stores the found path in this object's "foundPath" variable.
	 * @param robotPosNode - The robot's current position on the map in terms of GridLocation
	 * @param destPosNode  - A point on the map in terms of GridLocation
	 * @return			   - True if a path is found, false otherwise
	 */
	private boolean findPathHelper(PathNode node1, PathNode node2) {
		try {
			if (!isTraversable(node1)) {
				System.err.printf("The grid location %s is not traversable\n", node1.toString());
				return false;
			}
			if (!isTraversable(node2)) {
				System.err.printf("The grid location %s is not traversable\n", node2.toString());
				return false;
			}
			foundPath = pathFinding.searchPath(node1, node2);
			if (foundPath.size() == 0) {			// The destination point is surrounded by an obstacle or is inside an impassable area
				return false;
			}
			this.labelAndPrintMap(this.getAllPointsInFoundPath(this));	// FOR DEBUGGING
			nextPointCounter = 0;				// Set the nextPointCounter to 0 to start at the beginning of the found path
		} catch (Exception e) {
			// EITHER OF THE POINTS IS OUTSIDE THE MAP BOUNDS
			System.err.println("[ DECISION MAKER ] Error: Either one of the points is out of bounds.");
			return false;
		}
		return true;
	}
	
	/**
	 * Adjusts the destination point so that it would be
	 * far enough from impassable areas so that the robot
	 * can find a path to it.
	 * @param node	- The point to be adjusted
	 * @return		- The adjusted point
	 */
	private PathNode adjustNode(PathNode node) {
		int distanceFromPoint = pathFinding.getDistanceFromPoint();
		PathNode adjustedNode = new PathNode(node);
		if (adjustedNode.xMetres >= distanceFromPoint) {		// Check grid to the left of the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(adjustedNode.xMetres - distance, adjustedNode.yMetres);
				try {
					if (!isTraversable(loc)) {
						PathNode temp = new PathNode(adjustedNode.xMetres + 1, adjustedNode.yMetres);
						// Check if the node is still inside the borders and is not inside an impassable 
						// area if it is moved to the right by 1 grid
						if (adjustedNode.xMetres < map.columns() && 
								isTraversable(temp)) {					
							adjustedNode.xMetres += 1;					// Move the node to the right by 1 grid
						}
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (adjustedNode.xMetres <= map.columns() - distanceFromPoint - 1) {		// Check grid to the right of the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(adjustedNode.xMetres + distance, adjustedNode.yMetres);
				try {
					if (!isTraversable(loc)) {
						PathNode temp = new PathNode(adjustedNode.xMetres - 1, adjustedNode.yMetres);
						if (adjustedNode.xMetres >= 0 && 
								isTraversable(temp)) {
							adjustedNode.xMetres -= 1;				// Move the node to the left by 1 grid
						}
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (adjustedNode.yMetres >= distanceFromPoint) {		// Check grid below the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(adjustedNode.xMetres, adjustedNode.yMetres - distance);
				try {
					if (!isTraversable(loc)) {
						PathNode temp = new PathNode(adjustedNode.xMetres + 1, adjustedNode.yMetres);
						if (adjustedNode.yMetres < map.rows() &&
								isTraversable(temp)) {
							adjustedNode.yMetres += 1;				// Move the node up by 1 grid
						}
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (adjustedNode.yMetres <= map.rows() - distanceFromPoint - 1) {		// Check grid above the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(adjustedNode.xMetres, adjustedNode.yMetres + distance);
				try {
					if (!isTraversable(loc)) {
						PathNode temp = new PathNode(adjustedNode.xMetres - 1, adjustedNode.yMetres);
						if (adjustedNode.yMetres >= 0 &&
								isTraversable(temp)) {
							adjustedNode.yMetres -= 1;				// Move the node down by 1 grid
						}
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		return adjustedNode;
	}
	
	/**
	 * Informs the caller if there are more points in the found path to be traversed(i.e. it hasn't reached
	 * the destination point).
	 * @return  - True if there are no more points, false otherwise
	 */
	public boolean hasReached() {
		if (foundPath == null) {
			return true;
		}
		if (nextPointCounter >= 0 && nextPointCounter <= foundPath.size()-1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the given point in the map is traversable.
	 * @param p - The point in the map
	 * @return  - True if the point is traversable(i.e. outside an impassable area), false otherwise
	 */
	private boolean isTraversable(PathNode node) throws Map.OutOfMapBoundsException {
		Map.GridLocation loc = new Map.GridLocation((int)node.xMetres, (int)node.yMetres);
		boolean traversable = true;
		float obstacleProb;
		float craterProb;
		float noGoZoneProb;
		obstacleProb = map.get(Map.Property.OBSTACLE, loc);
		craterProb = map.get(Map.Property.CRATER, loc);
		noGoZoneProb = map.get(Map.Property.NO_GO_ZONE, loc);
		if (Float.compare(obstacleProb, 0.5f) > 0 || 
			Float.compare(craterProb, 0.5f) > 0 ||
			Float.compare(noGoZoneProb, 0.5f) > 0) {
			traversable = false;
		}
		
		return traversable;
	}
	
	/**
	 * Converts grid location(column, row) to point(x,y) in metres
	 * @param node - The node that contains the grid location in the map
	 * @return	   - The converted point
	 */
	private Point convertGridLocationToPoint(PathNode node) throws Map.OutOfMapBoundsException {
		Map.GridLocation gLoc = new Map.GridLocation(Math.round(node.xMetres), Math.round(node.yMetres));
		Point point = map.getCentrePoint(gLoc);
		return point;
	}
	
	/**
	 * Converts point(x,y) in metres to grid location(row, column)
	 * @param point - The point that contains a point in the map
	 * @return		- The converted node
	 */
	private PathNode convertPointToGridLocation(Point point) {
		Map.GridLocation loc = map.getGridLocation(point);
		// PathNode node = new Node(loc.col, loc.row);
		PathNode node = new PathNode(loc.row, loc.col);
		return node;
	}
	
	////////////////////////
	// DEBUGGING PURPOSES //
	////////////////////////
	public void printFoundPath() {
		System.out.print("Grid Locations: ");
		for (PathNode node: foundPath) {
			System.out.printf("%s, ",node.toString());
		}
		System.out.println();
		System.out.print("Point in metres: ");
		try {
			for (PathNode node: foundPath) {
				System.out.printf("%s, ",convertGridLocationToPoint(node).toString());
			}
		} catch (Map.OutOfMapBoundsException e) {
			
		}
		System.out.println();
	}
	private List<Point> getAllPointsInFoundPath(DecisionMaker dm) {
		List<Point> pointsInFoundPath = new ArrayList<>();
		while (!dm.hasReached()) {
			pointsInFoundPath.add(dm.getNextPosition());
		}
		return pointsInFoundPath;
	}
	private void labelAndPrintMap(List<Point> setOfPoints) {
		Iterator<Point> setOfPointsIterator = setOfPoints.iterator();
		while (setOfPointsIterator.hasNext()) {
			labelMap(setOfPointsIterator.next());
		}
	}
	private void labelMap(Point point) {
		try {
			map.set(Map.Property.TRACKS, point, 1.0f);
		} catch (Exception e) {
			System.err.printf("[ DECISION MAKER TEST ] Error: %s", e.getMessage());
		}
	}
}
