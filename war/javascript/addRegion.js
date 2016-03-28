// JavaScript Document
$(document).ready(function(){
	$(".errorText").hide();
	$("#AddRegionSubmit").click(function(){
		$(".errorText").hide();
		var error_counter = 0;
		var regionName = $("#regionName").val();
		event.preventDefault();
		if(regionName.length > 0){ $("#addRegionform").submit(); } else { $("#empty_RegionName").show(); }
		
	});
	
});