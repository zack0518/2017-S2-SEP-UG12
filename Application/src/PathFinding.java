import java.util.LinkedList;
import java.util.List;

/**
 * A class that implements the A* algorithm in finding a path from the current position of the robot to
 * a given point on the map.
 * @author ravilim
 *
 */

public class PathFinding {
	/**
	 * The map
	 */
	public Map map;
	/**
	 * A list containing nodes that are not visited but adjacent to visited nodes
	 */
	private List<PathNode> waitingList;
	/**
	 * A list containing nodes that were already visited
	 */
	private List<PathNode> accessedList;
	/**
	 * Path finding done
	 */
	private boolean done = false;
	private int distanceFromPoint;
	
	public PathFinding(Map map) {
		this.map = map;
		this.distanceFromPoint = 0;
	}
	
	/**
	 * Set the distanceFromPoint member field
	 * @param val	- The value to set the member to.
	 */
	public void setDistanceFromPoint(int val) {
		distanceFromPoint = val;
	}
	
	/**
	 * Get the distanceFromPoint member field
	 * @return	- The distanceFromPoint member field
	 */
	public int getDistanceFromPoint() {
		return this.distanceFromPoint;
	}
	
	/**
	 * This method searches for a path from p1 to p2.
	 * @param p1 - A first point(origin) on the map
	 * @param p2 - A second point(destination) on the map
	 * @return The found path(if a path exists the found path will have a size greater than 0)
	 */
	public List<PathNode> searchPath(PathNode p1, PathNode p2) {
		waitingList  = new LinkedList<PathNode>();
		accessedList = new LinkedList<PathNode>();

		waitingList.add(p1); // add starting list to waiting list

		PathNode currentNode;
		done = false;
		while (!done) { //TODO: Robot need to go to the next position
			if (waitingList.isEmpty()) {
				return new LinkedList<PathNode>();
			}
			currentNode = lowestfCosts(); // get node with lowest fCosts from waitingList
			accessedList.add(currentNode); // add current node to accessedList
			waitingList.remove(currentNode); // delete current node from waitingList
			
			if ((currentNode.xMetres == p2.xMetres)
					&& (currentNode.yMetres == p2.yMetres)) { // found goal
				return returnPath(p1, currentNode);
			}

			//for all adjacent nodes
			List<PathNode> adjacentNodes = getAdjacent(currentNode);
			currentNode.setAdjacentNodes(adjacentNodes);
			for (int i = 0; i < adjacentNodes.size(); i++) {
				PathNode currentAdj = adjacentNodes.get(i);
				if (!waitingList.contains(currentAdj)) { // node is not in waiting 
					currentAdj.setPrevious(currentNode); // set current node as previous
					currentAdj.sethCosts((PathNode)p2); // set hCosts of this node(estimated costs to goal)
					currentAdj.setgCosts(currentNode); // set gCosts of current node(costs from start to current node)
					waitingList.add(currentAdj); // add node to waiting list
				}
				else { // node is in waiting list
					if (currentAdj.getgCosts() > currentAdj.calculategCosts(currentNode)) { //costs from current node is lower than previous costs
						currentAdj.setPrevious(currentNode); // set current node as previous for this node 
						currentAdj.setgCosts(currentNode); // set gCosts of current node(costs from start to current node)
					}

				}
				if (waitingList.isEmpty()) { // no path exists
					return new LinkedList<PathNode>(); //return empty list
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Calculates the found path between two points
	 * @param start
	 * @param goal
	 * @return
	 */
	private List<PathNode> returnPath(PathNode start, PathNode goal) {

		LinkedList<PathNode> path = new LinkedList<PathNode>();
		PathNode temp = goal;

		boolean done = false;
		while(!done) { // If there are invalid node in the list, it will result in an infinite loop
			path.addFirst(temp);
			temp = temp.getPrevious();

			if (temp.equals(start)) {
				done = true;
			}
		}
		return path;
	}
	
	/**
	 * returns the node with the lowest fCosts in the waiting list.
	 */
	private PathNode lowestfCosts() {
		PathNode lowest = waitingList.get(0);
		for (int i = 0; i < waitingList.size(); i++) {
			if (Float.compare(waitingList.get(i).getfCosts(), lowest.getfCosts()) <= 0) {
				lowest = waitingList.get(i);
			}
		}
		return lowest;
	}
	
	/**
	 * Checks surroundings of a particular point if they are blocked or inside
	 * an impassable area. It checks for points above, below, left and right
	 * to the point.
	 * @param node				- A particular point to where it's 
	 * 							  surrounding is checked
	 * @param distanceFromPoint	- The distance from the point to the
	 * 							  edge of the surrounding that is examined
	 * @return					- True if it's surrounding is clear
	 */
	public boolean checkSurroundings(PathNode node) {
		if (!checkXYDirection(node) || !checkDiagonalDirection(node)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Check the surroundings along the diagonal directions
	 * (i.e. upper right and left; bottom right and left) of 
	 * the point.
	 * @param node	- A particular node on where it's 
	 * 				  surrounding is checked
	 * @return		- True if it's upper right and left; 
	 * 				  bottom right and left surroundings
	 * 				  are clear
	 */
	private boolean checkDiagonalDirection(PathNode node) {
		boolean clearFlag = true;
		if (node.xMetres <= map.columns() - distanceFromPoint - 1 && 
				node.yMetres <= map.rows() - distanceFromPoint - 1) {	// Check upper right diagonal
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(node.xMetres + distance, node.yMetres + distance);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (node.xMetres >= distanceFromPoint &&
				node.yMetres <= map.rows() - distanceFromPoint - 1) {	// Check upper left diagonal
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(node.xMetres - distance, node.yMetres + distance);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (node.xMetres >= distanceFromPoint && 
				node.yMetres >= distanceFromPoint) {						// Check bottom left diagonal
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(node.xMetres - distance, node.yMetres - distance);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (node.xMetres <= map.columns() - distanceFromPoint - 1 && 
				node.yMetres >= distanceFromPoint) {						// Check bottom right diagonal
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
				PathNode loc = new PathNode(node.xMetres + distance, node.yMetres - distance);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		return clearFlag;
	}
	
	/**
	 * Check the surroundings along the X(left and right) and Y(up and below) direction.
	 * @param node	- A particular node on where it's 
	 * 				  surrounding is checked
	 * @return		- True if it's X and Y surroundings
	 * 				  are clear
	 */
	private boolean checkXYDirection(PathNode node) {
		boolean clearFlag = true;
		if (node.xMetres >= distanceFromPoint) {		// Check grid to the left of the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
//				PathNode loc = new PathNode(node.xMetres - distanceFromPoint, node.yMetres);
				PathNode loc = new PathNode(node.xMetres - distance, node.yMetres);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (node.xMetres <= map.columns() - distanceFromPoint - 1) {		// Check grid to the right of the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
//				PathNode loc = new PathNode(node.xMetres + distanceFromPoint, node.yMetres);
				PathNode loc = new PathNode(node.xMetres + distance, node.yMetres);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (node.yMetres >= distanceFromPoint) {		// Check grid below the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
//				PathNode loc = new PathNode(node.xMetres, node.yMetres - distanceFromPoint);
				PathNode loc = new PathNode(node.xMetres, node.yMetres - distance);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		if (node.yMetres <= map.rows() - distanceFromPoint - 1) {		// Check grid above the node
			for (int distance = 1; distance <= distanceFromPoint; distance++) {
//				PathNode loc = new PathNode(node.xMetres, node.yMetres + distanceFromPoint);
				PathNode loc = new PathNode(node.xMetres, node.yMetres + distance);
				try {
					if (!isWalkable(loc)) {
						clearFlag = false;
					}
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("Point %s is out of bounds%n", loc);
				}
			}
		}
		return clearFlag;
	}
	
	/**
	 * Return a linked list with nodes adjacent to the given node
	 */
	private List<PathNode> getAdjacent(PathNode node) {
		float x = node.xMetres;
		float y = node.yMetres;
		List<PathNode> adj = new LinkedList<PathNode>();

		PathNode temp;
		try {
			if (x > 0) {
				temp = new PathNode((x - 1.0f), y);
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
					temp.setIsDiagonally(false);
					adj.add(temp);
				}
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
		
		try {
			if (x < map.columns()-1) {
				temp = new PathNode((x + 1.0f), y);
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
					temp.setIsDiagonally(false);
					adj.add(temp);
				}
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
		
		try {
			if (y > 0) {
				temp = new PathNode(x, (y - 1.0f));
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
					temp.setIsDiagonally(false);
					adj.add(temp);
				}
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}

		try {
			if (y < map.rows()-1) {
				temp = new PathNode(x, (y + 1.0f));
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
					temp.setIsDiagonally(false);
					adj.add(temp);
				}
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
		
		// DIAGONAL CASE
		try {
			if (x < map.columns()-1 && y < map.rows()-1) {
				temp = new PathNode((x + 1), (y + 1));
				temp.setWalkable(checkSurroundings(temp));
	            if (temp.isWalkable() && !accessedList.contains(temp)) {
	                temp.setIsDiagonally(true);
	                adj.add(temp);
	            }
	        }
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
        		
		try {
			if (x > 0 && y > 0) {
				temp = new PathNode((x - 1), (y - 1));
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
	                temp.setIsDiagonally(true);
	                adj.add(temp);
	            }
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
		
		try {
			if (x > 0 && y < map.rows()-1) {
				temp = new PathNode((x - 1), (y + 1));
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
	                temp.setIsDiagonally(true);
	                adj.add(temp);
	            }
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
		 
		try {
			if (x < map.columns()-1 && y > 0) {
				temp = new PathNode((x + 1), (y - 1));
				temp.setWalkable(checkSurroundings(temp));
				if (temp.isWalkable() && !accessedList.contains(temp)) {
	                temp.setIsDiagonally(true);
	                adj.add(temp);
	            }
			}
		} catch (Exception e) {
			System.err.println("Out of bounds");
		}
		
        return adj;
	}
	
	/**
	 * Set Walkable
	 * @param point
	 * @param bool
	 */
	public void setWalkable(PathNode point, boolean bool) {
		point.setWalkable(bool);
	}
	
	/**
	 * Returns true if the node is walkable, that is there is no obstacle on that node or is not part of a
	 * crater. 
	 * @param node - The node to be checked
	 * @return - True if node is walkable, otherwise false
	 */
	public boolean isWalkable(PathNode node) throws Map.OutOfMapBoundsException {
		Map.GridLocation loc = new Map.GridLocation((int)node.xMetres, (int)node.yMetres);
		boolean walkable = true;
		float obstacleProb;
		float craterProb;
		float noGoZoneProb;
		obstacleProb = map.get(Map.Property.OBSTACLE, loc);
		craterProb = map.get(Map.Property.CRATER, loc);
		noGoZoneProb = map.get(Map.Property.NO_GO_ZONE, loc);
		if (Float.compare(obstacleProb, 0.5f) > 0 || 
			Float.compare(craterProb, 0.5f) > 0 ||
			Float.compare(noGoZoneProb, 0.5f) > 0) {
			walkable = false;
		}
//		if (map.distanceToNearestObstacle(node, Robot.maximumRadiusFromCentre)
//		        < Robot.maximumRadiusFromCentre) {
//		    return false;
//		}
		return walkable;
	}
}
