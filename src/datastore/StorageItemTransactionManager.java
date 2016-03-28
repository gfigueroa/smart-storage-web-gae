/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import datastore.StorageItemTransaction.StorageItemTransactionAction;

import exceptions.InexistentObjectException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the StorageItemTransaction class.
 * 
 */

public class StorageItemTransactionManager {
	
	private static final Logger log = 
        Logger.getLogger(StorageItemTransactionManager.class.getName());
	
	/**
     * Get a StorageItemTransaction instance from the datastore given the StorageItemTransaction key.
     * @param key
     * 			: the StorageItemTransaction's key
     * @return StorageItemTransaction instance, null if StorageItemTransaction is not found
     */
	public static StorageItemTransaction getStorageItemTransaction(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		StorageItemTransaction storageItemTransaction;
		try  {
			storageItemTransaction = pm.getObjectById(StorageItemTransaction.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return storageItemTransaction;
	}
	
	/**
     * Get all StorageItemTransaction instances from the datastore.
     * @return All StorageItemTransaction instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageItemTransaction> getAllStorageItemTransactions() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageItemTransaction.class);

        List<StorageItemTransaction> types = null;
        try {
        	types = (List<StorageItemTransaction>) query.execute();
            // touch all elements
            for (StorageItemTransaction t : types)
                t.getStorageItemTransactionPerformedBy();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Get StorageItemTransaction instances from the datastore
     * from the given StorageItemInstance.
     * @param storageItemInstanceKey:
     * 			StorageItemInstance key
     * @return All StorageItemTransaction instances from the given
     * 		StorageItemInstance
     * TODO: Inefficient touching of objects
     */
	public static List<StorageItemTransaction> getStorageItemTransactionsFromStorageItemInstance(
			Key storageItemInstanceKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
        StorageItemInstance storageItemInstance = 
        		pm.getObjectById(StorageItemInstance.class, storageItemInstanceKey);

        List<StorageItemTransaction> types = null;
        try {
        	types = storageItemInstance.getStorageItemTransactions();
            // touch all elements
            for (StorageItemTransaction t : types)
                t.getStorageItemTransactionPerformedBy();
        } 
        finally {
        	pm.close();
        }

        Collections.sort(types);
        return types;
    }
	
	/**
     * Get last StorageItemTransaction instance from the datastore
     * from the given StorageItemInstance.
     * @param storageItemInstanceKey:
     * 			StorageItemInstance key
     * @return The last StorageItemTransaction instance from the given
     * 		StorageItemInstance
     * TODO: Inefficient touching of objects
     */
	public static StorageItemTransaction getLastStorageItemTransactionFromStorageItemInstance(
			Key storageItemInstanceKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	StorageItemInstance storageItemInstance = 
        			pm.getObjectById(StorageItemInstance.class, 
        					storageItemInstanceKey);
        	
        	Key lastStorageItemTransactionKey = 
        			storageItemInstance.getLastStorageItemTransaction();
        	StorageItemTransaction lastStorageItemTransaction = null;
        	if (lastStorageItemTransactionKey != null) {
        		lastStorageItemTransaction = pm.getObjectById(
        				StorageItemTransaction.class, 
        				lastStorageItemTransactionKey);
        	}
        	return lastStorageItemTransaction;
        } 
        catch (JDOObjectNotFoundException jonfe) {
        	return null;
        }
        finally {
        	pm.close();
        }
    }
	
	/**
     * Get StorageItemTransaction instances from the datastore
     * that reference the given StorageDevicePartition and StorageItem.
     * @param storageDevicePartitionKey:
     * 			StorageDevicePartition key
     * @param storageItemKey:
     * 			StorageItem key
     * @return StorageItemTransaction instances referencing the given
     * 		StorageDevicePartition and StorageItem
     */
	public static ArrayList<StorageItemTransaction> 
			getStorageItemTransactionsWithStorageDevicePartitionAndStorageItem(
					Key storageDevicePartitionKey, Key storageItemKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

        ArrayList<StorageItemTransaction> finalResult = new ArrayList<>();
        try {
    		StorageItem storageItem = 
    				pm.getObjectById(StorageItem.class, storageItemKey);
    		List<StorageItemInstance> storageItemInstances = 
    				storageItem.getStorageItemInstances();
    		
        	for (StorageItemInstance storageItemInstance : storageItemInstances) {
        		
        		// Skip if lastStorageItemTransaction is null
        		if (storageItemInstance.getLastStorageItemTransaction() == null) {
        			continue;
        		}
        		
        		try {
	        		StorageItemTransaction lastTransaction = 
	        				pm.getObjectById(
	        						StorageItemTransaction.class, 
	        						storageItemInstance.getLastStorageItemTransaction());
        			
	    			// Add tx only if it is a Store and belongs to this StorageDevicePartition
	    			if (lastTransaction.getStorageItemTransactionAction() == 
	    					StorageItemTransactionAction.STORE &&
	    					lastTransaction.getStorageDevicePartition().equals(
	    							storageDevicePartitionKey)) {
				    			
				    	finalResult.add(lastTransaction);
	    			}
        		}
        		catch(JDOObjectNotFoundException jonfe) {
        			continue;
        		}
        	}
        } 
        finally {
        	pm.close();
        }

        Collections.sort(finalResult);
        return finalResult;
    }
	
	/**
     * Get StorageItemTransaction instances from the datastore
     * that reference the given StorageDevice.
     * @param storageDeviceKey:
     * 			StorageDevice key
     * @return StorageItemTransaction instances referencing the given
     * 		StorageDevice
     */
	@SuppressWarnings("unchecked")
	public static HashMap<Key, HashMap<Key, ArrayList<StorageItemTransaction>>> 
			getStorageItemTransactionsWithStorageDevice(
					Key storageDeviceKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		StorageDevice storageDevice = 
				pm.getObjectById(StorageDevice.class, storageDeviceKey);
		List<StorageDeviceDoor> storageDeviceDoors = storageDevice.getStorageDeviceDoors();
		
        Query query = pm.newQuery(StorageItemTransaction.class);
        query.setFilter("storageDevicePartition == storageDevicePartitionParam");
        query.declareParameters(Key.class.getName() + " storageDevicePartitionParam");
        query.setOrdering("storageItemInstance asc");
        query.setOrdering("storageItemTransactionCreationDate desc");
        query.setRange(0, 1000);

    	// HashMap<StorageItem Key, HashMap of transactions per partition>
    	HashMap<Key, HashMap<Key, ArrayList<StorageItemTransaction>>> finalResult = new HashMap<>();;
        try {

        	for (StorageDeviceDoor storageDeviceDoor : storageDeviceDoors) {
            	List<StorageDevicePartition> storageDevicePartitions = 
            			storageDeviceDoor.getStorageDevicePartitions();
        		
            	for (StorageDevicePartition storageDevicePartition : storageDevicePartitions) {
	        		List<StorageItemTransaction> result = 
	            			(List<StorageItemTransaction>) query.execute(
	            					storageDevicePartition.getKey());
	                
	        		for (StorageItemTransaction storageItemTransaction : result) {
	        			StorageItemInstance storageItemInstance = 
	        					StorageItemInstanceManager.getStorageItemInstance(
	        							storageItemTransaction.getStorageItemInstance());
	        			
	        			// Add tx only if it is the last one for this StorageItemInstance
	        			// and if it is a Store
	        			if (storageItemInstance.getLastStorageItemTransaction() != null &&
	        					storageItemInstance.getLastStorageItemTransaction().equals(
	        					storageItemTransaction.getKey()) && 
	        					storageItemTransaction.getStorageItemTransactionAction() == 
	        							StorageItemTransactionAction.STORE) {
	        				
	        				// HashMap<StorageDevicePartition Key, ArrayList of StorageItemTransactions>
        			    	HashMap<Key, ArrayList<StorageItemTransaction>> transactionsPerPartition;
	        				Key storageItemKey = storageItemInstance.getKey().getParent();
	        				if (finalResult.get(storageItemKey) == null) {
	        			    	transactionsPerPartition = new HashMap<>();
	        				}
	        				else {
	        					transactionsPerPartition = finalResult.get(storageItemKey);
	        				}
	        				
	        				Key storageDevicePartitionKey = storageDevicePartition.getKey();
	        				if (transactionsPerPartition.get(storageDevicePartitionKey) == null) {
	        		        	ArrayList<StorageItemTransaction> transactions = 
	        		        			new ArrayList<>();
	        		        	transactions.add(storageItemTransaction);
	        		        	transactionsPerPartition.put(storageDevicePartitionKey, transactions);
	        				}
	        				else {
	        					transactionsPerPartition.get(storageDevicePartitionKey).add(
	        							storageItemTransaction);
	        				}
        			    			
        			    	finalResult.put(storageItemKey, transactionsPerPartition);
	        			}
	        		}
            	}
        	}
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return finalResult;
    }

	/**
     * Get StorageItemTransaction instances from the datastore
     * that reference the given CustomerUser (performed by).
     * @param customerUserKey:
     * 			CustomerUser key
     * @return All StorageItemTransaction instances referencing the given
     * 		CustomerUser
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<StorageItemTransaction> getStorageItemTransactionsPerformedBy(
			Key customerUserKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(StorageItemTransaction.class);
        query.setFilter("storageItemTransactionPerformedBy == customerUserParam");
        query.declareParameters(Key.class.getName() + " customerUserParam");
        query.setOrdering("storageItemTransactionCreationDate desc");

        List<StorageItemTransaction> types = null;
        try {
        	types = (List<StorageItemTransaction>) query.execute(customerUserKey);
            // touch all elements
            for (StorageItemTransaction t : types)
                t.getStorageItemTransactionPerformedBy();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Put StorageItemTransaction into datastore.
     * Stores the given StorageItemTransaction instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param storageItemInstanceKey
     * 			: the key of the StorageItemInstance where this transaction will be stored
     * @param storageItemTransaction
     * 			: the StorageItemTransaction instance to store
     */
	public static void putStorageItemTransaction(
			Key storageItemInstanceKey,
			StorageItemTransaction storageItemTransaction) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		StorageItemInstance storageItemInstance = 
				pm.getObjectById(StorageItemInstance.class, storageItemInstanceKey);
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			storageItemInstance.addStorageItemTransaction(storageItemTransaction);
			storageItemInstance.setLastStorageItemTransaction(
					storageItemTransaction.getKey());
			tx.commit();
			log.info("StorageItemTransaction \"" + 
					storageItemTransaction.getStorageItemTransactionPerformedBy() + 
					"\" stored successfully in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/**
    * Delete StorageItemTransaction from datastore.
    * Deletes the StorageItemTransaction corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the StorageItemTransaction instance to delete
	 * @throws InexistentObjectException 
    */
	public static void deleteStorageItemTransaction(Key key) 
			throws InexistentObjectException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			StorageItemTransaction storageItemTransaction = 
					pm.getObjectById(StorageItemTransaction.class, key);
			StorageItemInstance storageItemInstance = 
					pm.getObjectById(StorageItemInstance.class, key.getParent());
			Key storageItemTransactionKey = storageItemTransaction.getKey();
			
			tx.begin();
			storageItemInstance.removeStorageItemTransaction(storageItemTransaction);
			tx.commit();
			
			log.info("StorageItemTransaction \"" + storageItemTransactionKey + 
                     "\" deleted successfully from datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
