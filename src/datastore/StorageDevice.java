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
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the StorageDevice table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageDevice implements Serializable, Comparable<StorageDevice> {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
    @Persistent
    private Key deviceModel;
    
    @Persistent
    private String storageDeviceSerialNumber;
    
    @Persistent
    private String storageDeviceNickname;
    
    @Persistent
    private String storageDeviceDescription;
    
    @Persistent
    private String storageDevicePrivateEncryptionKey;
    
    @Persistent
    private Date storageDeviceManufacturedDate;
    
    @Persistent
    private Date storageDeviceShippingDate;
    
    @Persistent
    private Integer sensorDataEffectivePeriod;
    
    @Persistent
    private Integer alarmMessageEffectivePeriod;
    
    @Persistent
    private Boolean enableSensorDataUpload;
    
    @Persistent
    private Integer sensorReadingCacheSize;
    
    @Persistent
    private String storageDeviceComments;
    
    @Persistent
    private Date storageDeviceCreationDate;
    
    @Persistent
    private Date  storageDeviceModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<StorageDeviceDoor> storageDeviceDoors;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<SensorInstance> sensorInstances;

    /**
     * StorageDevice constructor.
     * @param deviceModel
     * 			: deviceModel model key
     * @param storageDeviceSerialNumber
     * 			: storageDevice number
     * @param storageDeviceNickname
     * 			: storageDevice nickname
     * @param storageDeviceDescription
     * 			: storageDevice description
     * @param storageDevicePrivateEncryptionKey
     * 			: storageDevice privateEncryptionKey
     * @param storageDeviceManufacturedDate
     * 			: storageDevice manufacturedDate
     * @param storageDeviceShippingDate
     * 			: storageDevice shippingDate
     * @param sensorDataEffectivePeriod
     * 			: storageDevice sensorDataEffectivePeriod
     * @param alarmMessageEffectivePeriod
     * 			: storageDevice alarmMessageEffectivePeriod
     * @param enableSensorDataUpload
     * 			: storageDevice enableSensorDataUpload
     * @param sensorReadingCacheSize
     * 			: SensorReadingCache size
     * @param storageDeviceComments
     * 			: storageDevice comments
     * @throws MissingRequiredFieldsException
     */
    public StorageDevice(Key deviceModel, 
    		String storageDeviceSerialNumber,
    		String storageDeviceNickname,
    		String storageDeviceDescription, 
    		String storageDevicePrivateEncryptionKey,
    		Date storageDeviceManufacturedDate, 
    		Date storageDeviceShippingDate,
    		Integer sensorDataEffectivePeriod,
    		Integer alarmMessageEffectivePeriod,
    		Boolean enableSensorDataUpload,
    		Integer sensorReadingCacheSize,
    		String storageDeviceComments) 
		throws MissingRequiredFieldsException {
        
    	// Check "required field" constraints
    	if (deviceModel == null || 
    			storageDeviceSerialNumber == null ||
    			storageDeviceNickname == null ||
    			storageDevicePrivateEncryptionKey == null || 
    			storageDeviceManufacturedDate == null || 
    			storageDeviceShippingDate == null ||
    			sensorDataEffectivePeriod == null ||
    			alarmMessageEffectivePeriod == null ||
    			enableSensorDataUpload == null ||
    			sensorReadingCacheSize == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
    	if (storageDeviceSerialNumber.isEmpty() ||
    			storageDeviceNickname.isEmpty() ||
    			storageDevicePrivateEncryptionKey.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.deviceModel = deviceModel;
    	this.storageDeviceSerialNumber = storageDeviceSerialNumber;
    	this.storageDeviceNickname = storageDeviceNickname;
    	this.storageDeviceDescription = storageDeviceDescription;
    	this.storageDevicePrivateEncryptionKey = storageDevicePrivateEncryptionKey;
    	this.storageDeviceManufacturedDate = storageDeviceManufacturedDate;
    	this.storageDeviceShippingDate = storageDeviceShippingDate;
    	this.sensorDataEffectivePeriod = sensorDataEffectivePeriod;
    	this.alarmMessageEffectivePeriod = alarmMessageEffectivePeriod;
    	this.enableSensorDataUpload = enableSensorDataUpload;
    	this.sensorReadingCacheSize = sensorReadingCacheSize;
    	this.storageDeviceComments = storageDeviceComments;
    	
    	this.storageDeviceDoors = new ArrayList<StorageDeviceDoor>();
    	this.sensorInstances = new ArrayList<SensorInstance>();
    	
    	Date now = new Date();
    	this.storageDeviceCreationDate = now;
    	this.storageDeviceModificationDate = now;
    }

	/**
     * Get StorageDevice key.
     * @return storageDevice key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get deviceModel.
     * @return deviceModel key
     */
    public Key getDeviceModel() {
        return deviceModel;
    }
    
    /**
     * Get StorageDevice serial number.
     * @return storageDevice serial number
     */
    public String getStorageDeviceSerialNumber() {
        return storageDeviceSerialNumber;
    }
    
    /**
	 * @return the storageDeviceNickname
	 */
	public String getStorageDeviceNickname() {
		return storageDeviceNickname;
	}

	/**
	 * @return the storageDeviceDescription
	 */
	public String getStorageDeviceDescription() {
		return storageDeviceDescription;
	}

	/**
	 * @return the storageDevicePrivateEncryptionKey
	 */
	public String getStorageDevicePrivateEncryptionKey() {
		return storageDevicePrivateEncryptionKey;
	}

	/**
	 * @return the storageDeviceManufacturedDate
	 */
	public Date getStorageDeviceManufacturedDate() {
		return storageDeviceManufacturedDate;
	}

	/**
	 * @return the storageDeviceShippingDate
	 */
	public Date getStorageDeviceShippingDate() {
		return storageDeviceShippingDate;
	}
	
	/**
	 * @return the sensorDataEffectivePeriod
	 */
	public Integer getSensorDataEffectivePeriod() {
		return sensorDataEffectivePeriod;
	}
	
	/**
	 * @return the alarmMessageEffectivePeriod
	 */
	public Integer getAlarmMessageEffectivePeriod() {
		if (alarmMessageEffectivePeriod == null) {
			alarmMessageEffectivePeriod = sensorDataEffectivePeriod;
		}
		return alarmMessageEffectivePeriod;
	}

	/**
	 * @return the enableSensorDataUpload
	 */
	public Boolean getEnableSensorDataUpload() {
		return enableSensorDataUpload;
	}
	
	/**
	 * @return the sensorReadingCacheSize
	 */
	public Integer getSensorReadingCacheSize() {
		if (sensorReadingCacheSize == null) {
			sensorReadingCacheSize = 10;
		}
		return sensorReadingCacheSize;
	}

	/**
	 * @return the storageDeviceComments
	 */
	public String getStorageDeviceComments() {
		return storageDeviceComments;
	}

    /**
     * Get storageDevice creation date.
     * @return the time this storageDevice was created
     */
    public Date getStorageDeviceCreationDate() {
        return storageDeviceCreationDate;
    }

    /**
     * Get storageDevice modification date.
     * @return the time this storageDevice was last modified
     */
    public Date getStorageDeviceModificationDate() {
        return storageDeviceModificationDate;
    }
    
    /**
     * Get StorageDeviceDoor list.
     * @return storageDeviceDoors
     */
    public ArrayList<StorageDeviceDoor> getStorageDeviceDoors() {
        return storageDeviceDoors;
    }
    
    /**
     * Get SensorInstance list.
     * @return storageDeviceDoors
     */
    public ArrayList<SensorInstance> getSensorInstances() {
        return sensorInstances;
    }
    
    /**
     * Compare this storageDevice with another storageDevice
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageDevice, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if (!(o instanceof StorageDevice ) ) return false;
        StorageDevice storageDevice = (StorageDevice) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(storageDevice.getKey()));
    }
    
	@Override
	public int compareTo(StorageDevice storageDevice) {
		return this.getStorageDeviceSerialNumber().compareTo(
				storageDevice.getStorageDeviceSerialNumber());
	}
    
    /**
     * Set StorageDevice serial number.
     * @param storageDeviceSerialNumber
     * 			: storageDevice serial number
     * @throws MissingRequiredFieldsException
     */
    public void setStorageDeviceSerialNumber(String storageDeviceSerialNumber)
    		throws MissingRequiredFieldsException {
    	if (storageDeviceSerialNumber == null || 
    			storageDeviceSerialNumber.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice serial number is missing.");
    	}
    	this.storageDeviceSerialNumber = storageDeviceSerialNumber;
    	this.storageDeviceModificationDate = new Date();
    }
    
	/**
	 * @param storageDeviceNickname the storageDeviceNickname to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceNickname(String storageDeviceNickname) 
			throws MissingRequiredFieldsException {
    	if (storageDeviceNickname == null || 
    			storageDeviceNickname.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice nickname is missing.");
    	}
		this.storageDeviceNickname = storageDeviceNickname;
		this.storageDeviceModificationDate = new Date();
	}
    
    /**
	 * @param storageDeviceDescription the storageDeviceDescription to set
	 */
	public void setStorageDeviceDescription(String storageDeviceDescription) {
		this.storageDeviceDescription = storageDeviceDescription;
		this.storageDeviceModificationDate = new Date();
	}

	/**
	 * @param storageDevicePrivateEncryptionKey the storageDevicePrivateEncryptionKey to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDevicePrivateEncryptionKey(
			String storageDevicePrivateEncryptionKey) 
					throws MissingRequiredFieldsException {
		if (storageDevicePrivateEncryptionKey == null || 
    			storageDevicePrivateEncryptionKey.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice privateEncryptionKey is missing.");
    	}
		this.storageDevicePrivateEncryptionKey = storageDevicePrivateEncryptionKey;
		this.storageDeviceModificationDate = new Date();
	}

	/**
	 * @param storageDeviceManufacturedDate the storageDeviceManufacturedDate to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceManufacturedDate(
			Date storageDeviceManufacturedDate) 
			throws MissingRequiredFieldsException {
		if (storageDeviceManufacturedDate == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice manufacturedDate is missing.");
    	}
		this.storageDeviceManufacturedDate = storageDeviceManufacturedDate;
		this.storageDeviceModificationDate = new Date();
	}

	/**
	 * @param storageDeviceShippingDate the storageDeviceShippingDate to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceShippingDate(Date storageDeviceShippingDate) 
			throws MissingRequiredFieldsException {
		if (storageDeviceShippingDate == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice shippingDate is missing.");
    	}
		this.storageDeviceShippingDate = storageDeviceShippingDate;
		this.storageDeviceModificationDate = new Date();
	}
	
	/**
	 * @param sensorDataEffectivePeriod the sensorDataEffectivePeriod to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setSensorDataEffectivePeriod(Integer sensorDataEffectivePeriod) 
			throws MissingRequiredFieldsException {
    	if (sensorDataEffectivePeriod == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice sensorDataEffectivePeriod is missing.");
    	}
		this.sensorDataEffectivePeriod = sensorDataEffectivePeriod;
		this.storageDeviceModificationDate = new Date();
	}
	
	/**
	 * @param alarmMessageEffectivePeriod the alarmMessageEffectivePeriod to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setAlarmMessageEffectivePeriod(Integer alarmMessageEffectivePeriod) 
			throws MissingRequiredFieldsException {
    	if (alarmMessageEffectivePeriod == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice alarmMessageEffectivePeriod is missing.");
    	}
		this.alarmMessageEffectivePeriod = alarmMessageEffectivePeriod;
		this.storageDeviceModificationDate = new Date();
	}

	/**
	 * @param enableSensorDataUpload the enableSensorDataUpload to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setEnableSensorDataUpload(Boolean enableSensorDataUpload) 
			throws MissingRequiredFieldsException {
    	if (enableSensorDataUpload == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice enableSensorDataUpload is missing.");
    	}
		this.enableSensorDataUpload = enableSensorDataUpload;
		this.storageDeviceModificationDate = new Date();
	}
	
	/**
	 * @param sensorReadingCacheSize the sensorReadingCacheSize to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setSensorReadingCacheSize(Integer sensorReadingCacheSize) 
			throws MissingRequiredFieldsException {
    	if (sensorReadingCacheSize == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDevice sensorReadingCacheSize is missing.");
    	}
		this.sensorReadingCacheSize = sensorReadingCacheSize;
		this.storageDeviceModificationDate = new Date();
	}

	/**
	 * @param storageDeviceComments the storageDeviceComments to set
	 */
	public void setStorageDeviceComments(String storageDeviceComments) {
		this.storageDeviceComments = storageDeviceComments;
		this.storageDeviceModificationDate = new Date();
	}
    
    /**
     * Add a new storageDeviceDoor to this storageDevice.
     * @param storageDeviceDoor
     * 			: new storageDeviceDoor to be added
     */
    public void addStorageDeviceDoor(
    		StorageDeviceDoor storageDeviceDoor) {
    	this.storageDeviceDoors.add(storageDeviceDoor);
    }
    
    /**
     * Add a new sensorInstance to this storageDevice.
     * @param sensorInstance
     * 			: new sensorInstance to be added
     */
    public void addSensorInstance(
    		SensorInstance sensorInstance) {
    	this.sensorInstances.add(sensorInstance);
    }
    
    /**
     * Remove storageDeviceDoor from the storageDevice.
     * @param storageDeviceDoor
     * 			: storageDeviceDoor to be removed
     * @throws InexistentObjectException
     */
    public void removeStorageDeviceDoor(
    		StorageDeviceDoor storageDeviceDoor) 
    		throws InexistentObjectException {
    	if (!this.storageDeviceDoors.remove(storageDeviceDoor)) {
    		throw new InexistentObjectException
    				(StorageDeviceDoor.class, "StorageDeviceDoor not found!");
    	}
    }
    
    /**
     * Remove sensorInstance from the storageDevice.
     * @param sensorInstance
     * 			: sensorInstance to be removed
     * @throws InexistentObjectException
     */
    public void removeSensorInstance(
    		SensorInstance sensorInstance) 
    		throws InexistentObjectException {
    	if (!this.sensorInstances.remove(sensorInstance)) {
    		throw new InexistentObjectException
    				(SensorInstance.class, "SensorInstance not found!");
    	}
    }
}