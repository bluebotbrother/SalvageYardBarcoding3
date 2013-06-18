package com.pamsware.salvageyardbarcoding;

import java.util.concurrent.ExecutionException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import android.os.AsyncTask;
import android.util.Log;
import com.pamsware.salvageyardbarcoding.MoveResults;

public class MovePart {
	  
    private String StrTagID;
    private String UserID;
    private String StrLocationCode;
    
    private MoveResults MR = new MoveResults();
    
    	/**
	 * @param strTagId - String of the current scanned bar code
	 * @param strLocationCode - Location part is being moved to
	 * @param userID - ASP.Net GUID of user that is logged in
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public MoveResults movePart ( String strTagId, String strLocationCode, String userID ) throws InterruptedException, ExecutionException  {
		    	    
		StrTagID=strTagId;
		StrLocationCode=strLocationCode;
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
          String BarCodingSOAP_ACTION1 = "http://barcodingtest.stcloud.pamsauto.com/webservices/MovePart";
          String BarCodingNAMESPACE = "http://barcodingtest.stcloud.pamsauto.com/webservices/";
          String BarCodingMETHOD_NAME1 = "MovePart";
          String BarCodingURL = "http://barcodingtest.stcloud.pamsauto.com/WebServices/MovePart.asmx";
          
        try {
      	   SoapObject Request = new SoapObject(BarCodingNAMESPACE, BarCodingMETHOD_NAME1);
      	   
	      	 PropertyInfo pi = new PropertyInfo();
	           
	            pi.setName("MoveResults");
	            pi.setValue(MR);
	            pi.setType(MR.getClass());
	            Request.addProperty(pi);
	             
	            Request.addProperty("TagID", StrTagID);
	            Request.addProperty("NewLocation", StrLocationCode);
	            Request.addProperty("UserID", UserID);

	            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            soapEnvelope.dotNet = true;
	            soapEnvelope.setOutputSoapObject(Request);
	            soapEnvelope.addMapping(BarCodingNAMESPACE, "MoveResults",new MoveResults().getClass());
	            
	            HttpTransportSE transport= new HttpTransportSE(BarCodingURL);

	            transport.call(BarCodingSOAP_ACTION1, soapEnvelope);
	            SoapObject response = (SoapObject)soapEnvelope.getResponse();
	           
	            MR.WasMoveSuccessFul = Boolean.parseBoolean(response.getProperty(0).toString());
	            MR.MoveFailureReason = response.getProperty(1).toString();
	              
      } catch (Exception e) {
            e.printStackTrace();
      }
        
      }
  }


