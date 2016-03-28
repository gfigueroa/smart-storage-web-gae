/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the AlarmTriggerMessage table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class AlarmTriggerMessage implements Serializable {
	
	public static enum AlarmTriggerMessageStatus {
		ALERT, PROCESSED, RESOLVED
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Key storageDevice;
	
	@Persistent 
	private Key sensorReading;

    @Persistent
    private AlarmTriggerMessageStatus alarmTriggerMessageStatus;
    
    @Persistent
    private String alarmTriggerMessageNote;
    
    @Persistent
    private Date alarmTriggerMessageCreationDate;
    
    @Persistent
    private Date alarmTriggerMessageModificationDate;

    /**
     * AlarmTriggerMessage constructor.
     * @param storageDevice
     * 			: StorageDevice key
     * @param sensorReading
     * 			: SensorReading key
     * @throws MissingRequiredFieldsException
     */
    public AlarmTriggerMessage(
    		Key storageDevice,
    		Key sensorReading) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (storageDevice == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.storageDevice = storageDevice;
        this.sensorReading = sensorReading;
        this.alarmTriggerMessageStatus = AlarmTriggerMessageStatus.ALERT; // Always begin as ALERT
        
        Date now = new Date();
        this.alarmTriggerMessageCreationDate = now;
        this.alarmTriggerMessageModificationDate = now;
    }

    /**
     * Get AlarmTriggerMessage key.
     * @return AlarmTriggerMessage key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get StorageDevice key.
     * @return StorageDevice key
     */
    public Key getStorageDevice() {
        return storageDevice;
    }
    
    /**
     * Get SensorReading
     * @return SensorReading key
     */
    public Key getSensorReading() {
        return sensorReading;
    }
    
    /**
     * Returns an AlarmTriggerMessageStatus based on its string representation
     * @param alarmTriggerMessageStatusString
     * @return an AlarmTriggerMessageStatus
     */
    public static AlarmTriggerMessageStatus getAlarmTriggerMessageStatusFromString(
    		String alarmTriggerMessageStatusString) {
    	
    	if (alarmTriggerMessageStatusString == null) {
    		return null;
    	}
    	
    	if (alarmTriggerMessageStatusString.equalsIgnoreCase("alert")) {
    		return AlarmTriggerMessageStatus.ALERT;
    	}
    	else if (alarmTriggerMessageStatusString.equalsIgnoreCase("processed")) {
    		return AlarmTriggerMessageStatus.PROCESSED;
    	}
    	else if (alarmTriggerMessageStatusString.equalsIgnoreCase("resolved")) {
    		return AlarmTriggerMessageStatus.RESOLVED;
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * Get AlarmTriggerMessage status.
     * @return AlarmTriggerMessage status
     */
    public AlarmTriggerMessageStatus getAlarmTriggerMessageStatus() {
    	return alarmTriggerMessageStatus;
    }
    
    /**
	 * @return the alarmTriggerMessageNote
	 */
	public String getAlarmTriggerMessageNote() {
		return alarmTriggerMessageNote;
	}

	/**
     * Get AlarmTriggerMessage creation date.
     * @return AlarmTriggerMessage creation date
     */
    public Date getAlarmTriggerMessageCreationDate() {
    	return alarmTriggerMessageCreationDate;
    }
    
    /**
     * Get AlarmTriggerMessage modification date.
     * @return AlarmTriggerMessage modification date
     */
    public Date getAlarmTriggerMessageModificationDate() {
    	return alarmTriggerMessageModificationDate;
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
        if ( !(o instanceof AlarmTriggerMessage ) ) return false;
        AlarmTriggerMessage dmp = (AlarmTriggerMessage) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }

    /**
     * Set AlarmTriggerMessage status.
     * @param alarmTriggerMessageStatus
     * 			: AlarmTriggerMessage status
     * @throws MissingRequiredFieldsException 
     */
    public void setAlarmTriggerMessageStatus(
    		AlarmTriggerMessageStatus alarmTriggerMessageStatus) 
    				throws MissingRequiredFieldsException {
    	
    	if (alarmTriggerMessageStatus == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmTriggerMessageStatus is missing.");
    	}
    	
    	this.alarmTriggerMessageStatus = alarmTriggerMessageStatus;
    	this.alarmTriggerMessageModificationDate = new Date();
    }
    

	/**
	 * @param alarmTriggerMessageNote the alarmTriggerMessageNote to set
	 */
	public void setAlarmTriggerMessageNote(String alarmTriggerMessageNote) {
		this.alarmTriggerMessageNote = alarmTriggerMessageNote;
		this.alarmTriggerMessageModificationDate = new Date();
	}
}