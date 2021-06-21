if (!this.TerritoryWiseSales) {
	this.TerritoryWiseSales = {};
}

(function() {
	'use strict';

	var territoryWiseAccountContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		
		
		
		loadAllLevels();
		
		onChangeFunctions();
		
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		
		// TerritoryWiseSales.filter()
		
		$('#btnDownload').on('click', function() {
			var tblTerritoryWiseAccounts = $("#tblSalesReport tbody");
			
			if (tblTerritoryWiseAccounts
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblTerritoryWiseAccounts[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
			
		});
		
		$('#btnApply').on('click', function() {

			TerritoryWiseSales.filter()
			
		});
		
	});
	
	function downloadXls() {
		// Avoid last column in each row
		
		var excelName = "territoryWiseSales";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblSalesReport'),excelName);
		 
	}
	
	TerritoryWiseSales.filter= function(){
		
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadNoOfLevels",
			type : "GET",
			success : function(loadNoOfLevels) {
				TerritoryWiseSales.getSalesReport(loadNoOfLevels);
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
	}
	
	TerritoryWiseSales.getSalesReport= function(loadNoOfLevels) {
		
		let documentPids = $('#dbDocument').val();
		if ("no" == documentPids) {
			documentPids = $('#dbDocument option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		
		let employeePids = $('#dbEmployee').val();
		if ("no" == employeePids) {
			employeePids = $('#dbEmployee option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		
		
		let parentIds = $('#dbLevel'+loadNoOfLevels.length).val();
		if ("-1" == parentIds) {
			parentIds = $('#dbLevel'+loadNoOfLevels.length+' option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		
		
// if ("-1" == accountPids) {
// accountPids = $('#dbAccount option').map(function() {
// return $(this).val();
// }).get().join(',');
// }
		
		
		
		
		$('#tBodySalesReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
	    console.log($("#dbDocument").val())
		$
				.ajax({
					url : territoryWiseAccountContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : employeePids,
						locationIds : parentIds,
						documentPid : documentPids,
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						noOfLevels : loadNoOfLevels.length
					},
					
					success : function(sales) {
						
						var levelTh="";
						
						
						
						var i;
						for(i=loadNoOfLevels.length;i>0;i--){
							levelTh += "<th>Level_"+i+"</th>";
						}
						
// $('#volumeAmountLabel').html("<label>Count : </label> <label
// id='lblCounts'>0</label> &nbsp;&nbsp;&nbsp;" +
// "<label>Volume : </label> <label id='lblVolume'>0</label> &nbsp;&nbsp;&nbsp;"
// +
// "<label>Amount : </label> <label id='lblAmount'>0</label>");
						
						var colspan= 8 + loadNoOfLevels.length;
						$('#tHeadSalesReport').html("<tr><th style='min-width: 80px;'>Employee</th><th>Account Profile</th><th>Document</th><th>Date</th><th>Item</th><th>Quantity</th><th>Unit Qty</th><th>Volume</th><th>Amount</th>"+levelTh+"");
						$('#tBodySalesReport').html("");
						if (sales.length == 0) {
							$('#tBodySalesReport')
								.html(
									"<tr><td colspan='"+colspan+"' align='center'>No data available</td></tr>");
							return;
						}
						var totalSales = 0;
						var count=sales.length;
						var totalVolume=0;
						var totalAmount=0;
						$.each(sales,function(index, sale){
							var unitQty = 1;
							if(sale.productUnitQty){
								unitQty = sale.productUnitQty;
							}
							
							var volume = Math.round((sale.volume) * 100) / 100;
							totalVolume += volume;
							
							var amount = Math.round((sale.rowTotal) * 100) / 100;
							totalAmount += amount;
							
							
							var levelTd="";
							var levelArray="";
							
							for(i=0;i<loadNoOfLevels.length;i++){
								
								if(sale.locations!=null){
								
								levelArray=sale.locations.split("~");
								
								// var location=levelArray[i].replace(" ", "_");
		
								
								 levelTd += "<td>"+levelArray[i].replace(/\s+/g,'_');+"</td>";
								 
								}else{
									 levelTd += "<td>-</td>";
								}
								
							}
							
							$('#tBodySalesReport')
							.append("<tr data-id='"
									+ sale.detailId
									+ "' data-parent=\"\"><td>"
									+ sale.employeeName
									+ "</td><td>" + sale.accountName + "</td>" 
									+ "</td><td>" + sale.documentName + "</td>"
									+ "<td >" + convertDateTimeFromServer(sale.createdDate) + "</td>"
									+ "<td>" + sale.productName + "</td>" 
									+ "<td>" + sale.quantity + "</td>" 
									+ "<td>" + unitQty + "</td>" 
									+ "<td style='width:10px;'>" + volume + "</td>" 
									+ "<td >" + amount + "</td>"
									+ levelTd
									+ "</tr>");
						});
						$("#lblCounts").text(count);
						$("#lblVolume").text(totalVolume.toFixed(2));
						$("#lblAmount").text(totalAmount.toFixed(2));
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	
	function onChangeFunctions(){
		
	
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadNoOfLevels",
			type : "GET",
			success : function(loadNoOfLevels) {
				console.log(loadNoOfLevels);
				
				
				$
				.each(
						loadNoOfLevels,
						function(index, level) {
							
							 $( "#dbLevel"+level).change(function() {
							    changeLevel(level, $( "#dbLevel"+level).val(),loadNoOfLevels.length);
							 });
							
						});
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
	}
	
// function convertDateTimeFromServer(date) {
// if (date) {
// return moment(date).format('MMM DD YYYY');
// } else {
// return "";
// }
// }
	
	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
	
	function changeLevel(level, territoryId,levelLength){
		// alert(level+"----"+territoryId)
		
		let parentIds = $('#dbLevel'+level).val();
		if ("-1" == parentIds) {
			parentIds = $('#dbLevel'+level+' option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		
		
		$(".loader").addClass('show');
		
		
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadChildLevels",
			type : "GET",
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			data : {
				level : level,
				territoryIds : parentIds
			},
			success : function(locationLevels) {
				console.log(locationLevels);
				
				$
				.each(
						locationLevels,
						function(index, locationLevelLevel) {
							
							$("#dbLevel"+locationLevelLevel.level).html("<option value='-1'>All</option>")
							$.each(locationLevelLevel.locationDtos, function(key, loc) {
								$("#dbLevel"+locationLevelLevel.level).append(
										"<option value='" + loc.id + "'>"
												+ loc.name + "</option>");
							});
							
							
							
							if(levelLength==locationLevelLevel.level){
								$("#dbAccount").html("<option value='-1'>All Account</option>")
								$.each(locationLevelLevel.accountProfileDTOs, function(key, acc) {
									$("#dbAccount").append(
											"<option value='" + acc.pid + "'>"
													+ acc.name + "</option>");
								});
								
							}
							
							
						});

				$("#loader").modal('hide');	
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function loadAllLevels(){
		
		
		$(".loader").addClass('show');
		
		
			$.ajax({
				url : territoryWiseAccountContextPath + "/loadAllLevels",
				type : "GET",
				beforeSend : function() {
					// Show image container
					$("#loader").modal('show');

				},
				success : function(locationLevels) {
					console.log(locationLevels);
					
					$
					.each(
							locationLevels,
							function(index, locationLevelLevel) {
								
								$("#dbLevel"+locationLevelLevel.level).html("<option value='-1'>All</option>")
								$.each(locationLevelLevel.locationDtos, function(key, loc) {
									$("#dbLevel"+locationLevelLevel.level).append(
											"<option value='" + loc.id + "'>"
													+ loc.name + "</option>");
								});
								
								
								
								if(locationLevels.length==locationLevelLevel.level){
									$("#dbAccount").html("<option value='-1'>All Account</option>")
									$.each(locationLevelLevel.accountProfileDTOs, function(key, acc) {
										$("#dbAccount").append(
												"<option value='" + acc.pid + "'>"
														+ acc.name + "</option>");
									});
									
								}
								
								
							});

					$("#loader").modal('hide');	
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
			
		 
	}

	TerritoryWiseSales.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

// function downloadXls() {
// // Avoid last column in each row
// $("#tblTerritoryWiseSales th:first-child, #tblTerritoryWiseSales
// td:first-child").hide();
//		
// var excelName = "territoryWiseAccounts";
// var table2excel = new Table2Excel();
// table2excel.export(document.getElementById('tblTerritoryWiseSales'),excelName);
// $("#tblTerritoryWiseSales th:first-child, #tblTerritoryWiseSales
// td:first-child").show();
// }
//	

	

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	
	function onError(httpResponse, exception) {
		var i;
		switch (httpResponse.status) {
		// connection refused, server not reachable
		case 0:
			addErrorAlert('Server not reachable', 'error.server.not.reachable');
			break;
		case 400:
			var errorHeader = httpResponse
					.getResponseHeader('X-orderfleetwebApp-error');
			var entityKey = httpResponse
					.getResponseHeader('X-orderfleetwebApp-params');
			if (errorHeader) {
				var entityName = entityKey;
				addErrorAlert(errorHeader, errorHeader, {
					entityName : entityName
				});
			} else if (httpResponse.responseText) {
				var data = JSON.parse(httpResponse.responseText);
				if (data && data.fieldErrors) {
					for (i = 0; i < data.fieldErrors.length; i++) {
						var fieldError = data.fieldErrors[i];
						var convertedField = fieldError.field.replace(
								/\[\d*\]/g, '[]');
						var fieldName = convertedField.charAt(0).toUpperCase()
								+ convertedField.slice(1);
						addErrorAlert(
								'Field ' + fieldName + ' cannot be empty',
								'error.' + fieldError.message, {
									fieldName : fieldName
								});
					}
				} else if (data && data.message) {
					addErrorAlert(data.message, data.message, data);
				} else {
					addErrorAlert(data);
				}
			} else {
				addErrorAlert(exception);
			}
			break;
		default:
			if (httpResponse.responseText) {
				var data = JSON.parse(httpResponse.responseText);
				if (data && data.description) {
					addErrorAlert(data.description);
				} else if (data && data.message) {
					addErrorAlert(data.message);
				} else {
					addErrorAlert(data);
				}
			} else {
				addErrorAlert(exception);
			}
		}
	}
})();