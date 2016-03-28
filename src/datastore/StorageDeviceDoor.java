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
 * This class represents the StorageDeviceDoor table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageDeviceDoor implements Serializable {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

    @Persistent
    private Key deviceModelDoor;
    
    @Persistent
    private Boolean storageDeviceDoorIsOpen;
    
    @Persistent
    private Date storageDeviceDoorLastOpenTime;

    @Persistent
    private String storageDeviceDoorComments;

    @Persistent
    private Date storageDeviceDoorCreationDate;
    
    @Persistent
    private Date storageDeviceDoorModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<StorageDevicePartition> storageDevicePartitions;

    /**
     * StorageDeviceDoor constructor.
     * @param deviceModelDoor
     * 			: DeviceModelDoor key
     * @param storageDeviceDoorComments
     * 			: StorageDeviceDoor comments
     * @throws MissingRequiredFieldsException
     */
    public StorageDeviceDoor(Key deviceModelDoor, 
    		String storageDeviceDoorComments)
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (deviceModelDoor == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.deviceModelDoor = deviceModelDoor;
        this.storageDeviceDoorIsOpen = false;
        this.storageDeviceDoorLastOpenTime = null;
        
        this.storageDeviceDoorComments = storageDeviceDoorComments;
        
        this.storageDevicePartitions = new ArrayList<>();
        
        Date now = new Date();
        this.storageDeviceDoorCreationDate = now;
        this.storageDeviceDoorModificationDate = now;
    }

    /**
     * Get StorageDeviceDoor key.
     * @return StorageDeviceDoor key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get StorageDeviceDoor key.
     * @return StorageDeviceDoor key
     */
    public Key getDeviceModelDoor() {
        return deviceModelDoor;
    }
    
    /**
	 * @return the storageDeviceDoorIsOpen
	 */
	public Boolean getStorageDeviceDoorIsOpen() {
		
		if (storageDeviceDoorIsOpen == null) {
			storageDeviceDoorIsOpen = false;
		}
		
		return storageDeviceDoorIsOpen;
	}

	/**
	 * @return the storageDeviceDoorLastOpenTime
	 */
	public Date getStorageDeviceDoorLastOpenTime() {
		return storageDeviceDoorLastOpenTime;
	}

	/**
     * Get StorageDeviceDoor comments.
     * @return StorageDeviceDoor comments
     */
    public String getStorageDeviceDoorComments() {
    	return storageDeviceDoorComments;
    }
    
    /**
     * Get StorageDeviceDoor creation date.
     * @return StorageDeviceDoor creation date
     */
    public Date getStorageDeviceDoorCreationDate() {
    	return storageDeviceDoorCreationDate;
    }
    
    /**
     * Get StorageDeviceDoor modification date.
     * @return StorageDeviceDoor modification date
     */
    public Date getStorageDeviceDoorModificationDate() {
    	return storageDeviceDoorModificationDate;
    }
    
    /**
     * Get StorageDevicePartitions
     * @return storageDevicePartitions
     */
    public ArrayList<StorageDevicePartition> getStorageDevicePartitions() {
    	return storageDevicePartitions;
    }
    
	/**
     * Compare this StorageDeviceDoor with another StorageDeviceDoor
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageDeviceDoor, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof StorageDeviceDoor ) ) return false;
        StorageDeviceDoor dmp = (StorageDeviceDoor) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
    /**
     * Set DeviceModelDoor.
     * @param deviceModelDoor
     * 			: DeviceModelDoor key
     * @throws MissingRequiredFieldsException
     */
    public void setDeviceModelDoor(Key deviceModelDoor)
    		throws MissingRequiredFieldsException {
    	if (deviceModelDoor == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"DeviceModelDoor is missing.");
    	}
    	this.deviceModelDoor = deviceModelDoor;
    	this.storageDeviceDoorModificationDate = new Date();
    }
    
    /**
     * Open the StorageDeviceDoor and set the open time to now
     */
    public void openStorageDeviceDoor() {
    	this.storageDeviceDoorIsOpen = true;
    	this.storageDeviceDoorLastOpenTime = new Date();
    }
    
    /**
     * Close the StorageDeviceDoor
     */
    public void closeStorageDeviceDoor() {
    	this.storageDeviceDoorIsOpen = false;
    }
    
    /**
     * Set StorageDeviceDoor comments.
     * @param storageDeviceDoorComments
     * 			: StorageDeviceDoor comments
     */
    public void setStorageDeviceDoorComments(String storageDeviceDoorComments) {
    	this.storageDeviceDoorComments = storageDeviceDoorComments;
    	this.storageDeviceDoorModificationDate = new Date();
    }
    
	/**
	 * Add a storageDevicePartition to this StorageDeviceDoor
	 * @param storageDevicePartition
	 */
	public void addStorageDevicePartition(StorageDevicePartition storageDevicePartition) {
		storageDevicePartitions.add(storageDevicePartition);
	}
	
    /**
     * Remove a StorageDevicePartitionfrom the StorageDeviceDoor.
     * @param storageDevicePartition
     * 			: StorageDevicePartition to be removed
     * @throws InexistentObjectException
     */
    public void removeStorageDevicePartition(StorageDevicePartition storageDevicePartition) 
    		throws InexistentObjectException {
    	if (!storageDevicePartitions.remove(storageDevicePartition)) {
    		throw new InexistentObjectException(
    				StorageDevicePartition.class, "StorageDevicePartition not found!");
    	}
    }
}