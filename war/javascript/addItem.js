// JavaScript Document
$(document).ready(function(){
	
	$('#storageItemInstanceExpirationTime').datetimepicker({format:'Y-m-d', timepicker:false}).click(function(){
		//var scrollBottom = $(window).scrollTop() + $(window).height();
		$(window).scrollTop(400);	
	});
	$("#AddButton").click(function(){
		if($("#addStorageItemInstance").prop("checked"))
		{			
			$("#addInstanceArea input").val("");
			$("#addInstanceArea").stop();
			$("#addInstanceArea").slideUp();
			$("#addStorageItemInstance").prop("checked", false);
		}
		else
		{
			$("#addInstanceArea").stop();
			$("#addInstanceArea").slideDown();
			$("#addStorageItemInstance").prop("checked", true);
		}
	});
	$("#addStorageItemInstance").change(function(){
		if($("#addStorageItemInstance").prop("checked"))
		{
			$("#addInstanceArea").stop();
			$("#addInstanceArea").slideDown();
		}
		else
		{
			$("#addInstanceArea input").val("");
			$("#addInstanceArea").stop();
			$("#addInstanceArea").slideUp();
		}
		
	});
});