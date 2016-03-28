/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the Region table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class Region implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String regionName;
    
    @Persistent
    private String regionTimeZone;
    
    @Persistent
    private String regionComments;
    
    @Persistent
    private Date regionCreationTime;

	@Persistent
    private Date regionModificationTime;

    /**
     * Region constructor.
     * @param regionName
     * 			: region name
     * @param regionTimeZone
     * 			: the region timezone
     * @param regionComments
     * 			: region comments
     * @throws MissingRequiredFieldsException
     */
    public Region(String regionName, 
    		String regionTimeZone,
    		String regionComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (regionName == null || regionTimeZone == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (regionName.trim().isEmpty() || regionTimeZone.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
        this.regionName = regionName;
        this.regionTimeZone = regionTimeZone;
        this.regionComments = regionComments;
        
        Date now = new Date();
        this.regionCreationTime = now;
        this.regionModificationTime = now;
    }

    /**
     * Get Region key.
     * @return region key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Get Region name.
     * @return restaurant region name
     */
    public String getRegionName() {
        return regionName;
    }

    /**
	 * @return the regionTimeZone
	 */
	public String getRegionTimeZone() {
		return regionTimeZone;
	}

	/**
     * Get Region comments.
     * @return restaurant region comments
     */
    public String getRegionComments() {
    	return regionComments;
    }
    
    /**
	 * @return the regionCreationTime
	 */
	public Date getRegionCreationTime() {
		return regionCreationTime;
	}

	/**
	 * @return the regionModificationTime
	 */
	public Date getRegionModificationTime() {
		return regionModificationTime;
	}
	
	/**
     * Compare this Region with another DRegion
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this Region, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof Region ) ) return false;
        Region r = (Region) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(r.getKey()));
    }
    
    /**
     * Set Region name.
     * @param regionName
     * 			: region name
     * @throws MissingRequiredFieldsException
     */
    public void setRegionName(String regionName)
    		throws MissingRequiredFieldsException {
    	if (regionName == null || regionName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Region name is missing.");
    	}
    	this.regionName = regionName;
        this.regionModificationTime = new Date();
    }
    
	/**
	 * @param regionTimeZone the regionTimeZone to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setRegionTimeZone(String regionTimeZone) 
			throws MissingRequiredFieldsException {
		if (regionTimeZone == null || regionTimeZone.isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Region time zone is missing.");
    	}
		this.regionTimeZone = regionTimeZone;
	}
    
    /**
     * Set Region comments.
     * @param regionComments
     * 			: region comments
     */
    public void setRegionComments(String regionComments) {
    	this.regionComments = regionComments;
    	this.regionModificationTime = new Date();
    }
}