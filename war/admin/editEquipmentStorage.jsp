<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="datastore.DeviceModel" %>
<%@ page import="datastore.DeviceModelManager" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="java.util.List" %>
<%@ page import="util.DateManager" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/jquery.datetimepicker.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addEquipmentStorage.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>SMASRV IoT</title>
</head>

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

String keyString = request.getParameter("k");

StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(KeyFactory.stringToKey(keyString));
DeviceModel deviceModel = DeviceModelManager.getDeviceModel(storageDevice.getDeviceModel());

Key customerKey = storageDevice.getKey().getParent();
Customer customer = CustomerManager.getCustomer(customerKey);
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Storage equipment</div>
	<form id="addStorageform" name="addStorageform" method="post" action="/manageCustomer?action=update&type=storageDevice&k=<%= keyString %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td width="130" height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Edit Storage Equipment Information：<%
String msg = request.getParameter("msg");
String action = request.getParameter("action");
if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("update")) {
%>
        <span class="warn_font_green">Edit Successful !</span>
        <% } 



%></td>
      <td height="60" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
      <td height="20">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="deviceModelId">Model：</label></td>
      <td width="354" height="60">
        <input name="deviceModelId" value="<%= deviceModel.getDeviceModelName() %>" type="text" class="textfield_style" id="deviceModelId" disabled></td>
      <td width="303" height="60" align="left">
    </td>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceNickname">*Nickname：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceNickname" value="<%= storageDevice.getStorageDeviceNickname() %>" type="text" class="textfield_style" id="storageDeviceNickname" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60" align="left"><div id="empty_storageDeviceNickname" class="errorText">*Enter storage equipment nickname</div>
    </td>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceDescription">Description：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceDescription" value="<%= storageDevice.getStorageDeviceDescription() %>" type="text" class="textfield_style" id="storageDeviceDescription" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceSerialNumber">*Serial Number：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceSerialNumber" value="<%= storageDevice.getStorageDeviceSerialNumber() %>" type="text" class="textfield_style" id="storageDeviceSerialNumber" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60" align="left"><div id="empty_DeviceSerialNumber" class="errorText">*Enter storage equipment serial number</div></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceManufacturedDate">*Manufactured Date：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceManufacturedDate" type="text" class="textfield_style" id="storageDeviceManufacturedDate" style="width:315px;" value="<%= DateManager.printDateAsStringWithDash(storageDevice.getStorageDeviceManufacturedDate()) %>" readonly <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
        <label for="storageDeviceManufacturedDate"><img src="../images/calendaricon.png" alt="" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
        <td width="303" height="60" align="left"><div id="empty_DeviceManufactured" class="errorText">*Enter storage equipment manufactured &nbsp;&nbsp;date</div></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceShippingDate">*Shipping Date：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceShippingDate" type="text" class="textfield_style" id="storageDeviceShippingDate" style="width:315px;" value="<%= DateManager.printDateAsStringWithDash(storageDevice.getStorageDeviceShippingDate()) %>" readonly <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
       <label for="storageDeviceShippingDate"> <img src="../images/calendaricon.png" alt="" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
        <td width="303" height="60" align="left"><div id="empty_DeviceShipping" class="errorText">*Enter storage equipment shipping  date</div></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="sensorDataEffectivePeriod">*Sensor Data Effective Period：</label></td>
      <td width="354" height="60">
        <input name="sensorDataEffectivePeriod" value="<%= storageDevice.getSensorDataEffectivePeriod() %>" type="text" class="textfield_style" id="sensorDataEffectivePeriod" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
        <td width="303" height="60" align="left">&nbsp;
          <div id="empty_sensorDataEffectivePeriod" class="errorText">*Invalid or empty value</div>
        days</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="alarmMessageEffectivePeriod">*Alarm Message Effective Period：</label></td>
      <td width="354" height="60">
        <input name="alarmMessageEffectivePeriod" type="text" class="textfield_style" id="alarmMessageEffectivePeriod" value="<%= storageDevice.getAlarmMessageEffectivePeriod() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
        <td width="303" height="60" align="left">&nbsp;
          <div id="empty_alarmMessageEffectivePeriod" class="errorText">*Invalid or empty value</div>
        days</td>
    </tr>
    <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) { %>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="enableSensorDataUpload">*Enable Sensor Data Upload：</label></td>
      <td width="354" height="60" align="left"><input type="checkbox" name="enableSensorDataUpload" id="enableSensorDataUpload" <%= storageDevice.getEnableSensorDataUpload() ? "checked" : "" %> <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
        </td>
        <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="sensorReadingCacheSize">*Sensor Reading Cache Size：</label></td>
      <td width="354" height="60">
        <input name="sensorReadingCacheSize" value="<%= storageDevice.getSensorReadingCacheSize() %>" type="text" class="textfield_style" id="sensorReadingCacheSize" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
        <td width="303" height="60" align="left">&nbsp;
          <div id="empty_sensorReadingCacheSize" class="errorText">*Invalid or empty value</div>
        readings</td>
    </tr>
    <%     }
	      %>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="customerId">Customer：</label></td>
      <td width="354" height="60">
        <input name="customerId" type="text" class="textfield_style" id="customerId" value="<%= customer.getCustomerName() + " (" + customer.getUser().getUserEmail().getEmail() + ")" %>" disabled></td>
        <td width="303" height="60" align="left">&nbsp;</td>
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
    <tr>
    
    <td colspan="4" align="center">
    <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) { %>
    <input type="submit" name="NewStorageSubmit" id="NewStorageSubmit" class="css_btn_class" value="Save">&nbsp;
    <% } %>
    <input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEquipmentStorage.jsp'" value="Close"></td>
    
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>