<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Country" %>
<%@ page import="datastore.CountryManager" %>
<%@ page import="datastore.Region" %>
<%@ page import="datastore.RegionManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

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

List<Country> countries = CountryManager.getAllCountries();
%>
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>SMASRV IoT</title>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
</head>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" valign="middle">Region List</td>
    <td width="2%" valign="middle"><a href="addRegion.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="16%" valign="middle"><a  href="addRegion.jsp"><span class="font_18">&nbsp;Add Region</span></a></td>
  </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <%
    	for (Country country : countries) {
                	List<Region> regions = RegionManager.getAllRegionsFromCountry(country.getKey());
    %>
	    <tr>
	      <td height="40" colspan="3" align="left" class="listTitle_CSS form_underline_blue radiusLeft"><%=country.getCountryName()%></td>
	    </tr>
    	<tr class="listCotent_CSS">
	      <th width="165" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Region Name</th>
	      <th width="13%" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Edit</th>
	      <th width="11%" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Delete</th>
	    </tr>
	    <%
	    	for (Region region : regions) {
	    %>
	    <tr class="listCotent_CSS">
	      <td height="30" align="left" class="font_18 form_underline_gray">&nbsp;<%= region.getRegionName() %></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><a href="editRegion.jsp?k=<%= KeyFactory.keyToString(region.getKey()) %>"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
	      <td height="30" align="center" class="font_18 form_underline_gray"><a href="/manageGlobalObject?action=delete&type=region&k=<%=  KeyFactory.keyToString(region.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a></td>
	    </tr>
	<%
    	}
    }
	%>
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>