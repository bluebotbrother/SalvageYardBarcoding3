package com.pamsware.salvageyardbarcoding;

import java.util.ArrayList;
import java.util.EventObject;

public class speechEvent extends EventObject {
	 private ArrayList _speechHeard;
	 
	 public speechEvent(Object source, ArrayList SpeechHeard ) {
			super(source);
			_speechHeard=SpeechHeard;
		}
		 public ArrayList speechHeard(){
			return _speechHeard;
			 
		 }
		 public interface speechListner {
			 public void speechEvent(speechEvent event);
		 }


}
