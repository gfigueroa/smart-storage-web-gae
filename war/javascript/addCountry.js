// JavaScript Document
$(document).ready(function(){
	$("#empty_CountryName").hide();
	$("#NewCountryNameSubmit").click(function(){
		var countryName = $("#countryName").val();
		event.preventDefault();
		if(countryName.length > 0 ){ $("#addCountryform").submit(); } else { $("#empty_CountryName").show(); }
		
	});
	
	
	
});