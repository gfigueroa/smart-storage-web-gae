<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.SensorInstance" %>
<%@ page import="datastore.SensorInstanceManager" %>
<%@ page import="datastore.SensorReading" %>
<%@ page import="datastore.SensorReadingManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.StorageDeviceDoor" %>
<%@ page import="datastore.StorageDeviceDoorManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.DateManager"  %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>SMASRV IoT</title>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/jquery.datetimepicker.css" rel="stylesheet" type="text/css">

<script src="../javascript/selectDate.js" type="text/javascript">
</script>
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
<script src="../javascript/listDoorData.js"></script>

</head>

<%
User sessionUser = (User)session.getAttribute("user");

if (sessionUser == null) {
	response.sendRedirect("../login.jsp");
}
else {
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR
		&&sessionUser.getUserType() != User.UserType.CUSTOMER
		&& sessionUser.getUserType() != User.UserType.CUSTOMER_USER) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

String storageDeviceKeyString = request.getParameter("k");
Key storageDeviceKey = KeyFactory.stringToKey(storageDeviceKeyString);
StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(storageDeviceKey);

// Get door number
String doorNumberString = request.getParameter("doorNumber");
int doorNumber = 1;
if (doorNumberString != null) {
	doorNumber = Integer.parseInt(doorNumberString);
}

// Get doors
List<StorageDeviceDoor> storageDeviceDoors = StorageDeviceDoorManager.getAllStorageDeviceDoorsFromStorageDevice(storageDeviceKey);

// Get sensor data
List<SensorInstance> sensorInstances = SensorInstanceManager.getAllSensorInstancesFromStorageDevice(storageDeviceKey);
List<SensorReading> doorReadings = null;
for (SensorInstance sensorInstance : sensorInstances) {
	if (sensorInstance.getSensorInstanceLabel().equals("Door_" + doorNumber)) {
		doorReadings = SensorReadingManager.getAllSensorReadingsFromSensorInstance(sensorInstance.getKey(), (long) 1000);
		break;
	}
}

// Calculate open times
Long lastTimeOpen = 0L;
String lastValue = "";
ArrayList<Long> openTimes = new ArrayList<Long>();
for (int i = doorReadings.size() - 1; i >= 0; i--) {
	SensorReading doorReading = doorReadings.get(i);
	
	Long thisTime = doorReading.getSensorReadingTime().getTime();
	String thisValue = doorReading.getSensorReadingValue();
	
	Long openTime = 0L;
	if (thisValue.equalsIgnoreCase("open")) {
		lastTimeOpen = thisTime;
	}
	else if (thisValue.equalsIgnoreCase("close") && lastValue.equalsIgnoreCase("open")) {
		openTime = thisTime - lastTimeOpen;
		lastTimeOpen = 0L;
	}
	
	openTimes.add(openTime);
	
	lastValue = thisValue;
}
Collections.reverse(openTimes);
%>

<body>
<%@include file="../header/header.jsp" %>

<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" valign="middle">Door Data List</td>
    </tr>
</table>
  </div>
  <form name="DeviceSearchForm" method="post" action="">
  <table width="800" border="0" align="center" cellpadding="1" cellspacing="0">
  <tr>
    <td height="48" colspan="4" scope="row"><table border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <th width="354" height="41" scope="row"> <input name="deviceSearch" type="text" class="textfield_style" id="deviceSearch" placeholder="Search Door Data">
        </th>
        <td width="40" height="41" align="center" valign="bottom" style="background-color:#D8D8D8;border: 0px solid #CCC;cursor:pointer;"><label for="searchButton"><img src="../images/search-icon-md.png" alt="" width="30" height="30" style="cursor:pointer;"></label></td>
      </tr>
    </table></td>
    </tr>
  <tr>
    <td height="15" colspan="4" class="form_underline" scope="row"></td>
    </tr>
  <tr>
    <th height="42" colspan="4" scope="row"><input type="submit" name="searchButton" id="searchButton" value="送出" style="display:none;"></th>
    </tr>
  <tr>
    <td width="20%" height="65" align="right" scope="row"><label for="doorStartTime">Start Date/Time：</label></td>
    <td width="32%" height="65">
      <input type="text" name="doorStartTime" id="doorStartTime" class="search_textfield_style" placeholder="Choose Start Date/Time"></td>
      
    <td width="20%" height="65" align="right"><label for="doorDayCount">Day Count：</label></td>
    <td height="65"><input type="text" name="doorDayCount" id="doorDayCount" class="search_textfield_style" placeholder="Input Day Count"></td>
  </tr>
  <tr>
    <td width="20%" height="65" align="right" scope="row"><label for="doorEndTime">End Date/Time：</label></td>
    <td width="32%" height="65"><input type="text" name="doorEndTime" id="doorEndTime" class="search_textfield_style" placeholder="Choose End Date/Time"></td>
    <td width="20%" height="65" align="right"><label for="doorDataCount">Data count：</label></td>
    <td height="65"><input type="text" name="doorDataCount" id="doorDataCount" class="search_textfield_style" placeholder="Input data point count"></td>
  </tr>
  <tr>
  <td width="20%" height="65" align="right"><label for="chooseTheDoor">Door：</label></td>
    <td height="65">
      <select name="doorNumberList" id="doorNumberList" class="search_textfield_style" onChange='changeDoorNumber("<%= storageDeviceKeyString %>")'>
      	<%
      	for (int i = 1; i <= storageDeviceDoors.size(); i++) {
      	%>
        	<option value="<%= i %>" <%= doorNumber == i ? "selected" : "" %>><%= "Door " + i %></option>
        <%
      	}
        %>
      </select></td>
    <td width="20%" height="65" align="right" scope="row">&nbsp;</td>
    <td width="32%" height="65">&nbsp;</td>
    
  </tr>
  <tr>
    <td height="80" colspan="4" align="center" class="form_underline" scope="row"><input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href = 'listSensor.jsp';" value="Close"></td>
    </tr>
  <tr>
    <td width="20%" height="22" align="right" scope="row">&nbsp;</td>
    <td width="32%" height="22">&nbsp;</td>
    <td width="20%" height="22" align="right">&nbsp;</td>
    <td height="22">&nbsp;</td>
  </tr>
</table>
</form>
<table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td height="40" colspan="5" align="center" class="listTitle_CSS form_underline_blue radiusLeft radiusRight"><%= storageDevice.getStorageDeviceSerialNumber() + " (" + storageDevice.getStorageDeviceNickname() + ")" %></td>
    </tr>
    <tr class="listCotent_CSS">
      <th width="25%" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">&nbsp;Date Time</th>
      <th width="25%" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">User</th>
      <th height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Open/Close</th>
      <th height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Total Open Time</th>
    </tr>
    <%
    int i = 0;
    for (SensorReading sensorReading : doorReadings) {
    	Long openTime = openTimes.get(i);
    %>
    <tr class="listCotent_CSS">
        <td width="25%" height="50" align="center" class="font_18 form_underline_gray">
        <%= DateManager.printDateAsString(sensorReading.getSensorReadingTime()) %>
        </td>
        <td width="25%" height="50" align="center" class="font_18 form_underline_gray">
        -
        </td>
        <td height="50" align="center" class="font_18 form_underline_gray">
        <%= sensorReading.getSensorReadingValue() %>
        </td>
        <td height="50" align="center" class="font_18 form_underline_gray">
        <%= openTime < 60000 ? Math.round(openTime / 1000.0) + " sec" : Math.round(openTime / 60000.0) + " min" %>
        </td>
    </tr>
    <%
    	i++;
    }
    %>

  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>