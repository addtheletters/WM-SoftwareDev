package falstad;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import falstad.Robot.Direction;
import falstad.Robot.Turn;
import generation.Order;

/**
 * Provides tests for {@link WallFollower} technique for solving maze.
 * 
 * @author Ben Zhang
 *
 */
public class WallFollowerTest extends WallFollower {
	
	@Before
	public void setUp(){
		BasicRobotDriverTest.setUpBasicRobotDriver(this, new MazeController("test/data/input.xml"));
		this.setSide(true);
	}
	
	/**
	 * Test that {@link WallFollower#primarySideDir()} gets the correct direction corresponding to the side being followed
	 */
	@Test
	public final void testPrimarySideDir(){
		Direction defaultDir = Direction.LEFT;
		assertEquals("Left is selected by default", defaultDir, primarySideDir());
		this.setSide(true);
		assertEquals("Left is selected", Direction.LEFT, primarySideDir());
		this.setSide(false);
		assertEquals("Right is selected", Direction.RIGHT, primarySideDir());
	}
	
	/**
	 * Test that checking for presence of adjacent / forward walls is accurate
	 */
	@Test
	public final void testHasWalls(){
		assertFalse("No wall in front to start", hasFrontWall());
		assertEquals("Left is selected by default", Direction.LEFT, primarySideDir());
		assertTrue("Has left (right in GUI) wall at start", hasSideWall());
		registeredTurn(Turn.RIGHT); // turn towards up (south)
		assertTrue("Has wall in front after turn", hasFrontWall());
		assertFalse("Has no wall on side after turn", hasSideWall());
	}
	
	/**
	 * Test that WallFollower reaches the exit correctly on a small perfect maze.
	 */
	@Test
	public final void testReachesExitSmallPerfect(){
		BasicRobotDriverTest.testDriverReachesExit(this, new TestMazeController(Order.Builder.Kruskal, 0, true, true));
	}
	
	/**
	 * Test that WallFollower reaches the exit correctly on the input.xml maze.
	 * On input.xml maze, can take 30 seconds or more to run.
	 */
	@Test
	public final void testReachesExitInput(){
		BasicRobotDriverTest.testDriverReachesExit(this, new TestMazeController("test/data/input.xml"));
	}
}
