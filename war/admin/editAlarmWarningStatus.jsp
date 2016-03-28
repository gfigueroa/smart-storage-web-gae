<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.AlarmWarning" %>
<%@ page import="datastore.AlarmWarningManager" %>
<%@ page import="datastore.AlarmWarningMessage" %>
<%@ page import="datastore.AlarmWarningMessage.AlarmWarningMessageStatus" %>
<%@ page import="datastore.AlarmWarningMessageManager" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.DateManager" %>
<%@ page import="util.Dictionary" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
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

String alarmWarningMessageKeyString = request.getParameter("k");
Key alarmWarningMessageKey = KeyFactory.stringToKey(alarmWarningMessageKeyString);
AlarmWarningMessage alarmWarningMessage = AlarmWarningMessageManager.getAlarmWarningMessage(alarmWarningMessageKey);

StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(alarmWarningMessage.getStorageDevice());
AlarmWarning alarmWarning = AlarmWarningManager.getAlarmWarning(alarmWarningMessageKey.getParent());
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Edit Alarm Warning Status</div>
	<form id="editWarningMessageform" name="editWarningMessageform" method="post" action="/manageCustomer?action=update&type=alarmWarningMessage&k=<%= alarmWarningMessageKeyString %>">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="73" align="left" class="form_underline" scope="row">Input Warning Message：</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row">Creation Time：</td>
      <td width="354" height="60"><%= DateManager.printDateAsString(alarmWarningMessage.getAlarmWarningMessageCreationDate()) %></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row">Modification Time：</td>
      <td width="354" height="60"><%= DateManager.printDateAsString(alarmWarningMessage.getAlarmWarningMessageModificationDate()) %></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row">Storage Equipment Serial Number：</td>
      <td width="354" height="60"><%= storageDevice.getStorageDeviceSerialNumber() %></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row">Dehumidifier Machine：</td>
      <td width="354" height="60"><%= alarmWarningMessage.getAlarmWarningMessageDehumidifierMachine() %></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row">Warning Message：</td>
      <td width="354" height="60"><%= alarmWarning.getAlarmWarningMessage() %></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="alarmTriggerCode">*Status：</label></td>
      <td width="354" height="60"><select name="alarmWarningMessageStatus" id="alarmTriggerCode" class="textfield_style" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>>
        <option value="alert" <%= alarmWarningMessage.getAlarmWarningMessageStatus() == AlarmWarningMessageStatus.ALERT ? "selected" : "" %>>Alert</option>
        <option value="processed" <%= alarmWarningMessage.getAlarmWarningMessageStatus() == AlarmWarningMessageStatus.PROCESSED ? "selected" : "" %>>Processed</option>
        <option value="resolved" <%= alarmWarningMessage.getAlarmWarningMessageStatus() == AlarmWarningMessageStatus.RESOLVED ? "selected" : "" %>>Resolved</option>
      </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td class="form_underline" width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td class="form_underline" height="60" align="right" scope="row"><label for="alarmWarningMessageNote">Note：</label></td>
      <td class="form_underline" width="354" height="60"><input name="alarmWarningMessageNote" type="text" class="textfield_style" id="alarmWarningMessageNote" value="<%= alarmWarningMessage.getAlarmWarningMessageNote() != null ? alarmWarningMessage.getAlarmWarningMessageNote() : "" %>" <%= sessionUser.getUserType() != User.UserType.ADMINISTRATOR && sessionUser.getUserType() != User.UserType.CUSTOMER ? "disabled" : "" %>></td>
      <td class="form_underline" width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td colspan="4" align="center">
      <%
	      if (sessionUser.getUserType() == User.UserType.ADMINISTRATOR || sessionUser.getUserType() == User.UserType.CUSTOMER) {
		%>
        <input type="submit" name="NewDeviceTypeSubmit" id="NewDeviceTypeSubmit" class="css_btn_class" value="Save">&nbsp;
        <% } %>
        <input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listAlarmMessage.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>