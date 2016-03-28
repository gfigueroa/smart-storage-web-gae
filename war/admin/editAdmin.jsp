<%@page import="java.net.URLDecoder"%>
<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="datastore.Administrator" %>
<%@ page import="datastore.AdministratorManager" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/editAdmin.js"></script>
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

String userKey = URLDecoder.decode(request.getParameter("k"), "UTF8");
Administrator currentUserToEdit = AdministratorManager.getAdministrator(KeyFactory.stringToKey(userKey));
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Administrator</div>
	<form id="addAdminform" name="addAdminform" method="post" action="/manageUser?action=update&type=administrator&update_type=info&k=<%= userKey %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Edit administrator information:<%
String msg = request.getParameter("msg");
String action = request.getParameter("action");
if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("update")) {
%>
        <span class="warn_font_green">Edit Successful !</span>
        <% } 



%></td>
      <td height="60" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
      <td height="20">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row">*User type：</td>
      <td width="354" height="50">
        <input name="userType" type="text" class="textfield_style" id="userType" value="Administrator" readonly>
      </td>
      <td width="303" height="50"></td>
    </tr>
    
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label
       for="administratorName">  *User name：</label></td>
      <td width="354" height="50"><input type="text" name="administratorName" id="administratorName"class="textfield_style" value="<%= currentUserToEdit.getAdministratorName() %>"></td>
      <td width="303" height="50" align="left"><div id="empty_administratorName" class="errorText">*Enter Administrator name </div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="userEmail">*E-mail：</label></td>
      <td width="354" height="50">
        <input name="userEmail" type="text" class="textfield_style" id="userEmail" value="<%= currentUserToEdit.getUser().getUserEmail().getEmail() %>"></td>
        <td width="303" height="50" align="left"><div id="empty_userEmail" class="errorText">*Invalid e-mail address</div></td>
    </tr>
    <tr>
      <td height="50"></td>
      <td height="50" colspan="2" align="center" scope="row">
      <% if(sessionUser.getUserType() == User.UserType.ADMINISTRATOR){ %>
      <a class="changePasswordStyle" href="#" onclick="window.open(' editAdminPassword.jsp?k=<%= userKey %>', 'Change Password', config='height=752,width=1112px');">Change Password</a>
      <% } %>
      </td>
      <td height="50"></td>
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
    <tr><td colspan="4" align="center"><input type="submit" name="NewAdminSubmit" id="NewAdminSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='../admin/listAdmin.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>