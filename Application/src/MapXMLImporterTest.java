import static org.junit.Assert.*;

import org.junit.Test;

public class MapXMLImporterTest {

    @Test
    public void testRasterize() {
        Map map = new Map(1.0f, 20, 20);
        // Octant 0. *
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (6.0f, 2.0f),   Map.Property.OBSTACLE, map);
        // Octant 1. *
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (1.0f, 8.0f),   Map.Property.CRATER, map);
        // Octant 2.
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (-3.0f, 7.0f),  Map.Property.RADIATION, map);
        // Octant 3.
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (-6.0f, 1.5f),  Map.Property.TRACKS, map);
        // Octant 4.
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (-6.0f, -5.0f), Map.Property.RADIATION, map);
        // Octant 5.
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (-3.0f, -8.0f), Map.Property.OBSTACLE, map);
        // Octant 6.
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (3.0f, -9.0f),  Map.Property.CRATER, map);
        // Octant 7.
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (9.0f, -3.0f),  Map.Property.TRACKS, map);
        
        map.print(null, null);
    }
    
    @Test
    public void testRasterizeVertHorizontal() {
        Map map = new Map(1.0f, 20, 20);
        // Vertical
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point ( 9.0f,  0.0f),   Map.Property.OBSTACLE,  map);
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point (-9.0f,  0.0f),   Map.Property.CRATER,    map);
        // Horizontal
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point ( 0.0f,  9.0f),   Map.Property.RADIATION, map);
        MapXMLImporter.rasterizeLine(new Point(0.0f, 0.0f), new Point ( 0.0f, -9.0f),   Map.Property.TRACKS,    map);
        
        map.print(null, null);
    }

    //@Test
    public void testParse() {
        Map map = new Map(0.05f, 35, 35);
        try {
            MapXMLImporter importer = new MapXMLImporter("example.xml", map, null);
        } catch (MapXMLImporter.Error e) {
            System.err.printf("Error raised: %s%n", e.getMessage());
            e.printStackTrace();
            fail("Error raised");
        }
        map.print(null, null);
    }
}
