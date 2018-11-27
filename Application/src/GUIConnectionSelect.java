import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 * The class implements a GUI to allow user select the connection mode 
 * If User select blueTooth or cable, the system will use the default ip address 10.0.1.1
 * If User select wifi, user need to input the ip address of the robot
 * @author Ziang Chen
 */
public class GUIConnectionSelect extends javax.swing.JFrame {

    /**
     * Creates new form GUIConnectionSelect
     */
    public GUIConnectionSelect() {
        initComponents();
        this.setLocationRelativeTo(null);
    }
                     
    private void initComponents() {

        theWholeLayout = new javax.swing.JPanel();
        connectionIntro = new javax.swing.JLabel("Please Select Connection Mode",SwingConstants.CENTER);
        buttonLayout = new javax.swing.JPanel();
        cable = new javax.swing.JButton();
        wifi = new javax.swing.JButton();
        blueTooth = new javax.swing.JButton();
        hasSelected=false;
        setPreferredSize(new java.awt.Dimension(530, 400));
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        theWholeLayout.setBackground(new java.awt.Color(36, 47, 65));
        theWholeLayout.setForeground(new java.awt.Color(36, 47, 65));
        connectionIntro.setForeground(new java.awt.Color(255, 255, 255));
        buttonLayout.setBackground(new java.awt.Color(36, 47, 65));
        
        setTitle("Select Connection Mode");
        setResizable(false);
        cable.setText("    USB");
        cable.setSize(new java.awt.Dimension(100, 30));
        cable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	cableActionPerformed(evt);
            }
        });
        
        wifi.setText("     Wifi");
        wifi.setSize(new java.awt.Dimension(100, 30));
        wifi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	wifiActionPerformed(evt);
            }
        });
        
        blueTooth.setText(" Bluetooth");
        blueTooth.setSize(new java.awt.Dimension(100, 30));
        blueTooth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	 blueToothActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout wholeLayout = new javax.swing.GroupLayout(buttonLayout);
        buttonLayout.setLayout(wholeLayout);
        wholeLayout.setHorizontalGroup(
            wholeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wholeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(wholeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(wifi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(blueTooth, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addContainerGap())
        );
        wholeLayout.setVerticalGroup(
            wholeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wholeLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(blueTooth, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cable, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wifi, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout setPosLayout = new javax.swing.GroupLayout(theWholeLayout);
        theWholeLayout.setLayout(setPosLayout);
        setPosLayout.setHorizontalGroup(
            setPosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setPosLayout.createSequentialGroup()
                .addGap(170, 170, 170)
                .addComponent(buttonLayout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, setPosLayout.createSequentialGroup()
                .addContainerGap(126, Short.MAX_VALUE)
                .addComponent(connectionIntro, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135))
        );
        setPosLayout.setVerticalGroup(
            setPosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(setPosLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(connectionIntro, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(buttonLayout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(theWholeLayout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(theWholeLayout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buttonIcons();
        pack();
    }           
    
    /**
     * By using USB cable
     * EV3 has the server address with the ip address 10.0.1.1.
     * The computer is assigned an ip address in the 10.0.1.x range
     * @param evt
     */
    public void cableActionPerformed(java.awt.event.ActionEvent evt) {  
    	ip = "10.0.1.1";
    	hasSelected = true;
    }     
    
    /**
     * By using wifi
     * EV3 will be assigned an ip address and it is displayed on the screen
     * User need to enter the ip address shown on the screen of brick
     * @param evt
     */
    public void wifiActionPerformed(java.awt.event.ActionEvent evt) {  
    	ip = JOptionPane.showInputDialog(null, "Please Input ip Address", "192.168.1.2");
    	if(ip != null) { hasSelected = true;}
    }     
    
    /**
     * By using blueTooth
     * EV3 has the server address with the ip address 10.0.1.1.
     * The computer is assigned an ip address in the 10.0.1.x range
     * @param evt
     */
    public void blueToothActionPerformed(java.awt.event.ActionEvent evt) {  
    	ip = "10.0.1.1";
    	hasSelected = true;
    }   
    
    public boolean connectionSelected(){
    	return  hasSelected;
    }
    
    public void connectionReset() {
    	hasSelected = false;
    }

    public void setStatus(String CurrentStatus){
    	 connectionIntro.setText("<html><div style='text-align: center;'>"+CurrentStatus+"</div></html>");
    	 if (Settings.Debug.showRobotSetup) System.out.println(CurrentStatus);
    }
    
    public void setStatusError(String CurrentStatus){
    		connectionIntro.setText("<html><div style='text-align: center;'>"+CurrentStatus+"</div></html>");
    		if (Settings.Debug.showRobotSetup) System.err.println(CurrentStatus);
   }
    
    public void connectionFailWindow(){
    	JOptionPane optionPane = new JOptionPane("ErrorMsg", JOptionPane.ERROR_MESSAGE);    
    	JDialog dialog = optionPane.createDialog("Failure");
    	dialog.setAlwaysOnTop(true);
    	dialog.setVisible(true);
    }
    public String getIPAddress(){
    	return ip;
    }
    private void buttonIcons(){
    	blueTooth.setIcon(new ImageIcon("img/blueTooth.png"));
    	cable.setIcon(new ImageIcon("img/usb.png"));
    	wifi.setIcon(new ImageIcon("img/wifi.png"));
    }
                
    private javax.swing.JButton blueTooth;
    private javax.swing.JButton cable;
    private javax.swing.JButton wifi;
    private javax.swing.JLabel connectionIntro;
    private javax.swing.JPanel theWholeLayout;
    private javax.swing.JPanel buttonLayout;
    public 	String ip;
    private volatile boolean hasSelected;               
}
