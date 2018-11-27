import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.*;

/**
 * @author Ziang Chen
 * The class is used for displaying Map on the GUI
 * There are 5 situation to display:
 * 1. (Default) Keep displaying what the map currently shows
 * 2. (DestinationCreate) Allows user to modify map to create a destination
 * 3. (AddNoGoZone) Allows user to modify map to create no go zones
 * 4. (ReachedDestination) robot has reached the destination
 */

public class GUIMapRendering extends JFrame {
	
    public enum DisplayType {
    	Default,
    	AddNoGoZone,
    	DestinationCreate,
    	ReachedDestination,
    }
    
    DisplayType CurrentType = DisplayType.Default;
    /**
     * Construct map displaying area
     */
    public GUIMapRendering(Handler handler) {
    	
    	setLayout(new GridBagLayout());
    	xCentre = (int) width/2;
    	yCentre = (int) height/2;
    	
    	robot=new ImageIcon("img/RobotPosition.png");
    	destination=new ImageIcon("img/destination.png");
    	
    	robotPositionX = xCentre;
    	robotPositionY = yCentre;
    	
    	zoomX = (int)width/2;
    	zoomY = (int)height/2;
    	
    	destinationAdded = false;
    	destinationShowed = false;
    	zoomed = false;
    	
    	drawPanel = new DrawPanel();
    	add(drawPanel);
    
    	drawPanel.setBackground(Color.white);
    	
    	this.handler = handler;
    	displayDestinationX = - destination.getIconWidth();
    	displayDestinationY = - destination.getIconHeight();
    	destinationX = -destination.getIconWidth();
    	destinationY = -destination.getIconHeight();
    	
    	displayGridRow =(int) Math.ceil((width / handler.map().rows()));
    	displayGridCol = (int)  Math.ceil((height / handler.map().columns()));

    	receiveMapData(true);
    	scenes();
    	zoomMoveListener();
    	displayRealTime();
    }
    
    /** Import the XML
     *  Import XML after map loading button is clicked
     *  @throws MapXMLImporter.Error
     */
    public void importXML() throws MapXMLImporter.Error{
    	try {
    		String FileName = JOptionPane.showInputDialog(null, "Please Enter The XML file name", "DTD.xml");
			MapXMLImporter mapXMLImporter = new MapXMLImporter(FileName,handler.map(),handler);
		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Export the xml map
     * Export XML after map loading button is clicked
     */
    public void exportXML(){
    	ShapeDetection shapeDetection = new ShapeDetection(handler.map());
    	Point robotPos = new Point(translateToDisplayX(robotPositionX), translateToDisplayY(robotPositionY));
    	
    	try {
			MapXMLExporter.export("SavedMap.xml",
									handler.map(),robotPos, 
									rotateDegree,
									shapeDetection.returnFootsteps(), 
									null, 
									null);
		} catch (MapXMLExporter.Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
     * Translate the point format that can be uses on Display
     * @param x
     * @return
     */
    public int translateToDisplayX(int row){
		return  row * displayGridRow;
    }
    
    public int translateToDisplayY(int col){
		return  (int) height - (col * displayGridCol) - displayGridCol;
    }
    
    /**
     * Translate the point format that can be uses on Matrix
     * @param x
     * @return
     */
    public int translateToMapRow(int x){
    	
    	int rowNum = (int) x / displayGridRow;
    	if(rowNum > handler.map.rows()){
    		rowNum = handler.map.rows();
    	}
    	return rowNum;
    }
    public int translateToMapCol(int y){
    	
    	int colNums = ((int) height -y - displayGridCol - destination.getIconHeight()) / displayGridCol;
    	if (colNums > handler.map.columns()){
    		colNums = handler.map.columns();
    	}
    	return colNums;
    }
    
    /**
     * Receive the input from the user
     * Change the scene according to the user operatoin
     * @param ChangedType
     */
    public void receiveType(DisplayType ChangedType){
      	CurrentType = ChangedType;
    	scenes();
    }
    
    /**
     * Each situation will have a different scene
     * The function is used to create a specific scene by calling the related function
     * 
     */
    private void scenes(){
    	switch (CurrentType) {
    		case Default:
    		case DestinationCreate:
    			createDest();
    			break;
    		case AddNoGoZone:
    			addNoGoZoneMotion();
    			break;
    		case ReachedDestination:
    	    	CurrentType = DisplayType.Default;
    			break;
    	}
    }
    
    /**
     * Get the current Type
     * @return
     */
    public DisplayType getDisplayType(){
    	return CurrentType;
    }
    
    /**
     * Recieve the data in grid from map structure
     */
    public void receiveMapData(boolean isInitial){
    	for(int i = 0; i < handler.map().columns(); i += 1 ){
    		for(int j = 0 ; j <  handler.map().rows(); j += 1 ){
    			
    			
    			RGBColor colorOnPos = handler.map().getColorAtPosition(new Map.GridLocation(i, j));
    			Map.Property propertyOnGrid =  handler.map().getProperty(new Map.GridLocation(i, j));
    			int x = translateToDisplayX(j);
    			int y = translateToDisplayY(i);
    			mapPoints.add(new DisplayMapPoint(x, y,i, j,colorOnPos, propertyOnGrid));
    		}
    	}
    	/**
    	 * Register each row and column at the beginning
    	 */
    	if(isInitial){
    		grids.addAll(mapPoints);
    	}
    }
    
    /**
     * Update the position of the robot 
     * Call repaint to display it in real time
     * The update rate is set by timer
     */
    private void displayRealTime() {
    	listener = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
            	
            	Point robotPos = handler.getRobotPosition(); 
            	rotateDegree = handler.getAngleDegree();
            	robotPositionX = handler.map().getGridLocation(robotPos).col;
            	robotPositionY = handler.map().getGridLocation(robotPos).row;
            	Point colorSensorPos = handler.getColorSensorPosition();
            	handler.map().getGridLocation(colorSensorPos);
    			System.out.println(handler.getRobotPosition());
            	System.out.println("GUI robot : "+robotPositionX+"    "+robotPositionY);
            	receiveMapData(false);
            	drawPanel.repaint();
            }
        };
        
        timer = new Timer(200, listener);
        timer.start();
    }
    public synchronized void stopTimer(){
    	timer.stop();
    }

    /**
     * Add destination where the user clicked and confirmed
     * Send it to UIEventQueue
     * Represent destination on the map
     */
    public void addDestination(){
    	destinationAdded = true;
    	drawPanel.repaint();
    }
    
    public DisplayMapPoint findPoint(int x, int y){
    	for(int i = 0 ; i < grids.size() ; i += 1){
    		if((x >= grids.get(i).GetX() && x <=  (grids.get(i).GetX() + displayGridRow)) &&
    			y >= grids.get(i).GetY() && y <=  (grids.get(i).GetY() + displayGridCol)){
    			return grids.get(i);
    		}
    	}
    	return null;
    }
    
    public Map.GridLocation getDestination(){
    	DisplayMapPoint destinationGrid = findPoint(displayDestinationX, displayDestinationY);
    	Map.GridLocation destinationMapGrid = new Map.GridLocation(destinationGrid.getGridRow(),destinationGrid.getGridCol());
		return destinationMapGrid;
    }

    
    /**
     * Zoom in and Zoom out
     * One click on Zoom in will increment scale 0.1
     * One click on Zoom out will decrement scale 0.1
     */
    public void zoomIn(){
    	if(scale<2){
    		zoomed = true;
    		scale += 0.1;
    	}
    	drawPanel.repaint();
    }
    public void zoomOut(){
    	if(scale > 1){
    		scale -= 0.1;
    	}else if (scale == 1){
    		zoomed = false;
    	}
    	drawPanel.repaint();
    }
    
    public void zoomMoveListener(){
    	drawPanel.addMouseListener(new java.awt.event.MouseAdapter() {
	    	public void mousePressed(java.awt.event.MouseEvent evt){
	    		if(zoomed){
	    			zoomX = evt.getX();
	    			zoomY = evt.getY();
	    			drawPanel.repaint();
	    		}
	    	}
	 });
    }
    
    /**
     * Present the destination where user clicked
     */
    private void createDest() {
    	drawPanel.addMouseListener(new java.awt.event.MouseAdapter() {
	    	public void mousePressed(java.awt.event.MouseEvent evt){
	    		if(!destinationAdded){
	    			displayDestinationX = evt.getX();
	    			displayDestinationY = evt.getY();
	    			drawPanel.repaint();
	    		}
	    	}
	 });
    }
    
    /**
     * Present the No Go Zone where user drag the mouse to make it
     */
    private void addNoGoZoneMotion(){

       MyMouseListener listener = new MyMouseListener();
       drawPanel.addMouseListener(listener);
       drawPanel.addMouseMotionListener(listener);
    }
    
    public Map.GridLocation getNGStartPoint(){
    	DisplayMapPoint noGoStart = findPoint(shapeX, shapeY);
    	Map.GridLocation noGoStartGrid = new Map.GridLocation(noGoStart.gridRow, noGoStart.gridCol);
    	return noGoStartGrid;
    	
    }
    
    public Map.GridLocation getNGEndPoint(){
    	DisplayMapPoint noGoEnd = findPoint(shapeX + shapeWidth, shapeY  + shapeHeight);
    	Map.GridLocation noGoEndGrid = new Map.GridLocation(noGoEnd.gridRow, noGoEnd.gridCol);
    	return noGoEndGrid;
    }
    
    class MyMouseListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
    		noGoStartX = e.getX();
    		noGoStartY = e.getY();
    		drawPanel.repaint();
        }
        public void mouseDragged(MouseEvent e) {
           	noGoEndX = e.getX();
        	noGoEndY = e.getY();
        	drawPanel.repaint();
        }
        public void mouseReleased(MouseEvent e) {
        	noGoEndX = e.getX();
        	noGoEndY = e.getY();
        	drawPanel.repaint();
        }
    }
    /**
     * remove it from the screen
     */
    void clearDestination(){
    	destinationX = - destination.getIconWidth();
    	destinationY = - destination.getIconHeight();
    }
    
    void clearNoGoZone(){
    	noGoStartX = 0;
    	noGoStartY = 0;
    	noGoEndX = 0;
    	noGoEndY = 0;
    }
    /**
     * Reset the positions and initial all values
     */
    public void reset(){
    	
    	clearDestination();
    	CurrentType = DisplayType.Default;
    	destinationAdded = false;
    	clearNoGoZone();
    	drawPanel.repaint();
    }

    /**
     * set the scale of zoom in/out for each component
     * @param MapComponent
     * @param g
     */
    private void zoomInAndZoomOut(Graphics2D MapComponent,Graphics g){
    	g.translate(zoomX, zoomY);
     	MapComponent.scale(scale, scale);
     	g.translate(-zoomX, -zoomY);
    }
    
    private class DrawPanel extends JPanel {
    	
        protected void paintComponent(Graphics g) {
        	
        	Graphics2D MapComponent = (Graphics2D) g;
        	zoomInAndZoomOut(MapComponent,g);
        	super.paintComponent(g);
        	Graphics2D RobotPosition= (Graphics2D) g.create();
            
         	/**
         	 * Rendering Map features from Map structure
         	 */
         	for (int i = 0; i < mapPoints.size(); i++) {
         		g.setColor(mapPoints.get(i).GetColor());
        		g.fillRect(mapPoints.get(i).PosX, mapPoints.get(i).PosY, displayGridRow, displayGridCol);
         	}
         	mapPoints.clear();
         	
        	/**
             * Display the Position of the robot
             * calculate the rotation degree
             */
         	int robotDisplayX = translateToDisplayX(robotPositionX)  - robot.getIconWidth()/2;
         	int robotDisplayY = translateToDisplayY(robotPositionY)  - robot.getIconHeight()/2;

            RobotPosition.rotate(Math.toRadians((double) - rotateDegree),
            								robotDisplayX + (robot.getIconWidth() / 2), 
            								(robotDisplayY + (robot.getIconHeight() / 2 )));
            
            robot.paintIcon(this,RobotPosition,robotDisplayX, robotDisplayY);
            
            zoomInAndZoomOut(RobotPosition, g);
            
            g.setColor(Color.red);
            /**
             * Display text to introduce the point is the destination
             */
            
        	if(destinationAdded){
        	 	destination.paintIcon(this, g,displayDestinationX-destination.getIconWidth()/2,displayDestinationY-destination.getIconHeight()/2);
            	g.drawString("destination", displayDestinationX, displayDestinationY + destination.getIconHeight());
            	zoomInAndZoomOut(MapComponent,g);
        	}
        	
        	/**
        	 * Map allows user modify the map
        	 * User can create destination by clicking
        	 * User can create No Go Zone by dragging
        	 */
         	switch (CurrentType) {
    		case DestinationCreate:
                destination.paintIcon(this, g,displayDestinationX-destination.getIconWidth()/2,displayDestinationY-destination.getIconHeight()/2);
                zoomInAndZoomOut(MapComponent,g);
                break;
                
    		case AddNoGoZone:
    			g.setColor(Color.GRAY);
    			shapeX = Math.min(noGoStartX,noGoEndX);
                shapeY = Math.min(noGoStartY,noGoEndY);
                shapeWidth = Math.abs(noGoStartX-noGoEndX); 
                shapeHeight= Math.abs(noGoStartY-noGoEndY);
                g.fillRect(shapeX, shapeY, shapeWidth, shapeHeight);
         	}
        	}
        
        
			public Dimension getPreferredSize() {
				return new Dimension((int) (width*scale), (int) (height*scale));
			}
    	}
    
    	/**
    	 * Stores the Data of Point used for Display on the GUI
    	 *
    	 */
		public class DisplayMapPoint {
			
			DisplayMapPoint(int PosX, 
							int PosY, 
							int row,
							int col,
							RGBColor FeatureColor,
							Map.Property Property){
				
				this.PosX = PosX;
				this.PosY = PosY;
				this.gridRow = row;
				this.gridCol = col;
				this.FeatureColor = FeatureColor;
				this.Property = Property;
			}
			public Color GetColor(){
				return new Color(FeatureColor.r,FeatureColor.g,FeatureColor.b);
			}
			public int GetX(){
				return PosX;
			}
			public int GetY(){
				return PosY;
			}
			public int getGridRow(){
				return gridRow;
			}
			
			public int getGridCol(){
				return gridCol;
			}
			public RGBColor FeatureColor;
			public Map.Property Property;
			int PosX,PosY,gridRow,gridCol;

		}
    	private final double width = 600;
    	private final double height= 380;
    	
    	private int displayGridRow,displayGridCol;
    	private int xCentre,yCentre;
    	private ArrayList <Point> recievedMapFigures;
    	private ImageIcon robot,destination;
    	private float rotateDegree;
    	
    	private int displayDestinationX,displayDestinationY;
    	private int destinationX,destinationY;
    	private int noGoStartX, noGoEndX,noGoStartY, noGoEndY;
    	
    	public int robotPositionX,robotPositionY;
    	private int shapeX,shapeY,shapeWidth,shapeHeight;
    	public float turnAngle;
    	public RGBColor colorOnPosition;
    	private DrawPanel drawPanel;
    	
    	private Handler handler;
    	private ActionListener listener;
    	boolean destinationAdded,destinationShowed;
    	boolean zoomed;
    	int zoomX,zoomY;
    	boolean stopTimer = false;
    	private double scale = 1;
    	Timer timer;
    	Point currentPoint;

    	List <DisplayMapPoint> mapPoints =  new ArrayList<DisplayMapPoint>();
    	List <DisplayMapPoint> grids = new ArrayList <DisplayMapPoint>();
        List<Point> footsteps = new ArrayList<Point>();
        List<List<Point>> vehicleTracks = new ArrayList<List<Point>>();
        List<List<Point>> landingTrack = new ArrayList<List<Point>>();
}
