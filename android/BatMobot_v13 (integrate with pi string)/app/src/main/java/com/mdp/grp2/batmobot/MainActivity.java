/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdp.grp2.batmobot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the main Activity.
 */
public class MainActivity extends Activity implements SensorEventListener, View.OnTouchListener {
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
//    public static final int MESSAGE_SAVE_DEVICE = 6;

    public static final int UPDATE_GRID = 7;


    // Pref keys
//    private static final String PREFS_LAST_DEVICE = "LastDevice";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_PHONE_MOTION = 3;

    //button press colors
    private  static final int FILTERED_GREY = Color.argb(155,185,185,185);
    private static final int TRANSPARENT_GREY = Color.argb(0,185,185,185);

    // Layout Views
    private Button createRbtn;
    private ImageButton upArrBtn;
    private ImageButton downArrBtn;
    private ImageButton rightArrBtn;
    private ImageButton leftArrBtn;
    private ImageButton exploreBtn;
    private ImageButton sPathBtn;

    //private ImageButton settingsBtn;
    private Switch switch_AM;
    private boolean switchAMStatus;
    private ImageButton updateBtn;

    public TextView robotStatus;

    private boolean autoMode = true;
    private boolean updateSelected = false;

    // Instance of Non-Activity Classes
    private RobotPosition robotPos = new RobotPosition(MainActivity.this);
    private MapGridView drawGridMap;
    private String[] gridMapArr;


    private ListView mConversationView;

    //sensor
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float gravity[];
    public byte phoneX, phoneY;
    public boolean send = false;
    public float[] linear_acceleration;
    public Boolean sensorTrigger, phoneMotion;

    private final int SIMPLE_NOTFICATION_ID = 1;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

//    private NotificationManager mNotificationManager;

//    private SharedPreferences prefs;
//    private Editor prefsEditor;

    private Toast customToast;

    //int[X][Y] <-- 2D array X-rows Y-cols
    //public int[][] robothead = new int[10][9], robotcentre = new int[10][8];

    public int[] robothead = new int[]{10,9}, robotcentre = new int[]{10,8};
    public int[] robotPartX = new int[]{0,0,0,0,0,0,0,0,0};
    public int[] robotPartY = new int[]{0,0,0,0,0,0,0,0,0};
    public String gridBuff;

    public void robotPos(){
        // 0 1 2
        // 3 4 5
        // 6 7 8
        robotPartX[0] = robotcentre[0] - 1;
        robotPartY[0] = robotcentre[1] + 1;

        robotPartX[1] = robotcentre[0];
        robotPartY[1] = robotcentre[1] + 1;

        robotPartX[2] = robotcentre[0] + 1;
        robotPartY[2] = robotcentre[1] + 1;

        robotPartX[3] = robotcentre[0] - 1;
        robotPartY[3] = robotcentre[1];

        robotPartX[4] = robotcentre[0];
        robotPartY[4] = robotcentre[1];

        robotPartX[5] = robotcentre[0] + 1;
        robotPartY[5] = robotcentre[1];

        robotPartX[6] = robotcentre[0] - 1;
        robotPartY[6] = robotcentre[1] -1;

        robotPartX[7] = robotcentre[0];
        robotPartY[7] = robotcentre[1] -1;

        robotPartX[8] = robotcentre[0] + 1;
        robotPartY[8] = robotcentre[1] -1;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefsEditor = prefs.edit();
        if (D) Log.e(TAG, "+++ ON CREATE +++");


        // Set up the window layout
        setContentView(R.layout.activity_main);

        initializeUiControls();

        //setup sensor
        sensorSetup();

        //Set up bluetooth connection once app launch
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Log.i("FNORD", "mNotificationManager: " + mNotificationManager);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private void sensorSetup(){
        gravity = new float[3];
        gravity[0] = 0;
        gravity[1] = 0;
        gravity[2] = 0;

        // Initializing the accelerometer stuff
        // Register this as SensorEventListener
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_GAME);

    }

    private void initializeUiControls(){
        /* Initialise MapGridView and Robot*/
        //Grid and Robot on start
        blankGridMapView();
//        robotPos();
//        createRobotPart(robotPartX, robotPartY);
//        createRobotHead(robothead[0] , robothead[1]);
//        createRobotBody(robotcentre[0], robotcentre[1]);


        //createRobot(2, 2, 2, 2);



        /** set UI controls **/
        //createRbtn = (Button) findViewById(R.id.btnCreateR);
        upArrBtn = (ImageButton) findViewById(R.id.btnUp);
        downArrBtn = (ImageButton) findViewById(R.id.btnDown);
        rightArrBtn = (ImageButton) findViewById(R.id.btnRight);
        leftArrBtn = (ImageButton) findViewById(R.id.btnLeft);
        exploreBtn = (ImageButton) findViewById(R.id.btnF1);
        sPathBtn = (ImageButton) findViewById(R.id.btnF2);


        //ImageButton settingsBtn = (ImageButton) findViewById(R.id.btnSetting);
        switch_AM = (Switch) findViewById(R.id.switch_auto_manual);
        updateBtn = (ImageButton) findViewById(R.id.btnUpdate);
        updateBtn.setVisibility(View.INVISIBLE);


        robotStatus = (TextView) findViewById(R.id.tvStatus);


        /** UI controls listeners **/

        //robot start position

        /*createRbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                blankGridMapView();
                createRobotPart(robotPartX, robotPartY);
                createRobotHead(robothead[0], robothead[1]);
                createRobotBody(robotcentre[0], robotcentre[1]);
                sendMessage("start " + "head x:" + robothead[0] + "head y:" + robothead[1]);
                sendMessage("start " + "body x:" + robotcentre[0] + "body y:" + robotcentre[1]);
                sendMessage("start " + "part x:" + robotPartX[0] + "part y:" + robotPartY[1]);

            }
        });
        */



        //up arrow
        btnEffect(upArrBtn);
        upArrBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                //moveRobotinMap(UP);
                triggerUp();

            }
        });

        //down arrow
        btnEffect(downArrBtn);
        downArrBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                triggerDown();

            }
        });

        //right arrow
        btnEffect(rightArrBtn);
        rightArrBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                triggerRight();
            }
        });

        //left arrow
        btnEffect(leftArrBtn);
        leftArrBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                triggerLeft();


            }
        });


        //explore
        btnEffect(exploreBtn);
        exploreBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                sendMessage ("PA:XP");
                robotStatus.setText("Explore Started");

            }
        });

        //shortest path
        btnEffect(sPathBtn);
        sPathBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                sendMessage ("PA:XS");
                robotStatus.setText("SP Started");

            }
        });



        switch_AM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    updateBtn.setVisibility(View.INVISIBLE);
                    //map will be auto updated
                    autoMode = true;

                    customToast = Toast.makeText(getApplicationContext(), "Auto Update Mode", Toast.LENGTH_SHORT);
                    customToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                    customToast.show();
                    sendGrid();






                    // Sends request to turn on continuous feedback on gridmap
                    //sendMessage("");

                }else{
                    //update btn visible
                    updateBtn.setVisibility(View.VISIBLE);
                    updateBtn.setClickable(true);
                    //update map manually
                    autoMode = false;
                    updateSelected = false;

                    customToast = Toast.makeText(getApplicationContext(), "Manual Update Mode", Toast.LENGTH_SHORT);
                    customToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                    customToast.show();


                    // Sends request to turn off continuous feedback of gridmap
                    //sendMessage("");

                }
            }
        });
        if (switch_AM.isChecked()){
            updateBtn.setVisibility(View.INVISIBLE);
            //map will be auto updated
            switchAMStatus = true;
            autoMode = true;

            customToast = Toast.makeText(getApplicationContext(), "Auto Update Mode", Toast.LENGTH_SHORT);
            customToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            customToast.show();





        }else{
            //update btn visible
            updateBtn.setVisibility(View.VISIBLE);
            updateBtn.setClickable(true);
            //update map manually
            switchAMStatus = false;
            autoMode = false;
            updateSelected = false;

            customToast = Toast.makeText(getApplicationContext(), "Manual Update Mode", Toast.LENGTH_SHORT);
            customToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            customToast.show();
        }

        //update btn when in manual mode
            updateBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    sendMessage("PA:GRID");
                    //sendMessage("GRID");
//                    initGridMapView(gridMapArr);
//                    robotStatus.setText("Grid Updated");

                }
            });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) {
                setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        if (switch_AM.isChecked()){
            updateBtn.setVisibility(View.INVISIBLE);
            //map will be auto updated
            autoMode = true;

            customToast = Toast.makeText(getApplicationContext(), "Auto Update Mode", Toast.LENGTH_SHORT);
            customToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            customToast.show();

        }else{
            //update btn visible
            updateBtn.setVisibility(View.VISIBLE);
            //update map manually
            autoMode = false;
            updateSelected = false;

            customToast = Toast.makeText(getApplicationContext(), "Manual Update Mode", Toast.LENGTH_SHORT);
            customToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            customToast.show();
        }

//        mNotificationManager.cancel(SIMPLE_NOTFICATION_ID);

//        Log.i("FNORD", "" + getIntent());

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();

//                String address = prefs.getString(PREFS_LAST_DEVICE, null);
////                String address ="00:02:72:3F:AE:22";
//                Log.i(TAG, " Address: " + address);
//                if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED && address != null) {
//
//                    if (mBluetoothAdapter.isDiscovering()) {
//                        mBluetoothAdapter.cancelDiscovery();
//                    }
//
//                    // Get the BluetoothDevice object
//                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//
//                    Log.i(TAG, " DeviceAddress:" + device.getAddress() );
//                    Log.i(TAG, " DeviceName:" + device.getName());
//
//                    // Attempt to connect to the device
//                    mChatService.connect(device);
//                }
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.message);
        mConversationView = (ListView) findViewById(R.id.lvStatus);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null){
            mChatService.stop();
        }
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero
            mOutStringBuffer.setLength(0);

        }
    }

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(D) Log.i(TAG, "handler in~");


            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {

                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to) + mConnectedDeviceName);
                            mConversationArrayAdapter.clear();
                            break;

                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;

                        case BluetoothChatService.STATE_LISTEN:

                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;

                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;

                case MESSAGE_READ:
                    String readText= (String) msg.obj;
                    mConversationArrayAdapter.add("Received:  " + readText);
                    break;

                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;

                /*
				 * retrieval of map, robot movement, obstacles detected
				 */

                case UPDATE_GRID:
                    gridBuff = (String) msg.obj;
                    //System.out.print("gridBuff" + gridBuff);
                    Log.d("myapp",gridBuff);

                    //gridBuff = gridBuff.substring(2);

                    gridMapArr = gridBuff.split(" ");
                    initGridMapView(gridMapArr);
                    updateSelected = false;
                    robotStatus.setText("Grid Updated");
                    break;

                default:
                    break;

            }

        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
//                    String address = data.getExtras()
//                           .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//                    String address ="00:02:72:3F:AE:22";
                    // Get the BLuetoothDevice object
//                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    connectDevice(data);
//                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case REQUEST_PHONE_MOTION:
                // When the Settings Activity returns
                if (resultCode == Activity.RESULT_OK) {
                    // motion_chkbox is now enabled, so start phone motion
                    // fetch the pm Booleany
                    phoneMotion = true;
                    boolean message = data.getExtras().getBoolean("pmState", phoneMotion);
                    Log.d(TAG, "+++++++pm status:" + message);
                    //Check is message is true
                    if (message) {
                        phoneMotion();
                    }
                }
                else{
                    phoneMotion = false;
                }
                break;
        }
    }

    private void connectDevice(Intent data) {
        Log.d(TAG, "in connectDevice");
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        String address ="00:02:72:3F:AE:22";
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            case R.id.settings:
                // Launch SettingsActivity
                configureSettings();
                return true;
        }
        return false;
    }

    private void configureSettings() {
        if(D) Log.d(TAG, "configure F1 F2 or motion function");
        Intent settingIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingIntent, REQUEST_PHONE_MOTION);
    }

    //Image btn when click, highlight effect
    public static void btnEffect(View ImageButton){
        ImageButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(FILTERED_GREY, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        float alpha = (float) 0.8;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        //sendMessage(String.valueOf(gravity[0]));


        // Normalize the gravity vector and rescale it so that every component
        // fits one byte.
        float size = (float) Math.sqrt(Math.pow(gravity[0], 2)
                + Math.pow(gravity[1], 2) + Math.pow(gravity[2], 2));
        phoneX = (byte) (128 * gravity[0] / size);
        phoneY = (byte) (128 * gravity[1] / size);
    }


    public void phoneMotion() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                newPhoneMotion();
            }
        }, 500);

    }

    public void newPhoneMotion() {
        if (phoneMotion) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (phoneY < -60){
                        sendMessage("HA:A");
                        //triggerLeft();
                    }

                    else if (phoneY > 60) {
                        sendMessage("HA:D");
                        //triggerRight();
                    }
                    else if (phoneX < -25) {
                        sendMessage("HA:W");
                        //triggerUP();
                    }
                    else if (phoneX > 100) {
                        sendMessage("HA:S");
                        //triggerDown();
                    }
                    phoneMotion();
                }
            }, 500);
        }

    }

    public void sendGrid() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                newSendGrid();
            }
        }, 500);

    }

    public void newSendGrid() {
        if (autoMode) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //sendMessage(String.valueOf(gravity[0]));
                    //sendMessage("x: " +String.valueOf(newX) +" y: " + String.valueOf(newY));
                    if(BluetoothChatService.STATE_CONNECTED == mChatService.getState()){
                        sendMessage("PA:GRID");
                        //sendMessage("GRID");

                    }

                    sendGrid();
                }
            }, 500);
        }

    }


    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


    /* GridMap and robotImg implementation*/
    // Implement Draw Grid function

    public void initGridMapView(String [] gridMapArr) {

        drawGridMap = new MapGridView(MainActivity.this, gridMapArr);
        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.gridLayout);
        // Cleans previous layout of grid to allow proper update of map
        gridLayout.removeAllViewsInLayout();
        gridLayout.addView(drawGridMap);

    }

    // Resets Grid Map to start state
    public void blankGridMapView() {
        // Resets Grid map
        drawGridMap = new MapGridView(MainActivity.this);
        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.gridLayout);
        // Cleans previous layout of grid to allow proper update of map
        gridLayout.removeAllViewsInLayout();
        gridLayout.addView(drawGridMap);

    }

    // Create robot on grid map
    public void createRobot(int BposX, int BposY, int RposX, int RposY) {

        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.robotImgView);
        Robot robot = new Robot(MainActivity.this, gridLayout);
        if (BposY == RposY) {
            if (BposX > RposX) { // face west
                robot.createDefaultRobot(BposX - 1, BposY - 1, 4);
            } else { // face east
                robot.createDefaultRobot(BposX - 1, BposY - 1, 0);
            }

        } else {
            if (BposY > RposY) { // face north
                robot.createDefaultRobot(BposX - 1, BposY - 1, 2);
            } else { // face south
                robot.createDefaultRobot(BposX - 1, BposY - 1, 6);
            }
        }

        //sendMessage("Received robot x,y,direction" + BposX + ", " + BposY + ", " + 2);

        Log.d("Received robot x,y,direction", BposX + ", " + BposY + ", " + 2);

    }

    private final int UP = 1;
    private final int BACK = 2;
    private final int LEFT_TURN = 3;
    private final int RIGHT_TURN = 4;

    // Move robot on map grid when any arrow set btns pressed
     public void moveRobotinMap(int action) {
        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.robotImgView);
        Robot robot = new Robot(MainActivity.this, gridLayout);

        // Handles actions to activate robot movements
        switch (action) {
            case UP:
                robot.moveForward();
                break;
            case BACK:
                robot.moveBackwards();
                break;
            case LEFT_TURN:
                robot.turnLeft();
                break;
            case RIGHT_TURN:
                robot.turnRight();
                break;
        }

    }
    public void triggerUp(){
//        moveUp();
//        if (gridMapArr != null) {
//            initGridMapView(gridMapArr);
//        } else { blankGridMapView();}
//
//        createRobotPart(robotPartX, robotPartY);
//        createRobotHead(robothead[0] , robothead[1]);
//        createRobotBody(robotcentre[0], robotcentre[1]);

        sendMessage ("HA:W");

    }
    public void triggerDown() {
//        moveDown();
        //moveRobotinMap(BACK);
//        if (gridMapArr != null) {
//            initGridMapView(gridMapArr);
//        } else { blankGridMapView();}
//
//        createRobotPart(robotPartX, robotPartY);
//        createRobotHead(robothead[0] , robothead[1]);
//        createRobotBody(robotcentre[0], robotcentre[1]);
        sendMessage ("HA:S");

    }
    public void triggerLeft() {
//        routeLeft();
        //moveRobotinMap(LEFT_TURN);
//        if (gridMapArr != null) {
//            initGridMapView(gridMapArr);
//        } else { blankGridMapView();}
//
//        createRobotPart(robotPartX, robotPartY);
//        createRobotHead(robothead[0] , robothead[1]);
//        createRobotBody(robotcentre[0], robotcentre[1]);
        sendMessage ("HA:A");

    }
    public void triggerRight(){
//        routeRight();
        //moveRobotinMap(RIGHT_TURN);
//        if (gridMapArr != null) {
//            initGridMapView(gridMapArr);
//        } else { blankGridMapView();}
//
//        createRobotPart(robotPartX, robotPartY);
//        createRobotHead(robothead[0] , robothead[1]);
//        createRobotBody(robotcentre[0], robotcentre[1]);
        sendMessage ("HA:D");

    }


    public String headLocation(){
        String pos = "";

        if (robothead[1] - robotcentre[1] == -1){
            pos = "north";
        }else if(robothead[1] - robotcentre[1] == 1){
            pos = "south";
        }else if (robothead[0] - robotcentre[0] == 1){
            pos = "east";
        }else{
            //head[0] - centre[1] ==1
            pos = "west";
        }

        return pos;
    }
//
//    public void routeLeft(){
//        if (headLocation() == "north"){
//            robothead[0] -= 1;
//            robothead[1] += 1;
//        }else if (headLocation() == "south"){
//            robothead[0] += 1;
//            robothead[1] -= 1;
//        }else if (headLocation() == "east"){
//            robothead[0] -= 1;
//            robothead[1] -= 1;
//        }else{
//            robothead[0] += 1;
//            robothead[1] += 1;
//        }
//        robotPos();
//    }
//
//    public void routeRight(){
//        if (headLocation() == "north"){
//            robothead[0] += 1;
//            robothead[1] += 1;
//        }else if (headLocation() == "south"){
//            robothead[0] -= 1;
//            robothead[1] -= 1;
//        }else if (headLocation() == "east"){
//            robothead[0] -= 1;
//            robothead[1] += 1;
//        }else{
//            robothead[0] += 1;
//            robothead[1] -= 1;
//        }
//        robotPos();
//    }
//
//    public void moveUp(){
//        if (headLocation() == "north" && robothead[1]-1>=0){
//            robothead[1] -= 1;
//            robotcentre[1] -= 1;
//        }else if (headLocation() == "south"&& robothead[1]+1<=14){
//            robothead[1] += 1;
//            robotcentre[1] += 1;
//        }else if (headLocation() == "east" && robothead[0]+1<=19){
//            robothead[0] += 1;
//            robotcentre[0]+= 1;
//        }else if(headLocation() == "west" && robothead[0]-1>=0){
//            robothead[0] -= 1;
//            robotcentre[0] -= 1;
//        }
//        robotPos();
//    }
//
//    public void moveDown(){
//        if (headLocation() == "north" && robothead[1]+3<=14){
//            robothead[1] += 1;
//            robotcentre[1] += 1;
//        }else if (headLocation() == "south"&& robothead[1]+1>=0){
//            robothead[1] -= 1;
//            robotcentre[1] -= 1;
//        }else if (headLocation() == "east"&& robothead[0]+1>=0){
//            robothead[0] -= 1;
//            robotcentre[0] -= 1;
//        }else if(headLocation() == "east" && robothead[0]+3<=19){
//            robothead[0] += 1;
//            robotcentre[0] += 1;
//        }
//        robotPos();
//    }
//
//    // Draws a single obstacle
//    public void createObstacle(int x, int y) {
//
//        drawGridMap = new MapGridView(MainActivity.this, x, y, MapGridView.CREATE_OBSTACLE);
//        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.gridLayout);
//        gridLayout.addView(drawGridMap);
//
//    }
//
//    // Draws a single obstacle
//    public void createRobotHead(int x, int y) {
//
//        drawGridMap = new MapGridView(MainActivity.this, x, y, MapGridView.CREATE_RBHEAD);
//        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.gridLayout);
//        gridLayout.addView(drawGridMap);
//
//    }
//    public void createRobotBody(int x, int y) {
//
//        drawGridMap = new MapGridView(MainActivity.this, x, y, MapGridView.CREATE_RBBODY);
//        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.gridLayout);
//        gridLayout.addView(drawGridMap);
//
//    }
//
//    public void createRobotPart(int[] x, int[] y) {
//
//        drawGridMap = new MapGridView(MainActivity.this, x, y, MapGridView.CREATE_RBPART);
//        RelativeLayout gridLayout = (RelativeLayout) findViewById(R.id.gridLayout);
//        gridLayout.addView(drawGridMap);
//
//    }
//    public void setMotion(){
//        phoneMotion = false;
//    }


}