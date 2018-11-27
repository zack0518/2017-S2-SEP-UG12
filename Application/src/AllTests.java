import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    //ApplicationTest.class,
	DecisionMakerTest.class,
    DistanceSensorInterpreterTest.class, 
    HandlerTest.class, HandlerToRobotQueueTest.class,
    MapTest.class, 
    //MapXMLExporterTest.class, 
    MapXMLImporterTest.class, NoGoZonesMarkerTest.class,
    PathFindingTest.class,
    PointToCommandParameterConverterTest.class,
    RobotTest.class, RobotJobQueueTest.class,
    RobotToHandlerQueueTest.class, ShapeDetectionTest.class})
public class AllTests {

}
