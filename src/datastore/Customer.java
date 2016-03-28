/*
 Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;

import exceptions.InexistentObjectException;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;

/**
 * This class represents the Customer table.
 * It is managed as a JDO to be stored in and retrieved from the GAE datastore.
 * 
 */

@SuppressWarnings("serial")
@PersistenceCapable
public class Customer implements Serializable {
	
	public static enum Status {
		ACTIVE, INACTIVE, UNCONFIRMED, DISABLED
	}
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private String confirmationKey;

    @Persistent(dependent = "true", defaultFetchGroup = "true")
    private User user;
    
    @Persistent
    private String customerName;
    
    @Persistent
    private String customerDescription;
    
    @Persistent
    private PhoneNumber customerPhoneNumber;
    
    @Persistent
    private Key region;
    
    @Persistent
    private PostalAddress customerAddress;
    
    @Persistent
    private Link customerWebsite;
    
    @Persistent
    private BlobKey customerLogo;
    
    @Persistent
    private Status customerStatus;
    
    @Persistent
    private String customerComments;
    
    @Persistent
    private Integer deviceModelVersion;
    
    @Persistent
    private Integer storageDeviceVersion;
    
    @Persistent
    private Integer storageDeviceContainerModelVersion;
    
    @Persistent
    private Integer storageDeviceContainerVersion;
    
    @Persistent
    private Integer storageItemVersion;
    
    @Persistent
    private Integer customerUserVersion;
    
    @Persistent
    private Date customerCreationDate;
    
    @Persistent
    private Date customerModificationDate;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<StorageDevice> storageDevices;
    
    @Element(dependent = "true")
    private ArrayList<StorageDeviceContainer> storageDeviceContainers;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<StorageItem> storageItems;
    
    @Persistent
    @Element(dependent = "true")
    private ArrayList<CustomerUser> customerUsers;
    
    /**
     * Customer constructor.
     * @param confirmationKey
     * 			: the confirmation key string of this Customer
     * @param user
     * 			: the user for this customer
     * @param customerName
     * 			: customer name
     * @param customerDescription
     * 			: customer description
     * @param customerPhoneNumber
     * 			: customer phone number
     * @param region
     * 			: customer region key
     * @param customerAddress
     * 			: customer address
     * @param customerWebsite
     * 			: customer website
     * @param customerLogo
     * 			: customer logo blob key
     * @param customerStatus
     * 			: the initial status of this customer
     * @param customerComments
     * 			: customer comments
     * @throws MissingRequiredFieldsException
     * @throws InvalidFieldFormatException 
     */
    public Customer(
    		String confirmationKey,
    		User user, 
    		String customerName,
    		String customerDescription,
    		PhoneNumber customerPhoneNumber,
    		Key region,
    		PostalAddress customerAddress,
    		Link customerWebsite,
    		BlobKey customerLogo,
    		Status customerStatus,
    		String customerComments)
    		throws MissingRequiredFieldsException {
        
    	// Check "required field" constraints
    	if (user == null || customerName == null || region == null) {
    		throw new MissingRequiredFieldsException(
    				this, "One or more required fields are missing.");
    	}
    	if (customerName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(
    				this, "One or more required fields are missing.");
    	}
    	
    	this.user = user;
    	
    	// Create key with user email
    	this.key = KeyFactory.createKey(
    			Customer.class.getSimpleName(), user.getUserEmail().getEmail());

    	this.confirmationKey = confirmationKey;
    	this.customerName = customerName;
    	this.customerDescription = customerDescription;
    	this.customerPhoneNumber = customerPhoneNumber;
    	this.region = region;
    	this.customerAddress = customerAddress;
        this.customerWebsite = customerWebsite;
        this.customerLogo = customerLogo;
        this.customerStatus = customerStatus;
        this.customerComments = customerComments;
        
        Date now = new Date();
        this.customerCreationDate = now;
        this.customerModificationDate = now;
        
    	// Create empty lists
    	this.storageDevices = new ArrayList<StorageDevice>();
    	this.storageDeviceContainers = new ArrayList<StorageDeviceContainer>();
    	this.storageItems = new ArrayList<StorageItem>();
    	this.customerUsers = new ArrayList<CustomerUser>();
    	
    	// Initialize the versions in 0
    	this.deviceModelVersion = 0;
    	this.storageDeviceVersion = 0;
    	this.storageDeviceContainerModelVersion = 0;
    	this.storageDeviceContainerVersion = 0;
    	this.storageItemVersion = 0;
    	this.customerUserVersion = 0;
    }
    
    /**
     * Get Customer key.
     * @return customer key
     */
    public Key getKey() {
        return key;
    }
    
    /**
     * Get Customer confirmation key string
     * @return customer confirmationKey
     */
    public String getConfirmationKey() {
    	return confirmationKey;
    }
    
    /**
     * Get Customer user.
     * @return customer user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Get Customer name.
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Get Customer description.
     * @return customer description
     */
    public String getCustomerDescription() {
        return customerDescription;
    }
    
    /**
     * Get Customer phone number.
     * @return Customer phone number
     */
    public PhoneNumber getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }
    
    /**
     * Get Customer region.
     * @return region ID
     */
    public Key getRegion() {
    	return region;
    }
    
    /**
     * Get Customer address.
     * @return customer address
     */
    public PostalAddress getCustomerAddress() {
    	return customerAddress;
    }
    
    /**
     * Get Customer web site.
     * @return customer web site
     */
    public Link getCustomerWebsite() {
        return customerWebsite;
    }
    
    /**
     * Get Customer logo.
     * @return customer logo blobkey
     */
    public BlobKey getCustomerLogo() {
        return customerLogo;
    }
    
	/**
	 * @return the customerStatus
	 */
	public Status getCustomerStatus() {
		return customerStatus;
	}
	
	/**
	 * Get a Status from a String representation
	 * @param customerStatusString: the string representation of the Status
	 * @return Status from string
	 */
	public static Status getCustomerStatusFromString(
			String customerStatusString) {

		if (customerStatusString == null) {
			return null;
		}
		else if (customerStatusString.equalsIgnoreCase("active")) {
			return Status.ACTIVE;
		}
		else if (customerStatusString.equalsIgnoreCase("inactive")) {
			return Status.INACTIVE;
		}
		else if (customerStatusString.equalsIgnoreCase("unconfirmed")) {
			return Status.UNCONFIRMED;
		}
		else if (customerStatusString.equalsIgnoreCase("disabled")) {
			return Status.DISABLED;
		}
		else {
			return null;
		}
		
	}
    
    /**
     * Get Customer comments.
     * @return customer comments
     */
    public String getCustomerComments() {
    	return customerComments;
    }
    
	/**
	 * @return the deviceModelVersion
	 */
	public Integer getDeviceModelVersion() {
		return deviceModelVersion;
	}

	/**
	 * @return the storageDeviceVersion
	 */
	public Integer getStorageDeviceVersion() {
		return storageDeviceVersion;
	}
	
	/**
	 * @return the storageDeviceContainerModelVersion
	 */
	public Integer getStorageDeviceContainerModelVersion() {
		return storageDeviceContainerModelVersion;
	}

	/**
	 * @return the storageDeviceContainerVersion
	 */
	public Integer getStorageDeviceContainerVersion() {
		return storageDeviceContainerVersion;
	}

	/**
	 * @return the storageItemVersion
	 */
	public Integer getStorageItemVersion() {
		return storageItemVersion;
	}

	/**
	 * @return the customerUserVersion
	 */
	public Integer getCustomerUserVersion() {
		return customerUserVersion;
	}

	/**
	 * @return the customerCreationDate
	 */
	public Date getCustomerCreationDate() {
		return customerCreationDate;
	}

	/**
	 * @return the customerModificationDate
	 */
	public Date getCustomerModificationDate() {
		return customerModificationDate;
	}

	/**
	 * @return the storageDevices
	 */
	public ArrayList<StorageDevice> getStorageDevices() {
		return storageDevices;
	}
	
	/**
	 * @return the storageDeviceContainers
	 */
	public ArrayList<StorageDeviceContainer> getStorageDeviceContainers() {
		return storageDeviceContainers;
	}

	/**
	 * @return the storageItems
	 */
	public ArrayList<StorageItem> getStorageItems() {
		return storageItems;
	}

	/**
	 * @return the customerUsers
	 */
	public ArrayList<CustomerUser> getCustomerUsers() {
		return customerUsers;
	}
	
	/**
     * Compare this customer with another Customer
     * @param o
     * 			: the object to compare
     * @returns true if the object to compare is equal to this Customer, 
	 *			false otherwise
     */
    @Override
    public boolean equals(Object o){
        if ( this == o ) return true;
        if ( !(o instanceof Customer ) ) return false;
        Customer c = (Customer) o;
        return KeyFactory.keyToString(this.getKey())
                .equals(KeyFactory.keyToString(c.getKey()));
    }

    /**
     * Set Customer name.
     * @param customerName
     * 			: customer name
     * @throws MissingRequiredFieldsException
     */
    public void setCustomerName(String customerName)
    		throws MissingRequiredFieldsException {
    	if (customerName == null || customerName.trim().isEmpty()) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Customer name is missing.");
    	}
    	this.customerName = customerName;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Set Customer phone number.
     * @param customerPhoneNumber
     * 			: customerPhoneNumber
     * @throws MissingRequiredFieldsException
     */
    public void setCustomerPhoneNumber(PhoneNumber customerPhoneNumber) {
    	this.customerPhoneNumber = customerPhoneNumber;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Set Customer description.
     * @param customerDescription
     * 			: customer description
     * @throws MissingRequiredFieldsException
     */
    public void setCustomerDescription(String customerDescription) {
    	this.customerDescription = customerDescription;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Set Customer region
     * @param region
     * 			: region ID
     * @throws MissingRequiredFieldsException 
     */
    public void setRegion(Key region) 
    		throws MissingRequiredFieldsException {
    	
    	if (region == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Customer name is missing.");
    	}
    	this.region = region;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Set Customer address
     * @param customerAddress
     * 			: customer address
     */
    public void setCustomerAddress(PostalAddress customerAddress) {
    	this.customerAddress = customerAddress;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Set Customer web site.
     * @param customerWebsite
     * 			: customer web site
     */
    public void setCustomerWebsite(Link customerWebsite) {
    	this.customerWebsite = customerWebsite;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Set Customer logo.
     * @param customerLogo
     * 			: customer logo blob key
     */
    public void setCustomerLogo(BlobKey customerLogo) {
    	this.customerLogo = customerLogo;
    	this.customerModificationDate = new Date();
    }
    
	/**
	 * @param customerStatus the customerStatus to set
	 * @throws MissingRequiredFieldsException 
	 */
	public void setCustomerStatus(Status customerStatus) 
			throws MissingRequiredFieldsException {
		
		if (customerStatus == null) {
    		throw new MissingRequiredFieldsException(this.getClass(), 
    				"Customer Status is missing.");
    	}
		
		this.customerStatus = customerStatus;
		this.customerModificationDate = new Date();
	}
    
    /**
     * Set Customer comments.
     * @param customerComments
     * 			: customer comments
     */
    public void setCustomerComments(String customerComments) {
    	this.customerComments = customerComments;
    	this.customerModificationDate = new Date();
    }
    
    /**
     * Add a new Storage Device to the customer.
     * @param storageDevice
     * 			: new Storage Device to be added
     */
    public void addStorageDevice(StorageDevice storageDevice) {
    	this.storageDevices.add(storageDevice);
    }
    
    /**
     * Add a new Storage Device Container to the customer.
     * @param storageDeviceContainer
     * 			: new Storage Device Container to be added
     */
    public void addStorageDeviceContainer(
    		StorageDeviceContainer storageDeviceContainer) {
    	this.storageDeviceContainers.add(storageDeviceContainer);
    }
    
    /**
     * Add a new storage Item  to this customer.
     * @param storageItem
     * 			: new storage Item to be added
     */
    public void addStorageItem(StorageItem storageItem) {
    	this.storageItems.add(storageItem);
    }
    
    /**
     * Add a new customer user to this customer.
     * @param customerUser
     * 			: new customerUser to be added
     */
    public void addCustomerUser(CustomerUser customerUser) {
    	this.customerUsers.add(customerUser);
    }

    /**
     * Remove a StorageDevice from the customer.
     * @param storageDevice
     * 			: Storage Device to be removed
     * @throws InexistentObjectException
     */
    public void removeStorageDevice(StorageDevice storageDevice) 
    		throws InexistentObjectException {
    	if (!storageDevices.remove(storageDevice)) {
    		throw new InexistentObjectException(
    				StorageDevice.class, "StorageDevice not found!");
    	}
    }
    
    /**
     * Remove a StorageDeviceContainer from the customer.
     * @param storageDeviceContainer
     * 			: Storage Device Container to be removed
     * @throws InexistentObjectException
     */
    public void removeStorageDeviceContainer(
    		StorageDeviceContainer storageDeviceContainer) 
    		throws InexistentObjectException {
    	if (!storageDeviceContainers.remove(storageDeviceContainer)) {
    		throw new InexistentObjectException(
    				StorageDeviceContainer.class, 
    				"StorageDeviceContainer not found!");
    	}
    }
    
    /**
     * Remove Storage Item from the customer.
     * @param storageItem
     * 			: Storage Item to be removed
     * @throws InexistentObjectException
     */
    public void removeStorageItem(StorageItem storageItem) 
    		throws InexistentObjectException {
    	if (!this.storageItems.remove(storageItem)) {
    		throw new InexistentObjectException
    				(StorageItem.class, "StorageItem not found!");
    	}
    }
    
    /**
     * Remove Customer User from the customer.
     * @param customerUser
     * 			: Customer User to be removed
     * @throws InexistentObjectException
     */
    public void removeCustomerUser(CustomerUser customerUser) 
    		throws InexistentObjectException {
    	if (!this.customerUsers.remove(customerUser)) {
    		throw new InexistentObjectException
    				(CustomerUser.class, "CustomerUser not found!");
    	}
    }
    
    /**
     * Update the Device Model Version number by 1.
     */
    public void updateDeviceModelVersion() {
    	deviceModelVersion++;
    }
    
    /**
     * Update the Storage Device Version number by 1.
     */
    public void updateStorageDeviceVersion() {
    	storageDeviceVersion++;
    }
    
    /**
     * Update the Storage Device Container Model Version number by 1.
     */
    public void updateStorageDeviceContainerModelVersion() {
    	storageDeviceContainerModelVersion++;
    }
    
    /**
     * Update the Storage Device Container Version number by 1.
     */
    public void updateStorageDeviceContainerVersion() {
    	storageDeviceContainerVersion++;
    }
    
    /**
     * Update the Storage Item Version number by 1.
     */
    public void updateStorageItemVersion() {
    	storageItemVersion++;
    }
    
    /**
     * Update the Customer User Version number by 1.
     */
    public void updateCustomerUserVersion() {
    	customerUserVersion++;
    }
}
