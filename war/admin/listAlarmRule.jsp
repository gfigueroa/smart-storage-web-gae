<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.AlarmTrigger" %>
<%@ page import="datastore.AlarmTriggerManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.User" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
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
%>

<body>
<%@include file="../header/header.jsp" %>

 
<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" valign="middle">Alarm Trigger Rule</td>
        <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
		%>
    <td width="3%" valign="middle"><a href="addAlarmRule.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="25%" align="center" valign="middle"><a  href="addAlarmRule.jsp"><span class="font_18">Add Trigger Rule</span></a></td>
    <%
	}
	%>
    </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td width="25%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusLeft">Device Serial No.</td>
      <td width="45%" align="center" class="listTitle_CSS form_underline_blue">Alarm trigger rule</td>
      <td align="center" class="listTitle_CSS form_underline_blue">Level</td>
      <td width="6%" height="40" align="center" class="listTitle_CSS form_underline_blue">Edit</td>
      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
		%>
      <td width="8%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusRight">Delete</td>
      <% } %>
    </tr>
    <%
    for (StorageDevice storageDevice : storageDevices) {
    	List<AlarmTrigger> alarmTriggers = AlarmTriggerManager.getAllAlarmTriggersFromStorageDevice(storageDevice.getKey());
    	
    	for (AlarmTrigger alarmTrigger : alarmTriggers) {
    %>
		    <tr class="listCotent_CSS">
		      <td width="25%" height="30" align="center" class="font_18 form_underline_gray"><%= storageDevice.getStorageDeviceSerialNumber() %></td>
		      <td width="45%" height="30" align="center" class="font_18 form_underline_gray"><%= AlarmTriggerManager.getAlarmTriggerString(alarmTrigger.getKey()) %></td>
		      <td height="30" align="center" class="font_18 form_underline_gray"><%= alarmTrigger.getAlarmTriggerLevel() %></td>
		      <td width="6%" height="30" align="center" class="font_18 form_underline_gray"><a href="editAlarmRule.jsp?k=<%= KeyFactory.keyToString(alarmTrigger.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
              <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
		%>
		      <td width="6%" height="30" align="center" class="font_18 form_underline_gray"><a href="/manageCustomer?action=delete&type=alarmTrigger&k=<%= KeyFactory.keyToString(alarmTrigger.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
              <% } %>
		    </tr>
	<%
		}
	}
	%>
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>