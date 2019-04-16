if (!this.SetOrderStatusInventory) {
	this.SetOrderStatusInventory = {};
}

(function() {
	'use strict';

	var setOrderStatusContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function(e) {
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		SetOrderStatusInventory.filter();
	});
	var orderStatuses;
	SetOrderStatusInventory.filter = function() {
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

		$('#tBodySetOrderStatus').html(
				"<tr><td colspan='5' align='center'>Please wait...</td></tr>");

//		$.ajax({
//			url : setOrderStatusContextPath + "/getOrderStatus",
//			method : 'GET',
//			success : function(data) {
//				orderStatuses = data;
//			},
//			error : function(xhr, error) {
//				onError(xhr, error);
//			}
//		});

		$
				.ajax({
					url : setOrderStatusContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $("#dbEmployee").val(),
						statusId : $("#dbStatus").val(),
						accountPid : $("#dbAccount").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val()
					},
					success : function(setOrderStatuss) {
						$('#tBodySetOrderStatus').html("");
						if (setOrderStatuss.length == 0) {
							$('#tBodySetOrderStatus')
									.html(
											"<tr><td colspan='5' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										setOrderStatuss,
										function(index, setOrderStatus) {

											$('#tBodySetOrderStatus')
													.append(
															"<tr><td>"
																	+ convertDateTimeFromServer(setOrderStatus.documentDate)
																	+ "</td><td>"
																	+ setOrderStatus.employeeName
																	+ "</td><td>"
																	+ setOrderStatus.receiverAccountName
																	+ "</td><td>"
																	+ setOrderStatus.documentTotal
																	+ "</td><td>"
																	+ (setOrderStatus.orderStatusName == null ? "" : setOrderStatus.orderStatusName )
																	+ "</td></tr>");

										});

					}
				});
	}

	function statusDiv(setOrderStatus) {
		var status = setOrderStatus.orderStatusName;
		var pid = setOrderStatus.pid;

		if (status == null) {
			status = "Set Status"
		}

		var div = " <div class='btn-group'> <span data-toggle='dropdown' aria-haspopup='true' id='btn_"
				+ pid
				+ "' aria-expanded='false' class='"
				+ "btn dropdown-toggle btn-success"
				+ "'  onclick='' style='cursor: pointer;'>"
				+ status
				+ "</span><div class='dropdown-menu dropdown-menu-right' style='background-color: #F0F0F0'>";

		if (orderStatuses.length == 0) {
			div += " <div> <a class='btn btn-default dropdown-item ' style='width: 100%; text-align: left;'>No Order Status Present</a> </div> </div>";
			div += " </div>";

			return div;
		}
		$
				.each(
						orderStatuses,
						function(index, status) {
							console.log(status);
							div += " <div> <a class='btn btn-default dropdown-item ' style='width: 100%; text-align: left;' onclick='SetOrderStatusInventory.changeStatus(\""
									+ pid
									+ "\",this);'>"
									+ status.name
									+ "</a> </div> ";
						});
	

		div += "</div> </div>";
		console.log(div)
		return div;
	}

	SetOrderStatusInventory.changeStatus = function(pid, obj) {
		var name = $("#btn_" + pid).text();
		if(name!="Set Status"){
		if (name == $(obj).text()) {
			console.log("same");
			return;
		}}
		console.log($(obj).text());
		console.log(name);
		if (confirm("are you confirm?")) {

			$.ajax({
				url : setOrderStatusContextPath + "/changeOrderStatus",
				method : 'POST',
				data : {
					inventoryPid : pid,
					status : $(obj).text()
				},
				success : function(data) {
//					onSaveSuccess(data);
					SetOrderStatusInventory.filter();
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}

	SetOrderStatusInventory.showDatePicker = function() {
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
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = setOrderStatusContextPath;
	}

	SetOrderStatusInventory.closeModalPopup = function(el) {
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