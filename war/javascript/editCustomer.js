// JavaScript Document
$(document).ready(function(){
	$("#NewCustomerSubmit").click(function(){
		event.preventDefault();
		
		$(".errorText").hide();
		var customerPrivilegeLevel = $("#customerPrivilegeLevel").val();
		var CustomerName = $("#customerName").val();
		var regionId = $("#regionId").val();
		var userEmail = $("#userEmail");
		var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
		var errorstype = 0;
		
		if(regionId == -1){ $("#empty_regionId").show(); }
		if(CustomerName.length == 0){ $("#empty_CustomerName").show(); $("#customerName").focus(); }
		if(customerPrivilegeLevel == -1){ $("#empty_customerPrivilegeLevel").show(); }
		var errorstype = 0;
		
		if(userEmail.val().length == 0){ $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		else if(!emailReg.test( userEmail.val() )) { $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		
		if(errorstype == 0){ $("#addCustomerform").submit() }
	});
})