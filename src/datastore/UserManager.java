/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package datastore;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import exceptions.ObjectExistsInDatastoreException;

/**
 * This class is used to manage the GAE Datastore operations 
 * (get, put, delete, update) made on the User class.
 */

public class UserManager {
	
	private static final Logger log = 
        Logger.getLogger(UserManager.class.getName());

	/**
     * Get a user from the datastore given its email.
     * @param email
     * 			: user email
     * @return the User instance, null if not found
     */
	@SuppressWarnings("unchecked")
	public static User getUser(String email) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        // look for the user as an administrator
        Key originalKey = 
        		KeyFactory.createKey(Administrator.class.getSimpleName(), 
        				email);
        Key ignoreCaseKey = 
        		KeyFactory.createKey(Administrator.class.getSimpleName(), 
        				email.toLowerCase());
        
		User user;
		try {
			user = pm.getObjectById(Administrator.class, 
					ignoreCaseKey).getUser();
		} 
		catch (JDOObjectNotFoundException e) {
			try {
				user = 
					pm.getObjectById(Administrator.class, 
							originalKey).getUser();
			}
			catch (JDOObjectNotFoundException e2) {
				user = null;
			}
		}

        // if it is not an admin lets look for a customer
        if (user == null) {
            originalKey = 
            		KeyFactory.createKey(Customer.class.getSimpleName(), 
            				email);
            ignoreCaseKey = 
            		KeyFactory.createKey(Customer.class.getSimpleName(), 
            				email.toLowerCase());
            
            try {
                user = pm.getObjectById(Customer.class, 
                		ignoreCaseKey).getUser();
            } 
            catch (JDOObjectNotFoundException e) {
    			try {
    				user = 
    					pm.getObjectById(Customer.class, 
    							originalKey).getUser();
    			}
    			catch (JDOObjectNotFoundException e2) {
    				user = null;
    			}
            }
        }

        // if it is not an admin or a customer could it be a customer user?
        if (user == null) {
        	
        	// Search all Customers for parent
            Query query = pm.newQuery(Customer.class);
            List<Customer> customers = (List<Customer>) query.execute();
        	for (Customer customer : customers) {
	            originalKey = 
	            		KeyFactory.createKey(customer.getKey(), 
	            				CustomerUser.class.getSimpleName(), 
	            				email);
	            ignoreCaseKey = 
	            		KeyFactory.createKey(customer.getKey(), 
	            				CustomerUser.class.getSimpleName(), 
	            				email.toLowerCase());
	            
	            try {
	                user = pm.getObjectById(CustomerUser.class, 
	                		ignoreCaseKey).getUser();
	            } 
	            catch (JDOObjectNotFoundException e) {
	    			try {
	    				user = 
	    					pm.getObjectById(CustomerUser.class, 
	    							originalKey).getUser();
	    			}
	    			catch (JDOObjectNotFoundException e2) {
	    				user = null;
	    			}
	            }
	            
	            if (user != null) {
	            	break;
	            }
        	}
        }

		pm.close();
		return user;
	}

	/**
     * Get a user from the datastore given its email
     * and verifying the user's hashed password.
     * @param email
     * 			: user email
     * @param hashedPass
     * 			: the hashed password of the user
     * @return the User instance, null if not found
     * TODO: Not specific enough for error message
     */
	public static User getUser(String email, String hashedPass) {
        User user = getUser(email);

        // let us verify the passwords, right now we will return a null
        // value if the passwords don't match. If you want a more specific
        // error message to be displayed then this is probably not right
        log.log(Level.WARNING, user == null? "null" : "not null");
        if (user == null || !user.getUserPassword().equals(hashedPass))
            return null;
        else
		    return user;
	}

	/**
     * Get a user from the datastore given the user itself
     * @param user
     * 			: the user to get
     * @return the User instance
     */
	public static User getUser(User user) {
	    return getUser(user.getUserEmail().getEmail());
    }

	/**
     * Get a user from the datastore given its key.
     * @param key
     * 			: user key
     * @return the User instance
     */
	public static User getUser(Key key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		User user;
		try  {
			user = pm.getObjectById(User.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return user;
	}

	/**
     * Get all users stored in the datastore.
     * @return all User instances
     */
    @SuppressWarnings("unchecked")
	public static List<User> getAllUsers() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(User.class);

        try {
            return (List<User>) query.execute();
        } 
        finally {
            query.closeAll();
        }
    }
    
	/**
     * Check if a user with this email already exists in the datastore.
     * @param email
     * 			: the email to check
     * @throws ObjectExistsInDatastoreException
     */
    public static boolean userExists(Email email) {

    	email = new Email(email.getEmail().toLowerCase());
    	
		Key administratorKey = 
				KeyFactory.createKey(Administrator.class.getSimpleName(), email.getEmail());
		Key custKey = 
				KeyFactory.createKey(Customer.class.getSimpleName(), email.getEmail());
		Key customerUserKey =
				KeyFactory.createKey(CustomerUser.class.getSimpleName(), email.getEmail());
		
		if (DatastoreManager.entityExists(Administrator.class, administratorKey) ||
				DatastoreManager.entityExists(Customer.class, custKey) || 
				DatastoreManager.entityExists(CustomerUser.class, customerUserKey)) {
			
			return true;
		}
		else {
			return false;
		}
    }
}
