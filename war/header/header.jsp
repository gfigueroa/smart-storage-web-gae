<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>

<!doctype html>

<meta charset="utf-8">
<title></title>
<link href="../css/header.css" rel="stylesheet" type="text/css">

<%
User loginUser = (User)session.getAttribute("user");
%>

<div id="header">
  <div id="header_logo"></div>
</div>
<div id="header_2"><table width="1050" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="10%" height="38"><div id="home_link"><a href="" class="header_font"><img src="../images/home1.png" width="24" height="24" > Home</a></div></td>
    <td align="right" class="header_font"><%= loginUser != null ? "(" + loginUser.getUserEmail().getEmail() + ")" : "" %>&nbsp;&nbsp;</td>
    <td width="9%" class="header_font">
    <%
	if (loginUser == null) {
	%>
    <a href="/login.jsp" class="header_font">Login</a>
    <% } else { %>
    <a href="/handleSession?action=destroy" class="header_font">Logout</a>
    <% } %>
	</td>
  </tr>
</table>
</div>
