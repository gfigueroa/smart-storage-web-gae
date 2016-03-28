/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import exceptions.InexistentObjectException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the Country table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class Country {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String countryName;
    
    @Persistent
    private String countryComments;
    
    @Persistent
    private Date countryCreationTime;

	@Persistent
    private Date countryModificationTime;
	
    @Persistent
    @Element(dependent = "true")
    private ArrayList<Region> regions;

    /**
     * Country constructor.
     * @param countryName
     * 			: country name
     * @param countryComments
     * 			: country comments
     * @throws MissingRequiredFieldsException
     */
    public Country(String countryName, 
    		String countryComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (countryName == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (countryName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
        this.countryName = countryName;
        this.countryComments = countryComments;
        
        Date now = new Date();
        this.countryCreationTime = now;
        this.countryModificationTime = now;
        
        this.regions = new ArrayList<>();
    }

    /**
     * Get Country key.
     * @return country key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Get Country name.
     * @return restaurant country name
     */
    public String getCountryName() {
        return countryName;
    }

	/**
     * Get Country comments.
     * @return restaurant country comments
     */
    public String getCountryComments() {
    	return countryComments;
    }
    
    /**
	 * @return the countryCreationTime
	 */
	public Date getCountryCreationTime() {
		return countryCreationTime;
	}

	/**
	 * @return the countryModificationTime
	 */
	public Date getCountryModificationTime() {
		return countryModificationTime;
	}
	
    /**
     * Get Regions
     * @return regions
     */
    public ArrayList<Region> getRegions() {
    	return regions;
    }
    
    /**
     * Set Country name.
     * @param countryName
     * 			: country name
     * @throws MissingRequiredFieldsException
     */
    public void setCountryName(String countryName)
    		throws MissingRequiredFieldsException {
    	if (countryName == null || countryName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Country name is missing.");
    	}
    	this.countryName = countryName;
        this.countryModificationTime = new Date();
    }
    
    /**
     * Set Country comments.
     * @param countryComments
     * 			: country comments
     */
    public void setCountryComments(String countryComments) {
    	this.countryComments = countryComments;
    	this.countryModificationTime = new Date();
    }
    
	/**
	 * Add a region to this DeviceModel
	 * @param region
	 */
	public void addRegion(Region region) {
		regions.add(region);
	}
	
    /**
     * Remove a Region from the DeviceModel.
     * @param region
     * 			: Region to be removed
     * @throws InexistentObjectException
     */
    public void removeRegion(Region region) 
    		throws InexistentObjectException {
    	if (!regions.remove(region)) {
    		throw new InexistentObjectException(
    				Region.class, "Region not found!");
    	}
    }
}