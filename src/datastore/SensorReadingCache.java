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
 * This class represents the SensorReadingCache table.
 * It stores a fixed number of sensorReadings before sending them to the 
 * SensorReading table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class SensorReadingCache implements Comparable<SensorReadingCache>, Serializable {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private Key parentKey;
    
    @Persistent
    private String sensorReadingValue;
    
    @Persistent
    private Date sensorReadingTime;

    /**
     * SensorReadingCache constructor.
     * @param parentKey
     * 			: the parent (SensorInstance) key
     * @param sensorReadingValue
     * 			: SensorReading description
     * @param sensorReadingTime
     * 			: SensorReading time
     * @throws MissingRequiredFieldsException
     */
    public SensorReadingCache(
    		Key parentKey,
    		String sensorReadingValue,
    		Date sensorReadingTime) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (parentKey == null || sensorReadingValue == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (sensorReadingValue.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

    	this.parentKey = parentKey;
        this.sensorReadingValue = sensorReadingValue;
        this.sensorReadingTime = sensorReadingTime;
    }

    /**
     * Get SensorReading key.
     * @return SensorReading key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get parent (SensorInstance) key.
     * @return SensorReading parent key
     */
    public Key getParentKey() {
        return parentKey;
    }

    /**
     * Get SensorReading value.
     * @return SensorReading value
     */
    public String getSensorReadingValue() {
    	return sensorReadingValue;
    }
    
    /**
     * Get SensorReading time.
     * @return SensorReading time
     */
    public Date getSensorReadingTime() {
    	return sensorReadingTime;
    }

	@Override
	public int compareTo(SensorReadingCache s1) {
		return s1.getSensorReadingTime().compareTo(
				this.getSensorReadingTime());
	}
	
	/**
     * Compare this SensorReadingCache with another SensorReadingCache
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this SensorReadingCache, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof SensorReadingCache ) ) return false;
        SensorReadingCache sr = (SensorReadingCache) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(sr.getKey()));
    }
}