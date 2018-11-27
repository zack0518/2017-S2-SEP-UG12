import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class MSEColorSensorInterpreterTest {
    @Test
    public void test() {
        MSEColorSensorInterpreter csi = new MSEColorSensorInterpreter(0.005f);
        Map map = new Map(0.01f, 20, 20); // 20cm * 20cm
        
        // Assign colors to properties.
        csi.setExpectedColor(Map.Property.NONE,      new RGBColor(1.0f, 1.0f, 1.0f));
        csi.setExpectedColor(Map.Property.CRATER,    new RGBColor(0.0f, 0.0f, 0.0f));
        csi.setExpectedColor(Map.Property.RADIATION, new RGBColor(0.2f, 1.0f, 0.2f));

        // Check successful identification and map updating for the assigned colors.
        // Crater check (exact color).
        try {
            assertEquals(csi.interpret(new RGBColor(0.0f, 0.0f, 0.0f), map, new Point(0.05f, -0.03f)),
                         Map.Property.CRATER);
            assertEquals(map.get(Map.Property.CRATER, new Point(0.05f, -0.03f)), 1.0f, 0.01f);
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
        
        // Radiation check (exact color)
        try {
            assertEquals(csi.interpret(new RGBColor(0.2f, 1.0f, 0.2f), map, new Point(-0.01f, 0.04f)),
                         Map.Property.RADIATION);
            assertEquals(map.get(Map.Property.RADIATION, new Point(-0.01f, 0.04f)), 1.0f, 0.01f);
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
        
        // None check (exact color)
        try {
            assertEquals(csi.interpret(new RGBColor(1.0f, 1.0f, 1.0f), map, new Point(-0.01f, 0.04f)),
                         Map.Property.NONE);
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
        
        // Bad color check.
        try {
            assertEquals(csi.interpret(new RGBColor(0.95f, 0.95f, 0.7f), map, new Point(0.01f, 0.02f)),
                         Map.Property.NONE);
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
        
        // Check with some error.
        // If crater color is black (0.0f...) and allowable MSE is 0.005f,
        // then (0.12f, 0.0f, 0.0f) with MSE=0.004800f should JUST pass,
        // and (0.0f, 0.123f, 0.0f) with MSE=0.005043f should JUST fail.
        try {
            assertEquals(csi.interpret(new RGBColor(0.12f, 0.0f, 0.0f), map, new Point(0f, 0f)),
                         Map.Property.CRATER);
            assertEquals(csi.interpret(new RGBColor(0.0f, 0.124f, 0.0f), map, new Point(0f, 0f)),
                         Map.Property.NONE);
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
    }

    @Test
    public void testSetColors() {
        MSEColorSensorInterpreter csi = new MSEColorSensorInterpreter(0.05f);
        RGBColor white = new RGBColor(0.120f, 0.140f, 0.110f);
        RGBColor black = new RGBColor(0.014f, 0.018f, 0.010f);
        RGBColor red   = new RGBColor(0.138f, 0.022f, 0.019f);
        RGBColor green = new RGBColor(0.018f, 0.058f, 0.019f);
        RGBColor blue  = new RGBColor(0.025f, 0.032f, 0.057f);
        // This map contains the expected colors for each feature on the map.
        HashMap<Map.Property, RGBColor> colorSensorColors = new HashMap<Map.Property, RGBColor>();
        colorSensorColors.put(Map.Property.NONE,             white);
        colorSensorColors.put(Map.Property.CRATER,           black);
        colorSensorColors.put(Map.Property.RADIATION,        green);
        colorSensorColors.put(Map.Property.TRACKS_FOOTSTEPS, red);
        colorSensorColors.put(Map.Property.TRACKS_VEHICLE,   blue);
        csi.setExpectedColors(colorSensorColors);
        
        // Set up dummy map.
        Map map = new Map(1.0f, 1, 1);
        
        Map.Property[] detectableAndAccessibleProps = {
                Map.Property.CRATER, Map.Property.RADIATION,
                Map.Property.TRACKS_FOOTSTEPS, Map.Property.TRACKS_VEHICLE
        };
        Point pos = new Point(0.5f, 0.5f);
        try {
            Map.Property interpretation = csi.interpret(white, map, pos);
            assertEquals(interpretation, Map.Property.NONE);
            for (Map.Property prop : detectableAndAccessibleProps) {
                map.set(prop, pos, 1.0f);
                assertEquals(csi.interpret(colorSensorColors.get(prop), map, pos), prop);
                map.set(prop, pos, 0.0f);
            }
        } catch (Map.OutOfMapBoundsException e) {
            fail("Out of bounds");
        }
    }
}
