// Create a SetMonthlySalesTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SetMonthlySalesTarget) {
	this.SetMonthlySalesTarget = {};
}

(function() {
	'use strict';

	var setMontlySalesTargetContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	
	$(document).ready(
			function() {
				
				
				$('#btnApplyMonthlyProductGroups').click(function() {
					SetMonthlySalesTarget.loadUserMonthlySales();
				});

			
				$('input[type=month]').MonthPicker().css('backgroundColor',
						'lightyellow');
				
				$("#txtMonth").MonthPicker({
				    Button: '<div class="input-group-addon"><a href="#"><i class="entypo-calendar"></i></a></div>'
				});
				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});
			});

	SetMonthlySalesTarget.loadUserMonthlySales = function() {

		if ($("#dbLocation").val() == "-1" || $('#txtMonth').val() == "") {
			if ($("#dbLocation").val() == "-1") {
				$('#tblSetTarget').html("");
				alert("select location");
			}
			return;
		}

		$('#tblSetTarget').html("<tr><td colspan='4'>Please wait...</td></tr>");
		$.ajax({
			url : setMontlySalesTargetContextPath + "/monthly-product-group-targets",
			type : 'GET',
			data : {
				locationsPid : $("#dbLocation").val(),
				monthAndYear : $('#txtMonth').val(),
			},
			success : function(productGroupTargets) {
				addRowsToTable(productGroupTargets);
			}
		});
	}


	var trTarget;
	SetMonthlySalesTarget.editProductGroupTarget = function(productGroupTargetPid,
			LocationProductGroupTargetPid, obj) {
		$('.error-msg').html('');
		trTarget = obj;

		$('#hdnproductGroupPid').val(productGroupTargetPid);
		$('#hdnLocationProductGroupTargetPid').val(LocationProductGroupTargetPid);
		var Amount = $(obj).closest("tr").find("td").eq(1).text();
		var Volume = $(obj).closest("tr").find("td").eq(2).text();
		$('#txtAmount').val(Amount)
		$('#txtVolume').val(Volume)
		$("#modalSetTarget").modal("show");
	}

	SetMonthlySalesTarget.saveTarget = function() {
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
			locationPid : $('#dbLocation').val(),
			productGroupPid : $('#hdnproductGroupPid').val(),
			productGroupLocationTragetPid : $('#hdnLocationProductGroupTargetPid').val(),
			volume : $('#txtVolume').val(),
			amount : $('#txtAmount').val(),
			monthAndYear : $('#txtMonth').val(),
			productGroupName : ""
		}
		$(".error-msg").html("Please wait.....");
		$
				.ajax({
					url : setMontlySalesTargetContextPath
							+ "/monthly-product-group-targets",
					type : "POST",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(monthlyTarget),
					success : function(result) {
						$("#modalSetTarget").modal("hide");

						// change target td
						$(trTarget).closest("tr").find("td").eq(1).text(
								result.amount);

						// change target td
						$(trTarget).closest("tr").find("td").eq(2).text(
								result.volume);

						// change button UserSalesTargetPid
						$(trTarget)
								.closest("tr")
								.find("td")
								.eq(3)
								.html(
										"<input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
												+ "onclick='SetMonthlySalesTarget.editProductGroupTarget(\""
												+ result.productGroupPid
												+ "\",\""
												+ result.productGroupLocationTragetPid
												+ "\",this);'/>");
						$("#saveTarget").prop('disabled', false)
					},
					error : function(textStatus, errorThrown) {
						$("#saveTarget").prop('disabled', false);
					},
				});
	}

	

	function addRowsToTable(productGroupTargets) {
		if (productGroupTargets) {
			$('#tblSetTarget').html("");
			$
					.each(
							productGroupTargets,
							function(index, productGroupTarget) {
								$('#tblSetTarget')
										.append(
												"<tr><td>"
														+ productGroupTarget.productGroupName
														+ "</td><td>"
														+ productGroupTarget.amount
														+ "</td><td>"
														+ productGroupTarget.volume
														+ "</td><td><input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
														+ "onclick='SetMonthlySalesTarget.editProductGroupTarget(\""
														+ productGroupTarget.productGroupPid
														+ "\",\""
														+ productGroupTarget.productGroupLocationTragetPid
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
		window.location = salesUserTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = salesUserTargetContextPath;
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