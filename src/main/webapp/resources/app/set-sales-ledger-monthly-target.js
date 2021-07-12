// Create a SetMonthlySalesLedgerWiseTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SetMonthlySalesLedgerWiseTarget) {
	this.SetMonthlySalesLedgerWiseTarget = {};

}

(function() {
	'use strict';

	var setSalesLedgerWiseMontlyTargetContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document)
			.ready(
					function() {

						$('#btnApplyMonthlySalesLedger').click(
								function() {
									SetMonthlySalesLedgerWiseTarget
											.loadUserMonthlySales();
								});

						$('input[type=month]').MonthPicker().css(
								'backgroundColor', 'lightyellow');

						$("#txtMonth")
								.MonthPicker(
										{
											Button : '<div class="input-group-addon"><a href="#"><i class="entypo-calendar"></i></a></div>'
										});
						// table search
						$('#btnSearch').click(function() {
							searchTable($("#search").val());
						});
					});

	SetMonthlySalesLedgerWiseTarget.loadUserMonthlySales = function() {

		$('#tblSetTarget').html("<tr><td colspan='4'>Please wait...</td></tr>");
		$.ajax({
			url : setSalesLedgerWiseMontlyTargetContextPath
					+ "/monthly-sales-ledger-wise-targets",
			type : 'GET',
			data : {
				monthAndYear : $('#txtMonth').val(),
			},
			success : function(productGroupTargets) {
				addRowsToTable(productGroupTargets);
			}
		});
	}

	var trTarget;
	SetMonthlySalesLedgerWiseTarget.editSalesLedgerWiseTarget = function(
			salesLedgerPid, salesLedgerWiseTargetPid, obj) {
		$('.error-msg').html('');
		trTarget = obj;

		$('#hdnSalesLedgerPid').val(salesLedgerPid);

		$('#hdnSalesLedgerWiseTargetPid').val(salesLedgerWiseTargetPid);

		var Amount = $(obj).closest("tr").find("td").eq(1).text();
		$('#txtAmount').val(Amount)
		$("#modalSetTarget").modal("show");
	}

	SetMonthlySalesLedgerWiseTarget.saveTarget = function() {
		$("#saveTarget").prop('disabled', true);
		$(".error-msg").html("");
		if ($('#txtAmount').val() == "") {
			$(".error-msg").html('Please enter the Amount');
			$("#saveTarget").prop('disabled', false)
			return;
		}

		if ($('#txtVolume').val() == "") {
			$(".error-msg").html('Please enter the Volume');
			$("#saveTarget").prop('disabled', false)
			return;
		}
		var monthlyTarget = {
			salesLedgerPid : $('#hdnSalesLedgerPid').val(),
			salesLedgerWiseTargetPid : $('#hdnSalesLedgerWiseTargetPid').val(),
			amount : $('#txtAmount').val(),
			monthAndYear : $('#txtMonth').val(),
			salesLedgerName : ""
		}
		$(".error-msg").html("Please wait.....");
		$
				.ajax({
					url : setSalesLedgerWiseMontlyTargetContextPath
							+ "/monthly-sales-ledger-wise-targets",
					type : "POST",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(monthlyTarget),
					success : function(result) {
						$("#modalSetTarget").modal("hide");

						// change target td
						$(trTarget).closest("tr").find("td").eq(1).text(
								result.amount);

						// change button UserSalesTargetPid
						$(trTarget)
								.closest("tr")
								.find("td")
								.eq(3)
								.html(
										"<input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
												+ "onclick='SetMonthlySalesLedgerWiseTarget.editSalesLedgerWiseTarget(\""
												+ result.salesLedgerPid
												+ "\",\""
												+ result.salesLedgerWiseTargetPid
												+ "\",this);'/>");
						$("#saveTarget").prop('disabled', false)
					},
					error : function(textStatus, errorThrown) {
						$("#saveTarget").prop('disabled', false);
					},
				});
	}

	function addRowsToTable(salesLedgerWiseTargets) {
		if (salesLedgerWiseTargets) {
			$('#tblSetTarget').html("");
			$
					.each(
							salesLedgerWiseTargets,
							function(index, salesWiseLedger) {
								$('#tblSetTarget')
										.append(
												"<tr><td>"
														+ salesWiseLedger.salesLedgerName
														+ "</td><td>"
														+ salesWiseLedger.amount
														+ "</td><td><input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
														+ "onclick='SetMonthlySalesLedgerWiseTarget.editSalesLedgerWiseTarget(\""
														+ salesWiseLedger.salesLedgerPid
														+ "\",\""
														+ salesWiseLedger.salesLedgerWiseTargetPid
														+ "\",this);'/></td></tr>");
							});
		} else {
			$('#tblSetTarget').html(
					"<tr><td colspan='4'>Activity not assigned</td></tr>")
		}

	}

	function searchTable(inputVal) {
		console.log(inputVal);
		var table = $('#tblSetTarget');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = setSalesLedgerWiseMontlyTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = setSalesLedgerWiseMontlyTargetContextPath;
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		salesUserTargetModel.pid = null; // reset salesUserTarget model;
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