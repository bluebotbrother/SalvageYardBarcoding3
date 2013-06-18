package com.pamsware.salvageyardbarcoding;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MediaButtonIntentReceiver extends BroadcastReceiver  {

public MediaButtonIntentReceiver() {
    super();
}

@Override
public void onReceive(Context context, Intent intent) {
    String intentAction = intent.getAction();
    Log.d("working","in media");
    if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
        return;
    }
    KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
    if (event == null) {
        return;
    }
    int action = event.getAction();
    if (action == KeyEvent.ACTION_DOWN) {
    // do something
        Toast.makeText(context, "BUTTON PRESSED!" , Toast.LENGTH_SHORT).show(); 
      //   String keyCodeStr = KeyEvent.keyCodeToString(event.getKeyCode());
       // String key=intent.getExtras().getString("EXTRA_KEY_EVENT");
       // Toast.makeText(context, keyCodeStr,Toast.LENGTH_LONG).show();

    }
    abortBroadcast();
}
}

