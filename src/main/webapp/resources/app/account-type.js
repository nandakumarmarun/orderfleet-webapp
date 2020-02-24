// Create a AccountType object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountType) {
	this.AccountType = {};
}
(function() {
	'use strict';

	var accountTypeContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#accountTypeForm");
	var deleteForm = $("#deleteForm");
	var accountTypeModel = {
		pid : null,
		name : null,
		alias : null,
		accountNameType : null,
		description : null,
		receiverSupplierType : null,

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		accountNameType : {
			valueNotEquals : "-1"
		},
		alias : {
			maxlength : 55
		},
		receiverSupplierType : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		},
		accountNameType : {
			required : "This field is required."
		},
		description : {
			maxlength : "This field cannot be longer than 250 characters."
		},
		receiverSupplierType : {
			required : "This field is required."
		}
	};

	$(document)
			.ready(
					function() {

						// add the rule here
						$.validator.addMethod("valueNotEquals", function(value,
								element, arg) {
							return arg != value;
						}, "");

						createEditForm.validate({
							rules : validationRules,
							messages : validationMessages,
							submitHandler : function(form) {
								createUpdateAccountType(form);
							}
						});

						deleteForm.submit(function(e) {
							// prevent Default functionality
							e.preventDefault();
							// pass the action-url of the form
							deleteAccountType(e.currentTarget.action);
						});
						$('#btnSaveAccounts').click(function() {
							saveAssignedAccounts();
						});

						$('#btnActivateAccountTypes').on('click', function() {
							activateAssignedAccountTypes();
						});

						$('#deactivatedAccounts').click(function() {
							addDeactivatedAccounts();
						});

						$('#btnSaveActivity').click(function() {
							saveAssignedActivities();
						});

						$('#btnSaveAssociatedAccountType').click(function() {
							saveAssociatedAccountTypes();
						});
						
						// table search
						$('#btnSearch').click(function() {
							searchAccountTable($("#search").val());
						});
						
						$('#btnSearch_accountTypes').click(function() {
							searchAccountTypeTable($("#search_accountTypes").val());
						});
						
						$('#btnSearch_activities').click(function() {
							searchActivityTable($("#search_activities").val());
						});

						/* select all checkbox in table product table */
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

	function searchAccountTable(inputVal) {
		var table = $('#tblAccounts');
		var filterBy = $("input[name='filter']:checked").val();		
		table.find('tr').each(
				function(index, row) {
					var allCells = $(row).find('td');
					if (allCells.length > 0) {	
						var found = false;
						allCells.each(function(index, td) {
							if (index == 0) {
								if (filterBy != "all") {
									var val = $(td).find('input').prop(
											'checked');
									var deselected = $(td).closest('tr').find(
											'td:eq(2)').text();
									if (filterBy == "selected") {
										if (!val) {
											return false;
										}
									} else if (filterBy == "unselected") {
										if (val) {
											return false;
										}
									} else if (filterBy == "deactivated") {
										if (deselected == "true") {
											return false;
										}
									}
								}
							}
							var regExp = new RegExp(inputVal, 'i');
							if (regExp.test($(td).text())) {
								found = true;
								return false;
							}
						});
						if (found == true)
							$(row).show();
						else
							$(row).hide();
					}
				});
	}
	
	function searchAccountTypeTable(inputVal) {
		var table = $('#tblAccountTypes');
		var filterBy = $("input[name='filter_accountTypes']:checked").val();		
		table.find('tr').each(
				function(index, row) {
					var allCells = $(row).find('td');
					if (allCells.length > 0) {	
						var found = false;
						allCells.each(function(index, td) {
							if (index == 0) {
								if (filterBy != "all") {
									var val = $(td).find('input').prop(
											'checked');
									var deselected = $(td).closest('tr').find(
											'td:eq(2)').text();
									if (filterBy == "selected") {
										if (!val) {
											return false;
										}
									} else if (filterBy == "unselected") {
										if (val) {
											return false;
										}
									} else if (filterBy == "deactivated") {
										if (deselected == "true") {
											return false;
										}
									}
								}
							}
							var regExp = new RegExp(inputVal, 'i');
							if (regExp.test($(td).text())) {
								found = true;
								return false;
							}
						});
						if (found == true)
							$(row).show();
						else
							$(row).hide();
					}
				});
	}
	
	function searchActivityTable(inputVal) {
		var table = $('#tblActivities');
		var filterBy = $("input[name='filter_activities']:checked").val();		
		table.find('tr').each(
				function(index, row) {
					var allCells = $(row).find('td');
					if (allCells.length > 0) {	
						var found = false;
						allCells.each(function(index, td) {
							if (index == 0) {
								if (filterBy != "all") {
									var val = $(td).find('input').prop(
											'checked');
									var deselected = $(td).closest('tr').find(
											'td:eq(2)').text();
									if (filterBy == "selected") {
										if (!val) {
											return false;
										}
									} else if (filterBy == "unselected") {
										if (val) {
											return false;
										}
									} else if (filterBy == "deactivated") {
										if (deselected == "true") {
											return false;
										}
									}
								}
							}
							var regExp = new RegExp(inputVal, 'i');
							if (regExp.test($(td).text())) {
								found = true;
								return false;
							}
						});
						if (found == true)
							$(row).show();
						else
							$(row).hide();
					}
				});
	}
	
	var accountTYpename = "";
	var valueExist = true;

	AccountType.loadAccounts = function(pid, obj) {
		accountTYpename = $(obj).closest('tr').find('td:nth-child(1)').text();
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchAccountTable("");

		// clear all check box
		$("#accountsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : accountTypeContextPath + "/findAccounts/" + pid,
			type : "GET",
			success : function(data) {
				accountTypeModel.pid = pid;
				if (data) {
					$.each(data, function(index, account) {
						$(
								"#accountsCheckboxes input:checkbox[value="
										+ account.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		$("#accountsModal").modal("show");
	}

	AccountType.loadActivities = function(pid, obj) {
		accountTYpename = $(obj).closest('tr').find('td:nth-child(1)').text();
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search_activities").val("");
		searchActivityTable("");

		// clear all check box
		$("#activityCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : accountTypeContextPath + "/findActivities/" + pid,
			type : "GET",
			success : function(data) {

				accountTypeModel.pid = pid;
				if (data) {
					$.each(data, function(index, activity) {
						$(
								"#activityCheckboxes input:checkbox[value="
										+ activity.activityPid + "]").prop(
								"checked", true);
						if (activity.assignNotification) {
							$("#drop-" + activity.activityPid).val("true");
						} else {
							$("#drop-" + activity.activityPid).val("false");
						}

					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		$("#activitiesModal").modal("show");
	}

	AccountType.loadAssociatedAccountTypes = function(pid, obj) {
		accountTYpename = $(obj).closest('tr').find('td:nth-child(1)').text();
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search_accountTypes").val("");
		searchAccountTypeTable("");

		// clear all check box
		$("#associatedAccountTypeCheckboxes input:checkbox").attr('checked',
				false);
		$
				.ajax({
					url : accountTypeContextPath
							+ "/findAssociatedAccountTypes/" + pid,
					type : "GET",
					success : function(data) {

						accountTypeModel.pid = pid;
						if (data) {
							$
									.each(
											data,
											function(index, accountType) {
												$(
														"#associatedAccountTypeCheckboxes input:checkbox[value="
																+ accountType.associatedAccountTypePid
																+ "]").prop(
														"checked", true);
											});
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
		$("#associatedAccountTypeModal").modal("show");
	}

	function saveAssignedAccounts() {

		$(".error-msg").html("");
		var selectedAccounts = "";

		$.each($("input[name='account']:checked"), function() {
			selectedAccounts += $(this).val() + ",";
		});

		if (selectedAccounts == "") {
			$(".error-msg").html("Please select Accounts");
			return;
		}
		$.ajax({
			url : accountTypeContextPath + "/assignAccounts",
			type : "POST",
			data : {
				pid : accountTypeModel.pid,
				assignedAccounts : selectedAccounts

			},
			success : function(status) {
				$("#activityModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedActivities() {

		$(".error-msg").html("");
		var selectedActivities = "";
		var activityNotification = "";
		$.each($("input[name='activity']:checked"), function() {
			selectedActivities += $(this).val() + "~"
					+ $('#drop-' + $(this).val()).val() + ",";

		});

		if (selectedActivities == "") {
			$(".error-msg").html("Please select Activities");
			return;
		}
		$.ajax({
			url : accountTypeContextPath + "/assignActivities",
			type : "POST",
			data : {
				pid : accountTypeModel.pid,
				assignedActivities : selectedActivities

			},
			success : function(status) {
				$("#activityModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssociatedAccountTypes() {

		$(".error-msg").html("");
		var selectedAccountTypes = "";

		$.each($("input[name='accountType']:checked"), function() {
			selectedAccountTypes += $(this).val() + "~"
					+ $('#drop-' + $(this).val()).val() + ",";

		});

		if (selectedAccountTypes == "") {
			$(".error-msg").html("Please select Account Types");
			return;
		}

		console.log(accountTypeModel.pid + "------------"
				+ selectedAccountTypes);
		$.ajax({
			url : accountTypeContextPath + "/assignAssociatedAccountTypes",
			type : "POST",
			data : {
				pid : accountTypeModel.pid,
				assignedAccountTypes : selectedAccountTypes

			},
			success : function(status) {
				$("#associatedAccountTypeModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function activateAssignedAccountTypes() {
		$(".error-msg").html("");
		var selectedAccountTypes = "";

		$.each($("input[name='accounttype']:checked"), function() {
			selectedAccountTypes += $(this).val() + ",";
		});

		if (selectedAccountTypes == "") {
			$(".error-msg").html("Please select Account Types");
			return;
		}
		$.ajax({
			url : accountTypeContextPath + "/activateAccountTypes",
			type : "POST",
			data : {
				accounttypes : selectedAccountTypes,
			},
			success : function(status) {
				$("#enableAccountTypesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function addDeactivatedAccounts() {
		if (valueExist) {
			$(this).html('Loading...');
			$
					.ajax({
						url : accountTypeContextPath + "/deactivatedAccounts",
						method : 'GET',
						success : function(data) {
							$
									.each(
											data,
											function(index, account) {
												var checkbox = "";
												if (account.accountTypeName == accountTYpename) {
													checkbox = "<input name='account' type='checkbox'value="
															+ account.pid
															+ " checked='checked' />";
												} else {
													checkbox = "<input name='account' type='checkbox'value="
															+ account.pid
															+ " />";
												}
												$('#allAccounts tbody').append(
														"<tr><td>" + checkbox
																+ "</td><td>"
																+ account.name
																+ "</td></tr>");
											});
							$('#deactivatedAccounts').html(
									'Add Deactivated Accounts');
							valueExist = false;
						},
						error : function(xhr, error) {
							$('#deactivatedAccounts').html(
									'Add Deactivated Accounts');
							onError(xhr, error);
						}
					});
		}
	}

	function createUpdateAccountType(el) {
		accountTypeModel.name = $('#field_name').val();
		accountTypeModel.alias = $('#field_alias').val();
		accountTypeModel.accountNameType = $('#field_accountNameType').val();
		accountTypeModel.description = $('#field_description').val();
		accountTypeModel.receiverSupplierType = $('#field_receiverSupplierType')
				.val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountTypeModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showAccountType(id) {
		$.ajax({
			url : accountTypeContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_accountNameType').text(
						(data.accountNameType == null ? ""
								: data.accountNameType));
				$('#lbl_description').text(
						(data.description == null ? "" : data.description));
				$('#lbl_receiverSupplierType').text(
						(data.receiverSupplierType == null ? ""
								: data.receiverSupplierType));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editAccountType(id) {
		$.ajax({
			url : accountTypeContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_accountNameType').val(
						(data.accountNameType == null ? ""
								: data.accountNameType));
				$('#field_description').val(
						(data.description == null ? "" : data.description));
				$('#field_receiverSupplierType').val(
						(data.receiverSupplierType == null ? ""
								: data.receiverSupplierType));
				// set pid
				accountTypeModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteAccountType(actionurl, id) {
		$.ajax({
			url : actionurl,
			method : 'DELETE',
			success : function(data) {
				onDeleteSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	AccountType.setActive = function(name, pid, active) {
		accountTypeModel.pid = pid;
		accountTypeModel.activated = active;
		accountTypeModel.name = name;
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : accountTypeContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(accountTypeModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountTypeContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = accountTypeContextPath;
	}

	AccountType.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showAccountType(id);
				break;
			case 1:
				editAccountType(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', accountTypeContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	AccountType.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		accountTypeModel.pid = null; // reset accountType model;
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