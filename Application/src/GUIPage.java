import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;



/** Author: Ziang Chen	
 * Graphical User Interface 
 * Provide a flat-design interface to interact with user
 * Receives the operation from user
 * Send the commands to UIEventQueue
 * The GUI applied group layout
*/

public class GUIPage extends javax.swing.JFrame {
	
    	// Switch Section
    	private javax.swing.JLabel currentMode;
    	private javax.swing.JPanel switchPanel;
    	private javax.swing.JButton manaulSwitch;
    	private javax.swing.JButton autoSwitch;
    	private javax.swing.JButton reset;
    	private javax.swing.JLabel resetIntro;
    	private javax.swing.JPanel theWholePanel;
    	
    	// Direction Control Section
    	private javax.swing.JPanel controlPanel;
      	private javax.swing.JButton forward, turnAntiClockWise;
    	private javax.swing.JButton turnClockWise;
    	private javax.swing.JButton backward;
    	private javax.swing.JButton stop;
    	private javax.swing.JButton close;
    	
    	// Map operatoin Section
    	private javax.swing.JPanel mapControlPanel;
    	private javax.swing.JPanel mapPanel;
    	private javax.swing.JButton createDest;
      	private javax.swing.JButton save;
        private javax.swing.JButton mapLoading;
    	private javax.swing.JButton zoomIn;
    	private javax.swing.JButton zoomOut;  
    	private javax.swing.JPanel mapDisplay;
    	public UIEventQueue uiEventQueue;
    	private javax.swing.JButton addNoGo;
    	private String currentModeIntro;
    	private DestinationState destStatus;
    	private NoGoZoneState noGoZoneStatus;
    	private javax.swing.JButton sensorMoveLeft;
    	private javax.swing.JButton sensorMoveRight;
    	
    	private ControlMode mode;
    	private GUIMapRendering mapTracking;
    	
        private enum DestinationState {
        	WaitingToCreate,
        	DecidingToCreate,
        	HasCreated
        }
        private enum NoGoZoneState {
        	WaitingToCreate,
        	DecidingToCreate,
        	WaitingAnother,
        	Finished
        }
        private enum ControlMode{
        	Manual,Auto;
        }
        
        
        // Initialize the layout and components of GUI
        // set the GUI to be displayed at centre
        public GUIPage(UIEventQueue uiEventQueue,GUIMapRendering GuiMapRendering) {
        	
        	this.uiEventQueue = uiEventQueue;
        	this.mapTracking = GuiMapRendering;
        	init();
        	this.setLocationRelativeTo(null);
            mapDisplay.add(mapTracking.getContentPane());
            mapDisplay.setLayout(new GridBagLayout());
            mapTracking.receiveType(GUIMapRendering.DisplayType.Default);
            
        }
                     
    private void init() {

        theWholePanel = new javax.swing.JPanel();
        mapPanel = new javax.swing.JPanel();
        mapControlPanel = new javax.swing.JPanel();
        zoomOut = new javax.swing.JButton();
        zoomIn = new javax.swing.JButton();
        save = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        forward = new javax.swing.JButton();
        turnClockWise = new javax.swing.JButton();
        turnAntiClockWise = new javax.swing.JButton();
        backward = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        switchPanel = new javax.swing.JPanel();
        autoSwitch = new javax.swing.JButton();
        currentMode = new javax.swing.JLabel();
        manaulSwitch = new javax.swing.JButton();
        reset = new javax.swing.JButton();
        resetIntro = new javax.swing.JLabel();
        createDest = new javax.swing.JButton();
        close= new javax.swing.JButton();
        mapLoading = new javax.swing.JButton();
        sensorMoveLeft = new javax.swing.JButton();
        destStatus=DestinationState.WaitingToCreate;
        noGoZoneStatus = NoGoZoneState .WaitingToCreate;
        mapDisplay = new javax.swing.JPanel();
       
        
        sensorMoveRight = new javax.swing.JButton();
    	javax.swing.JLabel sensorMoveIntro= new  javax.swing.JLabel();
    	buttonFormat(sensorMoveLeft);
    	buttonFormat(sensorMoveRight);
    	sensorMoveLeft.setText("Left");
    	sensorMoveRight.setText("Right");
        sensorMoveIntro.setText("Sensor Move Control");
        sensorMoveIntro.setForeground(new java.awt.Color(255, 255, 255));
       
 
        addNoGo = new javax.swing.JButton();
 
        // Add key listener and request focus
        addKeyListener(new InputKeyEvents());
        setTitle("UG12 - Lunar Rover Mapping Robot");
        setResizable(false);
        setFocusable(true);  
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        // set the size of display area
        setPreferredSize(new java.awt.Dimension(1135, 600));
        
       // setResizable(false);
        //set the property of the whole layout
        theWholePanel.setBackground(new java.awt.Color(36, 47, 65));
        theWholePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        theWholePanel.setRequestFocusEnabled(false);
        
        //set the property of the map area layout
        //mapPanel.setBackground(new java.awt.Color(0, 0, 0));
        mapPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mapPanel.setPreferredSize(new java.awt.Dimension(550, 390));
        mapDisplay.setBackground(new java.awt.Color(36, 47, 65));
        /*
         * Add Map display methods into the panel
         */
        
        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mapDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mapDisplay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        /*
         * Map Section
         */
        mapControlPanel.setBackground(new java.awt.Color(36, 47, 65));
    	// Zoom out button format
        zoomOut.setText("Zoom Out");
        buttonFormat(zoomOut);
        // Zoom in button format
        zoomIn.setText("Zoom In");
        buttonFormat(zoomIn);
        
        zoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            		ZoomInActionPerformed(evt);
            }
        });
        
        zoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            		ZoomOutActionPerformed(evt);
            }
        });
        // save button format
        save.setText("save Map");
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveActionPerformed(evt);
            }
        });
        buttonFormat(save);
        close.setText(" close");
        buttonFormat(close);
        
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseActionPerformed(evt);
            }
        });
        javax.swing.GroupLayout mapControlPanelLayout = new javax.swing.GroupLayout(mapControlPanel);
        mapControlPanel.setLayout(mapControlPanelLayout);
        
        
        /** Adding components to Map Control panel
        * The Position depends by the vertical and horizontal gap
        * The Gap size - the size of the gap
        * The three values indicates
        *	 min - the minimum size of the gap
        *	 pref - the preferred size of the gap
        *	 max - the maximum size of the gap
        * The parameters of preferredGap:
        * 	 comp1 - the first component
		*	 comp2 - the second component
		*	 type - the type of gap
        *    pref - the preferred size of the grap; one of DEFAULT_SIZE or a value >= 0
        *    max - the maximum size of the gap; one of DEFAULT_SIZE, PREFERRED_SIZE or a value >= 0
        */
        
        mapControlPanelLayout.setHorizontalGroup(
                mapControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mapControlPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(zoomIn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(zoomOut, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(save, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            mapControlPanelLayout.setVerticalGroup(
                mapControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mapControlPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(mapControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(zoomIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(zoomOut, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(save, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
        /*
         * Controller Section
         */
        controlPanel.setBackground(new java.awt.Color(36, 47, 65));
        controlPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        
        // Direction button format
        buttonFormat(forward);
        buttonFormat(turnClockWise);
        buttonFormat(turnAntiClockWise);
        buttonFormat(backward);
        buttonFormat(stop);
        
        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
                controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addGap(88, 88, 88)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(controlPanelLayout.createSequentialGroup()
                            .addComponent(forward, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                        .addGroup(controlPanelLayout.createSequentialGroup()
                            .addComponent(backward, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                                    .addComponent(sensorMoveLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(sensorMoveRight, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap())
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                                    .addComponent(sensorMoveIntro)
                                    .addGap(18, 18, 18))))))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(turnAntiClockWise, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(79, 79, 79)
                    .addComponent(turnClockWise, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                    .addComponent(stop, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(53, 53, 53))
            );
            controlPanelLayout.setVerticalGroup(
                controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(controlPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(forward, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(controlPanelLayout.createSequentialGroup()
                            .addGap(4, 4, 4)
                            .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(stop)
                                .addComponent(turnClockWise, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(turnAntiClockWise, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(backward, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(controlPanelLayout.createSequentialGroup()
                            .addGap(100, 100, 100)
                            .addComponent(sensorMoveIntro)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(sensorMoveLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(sensorMoveRight, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(96, 96, 96))
            );
        /*
         * Switch Section
         */
        switchPanel.setBackground(new java.awt.Color(36, 47, 65));
        switchPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        currentMode.setForeground(new java.awt.Color(255, 255, 255));
        currentModeIntro="Current Mode:  MANUAL";
        mode = ControlMode.Manual;
        currentMode.setText(currentModeIntro);
    	
        buttonFormat(reset);
        
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	ResetActionPerformed(evt);
           }
       });
        resetIntro.setForeground(new java.awt.Color(255, 255, 255));
        resetIntro.setText("Reset Map Creations");
        
        autoSwitch.setText("Auto");
        buttonFormat(autoSwitch);
        addNoGo.setText("Add No Go Zone");
        buttonFormat(addNoGo);
        addNoGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	 AddNoGoActionPerformed(evt);
            }
        });
        
        
        manaulSwitch.setText("Manual");
        buttonFormat(manaulSwitch);
    	manaulSwitch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    	
        javax.swing.GroupLayout switchPanelLayout = new javax.swing.GroupLayout(switchPanel);
        switchPanel.setLayout(switchPanelLayout);
        switchPanel.setLayout(switchPanelLayout);
        switchPanelLayout.setHorizontalGroup(
                switchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, switchPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(resetIntro)
                    .addGap(23, 23, 23))
                .addGroup(switchPanelLayout.createSequentialGroup()
                    .addGroup(switchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(switchPanelLayout.createSequentialGroup()
                            .addGap(70, 70, 70)
                            .addComponent(currentMode, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(switchPanelLayout.createSequentialGroup()
                            .addGap(33, 33, 33)
                            .addComponent(autoSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                            .addComponent(manaulSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(86, 86, 86)))
                    .addComponent(reset, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(43, 43, 43))
            );
            switchPanelLayout.setVerticalGroup(
                switchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(switchPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(switchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(switchPanelLayout.createSequentialGroup()
                            .addComponent(reset, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(resetIntro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(17, 17, 17))
                        .addGroup(switchPanelLayout.createSequentialGroup()
                            .addComponent(currentMode, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(switchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(manaulSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(autoSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            );

        createDest.setText("Create Destination");
        createDest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	CreateDestActionPerformed(evt);
            }
        });
        buttonFormat(createDest);
        mapLoading.setText("Load XML Map");
        buttonFormat(mapLoading);
        
        mapLoading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	try {
					loadXMLActionPerformed(evt);
				} catch (MapXMLImporter.Error e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        javax.swing.GroupLayout theWholePanelLayout = new javax.swing.GroupLayout(theWholePanel);
        theWholePanel.setLayout(theWholePanelLayout);
        theWholePanelLayout.setHorizontalGroup(
                theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(theWholePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(theWholePanelLayout.createSequentialGroup()
                            .addComponent(createDest, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(mapLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(addNoGo, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(mapControlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)))
                    .addGroup(theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(theWholePanelLayout.createSequentialGroup()
                            .addGap(33, 33, 33)
                            .addGroup(theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(switchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(theWholePanelLayout.createSequentialGroup()
                            .addGap(407, 407, 407)
                            .addComponent(close, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)))
                    .addGap(0, 879, Short.MAX_VALUE))
            );
            theWholePanelLayout.setVerticalGroup(
                theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(theWholePanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(close, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(createDest, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addNoGo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mapLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(11, 11, 11)
                    .addGroup(theWholePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(theWholePanelLayout.createSequentialGroup()
                            .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(switchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(mapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(5, 5, 5)
                    .addComponent(mapControlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(theWholePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(theWholePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buttonIcons();
        Listeners();

        pack();
    }                   
    private void buttonFormat(JButton currenButton){
    	currenButton.setBackground(new java.awt.Color(36, 47, 65));
    	currenButton.setForeground(new java.awt.Color(255, 255, 255));
    	currenButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    	currenButton.setContentAreaFilled(false);
    }
    
    /**
     * Give the button corresponding icon
     * Button icons are stored in img folder
     * 
     */
    private void buttonIcons(){
        forward.setIcon(new ImageIcon("img/forward-small.png"));
        turnAntiClockWise.setIcon(new ImageIcon("img/left-rotation-small.png"));
        turnClockWise.setIcon(new ImageIcon("img/right-rotation-small.png"));
        backward.setIcon(new ImageIcon("img/back-small.png"));
        stop.setIcon(new ImageIcon("img/stop-small.png")); 
        reset.setIcon(new ImageIcon("img/refresh.png"));
        zoomIn.setIcon(new ImageIcon("img/zoom-in.png"));
        zoomOut.setIcon(new ImageIcon("img/zoom-out.png"));
        save.setIcon(new ImageIcon("img/save.png"));
        close.setIcon(new ImageIcon("img/close.png")); 
        createDest.setIcon(new ImageIcon("img/dest.png")); 
        mapLoading.setIcon(new ImageIcon("img/import.png")); 
        addNoGo.setIcon(new ImageIcon("img/adding.png")); 
    }
	/**
	 *  Listener Section
	 *  Add Listener to the buttons on GUI
	 *  Send the related event to event queue 
	 *  Give feedback to user when mouse entered or clicked
	 */
	public void Listeners(){
	
	/**
	 *  Manual control Listeners	
	 */	
    forward.addMouseListener(new java.awt.event.MouseAdapter() {
    	public void mouseEntered(java.awt.event.MouseEvent evt) {
    		clickable(evt, forward);
    	}
    	public void mouseExited(java.awt.event.MouseEvent evt) {
    		mouseLeave(evt, forward);
    	}
    	public void mousePressed(java.awt.event.MouseEvent evt){
    		uiEventQueue.add(UIEventQueue.EventType.FORWARD_PRESSED);
			requestFocus();
    	}
    	public void mouseReleased(java.awt.event.MouseEvent evt){
    		uiEventQueue.add(UIEventQueue.EventType.FORWARD_RELEASED);
    		requestFocus();
    		
    	}
    });
    
    turnAntiClockWise.addMouseListener(new java.awt.event.MouseAdapter() {
    	public void mouseEntered(java.awt.event.MouseEvent evt) {
    		clickable(evt, turnAntiClockWise);
    	}
    	public void mouseExited(java.awt.event.MouseEvent evt) {
    		mouseLeave(evt, turnAntiClockWise);
    	}
    	public void mousePressed(java.awt.event.MouseEvent evt){
    		uiEventQueue.add(UIEventQueue.EventType.TURN_ANTICLOCKWISE_PRESSED);
			requestFocus();
    	}
    	public void mouseReleased(java.awt.event.MouseEvent evt){
    		uiEventQueue.add(UIEventQueue.EventType.TURN_ANTICLOCKWISE_RELEASED);
    		requestFocus();
    		
    	}
    });

    turnClockWise.addMouseListener(new java.awt.event.MouseAdapter()  {
    	public void mouseEntered(java.awt.event.MouseEvent evt) {
    		clickable(evt, turnClockWise);
    	}
    	public void mouseExited(java.awt.event.MouseEvent evt) {
    		mouseLeave(evt, turnClockWise);
    	}
    	public void mousePressed(java.awt.event.MouseEvent evt){
    		uiEventQueue.add(UIEventQueue.EventType.TURN_CLOCKWISE_PRESSED);
			requestFocus();
    	}
    	public void mouseReleased(java.awt.event.MouseEvent evt){
    		uiEventQueue.add(UIEventQueue.EventType.TURN_CLOCKWISE_RELEASED);
    		requestFocus();
    	}
    });
    
    backward.addMouseListener(new java.awt.event.MouseAdapter() {
    	 public void mouseEntered(java.awt.event.MouseEvent evt) {
         	clickable(evt, backward);
         }
         public void mouseExited(java.awt.event.MouseEvent evt) {
        	mouseLeave(evt, backward);
         }
        public void mousePressed(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType. BACKWARD_PRESSED);
    		requestFocus();
        }
        public void mouseReleased(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.BACKWARD_RELEASED);
        	requestFocus();
        }
    });
    
    stop.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
        	clickable(evt, stop);
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
        	mouseLeave(evt, stop);
        }
        public void mousePressed(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.EMERGENCY_STOP_PRESSED);
    		requestFocus();
        }
        public void mouseReleased(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.EMERGENCY_STOP_RELEASED);
        	requestFocus();
        }
    });
    sensorMoveLeft.addMouseListener(new java.awt.event.MouseAdapter() {
    	 public void mouseEntered(java.awt.event.MouseEvent evt) {
          	clickable(evt, sensorMoveLeft);
          }
          public void mouseExited(java.awt.event.MouseEvent evt) {
         	mouseLeave(evt, sensorMoveLeft);
          }
          
        public void mousePressed(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.SENSOR_LEFT_PRESSED);
    		requestFocus();
        }
        public void mouseReleased(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.SENSOR_LEFT_RELEASED);
        	requestFocus();
        }
    });
    
    sensorMoveRight.addMouseListener(new java.awt.event.MouseAdapter() {
   	 public void mouseEntered(java.awt.event.MouseEvent evt) {
   		 	clickable(evt, sensorMoveRight);
       }
       public void mouseExited(java.awt.event.MouseEvent evt) {
    	    mouseLeave(evt, sensorMoveRight);
       }
       
        public void mousePressed(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.SENSOR_RIGHT_PRESSED);
    		requestFocus();
        }
        public void mouseReleased(java.awt.event.MouseEvent evt){
        	uiEventQueue.add(UIEventQueue.EventType.SENSOR_RIGHT_RELEASED);
        	requestFocus();
        }
    });
    
    /**
     * Manual Auto Switch
     * 
     */
    manaulSwitch.addMouseListener(new java.awt.event.MouseAdapter() {
    	public void mousePressed(java.awt.event.MouseEvent evt){
    		mode = ControlMode.Manual;
    		currentModeIntro="Current Mode:  MANUAL";
    		currentMode.setText(currentModeIntro);
    		uiEventQueue.add(UIEventQueue.EventType.MANUAL);
    		// manual switch will be shown as pressed 
    		autoSwitch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    		manaulSwitch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
			requestFocus();
    	}
    });
    autoSwitch.addMouseListener(new java.awt.event.MouseAdapter() {
    	public void mousePressed(java.awt.event.MouseEvent evt){
    		mode = ControlMode.Auto;
    		currentModeIntro="Current Mode:  AUTO";
    		currentMode.setText(currentModeIntro);
    		uiEventQueue.add(UIEventQueue.EventType.AUTO);
    		
    		mapTracking.receiveType(GUIMapRendering.DisplayType.Default);
    		manaulSwitch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    		autoSwitch.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    		//  AUTO switch will be shown as pressed 
    		if(destStatus == DestinationState.HasCreated && mapTracking.getDestination() != null){
    				uiEventQueue.receiveDestination(mapTracking.getDestination());
    				uiEventQueue.add(UIEventQueue.EventType.DESTINATION_CREATED);
    			
    		}
    		requestFocus();
    	}	
    });
    }
	
	
	/**
	 * The boolean is used on sending one command when holding the key 
	 */
	boolean UpAblePress=true, LeftTurnAblePress=true, 
			RightTurnAblePress=true,BackAblePress=true, 
			StopAblePress=true,SensorLeftAblePress=true, 
			SensorRightAblePress=true;
	
	//Keyboard Action
	public class InputKeyEvents extends KeyAdapter{
	   public void keyPressed(KeyEvent e) {
     	  int key = e.getKeyCode();
     	    switch( key ) { 
     	        case KeyEvent.VK_UP:
     	        	if(UpAblePress){
     	        		forward.setBackground(new java.awt.Color(255, 255, 255));
     	        		uiEventQueue.add(UIEventQueue.EventType.FORWARD_PRESSED);
     	        		UpAblePress=false;
     	        	}
     	            break;
     	        case KeyEvent.VK_DOWN:
     	        	if(BackAblePress){
     	        		backward.setBackground(new java.awt.Color(255, 255, 255));
     	        		uiEventQueue.add(UIEventQueue.EventType. BACKWARD_PRESSED);
     	        		BackAblePress=false;
     	        	}
     	            break;
     	        case KeyEvent.VK_LEFT:
     	        	if(LeftTurnAblePress){
     	        		turnAntiClockWise.setBackground(new java.awt.Color(255, 255, 255));
     	        		uiEventQueue.add(UIEventQueue.EventType.TURN_ANTICLOCKWISE_PRESSED);
     	        		LeftTurnAblePress=false;
     	        	}
     	            break;
     	        case KeyEvent.VK_RIGHT :
     	        	if(RightTurnAblePress){
     	        		turnClockWise.setBackground(new java.awt.Color(255, 255, 255));
     	        		uiEventQueue.add(UIEventQueue.EventType.TURN_CLOCKWISE_PRESSED);
     	        		RightTurnAblePress=false;
     	        	}
     	            break;
     	       case KeyEvent.VK_SPACE :
     	    	   	if(StopAblePress){
     	    		   stop.setBackground(new java.awt.Color(255, 255, 255));
     	    		   uiEventQueue.add(UIEventQueue.EventType.EMERGENCY_STOP_PRESSED);
     	    		   StopAblePress=false;
     	    	   	}
    	            break;
     	     }
     	
	   }
	   public void keyReleased(KeyEvent e) {
      	  int key = e.getKeyCode();
      	  		switch( key ) { 
      	  		case KeyEvent.VK_UP:
      	  			forward.setBackground(new java.awt.Color(36, 47, 65));
      	  			uiEventQueue.add(UIEventQueue.EventType.FORWARD_RELEASED);
      	  			UpAblePress=true;
      	  		    break;
      	  		case KeyEvent.VK_DOWN:
  
      	  			backward.setBackground(new java.awt.Color(36, 47, 65));
      	  			uiEventQueue.add(UIEventQueue.EventType.BACKWARD_RELEASED);
      	  			BackAblePress=true;
      	  			break;
      	  		case KeyEvent.VK_LEFT:
      	  			turnAntiClockWise.setBackground(new java.awt.Color(36, 47, 65));
      	  			uiEventQueue.add(UIEventQueue.EventType.TURN_ANTICLOCKWISE_RELEASED);
      	  			LeftTurnAblePress=true;
      	  			break;
      	  		case KeyEvent.VK_RIGHT :
      	  			turnClockWise.setBackground(new java.awt.Color(36, 47, 65));
      	  			uiEventQueue.add(UIEventQueue.EventType.TURN_CLOCKWISE_RELEASED);
      	  			RightTurnAblePress=true;
      	  			break;
      	  		case KeyEvent.VK_SPACE :
      	  			stop.setBackground(new java.awt.Color(36, 47, 65));
      	  			uiEventQueue.add(UIEventQueue.EventType.EMERGENCY_STOP_RELEASED);
      	  			StopAblePress=true;
      	  			break;
      	  		}
      	  }
	  	}

		private void CloseActionPerformed(java.awt.event.ActionEvent evt) {  
				uiEventQueue.add(UIEventQueue.EventType.CLOSE);
	    }     
		
		/**
		 * Create Destination
		 */
		private void CreateDestActionPerformed(java.awt.event.ActionEvent evt) {   
			if ( mode == ControlMode.Manual ){
				JOptionPane.showMessageDialog(null,"Please swtich to auto mode then create destination");
			}else {
			  switch(destStatus){
			
				case WaitingToCreate:
				mapTracking.clearDestination();
				mapTracking.receiveType(GUIMapRendering.DisplayType.DestinationCreate);
				createDest.setText("Confirm");
				destStatus = DestinationState.DecidingToCreate;
				break;
				
				
				case DecidingToCreate:
				mapTracking.receiveType(GUIMapRendering.DisplayType.Default);
				createDest.setText("Create Destination");
				mapTracking.addDestination();
				destStatus = DestinationState.HasCreated;
				createDest.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
				
				// Sending message to UI event queue
				uiEventQueue.receiveDestination(mapTracking.getDestination());
				uiEventQueue.add(UIEventQueue.EventType.DESTINATION_CREATED);
				break;
			 }
		   }
			requestFocus();
	    }         
		
		/**
		 * Add no go zone Button event
		 * Two state : 
		 * When the state is waiting to create, user click on it and it will allow user to create no go zone
		 * When the state is deciding to create, 
		 * it will allow user to confirm the no go zone just created and send the event to event queue 
		 * @param evt
		 */
		private void AddNoGoActionPerformed(java.awt.event.ActionEvent evt) {
			switch(noGoZoneStatus){
			case WaitingToCreate:
				mapTracking.clearNoGoZone();
				mapTracking.receiveType(GUIMapRendering.DisplayType.AddNoGoZone);
				addNoGo.setText("Confirm");
				noGoZoneStatus = NoGoZoneState.DecidingToCreate;
				break;
				
			case DecidingToCreate:
				addNoGo.setText("Create Another");
				noGoZoneStatus = NoGoZoneState.WaitingToCreate;
				mapTracking.receiveType(GUIMapRendering.DisplayType.Default);
				
				uiEventQueue.receiveNoGoZoneStart(mapTracking.getNGStartPoint());
				uiEventQueue.receiveNoGoZoneEnd(mapTracking.getNGEndPoint());
				uiEventQueue.add(UIEventQueue.EventType.ADD_NO_GO_ZONES);
				break;
			}
			requestFocus();
		}
		
		/**
		 * erase the user created figures on GUI
		 * 
		 */
		private void reset(){

			noGoZoneStatus=NoGoZoneState.WaitingToCreate;
			destStatus=DestinationState.WaitingToCreate;
			mapTracking.receiveType(GUIMapRendering.DisplayType.Default);
			addNoGo.setText("Add No Go Zone");
			createDest.setText("Create Destination");
			createDest.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			
		}
		
		private void ResetActionPerformed(java.awt.event.ActionEvent evt){
			reset();
			mapTracking.reset();
			requestFocus();
		}
		
		private void ZoomOutActionPerformed(java.awt.event.ActionEvent evt) {                                         
			mapTracking.zoomOut();
			requestFocus();
		}     
		private void ZoomInActionPerformed(java.awt.event.ActionEvent evt) {                                         
			mapTracking.zoomIn();
			requestFocus();
		}                                        
        /**
         * Listener Event of loading map
         * 
         * @param evt
         * @throws MapXMLImporter.Error
         */
		private void loadXMLActionPerformed(java.awt.event.ActionEvent evt) throws MapXMLImporter.Error {  
			try {
				mapTracking.importXML();
			} catch (Error e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/**
		 * Listener Event of saving map into XML
		 * @param evt
		 */
		private void SaveActionPerformed(java.awt.event.ActionEvent evt) {
			mapTracking.exportXML();
		}
		
		
		//Feedback to user when the mouse is on the button
		private void clickable(java.awt.event.MouseEvent evt,JButton button) {                           
			button.setBackground(new java.awt.Color(255, 255, 255));
		}                          
		private void mouseLeave(java.awt.event.MouseEvent evt,JButton button) {      
			button.setBackground(new java.awt.Color(36, 47, 65));
		}                                     
                       
}
