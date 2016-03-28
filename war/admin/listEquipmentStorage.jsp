<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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
<title>SMASRV IoT</title>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/listEquipmentStorage.js"></script>
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
    <td height="50" valign="middle">Storage equipment List</td>
    <%
    	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
    %>
    <td width="3%" valign="middle"><a href="addEquipmentStorage.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="19%" valign="middle"><a href="addEquipmentStorage.jsp" class="font_18">Add equipment</a></td>
    <%
    	}
    %>
  </tr>
</table>
  </div>
  <%
  	String msg = request.getParameter("msg");
  String action = request.getParameter("action");
  if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("add")) {
  %>
	<table width="384" border="0" align="center" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td height="30" align="center" valign="top" class="warn_font_green">Add Equipment Successful !</td>
      </tr>
  </tbody>
</table>
<%
	}
else if(msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("delete")) {
%>
	<table width="384" border="0" align="center" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td height="30" align="center" valign="top" class="warn_font_red">Delete Equipment Successful !</td>
      </tr>
  </tbody>
</table>
<%
	}
%>
  <table width="800" height="40" border="0" align="center" cellpadding="0" cellspacing="0" class="listTitle_CSS form_underline_blue radiusLeft radiusRight">
    <tr>
      <td height="40" align="left" class="font_18">&nbsp;Storage equipment serial no.</td>
      <td width="20%" align="center" class="font_18">Edit</td>
      <%
      	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
      %>
      <td width="11%" align="center" class="font_18">Delete</td>
      <%
      	}
      %>
      </tr>
  </table>
  <%
  	int deviceModelCount = 0;
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
  %>
  <table class="listStorageHide" width="800" border="0" align="center" cellpadding="0" cellspacing="0" style="border-bottom-color:#FFFFFF">
    <tr OnClick='deviceShowFunction(<%= deviceModelCount %>, this);'>
      <td height="40" colspan="3" align="left" class="listTitle_CSS form_underline_blue " style="cursor:pointer;border-bottom-color:#FFFFFF"><%= deviceModel.getDeviceModelName() %></td>
    </tr>
    <%
    for (StorageDevice storageDevice : storageDevices) {
    %>
	    <tr class="listCotent_CSS">
	      <td height="30" align="left" class="font_18 form_underline_gray">&nbsp;<%= storageDevice.getStorageDeviceSerialNumber() %></td>
	      <td width="20%" height="30" align="center" class="font_18 form_underline_gray"><a href="editEquipmentStorage.jsp?k=<%= KeyFactory.keyToString(storageDevice.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
          <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
%>
	      <td width="11%" height="30" align="center" class="font_18 form_underline_gray"><a href="/manageCustomer?action=delete&type=storageDevice&k=<%=  KeyFactory.keyToString(storageDevice.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
          <% } %>
	    </tr>
    <%
    }
    %>
  </table>
  <%
  	deviceModelCount++;
  }
  %>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>