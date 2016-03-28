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
<script src="../javascript/listStorage.js"></script>
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

List<StorageDevice> storageDevices;
switch (sessionUser.getUserType()) {
	case ADMINISTRATOR:
		storageDevices = StorageDeviceManager.getAllStorageDevices();
		break;
	case CUSTOMER:
		storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomer(sessionUser.getKey().getParent());
		break;
	case CUSTOMER_USER:
		storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomer(sessionUser.getKey().getParent().getParent());
		break;
	default:
		storageDevices = null;
		break;
}
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <form name="ItemSearchForm" id="ItemSearchForm" method="GET" action="">
    <table width="800" border="0" align="center" cellpadding="1" cellspacing="0">
  <tr>
    <td width="104%" height="85" scope="row" class="form_underline"><table border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <th width="354" height="41" scope="row"> <input name="searchItem" type="text" class="textfield_style" id="searchItem" placeholder="Search Item">
        </th>
        <td width="40" height="41" align="center" valign="bottom" style="background-color:#D8D8D8;border: 0px solid #CCC;cursor:pointer;"><img id="searchItemButton" src="../images/search-icon-md.png" alt="" width="28" height="27" style="cursor:pointer;"></td>
      </tr>
    </table></td>
    </tr>
  <tr>
    <td height="15"  scope="row"></td>
  </tr>
  </table>
</form>
<table class="listItemHide" width="800" border="0" align="center" cellpadding="0" cellspacing="0">
	<%
	for (StorageDevice storageDevice : storageDevices) {
		// HashMap<StorageItem Key, HashMap of transactions per partition>
		HashMap<Key, HashMap<Key, ArrayList<StorageItemTransaction>>> transactionMap = StorageItemTransactionManager.getStorageItemTransactionsWithStorageDevice(storageDevice.getKey());
		if (!transactionMap.isEmpty()) {
	%>
		    <tr OnClick='ItemShowFunction(0, this);' style="cursor:pointer;">
		      <td height="40" colspan="4" align="left" class="listTitle_CSS form_underline_blue radiusLeft"><%= storageDevice.getStorageDeviceSerialNumber() %></td>
		      <td height="40" colspan="2" align="right" class="listTitle_CSS form_underline_blue radiusRight" style="padding-right:50px;">(<%= transactionMap.size() + (transactionMap.size() == 1 ? " item" : " items") %>)</td>
		    </tr>
		    <tr class="listCotent_CSS">
		      <th align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Item Name</th>
		      <th width="60" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">  Count</th>
		      <th width="160" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Last In</th>
		      <th width="200" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Location</th>
		      <th width="60" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Edit</th>
		      <th width="100" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Transactions</th>
		    </tr>
		    
		    <%
		    for (Key storageItemKey : transactionMap.keySet()) {
			    int storageItemCount = 0;
		    	StorageItem storageItem = StorageItemManager.getStorageItem(storageItemKey);
		    	HashMap<Key, ArrayList<StorageItemTransaction>> transactionsPerPartition = transactionMap.get(storageItemKey);
		    	for (Key storageDevicePartitionKey : transactionsPerPartition.keySet()) {
		    		StorageDevicePartition storageDevicePartition = StorageDevicePartitionManager.getStorageDevicePartition(storageDevicePartitionKey);
		    		DeviceModelPartition deviceModelPartition = DeviceModelPartitionManager.getDeviceModelPartition(storageDevicePartition.getDeviceModelPartition());
		    		StorageDeviceDoor storageDeviceDoor = StorageDeviceDoorManager.getStorageDeviceDoor(storageDevicePartitionKey.getParent());
		    		DeviceModelDoor deviceModelDoor = DeviceModelDoorManager.getDeviceModelDoor(storageDeviceDoor.getDeviceModelDoor());
		    		
		    		ArrayList<StorageItemTransaction> storageItemTransactions = transactionsPerPartition.get(storageDevicePartitionKey);
		    		CustomerUser lastIn = CustomerUserManager.getCustomerUser(storageItemTransactions.get(0).getStorageItemTransactionPerformedBy());
		    		Date lastDate = storageItemTransactions.get(0).getStorageItemTransactionCreationDate();
		    %>
				    <tr class="listCotent_CSS">
				      <td align="center" class="font_18 form_underline_gray"><%= storageItemCount == 0 ? storageItem.getStorageItemName() : "" %></td>
				      <td width="60" align="center" class="font_18 form_underline_gray"><%= storageItemTransactions.size() %></td>
				      <td width="160" align="center" class="font_18 form_underline_gray"><%= lastIn.getCustomerUserName() %><br>
				      <span style="font-weight: 700; color: #999; font-size: 14px;"><%= DateManager.printDateAsString(lastDate) %></span></td>
				      <td width="200" align="center" class="font_18 form_underline_gray"><%= "Door " + deviceModelDoor.getDeviceModelDoorNumber() %> → <%= deviceModelPartition.getDeviceModelPartitionName() %></td>
				      <td width="60" align="center" class="font_18 form_underline_gray"><a href="editItem.jsp?k=<%= KeyFactory.keyToString(storageItemKey) %>&pageFrom=listStorage" title="edit"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a></td>
				      <td width="100" align="center" class="font_18 form_underline_gray"><a href="editListItemDetail.jsp?storageItemId=<%= KeyFactory.keyToString(storageItemKey) %>&storageDevicePartitionId=<%= KeyFactory.keyToString(storageDevicePartition.getKey()) %>" title="transaction"><img src="../images/transaction.png" alt="" width="28" height="28"></a></td>
				    </tr>
	<%
					storageItemCount++;
		    	}
		    }
		}
	}
	%>
</table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>