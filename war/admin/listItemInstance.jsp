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
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.ArrayList" %>
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
<script src="../javascript/listItemInstance.js"></script>
<script type="text/javascript">
function MM_jumpMenu(targ,selObj,restore){ //v3.0
  eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
  if (restore) selObj.selectedIndex=0;
}
</script>
</head>

<%
User sessionUser = (User)session.getAttribute("user");

if (sessionUser == null) {
	response.sendRedirect("../login.jsp");
}
else {
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR &&
			sessionUser.getUserType() != User.UserType.CUSTOMER
			&& sessionUser.getUserType() != User.UserType.CUSTOMER_USER) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

String storageItemKeyString = request.getParameter("storageItemId");
Key storageItemKey = KeyFactory.stringToKey(storageItemKeyString);
StorageItem storageItem = StorageItemManager.getStorageItem(storageItemKey);

List<StorageItemInstance> storageItemInstances = StorageItemInstanceManager.getAllStorageItemInstancesFromStorageItem(storageItemKey);
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>

<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="7%" height="50" valign="middle"><a href="listItem.jsp"><img src="../images/backicon.png" alt="" width="28" height="28" class="editList_backicon_position"></a></td>
    <td width="70%" height="50" align="left" valign="middle">Item Instance List (<%= storageItem.getStorageItemName() %>)</td>
    <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) { %>
    <td width="3%" valign="middle"><a href="addItemInstance.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="20%" valign="middle"><a  href="addItemInstance.jsp?storageItemId=<%= storageItemKeyString %>"><span class="font_18">Add Item Instance</span></a></td>
    <% } %>
  </tr>
</table>
  </div>
  <form name="ItemSearchForm" method="GET" action="searchItemResult.jsp">
  <table width="800" border="0" align="center" cellpadding="1" cellspacing="0">
  <tr>
    <td width="104%" height="85" scope="row" class="form_underline"><table width="394" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <th width="354" height="41" scope="row"> <input name="searchItem" type="text" class="textfield_style" id="searchItem" placeholder="Search Item">
        </th>
        <td width="40" height="41" align="center" valign="bottom" style="background-color:#D8D8D8;border: 0px solid #CCC;cursor:pointer;"><label for="searchButton"><img src="../images/search-icon-md.png" alt="" width="28" height="27" style="cursor:pointer;"></label></td>
        
      </tr>
    </table>
      <table width="130" border="0" cellpadding="0" cellspacing="0" id="advanced_search_link" name="advanced_search_link">
        <tbody>
          <tr>
            <td><span style="font-size:12px">Advanced Search</span></td>
          </tr>
        </tbody>
      </table>
      <div id="advanced_search"><table width="100%"  border="0" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td width="36%" height="45" align="right"><label for="searchOwner">Owner：</label></td>
      <td width="52%" height="45"><input type="text" name="searchOwner" class="search_textfield_style" id="searchOwner" value=""></td>
      </tr>
    <tr>
      <td width="36%" height="45" align="right"><label for="searchOwner">Serial Number：</label></td>
      <td width="52%" height="45"><input type="text" name="searchSerialNumber" class="search_textfield_style" id="searchOwner" value=""></td>
      </tr>
    
    <tr>
      <td height="45" align="right"><label for="searchDate">Expiration Date：</label></td>
      <td height="45"><input type="text" name="searchDate" class="search_textfield_style" id="searchDate" value=""></td>
      </tr>
    <tr>
      <td height="45" align="right"><label for="searchWorksheet">Worksheet：</label></td>
      <td height="45"><input type="text" name="searchWorksheet" class="search_textfield_style" id="searchWorksheet" value=""></td>
      </tr>
    <tr>
      <td height="60" colspan="2" align="center"><input type="submit" name="Advanced_Search" id="Advanced_Search" class="css_btn_class" value="Search">
        <input type="button" name="Advanced_Search" id="Advanced_Search_close" class="css_btn_class" value="Close"></td>
      </tr>
  </tbody>
</table>
</div></td>
    </tr>
  <tr>
    <td height="15"  scope="row"></td>
  </tr>
  </table>
</form>
<table class="listItemHide" width="820" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr OnClick='ItemShowFunction(0, this);' style="cursor:pointer;">
      <td height="40" colspan="3" align="left" class="listTitle_CSS form_underline_blue radiusLeft"><%= storageItem.getStorageItemName() %>&nbsp;&nbsp;&nbsp;(<%= storageItem.getStorageItemPartNumber() %>)</td>
      <td height="40" colspan="3" align="right" class="listTitle_CSS form_underline_blue radiusRight" style="padding-right:50px;">(<%= storageItemInstances.size() + (storageItemInstances.size() == 1 ? " instance" : " instances") %>)</td>
    </tr>
    <tr class="listCotent_CSS">
      <th width="200" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Instance Label</th>
      <th width="150" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Serial No.</th>
      <th align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Equipment Serial No.</th>
      <th width="60" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Store</th>
      <th width="70" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Remove</th>
      <th width="100" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Action</th>
    </tr>
	<%
	for (StorageItemInstance storageItemInstance : storageItemInstances) {
		StorageItemTransaction storageItemTransaction = StorageItemTransactionManager.getLastStorageItemTransactionFromStorageItemInstance(storageItemInstance.getKey());
		StorageDevice storageDevice = null;
		StorageDevicePartition storageDevicePartition = null;
		DeviceModelPartition deviceModelPartition = null;
		DeviceModelDoor deviceModelDoor = null;
		if (storageItemTransaction != null) {
			if (storageItemTransaction.getStorageDevicePartition() != null && storageItemTransaction.getStorageItemTransactionAction() == StorageItemTransaction.StorageItemTransactionAction.STORE) {
				storageDevice = StorageDeviceManager.getStorageDevice(storageItemTransaction.getStorageDevicePartition().getParent().getParent());
				storageDevicePartition = StorageDevicePartitionManager.getStorageDevicePartition(storageItemTransaction.getStorageDevicePartition());
				deviceModelPartition = DeviceModelPartitionManager.getDeviceModelPartition(storageDevicePartition.getDeviceModelPartition());
				deviceModelDoor = DeviceModelDoorManager.getDeviceModelDoor(deviceModelPartition.getKey().getParent());
			}
		}
	%>
	    <tr class="listCotent_CSS">
	      <td width="200" align="center" class="font_16 form_underline_gray"><%= storageItemInstance.getStorageItemInstanceLabel() %></td>
	      <td width="150" align="center" class="font_16 form_underline_gray"><%= storageItemInstance.getStorageItemInstanceSerialNumber() %></td>
          <td align="center" class="font_16 form_underline_gray listItemInstanceShowLocation" locationdoor="<%= storageDevice != null ? "Door " + deviceModelDoor.getDeviceModelDoorNumber() : "" %>" locationpart="<%= storageDevice != null ? deviceModelPartition.getDeviceModelPartitionName() : "" %>"><%= storageDevice != null ? storageDevice.getStorageDeviceSerialNumber() : "-" %></td>
	      <td width="60" align="center" class="font_16 form_underline_gray">
	      <%
          if (storageDevice == null) {
          %>
	      <a href="?storageItemId=<% out.print(request.getParameter("storageItemId")); %>&storageItemInstanceId=<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>&action=store" title="store"><img src="../images/storeicon.png" width="24" height="24" onClick="ItemStore('');"></a>
	      <%
          }
	      %>
	      </td>
          <td width="70" align="center" class="font_16 form_underline_gray">
          <%
          if (storageDevice != null) {
          %>
          <a href="?storageItemId=<% out.print(request.getParameter("storageItemId")); %>&storageItemInstanceId=<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>&action=remove" title="remove"><img src="../images/removeicon.png" width="24" height="24" onClick="ItemRemove('<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>');"></a>
          <%
          }
          %>
          </td>
          <td width="100" align="center" class="font_18 form_underline_gray"><a href="listItemInstanceTrans.jsp?k=<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>" title="history"><img src="../images/history.png" width="24" height="24" alt=""/></a>&nbsp;&nbsp;<a href="editItemInstance.jsp?k=<%= KeyFactory.keyToString(storageItemInstance.getKey()) %>" title="edit"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a>
          <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) { %>
          &nbsp;&nbsp;<a href="/manageCustomer?action=delete&type=storageItemInstance&k=<%=  KeyFactory.keyToString(storageItemInstance.getKey()) %>&storageItemId=<%= storageItemKeyString %>" title="delete"><img src="../images/close_32.png" alt="" width="24" height="24"></a>
          <% } %>
          </td>
	    </tr>
	<%
	}
	%>
</table>

<% 
// Get StorageItemInstance
String storageItemInstanceKeyString = request.getParameter("storageItemInstanceId");
StorageItemInstance storageItemInstance = null;
if (storageItemInstanceKeyString != null) {
	Key storageItemInstanceKey = KeyFactory.stringToKey(storageItemInstanceKeyString);
	storageItemInstance = StorageItemInstanceManager.getStorageItemInstance(storageItemInstanceKey);
}

// Check action
String action = request.getParameter("action");

if(storageItemInstance != null && action != null && action.equalsIgnoreCase("store")) {
%>
	<div id="StoreArea"><form id="ItemStoreform" name="ItemStoreform" method="post" action="/manageCustomer?action=add&type=storageItemTransaction&storageItemTransactionAction=store&storageItemInstanceId=<%= storageItemInstanceKeyString != null ? storageItemInstanceKeyString : "" %>&pageFrom=listItemInstance">

	  <table width="800" border="0" align="center" cellpadding="0" cellspacing="1">
	    <tr>
	      <td width="130" height="20" align="right" scope="row">&nbsp;</td>
	      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
	      <td height="20" colspan="2">&nbsp;</td>
	      <td height="20"></td>
	    </tr>
	    <tr>
	      <td class="form_underline"></td>
	      <td colspan="3" align="center" class="form_underline" scope="row">&nbsp;</td>
	      <td class="form_underline"></td>
	    </tr>
	    <tr>
	      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemInstanceLabel">Instance label：</label></td>
	      <td height="60" colspan="2">
	        <input name="storageItemInstanceLabel" type="text" class="textfield_style" id="storageItemInstanceLabel" value="<%= storageItemInstance.getStorageItemInstanceLabel() %>" readonly></td>
	      <td width="303" height="60" align="left">&nbsp;</td>
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
	      <td height="40" colspan="2" align="right" valign="bottom" scope="row">*Storage Location：</td>
	      <td height="40" colspan="2">&nbsp;</td>
	      <td width="303" height="40">&nbsp;</td>
	    </tr>
	    <tr>
	      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceId">*Equipment Serial Number</label></td>
	      <td height="60" colspan="2" align="left">
	      <%
	      List<StorageDevice> storageDevices = StorageDeviceManager.getAllStorageDevicesFromCustomer(storageItemKey.getParent());
	        
	      String selectedStorageDeviceKeyString = request.getParameter("storageDeviceId");
	      StorageDevice selectedStorageDevice = null;
	      if (selectedStorageDeviceKeyString != null) {
	      	  Key selectedStorageDeviceKey = KeyFactory.stringToKey(selectedStorageDeviceKeyString);
	          selectedStorageDevice = StorageDeviceManager.getStorageDevice(selectedStorageDeviceKey);
	      }
	      %>
	      <select type="text" name="storageDeviceId" id="storageDeviceId" class="select_textfield_style" style="margin-left:8px; width:180px;" onChange="MM_jumpMenu('parent',this,0)">
	        <option value="" selected>Storage Equipment</option>
	        <%
	        for (StorageDevice storageDevice : storageDevices) {
	        %>
	        	<option value="?storageItemId=<%= storageItemKeyString %>&storageItemInstanceId=<%= storageItemInstanceKeyString %>&action=store&storageDeviceId=<%= KeyFactory.keyToString(storageDevice.getKey()) %>#StoreArea" <%= selectedStorageDevice != null && selectedStorageDevice.equals(storageDevice) ? "selected" : "" %>><%= storageDevice.getStorageDeviceSerialNumber() %></option>
	        <%
	        }
	        %>
	      </select></td>
	      <td height="60">&nbsp;</td>
	    </tr>
	    <tr>
	      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceDoorId">*Door</label></td>
	      <td width="145" height="60" align="center">
	      	<%
	      	ArrayList<StorageDeviceDoor> storageDeviceDoors;
			StorageDeviceDoor selectedStorageDeviceDoor = null;
	      	if (selectedStorageDevice != null) {
			    storageDeviceDoors = (ArrayList<StorageDeviceDoor>) StorageDeviceDoorManager.getAllStorageDeviceDoorsFromStorageDevice(selectedStorageDevice.getKey());
			        
			    String selectedStorageDeviceDoorKeyString = request.getParameter("storageDeviceDoorId");
			    if (selectedStorageDeviceDoorKeyString != null) {
			    	Key selectedStorageDeviceDoorKey = KeyFactory.stringToKey(selectedStorageDeviceDoorKeyString);
			        selectedStorageDeviceDoor = StorageDeviceDoorManager.getStorageDeviceDoor(selectedStorageDeviceDoorKey);
			    }
	      	}
	      	else {
	      		storageDeviceDoors = new ArrayList<StorageDeviceDoor>();
	      	}
		    %>
	        <select name="storageDeviceDoorId" disabled class="select_textfield_style" id="storageDeviceDoorId" onChange="MM_jumpMenu('parent',this,0)" type="text">
	          <option value="" selected>Door</option>
	          <%
	          for (StorageDeviceDoor storageDeviceDoor : storageDeviceDoors) {
	        	  DeviceModelDoor deviceModelDoor = DeviceModelDoorManager.getDeviceModelDoor(storageDeviceDoor.getDeviceModelDoor());
	          %>
	            <option value="?storageItemId=<%= storageItemKeyString %>&storageItemInstanceId=<%= storageItemInstanceKeyString %>&action=store&storageDeviceId=<%= KeyFactory.keyToString(selectedStorageDevice.getKey()) %>&storageDeviceDoorId=<%= KeyFactory.keyToString(storageDeviceDoor.getKey()) %>#StoreArea" <%= selectedStorageDeviceDoor != null && selectedStorageDeviceDoor.equals(storageDeviceDoor) ? "selected" : "" %>><%= deviceModelDoor.getDeviceModelDoorNumber() %></option>
	          <%
	          }
	          %>
          </select></td>
	      <td width="208"><label for="storageDevicePartitionId">*Partition</label>
	        <%
	      	ArrayList<StorageDevicePartition> storageDevicePartitions;
			StorageDevicePartition selectedStorageDevicePartition = null;
	      	if (selectedStorageDevice != null && selectedStorageDeviceDoor != null) {
			    storageDevicePartitions = (ArrayList<StorageDevicePartition>) StorageDevicePartitionManager.getAllStorageDevicePartitionsFromStorageDeviceDoor(selectedStorageDeviceDoor.getKey());
			    
			    String selectedStorageDevicePartitionKeyString = request.getParameter("storageDevicePartitionId");
			    if (selectedStorageDevicePartitionKeyString != null) {
			    	Key selectedStorageDevicePartitionKey = KeyFactory.stringToKey(selectedStorageDevicePartitionKeyString);
			        selectedStorageDevicePartition = StorageDevicePartitionManager.getStorageDevicePartition(selectedStorageDevicePartitionKey);
			    }
	      	}
	      	else {
	      		storageDevicePartitions = new ArrayList<StorageDevicePartition>();
	      	}
		    %>
	        <select name="storageDevicePartitionId" disabled class="select_textfield_style" id="storageDevicePartitionId" type="text"> <!-- onChange="MM_jumpMenu('parent',this,0)"> --> 
	          <option value="" selected>Partition</option>
	          <%
	          for (StorageDevicePartition storageDevicePartition : storageDevicePartitions) {
	        	  DeviceModelPartition deviceModelPartition = DeviceModelPartitionManager.getDeviceModelPartition(storageDevicePartition.getDeviceModelPartition());
	          %>
	          	<!-- <option value="?storageItemId=<%= storageItemKeyString %>&storageItemInstanceId=<%= storageItemInstanceKeyString %>&action=store&storageDeviceId=<%= KeyFactory.keyToString(selectedStorageDevice.getKey()) %>&storageDeviceDoorId=<%= KeyFactory.keyToString(selectedStorageDeviceDoor.getKey()) %>&storageDevicePartitionId=<%= KeyFactory.keyToString(storageDevicePartition.getKey()) %>" <%= selectedStorageDevicePartition != null && selectedStorageDevicePartition.equals(storageDevicePartition) ? "selected" : "" %>><%= deviceModelPartition.getDeviceModelPartitionName() %></option> -->
	            <option value="<%= KeyFactory.keyToString(storageDevicePartition.getKey()) %>"><%= deviceModelPartition.getDeviceModelPartitionName() %></option>
	          <%
	          }
	          %>
          </select></td>
	        <tr>
	      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemTransactionPerformedBy">*Performed by：</label></td>
	      <td height="60" colspan="2">
	        <select name="storageItemTransactionPerformedBy" class="textfield_style" id="storageItemTransactionPerformedBy">
	          <option value="">(Employee)</option>
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
	          <option value='<%= KeyFactory.keyToString(customerUser.getKey()) %>'><%= customerUser.getCustomerUserName() %></option>
	          <%
	          }
	          %>
	        </select></td>
	        <td width="303" height="60" align="left">&nbsp;</td>
	    	</tr>
	      <td width="303" height="60">&nbsp;</td>
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
	    <tr><td colspan="5" align="center"><input type="submit" name="ItemStoreSubmit" id="ItemStoreSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href = '?storageItemId=<% out.print(request.getParameter("storageItemId")); %>';" value="Cancel"></td>
	      </tr>
	  </table>
	</form>
	</div>
<%
} 

if(storageItemInstance != null && action != null && action.equalsIgnoreCase("remove")) {
%>
	<div id="RemoveArea"><form id="ItemStoreform" name="ItemStoreform" method="post" action="/manageCustomer?action=add&type=storageItemTransaction&storageItemTransactionAction=remove&storageItemInstanceId=<%= storageItemInstanceKeyString != null ? storageItemInstanceKeyString : "" %>&pageFrom=listItemInstance">
	  
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
	</form></div>
<%
}
%>

</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>