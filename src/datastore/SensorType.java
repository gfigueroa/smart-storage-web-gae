/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the SensorType table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class SensorType {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long key;

    @Persistent
    private String sensorTypeName;
    
    @Persistent
    private String sensorTypeDescription;
    
    @Persistent
    private String sensorTypeUnit;

    @Persistent
    private String sensorTypeComments;
    
    @Persistent
    private Date sensorTypeCreationDate;
    
    @Persistent
    private Date sensorTypeModificationDate;

    /**
     * SensorType constructor.
     * @param sensorTypeName
     * 			: SensorType name
     * @param sensorTypeDescription
     * 			: SensorType description
     * @param sensorTypeUnit
     * 			: SensorType unit
     * @param sensorTypeComments
     * 			: SensorType comments
     * @throws MissingRequiredFieldsException
     */
    public SensorType(String sensorTypeName, 
    		String sensorTypeDescription,
    		String sensorTypeUnit,
    		String sensorTypeComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (sensorTypeName == null || sensorTypeUnit == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (sensorTypeName.trim().isEmpty() || 
    			sensorTypeUnit.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.sensorTypeName = sensorTypeName;
        this.sensorTypeDescription = sensorTypeDescription;
        this.sensorTypeUnit = sensorTypeUnit;
        this.sensorTypeComments = sensorTypeComments;
        
        Date now = new Date();
        this.sensorTypeCreationDate = now;
        this.sensorTypeModificationDate = now;
    }

    /**
     * Get SensorType key.
     * @return SensorType key
     */
    public Long getKey() {
        return key;
    }
    
    /**
     * Get SensorType name.
     * @return SensorType name
     */
    public String getSensorTypeName() {
        return sensorTypeName;
    }

    /**
     * Get SensorType description.
     * @return SensorType description
     */
    public String getSensorTypeDescription() {
    	return sensorTypeDescription;
    }
    
    /**
	 * @return the sensorTypeUnit
	 */
	public String getSensorTypeUnit() {
		return sensorTypeUnit;
	}

	/**
     * Get SensorType comments.
     * @return SensorType comments
     */
    public String getSensorTypeComments() {
    	return sensorTypeComments;
    }
    
    /**
     * Get SensorType creation date.
     * @return SensorType creation date
     */
    public Date getSensorTypeCreationDate() {
    	return sensorTypeCreationDate;
    }
    
    /**
     * Get SensorType modification date.
     * @return SensorType modification date
     */
    public Date getSensorTypeModificationDate() {
    	return sensorTypeModificationDate;
    }
    
    /**
     * Set SensorType name.
     * @param sensorTypeName
     * 			: SensorType name
     * @throws MissingRequiredFieldsException
     */
    public void setSensorTypeName(String sensorTypeName)
    		throws MissingRequiredFieldsException {
    	if (sensorTypeName == null || sensorTypeName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Sensor type name is missing.");
    	}
    	this.sensorTypeName = sensorTypeName;
    	this.sensorTypeModificationDate = new Date();
    }
    
    /**
     * Set SensorType description.
     * @param sensorTypeDescription
     * 			: SensorType description
     */
    public void setSensorTypeDescription(String sensorTypeDescription) {
    	this.sensorTypeDescription = sensorTypeDescription;
    	this.sensorTypeModificationDate = new Date();
    }
    

	/**
	 * @param sensorTypeUnit the sensorTypeUnit to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setSensorTypeUnit(String sensorTypeUnit) 
			throws MissingRequiredFieldsException {
		if (sensorTypeUnit == null || sensorTypeUnit.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Sensor type unit is missing.");
    	}
		this.sensorTypeUnit = sensorTypeUnit;
		this.sensorTypeModificationDate = new Date();
	}
    
    /**
     * Set SensorType comments.
     * @param sensorTypeComments
     * 			: SensorType comments
     */
    public void setSensorTypeComments(String sensorTypeComments) {
    	this.sensorTypeComments = sensorTypeComments;
    	this.sensorTypeModificationDate = new Date();
    }
}