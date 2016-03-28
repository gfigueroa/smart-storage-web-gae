// JavaScript Document
$(document).ready(function(){	
	
	$("#NewAdminSubmit").click(function(){
		event.preventDefault();
		hideAllErrors ();
		var errorstype = 0;
		
		
		
		//定義欄位內容方便修改
		var administratorName = $("#administratorName");
		var userEmail = $("#userEmail");
		var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
		
		
		
		if(userEmail.val().length == 0){ $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		else if(!emailReg.test( userEmail.val() )) { $("#empty_userEmail").show(); userEmail.focus(); var errorstype = 1; }
		if(administratorName.val().length == 0){ $("#empty_administratorName").show(); administratorName.focus(); var errorstype = 1;}
		
		
		
		if(errorstype == 0){ $("#addAdminform").submit() }
		
		
	});
	
	
});


function hideAllErrors (){
	$(".errorText").hide();
	
}