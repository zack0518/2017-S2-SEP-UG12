import java.util.Vector;

/**
 * A fixed-size 2D storage container.  Matrices are zero-indexed.
 * @author jkortman
 * @param <T> the type of the matrix elements
 */
public class Matrix<T> {
    /**
     * Default constructor is disallowed; Matrices must have an associated size/height.
     */
    @SuppressWarnings("unused")
    private Matrix() {}
    
    /**
     * Create a matrix.
     * @param height    the number of rows in the matrix.
     * @param width     the number of columns in the matrix.
     */
    public Matrix(int height, int width) throws IllegalArgumentException {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        this.height = height;
        this.width = width;
        storage = new Vector<T>(height * width);
        storage.setSize(height * width);
    }
    
    /**
     * Set an element in the matrix.
     * @param row   the row/vertical position of the element to set.
     * @param col   the column/horizontal position of the element to set.
     * @param value the value to set the element to at the specified position.
     */
    public void set(int row, int col, T value) {
        storage.set(row * width + col, value);
    }
    
    /**
     * Fill an entire matrix with a specified value.
     * @param value the value to fill the matrix with.
     */
    public void fill(T value) {
        for (int i = 0; i < height; i += 1) {
            for (int j = 0; j < width; j += 1) {
                set(i, j, value);
            }
        }
    }
    
    /**
     * Retrieve a value from the matrix at a position.
     * @param row   the row/vertical position of the element to get.
     * @param col   the column/horizontal position of the element to get.
     * @return      the value at the specified position.
     */
    public T get(int row, int col) {
        return storage.get(row * width + col);
    }
    
    /**
     * Get the height of the matrix.
     * @return the height (number of rows) in the matrix.
     */
    public int height() {
        return height;
    }
    
    /**
     * Get the width of the matrix.
     * @return the width (number of columns) in the matrix.
     */
    public int width() {
        return width;
    }

    // Internal storage details.
    private int height;
    private int width;
    private Vector<T> storage;
}
