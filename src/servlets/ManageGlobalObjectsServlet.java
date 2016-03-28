/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.DateManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import datastore.AlarmWarning;
import datastore.AlarmWarningManager;
import datastore.AlarmWarningMessage;
import datastore.AlarmWarningMessage.AlarmWarningMessageStatus;
import datastore.AlarmWarningMessageManager;
import datastore.Country;
import datastore.CountryManager;
import datastore.Department;
import datastore.DepartmentManager;
import datastore.DeviceModel;
import datastore.DeviceModelDoor;
import datastore.DeviceModelDoorManager;
import datastore.DeviceModelManager;
import datastore.DeviceModelPartition;
import datastore.DeviceModelPartitionManager;
import datastore.DeviceServiceType;
import datastore.DeviceServiceTypeManager;
import datastore.Region;
import datastore.RegionManager;
import datastore.SensorType;
import datastore.SensorTypeManager;
import datastore.StorageDeviceContainerModel;
import datastore.StorageDeviceContainerModelManager;
import datastore.SystemManager;
import datastore.User;
import exceptions.MissingRequiredFieldsException;

/**
 * This servlet class is used to add, delete and update
 * global objects in the system.
 * 
 */

@SuppressWarnings("serial")
public class ManageGlobalObjectsServlet extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(ManageGlobalObjectsServlet.class.getName());
    
    // JSP file locations
    private static final String addDeviceServiceTypeJSP = 
    		"/admin/addEquipmentType.jsp";
    private static final String editDeviceServiceTypeJSP = 
    		"/admin/editEquipmentType.jsp";
    private static final String listDeviceServiceTypeJSP = 
    		"/admin/listEquipmentType.jsp";
    
    private static final String addDeviceModelJSP = "/admin/addEquipmentModel.jsp";
    private static final String editDeviceModelJSP = "/admin/editEquipmentModel.jsp";
    private static final String listDeviceModelJSP = "/admin/listEquipmentModel.jsp";
    
    private static final String addStorageDeviceContainerModelJSP = 
    		"/admin/addStorageDeviceContainerModel.jsp";
    private static final String editStorageDeviceContainerModelJSP = 
    		"/admin/editStorageDeviceContainerModel.jsp";
    private static final String listStorageDeviceContainerModelJSP = 
    		"/admin/listStorageDeviceContainerModel.jsp";
    
    private static final String addSensorTypeJSP = "/admin/addSensorType.jsp";
    private static final String editSensorTypeJSP = "/admin/editSensorType.jsp";
    private static final String listSensorTypeJSP = "/admin/listSensorType.jsp";
    
    private static final String addCountryJSP = "/admin/addCountry.jsp";
    private static final String editCountryJSP = "/admin/editCountry.jsp";
    private static final String listCountryJSP = "/admin/listCountry.jsp";
    
    private static final String addRegionJSP = "/admin/addRegion.jsp";
    private static final String editRegionJSP = "/admin/editRegion.jsp";
    private static final String listRegionJSP = "/admin/listRegion.jsp";
    
    private static final String addDepartmentJSP = "/admin/addDepartment.jsp";
    private static final String editDepartmentJSP = "/admin/editDepartment.jsp";
    private static final String listDepartmentJSP = "/admin/listDepartment.jsp";
    
    private static final String addAlarmWarningJSP = "/admin/addAlarmMessage.jsp";
    private static final String editAlarmWarningJSP = "/admin/editAlarmMessage.jsp";
    private static final String listAlarmWarningJSP = "/admin/listAlarmMessageManage.jsp";

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        
    	HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // Check that an administrator is carrying out the action
	    if (user == null || user.getUserType() != User.UserType.ADMINISTRATOR) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return;
	    }
    	
    	// Lets check the action required by the jsp
        String action = req.getParameter("action");
        
        // Common parameters
        String successURL = "";

        // DELETE
        if (action.equals("delete")) {
            // Retrieve the key     
        	String keyString = req.getParameter("k");

        	// Retrieve the object type to delete
        	String type = req.getParameter("type");

        	// Delete DeviceServiceType
        	if (type.equalsIgnoreCase("deviceServiceType")) {
            	successURL = listDeviceServiceTypeJSP;
        		
        		Long key = Long.parseLong(keyString);
        		DeviceServiceTypeManager.deleteDeviceServiceType(key);
            	SystemManager.updateDeviceServiceTypeListVersion();
        	}
        	// Delete DeviceModel
        	else if (type.equalsIgnoreCase("deviceModel")) {
            	successURL = listDeviceModelJSP;
        		
        		Key key = KeyFactory.stringToKey(keyString);
        		DeviceModelManager.deleteDeviceModel(key);
            	SystemManager.updateDeviceModelListVersion();
        	}
        	// Delete StorageDeviceContainerModel
        	else if (type.equalsIgnoreCase("storageDeviceContainerModel")) {
            	successURL = listStorageDeviceContainerModelJSP;
        		
        		Long key = Long.parseLong(keyString);
        		StorageDeviceContainerModelManager.deleteStorageDeviceContainerModel(key);
            	SystemManager.updateStorageDeviceContainerModelListVersion();
        	}
        	// Delete SensorType
        	else if (type.equalsIgnoreCase("sensorType")) {
            	successURL = listSensorTypeJSP;
        		
        		Long key = Long.parseLong(keyString);
        		SensorTypeManager.deleteSensorType(key);
            	SystemManager.updateSensorTypeListVersion();
        	}
        	// Delete Country
        	else if (type.equalsIgnoreCase("country")) {
            	successURL = listCountryJSP;
        		
            	Key key = KeyFactory.stringToKey(keyString);
        		CountryManager.deleteCountry(key);
        	}
        	// Delete Region
        	else if (type.equalsIgnoreCase("region")) {
        		successURL = listRegionJSP;
        		
        		Key key = KeyFactory.stringToKey(keyString);
        		RegionManager.deleteRegion(key);
        	}
        	// Delete Department
        	else if (type.equalsIgnoreCase("department")) {
            	successURL = listDepartmentJSP;
        		
        		Long key = Long.parseLong(keyString);
        		DepartmentManager.deleteDepartment(key);
            	SystemManager.updateDepartmentListVersion();
        	}
        	// Delete Alarm Warning
        	else if (type.equalsIgnoreCase("alarmWarning")) {
        		successURL = listAlarmWarningJSP;
        		
        		Key key = KeyFactory.stringToKey(keyString);
        		AlarmWarningManager.deleteAlarmWarning(key);
        	}
        	// No more choices
      		else {
      			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
      			return;
      		}

        	// Success URL
        	resp.sendRedirect(successURL + "?msg=success&action=" + action);
        }
        // No more choices
  		else {
  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  			return;
  		}
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
    	
    	HttpSession session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        
        // Check that an administrator is carrying out the action
	    if (user == null || user.getUserType() != User.UserType.ADMINISTRATOR) {
	    	resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	        return;
	    }
    	
        // Lets check the action required by the jsp
        String action = req.getParameter("action");
        
        // Common parameters
        String type = req.getParameter("type");
        String successURL = "";
        String failURL = "";
        
        // ADD
        if (action.equals("add")) {
            try {
	            
            	// Add DeviceServiceType
                if (type.equalsIgnoreCase("deviceServiceType")) {
	                successURL = listDeviceServiceTypeJSP;
	                failURL = addDeviceServiceTypeJSP;
                	
                	addOrUpdateDeviceServiceType(req, null, true);
	            }
                // Add DeviceModel
                else if (type.equalsIgnoreCase("deviceModel")) {
	                successURL = listDeviceModelJSP;
	                failURL = addDeviceModelJSP;
                	
                	addOrUpdateDeviceModel(req, null, true);
	            }
                // Add StorageDeviceContainerModel
                else if (type.equalsIgnoreCase("storageDeviceContainerModel")) {
	                successURL = listStorageDeviceContainerModelJSP;
	                failURL = addStorageDeviceContainerModelJSP;
                	
                	addOrUpdateStorageDeviceContainerModel(req, null, true);
	            }
                // Add SensorType
                else if (type.equalsIgnoreCase("sensorType")) {
	                successURL = listSensorTypeJSP;
	                failURL = addSensorTypeJSP;
                	
                	addOrUpdateSensorType(req, null, true);
	            }
                // Add Country
                else if (type.equalsIgnoreCase("country")) {
	                successURL = listCountryJSP;
	                failURL = addCountryJSP;
                	
                	addOrUpdateCountry(req, null, true); 
	            }
	            // Add Region
                else if (type.equalsIgnoreCase("region")) {
                	successURL = listRegionJSP;
                	failURL = addRegionJSP;
                	
                	addOrUpdateRegion(req, null, true);
                }
                else if (type.equalsIgnoreCase("department")) {
	                successURL = listDepartmentJSP;
	                failURL = addDepartmentJSP;
                	
                	addOrUpdateDepartment(req, null, true);
	            }
	            // Add Alarm Warning
                else if (type.equalsIgnoreCase("alarmWarning")) {
                	successURL = listAlarmWarningJSP;
                	failURL = addAlarmWarningJSP;
                	
                	addOrUpdateAlarmWarning(req, null, true);
                }
                // No more choices
          		else {
          			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
          			return;
          		}
	        	
	        	// Success URL
	        	resp.sendRedirect(successURL + "?msg=success&action=" + action);
            }
            catch (MissingRequiredFieldsException mrfe) {
                resp.sendRedirect(failURL + "?etype=MissingInfo");
                return;
            }
            catch (Exception ex) {
                log.log(Level.SEVERE, ex.toString());
                ex.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
	    }
        // UPDATE
	    else if (action.equals("update")) {
	    	String keyString = req.getParameter("k");
	    	try {
	    		
	    		// Update DeviceServiceType
            	if (type.equalsIgnoreCase("deviceServiceType")) {
                	successURL = editDeviceServiceTypeJSP;
                	failURL = editDeviceServiceTypeJSP;
            		
            		Long key = Long.parseLong(keyString);
            		addOrUpdateDeviceServiceType(req, key, false);
            	}
            	// Update DeviceModel
            	else if (type.equalsIgnoreCase("deviceModel")) {
                	successURL = editDeviceModelJSP;
                	failURL = editDeviceModelJSP;
            		
            		Key key = KeyFactory.stringToKey(keyString);
            		addOrUpdateDeviceModel(req, key, false);
            	}
            	// Update StorageDeviceContainerModel
            	else if (type.equalsIgnoreCase("storageDeviceContainerModel")) {
                	successURL = editStorageDeviceContainerModelJSP;
                	failURL = editStorageDeviceContainerModelJSP;
            		
            		Long key = Long.parseLong(keyString);
            		addOrUpdateStorageDeviceContainerModel(req, key, false);
            	}
            	// Update SensorType
            	else if (type.equalsIgnoreCase("sensorType")) {
                	successURL = editSensorTypeJSP;
                	failURL = editSensorTypeJSP;
            		
            		Long key = Long.parseLong(keyString);
            		addOrUpdateSensorType(req, key, false);
            	}
            	// Update Country
            	else if (type.equalsIgnoreCase("country")) {
                	successURL = editCountryJSP;
                	failURL = editCountryJSP;
            		
                	Key key = KeyFactory.stringToKey(keyString);
            		addOrUpdateCountry(req, key, false);
            	}
	            // Update Region
	    		else if (type.equalsIgnoreCase("region")) {
                	successURL = editRegionJSP;
                	failURL = editRegionJSP;
	    			
	    			Key key = KeyFactory.stringToKey(keyString);
	    			addOrUpdateRegion(req, key, false);
            	}
            	// Update SensorType
            	else if (type.equalsIgnoreCase("department")) {
                	successURL = editDepartmentJSP;
                	failURL = editDepartmentJSP;
            		
            		Long key = Long.parseLong(keyString);
            		addOrUpdateDepartment(req, key, false);
            	}
	            // Update Alarm Warning
	    		else if (type.equalsIgnoreCase("alarmWarning")) {
                	successURL = editAlarmWarningJSP;
                	failURL = editAlarmWarningJSP;
	    			
	    			Key key = KeyFactory.stringToKey(keyString);
	    			addOrUpdateAlarmWarning(req, key, false);
            	}
            	// No more choices
          		else {
          			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
          			return;
          		}
            	
    	    	// If success
                resp.sendRedirect(successURL + "?k=" + keyString + 
                		"&msg=success&action=" + action);
            }
            catch (MissingRequiredFieldsException mrfe) {
                resp.sendRedirect(failURL + "?etype=MissingInfo&k="
                        + keyString);
                return;
            }
            catch (Exception ex) {
                log.log(Level.SEVERE, ex.toString());
                ex.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        // No more choices
  		else {
  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  			return;
  		}
    }
    
    /**
     * Add or Update DeviceServiceType
     * @param req: the HTTPServletRequest
     * @param key: the DeviceServiceType key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateDeviceServiceType(HttpServletRequest req, 
    		Long key, boolean add) 
    		throws MissingRequiredFieldsException {
        
    	String deviceServiceTypeName = 
        		req.getParameter("deviceServiceTypeName");
        String deviceServiceTypeDescription = 
        		req.getParameter("deviceServiceTypeDescription");
        String deviceServiceTypeComments =
        		req.getParameter("deviceServiceTypeComments");
        
        if (add) {
	        DeviceServiceType deviceServiceType = 
	        		new DeviceServiceType(
	        				deviceServiceTypeName, 
	        				deviceServiceTypeDescription,
	        				deviceServiceTypeComments);
	        DeviceServiceTypeManager.putDeviceServiceType(deviceServiceType);
        }
        else {
        	DeviceServiceTypeManager.updateDeviceServiceTypeAttributes(
            		key, 
            		deviceServiceTypeName, 
            		deviceServiceTypeDescription, 
            		deviceServiceTypeComments);
        }
        
        // Update deviceServiceTypeListVersion
        SystemManager.updateDeviceServiceTypeListVersion();
    }
    
    /**
     * Add or Update DeviceModel
     * @param req: the HTTPServletRequest
     * @param key: the DeviceModel key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateDeviceModel(HttpServletRequest req, 
    		Key key, boolean add) 
    		throws MissingRequiredFieldsException {
        
    	// DeviceModel data
    	
    	String deviceServiceTypeKeyString =
    			req.getParameter("deviceServiceTypeId");
    	Long deviceServiceTypeKey = null;
    	if (!deviceServiceTypeKeyString.isEmpty()) {
    		deviceServiceTypeKey = Long.parseLong(deviceServiceTypeKeyString);
    	}
    	
    	String deviceModelName = 
        		req.getParameter("deviceModelName");
        String deviceModelDescription = 
        		req.getParameter("deviceModelDescription");
        
        String deviceModelDesignTimeString = 
        		req.getParameter("deviceModelDesignTime");
        Date deviceModelDesignTime = null;
        if (!deviceModelDesignTimeString.isEmpty()) {
        	deviceModelDesignTime = 
        			DateManager.getSimpleDateValueTaiwan(
        					deviceModelDesignTimeString);
        }
        
        String sensorDataUploadPeriodString = 
        		req.getParameter("sensorDataUploadPeriod");
        Integer sensorDataUploadPeriod = null;
        if (!sensorDataUploadPeriodString.isEmpty()) {
        	sensorDataUploadPeriod = 
        			Integer.parseInt(sensorDataUploadPeriodString);
        }
        
        String deviceModelComments =
        		req.getParameter("deviceModelComments");
        
        String temp1Hum1String = req.getParameter("temp1Hum1");
        Boolean temp1Hum1 = false;
        if (temp1Hum1String != null) {
        	temp1Hum1 = true;
        }
        String temp2Hum2String = req.getParameter("temp2Hum2");
        Boolean temp2Hum2 = false;
        if (temp2Hum2String != null) {
        	temp2Hum2 = true;
        }
        String doorOpenCloseString = req.getParameter("doorOpenClose");
        Boolean doorOpenClose = false;
        if (doorOpenCloseString != null) {
        	doorOpenClose = true;
        }
        String co2String = req.getParameter("co2");
        Boolean co2 = false;
        if (co2String != null) {
        	co2 = true;
        }
        String coString = req.getParameter("co");
        Boolean co = false;
        if (coString != null) {
        	co = true;
        }
        String fluxString = req.getParameter("flux");
        Boolean flux = false;
        if (fluxString != null) {
        	flux = true;
        }
        String infraredString = req.getParameter("infrared");
        Boolean infrared = false;
        if (infraredString != null) {
        	infrared = true;
        }
        String imageUploadString = req.getParameter("imageUpload");
        Boolean imageUpload = false;
        if (imageUploadString != null) {
        	imageUpload = true;
        }
        String alcoholString = req.getParameter("alcohol");
        Boolean alcohol = false;
        if (alcoholString != null) {
        	alcohol = true;
        }
        String electricCurrentString = 
        		req.getParameter("electricCurrent");
        Boolean electricCurrent = false;
        if (electricCurrentString != null) {
        	electricCurrent = true;
        }
        String atmosphericPressureString = 
        		req.getParameter("atmosphericPressure");
        Boolean atmosphericPressure = false;
        if (atmosphericPressureString != null) {
        	atmosphericPressure = true;
        }
        
        // DeviceModelDoors
        String deviceModelDoorCountString = 
        		req.getParameter("doorAmount");
        Integer deviceModelDoorCount = 0;
        if (deviceModelDoorCountString != null && !deviceModelDoorCountString.isEmpty()) {
        	deviceModelDoorCount = Integer.parseInt(deviceModelDoorCountString);
        }
        HashMap<DeviceModelDoor, ArrayList<DeviceModelPartition>> deviceModelDoorPartitions =
        		new HashMap<>();
        for (int i = 1; i <= deviceModelDoorCount; i++) {

        	Integer deviceModelDoorNumber = i;
        	String deviceModelDoorComments = null;
        	
        	DeviceModelDoor deviceModelDoor = 
        			new DeviceModelDoor(
        					deviceModelDoorNumber,
        					deviceModelDoorComments);
        	
        	String[] deviceModelPartitionNames = 
        			req.getParameterValues("deviceModelPartitionNameNo" + i + "[]");
        	
        	// DeviceModelPartitions
            Integer deviceModelPartitionCount = deviceModelPartitionNames.length;
            ArrayList<DeviceModelPartition> deviceModelPartitions = 
            		new ArrayList<>();
            for (int j = 0; j < deviceModelPartitionCount; j++) {
            	String deviceModelPartitionName = deviceModelPartitionNames[j];
            	String deviceModelPartitionComments = null;
            	
            	DeviceModelPartition deviceModelPartition = 
            			new DeviceModelPartition(
            					deviceModelPartitionName,
            					deviceModelPartitionComments
            					);
            	deviceModelPartitions.add(deviceModelPartition);
            }
            
            deviceModelDoorPartitions.put(deviceModelDoor, 
            		deviceModelPartitions);
        }
        
        if (add) {
	        DeviceModel deviceModel = 
	        		new DeviceModel(deviceServiceTypeKey,
	        	    		deviceModelName, 
	        	    		deviceModelDescription,
	        	    		deviceModelDesignTime,
	        	    		sensorDataUploadPeriod,
	        	    		deviceModelComments,
	        	    		temp1Hum1,
	        	    		temp2Hum2,
	        	    		doorOpenClose,
	        	    		co2,
	        	    		co,
	        	    		flux,
	        	    		infrared,
	        	    		imageUpload,
	        	    		alcohol,
	        	    		electricCurrent,
	        	    		atmosphericPressure
	        	    		);
	        DeviceModelManager.putDeviceModel(deviceModel);
	        
	        // Add DeviceModelDoors and DeviceModelPartitions
	        for (DeviceModelDoor deviceModelDoor : 
	        		deviceModelDoorPartitions.keySet()) {
	        	
	        	DeviceModelDoorManager.putDeviceModelDoor(
	        			deviceModel.getKey(), deviceModelDoor);
	        	
	        	for (DeviceModelPartition deviceModelPartition : 
	        			deviceModelDoorPartitions.get(deviceModelDoor)) {
	        		
	        		DeviceModelPartitionManager.putDeviceModelPartition(
	        				deviceModelDoor.getKey(), deviceModelPartition);
	        	}
	        }
        }
        else {
            DeviceModelManager.updateDeviceModelAttributes(
            		key, 
            		deviceServiceTypeKey, 
            		deviceModelName, 
            		deviceModelDescription, 
            		deviceModelDesignTime, 
            		sensorDataUploadPeriod,
            		deviceModelComments, 
            		temp1Hum1, 
            		temp2Hum2, 
            		doorOpenClose, 
            		co2, 
            		co, 
            		flux, 
            		infrared, 
            		imageUpload, 
            		alcohol, 
            		electricCurrent, 
            		atmosphericPressure);
        }
        
        // Update deviceServiceTypeListVersion
        SystemManager.updateDeviceModelListVersion();
    }
    
    /**
     * Add or Update StorageDeviceContainerModel
     * @param req: the HTTPServletRequest
     * @param key: the StorageDeviceContainerModel key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateStorageDeviceContainerModel(HttpServletRequest req, 
    		Long key, boolean add) 
    		throws MissingRequiredFieldsException {
    	
    	String storageDeviceContainerModelName = 
        		req.getParameter("storageDeviceContainerModelName");
        String storageDeviceContainerModelDescription = 
        		req.getParameter("storageDeviceContainerModelDescription");
        String storageDeviceContainerModelVersionNumber = 
        		req.getParameter("storageDeviceContainerModelVersionNumber");
        
        String storageDeviceContainerModelDesignTimeString = 
        		req.getParameter("storageDeviceContainerModelDesignTime");
        Date storageDeviceContainerModelDesignTime = null;
        if (!storageDeviceContainerModelDesignTimeString.isEmpty()) {
        	storageDeviceContainerModelDesignTime = 
        			DateManager.getDateValueISO8601(storageDeviceContainerModelDesignTimeString);
        }
        
        String storageDeviceContainerModelComments =
        		req.getParameter("storageDeviceContainerModelComments");
        
        if (add) {
	        StorageDeviceContainerModel storageDeviceContainerModel = 
	        		new StorageDeviceContainerModel(
	        	    		storageDeviceContainerModelName, 
	        	    		storageDeviceContainerModelDescription,
	        	    		storageDeviceContainerModelVersionNumber,
	        	    		storageDeviceContainerModelDesignTime,
	        	    		storageDeviceContainerModelComments
	        	    		) ;
	        StorageDeviceContainerModelManager.putStorageDeviceContainerModel(storageDeviceContainerModel);
        }
        else {
            StorageDeviceContainerModelManager.updateStorageDeviceContainerModelAttributes(
            		key, 
            		storageDeviceContainerModelName, 
            		storageDeviceContainerModelDescription, 
            		storageDeviceContainerModelVersionNumber, 
            		storageDeviceContainerModelDesignTime, 
            		storageDeviceContainerModelComments
            		);
        }
        
        // Update deviceServiceTypeListVersion
        SystemManager.updateStorageDeviceContainerModelListVersion();
    }
    
    /**
     * Add or Update SensorType
     * @param req: the HTTPServletRequest
     * @param key: the SensorType key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateSensorType(HttpServletRequest req, 
    		Long key, boolean add) 
    		throws MissingRequiredFieldsException {
    	
    	String sensorTypeName = 
        		req.getParameter("sensorTypeName");
        String sensorTypeDescription = 
        		req.getParameter("sensorTypeDescription");
        String sensorTypeUnit = 
        		req.getParameter("sensorTypeUnit");
        String sensorTypeComments =
        		req.getParameter("sensorTypeComments");
        
        if (add) {
	        SensorType sensorType = 
	        		new SensorType(
	        	    		sensorTypeName, 
	        	    		sensorTypeDescription,
	        	    		sensorTypeUnit,
	        	    		sensorTypeComments
	        	    		) ;
	        SensorTypeManager.putSensorType(sensorType);
        }
        else {
            SensorTypeManager.updateSensorTypeAttributes(
            		key, 
            		sensorTypeName, 
            		sensorTypeDescription, 
            		sensorTypeUnit, 
            		sensorTypeComments
            		);
        }
        
        // Update deviceServiceTypeListVersion
        SystemManager.updateSensorTypeListVersion();
    }
    
    /**
     * Add or Update Country
     * @param req: the HTTPServletRequest
     * @param key: the Country key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateCountry(HttpServletRequest req, 
    		Key key, boolean add) 
    		throws MissingRequiredFieldsException {
        
    	String countryName = req.getParameter("countryName");
        String countryComments = req.getParameter("countryComments");
        
        if (add) {
	        Country country = new Country(
	        		countryName,
	        		countryComments);
	        CountryManager.putCountry(country);
        }
        else {
            CountryManager.updateCountryAttributes(
            		key, 
            		countryName, 
            		countryComments);
        }
    }
    
    /**
     * Add or Update Region
     * @param req: the HTTPServletRequest
     * @param key: the Region key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateRegion(HttpServletRequest req, 
    		Key key, boolean add) 
    		throws MissingRequiredFieldsException {
    	
    	String countryKeyString = req.getParameter("countryId");
    	Key countryKey = null;
    	if (countryKeyString != null && !countryKeyString.isEmpty()) {
    		countryKey = KeyFactory.stringToKey(countryKeyString);
    	}
        
    	String regionName = req.getParameter("regionName");
        
        String regionTimeZone = req.getParameter("regionTimeZone");
        
        String regionComments = req.getParameter("regionComments");
        
        if (add) {
	        Region region = new Region(
	        		regionName,
	        		regionTimeZone,
	        		regionComments);
	        RegionManager.putRegion(countryKey, region);
        }
        else {
            RegionManager.updateRegionAttributes(
            		key, 
            		regionName, 
            		regionTimeZone, 
            		regionComments);
        }
    }
    
    /**
     * Add or Update Department
     * @param req: the HTTPServletRequest
     * @param key: the Department key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateDepartment(HttpServletRequest req, 
    		Long key, boolean add) 
    		throws MissingRequiredFieldsException {
    	
    	String departmentName = 
        		req.getParameter("departmentName");
        String departmentDescription = 
        		req.getParameter("departmentDescription");
        String departmentComments =
        		req.getParameter("departmentComments");
        
        if (add) {
        	Department sensorType = 
	        		new Department(
	        				departmentName, 
	        				departmentDescription,
	        				departmentComments
	        	    		) ;
        	DepartmentManager.putDepartment(sensorType);
        }
        else {
        	DepartmentManager.updateDepartmentAttributes(
            		key, 
            		departmentName, 
            		departmentDescription, 
            		departmentComments
            		);
        }
        
        // Update deviceServiceTypeListVersion
        SystemManager.updateDepartmentListVersion();
    }
    
    /**
     * Add or Update AlarmWarning
     * @param req: the HTTPServletRequest
     * @param key: the AlarmWarning key (null if Add)
     * @param add: whether to Add object or not
     * @throws MissingRequiredFieldsException 
     */
    private void addOrUpdateAlarmWarning(HttpServletRequest req, 
    		Key key, boolean add) 
    		throws MissingRequiredFieldsException {
    	
    	String deviceModelKeyString = req.getParameter("deviceModelId");
    	Key deviceModelKey = null;
    	if (deviceModelKeyString != null && !deviceModelKeyString.isEmpty()) {
    		deviceModelKey = KeyFactory.stringToKey(deviceModelKeyString);
    	}
        
    	String alarmWarningCodeString = req.getParameter("alarmWarningCode");
    	Integer alarmWarningCode = null;
    	if (!alarmWarningCodeString.isEmpty()) {
    		alarmWarningCode = Integer.parseInt(alarmWarningCodeString);
    	}
        
        String alarmWarningMessage = req.getParameter("alarmWarningMessage");
        
        String alarmWarningMaxCountString = req.getParameter("alarmWarningMaxCount");
        Integer alarmWarningMaxCount = null;
        if (!alarmWarningMaxCountString.isEmpty()) {
        	alarmWarningMaxCount = Integer.parseInt(alarmWarningMaxCountString);
        }
        
        if (add) {
        	AlarmWarning alarmWarning = new AlarmWarning(
        			alarmWarningCode,
        			alarmWarningMessage,
        			alarmWarningMaxCount);
        	AlarmWarningManager.putAlarmWarning(deviceModelKey, alarmWarning);
        }
        else {
        	AlarmWarningManager.updateAlarmWarningAttributes(
        			key, alarmWarningCode, alarmWarningMessage, alarmWarningMaxCount);
        }
    }

}
