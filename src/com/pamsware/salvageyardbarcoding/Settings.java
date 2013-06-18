package com.pamsware.salvageyardbarcoding;


import com.pamsware.salvageyardbarcoding.KTSyncData;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class Settings extends PreferenceActivity {
	
    private static final String TAG = "Settings";	
 
    
   
    private	PreferenceScreen configPref;
    private	PreferenceScreen infoPref;    
    private	PreferenceScreen otherPref;  
    
    private	PreferenceScreen symbolPref;
    private	PreferenceScreen optionPref;
    private	PreferenceScreen scanPref;
    private	PreferenceScreen dataPref;
    private	PreferenceScreen btPref;
    private	PreferenceScreen hidPref;
    private	PreferenceScreen systemPref;
    private	PreferenceScreen msrPref;   
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_BARCODE 		= 1;
    public static final int MESSAGE_OPTION 			= 2;
    public static final int MESSAGE_SCANOPTION 		= 3;
    public static final int MESSAGE_DATAPROCESS 	= 4;
    public static final int MESSAGE_BLUETOOTH 		= 5;    
    public static final int MESSAGE_HID 			= 6;
    public static final int MESSAGE_MSR 			= 7;    
    public static final int MESSAGE_SYSTEM 			= 8;
 
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
        setPreferenceScreen(createPreferenceHierarchy());
        
        KTSyncData.mKScan.mSettingHandler = mHandler;
        KTSyncData.mKScan.mSettingContext = this;     

    }
   
    @Override     
    protected void onResume() {
        super.onResume();          
        
        Log.d(TAG, "onResume");
        
        if ( KTSyncData.bForceTerminate )finish();
    }

    
    @SuppressWarnings("deprecation")
	private PreferenceScreen createPreferenceHierarchy() {
        // Root
        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
  
        // version preferences 
        PreferenceCategory versionCat = new PreferenceCategory(this);
        versionCat.setTitle(R.string.version);
        root.addPreference(versionCat);
               
       
        otherPref = getPreferenceManager().createPreferenceScreen(this);
        otherPref.setKey("other_preference");
        otherPref.setTitle(R.string.title_other_preference);
        otherPref.setSummary(R.string.summary_other_preference);
        versionCat.addPreference(otherPref);
        
        OtherSettings();
        
        if ( KTSyncData.bIsSLEDConnected ) {
        	configPref = getPreferenceManager().createPreferenceScreen(this);
        	configPref.setKey("config_preference");
        	configPref.setTitle(R.string.title_config_preference);
        	configPref.setSummary(R.string.summary_config_preference);
        	versionCat.addPreference(configPref);
        
        	ConfigSettings();
        }
        
        infoPref = getPreferenceManager().createPreferenceScreen(this);
        infoPref.setKey("info_preference");
        infoPref.setTitle(R.string.title_info_preference);
        infoPref.setSummary(R.string.summary_info_preference);
        versionCat.addPreference(infoPref);

        KDCInfo();
        
        return root; 
}
        
    
    
    @SuppressWarnings("deprecation")
	public void KDCInfo()
    {
    	
    	Log.d(TAG, "KDC Info");
    	
    	
        // Serial Number preference
		PreferenceScreen serialnoPref = getPreferenceManager().createPreferenceScreen(this);
        serialnoPref.setTitle(R.string.title_serialno_preference);
        
        String tempstring = new String(KTSyncData.SerialNumber, 0,10);

        serialnoPref.setSummary(tempstring);
        infoPref.addPreference(serialnoPref);    
        
        // Firmware version preference
		PreferenceScreen fwversionPref = getPreferenceManager().createPreferenceScreen(this);
        fwversionPref.setTitle(R.string.title_fwversion_preference);
        
        tempstring = new String(KTSyncData.FWVersion, 0, 10);
        if ( KTSyncData.FWBuild[0] != (byte) '[') {
        	tempstring += new String("[");
        	tempstring += new String(KTSyncData.FWBuild, 0, 8);
        	tempstring += new String("]");
        } else 
        	tempstring += new String(KTSyncData.FWBuild, 0, 6);
                
        fwversionPref.setSummary(tempstring);
        infoPref.addPreference(fwversionPref);          
               
        // MAC address preference
        PreferenceScreen macaddressPref = getPreferenceManager().createPreferenceScreen(this);
        macaddressPref.setTitle(R.string.title_macaddress_preference);
        
        tempstring = new String(KTSyncData.MACAddress, 0, 12);
        
        macaddressPref.setSummary(tempstring);
        infoPref.addPreference(macaddressPref); 
        
        // Firmware version preference
        PreferenceScreen btversionPref = getPreferenceManager().createPreferenceScreen(this);
        btversionPref.setTitle(R.string.title_btversion_preference);
        
        tempstring = new String(KTSyncData.BTVersion, 0, 5);
        
        btversionPref.setSummary(tempstring);
        infoPref.addPreference(btversionPref);    
        
        // Firmware version preference
        PreferenceScreen memoryPref = getPreferenceManager().createPreferenceScreen(this);
        memoryPref.setTitle(R.string.title_memory_preference);
        
        tempstring = String.format("%d Stored/%d KB Left", KTSyncData.StoredBarcode, KTSyncData.MemoryLeft);

        memoryPref.setSummary(tempstring);
        infoPref.addPreference(memoryPref);      
        
    	//Battery
        PreferenceScreen batteryPref = getPreferenceManager().createPreferenceScreen(this);
        batteryPref.setTitle(R.string.title_battery_preference);        
        tempstring = String.format("%d Left", KTSyncData.BatteryValue);

        batteryPref.setSummary(tempstring);
        infoPref.addPreference(batteryPref);         
	}

    public void OtherSettings()       
    {  
        // Auto connect
        CheckBoxPreference autoconnectPref = new CheckBoxPreference(this);
        autoconnectPref.setKey("Auto Connect");
        autoconnectPref.setTitle(R.string.title_autoconnect_preference);
        autoconnectPref.setSummary(R.string.summary_autoconnect_preference);
        otherPref.addPreference(autoconnectPref); 
    	
    }
	public void GetPreferences()
	{
		//String tempstring;
		byte[] temp;
		
		SharedPreferences app_preferences =     	
			PreferenceManager.getDefaultSharedPreferences(this);
		
		KTSyncData.AttachTimestamp = app_preferences.getBoolean("AttachTimeStamp", false);
		KTSyncData.AttachType = app_preferences.getBoolean("AttachBarcodeType", false);
		KTSyncData.AttachSerialNumber = app_preferences.getBoolean("AttachSerialNumber", false);
		temp = app_preferences.getString("Data Delimiter", "4").getBytes();
		KTSyncData.DataDelimiter = temp[0] - '0';
		temp = app_preferences.getString("Record Delimiter", "1").getBytes();
		KTSyncData.RecordDelimiter = temp[0] - '0';				
		KTSyncData.AutoConnect = app_preferences.getBoolean("Auto Connect", false);
		
		temp = app_preferences.getString("Data Destination", "1").getBytes();
		KTSyncData.Destination = temp[0] - '0';	

		KTSyncData.AttachLocation = app_preferences.getBoolean("AttachLocationData", false);		
		KTSyncData.SyncNonCompliant = app_preferences.getBoolean("SyncNonCompliant", false);
		KTSyncData.AttachQuantity = app_preferences.getBoolean("AttachQuantity", false);
		
		KTSyncData.EraseMemory = app_preferences.getBoolean("EraseMemory", false);				
		
		KTSyncData.EmailTo = app_preferences.getString("EmailTo", "");
		KTSyncData.EmailCc = app_preferences.getString("EmailCc", "");
		KTSyncData.EmailSubject = app_preferences.getString("EmailSubject", "Barcode data");	
		KTSyncData.EmailText = app_preferences.getString("EmailText", "Sent from android phone");		
	
	}  
	
    @Override
    public void onDestroy() {
    	Log.d(TAG, "onDestry");
    	
    	GetPreferences();
        super.onDestroy();       
    }      
    
    @SuppressWarnings("deprecation")
	public void ConfigSettings()
    {
 	
        // Symbology Selection
        symbolPref = getPreferenceManager().createPreferenceScreen(this);
        symbolPref.setKey("symbol_preference");
        symbolPref.setTitle(R.string.title_symbol_preference);
        symbolPref.setSummary(R.string.summary_symbol_preference);
        configPref.addPreference(symbolPref);

        SymbologySelection();

        // Option selection
        optionPref = getPreferenceManager().createPreferenceScreen(this);
        optionPref.setKey("option_preference");
        optionPref.setTitle(R.string.title_option_preference);
        optionPref.setSummary(R.string.summary_option_preference);
        configPref.addPreference(optionPref);

        OptionSelection();
        
        // Scan Option selection
        scanPref = getPreferenceManager().createPreferenceScreen(this);
        scanPref.setKey("scan_preference");
        scanPref.setTitle(R.string.title_scan_preference);
        scanPref.setSummary(R.string.summary_scan_preference);
        configPref.addPreference(scanPref);
        
        ScanOptions();
        
        // Data Process
        dataPref = getPreferenceManager().createPreferenceScreen(this);
        dataPref.setKey("data_preference");
        dataPref.setTitle(R.string.title_dataprocess_preference);
        dataPref.setSummary(R.string.summary_dataprocess_preference);
        configPref.addPreference(dataPref);

        DataProcess();
        
        // Bluetooth
        btPref = getPreferenceManager().createPreferenceScreen(this);
        btPref.setKey("bt_preference");
        btPref.setTitle(R.string.title_bt_preference);
        btPref.setSummary(R.string.summary_bt_preference);
        configPref.addPreference(btPref);

        BluetoothSettings();
        
        // HID
        hidPref = getPreferenceManager().createPreferenceScreen(this);
        hidPref.setKey("hid_preference");
        hidPref.setTitle(R.string.title_hid_preference);
        hidPref.setSummary(R.string.summary_hid_preference);
        configPref.addPreference(hidPref);

        HIDSettings();        
        
        // MSR
        msrPref = getPreferenceManager().createPreferenceScreen(this);
        msrPref.setKey("msr_preference");
        msrPref.setTitle(R.string.title_msr_preference);
        msrPref.setSummary(R.string.summary_msr_preference);
        configPref.addPreference(msrPref);
        
        MSRSettings();
       
        // SLED
        systemPref = getPreferenceManager().createPreferenceScreen(this);
        systemPref.setKey("system_preference");
        systemPref.setTitle(R.string.title_system_preference);
        systemPref.setSummary(R.string.summary_system_preference);
        configPref.addPreference(systemPref);
        
        SystemSettings();    	

    }
    
    private void SymbologySelection()
    {
        symbolPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	KTSyncData.mKScan.GetSymbolOption();

                return true;
            }
        }); 
    }
    
/////////////////////////////////////////////////////////////////////////////////////
    private void OptionSelection()
    { 
        optionPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetBarcodeOption();  	
                return true;
            }
        });          

    }

////////////////////////////////////////////////////////////////////////////////////    
    private void ScanOptions()
    {   
        scanPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetScanOptions();        	
                return true;
            }
        });     	
    }
    
    private void DataProcess()
    {  
        dataPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetDataProcess();         	
                return true;
            }
        }); 
  
    }
    
    private void BluetoothSettings()
    {          
        btPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetBluetoothOptions();           	
                return true;
            }
        });         

    }
    
    private void HIDSettings()
    {
        hidPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetHIDSettings();
                return true;
            }
        }); 
    }
    
    private void SystemSettings()
    {
        systemPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetSystemSettings();          	
                return true;
            }
        });      	
    }
    
    private void PreKDCInfo()
    {
        systemPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetBatteryCapacity();          	
                return true;
            }
        });      	
    }
    
    private void MSRSettings()
    { 
        msrPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick( Preference preference) {
            	if ( KTSyncData.bIsConnected )	KTSyncData.mKScan.GetMSRSettings();
                return true;
            }
        });
    }
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
	        	case MESSAGE_BARCODE:
	        		Intent symbolActivity = new Intent(getBaseContext(),
	        				SetBarcode.class);
	            			startActivity(symbolActivity);
	            break;                 
	        	case MESSAGE_OPTION:
	        		Intent optionActivity = new Intent(getBaseContext(),
	        				BarcodeOption.class);
	            			startActivity(optionActivity);
	            break;              
              
	        	case MESSAGE_SCANOPTION:
	        		Intent scanActivity = new Intent(getBaseContext(),
	        				ScanOption.class);
	            			startActivity(scanActivity);
	            break;              
	        	case MESSAGE_DATAPROCESS:
	        		Intent dataActivity = new Intent(getBaseContext(),
	        				DataProcess.class);
	            			startActivity(dataActivity);
	            break;             
	        	case MESSAGE_BLUETOOTH:
	        		Intent btActivity = new Intent(getBaseContext(),
	        				BluetoothSetting.class);
	            			startActivity(btActivity);
	            break;            
	        	case MESSAGE_HID:
	        		Intent hidActivity = new Intent(getBaseContext(),
	        				HIDSetting.class);
	            			startActivity(hidActivity);
	            break;            
	        	case MESSAGE_MSR:
	        		Intent msrActivity = new Intent(getBaseContext(),
	        				MSRSetting.class);
                			startActivity(msrActivity);
                break;            
		        case MESSAGE_SYSTEM:
	                Intent systemActivity = new Intent(getBaseContext(),
	                        SystemSetting.class);
	                		startActivity(systemActivity);
		        	break;
	            default: break;
            }
        }
    };    
}
