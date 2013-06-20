package com.pamsware.salvageyardbarcoding;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


public class Bluetooth extends Activity{

	BluetoothHelper mBluetoothHelper;

	@Override
	 protected void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
	
	     mBluetoothHelper = new BluetoothHelper(this);
	  

    }

	@Override
	protected
	void onResume()
	{ 
		super.onResume();
	    mBluetoothHelper.start();
	    Log.d("start","starting");
	}

	@Override
	protected void onPause()
	{
		Log.d("stop","stoping");
		mBluetoothHelper.stop();
		super.onPause();
	    
	}

	// inner class
	// BluetoothHeadsetUtils is an abstract class that has
	// 4 abstracts methods that need to be implemented.
	class BluetoothHelper extends BluetoothHeadsetUtils
	{
	    public BluetoothHelper(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		
	    @Override
	    public void onScoAudioDisconnected()
	    {
	        // Cancel speech recognizer if desired
	    	Log.d("sco","disconnected");
	    }

	    @Override
	    public void onScoAudioConnected()
	    {
	    	
	    	Log.d("sco","connected");
	        // Should start speech recognition here if not already started  
	    }

	    @Override
	    public void onHeadsetDisconnected()
	    
	    {
	    	Log.d("sco","headsetdisconnected");
	    }

	    @Override
	    public void onHeadsetConnected()
	    {
	    	Log.d("sco","headsetconnected");
	    }
	}
}