import static org.junit.Assert.*;

import org.junit.Test;

public class MapTest {    
    @Test
    public void test() {
        final int[] sizes = {1, 2, 3, 4, 5, 9, 10, 15, 99, 100 };
        final float[] steps = {0.001f, 0.1f, 0.37f, 1.0f, 1.006f, 10.0f, 1000.0f };
        for (int size: sizes) {
            for (float step: steps) {
                testGetSetMap(size, step);
            }
        }
    }
    
    public void testGetSetMap(int size, float step) {
        final float offset = step * (float)(size / 2);
        Map map = new Map(step, size, size);
        
        // (4, 4) is the centre position (0.0, 0.0).
        for (int i = 0; i < size; i += 1) {
            for (int j = 0; j < size; j += 1) {
                Point pos = new Point((float)j * step - offset + step * 0.5f,
                                      (float)i * step - offset + step * 0.5f);
                // Test getting.
                for (Map.Property prop : Map.Property.values()) {
                    if (prop == Map.Property.NONE) continue;
                    try {
                        float value_at = map.get(prop, pos);
                        assertTrue(String.format("size=%d, step=%f, row=%d, col=%d, x=%f, y=%f",
                                                 size, step, i, j, pos.xMetres, pos.yMetres),
                                   value_at < 0.5f);
                        map.set(prop, pos, 1.0f);
                    } catch (Map.OutOfMapBoundsException e) {
                        fail(String.format(
                            "Exception thrown accessing map at position "
                            + "row=%d,col=%d,x=%f,y=%f",
                            i, j, pos.xMetres, pos.yMetres));
                    }
                }
                
                // Test setting.
                for (Map.Property prop : Map.Property.values()) {
                    if (prop == Map.Property.NONE) continue;
                    try {
                        float value_at = map.get(prop, pos);
                        assertEquals(value_at, 1.0f, 0.000001f);
                    } catch (Map.OutOfMapBoundsException e) {
                        fail(String.format(
                            "Exception thrown accessing map at position "
                            + "row=%d,col=%d,x=%f,y=%f, during second loop only",
                            i, j, pos.xMetres, pos.yMetres));
                    }
                }
            }
        }
    }
    
    @Test
    public void testGetColor() {
        Map map = new Map(1.0f, 20, 20); // from -10 to 10 in x, y
        final RGBColor[] colors = {
                //new RGBColor(0.0f, 0.0f, 0.0f),
                new RGBColor(0.0f, 0.1f, 0.8f),
                new RGBColor(0.0f, 0.8f, 0.1f),
                new RGBColor(0.4f, 0.7f, 0.2f),
                new RGBColor(1.0f, 0.0f, 0.0f)
        };
        final Point[] positions = {
                new Point(0.0f, 0.0f),
                new Point(5.0f, 5.0f),
                new Point(-2.0f, 1.0f),
                new Point(4.0f, -9.0f),
                new Point(-8.0f, 0.04f)
        };
        
        for (Map.Property prop : Map.Property.values()) {
            if (prop == Map.Property.NONE) continue;
            for (RGBColor color : colors) {
                map.setPropertyColor(prop, color);
                for (Point pos : positions) {
                    System.out.printf("prop: %s, color: (%f,%f,%f), pos: (%f, %f)%n",
                            prop, color.r, color.g, color.b, pos.xMetres, pos.yMetres);
                    try {
                        // Check that the initial color is the base color.
                        RGBColor beforeColor = map.getColorAtPosition(pos);
                        assertEquals(beforeColor.r, 1.0f, 0.001f);
                        assertEquals(beforeColor.g, 1.0f, 0.001f);
                        assertEquals(beforeColor.b, 1.0f, 0.001f);
                        
                        // Check that the color is changed with the property.
                        map.setPropertyColor(prop, color);
                        map.set(prop, pos, 1.0f);
                        RGBColor propertyColor = map.getColorAtPosition(pos);
                        System.out.printf("property color: (%f, %f, %f)%n", propertyColor.r, propertyColor.g, propertyColor.b);
                        assertEquals(color.r, propertyColor.r, 0.001f);
                        assertEquals(color.g, propertyColor.g, 0.001f);
                        assertEquals(color.b, propertyColor.b, 0.001f);
                        
                        // Check that the color is restored when the property is decreased.
                        map.set(prop, pos, 0.05f);
                        RGBColor afterColor = map.getColorAtPosition(pos);
                        assertEquals(afterColor.r, 1.0f, 0.001f);
                        assertEquals(afterColor.g, 1.0f, 0.001f);
                        assertEquals(afterColor.b, 1.0f, 0.001f);
                    } catch(Map.OutOfMapBoundsException e) {
                        fail("Map out of bounds");
                    }
                }
            }
        }
        
    }
    
    //@Test
    public void testPrint() {
        final int size = 8;
        final float step = 1.0f;
        Map map = new Map(step, size, size);
        System.out.printf("Visual testing of map (size=%d, step=%f)%n", size, step);
        
        map.printProperty(Map.Property.OBSTACLE);
        try {
            System.out.println("Setting (0.0, 0.0) and (2.5, 2.5)");
            map.set(Map.Property.OBSTACLE, new Point(0.0f, 0.0f), 1.0f);
            map.set(Map.Property.OBSTACLE, new Point(2.5f, 2.6f), 1.0f);
        } catch (Map.OutOfMapBoundsException e) {
            // pass
        }
        map.printProperty(Map.Property.OBSTACLE);
    }
    
    @Test
    public void testClosestObstacle() {
        final int size = 10;
        final float step = 1.0f;
        Map map = new Map(step, size, size);
        try {
            map.set(Map.Property.OBSTACLE, new Point(4.0f, 0.0f), 1.0f);
            map.set(Map.Property.OBSTACLE, new Point(0.0f, 1.0f), 1.0f);
            assertEquals(
                    map.distanceToNearestObstacle(new Point(0.0f, 0.0f)),
                    1.0f,
                    0.001f);
            assertEquals(
                    map.distanceToNearestObstacle(new Point(3.0f, 0.0f)),
                    (float)Math.sqrt(2.0),
                    0.001f);
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
    }

}
