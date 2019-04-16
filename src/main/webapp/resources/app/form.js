// Create a Form object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Form) {
	this.Form = {};
}

(function() {
	'use strict';

	var formContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#formForm");
	var deleteForm = $("#deleteForm");
	var formModel = {
		pid : null,
		name : null,
		documentPid : null,
		description : null,
		jsCode : null,
		multipleRecord : null

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		documentPid : {
			valueNotEquals : "-1"
		},
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};

	$(document).ready(
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
						createUpdateForm(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteForm1(e.currentTarget.action);
				});

				$('#btnSaveQuestions').on('click', function() {
					saveAssignedQuestions();
				});

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				// select all checkbox in table tblProducts
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});

				$('#btnActivateForm').on('click', function() {
					activateAssignedForm();
				});

			});

	function activateAssignedForm() {
		$(".error-msg").html("");
		var selectedForm = "";

		$.each($("input[name='form']:checked"), function() {
			selectedForm += $(this).val() + ",";
		});

		if (selectedForm == "") {
			$(".error-msg").html("Please select Form");
			return;
		}
		$.ajax({
			url : formContextPath + "/activateForm",
			type : "POST",
			data : {
				forms : selectedForm,
			},
			success : function(status) {
				$("#enableFormModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function searchTable(inputVal) {
		var table = $('#tbodyQuestions');
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

	function createUpdateForm(el) {
		formModel.name = $('#field_name').val();
		formModel.documentPid = $('#field_document').val();
		formModel.description = $('#field_description').val();
		formModel.jsCode = $('#field_jsCode').val();
		formModel.multipleRecord = $('#field_multipleRecord').prop('checked');
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(formModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showForm(id, obj) {
		$
				.ajax({
					url : formContextPath + "/formElements/" + id,
					method : 'GET',
					success : function(data) {
						var formName = "";
						var formDiscription = "";
						$('#lbl_name').text(
								$(obj).closest('tr').find('td:eq(0)').text());
						$('#lbl_description').text(
								$(obj).closest('tr').find('td:eq(1)').text());
						$('#lbl_multipleRecord').text(
								$(obj).closest('tr').find('td:eq(2)').text());

						$('#tblQuestions').html("");
						var question = "";
						$
								.each(
										data,
										function(index, formElement) {
											question += ("<tr><td>"
													+ formElement.name + "</td></tr>");
										});

						if (question != "") {
							var table = "<thead><tr><th>Questions</th></tr></thead><tbody>"
									+ question + "</tbody>";
							$('#tblQuestions').append(table);
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function editForm(id) {
		$
				.ajax({
					url : formContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						console.log(data);
						console.log(formContextPath);
						$('#field_name').val(data.name);
						$('#field_document').val(data.documentPid);
						$('#field_jsCode').val(data.jsCode);
						$('#field_description').val(
								(data.description == null ? ""
										: data.description));
						$('#field_multipleRecord').prop("checked",
								data.multipleRecord);
						// set pid
						formModel.pid = data.pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteForm1(actionurl, id) {
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

	function loadQuestions(pid) {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#divQuestions input:checkbox[name='question']").attr('checked',
				false);
		$("#divQuestions input:checkbox[name='editable']")
				.attr('checked', true);
		$("#divQuestions input:checkbox[name='validationEnabled']").attr(
				'checked', false);
		$("#divQuestions input:checkbox[name='mandatory']").attr('checked',
				false);
		$("#divQuestions input:checkbox[name='visibility']")
		.attr('checked', true);
		$(".sortOrder").val(0);
		$(".reportOrder").val(0);
		$
				.ajax({
					url : formContextPath + "/form-elements/" + pid,
					type : "GET",
					success : function(formElements) {
						formModel.pid = pid;
						if (formElements) {
							$
									.each(
											formElements,
											function(index, formElement) {
												$(
														"#divQuestions input:checkbox[value="
																+ formElement.formElementPid
																+ "]").prop(
														"checked", true);
												$(
														"#"
																+ formElement.formElementPid
																+ "").val(
														formElement.sortOrder);
												$(
														"#reportOrder"
																+ formElement.formElementPid
																+ "")
														.val(
																formElement.reportOrder);
												$(
														"#editable"
																+ formElement.formElementPid
																+ "").prop(
														"checked",
														formElement.editable);
												$(
														"#validationEnabled"
																+ formElement.formElementPid
																+ "")
														.prop(
																"checked",
																formElement.validationEnabled);
												$(
														"#mandatory"
																+ formElement.formElementPid
																+ "").prop(
														"checked",
														formElement.mandatory);
												$(
														"#visibility"
																+ formElement.formElementPid
																+ "").prop(
														"checked",
														formElement.visibility);
											});
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function saveAssignedQuestions() {

		$(".error-msg").html("");
		var selectedQuestions = [];

		$.each($("input[name='question']:checked"),
				function() {
					var formElementPid = $(this).val();
					var sortOrder = $("#" + formElementPid + "").val();
					var reportOrder = $("#reportOrder" + formElementPid + "")
							.val();
					var editable = $("#editable" + formElementPid + "").prop(
							"checked");
					var validationEnabled = $(
							"#validationEnabled" + formElementPid + "").prop(
							"checked");
					var mandatory = $("#mandatory" + formElementPid + "").prop(
							"checked");
					var visibility = $("#visibility" + formElementPid + "").prop(
					"checked");
					selectedQuestions.push({
						formElementPid : formElementPid,
						sortOrder : sortOrder,
						reportOrder : reportOrder,
						editable : editable,
						validationEnabled : validationEnabled,
						mandatory : mandatory,
						visibility:visibility
					});
				});

		var formFormElement = {
			formPid : formModel.pid,
			formElementOrderDTOs : selectedQuestions,
		}

		if (selectedQuestions.length == 0) {
			$(".error-msg").html("Please select Questions");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : formContextPath + "/assignQuestions",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(formFormElement),
			success : function(status) {
				$("#assignQuestionsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	Form.setActive = function(name, pid, active) {
		formModel.pid = pid;
		formModel.name = name;
		formModel.activated = active;

		if (confirm("Are you confirm?")) {
			$.ajax({
				url : formContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(formModel),
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
		window.location = formContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = formContextPath;
	}

	Form.showModalPopup = function(el, id, action, obj) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showForm(id, obj);
				break;
			case 1:
				editForm(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', formContextPath + "/" + id);
				break;
			case 3:
				loadQuestions(id);
				break;
			}
		}
		el.modal('show');
	}

	Form.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		formModel.pid = null; // reset form model;
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