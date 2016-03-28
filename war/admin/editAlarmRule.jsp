<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.SensorInstance" %>
<%@ page import="datastore.SensorInstanceManager" %>
<%@ page import="datastore.AlarmTrigger" %>
<%@ page import="datastore.AlarmTriggerManager" %>
<%@ page import="datastore.SensorType" %>
<%@ page import="datastore.SensorTypeManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.User" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>

<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
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

String alarmTriggerKeyString = request.getParameter("k");
Key alarmTriggerKey = KeyFactory.stringToKey(alarmTriggerKeyString);
AlarmTrigger alarmTrigger = AlarmTriggerManager.getAlarmTrigger(alarmTriggerKey);

SensorInstance sensorInstance = SensorInstanceManager.getSensorInstance(alarmTrigger.getKey().getParent());
SensorType sensorType = SensorTypeManager.getSensorType(sensorInstance.getSensorType());
StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(sensorInstance.getKey().getParent());
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Alarm Trigger Rule</div>
	<form id="addWarningRuleform" name="addWarningRuleform" method="post" action="/manageCustomer?action=update&type=alarmTrigger&k=<%= alarmTriggerKeyString %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="73" align="left" class="form_underline" scope="row">Input Trigger Rule：</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceSerialNumber">Storage Equipment Serial Number：</label></td>
      <td width="354" height="60">
        <input name="storageDeviceSerialNumber" value="<%= storageDevice.getStorageDeviceSerialNumber() %>" type="text" class="textfield_style" id="storageDeviceSerialNumber" disabled></td>
      <td width="303" height="60" align="left">
    </td>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerCode">*Trigger Code：</label></td>
      <td width="354" height="60">        <input type="text" name="alarmTriggerCode" id="alarmTriggerCode" class="textfield_style" style="text-align:center;" value="<%= alarmTrigger.getAlarmTriggerCode() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="sensorTypeId">Sensor Type：</label></td>
      <td width="354" height="60">
        <input name="sensorTypeId" value="<%= sensorType.getSensorTypeName() %>" type="text" class="textfield_style" id="sensorTypeId" disabled></td>
      <td width="303" height="60" align="left">
    </td>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerConditionOperator">*Condition Operator：</label></td>
      <td width="354" height="60">
        <select name="alarmTriggerConditionOperator" id="alarmTriggerConditionOperator" class="textfield_style" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
          <option value="&gt;" <%= alarmTrigger.getAlarmTriggerConditionOperator() == AlarmTrigger.AlarmTriggerConditionOperator.GREATER_THAN ? "selected" : "" %>>&gt;</option>
          <option value="&lt;" <%= alarmTrigger.getAlarmTriggerConditionOperator() == AlarmTrigger.AlarmTriggerConditionOperator.LESS_THAN ? "selected" : "" %>>&lt;</option>
          <option value="=" <%= alarmTrigger.getAlarmTriggerConditionOperator() == AlarmTrigger.AlarmTriggerConditionOperator.EQUAL ? "selected" : "" %>>=</option>
          <option value="&gt;=" <%= alarmTrigger.getAlarmTriggerConditionOperator() == AlarmTrigger.AlarmTriggerConditionOperator.GREATER_THAN_OR_EQUAL ? "selected" : "" %>>&gt;=</option>
          <option value="&lt;=" <%= alarmTrigger.getAlarmTriggerConditionOperator() == AlarmTrigger.AlarmTriggerConditionOperator.LESS_THAN_OR_EQUAL ? "selected" : "" %>>&lt;=</option>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerConditionValue">*Trigger Value：</label></td>
      <td width="354" height="60">
        <input type="text" name="alarmTriggerConditionValue" id="alarmTriggerConditionValue" class="textfield_style" style="width:174px;text-align:center;" value="<%= alarmTrigger.getAlarmTriggerConditionValue() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
        <select name="alarmTriggerConditionUnit" id="alarmTriggerConditionUnit" class="textfield_style" style="width:174px;" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
          <option value="Seconds">Seconds</option>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerMaxCount">*Trigger Max Count：</label></td>
      <td width="354" height="60">        <input type="text" name="alarmTriggerMaxCount" id="alarmTriggerMaxCount" class="textfield_style" style="text-align:center;" value="<%= alarmTrigger.getAlarmTriggerMaxCount() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>></td>
      <td width="303" height="60">&nbsp;<%= "(Current count: " + alarmTrigger.getAlarmTriggerCount() + ")" %></td>
    </tr>
    <tr>
      <td class="form_underline" width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td class="form_underline" height="60" align="right" scope="row"><label for="alarmTriggerLevel">*Trigger Level：</label></td>
      <td class="form_underline" width="354" height="60">
        <select name="alarmTriggerLevel" id="alarmTriggerLevel" class="textfield_style" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
          <option value="INFORMATION" <%= alarmTrigger.getAlarmTriggerLevel() == AlarmTrigger.AlarmTriggerLevel.INFORMATION ? "selected" : "" %>>Information</option>
          <option value="WARNING" <%= alarmTrigger.getAlarmTriggerLevel() == AlarmTrigger.AlarmTriggerLevel.WARNING ? "selected" : "" %>>Warning</option>
          <option value="CRITICAL" <%= alarmTrigger.getAlarmTriggerLevel() == AlarmTrigger.AlarmTriggerLevel.CRITICAL ? "selected" : "" %>>Critical</option>
        </select></td>
      <td class="form_underline" width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td colspan="4" align="center">
      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
		%>
      <input type="submit" name="NewDeviceTypeSubmit" id="NewDeviceTypeSubmit" class="css_btn_class" value="Save">&nbsp;
      <% } %>
      <input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listAlarmRule.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>