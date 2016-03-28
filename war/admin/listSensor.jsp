<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.DeviceModel" %>
<%@ page import="datastore.DeviceModelManager" %>
<%@ page import="datastore.SensorInstance" %>
<%@ page import="datastore.SensorInstanceManager" %>
<%@ page import="datastore.SensorReading" %>
<%@ page import="datastore.SensorReadingManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.StorageDeviceDoor" %>
<%@ page import="datastore.StorageDeviceDoorManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>SMASRV IoT</title>
<style type="text/css">

</style>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/listSensor.js"></script>
</head>

<%
	Date now = new Date();

User sessionUser = (User)session.getAttribute("user");

if (sessionUser == null) {
	response.sendRedirect("../login.jsp");
}
else {
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR
		&& sessionUser.getUserType() != User.UserType.CUSTOMER
		&& sessionUser.getUserType() != User.UserType.CUSTOMER_USER) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

List<StorageDevice> storageDevices;
switch (sessionUser.getUserType()) {
	case ADMINISTRATOR:
		storageDevices = StorageDeviceManager.getAllStorageDevices();
		break;
	case CUSTOMER:
		storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomer(sessionUser.getKey().getParent());
		break;
	case CUSTOMER_USER:
		storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomer(sessionUser.getKey().getParent().getParent());
		break;
	default:
		storageDevices = null;
		break;
}

String showAllString = request.getParameter("all");
boolean showAll = false;
if (showAllString != null) {
	showAll = Boolean.parseBoolean(showAllString);
}
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="70%" height="50" valign="middle">Sensor List <%=showAll ? "(all data)" : "(active data)"%>
      </td>
    <td width="3%" style="padding-top:3px;" valign="middle"><a href="?all=<%if(request.getParameter("all") != null && request.getParameter("all").equals("false")){ out.print("true"); } else if(request.getParameter("all") != null && request.getParameter("all").equals("true")){ out.print("false"); } else { out.print("true"); }%>"><img src="../images/plus.png" width="20" height="20" alt=""/></a></td>
    <td width="27%" align="left" valign="middle"> <a style="color:#333;" href="?all=<%if(request.getParameter("all") != null && request.getParameter("all").equals("false")){ out.print("true"); } else if(request.getParameter("all") != null && request.getParameter("all").equals("true")){ out.print("false"); } else { out.print("true"); }%>"><%=showAll ? "Hide expired" : "Show all"%> data</a></td>
    </tr>
</table>
  </div>
  
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td width="20%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue radiusLeft">Device Serial No.</td>
      <td width="20%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue">1. Temp./Hum.</td>
      <td width="20%" align="center" valign="middle" class="listTitle_CSS form_underline_blue">2. Temp./Hum.</td>
      <td width="20%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue">Door 1</td>
      <td width="20%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue radiusRight">Detail</td>
      </tr>
      <%
      	for (StorageDevice storageDevice : storageDevices) {
                	  DeviceModel deviceModel = DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());
                	  List<SensorInstance> sensorInstances = SensorInstanceManager.getAllSensorInstancesFromStorageDevice(storageDevice.getKey());
                	  SensorReading temp1Reading = null;
                	  SensorReading hum1Reading = null;
                	  SensorReading temp2Reading = null;
                	  SensorReading hum2Reading = null;
                	  SensorReading door1Reading = null;
                	  boolean isExpired = false;
                	  for (SensorInstance sensorInstance : sensorInstances) {
                		  if (sensorInstance.getSensorInstanceLabel().equals("Temperature_1")) {
                			  temp1Reading = SensorReadingManager.getLastSensorReadingFromSensorInstance(sensorInstance.getKey(), true);
                			  if (!showAll && deviceModel.lastSensorDataUploadIsExpired(now, temp1Reading)) {
                		  	  	  isExpired = true;
                		  	  	  break;
                		  	  }
                		  }
                		  else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_1")) {
                			  hum1Reading = SensorReadingManager.getLastSensorReadingFromSensorInstance(sensorInstance.getKey(), true);
                			  if (!showAll && deviceModel.lastSensorDataUploadIsExpired(now, hum1Reading)) {
                		  	  	  isExpired = true;
                		  	  	  break;
                		  	  }
                		  }
                		  else if (sensorInstance.getSensorInstanceLabel().equals("Temperature_2")) {
                			  temp2Reading = SensorReadingManager.getLastSensorReadingFromSensorInstance(sensorInstance.getKey(), true);
                		  }
                		  else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_2")) {
                			  hum2Reading = SensorReadingManager.getLastSensorReadingFromSensorInstance(sensorInstance.getKey(), true);
                		  }
                		  else if (sensorInstance.getSensorInstanceLabel().equals("Door_1")) {
                			  door1Reading = SensorReadingManager.getLastSensorReadingFromSensorInstance(sensorInstance.getKey(), true);
                		  }
                	  }
                	  
                	  if (isExpired) {
                	  	  continue;
                	  }

                	  String temp1String = temp1Reading != null && !deviceModel.lastSensorDataUploadIsExpired(now, temp1Reading) ? temp1Reading.getSensorReadingValue() + "°C" : "-";
                	  String hum1String = hum1Reading != null && !deviceModel.lastSensorDataUploadIsExpired(now, hum1Reading) ? hum1Reading.getSensorReadingValue() + "%" : "-";
                	  String temp2String = temp2Reading != null && !deviceModel.lastSensorDataUploadIsExpired(now, temp2Reading) ? temp2Reading.getSensorReadingValue() + "°C" : "-";
                	  String hum2String = hum2Reading != null && !deviceModel.lastSensorDataUploadIsExpired(now, hum2Reading) ? hum2Reading.getSensorReadingValue() + "%" : "-";
                	  String door1String = door1Reading != null ? door1Reading.getSensorReadingValue() : "-";
                	  
                	  String temp1Hum1String = !temp1String.isEmpty() ? temp1String + " / " + hum1String : "-";
                	  String temp2Hum2String = !temp2String.isEmpty() ? temp2String + " / " + hum2String : "-";
      %>
	      <tr class="listCotent_CSS">
	      <td height="30" align="center" class="font_18 form_underline_gray"><%= storageDevice.getStorageDeviceSerialNumber() %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><%= temp1Hum1String %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><%= temp2Hum2String %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><%= door1String %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><a href="listSensorData.jsp?k=<%= KeyFactory.keyToString(storageDevice.getKey()) %>&period=day"><img src="../images/thermograph.png" width="28" height="26" title="View Humi&Temp"></a>
	      <% 
	      List<StorageDeviceDoor> storageDeviceDoors = StorageDeviceDoorManager.getAllStorageDeviceDoorsFromStorageDevice(storageDevice.getKey());
	      if (!storageDeviceDoors.isEmpty()) {
	      %>
	      	<a href="listDoorData.jsp?k=<%= KeyFactory.keyToString(storageDevice.getKey()) %>"><img src="../images/door.png" width="24" height="24" title="View door"></a>
	      <%
	      }
	      %>
	      </td>
	      </tr>
	  <%
      }
	  %>
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>