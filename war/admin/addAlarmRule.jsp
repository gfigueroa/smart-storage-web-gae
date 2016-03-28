<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.SensorType" %>
<%@ page import="datastore.SensorTypeManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.User" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>

<!doctype html>
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
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

List<StorageDevice> storageDevices = StorageDeviceManager.getAllStorageDevices();
List<SensorType> sensorTypes = SensorTypeManager.getAllSensorTypes();
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Add New Alarm Trigger Rule</div>
	<form id="addWarningRuleform" name="addWarningRuleform" method="post" action="/manageCustomer?action=add&type=alarmTrigger&keep_adding=true">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="73" align="left" class="form_underline" scope="row">Input Trigger Rule：</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceSerialNumber">*Storage Equipment Serial Number：</label></td>
      <td width="354" height="60">
        <select name="storageDeviceId" id="storageDeviceId" class="textfield_style">
          <%
          for (StorageDevice storageDevice : storageDevices) {
          %>
          	<option value="<%= KeyFactory.keyToString(storageDevice.getKey()) %>"><%= storageDevice.getStorageDeviceSerialNumber() %></option>
          <%
          }
          %>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerCode">*Trigger Code：</label></td>
      <td width="354" height="60">        <input type="text" name="alarmTriggerCode" id="alarmTriggerCode" class="textfield_style" style="text-align:center;"></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="sensorTypeId">*Sensor Type：</label></td>
      <td width="354" height="60">
        <select name="sensorTypeId" id="sensorTypeId" class="textfield_style">
          <%
          for (SensorType sensorType : sensorTypes) {
        	  if (sensorType.getSensorTypeName().equals("Door Open/Close")) {
          %>
          		<option value="<%= sensorType.getKey() %>"><%= sensorType.getSensorTypeName() %></option>
          <%
            }
          }
          %>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerConditionOperator">*Condition Operator：</label></td>
      <td width="354" height="60">
        <select name="alarmTriggerConditionOperator" id="alarmTriggerConditionOperator" class="textfield_style">
          <option value="&gt;">&gt;</option>
          <option value="&lt;">&lt;</option>
          <option value="=">=</option>
          <option value="&gt;=">&gt;=</option>
          <option value="&lt;=">&lt;=</option>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerConditionValue">*Trigger Value：</label></td>
      <td width="354" height="60">
        <input type="text" name="alarmTriggerConditionValue" id="alarmTriggerConditionValue" class="textfield_style" style="width:174px;text-align:center;">
        <select name="alarmTriggerConditionUnit" id="alarmTriggerConditionUnit" class="textfield_style" style="width:174px;">
          <option value="Seconds">Seconds</option>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerMaxCount">*Trigger Max Count：</label></td>
      <td width="354" height="60">        <input type="text" name="alarmTriggerMaxCount" id="alarmTriggerMaxCount" class="textfield_style" style="text-align:center;"></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td class="form_underline" width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td class="form_underline" height="60" align="right" scope="row"><label for="alarmTriggerLevel">*Trigger Level：</label></td>
      <td class="form_underline" width="354" height="60">
        <select name="alarmTriggerLevel" id="alarmTriggerLevel" class="textfield_style">
          <option value="INFORMATION">Information</option>
          <option value="WARNING">Warning</option>
          <option value="CRITICAL" selected="selected">Critical</option>
        </select></td>
      <td class="form_underline" width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td colspan="4" align="center"><input type="submit" name="NewDeviceTypeSubmit" id="NewDeviceTypeSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listAlarmRule.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>