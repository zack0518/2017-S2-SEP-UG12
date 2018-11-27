import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 * Utility class for loading an XML file into a Map class.
 * @author jkortman
 */
public class MapXMLImporter {
    public static void importMap(String fname, Map map, Handler handler) throws Error {
        MapXMLImporter importer = new MapXMLImporter(fname, map, handler);
    }
    
    public MapXMLImporter(String fname, Map map, Handler handler) throws Error {
        init(fname, map, handler);
    }
    
    public void init(String fname, Map map, Handler handler) throws Error {
        loadDoc(fname);
        this.map = map;
        this.units = "metres";
        parse(fname, map, handler);
    }
    
    public void parse(String fname, Map map, Handler handler) throws Error {
        expectTag(doc.getDocumentElement().getNodeName(), "lunarrovermap");
        
        // Get the units used for the map.
        this.units = doc.getDocumentElement().getAttribute("units");
        if (!unitConversions.containsKey(units)) {
            throw new Error("Expected valid unit attribute, got '" + units + "' instead");
        }
        
        Element root = doc.getDocumentElement();
        parseBoundary(getUniqueElement(root, "boundary"));
        parseObstacles(root);
        parseZones(root);
        this.topLevelAttributes = parseAttributes(root);
        parseVehicleStatus(getUniqueElement(root, "vehicle-status"), handler);
        if (getSingleOrNone(root, "apollo-landing-site")) {
            parseApolloLandingSite(getUniqueElement(root, "apollo-landing-site"));
        }
        parseRoverLandingSite(getUniqueElement(root, "rover-landing-site"));
        parseTrackToColor(getUniqueElement(root, "track-to-color"));
        parseTracks(root, map);
    }
    
    // Handle parts of the map.
    private void parseBoundary(Element boundaryElement) throws Error {
        List<Point> boundary = parsePointList(getUniqueElement(boundaryElement, "area"));
        // Currently does nothing, as we have no need for the boundary data.
    }

    private void parseVehicleStatus(Element e, Handler handler) throws Error {
        this.vehicleLocation = parsePoint(getUniqueElement(e, "point"));
        String headingAngle = getUniqueElement(e, "heading").getAttribute("angle");
        if (headingAngle.length() == 0) throw new Error("No heading in vehicle-status");
        this.vehicleHeading = Float.parseFloat(headingAngle);
        if (handler != null) {
            handler.updateRobotPosition(this.vehicleLocation);
            handler.updateAngleDegrees(this.vehicleHeading);
        }
    }
    
    private void parseApolloLandingSite(Element e) throws Error {
        parseObstacles(e);
    }
    
    private void parseRoverLandingSite(Element e) throws Error {
        this.roverLandingSite = parsePoint(getUniqueElement(e, "point"));
        map.setRoverLandingSite(roverLandingSite);
    }
    
    private void parseTrackToColor(Element e) throws Error {
        // TODO
        HashMap<String, String> attributes = parseAttributes(e);
        this.trackToColor = attributes;
    }
    
    private void parseObstacles(Element e) throws Error {
        NodeList obstacleNodes = e.getElementsByTagName("obstacle");
        for (int i = 0; i < obstacleNodes.getLength(); i += 1) {
            // Obstacles can contain either areas or lists of points.
            Element obstacleElement = (Element)obstacleNodes.item(i);
            List<Point> obstaclePoints = parsePointList(obstacleElement);
            if (obstaclePoints.size() > 0) {
                addMapPoints(obstaclePoints, Map.Property.OBSTACLE, map);
            } else {
                // If the obstacle contains an area, parse it and rasterize the list.
                List<Point> obstacleAreaPoints = parsePointList(getUniqueElement(obstacleElement, "area"));
                rasterizePointList(obstacleAreaPoints, Map.Property.OBSTACLE, map, true);
            }
        }
    }

    private void parseZones(Element e) throws Error {
        NodeList nodes = e.getElementsByTagName("zone");
        for (int i = 0; i < nodes.getLength(); i += 1) {
            Element elem = (Element)nodes.item(i);
            // Retrieve the property from the map data.
            Map.Property prop = null;
            String state = elem.getAttribute("state");
            if (state.equals("explored")) {
                // TODO
                continue;
            } if (state.equals("unexplored")) {
                // TODO
                continue;
            } else if (state.equals("nogo")) {
                prop = Map.Property.NO_GO_ZONE;
            } else if (state.equals("crater"))  {
                prop = Map.Property.CRATER;
            } else if (state.equals("radiation")) {
                prop = Map.Property.RADIATION;
            } else {
                throw new Error("Unknown or non-existent state attribute for zone tag: '" + state + "'");
            }
            assert prop != null;
            
            // Parse and rasterize the points.
            if (getSingleOrNone(elem, "area")) {
                List<Point> points = parsePointList(getUniqueElement(elem, "area"));
                rasterizePointList(points, prop, map, true);
            } else if (getSingleOrNone(e, "circle")) {
                // TODO
                rasterizeCircle(getUniqueElement(elem, "circle"), prop, map);
            } else {
                throw new Error("Expected a 'zone' or 'circle' tag in 'zone' tag");
            }
        }
    }

    /**
     * Parse the tracks in the XML file.
     */
    private void parseTracks(Element e, Map map) throws Error {
        NodeList nodes = e.getElementsByTagName("track");
        for (int i = 0; i < nodes.getLength(); i += 1) {
            Element elem = (Element)nodes.item(i);
            // Retrieve the property from the map data.
            Map.TrackType trackType = null;
            Map.Property prop = null;
            String type = elem.getAttribute("type");
            if (type.equals("vehicle")) {
                trackType = Map.TrackType.VEHICLE;
                prop = Map.Property.TRACKS_VEHICLE;
            } else if (type.equals("footprint")) {
                trackType = Map.TrackType.FOOTPRINT;
                prop = Map.Property.TRACKS_FOOTSTEPS;
            } else if (type.equals("landing")) {
                trackType = Map.TrackType.LANDING;
                prop = Map.Property.TRACKS_LANDING;
            } else {
                //throw new Error("Unknown or non-existent state attribute for zone tag: '" + state + "'");
                return;
            }
            assert trackType != null;

            // get the track points.
            List<Point> points = parsePointList(elem);
            if (trackType == Map.TrackType.FOOTPRINT) {
                // Fill in dots.
                addMapPoints(points, prop, map);
            } else {
                // Rasterize the lines into the map.
                rasterizePointList(points, prop, map, false);
            }
        }
    }
    
    /**
     * Add a set of points to a map property by setting each grid location a point is in.
     */
    private void addMapPoints(List<Point> points, Map.Property prop, Map map) {
        boolean warned = false;
        for (Point p : points) {
            try {
                map.set(prop, p, 1.0f);
            } catch (Map.OutOfMapBoundsException e) {
                if (!warned) {
                    System.err.printf("Warning: Out of bounds point when loading xml: %s at (%f, %f)%n", prop.name(), p.xMetres, p.yMetres);
                    warned = true;
                }
            }
        }
    }
    
    /**
     * Add a set of points to a map property by setting each grid location a point is in.
     */
    private void addMapLocations(List<Map.GridLocation> locations, Map.Property prop, Map map) {
        boolean warned = false;
        for (Map.GridLocation loc : locations) {
            try {
                map.set(prop, loc, 1.0f);
            } catch (Map.OutOfMapBoundsException e) {
                if (!warned) {
                    System.err.printf("Warning: Out of bounds location when loading xml: %s at (%d, %d)%n", prop.name(), loc.row, loc.col);
                    warned = true;
                }
            }
        }
    }

    /**
     * Parse attributes that are immediate children of element e.
     */
    private HashMap<String, String> parseAttributes(Element e) throws Error {
        HashMap<String, String> attributes = new HashMap<>();
        NodeList nodes = e.getElementsByTagName("attribute");
        for (int i = 0; i < nodes.getLength(); i += 1) {
            Element elem = (Element)nodes.item(i);
            if (!elem.getParentNode().isSameNode(e)) continue;
            String key = getUniqueElement(elem, "key").getTextContent();
            String value = getUniqueElement(elem, "value").getTextContent();
            attributes.put(key, value);
        }
        return attributes;
    }
    
    /**
     * Parse an individual <point> tag.
     * @param   e   the <point> element.
     * @return      the Point object equivalent of the element.
     * @throws      Error
     */
    private Point parsePoint(Element e) throws Error {
        float x = unitConversions.get(units) * Float.parseFloat(e.getAttribute("x"));
        float y = unitConversions.get(units) * Float.parseFloat(e.getAttribute("y"));
        return new Point(x, y);
    }
    
    /**
     * Parse an XML element containing an list of points.
     * @param   e   the tag that contains a list of <point> tags, e.g. <area> or <obstacle>
     * @return      a list of Points that make up the list.
     * @throws Error
     */
    private List<Point> parsePointList(Element element) throws Error {
        NodeList pointNodes = element.getElementsByTagName("point");
        ArrayList<Point> points = new ArrayList<>(pointNodes.getLength());
        for (int i = 0; i < pointNodes.getLength(); i += 1) {
            Element pointElement = (Element)pointNodes.item(i);
            if (!pointElement.getParentNode().isSameNode(element)) continue;
            points.add(parsePoint(pointElement));
        }
        return points;
    }
    
    /**
     * Rasterize a loop of points into the map using the provided property.
     */
    public static void rasterizePointList(List<Point> points, Map.Property prop, Map map, boolean loop) {
        for (int i = 0; i < points.size() - 1; i += 1) {
            // rasterize point i to point (i + 1) % list size.
            rasterizeLine(points.get(i), points.get(i + 1), prop, map);
        }
        if (loop) {
            rasterizeLine(points.get(points.size() - 1), points.get(0), prop, map);
        }
    }
    
    /**
     * Rasterize a line into the map using some property.
     * @param start the start of the line.
     * @param end   the end of the line.
     * @param prop  the property to draw the line into.
     */
    public static void rasterizeLine(Point start, Point end, Map.Property prop, Map map) {
        List<Map.GridLocation> line = Rasterizer.rasterize(start, end, map);
        boolean warned = false;
        
        for (Map.GridLocation loc : line) { 
            try {
                map.set(prop, loc, 1.0f);
            } catch (Map.OutOfMapBoundsException e) {
                if (!warned) {
                    System.err.printf("Warning: Map loading triggered out-of-bounds error for property %s.%n", prop.name());
                    warned = true;
                }
            }
        }
    }

    /**
     * Rasterize a circle into the map.
     */
    private void rasterizeCircle(Element e, Map.Property prop, Map map) throws Error {
        // TODO Auto-generated method stub
        Point midpoint = parsePoint(getUniqueElement(e, "point"));
        float radius = unitConversions.get(units) * Float.parseFloat(e.getAttribute("radius"));
        List<Map.GridLocation> locations = Rasterizer.rasterizeCircle(midpoint, radius, map);
        addMapLocations(locations, prop, map);
    }
    
    /**
     * Load the document.
     * @param fname the file name of the document to load.
     */
    private void loadDoc(String fname) throws Error {
        try {
            File f = new File(fname);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            this.doc = doc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.toString() + ": '" + e.getMessage() + "'");
        }
    }
    
    // Error handler functions.
    private void expectTag(String actual, String expected) throws Error {
        if (!actual.equals(expected)) {
            throw new Error("Expected tag '" + expected + "' but got '" + actual + "' instead");
        }
    }
    
    private Element getUniqueElement(Element e, String tag) throws Error {
        NodeList nodes = e.getElementsByTagName(tag);
        if (nodes.getLength() == 0) {
            throw new Error("Expected a tag with name '" + tag + "'");
        }
        if (nodes.getLength() > 1) {
            throw new Error("Expected exactly 1 tag with name '" + tag + "', but got " + Integer.toString(nodes.getLength()));
        }
        Node node = nodes.item(0);
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            throw new Error("Expected element but node type is not element for '" + tag + "'");
        }
        return (Element)node;
    }
    
    private boolean getSingleOrNone(Element e, String tag) throws Error {
        NodeList nodes = e.getElementsByTagName(tag);
        if (nodes.getLength() > 1) {
            throw new Error("Expected exactly 1 tag with name '" + tag + "', but got " + Integer.toString(nodes.getLength()));
        }
        return nodes.getLength() == 1;
    }
    
    /**
     * A helper class to throw when an XML parse attempt fails.
     */
    static class Error extends Exception {
        public Error(String s) {
            super(s);
        }
    }
    
    // The multipliers for various SI units to metres.
    private static final HashMap<String, Float> unitConversions = new HashMap<>();
    static {
        unitConversions.put("km",           1000.0f);
        unitConversions.put("metres",          1.0f);
        unitConversions.put("cm",              0.01f);
        unitConversions.put("mm",              0.001f);
    }
    
    private Document doc;
    private Map map;
    private String units;
    private HashMap<String, String> topLevelAttributes;
    private HashMap<String, String> trackToColor;
    private Point vehicleLocation;
    private float vehicleHeading;
    private Point roverLandingSite;
}
