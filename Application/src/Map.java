import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores processed sensor data received from the robot in a 2D map.
 * @author jkortman
 */
public class Map {
    /**
     * Default map constructor is disallowed.
     */
    @SuppressWarnings("unused")
    private Map() {}
    
    /**
     * Create a Map.
     * @param gridSizeMetres The size of each grid element in metres
     * @param rows           The number of rows in the map
     * @param cols           The number of columns in the map
     */
    public Map(float gridSizeMetres, int rows, int cols) {
        // The map is a stored as a grid of rows * cols. The map represents a
        // 2D coordinate system, with the centre of the map being (0.0, 0.0).
        // The Robot should be initialized at position (0.0, 0.0), facing along
        // the positive y-axis.
        this.gridSizeMetres = gridSizeMetres;
        this.roverLandingSite = new Point(0.0f, 0.0f);
        init(rows, cols);
    }
    
    /**
     * Initialize the internal Map state.
     * @param rows  The number of rows in the map
     * @param cols  The number of columns in the map
     * @param value The initial value of each element in each map property
     */
    private void init(int rows, int cols) {
        this.originLocation = new GridLocation(rows / 2, cols / 2);
        topLeftX = -1.0f * originLocation.row * gridSizeMetres;
        topLeftY = -1.0f * originLocation.col * gridSizeMetres;
        this.numRows = rows;
        this.numCols = cols;
        // Initialize the map colors.
        initColors();
        // Set up the matrix values.
        explored = new Matrix<Boolean>(rows, cols);
        explored.fill(false);
        this.propertyMaps = new HashMap<Property, Matrix<Float>>();
        for (Property prop : accessibleProperties) {
            if (prop == Property.NONE) continue;
            Matrix<Float> map = new Matrix<Float>(rows, cols);
            // Fill with 1.0 for no known property, 0.0 otherwise.
            map.fill(0.0f);
            propertyMaps.put(prop, map);
        }
    }
    
    /**
     * Get the location of the top-left point in the map.
     */
    public Point topLeft() {
        return new Point(topLeftX, topLeftY);
    }
    
    /**
     * Get the number of rows in the map grid.
     */
    public int rows() {
        return this.numRows;
    }
    
    /**
     * Get the number of columns in the map grid.
     */
    public int columns() {
        return this.numCols;
    }
    
    /**
     * Get the grid position of the origin.
     * @return The grid position at (0.0, 0.0) in metre coordinates.
     */
    public GridLocation origin() {
        return originLocation;
    }
    
    /**
     * Check if a GridLocation is out of bounds.
     */
    public boolean isOutOfBounds(GridLocation loc) {
        return (loc.col < 0 || loc.row < 0 || loc.row >= numRows || loc.col >= numCols);
    }
    
    /**
     * Check whether a position is within the bounds of the map.
     * @param  pos  the point to check
     * @return      true if pos is outside of the map bounds.
     */
    public boolean isOutOfBounds(Point pos) {
        return isOutOfBounds(getGridLocation(pos));
    }
    
    /**
     * Get the value of a property at some location using metre coordinates.
     * Will throw OutOfMapBoundsException if the bounds of the map are exceeded.
     * @param   prop the property to read
     * @param   pos  the metre-coordinate location to read from
     * @return       the value at the given location for the given property
     */
    public float get(Property prop, Point pos) throws OutOfMapBoundsException {
        GridLocation loc = getGridLocation(pos);
        return this.get(prop, loc);
    }
    
    /**
     * Get the value of a property at some location using grid coordinates.
     * Will throw OutOfMapBoundsException if the bounds of the map are exceeded.
     * @param   prop the property to read
     * @param   loc  the grid location to read from
     * @return       the value at the given location for the given property
     */
    public float get(Property prop, GridLocation loc) throws OutOfMapBoundsException {
        if (prop == Property.NONE) {
            throw new RuntimeException("get() does not allow use of Map.Property.NONE");
        }
        if (isOutOfBounds(loc)) {
            throw new OutOfMapBoundsException();
        }
        return propertyMaps.get(prop).get(loc.row, loc.col);
    }
    
    /**
     * Get the maximum-likelihood property at a location in the grid.
     * @param  loc  the grid location to check
     * @return      the most likely property, or null if no properties have
     *              likelihoods above 0.5.
     */
    public Map.Property getProperty(GridLocation loc) {
        final float threshold = 0.5f;
        Property maxProp = Property.NONE;
        float maxLikelihood = Float.MIN_VALUE;
        for (Property prop : accessibleProperties) {
            float valueAt = propertyMaps.get(prop).get(loc.row, loc.col);
            if (valueAt > threshold && valueAt > maxLikelihood) {
                maxProp = prop;
                maxLikelihood = valueAt;
            }
        }
        assert maxProp != null;
        return maxProp;
    }
    
    /**
     * Get the maximum-likelihood property at a metre-coordinate position.
     * @param  pos  the position to check in metre coordinates
     * @return      the most likely property, or null if no properties have
     *              likelihoods above 0.5.
     */
    public Map.Property getProperty(Point pos) {
        GridLocation loc = getGridLocation(pos);
        return getProperty(loc);
    }
    
    /**
     * Set the value of a property at a certain metre-coordinate point.
     * Will throw OutOfMapBoundsException if the bounds of the map are exceeded.
     * @param prop  the property to set
     * @param pos   the position where to set the given property in metre coordinates
     * @param value the value to set the property to at position
     */
    public void set(Property prop, Point pos, float value) throws OutOfMapBoundsException {
        GridLocation loc = getGridLocation(pos);
        set(prop, loc, value);
    }
    
    /**
     * Set the value of a property at a certain location in the grid.
     * Will throw OutOfMapBoundsException if the bounds of the map are exceeded.
     * @param prop  the property to set
     * @param loc   the grid coordinates where to set the given property
     * @param value the value to set the property to at position
     */
    public void set(Property prop, GridLocation loc, float value) throws OutOfMapBoundsException {
        if (prop == Property.NONE) {
            throw new RuntimeException("set() does not allow use of Map.Property.NONE");
        }
        if (isOutOfBounds(loc)) {
            throw new OutOfMapBoundsException();
        }
        propertyMaps.get(prop).set(loc.row, loc.col, value);
    }
    
    /**
     * Print the matrix for a property.
     * @param prop the property to print
     */
    public void printProperty(Property prop) {
        if (prop == Property.NONE) {
            throw new RuntimeException("print() does not allow use of Map.Property.NONE");
        }
        System.out.printf("Map[%s]%n", prop.name());
        for (int i = 0; i < numRows; i += 1) {
            for (int j = 0; j < numCols; j += 1) {
                System.out.printf("%.2f ", propertyMaps.get(prop).get(i, j));
            }
            System.out.printf("%n");
        }
    }
    
    /**
     * Print a visual representation of the map.
     */
    public void print(Point robotPosition, Point colorSensorPosition) {
        HashMap<Map.Property, String> propToStr = new HashMap<>();
        propToStr.put(Property.NONE,                "  ");
        propToStr.put(Property.OBSTACLE,            "OO");
        propToStr.put(Property.CRATER,              "{}");
        propToStr.put(Property.RADIATION,           "!!");
        propToStr.put(Property.TRACKS,              "//");
        propToStr.put(Property.BORDER,              "BB");
        propToStr.put(Property.NO_GO_ZONE,          "XX");
        propToStr.put(Property.TRACKS_FOOTSTEPS,    "FF");
        propToStr.put(Property.TRACKS_VEHICLE,      "VV");
        propToStr.put(Property.TRACKS_LANDING,      "LL");
        
        GridLocation robotLocation = null;
        if (robotPosition != null) robotLocation = getGridLocation(robotPosition);
        GridLocation colorSensorLocation = null;
        if (colorSensorPosition != null) colorSensorLocation = getGridLocation(colorSensorPosition);
        System.out.printf("Key: %n");
        for (Map.Property prop : Map.Property.values()) {
            final int pad = 18;
            System.out.printf("    %s:", prop.name());
            for (int i = 0; i < Math.max(pad - prop.name().length(), 0); i += 1) System.out.print(' ');
            System.out.printf("\"%s\"%n", propToStr.get(prop));
        }
        
        // Print header line.
        System.out.printf("+");
        for (int j = 0; j < numCols * 2; j += 1) {
            System.out.printf("-");
        }
        System.out.printf("+%n");
        // Print body.
        for (int i = numRows - 1; i >= 0; i -= 1) {
            System.out.printf("|");
            for (int j = 0; j < numCols; j += 1) {
                if (robotLocation != null && i == robotLocation.row && j == robotLocation.col) {
                    System.out.printf("[]");
                } else if (colorSensorLocation != null && i == colorSensorLocation.row && j == colorSensorLocation.col) {
                    System.out.printf("()");
                } else {
                    Property prop = getProperty(new GridLocation(i, j));
                    String s = propToStr.get(prop);
                    if (s == null) throw new RuntimeException("String for proprerty " + prop.name() + " not set");
                    System.out.printf("%s", s);
                }
            }
            System.out.printf("|%n");
        }
        // Print end line.
        System.out.printf("+");
        for (int j = 0; j < numCols * 2; j += 1) {
            System.out.printf("-");
        }
        System.out.printf("+%n");
    }
    
    /**
     * Get the grid size of the map.
     */
    public float getGridSize() {
        return gridSizeMetres;
    }
    
    /**
     * Get the position in the grid corresponding to a point on the map.
     * @param   p   the point to get the grid location of.
     * @return      the grid location that is assigned to point p.
     */
    public GridLocation getGridLocation(Point p) {
        int row = (int)((p.yMetres - topLeftY) / gridSizeMetres);
        int col = (int)((p.xMetres - topLeftX) / gridSizeMetres);
        if (row < 0 || col < 0 || row >= numRows || col >= numCols) {
            return GridLocation.makeOutOfBoundsLocation();
        }
        return new GridLocation(row, col);
    }
    
    /**
     * Get the centre point of a location in the grid in metre coordinates.
     * @param   loc a location in the grid.
     * @return
     */
    public Point getCentrePoint(GridLocation loc) throws OutOfMapBoundsException {
        if (isOutOfBounds(loc)) throw new OutOfMapBoundsException();
        float xMetres = ((float)loc.col + 0.5f) * gridSizeMetres + topLeftX;
        float yMetres = ((float)loc.row + 0.5f) * gridSizeMetres + topLeftY;
        return new Point(xMetres, yMetres);
    }
    
    /**
     * Get the distance to the nearest obstacle to point p (metres).
     * Returns Float.MAX_VALUE if it can't find an obstacle.
     */
    public float distanceToNearestObstacle(Point p, float maxAllowable) {
        float currentMinSquareDist = Float.MAX_VALUE;
        maxAllowable = (float)Math.pow(maxAllowable, 2.0);
        // Iterate over every grid position.
        // this is pretty inefficient, we can improve it if necessary.
        for (int row = 0; row < numRows; row += 1) {
            for (int col = 0; col < numCols; col += 1) {
                try {
                    GridLocation loc = new GridLocation(row, col);
                    // Skip if we don't see an obstacle.
                    if (get(Property.OBSTACLE, loc) < 0.5f) continue;
                    Point obstacle = getCentrePoint(loc);
                    float dsq = (float)Math.pow(obstacle.xMetres - p.xMetres, 2.0)
                              + (float)Math.pow(obstacle.yMetres - p.yMetres, 2.0);
                    if (dsq < currentMinSquareDist) {
                        currentMinSquareDist = dsq;
                    }
                    if (currentMinSquareDist < maxAllowable) return currentMinSquareDist;
                } catch (Map.OutOfMapBoundsException e) {
                    throw new RuntimeException("distanceToNearestObstacle() is bugged, hits out of bound region");
                }
            }
        }
        return (float)Math.sqrt(currentMinSquareDist);
    }
    
    public float distanceToNearestObstacle(Point p) {
        return distanceToNearestObstacle(p, 0.0f);
    }
    
    /**
     * Get the landing site position of the rover.
     */
    public Point getRoverLandingSite() {
        return roverLandingSite;
    }
    
    /**
     * Set the landing site position of the rover.
     */
    public void setRoverLandingSite(Point roverLandingSite) {
        this.roverLandingSite = roverLandingSite;
    }
    
    // The colors for different parts of the map.
    private RGBColor outOfBoundsColor;
    private HashMap<Property, RGBColor> propColors;
    
    /**
     * Initialize the colors for the map.
     */
    private void initColors() {
        // TODO: Make these adhere to expected map colors.
        outOfBoundsColor = new RGBColor(0.3f, 0.3f, 0.3f);                       // Dark grey
        propColors = new HashMap<Property, RGBColor>();
        propColors.put(Property.NONE,               new RGBColor(1.0f, 1.0f, 1.0f)); // White
        propColors.put(Property.OBSTACLE,           new RGBColor(0.0f, 0.0f, 0.0f)); // Black
        propColors.put(Property.CRATER,             new RGBColor(0.0f, 0.1f, 0.8f)); // Blue
        propColors.put(Property.RADIATION,          new RGBColor(0.0f, 0.8f, 0.1f)); // Green
        propColors.put(Property.TRACKS,             new RGBColor(1.0f, 0.0f, 0.0f)); // Red
        propColors.put(Property.TRACKS_FOOTSTEPS,   new RGBColor(1.0f, 0.0f, 0.0f)); // Red
        propColors.put(Property.TRACKS_VEHICLE,     new RGBColor(1.0f, 0.0f, 0.0f)); // Red
        propColors.put(Property.TRACKS_LANDING,     new RGBColor(1.0f, 0.0f, 0.0f)); // Red
        propColors.put(Property.BORDER,             new RGBColor(0.6f, 0.6f, 0.8f)); // Grey-blue
        propColors.put(Property.NO_GO_ZONE,         new RGBColor(0.6f, 0.6f, 0.8f)); // Grey-blue
    }
    
    /**
     * Set the color of a property.
     * @param prop  The property to assign a color to.
     * @param color The color to assign to the property. Must be non-null.
     * @throws IllegalArgumentException if given a null color.
     */
    public void setPropertyColor(Property prop, RGBColor color) {
        if (prop == Property.NONE) {
            throw new RuntimeException("setPropertyColor() does not allow use of Map.Property.NONE");
        }
        if (prop == null) throw new IllegalArgumentException();
        if (color == null) throw new IllegalArgumentException();
        propColors.put(prop, color);
    }
    
    /**
     * Get the expected map color for an arbitrary position on the map.
     * @param  pos the position to get the color of.
     * @return     the color at the position.
     */
    public RGBColor getColorAtPosition(Point pos) {
        return getColorAtPosition(getGridLocation(pos));
    }
    
    /**
     * Get the expected map color for an grid location on the map.
     * @param  loc the grid location to get the color of.
     * @return     the color at the position.
     */
    public RGBColor getColorAtPosition(GridLocation loc) {
        if (isOutOfBounds(loc)) {
            return outOfBoundsColor;
        }
        Property prop = getProperty(loc);
        if (prop == null) {
            throw new RuntimeException("getProperty() returned null.");
        }
        RGBColor color = propColors.get(prop);
        if (color == null) {
            throw new RuntimeException("Map has not been prepared for all possible map feature colors.");
        }
        return color;
    }
    
    /**
     * A simple exception that is thrown when the map goes beyond it's bounds.
     */
    public static class OutOfMapBoundsException extends Exception {
        // This seems to be expected by Eclipse.
        private static final long serialVersionUID = 1766051511371198505L;
        public OutOfMapBoundsException() {
            super("Map bounds exceeded");
        }
        public OutOfMapBoundsException(String message) {
            super(message);
        }
    }
    
    /**
     * Internal class representing a location in the grid.
     */
    static public class GridLocation {
        /**
         * Default constructor is disallowed externally, creates an out of bounds location.
         */
        private GridLocation() {
            this.row = -1;
            this.col = -1;
        }
        
        /**
         * Create a GridLocation.
         * @param row the vertical position
         * @param col the horizontal position
         */
        public GridLocation(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        /**
         * Manually create an out-of-bounds position.
         * @return a GridLocation that is guaranteed to be out-of-bounds.
         */
        static public GridLocation makeOutOfBoundsLocation() {
            return new GridLocation();
        }
        
        // The grid position.
        public int row;
        public int col;
    }

    // List of possible track types
    public enum TrackType {
        VEHICLE, FOOTPRINT, LANDING
    }
    // List of possible map properties.
    public enum Property {
        NONE, OBSTACLE, CRATER, RADIATION, TRACKS, BORDER, NO_GO_ZONE,
        TRACKS_FOOTSTEPS, TRACKS_VEHICLE, TRACKS_LANDING
    }
    // The accessible map properties, sorted by priority.
    public static final Property[] accessibleProperties = {
        Property.TRACKS_FOOTSTEPS, Property.TRACKS_VEHICLE,
        Property.TRACKS_LANDING, Property.TRACKS, 
        Property.OBSTACLE, Property.NO_GO_ZONE,
        Property.CRATER,
        Property.RADIATION,
        Property.BORDER,
        
    };
    
    // Storage specifications.
    // The size of each grid element (in metres).
    private float gridSizeMetres;
    // Row and column of the origin cell.
    // (The origin cell is (0.0, 0.0)).
    private GridLocation originLocation;
    // The position of the top-left cell.
    private float topLeftX;
    private float topLeftY;
    // Number of rows and columns.
    private int numRows;
    private int numCols;
    // The landing site of the robot.
    private Point roverLandingSite;
    
    // Internal storage.
    private Matrix<Boolean> explored;
    private HashMap<Property, Matrix<Float>> propertyMaps;
}