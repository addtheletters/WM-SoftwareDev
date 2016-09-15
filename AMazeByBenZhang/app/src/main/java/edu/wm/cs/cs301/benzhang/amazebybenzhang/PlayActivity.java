package edu.wm.cs.cs301.benzhang.amazebybenzhang;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.BasicRobot;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.BasicRobotDriver;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.MazeController;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.MazePanel;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Robot;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.RobotDriver;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.WallFollower;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Wizard;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeConfiguration;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";

    public static final String FINISH_ENERGY = "maze.key.finish.energy";
    public static final String FINISH_STEPS = "maze.key.finish.steps";
    public static final String FAILURE_REASON = "maze.key.finish.reason";

    /*
    private static final String MAZE_CONTROL_STATE = "maze.key.save.control";
    private static final String ROBOT_PARCEL_STATE = "maze.key.save.robot";
    private static final String ROBOT_DRIVER_STATE = "maze.key.save.driver";
    */

    private static BasicRobot robot;
    private static BasicRobotDriver driver;
    private static MazeController controller;

    public Handler handle = new Handler();
    //private Handler controllerHandle;

    private AlertDialog quitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        MazePanel panel = (MazePanel) this.findViewById(R.id.play_area_view);
        if(savedInstanceState == null) {
            Log.v(TAG, "Setting up controller, robot, and driver.");
            //maze = GeneratingActivity.mazeInfo;
            controller = new MazeController(SingleMazeHolder.maze, this, panel);
            driver = makeDriver(getIntentDriver());
        }else{
            // else, handle in onRestoreInstanceState
            controller.reinit(panel, this);
        }
//        if(driver != null) {
//            controller.setDriver(driver);
//        }

        // configure map control buttons
        final Button showMapBtn = (Button)findViewById(R.id.play_show_map_button);
        showMapBtn.setOnClickListener(new DebugButtonListener("show map"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                controller.toggleShowMap();
                updateMapButtonStatus();
            }
        });
        final Button showWallsBtn = (Button)findViewById(R.id.play_show_walls_button);
        showWallsBtn.setOnClickListener(new DebugButtonListener("show walls"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                controller.toggleShowWalls();
            }
        });
        final Button showSolutionBtn = (Button)findViewById(R.id.play_show_solution_button);
        showSolutionBtn.setOnClickListener(new DebugButtonListener("show solution"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                controller.toggleShowSolution();
            }
        });
        final Button zoomInBtn = (Button)findViewById(R.id.play_zoom_in_button);
        zoomInBtn.setOnClickListener(new DebugButtonListener("zoom in"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                controller.notifyViewerIncrementMapScale(10);
            }
        });
        final Button zoomOutBtn = (Button)findViewById(R.id.play_zoom_out_button);
        zoomOutBtn.setOnClickListener(new DebugButtonListener("zoom out"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                controller.notifyViewerDecrementMapScale(10);
            }
        });

        // configure move buttons
        final Button forwardBtn = (Button) findViewById(R.id.play_forward_move_button);
        forwardBtn.setOnClickListener(new DebugButtonListener("move forward"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                new Thread(){
                    public void run(){
                        robot.move(1, true);
                        checkRobotOK();
                    }
                }.start();
            }
        });
        final Button backBtn = (Button) findViewById(R.id.play_back_move_button);
        backBtn.setOnClickListener(new DebugButtonListener("move back"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                //controller.walk(true);
                new Thread(){
                    public void run(){
                        robot.move(-1, true);
                        checkRobotOK();
                    }
                }.start();
            }
        });
        final Button leftBtn = (Button) findViewById(R.id.play_left_move_button);
        leftBtn.setOnClickListener(new DebugButtonListener("turn left"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                new Thread(){
                    public void run(){
                        robot.rotate(Robot.Turn.RIGHT);
                        checkRobotOK();
                    }
                }.start();
            }
        });
        final Button rightBtn = (Button) findViewById(R.id.play_right_move_button);
        rightBtn.setOnClickListener(new DebugButtonListener("turn right"){
            @Override
            public void onClick(View view) {
                super.onClick(view);
                new Thread(){
                    public void run(){
                        robot.rotate(Robot.Turn.LEFT);
                        checkRobotOK();
                    }
                }.start();
            }
        });

        setEnergyBarLevel(100);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(savedInstanceState == null) {
            if(controller == null){
                return;
            }
            Thread controllerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    controller.init();
                }
            });
            Log.v(TAG, "Starting maze controller thread.");
            controllerThread.start();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putParcelable(MAZE_CONTROL_STATE, controller);
//        outState.putParcelable(ROBOT_PARCEL_STATE, robot);
//        outState.putString(ROBOT_DRIVER_STATE, driver.getName());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        controller = savedInstanceState.getParcelable(MAZE_CONTROL_STATE);
//        if(controller != null) {
//            controller.reattach(SingleMazeHolder.maze, this,(MazePanel)this.findViewById(R.id.play_area_view));
//        }
//        else{
//            Log.e(TAG, "Controller was null after restoring instance state! Why?");
//            controller = new MazeController(SingleMazeHolder.maze, this, (MazePanel)this.findViewById(R.id.play_area_view));
//        }
//        robot = savedInstanceState.getParcelable(ROBOT_PARCEL_STATE);
//        driver = makeDriver(savedInstanceState.getString(ROBOT_DRIVER_STATE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMapButtonStatus();
    }

    /**
     * Gets android View visibility which can be set for buttons
     * @param mapVisible the visibility status of the map
     * @return integer constant representing the view visibility
     */
    private void setVisibility(Button btn, boolean mapVisible){
        if(mapVisible){
            btn.setVisibility(View.VISIBLE);
        }
        else{
            btn.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Enables or disables map buttons depending on whether the map is currently being shown
     */
    private void updateMapButtonStatus(){
        boolean shouldEnable = controller.isMapShown();
        final Button showWallsBtn = (Button)findViewById(R.id.play_show_walls_button);
        setVisibility(showWallsBtn, shouldEnable);
        //showWallsBtn.setEnabled(shouldEnable);
        final Button showSolutionBtn = (Button)findViewById(R.id.play_show_solution_button);
        setVisibility(showSolutionBtn, shouldEnable);
        //showSolutionBtn.setEnabled(shouldEnable);
        final Button zoomInBtn = (Button)findViewById(R.id.play_zoom_in_button);
        setVisibility(zoomInBtn, shouldEnable);
        //zoomInBtn.setEnabled(shouldEnable);
        final Button zoomOutBtn = (Button)findViewById(R.id.play_zoom_out_button);
        setVisibility(zoomOutBtn, shouldEnable);
        //zoomOutBtn.setEnabled(shouldEnable);
    }

    /**
     * Make sure that the robot hasn't stopped after manual operations.
     * If it has, recognize that the maze has been failed.
     */
    private void checkRobotOK(){
        if(robot != null){
            if(robot.hasStopped()){
                handle.post(new Runnable() {
                    @Override
                    public void run() {
                        mazeFinished(true, "Robot stopped before exit reached.");
                    }
                });
            }
        }
    }

    /**
     * Gets the driver as set in the creating Intent.
     * @return string representing the driver
     */
    protected String getIntentDriver(){
        return getIntent().getStringExtra(AMazeActivity.DRIVER);
    }

    /**
     * Produce instance for the driver specified by string description, link with references
     * @return driver if one matches description, null if manual / none matches
     */
    protected BasicRobotDriver makeDriver(String driverName){
        if(robot == null) {
            robot = new BasicRobot();
        }
        robot.setMaze(controller);
        BasicRobotDriver d = null;
        if(driverName.equalsIgnoreCase("WallFollower")){
            d = new WallFollower();
        }
        else if(driverName.equalsIgnoreCase("Wizard")){
            d = new Wizard();
        }
        if(d != null) {
            d.setRobot(robot);
            if(controller != null){
                controller.setDriver(d);
            }
        }
        return d;
    }

    /**
     * Wraps up, stops robots running
     */
    private void halt(){
        Log.v(TAG, "Halting, killing robot");
        if(driver != null) {
            driver.kill();
        }
    }

    /**
     * Adds dialog requesting confirmation of quit when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "Back pressed, creating quit dialog");
        quitDialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PlayActivity.this.finish();
                        halt();
                    }
                })
                .setNegativeButton("No", null)
                .create();
        quitDialog.show();
    }

    /**
     * Wraps up and switches to finished activity.
     * @param failed true if failed to reach exit; false if successfully completed maze
     */
    public void mazeFinished(boolean failed, String failureReason){
        Log.v(TAG, "Maze was finished. Failure state is " + failed + (failed ? (", for reason:"+failureReason) : "") );
        if(quitDialog != null){
            quitDialog.dismiss();
        }
        Toast.makeText(PlayActivity.this, "Finished maze.", Toast.LENGTH_SHORT).show();
        Intent finishIntent = new Intent(this, FinishActivity.class);
        Bundle finishInfo = new Bundle();
        if(driver != null) {
            finishInfo.putFloat(FINISH_ENERGY, driver.getEnergyConsumption());
            finishInfo.putInt(FINISH_STEPS, driver.getPathLength());
            finishInfo.putString(AMazeActivity.DRIVER, driver.getClass().toString());
        }
        else if(robot != null){
            finishInfo.putFloat(FINISH_ENERGY, BasicRobot.INITIAL_BATTERY - robot.getBatteryLevel());
            finishInfo.putInt(FINISH_STEPS, robot.getOdometer());
        }
        if(failed){
            finishInfo.putString(FAILURE_REASON, failureReason);
        }
        finishIntent.putExtras(finishInfo);
        startActivity(finishIntent);
        finish();
    }

    /**
     * Sets the UI bar displaying energy remaining.
     * @param percentage the percent to set the bar to reflect, [0,100]
     */
    public void setEnergyBarLevel(int percentage){
        ProgressBar bar = (ProgressBar) findViewById(R.id.play_energy_bar);
        bar.setProgress(percentage);
    }

    /**
     * Gets the progress of the UI bar displaying remaining energy.
     * @return the percentage of the bar that is filled
     */
    private int getEnergyBarLevel(){
        ProgressBar bar = (ProgressBar) findViewById(R.id.play_energy_bar);
        return bar.getProgress();
    }

    /**
     * Listener for debug use in verifying that button inputs are registered.
     */
    private class DebugButtonListener implements View.OnClickListener, View.OnLongClickListener{
        private String name;
        public DebugButtonListener(String name){
            this.name = name;
        }
        @Override
        public void onClick(View view) {
            Log.v(TAG, "Registered '" + name + "' button pressed. Taking appropriate action.");
        }

        @Override
        public boolean onLongClick(View view) {
            this.onClick(view);
            return true;
        }
    }
}
