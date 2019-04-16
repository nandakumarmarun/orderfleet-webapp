// methods in a closure to avoid creating global variables.

if (!this.DashboardChartItem) {
	this.DashboardChartItem = {};
}

(function() {
	'use strict';

	var dashboardChartItemContextPath  = location.protocol + '//' + location.host + location.pathname;
	var createEditForm = $("#dashboardChartItemForm");
	var deleteForm = $("#deleteForm");
	
	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDashboardChartItem(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDashboardChartItem(e.currentTarget.action);
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function createUpdateDashboardChartItem(el) {
		if($('#dbDashboardItem').val() == "-1") {
			alert("Please select dashboard item");
			return false;
		}
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			data : {
				id : $('#hdnDashboardChartItemPid').val(),
				name :  $('#field_name').val(), 
				dashboardItemPid: $('#dbDashboardItem').val()
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showDashboardChartItem(id) {
		$.ajax({
			url : dashboardChartItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_dashboardItemName').text(data.dashboardItem.name);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDashboardChartItem(id) {
		$.ajax({
			url : dashboardChartItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				console.log(data.dashboardItem);
				$('#dbDashboardItem').val(data.dashboardItem.pid);
				$('#hdnDashboardChartItemPid').val(data.pid);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteDashboardChartItem(actionurl, id) {
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
		window.location = dashboardChartItemContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dashboardChartItemContextPath;
	}

	DashboardChartItem.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDashboardChartItem(id);
				break;
			case 1:
				editDashboardChartItem(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dashboardChartItemContextPath + "/" + id);
				break;
		
			}
		}
		el.modal('show');
	}
	
	DashboardChartItem.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
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