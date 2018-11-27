import java.util.Objects;

/**
 * A point in 2D space.
 * @author jkortman
 */
public class Point {
    /**
     * Create a point at the origin.
     */
    public Point() {
        xMetres = 0;
        yMetres = 0;
    }
    
    /**
     * Create a point at a position.
     * @param xMetres the x-position of the point in metres
     * @param yMetres the y-position of the point in metres
     */
    public Point(float xMetres, float yMetres) {
        this.xMetres = xMetres;
        this.yMetres = yMetres;
    }
    
    /**
     * Add two Points together.
     * @param  a a Point to add
     * @param  b a Point to add
     * @return   the sum of the two points
     */
    public static Point add(Point a, Point b) {
        return new Point(a.xMetres + b.xMetres, a.yMetres + b.yMetres);
    }
    
    @Override
    public String toString() {
    		return "(x,y):" + " (" + this.xMetres + "," + this.yMetres + ")"; 
    }
    
    @Override
    public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof Point)) {
			return false;
		}
		
		Point n = (Point)o;
		
		return (Float.compare(this.xMetres, n.xMetres) == 0 && 
				Float.compare(this.yMetres, n.yMetres) == 0);
	}
	
    @Override
	public int hashCode() {
		return Objects.hash(this.xMetres, this.yMetres);
	}
    
    
    // X and Y position.
    public float xMetres;
    public float yMetres;
}
