// JavaScript Document
$(document).ready(function(e) {
 
	 //sensor chart effect
	 $("#toggleSensor1").click(function(){
		 $("#Sensor1Chart").slideToggle();
		 $("#rangechangediv1").slideToggle();
	 });
		 if($("#toggleSensor2").length > 0){
			$("#toggleSensor2").click(function(){
			$("#Sensor2Chart").slideToggle();
			});
		 }
	//temp submit the form
	$("#temprangeform #tempmin").keypress(function(e){
		code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13)
		{
			temprangecheck();
		}
	});
	$("#temprangeform #tempmax").keypress(function(e){
		code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13)
		{
			temprangecheck();
		}
	});
	
	//humid submit the form
	$("#humidrangeform #humidmin").keypress(function(e){
		code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13)
		{
			humidrangecheck();
		}
	});
	$("#humidrangeform #humidmax").keypress(function(e){
		code = (e.keyCode ? e.keyCode : e.which);
		if (code == 13)
		{
			humidrangecheck();
		}
	});
	//lock range button
	$("#DayButton").click(function(){
		WeekButtonClear();
	});
	$("#WeekButton").click(function(){
		WeekButtonClear();
	});
	$("#MonthButton").click(function(){
		WeekButtonClear();
	});
	
});

function temprangecheck() {
		var tempmin = $("#tempmin").val();
		var tempmax = $("#tempmax").val();
			$("#temprangeform").submit();
	}
function humidrangecheck() {
		var tempmin = $("#humidmin").val();
		var tempmax = $("#humidmax").val();
		$("#humidrangeform").submit();
}
function WeekButtonClear() {
	$("#DayButton").prop("disabled", true);
	$("#WeekButton").prop("disabled", true);
	$("#MonthButton").prop("disabled", true);
}