/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the SensorType class.
 * 
 */

public class SensorTypeManager {
	
	private static final Logger log = 
        Logger.getLogger(SensorTypeManager.class.getName());
	
	/**
     * Get a SensorType instance from the datastore given the SensorType key.
     * @param key
     * 			: the SensorType's key
     * @return SensorType instance, null if SensorType is not found
     */
	public static SensorType getSensorType(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		SensorType sensorType;
		try  {
			sensorType = pm.getObjectById(SensorType.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return sensorType;
	}
	
	/**
     * Get SensorType instance from the datastore with this name.
     * @return SensorType that has this name
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static SensorType getSensorType(String sensorTypeName) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(SensorType.class);
        query.setFilter("sensorTypeName == sensorTypeNameParam");
        query.declareParameters("String sensorTypeNameParam");

        try {
        	List<SensorType> sensorTypes = (List<SensorType>) query.execute(sensorTypeName);
            // touch all elements
        	if (sensorTypes != null) {
	            for (SensorType t : sensorTypes)
	                t.getSensorTypeName();
	            return sensorTypes.get(0);
        	}
        	else {
        		return null;
        	}
        } 
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get all SensorType instances from the datastore.
     * @return All SensorType instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<SensorType> getAllSensorTypes() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(SensorType.class);

        List<SensorType> types = null;
        try {
        	types = (List<SensorType>) query.execute();
            // touch all elements
            for (SensorType t : types)
                t.getSensorTypeName();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Put SensorType into datastore.
     * Stations the given SensorType instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param sensorType
     * 			: the SensorType instance to store
     */
	public static void putSensorType(SensorType sensorType) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(sensorType);
			tx.commit();
			log.info("SensorType \"" + sensorType.getSensorTypeName() + 
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
    * Delete SensorType from datastore.
    * Deletes the SensorType corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the SensorType instance to delete
    */
	public static void deleteSensorType(Long key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorType sensorType = pm.getObjectById(SensorType.class, key);
			String SensorTypeName = sensorType.getSensorTypeName();
			tx.begin();
			pm.deletePersistent(sensorType);
			tx.commit();
			log.info("SensorType \"" + SensorTypeName + 
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
    * Update SensorType attributes.
    * Update's the given SensorType's attributes in the datastore.
    * @param key
    * 			: the key of the SensorType whose attributes will be updated
    * @param sensorTypeName
    * 			: the new name to give to the SensorType
    * @param sensorTypeDescription
    * 			: the new description to give to the SensorType
    * @param sensorTypeUnit
    * 			: the new unit to give to the SensorType
    * @param sensorTypeComments
    * 			: the new comments to give to the SensorType
	* @throws MissingRequiredFieldsException 
    */
	public static void updateSensorTypeAttributes(Long key,
			String sensorTypeName, String sensorTypeDescription,
			String sensorTypeUnit, String sensorTypeComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			SensorType sensorType = pm.getObjectById(SensorType.class, key);
			tx.begin();
			sensorType.setSensorTypeName(sensorTypeName);
			sensorType.setSensorTypeDescription(sensorTypeDescription);
			sensorType.setSensorTypeUnit(sensorTypeUnit);
			sensorType.setSensorTypeComments(sensorTypeComments);
			tx.commit();
			log.info("SensorType \"" + sensorTypeName + 
                     "\"'s attributes updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
