import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A node
 * @author ravilam
 */

public class PathNode extends Point {
	/**
	 * Costs to move sideways from one node to another
	 */
	protected static final int sideWaysMovementCost = 1;
	/**
	 * Costs to move diagonally from one node to another
	 */
	protected static final int diagonallyMovementCost = 2;

	/**
	 * A flag that signals robot, this node is walkable
	 */
	private boolean walkable;
	/**
	 * The previous node of current node on the currently calculated path
	 */
	private PathNode previous;
	/**
	 * Weather or not the move from previous to current is diagonally
	 */
	private boolean diagonally;
	/**
	 * Store calculated costs from starting node to current node 
	 */
	private float gCosts;
	/**
	 * Store estimated costs to get from current node to destination node
	 */
	private float hCosts;
	private List<PathNode> adjacentNodes;

	/**
	 * Constructs a walkable fetched node with given coordinates
	 * @param xMetres the x-position of the point in metres
     * @param yMetres the y-position of the point in metres
	 */
	public PathNode(float xMetres, float yMetres) {
		super(xMetres, yMetres);
		this.walkable = true;
		this.adjacentNodes = new ArrayList<PathNode>();
	}
	
	public PathNode(Point point) {
		this(point.xMetres, point.yMetres);
	}
	
	public PathNode() {
		this(0.0f, 0.0f);
	}

	/**
	 * Returns weather or not the move from the previous node to current node
	 * is diagonally.  If it returns false, it is sideways.
	 * @return
	 */
	public boolean isDiagonally() {
		return diagonally;
	}

	/**
	 * Sets weather or not the move from the previous node to current node
	 * is diagonally.  If it returns false, it is sideways.
	 * @param isDiagonally
	 */
	public void setIsDiagonally(boolean isDiagonally) {
		this.diagonally = isDiagonally;
	}

	/**
	 * Sets x and y coordinates
	 * @param x
	 * @param y
	 */
	public void setCoordinates(float x, float y){
		this.xMetres = x;
		this.yMetres = y;
	}

	/**
	 * @return xMetres
	 */
	public float getxMetres() {
		return xMetres;
	}

	/**
	 * @return yMetres
	 */
	public float getyMetres() {
		return yMetres;
	}

	/**
	 * @return walkable
	 */
	public boolean isWalkable() {
		return walkable;
	}

	/**
	 * Set walkable
	 * @param walkable
	 */
	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}

	/**
	 * @return previousNode
	 */
	public PathNode getPrevious() {
		return previous;
	}

	/**
	 * Set previous node
	 * @param previousNode
	 */
	public void setPrevious(PathNode previous) {
		this.previous = previous;
	}

	/**
	 * @return gCosts
	 */
	public float getgCosts() {
		return gCosts;
	}

	/**
	 * Sets gCosts
	 * @param gCosts
	 */
	private void setgCosts(float gCosts) {
		this.gCosts = gCosts;
	}

	/**
	 * Adding previous cost and the next gCost
	 * @param previousNode
	 * @param basicCost
	 */
	public void setgCosts(PathNode previousNode, int basicCost) {
		setgCosts(previousNode.getgCosts() + basicCost);
	}

	/**
	 * The cost for the movement of sideways and diagonally is different
	 */
	public void setgCosts(PathNode previousNode) {
		if (diagonally) {
			setgCosts(previousNode, diagonallyMovementCost);
		}
		else {
			setgCosts(previousNode, sideWaysMovementCost);
		}
	}

	/**
	 * Calculate the gCosts with the movementCost
	 * @param previousNode
	 */

	public float calculategCosts(PathNode previousNode) {
		if(diagonally) {
			return (previousNode.getgCosts() + diagonallyMovementCost);
		}
		else {
			return (previousNode.getgCosts() + sideWaysMovementCost);
		}
	}

	/**
	 * @return hCosts
	 */
	public float gethCosts() {
		return hCosts;
	}

	/**
	 * Sets hCosts
	 * @param hCosts
	 */
	public void sethCosts(float hCosts) {
		this.hCosts = hCosts;
	}
	
	/**
	 * Calculates the hCosts for current node to destination
	 * @param endNode
	 */
	public void sethCosts(PathNode endNode) {
		float d =(float)Math.sqrt(Math.pow(this.xMetres - endNode.xMetres, 2) + Math.pow(this.yMetres - endNode.yMetres, 2));
		sethCosts(d);
	}

	/**
	 * Return gCosts + hCosts
	 * @return fCosts
	 */
	public float getfCosts() {
		return gCosts + hCosts; 
	}

	/**
	 * Sets the adjacent nodes of this node
	 * @param adjacentNodes - The node's adjacent nodes
	 */
	public void setAdjacentNodes(List<PathNode> adjacentNodes) {
		this.adjacentNodes = adjacentNodes;
	}
	
	@Override
    public String toString() {
    		return "(col,row):" + " ("+ this.xMetres + "," + this.yMetres + ")"; 
    }
}