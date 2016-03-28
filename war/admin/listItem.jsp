<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.StorageItem" %>
<%@ page import="datastore.StorageItemManager" %>
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
<script src="../javascript/listItem.js"></script>
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

List<Customer> customers;
switch (sessionUser.getUserType()) {
	case ADMINISTRATOR:
		customers = CustomerManager.getAllCustomers();
		break;
	case CUSTOMER:
		Customer customer = CustomerManager.getCustomer(sessionUser);
		customers = new ArrayList<Customer>();
		customers.add(customer);
		break;
	case CUSTOMER_USER:
		customer = CustomerManager.getCustomer(sessionUser.getKey().getParent().getParent());
		customers = new ArrayList<Customer>();
		customers.add(customer);
		break;
	default:
		customers = null;
		break;
}

String searchString = request.getParameter("searchItem");
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="83%" height="50" align="left" valign="middle">Item List</td>
    <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) { %>
    <td width="3%" valign="middle"><a href="addItem.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="14%" valign="middle"><a  href="addItem.jsp"><span class="font_18">Add Item</span></a></td>
    <% } %>
  </tr>
</table>
  </div>
  <form name="ItemSearchForm" id="ItemSearchForm" method="GET" action="">
  <table width="800" border="0" align="center" cellpadding="1" cellspacing="0">
  <tr>
    <td width="104%" height="85" scope="row" class="form_underline"><table border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <th width="354" height="41" scope="row"> <input name="searchItem" type="text" class="textfield_style" id="searchItem" placeholder="Search Item" value="<%= searchString != null && !searchString.isEmpty() ? searchString : "" %>">
        </th>
        <td width="40" height="41" align="center" valign="bottom" style="background-color:#D8D8D8;border: 0px solid #CCC;cursor:pointer;"><img id="itemlistsearch" src="../images/search-icon-md.png" alt="" width="28" height="27" style="cursor:pointer;"></td>
      </tr>
    </table></td>
    </tr>
  <tr>
    <td height="15"  scope="row"></td>
  </tr>
  </table>
</form>
<table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
	      <td height="40" colspan="5" align="left" class="listTitle_CSS form_underline_blue radiusLeft radiusRight"><strong>Customer List</strong></td>
        </tr>
</table>
	<%
	int i = 0;
	for (Customer customer : customers) {
		List<StorageItem> storageItems;
		if (searchString == null || searchString.isEmpty()) {
			storageItems = StorageItemManager.getAllStorageItemsFromCustomer(customer.getKey());
		}
		else {
			storageItems = StorageItemManager.searchStorageItems(customer.getKey(), searchString);
		}
		
		if (storageItems.isEmpty()) {
			continue;
		}
	%>
    <table class="listItemHide" width="800" border="0" align="center" cellpadding="0" cellspacing="0">
	    <tr OnClick='ItemShowFunction(<%= i %>, this);' style="cursor:pointer;">
	      <td height="40" colspan="3" align="left" class="listTitle_CSS" style="border-bottom-color: white; padding-left:5px;"><%= customer.getCustomerName() %></td>
	      <td height="40" colspan="2" align="right" class="listTitle_CSS" style="padding-right:50px;border-bottom-color: white;">(<%= storageItems.size() + (storageItems.size() == 1 ? " item" : " items") %>)</td>
	    </tr>
	    <tr class="listCotent_CSS">
	      <th width="155" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Item Name</th>
	      <th width="313" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Part Number</th>
	      <th width="135" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Instance Count</th>
	      <th width="93" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Edit</th>
	      <th height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Instances</th>
	    </tr>
	    <%
		for (StorageItem storageItem : storageItems) {
	    %>
		    <tr class="listCotent_CSS">
		      <td align="center" class="font_18 form_underline_gray"><%= storageItem.getStorageItemName() %></td>
		      <td align="center" class="font_18 form_underline_gray"><%= storageItem.getStorageItemPartNumber() != null ? storageItem.getStorageItemPartNumber() : "" %></td>
		      <td align="center" class="font_18 form_underline_gray"><%= storageItem.getStorageItemInstancesCount() %><br>
		      <td width="100" align="center" class="font_18 form_underline_gray"><a href="editItem.jsp?k=<%= KeyFactory.keyToString(storageItem.getKey()) %>&pageFrom=listItem" title="edit"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a>
              <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) { %>
              &nbsp;&nbsp;<a href="/manageCustomer?action=delete&type=storageItem&k=<%=  KeyFactory.keyToString(storageItem.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a>
              <% } %>
              </td>
		      <td width="104" align="center" class="font_18 form_underline_gray"><a href="listItemInstance.jsp?storageItemId=<%= KeyFactory.keyToString(storageItem.getKey()) %>" title="view"><img src="../images/view.png" alt="" width="24" height="24"></a></td>
		    </tr>
	<%
		}
	    i++;
	%>
    </table>
    <%
	}
	%>

</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>