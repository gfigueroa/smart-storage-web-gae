// JavaScript Document
$(document).ready(function(){
	$("#NewCustomerSubmit").click(function(){
		event.preventDefault();
		
		$(".errorText").hide();
		var customerPrivilegeLevel = $("#customerPrivilegeLevel").val();
		var CustomerName = $("#customerName").val();
		var regionId = $("#regionId").val();
		var userEmail = $("#userEmail");
		var userPassword = $("#userPassword");
		var passwordCheck = $("#passwordCheck");
		var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;

		if(regionId == -1){ $("#empty_regionId").show(); }
		if(CustomerName.length == 0){ $("#empty_customerName").show(); $("#customerName").focus(); }
		if(customerPrivilegeLevel == -1){ $("#empty_customerPrivilegeLevel").show(); }
		var errorstype = 0;
		
		if(userPassword.val().length == 0){ $("#empty_userPassword").show(); userPassword.focus(); var errorstype = 1; } 
		else if(userPassword.val() != passwordCheck.val()){ $("#empty_passwordCheck").show(); passwordCheck.focus(); var errorstype = 1; }
		
		if(userEmail.val().length == 0){ $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		else if(!emailReg.test( userEmail.val() )) { $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		
		if(errorstype == 0){ $("#addCustomerform").submit() }
	});
})