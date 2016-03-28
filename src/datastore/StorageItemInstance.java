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
 * This class represents the StorageItemInstance table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageItemInstance implements Serializable, Comparable<StorageItemInstance> {
	
	public static enum IndicationLightValue {
		RED, GREEN
	}

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
    @Persistent
    private Key storageItemInstanceOwner;
    
    @Persistent
    private Key lastStorageItemTransaction;
    
    @Persistent
    private String storageItemInstanceSerialNumber;
    
    @Persistent
    private String storageItemInstanceLabel;
    
    @Persistent
    private Date storageItemInstanceExpirationTime;
    
    @Persistent
    private String storageItemInstanceWorksheet;
    
    @Persistent
    private IndicationLightValue storageItemInstanceIndicationLight;
    
    @Persistent
    private String storageItemInstanceComments;
    
    @Persistent
    private Date storageItemInstanceCreationDate;
    
    @Persistent
    private Date  storageItemInstanceModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<StorageItemTransaction> storageItemTransactions;

    /**
     * StorageItemInstance constructor.
     * @param storageItemInstanceOwner
     * 			: storageItemInstanceOwner model key
     * @param storageItemInstanceSerialNumber
     * 			: storageItemInstance number
     * @param storageItemInstanceLabel
     * 			: storageItemInstance manufacturedDate
     * @param storageItemInstanceExpirationTime
     * 			: storageItemInstance shippingDate
     * @param storageItemInstanceWorksheet
     * 			: storageItemInstance worksheet
     * @param storageItemInstanceIndicationLight
     * 			: storageItemInstance indicationLight
     * @param storageItemInstanceComments
     * 			: storageItemInstance comments
     * @throws MissingRequiredFieldsException
     */
    public StorageItemInstance(
    		Key storageItemInstanceOwner, 
    		String storageItemInstanceSerialNumber,
    		String storageItemInstanceLabel, 
    		Date storageItemInstanceExpirationTime,
    		String storageItemInstanceWorksheet,
    		IndicationLightValue storageItemInstanceIndicationLight,
    		String storageItemInstanceComments) 
		throws MissingRequiredFieldsException {
        
    	// Check "required field" constraints
    	if (storageItemInstanceSerialNumber == null || storageItemInstanceLabel == null
    			|| storageItemInstanceIndicationLight == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
    	if (storageItemInstanceSerialNumber.isEmpty() || storageItemInstanceLabel.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.storageItemInstanceOwner = storageItemInstanceOwner;
    	this.storageItemInstanceSerialNumber = storageItemInstanceSerialNumber;
    	this.storageItemInstanceLabel = storageItemInstanceLabel;
    	this.storageItemInstanceExpirationTime = storageItemInstanceExpirationTime;
    	this.storageItemInstanceWorksheet = storageItemInstanceWorksheet;
    	this.storageItemInstanceIndicationLight = storageItemInstanceIndicationLight;
    	this.storageItemInstanceComments = storageItemInstanceComments;
    	
    	Date now = new Date();
    	this.storageItemInstanceCreationDate = now;
    	this.storageItemInstanceModificationDate = now;
    	
    	this.storageItemTransactions = new ArrayList<>();
    }

	/**
     * Get StorageItemInstance key.
     * @return storageItemInstance key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get storageItemInstanceOwner.
     * @return storageItemInstanceOwner key
     */
    public Key getStorageItemInstanceOwner() {
        return storageItemInstanceOwner;
    }
    
    /**
     * Get lastStorageItemTransaction.
     * @return lastStorageItemTransaction key
     */
    public Key getLastStorageItemTransaction() {
    	return lastStorageItemTransaction;
    }
    
    /**
     * Get StorageItemInstance serial number.
     * @return storageItemInstance serial number
     */
    public String getStorageItemInstanceSerialNumber() {
        return storageItemInstanceSerialNumber;
    }
    
	/**
	 * @return the storageItemInstanceLabel
	 */
	public String getStorageItemInstanceLabel() {
		return storageItemInstanceLabel;
	}

	/**
	 * @return the storageItemInstanceExpirationTime
	 */
	public Date getStorageItemInstanceExpirationTime() {
		return storageItemInstanceExpirationTime;
	}

	/**
	 * @return the storageItemInstanceWorksheet
	 */
	public String getStorageItemInstanceWorksheet() {
		return storageItemInstanceWorksheet;
	}

	/**
	 * Get IndicationLightValue from a string
	 * @param indicationLightValueString
	 * @return IndicationLightValue represented by the string
	 */
	public static IndicationLightValue getIndicationLightValueFromString(
			String indicationLightValueString) {
		
		 if (indicationLightValueString == null) {
			 return null;
		 }
		 else if (indicationLightValueString.equalsIgnoreCase("red")) {
			 return IndicationLightValue.RED;
		 }
		 else if (indicationLightValueString.equalsIgnoreCase("green")) {
			 return IndicationLightValue.GREEN;
		 }
		 else {
			 return null;
		 }
	}
	
	/**
	 * @return the storageItemInstanceIndicationLight
	 */
	public IndicationLightValue getStorageItemInstanceIndicationLight() {
		if (storageItemInstanceIndicationLight == null) {
			storageItemInstanceIndicationLight = IndicationLightValue.GREEN;
		}
		return storageItemInstanceIndicationLight;
	}

	/**
	 * @return the storageItemInstanceComments
	 */
	public String getStorageItemInstanceComments() {
		return storageItemInstanceComments;
	}

    /**
     * Get storageItemInstance creation date.
     * @return the time this storageItemInstance was created
     */
    public Date getStorageItemInstanceCreationDate() {
        return storageItemInstanceCreationDate;
    }

    /**
     * Get storageItemInstance modification date.
     * @return the time this storageItemInstance was last modified
     */
    public Date getStorageItemInstanceModificationDate() {
        return storageItemInstanceModificationDate;
    }
    
    /**
     * Get storageItemTransactions
     * @return
     */
    public ArrayList<StorageItemTransaction> getStorageItemTransactions() {
    	return storageItemTransactions;
    }
    
    /**
     * Compare this storageItemInstance with another storageItemInstance
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageItemInstance, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if (!(o instanceof StorageItemInstance ) ) return false;
        StorageItemInstance storageItemInstance = (StorageItemInstance) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(storageItemInstance.getKey()));
    }
    
	@Override
	public int compareTo(StorageItemInstance sit) {
		return this.getStorageItemInstanceSerialNumber().compareTo(
				sit.getStorageItemInstanceSerialNumber());
	}
     
    /**
     * Set storageItemInstanceOwner.
     * @param storageItemInstanceOwner
     * 			: storageItemInstanceOwner key
     */
    public void setStorageItemInstanceOwner(Key storageItemInstanceOwner) {
    	this.storageItemInstanceOwner = storageItemInstanceOwner;
    	this.storageItemInstanceModificationDate = new Date();
    }
    
	/**
	 * @param lastStorageItemTransaction the lastStorageItemTransaction to set
	 */
	public void setLastStorageItemTransaction(Key lastStorageItemTransaction) {
		this.lastStorageItemTransaction = lastStorageItemTransaction;
	}
    
    /**
     * Set StorageItemInstance serial number.
     * @param storageItemInstanceSerialNumber
     * 			: storageItemInstance serial number
     * @throws MissingRequiredFieldsException 
     */
    public void setStorageItemInstanceSerialNumber(String storageItemInstanceSerialNumber) 
    		throws MissingRequiredFieldsException {
    	if (storageItemInstanceSerialNumber == null || 
    			storageItemInstanceSerialNumber.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageItemInstance SerialNumber is missing.");
    	}
    	this.storageItemInstanceSerialNumber = storageItemInstanceSerialNumber;
    	this.storageItemInstanceModificationDate = new Date();
    }
    
	/**
	 * @param storageItemInstanceLabel the storageItemInstanceLabel to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageItemInstanceLabel(
			String storageItemInstanceLabel) 
			throws MissingRequiredFieldsException {
		if (storageItemInstanceLabel == null || storageItemInstanceLabel.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"StorageItemInstance manufacturedDate is missing.");
    	}
		this.storageItemInstanceLabel = storageItemInstanceLabel;
		this.storageItemInstanceModificationDate = new Date();
	}

	/**
	 * @param storageItemInstanceExpirationTime the storageItemInstanceExpirationTime to set
	 */
	public void setStorageItemInstanceExpirationTime(Date storageItemInstanceExpirationTime) {
		this.storageItemInstanceExpirationTime = storageItemInstanceExpirationTime;
		this.storageItemInstanceModificationDate = new Date();
	}

	/**
	 * @param storageItemInstanceWorksheet the storageItemInstanceWorksheet to set
	 */
	public void setStorageItemInstanceWorksheet(String storageItemInstanceWorksheet) {
		this.storageItemInstanceWorksheet = storageItemInstanceWorksheet;
		this.storageItemInstanceModificationDate = new Date();
	}
	
	/**
	 * @param storageItemInstanceIndicationLight the storageItemInstanceIndicationLight to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setStorageItemInstanceIndicationLight(
			IndicationLightValue storageItemInstanceIndicationLight) 
					throws MissingRequiredFieldsException {
		
		if (storageItemInstanceIndicationLight == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"storageItemInstanceIndicationLight is missing.");
    	}
		
		this.storageItemInstanceIndicationLight = storageItemInstanceIndicationLight;
		this.storageItemInstanceModificationDate = new Date();
	}

	/**
	 * @param storageItemInstanceComments the storageItemInstanceComments to set
	 */
	public void setStorageItemInstanceComments(String storageItemInstanceComments) {
		this.storageItemInstanceComments = storageItemInstanceComments;
		this.storageItemInstanceModificationDate = new Date();
	}
	
	/**
     * Add a new StorageItemTransaction to this StorageItemInstance.
     * @param storageItemTransaction
     * 			: new storageItemTransaction to be added
     */
    public void addStorageItemTransaction(
    		StorageItemTransaction storageItemTransaction) {
    	this.storageItemTransactions.add(storageItemTransaction);
    }
    
    /**
     * Remove StorageItemTransaction from the StorageItemInstance.
     * @param storageItemTransaction
     * 			: storageItemTransactionto be removed
     * @throws InexistentObjectException
     */
    public void removeStorageItemTransaction(
    		StorageItemTransaction storageItemTransaction) 
    		throws InexistentObjectException {
    	if (!this.storageItemTransactions.remove(storageItemTransaction)) {
    		throw new InexistentObjectException
    				(StorageItemTransaction.class, "StorageItemTransaction not found!");
    	}
    }
}