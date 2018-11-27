import static org.junit.Assert.*;

import org.junit.Test;

public class NoGoZonesMarkerTest {
	Map map;
	NoGoZonesMarker ngzMarker;
	
	/**
	 * Tests if it marks a rectangular area when the start and end points are inside the bounds of the map.
	 * Given points are: upper left(start) and bottom right(end)
	 */
	@Test
	public void test1() {
		map = new Map(Settings.Map.gridSize, Settings.Map.rows, Settings.Map.cols);
		ngzMarker = new NoGoZonesMarker(map);
		int width = 10;
		int height = 20;
		Map.GridLocation startPoint = new Map.GridLocation(0, 0);
		Map.GridLocation endPoint = new Map.GridLocation(startPoint.row + height, startPoint.col + width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(startPoint, width, height);
		map.print(new Point(), new Point());
	}
	
	/**
	 * Tests if it can mark multiple rectangular area in the map.
	 * Given points are: upper left(start) and bottom right(end)
	 */
	@Test
	public void test2() {
		map = new Map(Settings.Map.gridSize, Settings.Map.rows, Settings.Map.cols);
		ngzMarker = new NoGoZonesMarker(map);
		// No-Go-Zone 1
		int width = 10;
		int height = 20;
		Map.GridLocation startPoint = new Map.GridLocation(0, 0);
		Map.GridLocation endPoint = new Map.GridLocation(startPoint.row + height, startPoint.col + width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(startPoint, width, height);
		// No-Go-Zone 2
		width = 15;
		height = 30;
		startPoint = new Map.GridLocation(6, 3);
		endPoint = new Map.GridLocation(startPoint.row + height, startPoint.col + width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(startPoint, width, height);
		
		map.print(new Point(), new Point());
	}
	
	@Test
	/**
	 * Tests if it can mark multiple rectangular area in the map.
	 * Given points are: upper left(end) and bottom right(start)
	 */
	public void test3() {
		map = new Map(Settings.Map.gridSize, Settings.Map.rows, Settings.Map.cols);
		ngzMarker = new NoGoZonesMarker(map);
		// No-Go-Zone 1
		int width = 3;
		int height = 4;
		Map.GridLocation startPoint = new Map.GridLocation(23, 20);
		Map.GridLocation endPoint = new Map.GridLocation(startPoint.row - height, startPoint.col - width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(endPoint, width, height);
		// No-Go-Zone 2
		width = 5;
		height = 3;
		startPoint = new Map.GridLocation(10, 8);
		endPoint = new Map.GridLocation(startPoint.row - height, startPoint.col - width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(endPoint, width, height);
		
		map.print(new Point(), new Point());
	}
	
	@Test
	/**
	 * Tests if it can mark multiple rectangular area in the map.
	 * Given points are: bottom left(end) and upper right(start)
	 */
	public void test4() {
		map = new Map(Settings.Map.gridSize, Settings.Map.rows, Settings.Map.cols);
		ngzMarker = new NoGoZonesMarker(map);
		// No-Go-Zone 1
		int width = 3;
		int height = 4;
		Map.GridLocation startPoint = new Map.GridLocation(23 - height, 20);
		Map.GridLocation endPoint = new Map.GridLocation(startPoint.row + height, startPoint.col - width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(new Map.GridLocation(startPoint.row, endPoint.col), width, height);
		// No-Go-Zone 2
		width = 5;
		height = 3;
		startPoint = new Map.GridLocation(10 - height, 8);
		endPoint = new Map.GridLocation(startPoint.row + height, startPoint.col - width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(new Map.GridLocation(startPoint.row, endPoint.col), width, height);
		
		map.print(new Point(), new Point());
	}
	
	@Test
	/**
	 * Tests if it can mark multiple rectangular area in the map.
	 * Given points are: bottom left(start) and upper right(end)
	 */
	public void test5() {
		map = new Map(Settings.Map.gridSize, Settings.Map.rows, Settings.Map.cols);
		ngzMarker = new NoGoZonesMarker(map);
		// No-Go-Zone 1
		int width = 3;
		int height = 4;
		Map.GridLocation startPoint = new Map.GridLocation(23 + height, 18);
		Map.GridLocation endPoint = new Map.GridLocation(startPoint.row - height, startPoint.col + width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(new Map.GridLocation(endPoint.row, startPoint.col), width, height);
		// No-Go-Zone 2
		width = 5;
		height = 3;
		startPoint = new Map.GridLocation(6 + height, 3);
		endPoint = new Map.GridLocation(startPoint.row - height, startPoint.col + width);
		ngzMarker.mark(startPoint, endPoint);
		assertNoGoZoneArea(new Map.GridLocation(endPoint.row, startPoint.col), width, height);
		
		map.print(new Point(), new Point());
	}

	private void assertNoGoZoneArea(Map.GridLocation startPoint, int width, int height) {
		Map.GridLocation endPoint = new Map.GridLocation(startPoint.row + height, startPoint.col + width);
		for (int row = (int)startPoint.row; row <= endPoint.row; row += 1) {
			for (int col = (int)startPoint.col; col <= endPoint.col; col += 1) {
				Map.GridLocation gridLoc = new Map.GridLocation(row, col);
				assertEquals("Testing if the point on the map is marked as a no-go-zone...",map.getProperty(gridLoc), Map.Property.NO_GO_ZONE);
			}
		}
	}
}
