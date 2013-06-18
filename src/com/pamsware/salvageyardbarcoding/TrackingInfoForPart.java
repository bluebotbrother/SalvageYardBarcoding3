package com.pamsware.salvageyardbarcoding;

import java.util.concurrent.ExecutionException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;
import android.util.Log;



public class TrackingInfoForPart {
	  
    private String StrTagID;
    private String UserID;
    private String StrTrackingInfo;
    
    private TrackingInfoForPartResults MR = new TrackingInfoForPartResults();
    
    	/**
	 * @param strTagId - String of the current scanned bar code
	 * @param strLocationCode - Location part is being moved to
	 * @param userID - ASP.Net GUID of user that is logged in
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public TrackingInfoForPartResults trackinginfoforpart ( String strTagId, String strTrackingInfo, String userID ) throws InterruptedException, ExecutionException  {
		    	    
		StrTagID=strTagId;
		StrTrackingInfo=strTrackingInfo;
		UserID=userID;
		
		 AsyncCallWS task = new AsyncCallWS();
         task.execute();
         task.get();
         
         return MR;		
	}
	
	 private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
	        @Override
	        protected Void doInBackground(Void... params) {
	            Log.i("async", "doInBackground");
	            CallWebService();
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            Log.i("async", "onPostExecute");
	        }

	        @Override
	        protected void onPreExecute() {
	            Log.i("async", "onPreExecute");
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {
	            Log.i("async", "onProgressUpdate");
	        }

	       
	    }
	  public void CallWebService()
      {
          String BarCodingSOAP_ACTION1 = "http://barcodingtest.stcloud.pamsauto.com/webservices/TrackingInfoForPart";
          String BarCodingNAMESPACE = "http://barcodingtest.stcloud.pamsauto.com/webservices/";
          String BarCodingMETHOD_NAME1 = "TrackingInfoForPart";
          String BarCodingURL = "http://barcodingtest.stcloud.pamsauto.com/WebServices/TrackingInfoForPart.asmx";
          
        try {
      	   SoapObject Request = new SoapObject(BarCodingNAMESPACE, BarCodingMETHOD_NAME1);
      	   
	      	 PropertyInfo pi = new PropertyInfo();
	           
	            pi.setName("TrackingInfoForPartResults");
	            pi.setValue(MR);
	            pi.setType(MR.getClass());
	            Request.addProperty(pi);
	             
	            Request.addProperty("TagID", StrTagID);
	            Request.addProperty("TrackingInfo", StrTrackingInfo);
	            Request.addProperty("UserID", UserID);

	            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            soapEnvelope.dotNet = true;
	            soapEnvelope.setOutputSoapObject(Request);
	            soapEnvelope.addMapping(BarCodingNAMESPACE, "TrackingInfoForPartResults",new TrackingInfoForPartResults().getClass());
	            
	            HttpTransportSE transport= new HttpTransportSE(BarCodingURL);

	            transport.call(BarCodingSOAP_ACTION1, soapEnvelope);
	            SoapObject response = (SoapObject)soapEnvelope.getResponse();
	           
	            MR.WasCallSuccessFul = Boolean.parseBoolean(response.getProperty(0).toString());
	            MR.CallFailureReason = response.getProperty(1).toString();
	              
      } catch (Exception e) {
            e.printStackTrace();
      }
        
      }
  }


