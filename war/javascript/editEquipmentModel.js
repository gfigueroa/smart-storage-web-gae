// JavaScript Document
$(document).ready(function(){
	$('#deviceModelDesignTime').datetimepicker({format:'Y-m-d', timepicker:false});
   
   //check fields
	$("#NewModelSubButton").click(function(){
		alert('hey');
		var errorfiled = 0;
		$(".errorText").hide();
		if($("#sensorDataUploadPeriod").val().length == 0){ $("#empty_sensorDataUploadPeriod").show(); $("#sensorDataUploadPeriod").focus(); var errorfiled = 1; }
		if($("#deviceModelDesignTime").val().length == 0){ $("#empty_deviceModelDesignTime").show(); $("#deviceModelDesignTime").focus(); var errorfiled = 1; }
		if($("#deviceModelName").val().length == 0){ $("#empty_deviceModelName").show(); $("#deviceModelName").focus(); var errorfiled = 1; }
		if(errorfiled != 0){ return false; }
	});
});