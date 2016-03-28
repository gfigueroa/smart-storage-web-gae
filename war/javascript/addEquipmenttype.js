// JavaScript Document
$(document).ready(function(){
	$("#empty_deviceServiceTypeName").hide();
	$("#NewDeviceTypeSubmit").click(function(){
		var deviceServiceTypeName = $("#deviceServiceTypeName").val();
		event.preventDefault();
		if(deviceServiceTypeName.length > 0 ){ $("#addDeviceTypeform").submit(); } else { $("#empty_deviceServiceTypeName").show(); }
		
	});
	
	
	
});