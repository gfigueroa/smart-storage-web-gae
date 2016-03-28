/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the StorageDeviceCustomerUser table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class StorageDeviceCustomerUser {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long key;

    @Persistent
    private Key customerUser;
    
    @Persistent
    private Key storageDevice;
    
    @Persistent
    private Date storageDeviceCustomerUserCreationDate;

    /**
     * StorageDeviceCustomerUser constructor.
     * @param customerUser
     * 			: StorageDeviceCustomerUser key
     * @param storageDevice
     * 			: StorageDevice key
     * @throws MissingRequiredFieldsException
     */
    public StorageDeviceCustomerUser(Key customerUser, 
    		Key storageDevice) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (customerUser == null || storageDevice == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.customerUser = customerUser;
        this.storageDevice = storageDevice;
        
        Date now = new Date();
        this.storageDeviceCustomerUserCreationDate = now;
    }

    /**
     * Get StorageDeviceCustomerUser key.
     * @return StorageDeviceCustomerUser key
     */
    public Long getKey() {
        return key;
    }
    
    /**
     * Get StorageDeviceCustomerUser key.
     * @return StorageDeviceCustomerUser key
     */
    public Key getCustomerUser() {
        return customerUser;
    }

    /**
     * Get StorageDevice key.
     * @return StorageDevice key
     */
    public Key getStorageDevice() {
    	return storageDevice;
    }
    
    /**
     * Get StorageDeviceCustomerUser creation date.
     * @return StorageDeviceCustomerUser creation date
     */
    public Date getStorageDeviceCustomerUserCreationDate() {
    	return storageDeviceCustomerUserCreationDate;
    }
}