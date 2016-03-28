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
 * This class represents the SensorInstance table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class SensorInstance implements Serializable {
	
	public static enum SensorStatus {
		OK, MALFUNCTION
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

    @Persistent
    private Long sensorType;
    
	@Persistent
	private Key storageDeviceDoor;
	
	@Persistent
	private Key lastSensorReading;
    
	@Persistent
	private Key lastSensorReadingCache;
	
    @Persistent
    private String sensorInstanceLabel;
    
    @Persistent
    private SensorStatus sensorStatus;
    
    @Persistent
    private Integer sensorReadingCacheCount;

    @Persistent
    private String sensorInstanceComments;
    
    @Persistent
    private Date sensorInstanceCreationDate;
    
    @Persistent
    private Date sensorInstanceModificationDate;

    @Persistent
    @Element(dependent = "true")
    private ArrayList<SensorReading> sensorReadings;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<AlarmTrigger> alarmTriggers;
    
    /**
     * SensorInstance constructor.
     * @param sensorType
     * 			: sensorType key
     * @param storageDeviceDoor
     * 			: StorageDeviceDoor key
     * @param sensorInstanceLabel
     * 			: sensorInstanceLabel
     * @param sensorStatus
     * 			: sensorStatus
     * @param sensorInstanceComments
     * 			: SensorInstance comments
     * @throws MissingRequiredFieldsException
     */
    public SensorInstance(Long sensorType,
    		Key storageDeviceDoor,
    		String sensorInstanceLabel,
    		SensorStatus sensorStatus,
    		String sensorInstanceComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (sensorType == null || sensorInstanceLabel == null ||
    			sensorStatus == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (sensorInstanceLabel.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.sensorType = sensorType;
        this.storageDeviceDoor = storageDeviceDoor;
        this.lastSensorReading = null;
        this.lastSensorReadingCache = null;
        this.sensorInstanceLabel = sensorInstanceLabel;
        this.sensorStatus = sensorStatus;
        this.sensorReadingCacheCount = 0;
        this.sensorInstanceComments = sensorInstanceComments;
        
        Date now = new Date();
        this.sensorInstanceCreationDate = now;
        this.sensorInstanceModificationDate = now;
        
        this.sensorReadings = new ArrayList<>();
        this.alarmTriggers = new ArrayList<>();
    }

    /**
     * Get SensorInstance key.
     * @return SensorInstance key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get sensorType key.
     * @return sensorType key
     */
    public Long getSensorType() {
        return sensorType;
    }
    
    /**
	 * @return the storageDeviceDoor
	 */
	public Key getStorageDeviceDoor() {
		return storageDeviceDoor;
	}
	
	/**
	 * Get last SensorReading key
	 * @return the key of the last SensorReading uploaded to this
	 * SensorInstance
	 */
	public Key getLastSensorReading() {
		return lastSensorReading;
	}
	
	/**
	 * Get last SensorReadingCache key
	 * @return the key of the last SensorReadingCache uploaded to this
	 * SensorInstance
	 */
	public Key getLastSensorReadingCache() {
		return lastSensorReadingCache;
	}

	/**
	 * @return the sensorInstanceLabel
	 */
	public String getSensorInstanceLabel() {
		return sensorInstanceLabel;
	}

	/**
	 * Returns a SensorStatus based on its string representation
	 * @param sensorStatusString
	 * @return a sensorStatus
	 */
	public static SensorStatus getSensorStatusFromString(
			String sensorStatusString) {
		
		if (sensorStatusString == null) {
			return null;
		}
		
		if (sensorStatusString.equalsIgnoreCase("OK")) {
			return SensorStatus.OK;
		}
		else if (sensorStatusString.equalsIgnoreCase("MALFUNCTION")) {
			return SensorStatus.MALFUNCTION;
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return the sensorStatus
	 */
	public SensorStatus getSensorStatus() {
		return sensorStatus;
	}

	/**
	 * 
	 * @return the sensorReadingCacheCount
	 */
	public Integer getSensorReadingCacheCount() {
		if (sensorReadingCacheCount == null) {
			sensorReadingCacheCount = 0;
		}
		
		return sensorReadingCacheCount;
	}

	/**
     * Get SensorInstance comments.
     * @return SensorInstance comments
     */
    public String getSensorInstanceComments() {
    	return sensorInstanceComments;
    }
    
    /**
     * Get SensorInstance creation date.
     * @return SensorInstance creation date
     */
    public Date getSensorInstanceCreationDate() {
    	return sensorInstanceCreationDate;
    }
    
    /**
     * Get SensorInstance modification date.
     * @return SensorInstance modification date
     */
    public Date getSensorInstanceModificationDate() {
    	return sensorInstanceModificationDate;
    }
    
	/**
	 * @return the sensorReadings
	 */
	public ArrayList<SensorReading> getSensorReadings() {
		return sensorReadings;
	}
	
	/**
	 * @return the alarmTriggers
	 */
	public ArrayList<AlarmTrigger> getAlarmTriggers() {
		return alarmTriggers;
	}

	/**
     * Compare this SensorInstance with another SensorInstance
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this SensorInstance, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof SensorInstance ) ) return false;
        SensorInstance dmp = (SensorInstance) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
    /**
     * Set SensorInstance name.
     * @param sensorType
     * 			: sensorType key
     * @throws MissingRequiredFieldsException
     */
    public void setSensorType(Long sensorType)
    		throws MissingRequiredFieldsException {
    	if (sensorType == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"sensorType is missing.");
    	}
    	this.sensorType = sensorType;
    	this.sensorInstanceModificationDate = new Date();
    }
    
	/**
	 * @param storageDeviceDoor the storageDeviceDoor to set
	 */
	public void setStorageDeviceDoor(Key storageDeviceDoor) {
		this.storageDeviceDoor = storageDeviceDoor;
		this.sensorInstanceModificationDate = new Date();
	}
	
	/**
	 * Set last SensorReading key
	 * @param lastSensorReading
	 */
	public void setLastSensorReading(Key lastSensorReading) {
		this.lastSensorReading = lastSensorReading;
	}
	
	/**
	 * Set last SensorReadingCache key
	 * @param lastSensorReadingCache
	 */
	public void setLastSensorReadingCache(Key lastSensorReadingCache) {
		this.lastSensorReadingCache = lastSensorReadingCache;
	}
    
	/**
	 * @param sensorInstanceLabel the sensorInstanceLabel to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setSensorInstanceLabel(String sensorInstanceLabel) 
			throws MissingRequiredFieldsException {
		if (sensorInstanceLabel == null || sensorInstanceLabel.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"sensorInstanceLabel is missing.");
    	}
		this.sensorInstanceLabel = sensorInstanceLabel;
		this.sensorInstanceModificationDate = new Date();
	}

	/**
	 * @param sensorStatus the sensorStatus to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setSensorStatus(SensorStatus sensorStatus) 
			throws MissingRequiredFieldsException {
		if (sensorStatus == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"sensorStatus is missing.");
    	}
		this.sensorStatus = sensorStatus;
		this.sensorInstanceModificationDate = new Date();
	}
	
	/**
	 * Increase the sensorReadingCacheCount by 1
	 */
	public void incrementSensorReadingCacheCount() {
		if (sensorReadingCacheCount == null) {
			sensorReadingCacheCount = 0;
		}
		
		sensorReadingCacheCount++;
	}
	
	/**
	 * Reset the sensorReadingCacheCount to 0
	 */
	public void resetSensorReadingCacheCount() {
		sensorReadingCacheCount = 0;
	}
    
    /**
     * Set SensorInstance comments.
     * @param sensorInstanceComments
     * 			: SensorInstance comments
     */
    public void setSensorInstanceComments(String sensorInstanceComments) {
    	this.sensorInstanceComments = sensorInstanceComments;
    	this.sensorInstanceModificationDate = new Date();
    }
    
	/**
	 * Add a SensorReading to this SensorInstance
	 * @param sensorReading
	 */
	public void addSensorReading(SensorReading sensorReading) {
		sensorReadings.add(sensorReading);
	}
	
    /**
     * Remove a SensorReading from the SensorInstance
     * @param sensorReading
     * 			: SensorReading to be removed
     * @throws InexistentObjectException
     */
    public void removeSensorReading(SensorReading sensorReading) 
    		throws InexistentObjectException {
    	if (!sensorReadings.remove(sensorReading)) {
    		throw new InexistentObjectException(
    				SensorReading.class, "SensorReading not found!");
    	}
    }
    
    /**
	 * Add a AlarmTrigger to this SensorInstance
	 * @param alarmTrigger
	 */
	public void addAlarmTrigger(AlarmTrigger alarmTrigger) {
		alarmTriggers.add(alarmTrigger);
	}
	
    /**
     * Remove a AlarmTrigger from the SensorInstance
     * @param alarmTrigger
     * 			: AlarmTrigger to be removed
     * @throws InexistentObjectException
     */
    public void removeAlarmTrigger(AlarmTrigger alarmTrigger) 
    		throws InexistentObjectException {
    	if (!alarmTriggers.remove(alarmTrigger)) {
    		throw new InexistentObjectException(
    				AlarmTrigger.class, "AlarmTrigger not found!");
    	}
    }
}