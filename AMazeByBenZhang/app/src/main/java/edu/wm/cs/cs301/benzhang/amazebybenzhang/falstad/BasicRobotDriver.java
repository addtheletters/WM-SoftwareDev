package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Robot.Direction;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Robot.Turn;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.CardinalDirection;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Distance;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Wall;

/**
 * Abstract RobotDriver implementing common features necessary for RobotDrivers.
 * 
 * CRC:
 * BasicRobotDriver (RobotDriver)
 * 
 * Responsibilities
 * Track energy used
 * Track steps taken
 * Provide instructions to robot so it attempts to navigate maze (abstract)
 * 
 * Collaborators
 * Robot to be controlled
 * MazeController in which the robot operates; triggers start of driving when UI switches to play mode
 * MazeApplication attaches driver to Robot and MazeController
 * 
 * @author Ben Zhang
 *
 */
public abstract class BasicRobotDriver implements RobotDriver {

    private static final String TAG = "BasicRobotDriver";

	/**
	 * Milliseconds to wait for actions (moves, turns) to complete. 
	 * Set at 100 to match well with current GUI speeds.
	 * Set at zero to move as quickly as possible.
	 */
	private static final int ACTION_DELAY = 150;
    private static final int LOOP_SLEEP = 5;
	
	protected Robot robot;
	
	private int stepsTaken = 0;
	private float energyUsed = 0;
	
	protected List<Wall> positionHistory;

    private boolean killed = false;
	
	public BasicRobotDriver(){
		reset();
	}
	
	/**
	 * Resets internal driver storage to original. Does not affect robot.
	 */
	protected void reset(){
		stepsTaken = 0;
		energyUsed = 0;
		positionHistory = new ArrayList<Wall>();
	}
	
	@Override
	public void setRobot(Robot r) {
		this.robot = r;
	}

	@Override
	public void setDimensions(int width, int height) {
		//System.out.println("Dimensions are not needed for this driver.");
	}

	@Override
	public void setDistance(Distance distance) {
		//System.out.println("Distance is not needed for this driver.");
	}

    /**
     * Tell this driver to stop running; used when activity is quit
     */
    public void kill(){
        killed = true;
    }

	/**
	 * Provides a single step of progress for simple maze solving drivers.
	 * For dumb algorithms, driveStep can be overwritten rather than the whole of drive2Exit.
	 * @return true if progress continues; false if step determines that maze cannot be solved
	 * @throws Exception if robot halts during this step
	 */
	protected boolean driveStep() throws Exception {
		throw new UnsupportedOperationException("No driving step is implemented.");
	}

    /**
     * Specific exception whose type is checked when determining if FinishActivity should be launched
     */
    public class ExitNotReachedException extends Exception{
        public ExitNotReachedException(String s){
            super(s);
        }
    }
	
	@Override
	public boolean drive2Exit() throws Exception{
		// This class is still effectively abstract here; thus, this function is best tested only when used by subclasses
		while(!robot.isAtGoal()){
			// check if the robot has stopped
            Thread.sleep(LOOP_SLEEP); // allow for interruption with sleep
			if(robot.hasStopped()){
				throw new ExitNotReachedException("Driver was stopped before it could reach exit.");
				// return false;
			}
            if(killed){
                throw new Exception("Driver operation was killed.");
            }
			//System.out.println("Robot is at " + getCurrentRobotStatus());
			
			// check that the robot is not going in circles
			if(robotInPreviousState()){	
				System.err.println("Driver looped back to position it was in before! " + getCurrentRobotStatus());
				//System.err.println("Forgot to reset, or forgot to overwrite a method?");
				//System.out.println("Path len was " + getPathLength());
				//return false;
			}
			else{
				registerRobotReached(); // record the state of the robot before this step
			}
			
			float initialEnergy = robot.getBatteryLevel();
			//System.out.println("Robot is at " + robot.getCurrentPosition()[0] + "," + robot.getCurrentPosition()[1]);
			// driveStep 
			if(!driveStep()){
				return false;
			}
			
			registerEnergyUsed(initialEnergy - robot.getBatteryLevel());
		}
		float energyBeforeExiting = robot.getBatteryLevel();
		boolean exited = exitIfAble();
		registerEnergyUsed(energyBeforeExiting - robot.getBatteryLevel());
		return exited;
	}
	
	/**
	 * Allow for subclasses to have a custom setup for driving using data from a completed maze
	 * @param controller the maze controller to examine for setup
	 */
	public void setupUsingMaze(MazeController controller){
		// setup only necessary for certain implementations of driving
	}
	
	/**
	 * Retrieve a wall object representing the current position and direction of the robot.
	 * @return Wall holding the position and direction of the robot
	 */
	protected Wall getCurrentRobotStatus(){
		try{
			return new Wall(robot.getCurrentPosition()[0], robot.getCurrentPosition()[1], robot.getCurrentDirection());
		}
		catch(Exception e){
			//System.out.println("Robot is outside of maze, cannot get current robot status");
			return null;
		}
	}
	
	/**
	 * Check if the robot is currently in a state (position and direction) that is has reached in the past already.
	 * This is useful for dumb algorithms, revealing that they are going in loops and will never be able to solve the maze.
	 * @return true if robot has reached the current position and direction before; false otherwise.
	 */
	protected boolean robotInPreviousState(){			
		Wall currentRobotStatus = getCurrentRobotStatus();
		if(currentRobotStatus == null){
			return false;
		}
		//System.out.println("Index of current is " +positionHistory.indexOf(currentRobotStatus));
		return positionHistory.contains(currentRobotStatus);
	}
	
	/**
	 * Record that the robot reached a state (position and direction).
	 */
	protected void registerRobotReached(){
		Wall currentRobotStatus = getCurrentRobotStatus();
		if(currentRobotStatus != null){
			positionHistory.add(currentRobotStatus);
		}
		else{
			System.out.println("Robot is outside of maze, cannot register reached position");
		}
	}
	
	/**
	 * Records a usage of energy.
	 * @param energy the amount of energy used
	 */
	protected void registerEnergyUsed(float energy){
		this.energyUsed += energy;
	}
	
	/**
	 * Records that a move was taken.
	 * @param steps number of steps done in this move
	 */
	protected void registerMoveTaken(int steps){
		this.stepsTaken += steps;
	}
	
	/**
	 * Takes a move and records the steps taken, waiting for completion if desired.
	 * @param steps the number of steps forward to take
	 */
	protected void registeredMove(int steps){
        //Log.v(TAG, "Robot took move " + steps);
		robot.move(steps, false);
		registerMoveTaken(steps);
		//registerEnergyUsed(robot.getEnergyForStepForward() * steps);
		try {
			passTime(ACTION_DELAY * steps);
			//Thread.sleep(ACTION_DELAY * steps);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private void passTime(long ms){
        //Log.v(TAG, "passing time " + ms);
		android.os.SystemClock.sleep(ms);
		// Thread.sleep(ms);
	}

	/**
	 * Takes a turn, waiting for completion if desired.
	 * @param turn the turn to take
	 */
	protected void registeredTurn(Turn turn){
        //Log.v(TAG, "Robot took turn " + turn);
		robot.rotate(turn);
		try{
			if(turn == Turn.AROUND){
				//registerEnergyUsed(robot.getEnergyForFullRotation() / 2);
				passTime(ACTION_DELAY * 2);
				//Thread.sleep(ACTION_DELAY * 2);
			}
			else if(turn == Turn.LEFT || turn == Turn.RIGHT){
				//registerEnergyUsed(robot.getEnergyForFullRotation() / 4);
				passTime(ACTION_DELAY);
			}
			else{
				System.err.println("Unknown turn.");
			}
		} catch(Exception e){
			//e.printStackTrace();
		}
	}
	
	/**
	 * Turns the robot the minimum amount needed (right or left) to end up facing in dir.
	 * If already facing that direction, does nothing.
	 * @param dir The direction to face after turning
	 */
	protected void turnToFace(CardinalDirection dir){
		Direction robotDir = dir.robotDirection(robot);
		if(robotDir != null && robotDir != Direction.FORWARD){
			registeredTurn( robotDir.turn() );
		}
	}
	
	/**
	 * Complete (exit) the maze, if there is a straight line out from the robot's current position.
	 * @return true if robot exited the maze; false if failed to locate or reach an exit.
	 */
	protected boolean exitIfAble(){
		for(Direction dir : Direction.values()){
			if(robot.canSeeGoal(dir)){
				if(dir.turn() != null) {
                    registeredTurn(dir.turn());
                }
				while(true){
					registeredMove(1);
					try {
						robot.getCurrentPosition(); // if robot has left the maze, this throws exception
					} catch (Exception e) {
						return true; // position is outside maze; successfully left maze
					}
					if(robot.hasStopped()){
						return false;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public float getEnergyConsumption() {
		return energyUsed;
	}

	@Override
	public int getPathLength() {
		return stepsTaken;
	}

	public abstract String getName();
}
