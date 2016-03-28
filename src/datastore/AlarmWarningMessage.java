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
 * This class represents the AlarmWarningMessage table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class AlarmWarningMessage implements Serializable {
	
	public static enum AlarmWarningMessageStatus {
		ALERT, PROCESSED, RESOLVED
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Key storageDevice;

    @Persistent
    private Integer alarmWarningMessageDehumidifierMachine;

    @Persistent
    private AlarmWarningMessageStatus alarmWarningMessageStatus;
    
    @Persistent
    private String alarmWarningMessageNote;
    
    @Persistent
    private Date alarmWarningMessageCreationDate;
    
    @Persistent
    private Date alarmWarningMessageModificationDate;

    /**
     * AlarmWarningMessage constructor.
     * @param storageDevice
     * 			: StorageDevice key
     * @param alarmWarningMessageDehumidifierMachine
     * 			: AlarmWarningMessage dehumidifierMachine
     * @throws MissingRequiredFieldsException
     */
    public AlarmWarningMessage(
    		Key storageDevice,
    		Integer alarmWarningMessageDehumidifierMachine) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (alarmWarningMessageDehumidifierMachine == null || 
    			storageDevice == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.storageDevice = storageDevice;
        this.alarmWarningMessageDehumidifierMachine = alarmWarningMessageDehumidifierMachine;
        this.alarmWarningMessageStatus = AlarmWarningMessageStatus.ALERT; // Always begin as ALERT
        this.alarmWarningMessageNote = null;
        
        Date now = new Date();
        this.alarmWarningMessageCreationDate = now;
        this.alarmWarningMessageModificationDate = now;
    }

    /**
     * Get AlarmWarningMessage key.
     * @return AlarmWarningMessage key
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
     * Get AlarmWarningMessage dehumidifierMachine.
     * @return AlarmWarningMessage dehumidifierMachine
     */
    public Integer getAlarmWarningMessageDehumidifierMachine() {
        return alarmWarningMessageDehumidifierMachine;
    }
    
    /**
     * Returns an AlarmWarningMessageStatus based on its string representation
     * @param alarmWarningMessageStatusString
     * @return an AlarmWarningMessageStatus
     */
    public static AlarmWarningMessageStatus getAlarmWarningMessageStatusFromString(
    		String alarmWarningMessageStatusString) {
    	
    	if (alarmWarningMessageStatusString == null) {
    		return null;
    	}
    	
    	if (alarmWarningMessageStatusString.equalsIgnoreCase("alert")) {
    		return AlarmWarningMessageStatus.ALERT;
    	}
    	else if (alarmWarningMessageStatusString.equalsIgnoreCase("processed")) {
    		return AlarmWarningMessageStatus.PROCESSED;
    	}
    	else if (alarmWarningMessageStatusString.equalsIgnoreCase("resolved")) {
    		return AlarmWarningMessageStatus.RESOLVED;
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * Get AlarmWarningMessage status.
     * @return AlarmWarningMessage status
     */
    public AlarmWarningMessageStatus getAlarmWarningMessageStatus() {
    	return alarmWarningMessageStatus;
    }
    
    /**
     * Get AlarmWarningMessage note
     * @return AlarmWarningMessage note
     */
    public String getAlarmWarningMessageNote() {
    	return alarmWarningMessageNote;
    }
    
    /**
     * Get AlarmWarningMessage creation date.
     * @return AlarmWarningMessage creation date
     */
    public Date getAlarmWarningMessageCreationDate() {
    	return alarmWarningMessageCreationDate;
    }
    
    /**
     * Get AlarmWarningMessage modification date.
     * @return AlarmWarningMessage modification date
     */
    public Date getAlarmWarningMessageModificationDate() {
    	return alarmWarningMessageModificationDate;
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
        if ( !(o instanceof AlarmWarningMessage ) ) return false;
        AlarmWarningMessage dmp = (AlarmWarningMessage) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }

    /**
     * Set AlarmWarningMessage status.
     * @param alarmWarningMessageStatus
     * 			: AlarmWarningMessage status
     * @throws MissingRequiredFieldsException 
     */
    public void setAlarmWarningMessageStatus(
    		AlarmWarningMessageStatus alarmWarningMessageStatus) 
    				throws MissingRequiredFieldsException {
    	
    	if (alarmWarningMessageStatus == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmWarningMessageStatus is missing.");
    	}
    	
    	this.alarmWarningMessageStatus = alarmWarningMessageStatus;
    	this.alarmWarningMessageModificationDate = new Date();
    }
    
    /**
     * Set AlarmWarningMessage note.
     * @param alarmWarningMessageNote
     * 			: AlarmWarningMessage note
     */
    public void setAlarmWarningMessageNote(String alarmWarningMessageNote) {
    	this.alarmWarningMessageNote = alarmWarningMessageNote;
    	this.alarmWarningMessageModificationDate = new Date();
    }
}