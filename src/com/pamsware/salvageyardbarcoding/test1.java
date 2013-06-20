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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import android.widget.TextView;

public class test1 extends Activity {
	
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
                
                
          	    //listener.addSpeechHeardListener(this);

                if (mBluetoothAdapter != null)
                {
                    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (mAudioManager.isBluetoothScoAvailableOffCall())
                    {    Log.d("check","check");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        {
                            mBluetoothAdapter.getProfileProxy(this, mHeadsetProfileListener, BluetoothProfile.HEADSET);
                        }
                    }
                }
            }

            @SuppressLint("NewApi")
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
                         mCountDown.cancel();
                    }
                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, mBluetoothHeadset);
                }
                Log.d(TAG, "onDestroy"); //$NON-NLS-1$
            }

            @SuppressLint("NewApi")
			protected BluetoothProfile.ServiceListener mHeadsetProfileListener = new BluetoothProfile.ServiceListener()
            {

                /**
                 * This method is never called, even when we closeProfileProxy on onPause.
                 * When or will it ever be called???
                 */
                @Override
                public void onServiceDisconnected(int profile)
                {
                    Log.d(TAG, "Profile listener onServiceDisconnected"); //$NON-NLS-1$
                    mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
                    unregisterReceiver(mHeadsetBroadcastReceiver);
                    mBluetoothHeadset = null;
                }

                @Override
                public void onServiceConnected(int profile, BluetoothProfile proxy)
                {
                    Log.d(TAG, "Profile listener onServiceConnected"); //$NON-NLS-1$

                    // mBluetoothHeadset is just a head set profile, 
                    // it does not represent a head set device.
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
//
                        String log;

                        // The audio should not yet be connected at this stage.
                        // But just to make sure we check.
                        if (mBluetoothHeadset.isAudioConnected(mConnectedHeadset))
                        {
                            log = "Profile listener audio already connected"; //$NON-NLS-1$     
                        }
                        else
                        {
                            // The if statement is just for debug. So far startVoiceRecognition always 
                            // returns true here. What can we do if it returns false? Perhaps the only
                            // sensible thing is to inform the user.
                            // Well actually, it only returns true if a call to stopVoiceRecognition is
                            // call somewhere after a call to startVoiceRecognition. Otherwise, if 
                            // stopVoiceRecognition is never called, then when the application is restarted
                            // startVoiceRecognition always returns false whenever it is called.
                            if (mBluetoothHeadset.startVoiceRecognition(mConnectedHeadset))
                            {
                                log = "Profile listener startVoiceRecognition returns true";
                                //$NON-NLS-1$
                            }
                            else
                            {
                                log = "Profile listener startVoiceRecognition returns false"; //$NON-NLS-1$
                            }   
                        }

                     //   mInfoTextview.setText("Device name = " + mConnectedHeadset.getName() //$NON-NLS-1$
                     //                           + "\n\n" + log); //$NON-NLS-1$
                        Log.d(TAG, log); 
                    }
//
                    // During the active life time of the app, a user may turn on and off the head set.
                    // So register for broadcast of connection states.
        //            registerReceiver(mHeadsetBroadcastReceiver, 
        //                            new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED ));
                    // Calling startVoiceRecognition does not result in immediate audio connection.
                    // So register for broadcast of audio connection states. This broadcast will
                    // only be sent if startVoiceRecognition returns true.
                    registerReceiver(mHeadsetBroadcastReceiver, 
                                   new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED));
                }
            };

            @SuppressLint("NewApi")
			protected BroadcastReceiver mHeadsetBroadcastReceiver = new BroadcastReceiver()
            {
//
                @Override
                public void onReceive(Context context, Intent intent)
                {  
                	Log.d("audio connected","working");
                	String action = intent.getAction();	
                	
                	//if(action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)){
                	//	int state=intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                		mCountDown.start();
                	//	Log.d("state",""+state);
                	//			Log.d("audio","audio connected");
                	}
                	
                };
//                    String action = intent.getAction();
//                    int state;
//                    int previousState = intent.getIntExtra(BluetoothHeadset.EXTRA_PREVIOUS_STATE, BluetoothHeadset.STATE_DISCONNECTED);
//                    String log = ""; //$NON-NLS-1$
//                    if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED))
//                    {
//                        state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_DISCONNECTED);
//                        if (state == BluetoothHeadset.STATE_CONNECTED)
//                        {
//                            mConnectedHeadset = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                            mInfoTextview.append("\n\nDevice name = " + mConnectedHeadset.getName()); //$NON-NLS-1$
//
//                            // Audio should not be connected yet but just to make sure.
//                            if (mBluetoothHeadset.isAudioConnected(mConnectedHeadset))
//                            {
//                                log = "Headset connected audio already connected"; //$NON-NLS-1$
//                            }
//                            else
//                            {
//                                // Calling startVoiceRecognition always returns false here, 
//                                // that why a count down timer is implemented to call
//                                // startVoiceRecognition in the onTick and onFinish.
//                                if (mBluetoothHeadset.startVoiceRecognition(mConnectedHeadset))
//                                {
//                                    log = "Headset connected startVoiceRecognition returns true";
//                                    //mCountDown.start();//$NON-NLS-1$
//                                }
//                                else
//                                {
//                                    log = "Headset connected startVoiceRecognition returns false"; //$NON-NLS-1$
//                                    mCountDown.start();
//                                }
//                            }
//                        }
//                        else if (state == BluetoothHeadset.STATE_DISCONNECTED)
//                        {
//                            // Calling stopVoiceRecognition always returns false here
//                            // as it should since the headset is no longer connected.
//                            Log.d("disconnect","device");
//                        	mConnectedHeadset = null;
//                        }
//                    }
//                    else // audio
//                    {
//                    	//
//                        state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
//                        if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED)
//                        {
//                            log = "Head set audio connected, cancel countdown timer";  //$NON-NLS-1$
////                            mAudioManager.startBluetoothSco();
////                        	
////                            MyRecognitionListener listener = new MyRecognitionListener();
////                    	    final SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
////                      	    sr.setRecognitionListener(listener);
////                      	  sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
//                            mCountDown.cancel();    
//                        }
//                        else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED)
//                        {
//                            // The headset audio is disconnected, but calling
//                            // stopVoiceRecognition always returns true here.
//                            boolean returnValue = mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
//                            log = "Audio disconnected stopVoiceRecognition return " + returnValue; //$NON-NLS-1$
//                        }
//                    }   
//
//                    log += "\nAction = " + action + "\nState = " + state //$NON-NLS-1$ //$NON-NLS-2$
//                            + " previous state = " + previousState; //$NON-NLS-1$
//                    mInfoTextview.append("\n\n" + log); //$NON-NLS-1$
//                    Log.d(TAG, log);
//                }
//            };


            @SuppressLint("NewApi")
			protected CountDownTimer mCountDown = new CountDownTimer(10000, 1000)
            {

                @Override
                public void onTick(long millisUntilFinished)
                {
                    String log;
                    if (mBluetoothHeadset.isAudioConnected(mConnectedHeadset))
                    {	
//                    	mAudioManager.startBluetoothSco();
//                    	
//                        MyRecognitionListener listener = new MyRecognitionListener();
//                	    final SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
//                  	    sr.setRecognitionListener(listener);
////              	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
////              	    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
////              	    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplication().getPackageName());
////                      sr.startListening(intent);
                 	  // sr.startListening(RecognizerIntent.getVoiceDetailsIntent(getApplicationContext()));
                       
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
                        }
                    }

                    mInfoTextview.append(log);
                    Log.d(TAG, log);
                }

                @Override
                public void onFinish()
                {
                    String log;
                    if (mBluetoothHeadset.isAudioConnected(mConnectedHeadset))
                    {
                        log = "\nonFinish audio already connected"; //$NON-NLS-1$
                    }
                    else
                    {
                        if (mBluetoothHeadset.startVoiceRecognition(mConnectedHeadset))
                        {
                            log = "\nonFinish startVoiceRecognition returns true"; //$NON-NLS-1$
                        }
                        else
                        {
                            log = "\nonFinish startVoiceRecognition returns false"; //$NON-NLS-1$
                        }
                    }
                    mInfoTextview.append(log);
                    Log.d(TAG, log);
                }
            };
        
        
        
        
        
}

