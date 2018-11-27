import java.util.List;
import java.util.ArrayList;

/**
 * Utility class to rasterize a line between two points.
 * @author jkortman
 *
 */
public class Rasterizer {
    /**
     * Rasterize a line between two points into a sequence grid locations in a given map.
     * @param start the start of the line.
     * @param end   the end of the line.
     * @param map   the map to rasterize.
     */
    public static List<Map.GridLocation> rasterize(Point start, Point end, Map map) {
        // The line is rasterized using Bresenham's algorithm,
        // with x = columns and y = rows.
        // see: https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
        
        // Determine the octant the line resides in.
        int octant = getOctant(start, end);
        
        // Modify the starting points based on the octant.
        Point a = switchFromOctant(octant, start);
        Point b = switchFromOctant(octant, end);
        
        final float dx = b.xMetres - a.xMetres;
        final float dy = b.yMetres - a.yMetres;
        final float derr = Math.abs(dy / dx);
        float err = 0.0f;
        float y = a.yMetres;
        
        List<Map.GridLocation> locations = new ArrayList<>();
        for (float x = a.xMetres; x <= b.xMetres; x += map.getGridSize()) { 
            // restore the point from the octant shift.
            Point target = switchToOctant(octant, new Point(x, y));
            locations.add(map.getGridLocation(target));
            err += derr;
            if (err >= 0.5f) {
                y += map.getGridSize();
                err -= 1.0f;
            }
        }
        return locations;
    }
    
    /**
     * Rasterize a circle given by a midpoint and radius.
     */
    public static List<Map.GridLocation> rasterizeCircle(Point midpoint, Float radius, Map map) {
        // Uses the algroithm described at https://en.wikipedia.org/wiki/Midpoint_circle_algorithm.
        int r = Math.round(radius / map.getGridSize());
        Map.GridLocation loc = map.getGridLocation(midpoint);
        //System.err.printf("Adding circle at point (%f, %f) -> location (%d, %d), radius %f/%d%n", midpoint.xMetres, midpoint.yMetres, loc.row, loc.col, radius, r);
        int x = r - 1;
        int y = 0;
        int dx = 1;
        int dy = 1;
        int err = dx - (r << 1);
        
        List<Map.GridLocation> locations = new ArrayList<>();
        while (x >= y) {
            // Add points to each octant.
            locations.add(new Map.GridLocation(loc.row + x, loc.col + y));
            locations.add(new Map.GridLocation(loc.row + y, loc.col + x));
            locations.add(new Map.GridLocation(loc.row - y, loc.col + x));
            locations.add(new Map.GridLocation(loc.row - x, loc.col + y));
            locations.add(new Map.GridLocation(loc.row - x, loc.col - y));
            locations.add(new Map.GridLocation(loc.row - y, loc.col - x));
            locations.add(new Map.GridLocation(loc.row + y, loc.col - x));
            locations.add(new Map.GridLocation(loc.row + x, loc.col - y));
            
            // rotate the current point.
            if (err <= 0) {
                y += 1;
                err += dy;
                dy += 2;
            }
            if (err > 0) {
                x -= 1;
                dx += 2;
                err += (-r << 1) + dx;
            }
            
        }
        return locations;
    }
    
    /**
     * Helper function to determine the numbered octant the line from a to be resides in.
     * See: (taken from https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm)
     *  \2|1/
     *  3\|/0
     * ---+---
     *  4/|\7
     *  /5|6\
     */
    private static int getOctant(Point a, Point b) {
        final Point p = new Point(b.xMetres - a.xMetres, b.yMetres - a.yMetres);
        final float slope = (float)p.yMetres / p.xMetres;
        if (slope == Float.POSITIVE_INFINITY) return 2;
        if (slope == Float.NEGATIVE_INFINITY) return 6;
        // Top-Right
        if (p.xMetres >= 0.0f && p.yMetres >= 0.0f) {
            if (slope <= 0.5f)  return 0;
            else                return 1;
        }
        // Top-Left
        if (p.xMetres <= 0.0f && p.yMetres >= 0.0f) {
            if (slope <= -0.5f) return 2;
            else                return 3;
        }
        // Bottom-Left
        if (p.xMetres <= 0.0f && p.yMetres <= 0.0f) {
            if (slope <= 0.5f)  return 4;
            else                return 5;
        }
        // Bottom-Right
        if (p.xMetres >= 0.0f && p.yMetres <= 0.0f) {
            if (slope <= -0.5f) return 6;
            else                return 7;
        }
        throw new RuntimeException("Couldn't match octant for two points, getOctant() is bugged!");
    }
    
    /**
     * Helper function to switch from a numbered octant to octant 0.
     * Developed using https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm.
     */
    private static Point switchFromOctant(int octant, Point p) {
        switch (octant) {
            case 0: return new Point( p.xMetres,  p.yMetres); // (x, y)
            case 1: return new Point( p.yMetres,  p.xMetres); // (y, x)
            case 2: return new Point( p.yMetres, -p.xMetres); // (y, -x)
            case 3: return new Point(-p.xMetres,  p.yMetres); // (-x, y)
            case 4: return new Point(-p.xMetres, -p.yMetres); // (-x, -y)
            case 5: return new Point(-p.yMetres, -p.xMetres); // (-y, -x)
            case 6: return new Point(-p.yMetres,  p.xMetres); // (-y, x)
            case 7: return new Point( p.xMetres, -p.yMetres); // (x, -y)
        }
        throw new RuntimeException("Received invalid octant: " + Integer.toString(octant));
    }
    
    /**
     * Helper function to switch to a numbered octant from octant 0.
     * Developed using https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm.
     */
    private static Point switchToOctant(int octant, Point p) {
        switch (octant) {
            case 0: return new Point( p.xMetres,  p.yMetres); // (x, y)
            case 1: return new Point( p.yMetres,  p.xMetres); // (y, x)
            case 2: return new Point(-p.yMetres,  p.xMetres); // (-y, x)
            case 3: return new Point(-p.xMetres,  p.yMetres); // (-x, y)
            case 4: return new Point(-p.xMetres, -p.yMetres); // (-x, -y)
            case 5: return new Point(-p.yMetres, -p.xMetres); // (-y, -x)
            case 6: return new Point( p.yMetres, -p.xMetres); // (y, -x)
            case 7: return new Point( p.xMetres, -p.yMetres); // (x, -y)
        }
        throw new RuntimeException("Received invalid octant: " + Integer.toString(octant));
    }
    
}
