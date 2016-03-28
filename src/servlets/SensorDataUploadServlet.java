/*
Copyright (c) 2014, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DateManager;
import util.EmailManager;

import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;

import datastore.AlarmWarning;
import datastore.AlarmWarningManager;
import datastore.AlarmWarningMessage;
import datastore.AlarmWarningMessageManager;
import datastore.Country;
import datastore.CountryManager;
import datastore.Customer;
import datastore.CustomerManager;
import datastore.DeviceModel;
import datastore.DeviceModelDoor;
import datastore.DeviceModelDoorManager;
import datastore.DeviceModelManager;
import datastore.DeviceServiceType;
import datastore.DeviceServiceTypeManager;
import datastore.Region;
import datastore.RegionManager;
import datastore.SensorInstance;
import datastore.SensorReadingCache;
import datastore.SensorReadingCacheManager;
import datastore.Customer.Status;
import datastore.SensorInstance.SensorStatus;
import datastore.SensorInstanceManager;
import datastore.SensorReading;
import datastore.SensorReadingManager;
import datastore.SensorType;
import datastore.SensorTypeManager;
import datastore.StorageDevice;
import datastore.StorageDeviceDoor;
import datastore.StorageDeviceDoorManager;
import datastore.StorageDeviceManager;
import datastore.User;
import datastore.User.UserType;
import exceptions.InvalidFieldFormatException;
import exceptions.MissingRequiredFieldsException;
import exceptions.ObjectExistsInDatastoreException;

/**
 * This servlet class is used to serve data uploads from a wireless module.
 * 
 */

@SuppressWarnings("serial")
public class SensorDataUploadServlet extends HttpServlet {

    private static final Logger log = 
        Logger.getLogger(SensorDataUploadServlet.class.getName());
    
    // JSP file locations
    private static final String thisServlet = "/sensorDataUpload";
    
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
	    // Delete expired sensor readings
	    else if (status.equals("deleteExpired")) {
	    	
	    	// Check whether to delete all expired data or not
	    	String storageDeviceSerialNumber = req.getParameter("serialNumber");
	    	if (storageDeviceSerialNumber != null && !storageDeviceSerialNumber.isEmpty()) {
		    	StorageDevice storageDevice = 
		    			StorageDeviceManager.getStorageDevice(storageDeviceSerialNumber);
		    	CronServlet.deleteSensorData(storageDevice.getKey());
	    	}
	    	else {
	    		CronServlet.deleteSensorData();
	    	}
	    	
	    	resp.getWriter().println(response);
	    }
	    // Delete all sensor readings from a StorageDevice
	    else if (status.equals("deleteAll")) {
	    	
	    	String storageDeviceSerialNumber = req.getParameter("serialNumber");
	    	StorageDevice storageDevice = 
	    			StorageDeviceManager.getStorageDevice(storageDeviceSerialNumber);
	    	SensorReadingManager.deleteAllSensorReadings(storageDevice.getKey());
	    	
	    	resp.getWriter().println(response);
	    }
	    // Delete all AlarmWarningMessages
	    else if (status.equals("delete_alarm_warning_messages_all")) {
	    	
	    	CronServlet.deleteAlarmMessages(true);
	    	
	    	resp.getWriter().println(response);
	    }
	    // Delete expired AlarmWarningMessages
	    else if (status.equals("delete_alarm_warning_messages")) {
	    	
	    	CronServlet.deleteAlarmMessages(false);
	    	
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
	        
	        // Device ID and Type are always present
	        String deviceSerialNumber = contentFields[0];
	        // Check if deviceSerialNumber exists
        	if (deviceSerialNumber == null) {
	        	resp.sendRedirect(thisServlet + "?status=fail&failReason=device_serial_number_not_found");
	        	return;
        	}
	        
	        // Create Customer, Region, Country, DeviceServiceType, DeviceModel, DeviceModelDoor, SensorType
	        // StorageDevice, StorageDeviceDoor, and SensorInstance if it is an initial upload
	        // Otherwise, use existing ones
        	String initialUpload = req.getParameter("initialUpload");
	        StorageDevice storageDevice;
	        if (initialUpload == null) {
	        	storageDevice = 
	        			StorageDeviceManager.getStorageDevice(deviceSerialNumber);
	        	
	        	// Check if StorageDevice exists
	        	if (storageDevice == null) {
		        	resp.sendRedirect(thisServlet + "?status=fail&failReason=storage_device_not_found");
		        	return;
	        	}
	        }
	        else {
	        	storageDevice = createTestingDatastore();
	        }
	        
	        // Check type
	        String type = contentFields[1];
	        // Periodic or aperiodic upload
	        if (type.equalsIgnoreCase("per") || type.equalsIgnoreCase("aper")) {
	        	
	        	// Check if sensor data upload is enabled (for periodic uploads)
	        	if (type.equalsIgnoreCase("per") && !storageDevice.getEnableSensorDataUpload()) {
		        	resp.sendRedirect(thisServlet + "?status=fail&failReason=data_upload_disabled");
		        	return;
	        	}
	        	
		    	List<SensorInstance> sensorInstances = 
		    			SensorInstanceManager.getAllSensorInstancesFromStorageDevice(
		    					storageDevice.getKey());
		    	
		    	DeviceModel deviceModel = 
		    			DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());
		    	int sensorDataUploadPeriod = deviceModel.getSensorDataUploadPeriod();
	        	
	        	boolean pass = storeSensorData(type, contentFields, sensorInstances, sensorReadingTime,
	        			sensorDataUploadPeriod);
	        	if (!pass) {
	        		resp.sendRedirect(thisServlet + "?status=ignore");
		        	return;
	        	}
	        }
	        // Warning
	        else if (type.equalsIgnoreCase("warn")) {
	        	uploadAlarmWarningMessage(storageDevice, contentFields);
	        }
	        // No more types
	        else {
	        	resp.sendRedirect(thisServlet + "?status=fail&failReason=wrong_type");
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
        catch (ObjectExistsInDatastoreException oeide) {
        	resp.sendRedirect(thisServlet + "?status=fail&" +
        			"failReason=ObjectExistsInDatastoreException");
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
     * @param type
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
    private boolean storeSensorData(String type, String[] contentFields, 
    		List<SensorInstance> sensorInstances, Date sensorReadingTime,
    		int sensorDataUploadPeriod) 
    				throws MissingRequiredFieldsException, NumberFormatException, 
    				InvalidFieldFormatException {
    	
    	// Get SensorInstances
        SensorInstance siT1 = null;
        SensorInstance siH1 = null;
        SensorInstance siT2 = null;
        SensorInstance siH2 = null;
        HashMap<Integer, SensorInstance> doorSensorInstances = new HashMap<>();

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
        		else if (sensorInstance.getLastSensorReading() != null && 
        				type.equalsIgnoreCase("per")) {
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
    				"Temperature_2")) {
    			siT2 = sensorInstance;
    		}
    		else if (sensorInstanceLabel.equalsIgnoreCase(
    				"Humidity_1")) {
    			siH1 = sensorInstance;
    		}
    		else if (sensorInstanceLabel.equalsIgnoreCase(
    				"Humidity_2")) {
    			siH2 = sensorInstance;
    		}
    		else if (sensorInstanceLabel.startsWith(
    				"Door_")) {
    			int doorNumber = 
    					Integer.parseInt(sensorInstanceLabel.substring(
    							sensorInstanceLabel.indexOf("_") + 1));
    			doorSensorInstances.put(doorNumber, sensorInstance);
    		}
        }
        
        // Periodic upload [storage_dev1_123, per, 21.70, 20.14, 22.01, 20.19]
        if (type.equalsIgnoreCase("per")) {
        	// The first set of sensors is mandatory
	        String temp1 = contentFields[2].trim();
	        String hum1 = contentFields[3].trim();
	        
	        // Check that sensorReadingValues are valid
	        Double.parseDouble(temp1);
	        Double.parseDouble(hum1);
	        
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
	        SensorReadingCacheManager.putSensorReadingCache(sensorReading1);
	        SensorReadingCacheManager.putSensorReadingCache(sensorReading2);

	        // The second set of sensors is optional
	        if (contentFields.length >= 6) {
		        String temp2 = contentFields[4].trim();
		        String hum2 = contentFields[5].trim();
		        
		        // Check that sensorReadingValues are valid
		        Double.parseDouble(temp2);
		        Double.parseDouble(hum2);
		        
//		        SensorReading sensorReading3 = 
//		        		new SensorReading(
//		        				siT2.getKey(),
//		        				temp2,
//		        				sensorReadingTime
//		        				);
//		        SensorReading sensorReading4 = 
//		        		new SensorReading(
//		        				siH2.getKey(),
//		        				hum2,
//		        				sensorReadingTime
//		        				);
		        SensorReadingCache sensorReading3 = 
		        		new SensorReadingCache(
		        				siT2.getKey(),
		        				temp2,
		        				sensorReadingTime
		        				);
		        SensorReadingCache sensorReading4 = 
		        		new SensorReadingCache(
		        				siH2.getKey(),
		        				hum2,
		        				sensorReadingTime
		        				);
		        
//		        SensorReadingManager.putSensorReading(siT2.getKey(), 
//		        		sensorReading3);
//		        SensorReadingManager.putSensorReading(siH2.getKey(), 
//		        		sensorReading4);
		        SensorReadingCacheManager.putSensorReadingCache(sensorReading3);
		        SensorReadingCacheManager.putSensorReadingCache(sensorReading4);
        	}
        }
        // Aperiodic upload [storage_dev1_123, aper, 2, open]
        else if (type.equalsIgnoreCase("aper")) {
        	String doorNumberString = contentFields[2].trim();
        	Integer doorNumber = Integer.parseInt(doorNumberString);
        	SensorInstance sensorInstance = doorSensorInstances.get(doorNumber);
        	Key storageDeviceDoorKey = sensorInstance.getStorageDeviceDoor();
        	
        	String doorStatus = contentFields[3].trim();
        	
	        // Check that sensorReadingValue is valid
	        if (!doorStatus.equalsIgnoreCase("open") && !doorStatus.equalsIgnoreCase("close")) {
	        	throw new InvalidFieldFormatException(this.getClass(), 
	        			"Value of doorStatus must be either \"close\" or \"open\".");
	        }
        	
        	SensorReading sensorReading5 =
	        		new SensorReading(
	        				sensorInstance.getKey(),
	        				doorStatus,
	        				sensorReadingTime
	        				);

        	SensorReadingManager.putSensorReading(sensorInstance.getKey(), 
        			sensorReading5);
        	
        	// Open or Close StorageDeviceDoor
        	if (doorStatus.equalsIgnoreCase("open")) {
        		StorageDeviceDoorManager.openOrCloseStorageDeviceDoor(
        				storageDeviceDoorKey, true);
        	}
        	else {
        		StorageDeviceDoorManager.openOrCloseStorageDeviceDoor(
        				storageDeviceDoorKey, false);
        	}
        }
        
        return true;
    }
    
    /**
     * Uploads an AlarmWarningMessage from a StorageDevice sensor module
     * @param storageDeviceKey: the key of the corresponding StorageDevice
     * @param contentFields
     * @throws NumberFormatException
     * @throws MissingRequiredFieldsException 
     * @throws MessagingException 
     * @throws UnsupportedEncodingException 
     */
    private void uploadAlarmWarningMessage(StorageDevice storageDevice, 
    		String[] contentFields)
    		throws NumberFormatException, MissingRequiredFieldsException, 
    		MessagingException, UnsupportedEncodingException {
    	
        String dehumidifierMachineString = contentFields[2].trim();
        Integer dehumidifierMachine = Integer.parseInt(dehumidifierMachineString);
        
        String alarmWarningCodeString = contentFields[3].trim();
        Integer alarmWarningCode = Integer.parseInt(alarmWarningCodeString);
        
        AlarmWarning alarmWarning = 
        		AlarmWarningManager.getAlarmWarningFromDeviceModelWithCode(
        				storageDevice.getDeviceModel(), alarmWarningCode);
        
        AlarmWarningMessage alarmWarningMessage = new AlarmWarningMessage(
        		storageDevice.getKey(),
        		dehumidifierMachine);
        AlarmWarningMessageManager.putAlarmWarningMessage(alarmWarning.getKey(), 
        		alarmWarningMessage);
        
        // Send AlarmWarningMessage emails
        if (alarmWarning.getAlarmWarningCount() <= 1 && 
        		alarmWarning.getAlarmWarningMaxCount() > 1) {
			String subject = 
					"Alarm warning in Storage Device \"" + 
							storageDevice.getStorageDeviceSerialNumber() + 
							"\" (Code: " + alarmWarningCode + ")";
			
			String body = subject + "\n";
			body += DateManager.printDateAsString(
					alarmWarningMessage.getAlarmWarningMessageCreationDate()) + "\n";
			body += alarmWarning.getAlarmWarningMessage() + "\n";
			
			EmailManager.sendAlarmMessageEmails(subject, body);
        }
    }
    
    /**
     * Creates instances of main Global types in datastore if the
     * datastore is empty.
     * @return The testing StorageDevice that was created
     * @throws MissingRequiredFieldsException 
     * @throws InvalidFieldFormatException 
     * @throws ObjectExistsInDatastoreException 
     */
    private StorageDevice createTestingDatastore() 
    		throws MissingRequiredFieldsException, 
    		InvalidFieldFormatException, 
    		ObjectExistsInDatastoreException {
    	Country c = new Country("Taiwan", "");
    	CountryManager.putCountry(c);
    	
    	Region r = new Region(
    			"Hsinchu", 
    			"Asia/Taipei",
        		"");
    	RegionManager.putRegion(c.getKey(), r);
    	
    	User u = new User(
    			new Email("customer@smasrv.com"), 
    			"customer", 
    			UserType.CUSTOMER
    			);
    	Customer cu = new Customer(
    			null,
    			u,
        		"Customer",
        		"Test",
        		new PhoneNumber("0975314927"),
        		r.getKey(),
        		new PostalAddress("Hsinchu City"),
        		null,
        		null,
        		Status.ACTIVE,
        		"");
    	CustomerManager.putCustomer(cu);
    	
    	DeviceServiceType dst = new DeviceServiceType(
    			"Device Service Type test", 
        		"Test",
        		"");
    	DeviceServiceTypeManager.putDeviceServiceType(dst);
    	
    	DeviceModel dm = new DeviceModel(
    			dst.getKey(),
        		"Device Model test", 
        		"Test",
        		new Date(),
        		60,
        		"",
        		true,
        		true,
        		true,
        		false,
        		false,
        		false,
        		false,
        		false,
        		false,
        		false,
        		false);
    	DeviceModelManager.putDeviceModel(dm);
    	
    	DeviceModelDoor dmd1 = new DeviceModelDoor(
    			1, 
        		""
    			);
    	DeviceModelDoor dmd2 = new DeviceModelDoor(
    			2, 
        		""
    			);
    	DeviceModelDoor dmd3 = new DeviceModelDoor(
    			3, 
        		""
    			);
    	DeviceModelDoorManager.putDeviceModelDoor(dm.getKey(), dmd1);
    	DeviceModelDoorManager.putDeviceModelDoor(dm.getKey(), dmd2);
    	DeviceModelDoorManager.putDeviceModelDoor(dm.getKey(), dmd3);
    	
    	SensorType stT = new SensorType(
    			"Temperature", 
        		"Temperature",
        		"Degrees Celcius",
        		""
    			);
    	SensorType stH = new SensorType(
    			"Humidity", 
        		"Humidity",
        		"Percentage",
        		""
    			);
    	SensorType stDOC = new SensorType(
    			"Door Open/Close", 
        		"Door Open/Close",
        		"Boolean",
        		""
    			);
    	SensorTypeManager.putSensorType(stT);
    	SensorTypeManager.putSensorType(stH);
    	SensorTypeManager.putSensorType(stDOC);
    	
    	StorageDevice sd = new StorageDevice(
    			dm.getKey(), 
        		"123",
        		"Storage Device test",
        		"Test", 
        		"123",
        		new Date(), 
        		new Date(),
        		30,
        		30,
        		true,
        		0,
        		""
    			);
    	StorageDeviceManager.putStorageDevice(cu.getKey(), sd);
    	
    	StorageDeviceDoor sdd1 = new StorageDeviceDoor(
    			dmd1.getKey(), 
        		"Door_1"
    			);
    	StorageDeviceDoor sdd2 = new StorageDeviceDoor(
    			dmd2.getKey(), 
        		"Door_2"
    			);
    	StorageDeviceDoor sdd3 = new StorageDeviceDoor(
    			dmd3.getKey(), 
        		"Door_3"
    			);
    	StorageDeviceDoorManager.putStorageDeviceDoor(sd.getKey(), sdd1);
    	StorageDeviceDoorManager.putStorageDeviceDoor(sd.getKey(), sdd2);
    	StorageDeviceDoorManager.putStorageDeviceDoor(sd.getKey(), sdd3);
    	
    	SensorInstance siT1 = new SensorInstance(
    			stT.getKey(),
        		null,
        		"Temperature_1",
        		SensorStatus.OK,
        		""
    			);
    	SensorInstance siH1 = new SensorInstance(
    			stH.getKey(),
        		null,
        		"Humidity_1",
        		SensorStatus.OK,
        		""
    			);
    	SensorInstance siT2 = new SensorInstance(
    			stT.getKey(),
        		null,
        		"Temperature_2",
        		SensorStatus.OK,
        		""
    			);
    	SensorInstance siH2 = new SensorInstance(
    			stH.getKey(),
        		null,
        		"Humidity_2",
        		SensorStatus.OK,
        		""
    			);
    	SensorInstance siD1 = new SensorInstance(
    			stDOC.getKey(),
        		sdd1.getKey(),
        		"Door_1",
        		SensorStatus.OK,
        		""
    			);
    	SensorInstance siD2 = new SensorInstance(
    			stDOC.getKey(),
        		sdd2.getKey(),
        		"Door_2",
        		SensorStatus.OK,
        		""
    			);
    	SensorInstance siD3 = new SensorInstance(
    			stDOC.getKey(),
        		sdd3.getKey(),
        		"Door_3",
        		SensorStatus.OK,
        		""
    			);
    	
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siT1);
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siH1);
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siT2);
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siH2);
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siD1);
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siD2);
    	SensorInstanceManager.putSensorInstance(sd.getKey(), siD3);
    	
    	return sd;
    }
}
