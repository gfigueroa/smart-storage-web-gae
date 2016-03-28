/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.datastore_simple;

import java.io.Serializable;

/**
 * This class represents a simple version of the AlarmWarningMessage table.
 * It is kept simple to return only some information to mobile apps.
 * 
 */

@SuppressWarnings("serial")
public class AlarmWarningMessageSimple implements Serializable {
    
	public String key;
	public String storageDeviceSerialNumber;
	public String alarmWarningMessage;
	public Integer dehumidiferMachine;
	public String alarmWarningMessageTime;
    
    /**
     * ChannelSimple constructor.
     * @param key
     * @param storageDeviceSerialNumber
     * @param alarmWarningMessage
     * @param dehumidifierMachine
     * @param alarmWarningMessageTime
     */
    public AlarmWarningMessageSimple(
    		String key,
    		String storageDeviceSerialNumber,
    		String alarmWarningMessage,
    		Integer dehumidifierMachine,
    		String alarmWarningMessageTime) {

    	this.key = key;
    	this.storageDeviceSerialNumber = storageDeviceSerialNumber;
    	this.alarmWarningMessage = alarmWarningMessage;
    	this.dehumidiferMachine = dehumidifierMachine;
    	this.alarmWarningMessageTime = alarmWarningMessageTime;
    }
    
    /**
     * Compare this AlarmWarningMessage with another AlarmWarningMessage
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this AlarmWarningMessage, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof AlarmWarningMessageSimple ) ) return false;
        AlarmWarningMessageSimple srs = (AlarmWarningMessageSimple) o;
        return this.key.equals(srs.key);
    }
    
}
