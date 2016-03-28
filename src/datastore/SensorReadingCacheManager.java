/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the SensorReadingCache class.
 * 
 */

public class SensorReadingCacheManager {
	
	public static enum Period {
		DAY, WEEK, MONTH
	}
	
	/**
	 * Get a Period from a string representation
	 * @param periodString: the Period as a String
	 * @return a Period
	 */
	public static Period getPeriodFromString(String periodString) {
		if (periodString == null) {
			return null;
		}
		else if (periodString.equalsIgnoreCase("day")) {
			return Period.DAY;
		}
		else if (periodString.equalsIgnoreCase("week")) {
			return Period.WEEK;
		}
		else if (periodString.equalsIgnoreCase("month")) {
			return Period.MONTH;
		}
		else {
			return null;
		}
	}
	
	private static final Logger log = 
        Logger.getLogger(SensorReadingCacheManager.class.getName());
	
	/**
     * Get a SensorReadingCache instance from the datastore given the SensorReadingCache key.
     * @param key
     * 			: the SensorReadingCache's key
     * @return SensorReadingCache instance, null if SensorReadingCache is not found
     */
	public static SensorReadingCache getSensorReadingCache(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		SensorReadingCache sensorReadingCache;
		try  {
			sensorReadingCache = pm.getObjectById(SensorReadingCache.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return sensorReadingCache;
	}
	
	/**
     * Get the last SensorReadingCache instance from the datastore that
     * belongs to this SensorInstance.
     * @param sensorInstanceKey:
     * 			The key of the SensorInstance
     * @return The last SensorReadingCache that belongs to the given 
     * 			SensorInstance
     */
	public static SensorReadingCache getLastSensorReadingCacheFromSensorInstance(
			Key sensorInstanceKey) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	SensorInstance sensorInstance = 
        			pm.getObjectById(SensorInstance.class, sensorInstanceKey);
        	Key lastSensorReadingCacheKey = sensorInstance.getLastSensorReadingCache();
        	SensorReadingCache lastSensorReadingCache = null;
        	if (lastSensorReadingCacheKey != null) {
        		lastSensorReadingCache = 
        				pm.getObjectById(SensorReadingCache.class, lastSensorReadingCacheKey);
        	}
        	
        	return lastSensorReadingCache;
        }
        catch (JDOObjectNotFoundException jonfe) {
        	return null;
        }
        finally {
        	pm.close();
        }
    }
	
	/**
     * Get all SensorReadingCache instances from the datastore that belong to
     * this SensorInstance.
     * @param sensorInstanceKey
     * @param oldestToNewest
     * @return All SensorReadingCache instances that belong to the given SensorInstance
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReadingCache> getAllSensorReadingCachesFromSensorInstance(
			Key sensorInstanceKey, boolean oldestToNewest) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		
    	Query query = pm.newQuery(SensorReadingCache.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");
        query.setOrdering("sensorReadingTime " + (oldestToNewest ? "asc" : "desc"));

        try {
        	List<SensorReadingCache> sensorReadingCaches = null;
            sensorReadingCaches = (List<SensorReadingCache>) query.execute(sensorInstanceKey);
            // Touch
            for (SensorReadingCache sensorReadingCache : sensorReadingCaches) {
            	sensorReadingCache.getKey();
            }

            return sensorReadingCaches;
        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get all SensorReadingCache instances from the datastore that
     * belong to this StorageDevice.
     * @param storageDeviceKey:
     * 			The key of the StorageDevice
     * @param @param oldestToNewest
     * @return All SensorReadingCaches that belong to the given 
     * 			StorageDevice
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReadingCache> getAllSensorReadingCachesFromStorageDevice(
			Key storageDeviceKey, boolean oldestToNewest) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		StorageDevice storageDevice = 
				pm.getObjectById(StorageDevice.class, storageDeviceKey);
		List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();
		
    	Query query = pm.newQuery(SensorReadingCache.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");
        query.setOrdering("sensorReadingTime " + (oldestToNewest ? "asc" : "desc"));
        
        ArrayList<SensorReadingCache> finalSensorReadingCaches = new ArrayList<>();
        try {
            for (SensorInstance s : sensorInstances) {
                List<SensorReadingCache> sensorReadingCaches = 
                		(List<SensorReadingCache>) query.execute(s.getKey());
                finalSensorReadingCaches.addAll(sensorReadingCaches);
            }
            return finalSensorReadingCaches;

        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
	 * Return whether the SensorReadingCache is full for a specific SensorInstance
	 * @param sensorInstanceKey
	 * @return true if cache is full, false otherwise
	 */
	public static boolean sensorReadingCacheIsFull(Key sensorInstanceKey) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        try {
            SensorInstance sensorInstance = 
            		pm.getObjectById(SensorInstance.class, 
            				sensorInstanceKey);
            StorageDevice storageDevice =
            		pm.getObjectById(StorageDevice.class, 
            				sensorInstanceKey.getParent());
            if (sensorInstance.getSensorReadingCacheCount() < 
            		storageDevice.getSensorReadingCacheSize()) {
            	return false;
            }
            else {
            	return true;
            }
        }
        finally {
        	pm.close();
        }
	}
	
	/**
	 * Dump all SensorReadingCache instances that belong to the given
	 * StorageDevice by first creating them as
	 * SensorReadings and then deleting them from the datastore. The SensorReadingCache
	 * count is also reset to 0 in each SensorInstance.
	 * @param sensorReadingInstance
	 */
	public static void dumpSensorReadingCacheFromStorageDevice(Key storageDeviceKey) {
		
		List<SensorReadingCache> sensorReadingCaches = 
				getAllSensorReadingCachesFromStorageDevice(storageDeviceKey, true);
		ArrayList<SensorReading> sensorReadings = new ArrayList<>();
		
		try {
			// Create new SensorReading list
			for (SensorReadingCache src : sensorReadingCaches) {
				SensorReading sensorReading = new SensorReading(
							src.getParentKey(),
							src.getSensorReadingValue(),
							src.getSensorReadingTime());
				sensorReadings.add(sensorReading);
			}
		} 
		catch (MissingRequiredFieldsException e) {
			e.printStackTrace();
		}
		
		// Delete SensorReadingCache list
		deleteAllSensorReadingCaches(storageDeviceKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	
        	// Make SensorReading list persistent
        	for (SensorReading sensorReading : sensorReadings) {
            	SensorInstance sensorInstance = 
            			pm.getObjectById(SensorInstance.class, 
            					sensorReading.getParentKey());
                sensorInstance.addSensorReading(sensorReading);
                sensorInstance.setLastSensorReading(sensorReading.getKey()); // Update last SensorReading
                
            	// Reset SensorReadingCache counter
            	sensorInstance.resetSensorReadingCacheCount();
        	}

            log.info("SensorReadingCaches dumped successfully in datastore.");
        }
        finally {
        	pm.close();
        }
	}
	
	/**
	 * Dump all SensorReadingCache instances that belong to the given
	 * SensorInstance by first creating them as
	 * SensorReadings and then deleting them from the datastore. The SensorReadingCache
	 * count is also reset to 0 in the SensorInstance.
	 * @param sensorReadingInstance
	 */
	public static void dumpSensorReadingCache(Key sensorInstanceKey) {
		
		List<SensorReadingCache> sensorReadingCaches = 
				getAllSensorReadingCachesFromSensorInstance(sensorInstanceKey, true);
		ArrayList<SensorReading> sensorReadings = new ArrayList<>();
		
		try {
			// Create new SensorReading list
			for (SensorReadingCache src : sensorReadingCaches) {
				SensorReading sensorReading = new SensorReading(
							sensorInstanceKey ,
							src.getSensorReadingValue(),
							src.getSensorReadingTime());
				sensorReadings.add(sensorReading);
			}
		} 
		catch (MissingRequiredFieldsException e) {
			e.printStackTrace();
		}
		
		// Delete SensorReadingCache list
		deleteAllSensorReadingCachesFromSensorInstance(sensorInstanceKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	SensorInstance sensorInstance = 
        			pm.getObjectById(SensorInstance.class, sensorInstanceKey);
        	
        	// Make SensorReading list persistent
        	for (SensorReading sensorReading : sensorReadings) {
                sensorInstance.addSensorReading(sensorReading);
                sensorInstance.setLastSensorReading(sensorReading.getKey()); // Update last SensorReading
        	}
        	
        	// Reset SensorReadingCache counter
        	sensorInstance.resetSensorReadingCacheCount();
        	
            log.info("SensorReadingCaches dumped successfully in datastore.");
        }
        finally {
        	pm.close();
        }
	}
	
	/**
     * Put SensorReadingCache into datastore.
     * Stores the given SensorReadingCache instance in the datastore.
     * @param sensorReadingCache
     *          : the SensorReadingCache instance to store
	 * @throws MissingRequiredFieldsException 
     */
    public static void putSensorReadingCache(SensorReadingCache sensorReadingCache) 
    		throws MissingRequiredFieldsException {
        
        // Check if the cache for this SensorInstance is full
    	boolean full = sensorReadingCacheIsFull(sensorReadingCache.getParentKey());
    	// If the cache is full, dump the data into SensorReading table
    	if (full) {
    		dumpSensorReadingCache(sensorReadingCache.getParentKey());
    	}

    	PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
        	 // Increase cache count for this SensorInstance
        	SensorInstance sensorInstance = 
            		pm.getObjectById(SensorInstance.class, 
            				sensorReadingCache.getParentKey());
        	sensorInstance.incrementSensorReadingCacheCount();
        	
            pm.makePersistent(sensorReadingCache);
            
        	// Finally, update last SensorReadingCache
        	sensorInstance.setLastSensorReadingCache(sensorReadingCache.getKey()); 
        	
            log.info("SensorReadingCache \"" + sensorReadingCache.getKey() + 
                "\" stored successfully in datastore.");
        }
        finally {
            pm.close();
        }
    }
     
    /**
    * Delete SensorReadingCache from datastore.
    * Deletes the SensorReadingCache corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the SensorReadingCache instance to delete
    */
    public static void deleteSensorReadingCache(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            SensorReadingCache sensorReadingCache = pm.getObjectById(SensorReadingCache.class, key);
            String sensorReadingCacheContent = KeyFactory.keyToString(sensorReadingCache.getKey());
            tx.begin();
            pm.deletePersistent(sensorReadingCache);
            tx.commit();
            log.info("SensorReadingCache \"" + sensorReadingCacheContent + 
                     "\" deleted successfully from datastore.");
        }
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
    
    /**
     * Delete all SensorReadingCaches from the datastore.
     * WARNING: This will delete all existing SensorReadingCaches from
     * all StorageDevices in the datastore.
     */
	public static void deleteAllSensorReadingCaches() {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(SensorReadingCache.class);

        try {
        	query.deletePersistentAll();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Deletes all sensor data from this Storage Device
     * 
     * @param storageDeviceKey: the storageDevice key
     */
	@SuppressWarnings("unchecked")
	public static void deleteAllSensorReadingCaches(Key storageDeviceKey) {
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();

    	StorageDevice storageDevice = 
    			pm.getObjectById(StorageDevice.class, storageDeviceKey);
    	
        Query query = pm.newQuery(SensorReadingCache.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");
        query.setRange(0, 1000);
        query.setOrdering("sensorReadingTime desc");

        Transaction tx = pm.currentTransaction();
        List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();
        try {
        	tx.begin();
        	for (SensorInstance sensorInstance : sensorInstances) {
        		List<SensorReadingCache> result = 
        				(List<SensorReadingCache>) query.execute(sensorInstance.getKey());
        		for (SensorReadingCache sensorReadingCache : result) {
        			pm.deletePersistent(sensorReadingCache);
        		}
        	}
        	tx.commit();
        }
        finally {
        	if (tx.isActive()) {
                tx.rollback();
            }
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Deletes all sensor data from this SensorInstance
     * 
     * @param sensorInstanceKey: the sensorInstance key
     */
	public static void deleteAllSensorReadingCachesFromSensorInstance(
			Key sensorInstanceKey) {
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
        Query query = pm.newQuery(SensorReadingCache.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");

        try {
        	query.deletePersistentAll(sensorInstanceKey);
        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
}
