package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.PlayActivity;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Constants.StateGUI;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Robot.Turn;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.CardinalDirection;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Cells;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Factory;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeConfiguration;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeContainer;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeFactory;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Order;

/**
 * Class handles the user interaction. 
 * It implements a state-dependent behavior that controls the display and reacts to key board input from a user. 
 * At this point user keyboard input is first dealt with a key listener (SimpleKeyListener)
 * and then handed over to a MazeController object by way of the keyDown method.
 *
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 */
public class MazeController implements Parcelable{

    private static final String TAG = "MazeController";
	
	private static final boolean SUPPRESS_PRINT = true;

    private static final long SLOWDOWN_DELAY = 5;

    public Handler controllerHandle;// = new Handler();
	
	// Follows a variant of the Model View Controller pattern (MVC).
	// This class acts as the controller that gets user input and operates on the model.
	// A MazeConfiguration acts as the model and this class has a reference to it.
	private MazeConfiguration mazeConfig ; 
	// Deviating from the MVC pattern, the controller has a list of viewers and 
	// notifies them if user input requires updates on the UI.
	// This is normally the task of the model in the MVC pattern.

    // handler to allow drawing to occur in UI thread
    //private Handler viewHandle;

	// views is the list of registered viewers that get notified
	final private ArrayList<Viewer> views = new ArrayList<Viewer>() ;

	// all viewers share access to the same graphics object, the panel, to draw on
    // android view to be handed to Viewer objects to be drawn on
    private MazePanel panel;

    // activity of play such that finishing maze can result in appropriate response
    private PlayActivity mazeActivity;

	// possible values are defined in Constants
	// user can navigate 
	// title -> generating -(escape) -> title
	// title -> generation -> play -(escape)-> title
	// title -> generation -> play -> finish -> title
	// STATE_PLAY is the main state where the user can navigate through the maze in a first person view

	private boolean showMaze;		 	// toggle switch to show overall maze on screen
	private boolean showSolution;		// toggle switch to show solution in overall maze on screen
	private boolean mapMode; // true: display map of maze, false: do not display map of maze
	// map_mode is toggled by user keyboard input, causes a call to draw_map during play mode

	// current position and direction with regard to MazeConfiguration
	private int px, py ; // current position on maze grid (x,y)
	private int dx, dy;  // current direction

	// current position and direction with regard to graphics view
	// graphics has intermediate views for a smoother experience of turns
	private int viewx, viewy; // current position
	private int viewdx, viewdy; // current view direction, more fine grained than (dx,dy)
	private int angle; // current viewing angle, east == 0 degrees
	//static final int viewz = 50;    
	private int walkStep; // counter for intermediate steps within a single step forward or backward
	private Cells seencells; // a matrix with cells to memorize which cells are visible from the current point of view
	// the FirstPersonDrawer obtains this information and the MapDrawer uses it for highlighting currently visible walls on the map

	//private int zscale = Constants.VIEW_HEIGHT/2;
	private RangeSet rset;
	
	// debug stuff
	private boolean deepdebug = false;
	private boolean allVisible = false;
	private boolean newGame = false;
	
	// if there is a driver attempting to navigate, will go here
	private RobotDriver driver;

	
	/**
	 * Constructor, provided complete maze configuration and possible driver
	 */
	public MazeController(MazeConfiguration config, PlayActivity activity, MazePanel view){
		reattach(config, activity, view);
	}

	/**
	 * Reassigns object variable values; For use when reconstructing controller from saved instance state
     */
	public void reattach(MazeConfiguration config, PlayActivity activity, MazePanel view){
		setMazeConfiguration(config);
		this.mazeActivity = activity;
		this.panel = view;
	}

	protected MazeController(Parcel in) {
		showMaze = in.readByte() != 0;
		showSolution = in.readByte() != 0;
		mapMode = in.readByte() != 0;
		px = in.readInt();
		py = in.readInt();
		dx = in.readInt();
		dy = in.readInt();
		viewx = in.readInt();
		viewy = in.readInt();
		viewdx = in.readInt();
		viewdy = in.readInt();
		angle = in.readInt();
		walkStep = in.readInt();
		deepdebug = in.readByte() != 0;
		allVisible = in.readByte() != 0;
		newGame = in.readByte() != 0;
	}

	public static final Creator<MazeController> CREATOR = new Creator<MazeController>() {
		@Override
		public MazeController createFromParcel(Parcel in) {
			return new MazeController(in);
		}

		@Override
		public MazeController[] newArray(int size) {
			return new MazeController[size];
		}
	};

    /**
     * For resuming from a saved instance state.
     */
    public void reinit(MazePanel view, PlayActivity activity){
        if(view != null) {
            this.panel = view;
            Log.v(TAG, "Reassigned panel.");
        }
        if(activity != null){
            this.mazeActivity = activity;
            Log.v(TAG, "Reassigned maze activity.");
        }
        panel.createBitmapsIfNeeded(false);

        cleanViews() ;
        // register views for the new maze
        FirstPersonDrawer fpdview = new FirstPersonDrawer(panel.getWidth(),panel.getHeight(), Constants.MAP_UNIT,
                Constants.STEP_SIZE, seencells, mazeConfig.getRootnode());

        //Log.v("MazeController", "pretty print");
        //mazeConfig.getMazedists().prettyPrint();

        MapDrawer mdview = new MapDrawer(panel.getWidth(),panel.getWidth(),Constants.MAP_UNIT,
                Constants.STEP_SIZE, seencells, Constants.MAP_SCALE, this);

        panel.addToReport(fpdview);
        panel.addToReport(mdview);

        addView(fpdview) ;
        // order of registration matters, code executed in order of appearance!
        addView(mdview) ;
    }

	/**
	 * Method to initialize internal attributes. Called separately from the constructor.
     * Intended to be run in thread separate from UI.
     * Initiates robot driver if one is given.
	 */
	public void init() {
		rset = new RangeSet();
        setupForUse(this.mazeConfig);

        reinit(null, null);

        // start driver if one is set
        Log.v(TAG, "Driving to exit.");
        if(driver != null){
            try{
                if(driver instanceof BasicRobotDriver){
                    ((BasicRobotDriver)driver).reset();
                    ((BasicRobotDriver)driver).setupUsingMaze(this);
                    ((BasicRobot)((BasicRobotDriver)driver).robot).reset();
                }
                driver.drive2Exit();
            }
            catch(Exception e){
                Log.e(TAG, "Robot was stopped!");
                e.printStackTrace();
                if(e instanceof BasicRobotDriver.ExitNotReachedException){
                    fail("Robot was stopped before reaching exit.");
                }
                else{
					Log.v(TAG, "Robot problem was either exit of activity or unexpected.");
                    //fail("Robot encountered a problem.");
                }
            }
        }

		notifyViewerRedraw();
	}

    /**
     * Introduces a delay.
     * @param ms delay time in milliseconds
     * @throws Exception signifying interrupt if interrupted
     */
    private void delay(long ms) throws Exception{
        //Thread.currentThread().sleep(25);
        android.os.SystemClock.sleep(ms);
    }
	
	public void setDriver(RobotDriver driver){
		this.driver = driver;
	}
	
	public RobotDriver getDriver(){
		return this.driver;
	}
	
	public MazeConfiguration getMazeConfiguration() {
		return mazeConfig ;
	}
	
	public void setMazeConfiguration(MazeConfiguration mazeConfig){
		this.mazeConfig = mazeConfig;
	}

    /**
     * Used by the robot to report what percent of its energy it has used.
     * Updates UI through handler.
     * @param percentage the percent of battery remaining, [0-100]
     */
    public void reportEnergyLevel(final int percentage){
        if(mazeActivity != null){
            //Log.v(TAG, "Controller had mazeActivity. Posting to handle.");
            mazeActivity.handle.post(new Runnable() {
                @Override
                public void run() {
                    mazeActivity.setEnergyBarLevel(percentage);
                }
            });
        }
        else{
            //Log.e(TAG, "Controller was missing mazeActivity.");
        }
    }

    /**
     * Maze is completed.
     */
    private void finish(){
        Log.v(TAG, "Notifying activity of success.");
        mazeActivity.handle.post(new Runnable() {
            @Override
            public void run() {
                mazeActivity.mazeFinished(false, null);
            }
        });
    }

    /**
     * Maze is failed.
     */
    private void fail(final String failureReason){
        Log.v(TAG, "Notifying activity of failure.");
		mazeActivity.handle.post(new Runnable() {
            @Override
            public void run() {
                mazeActivity.mazeFinished(true, failureReason);
            }
        });
    }

	/////////////////////////////// Methods for the Model-View-Controller Pattern /////////////////////////////
	/**
	 * Register a view
	 */
	public void addView(Viewer view) {
		views.add(view) ;
	}
	/**
	 * Unregister a view
	 */
	public void removeView(Viewer view) {
		views.remove(view) ;
	}
	/**
	 * Remove obsolete FirstPersonDrawer and MapDrawer
	 */
	private void cleanViews() {
		// go through views and remove viewers as needed
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			if ((v instanceof FirstPersonDrawer)||(v instanceof MapDrawer))
			{
				it.remove() ;
			}
		}

	}
	/**
	 * Notify all registered viewers to redraw their graphics
	 */
	protected void notifyViewerRedraw() {
        try {
            // go through views and notify each one
            Iterator<Viewer> it = views.iterator();
            while (it.hasNext()) {
                Viewer v = it.next();
                if (null == panel) {
                    if (!SUPPRESS_PRINT)
                        System.out.println("Maze.notifierViewerRedraw: can't get graphics object to draw on, skipping redraw operation");
                } else {
                    v.redraw(panel, StateGUI.STATE_PLAY, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle);
//                viewHandle.post(new ViewUpdater(v){
//                    @Override
//                    public void run() {
//                        this.viewer.redraw(panel, StateGUI.STATE_PLAY, px, py, viewdx, viewdy, walkStep, Constants.VIEW_OFFSET, rset, angle) ;
//                    }
//                });
                }
            }
            // update the screen with the buffer graphics
            panel.update();
            mazeActivity.handle.post(new Runnable() {
                @Override
                public void run() {
                    //panel.update();
                    panel.invalidateUI();
                }
            });
        }
        catch (ConcurrentModificationException e){
            Log.e(TAG, "Error notifying viewers; was screen orientation changing? " + e.getMessage());
        }
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeByte((byte) (showMaze ? 1 : 0));
		parcel.writeByte((byte) (showSolution ? 1 : 0));
		parcel.writeByte((byte) (mapMode ? 1 : 0));
		parcel.writeInt(px);
		parcel.writeInt(py);
		parcel.writeInt(dx);
		parcel.writeInt(dy);
		parcel.writeInt(viewx);
		parcel.writeInt(viewy);
		parcel.writeInt(viewdx);
		parcel.writeInt(viewdy);
		parcel.writeInt(angle);
		parcel.writeInt(walkStep);
		parcel.writeByte((byte) (deepdebug ? 1 : 0));
		parcel.writeByte((byte) (allVisible ? 1 : 0));
		parcel.writeByte((byte) (newGame ? 1 : 0));
	}

	/**
     * Abstract class to assist in posting view updates to the android GUI
     */
    protected abstract class ViewUpdater implements Runnable{
        protected Viewer viewer;
        public ViewUpdater(Viewer v){
            this.viewer = v;
        }
    }

	/** 
	 * Notify all registered viewers to increment the map scale
	 */
	public void notifyViewerIncrementMapScale(int amount) {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.incrementMapScale(amount) ;
		}
		// update the screen with the buffer graphics
		//panel.update() ;
		notifyViewerRedraw();
	}
	/** 
	 * Notify all registered viewers to decrement the map scale
	 */
	public void notifyViewerDecrementMapScale(int amount) {
		// go through views and notify each one
		Iterator<Viewer> it = views.iterator() ;
		while (it.hasNext())
		{
			Viewer v = it.next() ;
			v.decrementMapScale(amount) ;
		}
		// update the screen with the buffer graphics
		//panel.update() ;
		notifyViewerRedraw();
	}
	////////////////////////////// get methods ///////////////////////////////////////////////////////////////
	boolean isInMapMode() { 
		return mapMode ; 
	} 
	boolean isInShowMazeMode() { 
		return showMaze ; 
	} 
	boolean isInShowSolutionMode() { 
		return showSolution ; 
	}

	public MazePanel getPanel() {
		return panel ;
	}
	////////////////////////////// set methods ///////////////////////////////////////////////////////////////
	////////////////////////////// Actions that can be performed on the maze model ///////////////////////////
	public void setCurrentPosition(int x, int y)
	{
		px = x ;
		py = y ;
	}
	protected void setCurrentDirection(int x, int y)
	{
		dx = x ;
		dy = y ;
	}
	protected int[] getCurrentPosition() {
		int[] result = new int[2];
		result[0] = px;
		result[1] = py;
		return result;
	}
	protected CardinalDirection getCurrentDirection() {
		return CardinalDirection.East.getDirection(dx, dy);
	}

	/////////////////////// Methods for debugging ////////////////////////////////
	private void dbg(String str) {
		//System.out.println(str);
	}

	private void logPosition() {
		if (!deepdebug)
			return;
		dbg("x="+viewx/Constants.MAP_UNIT+" ("+
				viewx+") y="+viewy/Constants.MAP_UNIT+" ("+viewy+") ang="+
				angle+" dx="+dx+" dy="+dy+" "+viewdx+" "+viewdy);
	}
	
	//////////////////////// Methods for move and rotate operations ///////////////
	final double radify(int x) {
		return x*Math.PI/180;
	}
	/**
	 * Helper method for walk()
	 * @param dir 1 for walking forwards; -1 for walking backwards
	 * @return true if there is no wall in this direction
	 */
	private boolean checkMove(int dir) {
		// obtain appropriate index for direction (CW_BOT, CW_TOP ...) 
		// for given direction parameter
        if(isOutside(px, py)){
            return false;
        }

		int a = angle/90;
//		System.out.println("Angle is " + angle);
		if (dir == -1)
			a = (a+2) & 3; // TODO: check why this works
//		System.out.println("a is " + a);
		// check if cell has walls in this direction
		// returns true if there are no walls in this direction
		Cells cells = mazeConfig.getMazecells();

		return cells.hasMaskedBitsFalse(px, py, Constants.MASKS[a]) ;
	}
	
	/**
	 * Redraw and wait, used to obtain a smooth appearance for rotate and move operations
	 */
	private void slowedDownRedraw() {
		notifyViewerRedraw();
		try {
			delay(SLOWDOWN_DELAY);
		} catch (Exception e) { }
	}
	/**
	 * Intermediate step during rotation, updates the screen
	 */
	private void rotateStep() {
		angle = (angle+1800) % 360;
		viewdx = (int) (Math.cos(radify(angle))*(1<<16));
		viewdy = (int) (Math.sin(radify(angle))*(1<<16));
		slowedDownRedraw();
	}
	/**
	 * Performs a rotation with 4 intermediate views, 
	 * updates the screen and the internal direction
	 * @param dir for current direction
	 */
	synchronized private void rotate(int dir) {
		final int originalAngle = angle;
		final int steps = Constants.INTERMEDIATE_STEPS;

		for (int i = 0; i != steps; i++) {
			// add 1/n of 90 degrees per step
			// if dir is -1 then subtract instead of addition
			angle = originalAngle + dir*(90*(i+1))/steps; 
			rotateStep();
		}
		setCurrentDirection((int) Math.cos(radify(angle)), (int) Math.sin(radify(angle))) ;
		logPosition();
	}
	/**
	 * Moves in the given direction with 4 intermediate steps,
	 * updates the screen and the internal position
	 * @param dir
	 */
	synchronized private void walk(int dir) {
		if (!checkMove(dir))
			return;
		// walkStep is a parameter of the redraw method in FirstPersonDrawer
		// it is used there for scaling steps
		// so walkStep is implicitly used in slowedDownRedraw which triggers the redraw
		// operation on all listed viewers
		for (int step = 0; step != Constants.INTERMEDIATE_STEPS; step++) {
			walkStep += dir;
			slowedDownRedraw();
		}
		setCurrentPosition(px + dir*dx, py + dir*dy) ;
		walkStep = 0;
		logPosition();
	}
	
	/**
	 * Public exposure of {@link MazeController#rotate(int)} accepting a more clear argument
	 * @param turn enum value representing in which way to turn
	 */
	synchronized public void rotate(Robot.Turn turn){
		switch(turn){
		case LEFT:
//			System.out.println("Turning 'left' (appears right in GUI)");
//			System.out.println("Direction should be " + getCurrentDirection().rotateCounterCW());
			rotate(-1);
			break;
		case RIGHT:
//			System.out.println("Turning 'right' (appears left in GUI)");
//			System.out.println("Direction should be " + getCurrentDirection().rotateClockwise());
			rotate(1);
			break;
		case AROUND:
//			System.out.println("Turning around");
			rotate(-1);
			rotate(-1);
			break;
		default:
			System.err.println("MazeController#rotate: Unexpected turn enum value");
			break;
		}
		//System.out.println("Now direction is " + getCurrentDirection());
	}
	
	/**
	 * Public exposure of {@link MazeController#walk(int)} accepting a more clear argument
	 * @param backwards true to walk backwards; false to walk forwards
	 */
	synchronized public void walk(boolean backwards){
		if(backwards){
			walk(-1);
		}
		else{
			walk(1);
		}
	}

	/**
	 * checks if the given position is outside the maze
	 * @param x
	 * @param y
	 * @return true if position is outside, false otherwise
	 */
	private boolean isOutside(int x, int y) {
		return !mazeConfig.isValidPosition(x, y) ;
	}
	
	/**
	 * Checks if the current position is outside; if so, switches to finish screen.
	 */
	public void checkFinished(){
		if (isOutside(px,py)) {
			finish();
		}
	}

    // --------------------------- Handles for linkage with UI

    /**
     * Moves a step forward
     */
    synchronized public void forward(){
        walk(1);
        if(isOutside(px, py)){
            finish();
        }
    }

    /**
     * Moves a step backward
     */
    synchronized public void back(){
        walk(-1);
        if(isOutside(px, py)){
            finish();
        }
    }

    /**
     * Turns left
     */
    synchronized public void left(){
        rotate(Turn.RIGHT);
    }

    /**
     * Turns right
     */
    synchronized public void right(){
        rotate(Turn.LEFT);
    }

    /**
     * Gets the current status of map visibility
     * @return map visibility
     */
    public boolean isMapShown(){
        return mapMode;
    }

    /**
     * Toggles visibility of overview map
     */
    public void toggleShowMap(){
        mapMode = !mapMode;
        notifyViewerRedraw();
    }

    /**
     * Toggles visibility of unseen walls
     */
    public void toggleShowWalls(){
        showMaze = !showMaze;
        notifyViewerRedraw();
    }

    /**
     * Toggles visibility of solution line on overview
     */
    public void toggleShowSolution(){
        showSolution = !showSolution;
        notifyViewerRedraw();
    }

	////////// set methods for fields ////////////////////////////////
	
	protected void setupForUse(MazeConfiguration mazeConfig){
		// adjust internal state of maze model
		// visibility settings
		showMaze = false ;
		showSolution = false ;
		mapMode = false;
		// init data structure for visible walls
		seencells = new Cells(mazeConfig.getWidth()+1,mazeConfig.getHeight()+1) ;
		// obtain starting position
		int[] start = mazeConfig.getStartingPosition() ;
		setCurrentPosition(start[0],start[1]) ;
		// set current view direction and angle
		setCurrentDirection(1, 0) ; // east direction
		viewdx = dx<<16; 
		viewdy = dy<<16;
		angle = 0; // angle matches with east direction, hidden consistency constraint!
		walkStep = 0; // counts incremental steps during move/rotate operation
		
//		if(driver != null && driver instanceof BasicRobotDriver){
//			((BasicRobotDriver)driver).reset();
//			((BasicRobotDriver)driver).setupUsingMaze(this);
//			((BasicRobot)((BasicRobotDriver)driver).robot).reset();
//		}
	}
}
