import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ConnectException;

public interface RobotInterface {
    /**
     * This step function iterates one step over the behaviour of the robot.
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    void step() throws RemoteException, MalformedURLException, NotBoundException;

    /**
     * Connect the robot over the network.
     * @throws NotBoundException
     * @throws MalformedURLException
     */
    void connect(String IP, GUIConnectionSelect connectionWindow)
            throws RemoteException, MalformedURLException, NotBoundException, ConnectException;

    /**
     * A cleanup function. Closes all the motor and sensor ports.
     * @throws RemoteException
     */
    void cleanUp() throws RemoteException;
}