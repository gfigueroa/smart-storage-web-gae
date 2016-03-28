// JavaScript Document
$(document).ready(function(){
	//隱藏所有提示視窗
	//hideAllErrors ();
	
	
	$("#NewAdminSubmit").click(function(){
		event.preventDefault();
		hideAllErrors ();
		var errorstype = 0;
		
		
		
		//定義欄位內容方便修改
		var administratorName = $("#administratorName");
		var userEmail = $("#userEmail");
		var userPassword = $("#userPassword");
		var passwordCheck = $("#passwordCheck");
		var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
		
		
		
		if(userPassword.val().length == 0){ $("#empty_userPassword").show(); userPassword.focus(); var errorstype = 1; } 
		else if(userPassword.val() != passwordCheck.val()){ $("#empty_passwordCheck").show(); passwordCheck.focus(); var errorstype = 1; }
		if(userEmail.val().length == 0){ $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		else if(!emailReg.test( userEmail.val() )) { $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		if(administratorName.val().length == 0){ $("#empty_administratorName").show(); administratorName.focus(); var errorstype = 1;}
		
		
		
		if(errorstype == 0){ $("#addAdminform").submit() }
		
		
	});
	
	
});


function hideAllErrors (){
	$(".errorText").hide();
	
}