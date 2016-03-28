<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>

<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/addCustomer.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addCustomer.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>SMASRV IoT</title>
</head>

<%
Printer printer = new Printer(Dictionary.Language.CHINESE);
String language = request.getParameter("lang") != null ? request.getParameter("lang") : "CH";
if(language.equals("EN")) {
		printer.setLanguage(Dictionary.Language.ENGLISH);
}
if(language.equals("CH")) {
		printer.setLanguage(Dictionary.Language.CHINESE);
}

// If user is logged in, don't allow this page to open
User sessionUser = (User) session.getAttribute("user");
if (sessionUser != null) {
	// we check the user type to send him to his/her own main page
   	switch(sessionUser.getUserType()) {
       	case ADMINISTRATOR:
               response.sendRedirect("/admin/listAdmin.jsp");
               break;
           case CUSTOMER:
               response.sendRedirect("/admin/listCustomer.jsp");
               break;
           case CUSTOMER_USER:
               response.sendRedirect("/admin/listEmployee.jsp");
               break;
           default:
               // there should be no other
               // type of user
               assert(false);
       }
}

String action = request.getParameter("action");
%>

<body>
<%@include file="../header/header.jsp" %>

<div id="content_area">
<div class="title_bar title_name">Create a new <span class="form_underline">customer</span></div>
<form id="addCustomerform" name="addCustomerform" method="post" action="/manageUser?action=add&type=customer&keep_adding=true">

<!--"/manageUser?action=add&type=customer&keep_adding=true">-->
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" colspan="3" align="right" class="form_underline" scope="row">&nbsp;</td>
      </tr>
    <tr>
      <td height="48" colspan="3" align="center" valign="bottom" scope="row"><img src="../images/confirmsuccess.png" width="30" height="30" alt=""/></td>
      </tr>    
    <tr class="form_underline">
      <%
      String message = "";
      if (action.equalsIgnoreCase("add")) {
    	  message = "Congratulations!<br>You have successfully registered to our system.<br>However, in order to use your account, you must confirm your registration.<br>";
    	  message += "An email has been sent to your registered e-mail address with a confirmation link.";
      }
      else if (action.equalsIgnoreCase("confirmRegistration")) {
    	  message = "Congratulations!<br>Your account has been confirmed successfully.<br>You can now log in to our system.";
      }
      %>
      <td height="67" colspan="3" align="center" class="form_underline"><strong><%= message %></strong></td>
    </tr>
    <tr>
      <td width="209" height="20"></td>
      <td align="center" scope="row">&nbsp;</td>
      <td width="260"></td>
    </tr>
    <tr><td colspan="3" align="center">&nbsp;</td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>