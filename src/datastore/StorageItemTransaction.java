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
 * This class represents the StorageItemTransaction table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class StorageItemTransaction implements Comparable<StorageItemTransaction>, 
		Serializable{
	
	public static enum StorageItemTransactionAction {
		STORE, REMOVE, DISCARD, DONATE, SELL
	}
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private Key storageItemInstance;
    
    @Persistent
    private Key storageItemTransactionPerformedBy;
    
    @Persistent
    private Key storageDevicePartition;

    @Persistent
    private StorageItemTransactionAction storageItemTransactionAction;
    
    @Persistent
    private Date storageItemTransactionCreationDate;

    /**
     * StorageItemTransaction constructor.
     * @param storageItemInstance
     * 			: StorageItemInstance (parent) key
     * @param storageItemTransactionPerformedBy
     * 			: StorageItemTransaction performed by (CustomerUser Key)
     * @param storageDevicePartition
     * 			: StorageItemTransaction StorageDevicePartition key
     * @param storageItemTransactionAction
     * 			: StorageItemTransaction action
     * @throws MissingRequiredFieldsException
     */
    public StorageItemTransaction(
    		Key storageItemInstance,
    		Key storageItemTransactionPerformedBy, 
    		Key storageDevicePartition,
    		StorageItemTransactionAction storageItemTransactionAction) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (storageItemInstance == null ||
    			storageItemTransactionPerformedBy == null || 
    			storageItemTransactionAction == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
    	// Check action-based "required field" constraints
    	if (storageItemTransactionAction == StorageItemTransactionAction.STORE) {
    		if (storageDevicePartition == null) {
    			throw new MissingRequiredFieldsException(this.getClass(), 
        				"One or more required fields are missing.");
    		}
    	}

    	this.storageItemInstance = storageItemInstance;
        this.storageItemTransactionPerformedBy = storageItemTransactionPerformedBy;
        this.storageDevicePartition = storageDevicePartition;
        this.storageItemTransactionAction = storageItemTransactionAction;
        
        Date now = new Date();
        this.storageItemTransactionCreationDate = now;
    }

    /**
     * Get StorageItemTransaction key.
     * @return StorageItemTransaction key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get StorageItemInstance key
     * @return StorageItemInstance key
     */
    public Key getStorageItemInstance() {
    	return storageItemInstance;
    }

	/**
     * Get StorageItemTransaction performed by.
     * @return StorageItemTransaction performed by
     */
    public Key getStorageItemTransactionPerformedBy() {
        return storageItemTransactionPerformedBy;
    }

    /**
     * Get StorageItemTransaction StorageDevicePartition
     * @return StorageItemTransaction StorageDevicePartition key
     */
    public Key getStorageDevicePartition() {
    	return storageDevicePartition;
    }
    
    /**
     * Get storageItemTransactionAction from String
     * @param storageItemTransactionActionString
     * @return a StorageItemTransactionAction
     */
    public static StorageItemTransactionAction getStorageItemTransactionActionFromString(
    		String storageItemTransactionActionString) {
    	
    	if (storageItemTransactionActionString == null) {
    		return null;
    	}
    	else if (storageItemTransactionActionString.equalsIgnoreCase("store")) {
    		return StorageItemTransactionAction.STORE;
    	}
    	else if (storageItemTransactionActionString.equalsIgnoreCase("remove")) {
    		return StorageItemTransactionAction.REMOVE;
    	}
    	else if (storageItemTransactionActionString.equalsIgnoreCase("discard")) {
    		return StorageItemTransactionAction.DISCARD;
    	}
    	else if (storageItemTransactionActionString.equalsIgnoreCase("donate")) {
    		return StorageItemTransactionAction.DONATE;
    	}
    	else if (storageItemTransactionActionString.equalsIgnoreCase("sell")) {
    		return StorageItemTransactionAction.SELL;
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * Get StorageItemTransaction action.
     * @return StorageItemTransaction action
     */
    public StorageItemTransactionAction getStorageItemTransactionAction() {
    	return storageItemTransactionAction;
    }
    
    /**
     * Get StorageItemTransaction creation date.
     * @return StorageItemTransaction creation date
     */
    public Date getStorageItemTransactionCreationDate() {
    	return storageItemTransactionCreationDate;
    }
    
	@Override
	public int compareTo(StorageItemTransaction t1) {
		return t1.getStorageItemTransactionCreationDate().compareTo(
				this.getStorageItemTransactionCreationDate());
	}
	
	/**
     * Compare this StorageItemTransaction with another StorageItemTransaction
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this StorageItemTransaction, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof StorageItemTransaction ) ) return false;
        StorageItemTransaction sit = (StorageItemTransaction) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(sit.getKey()));
    }
}