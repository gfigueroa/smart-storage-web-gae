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

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the StorageDeviceContainerModel table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class StorageDeviceContainerModel {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long key;

    @Persistent
    private String storageDeviceContainerModelName;
    
    @Persistent
    private String storageDeviceContainerModelDescription;
    
    @Persistent
    private String storageDeviceContainerModelVersionNumber;
    
    @Persistent
    private Date storageDeviceContainerModelDesignTime;

    @Persistent
    private String storageDeviceContainerModelComments;
    
    @Persistent
    private Date storageDeviceContainerModelCreationDate;
    
    @Persistent
    private Date storageDeviceContainerModelModificationDate;

    /**
     * StorageDeviceContainerModel constructor.
     * @param storageDeviceContainerModelName
     * 			: StorageDeviceContainerModel name
     * @param storageDeviceContainerModelDescription
     * 			: StorageDeviceContainerModel description
     * @param storageDeviceContainerModelVersionNumber
     * 			: StorageDeviceContainerModel version number
     * @param storageDeviceContainerModelDesignTime
     * 			: StorageDeviceContainerModel design time
     * @param storageDeviceContainerModelComments
     * 			: StorageDeviceContainerModel comments
     * @throws MissingRequiredFieldsException
     */
    public StorageDeviceContainerModel(String storageDeviceContainerModelName, 
    		String storageDeviceContainerModelDescription,
    		String storageDeviceContainerModelVersionNumber,
    		Date storageDeviceContainerModelDesignTime,
    		String storageDeviceContainerModelComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (storageDeviceContainerModelName == null || 
    			storageDeviceContainerModelDesignTime == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (storageDeviceContainerModelName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.storageDeviceContainerModelName = storageDeviceContainerModelName;
        this.storageDeviceContainerModelDescription = storageDeviceContainerModelDescription;
        this.storageDeviceContainerModelVersionNumber = storageDeviceContainerModelVersionNumber;
        this.storageDeviceContainerModelDesignTime = storageDeviceContainerModelDesignTime;
        this.storageDeviceContainerModelComments = storageDeviceContainerModelComments;
        
        Date now = new Date();
        this.storageDeviceContainerModelCreationDate = now;
        this.storageDeviceContainerModelModificationDate = now;
    }

    /**
     * Get StorageDeviceContainerModel key.
     * @return StorageDeviceContainerModel key
     */
    public Long getKey() {
        return key;
    }
    
    /**
     * Get StorageDeviceContainerModel name.
     * @return StorageDeviceContainerModel name
     */
    public String getStorageDeviceContainerModelName() {
        return storageDeviceContainerModelName;
    }

    /**
     * Get StorageDeviceContainerModel description.
     * @return StorageDeviceContainerModel description
     */
    public String getStorageDeviceContainerModelDescription() {
    	return storageDeviceContainerModelDescription;
    }
    
    /**
	 * @return the storageDeviceContainerModelVersionNumber
	 */
	public String getStorageDeviceContainerModelVersionNumber() {
		return storageDeviceContainerModelVersionNumber;
	}

	/**
	 * @return the storageDeviceContainerModelDesignTime
	 */
	public Date getStorageDeviceContainerModelDesignTime() {
		return storageDeviceContainerModelDesignTime;
	}

	/**
     * Get StorageDeviceContainerModel comments.
     * @return StorageDeviceContainerModel comments
     */
    public String getStorageDeviceContainerModelComments() {
    	return storageDeviceContainerModelComments;
    }
    
    /**
     * Get StorageDeviceContainerModel creation date.
     * @return StorageDeviceContainerModel creation date
     */
    public Date getStorageDeviceContainerModelCreationDate() {
    	return storageDeviceContainerModelCreationDate;
    }
    
    /**
     * Get StorageDeviceContainerModel modification date.
     * @return StorageDeviceContainerModel modification date
     */
    public Date getStorageDeviceContainerModelModificationDate() {
    	return storageDeviceContainerModelModificationDate;
    }
    
    /**
     * Set StorageDeviceContainerModel name.
     * @param storageDeviceContainerModelName
     * 			: StorageDeviceContainerModel name
     * @throws MissingRequiredFieldsException
     */
    public void setStorageDeviceContainerModelName(
    		String storageDeviceContainerModelName)
    		throws MissingRequiredFieldsException {
    	if (storageDeviceContainerModelName == null || 
    			storageDeviceContainerModelName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDeviceContainerModel name is missing.");
    	}
    	this.storageDeviceContainerModelName = storageDeviceContainerModelName;
    	this.storageDeviceContainerModelModificationDate = new Date();
    }
    
    /**
     * Set StorageDeviceContainerModel description.
     * @param storageDeviceContainerModelDescription
     * 			: StorageDeviceContainerModel description
     */
    public void setStorageDeviceContainerModelDescription(
    		String storageDeviceContainerModelDescription) {
    	this.storageDeviceContainerModelDescription = 
    			storageDeviceContainerModelDescription;
    	this.storageDeviceContainerModelModificationDate = new Date();
    }
    

	/**
	 * @param storageDeviceContainerModelVersionNumber 
	 * 		the storageDeviceContainerModelVersionNumber to set
	 */
	public void setStorageDeviceContainerModelVersionNumber(
			String storageDeviceContainerModelVersionNumber) {
		this.storageDeviceContainerModelVersionNumber = 
				storageDeviceContainerModelVersionNumber;
		this.storageDeviceContainerModelModificationDate = new Date();
	}

	/**
	 * @param storageDeviceContainerModelDesignTime 
	 * 		the storageDeviceContainerModelDesignTime to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceContainerModelDesignTime(
			Date storageDeviceContainerModelDesignTime) 
					throws MissingRequiredFieldsException {
		
		if (storageDeviceContainerModelDesignTime == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDeviceContainerModel design time is missing.");
    	}
		
		this.storageDeviceContainerModelDesignTime = 
				storageDeviceContainerModelDesignTime;
		this.storageDeviceContainerModelModificationDate = new Date();
	}
    
    /**
     * Set StorageDeviceContainerModel comments.
     * @param storageDeviceContainerModelComments
     * 			: StorageDeviceContainerModel comments
     */
    public void setStorageDeviceContainerModelComments(
    		String storageDeviceContainerModelComments) {
    	this.storageDeviceContainerModelComments = storageDeviceContainerModelComments;
    	this.storageDeviceContainerModelModificationDate = new Date();
    }
}