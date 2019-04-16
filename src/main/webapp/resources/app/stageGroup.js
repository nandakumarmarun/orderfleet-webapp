// methods in a closure to avoid creating global variables.

if (!this.StageGroup) {
	this.StageGroup = {};
}

(function() {
	'use strict';

	var stageGroupContextPath  = location.protocol + '//' + location.host + location.pathname;
	var createEditForm = $("#stageGroupForm");
	var deleteForm = $("#deleteForm");
	var stageGroupModel = {
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
				createUpdateStageGroup(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteStageGroup(e.currentTarget.action);
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

	function createUpdateStageGroup(el) {
		stageGroupModel.name = $('#field_name').val();
		stageGroupModel.alias = $('#field_alias').val();
		stageGroupModel.description = $('#field_description').val();
		
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(stageGroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showStageGroup(id) {
		$.ajax({
			url : stageGroupContextPath + "/" + id,
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

	function editStageGroup(id) {
		$.ajax({
			url : stageGroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				// set pid
				stageGroupModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteStageGroup(actionurl, id) {
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

	StageGroup.setActive = function(name, pid, activated) {
		stageGroupModel.pid = pid;
		stageGroupModel.activated = activated;
		stageGroupModel.name = name;

		if (confirm("Are you confirm?")) {
			$.ajax({
				url : stageGroupContextPath + "/change",
				method : 'POST',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(stageGroupModel),
				success : function(data) {
					onSaveSuccess(data)
				},

				error : function(xhr, error) {
					onError(xhr, error)
				}
			});
		}
	}
	
	StageGroup.loadStages = function(pid, obj) {
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#stagesCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : stageGroupContextPath + "/findStages/" + pid,
			type : "GET",
			success : function(data) {
				$('#hdnStageGroup').val(pid)
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
			url : stageGroupContextPath + "/assignStages",
			type : "POST",
			data : {
				pid : $('#hdnStageGroup').val(),
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
		window.location = stageGroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = stageGroupContextPath;
	}

	StageGroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showStageGroup(id);
				break;
			case 1:
				editStageGroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', stageGroupContextPath + "/" + id);
				break;
		
			}
		}
		el.modal('show');
	}
	
	StageGroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		stageGroupModel.pid = null; // reset Stage model;
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