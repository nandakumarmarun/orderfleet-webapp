// Create a SalesTargetBlock object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SalesTargetBlock) {
	this.SalesTargetBlock = {};
}

(function() {
	'use strict';

	var salesTargetBlockContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#salesTargetBlockForm");
	var deleteForm = $("#deleteForm");
	var salesTargetBlockModel = {
		pid : null,
		name : null,
		startMonth : 0,
		startMonthName : null,
		startMonthMinus : 0,
		startMonthYearMinus : null,
		endMonth : 0,
		endMonthName : null,
		endMonthMinus : 0,
		endMonthYearMinus : null,
		createDynamicLabel : null,
		description : null,
		targetSettingType : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		targetSettingType : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		description : {
			maxlength : "This field cannot be longer than 250 characters."
		}
	};

	$(document).ready(
			function() {

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateSalesTargetBlock(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteSalesTargetBlock(e.currentTarget.action);
				});

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				$('#btnSaveSalesTargetgroups').on('click', function() {
					saveAssignedSalesTargetgroups();
				});

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				// select all checkbox in table tblPriceLevels
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});

			});

	function searchTable(inputVal) {
		var table = $('#tblSalesTargetgroups');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
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
	function createUpdateSalesTargetBlock(el) {
		salesTargetBlockModel.name = $('#field_name').val();

		salesTargetBlockModel.startMonth = $('#field_startMonth').val();
		salesTargetBlockModel.startMonthName = $(
				'#field_startMonth option:selected').text();
		salesTargetBlockModel.startMonthMinus = $('#field_startMonthMinus')
				.val();
		salesTargetBlockModel.startMonthYearMinus = $(
				'#field_startMonthYearMinus').val();

		salesTargetBlockModel.endMonth = $('#field_endMonth').val();
		salesTargetBlockModel.endMonthName = $(
				'#field_endMonth option:selected').text();
		salesTargetBlockModel.endMonthMinus = $('#field_endMonthMinus').val();
		salesTargetBlockModel.endMonthYearMinus = $('#field_endMonthYearMinus')
				.val();

		salesTargetBlockModel.createDynamicLabel = $(
				'#field_createDynamicLabel').prop("checked");
		salesTargetBlockModel.description = $('#field_description').val();

		salesTargetBlockModel.targetSettingType = $('#field_targetSettingType').val();

		console.log(salesTargetBlockModel);

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesTargetBlockModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editSalesTargetBlock(id) {
		$.ajax({
			url : salesTargetBlockContextPath + "/" + id,
			method : 'GET',
			success : function(data) {

				$('#field_name').val(data.name);

				$('#field_startMonth').val(data.startMonth);
				$('#field_startMonthMinus').val(data.startMonthMinus);
				$('#field_startMonthYearMinus').val(data.startMonthYearMinus);

				$('#field_endMonth').val(data.endMonth);
				$('#field_endMonthMinus').val(data.endMonthMinus);
				$('#field_endMonthYearMinus').val(data.endMonthYearMinus);

				$('#field_createDynamicLabel').prop("checked",
						data.createDynamicLabel);
				$('#field_description').val(data.description);

				$('#field_targetSettingType').val(data.targetSettingType);
				// set pid
				salesTargetBlockModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function loadSalesTargetGroups(pid, type) {

		$("input[name='filter'][value='all']").prop("checked", true);

		$('#tblSalesTargetgroups').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");

		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#salesTargetgroupsCheckboxes input:checkbox").attr('checked', false);
		$
				.ajax({
					url : salesTargetBlockContextPath
							+ "/findSalesTargetGroups/" + pid,
					type : "GET",
					data : {
						targetSettingType : type
					},
					success : function(data) {
						if (!data.length > 0) {
							$('#tblSalesTargetgroups')
									.html(
											"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						}

						if (data) {

							$('#tblSalesTargetgroups').html("");
							$.each(data, function(index, salesTargetgroup) {
								var checked = "";
								if (salesTargetgroup.alias == 'TRUE') {
									checked = "checked"
								}
								$('#tblSalesTargetgroups').append(
										"<tr><td><input name='salesTargetgroup' type='checkbox'value="+ salesTargetgroup.pid+" "+checked+"/></td>" +
											"<td>"+ salesTargetgroup.name + "</td></tr>");
							});

						}

						salesTargetBlockModel.pid = pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function saveAssignedSalesTargetgroups() {

		$(".error-msg").html("");
		var selectedSalesTargetgroups = "";

		$.each($("input[name='salesTargetgroup']:checked"), function() {
			selectedSalesTargetgroups += $(this).val() + ",";
		});
		 if (selectedSalesTargetgroups == "") {
		 $(".error-msg").html("Please select Sales Target Group");
		 return;
		 }
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : salesTargetBlockContextPath + "/saveAssignSalesTargetgroups",
			type : "POST",
			data : {
				pid : salesTargetBlockModel.pid,
				assignedSalesTargetgroups : selectedSalesTargetgroups,
			},
			success : function(status) {
				$("#assignSalesTargetgroupsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function deleteSalesTargetBlock(actionurl, id) {
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetBlockContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = salesTargetBlockContextPath;
	}

	SalesTargetBlock.showModalPopup = function(el, id, action, type) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showSalesTargetBlock(id);
				break;
			case 1:
				editSalesTargetBlock(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', salesTargetBlockContextPath + "/"
						+ id);
				break;
			case 3:
				loadSalesTargetGroups(id, type);
				break;
			}
		}
		el.modal('show');
	}

	SalesTargetBlock.onchangeStartMonth = function() {
		var value = $('#field_startMonth').val();
		if (value == 0) {
			$("#field_startMonthMinus").attr("readonly", false);
		} else {
			$("#field_startMonthMinus").val(0);
			$("#field_startMonthMinus").attr("readonly", true);
		}
	}

	SalesTargetBlock.onchangeEndMonth = function() {
		var value = $('#field_endMonth').val();
		if (value == 0) {
			$("#field_endMonthMinus").attr("readonly", false);
		} else {
			$("#field_endMonthMinus").val(0);
			$("#field_endMonthMinus").attr("readonly", true);
		}
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		salesTargetBlockModel.pid = null; // reset salesTargetBlock
		// model;
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