<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>
<head>
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/jquery.datetimepicker.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
<script src="../javascript/addNews.js" type="text/javascript"></script>
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
%>

<body>
<%@include file="../header/header.jsp" %>
<div id="menu_back">
<div id="menu">
<%@include file="../menu/menu.jsp" %>
</div></div>
<div id="content_area">
<div class="title_bar title_name">Retail Store News</div>
	<form id="addCountryform" name="addCountryform" method="post" action="">
  
  <table width="100%" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <td height="73" colspan="2" align="right" class="form_underline" scope="row">Add Retail Store News：</td>
      <td height="73" class="form_underline">&nbsp;</td>
      <td height="73" class="form_underline"></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td width="208" height="60" align="right" scope="row">*News Title：</td>
      <td width="354" height="60">
        <input name="rn_title" type="text" class="textfield_style" id="rn_title">
        </td>
      <td width="303" height="60"><div id="empty_CountryName" class="errorText">*Enter country name</div></td>
    </tr>
    <tr>
      <td width="130" height="134" align="right" scope="row">&nbsp;</td>
      <td height="134" align="right" scope="row">*News Content：</td>
      <td width="354" height="134"><textarea name="rn_content" class="news_content_size" id="rn_content"></textarea></td>
      <td width="303" height="134"><div id="empty_CountryName" class="errorText">*Enter country name</div></td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row">*Release Date：</td>
      <td width="354" height="60">
        <input name="newsReleaseDate" type="text" class="textfield_style" id="newsReleaseDate" style="width:315px;">
        <img src="../images/calendaricon.png" width="30" height="29" class="calendarposition" style="cursor:pointer;">        </td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row">*Expiration Date：</td>
      <td width="354" height="60">
        <input name="newsExpirationDate" type="text" class="textfield_style" id="newsExpirationDate" style="width:315px;">
        <img src="../images/calendaricon.png" width="30" height="29" class="calendarposition" style="cursor:pointer;">        </td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td height="60" align="right" scope="row"><label for="rn_image">Image：</label></td>
      <td width="354" height="60">
      
        <input name="rn_image" type="file" style="display:none;" id="rn_image">
       <label for="rn_image"> <input type="button" name="imagesUpload" id="imagesUpload" class="css_btn_class" value="Images"></label></td>
      <td width="303" height="60"></td>
    </tr>
    <tr>
      <td class="form_underline" width="130" height="60" align="right" scope="row">&nbsp;</td>
      <td class="form_underline" height="60" align="right" scope="row"><label for="newsHyperLink">HyperLink：</label></td>
      <td class="form_underline" width="354" height="60"><input name="newsHyperLink" type="text" class="textfield_style" id="newsHyperLink"></td>
      <td class="form_underline" width="303" height="60"></td>
    </tr>
    <tr>
      <td></td>
      <td colspan="2" align="center" scope="row">&nbsp;</td>
      <td></td>
    </tr>
    <tr>
      <td colspan="4" align="center"><input type="submit" name="NewCountryNameSubmit" id="NewCountryNameSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listNews.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>