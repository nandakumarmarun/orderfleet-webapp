// Create a Location object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Location) {
	this.Location = {};
}

(function() {
	'use strict';

	var locationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#locationForm");
	var deleteForm = $("#deleteForm");
	var locationModel = {
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

	$(location).ready(
			function() {

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateLocation(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteLocation(e.currentTarget.action);
				});

				$('#btnSaveAccountProfiles').on('click', function() {
					saveAssignedAccountProfiles();
				});

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});
				// select all checkbox in table tBodyAccountProfile
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]:visible').prop(
									'checked',  $(this).prop('checked'));
						});
				console.log(this);
//				$(element).is(":visible");
				$('#btnActivateLocation').on('click', function() {
					activateAssignedLocation();
				});
				
				

			});
	
	function activateAssignedLocation() {
		$(".error-msg").html("");
		var selectedLocation = "";

		$.each($("input[name='location']:checked"), function() {
			selectedLocation += $(this).val() + ",";
		});

		if (selectedLocation == "") {
			$(".error-msg").html("Please select Location");
			return;
		}
		$.ajax({
			url : locationContextPath + "/activateLocation",
			type : "POST",
			data : {
				locations : selectedLocation,
			},
			success : function(status) {
				$("#enableLocationModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function searchTable(inputVal) {
		var table = $('#tBodyAccountProfile');
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

	function createUpdateLocation(el) {
		locationModel.name = $('#field_name').val();
		locationModel.alias = $('#field_alias').val();
		locationModel.description = $('#field_description').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(locationModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showLocation(id) {
		$.ajax({
			url : locationContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_description').text(data.description);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editLocation(id) {
		$.ajax({
			url : locationContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_description').val(data.description);
				// set pid
				locationModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteLocation(actionurl, id) {
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

	Location.assignAccountProfiles = function(el, pid, type) {
		locationModel.pid = pid;

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#divAccountProfiles input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : locationContextPath + "/accounts",
			type : "GET",
			data : {
				locationPid : locationModel.pid
			},
			success : function(assignedAccountProfiles) {
				if (assignedAccountProfiles) {
					$.each(assignedAccountProfiles, function(index,
							accountProfile) {
						$(
								"#divAccountProfiles input:checkbox[value="
										+ accountProfile.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		el.modal('show');

	}

	function saveAssignedAccountProfiles() {

		$(".error-msg").html("");
		var selectedAccountProfiles = "";

		$.each($("input[name='accountProfile']:checked"), function() {
			selectedAccountProfiles += $(this).val() + ",";
		});

		console.log(selectedAccountProfiles);
		if (selectedAccountProfiles == "") {
			$(".error-msg").html("Please select Price Level");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : locationContextPath + "/assign-accounts",
			type : "POST",
			data : {
				locationPid : locationModel.pid,
				assignedAccountProfiles : selectedAccountProfiles
			},
			success : function(status) {
				$("#accountProfilesModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	Location.setActive=function(name,pid,activated){
		locationModel.pid=pid;
		locationModel.activated=activated;
		locationModel.name=name;
		
		if(confirm("Are you confirm?")){
			$.ajax({
				url:locationContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(locationModel),
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
		window.location = locationContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = locationContextPath;
	}

	Location.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showLocation(id);
				break;
			case 1:
				editLocation(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', locationContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Location.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		locationModel.pid = null; // reset location model;
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