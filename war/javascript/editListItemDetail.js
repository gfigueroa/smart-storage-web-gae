// JavaScript Document
function ItemRemove(removeid) {
	$("#RemoveArea").stop();
	if($("#removehiddenItemId").val() == removeid){ CancelEdit(); }
	else {
		CancelEdit();
		$("#removehiddenItemId").attr("value", removeid);
		$("#RemoveArea").show("normal");
	}
}

function CancelEdit() {
	$("#StoreArea").stop();
	$("#RemoveArea").stop();	
	$("#storageItemOwner option:eq(1)").remove();
	$("#StoreArea").hide("normal");
	$("#RemoveArea").hide("normal");
	$("#removehiddenItemId").attr("value", "");
	
}