// JavaScript Document
$(document).ready(function(){
	$('#storageDeviceManufacturedDate').datetimepicker({format:'Y-m-d', timepicker:false});
	$('#storageDeviceShippingDate').datetimepicker({format:'Y-m-d', timepicker:false});
	$("#NewStorageSubmit").click(function(){
		$(".errorText").hide();
		var error_counter = 0;
		var storageDeviceSerialNumber = $("#storageDeviceSerialNumber").val();
		var storageDeviceManufacuredDate = $("#storageDeviceManufacturedDate").val();
		var storageDeviceShippingDate = $("#storageDeviceShippingDate").val();
		var storageDeviceNickname = $("#storageDeviceNickname").val();
		var sensorDataEffectivePeriod = $("#sensorDataEffectivePeriod").val();
		var alarmMessageEffectivePeriod = $("#alarmMessageEffectivePeriod").val();
		
		if(storageDeviceShippingDate.length == 0){ $("#empty_DeviceShipping").show(); var error_counter = 1;}
		if(storageDeviceManufacuredDate.length == 0){ $("#empty_DeviceManufactured").show(); var error_counter = 1; }
		
		if(alarmMessageEffectivePeriod.length == 0 || isNaN(alarmMessageEffectivePeriod)){ $("#empty_alarmMessageEffectivePeriod").show(); $("#alarmMessageEffectivePeriod").focus(); var error_counter = 1; }
		if(sensorDataEffectivePeriod.length == 0 || isNaN(sensorDataEffectivePeriod)){ $("#empty_sensorDataEffectivePeriod").show(); $("#sensorDataEffectivePeriod").focus(); var error_counter = 1; }
		
		if(storageDeviceSerialNumber.length == 0){ $("#empty_DeviceSerialNumber").show(); $("#storageDeviceSerialNumber").focus(); var error_counter = 1; }
		if(storageDeviceNickname.length == 0){ $("#empty_storageDeviceNickname").show(); $("#storageDeviceNickname").focus(); var error_counter = 1; }
		if(error_counter != 0) { return false; }
		
		
		
	});
	
});