<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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

List<DeviceModel> deviceModels = DeviceModelManager.getAllDeviceModels();
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="82%" height="50" valign="middle">Model List</td>
    <%
    	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
    %>
    <td width="3%" valign="middle"><a href="addEquipmentModel.jsp"><img src="../images/round_plus.png" width="20" height="20" alt=""></a></td>
    
    <td width="15%" valign="middle"><a href="addEquipmentModel.jsp"><span class="font_18">Add Model</span></a></td>
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
      <td height="30" align="center" valign="top" class="warn_font_green">Add Model Successful !</td>
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
      <td height="30" align="center" valign="top" class="warn_font_red">Delete Model Successful !</td>
      </tr>
  </tbody>
</table>
<%
	}
%>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td height="40" align="left" class="listTitle_CSS form_underline_blue radiusLeft">Model Name</td>
      <td width="20%" height="40" align="center" class="listTitle_CSS form_underline_blue"><%
      	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
      %>Edit<%
      	}
      %><%
      	if (sessionUser.getUserType() == User.UserType.CUSTOMER) {
      %>View<%
      	}
      %></td>
      <%
      	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
      %>
      <td width="11%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusRight">Delete</td>
      <%
      	}
      %>
    </tr>
    <%
    	for (DeviceModel deviceModel : deviceModels) {
    %>
	    <tr class="listCotent_CSS">
	      <td height="30" align="left" class="font_18 form_underline_gray">&nbsp;<%= deviceModel.getDeviceModelName() %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><a href="editEquipmentModel.jsp?k=<%= KeyFactory.keyToString(deviceModel.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
	      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
	      %>
          <td height="30" align="center" class="font_18 form_underline_gray">
	      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
	      %>
	      <a href="/manageGlobalObject?action=delete&type=deviceModel&k=<%= KeyFactory.keyToString(deviceModel.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a>
	      <%
	      }
	      %>
	      </td>
          <%
	      }
	      %>
	    </tr>
    <%
    }
    %>
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>