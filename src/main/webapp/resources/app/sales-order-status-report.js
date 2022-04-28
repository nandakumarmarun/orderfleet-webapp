// Create a InventoryVoucher object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.InventoryVoucher) {
	this.InventoryVoucher = {};
}

(function() {
	'use strict';

	var inventoryVoucherContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var inventoryVoucherHeaderPid = "";
	var inventoryPidsPdf = "";

	$(document)
			.ready(
					function() {

						$('.selectpicker').selectpicker();

						$("#txtToDate").datepicker({
							dateFormat : "dd-mm-yy"
						});
						$("#txtFromDate").datepicker({
							dateFormat : "dd-mm-yy"
						});
						// load today data
						// InventoryVoucher.filter();

						$('#selectAll').on('click', function() {
							selectAllInventoryVoucher(this);
						});
						$('#downloadXls').on('click', function() {
							InventoryVoucher.downloadXls();
						});
						if ($("#dbDocumentType").val != "no") {
							loadAllDocumentByDocumentType();
						}
						$("#dbDocumentType").change(function() {
							loadAllDocumentByDocumentType();
						});

						$('#packingSlipByCustomer').on('click', function() {
							packingSlipByCustomer();
						});

						$("#btnApply").on('click', function() {
							InventoryVoucher.filter();
						});


						$('input:checkbox.allcheckbox')
								.click(
										function() {
											$(this)
													.closest('table')
													.find(
															'tbody tr td input[type="checkbox"]:visible')
													.prop(
															'checked',
															$(this).prop(
																	'checked'));
										});

					});


	function addProducts() {
		$(".error-msg").html("");
		var selectedProducts = "";
		var selectedinventoryVoucherHeaderPid = inventoryVoucherHeaderPid;
		$.each($("input[name='productProfile']:checked"), function() {
			selectedProducts += $(this).val() + ",";
		});
		console.log(selectedProducts);
		if (selectedProducts == "") {
			$(".error-msg").html("Please select Products");
			return;
		}

		console.log(selectedinventoryVoucherHeaderPid);
		$.ajax({
			url : inventoryVoucherContextPath + "/addNewProduct",
			type : "POST",
			data : {
				assignedproducts : selectedProducts,
				inventoryHeaderPid : selectedinventoryVoucherHeaderPid
			},
			success : function(status) {
				$("#productModel").modal("hide");
				// onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function loadAllDocumentByDocumentType() {
		if ($('#dbDocumentType').val() == "no") {
			$("#dbDocument").html("<option>All</option>");
			alert("Please Select Document Type");
			return;
		}
		var documentType = $('#dbDocumentType').val();
		$("#dbDocument").html("<option>Documents loading...</option>")
		$.ajax({
			url : inventoryVoucherContextPath + "/load-document",
			type : 'GET',
			data : {
				voucherType : documentType,
			},
			success : function(documents) {
				$("#dbDocument").html("<option value='no'>All</option>")
				$.each(documents, function(key, document) {
					$("#dbDocument").append(
							"<option value='" + document.pid + "'>"
									+ document.name + "</option>");
				});

				InventoryVoucher.filter();

			}
		});
	}


	InventoryVoucher.searchByName = function(inputvalue) {
		var input = "";
		console.log($("#search").val());
		$.ajax({
			url : inventoryVoucherContextPath + "/searchByName",
			method : 'GET',
			data : {
				input : inputvalue
			},

			success : function(ProdctProfiles) {
				addTableBodyvalues(ProdctProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	InventoryVoucher.updateInventory = function(ivhPid, ivdId) {
		$.ajax({
			url : inventoryVoucherContextPath + "/updateInventory",
			data : {
				id : ivdId,
				ivhPid : ivhPid,
				quantity : $('#updatedQty-' + ivdId).val(),
				sellingrate : $('#sellingRate-' + ivdId).val()
			},
			method : 'GET',
			success : function(data) {
				showInventoryVoucher(ivhPid);
				InventoryVoucher.filter();
			}

		});
	}

	InventoryVoucher.filter = function() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		if ($("#dbDocumentType").val() == "no") {
			alert("Please Select Document Type")
			return;
		}

		let docPids = $('#dbDocument').val();
		let empPids = $('#dbEmployee').val();
		if ("no" == docPids) {
			docPids = $('#dbDocument option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		if ("no" == empPids) {
			empPids = $('#dbEmployee option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		$('#tBodyInventoryVoucher').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		/*
		 * console.log($("#dbStatus").val() + "----" + empPids + "----" +
		 * $("#dbAccount").val() + "---" + $("#dbDateSearch").val() + "---" +
		 * $("#txtFromDate").val() + "---" + $("#txtToDate").val() + "---" +
		 * $("#dbDocumentType").val() + "---" + docPids);
		 */
		$
				.ajax({
					url : inventoryVoucherContextPath + "/filter",
					type : 'GET',
					data : {
						tallyDownloadStatus : $("#dbStatus").val(),
						salesOrderStatus : $("#salesStatus").val(),
						employeePids : empPids,
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						voucherType : $("#dbDocumentType").val(),
						documentPids : docPids,
					},
					success : function(inventoryVouchers) {
						$("#lblCounts").text("0");
						$('#tBodyInventoryVoucher').html("");
						if (inventoryVouchers.length == 0) {
							$('#tBodyInventoryVoucher')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var counts = 0;
						let totAmount = 0;
						let totVolume = 0;
						inventoryPidsPdf = "";
						var deliveryDate = ""
						$
								.each(
										inventoryVouchers,
										function(index, inventoryVoucher) {
											var locationName = inventoryVoucher.location;
										
											counts += 1;
											
											if (inventoryVoucher.location == "No Location"
												&& inventoryVoucher.accountProfileLatitude != 0) {
											locationName = "<span class='btn btn-success'  id='"
													+ inventoryVoucher.exicutiveTaskExecutionPid
													+ "' onClick='InventoryVoucher.getLocation(this)' >get location</span>";
										}
											

											$('#tBodyInventoryVoucher')
													.append(
															"<tr><td>"
																	+ inventoryVoucher.documentNumberLocal
																	+ "</td><td>"
																	+ convertDateTimeFromServer(inventoryVoucher.serverDate)
																	+ "</td><td>"
																	+ inventoryVoucher.employeeName
																	+ "</td><td>"
																	+ inventoryVoucher.receiverAccountName
																	+ "</td><td>"
																	+inventoryVoucher.district
																	+ "</td><td>"
																	+ inventoryVoucher.receiverAccountLocation
																	+ "</td><td>"																	
																	+inventoryVoucher.territory
																	+ "</td><td>"
																	+deliveryDate
																	+ "</td><td>"
																	+locationName
																	+ "</td><td>"
																	+spanSalesOrderStatus(
																			inventoryVoucher.pid,
																			inventoryVoucher.salesOrderStatus,
																			inventoryVoucher.tallyDownloadStatus
																			)
																	+ "</td><td>"
																	+ (inventoryVoucher.updatedStatus ? inventoryVoucher.documentTotalUpdated
																			: inventoryVoucher.documentTotal)
																	+ "</td></tr>");
										});
						$("#lblCounts").text(counts);
						if (totAmount != 0) {
							$("#totalDocument").html(
									"(" + totAmount.toFixed(2) + ")");
						}
						if (totVolume != 0) {
							$("#totalVolume").html(
									"(" + totVolume.toFixed(2) + ")");
						}
					}
				});
	}

	InventoryVoucher.getLocation = function(obj) {
		var pid = $(obj).attr("id");
		$(obj).html("loading...");
		$.ajax({
			url : inventoryVoucherContextPath + "/updateLocation/" + pid,
			method : 'GET',
			success : function(data) {
				$(obj).html(data.location);
				$(obj).removeClass("btn-success");
				$(obj).removeClass("btn");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	

	

	function spanStatus(inventoryVoucherPid, status, managementStatus) {

		var pending = "'" + 'PENDING' + "'";
		var processing = "'" + 'PROCESSING' + "'";
		var completed = "'" + 'COMPLETED' + "'";
		var spanStatus = "";
		var pid = "'" + inventoryVoucherPid + "'";

		var pointerEnable = '';

		var pointerDisable = '';

		switch (status) {
		case 'PENDING':
			pointerEnable = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer" >';
			pointerDisable = '<div class="dropdown"><span class="label label-default dropdown-toggle" >';

			spanStatus = (managementStatus == "APPROVE" ? pointerEnable
					: pointerDisable)
					+ 'PENDING <span></span></span></div>';
			// <span class="'
			// + (managementStatus == "APPROVE" ? 'caret' : '')
			// + '"></span></span>'
			// + '<ul class="dropdown-menu">'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + processing
			// + ')" style="cursor: pointer;"><a>PROCESSING</a></li>'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + completed
			// + ')" style="cursor: pointer;"><a>COMPLETED</a></li>'
			// + '</ul></div>';

			break;
		case 'PROCESSING':
			pointerEnable = '<div class="dropdown"><span class="label label-warning dropdown-toggle" data-toggle="dropdown" style="cursor: pointer" >';
			pointerDisable = '<div class="dropdown"><span class="label label-warning dropdown-toggle" >';

			spanStatus = (managementStatus == "APPROVE" ? pointerEnable
					: pointerDisable)
					+ 'PROCESSING <span></span></span></div>';
			// <span class="'
			// + (managementStatus == "APPROVE" ? 'caret' : '')
			// + '"></span></span>'
			// + '<ul class="dropdown-menu">'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + pending
			// + ')" style="cursor: pointer;"><a>PENDING</a></li>'
			// + '<li onclick="InventoryVoucher.setStatus('
			// + pid
			// + ','
			// + completed
			// + ')" style="cursor: pointer;"><a>COMPLETED</a></li>'
			// + '</ul></div>';

			break;
		case 'COMPLETED':

			pointerEnable = '<div class="dropdown"><span class="label label-success dropdown-toggle" data-toggle="dropdown" style="cursor: pointer" >';
			pointerDisable = '<div class="dropdown"><span class="label label-success dropdown-toggle" >';
			spanStatus = (managementStatus == "APPROVE" ? pointerEnable
					: pointerDisable)
					+ 'COMPLETED <span></span></span></div>';
			break;
		}

		/*
		 * if (status) { spanStatus = '<span class="label label-success"
		 * style="cursor: pointer;">Processed</span>'; } else { spanStatus = '<span
		 * class="label label-default" onclick="InventoryVoucher.setStatus(' +
		 * pid + ',' + !status + ')" style="cursor: pointer;">Pending</span>'; }
		 */
		return spanStatus;
	}

	function spanSalesOrderStatus(inventoryVoucherPid, status, tallyStatus) {
		var created = "'" +'CREATED'+ "'";
		var canceld = "'" +'CANCELED'+"'";
		var deliverd = "'" +'DELIVERD'+"'";
		var confirmed = "'" +'CONFIRM'+"'";
		var salesOrderStatus = "";
		var pid = "'" + inventoryVoucherPid + "'";
		switch (status) {
		case 'CREATED':
			salesOrderStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
				+ 'CREATED<span class="caret"></span></span>'
				+ '<ul class="dropdown-menu">'
					+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
					+ pid
					+ ','
					+ canceld
					+ ')" style="cursor: pointer;"><a>CANCELD</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
					+ pid
					+ ','
					+ confirmed
					+ ')" style="cursor: pointer;"><a>CONFIRMED</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
					+ pid
					+ ','
					+ deliverd 
					+ ')" style="cursor: pointer;"><a>DELIVERD</a></li>'
					+ '</ul></div>';
			break;
			case 'DELIVERD':
			salesOrderStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
				+ 'DELIVERD<span class="caret"></span></span>'
				+ '<ul class="dropdown-menu">'
					+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
					+ pid
					+ ','
					+ canceld
					+ ')" style="cursor: pointer;"><a>CANCELD</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
					+ pid
					+ ','
					+ confirmed
					+ ')" style="cursor: pointer;"><a>CONFIRMED</a></li>'
					+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
					+ pid
					+ ','
					+ created 
					+ ')" style="cursor: pointer;"><a>DELIVERD</a></li>'
					+ '</ul></div>';
			break;
			case 'CANCELED':
				salesOrderStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
					+ 'CANCELED<span class="caret"></span></span>'
					+ '<ul class="dropdown-menu">'
						+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
						+ pid
						+ ','
						+ created
						+ ')" style="cursor: pointer;"><a>CANCELD</a></li>'
						+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
						+ pid
						+ ','
						+ confirmed
						+ ')" style="cursor: pointer;"><a>CONFIRMED</a></li>'
						+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
						+ pid
						+ ','
						+ deliverd 
						+ ')" style="cursor: pointer;"><a>DELIVERD</a></li>'
						+ '</ul></div>';
				break;
			case 'CONFIRM':
				if (tallyStatus == 'COMPLETED') {
					salesOrderStatus = '<div ><span class="label label-success dropdown-toggle" data-toggle="dropdown" >'
							+ 'CONFIRME <span></span></span></div>';
				}else{
					salesOrderStatus = '<div class="dropdown"><span class="label label-default dropdown-toggle" data-toggle="dropdown" style="cursor: pointer;">'
						+ 'CONFIRM<span class="caret"></span></span>'
						+ '<ul class="dropdown-menu">'
							+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
							+ pid
							+ ','
							+ canceld
							+ ')" style="cursor: pointer;"><a>CANCELD</a></li>'
							+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
							+ pid
							+ ','
							+ created
							+ ')" style="cursor: pointer;"><a>CREATED</a></li>'
							+ '<li onclick="InventoryVoucher.setSalesOrderStatus('
							+ pid
							+ ','
							+ deliverd 
							+ ')" style="cursor: pointer;"><a>DELIVERD</a></li>'
							+ '</ul></div>';
				}
				break;
		}
		
		return salesOrderStatus;
	}


	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = inventoryVoucherContextPath;
	}

	

	
	InventoryVoucher.setSalesOrderStatus = function(pid,
			salesOrderStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : inventoryVoucherContextPath
						+ "/changeSalesOrderStatus",
				method : 'GET',
				data : {
					pid : pid,
					salesOrderStatus : salesOrderStatus
				},
				success : function(data) {
					console.log(data);
					if (data == 'failed') {
						alert("Cannot update a downloaded order");
					}
					InventoryVoucher.filter();
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}
	
	
	InventoryVoucher.setSalesManagementStatus = function(pid,
			salesManagementStatus) {

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : inventoryVoucherContextPath
						+ "/changeSalesManagementStatus",
				method : 'GET',
				data : {
					pid : pid,
					salesManagementStatus : salesManagementStatus
				},
				success : function(data) {
					console.log(data);
					if (data == 'failed') {
						alert("Cannot update a downloaded order");
					}
					InventoryVoucher.filter();
					// onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	InventoryVoucher.showDatePicker = function() {
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

	function selectAllInventoryVoucher(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}
	
	InventoryVoucher.downloadXls = function() {
		var excelName = "acvts_txns_"+ new Date().toISOString().replace(/[\-\:\.]/g, "");
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('inventoryVoucherTable'),excelName);
	}

	InventoryVoucher.showModalPopup = function(el, pid, action) {
		if (pid) {
			switch (action) {
			case 0:
				showInventoryVoucher(pid);
				break;
			case 1:
				inventoryVoucherHeaderPid = pid;
				console.log(pid);
				console.log(inventoryVoucherHeaderPid);
				break;
			}

		}
		el.modal('show');
	}

	InventoryVoucher.closeModalPopup = function(el) {
		el.modal('hide');
		$('#search').find("input[type=text], textarea").val("");
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
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