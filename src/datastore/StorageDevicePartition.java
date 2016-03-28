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
 * This class represents the StorageDevicePartition table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageDevicePartition implements Serializable {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

    @Persistent
    private Key deviceModelPartition;

    @Persistent
    private String storageDevicePartitionComments;
    
    @Persistent
    private Date storageDevicePartitionCreationDate;
    
    @Persistent
    private Date storageDevicePartitionModificationDate;

    /**
     * StorageDevicePartition constructor.
     * @param deviceModelPartition
     * 			: deviceModelPartition key
     * @param storageDevicePartitionComments
     * 			: StorageDevicePartition comments
     * @throws MissingRequiredFieldsException
     */
    public StorageDevicePartition(Key deviceModelPartition, 
    		String storageDevicePartitionComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (deviceModelPartition == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.deviceModelPartition = deviceModelPartition;
        this.storageDevicePartitionComments = storageDevicePartitionComments;
        
        Date now = new Date();
        this.storageDevicePartitionCreationDate = now;
        this.storageDevicePartitionModificationDate = now;
    }

    /**
     * Get StorageDevicePartition key.
     * @return StorageDevicePartition key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get deviceModelPartition key.
     * @return deviceModelPartition key
     */
    public Key getDeviceModelPartition() {
        return deviceModelPartition;
    }
    
    /**
     * Get StorageDevicePartition comments.
     * @return StorageDevicePartition comments
     */
    public String getStorageDevicePartitionComments() {
    	return storageDevicePartitionComments;
    }
    
    /**
     * Get StorageDevicePartition creation date.
     * @return StorageDevicePartition creation date
     */
    public Date getStorageDevicePartitionCreationDate() {
    	return storageDevicePartitionCreationDate;
    }
    
    /**
     * Get StorageDevicePartition modification date.
     * @return StorageDevicePartition modification date
     */
    public Date getStorageDevicePartitionModificationDate() {
    	return storageDevicePartitionModificationDate;
    }
    
	/**
     * Compare this StorageDevicePartition with another StorageDevicePartition
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageDevicePartition, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof StorageDevicePartition ) ) return false;
        StorageDevicePartition dmp = (StorageDevicePartition) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
    /**
     * Set StorageDevicePartition name.
     * @param deviceModelPartition
     * 			: deviceModelPartition key
     * @throws MissingRequiredFieldsException
     */
    public void setDeviceModelPartition(Key deviceModelPartition)
    		throws MissingRequiredFieldsException {
    	if (deviceModelPartition == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"deviceModelPartition is missing.");
    	}
    	this.deviceModelPartition = deviceModelPartition;
    	this.storageDevicePartitionModificationDate = new Date();
    }
    
    /**
     * Set StorageDevicePartition comments.
     * @param storageDevicePartitionComments
     * 			: StorageDevicePartition comments
     */
    public void setStorageDevicePartitionComments(String storageDevicePartitionComments) {
    	this.storageDevicePartitionComments = storageDevicePartitionComments;
    	this.storageDevicePartitionModificationDate = new Date();
    }
}