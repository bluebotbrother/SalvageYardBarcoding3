package com.pamsware.salvageyardbarcoding;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;




import com.pamsware.salvageyardbarcoding.BluetoothChatService;
import com.pamsware.salvageyardbarcoding.DeviceListActivity;
import com.pamsware.salvageyardbarcoding.KScan;
import com.pamsware.salvageyardbarcoding.KTSyncData;
import com.pamsware.salvageyardbarcoding.R;
import com.pamsware.salvageyardbarcoding.Settings;
import com.pamsware.salvageyardbarcoding.MovePart;
import com.pamsware.salvageyardbarcoding.speechEvent.speechListner;




import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAssignedNumbers;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


 





@SuppressLint("NewApi")
public class MainActivity extends Activity implements TextToSpeech.OnInitListener, speechListner {

    private String strCurrentLocation = "";
    private String strTagId= "";
    private String strUserId = "test";
    private String strTrackingInfo = "";
    Button btnTest;
   
    
    
    private byte[] displayBuf = new byte[256];
    private String displayMessage;
    
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    private static final int SETTINGS=0;	
  
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_DISPLAY = 6;    
    public static final int MESSAGE_SEND = 7;     
    
    public static final int MESSAGE_SETTING = 255;
    public static final int MESSAGE_EXIT = 0;    
    
    public boolean shift;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;    
    // Member object for the chat services
    //private BluetoothChatService mChatService = null;
    
    public BluetoothDevice connecteddevice = null;    
    
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;    
    private ListView mConversationView;
    private TextView blueToothstatusTitle;    
    private TextView activityTitle; 
    BluetoothHelper mBluetoothHelper;
    
    // Scanner Vars
    private TextToSpeech tts;
   
    // Enumeration of the available functions the scanner does on this activity
    public enum functionMode{ Move, Grade, Pull, StageInspect, ShipInspect, Tracking };
    
    // VAR to hold the current functionMode and set to Move parts as default
    private functionMode currentMode = functionMode.Move;
    
    private int intSpeechFailureCount;
   
    MediaButtonIntentReceiver btReceiver = new MediaButtonIntentReceiver();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        // Set up the window layout
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        activityTitle = (TextView) findViewById(R.id.title_left_text);
        activityTitle.setText(R.string.app_name);
        blueToothstatusTitle = (TextView) findViewById(R.id.title_right_text);
        mBluetoothHelper = new BluetoothHelper(this);
        
        //registers the receiver when bluetooth is connected/disconnect but not paired
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
       // this.registerReceiver(mReceiver, filter2);
        //this.registerReceiver(mReceiver, filter3);
    

            
        tts = new TextToSpeech(this,this);

        
        btnTest= (Button)findViewById(R.id.btnTest);
       
        
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.MoveResults);
        mConversationView.setAdapter(mConversationArrayAdapter);    
        
        //Scanner Code
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        KTSyncData.mKScan = new KScan(this, mHandler);

        KTSyncData.BufferRead = 0;
        KTSyncData.BufferWrite = 0;
        
        for (int i = 0; i < 10; i++) {
        	KTSyncData.SerialNumber[i] = '0';
        	KTSyncData.FWVersion[i] = '0';
        }
        
        GetPreferences();
        
        KTSyncData.bIsRunning = true;
        
        KTSyncData.bIsOver_233 = false;
        
        StringBuffer buf = new StringBuffer();
        buf.append(Build.VERSION.RELEASE);

        String version = buf.toString();
        String target = "2.3.3";
        if ( version.compareTo(target) > 0 ) {
        	KTSyncData.bIsOver_233 = true;
        }      
        //Scanner Code
        
        MyRecognitionListener listener = new MyRecognitionListener();
  	    final SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
  	    
  	    sr.setRecognitionListener(listener);
  	    listener.addSpeechHeardListener(this);
  	    
  	  
  	
  	
     

    //  IntentFilter btIntentFilter = new IntentFilter();
     // btIntentFilter.addCategory(BluetoothHeadset.VENDOR_SPECIFIC_HEADSET_EVENT_COMPANY_ID_CATEGORY + "." + BluetoothAssignedNumbers.MOTOROLA);
    //  btIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    //  btIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    //  btIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    //  btIntentFilter.addAction(BluetoothHeadset.ACTION_VENDOR_SPECIFIC_HEADSET_EVENT);
    //  btIntentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
    //  btIntentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
      
     // btIntentFilter.setPriority(10000000);
    //  registerReceiver(btReceiver, btIntentFilter); 
  	  
  	    
  	  //  IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);//"android.intent.action.MEDIA_BUTTON"
  	  //  MediaButtonIntentReceiver r = new MediaButtonIntentReceiver();
      //  filter.setPriority(100000); 
       // registerReceiver(r, filter);
        

        MediaButtonIntentReceiver buttonaction=new MediaButtonIntentReceiver();
        IntentFilter buttonpressed = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);  
        buttonpressed.setPriority(10000); 
        this.registerReceiver(buttonaction,buttonpressed);    


  	  
       // AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
      //  manager.registerMediaButtonEventReceiver(new ComponentName(getPackageName(), MediaButtonIntentReceiver.class.getName()));
       // manager.setBluetoothScoOn(true);
          
      
      
        btnTest.setOnClickListener(new View.OnClickListener() {
	                  @Override
	                  public void onClick(View v) 
	                  {
	                	  sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
	                		 // May need this for headsets
	                	  //http://stackoverflow.com/questions/14991158/using-the-android-recognizerintent-with-a-bluetooth-headset
	                  
	        }
	        });
         
       
    }
    	
   
    
  //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("intent","working");
        	String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            //check if bluetooth device is paired
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();        
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
           // If there are paired devices
           if (pairedDevices.size() > 0) {
           // Loop through paired devices
           //for (BluetoothDevice device : pairedDevices) {
             // Add the name and address to an array adapter to show in a ListView
           //  mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
             
        	   Intent speechtotext=new Intent(MainActivity.this,BluetoothTesting.class);
        	   Log.d("device","paired");
               startActivity(speechtotext);
           }else{
    	        Log.d("device","not paired");
             }
         
            
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
               Log.d("bluetooth", "connected");
            	//Device found
            }
            //else if (BluetoothAdapter.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
           //} 
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
               //Done searching
            }
            //else if (BluetoothAdapter.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            //}
            //else if (BluetoothAdapter.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
            }           
        };

           
    
    
    @Override
	public void speechEvent(com.pamsware.salvageyardbarcoding.speechEvent event) {
		
			
			 boolean bolFoundActivity=false;
			 for (int i = 0; i < event.speechHeard().size();i++ ) {
				   Log.d("SpeechEvent", "result=" + event.speechHeard().get(i));
				  
				   if (String.valueOf(event.speechHeard().get(i)).contains("move")==true) {
					   currentMode=functionMode.Move;
					   strCurrentLocation="";
					   SayIt("Ready to Move");
					   intSpeechFailureCount=0;
					   bolFoundActivity=true;
					   break;
					   }
				   
				   if (String.valueOf(event.speechHeard().get(i)).contains("grade")==true) {
					   currentMode=functionMode.Grade;
					   SayIt("Lets get Grading");
					   intSpeechFailureCount=0;
					   bolFoundActivity=true;
					   break;
					   }
				   
				   if (String.valueOf(event.speechHeard().get(i)).contains("ship")==true) {
					   currentMode=functionMode.ShipInspect;
					   SayIt("Ship Inspect Ready");
					   intSpeechFailureCount=0;
					   bolFoundActivity=true;
					   break;
					   }
				   
				   if (String.valueOf(event.speechHeard().get(i)).contains("pull")==true) {
					   currentMode=functionMode.Pull;
					   SayIt("tell me which route or say list");
					   intSpeechFailureCount=0;
					   bolFoundActivity=true;
					   break;
					   }
				   
				   if (String.valueOf(event.speechHeard().get(i)).contains("track")==true) {
					   currentMode=functionMode.Tracking;
					   SayIt("ready to get tracking info");
					   intSpeechFailureCount=0;
					   bolFoundActivity=true;
					   break;
					   }
				   
				   if (String.valueOf(event.speechHeard().get(i)).contains("stage")==true) {
					   currentMode=functionMode.StageInspect;
					   SayIt("Lets Check out Some Parts");
					   intSpeechFailureCount=0;
					   bolFoundActivity=true;
					   break;
					   }
				   
				  }
			 // Could not find any activity match
			 if(bolFoundActivity==false) {
				 intSpeechFailureCount+=1;
				 switch (intSpeechFailureCount) {
				 case 1: SayIt("Sorry, I didn't understand");
				 	break;
				 case 2: SayIt("I still didn't understand what you are saying");
				 	break;
				 case 3: SayIt("We dont do that here");
				 	break;
				 case 4: SayIt("I dont speak spanish");
				 	break;
				 default: SayIt("Lets just play chop sticks");
				 	break;
				 }
				 
			 }
	}

    
    private void SayIt(String strCrapToSay){
     
   	  tts.setLanguage(Locale.US);
   	  tts.speak(strCrapToSay, TextToSpeech.QUEUE_ADD, null);  
    }
    private void connectScanner() {
    	Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
   
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.connect:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);    	
            return true;
        case R.id.setting:
            // Ensure this device is discoverable by others
            //ensureDiscoverable();
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
            //KTSyncData.mKScan.GetMemoryStatus();
            return true;
        case R.id.moveparts:
				Toast.makeText(this, "Move Parts", Toast.LENGTH_LONG).show();        	
				currentMode=functionMode.Move;
				activityTitle.setText(R.string.app_name+" Move Part");
				return true; 
        case R.id.gradeparts:
			Toast.makeText(this, "Grade Parts", Toast.LENGTH_LONG).show();        	
			currentMode=functionMode.Grade;
			activityTitle.setText(R.string.app_name+" Grade Part");
        case R.id.stageinspectparts:
			Toast.makeText(this, "Stage Inspect Parts", Toast.LENGTH_LONG).show();        	
			currentMode=functionMode.StageInspect;
			activityTitle.setText(R.string.app_name+" Stage Inspect Part");
        case R.id.tracking:
			Toast.makeText(this, "Gather Tracking", Toast.LENGTH_LONG).show();        	
			currentMode=functionMode.Tracking;
			activityTitle.setText(R.string.app_name+" Tracking");
			strTrackingInfo="";
        case R.id.pullparts:
			Toast.makeText(this, "Pull Parts", Toast.LENGTH_LONG).show();        	
			currentMode=functionMode.Pull;
			activityTitle.setText(R.string.app_name+" Pull Parts");
        case R.id.shipinspectparts:
			Toast.makeText(this, "Ship Inspect Parts", Toast.LENGTH_LONG).show();        	
			currentMode=functionMode.ShipInspect;
			activityTitle.setText(R.string.app_name+" Ship Inspect Parts");
        }
        return false;
    }      
    
   private void handleReceivedScan(String strScannedData) {
	   boolean bolNeedsMove = false;
	   boolean bolMatched = false;
	
	   int strLen = strScannedData.length();
	   
	   if (strLen>3) {
		   
		   String strChar0 = strScannedData.substring(0,1);
		   String strChar4 = strScannedData.substring(3,4);
			 
		   if (strChar4.equalsIgnoreCase("-")) {
			   strCurrentLocation=strScannedData;
			   bolMatched=true;
		   }
		   if (strChar0.equalsIgnoreCase("R")) {
			   strTagId=strScannedData;
			   bolNeedsMove=true;
			   bolMatched=true;
		   }
		   if (strChar0.equalsIgnoreCase("G")) {
			   strTagId=strScannedData;
			   bolNeedsMove=true;
			   bolMatched=true;
		   }
		   if (!bolMatched) {
			   strTrackingInfo=strScannedData;
		   }
		   
		   MoveResults MR ;
	       MR = new MoveResults();
	       MovePart mover = new MovePart();
	          
	       String strListViewMessage;
	       
		   //Move a Part
		   if (bolNeedsMove && strCurrentLocation.length()>3 && currentMode== functionMode.Move) {
			   
			 
	           
	      	 	try { 
	      	 	
	      	 		MR = mover.movePart(strTagId,strCurrentLocation,strUserId);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
	      	    
	      	     
	      	     if (MR.WasMoveSuccessFul) {
	      	    	strListViewMessage = "Moved : "+strTagId+System.getProperty("line.separator")+ "to Loc : "+strCurrentLocation+System.getProperty("line.separator")+getCurrentTimeStamp();
	      	    	SayIt("Part Moved");
	      	    	strTagId="";
	      	     }
	      	     else
	      	     {
	      	    	strListViewMessage = "FAILED : "+strTagId+ "to Loc : "+strCurrentLocation+System.getProperty("line.separator")+getCurrentTimeStamp()+System.getProperty("line.separator")+MR.MoveFailureReason ;
	      	    	sendMessage("GMB1GMB1GMB1GMB1GMB1");
	      	    	sendMessage("GM1;0#GMT"+MR.MoveFailureReason+"\r");
	      	    	SayIt("Fail ... "+MR.MoveFailureReason);
	      	    	strTagId="";
	      	     }
	      	     
	      	 //Ship Inspect a Part
	  		   if (bolNeedsMove && currentMode== functionMode.ShipInspect) {
	  			   
	  	      	 	try { 
	  	      	 	
	  	      	 		MR = mover.movePart(strTagId,"SHIPINSPECT",strUserId);
	  					} catch (InterruptedException e) {
	  						e.printStackTrace();
	  					} catch (ExecutionException e) {
	  						e.printStackTrace();
	  					}
	  	      	    
	  	      	     
	  	      	     if (MR.WasMoveSuccessFul) {
	  	      	    	strListViewMessage = "Ship Inspect : "+strCurrentLocation+System.getProperty("line.separator")+getCurrentTimeStamp();
	  	      	    	SayIt("Part Inspected");
	  	      	    	strTagId="";
	  	      	     }
	  	      	     else
	  	      	     {
	  	      	    	strListViewMessage = "FAILED : "+strTagId+ "Ship Inspect : "+getCurrentTimeStamp()+System.getProperty("line.separator")+MR.MoveFailureReason ;
	  	      	    	sendMessage("GMB1GMB1GMB1GMB1GMB1");
	  	      	    	sendMessage("GM1;0#GMT"+MR.MoveFailureReason+"\r");
	  	      	    	SayIt("Fail ... "+MR.MoveFailureReason);
	  	      	    	strTagId="";
	  	      	     }
	  		    }
	      	     
	  		   
	  		   //Stage Inspect a Part
	  		   if (bolNeedsMove && currentMode== functionMode.StageInspect) {
	  			   
	  	      	 	try { 
	  	      	 	
	  	      	 		MR = mover.movePart(strTagId,"STAGEINSPECT",strUserId);
	  					} catch (InterruptedException e) {
	  						e.printStackTrace();
	  					} catch (ExecutionException e) {
	  						e.printStackTrace();
	  					}
	  	      	    
	  	      	     
	  	      	     if (MR.WasMoveSuccessFul) {
	  	      	    	 strListViewMessage = "Stage Inspect : "+strCurrentLocation+System.getProperty("line.separator")+getCurrentTimeStamp();
	  	      	    	 SayIt("Part Inspected");
	  	      	    	 if (MR.MoveFailureReason.contains("PIC")==true){
	  	      	    		SayIt("Need Picture");
	  	      	    		strListViewMessage+=System.getProperty("line.separator")+"Need Pic";
	  	      	    	 } 
	  	      	    	strTagId="";
	  	      	     }
	  	      	     else
	  	      	     {
	  	      	    	strListViewMessage = "FAILED : "+strTagId+ "Stage Inspect : "+getCurrentTimeStamp()+System.getProperty("line.separator")+MR.MoveFailureReason ;
	  	      	    	sendMessage("GMB1GMB1GMB1GMB1GMB1");
	  	      	    	sendMessage("GM1;0#GMT"+MR.MoveFailureReason+"\r");
	  	      	    	SayIt("Fail ... "+MR.MoveFailureReason);
	  	      	    	strTagId="";
	  	      	     }
	  		    }
	  		   
	  		   //TrackingInfo for a Part
	  		   if (bolNeedsMove && currentMode == functionMode.Tracking && strTrackingInfo.length()>3) {
	  			    TrackingInfoForPartResults TR ;
	      	 		TR = new TrackingInfoForPartResults();
	      	 		TrackingInfoForPart tracker = new TrackingInfoForPart();
	  	      	 	try { 
	  	      	 		
	  	      	 	
	  	      	 		TR = tracker.trackinginfoforpart(strTagId,strTrackingInfo,strUserId);
	  					} catch (InterruptedException e) {
	  						e.printStackTrace();
	  					} catch (ExecutionException e) {
	  						e.printStackTrace();
	  					}
	  	      	    
	  	      	     
	  	      	     if (TR.WasCallSuccessFul) {
	  	      	    	strListViewMessage = "Tracking Info : "+strTagId+System.getProperty("line.separator")+getCurrentTimeStamp()+strTrackingInfo;
	  	      	    	SayIt("Tracking Info Saved");
	  	      	    	strTagId="";
	  	      	     }
	  	      	     else
	  	      	     {
	  	      	    	strListViewMessage = "FAILED : "+strTagId+ "Tracking Info : "+getCurrentTimeStamp()+System.getProperty("line.separator")+TR.CallFailureReason ;
	  	      	    	sendMessage("GMB1GMB1GMB1GMB1GMB1");
	  	      	    	sendMessage("GM1;0#GMT"+TR.CallFailureReason+"\r");
	  	      	    	SayIt("Fail ... "+TR.CallFailureReason);
	  	      	    	strTagId="";
	  	      	     }
	  		    }
	  		   
	  		   
	      	     mConversationArrayAdapter.add(strListViewMessage);
	      	 	 
		   }  
	   }
   	}
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
   
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
    //Scanner Code
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (KTSyncData.mChatService != null) KTSyncData.mChatService.stop();
        KTSyncData.mChatService = null;
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        KTSyncData.bIsRunning = false;
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }  
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (KTSyncData.mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (KTSyncData.mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
            	KTSyncData.mChatService.start();
            }
        }
        KTSyncData.bIsBackground = false;

        if ( KTSyncData.bIsConnected && KTSyncData.LockUnlock) {
        	Toast.makeText(this, "KTDemo Main Screen", Toast.LENGTH_LONG).show();
        	KTSyncData.mKScan.LockUnlockScanButton(true);
        }
        
        KTSyncData.mKScan.mHandler = mHandler;         
    }    
    
    //SCanner Code
    
   
    //Scanner Code
    private Runnable mUpdateTimeTask = new Runnable() {   
    	public void run() {       
    		if ( KTSyncData.AutoConnect && KTSyncData.bIsRunning )
        		KTSyncData.mChatService.connect(connecteddevice);
    	}
    };   
    
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        KTSyncData.mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (KTSyncData.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            KTSyncData.mChatService.write(send);
            Log.d("sending Message",message);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
           // mOutEditText.setText(mOutStringBuffer);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (KTSyncData.mChatService == null)	setupChat();
        }   
    }
    
    public void GetPreferences()
	{
		String tempstring;
		byte[] temp;
		
		SharedPreferences app_preferences =     	
		PreferenceManager.getDefaultSharedPreferences(this);
			
		KTSyncData.AutoConnect = app_preferences.getBoolean("Auto Connect", false);
		
		KTSyncData.AttachTimestamp = app_preferences.getBoolean("AttachTimeStamp", false);
		KTSyncData.AttachType = app_preferences.getBoolean("AttachBarcodeType", false);
		KTSyncData.AttachSerialNumber = app_preferences.getBoolean("AttachSerialNumber", false);
		temp = app_preferences.getString("Data Delimiter", "4").getBytes();
		KTSyncData.DataDelimiter = temp[0] - '0';
		temp = app_preferences.getString("Record Delimiter", "1").getBytes();
		KTSyncData.RecordDelimiter = temp[0] - '0';		
		
		KTSyncData.AttachLocation = app_preferences.getBoolean("AttachLocationData", false);		
		KTSyncData.SyncNonCompliant = app_preferences.getBoolean("SyncNonCompliant", false);
		KTSyncData.AttachQuantity = app_preferences.getBoolean("AttachQuantity", false);		
	}   
    public void Sleep(int timeout)
    {
	    long endTime = System.currentTimeMillis() + timeout;
	    while (System.currentTimeMillis() < endTime) {
	        synchronized (this) {
	            try {
	                wait(endTime - System.currentTimeMillis());
	            } catch (Exception e) {
	            }
	        }
	    }     
    }      
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                	blueToothstatusTitle.setText(R.string.title_connected_to);
                	blueToothstatusTitle.append(mConnectedDeviceName);
                    mConversationArrayAdapter.clear();
                    removeCallbacks(mUpdateTimeTask);                      
                    KTSyncData.mKScan.DeviceConnected(true);
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                	blueToothstatusTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                	blueToothstatusTitle.setText(R.string.title_not_connected);
                    break;
                case BluetoothChatService.STATE_LOST:
                    KTSyncData.bIsConnected = false;
                    blueToothstatusTitle.setText(R.string.title_not_connected);
                    postDelayed(mUpdateTimeTask, 2000);       
                	break;
                case BluetoothChatService.STATE_FAILED:
                	blueToothstatusTitle.setText(R.string.title_not_connected);                    
                    postDelayed(mUpdateTimeTask, 5000);
                    break;                    
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                Log.d("Writing Message",writeMessage);
               // mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:      	
                byte[] readBuf = (byte[]) msg.obj;
                
                // construct a string from the valid bytes in the buffer
                //String readMessage = new String(readBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                
                for (int i = 0; i < msg.arg1; i++) KTSyncData.mKScan.HandleInputData(readBuf[i]);
                Log.d("Reading Message","1");
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_DISPLAY:
                //byte[] 
                displayBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                //String 
                displayMessage = new String(displayBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(displayMessage);
                //dispatchBarcode(displayBuf, msg.arg1);  
                KTSyncData.bIsSyncFinished = true;
                handleReceivedScan(displayMessage);
                Log.d("MESSAGE_DISPLAY",displayMessage);
            	break;            
	        case MESSAGE_SEND:
	        	//mConversationArrayAdapter.add(new String("1"));
                byte[] sendBuf = (byte[]) msg.obj;
                
                KTSyncData.mChatService.write(sendBuf);
	        	break;
	        case MESSAGE_SETTING:
                Intent settingsActivity = new Intent(getBaseContext(),
                        Settings.class);
                startActivity(settingsActivity);   
	        	break;	        	
	        }            
        }
    };
    @Override
  	public void onActivityResult(int requestCode, int resultCode, Intent data) {
          if(D) Log.d(TAG, "onActivityResult " + resultCode);
          switch (requestCode) {
          case REQUEST_CONNECT_DEVICE:
              // When DeviceListActivity returns with a device to connect
              if (resultCode == Activity.RESULT_OK) {
                  // Get the device MAC address
                  String address = data.getExtras()
                                       .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                  // Get the BLuetoothDevice object
                  connecteddevice = mBluetoothAdapter.getRemoteDevice(address);
                  // Attempt to connect to the device
                  KTSyncData.mChatService.connect(connecteddevice);                  
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
          }
      }
	@Override
	 public void onInit(int status) {
		 
        if (status == TextToSpeech.SUCCESS) {
 
            int result = tts.setLanguage(Locale.US);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } 
 
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
 
    }//SCanner Code




} 


 


