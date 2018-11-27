import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class PathFindingTest {
	Map map;
	PathFinding pathFinding;
	int distanceFromPoint;
	
	public PathFindingTest() {
		this.map = new Map(0.02f,     // 1cm grid size
                			   60 * 2,    // 119cm is the maximum edge length of an A0 sheet.
                			   60 * 2);
		pathFinding = new PathFinding(this.map);
		this.distanceFromPoint = 3;
	}
	
	/**
	 * No obstacles, "o" represents no obstacle in the path,
	 * "R" represents the robot and "D" represents the destination.
	 * 
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * R o o o o o o o o o o o o o o o o o o o o o o D
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 */
	@Test
	public void test1() {
		PathNode robotPosNode = new PathNode(5.0f, 0f);
		PathNode destPosNode = new PathNode(60.0f, 90.0f);
		pathFinding.setDistanceFromPoint(distanceFromPoint);
		List<PathNode> foundPath = pathFinding.searchPath(robotPosNode, destPosNode);
		Map.GridLocation robotLoc = new Map.GridLocation(Math.round(robotPosNode.xMetres), Math.round(robotPosNode.yMetres));
		Map.GridLocation destLoc = new Map.GridLocation(Math.round(destPosNode.xMetres), Math.round(destPosNode.yMetres));
		try {
			map.set(Map.Property.TRACKS, destLoc, 1.0f);
			map.set(Map.Property.TRACKS, robotLoc, 1.0f);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (PathNode point: foundPath) {
			Map.GridLocation robotPath = new Map.GridLocation(Math.round(point.xMetres), Math.round(point.yMetres));
			try {
				map.set(Map.Property.TRACKS, robotPath, 1.0f);
			} catch (Exception e) {
				
			}
		}
		assertFoundPath(foundPath);
		if (Settings.Debug.showMap) {
			map.print((Point)robotPosNode, new Point());
		}
	}
	
	/**
	 * One obstacle with a rectangular shape as shown below, "x" 
	 * represents the obstacles, "R" represents the robot and "D" 
	 * represents the destination.
	 * 
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * R o o o o o o o o o o o x x x x o o o o o o o D
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 */
	@Test
	public void test2() {
		map = new Map(0.02f,     // 1cm grid size
 			   		  60 * 2,    
 			   		  60 * 2);
		pathFinding = new PathFinding(map);
		
		try {
			for (int col = 30; col <= 50; col += 1) {
				for (int row = 20; row <= 50; row += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		PathNode robotPosNode = new PathNode(30.0f, 0f);
		PathNode destPosNode = new PathNode(20.0f, 70.0f);
		pathFinding.setDistanceFromPoint(distanceFromPoint);
		List<PathNode> foundPath = pathFinding.searchPath(robotPosNode, destPosNode);
		Map.GridLocation robotLoc = new Map.GridLocation(Math.round(robotPosNode.xMetres), Math.round(robotPosNode.yMetres));
		Map.GridLocation destLoc = new Map.GridLocation(Math.round(destPosNode.xMetres)+1, Math.round(destPosNode.yMetres)+1);
		try {
			map.set(Map.Property.TRACKS, destLoc, 1.0f);
			map.set(Map.Property.TRACKS, robotLoc, 1.0f);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (PathNode point: foundPath) {
			Map.GridLocation robotPath = new Map.GridLocation(Math.round(point.xMetres), Math.round(point.yMetres));
			try {
				map.set(Map.Property.TRACKS, robotPath, 5.0f);
			} catch (Exception e) {
				
			}
		}
		assertFoundPath(foundPath);
		if (Settings.Debug.showMap) {
			map.print((Point)robotPosNode, new Point());
		}
	}
	
	/**
	 * Two obstacles both with a rectangular shape as shown below, "x" 
	 * represents the obstacles, "R" represents the robot and "D" represents 
	 * the destination.
	 * 
	 * o o o x x x o o o o o o o o o o o o o o o o o o
	 * o o o x x x o o o o o o o o o o o o o o o o o o
	 * o o o x x x o o o o o o o o o o o o o o o o o o
	 * o o o x x x o o o o o o x x x x o o o o o o o o
	 * o o o x x x o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * R o o o o o o o o o o o x x x x o o o o o o o D
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 */
	@Test
	public void test3() {
		map = new Map(0.02f,     // 1cm grid size
 			   		  60 * 2,    
 			   		  60 * 2);
		pathFinding = new PathFinding(map);
		try {
			final float offset = ((float)1)/100 * (float)(1/2);
			// Rectangular obstacle 1
			for (int col = 60; col <= 90; col += 1) {
				for (int row = 50; row <= 90; row += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			}
			// Rectangular obstacle 2
			for (int col = 20; col <= 50; col += 1) {
				for (int row = 10; row <= 70; row += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			}
			// Crater
			for (int row = 10; row <= 50; row += 1) {
				int inc = 8;
				if (row == 10 || row == 50) {
					inc = 1;
				}
				for (int col = 7; col <= 15; col += inc) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.CRATER, loc, 1.0f);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		PathNode robotPosNode = new PathNode(30.0f, 0f);
		PathNode destPosNode = new PathNode(80.0f, 100.0f);
		pathFinding.setDistanceFromPoint(distanceFromPoint);
		List<PathNode> foundPath = pathFinding.searchPath(robotPosNode, destPosNode);
		Map.GridLocation robotLoc = new Map.GridLocation(Math.round(robotPosNode.xMetres), Math.round(robotPosNode.yMetres));
		Map.GridLocation destLoc = new Map.GridLocation(Math.round(destPosNode.xMetres)+1, Math.round(destPosNode.yMetres)+1);
		try {
			map.set(Map.Property.TRACKS, destLoc, 1.0f);
			map.set(Map.Property.TRACKS, robotLoc, 1.0f);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (PathNode point: foundPath) {
			Map.GridLocation robotPath = new Map.GridLocation(Math.round(point.xMetres), Math.round(point.yMetres));
			try {
				map.set(Map.Property.TRACKS, robotPath, 5.0f);
			} catch (Exception e) {
				
			}
		}
		
		if (Settings.Debug.showMap) {
			map.print((Point)robotPosNode, new Point());
		}
	}
	
	/**
	 * An obstacle with an "L" shape rotated 90 degrees clockwise as shown 
	 * below, "x" represents the obstacles, "R" represents the robot and "D" 
	 * represents the destination.
	 * 
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o x x x x x x x x x x x x
	 * o o o o o o o o o o o o x x x x x x x x x x x x
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * R o o o o o o o o o o o x x x x o o o o o o o D
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 * o o o o o o o o o o o o x x x x o o o o o o o o
	 */
	@Test
	public void test4() {
		map = new Map(0.01f,     // 1cm grid size
 			   		  20 * 2,    
 			   		  20 * 2);
		pathFinding = new PathFinding(map);
		try {
			int xLimit = 6;
			final float offset = ((float)1)/100 * (float)(1/2);
			for (int y = 19; y >= -5f; y -= 1f) {
				if (y <= 6) {
					xLimit = 20;
				}
				for (int x = 1; x < xLimit; x += 1f) {
					map.set(Map.Property.OBSTACLE, 
							new Point((float)x/100 - offset + ((float)1)/100 * 0.5f, 
									  (float)y/100 - offset + ((float)1)/100 * 0.5f), 
							1.0f);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		PathNode robotPosNode = new PathNode(20.0f, 0f);
		PathNode destPosNode = new PathNode(30.0f, 35.0f);
		pathFinding.setDistanceFromPoint(distanceFromPoint);
		List<PathNode> foundPath = pathFinding.searchPath(robotPosNode, destPosNode);
		Map.GridLocation robotLoc = new Map.GridLocation(Math.round(robotPosNode.xMetres), Math.round(robotPosNode.yMetres));
		Map.GridLocation destLoc = new Map.GridLocation(Math.round(destPosNode.xMetres), Math.round(destPosNode.yMetres));
		try {
			map.set(Map.Property.TRACKS, destLoc, 1.0f);
			map.set(Map.Property.TRACKS, robotLoc, 1.0f);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (PathNode point: foundPath) {
			Map.GridLocation robotPath = new Map.GridLocation(Math.round(point.xMetres), Math.round(point.yMetres));
			try {
				map.set(Map.Property.TRACKS, robotPath, 5.0f);
			} catch (Exception e) {
				
			}
		}
		assertEquals("Testing how many points are in the found path...", 0, foundPath.size());
		if (Settings.Debug.showMap) {
			map.print((Point)robotPosNode, new Point());
		}
	}

	/**
	 * An obstacle with an "L" shape rotated 90 degrees clockwise as shown 
	 * below, "x" represents the obstacles, "R" represents the robot and "D" 
	 * represents the destination.
	 * 
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o D
	 * o o o o x x x x o o o o o o o o o o o o o o o o
	 * o o o o x o o x o o o o o o x x x x x o o o o o
	 * o o o o x o o x o o x x o o x o o o x o o o o o
	 * o o o o x o o x o o x x o o x o o o x o o o o o
	 * o o o o x x x x o o x x o o x o o o x o o o o o
	 * R o o o o o o o o o x x o o x o o o x o o o o o
	 * o o o o o o o o o o x x o o x x x x x o o o o o
	 * o o o o o o o o o o x x o o o o o o o o o o o o
	 */
	@Test
	public void test5() {
		map = new Map(0.02f,     // 1cm grid size
 			   		  60 * 2,    
 			   		  60 * 2);
		pathFinding = new PathFinding(map);
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
			for (int row = 90; row <= map.rows()-1; row += 1) {
				for (int col = 30; col <= 36; col += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			} 
			
			// Rectangular obstacle in the upper middle
			for (int row = 0; row <= 40; row += 1) {
				for (int col = 30; col <= 36; col += 1) {
					Map.GridLocation loc = new Map.GridLocation(row, col);
					map.set(Map.Property.OBSTACLE, loc, 1.0f);
				}
			} 
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		PathNode robotPosNode = new PathNode(20.0f, 0f);
		PathNode destPosNode = new PathNode(3.0f, 50.0f);
		pathFinding.setDistanceFromPoint(distanceFromPoint);
		List<PathNode> foundPath = pathFinding.searchPath(robotPosNode, destPosNode);
		Map.GridLocation robotLoc = new Map.GridLocation(Math.round(robotPosNode.xMetres), Math.round(robotPosNode.yMetres));
		Map.GridLocation destLoc = new Map.GridLocation(Math.round(destPosNode.xMetres), Math.round(destPosNode.yMetres));
		try {
			map.set(Map.Property.TRACKS, destLoc, 1.0f);
			map.set(Map.Property.TRACKS, robotLoc, 1.0f);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for (PathNode point: foundPath) {
			Map.GridLocation robotPath = new Map.GridLocation(Math.round(point.xMetres), Math.round(point.yMetres));
			try {
				map.set(Map.Property.TRACKS, robotPath, 1.0f);
			} catch (Exception e) {
				
			}
		}
		assertFoundPath(foundPath);
		
		if (Settings.Debug.showMap) {
			map.print((Point)robotPosNode, new Point());
		}
	}
	
	/**
	 * Assert if the found path doesn't include any grid that has 
	 * obstacle or part of a crater
	 */
	private void assertFoundPath(List<PathNode> foundPath) {
		for (PathNode point: foundPath) {
			Map.Property prop;
			try {
				prop = map.getProperty(convertGridLocationToPoint(point));
				assertEquals("Testing for obstacle...", true, prop != Map.Property.OBSTACLE);
				assertEquals("Testing for creater...", true, prop != Map.Property.CRATER);
				assertEquals("Testing for no go zone...", true, prop != Map.Property.NO_GO_ZONE);
				assertEquals("Testing for border...", true, prop != Map.Property.BORDER);
			} catch (Map.OutOfMapBoundsException e) {
				System.err.println("Out of bounds.");
			}
		}
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
}
