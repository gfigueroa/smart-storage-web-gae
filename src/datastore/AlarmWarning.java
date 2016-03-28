/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the AlarmWarning table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class AlarmWarning implements Serializable, Comparable<AlarmWarning> {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

    @Persistent
    private Integer alarmWarningCode;

    @Persistent
    private String alarmWarningMessage;
    
    @Persistent
    private Integer alarmWarningCount;
    
    @Persistent
    private Integer alarmWarningMaxCount;
    
    @Persistent
    private Date alarmWarningCreationDate;
    
    @Persistent
    private Date alarmWarningModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<AlarmWarningMessage> alarmWarningMessages;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<AlarmWarningStorageDevice> alarmWarningStorageDevices;

    /**
     * AlarmWarning constructor.
     * @param alarmWarningCode
     * 			: AlarmWarning code
     * @param alarmWarningMessage
     * 			: AlarmWarning message
     * @param alarmWarningMaxCount
     * 			: AlarmWarning max count
     * @throws MissingRequiredFieldsException
     */
    public AlarmWarning(Integer alarmWarningCode, 
    		String alarmWarningMessage,
    		Integer alarmWarningMaxCount) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (alarmWarningCode == null || alarmWarningMessage == null ||
    			alarmWarningMaxCount == null ||
    			alarmWarningMessage.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.alarmWarningCode = alarmWarningCode;
        this.alarmWarningMessage = alarmWarningMessage;
        
        this.alarmWarningMessages = new ArrayList<>();
        this.alarmWarningStorageDevices = new ArrayList<>();
        
        this.alarmWarningCount = 0; // Initialize in 0
        
        this.alarmWarningMaxCount = alarmWarningMaxCount;
        
        Date now = new Date();
        this.alarmWarningCreationDate = now;
        this.alarmWarningModificationDate = now;
    }

    /**
     * Get AlarmWarning key.
     * @return AlarmWarning key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get AlarmWarning name.
     * @return AlarmWarning name
     */
    public Integer getAlarmWarningCode() {
        return alarmWarningCode;
    }
    
    /**
     * Get AlarmWarning message.
     * @return AlarmWarning message
     */
    public String getAlarmWarningMessage() {
    	return alarmWarningMessage;
    }
    
	/**
	 * @return the alarmWarningCount
	 */
	public Integer getAlarmWarningCount() {
		if (alarmWarningCount == null) {
			alarmWarningCount = 0;
		}
		return alarmWarningCount;
	}

	/**
	 * @return the alarmWarningMaxCount
	 */
	public Integer getAlarmWarningMaxCount() {
		if (alarmWarningMaxCount == null) {
			alarmWarningMaxCount = 3;
		}
		return alarmWarningMaxCount;
	}
    
    /**
     * Get AlarmWarning creation date.
     * @return AlarmWarning creation date
     */
    public Date getAlarmWarningCreationDate() {
    	return alarmWarningCreationDate;
    }
    
    /**
     * Get AlarmWarning modification date.
     * @return AlarmWarning modification date
     */
    public Date getAlarmWarningModificationDate() {
    	return alarmWarningModificationDate;
    }
    
    /**
     * Get AlarmWarningMessages
     * @return alarmWarningMessages
     */
    public ArrayList<AlarmWarningMessage> getAlarmWarningMessages() {
    	return alarmWarningMessages;
    }
    
    /**
     * Get AlarmWarningStorageDevices
     * @return alarmWarningStorageDevices
     */
    public ArrayList<AlarmWarningStorageDevice> getAlarmWarningStorageDevices() {
    	return alarmWarningStorageDevices;
    }
    
	/**
     * Compare this AlarmWarning with another AlarmWarning
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this AlarmWarning, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof AlarmWarning ) ) return false;
        AlarmWarning dmp = (AlarmWarning) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
	@Override
	public int compareTo(AlarmWarning alarmWarning) {
		return this.alarmWarningCode.compareTo(
				alarmWarning.getAlarmWarningCode());
	}
    
    /**
     * Set AlarmWarning name.
     * @param alarmWarningCode
     * 			: AlarmWarning name
     * @throws MissingRequiredFieldsException
     */
    public void setAlarmWarningCode(Integer alarmWarningCode)
    		throws MissingRequiredFieldsException {
    	if (alarmWarningCode == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmWarningCode is missing.");
    	}
    	this.alarmWarningCode = alarmWarningCode;
    	this.alarmWarningModificationDate = new Date();
    }
    
    /**
     * Set AlarmWarning message.
     * @param alarmWarningMessage
     * 			: AlarmWarning message
     * @throws MissingRequiredFieldsException 
     */
    public void setAlarmWarningMessage(String alarmWarningMessage) 
    		throws MissingRequiredFieldsException {
    	if (alarmWarningMessage == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmWarningMessage is missing.");
    	}
    	this.alarmWarningMessage = alarmWarningMessage;
    	this.alarmWarningModificationDate = new Date();
    }
    
    /**
	 * Reset the alarmWarningCount to 0;
	 */
	public void resetAlarmWarningCount() {
		this.alarmWarningCount = 0;
	}
	
	/**
	 * Increase the alarmWarningCount by 1.
	 * If the warning count is greater than or equal to the max count, then
	 * the function returns false.
	 * @return True if alarmWarningCount is less than alarmWarningMaxCount,
	 * 			False otherwise
	 */
	public boolean increaseAlarmWarningCount() {
		
		if (alarmWarningCount == null) {
			alarmWarningCount = 0;
		}
		if (alarmWarningMaxCount == null) {
			alarmWarningMaxCount = 3;
		}
		
		if (alarmWarningCount < alarmWarningMaxCount) {
			this.alarmWarningCount++;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Decrease the alarmWarningCount by 1.
	 * If the warning count is less than or equal to 0, then
	 * the function returns false.
	 * @return True if alarmWarningCount is greater than 0,
	 * 			False otherwise
	 */
	public boolean decreaseAlarmWarningCount() {
		
		if (alarmWarningCount == null) {
			alarmWarningCount = 0;
		}
				
		if (alarmWarningCount > 0) {
			this.alarmWarningCount--;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param alarmWarningMaxCount the alarmWarningMaxCount to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAlarmWarningMaxCount(Integer alarmWarningMaxCount) 
			throws MissingRequiredFieldsException {
    	if (alarmWarningMaxCount == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmWarningMaxCount is missing.");
    	}
		this.alarmWarningMaxCount = alarmWarningMaxCount;
		this.alarmWarningModificationDate = new Date();
	}
    
	/**
	 * Add a alarmWarningMessage to this AlarmWarning
	 * @param alarmWarningMessage
	 */
	public void addAlarmWarningMessage(AlarmWarningMessage alarmWarningMessage) {
		alarmWarningMessages.add(alarmWarningMessage);
	}
	
    /**
     * Remove a AlarmWarningMessage from the AlarmWarning.
     * @param alarmWarningMessage
     * 			: AlarmWarningMessage to be removed
     * @throws InexistentObjectException
     */
    public void removeAlarmWarningMessage(AlarmWarningMessage alarmWarningMessage) 
    		throws InexistentObjectException {
    	if (!alarmWarningMessages.remove(alarmWarningMessage)) {
    		throw new InexistentObjectException(
    				AlarmWarningMessage.class, "AlarmWarningMessage not found!");
    	}
    }
    
	/**
	 * Add a alarmWarningStorageDevice to this AlarmWarning
	 * @param alarmWarningStorageDevice
	 */
	public void addAlarmWarningStorageDevice(
			AlarmWarningStorageDevice alarmWarningStorageDevice) {
		alarmWarningStorageDevices.add(alarmWarningStorageDevice);
	}
	
    /**
     * Remove a AlarmWarningStorageDevice from the AlarmWarning.
     * @param alarmWarningStorageDevice
     * 			: AlarmWarningStorageDevice to be removed
     * @throws InexistentObjectException
     */
    public void removeAlarmWarningStorageDevice(
    		AlarmWarningStorageDevice alarmWarningStorageDevice) 
    		throws InexistentObjectException {
    	if (!alarmWarningStorageDevices.remove(alarmWarningStorageDevice)) {
    		throw new InexistentObjectException(
    				AlarmWarningStorageDevice.class, 
    				"AlarmWarningStorageDevice not found!");
    	}
    }
}