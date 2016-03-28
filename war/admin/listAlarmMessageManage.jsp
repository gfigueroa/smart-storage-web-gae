<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.AlarmWarning" %>
<%@ page import="datastore.AlarmWarningManager" %>
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
    <td height="50" valign="middle">Alarm Warning Rules</td>
    <%
    	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
    %>
    <td width="3%" valign="middle"><a href="addAlarmMessage.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="25%" align="left" valign="middle"><a  href="addAlarmMessage.jsp"><span class="font_18">Add Warning Rule</span></a></td>
    <%
    	}
    %>
    </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td width="31%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusLeft">Equipment Model</td>
      <td width="13%" align="center" class="listTitle_CSS form_underline_blue ">Warning Code</td>
      <td width="40%" align="center" class="listTitle_CSS form_underline_blue ">Warning Message</td>
      <td width="6%" height="40" align="center" class="listTitle_CSS form_underline_blue">Edit</td>
      <%
      	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
      %>
      <td width="10%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusRight">Delete</td>
      <%
      	}
      %>
    </tr>
    <%
    	for (DeviceModel deviceModel : deviceModels) {
        	List<AlarmWarning> alarmWarnings = AlarmWarningManager.getAllAlarmWarningsFromDeviceModel(deviceModel.getKey());
        	for (AlarmWarning alarmWarning : alarmWarnings) {
    %>
    <tr class="listCotent_CSS">
      <td height="30" align="center" class="font_18 form_underline_gray"><%= deviceModel.getDeviceModelName() %></td>
      <td width="13%" align="center" class="font_18 form_underline_gray"><%= alarmWarning.getAlarmWarningCode() %></td>
      <td width="40%" height="30" align="left" class="font_18 form_underline_gray"><%= alarmWarning.getAlarmWarningMessage() %></td>
      <td width="6%" height="30" align="center" class="font_18 form_underline_gray"><a href="editAlarmMessage.jsp?k=<%= KeyFactory.keyToString(alarmWarning.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
		%>
      <td width="10%" height="30" align="center" class="font_18 form_underline_gray"><a href="/manageGlobalObject?action=delete&type=alarmWarning&k=<%= KeyFactory.keyToString(alarmWarning.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
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