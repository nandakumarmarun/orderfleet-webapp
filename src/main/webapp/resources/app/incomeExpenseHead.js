if (!this.IncomeExpenseHead) {
	this.IncomeExpenseHead = {};
}

(function() {
	'use strict';

	var incomeExpenseHeadContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#incomeExpenseHeadForm");
	var deleteForm = $("#deleteForm");
	var incomeExpenseHeadModel = {
		pid : null,
		name : null,
		alias : null,
		description : null
		
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
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
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateIncomeExpenseHead(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteIncomeExpenseHead(e.currentTarget.action);
		});
		
		$('#btnActivateIncomeExpenseHead').on('click', function() {
			activateAssignedIncomeExpenseHead();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateAssignedIncomeExpenseHead() {
		$(".error-msg").html("");
		var selectedIncomeExpenseHead = "";

		$.each($("input[name='incomeExpenseHead']:checked"), function() {
			selectedIncomeExpenseHead += $(this).val() + ",";
		});

		if (selectedIncomeExpenseHead == "") {
			$(".error-msg").html("Please select IncomeExpenseHead");
			return;
		}
		$.ajax({
			url : incomeExpenseHeadContextPath + "/activateIncomeExpenseHead",
			type : "POST",
			data : {
				incomeExpenseHeads : selectedIncomeExpenseHead,
			},
			success : function(status) {
				$("#enableIncomeExpenseHeadModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateIncomeExpenseHead(el) {
		incomeExpenseHeadModel.name = $('#field_name').val();
		incomeExpenseHeadModel.alias = $('#field_alias').val();
		incomeExpenseHeadModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(incomeExpenseHeadModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showIncomeExpenseHead(id) {
		$.ajax({
			url : incomeExpenseHeadContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text((data.description == null ? "" : data.description));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editIncomeExpenseHead(id) {
		$.ajax({
			url : incomeExpenseHeadContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				// set pid
				incomeExpenseHeadModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteIncomeExpenseHead(actionurl, id) {
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

	IncomeExpenseHead.setActive=function(name,pid,active){
		incomeExpenseHeadModel.pid=pid;
		incomeExpenseHeadModel.activated=active;
		incomeExpenseHeadModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:incomeExpenseHeadContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(incomeExpenseHeadModel),
				success:function(data){
					onSaveSuccess(data);
				},
				error:function(xhr,error){
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = incomeExpenseHeadContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = incomeExpenseHeadContextPath;
	}

	IncomeExpenseHead.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showIncomeExpenseHead(id);
				break;
			case 1:
				editIncomeExpenseHead(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', incomeExpenseHeadContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	IncomeExpenseHead.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		incomeExpenseHeadModel.pid = null; // reset incomeExpenseHead model;
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