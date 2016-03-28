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
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%@include file="../header/header.jsp" %>
<link href="../css/public.css" rel="stylesheet" type="text/css" />
<title>SMASRV IoT</title>

<script language="JavaScript" type="text/javascript">

function encodeData()
{
	var content = document.getElementsByName("content")[0];
	content.value =
 			document.getElementsByName("device_id")[0].value + "," +
 			document.getElementsByName("type")[0].value + "," +
 			document.getElementsByName("temp1")[0].value + "," +
 			document.getElementsByName("hum1")[0].value + "," +
 			document.getElementsByName("temp2")[0].value + "," +
 			document.getElementsByName("hum2")[0].value;
	
	content.value = encodeURIComponent(content.value);
	
	return true;
}

</script>
</head>

<body>

<form method="post" id="form1" name="form1" 
      action="/sensorDataCacheUpload" 
      onsubmit="return encodeData();"
      class="form-style">

    <fieldset>
    <legend><%= printer.print("Sensor Data Cache Upload Test") %></legend>
	
	<div>
	  <h2><%= printer.print("Data") %></h2>
	</div>
	
	<div>
        <label for="device_id"><span><%= printer.print("Device Serial Number") %> <span class="required_field">*</span></span></label>
		<input type="text" name="device_id" class="input_extra_large" value="123" /><br />
		<div id="device_id"></div>
	</div>
	
	<div>
        <label for="type"><span><%= printer.print("Type") %> <span class="required_field">*</span></span></label>
		<input type="text" name="type" class="input_extra_large" value="per" readonly="readonly"/><br />
		<div id="type"></div>
	</div>
	
	<div>
        <label for="temp1"><span><%= printer.print("Temperature 1") %></span></label>
		<input type="text" name="temp1" class="input_extra_large" value="26.0" /><br />
		<div id="temp1"></div>
	</div>
	
	<div>
        <label for="hum1"><span><%= printer.print("Humidity 1") %></span></label>
		<input type="text" name="hum1" class="input_extra_large" value="0.45" /><br />
		<div id="hum1"></div>
	</div>
	
	<div>
        <label for="temp2"><span><%= printer.print("Temperature 2") %></span></label>
		<input type="text" name="temp2" class="input_extra_large" value="30.5" /><br />
		<div id="temp2"></div>
	</div>

	<div>
        <label for="hum2"><span><%= printer.print("Humidity 2") %></span></label>
		<input type="text" name="hum2" class="input_extra_large" value="0.1" /><br />
		<div id="hum2"></div>
	</div>
	
	<input type="text" name="content" value="" style="display:none;" />
	
	</fieldset>
  
	<br class="clearfloat" />

	<input type="submit" value="<%= printer.print("Update") %>" class="button_style"/>

</form>

</body>
</html>