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
 * This class represents the DeviceModelDoor table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class DeviceModelDoor implements Serializable, Comparable<DeviceModelDoor> {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

    @Persistent
    private Integer deviceModelDoorNumber;

    @Persistent
    private String deviceModelDoorComments;
    
    @Persistent
    private Date deviceModelDoorCreationDate;
    
    @Persistent
    private Date deviceModelDoorModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<DeviceModelPartition> deviceModelPartitions;

    /**
     * DeviceModelDoor constructor.
     * @param deviceModelDoorNumber
     * 			: DeviceModelDoor name
     * @param deviceModelDoorComments
     * 			: DeviceModelDoor comments
     * @throws MissingRequiredFieldsException
     */
    public DeviceModelDoor(Integer deviceModelDoorNumber, 
    		String deviceModelDoorComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (deviceModelDoorNumber == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.deviceModelDoorNumber = deviceModelDoorNumber;
        this.deviceModelDoorComments = deviceModelDoorComments;
        
        this.deviceModelPartitions = new ArrayList<>();
        
        Date now = new Date();
        this.deviceModelDoorCreationDate = now;
        this.deviceModelDoorModificationDate = now;
    }

    /**
     * Get DeviceModelDoor key.
     * @return DeviceModelDoor key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get DeviceModelDoor name.
     * @return DeviceModelDoor name
     */
    public Integer getDeviceModelDoorNumber() {
        return deviceModelDoorNumber;
    }
    
    /**
     * Get DeviceModelDoor comments.
     * @return DeviceModelDoor comments
     */
    public String getDeviceModelDoorComments() {
    	return deviceModelDoorComments;
    }
    
    /**
     * Get DeviceModelDoor creation date.
     * @return DeviceModelDoor creation date
     */
    public Date getDeviceModelDoorCreationDate() {
    	return deviceModelDoorCreationDate;
    }
    
    /**
     * Get DeviceModelDoor modification date.
     * @return DeviceModelDoor modification date
     */
    public Date getDeviceModelDoorModificationDate() {
    	return deviceModelDoorModificationDate;
    }
    
    /**
     * Get DeviceModelPartitions
     * @return deviceModelPartitions
     */
    public ArrayList<DeviceModelPartition> getDeviceModelPartitions() {
    	return deviceModelPartitions;
    }
    
	/**
     * Compare this DeviceModelDoor with another DeviceModelDoor
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this DeviceModelDoor, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof DeviceModelDoor ) ) return false;
        DeviceModelDoor dmp = (DeviceModelDoor) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(dmp.getKey()));
    }
    
	@Override
	public int compareTo(DeviceModelDoor dmd) {
		return this.getDeviceModelDoorNumber().compareTo(
				dmd.getDeviceModelDoorNumber());
	}
    
    /**
     * Set DeviceModelDoor name.
     * @param deviceModelDoorNumber
     * 			: DeviceModelDoor name
     * @throws MissingRequiredFieldsException
     */
    public void setDeviceModelDoorNumber(Integer deviceModelDoorNumber)
    		throws MissingRequiredFieldsException {
    	if (deviceModelDoorNumber == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"deviceModelDoorNumber is missing.");
    	}
    	this.deviceModelDoorNumber = deviceModelDoorNumber;
    	this.deviceModelDoorModificationDate = new Date();
    }
    
    /**
     * Set DeviceModelDoor comments.
     * @param deviceModelDoorComments
     * 			: DeviceModelDoor comments
     */
    public void setDeviceModelDoorComments(String deviceModelDoorComments) {
    	this.deviceModelDoorComments = deviceModelDoorComments;
    	this.deviceModelDoorModificationDate = new Date();
    }
    
	/**
	 * Add a deviceModelPartition to this DeviceModelDoor
	 * @param deviceModelPartition
	 */
	public void addDeviceModelPartition(DeviceModelPartition deviceModelPartition) {
		deviceModelPartitions.add(deviceModelPartition);
	}
	
    /**
     * Remove a DeviceModelPartitionfrom the DeviceModelDoor.
     * @param deviceModelPartition
     * 			: DeviceModelPartition to be removed
     * @throws InexistentObjectException
     */
    public void removeDeviceModelPartition(DeviceModelPartition deviceModelPartition) 
    		throws InexistentObjectException {
    	if (!deviceModelPartitions.remove(deviceModelPartition)) {
    		throw new InexistentObjectException(
    				DeviceModelPartition.class, "DeviceModelPartition not found!");
    	}
    }
}