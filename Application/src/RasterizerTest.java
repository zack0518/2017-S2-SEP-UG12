import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class RasterizerTest {
    public void printLine(List<Map.GridLocation> line) {
        System.err.printf("[%n");
        for (Map.GridLocation loc : line) {
            System.err.printf("    (%d, %d)%n", loc.row, loc.col);
        }
        System.err.printf("]%n");
    }
    
    @Test
    public void test1() {
        Map map = new Map(1.0f, 10, 10);
        Map.GridLocation start = new Map.GridLocation(0, 0);
        Map.GridLocation end = new Map.GridLocation(9, 9);
        try {
            List<Map.GridLocation> line = Rasterizer.rasterize(
                    map.getCentrePoint(start), map.getCentrePoint(end), map);
            for (int i = 0; i < 10; i += 1) {
                assertEquals(line.get(i).row, i);
                assertEquals(line.get(i).col, i);
            }
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
    }
    
    @Test
    public void test2() {
        Map map = new Map(10.0f, 5, 5);
        Map.GridLocation start = new Map.GridLocation(0, 4);
        Map.GridLocation end = new Map.GridLocation(4, 0);
        try {
            List<Map.GridLocation> line = Rasterizer.rasterize(
                    map.getCentrePoint(start), map.getCentrePoint(end), map);
            for (int i = 0; i < 4; i += 1) {
                assertEquals(line.get(i).row, i);
                assertEquals(line.get(i).col, 4 - i);
            }
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
    }
    
    @Test
    public void test3() {
        Map map = new Map(0.02f, 7, 7);
        Map.GridLocation start = new Map.GridLocation(0, 1);
        Map.GridLocation end = new Map.GridLocation(0, 4);
        try {
            List<Map.GridLocation> line = Rasterizer.rasterize(
                    map.getCentrePoint(start), map.getCentrePoint(end), map);
            for (int i = 0; i < 3; i += 1) {
                assertEquals(line.get(i).row, 0);
                assertEquals(line.get(i).col, i + 1);
            }
        } catch (Map.OutOfMapBoundsException e) {
            fail("out of bounds");
        }
    }
}
