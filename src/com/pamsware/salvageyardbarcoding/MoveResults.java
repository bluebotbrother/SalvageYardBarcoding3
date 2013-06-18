package com.pamsware.salvageyardbarcoding;

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class MoveResults implements KvmSerializable {
    public boolean WasMoveSuccessFul;
    public String MoveFailureReason;
        
    public MoveResults(){}
    

    public MoveResults(boolean wasMoveSuccessFul, String moveFailureReason) {
        
    	WasMoveSuccessFul = wasMoveSuccessFul;
    	MoveFailureReason = moveFailureReason;
       
    }


    public Object getProperty(int arg0) {
        
        switch(arg0)
        {
        case 0:
            return WasMoveSuccessFul;
        case 1:
            return MoveFailureReason;
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
            info.name = "WasMoveSuccessFul";
            break;
        case 1:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "MoveFailureReason";
            break;
        default:break;
        }
    }

    public void setProperty(int index, Object value) {
        switch(index)
        {
        case 0:
        	WasMoveSuccessFul = Boolean.parseBoolean(value.toString());
            break;
        case 1:
        	MoveFailureReason = value.toString();
            break;
        default:
            break;
        }
    }

}
