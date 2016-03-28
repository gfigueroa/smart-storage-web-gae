<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.DeviceServiceType" %>
<%@ page import="datastore.DeviceServiceTypeManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>SMASRV IoT</title>
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
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

List<DeviceServiceType> deviceServiceTypes = DeviceServiceTypeManager.getAllDeviceServiceTypes();
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" valign="middle">Equipment Type</td>
    <td width="2%" valign="middle"><a href="addEquipmentType.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="13%" valign="middle"><a  href="addEquipmentType.jsp"><span class="font_18">&nbsp;Add Type</span></a></td>
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
      <td height="30" align="center" valign="top" class="warn_font_green">Add Type Successful !</td>
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
      <td height="30" align="center" valign="top" class="warn_font_red">Delete Type Successful !</td>
      </tr>
  </tbody>
</table>
<%
	}
%>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td height="40" align="left" class="listTitle_CSS form_underline_blue radiusLeft">Type Name</td>
      <td width="13%" height="40" align="center" class="listTitle_CSS form_underline_blue">Edit</td>
      <td width="11%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusRight">Delete</td>
    </tr>
    <%
    	for (DeviceServiceType deviceServiceType : deviceServiceTypes) {
    %>
	    <tr class="listCotent_CSS">
	      <td height="30" align="left" class="font_18 form_underline_gray">&nbsp;<%= deviceServiceType.getDeviceServiceTypeName() %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><a href="editEquipmentType.jsp?k=<%= deviceServiceType.getKey() %>" title="Edit Type"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><a href="/manageGlobalObject?action=delete&type=deviceServiceType&k=<%= deviceServiceType.getKey() %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
	    </tr>
	<%
    }
	%>

  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>