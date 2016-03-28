// JavaScript Document
$(document).ready(function(){
	$("#empty_DepartmentName").hide();
	$("#NewDepartmentNameSubmit").click(function(){
		var departmentName = $("#departmentName").val();
		event.preventDefault();
		if(departmentName.length > 0 ){ $("#addDepartmentform").submit(); } else { $("#empty_DepartmentName").show(); }
		
	});
	
	
	
});