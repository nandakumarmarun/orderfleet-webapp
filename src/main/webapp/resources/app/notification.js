// Create a Notification object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Notification) {
	this.Notification = {};
}

(function() {
	'use strict';
	var notificationContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {

		$('#sendNotification').on('click', function() {
			sendNotification();
		});

		$('#checkAll').on('click', function() {
			onSelectAllExecutive(this);
		});

		$('#chkImportant').change(function() {
			important(this.checked);
		});

	});

	function sendNotification() {

		var resendTime = "";
		var isImportant = $("#chkImportant").is(":checked");
		if (isImportant == true) {
			resendTime = $("#txtTime").val();
		} else {
			resendTime = 0;
		}

		$("#modalStatus").modal("show");
		$('#divMsg').show();
		$('#divMsg').html("Please wait...");
		$('#tblStatus').hide();

		if ($('#txtMsg').val() == "") {
			$('#divMsg').html('Please enter message');
			return;
		}

		if ($('#txtTime').val() == "") {
			$('#divMsg').html('Please set resend time');
			return;
		}
		
		var selectedUsers = [];
		$.each($("input[name='check-one']:checked"), function() {
			var v = $(this).val();
			selectedUsers.push(v);
		});

		if (selectedUsers == "") {
			$('#divMsg').html('Please select executives');
			return;
		}
		let notification = {
			title :  $('#msgTitle').val(),
			message : $('#txtMsg').val(),
			users : selectedUsers,
			isImportant : isImportant,
			resendTime : resendTime
		}

		$.ajax({
			url : notificationContextPath,
			type : 'POST',
			data : JSON.stringify(notification),
			contentType : "application/json",
			success : function(status) {
				$('#tdTotal').text(status.total);
				$('#tdSuccess').text(status.success);
				$('#tdFailed').text(status.failed);
				$('#divMsg').hide();
				$('#tblStatus').show();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	// check all executive
	function onSelectAllExecutive(checkbox) {
		if (checkbox.checked) {
			$('.check-one').prop('checked', true);
		} else {
			$('.check-one').prop('checked', false);
		}
	}

	function textKeyUp(obj) {
		if (/\D/g.test(obj.value)) {
			// Filter non-digits from input value.
			obj.value = obj.value.replace(/\D/g, '');
		}
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

function important(obj) {
	if (obj == true) {
		$("#divImp")
				.html(
						"<input type='checkbox' style='height: 20px; width: 20px' id='chkImportant' onchange='important(this.checked)' checked> <label style='font-size: 15px; font: bold;'>Important</label> &nbsp;&nbsp; <label style='font-size: 15px; font: bold;'>Set ResendTime:</label> <input type='number' id='txtTime' maxlength='2' size='10'style='width: 50px' > <label style='font-size: 13px; color: black'>Minutes</label>");
	} else {

		$("#divImp")
				.html(
						"<input type='checkbox' style='height: 20px; width: 20px' id='chkImportant' onchange='important(this.checked)'> <label style='font-size: 15px; font: bold;'>Important</label>");
	}
}