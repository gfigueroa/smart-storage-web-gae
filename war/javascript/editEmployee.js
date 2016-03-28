// JavaScript Document
$(document).ready(function(){
	$("#NewCustomerSubmit").click(function(){
		event.preventDefault();
		
		$(".errorText").hide();
		var CustomerName = $("#customerUserName").val();

		
		var errorstype = 0;
		if(CustomerName.length == 0){ $("#empty_customerName").show(); $("#customerName").focus(); var errorstype = 1; }
		
		if(errorstype == 0){ $("#editCustomerform").submit() }
	});
})