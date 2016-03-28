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

import com.google.appengine.api.datastore.Key;

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the Country class.
 * 
 */

public class CountryManager {
	
	private static final Logger log = Logger.getLogger(CountryManager.class.getName());
	
	/**
     * Get a Country instance from the datastore given the Country key.
     * @param key
     * 			: the country's key
     * @return country instance, null if country is not found
     */
	public static Country getCountry(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Country country;
		try  {
			country = pm.getObjectById(Country.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return country;
	}
	
	/**
     * Get all Country instances from the datastore.
     * @return All Country instances
     * TODO: Make "touching" of countrys more efficient
     */
	@SuppressWarnings("unchecked")
	public static List<Country> getAllCountries() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Country.class);

        List<Country> countries = null;
        try {
        	countries = (List<Country>) query.execute();
            // touch all elements
            for (Country r : countries)
                r.getCountryName();
        } finally {
        	pm.close();
            query.closeAll();
        }

        return countries;
    }
	
	/**
     * Put Country into datastore.
     * Stores the given country instance in the datastore calling the PersistenceManager's
     * makePersistent() method.
     * @param country
     * 			: the country instance to store
     */
	public static void putCountry(Country country) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(country);
			tx.commit();
			log.info("Country \"" + country.getCountryName() + 
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
    * Delete Country from datastore.
    * Deletes the country corresponding to the given key
    * from the datastore calling the PersistenceManager's deletePersistent() method.
    * @param key
    * 			: the key of the country instance to delete
    */
	public static void deleteCountry(Key key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Country country = pm.getObjectById(Country.class, key);
			String countryName = country.getCountryName();
			tx.begin();
			pm.deletePersistent(country);
			tx.commit();
			log.info("Country \"" + countryName + "\" deleted successfully from datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
    * Update Country attributes.
    * Update's the given country's attributes in the datastore.
    * @param key
    * 			: the key of the country whose attributes will be updated
    * @param countryName
    * 			: the new name to give to the country
    * @param countryComments
    * 			: the new comments to give to the country
	* @throws MissingRequiredFieldsException 
    */
	public static void updateCountryAttributes(
			Key key, 
			String countryName,
			String countryComments) 
					throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Country country = pm.getObjectById(Country.class, key);
			tx.begin();
			country.setCountryName(countryName);
			country.setCountryComments(countryComments);
			tx.commit();
			log.info("Country \"" + countryName + "\"'s attributes updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
