package com.pamsware.salvageyardbarcoding;

import android.content.Context;

 
public  class BluetoothHelper extends BluetoothHeadsetUtils {

 public BluetoothHelper(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

 @Override
 public void onScoAudioDisconnected()
 {
     // Cancel speech recognizer if desired
 }

 @Override
 public void onScoAudioConnected()
 {           
     // Should start speech recognition here if not already started  
 }

 @Override
 public void onHeadsetDisconnected()
 {

 }

 @Override
 public void onHeadsetConnected()
 {

 }
}
