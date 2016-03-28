<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.DeviceServiceType" %>
<%@ page import="datastore.DeviceServiceTypeManager" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>

<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/jquery.datetimepicker.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/addEquipmentModel.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>SMASRV IoT</title>
</head>

<%
	User sessionUser = (User)session.getAttribute("user");
if (sessionUser == null) {
	response.sendRedirect("../login.jsp");
}
else {
	if (sessionUser.getUserType() != User.UserType.ADMINISTRATOR) {
  		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);  		
  	}
}

List<DeviceServiceType> deviceServiceTypes = DeviceServiceTypeManager.getAllDeviceServiceTypes();
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Add a new equipment model</div>
	<form id="addDeviceModelform" name="addDeviceModelform" method="post" action="/manageGlobalObject?action=add&type=deviceModel&keep_adding=true">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="60" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="60" align="left" class="form_underline" scope="row"><p>Model Information:</p></td>
      <td height="60" class="form_underline">&nbsp;</td>
      <td height="60" class="form_underline"></td>
    </tr>
    <tr>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td height="20" align="right" scope="row">&nbsp;</td>
      <td height="20">&nbsp;</td>
      <td height="20"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceServiceTypeId">*Select Type：</label></td>
      <td width="354" height="60" class="login_textfield">
        <select name="deviceServiceTypeId" class="textfield_style" id="deviceServiceTypeId">
        <%
        	for (DeviceServiceType deviceServiceType : deviceServiceTypes) {
        %>
          	<option value="<%= deviceServiceType.getKey() %>" ><%= deviceServiceType.getDeviceServiceTypeName() %></option>
        <%
        }
        %>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceModelName">*Model Name：</label></td>
      <td width="354" height="60" class="login_textfield">
        <input type="text" name="deviceModelName" id="deviceModelName" class="textfield_style"></td>
      <td width="303" height="60"><div id="empty_deviceModelName" class="errorText">*Enter model name</div></td>
    </tr>
    
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceModelDescription">Model Description：</label></td>
      <td width="354" height="60" class="login_textfield">
        <input name="deviceModelDescription" type="text" class="textfield_style" id="deviceModelDescription"></td>
      <td width="303" height="60"></td>
    </tr>
    
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="deviceModelDesignTime">*Model Design Time：</label></td>
      <td width="354" height="60">
        <input name="deviceModelDesignTime" type="text" class="textfield_style" id="deviceModelDesignTime" style="width:315px;" readonly>
        <label for="deviceModelDesignTime"><img src="../images/calendaricon.png" width="30" height="29" class="calendarposition" style="cursor:pointer;"></label></td>
      <td width="303" height="60" align="left"><div id="empty_deviceModelDesignTime" class="errorText">*Enter model design time</div></td>
    </tr>
    
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="sensorDataUploadPeriod">*Data Upload Period：</label></td>
      <td width="354" height="60" class="login_textfield">
        <input name="sensorDataUploadPeriod" type="text" class="textfield_style" id="sensorDataUploadPeriod"></td>
      <td width="303" height="60">&nbsp;
        <div id="empty_sensorDataUploadPeriod" class="errorText">*Enter data upload period</div>
        seconds</td>
    </tr>
    
    <tr>
      <td height="21" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="21" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td height="21" class="form_underline">&nbsp;</td>
      <td height="21" class="form_underline"></td>
    </tr>
    <tr>
      <td height="70" align="right" scope="row">&nbsp;</td>
      <td height="70" align="right" scope="row">*Sensor Configuration :</td>
      <td height="70">&nbsp;</td>
      <td height="70"></td>
    </tr>
    <tr>
      <td width="130" height="60" rowspan="2" align="right" scope="row">&nbsp;</td>
      <td colspan="2" rowspan="4" scope="row"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td width="40%" height="30">
            <input name="temp1Hum1" type="checkbox" id="temp1Hum1" value="true" checked="checked">            Temp/Hum 1</td>
          <td width="40%" height="30" align="left"><input name="infrared" type="checkbox" id="infrared">            Infrared</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="temp2Hum2" type="checkbox" id="temp2Hum2" value="true" checked="checked">            Temp/Hum 2</td>
          <td height="30" align="left"><input name="imageUpload" type="checkbox" id="imageUpload"  value="true">            imageUpload</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="doorOpenClose" type="checkbox" id="doorOpenClose" value="true">            <label for="doorOpenClose">Door open/close</label></td>
          <td height="30" align="left"><input name="alcohol" type="checkbox" id="alcohol" value="true">            alcohol</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="co2" type="checkbox" id="co2" value="true">            CO2</td>
          <td height="30" align="left"><input name="electricCurrent" type="checkbox" id="electricCurrent" value="true">            Electrical Current</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="co" type="checkbox" id="co" value="true">            CO</td>
          <td height="30" align="left"><input name="atmosphericPressure" type="checkbox" id="atmosphericPressure" value="true">            Atmosphere pressure</td>
          </tr>
        <tr>
          <th width="20%" height="30" scope="row">&nbsp;</th>
          <td height="30"><input name="flux" type="checkbox" id="flux" value="true">            Flux</td>
          <td height="30" align="left"><input name="printer" type="checkbox" id="printer" value="true">            Finger print</td>
          </tr>
      </table></td>
      <td width="303" height="29" align="left"><input name="N2" type="checkbox" id="N2">            N2</td>
    </tr>
    <tr>
      <td height="30" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="73" align="right" class="form_underline" scope="row">&nbsp;</td>
      <td width="208" height="73" align="right" class="form_underline" scope="row">Partition Configuration :</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr id="selectModelDoortable">
      <td height="25" align="right" scope="row">&nbsp;</td>
      <td height="25" align="right" scope="row">&nbsp;</td>
      <td height="25">&nbsp;</td>
      <td height="25"></td>
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
    <tr><td colspan="4" align="center"><input type="submit" name="NewModelSubButton" id="NewModelSubButton" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listEquipmentModel.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>