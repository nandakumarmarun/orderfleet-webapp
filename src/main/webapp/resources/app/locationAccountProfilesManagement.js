if (!this.LocationAccountProfilesManagement) {
	this.LocationAccountProfilesManagement = {};
}

(function() {
	'use strict';

	var locationAccountProfileManagementContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	var locationAccountProfilesManagementModel = {
		pid : null,
		name : null,
		locationName : null,

	};

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

	var $row = $('#tBodyAccountProfileDemo tr');
	$("#search").on("keyup", function() {
		var value = $(this).val().toUpperCase();

		$("#tBodyAccountProfileDemo tr").each(function(index) {

			if (index !== 0) {
				$row = $(this);

				var $tdElement = $row.find("td:first");
				var id = $tdElement.text();

				var matchedIndex = id.indexOf(value);

				if (matchedIndex != 0) {
					$row.hide();
				} else {

					$row.show();
				}
			}
		});
	});

	function showAssignedLocation(id) {
		let text;
		if (confirm("Do u want to change location") == true) {
			text = "You pressed YES!";

			var e = document.getElementById("field_location-" + id);
			var strUser = e.value;

			$(".error-msg").html("Please wait.....");
			$.ajax({
				url : locationAccountProfileManagementContextPath + "/assign-locations",
				type : "POST",
				data : {
					locationPid : strUser,
					assignedAccountProfiles : id
				},
				success : function(status) {

					window.location = locationAccountProfileManagementContextPath;
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		} else {
			text = "You canceled!";
		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = locationAccountProfileManagementContextPath;
	}
	LocationAccountProfilesManagement.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showAssignedLocation(id);
				break;
			}
		}
		el.modal('show');
	}

	LocationAccountProfilesManagement.closeModalPopup = function(el) {
		el.modal('hide');
	}
	function resetForm() {
		$('.alert').hide();
		locationAccountProfilesManagementModel.pid = null; // reset department model;
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