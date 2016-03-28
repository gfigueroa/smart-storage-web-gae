// JavaScript Document

function EmployeeShowFunction(trnum, obj) {
	$(".listEmployeeHide:eq("+trnum+") tr:not(:first-child)").stop();
	$(".listEmployeeHide:eq("+trnum+") tr:not(:first-child)").toggle("slow");
	
}