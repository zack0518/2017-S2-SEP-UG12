import java.util.HashMap;

/**
 * Takes an RGB color and assigns it to one of a number of discrete colors.
 * @author jkortman
 */
public class ColorDetector {
    public enum Color {
        BLACK,
        WHITE,
        BLUE,
        GREEN,
        PURPLE
    }
    
    /**
     * Create a color detector with some reference colors.
     * @param reference the reference colors used to identify colors.
     */
    public ColorDetector(HashMap<Color, RGBColor> reference, Color defaultColor, float allowableMSE, float allowableGrayscaleError) {
        this.reference = reference;
        this.defaultColor = defaultColor;
        this.allowableMSE = allowableMSE;
        this.allowableGrayscaleError = allowableGrayscaleError;
    }
    
    /**
     * Convert an RGBColor to it's closest Color equivalent.
     * @param   testColor   the rgb triple to test.
     * @return              the closest color to the testColor.
     */
    public Color detect(RGBColor testColor) {
        // We first evaluate whether the color is grayscale.
        if (isGrayscale(testColor)) {
            return getBlackOrWhiteProperty(testColor);
        }
        // The current minimum-error and associated color.
        float minError = Float.MAX_VALUE;
        Color minColor = defaultColor;
        
        for (Color c : reference.keySet()) {
            RGBColor refColor = reference.get(c);
            float err = RGBColor.mse(testColor, refColor);
            if (err <= allowableMSE && err < minError) {
                minError = err;
                minColor = c;
            }
        }
        return minColor;
    }
    
    /**
     * Determine if a given color is grayscale, within the allowed per-channel error.
     * @param   c   The color to check.
     * @return      Whether the color is grayscale or not.
     */
    private boolean isGrayscale(RGBColor c) {
        final RGBColor normalizedColor = normalize(c);
        final float[] channels = {normalizedColor.r, normalizedColor.g, normalizedColor.b};
        for (float channel : channels) {
            if (Math.abs(channel - 1.0f / 3.0f) > allowableGrayscaleError) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get the black or white property for a particular color, depending on whether
     * the color is closer to black or white.
     * @param   c   The color to get the associated property.
     * @return      The property associated with the color.
     */
    private Color getBlackOrWhiteProperty(RGBColor c) {
        final float whiteErr = RGBColor.mse(c, reference.get(Color.WHITE));
        final float blackErr = RGBColor.mse(c, reference.get(Color.BLACK));
        if (blackErr < whiteErr) return Color.BLACK;
        return Color.WHITE;
    }
    
    /**
     * Normalize an RGBColor such that it sums to 1.
     * @param   color   The color to normalize.
     * @return          The normalized color.
     */
    private RGBColor normalize(RGBColor c) {
        final float s = c.r + c.g + c.b;
        return new RGBColor(c.r / s, c.g / s, c.b / s);
    }

    private HashMap<Color, RGBColor> reference;
    private Color defaultColor;
    private float allowableMSE;
    private float allowableGrayscaleError;
}
