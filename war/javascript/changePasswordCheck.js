// JavaScript Document
$(document).ready(function(){
	$("#newPasswordSubmit").click(function(){
		var userNewPassword = $("#userNewPassword").val();
		var userPasswordCheck = $("#userPasswordCheck").val();
		var fieldserror = 0;
		if(userNewPassword.length == 0){ $("#empty_newPassword").show(); fieldserror++; } else { $("#empty_newPassword").hide(); }
		if(userNewPassword != userPasswordCheck){  $("#empty_passwordCheck").show(); fieldserror++; } else { $("#empty_passwordCheck").hide(); }
		
		
		
		if(fieldserror > 0){ return false; }
	});
	
	
});