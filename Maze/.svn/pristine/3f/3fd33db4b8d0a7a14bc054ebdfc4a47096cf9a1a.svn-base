package falstad;

import java.awt.Point;

import falstad.Constants.StateGUI;
import generation.CardinalDirection;

/**
 * Basic implementation of Robot, with complete sensor suite.
 * 
 * Note about MazeController and the GUI:
 * In the GUI, East and West are correctly aligned but North and South are reversed.
 * This results in 'left' turns appearing to turn right, and 'right' turns appearing to turn left.
 * However, this inconsistency is limited to rendering. 
 * For the sake of sense in the code base, the Direction and Turn will accurately reflect real directional relations
 * rather than those of the GUI, with the connections to the GUI through MazeController performing the conversion
 * of turns into the opposite rotation.
 * 
 * CRC card:
 * 
 * BasicRobot (Robot)
 * 
 * Responsibilities:
 * Movement (turning, stepping forwards) in maze
 * Sensor information about surroundings
 * Track battery capacity 
 * 
 * Collaborators:
 * RobotDriver to control the robot
 * MazeController to drive through 
 * 
 * @author Ben Zhang
 *
 */
public class BasicRobot implements Robot {
	
	/*
	 * Constants for energy costs and battery levels
	 */
	protected static final float INITIAL_BATTERY = 2500;
	protected static final float DISTANCE_SENSE_COST = 1;
	protected static final float ROTATE_360_COST = 12;
	protected static final float MOVE_COST = 5;
	
	/*
	 * As specified by the project description:
	 The initial battery level is 2500.
	• The energy costs are:
	• Distance sensing in one direction: 1
	• Rotating left or right: 3 for 90 degrees, 6 for 180 degrees, 12 for 360 degrees
	• Moving forward one step (one cell): 5
	 */

	private MazeController maze;
	protected float battery;
	protected boolean stopped;
	
	public BasicRobot(){
		reset();
	}
	
	/**
	 * Resets the robot to its initial battery capacity and un-stops.
	 */
	protected void reset(){
		battery = INITIAL_BATTERY;
		stopped = false;
	}
	
	@Override
	public void rotate(Turn turn) {
		//facing = worldDirectionOf(turnDirection(turn));
		if(useEnergyIfAble(getTurnEnergy(turn))){
			maze.rotate(turn);			
		}
		else{			
			System.out.println("BasicRobot: Robot did not have enough energy to rotate. Stopping.");
			stop();
		}
	}
	
	/**
	 * Calculates the energy required to perform the specified turn.
	 * @param turn the turn to be performed
	 * @return the amount of energy needed to perform the turn
	 */
	protected float getTurnEnergy(Turn turn){
		switch(turn){
		case LEFT:
		case RIGHT:
			return getEnergyForFullRotation() / 4;
		default: // case AROUND
			return getEnergyForFullRotation() / 2;
		}
	}

	@Override
	public void move(int distance, boolean manual) {
		for(int i = 0; i < distance; i++){
			boolean successfulStep = attemptStepForward();
			if(successfulStep){
				continue;
			}
			break;
		}
	}
	
	/**
	 * Attempts to move robot forward one space; success depends on energy available in battery.
	 * Stops the robot if not enough energy is available.
	 * @return true if energy is used and walk is attempted; false if not enough energy remains.
	 */
	protected boolean attemptStepForward(){
		if(isBlocked()){
			System.out.println("BasicRobot: Robot walked into wall.");
			return false;
		}
		boolean usedEnergy = useEnergyIfAble(getEnergyForStepForward());
		if(usedEnergy){
			maze.walk(false);
			maze.checkFinished();
			return true;
		}
		else{
			System.out.println("BasicRobot: Robot did not have enough energy to step forward. Stopping.");
			stop();
			return false;
		}
	}

	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] pos = maze.getCurrentPosition();
		if(!maze.getMazeConfiguration().isValidPosition(pos[0], pos[1])){
			throw new Exception("Current position was invalid! " + pos[0] + "," + pos[1]);
		}
		return pos;//new int[] {position.x, position.y};
	}
	
	/**
	 * Wraps current position into a Point object and avoids need to handle exception specified by getCurrentPosition
	 * @return Point representing the current position
	 */
	protected Point getCurrentPoint(){
		int[] pos = maze.getCurrentPosition();
		return new Point(pos[0], pos[1]);
	}

	@Override
	public void setMaze(MazeController maze) {
		//System.out.println("Setting maze again to " + maze);
		this.maze = maze;
	}
	
	/**
	 * Gets the current MazeController.
	 * @return maze controller this robot is attached to
	 */
	protected MazeController getMaze(){
		return this.maze;
	}

	@Override
	public boolean isAtGoal() {
		Point here = getCurrentPoint();
		return maze.getMazeConfiguration().getMazecells().isExitPosition(here.x, here.y);
	}

	@Override
	public boolean canSeeGoal(Direction direction) throws UnsupportedOperationException {
		return distanceToObstacle(direction) == Integer.MAX_VALUE;
	}

	@Override
	public boolean isInsideRoom() throws UnsupportedOperationException {
		Point here = getCurrentPoint();
		return maze.getMazeConfiguration().getMazecells().isInRoom(here.x, here.y);
	}

	@Override
	public boolean hasRoomSensor() {
		return true;
	}

	@Override
	public CardinalDirection getCurrentDirection() {
		return maze.getCurrentDirection();
		//return facing;
	}

//	/**
//	 * Describes the cardinal direction that a particular robot direction currently represents
//	 * @param direction the robot-relative direction
//	 * @return the cardinal direction
//	 */
//	protected CardinalDirection worldDirectionOf( Direction direction ){
//		switch(direction){
//		case FORWARD:
//			return getCurrentDirection();
//		case BACKWARD:
//			return getCurrentDirection().oppositeDirection();
//		case LEFT:
//			return getCurrentDirection().rotateCounterCW();
//		case RIGHT:
//			return getCurrentDirection().rotateClockwise(); // TODO make sure these aren't messed up...
//		default:
//			System.err.println("BasicRobot: Unknown direction");
//			return getCurrentDirection();
//		}
//	}
//	
	@Override
	public float getBatteryLevel() {
		return battery;
	}

	@Override
	public void setBatteryLevel(float level) {
		//"BasicRobot: Battery level should not be negative.
		this.battery = level;
	}

	@Override
	public float getEnergyForFullRotation() {
		return ROTATE_360_COST;
	}

	@Override
	public float getEnergyForStepForward() {
		return MOVE_COST;
	}
	
	/**
	 * Checks that an amount of energy is available; if so, it is consumed.
	 * @param energy the amount of energy to try and consume
	 * @return true if energy is used; false if not enough is available
	 */
	protected boolean useEnergyIfAble(float energy){
		if(getBatteryLevel() >= energy){
			setBatteryLevel(getBatteryLevel() - energy);
			
			// at some point should consolidate stops so that this is the only one necessary
			if(getBatteryLevel() <= 0){
				stop();
			}
			//System.out.println("Robot used energy, now has " + getBatteryLevel());
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Sets the robot to a 'stopped' state. Drivers should recognize this and cease operation.
	 */
	protected void stop(){
		System.out.println("Stopped. Battery is " + getBatteryLevel());
		stopped = true;
	}

	@Override
	public boolean hasStopped() {
		if(maze != null){
			if(maze.getState() == StateGUI.STATE_PLAY){
				return stopped;
			}
			else{
				System.out.println("Maze not in playing state.");
			}
		}
		else{
			System.out.println("Maze was null.");
		}
		return true;
	}
	
	/**
	 * Checks if a move forward is blocked, circumventing energy cost. 
	 * @return true if move is possible; false otherwise.
	 */
	private boolean isBlocked(){
		Point here = getCurrentPoint();
		CardinalDirection facing = getCurrentDirection();
		return maze.getMazeConfiguration().getMazecells().hasMaskedBitsTrue(here.x, here.y, facing.getCWConstantForDirection());
	}

	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		if(!useEnergyIfAble(DISTANCE_SENSE_COST)){
			System.out.println("BasicRobot: Robot has no energy to find distance to obstacle.");
			stop();
			return -1;
		}

		boolean wallFound = false;
		int distance = 0;
		Point checkpos = getCurrentPoint();
		CardinalDirection checkdir = direction.worldDirectionOf(this);
		while(!wallFound){
			if(maze.getMazeConfiguration().hasWall(checkpos.x, checkpos.y, checkdir) ){ // checkCanGo(checkpos.x, checkpos.y, checkdir)){//
				wallFound = true;
				break;
			}
//			if(checkdir == CardinalDirection.North || checkdir == CardinalDirection.South){
//				checkpos.translate(checkdir.getDirection()[0], -checkdir.getDirection()[1]);
//			}
//			else{
			checkpos.translate(checkdir.getDirection()[0], checkdir.getDirection()[1]);
//			}
			distance ++;
			
			if(!(maze.getMazeConfiguration().isValidPosition(checkpos.x, checkpos.y))){
				return Integer.MAX_VALUE; // left the maze; found the exit
			}
		}
		return distance;
	}

	@Override
	public boolean hasDistanceSensor(Direction direction) {
		return true;
	}
	
}
