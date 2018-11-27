import static org.junit.Assert.*;

import org.junit.Test;

public class PointToCommandParameterConverterTest {

	@Test
	public void testPositiveTurningAngle() {
		PointToCommandParameterConverter ptcpc = new PointToCommandParameterConverter();
		Point point = new Point(5.0f, 1.0f);
		Point robotPosition = new Point(2.0f, 4.0f);
		float robotOrientation = -60.0f;
		PointToCommandParameterConverter.AngleDistance result = ptcpc.convert(
				point, 
				robotPosition, 
				robotOrientation);
		assertEquals(-75.0f, result.angle, 11);
		assertEquals(4.24f, result.distance,0.1);
		
		point = new Point(-3.0f, 2.0f);
		robotPosition = new Point(-1.0f, 0.5f);
		robotOrientation = 150.0f;
		result = ptcpc.convert(
				point, 
				robotPosition, 
				robotOrientation);
		assertEquals(-96.89f, result.angle, 11);
		assertEquals(2.5f, result.distance, 0.1);
	}

	@Test
	public void testNegativeTurningAngle() {
		PointToCommandParameterConverter ptcpc = new PointToCommandParameterConverter();
		Point point = new Point(-1.0f, -2.0f);
		Point robotPosition = new Point(4.0f, 3.0f);
		float robotOrientation = -20.0f;
		PointToCommandParameterConverter.AngleDistance result = ptcpc.convert(
				point, 
				robotPosition, 
				robotOrientation);
		assertEquals(155.0f, result.angle, 11);
		assertEquals(7.071f, result.distance, 0.1);
		
		point = new Point(-2.0f, 2.5f);
		robotPosition = new Point(3.0f, -3.0f);
		robotOrientation = -110.0f;
		result = ptcpc.convert(
				point, 
				robotPosition, 
				robotOrientation);
		assertEquals(152.27f,result.angle, 11);
		assertEquals(7.43f, result.distance, 0.1);
		
		point = new Point(-3.0f, 2.0f);
		robotPosition = new Point(-1.0f, 0.5f);
		robotOrientation = -70.0f;
		result = ptcpc.convert(
				point, 
				robotPosition, 
				robotOrientation);
		assertEquals(123.13f, result.angle, 11);
		assertEquals(2.5f, result.distance, 0.1);
	}
}
