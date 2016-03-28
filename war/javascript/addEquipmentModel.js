// JavaScript Document
$(document).ready(function(){
	$('#deviceModelDesignTime').datetimepicker({format:'Y-m-d', timepicker:false});
	
	//check fields
	$("#NewModelSubButton").click(function(){
		var errorfiled = 0;
		$(".errorText").hide();
		if($("#sensorDataUploadPeriod").val().length == 0){ $("#empty_sensorDataUploadPeriod").show(); $("#sensorDataUploadPeriod").focus(); var errorfiled = 1; }
		if($("#deviceModelDesignTime").val().length == 0){ $("#empty_deviceModelDesignTime").show(); $("#deviceModelDesignTime").focus(); var errorfiled = 1; }
		if($("#deviceModelName").val().length == 0){ $("#empty_deviceModelName").show(); $("#deviceModelName").focus(); var errorfiled = 1; }
		if(errorfiled != 0){ return false; }
	});
	
   
	$("#doorOpenClose").change(function(){
		controldooropenclosesettings(this);
	});
	
	if($("#doorOpenClose").is(':checked')){
		controldooropenclosesettings($("#doorOpenClose"));
	}
});

function controldooropenclosesettings(self){
	if($(self).prop("checked") == true)
	 {
		var amountTotal = 10;
		for(var i=1; i <= amountTotal; i++){
			var loopString = loopString+"<option value='"+i+"'>"+i+"</option>";
			
		}
		var selectContent = "  <select name='doorAmount' id='doorAmount' onChange='callselectmodeldoor();'><option value='-1'>Num</option>"+loopString+"</select>";
       $("label[for='doorOpenClose']").after(selectContent);
	 }
	 else
	 {
		 $("#doorAmount").remove();
		 if($("#selectModelDoorNum").length != 0){ $("#selectModelDoortable").next().remove(); }
	 }
}
function callselectmodeldoor() {
	$("tr[id^='createPartitionTable']").remove();
	$("tr[class^='addedPartitiontext']").remove();
	if($("#doorAmount").val() != "-1")
	{
		if($("#selectModelDoorNum").length != 0){ $("#selectModelDoortable").next().remove(); }
		var doorAmount = $("#doorAmount").val();
		for(var i=1; i <= doorAmount; i++)
		{
			var loopDoorName = loopDoorName+"<option value='"+i+"'>Door "+i+"</option>";
		}
		
		$("#selectModelDoortable").after("<tr>      <td width='130' height='60' align='right' scope='row'>&nbsp;</td><td height='60' align='right' scope='row'>Select storage door：</td>      <td width='354' height='60'><select name='selectModelDoorNum' id='selectModelDoorNum' class='textfield_style' onChange='callselectmodeldoorchange("+doorAmount+");'>"+loopDoorName+"</select></td>      <td width='303' height='60'></td>    </tr><tr id='createPartitionTable_1'>      <td width='130' height='60' align='right' scope='row'>&nbsp;</td>      <td height='60' align='right' scope='row'>&nbsp;</td>      <td width='354' height='60'><a OnClick='addToPartition(1);' href='#0' class='font_18'><img src='../images/round_plus.png' width='20' height='20'>Add storage partition label</a></td>      <td width='303' height='60' align='left'>&nbsp;</td>    </tr>");
	}
}

function callselectmodeldoorchange(doorAmount) {
	for(var i=1; i <= doorAmount; i++)
		{
			$(".addedPartitiontext_"+i).hide();
			$("#createPartitionTable_"+i).hide();
		}
	var selectModelDoorNum = $("#selectModelDoorNum").val();
	if($(".addedPartitiontext_"+selectModelDoorNum).length != 0 && $("#createPartitionTable_"+selectModelDoorNum).length != 0){
		$(".addedPartitiontext_"+selectModelDoorNum).show(); $("#createPartitionTable_"+selectModelDoorNum).show(); 
	}
	else
	{
		$("#selectModelDoortable").next().after("<tr id='createPartitionTable_"+selectModelDoorNum+"'>      <td width='130' height='60' align='right' scope='row'>&nbsp;</td>      <td height='60' align='right' scope='row'>&nbsp;</td>      <td width='354' height='60'><a OnClick='addToPartition("+selectModelDoorNum+");' href='#0' class='font_18'><img src='../images/round_plus.png' width='20' height='20'>Add storage partition label</a></td>      <td width='303' height='60' align='left'>&nbsp;</td>    </tr>"); 
	}
}

function addToPartition(tablerow) {
	$("#createPartitionTable_"+tablerow).before("<tr class='addedPartitiontext_"+tablerow+"'>      <td width='130' height='60' align='right' scope='row'>&nbsp;</td>      <td height='60' align='right' scope='row'><label for='deviceModelPartitionName'>Storage partition label：</label></td>      <td width='354' height='60'><input name='deviceModelPartitionNameNo"+tablerow+"[]' type='text' class='textfield_style' id='deviceModelPartitionName'></td>      <td width='303' height='60'></td>    </tr>");
}