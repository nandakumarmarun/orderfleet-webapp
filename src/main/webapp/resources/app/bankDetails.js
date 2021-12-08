// Create a BankDetails object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.BankDetails) {
	this.BankDetails = {};
}

(function() {
	'use strict';

	var bankDetailsContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#bankDetailsForm");
	var deleteForm = $("#deleteForm");
	var bankDetailsModal = {

		bankName : null,
		branch : null,
		accountNumber : null,
		accountHolderName : null,
		ifscCode : null,
		swiftCode : null,
		phoneNumber : null,
		qrImage : null,
		qrImageContentType : null
	};

	// Specify the validation rules
	var validationRules = {
		bankName : {
			required : true,
			maxlength : 255
		},
		branch : {
			required : true,
			maxlength : 255
		},
		accountNumber : {
			required : true,
			maxlength : 255
		},
		accountHolderName : {
			required : true,
			maxlength : 255
		},
		ifscCode : {
			required : true,
			maxlength : 255
		},
		swiftCode : {
			required : true,
			maxlength : 255
		},
		phoneNumber : {
			required : true,
			maxlength : 10
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		bankName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		branch : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		accountNumber : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		accountHolderName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		ifscCode : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		swiftCode : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		phoneNumber : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 10 characters."
		},
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateBankDetails(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteBankDetails(e.currentTarget.action);
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function createUpdateBankDetails(el) {
		bankDetailsModal.bankName = $('#field_bank_name').val();
		bankDetailsModal.branch = $('#field_branch').val();
		bankDetailsModal.accountNumber = $('#field_account_number').val();
		bankDetailsModal.accountHolderName = $('#field_account_holder_name')
				.val();
		bankDetailsModal.ifscCode = $('#field_ifsc_code').val();
		bankDetailsModal.swiftCode = $('#field_swift_code').val();
		bankDetailsModal.phoneNumber = $('#field_phone_number').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(bankDetailsModal),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	$('#qrImage').on(
			'change',
			function() {
				var file = $(this)[0].files[0]; // only one file exist
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);

				fileReader.onload = function(e) {
					$('#previewImage').attr('src', fileReader.result);
					var base64Data = e.target.result.substr(e.target.result
							.indexOf('base64,')
							+ 'base64,'.length);
					bankDetailsModal.qrImage = base64Data;
					bankDetailsModal.qrImageContentType = file.type;
				};

			});

	function showBankDetails(id) {
		$.ajax({
			url : bankDetailsContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_bank_name').text(data.bankName);
				$('#lbl_branch').text(data.branch);
				$('#lbl_account_number').text(data.accountNumber);
				$('#lbl_account_holder_name').text(data.accountHolderName);
				$('#lbl_ifsc_code').text(data.ifscCode);
				$('#lbl_swift_code').text(data.swiftCode);
				$('#lbl_phone_number').text(data.phoneNumber);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editBankDetails(id) {
		$
				.ajax({
					url : bankDetailsContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#field_bank_name').val(data.bankName);
						$('#field_branch').val(
								(data.branch == null ? "" : data.branch));
						$('#field_account_number').val(
								(data.accountNumber == null ? ""
										: data.accountNumber));
						$('#field_account_holder_name').val(
								(data.accountHolderName == null ? ""
										: data.accountHolderName));
						$('#field_ifsc_code').val(
								(data.ifscCode == null ? "" : data.ifscCode));
						$('#field_swift_code').val(
								(data.swiftCode == null ? "" : data.swiftCode));
						$('#field_phone_number').val(
								(data.phoneNumber == null ? ""
										: data.phoneNumber));
						if (data.qrImage != null) {
							$('#previewImage').attr('src',
									'data:image/png;base64,' + data.qrImage);
						}

						// set pid
						bankDetailsModal.pid = data.pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteBankDetails(actionurl, id) {
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

	BankDetails.setActive = function(name, pid, active) {
		bankDetailsModal.pid = pid;
		bankDetailsModal.activated = active;
		bankDetailsModal.name = name;
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : bankDetailsContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(bankDetailsModal),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = bankDetailsContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = bankDetailsContextPath;
	}

	BankDetails.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showBankDetails(id);
				break;
			case 1:
				editBankDetails(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', bankDetailsContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	BankDetails.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		bankDetailsModal.pid = null; // reset bank model;
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