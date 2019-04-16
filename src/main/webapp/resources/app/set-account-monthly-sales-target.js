// Create a SetMonthlySalesTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SetAccountMonthlySalesTarget) {
	this.SetAccountMonthlySalesTarget = {};
}

(function() {
	'use strict';
	
	var contextPath = location.protocol + '//'	+ location.host;
	
	$(document).ready(
			function() {
				$('#txtMonthAccountWise').MonthPicker({
					ShowIcon : false
				});
				
				$('input[type=month]').MonthPicker().css('backgroundColor',
						'lightyellow');
				
				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});
				
				$('.alphabets').click(function() {
					loadAccountMonthlySales(this.name);
				});
				
				$('#sbLocation').change(function() {
					loadAccountProfileByLocation();
				});
				
			});
	
	function loadAccountProfileByLocation() {
		if($('#sbLocation').val() == "-1") {
			$('#tblSetTarget').find('tr').show();
			return;
		}
		if($('#tblSetTarget tr').length <= 0){
			$('#sbLocation').val("-1");
			alert("No data to filter.");
			return;
		}
		$.ajax({
			url : contextPath + "/web/locations/accounts",
			type : 'GET',
			data : {
				locationPid : $('#sbLocation').val(),
			},
			success : function(accountProfiles) {
				console.log(accountProfiles);
				//hide all rows
				$('#tblSetTarget').find('tr').hide();
				$.each(accountProfiles,function(index, account) {
					$('#tblSetTarget').find('tr#'+ account.pid).show();
				});
			}
		});
	}
	
	function loadAccountMonthlySales(filterBy) {
		if ($('#dbSalesTargetGroup').val() == "-1" || $('#txtMonthAccountWise').val() == "") {
			alert("Please select a target group and date");
			$('#tblSetTarget').html("");
			return;
		}
		$('#tblSetTarget').html("<tr><td colspan='4'>Please wait...</td></tr>");
		$.ajax({
			url : contextPath + "/web/account-monthly-sales-targets/load",
			type : 'GET',
			data : {
				salesTargetGroupPid : $('#dbSalesTargetGroup').val(),
				monthAndYear : $('#txtMonthAccountWise').val(),
				filterBy : filterBy
			},
			success : function(salesTargets) {
				addRowsToTableAccountWise(salesTargets);
			}
		});
	}

	SetAccountMonthlySalesTarget.saveAccountWiseTarget = function(accountPid,
			userSalesTargetPid, obj) {
		var trTarget;
		$(obj).prop('disabled', true);
		
		var amount = $('#txtAmount'+accountPid).val();
		var volume = $('#txtVolume'+accountPid).val();
				
		if (amount == "") {
			alert('Please enter the Amount');
			$(obj).prop('disabled', false)
			return;
		}

		if (volume == "") {
			alert('Please enter the Volume');
			$(obj).prop('disabled', false)
			return;
		}

		var monthlyTarget = {
			userPid : null,
			salesTargetGroupPid : $('#dbSalesTargetGroup').val(),
			accountProfilePid : accountPid,
			salesTargetGroupUserTragetPid : userSalesTargetPid,
			volume : volume,
			amount : amount,
			monthAndYear : $('#txtMonthAccountWise').val(),
			salesTargetGroupName : ""
		}
		
		$
				.ajax({
					url : contextPath + "/web/account-monthly-sales-targets",
					type : "POST",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(monthlyTarget),
					success : function(result) {
						alert('success');
						// change button UserSalesTargetPid
						$(trTarget)
								.closest("tr")
								.find("td")
								.eq(3)
								.html(
										"<input class='btn btn-success' style='width:100px'  type='button' value='Update' "
												+ "onclick='SetMonthlySalesTarget.saveAccountWiseTarget(\""
												+ result.accountProfilePid
												+ "\",\""
												+ result.salesTargetGroupUserTragetPid
												+ "\",this);'/>");
						$(obj).prop('disabled', false)
					},
					error : function(textStatus, errorThrown) {
						alert('failed');
						$(obj).prop('disabled', false);
					},
				});
	}

	function addRowsToTable(salesTargets) {
		if (salesTargets) {
			$('#tblSetTarget').html("");
			$
					.each(
							salesTargets,
							function(index, salesTarget) {
								$('#tblSetTarget')
										.append(
												"<tr><td>"
														+ salesTarget.salesTargetGroupName
														+ "</td><td>"
														+ salesTarget.amount
														+ "</td><td>"
														+ salesTarget.volume
														+ "</td><td><input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
														+ "onclick='SetMonthlySalesTarget.editActivityTarget(\""
														+ salesTarget.salesTargetGroupPid
														+ "\",\""
														+ salesTarget.salesTargetGroupUserTragetPid
														+ "\",this);'/></td></tr>");
							});
		} else {
			$('#tblSetTarget').html(
					"<tr><td colspan='4'>Activity not assigned</td></tr>")
		}

	}
	
	function addRowsToTableAccountWise(salesTargets) {
		if (salesTargets) {
			$('#tblSetTarget').html("");
			$
					.each(
							salesTargets,
							function(index, salesTarget) {
								$('#tblSetTarget')
										.append(
												"<tr id='"+ salesTarget.accountProfilePid +"'><td>"
														+ salesTarget.accountProfileName
														+ "</td><td><input id='txtAmount"+salesTarget.accountProfilePid+"' type='number' value='"
														+ salesTarget.amount
														+ "' ></td><td><input id='txtVolume"+salesTarget.accountProfilePid+"' type='number' value='"
														+ salesTarget.volume
														+ "' ></td><td><input class='btn btn-success' style='width:100px'  type='button' value='Update' "
														+ "onclick='SetAccountMonthlySalesTarget.saveAccountWiseTarget(\""
														+ salesTarget.accountProfilePid
														+ "\",\""
														+ salesTarget.salesTargetGroupUserTragetPid
														+ "\",this);'/></td></tr>");
							});
		} else {
			$('#tblSetTarget').html(
					"<tr><td colspan='4'>Activity not assigned</td></tr>")
		}

	}
	
	function searchTable(inputVal) {
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