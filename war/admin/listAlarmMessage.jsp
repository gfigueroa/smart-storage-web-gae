<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.AlarmWarning" %>
<%@ page import="datastore.AlarmWarningManager" %>
<%@ page import="datastore.AlarmWarningMessage" %>
<%@ page import="datastore.AlarmWarningMessage.AlarmWarningMessageStatus" %>
<%@ page import="datastore.AlarmWarningMessageManager" %>
<%@ page import="datastore.AlarmTrigger" %>
<%@ page import="datastore.AlarmTriggerManager" %>
<%@ page import="datastore.AlarmTriggerMessage" %>
<%@ page import="datastore.AlarmTriggerMessage.AlarmTriggerMessageStatus" %>
<%@ page import="datastore.AlarmTriggerMessageManager" %>
<%@ page import="datastore.DeviceModel" %>
<%@ page import="datastore.DeviceModelManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}
</script>
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

String statusString = request.getParameter("status");
AlarmWarningMessageStatus warningStatus = AlarmWarningMessageStatus.ALERT;
AlarmTriggerMessageStatus triggerStatus = AlarmTriggerMessageStatus.ALERT;
if (statusString != null) {
	warningStatus = AlarmWarningMessage.getAlarmWarningMessageStatusFromString(statusString);
	triggerStatus = AlarmTriggerMessage.getAlarmTriggerMessageStatusFromString(statusString);
}

List<DeviceModel> deviceModels;
switch (sessionUser.getUserType()) {
	case ADMINISTRATOR:
		deviceModels = DeviceModelManager.getAllDeviceModels();
		break;
	case CUSTOMER:
		deviceModels = DeviceModelManager.getAllDeviceModelsFromCustomer(sessionUser.getKey().getParent());
		break;
	case CUSTOMER_USER:
		deviceModels = DeviceModelManager.getAllDeviceModelsFromCustomer(sessionUser.getKey().getParent().getParent());
		break;
	default:
		deviceModels = null;
		break;
}
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" valign="middle">Alarm Message List</td>
  </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td width="35%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusLeft">Storage Equipment Serial No</td>
      <td width="40%" align="center" class="listTitle_CSS form_underline_blue ">Warning message</td>
      <td width="13%" align="center" class="listTitle_CSS form_underline_blue "><form name="form" id="form">
        <select name="WarningStatus" id="WarningStatus" class="textfield_style" style="width:94px;height:30px;" onChange="MM_jumpMenu('parent',this,0)">
          <option value="listAlarmMessage.jsp?status=alert" <%=warningStatus == AlarmWarningMessageStatus.ALERT ? "selected" : ""%>>Alert</option>
          <option value="listAlarmMessage.jsp?status=processed" <%=warningStatus == AlarmWarningMessageStatus.PROCESSED ? "selected" : ""%>>Processed</option>
          <option value="listAlarmMessage.jsp?status=resolved" <%=warningStatus == AlarmWarningMessageStatus.RESOLVED ? "selected" : ""%>>Resolved</option>
        </select>
      </form></td>
      <td width="6%" height="40" align="center" class="listTitle_CSS form_underline_blue">Edit</td>
      <%
      	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) {
      %>
      <td width="10%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusRight">Delete</td>
      <%
      	}
      %>
    </tr>
    
    <%
        	for (DeviceModel deviceModel : deviceModels) {
            	
            	List<StorageDevice> storageDevices;
            	switch (sessionUser.getUserType()) {
            		case ADMINISTRATOR:
            			storageDevices = StorageDeviceManager.getAllStorageDevicesFromDeviceModel(deviceModel.getKey());
            			break;
            		case CUSTOMER:
            			storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomerAndDeviceModel(sessionUser.getKey().getParent(), deviceModel.getKey());
            			break;
            		case CUSTOMER_USER:
            			storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomerAndDeviceModel(sessionUser.getKey().getParent().getParent(), deviceModel.getKey());
            			break;
            		default:
            			storageDevices = null;
            			break;
            	}
            	
            	for (StorageDevice storageDevice : storageDevices) {
            		List<AlarmWarningMessage> alarmWarningMessages = AlarmWarningMessageManager.getAllAlarmWarningMessagesFromStorageDevice(storageDevice.getKey(), warningStatus);
            		for (AlarmWarningMessage alarmWarningMessage : alarmWarningMessages) {
            			AlarmWarning alarmWarning = AlarmWarningManager.getAlarmWarning(alarmWarningMessage.getKey().getParent());
        %>
			    <tr class="listCotent_CSS">
			      <td height="35" align="center" class="font_18 form_underline_gray"><%= storageDevice.getStorageDeviceSerialNumber() %></td>
			      <td width="40%" height="30" align="left" class="font_18 form_underline_gray"><%= alarmWarning.getAlarmWarningMessage() %></td>
			      <td width="13%" height="30" align="center" class="font_18 form_underline_gray"><%= alarmWarningMessage.getAlarmWarningMessageStatus() %></td>
			      <td width="6%" height="30" align="center" class="font_18 form_underline_gray"><a href="editAlarmWarningStatus.jsp?k=<%= KeyFactory.keyToString(alarmWarningMessage.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
			      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) {
		%>
                  <td width="10%" height="30" align="center" class="font_18 form_underline_gray"><a href="/manageCustomer?action=delete&type=alarmWarningMessage&k=<%= KeyFactory.keyToString(alarmWarningMessage.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
                  <% } %>
			    </tr>
    <%
    		}
    		
    		List<AlarmTriggerMessage> alarmTriggerMessages = AlarmTriggerMessageManager.getAllAlarmTriggerMessagesFromStorageDevice(storageDevice.getKey(), triggerStatus);
    		for (AlarmTriggerMessage alarmTriggerMessage : alarmTriggerMessages) {
    			AlarmTrigger alarmTrigger = AlarmTriggerManager.getAlarmTrigger(alarmTriggerMessage.getKey().getParent());
    %>
        		<tr class="listCotent_CSS">
			      <td height="35" align="center" class="font_18 form_underline_gray"><%= storageDevice.getStorageDeviceSerialNumber() %></td>
			      <td width="40%" height="30" align="left" class="font_18 form_underline_gray"><%= AlarmTriggerManager.getAlarmTriggerString(alarmTrigger.getKey()) %></td>
			      <td width="13%" height="30" align="center" class="font_18 form_underline_gray"><%= alarmTriggerMessage.getAlarmTriggerMessageStatus() %></td>
			      <td width="6%" height="30" align="center" class="font_18 form_underline_gray"><a href="editAlarmTriggerStatus.jsp?k=<%= KeyFactory.keyToString(alarmTriggerMessage.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
			      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) {
		%>
                  <td width="10%" height="30" align="center" class="font_18 form_underline_gray"><a href="/manageCustomer?action=delete&type=alarmTriggerMessage&k=<%= KeyFactory.keyToString(alarmTriggerMessage.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
                  <% } %>
			    </tr>
    <%
    		}
    	}
    }
    %>
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>