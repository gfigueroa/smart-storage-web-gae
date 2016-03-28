<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.CustomerUser" %>
<%@ page import="datastore.CustomerUserManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.StorageDeviceDoor" %>
<%@ page import="datastore.StorageDeviceDoorManager" %>
<%@ page import="datastore.StorageDevicePartition" %>
<%@ page import="datastore.StorageDevicePartitionManager" %>
<%@ page import="datastore.DeviceModelDoor" %>
<%@ page import="datastore.DeviceModelDoorManager" %>
<%@ page import="datastore.DeviceModelPartition" %>
<%@ page import="datastore.DeviceModelPartitionManager" %>
<%@ page import="datastore.StorageItem" %>
<%@ page import="datastore.StorageItemManager" %>
<%@ page import="datastore.StorageItemInstance" %>
<%@ page import="datastore.StorageItemInstanceManager" %>
<%@ page import="datastore.StorageItemTransaction" %>
<%@ page import="datastore.StorageItemTransactionManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.DateManager" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>SMASRV IoT</title>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/editListItemDetail.js"></script>
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

String storageItemKeyString = request.getParameter("storageItemId");
Key storageItemKey = KeyFactory.stringToKey(storageItemKeyString);
StorageItem storageItem = StorageItemManager.getStorageItem(storageItemKey);

String storageDevicePartitionKeyString = request.getParameter("storageDevicePartitionId");
Key storageDevicePartitionKey = KeyFactory.stringToKey(storageDevicePartitionKeyString);
StorageDevicePartition storageDevicePartition = StorageDevicePartitionManager.getStorageDevicePartition(storageDevicePartitionKey);
DeviceModelPartition deviceModelPartition = DeviceModelPartitionManager.getDeviceModelPartition(storageDevicePartition.getDeviceModelPartition());
StorageDeviceDoor storageDeviceDoor = StorageDeviceDoorManager.getStorageDeviceDoor(storageDevicePartitionKey.getParent());
DeviceModelDoor deviceModelDoor = DeviceModelDoorManager.getDeviceModelDoor(storageDeviceDoor.getDeviceModelDoor());
StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(storageDevicePartitionKey.getParent().getParent());

ArrayList<StorageItemTransaction> storageItemTransactions = StorageItemTransactionManager.getStorageItemTransactionsWithStorageDevicePartitionAndStorageItem(storageDevicePartitionKey, storageItemKey);
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td width="5%" height="50" valign="middle"><a href="listStorage.jsp"><img src="../images/backicon.png" alt="" width="28" height="28" class="editList_backicon_position"></a></td>
    <td width="65%" height="50"><%= storageItem.getStorageItemName() %> ( Item Instance Name ) </td>
    <td width="30%" height="50" align="center" style="border-left:1px solid rgba(99,99,99,0.50);">Transaction History</td>
    
    </tr>
</table>
  </div>
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td width="30%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusLeft">Location</td>
      <td width="30%" align="center" class="listTitle_CSS form_underline_blue ">Operator</td>
      <td width="20%" align="center" class="listTitle_CSS form_underline_blue ">Date</td>
      <td width="20%" height="40" align="center" class="listTitle_CSS form_underline_blue radiusRight">Remove</td>
    </tr>
    
    <%
    for (StorageItemTransaction storageItemTransaction : storageItemTransactions) {
    	CustomerUser customerUser = CustomerUserManager.getCustomerUser(storageItemTransaction.getStorageItemTransactionPerformedBy());
    	StorageItemInstance storageItemInstance = StorageItemInstanceManager.getStorageItemInstance(storageItemTransaction.getStorageItemInstance());
    %>
	    <tr class="listCotent_CSS">
	      <td width="30%" height="30" align="center" class="font_18 form_underline_gray"><%= storageDevice.getStorageDeviceSerialNumber() %> → <%= "Door " + deviceModelDoor.getDeviceModelDoorNumber() %> <br>
	      → <%= deviceModelPartition.getDeviceModelPartitionName() %></td>
	      <td width="30%" height="30" align="center" class="font_18 form_underline_gray"><%= customerUser.getCustomerUserName() %></td>
	      <td width="20%" height="30" align="center" class="font_18 form_underline_gray"><%= DateManager.printDateAsString(storageItemTransaction.getStorageItemTransactionCreationDate()) %></td>
	      <td width="20%" height="30" align="center" class="font_18 form_underline_gray"><a href="?storageItemId=<%= storageItemKeyString %>&storageDevicePartitionId=<%= storageDevicePartitionKeyString %>&storageItemInstanceId=<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>" title="remove"><img src="../images/removeicon.png" width="24" height="24" onClick="ItemRemove('<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>');"></a></td>
	    </tr>
    <%
    }
    %>
  </table>

<%
String storageItemInstanceKeyString = request.getParameter("storageItemInstanceId");
StorageItemInstance storageItemInstance = null;
if (storageItemInstanceKeyString != null) {
	Key storageItemInstanceKey = KeyFactory.stringToKey(storageItemInstanceKeyString);
	storageItemInstance = StorageItemInstanceManager.getStorageItemInstance(storageItemInstanceKey);
}

if (storageItemInstance != null) {
%>
	<div id="RemoveArea">
		<form id="ItemStoreform" name="ItemStoreform" method="post" action="/manageCustomer?action=add&type=storageItemTransaction&storageItemTransactionAction=remove&storageItemInstanceId=<%= storageItemInstanceKeyString != null ? storageItemInstanceKeyString : "" %>&storageDevicePartitionId=<%= storageDevicePartitionKeyString %>&pageFrom=editListItemDetail">
		  
		  <table width="800" border="0" align="center" cellpadding="0" cellspacing="1">
		    <tr>
		      <td width="130" height="20" align="right" scope="row">&nbsp;</td>
		      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
		      <td height="20">&nbsp;</td>
		      <td height="20"></td>
		    </tr>
		    <tr>
		      <td class="form_underline"></td>
		      <td colspan="2" align="center" class="form_underline" scope="row">&nbsp;</td>
		      <td class="form_underline"></td>
		    </tr>
		    <tr>
		      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceLabel">Instance label：</label></td>
		      <td height="60" colspan="2">
		        <input name="storageItemInstanceLabel" type="text" class="textfield_style" id="storageItemInstanceLabel" value="<%= storageItemInstance.getStorageItemInstanceLabel() %>" readonly></td>
		      <td width="303" height="60" align="left">&nbsp;</td>
		    </tr>
		    <tr>
		      <td height="40" colspan="2" align="right" valign="middle" scope="row">*Performed by：</td>
		      <td height="40" valign="middle"><select name="storageItemTransactionPerformedBy" class="textfield_style" id="storageItemTransactionPerformedBy">
		        <option value="" selected="selected">(Employee)</option>
		        <%
		          List<CustomerUser> customerUsers;
		    	  switch (sessionUser.getUserType()) {
		    	  	  case ADMINISTRATOR:
		    		  	  customerUsers = CustomerUserManager.getAllCustomerUsersFromCustomer(storageItemKey.getParent());
		    		  	  break;
		    	  	  case CUSTOMER:
		    	  		  customerUsers = CustomerUserManager.getAllCustomerUsersFromCustomer(storageItemKey.getParent());
		    		  	  break;
		    	  	  case CUSTOMER_USER:
		    	  		  CustomerUser customerUser = CustomerUserManager.getCustomerUser(sessionUser);
		    	  		  customerUsers = new ArrayList<CustomerUser>();
		    	  		  customerUsers.add(customerUser);
		    	  		  break;
		    	      default:
		    	    	  customerUsers = null;
		    	    	  break;
		    	  }
		        for (CustomerUser customerUser : customerUsers) {
		        %>
		        	<option value="<%= KeyFactory.keyToString(customerUser.getKey()) %>"><%= customerUser.getCustomerUserName() %></option>
		        <%
		        }
		        %>
		      </select></td>
		      <td height="40" valign="middle">&nbsp;</td>
		    </tr>
		    <tr>
		      <th height="40" colspan="2" align="center" valign="bottom" scope="row">Purpose：</th>
		      <td height="40">&nbsp;</td>
		      <td width="303" height="40">&nbsp;</td>
		    </tr>
		    <tr>
		      <td height="60" colspan="4" align="center" scope="row">Do you want to remove this item? Leave a work sheet to us.</td>
		      </tr>
		    <tr>
		      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceWorksheet">Worksheet：</label></td>
		      <td height="60" colspan="2">
		        <input name="storageItemInstanceWorksheet" type="text" class="textfield_style" id="storageItemInstanceWorksheet" value="<%= storageItemInstance.getStorageItemInstanceWorksheet() %>"></td>
		      <td width="303" height="60" align="left">
		      <input name="storehiddenItemId" type="hidden" id="storehiddenItemId" value="store id undefine">
		      </td>
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
		    <tr><td colspan="4" align="center"><input type="submit" name="ItemStoreSubmit" id="ItemStoreSubmit" class="css_btn_class" value="Yes">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="CancelEdit();" value="No"></td>
		      </tr>
		</table>
		</form>
	</div>
<%
}
%>
  
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>