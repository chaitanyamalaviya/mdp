package com.mdp.grp2.batmobot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mdp.grp2.batmobot.R;

import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends Activity implements View.OnClickListener {

    // Debugging
    private static final String TAG = "SettingsActivity";
    private static final boolean D = true;

    private Button saveF1;
    private EditText cmdF1;

    private Button saveF2;
    private EditText cmdF2;

    //private Button motionFunction;
    public CheckBox motionFunctionChkBx;
    public byte phoneX, phoneY;
    public boolean phoneMotion = false;


    // Instance of MainActivity
    private MainActivity mainActivityObj = null;

    public static String filename = "Commands";
    SharedPreferences cmdString;

    Context context;

    /*// Constructor
    public SettingsActivity(Context context){
        this.context = context;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setupVariables();
        cmdString = getSharedPreferences(filename, 0);
    }

    private void setupVariables() {

        saveF1 = (Button) findViewById(R.id.btnSaveF1);
        cmdF1 = (EditText) findViewById(R.id.etCommandF1);
        saveF2 = (Button) findViewById(R.id.btnSaveF2);
        cmdF2 = (EditText) findViewById(R.id.etCommandF2);

        motionFunctionChkBx = (CheckBox) findViewById(R.id.motionFnCheckBox);

        loadSharedPref();

        saveF1.setOnClickListener(this);
        saveF2.setOnClickListener(this);

        /*motionFunction = (Button) findViewById(R.id.motionFunction);
        motionFunction.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                phoneMotion = !phoneMotion;
                if (phoneMotion) {
                    phoneMotion();
                    robotStatus.setText("Phone Motion enabled");
                } else {
                    //status.setText("Phone Motion disabled");
                }
            }
        });*/

        addListenerOnChkMF();


    }

    public void addListenerOnChkMF() {

        motionFunctionChkBx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkMF checked?

                if (((CheckBox) v).isChecked()) {
                    //phoneMotion();

                    saveChkBox();
                    Toast.makeText(getApplicationContext(), "Phone Motion Enabled", Toast.LENGTH_SHORT).show();

                    //Intent intentMessage=new Intent();
                    // put the message to return as result in Intent
                    phoneMotion = true;
                    getIntent().putExtra("pmState", phoneMotion);



                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();

                } else {
                    saveChkBox();
                    phoneMotion = false;
                    getIntent().putExtra("pmState", phoneMotion);
                    Toast.makeText(getApplicationContext(), "Phone Motion Disabled", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void loadSharedPref() {
        cmdString = getSharedPreferences(filename, 0);

        String retrieveCmdF1 = cmdString.getString("sharedStringF1", "");
        cmdF1.setText(retrieveCmdF1);

        String retrieveCmdF2 = cmdString.getString("sharedStringF2", "");
        cmdF2.setText(retrieveCmdF2);

        boolean checkBoxValue = cmdString.getBoolean("CheckBox_Value", false);
        motionFunctionChkBx.setChecked(checkBoxValue);
        phoneMotion = checkBoxValue;

        //Toast.makeText(getApplicationContext(),
          //      motionFunctionChkBx.isChecked() + " Saved PM" + phoneMotion, Toast.LENGTH_LONG)
            //    .show();
    }

    public void saveCmdF1() {
        String stringDataF1 = cmdF1.getText().toString();
        SharedPreferences.Editor editor = cmdString.edit();
        editor.putString("sharedStringF1", stringDataF1);
        editor.commit();
    }

    public void saveCmdF2() {
        String stringDataF2 = cmdF2.getText().toString();
        SharedPreferences.Editor editor = cmdString.edit();
        editor.putString("sharedStringF2", stringDataF2);
        editor.commit();
    }

    private void saveChkBox() {
        Boolean boolChkBoxData = motionFunctionChkBx.isChecked();
        SharedPreferences.Editor editor = cmdString.edit();
        editor.putBoolean("CheckBox_Value", boolChkBoxData);
        editor.commit();
        //Toast.makeText(getApplicationContext(),
          //      motionFunctionChkBx.isChecked() + " Saved pm " + phoneMotion, Toast.LENGTH_LONG)
            //    .show();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnSaveF1:
                saveCmdF1();
                Toast.makeText(getApplicationContext(),
                        cmdF1.getText().toString() + " Saved", Toast.LENGTH_LONG)
                        .show();
                break;

            case R.id.btnSaveF2:
                saveCmdF2();
                Toast.makeText(getApplicationContext(),
                        cmdF2.getText().toString() + " Saved", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

