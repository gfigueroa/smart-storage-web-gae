<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.DeviceModel" %>
<%@ page import="datastore.DeviceModelManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

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
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

List<DeviceModel> deviceModels = DeviceModelManager.getAllDeviceModels();
List<Customer> customers = CustomerManager.getAllCustomers();
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Add Storage equipment</div>
	<form id="addStorageform" name="addStorageform" method="post" action="/manageCustomer?action=add&type=storageDevice&keep_adding=true">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td width="130" height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Input Storage Equipment Information：</td>
      <td height="60" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
      <td height="20">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="deviceModelId">*Model：</label></td>
      <td width="354" height="60">
        <select name="deviceModelId" class="textfield_style" id="deviceModelId">
        <%
        	for (DeviceModel deviceModel : deviceModels) {
        %>
          	<option value="<%= KeyFactory.keyToString(deviceModel.getKey()) %>"><%= deviceModel.getDeviceModelName() %></option>
        <%
        }
        %>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceNickname">*Nickname：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceNickname" type="text" class="textfield_style" id="storageDeviceNickname"></td>
      <td width="303" height="60" align="left"><div id="empty_storageDeviceNickname" class="errorText">*Enter storage equipment nickname</div></td>
       <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceDescription">Description：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceDescription" type="text" class="textfield_style" id="storageDeviceDescription"></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceSerialNumber">*Serial Number：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceSerialNumber" type="text" class="textfield_style" id="storageDeviceSerialNumber"></td>
      <td width="303" height="60" align="left"><div id="empty_DeviceSerialNumber" class="errorText">*Enter storage equipment serial number</div></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceManufacturedDate">*Manufactured Date：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceManufacturedDate" type="text" class="textfield_style" id="storageDeviceManufacturedDate" style="width:315px;">
        <label for="storageDeviceManufacturedDate"><img src="../images/calendaricon.png" alt="" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
        <td width="303" height="60" align="left"><div id="empty_DeviceManufactured" class="errorText">*Enter storage equipment manufactured &nbsp;&nbsp;date</div></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceShippingDate">*Shipping Date：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceShippingDate" type="text" class="textfield_style" id="storageDeviceShippingDate" style="width:315px;">
       <label for="storageDeviceShippingDate"> <img src="../images/calendaricon.png" alt="" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
        <td width="303" height="60" align="left"><div id="empty_DeviceShipping" class="errorText">*Enter storage equipment shipping  date</div></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="sensorDataEffectivePeriod">*Sensor Data Effective Period：</label></td>
      <td width="354" height="60">
        <input name="sensorDataEffectivePeriod" type="text" class="textfield_style" id="sensorDataEffectivePeriod"></td>
        <td width="303" height="60" align="left">&nbsp;
          <div id="empty_sensorDataEffectivePeriod" class="errorText">*Invalid or empty value</div>
        days</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="alarmMessageEffectivePeriod">*Alarm Message Effective Period：</label></td>
      <td width="354" height="60">
        <input name="alarmMessageEffectivePeriod" type="text" class="textfield_style" id="alarmMessageEffectivePeriod"></td>
        <td width="303" height="60" align="left">&nbsp;
          <div id="empty_alarmMessageEffectivePeriod" class="errorText">*Invalid or empty value</div>
        days</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="enableSensorDataUpload">*Enable Sensor Data Upload：</label></td>
      <td width="354" height="60" align="left"><input type="checkbox" name="enableSensorDataUpload" id="enableSensorDataUpload" value="true" checked>
        </td>
        <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="sensorReadingCacheSize">*Sensor Reading Cache Size：</label></td>
      <td width="354" height="60">
        <input name="sensorReadingCacheSize" type="text" class="textfield_style" id="sensorReadingCacheSize"></td>
        <td width="303" height="60" align="left">&nbsp;
          <div id="empty_sensorReadingCacheSize" class="errorText">*Invalid or empty value</div>
        readings</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="customerId">*Customer：</label></td>
      <td width="354" height="60">
        <select name="c_key" id="customerId" class="textfield_style">
        <%
        for (Customer customer : customers) {
        %>
          <option value="<%= KeyFactory.keyToString(customer.getKey()) %>"><%= customer.getCustomerName() + " (" + customer.getUser().getUserEmail().getEmail() + ")" %></option>
        <%
        }
        %>
        </select></td>
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
    <tr><td colspan="4" align="center"><input type="submit" name="NewStorageSubmit" id="NewStorageSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEquipmentStorage.jsp'" value="Close"></td>
      </tr>
</table>
</form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>