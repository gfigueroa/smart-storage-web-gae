<%@ page contentType="text/html; charset=utf-8" language="java" errorPage="" %>
<%@ page import="datastore.SensorInstance" %>
<%@ page import="datastore.SensorInstanceManager" %>
<%@ page import="datastore.SensorReading" %>
<%@ page import="datastore.SensorReadingManager" %>
<%@ page import="datastore.SensorReadingManager.Period" %>
<%@ page import="datastore.StorageDevice" %>
<%@ page import="datastore.StorageDeviceManager" %>
<%@ page import="datastore.User" %>
<%@ page import="datastore.UserManager" %>
<%@ page import="util.DateManager"  %>
<%@ page import="util.Printer" %>
<%@ page import="util.Dictionary" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<!doctype html>
<html>

<%

String sessionUserString = request.getParameter("user");
User sessionUser;
if (sessionUserString != null && sessionUserString.equals("customer")) {
	sessionUser = UserManager.getUser("customer@smasrv.com");
}
else {
	sessionUser = UserManager.getUser("jason@uconnect.com.tw");
}

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

String storageDeviceKeyString = request.getParameter("k");
Key storageDeviceKey = KeyFactory.stringToKey(storageDeviceKeyString);
StorageDevice storageDevice = StorageDeviceManager.getStorageDevice(storageDeviceKey);

// Data period
String periodString = request.getParameter("period");
Period period = Period.DAY;
if (periodString != null) {
	period = SensorReadingManager.getPeriodFromString(periodString);
}

//Temperature data
int inttempmin = 0;
int inttempmax = 0;
if(request.getParameter("tempmin") != null || request.getParameter("tempmax") != null) {
	String tempmin = request.getParameter("tempmin");
	String tempmax = request.getParameter("tempmax");
	inttempmin = Integer.parseInt(tempmin);
	inttempmax = Integer.parseInt(tempmax);
	if(inttempmin >= inttempmax || (inttempmax > 100 || inttempmax < 0) || (inttempmin > 100 || inttempmin < 0)) {
		//if the format isn't currect, the value will change defaul.
		inttempmin = 0;
		inttempmax = 100;
	}
}
else {
	//if it doesn't get URL paramters, the value will change defaul.
	inttempmin = 0;
	inttempmax = 100;
}
//Humidity data
int inthumidmin = 0;
int inthumidmax = 0;
if(request.getParameter("humidmin") != null || request.getParameter("humidmax") != null) {
	String humidmin = request.getParameter("humidmin");
	String humidmax = request.getParameter("humidmax");
	inthumidmin = Integer.parseInt(humidmin);
	inthumidmax = Integer.parseInt(humidmax);
	if(inthumidmin >= inthumidmax || (inthumidmax > 100 || inthumidmax < 0) || (inthumidmin > 100 || inthumidmin < 0)) {
		//if the format isn't currect, the value will change defaul.
		inthumidmin = 0;
		inthumidmax = 100;
	}
}
else {
	//if it doesn't get URL paramters, the value will change defaul.
	inthumidmin = 0;
	inthumidmax = 100;
}

// Get sensor data for tables and charts
List<SensorInstance> sensorInstances = SensorInstanceManager.getAllSensorInstancesFromStorageDevice(storageDeviceKey);
List<SensorReading> chartTemp1SensorReadings = null;
List<SensorReading> chartHum1SensorReadings = null;
List<SensorReading> chartTemp2SensorReadings = null;
List<SensorReading> chartHum2SensorReadings = null;
List<SensorReading> temp1SensorReadings = null;
List<SensorReading> hum1SensorReadings = null;
List<SensorReading> temp2SensorReadings = null;
List<SensorReading> hum2SensorReadings = null;
for (SensorInstance sensorInstance : sensorInstances) {
	if (sensorInstance.getSensorInstanceLabel().equals("Temperature_1")) {
		// temp1SensorReadings = SensorReadingManager.getAllSensorReadingsFromSensorInstance(sensorInstance.getKey(), (long) 1000);
		temp1SensorReadings = SensorReadingManager.getSensorReadingsFromSensorInstance(sensorInstance.getKey(), period);
		chartTemp1SensorReadings = SensorReadingManager.getFixedSensorReadings(temp1SensorReadings, period);
	}
	else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_1")) {
		hum1SensorReadings = SensorReadingManager.getSensorReadingsFromSensorInstance(sensorInstance.getKey(), period);
		chartHum1SensorReadings = SensorReadingManager.getFixedSensorReadings(hum1SensorReadings, period);
	}
	else if (sensorInstance.getSensorInstanceLabel().equals("Temperature_2")) {
		temp2SensorReadings = SensorReadingManager.getSensorReadingsFromSensorInstance(sensorInstance.getKey(), period);
		chartTemp2SensorReadings = SensorReadingManager.getFixedSensorReadings(temp2SensorReadings, period);
	}
	else if (sensorInstance.getSensorInstanceLabel().equals("Humidity_2")) {
		hum2SensorReadings = SensorReadingManager.getSensorReadingsFromSensorInstance(sensorInstance.getKey(), period);
		chartHum2SensorReadings = SensorReadingManager.getFixedSensorReadings(hum2SensorReadings, period);
	}
}

// Get min and max sensor data
double minTemp1 = Double.POSITIVE_INFINITY;
double minHum1 = Double.POSITIVE_INFINITY;
double minTemp2 = Double.POSITIVE_INFINITY;
double minHum2 = Double.POSITIVE_INFINITY;
double maxTemp1 = Double.NEGATIVE_INFINITY;
double maxHum1 = Double.NEGATIVE_INFINITY;
double maxTemp2 = Double.NEGATIVE_INFINITY;
double maxHum2 = Double.NEGATIVE_INFINITY;
if (temp1SensorReadings != null) {
	for (SensorReading sensorReading : temp1SensorReadings) {
		try {
	double reading = Double.parseDouble(sensorReading.getSensorReadingValue());
	if (reading < minTemp1) {
		minTemp1 = reading;
	}
	if (reading > maxTemp1) {
		maxTemp1 = reading;
	}
		}
		catch (NumberFormatException nfe) {
	System.out.println("Invalid value for temp1.");
		}
	}
}
if (hum1SensorReadings != null) {
	for (SensorReading sensorReading : hum1SensorReadings) {
		try {
	double reading = Double.parseDouble(sensorReading.getSensorReadingValue());
	if (reading < minHum1) {
		minHum1 = reading;
	}
	if (reading > maxHum1) {
		maxHum1 = reading;
	}
		}
		catch (NumberFormatException nfe) {
	System.out.println("Invalid value for hum1.");
		}
	}
}
if (temp2SensorReadings != null) {
	for (SensorReading sensorReading : temp2SensorReadings) {
		try {
	double reading = Double.parseDouble(sensorReading.getSensorReadingValue());
	if (reading < minTemp2) {
		minTemp2 = reading;
	}
	if (reading > maxTemp2) {
		maxTemp2 = reading;
	}
		}
		catch (NumberFormatException nfe) {
	System.out.println("Invalid value for temp2.");
		}
	}
}
if (hum2SensorReadings != null) {
	for (SensorReading sensorReading : hum2SensorReadings) {
		try {
	double reading = Double.parseDouble(sensorReading.getSensorReadingValue());
	if (reading < minHum2) {
		minHum2 = reading;
	}
	if (reading > maxHum2) {
		maxHum2 = reading;
	}
		}
		catch (NumberFormatException nfe) {
	System.out.println("Invalid value for hum2.");
		}
	}
}

// Generate strings
List<String> temp1Hum1SensorReadings = new ArrayList<String>();
List<String> temp2Hum2SensorReadings = new ArrayList<String>();
if (temp1SensorReadings != null && hum1SensorReadings != null) {
	int listSize = temp1SensorReadings.size() <= hum1SensorReadings.size() ? temp1SensorReadings.size() : hum1SensorReadings.size();
	for (int i = 0; i < listSize; i++) {
		String temp1Hum1SensorReading = DateManager.printDateAsString(temp1SensorReadings.get(i).getSensorReadingTime()) + " - " + temp1SensorReadings.get(i).getSensorReadingValue() + "°C / " + hum1SensorReadings.get(i).getSensorReadingValue() + "%";
		temp1Hum1SensorReadings.add(temp1Hum1SensorReading);
	}
}
if (temp2SensorReadings != null && hum2SensorReadings != null) {
	int listSize = temp2SensorReadings.size() <= hum2SensorReadings.size() ? temp2SensorReadings.size() : hum2SensorReadings.size();
	for (int i = 0; i < listSize; i++) {
		String temp2Hum2SensorReading = DateManager.printDateAsString(temp2SensorReadings.get(i).getSensorReadingTime()) + " - " + temp2SensorReadings.get(i).getSensorReadingValue() + "°C / " + hum2SensorReadings.get(i).getSensorReadingValue() + "%";
		temp2Hum2SensorReadings.add(temp2Hum2SensorReading);
	}
}
%>

<head>
<meta charset="utf-8">
<title>U-connect</title>
<link href="../css/public.css" rel="stylesheet" type="text/css">
<link href="../css/main_list_css.css" rel="stylesheet" type="text/css">
<link href="../css/listSensorData.css" rel="stylesheet" type="text/css">

<script src="../javascript/selectDate.js" type="text/javascript">
</script>
<script src="../javascript/jquery-1.10.1.min.js"></script>
<script src="../javascript/listSensorData.js"></script>
<script src="../javascript/highcharts.js"></script>
<script src="../javascript/exporting.js"></script>
<script>
$(function () {
		var TodayDate=new Date();
		<% if(request.getParameter("period") != null && request.getParameter("period").equals("week")) { %>
		TodayDate.setDate(TodayDate.getDate()-7);
		<% } else if(request.getParameter("period") != null && request.getParameter("period").equals("month")) { %>
		TodayDate.setDate(TodayDate.getDate()-30);
		<% } else { %>		
		TodayDate.setDate(TodayDate.getDate()-1);			
		<% } %>
        $('#Sensor1Chart').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: 'Sensor Temperature and Humidity Charts'
            },
            subtitle: {
                text: '<% if(request.getParameter("period") != null && request.getParameter("period").equals("week")) { out.print("Week"); } else if(request.getParameter("period") != null && request.getParameter("period").equals("month")) { out.print("Month"); } else { out.print("Date"); } %>'
            },
            xAxis: {
                type: 'datetime',
                labels: {
                    overflow: 'justify'
                }
            },
            yAxis: [{ // Primary yAxis
			min:<%= inthumidmin %>,
			max:<%= inthumidmax %>,
			startOnTick: false,
            labels: {
                format: '{value}%',
                style: {
                    color: Highcharts.getOptions().colors[2]
                },
            },
            title: {
                text: 'Humidity scale',
                style: {
                    color: Highcharts.getOptions().colors[2]
                }
            },
			alternateGridColor: null,
				plotBands: [{
					from: 0,
					to: 100,
					color:'rgba(68, 170, 213, 0.1)',
				}],
            opposite: true

        }, { // Secondary yAxis
		
			min:<%= inttempmin %>,
			max:<%= inttempmax %>,
            title: {
                text: 'Temperature scale',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            labels: {
                format: '{value}°C',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
			alternateGridColor: null,
				plotBands: [{
					from: 0,
					to: 50,
					color:'rgba(68, 170, 213, 0.1)',
				}]
        }],
            tooltip: {
                valueSuffix: ''
            },
            plotOptions: {
                spline: {
                    lineWidth: 2,
                    states: {
                        hover: {
                            lineWidth: 3
                        }
                    },
                    marker: {
						radius:2,
                        enabled: true,
                    },
                    pointInterval: <% if(request.getParameter("period") != null && request.getParameter("period").equals("week")) { %>70 * 60 * 1000,<% } else if(request.getParameter("period") != null && request.getParameter("period").equals("month")) { %>300 * 60 * 1000,<% } else { %>10 * 60 * 1000, <% } %>
                    pointStart: Date.UTC(TodayDate.getFullYear(), TodayDate.getMonth(), TodayDate.getDate(), TodayDate.getHours(), TodayDate.getMinutes()+
					<% if(request.getParameter("period") != null && request.getParameter("period").equals("week")) { %>
					70
					<% } else if(request.getParameter("period") != null && request.getParameter("period").equals("month")) { %>
					300
					<% } else { %>
					10
					<% } %>, 0)
                }
            },
            series: [{
            name: 'Temperature',
            type: 'spline',
            yAxis: 1,
            data: [<%
                   for (int i = 0; i < chartTemp1SensorReadings.size(); i++) {
                	   SensorReading sensorReading = chartTemp1SensorReadings.get(i);
                	   if (sensorReading != null) {
                		   out.print(sensorReading.getSensorReadingValue());
                	   }
                	   else {
                		   out.print("null");
                	   }
                	   if (i < chartTemp1SensorReadings.size() - 1) {
                		   out.print(", ");
                	   }
                   } 
                   %>],
            tooltip: {
                valueSuffix: ' °C'
            }

        }, {
            name: 'Humidity',
            type: 'spline',
            data: [<%
                   for (int i = 0; i < chartHum1SensorReadings.size(); i++) {
                	   SensorReading sensorReading = chartHum1SensorReadings.get(i);
                	   if (sensorReading != null) {
                		   out.print(sensorReading.getSensorReadingValue());
                	   }
                	   else {
                		   out.print("null");
                	   }
                	   if (i < chartHum1SensorReadings.size() - 1) {
                		   out.print(", ");
                	   }
                   } 
                   %>],
            tooltip: {
                valueSuffix: ' %'
            }
        }]
            ,
            navigation: {
                menuItemStyle: {
                    fontSize: '10px'
                }
            }
        });
    });
</script>

</head>

<body>
<%@include file="../header/headerUconnect.jsp" %>


<div id="menu_back"><div id="menu"><%@include file="../menu/Uconnect_menu.jsp" %></div></div>
<div id="content_area">
  <div class="title_bar title_name"><table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="50" valign="middle">Sensor Data</td>
    </tr>
</table>
  </div>
  <form name="DeviceSearchForm" method="post" action="">
  <table width="800" border="0" align="center" cellpadding="1" cellspacing="0">
  <tr>
    <td height="80" colspan="4" align="center" class="form_underline" scope="row"><input name="DayButton" type="button" class="<% if(request.getParameter("period") != null &&request.getParameter("period").equals("day")) { out.print("css_btn_class_onclick"); } else { out.print("css_btn_class"); } %>" id="DayButton" title="Day" onclick="window.location.href = '?k=<% out.print(request.getParameter("k")); %>&period=day';" value="&nbsp;&nbsp;Day&nbsp;&nbsp;">
      <input name="WeekButton" type="button" class="<% if(request.getParameter("period") != null &&request.getParameter("period").equals("week")) { out.print("css_btn_class_onclick"); } else { out.print("css_btn_class"); } %>" id="WeekButton" title="Week" onclick="window.location.href = '?k=<% out.print(request.getParameter("k")); %>&period=week';" value="Week">
      <input name="MonthButton" type="button" class="<% if(request.getParameter("period") != null &&request.getParameter("period").equals("month")) { out.print("css_btn_class_onclick"); } else { out.print("css_btn_class"); } %>" id="MonthButton" title="Month" onclick="window.location.href = '?k=<% out.print(request.getParameter("k")); %>&period=month';" value="Month"></td>
  </tr>
  <tr>
    <td width="20%" height="15" align="right" scope="row">&nbsp;</td>
    <td width="32%" height="15">&nbsp;</td>
    <td width="16%" height="15" align="right">&nbsp;</td>
    <td height="15">&nbsp;</td>
  </tr>
</table>
</form>
<div id="toggleSensor1" class="listTitle_CSS form_underline_blue radiusLeft radiusRight SensorToggleBox">
  <table width="100%" border="0">
    <tbody>
      <tr>
        <td align="center"><span style="float:left; position:absolute;left:10px;">Sensor 1 Chart</span><%= storageDevice.getStorageDeviceSerialNumber() + " (" + storageDevice.getStorageDeviceNickname() + ")" %></td>
        </tr>
    </tbody>
  </table>
</div>
<div id="rangechangediv1" class="tempandhimrange">
<form action="" method="get" name="temprangeform" id="temprangeform">
  <input name="k" type="hidden" id="k" value="<% if(request.getParameter("k") != null){ out.print(request.getParameter("k")); } %>">
<input name="period" type="hidden" id="period" value="<% if(request.getParameter("period") != null){ out.print(request.getParameter("period")); } %>">
<div id="temp1rangemin">
  <input name="tempmin" type="text" class="textfield_style" id="tempmin" placeholder="min" style="width:60px;height:20px;margin-left:10px;text-align:center;" value="<%= inttempmin %>">  
  </div>
  
  <div id="temp1rangemax">
  <input name="tempmax" type="text" class="textfield_style" id="tempmax" placeholder="max" style="width:60px;height:20px;text-align:center;" value="<%= inttempmax %>">
  </div>
  <input name="humidmin" type="hidden" class="textfield_style" id="humidmin" placeholder="min" style="width:60px;height:20px;margin-left:2px;text-align:center;" value="<%= inthumidmin %>">
    <input name="humidmax" type="hidden" class="textfield_style" id="humidmax" placeholder="max" style="width:60px;height:20px;text-align:center;" value="<%= inthumidmax %>">
  </form>
  <form action="" method="get" name="humidrangeform" id="humidrangeform">
    <input name="k" type="hidden" id="k" value="<% if(request.getParameter("k") != null){ out.print(request.getParameter("k")); } %>">
<input name="period" type="hidden" id="period" value="<% if(request.getParameter("period") != null){ out.print(request.getParameter("period")); }  %>">
<input name="tempmin" type="hidden" class="textfield_style" id="tempmin" placeholder="min" style="width:60px;height:20px;margin-left:10px;text-align:center;" value="<%= inttempmin %>">  
  <input name="tempmax" type="hidden" class="textfield_style" id="tempmax" placeholder="max" style="width:60px;height:20px;text-align:center;" value="<%= inttempmax %>">
  <div id="hum1rangemin">
    <input name="humidmin" type="text" class="textfield_style" id="humidmin" placeholder="min" style="width:60px;height:20px;margin-left:2px;text-align:center;" value="<%= inthumidmin %>">
    </div>
    <div id="hum1rangemax">
    <input name="humidmax" type="text" class="textfield_style" id="humidmax" placeholder="max" style="width:60px;height:20px;text-align:center;" value="<%= inthumidmax %>">
    </div>
  </form>
  
</div>
  <div id="Sensor1Chart" class="SensorChartStyle"></div>

  
  <table width="800" border="0" align="center" cellpadding="0" cellspacing="0" class="device_form_line">
    <tr>
      <td height="40" colspan="3" align="center" class="listTitle_CSS form_underline_blue radiusLeft radiusRight"><%= storageDevice.getStorageDeviceSerialNumber() + " (" + storageDevice.getStorageDeviceNickname() + ")" %></td>
    </tr>
    <tr>
      <td height="40" colspan="3" align="center">
      <input type="button" name="buttonExport" id="buttonExport" class="css_btn_class" onclick="window.location.href = '/fileCreation?type=sensorData&storageDeviceId=<%= storageDeviceKeyString %>&period=<%= period %>';" value="Export"/>
      </td>
    </tr>
    <tr class="listCotent_CSS">
      <td width="50%" height="500" align="left" class="font_18 "><table width="355" border="0" align="center" cellpadding="0" cellspacing="0" class="device_sensor_form_line">
        <tr>
          <th colspan="3" bgcolor="#66CCFF" scope="row">Sensor 1</th>
        </tr>
        <tr>
          <td width="44%" height="120" align="right" scope="row"><p>Max：</p>
            <p>Min：</p>
          </td>
          <td width="56%" align="left" scope="row">
          	<p><%= maxTemp1 + "°C / " + maxHum1 + "%" %></p>
            <p><%= minTemp1 + "°C / " + minHum1 + "%" %></p>
          </td>
        </tr>
        <tr>
          <td height="348" colspan="3" align="center" scope="row">
          <textarea cols='40' rows='25'><% for (String temp1Hum1 : temp1Hum1SensorReadings) { %><%= temp1Hum1 %>&#13;&#10;<% } %></textarea>
          &nbsp;
          </td>
        </tr>
      </table></td>
      <td height="500" align="center" class="font_18 "><table width="355" border="0" align="center" cellpadding="0" cellspacing="0" class="device_sensor_form_line">
        <tr>
          <th colspan="3" scope="row" bgcolor="#66CCFF">Sensor 2</th>
        </tr>
        <tr>
          <td width="44%" height="120" align="right" scope="row"><p>Max：</p>
            <p>Min：</p>
          </td>
          <td width="56%" align="left" scope="row">
          	<p><%= maxTemp2 + "°C / " + maxHum2 + "%" %></p>
            <p><%= minTemp2 + "°C / " + minHum2 + "%" %></p>
          </td>
        </tr>
        <tr>
          <td height="348" colspan="3" align="center" scope="row">
          <textarea cols='40' rows='25'><% for (String temp2Hum2 : temp2Hum2SensorReadings) { %><%= temp2Hum2 %>&#13;&#10;<% } %></textarea>
          &nbsp;
          </td>
        </tr>
      </table></td>
    </tr>
  </table>
  
  <table border="0" align="center">
    <tr>
      <th scope="row"><input type="button" name="buttonClose" id="buttonClose" class="css_btn_class" onclick="window.location.href = 'listSensorUconnect.jsp';" value="Close"></th>
    </tr>
  </table>
</div>
<%@include file="../footer/footer.jsp" %>
</body>
</html>