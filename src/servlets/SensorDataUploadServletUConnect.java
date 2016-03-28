/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DateManager;
import datastore.DeviceModel;
import datastore.DeviceModelManager;
import datastore.SensorInstance;
import datastore.SensorInstanceManager;
import datastore.SensorReading;
import datastore.SensorReadingCache;
import datastore.SensorReadingCacheManager;
import datastore.SensorReadingManager;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;

/**
 * This servlet class is used to serve data uploads from a wireless module.
 * The data format is different from the original one. It is used for
 * wireless modules from UConnect.
 * 
 */

@SuppressWarnings("serial")
public class SensorDataUploadServletUConnect extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(SensorDataUploadServletUConnect.class.getName());
    
    // JSP file locations
    private static final String thisServlet = "/sensorDataUpload2";
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	
	    // Lets check the action and parameters returned
	    String status = req.getParameter("status");
	    
	    String response = "";
	    response += "msg=" + status;
	    // Success
	    if (status.equals("success")) {
	    	resp.getWriter().println(response);
	    }
	    // Ignore
	    else if (status.equals("ignore")) {
	    	resp.getWriter().println(response);
	    }
	    // Fail
	    else if (status.equals("fail")){
	    	String failReason = req.getParameter("failReason");
	    	response += "(" + failReason + ")";
	    	resp.getWriter().println(response);
	    }
	    else {
	    	resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
            		"Incorrect value for \"status\" parameter.");
	    }
	}

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
    	
    	// First, get the upload time
    	Date sensorReadingTime = new Date();
    	
        try {
	        // Get content
	        String content = req.getParameter("content");
	        
	        // If no content
	        if (content == null || content.isEmpty()) {
	        	resp.sendRedirect(thisServlet + "?status=fail&failReason=no_content");
	        	return;
	        }
	        
	        content = URLDecoder.decode(content, "UTF-8");
	        
	        String[] contentFields = content.split(",");
	        for (int i = 0; i < contentFields.length; i++) {
	        	contentFields[i] = contentFields[i].trim();
	        }
	        
	        // Device ID is always present
	        String deviceSerialNumber = contentFields[0];
	        // Check if deviceSerialNumber exists
        	if (deviceSerialNumber == null) {
	        	resp.sendRedirect(thisServlet + 
	        			"?status=fail&failReason=device_serial_number_not_found");
	        	return;
        	}

	        StorageDevice storageDevice = 
	        			StorageDeviceManager.getStorageDevice(deviceSerialNumber);
        	// Check if StorageDevice exists
        	if (storageDevice == null) {
	        	resp.sendRedirect(thisServlet + 
	        			"?status=fail&failReason=storage_device_not_found");
	        	return;
        	}

        	// Check if sensor data upload is enabled (for periodic uploads)
        	if (!storageDevice.getEnableSensorDataUpload()) {
	        	resp.sendRedirect(thisServlet + 
	        			"?status=fail&failReason=data_upload_disabled");
	        	return;
        	}
        	
	    	List<SensorInstance> sensorInstances = 
	    			SensorInstanceManager.getAllSensorInstancesFromStorageDevice(
	    					storageDevice.getKey());
	    	
	    	DeviceModel deviceModel = 
	    			DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());
	    	int sensorDataUploadPeriod = deviceModel.getSensorDataUploadPeriod();
        	
        	boolean pass = storeSensorData(
        			contentFields, 
        			sensorInstances, 
        			sensorReadingTime,
        			sensorDataUploadPeriod);
        	if (!pass) {
        		resp.sendRedirect(thisServlet + "?status=ignore");
	        	return;
        	}
	        
	        String message = content;
	        message = URLEncoder.encode(message, "UTF-8");
	        resp.setStatus(HttpServletResponse.SC_OK);
	        
            resp.sendRedirect(thisServlet + "?status=success");
            return;
        }
        catch (IndexOutOfBoundsException ioobe) {
            resp.sendRedirect(thisServlet + "?status=fail&" +
            		"failReason=IndexOutOfBoundsException");
            return;
        }
        catch (MissingRequiredFieldsException mrfe) {
        	resp.sendRedirect(thisServlet + "?status=fail&" +
        			"failReason=MissingRequiredFieldsException");
        	return;
        }
        catch (InvalidFieldFormatException iffe) {
        	resp.sendRedirect(thisServlet + "?status=fail&" +
        			"failReason=InvalidFieldFormatException");
        	return;
		} 
        catch (NumberFormatException nfe) {
        	resp.sendRedirect(thisServlet + "?status=fail&" +
        			"failReason=NumberFormatException");
        	return;
		}
        catch (Exception ex) {
            log.log(Level.SEVERE, ex.toString());
            ex.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            		"Internal server error.");
            return;
        }
    }
    
    /**
     * Stores the data uploaded by a particular StorageDevice
     * @param contentFields
     * @param sensorInstances
     * @param sensorReadingTime
     * @param sensorDataUploadPeriod
     * @throws MissingRequiredFieldsException
     * @throws NumberFormatException
     * @throws InvalidFieldFormatException 
     * @return False is the SensorReadings to store don't comply with the DeviceModel's
     * 			sensorDataUploadPeriod, True otherwise.
     */
    private boolean storeSensorData(String[] contentFields, 
    		List<SensorInstance> sensorInstances, Date sensorReadingTime,
    		int sensorDataUploadPeriod) 
    				throws MissingRequiredFieldsException, NumberFormatException, 
    				InvalidFieldFormatException {
    	
    	// Get SensorInstances
        SensorInstance siT1 = null;
        SensorInstance siH1 = null;
    	for (SensorInstance sensorInstance : sensorInstances) {
    		  		
    		String sensorInstanceLabel = sensorInstance.getSensorInstanceLabel();
    		if (sensorInstanceLabel.equalsIgnoreCase(
    				"Temperature_1")) {
    			
        		// Check if the current SensorReading complies with the
        		// DeviceModel's sensorDataUploadPeriod
    			
    			// First check last SensorReadingCache
        		if (sensorInstance.getLastSensorReadingCache() != null) {
        			SensorReadingCache lastSensorReadingCache = 
        					SensorReadingCacheManager.getLastSensorReadingCacheFromSensorInstance(
        							sensorInstance.getKey());
        			if (lastSensorReadingCache != null) {
        				Date lastSensorReadingTime = 
        						lastSensorReadingCache.getSensorReadingTime();
        				Date sensorReadingTimeMinusPeriod =
        						DateManager.subtractSecondsFromDate(
        								sensorReadingTime, sensorDataUploadPeriod);
        				if (sensorReadingTimeMinusPeriod.compareTo(
        						lastSensorReadingTime) < 0) {
        					return false;
        				}
        			}
        		}
        		// Then check last SensorReading
        		else if (sensorInstance.getLastSensorReading() != null) {
        			SensorReading lastSensorReading = 
        					SensorReadingManager.getLastSensorReadingFromSensorInstance(
        							sensorInstance.getKey(), false);
        			if (lastSensorReading != null) {
        				Date lastSensorReadingTime = 
        						lastSensorReading.getSensorReadingTime();
        				Date sensorReadingTimeMinusPeriod =
        						DateManager.subtractSecondsFromDate(
        								sensorReadingTime, sensorDataUploadPeriod);
        				if (sensorReadingTimeMinusPeriod.compareTo(
        						lastSensorReadingTime) < 0) {
        					return false;
        				}
        			}
        		}
    			
    			siT1 = sensorInstance;
    		}
    		else if (sensorInstanceLabel.equalsIgnoreCase(
    				"Humidity_1")) {
    			siH1 = sensorInstance;
    		}
        }
        
        // Periodic upload [storage_dev1_123,per,+025.5C,0064.0%,XX]
    	String type = contentFields[1];
    	
    	// Check type
    	if (type.equalsIgnoreCase("per")) {
	    	// Only one set of sensors
	        String temp1 = contentFields[2].trim(); // +025.5C%\
	        temp1 = temp1.substring(0, temp1.length() - 1); // Remove last character
	        String hum1 = contentFields[3].trim();  // 0064.0%
	        hum1 = hum1.substring(0, hum1.length() - 1); // Remove last character
	        
	        // Check that sensorReadingValues are valid
	        double temp1Value = Double.parseDouble(temp1);
	        double hum1Value = Double.parseDouble(hum1);
	        // Convert back to String
	        temp1 = String.valueOf(temp1Value);
	        hum1 = String.valueOf(hum1Value);
	        
//	        SensorReading sensorReading1 = 
//	        		new SensorReading(
//	        				siT1.getKey(),
//	        				temp1,
//	        				sensorReadingTime
//	        				);
//	        SensorReading sensorReading2 = 
//	        		new SensorReading(
//	        				siH1.getKey(),
//	        				hum1,
//	        				sensorReadingTime
//	        				);
	        SensorReadingCache sensorReading1 = 
	        		new SensorReadingCache(
	        				siT1.getKey(),
	        				temp1,
	        				sensorReadingTime
	        				);
	        SensorReadingCache sensorReading2 = 
	        		new SensorReadingCache(
	        				siH1.getKey(),
	        				hum1,
	        				sensorReadingTime
	        				);
	        
//	        SensorReadingManager.putSensorReading(siT1.getKey(), 
//	        		sensorReading1);
//	        SensorReadingManager.putSensorReading(siH1.getKey(), 
//	        		sensorReading2);
	        SensorReadingCacheManager.putSensorReadingCache( 
	        		sensorReading1);
	        SensorReadingCacheManager.putSensorReadingCache(
	        		sensorReading2);
    	}

        return true;
    }
}
