/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the Region class.
 * 
 */

public class RegionManager {
	
	private static final Logger log = 
        Logger.getLogger(RegionManager.class.getName());
	
	/**
     * Get a Region instance from the datastore given the Region key.
     * @param key
     * 			: the Region's key
     * @return Region instance, null if Region is not found
     */
	public static Region getRegion(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Region region;
		try  {
			region = pm.getObjectById(Region.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return region;
	}
	
	/**
     * Get all Region instances from the datastore.
     * @return All Region instances
     * TODO: Make "touching" of regions more efficient
     */
	@SuppressWarnings("unchecked")
	public static List<Region> getAllRegions() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Region.class);

        List<Region> regions = null;
        try {
        	regions = (List<Region>) query.execute();
            // touch all elements
            for (Region r : regions)
                r.getRegionName();
        } finally {
        	pm.close();
            query.closeAll();
        }

        return regions;
    }
	
	/**
     * Get all Region instances from the datastore that belong to
     * this Country.
     * @param countryKey
     * @return All Region instances that belong to the given Country
     * TODO: Inefficient touching of objects
     */
	public static List<Region> getAllRegionsFromCountry(
			Key countryKey) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        
        Country country = pm.getObjectById(Country.class, countryKey);
         
        List<Region> result = null;
        ArrayList<Region> finalResult = new ArrayList<Region>();
        try {
            result = country.getRegions();
            for (Region region : result) {
            	region.getRegionName();
            	finalResult.add(region);
            }
        }
        finally {
            pm.close();
        }

        return finalResult;
    }
	
    /**
     * Put Region into datastore.
     * Stores the given Region instance in the datastore for this
     * country.
     * @param countryKey
     *          : the key of the Country where the region will be added
     * @param region
     *          : the Region instance to country
     */
    public static void putRegion(Key countryKey, Region region) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Country country = 
                    pm.getObjectById(Country.class, countryKey);
            tx.begin();
            country.addRegion(region);
            tx.commit();
            log.info("Region \"" + region.getRegionName() + 
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
    * Delete Region from datastore.
    * Deletes the Region corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    *           : the key of the Region instance to delete
    */
    public static void deleteRegion(Key key) { 
         
        PersistenceManager pm = PMF.get().getPersistenceManager();
         
        Transaction tx = pm.currentTransaction();
        try {
            Country country = 
            		pm.getObjectById(Country.class, key.getParent());
            Region region = 
            		pm.getObjectById(Region.class, key);
            String regionContent = region.getRegionName();
            tx.begin();
            country.removeRegion(region);
            tx.commit();
            log.info("Region \"" + regionContent + 
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
    * Update Region attributes.
    * Update's the given Region's attributes in the datastore.
    * @param key
    * 			: the key of the Region whose attributes will be updated
    * @param regionName
    * 			: the new name to give to the Region
    * @param regionTimeZone
    * 			: the new timezone to give to the Region
    * @param regionComments
    * 			: the new comments to give to the Region
	* @throws MissingRequiredFieldsException 
    */
	public static void updateRegionAttributes(Key key,
			String regionName,
			String regionTimeZone,
			String regionComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Region region = pm.getObjectById(Region.class, key);
			tx.begin();
			region.setRegionName(regionName);
			region.setRegionTimeZone(regionTimeZone);
			region.setRegionComments(regionComments);
			tx.commit();
			log.info("Region \"" + regionName + 
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
