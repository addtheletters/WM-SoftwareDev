package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Robot.Direction;

/**
 * RobotDriver which attempts to reach an exit by following the walls of the maze.
 * Has the flexibility to follow either the left or right walls.
 * 
 * CRC card:
 * 
 * WallFollower extends BasicRobotDriver (abstract RobotDriver)
 * 
 * Responsibilities:
 * Give instructions to Robot so it reaches the exit
 * 
 * Collaborators:
 * See abstract superclass
 * 
 * @author Ben Zhang
 *
 */
public class WallFollower extends BasicRobotDriver {

	// in the GUI, following the left wall actually means it will turn right as much as possible
	protected boolean left;

	/**
	 * Default constructor. Follows the internal 'left' direction, corresponding with right turns in the GUI
	 */
	public WallFollower(){
		this(true);
	}
	
	/**
	 * Constructor to specify a side (left or right) to follow
	 * @param followLeftSide true to follow the internal left direction; false to follow the internal right direction
	 */
	public WallFollower(boolean followLeftSide){
		left = followLeftSide;
	}
	
	/**
	 * Set the side to follow
	 * @param left true to follow the left; false to follow the right
	 */
	protected void setSide(boolean left){
		this.left = left;
	}
	
	@Override
	protected boolean driveStep() throws Exception {
		if(hasSideWall()){
			if(!hasFrontWall()){
				registeredMove(1);
			}
			else{
				registeredTurn(primarySideDir().opposite().turn());
			}
		}
		else{
			registeredTurn(primarySideDir().turn());
			registeredMove(1);
		}
		return true;
	}

	@Override
	public String getName() {
		return "WallFollower";
	}

	/**
	 * Determines if there is a wall along the side which this driver is attempting to follow
	 * @return true if wall is present; false otherwise
	 */
	protected boolean hasSideWall(){
		return robot.distanceToObstacle(primarySideDir()) == 0;
	}
	
	/**
	 * Determines if there is a wall directly in front of this driver's robot
	 * @return true if wall is present; false otherwise
	 */
	protected boolean hasFrontWall(){
		return robot.distanceToObstacle(Direction.FORWARD) == 0;
	}
	
	/**
	 * Gets the Direction which represents the side that this driver is set to follow (left or right)
	 * @return direction of the side to be followed
	 */
	protected Direction primarySideDir(){
		if(left){
			return Direction.LEFT;
		}
		else{
			return Direction.RIGHT;
		}
	}
	
}
