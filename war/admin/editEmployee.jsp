<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.Customer" %>
<%@ page import="datastore.CustomerManager" %>
<%@ page import="datastore.Department" %>
<%@ page import="datastore.DepartmentManager" %>
<%@ page import="datastore.CustomerUser" %>
<%@ page import="datastore.CustomerUserManager" %>
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
<link href="../css/addCustomer.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/editEmployee.js"></script>
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

String customerUserKeyString = request.getParameter("k");
Key customerUserKey = KeyFactory.stringToKey(customerUserKeyString);
CustomerUser customerUser = CustomerUserManager.getCustomerUser(customerUserKey);

Customer customer = CustomerManager.getCustomer(customerUserKey.getParent());

List<Department> departments = DepartmentManager.getAllDepartments();
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit <span class="form_underline">Employee</span></div>
<form id="editCustomerform" name="editCustomerform" method="post" action="/manageCustomer?action=update&type=customerUser&update_type=info&k=<%= customerUserKeyString %>">

<!--"/manageUser?action=add&type=customer&keep_adding=true">-->
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="60" colspan="2" align="left" class="form_underline" scope="row">Edit employee information：<%
String msg = request.getParameter("msg");
String action = request.getParameter("action");
if (msg != null && msg.equalsIgnoreCase("success") && action != null && action.equals("update")) {
%>
        <span class="warn_font_green">Edit Successful !</span>
        <% } 



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
      <td height="50" align="right" scope="row"><label for="c_key">*Customer：</label></td>
      <td width="354" height="50"><input type="text" name="c_key" id="c_key" class="textfield_style" disabled value="<%= customer.getCustomerName() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
    </tr>
    
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserName">*Name：</label></td>
      <td width="354" height="50"><input type="text" name="customerUserName" id="customerUserName" class="textfield_style" value="<%= customerUser.getCustomerUserName() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left"><div id="empty_customerName" class="errorText">*Enter customer username</div></td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="userEmail">*Email：</label></td>
      <td width="354" height="50">
        <input name="userEmail" type="text" class="textfield_style" id="userEmail" disabled value="<%= customerUser.getUser().getUserEmail().getEmail() %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserDescription">Description：</label></td>
      <td width="354" height="50">
        <input name="customerUserDescription" type="text" class="textfield_style" id="customerUserDescription" value="<%= customerUser.getCustomerUserDescription() != null ? customerUser.getCustomerUserDescription() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserGender">Gender：</label></td>
      <td width="354" height="50">
        <select name="customerUserGender" class="textfield_style" id="customerUserGender" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>>
          <option value="male" <%= customerUser.getCustomerUserGender() == CustomerUser.Gender.MALE ? "selected" : "" %>>Male</option>
          <option value="female" <%= customerUser.getCustomerUserGender() == CustomerUser.Gender.FEMALE ? "selected" : "" %>>Female</option>
        </select></td>
        <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserTitle">Title：</label></td>
      <td width="354" height="50">
        <input name="customerUserTitle" type="text" class="textfield_style" id="customerUserTitle" value="<%= customerUser.getCustomerUserTitle() != null ? customerUser.getCustomerUserTitle() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="departmentId">Department：</label></td>
      <td width="354" height="50">
        <select name="departmentId" class="textfield_style" id="departmentId" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>>
          <%
          for (Department department : departments) {
          %>
          	<option value="<%= department.getKey() %>" <%= department.getKey().equals(customerUser.getDepartment()) ? "selected" : "" %>><%= department.getDepartmentName() %></option>
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
        <input name="customerUserPhoneNumber" type="text" class="textfield_style" id="customerUserPhoneNumber" value="<%= customerUser.getCustomerUserPhoneNumber() != null ? customerUser.getCustomerUserPhoneNumber().getNumber() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>

    <tr>
      <td width="130" height="50" align="right" scope="row">&nbsp;</td>
      <td height="50" align="right" scope="row"><label for="customerUserComments">Comments：</label></td>
      <td width="354" height="50">
        <input name="customerUserComments" type="text" class="textfield_style" id="customerUserComments" value="<%= customerUser.getCustomerUserComments() != null ? customerUser.getCustomerUserComments() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td width="303" height="50" align="left">&nbsp;</td>
    </tr>

	<%
	if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR
			|| sessionUser.getUserType() == User.UserType.CUSTOMER
			|| (sessionUser.getUserType() == User.UserType.CUSTOMER_USER && sessionUser.equals(customerUser.getUser()))) {
	%>
	    <tr>
	      <td height="50"></td>
	      <td height="50" colspan="2" align="center" scope="row"><a class="changePasswordStyle" href="#" onclick="window.open(' editEmployeePassword.jsp?k=<%= customerUserKeyString %>', 'Change Password', config='height=752,width=1112px');">Change Password</a></td>
	      <td height="50"></td>
	    </tr>
    <%
	}
    %>
    
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
    <input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEmployee.jsp?k=<%= customerUserKeyString %>'" value="Close">
    </td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>