// JavaScript Document
$(document).ready(function() {
	//$(".listStorageHide tr:not(:first-child)").hide();
});



function deviceShowFunction(trnum, obj) {
	$(".listStorageHide:eq("+trnum+") tr:not(:first)").stop();
	$(".listStorageHide:eq("+trnum+") tr:not(:first)").toggle("slow");
	
}