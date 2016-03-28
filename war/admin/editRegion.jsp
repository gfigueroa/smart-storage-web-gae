<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="datastore.Region" %>
<%@ page import="datastore.RegionManager" %>
<%@ page import="datastore.CountryManager" %>
<%@ page import="datastore.Country" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html >
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addRegion.js"></script>
<script src="../javascript/selectDate.js" type="text/javascript"></script>
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

String regionKey = request.getParameter("k");
Region region = RegionManager.getRegion(KeyFactory.stringToKey(regionKey));

Country country = CountryManager.getCountry(region.getKey().getParent());
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Region</div>
	<form id="addRegionform" name="addRegionform" method="post" action="/manageGlobalObject?action=update&type=region&k=<%= regionKey %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="73" align="left" class="form_underline" scope="row">Edit Region Information:</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td height="40" align="right" scope="row">&nbsp;</td>
      <td height="40" align="right" scope="row">&nbsp;</td>
      <td height="40">&nbsp;</td>
      <td height="40"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="countryId">Country name：</label></td>
      <td width="354" height="60">
        <input name="countryId" type="text" class="textfield_style" id="countryId" value="<%= country.getCountryName() %>" disabled></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="regionName">*Region Name：</label></td>
      <td width="354" height="60">
        <input name="regionName" type="text" class="textfield_style" id="regionName" value="<%= region.getRegionName() %>"></td>
      <td width="303" height="60" align="left"><div id="empty_RegionName" class="errorText">*Enter region name</div></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="regionTimeZone">*Region Time Zone：</label></td>
      <td width="354" height="60">
        <select name="regionTimeZone" class="textfield_style" id="regionTimeZone">
        <option value="-12.0" <%= region.getRegionTimeZone().equals("-12.0") ? "selected" : "" %>>(GMT -12:00) Eniwetok, Kwajalein</option>
      <option value="-11.0" <%= region.getRegionTimeZone().equals("-11.0") ? "selected" : "" %>>(GMT -11:00) Midway Island, Samoa</option>
      <option value="-10.0" <%= region.getRegionTimeZone().equals("-10.0") ? "selected" : "" %>>(GMT -10:00) Hawaii</option>
      <option value="-9.0" <%= region.getRegionTimeZone().equals("-9.0") ? "selected" : "" %>>(GMT -9:00) Alaska</option>
      <option value="-8.0" <%= region.getRegionTimeZone().equals("-8.0") ? "selected" : "" %>>(GMT -8:00) Pacific Time (US &amp; Canada)</option>
      <option value="-7.0" <%= region.getRegionTimeZone().equals("-7.0") ? "selected" : "" %>>(GMT -7:00) Mountain Time (US &amp; Canada)</option>
      <option value="-6.0" <%= region.getRegionTimeZone().equals("-6.0") ? "selected" : "" %>>(GMT -6:00) Central Time (US &amp; Canada), Mexico City</option>
      <option value="-5.0" <%= region.getRegionTimeZone().equals("-5.0") ? "selected" : "" %>>(GMT -5:00) Eastern Time (US &amp; Canada), Bogota, Lima</option>
      <option value="-4.0" <%= region.getRegionTimeZone().equals("-4.0") ? "selected" : "" %>>(GMT -4:00) Atlantic Time (Canada), Caracas, La Paz</option>
      <option value="-3.5" <%= region.getRegionTimeZone().equals("-3.5") ? "selected" : "" %>>(GMT -3:30) Newfoundland</option>
      <option value="-3.0" <%= region.getRegionTimeZone().equals("-3.0") ? "selected" : "" %>>(GMT -3:00) Brazil, Buenos Aires, Georgetown</option>
      <option value="-2.0" <%= region.getRegionTimeZone().equals("-2.0") ? "selected" : "" %>>(GMT -2:00) Mid-Atlantic</option>
      <option value="-1.0" <%= region.getRegionTimeZone().equals("-1.0") ? "selected" : "" %>>(GMT -1:00 hour) Azores, Cape Verde Islands</option>
      <option value="0.0" <%= region.getRegionTimeZone().equals("0.0") ? "selected" : "" %>>(GMT) Western Europe Time, London, Lisbon, Casablanca</option>
      <option value="1.0" <%= region.getRegionTimeZone().equals("1.0") ? "selected" : "" %>>(GMT +1:00 hour) Brussels, Copenhagen, Madrid, Paris</option>
      <option value="2.0" <%= region.getRegionTimeZone().equals("2.0") ? "selected" : "" %>>(GMT +2:00) Kaliningrad, South Africa</option>
      <option value="3.0" <%= region.getRegionTimeZone().equals("3.0") ? "selected" : "" %>>(GMT +3:00) Baghdad, Riyadh, Moscow, St. Petersburg</option>
      <option value="3.5" <%= region.getRegionTimeZone().equals("3.5") ? "selected" : "" %>>(GMT +3:30) Tehran</option>
      <option value="4.0" <%= region.getRegionTimeZone().equals("4.0") ? "selected" : "" %>>(GMT +4:00) Abu Dhabi, Muscat, Baku, Tbilisi</option>
      <option value="4.5" <%= region.getRegionTimeZone().equals("4.5") ? "selected" : "" %>>(GMT +4:30) Kabul</option>
      <option value="5.0" <%= region.getRegionTimeZone().equals("5.0") ? "selected" : "" %>>(GMT +5:00) Ekaterinburg, Islamabad, Karachi, Tashkent</option>
      <option value="5.5" <%= region.getRegionTimeZone().equals("5.5") ? "selected" : "" %>>(GMT +5:30) Bombay, Calcutta, Madras, New Delhi</option>
      <option value="5.75" <%= region.getRegionTimeZone().equals("5.75") ? "selected" : "" %>>(GMT +5:45) Kathmandu</option>
      <option value="6.0" <%= region.getRegionTimeZone().equals("6.0") ? "selected" : "" %>>(GMT +6:00) Almaty, Dhaka, Colombo</option>
      <option value="7.0" <%= region.getRegionTimeZone().equals("7.0") ? "selected" : "" %>>(GMT +7:00) Bangkok, Hanoi, Jakarta</option>
      <option value="8.0" <%= region.getRegionTimeZone().equals("8.0") ? "selected" : "" %>>(GMT +8:00) Beijing, Taiwan, Perth, Singapore, Hong Kong</option>
      <option value="9.0" <%= region.getRegionTimeZone().equals("9.0") ? "selected" : "" %>>(GMT +9:00) Tokyo, Seoul, Osaka, Sapporo, Yakutsk</option>
      <option value="9.5" <%= region.getRegionTimeZone().equals("9.5") ? "selected" : "" %>>(GMT +9:30) Adelaide, Darwin</option>
      <option value="10.0" <%= region.getRegionTimeZone().equals("10.0") ? "selected" : "" %>>(GMT +10:00) Eastern Australia, Guam, Vladivostok</option>
      <option value="11.0" <%= region.getRegionTimeZone().equals("11.0") ? "selected" : "" %>>(GMT +11:00) Magadan, Solomon Islands, New Caledonia</option>
      <option value="12.0" <%= region.getRegionTimeZone().equals("12.0") ? "selected" : "" %>>(GMT +12:00) Auckland, Wellington, Fiji, Kamchatka</option>
      </select></td>
        <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
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
    <tr><td colspan="4" align="center"><input type="submit" name="AddRegionSubmit" id="AddRegionSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listRegion.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>