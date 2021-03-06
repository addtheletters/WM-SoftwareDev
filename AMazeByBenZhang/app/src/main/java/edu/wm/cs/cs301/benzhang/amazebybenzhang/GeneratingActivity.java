package edu.wm.cs.cs301.benzhang.amazebybenzhang;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.MazeFileReader;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.MazeFileWriter;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeConfiguration;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.MazeFactory;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Order;

public class GeneratingActivity extends AppCompatActivity {

    private static final String TAG = "GeneratingActivity";

    private static final String ABORT_STATE = "maze.key.save.abort";

    public Handler delayer = new Handler();
    private static MazeFactory factory; // static so rotation changes do not destroy
    private ActivitySyncOrder order;

    private AlertDialog cancelDialog;

    private boolean aborted = false;

    //private int progress = 0;
    //private SimulatedBuildProgress buildTracker;
    //private static final int PROGRESS_UPDATE_DELAY = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generating);
        if(savedInstanceState == null) {
            startGeneration();
        }
        else{
            Log.v(TAG, "skipped restarting generation (saved instance state found)");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ABORT_STATE, aborted);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        aborted = savedInstanceState.getBoolean(ABORT_STATE, false);
    }

    /**
     * Adds dialog requesting confirmation of abort when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "Back button pressed, creating cancel dialog");
        cancelDialog = new AlertDialog.Builder(this)
                .setMessage("Abort loading?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delayer.removeCallbacksAndMessages(null); // cancel any simulated operations
                        if(order != null) {
                            getApplication().unregisterActivityLifecycleCallbacks(order);
                        }
                        if(factory != null) {
                            factory.cancel(); // cancel factory if running
                            Log.v(TAG, "Cancelled factory order.");
                        }
                        aborted = true;
                        GeneratingActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .create();
        cancelDialog.show();
    }


    /**
     * Starts generation of maze using settings given through the creating intent
     */
    protected void startGeneration(){
        // note: generator or could not matter, if loading maze from a file
        //Toast.makeText(this, "Starting c:" + complexity + ",g:" + generator + ",d:" + driver, Toast.LENGTH_SHORT).show();
        boolean loadWorked = false;
        if(getIntentLoad()){
            try{
                Log.v(TAG, "Trying to load from file.");
                load();
                loadWorked = true;
            }
            catch(Exception e){
                Log.v(TAG, "Failed to load from file.");
                Toast.makeText(GeneratingActivity.this, "Failed to load from file. Falling back to generation.", Toast.LENGTH_SHORT).show();
            }
        }

        if(!loadWorked) {
            Log.v(TAG, "Not loaded from file, generating new maze");
            build();
        }
    }

    /**
     * Simulates creation of a maze occurring over some period of time, with updates to UI
     * @param opVerb verb to simulate using to create the maze
     * @param buildTime time in milliseconds for build to complete
     * @param buildChanges number of updates to make to the GUI during the time taken
     */
    private void simulateBuild(final String opVerb, int buildTime, int buildChanges){
        setLoadingCaption(opVerb + " maze...");
        for(int i = 0; i < buildTime; i+= buildTime / buildChanges){
            delayer.postDelayed(new SimulatedBuildProgress(100 * i / buildTime), i);
        }
        delayer.postDelayed(new Runnable(){
            @Override
            public void run() {
                // simulated 'done'
                setLoadingCaption("Done!");
                Toast.makeText(GeneratingActivity.this, "Finished " + opVerb + ".", Toast.LENGTH_SHORT).show();
                done();
            }
        }, buildTime);
    }

    /**
     * Loads maze from a file.
     */
    private void load(){ // maybe this should take a filename argument?
        Log.v(TAG, "Attempting to load.");
        setLoadingCaption("Loading maze...");
        new Thread(){
            @Override
            public void run() {
                try {
                    SingleMazeHolder.maze = MazeFileReader.loadFile(GeneratingActivity.this, getIntentFilename());
                    if(!aborted) {
                        done();
                    }
                }
                catch(Exception e){
                    Log.e(TAG, "Failed to load maze from file " + getIntentFilename() + ": " + e.getMessage());
                }
            }
        }.start();
    }

    private interface ActivitySyncOrder extends Order, Application.ActivityLifecycleCallbacks{

    }

    private void build(){
        setLoadingCaption("Generating maze...");
        if(factory == null) {
            factory = new MazeFactory();
        }
        Log.v(TAG, "Placing order.");
        order = new ActivitySyncOrder(){
            private Activity a;

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                a = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            @Override
            public int getSkillLevel() {
                return getIntentComplexity();
            }

            @Override
            public Builder getBuilder() {
                String gen = getIntentGenerator();
                Builder parsed = Order.Builder.parseBuilder(gen);
                return (parsed == null) ? Builder.DFS : parsed;
            }

            @Override
            public boolean isPerfect() {
                return false;
            }

            @Override
            public void deliver(MazeConfiguration mazeConfig) {
                Log.v(TAG, "Delivering result of order.");
                SingleMazeHolder.maze = mazeConfig;

                try {
                    MazeFileWriter.storeConfig(GeneratingActivity.this, getIntentFilename(), mazeConfig);
                }
                catch(Exception e){
                    Log.e(TAG, "Failed to store generated maze: " + e.getMessage());
                }

                if(!aborted) {
                    done();
                }
            }

            @Override
            public void updateProgress(final int percentage) {
                if(a instanceof GeneratingActivity) {
                    ((GeneratingActivity) a).delayer.post(new Runnable() {
                        @Override
                        public void run() {
                            if(a instanceof GeneratingActivity) {
                                ((GeneratingActivity) a).setLoadingCaption("Building BST... (" + percentage + "%)");
                            }
                        }
                    });
                }
            }
        };
        factory.order(order);
        getApplication().registerActivityLifecycleCallbacks(order);
    }

    /**
     * Runnable updating UI numbers.
     */
    private class SimulatedBuildProgress implements Runnable{
        int progress;
        public SimulatedBuildProgress(int progress){
            this.progress = progress;
        }
        @Override
        public void run() {
            setLoadingCaption("Building BST... (" + this.progress + "%)");
        }
    }

    /**
     * Sets the caption text below the loading icon
     * @param text the text to display
     */
    public void setLoadingCaption(String text){
        TextView captionBox = (TextView) findViewById(R.id.generating_caption);
        captionBox.setText(text);
    }

    /**
     * Starts the play activity. To be called when generation is complete.
     */
    protected void done(){
        Log.v(TAG, "Maze obtained.");
        //this.mazeInfo = new PlaceholderMaze(getIntentComplexity(), getIntentGenerator());
        if(cancelDialog != null) {
            cancelDialog.dismiss();
        }
        if(order != null){
            getApplication().unregisterActivityLifecycleCallbacks(order);
        }
        Intent playIntent = new Intent(this, PlayActivity.class);
        Bundle playInfo = new Bundle();
        // possibly pass info such as driver? could take care of this here when instantiating controller
        playInfo.putString(AMazeActivity.DRIVER, getIntentDriver());
        playIntent.putExtras(playInfo);
        startActivity(playIntent);
        finish();
    }

    /**
     * Gets the preference of whether to load from file if available from the creating Intent.
     * @return true if should attempt load from file; false otherwise.
     */
    protected boolean getIntentLoad(){
        return getIntent().getBooleanExtra(AMazeActivity.LOAD_EXISTING, false);
    }

    /**
     * Gets the maze complexity as set in the creating Intent.
     * @return integer representing the complexity
     */
    protected int getIntentComplexity(){
        return getIntent().getIntExtra(AMazeActivity.COMPLEXITY, 0);
    }

    /**
     * Gets the driver as set in the creating Intent.
     * @return string representing the driver
     */
    protected String getIntentDriver(){
        return getIntent().getStringExtra(AMazeActivity.DRIVER);
    }

    /**
     * Gets the generator as set in the creating Intent.
     * @return string representing the generator
     */
    protected String getIntentGenerator(){
        return getIntent().getStringExtra(AMazeActivity.GENERATOR);
    }

    /**
     * Gets the file name to load as set in the creating Intent.
     * @return string of the file name
     */
    private String getIntentFilename(){
        return getIntent().getStringExtra(AMazeActivity.FILENAME);
    }
}
