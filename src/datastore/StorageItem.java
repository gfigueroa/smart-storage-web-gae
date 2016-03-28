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
 * This class represents the StorageItem table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageItem implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
	private Key customerKey;

    @Persistent
    private String storageItemPartNumber;
    
    @Persistent
    private String storageItemName;
    
    @Persistent
    private String storageItemDescription;
    
    @Persistent
    private Integer storageItemMSDLevel;
    
    @Persistent
    private String storageItemComments;
    
    @Persistent
    private Date storageItemCreationDate;
    
    @Persistent
    private Date  storageItemModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<StorageItemInstance> storageItemInstances;
    
    @Persistent
    private Integer storageItemInstancesCount;

    /**
     * StorageItem constructor.
     * @param customerKey
     * 			: Customer key
     * @param storageItemPartNumber
     * 			: storageItem privateEncryptionKey
     * @param storageItemName
     * 			: storageItem manufacturedDate
     * @param storageItemDescription
     * 			: storageItem description
     * @param storageItemMSDLevel
     * 			: storageItem MSDLevel
     * @param storageItemComments
     * 			: storageItem comments
     * @throws MissingRequiredFieldsException
     */
    public StorageItem(
    		Key customerKey,
    		String storageItemPartNumber,
    		String storageItemName, 
    		String storageItemDescription, 
    		Integer storageItemMSDLevel,
    		String storageItemComments) 
		throws MissingRequiredFieldsException {
        
    	// Check "required field" constraints
    	if (storageItemName == null || customerKey == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
    	if (storageItemName.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.customerKey = customerKey;
    	this.storageItemPartNumber = storageItemPartNumber;
    	this.storageItemName = storageItemName;
    	this.storageItemDescription = storageItemDescription;
    	this.storageItemMSDLevel = storageItemMSDLevel;
    	this.storageItemComments = storageItemComments;
    	
    	Date now = new Date();
    	this.storageItemCreationDate = now;
    	this.storageItemModificationDate = now;
    	
    	this.storageItemInstances = new ArrayList<>();
    	this.storageItemInstancesCount = 0;
    }

	/**
     * Get StorageItem key.
     * @return storageItem key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get Customer key
     * @return customerKey
     */
    public Key getCustomerKey() {
    	return customerKey;
    }
    
	/**
	 * @return the storageItemPartNumber
	 */
	public String getStorageItemPartNumber() {
		return storageItemPartNumber;
	}
    
	/**
	 * @return the storageItemName
	 */
	public String getStorageItemName() {
		return storageItemName;
	}
    
    /**
	 * @return the storageItemDescription
	 */
	public String getStorageItemDescription() {
		return storageItemDescription;
	}

	/**
	 * @return the storageItemMSDLevel
	 */
	public Integer getStorageItemMSDLevel() {
		return storageItemMSDLevel;
	}

	/**
	 * @return the storageItemComments
	 */
	public String getStorageItemComments() {
		return storageItemComments;
	}

    /**
     * Get storageItem creation date.
     * @return the time this storageItem was created
     */
    public Date getStorageItemCreationDate() {
        return storageItemCreationDate;
    }

    /**
     * Get storageItem modification date.
     * @return the time this storageItem was last modified
     */
    public Date getStorageItemModificationDate() {
        return storageItemModificationDate;
    }
    

	/**
	 * @return the storageItemInstances
	 */
	public ArrayList<StorageItemInstance> getStorageItemInstances() {
		return storageItemInstances;
	}
	
	/**
	 * @return the storageItemInstance size
	 */
	public Integer getStorageItemInstancesCount() {
		return storageItemInstancesCount;
	}
    
    /**
     * Compare this storageItem with another storageItem
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageItem, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if (!(o instanceof StorageItem ) ) return false;
        StorageItem storageItem = (StorageItem) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(storageItem.getKey()));
    }
    
	/**
	 * @param storageItemPartNumber the storageItemPartNumber to set
	 */
	public void setStorageItemPartNumber(String storageItemPartNumber) {
		this.storageItemPartNumber = storageItemPartNumber;
		this.storageItemModificationDate = new Date();
	}
    
	/**
	 * @param storageItemName the storageItemName to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageItemName(
			String storageItemName) 
			throws MissingRequiredFieldsException {
		if (storageItemName == null || storageItemName.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageItem manufacturedDate is missing.");
    	}
		this.storageItemName = storageItemName;
		this.storageItemModificationDate = new Date();
	}
    
    /**
	 * @param storageItemDescription the storageItemDescription to set
	 */
	public void setStorageItemDescription(String storageItemDescription) {
		this.storageItemDescription = storageItemDescription;
		this.storageItemModificationDate = new Date();
	}
	
	/**
	 * @param storageItemMSDLevel the storageItemMSDLevel to set
	 */
	public void setStorageItemMSDLevel(Integer storageItemMSDLevel) {
		this.storageItemMSDLevel = storageItemMSDLevel;
		this.storageItemModificationDate = new Date();
	}

	/**
	 * @param storageItemComments the storageItemComments to set
	 */
	public void setStorageItemComments(String storageItemComments) {
		this.storageItemComments = storageItemComments;
		this.storageItemModificationDate = new Date();
	}
	
    /**
     * Add a new StorageItemInstance to this StorageItem.
     * @param storageItemInstance
     * 			: new storageItemInstance to be added
     */
    public void addStorageItemInstance(StorageItemInstance storageItemInstance) {
    	this.storageItemInstances.add(storageItemInstance);
    	this.storageItemInstancesCount++;
    }
    
    /**
     * Remove StorageItemInstance from the StorageItem.
     * @param storageItemInstance
     * 			: StorageItemInstance to be removed
     * @throws InexistentObjectException
     */
    public void removeStorageItemInstance(StorageItemInstance storageItemInstance) 
    		throws InexistentObjectException {
    	if (!this.storageItemInstances.remove(storageItemInstance)) {
    		throw new InexistentObjectException
    				(StorageItemInstance.class, "StorageItemInstance not found!");
    	}
    	this.storageItemInstancesCount--;
    }
}