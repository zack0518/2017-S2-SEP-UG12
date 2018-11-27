import java.util.HashMap;

/**
 * Converts detected colors to map properties and loads them into the map.
 * @author jkortman
 */
public class ColorInterpreter {
    public ColorInterpreter(HashMap<ColorDetector.Color, Map.Property> key) {
        this.key = key;
    }
    
    /**
     * Interpret a detected color as a feature.
     * @param c     The detected color from the ColorDetector.
     * @param map   The map to load the interpretation into.
     * @param pos   The position of the detected color.
     * @return      The interpreted property, or null if no successful interpretation.
     * @throws      Map.OutOfMapBoundsException
     */
    public Map.Property interpret(ColorDetector.Color c, Map map, Point pos) throws Map.OutOfMapBoundsException {
        Map.Property prop = key.get(c);
        if (prop == null) {
            throw new RuntimeException("Color " + c.toString() + " is not in the ColorInterpreter key");
        }
        if (Settings.Debug.showColorSensorInterpretation) {
            System.out.printf("Interpretation at pos=(%f, %f): %s -> %s%n",
                    pos.xMetres, pos.yMetres, c.toString(), prop.toString());
        }
        
        // Update the map.
        if (prop != Map.Property.NONE) {
            map.set(prop, pos, 1.0f);
        } 
        
        if (Settings.Debug.showDetectedColorPropery) {
            if (prop == null)   System.out.printf("Property detected: null%n");
            else                System.out.printf("Property detected: %s%n", prop.name());
        }
        
        return prop;
    }

    private HashMap<ColorDetector.Color, Map.Property> key;
}
