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
 * This class represents the AlarmWarningStorageDevice table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class AlarmWarningStorageDevice implements Serializable {
	
	public static enum AlarmWarningStorageDeviceStatus {
		ALERT, PROCESSED, RESOLVED
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Key storageDevice;

    @Persistent
    private Integer alarmWarningStorageDeviceDehumidifierMachine;

    @Persistent
    private AlarmWarningStorageDeviceStatus alarmWarningStorageDeviceStatus;
    
    @Persistent
    private String alarmWarningStorageDeviceNote;
    
    @Persistent
    private Date alarmWarningStorageDeviceCreationDate;
    
    @Persistent
    private Date alarmWarningStorageDeviceModificationDate;

    /**
     * AlarmWarningStorageDevice constructor.
     * @param storageDevice
     * 			: StorageDevice key
     * @param alarmWarningStorageDeviceDehumidifierMachine
     * 			: AlarmWarningStorageDevice dehumidifierMachine
     * @throws MissingRequiredFieldsException
     */
    public AlarmWarningStorageDevice(
    		Key storageDevice,
    		Integer alarmWarningStorageDeviceDehumidifierMachine) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (alarmWarningStorageDeviceDehumidifierMachine == null || 
    			storageDevice == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.storageDevice = storageDevice;
        this.alarmWarningStorageDeviceDehumidifierMachine = alarmWarningStorageDeviceDehumidifierMachine;
        this.alarmWarningStorageDeviceStatus = AlarmWarningStorageDeviceStatus.ALERT; // Always begin as ALERT
        this.alarmWarningStorageDeviceNote = null;
        
        Date now = new Date();
        this.alarmWarningStorageDeviceCreationDate = now;
        this.alarmWarningStorageDeviceModificationDate = now;
    }

    /**
     * Get AlarmWarningStorageDevice key.
     * @return AlarmWarningStorageDevice key
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
     * Get AlarmWarningStorageDevice dehumidifierMachine.
     * @return AlarmWarningStorageDevice dehumidifierMachine
     */
    public Integer getAlarmWarningStorageDeviceDehumidifierMachine() {
        return alarmWarningStorageDeviceDehumidifierMachine;
    }
    
    /**
     * Returns an AlarmWarningStorageDeviceStatus based on its string representation
     * @param alarmWarningStorageDeviceStatusString
     * @return an AlarmWarningStorageDeviceStatus
     */
    public static AlarmWarningStorageDeviceStatus getAlarmWarningStorageDeviceStatusFromString(
    		String alarmWarningStorageDeviceStatusString) {
    	
    	if (alarmWarningStorageDeviceStatusString == null) {
    		return null;
    	}
    	
    	if (alarmWarningStorageDeviceStatusString.equalsIgnoreCase("alert")) {
    		return AlarmWarningStorageDeviceStatus.ALERT;
    	}
    	else if (alarmWarningStorageDeviceStatusString.equalsIgnoreCase("processed")) {
    		return AlarmWarningStorageDeviceStatus.PROCESSED;
    	}
    	else if (alarmWarningStorageDeviceStatusString.equalsIgnoreCase("resolved")) {
    		return AlarmWarningStorageDeviceStatus.RESOLVED;
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * Get AlarmWarningStorageDevice status.
     * @return AlarmWarningStorageDevice status
     */
    public AlarmWarningStorageDeviceStatus getAlarmWarningStorageDeviceStatus() {
    	return alarmWarningStorageDeviceStatus;
    }
    
    /**
     * Get AlarmWarningStorageDevice note
     * @return AlarmWarningStorageDevice note
     */
    public String getAlarmWarningStorageDeviceNote() {
    	return alarmWarningStorageDeviceNote;
    }
    
    /**
     * Get AlarmWarningStorageDevice creation date.
     * @return AlarmWarningStorageDevice creation date
     */
    public Date getAlarmWarningStorageDeviceCreationDate() {
    	return alarmWarningStorageDeviceCreationDate;
    }
    
    /**
     * Get AlarmWarningStorageDevice modification date.
     * @return AlarmWarningStorageDevice modification date
     */
    public Date getAlarmWarningStorageDeviceModificationDate() {
    	return alarmWarningStorageDeviceModificationDate;
    }
    
	/**
     * Compare this AlarmWarningStorageDevice with another AlarmWarningStorageDevice
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this AlarmWarningStorageDevice, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof AlarmWarningStorageDevice ) ) return false;
        AlarmWarningStorageDevice dmp = (AlarmWarningStorageDevice) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }

    /**
     * Set AlarmWarningStorageDevice status.
     * @param alarmWarningStorageDeviceStatus
     * 			: AlarmWarningStorageDevice status
     * @throws MissingRequiredFieldsException 
     */
    public void setAlarmWarningStorageDeviceStatus(
    		AlarmWarningStorageDeviceStatus alarmWarningStorageDeviceStatus) 
    				throws MissingRequiredFieldsException {
    	
    	if (alarmWarningStorageDeviceStatus == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmWarningStorageDeviceStatus is missing.");
    	}
    	
    	this.alarmWarningStorageDeviceStatus = alarmWarningStorageDeviceStatus;
    	this.alarmWarningStorageDeviceModificationDate = new Date();
    }
    
    /**
     * Set AlarmWarningStorageDevice note.
     * @param alarmWarningStorageDeviceNote
     * 			: AlarmWarningStorageDevice note
     */
    public void setAlarmWarningStorageDeviceNote(String alarmWarningStorageDeviceNote) {
    	this.alarmWarningStorageDeviceNote = alarmWarningStorageDeviceNote;
    	this.alarmWarningStorageDeviceModificationDate = new Date();
    }
}