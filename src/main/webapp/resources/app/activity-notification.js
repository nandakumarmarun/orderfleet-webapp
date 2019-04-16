if (!this.ActivityNotification) {
	this.ActivityNotification = {};
}

(function() {
	'use strict';
	var activityNotificationid = 0;
	var phoneNumberArrays = [];
	var activityNotificationContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var createEditForm = $("#activityNotificationForm");
	var deleteForm = $("#deleteForm");
	var activityNotificationModel = {
		id : null,
		notificationType : null,
		activityPid : null,
		activityName : null,
		documentName : null,
		documentPid : null,
		sendCustomer : null,
		other : null,
		phoneNumbers : null
	};

	// Specify the validation rules
	var validationRules = {
		activityPid : {
			valueNotEquals : "-1"
		},
		documentPid : {
			valueNotEquals : "-1"
		},
	};

	$(document).ready(function() {
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			submitHandler : function(form) {
				createUpdateActivityNotification(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteActivityNotification(e.currentTarget.action);
		});

		$("#field_activity").change(function() {
			loadDocumentByActivityId($('#field_activity').val());
		});
		$('#btnAddOption').on('click', function() {
			addToOption();
		});

		$('input[type=radio][name=notificationType]').on('change', function() {
			notificationTypeChangeEvent();
		});

	});

	function notificationTypeChangeEvent() {

		var type = $("input[type='radio'][name='notificationType']:checked")
				.val();
		if (type == "EMAIL") {
			$("#lbl_ph_Mail").text("Mail Id");
			$("#field_phoneNumber").attr("placeholder", "mail Id");
		} else {
			$("#lbl_ph_Mail").text("Phone Number");
			$("#field_phoneNumber").attr("placeholder", "phone Number");
		}

		phoneNumberArrays = [];
		createOptionsTableView(phoneNumberArrays);
	}

	function addToOption() {
		var option = $('#field_phoneNumber').val();
		if (option == "") {
			return;
		}

		var selectedNotificationType = $(
				"input[type='radio'][name='notificationType']:checked").val();

		if (selectedNotificationType == null) {
			alert("please select notification type");
			return;
		}

		if (selectedNotificationType == "SMS"
				|| selectedNotificationType == "PUSH") {
			var onlyNumber = option.replace(/[^0-9]/g, '');

			if (!parseInt(option)) {
				alert("not a valid number");
				return;
			} else if (onlyNumber.length != 10) {
				alert('Phone number must be 10 digits.');
				return;
			}

			option = onlyNumber;

		} else if (selectedNotificationType == "EMAIL") {
			var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			var isMail = regex.test(option);
			if (!isMail) {
				alert("please enter valid mail id");
				return;
			}
		}

		remove(phoneNumberArrays, option);
		phoneNumberArrays.push(option);
		createOptionsTableView(phoneNumberArrays);
		$('#field_phoneNumber').val("");
	}

	function createOptionsTableView(options) {
		$('#tblPhoneNumbers').html("");
		$
				.each(
						options,
						function(index, option) {
							$('#tblPhoneNumbers')
									.append(
											"<tr><td>"
													+ option
													+ "</td><td><button type='button' onclick='ActivityNotification.removeOption(this,\""
													+ option
													+ "\")'>&times;</button></td></tr>");
						});
	}

	ActivityNotification.removeOption = function(obj, option) {
		remove(phoneNumberArrays, option);
		$(obj).closest('tr').remove();
	}

	function remove(array, option) {
		for (var i = 0; i < array.length; i++) {
			var value = array[i];
			if (value == option) {
				array.splice(i, 1);
				break;
			}
		}
	}

	function createUpdateActivityNotification(el) {
		var selectedNotificationType = "";

		$.each($("input[name='notificationType']:checked"), function() {
			selectedNotificationType += $(this).val();
		});
		var other = $("#chk_others").is(':checked');

		if (other) {

			var phoneNumbers = "";
			$.each(phoneNumberArrays, function(index, option) {
				phoneNumbers += option + ",";
			});
			activityNotificationModel.phoneNumbers = phoneNumbers.substring(0,
					phoneNumbers.length - 1);

			if (activityNotificationModel.phoneNumbers == "") {
				alert("please add "+$("#lbl_ph_Mail").text());
				return;
			}

		} else {
			activityNotificationModel.phoneNumbers = "";
		}

		activityNotificationModel.id = activityNotificationid;
		activityNotificationModel.notificationType = selectedNotificationType;
		activityNotificationModel.activityPid = $("#field_activity").val();
		activityNotificationModel.documentPid = $("#field_document").val();
		activityNotificationModel.sendCustomer = $("#chk_sendCustomer").is(
				':checked');
		activityNotificationModel.other = other;
		activityNotificationModel.activityName = $(
				"#field_activity option:selected").text();
		activityNotificationModel.documentName = $(
				"#field_document option:selected").text();

		$.ajax({
			type : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(activityNotificationModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editActivityNotification(ids) {
		$('#field_activity').val("-1");
		$('#field_document').val("-1");
		var id = ids.split('_');
		loadDocumentByActivityId(id[0]);

		$.ajax({
			url : activityNotificationContextPath + "/" + id[0],
			method : 'GET',
			data : {
				documentPid : id[1],
				notificationType : id[2],
			},
			success : function(data) {
				$('#field_activity').val(data.activityPid);
				$('#field_document').val(data.documentPid);
				$("[name=notificationType]").val([ data.notificationType ]);
				$("#chk_sendCustomer").prop('checked', data.sendCustomer);
				$("#chk_others").prop('checked', data.other);

				if (data.other) {
					$("#showPhoneDiv").show();
					phoneNumberArrays = data.phoneNumbers.split(',');
					createOptionsTableView(phoneNumberArrays)
				}

				var type = $(
						"input[type='radio'][name='notificationType']:checked")
						.val();
				if (type == "EMAIL") {
					$("#lbl_ph_Mail").text("Mail Id");
					$("#field_phoneNumber").attr("placeholder", "mail Id");
				}

				// set pid
				activityNotificationid = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteActivityNotification(actionurl, id) {
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

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = activityNotificationContextPath;
	}

	function loadDocumentByActivityId(activityPid) {
		$("#field_document").html("<option>Documents loading...</option>")
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/load-documents/" + activityPid,
			type : 'GET',
			async : false,
			success : function(documents) {
				$("#field_document").html(
						"<option value='-1'>Select Document</option>")
				$.each(documents, function(key, document) {
					$("#field_document").append(
							"<option value='" + document.pid + "'>"
									+ document.name + "</option>");

				});
			}
		});
	}

	function showActivityNotification(ids) {
		var id = ids.split('_');

		activityNotificationModel

		$
				.ajax({
					url : activityNotificationContextPath + "/" + id[0],
					method : 'GET',
					data : {
						documentPid : id[1],
						notificationType : id[2],
					},
					success : function(data) {
						$('#lbl_activity').text(data.activityName);
						$('#lbl_document').text(data.documentName);
						$('#lbl_notificationType').text(data.notificationType);
						$('#tb_Options').html("");
						var tRow = "";

						var pNumbers = data.phoneNumbers.split(',');

						$.each(pNumbers, function(index, ph) {
							tRow += ("<tr><td>" + ph + "</td></tr>");
						});
						if (tRow != "") {
							var table = "<thead><tr><th>Options</th></tr></thead><tbody id='tblViewOptions'>"
									+ tRow + "</tbody>";
							$('#tb_Options').append(table);
						}
						// tb_Options
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ActivityNotification.showPhoneNumber = function(obj) {
		var show = $(obj).is(':checked');
		if (show) {
			$("#showPhoneDiv").show();
		} else {
			$("#showPhoneDiv").hide();
		}
	}

	ActivityNotification.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showActivityNotification(id);
				break;
			case 1:
				editActivityNotification(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', activityNotificationContextPath + "/"
						+ id);
				break;
			}
		}
		el.modal('show');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		phoneNumberArrays = [];
		$("#tblPhoneNumbers").html("");
		$("#showPhoneDiv").hide();
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = activityNotificationContextPath;
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