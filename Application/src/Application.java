import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.util.HashMap;

/**
 * This program connect all modules and queues and start up the robot.
 * @author cyrusvillacampa
 *
 */
public class Application {
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
        /**
         * Create queues
         */
        UIEventQueue uiEventQueue = new UIEventQueue();
        HandlerToRobotQueue handlerToRobotQueue = new HandlerToRobotQueue();
        RobotToHandlerQueue robotToHandlerQueue = new RobotToHandlerQueue();
        
        /**
         * Create the Handler and Robot modules and connect them to the queues
         */
        System.out.println("Connecting Handler and Robot to the queues...");
        // The highlighter colors determined by experimentation on our map.
        /*
        RGBColor white = new RGBColor(0.120f, 0.140f, 0.110f);
        RGBColor black = new RGBColor(0.014f, 0.018f, 0.010f);
        RGBColor red   = new RGBColor(0.138f, 0.022f, 0.019f);
        RGBColor green = new RGBColor(0.018f, 0.058f, 0.019f);
        RGBColor blue  = new RGBColor(0.025f, 0.032f, 0.057f);
        // This map contains the expected colors for each feature on the map.
        HashMap<Map.Property, RGBColor> colorSensorColors = new HashMap<Map.Property, RGBColor>();
        colorSensorColors.put(Map.Property.NONE,            white);
        colorSensorColors.put(Map.Property.CRATER,          black);
        colorSensorColors.put(Map.Property.RADIATION,       green);
        colorSensorColors.put(Map.Property.FOOTSTEP,        red);
        colorSensorColors.put(Map.Property.VEHICLE_TRACK,   blue);
        */
        Handler handler = new Handler(uiEventQueue,
                                      handlerToRobotQueue,
                                      robotToHandlerQueue);

        /**
         * Added by Ziang Chen
         * A busy-wait loop to wait the user select the connection method
         * This is optimized by Thread.sleep(500) to avoid increase CPU load
         */
        GUIConnectionSelect connectionSelect = new GUIConnectionSelect();
        connectionSelect.setVisible(true);
        String IP = null;
        
        RobotInterface robot = new Robot(handlerToRobotQueue, robotToHandlerQueue, handler.getColorDetector());

        try {
            while(IP == null) {
                Thread.sleep(500);
                if(connectionSelect.connectionSelected()){
                	try {
                		IP = connectionSelect.getIPAddress();
                        connectionSelect.setStatus("Connecting...");
                        robot.connect(IP, connectionSelect);
                	} catch (ConnectException e) {
                    	System.err.println("Connection Timed Out.");
                    	connectionSelect.setStatus("Failed To Make Connection (Timed Out) Please Set Connection Mode");
                    	IP = null;
                    	connectionSelect.connectionReset();
                    }
                }
            }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } 
        

        /**
         * Create the GUI and connect it with the uiEventQueue
         */
        connectionSelect.setStatus("Connecting GUI and event queue...");
        connectionSelect.setVisible(false);
        GUIMapRendering GuiMapRendering = new GUIMapRendering(handler);
        new GUIConstructor(uiEventQueue, GuiMapRendering);
        connectionSelect.setStatus("Connecting GUI and event queue... Done!");
        connectionSelect.setVisible(false);
        
        /**
         * Start the handler and the robot
         */
        try {
            while (!handler.getExitFlag()) {
                handler.step();
                robot.step();
                if (Settings.Debug.showMap) {
                    handler.map.print(handler.getRobotPosition(), handler.getColorSensorPosition());
                }
            }
        } catch (Throwable e) {
            System.out.printf("caught: '%s'%nmessage: '%s'%n", e.toString(), e.getMessage());
            e.printStackTrace(System.out);
        }
        
        System.out.print("Cleaning up resources...");
        robot.cleanUp();
        System.out.print(" Done!\n");
        System.out.println("Closing Application... Done!");
        System.exit(0);
    }
    
}
