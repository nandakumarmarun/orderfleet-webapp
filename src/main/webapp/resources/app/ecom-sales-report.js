if (!this.EcomSalesReport) {
	this.EcomSalesReport = {};
}

(function() {
	'use strict';

	var ecomSalesReportContextPath = location.protocol + '//'
	+ location.host + location.pathname;

	var dashboardEmployees=[];
	var otherEmployees=[];
	var allEmployeesMap = new Object();
   // Object for reject acccount profile
	var object = null;
	var ecomSalesReportPid = null;
	
	EcomSalesReport.downloadXls=function() {
		$("#tblEcomSalesReport th:nth-last-child(2), #tblEcomSalesReport td:nth-last-child(2)").hide();
		var excelName = "activities\transactions";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblEcomSalesReport'),excelName);
		 $("#tblEcomSalesReport th:nth-last-child(2), #tblEcomSalesReport td:nth-last-child(2)").show();
	}
	
	EcomSalesReport.loadAccountProfileByEmployee= function(){
		$("#dbAccount").html('<option>Loading...</option>');
		if($("#dbEmployee").val()=="Dashboard Employees" || $("#dbEmployee").val()=="no"){
			$("#dbAccount").html('<option value="no">All Account</option>');
			return;
		}
		
		$
		.ajax({
			url : ecomSalesReportContextPath + "/loadAccountProfile",
			type : 'GET',
			data : {
				employeePid : $('#dbEmployee').val(),
			},
			success : function(accounts) {
				$("#dbAccount").html('<option value="no">All Account</option>');
				if (accounts != null && accounts.length > 0) {
					$.each(accounts, function(index, account) {
						$("#dbAccount").append(
								'<option value="' + account.pid + '">' + account.name
										+ '</option>');
					});
				}
			},
		});
		
	}

	EcomSalesReport.filter = function() {
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		
		
		$('#tBodyEcomSalesReport').html(
			"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : ecomSalesReportContextPath + "/filter",
				type : 'GET',
				data : {
					employeePid : $('#dbEmployee').val(),
					accountPid : $("#dbAccount").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val()
				},
				success : function(ecomSalesReports) {
					$('#tBodyEcomSalesReport').html("");

					$("#lblTransactionAmount").text("0");
					$("#lblTransactionVolume").text("0");
					if (ecomSalesReports.length == 0) {
						$('#tBodyEcomSalesReport')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
						return;
					}
					var transactionAmount = 0;
					var transactionVolume = 0;
					$
						.each(
							ecomSalesReports,
							function(index, ecomSalesReport) {
								
								var locationName=ecomSalesReport.location;
								
								if(ecomSalesReport.locationType=="GpsLocation" && ecomSalesReport.latitude==0 || ecomSalesReport.locationType=="TowerLocation" && ecomSalesReport.latitude==0 || ecomSalesReport.location=="Unable to find location"){
									locationName="<span class='btn btn-success'  id='"+ecomSalesReport.pid+"' onClick='EcomSalesReport.getLocation(this)' >get location</span>";
								}
								var locationIcon = locationIcons(ecomSalesReport);
								var activityStatusTd = "<td>"
									+ ecomSalesReport.activityStatus
									+ "</td>";
								if (ecomSalesReport.activityStatus == 'ACCEPTED') {
									activityStatusTd = "<td><font color='green'>"
										+ ecomSalesReport.activityStatus
										+ "</td>";
								} else if (ecomSalesReport.activityStatus == 'REJECTED') {
									activityStatusTd = "<td id='rejectReason' title='" + ecomSalesReport.rejectReasonRemark + "'><font color='red'>" + ecomSalesReport.activityStatus + ""
										+ "</td>";
								}
								$('#tBodyEcomSalesReport')
									.append(
										"<tr data-id='"
										+ ecomSalesReport.pid
										+ "' data-parent=\"\"><td>"
										+ ecomSalesReport.accountProfileName
										+ "</td><td>"
										+ convertDateTimeFromServer(ecomSalesReport.createdDate)
										+ "</td><td>"
										+ convertDateTimeFromServer(ecomSalesReport.plannedDate)
										+ "</td><td><label>GPS Location : </label> "
										+ locationName
										+ locationIcon + "<br><label>Customer Location : </label> "
										+ (ecomSalesReport.accountProfileLocation == null ? "" : ecomSalesReport.accountProfileLocation)
										+ "</td>"
										+ activityStatusTd
										+ "<td><button type='button' title='accept' class='btn btn-green entypo-thumbs-up' aria-label='entypo-thumbs-up' onclick='EcomSalesReport.accept(\""
										+ ecomSalesReport.pid
										+ "\",this);'></button>&nbsp;&nbsp;<button type='button' title='reject' class='btn btn-red entypo-thumbs-down' aria-label='entypo-thumbs-down' onclick='EcomSalesReport.showModal(\"" + ecomSalesReport.pid + "\",this);'></button></td><td>"
										+ (ecomSalesReport.remarks == null ? "" : ecomSalesReport.remarks)
										+ "</td></tr>");

								$
									.each(
										ecomSalesReport.executiveTaskExecutionDetailViews,
										function(index,
											executionDetail) {
											if (executionDetail.documentType == "INVENTORY_VOUCHER") {
												transactionAmount += executionDetail.documentTotal;
												transactionVolume += executionDetail.documentVolume;
											} 
											var dynamicvalue = "";
											if (executionDetail.documentType != "DYNAMIC_DOCUMENT") {
												$(
													'#tBodyEcomSalesReport')
													.append(
														"<tr style='background: rgba(225, 225, 225, 0.66);' data-id='"
														+ executionDetail.pid
														+ "1' data-parent='"
														+ ecomSalesReport.pid
														+ "'><td>&nbsp;</td><td colspan='3'>"
														+ executionDetail.documentName
														+ "</td><td>"
														+ executionDetail.documentTotal
														+ "</td><td><button type='button' class='btn btn-white' onclick='EcomSalesReport.viewDetails(\""
														+ executionDetail.pid
														+ "\",\""
														+ executionDetail.documentType
														+ "\");'>View Details</button></td></tr>");
											} 
										});
							});

					$("#lblTransactionAmount").text(
						transactionAmount.toFixed(2));
					$("#lblTransactionVolume").text(transactionVolume);

					$('.collaptable')
						.aCollapTable(
							{
								startCollapsed : true,
								addColumn : false,
								plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
								minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
							});
				}
			});
	}
	
	
	 EcomSalesReport.getLocation=function(obj){
		 var pid= $(obj).attr("id");
		 $(obj).html("loading...");
		$.ajax({
			url : ecomSalesReportContextPath+"/updateLocation/" + pid,
			method : 'GET',
			success : function(data) {
			console.log(data.location);
			$(obj).html(data.location);
			$(obj).removeClass("btn-success");
			$(obj).removeClass("btn");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		
	}
	
	function locationIcons(userData) {

		var images = "";
		if (userData.locationType != null) {

			if (userData.locationType == "GpsLocation") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/location.png" width="20px">';
			} else if (userData.locationType == "TowerLocation") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/tower.png" width="20px">';
			} else if (userData.locationType == "FlightMode") {
				images = '<img src="'
						+ contextPath
						+ '/resources/assets/images/map/flight.png" width="20px">';
			}
			// check gsp is Off
			if (userData.isGpsOff && userData.locationType != "GpsLocation") {
				images += ' <img src="'
						+ contextPath
						+ '/resources/assets/images/map/gps-off.png" width="20px">';
			}
			// check Mobile Data is Off
			if (userData.isMobileDataOff) {
				images += ' <img src="'
						+ contextPath
						+ '/resources/assets/images/map/mobile-data-off.png" width="20px">';
			}

		}
		return images;
	}

	EcomSalesReport.accept = function(pid, obj) {
		$.ajax({
			url : location.protocol + '//' + location.host
			 + "/web/verification/accept/" + pid,
			method : 'PUT',
			success : function(status) {
				if (status) {
					$(object).closest('tr').find('td').eq(6).prop('title', "");
					$(obj).closest('tr').find('td').eq(6).text("ACCEPTED");
					$(obj).closest('tr').find('td').eq(6).css({
						"color" : "green"
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	EcomSalesReport.reject = function() {
		var reasons=$("#field_reason").val();
		
		if(reasons==null){
			$("#reasonAlert").css('display', 'block');
			return;
		}
		$.ajax({
			url : location.protocol + '//' + location.host
			 + "/web/verification/reject",
			method : 'PUT',
			data:{
				pid:ecomSalesReportPid,
				reason:reasons,
			},
			success : function(status) {				
				if (status) {
					$(object).closest('tr').find('td').eq(6).prop('title', reasons);
					$(object).closest('tr').find('td').eq(6).text("REJECTED");
					$(object).closest('tr').find('td').eq(6).css({
						"color" : "red"
					});
				}
				$("#myModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	EcomSalesReport.showModal = function(pid, obj) {
		object = obj;
		ecomSalesReportPid = pid;
		$("#field_reason").val("") ;
		$("#myModal").modal("show");

	}

	EcomSalesReport.viewDetails = function(pid, documentType) {
		if (documentType == "INVENTORY_VOUCHER") {
			showInventoryVoucher(pid);
		} 
	}

	

	function showInventoryVoucher(pid) {
		var employeePid = $("#dbEmployee").val();
		$
			.ajax({
				url : contextPath + "/web/ecom-sales-reports/inventory-voucher/" + pid + "/"+ employeePid,
				method : 'GET',
				success : function(data) {
					$('#lbl_documentNumberIc').text(
						data.documentNumberLocal);
					$('#lbl_userIc').text(data.userName);
					$('#lbl_documentIc').text(data.documentName);
					$('#lbl_documentDateIc').text(
						convertDateTimeFromServer(data.createdDate));
					$('#lbl_receiverIc').text(data.receiverAccountName);
					$('#lbl_supplierIc').text(data.supplierAccountName);
					$('#lbl_documentDiscountAmountIc').text(
						data.docDiscountAmount);
					$('#lbl_documentDiscountPercentageIc').text(
						data.docDiscountPercentage);

					$('#tblVoucherDetailsIc').html("");
					var totalDocument=0;
					$
						.each(
							data.inventoryVoucherDetails,
							function(index, voucherDetail) {
								totalDocument=totalDocument+voucherDetail.rowTotal;
								$('#tblVoucherDetailsIc')
									.append(
										"<tr data-id='"
										+ index
										+ "'><td>"
										+ voucherDetail.productName
										+ "</td><td>"
										+ voucherDetail.quantity
										+ "</td><td>"
										+ voucherDetail.freeQuantity
										+ "</td><td>"
										+ voucherDetail.sellingRate
										+ "</td><td>"
										+ voucherDetail.taxPercentage
										+ "</td><td>"
										+ voucherDetail.discountPercentage
										+ "</td><td>"
										+ voucherDetail.discountAmount
										+ "</td><td>"
										+ voucherDetail.rowTotal
										+ "</td></tr>");
								$
									.each(
										voucherDetail.inventoryVoucherBatchDetails,
										function(index1,
											inventoryVoucherBatch) {
											$(
												'#tblVoucherDetailsIc')
												.append(
													"<tr style='background: rgba(225, 225, 225, 0.66);' data-parent='"
													+ index
													+ "' >"
													+ "<td colspan='2'>Batch Number : "
													+ inventoryVoucherBatch.batchNumber
													+ "</td><td colspan='3'>Stock Location : "
													+ inventoryVoucherBatch.stockLocationName
													+ "</td><td colspan='2'>Quantity : "
													+ inventoryVoucherBatch.quantity
													+ "</td></tr>");
										});
							});
					$('#lbl_documentTotalIc').text(totalDocument);
//					$('#divVoucherDetailsInventory .collaptable')
//						.aCollapTable(
//							{
//								startCollapsed : true,
//								addColumn : false,
//								plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
//								minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
//							});
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		$('#viewModalInventoryVoucher').modal('show');
	}





	EcomSalesReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}

	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}

	
	EcomSalesReport.closeModalPopup = function(el) {
		el.modal('hide');
	}

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