import static org.junit.Assert.*;

import org.junit.Test;

public class RobotTest {

    @Test
    public void test() {
        
    }

    public void readGyroSensorTest(){
        // This function can be tested by measuring the angle as the robot turns and comparing
        // it (with a protractor or equivalent). Rotate the robot smoothly. This can be done by 
        // hand or through the software.
        // The return values can be displayed by using the showRobotMessages debug setting.
    }

    public void readDistanceSensorTest(){
        // This function can be tested by measuring the distance and comparing it (with a ruler) 
        // with the distance the ultrasonic sensor is giving. ultrasonicSensor() returns the 
        // distance detected plus the distance from the centre of the robot to the head of the 
        // sensor as set by the headLength variable.
        // The return values can be displayed by using the showRobotMessages debug setting.
    }

    public void readColorSensorTest(){
        // This function can be tested against various colors on a sheet of paper. The returned 
        // value is extremely sensitive to changing lighting conditions, or the area of the colour 
        // beneath the sensor. For best results, use solid colours in blocks twice the size of the 
        // sensor head. If available, use independent measurements of colours, and configure the
        // reference colours in Settings, and view output of the ColorDetector.
        // The return values can be displayed by using the showRobotMessages debug setting.
    }

    public void setMotorSpeedsTest(){
        // This function can be tested by changing the values of driveSpeed and colorMoverSpeed, 
        // and and observing the change in speed when using motors under operation.
        // The values should be tuned to provide smooth motion, without too long an acceleration.
    }

    public void isMovingTest(){
        // This function can be tested by turning on the showUpdatePositionOutput debug setting, 
        // and observing the output while robot is under operation. Should be true whenever the 
        // main motors are in motion.
    }

    public void isDrivingTest(){
        // This function can be tested by turning on the showUpdatePositionOutput debug setting, 
        // and observing the output while robot is under operation. Should be true whenever the 
        // main motors are turning the same direction (forwards or backwards).
    }

    public void isTurningTest(){
        // This function can be tested by turning on the showUpdatePositionOutput debug setting, 
        // and observing the output while robot is under operation. Should be true whenever the 
        // main motors are turning in the opposite direction (causing robot to turn left/right).
    }

    public void isArmMovingTest(){
        // This function can be tested by turning on the showUpdatePositionOutput debug setting, 
        // and observing the output while robot is under operation. Should be true whenever the 
        // main motors are turning in the opposite direction (causing robot to turn left/right).
    }

    public void motorDistanceTest(){
        // This function can be tested by turning on the showUpdatePositionOutput debug setting, 
        // and observing the output while robot is under operation. After a forwards/backwards
        // motion, compare outputed value to the physical measurement.
        // Calibrate by setting moveErr based on the difference between the results to fix error
        // caused by motor inaccuracies.
    }

    public void updatePositionTest(){
        // This function can be tested by turning on the showUpdatePositionOutput debug setting, 
        // and observing the output while robot is under operation. Compare distance, angle, and 
        // position measurements of the physical robot with the reported values.
    }

    public void driveTest(float distance){
        // This function can be tested by sending move back and forward comands in manual mode and
        // auto mode. Robot should move the approriate direction, and in the case of auto mode, 
        // the appropriate distance (the distance travelled may be greater than specified due to 
        // non-instantaneous acceleration, but should be properly reported by the robot).
        // Can also test changing the acceleration values of the motors and see that any change is
        // reflected by the robot's motion.
    }

    public void turnTest(float degrees){
        // This function can be tested by sending turn left and right comands in manual mode and
        // auto mode. Robot should move the approriate direction, and in the case of auto mode, 
        // the appropriate angle (the angle travelled may be greater than specified due to 
        // non-instantaneous acceleration, but should be properly reported by the robot).
        // Can also test changing the acceleration values of the motors and see that any change is
        // reflected by the robot's motion.
    }

    public void stopDrivingTest(){
        // This function can be tested by observing that the robot stops when the function is 
        // called. The tachometers should also be reset (to 0) in this function.
    }

     void colorMoverForeverTest(float direction){
        // This function can be tested by:
        // 1. Verifying that the color mover arm turns until told to stop.
        // 2. Verify that the arm rotates in the desired direction
    }

    public void colorMoverStopTest(){
        // This function can be tested by verifying that the color mover arm stops moving once 
        // this function is called.
    }

    public void interupt(){
        // This function can be tested by causing the robot to trigger a reflex action, and 
        // verifying that an interupt message is sent in response.
    }

    public void objectAvoidTest() {
        // This function can be tested by causing the robot to trigger a reflex action that makes
        // the robot react.
    }

    public void reflexTest(){
        // This function can be tested by:
        // 1. Verifing that this function is called regularly
        // 2. Trying to drive the robot into obstacles and the distance if statement is triggered.
        // 3. Trying to drive the robot over a black line and the color if statement is triggered.
    }

    public void sendMessageTest(boolean isPriority){
        // This function can be tested by enabling the showRobotMessages debug setting.
        // This funciton has been overloaded to make priority an optional argument. 
        // The other function takes no arguments and calls this function with parameter set false.
    }

    public void stepTest(){
        // This function can be tested by:
        // 1. Verifing that messages can be read and the corresponding functions are called. 
        //      > This is done by turning on the showRobotCommands debug setting.
        // 2. Verifying the updatePosition function is called.
        //      > This is done by turning on the showUpdatePositionOutput debug setting.
        // 3. Verifying that motion commands received while in auto mode are performed correctly.
        // 4. Verifying that messages are sent regularly.
        //      > This is done by turning on the showRobotMessages debug setting and checking for 
        //        regular messages with "isPriority: false".   
    }

    public void connectTest(String IP, GUIConnectionSelect connectionWindow){
        // This function can be tested by running the program and attempting to connect to the
        // robot. The connection manager window will report whether each part of setup is 
        // successful. If the main window with the map and controls appears, and the robot accepts
        // commmands, then connection was succesful.
        // Note: The ultrasonic sensor occasionally fails to initialise, and the program will 
        // close. This is an issue with the hardware and/or the Lejos firmware running on it.
    }

    public void initTest( String IP ){
        // This function can be tested by checking that all positions start off initialised 
        // correctly, and the job queue is empty.
    }

    public void cleanUpTest(){
        // This function can be tested by verifying that the Handler is able to connect to the 
        // robot for a 2nd consecutive time.
    }
}
