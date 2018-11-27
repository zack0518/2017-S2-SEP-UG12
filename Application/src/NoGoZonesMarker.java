/**
 * This class handles the marking of the No-Go-Zones in the map object.
 * @author cyrusvillacampa
 *
 */

public class NoGoZonesMarker {
	private Map map;
	public NoGoZonesMarker(Map map) {
		this.map = map;
	}
	
	/**
	 * This marks the points in the map that are in the No-Go-Zones.
	 * @param start	- Upper left point of the rectangle 
	 * @param end	- Lower right point of the rectangle
	 */
	public void mark(Map.GridLocation startPoint, Map.GridLocation endPoint) {
		Map.GridLocation start = new Map.GridLocation(startPoint.row, startPoint.col);
		Map.GridLocation end = new Map.GridLocation(endPoint.row, endPoint.col);
		if (startPoint.row > endPoint.row && startPoint.col < endPoint.col) {		// Given points are bottom left(start) and upper right(end)
			int height = start.row - end.row;
			start.row -= height;
			end.row += height;
		} else if (startPoint.row < endPoint.row && startPoint.col > endPoint.col) { // Given points are bottom left(end) and upper right(start)
			int width = start.col - end.col;
			start.col -= width;
			end.col += width;
		} else if (startPoint.row > endPoint.row && startPoint.col > endPoint.col) {	// Given points are upper left(end) and bottom right(start)
			Map.GridLocation temp = new Map.GridLocation(start.row, start.col);
			start = end;
			end = temp;
		}
		for (int row = start.row; row <= end.row; row += 1) {
			for (int col = start.col; col <= end.col; col += 1) {
				Map.GridLocation gridLoc = new Map.GridLocation(row, col);
				try {
					map.set(Map.Property.NO_GO_ZONE, gridLoc, 1.0f);
				} catch (Map.OutOfMapBoundsException e) {
					System.err.printf("The grid location %s is out of bounds\n", gridLoc);
				}
			}
		}
	}
}
