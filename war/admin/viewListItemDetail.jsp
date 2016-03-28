<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>

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
%>

<body>
<%@include file="../header/header.jsp" %>

<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="93%" height="50" valign="middle">Medicine(Door 1 → Partition 2)</td>
    <td width="7%" valign="middle"><a href="listItem.jsp"><img src="../images/backicon.png" alt="" width="35" height="35" class="editList_backicon_position"></a></td>
    </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td width="25%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue radiusLeft">User</td>
      <td align="center" valign="middle" class="listTitle_CSS form_underline_blue ">Date</td>
      <td width="20%" align="center" valign="middle" class="listTitle_CSS form_underline_blue ">Change</td>
      <td width="20%" align="center" valign="middle" class="listTitle_CSS form_underline_blue radiusRight">Remain</td>
    </tr>
    <tr class="listCotent_CSS">
      <td width="25%" height="30" align="center" class="font_18 form_underline_gray">Andy</td>
      <td height="30" align="center" class="font_18 form_underline_gray">2014/04/22 15:00</td>
      <td width="20%" height="30" align="center" class="font_18 form_underline_gray">In x 2</td>
      <td width="20%" height="30" align="center" class="font_18 form_underline_gray">x 10</td>
    </tr>
      <tr class="listCotent_CSS">
        <td width="25%" height="30" align="center" class="font_18 form_underline_gray">John</td>
        <td height="30" align="center" class="font_18 form_underline_gray">2014/04/20 15:00</td>
        <td width="20%" height="30" align="center" class="font_18 form_underline_gray">Out x1 </td>
        <td width="20%" height="30" align="center" class="font_18 form_underline_gray">x 8</td>
    </tr>
  </table>
  
</div>

<%@include file="../footer/footer.jsp" %>
</body>
</html>