<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.StorageItem" %>
<%@ page import="datastore.StorageItemManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>

<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/jquery.datetimepicker.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
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
		&&sessionUser.getUserType() != User.UserType.CUSTOMER
		&& sessionUser.getUserType() != User.UserType.CUSTOMER_USER) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

String storageItemKeyString = request.getParameter("k");
Key storageItemKey = KeyFactory.stringToKey(storageItemKeyString);
StorageItem storageItem = StorageItemManager.getStorageItem(storageItemKey);

Customer customer = CustomerManager.getCustomer(storageItemKey.getParent());

String pageFrom = request.getParameter("pageFrom");
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Item</div>
	<form id="addStorageform" name="addStorageform" method="post" action="/manageCustomer?action=update&type=storageItem&k=<%= storageItemKeyString %>&pageFrom=<%= pageFrom %>">
  
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td width="76" height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="73" colspan="3" align="left" class="form_underline" scope="row">Modify item information：</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td width="222" height="20" align="right" scope="row">&nbsp;</td>
      <td height="20" colspan="2">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="Customer">*Customer：</label></td>
      <td height="60" colspan="2">
        <input type="text" name="customer" class="textfield_style" id="customer" value="<%= customer.getCustomerName() %>" disabled></td>
        <td width="86" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemPartNumber"> Part Number：</label></td>
      <td height="60" colspan="2">
        <input type="text" name="storageItemPartNumber" class="textfield_style" id="storageItemPartNumber" value="<%= storageItem.getStorageItemPartNumber() != null ? storageItem.getStorageItemPartNumber() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="86" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemName">*Name：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemName" type="text" class="textfield_style" id="storageItemName" value="<%= storageItem.getStorageItemName() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="86" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemDescription">Description：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemDescription" type="text" class="textfield_style" id="storageItemDescription" value="<%= storageItem.getStorageItemDescription() != null ? storageItem.getStorageItemDescription() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="86" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemMSDLevel">MSD Level：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemMSDLevel" type="text" class="textfield_style" id="storageItemMSDLevel" value="<%= storageItem.getStorageItemMSDLevel() != null ? storageItem.getStorageItemMSDLevel() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="86" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td class="form_underline"></td>
      <td colspan="3" align="center" class="form_underline" scope="row">&nbsp;</td>
      <td class="form_underline"></td>
    </tr>
    <tr>
      <td></td>
      <td colspan="3" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr><td colspan="5" align="center">
    <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) { %>
    <input type="submit" name="NewStorageSubmit" id="NewStorageSubmit" class="css_btn_class" value="Save">&nbsp;
    <% } %>
    <input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='<%= pageFrom %>.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>