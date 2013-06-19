package com.pamsware.salvageyardbarcoding;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import com.pamsware.salvageyardbarcoding.speechEvent.speechListner;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

@SuppressLint("NewApi")
class MyRecognitionListener  implements RecognitionListener {

	private ArrayList _SpeechHeard;
	private List _listeners = new ArrayList();

	public synchronized void addSpeechHeardListener( speechListner l ) {
        Log.d("t", "t");
		_listeners.add( l );
    }
    
    public synchronized void removeSpeechHeardListener( speechListner l ) {
        _listeners.remove( l );
    }
    
    private synchronized void _fireSpeechHeardEvent(ArrayList strlist) {
    	speechEvent  spHeard = new speechEvent ( this ,strlist);
        Iterator listeners = _listeners.iterator();
        while( listeners.hasNext() ) {
            ( (speechListner) listeners.next() ).speechEvent(spHeard );
        }
    }
      
   
    
	@Override
	 public void onBeginningOfSpeech() {
	  Log.d("Speech", "onBeginningOfSpeech");
	 }

	 @Override
	 public void onBufferReceived(byte[] buffer) {
	  Log.d("Speech", "onBufferReceived");
	 }

	 @Override
	 public void onEndOfSpeech() {
	  Log.d("Speech", "onEndOfSpeech");
	 }

	 @Override
	 public void onError(int error) {
//		 if ((error == SpeechRecognizer.ERROR_NO_MATCH)
//				  || (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)){
//
//				  }  
//				  else if(error==SpeechRecognizer.ERROR_RECOGNIZER_BUSY){
//
//				  }
//				  recognizer.startListening(recognizerIntent);
//				}  
	  Log.d("Speech", "onError + Error code is "+error);
	 }

	 @Override
	 public void onEvent(int eventType, Bundle params) {
	  Log.d("Speech", "onEvent");
	 }

	 @Override
	 public void onPartialResults(Bundle partialResults) {
	  Log.d("Speech", "onPartialResults");
	 }

	 @Override
	 public void onReadyForSpeech(Bundle params) {
	  Log.d("Speech", "onReadyForSpeech");
	 }
	 

	 @Override
	 public void onResults(Bundle results) {
	  Log.d("Speech", "onResults");
	  ArrayList strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	  _fireSpeechHeardEvent(strlist);
	  
	  for (int i = 0; i < strlist.size();i++ ) {
	   Log.d("SpeechClass", "result=" + strlist.get(i));
	   
	  }
	 }

	 @Override
	 public void onRmsChanged(float rmsdB) {
	  Log.d("Speech", "onRmsChanged");
	 }
	 
	}





