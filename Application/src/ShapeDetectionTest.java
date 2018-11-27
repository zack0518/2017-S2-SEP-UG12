import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ShapeDetectionTest {
	Map map;
	ShapeDetection shapeDetection;
	
	public ShapeDetectionTest(){
		this.map = new Map(0.01f,     // 1cm grid size
 			               20 * 2,    // 119cm is the maximum edge length of an A0 sheet.
 			               20 * 2);
		shapeDetection = new ShapeDetection(map);
	}
	/**
	 * 
	 * o o o o o o o o o o o o o o o . . o o o o o o o
	 * o o o o o o o o o o o o o o o . . o o o o o o o
	 * o o o o o o o o o o o o o o o . . o o o o o o o
	 * o o o o o o o o o o o o o o o . . o o o o o o o
	 * o o o o o o o o o o o o o o o . . o o o o o o o
	 * o o o o o . o . o o o o o o o . . o o o o o o o
	 * o . . . . o o o o o o o o o o o o o o o o o o o
	 * . o . . o o o o o o o o o o o o o o o o o o o D
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 * o o o o o o o o o o o o o o o o o o o o o o o o
	 */
	@Test
	public void test1() {
		
		map = new Map(0.01f,     // 1cm grid size
		   		  20 * 2,    
		   		  20 * 2);
		
		shapeDetection = new ShapeDetection(map);
		
		try {	
				//Setup dummy map
				
				for (int col = 19; col <= 20; col += 1) {
					for (int row = 30; row <= 39; row += 1) {
						Map.GridLocation loc = new Map.GridLocation(row, col);
						map.set(Map.Property.TRACKS, loc, 1.0f);
					}
				}
				
				Map.GridLocation loc0 = new Map.GridLocation(5, 3);
				map.set(Map.Property.TRACKS, loc0, 1.0f);
				Map.GridLocation loc1 = new Map.GridLocation(4, 2);
				map.set(Map.Property.TRACKS, loc1, 1.0f);
				Map.GridLocation loc2 = new Map.GridLocation(4, 4);
				map.set(Map.Property.TRACKS, loc2, 1.0f);
				Map.GridLocation loc3 = new Map.GridLocation(5, 5);
				map.set(Map.Property.TRACKS, loc3, 1.0f);
				Map.GridLocation loc4 = new Map.GridLocation(5, 4);
				map.set(Map.Property.TRACKS, loc4, 1.0f);
				Map.GridLocation loc5 = new Map.GridLocation(4, 5);
				map.set(Map.Property.TRACKS, loc5, 1.0f);
				Map.GridLocation loc6 = new Map.GridLocation(5, 6);
				map.set(Map.Property.TRACKS, loc6, 1.0f);			
				Map.GridLocation loc7 = new Map.GridLocation(7, 7);
				map.set(Map.Property.TRACKS, loc7, 1.0f);
				Map.GridLocation loc8 = new Map.GridLocation(7, 9);
				map.set(Map.Property.TRACKS, loc8, 1.0f);
				
				shapeDetection.detectionIntegration();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
		if (Settings.Debug.showMap) {
			map.print(new Point(), new Point());
		}
	}
}
