package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.CardinalDirection;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeConfiguration;

/**
 * RobotDriver which attempts to reach the exit by following the distance-to-exit measures
 * stored in the maze configuration.
 * 
 * CRC:
 * Wizard extends BasicRobotDriver (abstract RobotDriver)
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
public class Wizard extends BasicRobotDriver {
	
	protected MazeConfiguration mazeConfig;
	
	@Override
	public void setupUsingMaze(MazeController controller){
		mazeConfig = controller.getMazeConfiguration();
	}

	@Override
	public String getName() {
		return "Wizard";
	}

	@Override
	protected boolean driveStep() throws Exception {
		int[] robotPos = robot.getCurrentPosition();
		int[] closer = mazeConfig.getNeighborCloserToExit(robotPos[0], robotPos[1]);
		turnToFace( CardinalDirection.East.getDirection(closer[0] - robotPos[0], closer[1] - robotPos[1]) );
		registeredMove(1);
		//System.out.println("Moved forward.");
		return true;
	}
	
}
