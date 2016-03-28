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
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>

<!doctype html>
<html>
<head>
<link href="css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="css/public.css" rel="stylesheet" type="text/css">
<link href="css/addCustomer.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addCustomer.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>SMASRV IoT</title>
</head>

<%
	Printer printer = new Printer(Dictionary.Language.CHINESE);
String language = request.getParameter("lang") != null ? request.getParameter("lang") : "CH";
if(language.equals("EN")) {
		printer.setLanguage(Dictionary.Language.ENGLISH);
}
if(language.equals("CH")) {
		printer.setLanguage(Dictionary.Language.CHINESE);
}

// If user is logged in, don't allow this page to open
User sessionUser = (User) session.getAttribute("user");
if (sessionUser != null) {
	// we check the user type to send him to his/her own main page
   	switch(sessionUser.getUserType()) {
       	case ADMINISTRATOR:
               response.sendRedirect("/admin/listAdmin.jsp");
               break;
           case CUSTOMER:
               response.sendRedirect("/admin/listCustomer.jsp");
               break;
           case CUSTOMER_USER:
               response.sendRedirect("/admin/listEmployee.jsp");
               break;
           default:
               // there should be no other
               // type of user
               assert(false);
       }
}

BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
List<Country> countries = CountryManager.getAllCountries();
List<Region> regions = RegionManager.getAllRegions();
%>

<body>
<%@include file="../header/header.jsp" %>

<div id="content_area">
<div class="title_bar title_name">Create a new <span class="form_underline">customer</span></div>
<form id="addCustomerform" name="addCustomerform" method="post" action="/userRegistration?action=add&type=customer">

<!--"/manageUser?action=add&type=customer&keep_adding=true">-->
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Input customer's information：</td>
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
      <td height="50" align="right" scope="row"><label for="customerName">*Company：</label></td>
      <td width="354" height="50"><input type="text" name="customerName" id="customerName" class="textfield_style"></td>
      <td width="303" height="50" align="left"><div id="empty_customerName" class="errorText">*Enter customer name</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="userEmail">*Email：</label></td>
      <td width="354" height="50">
        <input name="userEmail" type="text" class="textfield_style" id="userEmail"></td>
      <td width="303" height="50"><div id="empty_userEmail" class="errorText">*Invalid e-mail address</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="userPassword">*Password：</label></td>
      <td width="354" height="50">
        <input name="userPassword" type="password" class="textfield_style" id="userPassword"></td>
      <td width="303" height="50"><div id="empty_userPassword" class="errorText">*Enter password</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="passwordCheck">*Confirm Password：</label></td>
      <td width="354" height="50">
        <input name="passwordCheck" type="password" class="textfield_style" id="passwordCheck"></td>
      <td width="303" height="50"><div id="empty_passwordCheck" class="errorText">*Password didn't match</div></td>
    </tr>
    
     <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerDescription">Description：</label></td>
      <td width="354" height="50">
        <input name="customerDescription" type="text" class="textfield_style" id="customerDescription"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerPhoneNumber">Phone Number：</label></td>
      <td width="354" height="50">
        <input name="customerPhoneNumber" type="text" class="textfield_style" id="customerPhoneNumber"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="countryId"> *Country：</label></td>
      <td width="354" height="50">
        <select name="countryId" class="textfield_style" id="countryId">
        <%
        	for (Country country : countries) {
        %>
          <option value="<%=KeyFactory.keyToString(country.getKey())%>"><%=country.getCountryName()%></option>
        <%
        	}
        %>
        </select></td>
        <td width="303" height="50" align="left"><div id="empty_countryId" class="errorText">*Select customer country</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="deparmentID">Region：</label></td>
      <td width="354" height="50">
        <select name="regionId" class="textfield_style" id="regionId">
        <%
        	for (Region region : regions) {
        %>
          <option value="<%= KeyFactory.keyToString(region.getKey()) %>"><%= region.getRegionName() %></option>
        <%
	    }
        %>
        </select></td>
        <td width="303" height="50" align="left"><div id="empty_regionId" class="errorText">*Select customer region</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerAddress1">Address：</label></td>
      <td width="354" height="50">
        <input name="customerAddress1" type="text" class="textfield_style" id="customerAddress1"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerAddress2"></label></td>
      <td width="354" height="50">
        <input name="customerAddress2" type="text" class="textfield_style" id="customerAddress2"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerWebsite">Website：</label></td>
      <td width="354" height="50">
        <input name="customerWebsite" type="text" class="textfield_style" id="customerWebsite"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    
    <!-- 
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerLogo">Logo：</label></td>
      <td width="354" height="50">
		<input type="file" name="customerLogo" class="textfield_style" value="" id="customerLogo" /></td>
	  <td width="303" height="50" align="left">&nbsp;</td>
	</tr>
    -->
    
    <tr class="form_underline">
      <td height="26" class="form_underline"></td>
      <td colspan="2" align="center" class="form_underline" scope="row">&nbsp;</td>
      <td class="form_underline"></td>
    </tr>
    <tr>
      <td height="20"></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr><td colspan="4" align="center"><input type="submit" name="NewCustomerSubmit" id="NewCustomerSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='../login.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>