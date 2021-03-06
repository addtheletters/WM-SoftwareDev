package edu.wm.cs.cs301.benzhang.amazebybenzhang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {

    private static final String TAG = "FinishActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        String driver = getIntentDriver();
        float energyUsed = getIntentEnergy();
        int stepsTaken = getIntentSteps();
        String fail = getIntentFailure();

        Log.v(TAG, "Displaying finish result text depending upon intent extras.");

        if(fail == null || fail.length() == 0) {
            Log.v(TAG, "Maze was completed successfully.");

            // successfully made it to end of maze
            if (driver == null || driver.length() == 0 || driver.equals("Manual")) {
                setMainText(getString(R.string.you_win));
                setFirstSubText("");
                if (energyUsed > 0) {
                    setSecondSubText("Used " + energyUsed + " energy.");
                }
                else{
                    setSecondSubText(getString(R.string.good_job));
                }
            } else {
                setMainText(getString(R.string.robot_win));
                setFirstSubText("Driver was " + driver);
                if (energyUsed > 0) {
                    setSecondSubText("Used " + energyUsed + " energy.");
                }
                else{
                    setSecondSubText(getString(R.string.yay));
                }
            }
        }
        else{
            Log.v(TAG, "Maze was failed.");

            // failure occurred
            if (driver == null || driver.length() == 0 || driver.equals("Manual")) {
                setMainText(getString(R.string.you_lose));
            } else {
                setMainText(getString(R.string.robot_lose));
            }
            setFirstSubText(fail);
            if (energyUsed > 0) {
                setSecondSubText("Used " + energyUsed + " energy.");
            }
            else{
                setSecondSubText(getString(R.string.too_bad));
            }
        }
        if(stepsTaken > 0){
            setThirdSubText("Took " + stepsTaken + " steps.");
        }
        else{
            setThirdSubText("");
        }
    }

    /**
     * Sets the content of the main text view of the finish activity.
     * @param text the text to display
     */
    protected void setMainText(String text){
        final TextView display = (TextView) findViewById(R.id.finish_main_text);
        display.setText(text);
    }

    /**
     * Sets the content of the upper secondary text view of the finish activity.
     * @param text the text to display
     */
    protected void setFirstSubText(String text){
        final TextView display = (TextView) findViewById(R.id.finish_sub_text);
        display.setText(text);
    }

    /**
     * Sets the content of the middle secondary view of the finish activity.
     * @param text the text to display
     */
    protected void setSecondSubText(String text){
        final TextView display = (TextView) findViewById(R.id.finish_second_sub_text);
        display.setText(text);
    }

    /**
     * Sets the content of the bottom secondary view of the finish activity.
     * @param text the text to display
     */
    protected void setThirdSubText(String text){
        final TextView display = (TextView) findViewById(R.id.finish_third_sub_text);
        display.setText(text);
    }

    /**
     * Gets the driver as set in the creating Intent.
     * @return string representing the driver
     */
    protected String getIntentDriver(){
        return getIntent().getStringExtra(AMazeActivity.DRIVER);
    }

    /**
     * Gets the energy used as set in the creating Intent.
     * @return float representing the amount of energy used
     */
    protected float getIntentEnergy(){ return getIntent().getFloatExtra(PlayActivity.FINISH_ENERGY, 0f); }

    /**
     * Gets the reason for failure (if failed) as set in the creating Intent.
     * @return string representing why failure occurred
     */
    protected String getIntentFailure(){ return getIntent().getStringExtra(PlayActivity.FAILURE_REASON); }

    /**
     * Gets the steps taken, as set in creating Intent.
     * @return number of steps taken, 0 if none provided
     */
    protected int getIntentSteps(){ return getIntent().getIntExtra(PlayActivity.FINISH_STEPS, 0); }

}
