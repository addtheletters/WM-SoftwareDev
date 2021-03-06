package falstad;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import generation.CardinalDirection;

/**
 * Provides tests for {@link BasicRobot} methods.
 * 
 * @author Ben Zhang
 *
 */
public class BasicRobotTest extends BasicRobot{

	/**
	 * Acceptable floating-point number error margin for equality testing
	 */
	private static final float ENERGY_FP_ERROR = 0.01f;
	
	/**
	 * Warps the robot to the specified position for testing purposes.
	 * @param x
	 * @param y
	 */
	protected void teleportTo(int x, int y){
		getMaze().setCurrentPosition(x, y);
	}
	
	@Before
	public void setUp(){
		setMaze( new MazeController("test/data/input.xml") );
		reset();
		getMaze().init();
		//getMaze().getMazeConfiguration().getMazedists().prettyPrint();
	}
	
	/**
	 * Tests that getCurrentPosition and getCurrentPoint of BasicRobot work correctly.
	 * 
	 * Used independently, tests correctness in the initial position.
	 * Can also be used to verify correct position values after motion.
	 */
	@Test
	public final void testGetPosition(){
		int[] correctPos = getMaze().getCurrentPosition();
		Point correctPoint = new Point(correctPos[0], correctPos[1]);
		int[] current = null;
		try{
			current = getCurrentPosition();
		}
		catch(Exception e){
			assertTrue("Exception occurred on getting position " + correctPoint.x + "," + correctPoint.y, false);
		}
		assertArrayEquals("Position (arrays) should be the same as maze controller", correctPos, current);
		assertEquals("Position (points) should be the same as maze controller", correctPoint, getCurrentPoint());
	}
	
	/**
	 * Tests that getCurrentDirection of BasicRobot works correctly.
	 * Used independently, tests correctness in the initial position.
	 * Can also be used to verify correct direction values after motion.
	 */
	@Test
	public final void testGetDirection(){
		assertEquals("Direction should reflect direction faced in MazeController", getMaze().getCurrentDirection(), getCurrentDirection() );
	}
	
	/**
	 * Tests that move correctly navigates the maze.
	 * 
	 * On the input.xml maze, the robot starts in a long hallway with space to move forwards.
	 */
	@Test
	public final void testMove (){
		int[] startPos = getMaze().getMazeConfiguration().getStartingPosition();
		Point startPoint = new Point( startPos[0], startPos[1] );
		assertEquals("Starts in position", startPoint,  getCurrentPoint());
		assertEquals("Starts facing to the east", CardinalDirection.East, getCurrentDirection());
		
		// Test that moving 0 does nothing
		move(0, false);
		assertEquals("Still in start", startPoint, getCurrentPoint());
		
		// Test moving 1 forwards. Should successfully move
		move(1, false);
		Point followPos = new Point(startPoint);
		int[] followDir = null;
		followDir = CardinalDirection.East.getDirection();
		followPos.translate( followDir[0], followDir[1] );
		assertEquals("Now shifted position", followPos, getCurrentPoint());
		assertEquals("Facing same way", CardinalDirection.East, getCurrentDirection());
		
		// Test moving far forwards, should hit wall after moving 10 and be in followPos
		int attemptDistance = 100;
		move(attemptDistance, false);
		int actualDistance = 10;
		followPos.translate(actualDistance * followDir[0], actualDistance * followDir[1]);
		assertEquals("Should have reached", followPos, getCurrentPoint());
		
		// Verify that get for position and direction have not failed after movement
		testGetPosition();
		testGetDirection();
	}
	
	/**
	 * Tests that rotate correctly rotates.
	 * 
	 * Note: In MazeController, South and North directions are opposite to reality, resulting in unrealistic
	 * relationships between the cardinal directions (turning left from west producing north rather than south).
	 */
	@Test
	public final void testRotate(){
		assertEquals("Starting facing east", CardinalDirection.East, getCurrentDirection());
		rotate(Turn.AROUND);
		assertEquals("Turnaround results in facing west", CardinalDirection.West, getCurrentDirection());
		rotate(Turn.LEFT); // in the GUI, this is a right turn
		assertEquals("Left from west is south (up in GUI)", CardinalDirection.South, getCurrentDirection());
		rotate(Turn.AROUND);
		assertEquals("Turnaround results in facing north (down in GUI)", CardinalDirection.North, getCurrentDirection());
		rotate(Turn.RIGHT); // in the GUI, this is a left turn
		assertEquals("Right from north (down in GUI) is east", CardinalDirection.East, getCurrentDirection());
	}

	/**
	 * Tests that distanceToObstacle correctly gives distances.
	 */
	@Test
	public final void testDistanceToObstacle(){
		for(Direction dir : Direction.values()){
			assertTrue(hasDistanceSensor(dir));
		}
		
		int startDistanceFromFront = 11;
		assertEquals("Back and side walls start directly adjacent", 0, distanceToObstacle(Direction.BACKWARD));
		assertEquals("Back and side walls start directly adjacent", 0, distanceToObstacle(Direction.LEFT));
		assertEquals("Back and side walls start directly adjacent", 0, distanceToObstacle(Direction.RIGHT));
		assertEquals("Some distance away from wall in front", startDistanceFromFront, distanceToObstacle(Direction.FORWARD));
		
		int firstMove = 2;
		move(firstMove, false);	
		assertEquals("Back wall some distance away", firstMove, distanceToObstacle(Direction.BACKWARD));
		assertEquals("Side walls directly adjacent", 0, distanceToObstacle(Direction.LEFT));
		assertEquals("Side walls directly adjacent", 0, distanceToObstacle(Direction.RIGHT));
		assertEquals("Front wall some distance away", startDistanceFromFront - firstMove,distanceToObstacle(Direction.FORWARD));
		
		// move the rest of the way to the end; short hallway should be open to the left
		move(startDistanceFromFront - firstMove, false);
		int sideHallSize = 2;
		assertEquals("Back wall some distance away", startDistanceFromFront, distanceToObstacle(Direction.BACKWARD));
		assertEquals("Side wall some distance away", sideHallSize, distanceToObstacle(Direction.RIGHT)); // appears on left in GUI
		assertEquals("Opposite side wall directly adjacent", 0, distanceToObstacle(Direction.LEFT));
		assertEquals("Front wall directly adjacent", 0, distanceToObstacle(Direction.FORWARD));
	}
	
	/**
	 * Test that motions fail properly when no energy is available and that the robot stops
	 */
	@Test
	public final void testNoBatteryOperation(){
		assertFalse("Should not be stopped at start", hasStopped());
		
		setBatteryLevel(0);
		// distance returns a negative value to signify failure (perhaps should throw exception)
		assertEquals("Should fail to scan without battery", -1, distanceToObstacle(Direction.FORWARD));
		assertTrue("Should have stopped", hasStopped());
		
		reset();
		setBatteryLevel(0);
		Point before = getCurrentPoint();
		// should not be able to move
		move(5, false);
		assertEquals("Should not have moved", before, getCurrentPoint());
		assertTrue("Should have stopped", hasStopped());

		reset();
		setBatteryLevel(0);
		CardinalDirection beforeDir = getCurrentDirection();
		// should not be able to turn
		rotate(Turn.LEFT);
		assertEquals("Should not have turned", beforeDir, getCurrentDirection());
		rotate(Turn.AROUND);
		assertEquals("Should not have turned", beforeDir, getCurrentDirection());
		assertTrue("Should have stopped", hasStopped());
	}
	
	/**
	 * Test that robot correctly can tell when it is at an exit/goal
	 */
	@Test
	public final void testAtGoal(){
		assertFalse("Should not start at exit", isAtGoal());
		int[] exit = getMaze().getMazeConfiguration().getMazedists().getExitPosition();
		teleportTo(exit[0], exit[1]);
		assertTrue("Should be at exit", isAtGoal());
	}
	
	/**
	 * Test that the robot can tell when it is in a room
	 */
	@Test
	public final void testInRoom(){
		assertTrue("Has room sensor", hasRoomSensor());
		
		assertFalse("Should not start in room", isInsideRoom());
		int[] posInRoom = {9,9}; // position (9,9) is inside a room
		teleportTo(posInRoom[0], posInRoom[1]);
		assertTrue("Should be in room", isInsideRoom());
	}
	
	/**
	 * Test that robot correctly validates current position
	 */
	@Test
	public final void testValidPosition(){
		int[] testPos;
		boolean caughtBadPosition = false;
		try{
			testPos = getCurrentPosition();
			assertArrayEquals("Start position is valid", testPos, getMaze().getCurrentPosition());			
			
		}
		catch(Exception e){
			assertTrue("Should not fail to get valid start position", false); // should not result in exception
		}
		teleportTo(-1, -1);
		try{
			testPos = getCurrentPosition();
			assertTrue("Should fail to get invalid position", false); // should have reached exception before here
		}
		catch(Exception e){
			caughtBadPosition = true;
		}
		assertTrue("Caught bad position", caughtBadPosition);
	}
	
	/**
	 * Test that robot can correctly perceive the exit
	 */
	@Test
	public final void testSeeExit(){
		for(Direction dir : Direction.values()){
			assertFalse("Should not be able to see exit anywhere at start",canSeeGoal(dir));
		}
		teleportTo(0, 3);
		assertTrue("Should be on goal cell", isAtGoal());
		CardinalDirection exitWay = CardinalDirection.West;
		boolean sawExit = false;
		for(Direction dir: Direction.values()){
			if(dir.worldDirectionOf(this) == exitWay){
				assertTrue("Should see exit in the direction of the exit", canSeeGoal(dir));
				sawExit = true;
			}
			else{
				assertFalse("Should not be able to see exit in non-exit directions", canSeeGoal(dir));
			}
		}
		assertTrue("Should have seen exit in one of the directions", sawExit);
	}
	
	/**
	 * Test that robot consumes energy in correct amounts as well as reset for battery level and 'stop' condition
	 */
	@Test
	public final void testEnergyUse(){
		float supposedRemainingBattery = getBatteryLevel(); // check initial battery level		
		assertEquals("Battery should start at initial battery level", INITIAL_BATTERY, supposedRemainingBattery, ENERGY_FP_ERROR);
		
		// test behavior when not out of battery but too low for certain actions
		setBatteryLevel(getEnergyForStepForward() + 0.1f); // enough energy for one step but not two
		// check that set was successful
		assertEquals("Battery level should be", getEnergyForStepForward() + 0.1f, getBatteryLevel(), ENERGY_FP_ERROR);
		
		move(2, false); // should cause stop after moving a single space
		int[] startPos = getMaze().getMazeConfiguration().getMazedists().getStartPosition();
		Point startMoveOne = new Point(startPos[0] + 1, startPos[1]);
		try{
			assertEquals("Should have moved just one from start", startMoveOne, getCurrentPoint());
		}
		catch(Exception e){
			assertTrue("getCurrentPosition should succeed on valid start position",false);
		}
		assertTrue("Attempted move without enough battery causes stop", hasStopped());
		
		// test that reset correctly fills battery
		reset();
		assertEquals("Reset should make battery full again", INITIAL_BATTERY, getBatteryLevel(), ENERGY_FP_ERROR);
		assertFalse("Should no longer be stopped after reset", hasStopped());
		
		// test running out of battery during turn
		setBatteryLevel(getEnergyForFullRotation() / 4 + 0.1f); // enough for one turn but not an around turn
		CardinalDirection facingBeforeTryTurn = getCurrentDirection();
		rotate(Turn.AROUND); // should cause stop
		assertEquals("Should not have turned", facingBeforeTryTurn, getCurrentDirection());
		assertTrue("Attempted turn without enough battery causes stop", hasStopped());
		
		// test distance sensing, which should work despite stoppage and not enough battery for other actions
		while(DISTANCE_SENSE_COST < getBatteryLevel()){
			assertNotEquals("Distance measure should be valid", -1, distanceToObstacle(Direction.FORWARD));			
		}
		assertEquals("Distance measure should fail after completely out", -1, distanceToObstacle(Direction.FORWARD));
		
		reset();
		
		// test that checking for distance to obstacles uses battery
		for(Direction dir : Direction.values()){
			distanceToObstacle(dir);
			supposedRemainingBattery -= DISTANCE_SENSE_COST;
			assertEquals("Battery should be slightly drained by checking distances to obstacles", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		}
		
		// test that moving drains battery 
		// move 0 does not
		move(0, false);
		assertEquals("Battery should not change when move has no effect", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		move(1, false);
		supposedRemainingBattery -= getEnergyForStepForward();
		assertEquals("Battery should decrease when moving 1", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		
		int tilesLeftAhead = distanceToObstacle(Direction.FORWARD);
		supposedRemainingBattery -= DISTANCE_SENSE_COST;
		assertEquals("Battery should be slightly drained by checking distances to obstacles", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		move(tilesLeftAhead + 1, false); // try moving more than actually can
		supposedRemainingBattery -= getEnergyForStepForward() * tilesLeftAhead;
		assertEquals("Battery should be drained only for moves that were possible", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		
		// test that turning drains battery
		rotate(Turn.LEFT);
		supposedRemainingBattery -= getEnergyForFullRotation() / 4;
		assertEquals("Left turn should drain some", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		rotate(Turn.AROUND);
		supposedRemainingBattery -= getEnergyForFullRotation() / 2;
		assertEquals("About turn should drain more", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		rotate(Turn.RIGHT);
		supposedRemainingBattery -= getEnergyForFullRotation() / 4;
		assertEquals("Right turn should drain some", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
		rotate(Turn.RIGHT); // face the hallway pointing south
		supposedRemainingBattery -= getEnergyForFullRotation() / 4;
		assertEquals("Right turn should drain some", supposedRemainingBattery, getBatteryLevel(), ENERGY_FP_ERROR);
	}
	
}
