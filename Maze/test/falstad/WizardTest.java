package falstad;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import generation.Order;

/**
 * Provides tests for {@link Wizard} technique for solving maze.
 * 
 * @author Ben Zhang
 *
 */
public class WizardTest extends Wizard {
	
	/**
	 * Test that Wizard reaches the exit correctly on a small perfect maze.
	 */
	@Test
	public final void testReachesExitSmallPerfect(){
		BasicRobotDriverTest.testDriverReachesExit(this, new TestMazeController(Order.Builder.Kruskal, 0, true, true));
	}
	
	/**
	 * Test that Wizard reaches the exit correctly on the input.xml maze.
	 * On input.xml maze, can take 30 seconds or more to run.
	 */
	@Test
	public final void testReachesExitInput(){
		BasicRobotDriverTest.testDriverReachesExit(this, new TestMazeController("test/data/input.xml"));
	}
}
