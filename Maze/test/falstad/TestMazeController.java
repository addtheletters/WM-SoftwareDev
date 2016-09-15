package falstad;

import java.lang.Thread.State;

import falstad.Constants.StateGUI;
import generation.MazeConfiguration;
import generation.Order.Builder;

/**
 * Testing class overwriting certain functionality of MazeController for simplicity.
 * 
 * @author Ben Zhang
 */
public class TestMazeController extends MazeController {
	
	public TestMazeController(String filename){
		super(filename);
	}
	
	public TestMazeController(Builder builder, int skill, boolean perfect, boolean deterministic) {
		super(builder, skill, perfect, deterministic);
	}

	@Override
	public void deliver(MazeConfiguration mazeConfig) {
		System.out.println("Delivered!");
		//mazeConfig.getMazedists().prettyPrint();
		setMazeConfiguration(mazeConfig);
		setupForUse(mazeConfig);
		
		switchToPlayingScreen();
	}
	
	@Override
	protected void switchToPlayingScreen() {
		assert getState() == StateGUI.STATE_GENERATING : "MazeController.switchToPlayingScreen: unexpected current state " + getState() ;
		// set the current state for the state-dependent behavior
		System.out.println("Switching state...");
		setState(StateGUI.STATE_PLAY);
	}
}
