import java.io.File;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.*;

/**
 * Provides utilities to export a Map to XML.
 * @author jkortman
 *
 */
public class MapXMLExporter {
    // the drawing method for regions.
    private enum DrawMethod { OUTLINES, SQUARES };
    private static final DrawMethod regionDrawMethod = DrawMethod.SQUARES;
    
    public static void export(
            String fname,
            Map map,
            Point robotPosition,
            float robotHeadingDegrees,
            List<Point> footsteps,
            List<Point> vehicleTracks,
            List<Point> landingTracks
            ) throws Error {
        Document doc = makeDoc();
        
        // Root of XML.
        Element root = doc.createElement("lunarrovermap");
        root.setAttribute("units", "metres");
        doc.appendChild(root);
        
        // Add attributes.
        addAttributeTag(root, "Survey Date", getSurveyDate());
        addAttributeTag(root, "Robot Model", "Lego Mindstorms EV3");
        
        // Add boundary.
        // TODO: Should it be this, or sensed boundary??
        Element boundary = doc.createElement("boundary");
        addPointList(boundary, "area", getBoundaryPoints(map));
        root.appendChild(boundary);
        
        // Add vehicle status.
        addVehicleStatus(root, robotPosition, robotHeadingDegrees); // TODO
        
        // Add apollo landing site.
        addApolloLandingSite(root);
        
        // Add rover landing site.
        addRoverLandingSite(root, map.getRoverLandingSite());
        
        // Add track-to-color rules.
        addTrackToColor(root);

        // Add obstacles.
        // TODO
        addObstacles(root, map);
        
        // Add zones.
        // TODO
        addZone(root, map, "nogo",      Map.Property.NO_GO_ZONE);
        addZone(root, map, "crater",    Map.Property.CRATER);
        addZone(root, map, "radiation", Map.Property.RADIATION);
        
        // Add tracks.
        // TODO
        if (footsteps != null)      addTrack(root, Map.TrackType.FOOTPRINT,  footsteps);
        if (vehicleTracks != null)  addTrack(root, Map.TrackType.VEHICLE,    vehicleTracks);
        if (landingTracks != null)  addTrack(root, Map.TrackType.LANDING,    landingTracks);
        
        saveDoc(fname, doc);
    }
    
    /**
     * Add track-to-color data to the doc.
     */
    private static void addTrackToColor(Element e) {
        Element ttc = e.getOwnerDocument().createElement("track-to-color");
        addAttributeTag(ttc, "vehicle",     "0000FF");
        addAttributeTag(ttc, "footprint",   "00FF00");
        addAttributeTag(ttc, "landing",     "FF0000");
        e.appendChild(ttc);
    }

    private static void addRoverLandingSite(Element e, Point point) {
        Element landingSite = e.getOwnerDocument().createElement("rover-landing-site");
        Element pElement = e.getOwnerDocument().createElement("point");
        pElement.setAttribute("x", Float.toString(point.xMetres));
        pElement.setAttribute("y", Float.toString(point.yMetres));
        landingSite.appendChild(pElement);
        e.appendChild(landingSite);
    }

    /**
     * Add track data to the doc.
     */
    private static void addTrack(Element e, Map.TrackType trackType, List<Point> points) {
        HashMap<String, String> attrs = new HashMap<>();
        String name = null;
        switch (trackType) {
        case FOOTPRINT: name = "footprint"; break;
        case VEHICLE:   name = "vehicle";   break;
        case LANDING:   name = "landing";   break;
        }
        assert name != null;
        attrs.put("type", name);
        addPointList(e, "track", points, attrs);
    }

    /**
     * Add the apollo landing site to the doc.
     * @param root
     */
    private static void addApolloLandingSite(Element e) {
        Element landingSite = e.getOwnerDocument().createElement("apollo-landing-site");
        e.appendChild(landingSite);
    }

    /**
     * Get the current date as a string.
     */
    private static String getSurveyDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }

    /**
     * Add a <zone> tag.
     */
    private static void addZone(Element e, Map map, String name, Map.Property prop) throws Error {
        List<List<Point>> zoneRegions = getRegions(map, prop);
        for (List<Point> region : zoneRegions) {
            Element zone = e.getOwnerDocument().createElement("zone");
            zone.setAttribute("state", name);
            addPointList(zone, "area", region);
            e.appendChild(zone);
        }
    }
    
    /**
     * Get the regions using the current region draw method.
     */
    private static List<List<Point>> getRegions(Map map, Map.Property prop) throws Error {
        if (regionDrawMethod == DrawMethod.OUTLINES) {
            return getRegionOutlines(map, prop);
        } else if (regionDrawMethod == DrawMethod.SQUARES) {
            return getRegionSquares(map, prop);
        }
        throw new Error("Unknown draw method " + regionDrawMethod.name());
    }

    /**
     * Add <obstacle> tag to the map.
     */
    private static void addObstacles(Element e, Map map) throws Error {
        List<List<Point>> obstacles = getRegions(map, Map.Property.OBSTACLE);
        for (List<Point> obstacle : obstacles) {
            addPointList(e, "obstacle", obstacle);
        }
    }

    /**
     * Given a map property, transform the Map into a number of regions that are occupied by that property.
     * Uses a border-detection algorithm to 
     * @param map   The map to read.
     * @param prop  The property to get regions for.
     * @return      A list of regions. Each region is a list of points that forms an outline for that region.
     */
    private static List<List<Point>> getRegionOutlines(Map map, Map.Property prop) {
        // TODO
        throw new RuntimeException("getRegionOutlines() unimplemented.");
    }
    
    /**
     * Given a map property, transform the Map into a number of regions that are occupied by that property.
     * Each map grid is a square region.
     * @param map   The map to read.
     * @param prop  The property to get regions for.
     * @return      A list of regions. Each region is a list of points that forms an outline for that region.
     */
    private static List<List<Point>> getRegionSquares(Map map, Map.Property prop) {
        // Iterate over the map. For each grid square that is occupied by the given property,
        // generate a square wrapping that grid element.
        final float eps = 0.00001f;
        List<List<Point>> regions = new ArrayList<List<Point>>();
        for (int row = 0; row < map.rows(); row += 1) {
            for (int col = 0; col < map.columns(); col += 1) {
                Map.GridLocation loc = new Map.GridLocation(row, col);
                if (map.getProperty(loc) != prop) continue;
                List<Point> square = new ArrayList<>(4);
                try {
                    Point centre = map.getCentrePoint(loc);
                    // top-left
                    square.add(Point.add(centre, new Point(
                            -0.5f * map.getGridSize(),
                            -0.5f * map.getGridSize())));
                    // top-right
                    square.add(Point.add(centre, new Point(
                             0.5f * map.getGridSize() - eps,
                            -0.5f * map.getGridSize())));
                    // bottom-right
                    square.add(Point.add(centre, new Point(
                             0.5f * map.getGridSize() - eps,
                             0.5f * map.getGridSize() - eps)));
                    // bottom-left
                    square.add(Point.add(centre, new Point(
                            -0.5f * map.getGridSize(),
                             0.5f * map.getGridSize() - eps)));
                    regions.add(square);
                } catch (Map.OutOfMapBoundsException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return regions;
    }

    /**
     * Add the vehicle status to the map.
     */
    private static void addVehicleStatus(Element e, Point location, float headingDegrees) {
        Element vehicleStatus = e.getOwnerDocument().createElement("vehicle-status");
        // Add <point>.
        Element point = e.getOwnerDocument().createElement("point");
        point.setAttribute("x", Float.toString(location.xMetres));
        point.setAttribute("y", Float.toString(location.yMetres));
        vehicleStatus.appendChild(point);
        // Add <heading>.
        Element heading = e.getOwnerDocument().createElement("heading");
        heading.setAttribute("angle", Float.toString(headingDegrees));
        vehicleStatus.appendChild(heading);
        e.appendChild(vehicleStatus);
    }

    /**
     * Get the boundary corner points from the Map.
     */
    private static List<Point> getBoundaryPoints(Map map) {
        return Arrays.asList(
                map.topLeft(),
                new Point( map.topLeft().xMetres,  map.topLeft().yMetres),
                new Point(-map.topLeft().xMetres,  map.topLeft().yMetres),
                new Point(-map.topLeft().xMetres, -map.topLeft().yMetres),
                new Point( map.topLeft().xMetres, -map.topLeft().yMetres));
    }

    /**
     * Add a list of points to the document.
     */
    private static void addPointList(Element e, String name, List<Point> points) {
        addPointList(e, name, points, null);
    }
    
    /**
     * Add a list of points to the document.
     */
    private static void addPointList(Element e, String name, List<Point> points, HashMap<String, String> attrs) {
        Element list = e.getOwnerDocument().createElement(name);
        if (attrs != null) {
            for (String attr: attrs.keySet()) {
                list.setAttribute(attr, attrs.get(attr));
            }
        }
        // add points to the list.
        for (Point p : points) {
            Element pElement = e.getOwnerDocument().createElement("point");
            pElement.setAttribute("x", Float.toString(p.xMetres));
            pElement.setAttribute("y", Float.toString(p.yMetres));
            list.appendChild(pElement);
        }
        e.appendChild(list);
    }

    /**
     * Add an <attribute> tag as a child to the given element.
     */
    private static void addAttributeTag(Element e, String key, String value) {
        Element attr = e.getOwnerDocument().createElement("attribute");
        // Add <key>
        Element keyTag = e.getOwnerDocument().createElement("key");
        keyTag.setTextContent(key);
        attr.appendChild(keyTag);
        // Add <value>
        Element valueTag = e.getOwnerDocument().createElement("value");
        valueTag.setTextContent(value);
        attr.appendChild(valueTag);
        e.appendChild(attr);
    }
    
    /**
     * Create an empty XML document.
     * @return
     * @throws Error
     */
    private static Document makeDoc() throws Error {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return db.newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error("ParserConfigurationException: " + e.getMessage());
        }
    }
    
    /**
     * Save the doc to an XML file.
     * @param fname     the output filename
     * @param doc       the XML DOM document
     * @throws Error
     */
    private static void saveDoc(String fname, Document doc) throws Error {
        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.transform(new DOMSource(doc), new StreamResult(new File(fname)));
        } catch (TransformerFactoryConfigurationError e) {
            throw new Error("TransformerFactoryConfigurationError: " + e.getMessage());
        } catch (TransformerConfigurationException e) {
            throw new Error("TransformerConfigurationException: " + e.getMessage());
        }catch (TransformerException e) {
            throw new Error("TransformerException: " + e.getMessage());
        }
    }
    
    /**
     * A helper class to throw when an XML write attempt fails.
     */
    static class Error extends Exception {
        public Error(String s) {
            super(s);
        }
    }
}
