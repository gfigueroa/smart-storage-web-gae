<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="datastore.UserManager" %>

<%
  User loggedUser = (User) session.getAttribute("user");
%>

<%
switch (loggedUser.getUserType()) {
	case ADMINISTRATOR:
%>
		<jsp:include page="../menu/admin_menu.jsp" />
<%
		break;
	case CUSTOMER:
%>
		<jsp:include page="../menu/customer_menu.jsp" />
<%
		break;
	case CUSTOMER_USER:
%>
		<jsp:include page="../menu/customer_menu.jsp" />
<%
		break;
}
%>