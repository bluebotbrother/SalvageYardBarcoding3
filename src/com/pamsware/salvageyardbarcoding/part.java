package com.pamsware.salvageyardbarcoding;

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class part implements KvmSerializable {
	    public int InventoryID;
	    public String LocationCode;
	    public String CondAndOpts;
	    
	    public part(){}
	    

	    public part(int inventoryID, String locationCode, String condAndOpts) {
	        
	    	InventoryID = inventoryID;
	    	LocationCode = locationCode;
	        CondAndOpts = condAndOpts;
	    }


	    public Object getProperty(int arg0) {
	        
	        switch(arg0)
	        {
	        case 0:
	            return InventoryID;
	        case 1:
	            return LocationCode;
	        case 2:
	            return CondAndOpts;
	        }
	        
	        return null;
	    }

	    public int getPropertyCount() {
	        return 3;
	    }

	    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
	        switch(index)
	        {
	        case 0:
	            info.type = PropertyInfo.INTEGER_CLASS;
	            info.name = "InventoryID";
	            break;
	        case 1:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "LocationCode";
	            break;
	        case 2:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "CondAndOpts";
	            break;
	        default:break;
	        }
	    }

	    public void setProperty(int index, Object value) {
	        switch(index)
	        {
	        case 0:
	        	InventoryID = Integer.parseInt(value.toString());
	            break;
	        case 1:
	        	LocationCode = value.toString();
	            break;
	        case 2:
	        	CondAndOpts = value.toString();
	            break;
	        default:
	            break;
	        }
	    }

}
