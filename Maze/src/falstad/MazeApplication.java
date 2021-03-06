/**
 * 
 */
package falstad;

import generation.Order;

import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JFrame;


/**
 * This class is a wrapper class to startup the Maze game as a Java application
 * 
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 * 
 * TODO: use logger for output instead of Sys.out
 */
public class MazeApplication extends JFrame {

	// not used, just to make the compiler, static code checker happy
	private static final long serialVersionUID = 1L;

	private KeyListener kl ;

	private MazeController controller ;
	private Robot robot;
	private RobotDriver driver;
	
	/**
	 * Constructor
	 */
	public MazeApplication() {
		super() ;
		System.out.println("MazeApplication: maze will be generated with a randomized algorithm.");
		controller = new MazeController() ;
		init() ;
	}

	/**
	 * Constructor that loads a maze from a given file or uses a particular method to generate a maze
	 */
	public MazeApplication(String parameter) {
		super() ;
		// scan parameters
		// Case 1: Prim
		if ("Prim".equalsIgnoreCase(parameter))
		{
			System.out.println("MazeApplication: generating random maze with Prim's algorithm");
			controller = new MazeController(Order.Builder.Prim) ;
			init() ;
			return ;
		}
		// Case 2: Kruskal
		if("Kruskal".equalsIgnoreCase(parameter)){
			System.out.println("MazeApplication: generating random maze with Kruskal's algorithm");
			controller = new MazeController(Order.Builder.Kruskal);
			init();
			return;
		}
		
		// Case 3: a file
		File f = new File(parameter) ;
		if (f.exists() && f.canRead())
		{
			System.out.println("MazeApplication: loading maze from file: " + parameter);
			controller = new MazeController(parameter) ;
			init();
			return ;
		}
		// Default case: 
		System.out.println("MazeApplication: unknown parameter value: " + parameter + " ignored, operating in default mode.");
		controller = new MazeController() ;
		init() ;
	}
	
	/**
	 * Constructor for altered behavior based on 'flag' parameters
	 * @param params
	 */
	public MazeApplication(String[] params){
		super();
		int i = 0;
		while(i < params.length){
			if(params[i].charAt(0) == '-'){
				System.out.println("Recognized parameter " + params[i]);
				if(i + 1 < params.length){
					setParam(params[i].substring(1), params[i+1]);
				}
				else{
					System.err.println("No value provided for param " + params[i]);					
				}
			}
			else{
				System.out.println("MazeApplication: Non-flag parameter ignored, please use flags or provide fewer parameters.");
			}
			i+=2;
		}
		if(controller == null){
			System.out.println("No controller specified by parameters. Defaulting.");
			controller = new MazeController();
		}
		
		if(robot == null){
			System.out.println("Robot set to default basic.");
			robot = new BasicRobot();
			robot.setMaze(controller);
		}
		if(driver == null){
			System.out.println("No robot driver set.");
			//driver = new WallFollower();
		}
		else{
			driver.setRobot(robot);
		}
		init();
	}
	
	/**
	 * Returns the builder associated with the given name, if one exists
	 * @param builderName string representing the builder
	 * @return builder if one is found; null otherwise
	 */
	private Order.Builder builderFor(String builderName){
		if("DFS".equalsIgnoreCase(builderName)){
			return Order.Builder.DFS;
		}
		else if("Prim".equalsIgnoreCase(builderName)){
			return Order.Builder.Prim;
		}
		else if("Kruskal".equalsIgnoreCase(builderName)){
			return Order.Builder.Kruskal;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Sets up a certain aspect of the application based on a supplied parameter's flag and value
	 * @param flag
	 * @param value
	 */
	private void setParam(String flag, String value){
		switch(flag){
		case "det":
			if(controller != null){
				System.err.println("Controller already set. Flag " + flag + " has no effect.");
				return;
			}
			System.out.println("Parameter " + value + " used to set generation algorithm with low skill, deterministic, perfect factory.");
			Order.Builder builder = builderFor(value);
			if(builder != null){
				controller = new MazeController(builder, 1, true, true);
			}
			else{
				System.out.println("Unrecognized generation algorithm. Defaulting.");
				controller = new MazeController();
			}
			break;
		case "g":
			if(controller != null){
				System.err.println("Controller already set. Flag " + flag + " has no effect.");
				return;
			}
			System.out.println("Parameter " + value + " used to set generation algorithm.");
			if("DFS".equalsIgnoreCase(value)){
				controller = new MazeController(Order.Builder.DFS);
			}
			else if("Prim".equalsIgnoreCase(value)){
				controller = new MazeController(Order.Builder.Prim);
			}
			else if("Kruskal".equalsIgnoreCase(value)){
				controller = new MazeController(Order.Builder.Kruskal);
			}
			else{
				System.out.println("Unrecognized generation algorithm. Defaulting.");
				controller = new MazeController();
			}
			break;
		case "d":
			System.out.println("Parameter " + value + " used to select robot driver.");
			if("WallFollower".equalsIgnoreCase(value)){
				driver = new WallFollower();
			}
			else if("Wizard".equalsIgnoreCase(value)){
				driver = new Wizard();
			}
			else{
				System.out.println("Unrecognized driver; using manual driving.");
				// manual operation
			}
			break;
		case "f":
			if(controller != null){
				System.err.println("Controller already set. Flag " + flag + " has no effect.");
				return;
			}
			System.out.println("Attempting to load " + value + " as maze file.");
			File f = new File(value);
			if(f.exists() && f.canRead()){
				controller = new MazeController(value);
				System.out.println("Loaded.");
			}
			else{
				System.err.println("Failed to load as file.");
			}
			break;
		default:
			System.out.println("Flag " + flag + " had no effect.");
			break;
		}
	}

	/**
	 * Initializes some internals and puts the game on display.
	 */
	private void init() {
		if(this.driver != null){
			controller.setDriver(driver);
		}
		
		add(controller.getPanel()) ;
		
		kl = new SimpleKeyListener(this, controller) ;
		addKeyListener(kl) ;
		
		setSize(400, 400) ;
		setVisible(true) ;
		
		// focus should be on the JFrame of the MazeApplication and not on the maze panel
		// such that the SimpleKeyListener kl is used
		setFocusable(true) ;
		
		controller.init();
	}
	
	/**
	 * Main method to launch Maze as a java application.
	 * The application can be operated in two ways. The intended normal operation is to provide no parameters
	 * and the maze will be generated by a particular algorithm. If a filename is given, the maze will be loaded
	 * from that file. The latter option is useful for development to check particular mazes.
	 * @param args is optional, first parameter is a filename with a given maze
	 */
	public static void main(String[] args) {
		MazeApplication a ; 
		switch (args.length) {
		case 1 :
			a = new MazeApplication(args[0]);
			break;
		case 0 : 
			a = new MazeApplication();
			break;
		default :
			a = new MazeApplication(args);
			break;
		}
		a.repaint() ;
	}

}
