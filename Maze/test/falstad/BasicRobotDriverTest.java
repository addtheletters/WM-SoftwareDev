package falstad;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import falstad.Constants.StateGUI;
import falstad.Robot.Turn;
import generation.CardinalDirection;
import generation.Order;

/**
 * Provides tests for BasicRobotDriver's non-abstract functions.
 * 
 * @author Ben Zhang
 *
 */
public class BasicRobotDriverTest extends BasicRobotDriver {
	
	/**
	 * Acceptable floating-point number error margin for equality testing
	 */
	private static final float ENERGY_FP_ERROR = 0.01f;
	
	/**
	 * Sets up essentials for testing a BasicRobotDriver. 
	 * Assigns a controller using the input.xml maze, creates a robot, attaches all together.
	 * @param brd the driver which should be set up
	 */
	public static void setUpBasicRobotDriver(BasicRobotDriver brd, MazeController controller){

		if(controller.needsGenerating()){
			System.out.println("Started generating.");
			controller.startFactory();
			controller.waitForFactoryCompletion();
		}
		
		controller.init();
		controller.getMazeConfiguration().getMazedists().prettyPrint();
		
		controller.setState(StateGUI.STATE_PLAY);
		
		Robot rob = new BasicRobotTest();
		//System.out.println("Setting maze to " + controller);
		rob.setMaze( controller );
		brd.reset();
		brd.setRobot(rob);
		brd.setupUsingMaze(controller);
	}

	@Override
	public boolean drive2Exit() throws Exception {
		System.out.println("Warning: BasicRobotDriverTest drive to exit is designed to use energy and fail miserably");
		return super.drive2Exit();
		//throw new UnsupportedOperationException("Test for abstract class does not have driving implemented.");
	}

	@Override
	protected boolean driveStep() throws Exception {
		// spin in place. Designed to fail.
		registeredTurn(Turn.LEFT);
		return true;
		//throw new UnsupportedOperationException("Test for abstract class does not have driving implemented.");
	}
	
	@Before
	public void setUp(){
		setUpBasicRobotDriver(this, new MazeController("test/data/input.xml"));
	}
	
	/**
	 * Reusable test for actual implementations to check that driver reaches exit given certain maze parameters.
	 */
	public static void testDriverReachesExit(BasicRobotDriver driver, TestMazeController controller){
		setUpBasicRobotDriver(driver, controller);
		
		boolean reachesExit = false;
		try{
			reachesExit = driver.drive2Exit();
		}
		catch(Exception e){
			e.printStackTrace();
			assertTrue("Should not have exception trying to reach exit", false);
		}
		assertTrue("Should reach exit", reachesExit);
	}
	
	/**
	 * Tests that BasicRobotDriver correctly registers moves taken, turns made, and saving history of robot's state.
	 * Also checks that energy usage is recorded properly and its values are sane.
	 */
	@Test
	public final void testRegisterAndReset(){
		assertEquals("No steps have been taken", 0, getPathLength());
		assertEquals("No state history has been saved", 0, this.positionHistory.size());
		assertEquals("No energy used", 0, getEnergyConsumption(), ENERGY_FP_ERROR);
		
		// test moving straight
		float startEnergy = robot.getBatteryLevel();
		registerRobotReached();
		assertEquals("State history size has increased", 1, this.positionHistory.size() );
		
		int firstMoveSize = 1;
		registeredMove(firstMoveSize);
		assertFalse("Reached position not in history", robotInPreviousState());
		assertEquals("Steps have been taken", firstMoveSize, getPathLength());
		
		registerRobotReached();
		assertEquals("State history size has increased", 2, this.positionHistory.size() );
		assertTrue("Can find position in history", robotInPreviousState());
		
		int secondMoveSize = 10;
		registeredMove(secondMoveSize);
		assertFalse("Reached position not in history", robotInPreviousState());
		assertEquals("Steps have been taken", secondMoveSize + firstMoveSize, getPathLength());
		
		registerRobotReached();
		assertEquals("State history size has increased", 3, this.positionHistory.size() );
		assertTrue("Can find position in history", robotInPreviousState());
		
		float endEnergy = robot.getBatteryLevel();
		registerEnergyUsed(startEnergy - endEnergy);
		assertEquals("Used energy", (firstMoveSize + secondMoveSize) * robot.getEnergyForStepForward(), getEnergyConsumption(), ENERGY_FP_ERROR);
		
		float energyUsedBeforeTurn = getEnergyConsumption();
		
		// test turning
		startEnergy = robot.getBatteryLevel();
		// face south hallway
		registeredTurn(Turn.RIGHT);
		assertEquals("Robot now facing South", CardinalDirection.South, robot.getCurrentDirection());
		assertEquals("Turn does not count as step, still same steps taken as before", secondMoveSize + firstMoveSize, getPathLength());
		assertFalse("Same position different direction is not yet in history", robotInPreviousState());
		
		registerRobotReached();
		assertTrue("Can find position after turn in history", robotInPreviousState());
		// turn around
		registeredTurn(Turn.AROUND);
		assertEquals("Robot now facing North", CardinalDirection.North, robot.getCurrentDirection());
		assertEquals("Turn does not count as step, still same steps taken as before", secondMoveSize + firstMoveSize, getPathLength());
		assertFalse("Same position different direction is not yet in history", robotInPreviousState());

		registerRobotReached();
		assertTrue("Can find position after turn in history", robotInPreviousState());
		
		endEnergy = robot.getBatteryLevel();
		registerEnergyUsed(startEnergy - endEnergy);
		assertEquals("Used energy", robot.getEnergyForFullRotation() * 3 / 4 + energyUsedBeforeTurn, getEnergyConsumption(), ENERGY_FP_ERROR);
		
		// test reset
		reset();
		assertEquals("Reset steps taken to 0", 0, getPathLength());
		assertEquals("Reset history to contain nothing", 0, this.positionHistory.size());
		assertEquals("Reset energy used", 0, getEnergyConsumption(), ENERGY_FP_ERROR);
	}
	
	
	
	/**
	 * Test that {@link BasicRobotDriver#turnToFace(CardinalDirection)} correctly turns to face in the specified direction
	 */
	@Test
	public final void testTurnToFace(){
		assertEquals("Starting facing East", CardinalDirection.East, robot.getCurrentDirection());
		turnToFace(CardinalDirection.West);
		assertEquals("Now should face West", CardinalDirection.West, robot.getCurrentDirection());
		turnToFace(CardinalDirection.North);
		assertEquals("Now should face North", CardinalDirection.North, robot.getCurrentDirection());
		turnToFace(CardinalDirection.East);
		assertEquals("Now should face East", CardinalDirection.East, robot.getCurrentDirection());
		turnToFace(CardinalDirection.South);
		assertEquals("Now should face South", CardinalDirection.South, robot.getCurrentDirection());
	}
	
	/**
	 * Test that {@link BasicRobotDriver#exitIfAble()} correctly exits or reports failure to exit
	 */
	@Test
	public final void testExitIfAble(){
		assertFalse("Should not be able to exit from start position", exitIfAble());
		int[] exit = ((BasicRobotTest)robot).getMaze().getMazeConfiguration().getMazedists().getExitPosition();
		((BasicRobotTest)robot).teleportTo(exit[0], exit[1]);
		
		// confirm that fails to exit if no energy to look for exit
		robot.setBatteryLevel(1);
		assertFalse("Should not have enough battery to exit", exitIfAble());
		
		// some energy to look for exit but no energy to move
		((BasicRobot)robot).reset();
		robot.setBatteryLevel(5);
		assertFalse("Should not have enough battery to exit", exitIfAble());
		
		((BasicRobot)robot).reset();
		assertTrue("Should be able to exit from goal position", exitIfAble());
		boolean gotPosition = false;
		try{
			robot.getCurrentPosition();
			gotPosition = true;
		}
		catch(Exception e){
			// this should be reached
		}
		assertFalse("Position is not valid outside maze; getting position should fail", gotPosition);
		int registeredStates = getPathLength();
		registerRobotReached(); // should fail because outside maze
		assertEquals("No additional invalid robot states should be saved", registeredStates, getPathLength());
	}
	
	/**
	 * Test that {@link BasicRobotDriver#drive2Exit()} correctly throws exception when out of energy before reaching exit
	 */
	@Test
	public final void testEnergyShortage(){
		robot.setBatteryLevel(40); // start at low battery so this doesn't take forever
		// should rotate around in place until out of energy
		boolean failed = false;
		try{
			failed = !drive2Exit();
		} 
		catch(Exception e){
			failed = true;
		}
		assertTrue("Test drive should fail miserably", failed);
	}
	
}
