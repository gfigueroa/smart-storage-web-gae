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
 * This class represents the SensorReading table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class SensorReading implements Comparable<SensorReading>, Serializable {
	
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
     * SensorReading constructor.
     * @param parentKey
     * 			: the parent (SensorInstance) key
     * @param sensorReadingValue
     * 			: SensorReading description
     * @param sensorReadingTime
     * 			: SensorReading time
     * @throws MissingRequiredFieldsException
     */
    public SensorReading(
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
	public int compareTo(SensorReading s1) {
		return s1.getSensorReadingTime().compareTo(
				this.getSensorReadingTime());
	}
	
	/**
     * Compare this SensorReading with another SensorReading
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this SensorReading, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof SensorReading ) ) return false;
        SensorReading sr = (SensorReading) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(sr.getKey()));
    }
}