// methods in a closure to avoid creating global variables.

if (!this.Funnel) {
	this.Funnel = {};
}

(function() {
	'use strict';

	var funnelContextPath  = location.protocol + '//' + location.host + location.pathname;
	var createEditForm = $("#funnelForm");
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
				createUpdateFunnel(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteFunnel(e.currentTarget.action);
		});
		
		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
		
		$('#btnSaveStages').click(function() {
			saveAssignedStages();
		});
	});

	function createUpdateFunnel(el) {
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			data : {
				id : $('#hdnFunnelId').val(),
				name :  $('#field_name').val(), 
				sortOrder: $('#field_sortOrder').val()
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showFunnel(id) {
		$.ajax({
			url : funnelContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_sortOrder').text(data.sortOrder);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editFunnel(id) {
		$.ajax({
			url : funnelContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_sortOrder').val((data.sortOrder));
				$('#hdnFunnelId').val(data.id);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteFunnel(actionurl, id) {
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
	
	Funnel.loadStages = function(id, obj) {
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#stagesCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : funnelContextPath + "/findStages/" + id,
			type : "GET",
			success : function(data) {
				$('#hdnFunnelId').val(id)
				if (data) {
					$.each(data, function(index, stage) {
						$(
								"#stagesCheckboxes input:checkbox[value="
										+ stage.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		$("#stagesModal").modal("show");
	}

	function saveAssignedStages() {
		$(".error-msg").html("");
		var selectedStages = "";

		$.each($("input[name='stage']:checked"), function() {
			selectedStages += $(this).val() + ",";
		});

		if (selectedStages == "") {
			$(".error-msg").html("Please select Stage");
			return;
		}
		$.ajax({
			url : funnelContextPath + "/assignStages",
			type : "POST",
			data : {
				id : $('#hdnFunnelId').val(),
				assignedStagePids : selectedStages,
			},
			success : function(status) {
				$("#stagesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = funnelContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = funnelContextPath;
	}

	Funnel.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showFunnel(id);
				break;
			case 1:
				editFunnel(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', funnelContextPath + "/" + id);
				break;
		
			}
		}
		el.modal('show');
	}
	
	Funnel.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		//funnelModel.pid = null; // reset Stage model;
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
	
	function searchTable(inputVal) {
		var table = $('#tblStages');
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
})();