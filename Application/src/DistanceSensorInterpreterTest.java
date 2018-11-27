import static org.junit.Assert.*;

import org.junit.Test;

public class DistanceSensorInterpreterTest {

    @Test
    public void test() {
        Map map = new Map(0.01f, 20, 20); // 20cm * 20cm
        DistanceSensorInterpreter dsi = new DistanceSensorInterpreter();
        
        // At a heading of 0.
        assertEquals(dsi.interpret(0.055f, map, new Point(0.0f, 0.0f), 0.0f),
                     DistanceSensorInterpreter.Interpretation.SUCCESS);
        try {
            assertEquals(map.get(Map.Property.OBSTACLE, new Point(0.0f, 0.055f)), 1.0f, 0.001f);
        } catch (Map.OutOfMapBoundsException e) {
            fail("Out of bounds");
        }
        
        // At a heading of 270.
        assertEquals(dsi.interpret(0.04f, map, new Point(0.03f, -0.02f), 270.0f),
                     DistanceSensorInterpreter.Interpretation.SUCCESS);
        try {
            assertEquals(map.get(Map.Property.OBSTACLE, new Point(0.07f, -0.02f)), 1.0f, 0.001f);
        } catch (Map.OutOfMapBoundsException e) {
            fail("Out of bounds");
        }
        
        // At a heading of 140.
        assertEquals(dsi.interpret(0.07f, map, new Point(0.02f, 0.03f), 140.0f),
                     DistanceSensorInterpreter.Interpretation.SUCCESS);
        try {
            assertEquals(map.get(Map.Property.OBSTACLE,
                                 new Point(0.02f - 0.07f * (float)Math.cos(Math.toRadians(50.0f)),
                                           0.03f - 0.07f * (float)Math.sin(Math.toRadians(50.0f)))
                                 ),
                         1.0f, 0.001f);
        } catch (Map.OutOfMapBoundsException e) {
            fail("Out of bounds");
        }
        
        // Test when provided with infinity.
        assertEquals(dsi.interpret(Float.POSITIVE_INFINITY, map, new Point(-0.07f, -0.05f), 70.0f),
                     DistanceSensorInterpreter.Interpretation.INFINITY);
        assertEquals(dsi.interpret(Float.NEGATIVE_INFINITY, map, new Point(10.0f, 30.0f), 130.0f),
                     DistanceSensorInterpreter.Interpretation.INFINITY);
        
        // Test when given an out-of-bounds location.
        assertEquals(dsi.interpret(17.0f, map, new Point(1.0f, 2.0f), 170.0f),
                     DistanceSensorInterpreter.Interpretation.OUT_OF_BOUNDS);
    }
}
