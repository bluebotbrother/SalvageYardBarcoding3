package com.pamsware.salvageyardbarcoding;




import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;


public class BluetoothTesting extends Activity {

      Intent intent=new Intent();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoothtesting);
        
        TextView tv=(TextView) findViewById(R.id.message);
        tv.setText("Press the button and speak");
        
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
       this.registerReceiver(disconnect, filter3);
        
        MediaButtonIntentReceiver buttonaction=new MediaButtonIntentReceiver();
        IntentFilter buttonpressed = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
      
        buttonpressed.setPriority(10000); 
        this.registerReceiver(buttonaction,buttonpressed);        
	}
// 
//	@Override
//	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//	    Log.d("b","pressed");
//		if (keyCode == KeyEvent.KEYCODE_CALL) {
//	        // do your stuff here
//	        Log.d("button","pressed");
//	    	return true;
//	    }
//	    Log.d("button","unpressed");
//	    return false;
//	}
//	private final BroadcastReceiver buttonaction = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//        	KeyEvent key = (KeyEvent) 
//        			intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//        	Log.d("in","buttonaction");
//        	int action = key.getAction();
//            if (action == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
//        	Intent goback=new Intent(BluetoothTesting.this,MainActivity.class);
//          startActivity(goback);
//            }
//        }
//    };
  	
	
	private final BroadcastReceiver disconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          Intent goback=new Intent(BluetoothTesting.this,MainActivity.class);
          startActivity(goback);
 
        }
    };

}