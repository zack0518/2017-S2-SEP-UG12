import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class DecisionMakerTest {
	int rows = 60*2;
    int cols = 60*2;
    float gridSize = 0.02f;
    Map map;

	@Test
	/**
	 * A map without any obstacles or crater(i.e. all points are traversable) and also no unknown points.
	 */
	public void test1() {
		map = new Map(gridSize, rows, cols);
		PathFinding pathFinding = new PathFinding(map);
		DecisionMaker dm = createDM(map, pathFinding);
		Point robotPos = new Point(-0.3f, -0.15f);
		Point destPos = new Point(0.5f, 0.0f);
		dm.findPath(robotPos, convertPointToGridLocation(destPos));
		List<Point> foundPath = getAllPointsInFoundPath(dm);
		assertFoundPath(foundPath);
		labelAndPrintMap(robotPos, foundPath);
	}
	
	/**
	 * A map with an obstacle in it.
	 */
	@Test
	public void test2() {
		map = new Map(gridSize, rows, cols);
		// Mark the obstacle on the map
		markMap(map);
		PathFinding pathFinding = new PathFinding(map);
		DecisionMaker dm = createDM(map, pathFinding);
		Point robotPos = new Point(-0.9f, 0.7f);
		Point destPos = new Point(0.7f, 0.0f);
		dm.findPath(robotPos, convertPointToGridLocation(destPos));
		List<Point> foundPath = getAllPointsInFoundPath(dm);
		assertFoundPath(foundPath);
		labelAndPrintMap(robotPos, foundPath);
	}
	
	/**
	 * A map with some obstacle in it and robot in different initial position.
	 * The destination point is close from a crater(i.e. the destination point
	 * is adjacent to the crater). This test checks if the DecisionMaker class 
	 * adjusts the destination point(i.e. move the point to the left) so that 
	 * a the pathfinding algorithm can find a path.
	 *
	 */
	@Test
	public void test3() {
		map = new Map(gridSize, rows, cols);
		markMap(map);
		PathFinding pathFinding = new PathFinding(map);
		DecisionMaker dm = createDM(map, pathFinding);
		Point robotPos = new Point(-0.9f, -0.2f);
		Point destPos = new Point(-0.45f, -0.7f);
		labelMap(destPos);
		dm.findPath(robotPos, convertPointToGridLocation(destPos));
		List<Point> foundPath = getAllPointsInFoundPath(dm);
		assertFoundPath(foundPath);
		labelAndPrintMap(robotPos, foundPath);
	}
	
	/**
	 * A map with some obstacle in it and robot in different initial position.
	 * The destination point is close from a crater(i.e. the destination point
	 * is adjacent to the crater). This test checks if the DecisionMaker class 
	 * adjusts the destination point(i.e. move the point to the right) so that 
	 * a the pathfinding algorithm can find a path.
	 *
	 */
	@Test
	public void test4() {
		map = new Map(gridSize, rows, cols);
		markMap(map);
		PathFinding pathFinding = new PathFinding(map);
		DecisionMaker dm = createDM(map, pathFinding);
		Point robotPos = new Point(-0.9f, -0.2f);
		Point destPos = new Point(0.25f, -0.7f);
		labelMap(destPos);
		dm.findPath(robotPos, convertPointToGridLocation(destPos));
		List<Point> foundPath = getAllPointsInFoundPath(dm);
		assertFoundPath(foundPath);
		labelAndPrintMap(robotPos, foundPath);
	}
	
	/**
	 * A map with some obstacle in it and robot in different initial position.
	 * The destination point is close from a crater(i.e. the destination point
	 * is adjacent to the crater). This test checks if the DecisionMaker class 
	 * adjusts the destination point(i.e. move the point down and right) so that 
	 * a the pathfinding algorithm can find a path.
	 *
	 */
	@Test
	public void test5() {
		map = new Map(gridSize, rows, cols);
		markMap(map);
		PathFinding pathFinding = new PathFinding(map);
		DecisionMaker dm = createDM(map, pathFinding);
		Point robotPos = new Point(-0.9f, -0.2f);
		Point destPos = new Point(-0.45f, -0.44f);
		labelMap(destPos);
		dm.findPath(robotPos, convertPointToGridLocation(destPos));
		List<Point> foundPath = getAllPointsInFoundPath(dm);
		assertFoundPath(foundPath);
		labelAndPrintMap(robotPos, foundPath);
	}
	
	/**
	 * A map with some obstacle in it and robot in different initial position.
	 * The destination point is close from a crater(i.e. the destination point
	 * is adjacent to the crater). This test checks if the DecisionMaker class 
	 * adjusts the destination point(i.e. move the point up ) so that 
	 * a the pathfinding algorithm can find a path.
	 *
	 */
	@Test
	public void test6() {
		map = new Map(gridSize, rows, cols);
		markMap(map);
		PathFinding pathFinding = new PathFinding(map);
		DecisionMaker dm = createDM(map, pathFinding);
		Point robotPos = new Point(-0.9f, -0.2f);
		Point destPos = new Point(0.3f, -0.03f);
		labelMap(destPos);
		dm.findPath(robotPos, convertPointToGridLocation(destPos));
		List<Point> foundPath = getAllPointsInFoundPath(dm);
		assertFoundPath(foundPath);
		labelAndPrintMap(robotPos, foundPath);
	}
	
	/**
	 * Converts point(x,y) in metres to grid location(row, column)
	 * @param point - The point that contains a point in the map
	 * @return		- The converted node
	 */
	private Map.GridLocation convertPointToGridLocation(Point point) {
		Map.GridLocation loc = map.getGridLocation(point);
		return loc;
	}
	
	/**
	 * Marks the map with predefined obstacles and craters.
	 * @param map	- THe map that will be marked
	 */
	private void markMap(Map map) {
		try {
			// Crater 1
			for (int row = 38; row <= 65; row += 1) {
				int inc = 23;
				if (row == 38 || row == 65) {
					inc = 1;
				}
				for (int col = 37; col <= 60; col += inc) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.CRATER, loc, 1.0f);
				}
			}
			
			// Crater 2
			for (int row = 22; row <= 57; row += 1) {
				int inc = 17;
				if (row == 22 || row == 57) {
					inc = 1;
				}
				for (int col = 73; col <= 90; col += inc) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.CRATER, loc, 1.0f);
				}
			}
			
			// Rectangular obstacle in the lower middle
			for (int row = 80; row <= map.rows()-1; row += 1) {
				for (int col = 30; col <= 36; col += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			} 
			
			// Rectangular obstacle in the upper middle
			for (int row = 0; row <= 60; row += 1) {
				for (int col = 30; col <= 36; col += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			} 
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Extract all the points in the found path.
	 * @param dm
	 */
	private List<Point> getAllPointsInFoundPath(DecisionMaker dm) {
		List<Point> pointsInFoundPath = new ArrayList<>();
		while (!dm.hasReached()) {
			pointsInFoundPath.add(dm.getNextPosition());
		}
		return pointsInFoundPath;
	}
	
	/**
	 * Label the points on the map using the given argument and prints out the map.
	 * @param setOfPoints
	 */
	private void labelAndPrintMap(Point robotPos, List<Point> setOfPoints) {
		Iterator<Point> setOfPointsIterator = setOfPoints.iterator();
		while (setOfPointsIterator.hasNext()) {
			labelMap(setOfPointsIterator.next());
		}
//		labelMap(new Point(0.1f, 0.0f));
		printMap(robotPos);
	}
	
	/**
	 * Label a point in the map
	 * @param point - A point in the map
	 */
	private void labelMap(Point point) {
		try {
			map.set(Map.Property.TRACKS, point, 1.0f);
		} catch (Exception e) {
			System.err.printf("[ DECISION MAKER TEST ] Error: %s", e.getMessage());
		}
	}
	
	/**
	 * Prints a visual representation of the map
	 * @param robotPos
	 */
	private void printMap(Point robotPos) {
		if (Settings.Debug.showMap) {
			map.print(robotPos, new Point());
		}
	}
	
	/**
	 * Creates a DecisionMaker object based from the given arguments
	 * @return - The DecisionMaker object
	 */
	private DecisionMaker createDM(Map map, PathFinding pathFinding) {
		return new DecisionMaker(map);
	}
	
	/**
	 * Assert if the found path doesn't include any grid that has 
	 * obstacle or part of a crater or in a no-go-zone
	 */
	private void assertFoundPath(List<Point> foundPath) {
		for (Point point: foundPath) {
			Map.Property prop = map.getProperty(point);
			assertEquals("Testing for obstacle...", true, prop != Map.Property.OBSTACLE);
			assertEquals("Testing for creater...", true, prop != Map.Property.CRATER);
			assertEquals("Testing for no go zone...", true, prop != Map.Property.NO_GO_ZONE);
			assertEquals("Testing for border...", true, prop != Map.Property.BORDER);
		}
	}
}
