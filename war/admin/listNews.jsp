<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
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
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" align="left" valign="middle">News List</td>
    <td width="2%" valign="middle"><a href="addNews.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="16%" valign="middle"><a  href="addNews.jsp"><span class="font_18">&nbsp;Add News</span></a></td>
  </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <th width="13%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue radiusLeft">Logo</th>
      <td width="65%" height="40" align="center" valign="middle" class="listTitle_CSS form_underline_blue "><strong>News Title</strong></td>
      <td width="8%" align="center" valign="middle" class="listTitle_CSS form_underline_blue "><strong>Status</strong></td>
      <td width="14%" align="center" valign="middle" class="listTitle_CSS form_underline_blue radiusRight"><strong>Actions</strong></td>
    </tr>
    
    <tr class="listCotent_CSS">
      <td height="30" align="center" class="font_18 form_underline_gray"><img src="../images/newsPhoto/news_test1.jpg" width="100" height="75" alt=""/></td>
      <td height="30" align="center" class="font_18 form_underline_gray">News Title</td>
      <td align="center" class="font_18 form_underline_gray">Active</td>
      <td width="14%" align="center" class="font_18 form_underline_gray"><img src="../images/view.png" width="24" height="24" alt=""/> <img src="../images/pencil_32.png" width="24" height="24" alt=""/> <img src="../images/close_32.png" width="24" height="24" alt=""/></td>
    </tr>
    
   
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>