// JavaScript Document
$(document).ready(function(){
	$('#newsReleaseDate').datetimepicker({format:'Y-m-d', timepicker:false}).click(function(){ $(window).scrollTop(200); });
	$('#newsExpirationDate').datetimepicker({format:'Y-m-d', timepicker:false}).click(function(){ $(window).scrollTop(200); });
	
});