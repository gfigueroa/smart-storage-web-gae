// JavaScript Document
$(document).ready(function(e) {
	
	$('#doorStartTime').datetimepicker({
	  format:'Y-m-d H:i',
	  onShow:function( ct ){
	   this.setOptions({
	    maxDate:$('#doorEndTime').val()?$('#doorEndTime').val():false
	   })
	  },
	 });
 
	$('#doorEndTime').datetimepicker({
	  format:'Y-m-d H:i',
	  onShow:function( ct ){
	   this.setOptions({
	    minDate:$('#doorStartTime').val()?$('#doorStartTime').val():false
	   })
	  },
	 });
});

function changeDoorNumber(storageDeviceKey) {
	var doorNumber = document.getElementById("doorNumberList").value;
	window.location.replace("listDoorData.jsp?k=" + storageDeviceKey +
			"&doorNumber=" + doorNumber);
}