// JavaScript Document
$(document).ready(function(e) {	
	ChangeDeviceID();
	
	//when mouse over show the Location message
	$(".listItemInstanceShowLocation").hover(function(){
		var locationdoor = $(this).attr('locationdoor');
		var locationpart = $(this).attr('locationpart');
		if((locationdoor.length != '0') && (locationpart.length != '0')){
			var tooltip_html = "<div id='tooltip'><table><tr><td height='25' width='80' align='right'>Door：</td><td>"+locationdoor+"<td></tr><tr><td height='25' width='80' align='right'>Partition：</td><td>"+locationpart+"</td></tr></table></div>";	
			var x = 10;	
			var y = 20;	
			$("body").append(tooltip_html);
			$("#tooltip").css({"top":(event.pageY+y)+"px","left":(event.pageX+x)+"px"}).fadeIn("fast");
			$(this).mousemove(function(){ $("#tooltip").css({"top":(event.pageY+y)+"px","left":(event.pageX+x)+"px"}); });
		}
	},function(){ 
		$("#tooltip").remove(); 		
	});
	
	
	
	
	$("#storageDeviceId").change(function(){
			ChangeDeviceID();	
	});
	
	function ChangeDeviceID(){
		var storageDeviceId = $("#storageDeviceId").val();
		
		if(storageDeviceId == '')
		{
			$("#storageDeviceDoorId").attr("disabled", true).find('option:first').prop("selected", true);			
			$("#storageDevicePartitionId").attr("disabled", true).find('option:first').prop("selected", true);
		}
		else
		{
			$("#storageDeviceDoorId").attr("disabled", false);
			$("#storageDevicePartitionId").attr("disabled", false);
		}		
	}
	
	
	
	$("#advanced_search_link").click(function () {
		if($("#advanced_search").css("display") == "none")
		{
		  $("#advanced_search").slideDown();
		}
		else
		{
			$("#advanced_search").slideUp();
			clearAdvance ();
		}
	});
  
	$("#Advanced_Search_close").click(function () {
		$("#advanced_search").slideUp();
		clearAdvance ();
	});
	
	
	function clearAdvance () {
		$("#searchOwner").val("");
		$("#searchDate").val("");
		$("#searchWorksheet").val("");
		
	}
	
});
