/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package webservices.datastore_simple;

import java.io.Serializable;

/**
 * This class represents a simple version of the AlarmTriggerMessage table.
 * It is kept simple to return only some information to mobile apps.
 * 
 */

@SuppressWarnings("serial")
public class AlarmTriggerMessageSimple implements Serializable {
    
	public String key;
	public String storageDeviceSerialNumber;
	public String alarmTriggerMessage;
	public String alarmTriggerMessageTime;
	public SensorReadingSimple sensorReading;
    
    /**
     * ChannelSimple constructor.
     * @param key
     * @param storageDeviceSerialNumber
     * @param alarmTriggerMessage
     * @param alarmTriggerMessageTime
     * @param sensorReading
     */
    public AlarmTriggerMessageSimple(
    		String key,
    		String storageDeviceSerialNumber,
    		String alarmTriggerMessage,
    		String alarmTriggerMessageTime,
    		SensorReadingSimple sensorReading) {

    	this.key = key;
    	this.storageDeviceSerialNumber = storageDeviceSerialNumber;
    	this.alarmTriggerMessage = alarmTriggerMessage;
    	this.alarmTriggerMessageTime = alarmTriggerMessageTime;
    	this.sensorReading = sensorReading;
    }
    
    /**
     * Compare this AlarmTriggerMessage with another AlarmTriggerMessage
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this AlarmTriggerMessage, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof AlarmTriggerMessageSimple ) ) return false;
        AlarmTriggerMessageSimple srs = (AlarmTriggerMessageSimple) o;
        return this.key.equals(srs.key);
    }
    
}
