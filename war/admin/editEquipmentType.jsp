<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="datastore.DeviceServiceType" %>
<%@ page import="datastore.DeviceServiceTypeManager" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>

<!doctype html>
<html >
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addEquipmenttype.js"></script>
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

Long key = 0L;
try{
	key = Long.parseLong(request.getParameter(("k")));
}catch(Exception n){
	response.sendRedirect("../login.jsp");
}

DeviceServiceType currentDeviceServiceTypeToEdit = DeviceServiceTypeManager.getDeviceServiceType(key);
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Equipment type</div>
	<form id="addDeviceTypeform" name="addAdminform" method="post" action="/manageGlobalObject?action=update&type=deviceServiceType&k=<%= key %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="73" colspan="2" align="left" class="form_underline" scope="row">Edit Equipment Type：<%
String msg = request.getParameter("msg");
String action = request.getParameter("action");
if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("update")) {
%>
        <span class="warn_font_green">Edit Successful !</span>
        <% } 



%></td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td width="208" height="60" align="right" scope="row">*Equipment Type：</td>
      <td width="354" height="60">
        <input name="deviceServiceTypeName" type="text" class="textfield_style" id="deviceServiceTypeName" value="<%= currentDeviceServiceTypeToEdit.getDeviceServiceTypeName() %>">
        </td>
      <td width="303" height="60"><div id="empty_deviceServiceTypeName" class="errorText">*Enter equipment type	</div></td>
    </tr>
    <tr>
      <td class="form_underline" width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td class="form_underline" height="60" align="right" scope="row">Description：</td>
      <td class="form_underline" width="354" height="60"><label for="deviceServiceTypeDescription"></label>
        <input name="deviceServiceTypeDescription" type="text" class="textfield_style" id="deviceServiceTypeDescription" value="<%= currentDeviceServiceTypeToEdit.getDeviceServiceTypeDescription() %>"></td>
      <td class="form_underline" width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td colspan="4" align="center"><input type="submit" name="NewDeviceTypeSubmit" id="NewDeviceTypeSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEquipmentType.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>