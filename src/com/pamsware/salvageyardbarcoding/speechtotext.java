package com.pamsware.salvageyardbarcoding;

import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;

public class speechtotext extends Activity{

	
	protected TextView mInfoTextview;
    protected BluetoothAdapter mBluetoothAdapter;
    protected BluetoothHeadset mBluetoothHeadset;
    protected BluetoothDevice mConnectedHeadset;

    protected AudioManager mAudioManager;

    SpeechRecognizer sr;   
    private static final String TAG = "Bluetooth Headset"; //$NON-NLS-1$

    @SuppressLint({ "InlinedApi", "NewApi" })
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        // Set up the window layout
        
        setContentView(R.layout.bluetoothtesting);
        mInfoTextview = (TextView) findViewById(R.id.message);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null)
        {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager.isBluetoothScoAvailableOffCall())
            {    Log.d("compatible","this device is compatable");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    mBluetoothAdapter.getProfileProxy(this, mHeadsetProfileListener, BluetoothProfile.HEADSET);
                }
            }
        
        }
        
    }
    
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            if (mBluetoothHeadset != null)
            {
                // Need to call stopVoiceRecognition here when the app
                // change orientation or close with headset still turns on.
                mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
                 unregisterReceiver(mHeadsetBroadcastReceiver);
                 mAudioManager.stopBluetoothSco();          
                    mCountDown.cancel();
            }
            mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
        }
        Log.d(TAG, "onDestroy"); //$NON-NLS-1$
    }
    
   
    
    protected BluetoothProfile.ServiceListener mHeadsetProfileListener= new BluetoothProfile.ServiceListener()
            {

				@Override
				public void onServiceConnected(int profile,
						BluetoothProfile proxy) {
					
				Log.d("service","connected");
				
                mBluetoothHeadset = (BluetoothHeadset) proxy;

                // If a head set is connected before this application starts,
                // ACTION_CONNECTION_STATE_CHANGED will not be broadcast. 
                // So we need to check for already connected head set.
                List<BluetoothDevice> devices = mBluetoothHeadset.getConnectedDevices();
                if (devices.size() > 0)
                {
                    // Only one head set can be connected at a time, 
                    // so the connected head set is at index 0.
                    mConnectedHeadset = devices.get(0);

                    String log;

                    // The audio should not yet be connected at this stage.
                    // But just to make sure we check.
                    if (mBluetoothHeadset.isAudioConnected(mConnectedHeadset))
                    {
                        log = "Profile listener audio already connected";
                           
                    }else
                    {	
//                        mAudioManager.setBluetoothScoOn(true);
//                        mAudioManager.startBluetoothSco();  
                if (mBluetoothHeadset.startVoiceRecognition(mConnectedHeadset))
                {
                    log = "Profile listener startVoiceRecognition returns true";
              
  
                }
                else
                {
                    log = "Profile listener startVoiceRecognition returns false"; //$NON-NLS-1$
                }   

                    }
                Log.d("TAG",log);    
                }
				
				
                registerReceiver(mHeadsetBroadcastReceiver, 
                        new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED));

				}
                
                
				@Override
				public void onServiceDisconnected(int profile) {
					// TODO Auto-generated method stub
					
					Log.d(TAG, "Profile listener onServiceDisconnected"); //$NON-NLS-1$
//                    mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
//                    unregisterReceiver(mHeadsetBroadcastReceiver);
//                    mBluetoothHeadset = null;
					
				}
            	
            };
            
            protected BroadcastReceiver mHeadsetBroadcastReceiver = new BroadcastReceiver()
            {

            	
                @Override
                public void onReceive(Context context, Intent intent)
                 {  
                //	mAudioManager.setBluetoothA2dpOn(true);
                	
                	mAudioManager.setBluetoothScoOn(true);
                	int prestate=intent.getIntExtra(BluetoothHeadset.EXTRA_PREVIOUS_STATE, -1);
                	int curstate=intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
                	
                	if(curstate==mBluetoothHeadset.STATE_CONNECTED){
                		Log.d("state","connected");
                		mAudioManager.setBluetoothScoOn(true);
                		mAudioManager.startBluetoothSco();
                		mCountDown.start();
                	}else
                		if(curstate==mBluetoothHeadset.STATE_DISCONNECTED)
                	{
                			mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
                		
                			Log.d("state","disconnected");
                	}else
                		if(curstate==mBluetoothHeadset.STATE_CONNECTING){
                			Log.d("state","connecting");
                		}else
                			if((curstate==mBluetoothHeadset.STATE_DISCONNECTING))
                			{
                				Log.d("state","disconnecting");
                			}
                			
                	Log.d("prestate",""+prestate);
                	Log.d("curstate",""+curstate);
                	
                }

				
            };
            
            
            
            @SuppressLint("NewApi")
			protected CountDownTimer mCountDown = new CountDownTimer(10000, 1000)
            {

                @Override
                public void onTick(long millisUntilFinished)
                {

                	 String log;
                     if (mBluetoothHeadset.isAudioConnected(mConnectedHeadset))
                     {	
         
                     	log = "\nonTick audio already connected"; //$NON-NLS-1$
                     	
                        
                     }
                     else
                     {
                         // First stick calls always returns false. The second stick
                         // always returns true if the countDownInterval is set to 1000.
                         // It is somewhere in between 500 to a 1000.
                         if (mBluetoothHeadset.startVoiceRecognition(mConnectedHeadset))
                         {
                         	
                             log = "\nonTick startVoiceRecognition returns true"; //$NON-NLS-1$
                         }
                         else
                         {
                             log = "\nonTick startVoiceRecognition returns false"; //$NON-NLS-1$
                             mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
                         }
                     }

                    Log.d(TAG,log);
                     Log.d(TAG, "countdown start working");
                }

                @Override
                public void onFinish()
                {   Log.d("countdown", "finsih");
                
                mAudioManager.startBluetoothSco();
                mAudioManager.setBluetoothScoOn(true);
            MyRecognitionListener listener = new MyRecognitionListener();
      	    final SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
      	    sr.setRecognitionListener(listener);
      	  mAudioManager.startBluetoothSco();
          mAudioManager.setBluetoothScoOn(true);
      	    sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
      	    
               
                
                MediaPlayer mp;
//                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                mAudioManager.setBluetoothScoOn(true);
 //               mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
//       		mAudioManager.startBluetoothSco();

       		
                 
//        		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
//        		
//        		mAudioManager.setMode(AudioManager.MODE_IN_CALL);
//        		mAudioManager.setSpeakerphoneOn(false);
//        		mAudioManager.setWiredHeadsetOn(false);
//         		mAudioManager.setBluetoothA2dpOn(true);
//        		mAudioManager.setBluetoothScoOn(true);
//        		mp = MediaPlayer.create(getApplicationContext(), R.raw.nowcharging);
//        		mp.start();
//        		while (mp.isPlaying()) {
//        			Log.i("Easy Charger", "Playing the Intro Audio");
//        		}
//
//        		mp.stop();
//        		mp.release();
               }
            };
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

  
    
    }

