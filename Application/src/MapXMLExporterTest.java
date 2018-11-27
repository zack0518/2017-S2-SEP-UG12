import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class MapXMLExporterTest {

    @Test
    public void test() {
        final float gridSizeMetres = 0.05f;
        final int rows = 35;
        final int cols = 35;
        Map map = new Map(gridSizeMetres, rows, cols);
        // Import
        try {
            MapXMLImporter importer = new MapXMLImporter("example.xml", map, null);
        } catch (MapXMLImporter.Error e) {
            System.err.printf("Importer error raised: %s%n", e.getMessage());
            e.printStackTrace();
            fail("Error raised");
        }
        System.out.println("==============");
        System.out.println("=== Before ===");
        System.out.println("==============");
        map.print(null, null);
        
        // Export
        try {
            MapXMLExporter.export("output-test.xml",
                                  map,
                                  new Point(0.3f, -0.2f),
                                  270.0f,
                                  Arrays.asList(new Point(0.5f, 0.5f), new Point(0.0f, 0.5f)),
                                  Arrays.asList(new Point(-0.5f, 0.2f), new Point(-0.5f, 0.5f)),
                                  Arrays.asList(new Point(0.3f, -0.8f), new Point(-0.1f, 0.1f)));
        } catch (MapXMLExporter.Error e) {
            System.err.printf("Exporter error raised: %s%n", e.getMessage());
            e.printStackTrace();
            fail("Error raised");
        }
        
        // Re-import and print.
        Map map2 = new Map(gridSizeMetres, rows, cols);
        try {
            MapXMLImporter importer = new MapXMLImporter("output-test.xml", map2, null);
        } catch (MapXMLImporter.Error e) {
            System.err.printf("Importer error raised: %s%n", e.getMessage());
            e.printStackTrace();
            fail("Error raised");
        }
        System.out.println("=================================");
        System.out.println("=== After (with added tracks) ===");
        System.out.println("=================================");
        map2.print(null, null);
    }

}
