/**
 * Contains application-wide constant values.
 * @author jkortman
 */
public class Settings {
    /**
     * Constructor is not allowed, Settings is not a class that can be instantiated.
     */
    private Settings() {}
    
    // The allowed MSE error for colors.
    public static final float allowedColorMSE = 1.0f;           // TODO: NEEDS TO BE TESTED AND REVISED
    public static final float allowedGrayscaleError = 0.09f;    // TODO: NEEDS TO BE TESTED AND REVISED
    // The reference colors for color detection.
    public static class ReferenceColors {
        public static final RGBColor black  = new RGBColor(0.013722f, 0.015861f, 0.013028f);
        public static final RGBColor white  = new RGBColor(0.146857f, 0.171757f, 0.135786f);
        public static final RGBColor purple = new RGBColor(0.095f, 0.095f, 0.115f); // TODO
        public static final RGBColor green  = new RGBColor(0.016f, 0.060f, 0.021f); // TODO
        public static final RGBColor blue   = new RGBColor(0.037f, 0.141f, 0.145f); // TODO
    }
    
    /**
     * Map properties.
     */
    public static class Map {
        public static final int   rows      = 120;
        public static final int   cols      = 120;
        public static final float gridSize  = 0.02f; // metres
    }
    
    /**
     * Debugging output settings.
     */
    public static class Debug {
    	// Prints from Robot.java
    	public static final boolean showUpdatePositionOutput	  = false;
        public static final boolean showRobotMessages			  = false;
        public static final boolean showRobotCommands			  = true;
        public static final boolean showRobotJobStatus            = true;
        public static final boolean showRobotSetup				  = true;
    	
        // Prints from Handler
        public static final boolean showCommandParameters		  = false;
        public static final boolean showManualCommands 			  = true;
        public static final boolean showColorSensor               = false;
        public static final boolean showLocation                  = false;
        public static final boolean usePresetMap                  = false;
        
        // Prints from other sources
        public static final boolean showMap                       = true;
        public static final boolean showDetectedColorPropery      = false;
        public static final boolean showColorSensorInterpretation = false;
    }
}
