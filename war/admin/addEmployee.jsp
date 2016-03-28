<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Department" %>
<%@ page import="datastore.DepartmentManager" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/addCustomer.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addEmployee.js"></script>
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
	default:
		customers = null;
		break;
}
List<Department> departments = DepartmentManager.getAllDepartments();
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Add a new <span class="form_underline">customer</span> user</div>
<form id="addCustomerform" name="addCustomerform" method="post" action="/manageCustomer?action=add&type=customerUser&keep_adding=true">

<!--"/manageUser?action=add&type=customer&keep_adding=true">-->
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Input customer user's information：</td>
      <td height="60" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td width="208" height="20" align="right" scope="row">&nbsp;</td>
      <td height="20">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row">*Customer：</td>
      <td height="50"><select name="c_key" class="textfield_style" id="c_key">
        <%
        for (Customer customer : customers) {
        %>
        <option value="<%= KeyFactory.keyToString(customer.getKey()) %>"><%= customer.getCustomerName() %></option>
        <%
        }
        %>
      </select></td>
      <td height="50">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserName">*Name：</label></td>
      <td width="354" height="50"><input type="text" name="customerUserName" id="customerUserName" class="textfield_style"></td>
      <td width="303" height="50" align="left"><div id="empty_customerName" class="errorText">*Enter customer username</div></td>
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
    </tr><tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserDescription">Description：</label></td>
      <td width="354" height="50">
        <input name="customerUserDescription" type="text" class="textfield_style" id="customerUserDescription"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserGender">Gender：</label></td>
      <td width="354" height="50">
        <select name="customerUserGender" class="textfield_style" id="customerUserGender">
          <option value="male">Male</option>
          <option value="female">Female</option>
        </select></td>
        <td width="303" height="50" align="left"><div id="empty_customerUserGender" class="errorText">*Select customer user gender</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserTitle">Title：</label></td>
      <td width="354" height="50">
        <input name="customerUserTitle" type="text" class="textfield_style" id="customerUserTitle"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="departmentId">Department：</label></td>
      <td width="354" height="50">
        <select name="departmentId" class="textfield_style" id="departmentId">
          <%
          for (Department department : departments) {
          %>
          	<option value="<%= department.getKey() %>"><%= department.getDepartmentName() %></option>
          <%
          }
          %>
        </select></td>
        <td width="303" height="50" align="left"></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserPhoneNumber">Phone Number：</label></td>
      <td width="354" height="50">
        <input name="customerUserPhoneNumber" type="text" class="textfield_style" id="customerUserPhoneNumber"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>

    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserComments">Comments：</label></td>
      <td width="354" height="50">
        <input name="customerUserComments" type="text" class="textfield_style" id="customerUserComments"></td>
      <td width="303" height="50" align="left">&nbsp;</td>
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
    <tr><td colspan="4" align="center"><input type="submit" name="NewCustomerSubmit" id="NewCustomerSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEmployee.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>