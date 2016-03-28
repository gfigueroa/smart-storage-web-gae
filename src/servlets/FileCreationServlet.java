/*
Copyright (c) 2011, 智慧人科技服務股份有限公司 (Smart Personalized Service Technology, Inc.) 
All rights reserved.
*/

package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DateManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import datastore.DeviceModel;
import datastore.DeviceModelManager;
import datastore.SensorInstance;
import datastore.SensorInstanceManager;
import datastore.SensorReading;
import datastore.SensorReadingManager;
import datastore.SensorReadingManager.Period;
import datastore.StorageDevice;
import datastore.StorageDeviceManager;

/**
 * This servlet class is used to create text files for download
 * from an HTTP Request
 * 
 */

public class FileCreationServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4863261420598524861L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {
        
		// Create buffer that will save text
		StringBuffer buffer = new StringBuffer();
		
		// Check type
		String type = req.getParameter("type");
		
		byte[] bytes = null;
		// Sensor Data
		if (type.equalsIgnoreCase("sensorData")) {
			bytes = createSensorDataFile(req, resp, buffer);
		}
  		// No more choices
  		else {
  			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
  			return;
  		}

		// Display "Save File" window
		resp.getOutputStream().write(bytes, 0, bytes.length);
    }
	
	/**
	 * Creates a CSV file with sensor data corresponding to a specific StorageDevice.
	 * The format of the CSV file is H1,T1,H2,T2,date/time(YYYY/MM/DD HH:MM:SS)
	 * @param req
	 * @param resp
	 * @param buffer
	 * @return an array of bytes with the text to save to the CSV file
	 */
	private byte[] createSensorDataFile(HttpServletRequest req, 
			HttpServletResponse resp,
			StringBuffer buffer) {
		
		// Get StorageDevice information
		String storageDeviceKeyString = req.getParameter("storageDeviceId");
		Key storageDeviceKey = KeyFactory.stringToKey(storageDeviceKeyString);
		StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(storageDeviceKey);

		DeviceModel deviceModel = 
				DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());
		int sensorDataUploadPeriod = deviceModel.getSensorDataUploadPeriod();
		
		// Generate Header
		buffer.append("***Humidity,Temperature,Humidity2,Temperature2,TIME***\n");
		buffer.append("Interval=" + sensorDataUploadPeriod + " Seconds\n");
		
		// Get period
		String periodString = req.getParameter("period");
		Period period = SensorReadingManager.getPeriodFromString(periodString);

		// Get sensor data for tables and charts
		List<SensorInstance> sensorInstances = 
				SensorInstanceManager.getAllSensorInstancesFromStorageDevice(storageDeviceKey);
		List<SensorReading> temp1SensorReadings = null;
		List<SensorReading> hum1SensorReadings = null;
		List<SensorReading> temp2SensorReadings = null;
		List<SensorReading> hum2SensorReadings = null;
		for (SensorInstance sensorInstance : sensorInstances) {
			if (sensorInstance.getSensorInstanceLabel().equals("Temperature_1")) {
				temp1SensorReadings = 
						SensorReadingManager.getSensorReadingsFromSensorInstance(
								sensorInstance.getKey(), period);
			}
			else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_1")) {
				hum1SensorReadings = 
						SensorReadingManager.getSensorReadingsFromSensorInstance(
								sensorInstance.getKey(), period);
			}
			else if (sensorInstance.getSensorInstanceLabel().equals("Temperature_2")) {
				temp2SensorReadings = 
						SensorReadingManager.getSensorReadingsFromSensorInstance(
								sensorInstance.getKey(), period);
			}
			else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_2")) {
				hum2SensorReadings = 
						SensorReadingManager.getSensorReadingsFromSensorInstance(
								sensorInstance.getKey(), period);
			}
		}
		
		// Generate strings H1,T1,H2,T2,date/time(YYYY/MM/DD HH:MM:SS)
		List<String> sensorReadings = new ArrayList<String>();
		int listSize1 = 0;
		if (temp1SensorReadings != null && hum1SensorReadings != null) {
			listSize1 = 
					temp1SensorReadings.size() <= hum1SensorReadings.size() ? 
							temp1SensorReadings.size() : hum1SensorReadings.size();
			for (int i = 0; i < listSize1; i++) {
				String hum1Temp1SensorReading = 
						hum1SensorReadings.get(i).getSensorReadingValue() + "," +
						temp1SensorReadings.get(i).getSensorReadingValue();
				if (temp2SensorReadings == null || temp2SensorReadings.isEmpty()) {
					hum1Temp1SensorReading += "," + DateManager.printDateAsStringDrStorage(
							hum1SensorReadings.get(i).getSensorReadingTime());
				}
				sensorReadings.add(hum1Temp1SensorReading);
			}
		}
		if (temp2SensorReadings != null && hum2SensorReadings != null) {
			int listSize = 
					temp2SensorReadings.size() <= hum2SensorReadings.size() ? 
							temp2SensorReadings.size() : hum2SensorReadings.size();
			for (int i = 0; i < listSize && i < listSize1 ; i++) {
				String hum2Temp2SensorReading = 
						"," + hum2SensorReadings.get(i).getSensorReadingValue() + "," +
						temp2SensorReadings.get(i).getSensorReadingValue() + "," + 
						DateManager.printDateAsStringDrStorage(
								temp2SensorReadings.get(i).getSensorReadingTime());
				String oldReading = sensorReadings.get(i);
				sensorReadings.set(i, oldReading + hum2Temp2SensorReading);
			}
		}
		
		for (String sensorReading : sensorReadings) {
			buffer.append(sensorReading + "\n");
		}
		
		byte[] bytes = buffer.toString().getBytes();
		
		// This will suggest a filename for the browser to use (serialNumber.csv)
		resp.addHeader("Content-Disposition", 
				"attachment; filename=\"" + storageDevice.getStorageDeviceSerialNumber() +
				".csv\"");
		
		return bytes;
	}

}