<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.CustomerUser" %>
<%@ page import="datastore.CustomerUserManager" %>
<%@ page import="datastore.StorageItem" %>
<%@ page import="datastore.StorageItemManager" %>
<%@ page import="datastore.StorageItemInstance" %>
<%@ page import="datastore.StorageItemInstanceManager" %>
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
<script src="../javascript/addItemInstance.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
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
		&& sessionUser.getUserType() != User.UserType.CUSTOMER) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

String storageItemKeyString = request.getParameter("storageItemId");
Key storageItemKey = KeyFactory.stringToKey(storageItemKeyString);
StorageItem storageItem = StorageItemManager.getStorageItem(storageItemKey);

List<CustomerUser> customerUsers = CustomerUserManager.getAllCustomerUsersFromCustomer(storageItemKey.getParent());
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Add a new item instance (<%= storageItem.getStorageItemName() %>)</div>
	<form id="addStorageform" name="addStorageform" method="post" action="/manageCustomer?action=add&type=storageItemInstance&keep_adding=true&storageItemId=<%= storageItemKeyString %>">
  
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td width="130" height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="73" colspan="3" align="left" class="form_underline" scope="row">Input a new item instance：</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
      <td height="20" colspan="2">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceOwner">Owner：</label></td>
      <td height="60" colspan="2">
        <select name="storageItemInstanceOwner" class="textfield_style" id="customerId">
            <option value="">Select a customer user</option>
          <%
          for (CustomerUser customerUser : customerUsers) {
          %>
          	<option value="<%= KeyFactory.keyToString(customerUser.getKey()) %>"><%= customerUser.getCustomerUserName() %></option>
          <%
          }
          %>
        </select></td>
        <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceSerialNumber">*Serial Number：</label></td>
      <td height="60" colspan="2">
        <input type="text" name="storageItemInstanceSerialNumber" class="textfield_style" id="storageItemInstanceSerialNumber"></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceLabel">*Label：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemInstanceLabel" type="text" class="textfield_style" id="storageItemInstanceLabel"></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceExpirationTime">Expiration Date：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemInstanceExpirationTime" type="text" class="textfield_style" id="storageItemInstanceExpirationTime" style="width:315px;" readonly="readonly"><label for="storageItemInstanceExpirationTime"><img src="../images/calendaricon.png" alt="" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceWorksheet">Worksheet：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemInstanceWorksheet" type="text" class="textfield_style" id="storageItemInstanceWorksheet"></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceIndicationLight">Indication  Light：</label></td>
      <td height="60" colspan="2"><p>
        <label>
          <input name="storageItemInstanceIndicationLight" type="radio" id="storageItemInstanceIndicationLight_0" value="Red">
          Red</label>
        <label>
          <input type="radio" name="storageItemInstanceIndicationLight" value="Green" id="storageItemInstanceIndicationLight_1" checked="checked">
          Green</label>
        <br>
      </p></td>
      <td width="303" height="60" align="left">&nbsp;</td>
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
    <tr><td colspan="5" align="center"><input type="submit" name="NewStorageSubmit" id="NewStorageSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listItemInstance.jsp?storageItemId=<%= storageItemKeyString %>'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>