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

import exceptions.MissingRequiredFieldsException;

/**
 * This class is used to manage the GAE Datastore operations (get, put, delete, update)
 * made on the Department class.
 * 
 */

public class DepartmentManager {
	
	private static final Logger log = 
        Logger.getLogger(DepartmentManager.class.getName());
	
	/**
     * Get a Department instance from the datastore given the Department key.
     * @param key
     * 			: the Department's key
     * @return Department instance, null if Department is not found
     */
	public static Department getDepartment(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Department department;
		try  {
			department = pm.getObjectById(Department.class, key);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
		pm.close();
		return department;
	}
	
	/**
     * Get Department instance from the datastore with this name.
     * @return Department that has this name
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static Department getDepartment(String departmentName) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Department.class);
        query.setFilter("departmentName == departmentNameParam");
        query.declareParameters("String departmentNameParam");

        try {
        	List<Department> departments = (List<Department>) query.execute(departmentName);
            // touch all elements
        	if (departments != null) {
	            for (Department t : departments)
	                t.getDepartmentName();
	            return departments.get(0);
        	}
        	else {
        		return null;
        	}
        } 
        finally {
        	pm.close();
            query.closeAll();
        }
    }
	
	/**
     * Get all Department instances from the datastore.
     * @return All Department instances
     * TODO: Inefficient touching of objects
     */
	@SuppressWarnings("unchecked")
	public static List<Department> getAllDepartments() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(Department.class);

        List<Department> types = null;
        try {
        	types = (List<Department>) query.execute();
            // touch all elements
            for (Department t : types)
                t.getDepartmentName();
        } 
        finally {
        	pm.close();
            query.closeAll();
        }

        return types;
    }
	
	/**
     * Put Department into datastore.
     * Stations the given Department instance in the datastore calling the 
     * PersistenceManager's makePersistent() method.
     * @param department
     * 			: the Department instance to store
     */
	public static void putDepartment(Department department) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(department);
			tx.commit();
			log.info("Department \"" + department.getDepartmentName() + 
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
    * Delete Department from datastore.
    * Deletes the Department corresponding to the given key
    * from the datastore calling the PersistenceManager's 
    * deletePersistent() method.
    * @param key
    * 			: the key of the Department instance to delete
    */
	public static void deleteDepartment(Long key) {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Department department = pm.getObjectById(Department.class, key);
			String DepartmentName = department.getDepartmentName();
			tx.begin();
			pm.deletePersistent(department);
			tx.commit();
			log.info("Department \"" + DepartmentName + 
                     "\" deleted successfully from datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	/**
    * Update Department attributes.
    * Update's the given Department's attributes in the datastore.
    * @param key
    * 			: the key of the Department whose attributes will be updated
    * @param departmentName
    * 			: the new name to give to the Department
    * @param departmentDescription
    * 			: the new description to give to the Department
    * @param departmentComments
    * 			: the new comments to give to the Department
	* @throws MissingRequiredFieldsException 
    */
	public static void updateDepartmentAttributes(
			Long key,
			String departmentName, 
			String departmentDescription,
			String departmentComments) 
                       throws MissingRequiredFieldsException {	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Transaction tx = pm.currentTransaction();
		try {
			Department department = pm.getObjectById(Department.class, key);
			tx.begin();
			department.setDepartmentName(departmentName);
			department.setDepartmentDescription(departmentDescription);
			department.setDepartmentComments(departmentComments);
			tx.commit();
			log.info("Department \"" + departmentName + 
                     "\"'s attributes updated in datastore.");
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
	
}
