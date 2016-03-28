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
 * This class represents the AlarmTrigger table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class AlarmTrigger implements Serializable, Comparable<AlarmTrigger> {
	
	public static enum AlarmTriggerConditionOperator {
		GREATER_THAN, LESS_THAN, EQUAL, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
	}
	
	public static enum AlarmTriggerLevel {
		INFORMATION, WARNING, CRITICAL
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Key storageDeviceDoor;

    @Persistent
    private Integer alarmTriggerCode;
    
    @Persistent
    private AlarmTriggerConditionOperator alarmTriggerConditionOperator;
    
    @Persistent
    private Double alarmTriggerConditionValue;
    
    @Persistent
    private Integer alarmTriggerCount;
    
    @Persistent
    private Integer alarmTriggerMaxCount;
    
    @Persistent 
    private AlarmTriggerLevel alarmTriggerLevel;

    @Persistent
    private String alarmTriggerComments;
    
    @Persistent
    private Date alarmTriggerCreationDate;
    
    @Persistent
    private Date alarmTriggerModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<AlarmTriggerMessage> alarmTriggerMessages;

    /**
     * AlarmTrigger constructor.
     * @param storageDeviceDoor
     * 			: StorageDeviceDoor key
     * @param alarmTriggerCode
     * 			: AlarmTrigger code
     * @param alarmTriggerConditionOperator
     * 			: AlarmTrigger condition operator
     * @param alarmTriggerConditionValue
     * 			: AlarmTrigger condition value
     * @param alarmTriggerMaxCount
     * 			: AlarmTrigger max count
     * @param alarmTriggerLevel
     * 			: AlarmTrigger level
     * @param alarmTriggerComments
     * 			: AlarmTrigger comments
     * @throws MissingRequiredFieldsException
     */
    public AlarmTrigger(
    		Key storageDeviceDoor,
    		Integer alarmTriggerCode, 
    		AlarmTriggerConditionOperator alarmTriggerConditionOperator,
    		Double alarmTriggerConditionValue,
    		Integer alarmTriggerMaxCount,
    		AlarmTriggerLevel alarmTriggerLevel,
    		String alarmTriggerComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (alarmTriggerCode == null || alarmTriggerConditionOperator == null ||
    			alarmTriggerConditionValue == null || alarmTriggerMaxCount == null ||
    			alarmTriggerLevel == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.storageDeviceDoor = storageDeviceDoor;
        this.alarmTriggerCode = alarmTriggerCode;
        this.alarmTriggerConditionOperator = alarmTriggerConditionOperator;
        this.alarmTriggerConditionValue = alarmTriggerConditionValue;
        
        this.alarmTriggerCount = 0; // Initialize in 0
        
        this.alarmTriggerMaxCount = alarmTriggerMaxCount;
        this.alarmTriggerLevel = alarmTriggerLevel;
        this.alarmTriggerComments = alarmTriggerComments;
        
        this.alarmTriggerMessages = new ArrayList<>();
        
        Date now = new Date();
        this.alarmTriggerCreationDate = now;
        this.alarmTriggerModificationDate = now;
    }

    /**
     * Get AlarmTrigger key.
     * @return AlarmTrigger key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get StorageDeviceDoor
     * @return StorageDeviceDoor key
     */
    public Key getStorageDeviceDoor() {
    	return storageDeviceDoor;
    }
    
    /**
     * Get AlarmTrigger name.
     * @return AlarmTrigger name
     */
    public Integer getAlarmTriggerCode() {
        return alarmTriggerCode;
    }
    
    /**
     * Get AlarmTriggerConditionOpertor from a string representation
     * @param alarmTriggerConditionOperatorString
     * @return an AlarmTriggerConditionOperator
     */
    public static AlarmTriggerConditionOperator getAlarmTriggerConditionOperatorFromString(
    		String alarmTriggerConditionOperatorString) {
    	
    	if (alarmTriggerConditionOperatorString == null) {
    		return null;
    	}

    	if (alarmTriggerConditionOperatorString.equalsIgnoreCase(">")) {
    		return AlarmTriggerConditionOperator.GREATER_THAN;
    	}
    	else if (alarmTriggerConditionOperatorString.equalsIgnoreCase("<")) {
    		return AlarmTriggerConditionOperator.LESS_THAN;
    	}
    	else if (alarmTriggerConditionOperatorString.equalsIgnoreCase("=")) {
    		return AlarmTriggerConditionOperator.EQUAL;
    	}
    	else if (alarmTriggerConditionOperatorString.equalsIgnoreCase(">=")) {
    		return AlarmTriggerConditionOperator.GREATER_THAN_OR_EQUAL;
    	}
    	else if (alarmTriggerConditionOperatorString.equalsIgnoreCase("<=")) {
    		return AlarmTriggerConditionOperator.LESS_THAN_OR_EQUAL;
    	}
    	else {
    		return null;
    	}
    	
    }
    
    /**
     * Get string representation of the alarmTriggerConditionOperator
     * @return alarmTriggerConditionOperator string
     */
    public String getAlarmTriggerConditionOperatorString() {
    	switch (alarmTriggerConditionOperator) {
    		case GREATER_THAN:
    			return ">";
    		case LESS_THAN:
    			return "<";
    		case EQUAL:
    			return "=";
    		case GREATER_THAN_OR_EQUAL:
    			return ">=";
    		case LESS_THAN_OR_EQUAL:
    			return "<=";
    		default:
    			return null;
    	}
    }
    
    /**
	 * @return the alarmTriggerConditionOperator
	 */
	public AlarmTriggerConditionOperator getAlarmTriggerConditionOperator() {
		return alarmTriggerConditionOperator;
	}

	/**
	 * @return the alarmTriggerConditionValue
	 */
	public Double getAlarmTriggerConditionValue() {
		return alarmTriggerConditionValue;
	}

	/**
	 * @return the alarmTriggerCount
	 */
	public Integer getAlarmTriggerCount() {
		return alarmTriggerCount;
	}

	/**
	 * @return the alarmTriggerMaxCount
	 */
	public Integer getAlarmTriggerMaxCount() {
		return alarmTriggerMaxCount;
	}

	/**
     * Get AlarmTriggerLevel from a string representation
     * @param alarmTriggerLevelString
     * @return an AlarmTriggerLevel
     */
    public static AlarmTriggerLevel getAlarmTriggerLevelFromString(
    		String alarmTriggerLevelString) {
    	
    	if (alarmTriggerLevelString == null) {
    		return null;
    	}

    	if (alarmTriggerLevelString.equalsIgnoreCase("information")) {
    		return AlarmTriggerLevel.INFORMATION;
    	}
    	else if (alarmTriggerLevelString.equalsIgnoreCase("warning")) {
    		return AlarmTriggerLevel.WARNING;
    	}
    	else if (alarmTriggerLevelString.equalsIgnoreCase("critical")) {
    		return AlarmTriggerLevel.CRITICAL;
    	}
    	else {
    		return null;
    	}
    	
    }
	
	/**
	 * @return the alarmTriggerLevel
	 */
	public AlarmTriggerLevel getAlarmTriggerLevel() {
		return alarmTriggerLevel;
	}

	/**
	 * @return the alarmTriggerComments
	 */
	public String getAlarmTriggerComments() {
		return alarmTriggerComments;
	}

	/**
     * Get AlarmTrigger creation date.
     * @return AlarmTrigger creation date
     */
    public Date getAlarmTriggerCreationDate() {
    	return alarmTriggerCreationDate;
    }
    
    /**
     * Get AlarmTrigger modification date.
     * @return AlarmTrigger modification date
     */
    public Date getAlarmTriggerModificationDate() {
    	return alarmTriggerModificationDate;
    }
    
    /**
     * Get AlarmTriggerMessages
     * @return alarmTriggerMessages
     */
    public ArrayList<AlarmTriggerMessage> getAlarmTriggerMessages() {
    	return alarmTriggerMessages;
    }
    
    /**
     * Checks whether the AlarmTrigger is triggered with the given
     * realValue.
     * @param realValue: the value to compare with the alarmTriggerConditionValue
     * @return True if the condition matches, False otherwise
     */
    public boolean alarmTriggered(double realValue) {
    	
    	boolean alarmTriggered = false;
    	
		switch (alarmTriggerConditionOperator) {
			case GREATER_THAN:
				if (realValue > alarmTriggerConditionValue) {
					alarmTriggered = true;
				}
				break;
			case LESS_THAN:
				if (realValue < alarmTriggerConditionValue) {
					alarmTriggered = true;
				}
				break;
			case EQUAL:
				if (realValue == alarmTriggerConditionValue) {
					alarmTriggered = true;
				}
				break;
			case GREATER_THAN_OR_EQUAL:
				if (realValue >= alarmTriggerConditionValue) {
					alarmTriggered = true;
				}
				break;
			case LESS_THAN_OR_EQUAL:
				if (realValue <= alarmTriggerConditionValue) {
					alarmTriggered = true;
				}
				break;
			default:
				break;
		}
		
		return alarmTriggered;
    }
    
	/**
     * Compare this AlarmTrigger with another AlarmTrigger
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this AlarmTrigger, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof AlarmTrigger ) ) return false;
        AlarmTrigger dmp = (AlarmTrigger) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
	@Override
	public int compareTo(AlarmTrigger alarmTrigger) {
		return this.alarmTriggerCode.compareTo(
				alarmTrigger.getAlarmTriggerCode());
	}
    
    /**
     * Set AlarmTrigger name.
     * @param alarmTriggerCode
     * 			: AlarmTrigger name
     * @throws MissingRequiredFieldsException
     */
    public void setAlarmTriggerCode(Integer alarmTriggerCode)
    		throws MissingRequiredFieldsException {
    	if (alarmTriggerCode == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmTriggerCode is missing.");
    	}
    	this.alarmTriggerCode = alarmTriggerCode;
    	this.alarmTriggerModificationDate = new Date();
    }
    
    /**
	 * @param alarmTriggerConditionOperator the alarmTriggerConditionOperator to set
     * @throws MissingRequiredFieldsException 
	 */
	public void setAlarmTriggerConditionOperator(
			AlarmTriggerConditionOperator alarmTriggerConditionOperator) 
					throws MissingRequiredFieldsException {
    	if (alarmTriggerConditionOperator == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmTriggerConditionOperator is missing.");
    	}
		this.alarmTriggerConditionOperator = alarmTriggerConditionOperator;
		this.alarmTriggerModificationDate = new Date();
	}

	/**
	 * @param alarmTriggerConditionValue the alarmTriggerConditionValue to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAlarmTriggerConditionValue(Double alarmTriggerConditionValue) 
			throws MissingRequiredFieldsException {
    	if (alarmTriggerConditionValue == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmTriggerConditionValue is missing.");
    	}
		this.alarmTriggerConditionValue = alarmTriggerConditionValue;
		this.alarmTriggerModificationDate = new Date();
	}

	/**
	 * Reset the alarmTriggerCount to 0;
	 */
	public void resetAlarmTriggerCount() {
		this.alarmTriggerCount = 0;
	}
	
	/**
	 * Increase the alarmTriggerCount by 1.
	 * If the trigger count is greater than or equal to the max count, then
	 * the function returns false.
	 * @return True if alarmTriggerCount is less than alarmTriggerMaxCount,
	 * 			False otherwise
	 */
	public boolean increaseAlarmTriggerCount() {
		if (alarmTriggerCount < alarmTriggerMaxCount) {
			this.alarmTriggerCount++;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Decrease the alarmTriggerCount by 1.
	 * If the trigger count is less than or equal to 0, then
	 * the function returns false.
	 * @return True if alarmTriggerCount is greater than 0,
	 * 			False otherwise
	 */
	public boolean decreaseAlarmTriggerCount() {
		if (alarmTriggerCount > 0) {
			this.alarmTriggerCount--;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @param alarmTriggerMaxCount the alarmTriggerMaxCount to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAlarmTriggerMaxCount(Integer alarmTriggerMaxCount) 
			throws MissingRequiredFieldsException {
    	if (alarmTriggerMaxCount == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmTriggerMaxCount is missing.");
    	}
		this.alarmTriggerMaxCount = alarmTriggerMaxCount;
		this.alarmTriggerModificationDate = new Date();
	}

	/**
	 * @param alarmTriggerLevel the alarmTriggerLevel to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAlarmTriggerLevel(AlarmTriggerLevel alarmTriggerLevel) 
			throws MissingRequiredFieldsException {
    	if (alarmTriggerLevel == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"alarmTriggerLevel is missing.");
    	}
		this.alarmTriggerLevel = alarmTriggerLevel;
		this.alarmTriggerModificationDate = new Date();
	}

	/**
	 * @param alarmTriggerComments the alarmTriggerComments to set
	 */
	public void setAlarmTriggerComments(String alarmTriggerComments) {
		this.alarmTriggerComments = alarmTriggerComments;
		this.alarmTriggerModificationDate = new Date();
	}
    
	/**
	 * Add a alarmTriggerMessage to this AlarmTrigger
	 * @param alarmTriggerMessage
	 */
	public void addAlarmTriggerMessage(AlarmTriggerMessage alarmTriggerMessage) {
		alarmTriggerMessages.add(alarmTriggerMessage);
	}
	
    /**
     * Remove a AlarmTriggerMessagefrom the AlarmTrigger.
     * @param alarmTriggerMessage
     * 			: AlarmTriggerMessage to be removed
     * @throws InexistentObjectException
     */
    public void removeAlarmTriggerMessage(AlarmTriggerMessage alarmTriggerMessage) 
    		throws InexistentObjectException {
    	if (!alarmTriggerMessages.remove(alarmTriggerMessage)) {
    		throw new InexistentObjectException(
    				AlarmTriggerMessage.class, "AlarmTriggerMessage not found!");
    	}
    }
}