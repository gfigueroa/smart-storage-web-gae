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

<div id="headerUconnect">
  <div id="header_logo_Uconnect"></div>
</div>
<div id="header_2"><table width="1050" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="10%" height="38"></td>
    <td align="right" class="header_font">&nbsp;</td>
    <td width="9%" class="header_font">
	</td>
  </tr>
</table>
</div>
