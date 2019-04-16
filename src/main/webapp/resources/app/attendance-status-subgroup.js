if (!this.AttendanceStatusSubgroup) {
	this.AttendanceStatusSubgroup = {};
}

(function() {
	'use strict';

	var attendanceStatusSubgroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#attendanceStatusSubgroupForm");
	var deleteForm = $("#deleteForm");
	var attendanceStatusSubgroupModel = {
			id:null,
		name : null,
		description : null,
		code:null,
		attendanceStatus:null,
		sortOrder : 0
		
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		code : {
			maxlength : 3
		},
		attendanceStatus : {
			valueNotEquals : "-1"
		},
		description : {
			maxlength : 250
		}
		
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		code : {
			maxlength : "This field cannot be longer than 3 characters."
		},
		description : {
			maxlength : "This field cannot be longer than 250 characters."
		},
		
		
	};

	$(document).ready(function() {
		
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "Please Select Attendance Status");
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateAttendanceStatusSubgroup(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteAttendanceStatusSubgroup(e.currentTarget.action);
		});
		$('#btnSearch').click(function() {
			searchTable($("#searchAttendance").val());
		});
		
	});

	function searchTable(inputVal) {
		var table = $('#tBodyattendanceStatusSubgroupTable');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
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
	
	function createUpdateAttendanceStatusSubgroup(el) {
		attendanceStatusSubgroupModel.name = $('#field_name').val();
		attendanceStatusSubgroupModel.code = $('#field_code').val();
		attendanceStatusSubgroupModel.description = $('#field_description').val();
		attendanceStatusSubgroupModel.attendanceStatus = $('#field_attendanceStatus').val();
		attendanceStatusSubgroupModel.sortOrder = $('#field_sortOrder').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(attendanceStatusSubgroupModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showAttendanceStatusSubgroup(id) {
		$.ajax({
			url : attendanceStatusSubgroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_code').text((data.code == null ? "" : data.code));
				$('#lbl_description').text((data.description == null ? "" : data.description));
				$('#lbl_attendanceStatus').text(data.attendanceStatus);
				$('#lbl_sortOrder').text(data.sortOrder);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editAttendanceStatusSubgroup(id) {
		$.ajax({
			url : attendanceStatusSubgroupContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_code').val((data.code == null ? "" : data.code));
				$('#field_description').val((data.description == null ? "" : data.description));
				$('#field_attendanceStatus').val(data.attendanceStatus);
				$('#field_sortOrder').val((data.sortOrder));
				// set pid
				attendanceStatusSubgroupModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteAttendanceStatusSubgroup(actionurl, id) {
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
		window.location = attendanceStatusSubgroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = attendanceStatusSubgroupContextPath;
	}

	AttendanceStatusSubgroup.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showAttendanceStatusSubgroup(id);
				break;
			case 1:
				editAttendanceStatusSubgroup(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', attendanceStatusSubgroupContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	AttendanceStatusSubgroup.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		attendanceStatusSubgroupModel.id = null; // reset attendanceStatusSubgroup model;
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