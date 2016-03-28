<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="datastore.DeviceModel" %>
<%@ page import="datastore.DeviceModelManager" %>
<%@ page import="datastore.DeviceModelDoor" %>
<%@ page import="datastore.DeviceModelDoorManager" %>
<%@ page import="datastore.DeviceModelPartition" %>
<%@ page import="datastore.DeviceModelPartitionManager" %>
<%@ page import="datastore.DeviceServiceType" %>
<%@ page import="datastore.DeviceServiceTypeManager" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="util.DateManager" %>

<%
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

String deviceModelKey = request.getParameter("k");

DeviceModel deviceModel = DeviceModelManager.getDeviceModel(KeyFactory.stringToKey(deviceModelKey));
List<DeviceServiceType> deviceServiceTypes = DeviceServiceTypeManager.getAllDeviceServiceTypes();

List<DeviceModelDoor> deviceModelDoors = DeviceModelDoorManager.getAllDeviceModelDoorsFromDeviceModel(KeyFactory.stringToKey(deviceModelKey));
%>

<!doctype html>
<html >
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/jquery.datetimepicker.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/editEquipmentModel.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>SMASRV IoT</title>
</head>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit equipment model</div>
	<form id="addDeviceModelform" name="addDeviceModelform" method="post" action="/manageGlobalObject?action=update&type=deviceModel&k=<%=deviceModelKey%>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row"><p>Model Information:<%
      	String msg = request.getParameter("msg");
      String action = request.getParameter("action");
      if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("update")) {
      %>
        <span class="warn_font_green">Edit Successful !</span>
        <%
        	}
        %></p></td>
      <td height="60" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td height="20">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceServiceTypeId">*Select Type：</label></td>
      <td width="354" height="60" class="login_textfield">
        <select name="deviceServiceTypeId" class="textfield_style" id="deviceServiceTypeId" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : ""%>>
          <%
          	for (DeviceServiceType deviceServiceType : deviceServiceTypes) {
          %>
          	<option value="<%= deviceServiceType.getKey() %>" <%= deviceModel.getDeviceServiceType().equals(deviceServiceType.getKey()) ? "selected" : "" %> ><%= deviceServiceType.getDeviceServiceTypeName() %></option>
           <%
        	}
           %>
        </select></td>
      <td width="303" height="60"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceModelName">*Model Name：</label></td>
      <td width="354" height="60" class="login_textfield">
        <input type="text" name="deviceModelName" id="deviceModelName" class="textfield_style" value="<%= deviceModel.getDeviceModelName() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60"><div id="empty_deviceModelName" class="errorText">*Enter model name</div></td>
    </tr>
    
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceModelDescription">Model Description：</label></td>
      <td width="354" height="60" class="login_textfield">
        <input name="deviceModelDescription" type="text" class="textfield_style" id="deviceModelDescription" value="<%= deviceModel.getDeviceModelDescription()  %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60"></td>
    </tr>
    
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceModelDesignTime">*Model Design Time：</label></td>
      <td width="354" height="60">
        <input name="deviceModelDesignTime" type="text" class="textfield_style" id="deviceModelDesignTime" style="width:315px;" value="<%= DateManager.printDateAsStringWithDash(deviceModel.getDeviceModelDesignTime()) %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %> readonly>
        <label for="deviceModelDesignTime"><img src="../images/calendaricon.png" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
      <td width="303" height="60" align="left"><div id="empty_deviceModelDesignTime" class="errorText">*Enter model design time</div></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="sensorDataUploadPeriod">*Data Upload Interval：</label></td>
      <td width="354" height="60" class="login_textfield">
        <input name="sensorDataUploadPeriod" type="text" class="textfield_style" id="sensorDataUploadPeriod" value="<%= deviceModel.getSensorDataUploadPeriod() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60">&nbsp;
        <div id="empty_sensorDataUploadPeriod" class="errorText">*Enter data upload period</div>
        seconds</td>
    </tr>
    <tr>
      <td height="21" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="21" align="left" class="form_underline" scope="row"></td>
      <td height="21" class="form_underline">&nbsp;</td>
      <td height="21" class="form_underline"></td>
    </tr>
    <tr>
      <td height="70" align="right" scope="row">&nbsp;</td>
      <td height="70" align="right" scope="row">*Sensor Configuration :</td>
      <td height="70">&nbsp;</td>
      <td height="70"></td>
    </tr>
    <tr>
      <td width="130" height="60" rowspan="2" align="right" scope="row">&nbsp;</td>
      <td colspan="2" rowspan="4" scope="row"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td width="40%" height="30">
            <input name="temp1Hum1" type="checkbox" id="temp1Hum1" <%= deviceModel.getTemp1Hum1() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Temp/Hum 1</td>
          <td width="40%" height="30" align="left"><input name="infrared" type="checkbox" id="infrared" <%= deviceModel.getInfrared() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Infrared</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="temp2Hum2" type="checkbox" id="temp2Hum2" <%= deviceModel.getTemp2Hum2() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Temp/Hum 2</td>
          <td height="30" align="left"><input name="imageUpload" type="checkbox" id="imageUpload" <%= deviceModel.getImageUpload() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            imageUpload</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="doorOpenClose" type="checkbox" id="doorOpenClose" <%= deviceModel.getDoorOpenClose() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            <label for="doorOpenClose">Door open/close</label></td>
          <td height="30" align="left"><input name="alcohol" type="checkbox" id="alcohol" <%= deviceModel.getAlcohol() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            alcohol</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="co2" type="checkbox" id="co2" <%= deviceModel.getCO2() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            CO2</td>
          <td height="30" align="left"><input name="electricCurrent" type="checkbox" id="electricCurrent" <%= deviceModel.getElectricCurrent() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Electrical Current</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="co" type="checkbox" id="co" <%= deviceModel.getCO() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            CO</td>
          <td height="30" align="left"><input name="atmosphericPressure" type="checkbox" id="atmosphericPressure" <%= deviceModel.getAtmosphericPressure() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Atmosphere pressure</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="flux" type="checkbox" id="flux" <%= deviceModel.getFlux() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Flux</td>
          <td height="30" align="left"><input name="printer" type="checkbox" id="printer"  <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>            Finger print</td>
          </tr>
      </table></td>
      <td width="303" height="29" align="left"><input name="N2" type="checkbox" id="N2" <%= deviceModel.getInfrared() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
        N2</td>
    </tr>
    <tr>
      <td height="30" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row"></td>
      <td width="303" height="60" align="left"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row"></td>
      <td width="303" height="60" align="left"></td>
    </tr>
    <tr>
      <td height="30" align="right" class="form_underline" scope="row"></td>
      <td width="208" height="30" align="right" class="form_underline" scope="row"></td>
      <td height="30" class="form_underline"></td>
      <td height="30" class="form_underline"></td>
    </tr>
    <tr>
      <td height="70" align="right" scope="row">&nbsp;</td>
      <td height="70" align="right" scope="row">Partition Configuration :</td>
      <td height="70">&nbsp;</td>
      <td height="70"></td>
    </tr>
    
    <%
    for (DeviceModelDoor deviceModelDoor : deviceModelDoors) {
    	List<DeviceModelPartition> deviceModelPartitions = DeviceModelPartitionManager.getAllDeviceModelPartitionsFromDeviceModelDoor(deviceModelDoor.getKey());
    %>
    	<tr id="modelDoor<%= deviceModelDoor.getDeviceModelDoorNumber() %>">
    	<td height="40">&nbsp;</td>
    	<td height="40" align="right" scope="row"><b><%= "Door " + deviceModelDoor.getDeviceModelDoorNumber() + ": " %></b></td>
    	<td height="40" align="left" scope="row">&nbsp;<%= deviceModelPartitions.size() + " partitions" %></td>
    	<td height="40">&nbsp;</td>
    	</tr>
    <%
    }
    %>
	
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td class="form_underline"></td>
      <td colspan="2" align="center" class="form_underline" scope="row">&nbsp;</td>
      <td class="form_underline"></td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr><td colspan="4" align="center"><%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
	      %><input type="submit" name="NewModelSubButton" id="NewModelSubButton" class="css_btn_class" value="Save"><%
	      }
	      %>&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEquipmentModel.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>