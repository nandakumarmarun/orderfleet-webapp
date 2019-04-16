if (!this.InventoryClosingReport) {
	this.InventoryClosingReport = {};
}

(function() {
	'use strict';

	var inventoryClosingReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	
	var InventoryClosingHeader = {
			selectedUserPid : "",
			inventoryClosingDetailProducts : [],
			inventoryClosingProductDetailSettingGroups : []
	};
	var InventoryClosingDetailProduct = {
			productProfileName:null,
			initial:0.0,
			closing:0.0,
	};
	
	var InventoryClosingProductDetailSettingGroup = {
			inventoryClosingReportSettingGroupPid :null,
			flow : null,
			
	};
	$(document).ready(function() {
		$("#btnClose").click(function(){
			closeInventory();
		});
	});
	
	InventoryClosingReport.onLoadReport = function() {
		if($('#dbEmployee').val() == "no"){
			alert("Please select Employee")
			return;
		}
		InventoryClosingHeader.selectedUserPid=$('#dbEmployee').val();
		$("#btnFilter").html('Loading...');
		$("#btnFilter").prop('disabled', true);
		
		$("#tbodyInventory").html("");
		$("#trInventory").html("");
		var inventoryClosingProductDetailSettingGroups=[] ;
		var inventoryClosingDetailProducts=[];
		$.ajax({
			url : inventoryClosingReportContextPath + "/loadClosingReport",
			type : 'GET',
			data : {
				employeePid:$('#dbEmployee').val()
			},
			success : function(inventoryClosingReport) {
				$("#btnFilter").html('Load');
				$("#btnFilter").prop('disabled', false);
				var th = "<th>Products</th><th>Initial</th>";
				if (inventoryClosingReport.reportSettingGroupDTOHeaders != null && inventoryClosingReport.reportSettingGroupDTOHeaders.length > 0) {
					$.each(inventoryClosingReport.reportSettingGroupDTOHeaders, function(index, inventoryClosingReportSetting) {
						inventoryClosingProductDetailSettingGroups.push({
							inventoryClosingReportSettingGroupPid : inventoryClosingReportSetting.pid,
							flow : inventoryClosingReportSetting.flow,
						});
						th +='<th>'+ inventoryClosingReportSetting.name +'</th>';
					});
					th +='<th>Closing</th>';
				}
				InventoryClosingHeader.inventoryClosingProductDetailSettingGroups=inventoryClosingProductDetailSettingGroups;
				$("#trInventory").append(th);
				
				var tbody = "";
				if (inventoryClosingReport.productQuantityMap != null) {
					$.each(inventoryClosingReport.productQuantityMap, function(pName, quantities) {
						InventoryClosingDetailProduct.productProfileName=pName;
						tbody +='<tr><td>'+ pName +'</td>';
					    $.each(quantities, function(index, qty) {
					    	if(index==0){
					    	InventoryClosingDetailProduct.initial=qty;
					    	}
					    	if(index==quantities.length-1){
					    	InventoryClosingDetailProduct.closing=qty;
					    	}
					    	tbody+='<td>'+ qty +'</td>';
						});
					    inventoryClosingDetailProducts.push({
					    	productProfileName:InventoryClosingDetailProduct.productProfileName,
					    	initial:InventoryClosingDetailProduct.initial,
					    	closing:InventoryClosingDetailProduct.closing
						});
					    
					    tbody+='</tr>';
					});
				}
				InventoryClosingHeader.inventoryClosingDetailProducts=inventoryClosingDetailProducts;
				$("#tbodyInventory").append(tbody);
				
			},
			error : function(xhr, error) {
				$("#btnFilter").html('Load');
				$("#btnFilter").prop('disabled', false);
			}
		});
	}
	

	function closeInventory() {
		if (confirm("Are you sure!")) {
			$("#divReport").css('display','none');
			$("#divPreLoader").css('display','block');
		} else {
			return;
		}
		
		$.ajax({
            url: inventoryClosingReportContextPath + "/close-inventory-report",
            type: 'post',
            contentType: "application/json; charset=utf-8",
            data : JSON.stringify(InventoryClosingHeader),
            success: function (data) {
            	console.log("success");
            },
            error : function(xhr, error) {
            	if(xhr.status == "200"){
            		$("#trInventory").html("");
    				$("#tbodyInventory").html("");
            	} else {
    				console.log("Error => " + JSON.stringify(error));
            	}
            	$("#dbEmployee").val("no");
				$("#divPreLoader").css('display','none');
				$("#divReport").css('display','block');
            }
        });
	}
		
})();