<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.User" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>

<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>SMASRV IoT</title>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/jquery.datetimepicker.js" type="text/javascript"></script>
<script src="../javascript/addItem.js"></script>
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


<div id="menu_back"><div id="menu"><%@include file="../menu/menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="93%" height="50" align="left" valign="middle">Item Search Result</td>
    <td width="7%" valign="middle"><a href="listItem.jsp"><img src="../images/backicon.png" alt="" width="35" height="35"></a></td>
    </tr>
</table>
  </div>
  <form name="ItemSearchForm" method="GET" action="">
  <table width="800" border="0" align="center" cellpadding="1" cellspacing="0">
  <tr>
    <td width="104%" height="48" scope="row"><table border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <th width="354" height="41" scope="row"> <input name="searchItem" type="text" class="textfield_style" id="searchItem" placeholder="Search Item">
        </th>
        <td width="40" height="41" align="center" valign="bottom" style="background-color:#D8D8D8;border: 0px solid #CCC;cursor:pointer;"><label for="searchButton"><img src="../images/search-icon-md.png" alt="" width="28" height="27" style="cursor:pointer;"></label></td>
      </tr>
    </table></td>
    </tr>
  <tr>
    <td height="15" scope="row"></td>
    </tr><input type="submit" name="searchButton" id="searchButton" value="search" style="display:none;">
</table>
</form>
<form id="itemResultform" name="addStorageform" method="post" action="">
  
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="1">
    <tr>
      <th width="130" height="30" align="right" class="form_underline" scope="row">&nbsp;</th>
      <th height="30" colspan="3" align="left" class="form_underline" scope="row">Result：</th>
      <th height="30" class="form_underline"></th>
    </tr>
    <tr>
      <td height="40" align="right" scope="row">&nbsp;</td>
      <td width="208" height="40" align="right" scope="row">&nbsp;</td>
      <td height="40" colspan="2">&nbsp;</td>
      <td height="40"></td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDeviceContainerId"> Matched Item：</label></td>
      <td height="60" colspan="2">
        <select name="storageDeviceContainerId" class="textfield_style" id="storageDeviceContainerId">
          <option>Matched Item List</option>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="40" colspan="2" align="right" valign="bottom" scope="row">Storage Location：</td>
      <td height="40" colspan="2">&nbsp;</td>
      <td width="303" height="40">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageDoor">Door</label></td>
      <td width="145" height="60" align="center">
        <select type="text" name="storageDoor" id="storageDoor" class="select_textfield_style">
          <option>Door</option>
        </select></td>
      <td width="208"><label for="storagePartition">Partition</label>       &nbsp;
        <select type="text" name="storagePartition" id="storagePartition" class="select_textfield_style">
          <option>Partition</option>
        </select></td>
      <td width="303" height="60">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row">Last Transaction：</td>
      <td height="60" colspan="2">&nbsp;Peter&nbsp;&nbsp;&nbsp;2014/05/08 15:50&nbsp;&nbsp;&nbsp;out</td>
      <td height="60" align="left">&nbsp;</td>
    </tr>
     <tr>
       <td height="60" colspan="2" align="right" scope="row"><label for="storageItemOwner">Owner：</label></td>
       <td height="60" colspan="2">
         <select name="storageItemOwner" class="textfield_style" id="storageItemOwner">
           <option>Item Owner List</option>
        </select></td>
       <td width="303" height="60" align="left">&nbsp;</td>
     </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemDescription">Work Sheet：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemDescription" type="text" class="textfield_style" id="storageItemDescription"></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemMSDLevel">MSD Level：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemMSDLevel" type="text" class="textfield_style" id="storageItemMSDLevel"></td>
      <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
    <tr>
      <td height="60" colspan="2" align="right" scope="row"><label for="storageItemExpirationTime">Expiration Time：</label></td>
      <td height="60" colspan="2">
        <input name="storageItemExpirationTime" type="text" class="textfield_style" id="storageItemExpirationTime"></td>
        <td width="303" height="60" align="left">&nbsp;</td>
    </tr>
   
    <tr>
      <td></td>
      <td colspan="3" align="center" scope="row">&nbsp;</td>
      <td></td>
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
    <tr><td colspan="5" align="center"><input type="submit" name="NewStorageSubmit" id="NewStorageSubmit" class="css_btn_class" value="Save">&nbsp;<input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href='listItem.jsp'" value="Close"></td>
      </tr>
</table>
  </form>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>