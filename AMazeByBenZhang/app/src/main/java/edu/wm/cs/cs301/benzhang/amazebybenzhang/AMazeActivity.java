package edu.wm.cs.cs301.benzhang.amazebybenzhang;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.MazeFileReader;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.MazeFileWriter;
import edu.wm.cs.cs301.benzhang.amazebybenzhang.generation.Order;

public class AMazeActivity extends AppCompatActivity {

    private static final String TAG = "AMazeActivity";

//    private Button generateBtn;
//    private Button loadBtn;
//    private SeekBar complexityBar;
//    private Spinner generatorSpn;
//    private Spinner driverSpn;

    public static final String COMPLEXITY = "maze.key.start.complexity";
    public static final String GENERATOR = "maze.key.start.generator";
    public static final String DRIVER = "maze.key.driver";
    public static final String LOAD_EXISTING = "maze.key.start.load";

    public static final String FILENAME = "maze.key.start.filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaze);

        // configure complexity slider
        final SeekBar complexityBar = (SeekBar) findViewById(R.id.complexity_seekbar);
        complexityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setComplexityDisplayNum( i );
                updateLoadButtonStatus();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {/* no effect */}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.v(TAG, "Registered 'complexity' value changed.");
//                Toast.makeText(AMazeActivity.this, "Registered 'complexity' value changed.", Toast.LENGTH_SHORT).show();
            }
        });

        // configure spinner for drivers
        final Spinner driverSpn = (Spinner) findViewById(R.id.driver_selector);
        ArrayAdapter<CharSequence> driveAdapter = ArrayAdapter.createFromResource(this, R.array.driver_options, R.layout.spinner_text_choice);
        driveAdapter.setDropDownViewResource(R.layout.spinner_text_choice);
        driverSpn.setAdapter(driveAdapter);
        driverSpn.setOnItemSelectedListener(new DebugSpinnerListener("driver"));

        // configure spinner for generators
        final Spinner generatorSpn = (Spinner) findViewById(R.id.generator_selector);
        ArrayAdapter<CharSequence> genAdapter = ArrayAdapter.createFromResource(this, R.array.generator_options, R.layout.spinner_text_choice);
        genAdapter.setDropDownViewResource(R.layout.spinner_text_choice);
        generatorSpn.setAdapter(genAdapter);
        generatorSpn.setOnItemSelectedListener(new DebugSpinnerListener("generator"){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                super.onItemSelected(adapterView, view, i, l);
                updateLoadButtonStatus();
            }
        });

        // configure buttons
        final Button generateBtn = (Button) findViewById(R.id.generate_button);
        final Button loadBtn = (Button) findViewById(R.id.load_button);

        generateBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // generate
                Log.v(TAG, "Registered 'generate' button pressed.");
                //Toast.makeText(AMazeActivity.this, "Registered 'generate' button pressed.", Toast.LENGTH_SHORT).show();
                if(canLoad()){
                    createGenerateConfirmDialog();
                }
                else {
                    switchActivityGenerate(false);
                }
            }
        });
        loadBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // load
                Log.v(TAG, "Registered 'load' button pressed.");
                //Toast.makeText(AMazeActivity.this, "Registered 'load' button pressed.", Toast.LENGTH_SHORT).show();
                if(getComplexity() > 3) {
                    createLoadConfirmDialog();
                }
                else{
                    switchActivityGenerate(true);
                }
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        updateLoadButtonStatus();
        setComplexityDisplayNum( getComplexity() );
    }

    /**
     * Brings up dialog confirming an overwrite of that maze with new generation
     */
    private void createGenerateConfirmDialog() {
        Log.v(TAG, "Requested generate when load was available, showing confirmation dialog");
        new AlertDialog.Builder(this)
                .setMessage("Generate and overwrite existing maze?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchActivityGenerate(false);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Brings up dialog confirming desire to load a maze despite large time requirement
     */
    private void createLoadConfirmDialog(){
        Log.v(TAG, "Requested load of large complexity, showing confirmation dialog");
        new AlertDialog.Builder(this)
                .setMessage("Loading complex mazes takes a long time (several minutes or more). Continue?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchActivityGenerate(true);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Listener for debug purposes, logging value changes.
     */
    private class DebugSpinnerListener implements Spinner.OnItemSelectedListener{
        String name;
        public DebugSpinnerListener(String name){
            this.name = name;
        }
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.v(TAG, "Registered '" + name + "' value changed.");
            //Toast.makeText(AMazeActivity.this, "Registered '"+ name +"' value changed.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {/* no effect */}
    }

    /**
     * Switches to the generation activity, passing information provided by GUI settings.
     * @param load true to load from previously created file (if possible), false to generate new
     */
    protected void switchActivityGenerate(boolean load){
        Log.v(TAG, "Switching to generating activity.");
        Intent genIntent = new Intent(this, GeneratingActivity.class);
        Bundle genInfo = new Bundle();
        genInfo.putInt(COMPLEXITY, getComplexity());
        genInfo.putString(GENERATOR, getGenerator());
        genInfo.putString(DRIVER, getDriver());
        genInfo.putString(FILENAME, MazeFileWriter.autoFilename(getComplexity(), Order.Builder.parseBuilder(getGenerator())));
        if(load){
            if(!canLoad()){
                Log.e(TAG, "Tried to load when it wasn't possible (no existing file?)");
            }
            genInfo.putBoolean(LOAD_EXISTING, true);

        }
        genIntent.putExtras(genInfo);
        startActivity(genIntent);
    }

    /**
     * Toasts displaying current maze generation settings provided by GUI settings.
     */
    protected void logCurrentSettings(){
        Toast.makeText(this, "C:" + getComplexity() + ",D:" + getDriver() + ",G:" + getGenerator(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the display number beside the complexity selection slider to a number.
     * @param i the number to display
     */
    protected void setComplexityDisplayNum(int i) {
        final TextView complexityDisplay = (TextView) findViewById(R.id.complexity_display_num);
        complexityDisplay.setText(String.format("%2d", i));
    }



    protected boolean canLoad(){
        File possibleFile = MazeFileReader.getFile(this, MazeFileWriter.autoFilename(getComplexity(), Order.Builder.parseBuilder(getGenerator())));
        return possibleFile.isFile() && possibleFile.canRead();
    }


    /**
     * Enables or disables the 'load' button based on availability of maze of current settings.
     */
    protected void updateLoadButtonStatus(){
        boolean fileExists = canLoad();
        final Button loadButton = (Button) findViewById(R.id.load_button);
        loadButton.setEnabled(fileExists);
        if(fileExists){
            Log.v(TAG, "Enabling load, file for settings exists.");
        }
    }

    /**
     * Gets the complexity set by the UI element.
     * @return complexity provided by user setting
     */
    protected int getComplexity(){
        final SeekBar complexityBar = (SeekBar) findViewById(R.id.complexity_seekbar);
        return complexityBar.getProgress();
    }

    /**
     * Gets the driver setting from the UI element.
     * @return string representing the driver selected by the user
     */
    protected String getDriver(){
        final Spinner driverSpn = (Spinner) findViewById(R.id.driver_selector);
        return driverSpn.getSelectedItem().toString();
    }

    /**
     * Gets the generator setting from the UI element.
     * @return string representing the generator selected by the user
     */
    protected String getGenerator(){
        final Spinner generatorSpn = (Spinner) findViewById(R.id.generator_selector);
        return generatorSpn.getSelectedItem().toString();
    }
}
