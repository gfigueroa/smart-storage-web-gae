<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="datastore.Department" %>
<%@ page import="datastore.DepartmentManager" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html >
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addDepartment.js"></script>
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

String departmentKeyString = request.getParameter("k");
Long departmentKey = Long.parseLong(departmentKeyString);
Department department = DepartmentManager.getDepartment(departmentKey);
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Department</div>
<form id="addDepartmentform" name="addDepartmentform" method="post" action="/manageGlobalObject?action=update&type=department&k=<%= departmentKey %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="73" align="left" class="form_underline" scope="row">Input Department：</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td class="form_underline" width="130" height="120" align="right" scope="row">&nbsp;</td>
      <td class="form_underline" height="120" align="right" scope="row">*Department Name：</td>
      <td class="form_underline" width="354" height="120">
        <input name="departmentName" type="text" class="textfield_style" id="departmentName" value="<%= department.getDepartmentName() %>">
        </td>
      <td class="form_underline" width="303" height="120"><div id="empty_DepartmentName" class="errorText">*Enter department name</div></td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td colspan="4" align="center"><input type="submit" name="NewDepartmentNameSubmit" id="NewDepartmentNameSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listDepartment.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>