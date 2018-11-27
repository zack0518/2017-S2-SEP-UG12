import java.util.HashMap;
import java.util.ArrayList;

/**
 * Takes raw RGB color data and interprets it as some kind of terrain element.
 * @author jkortman
 */
public class MSEColorSensorInterpreter {
    /**
     * Default constructor is disallowed.
     */
    @SuppressWarnings("unused")
    private MSEColorSensorInterpreter() {}
    
    /** 
     * Create a new interpreter.
     */
    public MSEColorSensorInterpreter(float allowedError) {
        properties = new ArrayList<Map.Property>(Map.Property.values().length);
        expectedColors = new HashMap<Map.Property, RGBColor>();
        setAllowableError(allowedError);
    }
    
    /**
     * Interpret a detected color as a feature.
     * @param c     The detected color.
     * @param map   The map to load the interpretation into.
     * @param pos   The position of the detected color.
     * @return      The interpreted property, or null if no successful interpretation.
     * @throws      Map.OutOfMapBoundsException
     */
    public Map.Property interpret(RGBColor c, Map map, Point pos) throws Map.OutOfMapBoundsException {
        // The current minimum-error and associated property.
        float minError = Float.MAX_VALUE;
        Map.Property minProp = Map.Property.NONE;
        
        for (Map.Property prop : properties) {
            RGBColor propColor = expectedColors.get(prop);
            if (propColor == null) {
                throw new RuntimeException(String.format("Expected color for property %s not set", prop.name()));
            }
            float err = mse(c, propColor);
            if (err <= allowedError) {
                // We may want to keep track of the number of possible candidate
                // properties for this interpretation here.
                // e.g. candidates += 1
                if (err < minError) {
                    minError = err;
                    minProp = prop;
                }
            }
        }
        
        assert minProp != null;
        
        // Update the map.
        if (minProp != Map.Property.NONE) {
            // If we identified a color-based property,
            // Vary certainty from 1.0 at 0 error to 0.5 at max allowable error.
            float certainty = 0.5f + 0.5f * (allowedError - minError) / allowedError;
            if (Settings.Debug.showColorSensorInterpretation) {
                System.out.printf("Interpretation pos=(%f, %f): %s, err %f -> certainty = %f\n",
                        pos.xMetres, pos.yMetres, minProp.toString(), minError, certainty);
            }
            map.set(minProp, pos, certainty);
        } 
        
        if (Settings.Debug.showDetectedColorPropery) {
            if (minProp == null) System.out.printf("Property detected: NONE%n");
            else                 System.out.printf("Property detected: %s%n", minProp.name());
        }
        
        return minProp;
    }
    
    /**
     * Set the maximum allowable error for a color to be accepted as a feature.
     * @param error the maximum allowed mean-squared error.
     */
    public void setAllowableError(float error) {
        allowedError = error;
    }
    
    /**
     * Set the expected base color.
     * @param color     the expected color of the map base.
     */
    public void setExpectedBaseColor(RGBColor color) {
        setExpectedColor(null, color);
    }
    
    /**
     * Set the expected color of a property.
     * @param prop the property
     * @param c    the expected color of the property
     */
    public void setExpectedColor(Map.Property prop, RGBColor c) {
        properties.add(prop);
        expectedColors.put(prop, c);
    }
    
    /**
     * Set expected colors through a HashMap.
     * @param colors A HashMap that maps properties to their displayed colors.
     */
    public void setExpectedColors(HashMap<Map.Property, RGBColor> colors) {
        for (Map.Property prop : colors.keySet()) {
            setExpectedColor(prop, colors.get(prop));
        }
    }
    
    /**
     * Calculate the mean-squared error of two colors.s
     * @param a the first color
     * @param b the second color
     * @return  the mean squared error
     */
    public static float mse(RGBColor a, RGBColor b) {
        double sum = Math.pow(a.r - b.r, 2.0f)
                   + Math.pow(a.g - b.g, 2.0f)
                   + Math.pow(a.b - b.b, 2.0f);
        return (1.0f / 3.0f) * (float)sum;
    }

    // The expected colors of each property.
    HashMap<Map.Property, RGBColor> expectedColors;
    // The allowed MSE error.
    float allowedError;
    // The properties to check for.
    ArrayList<Map.Property> properties;
}
