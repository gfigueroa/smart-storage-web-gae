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
 * This class represents the DeviceModelPartition table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class DeviceModelPartition implements Serializable {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

    @Persistent
    private String deviceModelPartitionName;

    @Persistent
    private String deviceModelPartitionComments;
    
    @Persistent
    private Date deviceModelPartitionCreationDate;
    
    @Persistent
    private Date deviceModelPartitionModificationDate;

    /**
     * DeviceModelPartition constructor.
     * @param deviceModelPartitionName
     * 			: DeviceModelPartition name
     * @param deviceModelPartitionComments
     * 			: DeviceModelPartition comments
     * @throws MissingRequiredFieldsException
     */
    public DeviceModelPartition(String deviceModelPartitionName, 
    		String deviceModelPartitionComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (deviceModelPartitionName == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (deviceModelPartitionName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.deviceModelPartitionName = deviceModelPartitionName;
        this.deviceModelPartitionComments = deviceModelPartitionComments;
        
        Date now = new Date();
        this.deviceModelPartitionCreationDate = now;
        this.deviceModelPartitionModificationDate = now;
    }

    /**
     * Get DeviceModelPartition key.
     * @return DeviceModelPartition key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get DeviceModelPartition name.
     * @return DeviceModelPartition name
     */
    public String getDeviceModelPartitionName() {
        return deviceModelPartitionName;
    }
    
    /**
     * Get DeviceModelPartition comments.
     * @return DeviceModelPartition comments
     */
    public String getDeviceModelPartitionComments() {
    	return deviceModelPartitionComments;
    }
    
    /**
     * Get DeviceModelPartition creation date.
     * @return DeviceModelPartition creation date
     */
    public Date getDeviceModelPartitionCreationDate() {
    	return deviceModelPartitionCreationDate;
    }
    
    /**
     * Get DeviceModelPartition modification date.
     * @return DeviceModelPartition modification date
     */
    public Date getDeviceModelPartitionModificationDate() {
    	return deviceModelPartitionModificationDate;
    }
    
	/**
     * Compare this DeviceModelPartition with another DeviceModelPartition
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this DeviceModelPartition, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof DeviceModelPartition ) ) return false;
        DeviceModelPartition dmp = (DeviceModelPartition) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
    /**
     * Set DeviceModelPartition name.
     * @param deviceModelPartitionName
     * 			: DeviceModelPartition name
     * @throws MissingRequiredFieldsException
     */
    public void setDeviceModelPartitionName(String deviceModelPartitionName)
    		throws MissingRequiredFieldsException {
    	if (deviceModelPartitionName == null || deviceModelPartitionName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"deviceModelPartitionName is missing.");
    	}
    	this.deviceModelPartitionName = deviceModelPartitionName;
    	this.deviceModelPartitionModificationDate = new Date();
    }
    
    /**
     * Set DeviceModelPartition comments.
     * @param deviceModelPartitionComments
     * 			: DeviceModelPartition comments
     */
    public void setDeviceModelPartitionComments(String deviceModelPartitionComments) {
    	this.deviceModelPartitionComments = deviceModelPartitionComments;
    	this.deviceModelPartitionModificationDate = new Date();
    }
}