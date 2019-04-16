// Create a RealTimeAPI object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.RealTimeAPI) {
	this.RealTimeAPI = {};
}

(function() {
	'use strict';

	var realTimeAPIContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#realTimeAPIForm");
	var deleteForm = $("#deleteForm");
	var realTimeAPIModel = {
		id : null,
		name : null,
		api : null,
		version : null,
		companyPid : null,
		companyname : null,
		activated : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		api : {
			required : true,
			maxlength : 255
		},
		version : {
			required : true,
			maxlength : 55
		},
		companyPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		api : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document).ready(
			function() {
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateRealTimeAPI(form);
					}
				});

				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteRealTimeAPI(e.currentTarget.action);
				});

				$('#btnActivateRealTimeAPI').on('click', function() {
					activateAssignedRealTimeAPI();
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function activateAssignedRealTimeAPI() {
		$(".error-msg").html("");
		var selectedRealTimeAPI = "";

		$.each($("input[name='realTimeAPI']:checked"), function() {
			selectedRealTimeAPI += $(this).val() + ",";
		});

		if (selectedRealTimeAPI == "") {
			$(".error-msg").html("Please select RealTimeAPI");
			return;
		}
		$.ajax({
			url : realTimeAPIContextPath + "/activateRealTimeAPI",
			type : "POST",
			data : {
				realTimeAPIs : selectedRealTimeAPI,
			},
			success : function(status) {
				$("#enableRealTimeAPIModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function createUpdateRealTimeAPI(el) {

		realTimeAPIModel.name = $('#field_name').val();
		realTimeAPIModel.api = $('#field_api').val();
		realTimeAPIModel.version = $('#field_version').val();
		realTimeAPIModel.companyPid = $("#field_company").val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(realTimeAPIModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showRealTimeAPI(id) {
		$.ajax({
			url : realTimeAPIContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_api').text((data.api == null ? "" : data.api));
				$('#lbl_version').text(
						(data.version == null ? "" : data.version));
				$("#field_company").val(data.companyPid);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editRealTimeAPI(id) {
		$.ajax({
			url : realTimeAPIContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_api').val((data.api == null ? "" : data.api));
				$('#field_version').val(
						(data.version == null ? "" : data.version));
				$("#field_company").val(data.companyPid);
				// set id
				realTimeAPIModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteRealTimeAPI(actionurl) {
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

	RealTimeAPI.setActive = function(name, id, active) {
		realTimeAPIModel.id = id;
		realTimeAPIModel.activated = active;
		realTimeAPIModel.name = name;
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : realTimeAPIContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(realTimeAPIModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}

	RealTimeAPI.filter = function() {
		$
				.ajax({
					url : realTimeAPIContextPath + "/filter",
					method : 'GET',
					data : {
						companyPid : $('#dbCompany').val()
					},
					success : function(realTimeAPIs) {
						$('#tbodyTableRealTimeAPI').html("");
						if (realTimeAPIs.length == 0) {
							$('#tbodyTableRealTimeAPI')
									.html(
											"<tr><td colspan='6' align='center'>No data available</td></tr>");
							return;
						}
						$.each(realTimeAPIs, function(index, realTimeAPI) {
							$('#tbodyTableRealTimeAPI')
							.append(
									"<tr><td>"
											+ realTimeAPI.companyName
											+ "</td><td>"
											+ realTimeAPI.name
											+ "</td><td>"
											+ realTimeAPI.api
											+ "</td><td>"
											+ realTimeAPI.version
											+ "</td><td>"
											+ toSetActiveOrDeactive(realTimeAPI)
											+ "</td><td>"
											+ "<button type='button' class='btn btn-blue' onclick='RealTimeAPI.showModalPopup($(\"#viewModal\"),\""
											+ realTimeAPI.id
											+ "\",0);'>View</button>&nbsp;" 
											+"<button type='button' class='btn btn-blue' onclick='RealTimeAPI.showModalPopup($(\"#myModal\"),\""
											+ realTimeAPI.id
											+ "\",1);'>Edit</button>&nbsp;"
											+"<button type='button' class='btn btn-danger' onclick='RealTimeAPI.showModalPopup($(\"#deleteModal\"),\""
											+ realTimeAPI.id
											+ "\",2);'>Delete</button>" 
											+"</td></tr>");
			
						});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = realTimeAPIContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = realTimeAPIContextPath;
	}

	function toSetActiveOrDeactive(realTimeAPI){
		var activate = realTimeAPI.activated ? 'Activated' : 'Deactivated';
		var lbl_color = realTimeAPI.activated? 'label-success':'label-danger';
		var span = "<span class='label "+lbl_color+"' onclick='RealTimeAPI.setActive(\""+realTimeAPI.name+"\","+realTimeAPI.id+","+!realTimeAPI.activated+")' style='cursor: pointer;' >"+activate+"</span>";
		return span;
	}
	RealTimeAPI.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showRealTimeAPI(id);
				break;
			case 1:
				editRealTimeAPI(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', realTimeAPIContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	RealTimeAPI.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		realTimeAPIModel.id = null; // reset realTimeAPI model;
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