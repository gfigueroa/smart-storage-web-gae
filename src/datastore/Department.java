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
 * This class represents the Department table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@PersistenceCapable
public class Department {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long key;

    @Persistent
    private String departmentName;
    
    @Persistent
    private String departmentDescription;

    @Persistent
    private String departmentComments;
    
    @Persistent
    private Date departmentCreationDate;
    
    @Persistent
    private Date departmentModificationDate;

    /**
     * Department constructor.
     * @param departmentName
     * 			: Department name
     * @param departmentDescription
     * 			: Department description
     * @param departmentComments
     * 			: Department comments
     * @throws MissingRequiredFieldsException
     */
    public Department(String departmentName, 
    		String departmentDescription,
    		String departmentComments) 
    		throws MissingRequiredFieldsException {
    	
    	// Check "required field" constraints
    	if (departmentName == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (departmentName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}

        this.departmentName = departmentName;
        this.departmentDescription = departmentDescription;
        this.departmentComments = departmentComments;
        
        Date now = new Date();
        this.departmentCreationDate = now;
        this.departmentModificationDate = now;
    }

    /**
     * Get Department key.
     * @return Department key
     */
    public Long getKey() {
        return key;
    }
    
    /**
     * Get Department name.
     * @return Department name
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Get Department description.
     * @return Department description
     */
    public String getDepartmentDescription() {
    	return departmentDescription;
    }

	/**
     * Get Department comments.
     * @return Department comments
     */
    public String getDepartmentComments() {
    	return departmentComments;
    }
    
    /**
     * Get Department creation date.
     * @return Department creation date
     */
    public Date getDepartmentCreationDate() {
    	return departmentCreationDate;
    }
    
    /**
     * Get Department modification date.
     * @return Department modification date
     */
    public Date getDepartmentModificationDate() {
    	return departmentModificationDate;
    }
    
    /**
     * Set Department name.
     * @param departmentName
     * 			: Department name
     * @throws MissingRequiredFieldsException
     */
    public void setDepartmentName(String departmentName)
    		throws MissingRequiredFieldsException {
    	if (departmentName == null || departmentName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Department name is missing.");
    	}
    	this.departmentName = departmentName;
    	this.departmentModificationDate = new Date();
    }
    
    /**
     * Set Department description.
     * @param departmentDescription
     * 			: Department description
     */
    public void setDepartmentDescription(String departmentDescription) {
    	this.departmentDescription = departmentDescription;
    	this.departmentModificationDate = new Date();
    }
    
    /**
     * Set Department comments.
     * @param departmentComments
     * 			: Department comments
     */
    public void setDepartmentComments(String departmentComments) {
    	this.departmentComments = departmentComments;
    	this.departmentModificationDate = new Date();
    }
}