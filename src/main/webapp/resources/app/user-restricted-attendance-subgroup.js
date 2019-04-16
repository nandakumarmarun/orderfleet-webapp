if (!this.UserRestrictedAttendanceSubgroup) {
	this.UserRestrictedAttendanceSubgroup = {};
}

(function() {
	'use strict';

	var userRestrictedAttendanceSubgroupContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#userRestrictedAttendanceSubgroupForm");
	var deleteForm = $("#deleteForm");
	var userRestrictedAttendanceSubgroupModel = {
		id : null,
		userPid : null,
		attendanceSubgroupId : null,
		
	};
	$(document).ready(function() {
		$('#btnSave').on('click', function() {
			createUpdateUserRestrictedAttendanceSubgroup();
		});
		
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function createUpdateUserRestrictedAttendanceSubgroup() {
		var selectedSubgroups = "";
		$.each($("input[name='attendanceSubgroup']:checked"), function() {
			selectedSubgroups+=$(this).val()+",";
		});
		$.ajax({
			url : userRestrictedAttendanceSubgroupContextPath + "/save",
			type : "POST",
			data : {
				userPid : userRestrictedAttendanceSubgroupModel.userPid,
				selectedSubgroups : selectedSubgroups,
			},
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	function loadUserRestrictedAttendanceSubgroup(userPid) {
		userRestrictedAttendanceSubgroupModel.userPid = userPid;

		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : userRestrictedAttendanceSubgroupContextPath + "/" + userPid,
			type : "GET",
			success : function(userRestrictedAttendanceSubgroups) {
				if (userRestrictedAttendanceSubgroups) {
					$.each(userRestrictedAttendanceSubgroups, function(index, userRestrictedAttendanceSubgroup) {
						    $("#documentsCheckboxes input:checkbox[value="+ userRestrictedAttendanceSubgroup.attendanceSubgroupId + "]").prop("checked",true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	}

	UserRestrictedAttendanceSubgroup.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserRestrictedAttendanceSubgroup(id);
				break;
			}
		}
		el.modal('show');
	}

	UserRestrictedAttendanceSubgroup.closeModalPopup = function(el) {
		el.modal('hide');
	}


	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userRestrictedAttendanceSubgroupContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = userRestrictedAttendanceSubgroupContextPath;
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		userRestrictedAttendanceSubgroupModel.pid = null; // reset userRestrictedAttendanceSubgroup model;
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