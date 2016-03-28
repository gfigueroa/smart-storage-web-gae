/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the StorageDeviceContainer table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageDeviceContainer implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private Key storageDevicePartition;
	
	@Persistent
	private Key storageDeviceContainerParent;
	
    @Persistent
    private Long storageDeviceContainerModel;
    
    @Persistent
    private String storageDeviceContainerSerialNumber;
    
    @Persistent
    private String storageDeviceContainerLabel;
    
    @Persistent
    private String storageDeviceContainerDescription;
    
    @Persistent
    private Date storageDeviceContainerManufacturedDate;
    
    @Persistent
    private Date storageDeviceContainerShippingDate;
    
    @Persistent
    private String storageDeviceContainerComments;
    
    @Persistent
    private Date storageDeviceContainerCreationDate;
    
    @Persistent
    private Date  storageDeviceContainerModificationDate;

    /**
     * StorageDeviceContainer constructor.
     * @param storageDevicePartition
     * 			: storageDevicePartition key
     * @param storageDeviceContainerParent
     * 			: storageDeviceContainer parent key
     * @param storageDeviceContainerModel
     * 			: storageDeviceContainerModel model key
     * @param storageDeviceContainerSerialNumber
     * 			: storageDeviceContainer number
     * @param storageDeviceContainerLabel
     * 			: storageDeviceContainer label
     * @param storageDeviceContainerDescription
     * 			: storageDeviceContainer description
     * @param storageDeviceContainerManufacturedDate
     * 			: storageDeviceContainer manufacturedDate
     * @param storageDeviceContainerShippingDate
     * 			: storageDeviceContainer shippingDate
     * @param storageDeviceContainerComments
     * 			: storageDeviceContainer comments
     * @throws MissingRequiredFieldsException
     */
    public StorageDeviceContainer(
    		Key storageDevicePartition,
    		Key storageDeviceContainerParent,
    		Long storageDeviceContainerModel, 
    		String storageDeviceContainerSerialNumber,
    		String storageDeviceContainerLabel,
    		String storageDeviceContainerDescription, 
    		Date storageDeviceContainerManufacturedDate, 
    		Date storageDeviceContainerShippingDate,
    		String storageDeviceContainerComments) 
		throws MissingRequiredFieldsException {
        
    	// Check "required field" constraints
    	if (storageDeviceContainerModel == null || 
    			storageDeviceContainerSerialNumber == null ||
    			storageDeviceContainerLabel == null ||
    			storageDeviceContainerManufacturedDate == null || 
    			storageDeviceContainerShippingDate == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
    	if (storageDeviceContainerSerialNumber.isEmpty() ||
    			storageDeviceContainerLabel.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.storageDevicePartition = storageDevicePartition;
    	this.storageDeviceContainerParent = storageDeviceContainerParent;
    	this.storageDeviceContainerModel = storageDeviceContainerModel;
    	this.storageDeviceContainerSerialNumber = storageDeviceContainerSerialNumber;
    	this.storageDeviceContainerLabel = storageDeviceContainerLabel;
    	this.storageDeviceContainerDescription = storageDeviceContainerDescription;
    	this.storageDeviceContainerManufacturedDate = storageDeviceContainerManufacturedDate;
    	this.storageDeviceContainerShippingDate = storageDeviceContainerShippingDate;
    	this.storageDeviceContainerComments = storageDeviceContainerComments;
    	
    	Date now = new Date();
    	this.storageDeviceContainerCreationDate = now;
    	this.storageDeviceContainerModificationDate = now;
    }

	/**
     * Get StorageDeviceContainer key.
     * @return storageDeviceContainer key
     */
    public Key getKey() {
        return key;
    }
    
    /**
	 * @return the storageDevicePartition
	 */
	public Key getStorageDevicePartition() {
		return storageDevicePartition;
	}

	/**
	 * @return the storageDeviceContainerParent
	 */
	public Key getStorageDeviceContainerParent() {
		return storageDeviceContainerParent;
	}


	/**
     * Get storageDeviceContainerModel.
     * @return storageDeviceContainerModel key
     */
    public Long getStorageDeviceContainerModel() {
        return storageDeviceContainerModel;
    }
    
    /**
     * Get StorageDeviceContainer serial number.
     * @return storageDeviceContainer serial number
     */
    public String getStorageDeviceContainerSerialNumber() {
        return storageDeviceContainerSerialNumber;
    }
    
	/**
	 * @return the storageDeviceContainerLabel
	 */
	public String getStorageDeviceContainerLabel() {
		return storageDeviceContainerLabel;
	}
    
    /**
	 * @return the storageDeviceContainerDescription
	 */
	public String getStorageDeviceContainerDescription() {
		return storageDeviceContainerDescription;
	}

	/**
	 * @return the storageDeviceContainerManufacturedDate
	 */
	public Date getStorageDeviceContainerManufacturedDate() {
		return storageDeviceContainerManufacturedDate;
	}

	/**
	 * @return the storageDeviceContainerShippingDate
	 */
	public Date getStorageDeviceContainerShippingDate() {
		return storageDeviceContainerShippingDate;
	}

	/**
	 * @return the storageDeviceContainerComments
	 */
	public String getStorageDeviceContainerComments() {
		return storageDeviceContainerComments;
	}

    /**
     * Get storageDeviceContainer creation date.
     * @return the time this storageDeviceContainer was created
     */
    public Date getStorageDeviceContainerCreationDate() {
        return storageDeviceContainerCreationDate;
    }

    /**
     * Get storageDeviceContainer modification date.
     * @return the time this storageDeviceContainer was last modified
     */
    public Date getStorageDeviceContainerModificationDate() {
        return storageDeviceContainerModificationDate;
    }
    
    /**
     * Compare this storageDeviceContainer with another storageDeviceContainer
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageDeviceContainer, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if (!(o instanceof StorageDeviceContainer) ) return false;
        StorageDeviceContainer storageDeviceContainer = (StorageDeviceContainer) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(storageDeviceContainer.getKey()));
    }
    
	/**
	 * @param storageDevicePartition the storageDevicePartition to set
	 */
	public void setStorageDevicePartition(Key storageDevicePartition) {
		this.storageDevicePartition = storageDevicePartition;
		this.storageDeviceContainerModificationDate = new Date();
	}

	/**
	 * @param storageDeviceContainerParent the storageDeviceContainerParent to set
	 */
	public void setStorageDeviceContainerParent(Key storageDeviceContainerParent) {
		this.storageDeviceContainerParent = storageDeviceContainerParent;
		this.storageDeviceContainerModificationDate = new Date();
	}
    
    /**
     * Set storageDeviceContainerModel.
     * @param storageDeviceContainerModel
     * 			: storageDeviceContainerModel key
     * @throws MissingRequiredFieldsException
     */
    public void setStorageDeviceContainerModel(Long storageDeviceContainerModel)
    		throws MissingRequiredFieldsException {
    	if (storageDeviceContainerModel == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"storageDeviceContainerModel is missing.");
    	}
    	this.storageDeviceContainerModel = storageDeviceContainerModel;
    	this.storageDeviceContainerModificationDate = new Date();
    }
    
    /**
     * Set StorageDeviceContainer serial number.
     * @param storageDeviceContainerSerialNumber
     * 			: storageDeviceContainer serial number
     * @throws MissingRequiredFieldsException
     */
    public void setStorageDeviceContainerSerialNumber(String storageDeviceContainerSerialNumber)
    		throws MissingRequiredFieldsException {
    	if (storageDeviceContainerSerialNumber == null || 
    			storageDeviceContainerSerialNumber.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDeviceContainer serial number is missing.");
    	}
    	this.storageDeviceContainerSerialNumber = storageDeviceContainerSerialNumber;
    	this.storageDeviceContainerModificationDate = new Date();
    }
    

	/**
	 * @param storageDeviceContainerLabel the storageDeviceContainerLabel to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceContainerLabel(String storageDeviceContainerLabel) 
			throws MissingRequiredFieldsException {
		if (storageDeviceContainerLabel == null || 
    			storageDeviceContainerLabel.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDeviceContainer label is missing.");
    	}
		this.storageDeviceContainerLabel = storageDeviceContainerLabel;
		this.storageDeviceContainerModificationDate = new Date();
	}
    
    /**
	 * @param storageDeviceContainerDescription the storageDeviceContainerDescription to set
	 */
	public void setStorageDeviceContainerDescription(String storageDeviceContainerDescription) {
		this.storageDeviceContainerDescription = storageDeviceContainerDescription;
		this.storageDeviceContainerModificationDate = new Date();
	}

	/**
	 * @param storageDeviceContainerManufacturedDate the storageDeviceContainerManufacturedDate to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceContainerManufacturedDate(
			Date storageDeviceContainerManufacturedDate) 
			throws MissingRequiredFieldsException {
		if (storageDeviceContainerManufacturedDate == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDeviceContainer manufacturedDate is missing.");
    	}
		this.storageDeviceContainerManufacturedDate = storageDeviceContainerManufacturedDate;
		this.storageDeviceContainerModificationDate = new Date();
	}

	/**
	 * @param storageDeviceContainerShippingDate the storageDeviceContainerShippingDate to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageDeviceContainerShippingDate(Date storageDeviceContainerShippingDate) 
			throws MissingRequiredFieldsException {
		if (storageDeviceContainerShippingDate == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageDeviceContainer shippingDate is missing.");
    	}
		this.storageDeviceContainerShippingDate = storageDeviceContainerShippingDate;
		this.storageDeviceContainerModificationDate = new Date();
	}

	/**
	 * @param storageDeviceContainerComments the storageDeviceContainerComments to set
	 */
	public void setStorageDeviceContainerComments(String storageDeviceContainerComments) {
		this.storageDeviceContainerComments = storageDeviceContainerComments;
		this.storageDeviceContainerModificationDate = new Date();
	}
}