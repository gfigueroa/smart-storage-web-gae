<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.Customer.Status" %>
<%@ page import="datastore.CountryManager" %>
<%@ page import="datastore.RegionManager" %>
<%@ page import="datastore.Country" %>
<%@ page import="datastore.Region" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>

<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/addCustomer.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/editCustomer.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>SMASRV IoT</title>
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

String userKey = URLDecoder.decode(request.getParameter("k"), "UTF8");
Customer currentUserToEdit = CustomerManager.getCustomer(KeyFactory.stringToKey(userKey));

List<Country> countries = CountryManager.getAllCountries();
Country customerCountry = CountryManager.getCountry(currentUserToEdit.getRegion().getParent());

List<Region> regions = RegionManager.getAllRegions();
Region customerRegion = RegionManager.getRegion(currentUserToEdit.getRegion());
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit <span class="form_underline">Customer</span></div>
	<form id="addCustomerform" name="addCustomerform" method="post" action="/manageUser?action=update&type=customer&update_type=info&k=<%=userKey%>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Edit customer information：<%
      	String msg = request.getParameter("msg");
            String action = request.getParameter("action");
            if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("update")) {
      %>
        <span class="warn_font_green">Edit Successful !</span>
        <%
        	}
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
      <td height="50" align="right" scope="row"><label
       for="CustomerName">*Company：</label></td>
      <td width="354" height="50"><input type="text" name="customerName" id="customerName"class="textfield_style" value="<%=currentUserToEdit.getCustomerName()%>" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : ""%>></td>
      <td width="303" height="50" align="left"><div id="empty_CustomerName" class="errorText">*Enter company name</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="userEmail">*Email：</label></td>
      <td width="354" height="50">
        <input name="userEmail" type="text" class="textfield_style" id="userEmail" value="<%=currentUserToEdit.getUser().getUserEmail().getEmail()%>" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : ""%>></td>
      <td width="303" height="50"><div id="empty_userEmail" class="errorText">*Invalid e-mail address</div></td>
    </tr>
    
     <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerDescription">Description：</label></td>
      <td width="354" height="50">
        <input name="customerDescription" type="text" class="textfield_style" id="customerDescription" value="<%=currentUserToEdit.getCustomerDescription()%>" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : ""%>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerPhoneNumber">Phone Number：</label></td>
      <td width="354" height="50">
        <input name="customerPhoneNumber" type="text" class="textfield_style" id="customerPhoneNumber" value="<%=currentUserToEdit.getCustomerPhoneNumber() != null ? currentUserToEdit.getCustomerPhoneNumber().getNumber() : ""%>" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : ""%>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="regionId"> *Country：</label></td>
      <td width="354" height="50">
        <select name="countryId" class="textfield_style" id="countryId" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : ""%> <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : ""%>>
        <%
        	for (Country country : countries) {
        %>
          <option value="<%=KeyFactory.keyToString(country.getKey())%>" <%=country.equals(customerCountry)%>><%=country.getCountryName()%></option>
        <%
        	}
        %>
        </select></td>
        <td width="303" height="50" align="left"><div id="empty_regionId" class="errorText">*Select customer country</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="regionId">*Region：</label></td>
      <td width="354" height="50">
        <select name="regionId" class="textfield_style" id="regionId" <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : ""%> <%=sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : ""%>>
        <%
        	for (Region region : regions) {
        %>
          <option value="<%= KeyFactory.keyToString(region.getKey()) %>" <%= region.equals(customerRegion) ? "selected" : "" %>><%= region.getRegionName() %></option>
        <%
	    }
        %>
        </select></td>
        <td width="303" height="50" align="left"><div id="empty_regionId" class="errorText">*Select customer region</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerAddress">Address：</label></td>
      <td width="354" height="50">
        <input name="customerAddress1" type="text" class="textfield_style" id="customerAddress1" value="<%= currentUserToEdit.getCustomerAddress() != null ? currentUserToEdit.getCustomerAddress().getAddress() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>>
        <input name="customerAddress2" type="text" class="textfield_style" id="customerAddress2" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerWebsite">Website：</label></td>
      <td width="354" height="50">
        <input name="customerWebsite" type="text" class="textfield_style" id="customerWebsite" value="<%= currentUserToEdit.getCustomerWebsite() != null ? currentUserToEdit.getCustomerWebsite().getValue() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerComments">Comments：</label></td>
      <td width="354" height="50">
        <input name="customerComments" type="text" class="textfield_style" id="customerComments" value="<%= currentUserToEdit.getCustomerComments() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerLogo">Trademark：</label></td>
      <td width="354" height="50"><label for="customerLogo">
      <div class="textfield_style filepathword" id="trademarkPath"><span style='color: #999;'>Click to choose file</span></div></label>
        <input name="customerLogo" type="file" style="display:none;" class="textfield_style" id="customerLogo" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>>
      </td>
        <td width="303" height="50" align="left"><div id="trademarkimg"></div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerStatus">*Status：</label></td>
      <td width="354" height="50">
        <select name="customerStatus" class="textfield_style" id="customerStatus" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR ? "disabled" : "" %>>
          <option value="unconfirmed" <%= currentUserToEdit.getCustomerStatus() == Status.UNCONFIRMED ? "selected" : "" %>>Unconfirmed</option>
          <option value="active" <%= currentUserToEdit.getCustomerStatus() == Status.ACTIVE ? "selected" : "" %>>Active</option>
          <option value="inactive" <%= currentUserToEdit.getCustomerStatus() == Status.INACTIVE ? "selected" : "" %>>Inactive</option>
          <option value="disabled" <%= currentUserToEdit.getCustomerStatus() == Status.DISABLED ? "selected" : "" %>>Disabled</option>
        </select></td>
        <td width="303" height="50" align="left"><div id="empty_customerStatus" class="errorText">*Select customer status</div></td>
    </tr>
    <tr>
      <td height="50"></td>
      <td height="50" colspan="2" align="center" scope="row">
      <% if(sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER){ %>
      <a class="changePasswordStyle" href="#" onclick="window.open(' editCustomerPassword.jsp?k=<%= userKey %>', 'Change Password', config='height=752,width=1112px');">Change Password</a>
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
    <tr><td colspan="4" align="center">
    <% if(sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER){ %>
    <input type="submit" name="NewCustomerSubmit" id="NewCustomerSubmit" class="css_btn_class" value="Save">&nbsp;
    <% } %>
    <input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listCustomer.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>