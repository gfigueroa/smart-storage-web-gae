/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import util.DateManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.InexistentObjectException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the SensorReading class.
 * 
 */

public class SensorReadingManager {
	
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
        Logger.getLogger(SensorReadingManager.class.getName());
	
	/**
     * Get a SensorReading instance from the datastore given the SensorReading key.
     * @param key
     * 			: the SensorReading's key
     * @return SensorReading instance, null if SensorReading is not found
     */
	public static SensorReading getSensorReading(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		SensorReading sensorReading;
		try  {
			sensorReading = pm.getObjectById(SensorReading.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return sensorReading;
	}
	
	/**
     * Get all SensorReading instances from the datastore that belong to
     * this SensorInstance.
     * @param sensorInstanceKey
     * @param limit: the max number of readings to return
     * @return All SensorReading instances that belong to the given SensorInstance
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReading> getAllSensorReadingsFromSensorInstance(
			Key sensorInstanceKey, Long limit) {

		// Dump the cache first
		SensorReadingCacheManager.dumpSensorReadingCache(sensorInstanceKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// 1000 is the max limit
		if (limit == null) {
			limit = (long) 1000;
		}
		else if (limit > 1000 || limit < 0) {
			limit = (long) 1000;
		}
		
    	Query query = pm.newQuery(SensorReading.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");
        query.setOrdering("sensorReadingTime desc");
        query.setRange(0, limit);

        try {
        	List<SensorReading> sensorReadings = null;
            sensorReadings = (List<SensorReading>) query.execute(sensorInstanceKey);
            // Touch
            for (SensorReading sensorReading : sensorReadings) {
            	sensorReading.getKey();
            }
            
//            Collections.sort(sensorReadings);
            return sensorReadings;
        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get all SensorReading instances from the datastore that
     * belong to this SensorInstance with the given time period
     * (Day, Week or Month).
     * @param sensorInstanceKey:
     * 			The key of the SensorInstance
     * @param period:
     * 			The period starting from the current time (DAY, WEEK or MONTH)
     * @return SensorReadings that belong to the given 
     * 			SensorInstance and period
     * @TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReading> getSensorReadingsFromSensorInstance(
			Key sensorInstanceKey, Period period) {
		
		// Dump the cache first
		SensorReadingCacheManager.dumpSensorReadingCache(sensorInstanceKey);
		
		Date now = new Date();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// 1000 is the max limit
		Long limit = (long) 1000;
		
		// dateTo and dateFrom, and minutePeriod
		Date dateTo = new Date(); // Assign current date
		Date dateFrom = null;
		switch (period) {
			case DAY:
				dateFrom = DateManager.subtractDaysFromDate(now, 1);
				break;
			case WEEK:
				dateFrom = DateManager.subtractDaysFromDate(now, 7);
				break;
			case MONTH:
				dateFrom = DateManager.subtractDaysFromDate(now, 31);
				break;
			default:
				assert(false);
		}
		
    	Query query = pm.newQuery(SensorReading.class);
    	if (dateFrom != null) {
	        query.setFilter("parentKey == sensorInstanceKeyParam " +
	        		"&& sensorReadingTime >= dateFromParam " +
	        		"&& sensorReadingTime <= dateToParam");
    	}
    	else {
    		query.setFilter("parentKey == sensorInstanceKeyParam " +
	        		"&& sensorReadingTime <= dateToParam");
    	}
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam, " +
        		"java.util.Date dateFromParam, java.util.Date dateToParam");
        query.setOrdering("sensorReadingTime desc");
        query.setRange(0, limit);
        
        try {
            List<SensorReading> sensorReadings = 
            		(List<SensorReading>) query.execute(
            				sensorInstanceKey, dateFrom, dateTo);
            
            // Touch objects
            for (SensorReading sensorReading : sensorReadings) {
            	sensorReading.getKey();
            }
            
            return sensorReadings;
        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get exactly 144 SensorReading instances from the datastore that
     * are contained in the given list of SensorReadings, with the given time period
     * (Day, Week or Month).
     * @param sensorReadings:
     * 			The list of SensorReadings from which to extract a less
     * 			granular list
     * @param period:
     * 			The period starting from the current time (DAY, WEEK or MONTH)
     * @return SensorReadings that belong to the given 
     * 			SensorReading list and period
     */
	public static List<SensorReading> getFixedSensorReadings(
			List<SensorReading> sensorReadings, Period period) {
		
		Date now = new Date();
		
		int periodMinutes = 0;
		switch (period) {
			case DAY:
				periodMinutes = 10; // = (1 * 24 * 60) / 144
				break;
			case WEEK:
				periodMinutes = 70; // = (7 * 24 * 60) / 144
				break;
			case MONTH:
				periodMinutes = 300; // = (30 * 24 * 60) / 144
				break;
			default:
				assert(false);
		}
		
		// Create fixed reading list
        HashMap<Date, SensorReading> sensorReadingMap = 
        		new HashMap<>(144); // 144 items
        ArrayList<Date> fixedDateList = new ArrayList<Date>();
        for (int i = 144; i >= 1; i--) {
        	Date dateToStore = DateManager.subtractSecondsFromDate(now, periodMinutes * 60 * i);
        	sensorReadingMap.put(dateToStore, null);
        	fixedDateList.add(dateToStore);
        }
        
        ArrayList<SensorReading> finalSensorReadings = new ArrayList<>(144);
        try {
            // Process SensorReadings
            for (SensorReading sensorReading : sensorReadings) {
            	Date sensorReadingTime = sensorReading.getSensorReadingTime();
            	Date keyDate = 
            			DateManager.getClosestDateFromList(
            					fixedDateList,
            					sensorReadingTime,
            					periodMinutes);
            	if (keyDate != null) {
            		sensorReadingMap.put(keyDate, sensorReading);
            	}
            }
            
            // Map the sensorReadingMap into the final list
            
            for (Date date : fixedDateList) {
            	SensorReading sensorReading = sensorReadingMap.get(date);
            	finalSensorReadings.add(sensorReading);
            }
            
            return finalSensorReadings;
        }
        catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
	
	/**
     * Get exactly 144 SensorReading instances from the datastore that
     * belong to this SensorInstance with the given time period
     * (Day, Week or Month).
     * @param sensorInstanceKey:
     * 			The key of the SensorInstance
     * @param period:
     * 			The period starting from the current time (DAY, WEEK or MONTH)
     * @return SensorReadings that belong to the given 
     * 			SensorInstance and period
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReading> getFixedSensorReadingsFromSensorInstance(
			Key sensorInstanceKey, Period period) {
		
		// Dump the cache first
		SensorReadingCacheManager.dumpSensorReadingCache(sensorInstanceKey);
		
		Date now = new Date();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// 1000 is the max limit
		Long limit = (long) 1000;
		
		// dateTo and dateFrom, and minutePeriod
		Date dateTo = new Date(); // Assign current date
		Date dateFrom = null;
		int periodMinutes = 0;
		switch (period) {
			case DAY:
				dateFrom = DateManager.subtractDaysFromDate(now, 1);
				periodMinutes = 10; // = (1 * 24 * 60) / 144
				break;
			case WEEK:
				dateFrom = DateManager.subtractDaysFromDate(now, 7);
				periodMinutes = 70; // = (7 * 24 * 60) / 144
				break;
			case MONTH:
				dateFrom = DateManager.subtractDaysFromDate(now, 31);
				periodMinutes = 300; // = (30 * 24 * 60) / 144
				break;
			default:
				assert(false);
		}
		
		// Create fixed reading list
        HashMap<Date, SensorReading> sensorReadingMap = 
        		new HashMap<>(144); // 144 items
        ArrayList<Date> fixedDateList = new ArrayList<Date>();
        for (int i = 144; i >= 1; i--) {
        	Date dateToStore = DateManager.subtractSecondsFromDate(now, periodMinutes * 60 * i);
        	sensorReadingMap.put(dateToStore, null);
        	fixedDateList.add(dateToStore);
        }
		
    	Query query = pm.newQuery(SensorReading.class);
    	if (dateFrom != null) {
	        query.setFilter("parentKey == sensorInstanceKeyParam " +
	        		"&& sensorReadingTime >= dateFromParam " +
	        		"&& sensorReadingTime <= dateToParam");
    	}
    	else {
    		query.setFilter("parentKey == sensorInstanceKeyParam " +
	        		"&& sensorReadingTime <= dateToParam");
    	}
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam, " +
        		"java.util.Date dateFromParam, java.util.Date dateToParam");
        query.setOrdering("sensorReadingTime desc");
        query.setRange(0, limit);
        
        try {
            List<SensorReading> sensorReadings = 
            		(List<SensorReading>) query.execute(
            				sensorInstanceKey, dateFrom, dateTo);
            
            // Process SensorReadings
            for (SensorReading sensorReading : sensorReadings) {
            	Date sensorReadingTime = sensorReading.getSensorReadingTime();
            	Date keyDate = 
            			DateManager.getClosestDateFromList(
            					fixedDateList,
            					sensorReadingTime,
            					periodMinutes);
            	if (keyDate != null) {
            		sensorReadingMap.put(keyDate, sensorReading);
            	}
            }
            
            // Map the sensorReadingMap into the final list
            ArrayList<SensorReading> finalSensorReadings = new ArrayList<>(144);
            for (Date date : fixedDateList) {
            	SensorReading sensorReading = sensorReadingMap.get(date);
            	finalSensorReadings.add(sensorReading);
            }
            
            return finalSensorReadings;
        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get the last SensorReading instance from the datastore that
     * belongs to this SensorInstance.
     * @param sensorInstanceKey:
     * 			The key of the SensorInstance
     * @param dumpSensorReadingCache:
     * 			Whether to dump the SensorReadingCaches for this
     * 			SensorInstance before reading or not.
     * @return The last SensorReadings that belong to the given 
     * 			SensorInstance
     */
	public static SensorReading getLastSensorReadingFromSensorInstance(
			Key sensorInstanceKey, boolean dumpSensorReadingCache) {
		
		// Dump the cache first
		if (dumpSensorReadingCache) {
			SensorReadingCacheManager.dumpSensorReadingCache(sensorInstanceKey);
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
        	SensorInstance sensorInstance = 
        			pm.getObjectById(SensorInstance.class, sensorInstanceKey);
        	Key lastSensorReadingKey = sensorInstance.getLastSensorReading();
        	SensorReading lastSensorReading = null;
        	if (lastSensorReadingKey != null) {
        		lastSensorReading = 
        				pm.getObjectById(SensorReading.class, lastSensorReadingKey);
        	}
        	
        	return lastSensorReading;
        }
        catch (JDOObjectNotFoundException jonfe) {
        	return null;
        }
        finally {
        	pm.close();
        }
    }
	
	/**
     * Get all SensorReading instances from the datastore that
     * belong to this StorageDevice.
     * @param storageDeviceKey:
     * 			The key of the StorageDevice
     * @param limit: the max number of readings to return
     * @return All SensorReadings that belong to the given 
     * 			StorageDevice
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReading> getAllSensorReadingsFromStorageDevice(
			Key storageDeviceKey, Long limit) {
		
		// Dump the cache first
		SensorReadingCacheManager.dumpSensorReadingCacheFromStorageDevice(
				storageDeviceKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		StorageDevice storageDevice = 
				pm.getObjectById(StorageDevice.class, storageDeviceKey);
		List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();

		// 1000 is the max limit
		if (limit == null) {
			limit = (long) 1000;
		}
		else if (limit > 1000 || limit < 0) {
			limit = (long) 1000;
		}
		
    	Query query = pm.newQuery(SensorReading.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");
        query.setOrdering("sensorReadingTime desc");
        query.setRange(0, limit);
        
        ArrayList<SensorReading> finalSensorReadings = new ArrayList<>();
        try {
            for (SensorInstance s : sensorInstances) {
                List<SensorReading> sensorReadings = 
                		(List<SensorReading>) query.execute(s.getKey());
                finalSensorReadings.addAll(sensorReadings);
            }
            return finalSensorReadings;

        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get the last SensorReading instances from the datastore that
     * belong to this StorageDevice.
     * @param storageDeviceKey:
     * 			The key of the StorageDevice
     * @return The last SensorReadings that belong to the given 
     * 			StorageDevice
     */
	public static List<SensorReading> getLastSensorReadingsFromStorageDevice(
			Key storageDeviceKey) {
		
		// Dump the cache first
		SensorReadingCacheManager.dumpSensorReadingCacheFromStorageDevice(
				storageDeviceKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		StorageDevice storageDevice = 
				pm.getObjectById(StorageDevice.class, storageDeviceKey);
		List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();
        
        ArrayList<SensorReading> finalSensorReadings = new ArrayList<>();
        try {
            for (SensorInstance s : sensorInstances) {
            	SensorReading sensorReading = null;
            	if (s.getLastSensorReading() != null) {
            		try {
                		sensorReading = pm.getObjectById(SensorReading.class, 
            					s.getLastSensorReading());
            		}
                    catch (JDOObjectNotFoundException jonfe) {
                    	;
                    }
            	}
            	if (sensorReading != null) {
            		finalSensorReadings.add(sensorReading);
            	}
            }
            return finalSensorReadings;

        }
        finally {
        	pm.close();
        }
    }
	
	/**
     * Get all SensorReading instances from the datastore that
     * belong to this StorageDevice with the given time period.
     * @param storageDeviceKey:
     * 			The key of the StorageDevice
     * @param limit: 
     * 			The max number of readings to return
     * @param dateFrom:
     * 			The starting date and time of the readings
     * @param dateTo:
     * 			The ending date and time of the readings
     * @return All SensorReadings that belong to the given 
     * 			StorageDevice
     */
	@SuppressWarnings("unchecked")
	public static List<SensorReading> getSensorReadingsFromStorageDevice(
			Key storageDeviceKey, Long limit, Date dateFrom, Date dateTo) {
		
		// Dump the cache first
		SensorReadingCacheManager.dumpSensorReadingCacheFromStorageDevice(
				storageDeviceKey);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		StorageDevice storageDevice = 
				pm.getObjectById(StorageDevice.class, storageDeviceKey);
		List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();

		// 1000 is the max limit
		if (limit == null) {
			limit = (long) 1000;
		}
		else if (limit > 1000 || limit < 0) {
			limit = (long) 1000;
		}
		
		// Check dateTo parameter
		if (dateTo == null) {
			dateTo = new Date(); // Assign current date
		}
		
    	Query query = pm.newQuery(SensorReading.class);
    	if (dateFrom != null) {
	        query.setFilter("parentKey == sensorInstanceKeyParam " +
	        		"&& sensorReadingTime >= dateFromParam " +
	        		"&& sensorReadingTime <= dateToParam");
    	}
    	else {
    		query.setFilter("parentKey == sensorInstanceKeyParam " +
	        		"&& sensorReadingTime <= dateToParam");
    	}
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam, " +
        		"java.util.Date dateFromParam, java.util.Date dateToParam");
        query.setOrdering("sensorReadingTime desc");
        query.setRange(0, limit);
        
        ArrayList<SensorReading> finalSensorReadings = new ArrayList<>();
        try {
            for (SensorInstance s : sensorInstances) {
                List<SensorReading> sensorReadings = 
                		(List<SensorReading>) query.execute(
                				s.getKey(), dateFrom, dateTo);
                finalSensorReadings.addAll(sensorReadings);
            }
            return finalSensorReadings;

        }
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Put SensorReading into datastore.
     * Stores the given SensorReading instance in the datastore for this
     * sensorInstance.
     * When the SensorReading is stored, the last SensorReading key is also
     * updated in the SensorInstance for quick retrieval.
     * @param sensorInstanceKey
     *          : the key of the SensorInstance where the sensorReading will be added
     * @param sensorReading
     *          : the SensorReading instance to store in the sensorInstance
     */
    public static void putSensorReading(Key sensorInstanceKey, 
    		SensorReading sensorReading) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Transaction tx = pm.currentTransaction();
        try {
            SensorInstance sensorInstance = 
                    pm.getObjectById(SensorInstance.class, sensorInstanceKey);
            tx.begin();
            sensorInstance.addSensorReading(sensorReading);
            sensorInstance.setLastSensorReading(sensorReading.getKey()); // Update last SensorReading
            tx.commit();
            log.info("SensorReading \"" + sensorReading.getKey() + 
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
    * Delete SensorReading from datastore.
    * Deletes the SensorReading corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the SensorReading instance to delete
    */
    public static void deleteSensorReading(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            SensorInstance sensorInstance = pm.getObjectById(SensorInstance.class, key.getParent());
            SensorReading sensorReading = pm.getObjectById(SensorReading.class, key);
            String sensorReadingContent = KeyFactory.keyToString(sensorReading.getKey());
            tx.begin();
            sensorInstance.removeSensorReading(sensorReading);
            tx.commit();
            log.info("SensorReading \"" + sensorReadingContent + 
                     "\" deleted successfully from datastore.");
        }
        catch (InexistentObjectException e) {
            e.printStackTrace();
        }
        finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }
    }
    
    /**
     * Delete all SensorReadings from the datastore.
     * WARNING: This will delete all existing SensorReadings from
     * all StorageDevices in the datastore.
     */
	public static void deleteAllSensorReadings() {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(SensorReading.class);

        try {
        	query.deletePersistentAll();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }
    }
    
    /**
     * Deletes all sensor data from this Storage Device that have been expired
     * (older than today minus sensorDataEffectivePeriod)
     * @param storageDeviceKey: the storageDevice key
     */
	public static void deleteAllExpiredSensorReadings(Key storageDeviceKey) {
    	
    	Date now = new Date();
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();

    	StorageDevice storageDevice = 
    			pm.getObjectById(StorageDevice.class, storageDeviceKey);
    	
    	int sensorDataEffectivePeriod = storageDevice.getSensorDataEffectivePeriod();
    	Date sensorDataEffectiveDate = 
    			DateManager.subtractDaysFromDate(now, sensorDataEffectivePeriod);
    	
        Query query = pm.newQuery(SensorReading.class);
        query.setFilter("sensorReadingTime < effectiveDateParam && " +
        		"parentKey == sensorInstanceKeyParam");
        query.declareParameters("java.util.Date effectiveDateParam, " +
        		Key.class.getName() + " sensorInstanceKeyParam");

        List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();
        try {
        	for (SensorInstance sensorInstance : sensorInstances) {
        		query.deletePersistentAll(sensorDataEffectiveDate, 
        				sensorInstance.getKey());
        	}
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
	public static void deleteAllSensorReadings(Key storageDeviceKey) {
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();

    	StorageDevice storageDevice = 
    			pm.getObjectById(StorageDevice.class, storageDeviceKey);
    	
        Query query = pm.newQuery(SensorReading.class);
        query.setFilter("parentKey == sensorInstanceKeyParam");
        query.declareParameters(Key.class.getName() + " sensorInstanceKeyParam");
        query.setRange(0, 1000);
        query.setOrdering("sensorReadingTime desc");

        Transaction tx = pm.currentTransaction();
        List<SensorInstance> sensorInstances = storageDevice.getSensorInstances();
        try {
        	tx.begin();
        	for (SensorInstance sensorInstance : sensorInstances) {
        		List<SensorReading> result = 
        				(List<SensorReading>) query.execute(sensorInstance.getKey());
        		for (SensorReading sensorReading : result) {
        			sensorInstance.removeSensorReading(sensorReading);
        		}
        	}
        	tx.commit();
        }
        catch (InexistentObjectException e) {
        	e.printStackTrace();
        }
        finally {
        	if (tx.isActive()) {
                tx.rollback();
            }
        	pm.close();
            query.closeAll();
        }
    }
	
}
