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

import util.FieldValidator;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PhoneNumber;

import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the CustomerUser table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class CustomerUser implements Serializable {

	// Enumerator for gender
	public static enum Gender {
		MALE, FEMALE
	}
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent(dependent = "true", defaultFetchGroup = "true")
    private User user;
    
    @Persistent
    private String customerUserName;
    
	@Persistent
    private String customerUserDescription;
    
    @Persistent
    private Gender customerUserGender;
    
    @Persistent
    private String customerUserTitle;
    
    @Persistent
    private Long department;
    
    @Persistent
    private PhoneNumber customerUserPhoneNumber;
    
    @Persistent
    private String customerUserComments;
    
    @Persistent
    private Date customerUserCreationTime;
    
    @Persistent
    private Date customerUserModificationTime;

	/**
     * CustomerUser constructor.
     * @param user
     * 			: user for this customerUser
     * @param customerUserName
     * 			: customerUser name
     * @param customerUserDescription
     * 			: customerUser description
     * @param customerUserGender
     * 			: customerUser gender
     * @param customerUserTitle
     * 			: customerUser title
     * @param department
     * 			: customerUser department
     * @param customerUserPhoneNumber
     * 			: customerUser phone number
     * @param customerUserComments
     * 			: customerUser comments
     * @throws MissingRequiredFieldsException
     * @throws InvalidFieldFormatException
     */
    public CustomerUser(
    		User user, 
    		String customerUserName, 
    		String customerUserDescription, 
            Gender customerUserGender,
            String customerUserTitle,
            Long department, 
            PhoneNumber customerUserPhoneNumber, 
    		String customerUserComments) 
    		throws MissingRequiredFieldsException, 
    		InvalidFieldFormatException {
        
    	// Check "required field" constraints
    	if (user == null || customerUserName == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	if (customerUserName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"One or more required fields are missing.");
    	}
    	
    	// Check phone number format
    	if (customerUserPhoneNumber != null) {
	    	if (!FieldValidator.isValidPhoneNumber(customerUserPhoneNumber.getNumber())) {
	    		throw new InvalidFieldFormatException(this.getClass(), "Invalid phone number.");
	    	}
    	}
    	
    	this.user = user;
    	
    	// Create key with user email
    	this.key = KeyFactory.createKey(
    			CustomerUser.class.getSimpleName(), user.getUserEmail().getEmail());
    	
    	this.customerUserName = customerUserName;
    	this.customerUserDescription = customerUserDescription;
    	this.customerUserGender = customerUserGender;
    	this.customerUserTitle = customerUserTitle;
    	this.department = department;
        this.customerUserPhoneNumber = customerUserPhoneNumber;
        this.customerUserComments = customerUserComments;
        
        Date now = new Date();
        this.customerUserCreationTime = now;
        this.customerUserModificationTime = now;
    }

    /**
     * Get CustomerUser key.
     * @return customerUser key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get CustomerUser user.
     * @return customerUser user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Get CustomerUser name.
     * @return customerUser name
     */
    public String getCustomerUserName() {
        return customerUserName;
    }
    
    /**
	 * @return the customerUserDescription
	 */
	public String getCustomerUserDescription() {
		return customerUserDescription;
	}
	
	/**
     * Get Gender from String.
     * @param genderString: the string representation of the Gender
     * @return a Gender based on the given string representation
     */
    public static Gender getGenderFromString(String genderString) {
    	if (genderString == null) {
    		return null;
    	}
    	
    	if (genderString.equalsIgnoreCase("male")) {
    		return Gender.MALE;
    	}
    	else if (genderString.equalsIgnoreCase("female")) {
    		return Gender.FEMALE;
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * Get CustomerUser gender.
     * @return customerUser gender
     */
    public Gender getCustomerUserGender() {
        return customerUserGender;
    }

	/**
	 * @return the customerUserTitle
	 */
	public String getCustomerUserTitle() {
		return customerUserTitle;
	}

	/**
	 * @return the department key
	 */
	public Long getDepartment() {
		return department;
	}

    /**
     * Get CustomerUser phone number.
     * @return customerUser phone number
     */
    public PhoneNumber getCustomerUserPhoneNumber() {
        return customerUserPhoneNumber;
    }
    
    /**
     * Get CustomerUser comments.
     * @return customerUser comments
     */
    public String getCustomerUserComments() {
    	return customerUserComments;
    }
    
	/**
     * Compare this CustomerUser with another CustomerUser
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this CustomerUser, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof CustomerUser ) ) return false;
        CustomerUser cu = (CustomerUser) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(cu.getKey()));
    }
    
    /**
     * Set CustomerUser name.
     * @param customerUserName
     * 			: customerUser name
     * @throws MissingRequiredFieldsException 
     */
    public void setCustomerUserName(String customerUserName) 
    		throws MissingRequiredFieldsException {
    	
    	if (customerUserName == null || customerUserName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"CustomerUser name is missing.");
    	}
    	this.customerUserName = customerUserName;
    	this.customerUserModificationTime = new Date();
    }
    
    /**
	 * @param customerUserDescription the customerUserDescription to set
	 */
	public void setCustomerUserDescription(String customerUserDescription) {
		this.customerUserDescription = customerUserDescription;
		this.customerUserModificationTime = new Date();
	}
	
    /**
     * Set CustomerUser gender.
     * @param customerUserGender
     * 			: customerUser gender
     */
    public void setCustomerUserGender(Gender customerUserGender) {
    	this.customerUserGender = customerUserGender;
    	this.customerUserModificationTime = new Date();
    }

	/**
	 * @param customerUserTitle the customerUserTitle to set
	 */
	public void setCustomerUserTitle(String customerUserTitle) {
		this.customerUserTitle = customerUserTitle;
		this.customerUserModificationTime = new Date();
	}

	/**
	 * @param department key
	 * 			: the department to set
	 */
	public void setDepartment(Long department) {
		this.department = department;
		this.customerUserModificationTime = new Date();
	}
    
    /**
     * Set CustomerUser phone number.
     * @param customerUserPhoneNumber
     * 			: customerUser phone number
     * @throws InvalidFieldFormatException 
     */
    public void setCustomerUserPhoneNumber(PhoneNumber customerUserPhoneNumber) 
    		throws InvalidFieldFormatException {
    	
    	// Check phone number format
    	if (customerUserPhoneNumber != null) {
	    	if (!FieldValidator.isValidPhoneNumber(
	    			customerUserPhoneNumber.getNumber())) {
	    		throw new InvalidFieldFormatException(
	    				this.getClass(), "Invalid phone number.");
	    	}
    	}
    	
    	this.customerUserPhoneNumber = customerUserPhoneNumber;
    	this.customerUserModificationTime = new Date();
    }
    
    /**
     * Set CustomerUser comments.
     * @param customerUserComments
     * 			: customerUser comments
     */
    public void setCustomerUserComments(String customerUserComments) {
    	this.customerUserComments = customerUserComments;
    	this.customerUserModificationTime = new Date();
    }
    
}
