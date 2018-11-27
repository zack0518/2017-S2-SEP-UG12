import java.util.LinkedList;
import java.util.List;

/**
 * Separating TRACKS and FOOTSTEP
 * @author ravilam
 */


public class ShapeDetection {
	/**
	 * The map
	 */
	public Map map;
	/**
	 * The list contains points that were already accessed and checked
	 */
	private List<Point> footstepsAccessedList;	
	/**
	 * The list contains points that are not visited
	 */
	private List<Point> footstepsWaitingList;
	/**
	 * The list contains tracks that are unknown types
	 */
	private List<Point> unknownTracksList;
	/**
	 * The list contains vehicle tracks
	 */
	private List<Point> vehiclesTracks;
		
	public ShapeDetection(Map map){
		this.map = map;
	}
	
	/**
	 * To identify the given point is a footstep or not, 
	 * if yes, add that point to the waiting list
	 * @param p
	 * @throws Map.OutOfMapBoundsException
	 */
	public void firstDetection(Point p) throws Map.OutOfMapBoundsException {
		
		float trackProb = trackProbCheck(p.xMetres, p.yMetres);
		
		if (Float.compare(trackProb, 0.5f) > 0) { //It is purple(red)
			int numOfAdjacentPurple = adjacentPurpleCounter(p);
			if (numOfAdjacentPurple < 2) {        //x x x  x x x  p x x   |   x x x  x x x     o = TRACK property(red)  
				footstepsWaitingList.add(p);      //x p x  x p o  x x x   |   x p o  o p o     x = None or other properties
			}                                     //x x x  x x x  x x x   |   x o o  o o o     p = given point
			else {
				unknownTracksList.add(p);
			}
		}                                         
		                                          // p must be a footstep | Tracks(Not sure, need the second round checking)
	}
	/**
	 * According to the waiting list and find out the footsteps around the points
	 * @throws Map.OutOfMapBoundsException
	 */
	public void secondDetection() throws Map.OutOfMapBoundsException {
		
		while(!footstepsWaitingList.isEmpty()) {
			
			Point currentPoint = footstepsWaitingList.get(0);  // x x x  x x x    o = TRACK property(red)
			footstepsAccessedList.add(currentPoint);           // x f x  x f o    x = None or other properties
			footstepsWaitingList.remove(currentPoint);         // x x o  x x o    f = FOOTSTEP property 
			typeEstimation(currentPoint);                      
		}                                                      // o must be footsteps
	}                                                          //(Assume footsteps and vehicle tracks are not sticking together)
	
	/**
	 * Set the accessed and checked point to that property
	 * @throws Map.OutOfMapBoundsException
	 */
	public void setTracksTypes() throws Map.OutOfMapBoundsException {
		
		for(int i = 0; i < footstepsAccessedList.size(); i++) {
			Point p = footstepsAccessedList.get(i);
			Map.GridLocation loc = new Map.GridLocation((int)p.xMetres, (int)p.yMetres);
			map.set(Map.Property.TRACKS, loc, 0.0f);	
			map.set(Map.Property.TRACKS_FOOTSTEPS, loc, 1.0f);	
		}
	}
	
	/**
	 * Run the whole detection process
	 * @throws Map.OutOfMapBoundsException
	 */
	
	public void detectionIntegration() throws Map.OutOfMapBoundsException {
		footstepsAccessedList  = new LinkedList<Point>();
		footstepsWaitingList = new LinkedList<Point>();
		unknownTracksList = new LinkedList<Point>();
		
		for(int x = 0; x < map.columns() - 1; x++) {
			for(int y = 0; y < map.rows() - 1; y++) {
				Point p = new Point(x, y);
				firstDetection(p);
			}
		}
		
		secondDetection();
		
		setTracksTypes();
	}
	
	/**
	 * Return the list of footsteps
	 * @return
	 */
	public List<Point> returnFootsteps() {
		return footstepsAccessedList;
	}
	
	
	/**
	 * A counter to count the number of adjacent points that are purple(red) of the given point
	 * @param p
	 * @return
	 * @throws Map.OutOfMapBoundsException
	 */
	private int adjacentPurpleCounter(Point p) throws Map.OutOfMapBoundsException {
		
		float x = p.xMetres;
		float y = p.yMetres;
		int count = 0;
		float temp;
		
		if (x > 0) {
			temp = trackProbCheck((x - 1.0f), y);
			if(Float.compare(temp, 0.5f) > 0) {
				count += 1;
			}	
		}
		
		if(y > 0) {
			temp = trackProbCheck(x, (y - 1.0f));
			if(Float.compare(temp, 0.5f) > 0) {
				count += 1;
			}
		}
		
		if (x < map.columns() - 1) {
			temp = trackProbCheck((x + 1.0f), y);
			if(Float.compare(temp, 0.5f) > 0) {
				count += 1;
			}
		
		}
		
		if (y < map.rows() - 1) {
			temp = trackProbCheck(x, (y + 1.0f));
			if(Float.compare(temp, 0.5f) > 0) {
				count += 1;
			}
		}
		
		return count;	
	}
	
	/**
	 * Searching the given point around to see if there are TRACKS property points, they should be footsteps
	 * (Assume footsteps and vehicle tracks are not sticking together)
	 * @param p
	 * @throws Map.OutOfMapBoundsException
	 */
	private void typeEstimation(Point p) throws Map.OutOfMapBoundsException {
			
		float x = p.xMetres;
		float y = p.yMetres;
		
		float temp;
		Point tempP;
		
		if (x > 0) {
			temp = trackProbCheck((x - 1.0f), y);
			tempP = new Point((x - 1.0f), y);
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);
			}
			
		}
		
		if (y > 0) {
			temp = trackProbCheck(x, (y - 1.0f));
			tempP = new Point(x, (y - 1.0f));
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);

			}
			
		}
		
		if (x < map.columns() - 1) {
			temp = trackProbCheck((x + 1.0f), y);
			tempP = new Point((x + 1.0f), y);
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);

			}
			
		}
		
		if (y < map.rows() - 1) {
			temp = trackProbCheck(x, (y + 1.0f));
			tempP = new Point(x, (y + 1.0f));
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);

			}
			
		}
		
		//Diagonal
		
		if (x < map.columns() - 1 && y < map.rows() - 1) {
			temp = trackProbCheck((x + 1.0f), (y + 1.0f));
			tempP = new Point((x + 1.0f), (y + 1.0f));
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);

			}
			
        }
		
		if (x > 0 && y > 0) {
			temp = trackProbCheck((x - 1.0f), (y - 1.0f));
			tempP = new Point((x - 1.0f), (y - 1.0f));
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);

			}
			
			
		}
		
		if (x > 0 && y < map.rows()-1) {
			temp = trackProbCheck((x - 1.0f), (y + 1.0f));
			tempP = new Point((x - 1.0f), (y + 1.0f));
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);

			}
			
		}
		
		if (x < map.columns()-1 && y > 0) {
			temp = trackProbCheck((x + 1.0f), (y - 1.0f));
			tempP = new Point((x + 1.0f), (y - 1.0f));
			if((Float.compare(temp, 0.5f) > 0) && !footstepsWaitingList.contains(tempP) && !footstepsAccessedList.contains(tempP)) {
				footstepsWaitingList.add(tempP);
				unknownTracksList.remove(tempP);
			}
			
		}
		
	}
	
	/**
	 * Return the probability of TRACK property of the given coordinate
	 * @param x
	 * @param y
	 * @return
	 * @throws Map.OutOfMapBoundsException
	 */
	private float trackProbCheck(float x, float y) throws Map.OutOfMapBoundsException {
		
		Map.GridLocation loc = new Map.GridLocation((int)x, (int)y);
		float trackProb;
		trackProb = map.get(Map.Property.TRACKS, loc);
		
		return trackProb;
	}

}
