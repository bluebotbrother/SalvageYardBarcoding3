package com.pamsware.salvageyardbarcoding;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class TrackingInfoForPartResults implements KvmSerializable {
	    public boolean WasCallSuccessFul;
	    public String CallFailureReason;
	        
	    public TrackingInfoForPartResults(){}
	    

	    public TrackingInfoForPartResults(boolean wasCallSuccessFul, String callFailureReason) {
	        
	    	WasCallSuccessFul = wasCallSuccessFul;
	    	CallFailureReason = callFailureReason;
	       
	    }


	    public Object getProperty(int arg0) {
	        
	        switch(arg0)
	        {
	        case 0:
	            return WasCallSuccessFul;
	        case 1:
	            return CallFailureReason;
	        }
	        
	        return null;
	    }

	    public int getPropertyCount() {
	        return 2;
	    }

	    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
	        switch(index)
	        {
	        case 0:
	            info.type = PropertyInfo.BOOLEAN_CLASS;
	            info.name = "WasCallSuccessFul";
	            break;
	        case 1:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "CallFailureReason";
	            break;
	        default:break;
	        }
	    }

	    public void setProperty(int index, Object value) {
	        switch(index)
	        {
	        case 0:
	        	WasCallSuccessFul = Boolean.parseBoolean(value.toString());
	            break;
	        case 1:
	        	CallFailureReason = value.toString();
	            break;
	        default:
	            break;
	        }
	    }

	}


