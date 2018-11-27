/**
 * Class representing an RGB color.
 * @author jkortman
 */
public class RGBColor {
    /**
     * Default constructor is disallowed
     */
    @SuppressWarnings("unused")
    private RGBColor() {}
    
    /**
     * Create a color with R, G, B components.
     * @param r the red color component
     * @param g the green color component
     * @param b the blue color component
     */
    public RGBColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public float r, g, b;
    
    /**
     * Calculate the mean-squared error of two colors.
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
}
