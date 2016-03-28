<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>

<% 
	Printer printer = new Printer(Dictionary.Language.CHINESE);
	String language = request.getParameter("lang") != null ? request.getParameter("lang") : "CH";
	if(language.equals("EN")) {
			printer.setLanguage(Dictionary.Language.ENGLISH);
	}
	if(language.equals("CH")) {
			printer.setLanguage(Dictionary.Language.CHINESE);
	}

	final String TESTING_PRIVATE_KEY = 
			"xL354/vEK1BMWFwNDobgQizZKmn0APcVKkV11TqL4IY=";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link type="text/css" href="../stylesheets/default-layout.css" rel="stylesheet" />

<script language="JavaScript" type="text/javascript" src="../js/crypto.js">
</script>

<script src="http://crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/tripledes.js"></script>
<script src="http://crypto-js.googlecode.com/svn/tags/3.1.2/build/components/mode-ecb-min.js"></script>

<script language="JavaScript" type="text/javascript">

function encryptData()
{
	var content = document.getElementsByName("content")[0];
	content.value = "u_email=" + document.getElementsByName("u_email")[0].value + "&" +
  			"device_id=" + document.getElementsByName("device_id")[0].value + "&" +
  			"temperature=" + document.getElementsByName("temperature")[0].value + "&" +
  			"humidity=" + document.getElementsByName("humidity")[0].value + "&" +
  			"door_open=" + document.getElementsByName("door_open")[0].value + "&" +
  			"door_close=" + document.getElementsByName("door_close")[0].value + "&" +
  			"timestamp=" + document.getElementsByName("timestamp")[0].value;
	
	
	content.value = CryptoJS.TripleDES.encrypt(content.value, CryptoJS.enc.Utf8.parse("<%= TESTING_PRIVATE_KEY %>"), 
			{mode: CryptoJS.mode.ECB});
	
	return encodeData();
}

function encodeData()
{
	var content = document.getElementsByName("content")[0];
	content.value = encodeURIComponent(content.value);
	
	return true;
}

function checkPasswords()
{
  var passwordInput = document.getElementsByName("u_password")[0];
  var passwordConfirmInput = document.getElementsByName("u_password_confirm")[0];
  
  if (passwordInput.value == passwordConfirmInput.value) {
  	return encryptData();
  }
  else {
  	alert("<%=printer.print("The password you entered doesn't match the confirmation password")%>.");
    return false;
  }
}
</script>

<%@  include file="../header/page-title.html" %>
</head>

<body>

<form method="post" id="form1" name="form1" 
      action="/testing" 
      onsubmit="return checkPasswords();"
      class="form-style">

    <fieldset>
    <legend><%= printer.print("Device Reading") %></legend>
	
	<div>
	  <h2><%= printer.print("Data") %></h2>
	</div>

    <div>
        <label for="u_email"><span><%= printer.print("E-mail") %> <span class="required_field">*</span></span></label>
		<input type="text" name="u_email" class="input_extra_large" value="customer@smasrv.com" /><br />
		<div id="u_email"></div>
	</div>

    <div>
       	<label for="u_password"><span><%= printer.print("Password") %> <span class="required_field">*</span></span></label>
		<input type="password" name="u_password" class="input_extra_large" value="password" /><br />
		<div id="u_password"></div>
	</div>
	
	<div>
       	<label for="u_password_confirm"><span><%= printer.print("Confirm Password") %> <span class="required_field">*</span></span></label>
		<input type="password" name="u_password_confirm" class="input_extra_large" value="password" /><br />
		<div id="u_password_confirm"></div>
	</div>
	
	<div>
        <label for="device_id"><span><%= printer.print("Device ID") %> <span class="required_field">*</span></span></label>
		<input type="text" name="device_id" class="input_extra_large" value="abc123" /><br />
		<div id="device_id"></div>
	</div>
	
	<div>
        <label for="temperature"><span><%= printer.print("Temperature") %> <span class="required_field">*</span></span></label>
		<input type="text" name="temperature" class="input_extra_large" value="26.0" /><br />
		<div id="temperature"></div>
	</div>
	
	<div>
        <label for="humidity"><span><%= printer.print("Humidity") %> <span class="required_field">*</span></span></label>
		<input type="text" name="humidity" class="input_extra_large" value="50.0" /><br />
		<div id="humidity"></div>
	</div>
	
	<div>
        <label for="door_open"><span><%= printer.print("Door Open") %> <span class="required_field">*</span></span></label>
		<input type="text" name="door_open" class="input_extra_large" value="2014-03-05T21:22:19+08:00" /><br />
		<div id="door_open"></div>
	</div>
	
	<div>
        <label for="door_close"><span><%= printer.print("Door Close") %> <span class="required_field">*</span></span></label>
		<input type="text" name="door_close" class="input_extra_large" value="2014-03-05T22:22:19+08:00" /><br />
		<div id="door_close"></div>
	</div>
	
	<div>
        <label for="timestamp"><span><%= printer.print("Timestamp") %> <span class="required_field">*</span></span></label>
		<input type="text" name="timestamp" class="input_extra_large" value="2014-03-05T23:22:19+08:00" /><br />
		<div id="timestamp"></div>
	</div>
	
	<input type="text" name="content" value="" style="display:none;" />
	
	</fieldset>
  
	<br class="clearfloat" />
	
	<input type="button" value="<%= printer.print("Close") %>" onclick="location.href='/login.jsp'" class="button-close"/>

	<input type="submit" value="<%= printer.print("Update") %>" class="button_style"/>

</form>

</body>
</html>