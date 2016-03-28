<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.CustomerUser" %>
<%@ page import="datastore.CustomerUserManager" %>
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
<script src="../javascript/listEmployee.js"></script>
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
%>

<body>
<%@include file="../header/header.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="77%" height="50" align="left" valign="middle">Employee List</td>
    <% if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) { %>
    <td width="3%" valign="middle"><a href="addEmployee.jsp"><img src="../images/round_plus.png" width="20" height="20" alt="新增管理者"></a></td>
    <td width="20%" valign="middle"><a  href="addEmployee.jsp"><span class="font_18">Add Employee</span></a></td>
    <% } %>
  </tr>
</table>
  </div>
  <%
String msg = request.getParameter("msg");
String action = request.getParameter("action");
if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("add")) {
%>
	<table width="384" border="0" align="center" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td height="30" align="center" valign="top" class="warn_font_green">Add Employee Successful !</td>
      </tr>
  </tbody>
</table>
<% }
else if(msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("delete")) {
%>
	<table width="384" border="0" align="center" cellpadding="0" cellspacing="0">
  <tbody>
    <tr>
      <td height="30" align="center" valign="top" class="warn_font_red">Delete Employee Successful !</td>
      </tr>
  </tbody>
</table>
<% } 



%>
<% 
if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR) {
%>
<table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
	      <td height="40" colspan="5" align="left" class="listTitle_CSS form_underline_blue radiusLeft radiusRight"><strong>Customer List</strong></td>
        </tr>
</table>
<%    
}   
%>
	<%
	int i = 0;
	for (Customer customer : customers) {
		List<CustomerUser> customerUsers = CustomerUserManager.getAllCustomerUsersFromCustomer(customer.getKey());
		
		if (customerUsers.isEmpty()) {
			continue;
		}
	%>
    <table class="listEmployeeHide" width="800" border="0" align="center" cellpadding="0" cellspacing="0">
	    <tr OnClick='EmployeeShowFunction(<%= i %>, this);' style="cursor:pointer;">
	      <td height="40" colspan="2" align="left" class="listTitle_CSS" style="border-bottom-color: white; padding-left:5px;"><%= customer.getCustomerName() %></td>
	      <td width="90" height="40" colspan="2" align="right" class="listTitle_CSS" style="padding-right:50px;border-bottom-color: white;">(<%= customerUsers.size() + (customerUsers.size() == 1 ? " employee" : " employees" ) %>)</td>
	    </tr>
	    <tr class="listCotent_CSS">
	      <th width="165" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Employee Name</th>
	      <th width="313" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Email</th>
	      <th width="100" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Edit</th>
	      <th width="90" height="30" align="center" bgcolor="#E0EEFC" class="font_18 form_underline_gray">Transactions</th>
	    </tr>
	    <%
		for (CustomerUser customerUser : customerUsers) {
	    %>
		    <tr class="listCotent_CSS">
		      <td align="center" class="font_18 form_underline_gray"><%= customerUser.getCustomerUserName() %></td>
		      <td align="center" class="font_18 form_underline_gray"><%= customerUser.getUser().getUserEmail().getEmail() %></td>
		      <td width="100" align="center" class="font_18 form_underline_gray"><a href="editEmployee.jsp?k=<%= KeyFactory.keyToString(customerUser.getKey()) %>" title="edit"><img src="../images/pencil_32.png" alt="" width="24" height="24"></a>
              <% if(sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER){ %>
              &nbsp;&nbsp;<a href="/manageCustomer?action=delete&type=customerUser&k=<%=  KeyFactory.keyToString(customerUser.getKey()) %>"><img src="../images/close_32.png" alt="" width="24" height="24"></a>
              <% } %>
              </td>
		      <td width="90" align="center" class="font_18 form_underline_gray"><a href="listEmployeeTransactions.jsp?customerUserId=<%= KeyFactory.keyToString(customerUser.getKey()) %>" title="view"><img src="../images/view.png" alt="" width="24" height="24"></a></td>
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