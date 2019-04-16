// Create a Knowledgebase object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Knowledgebase) {
	this.Knowledgebase = {};
}

(function() {
	'use strict';

	var knowledgebaseContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#knowledgebaseForm");
	var deleteForm = $("#deleteForm");
	var knowledgebaseModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		productGroupPid : null,
		productGroupName : null
	
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
		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateKnowledgebase(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteKnowledgebase(e.currentTarget.action);
		});
		
		$('#btnActivateKnowledgebase').on('click', function() {
			activateAssignedKnowledgebase();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateAssignedKnowledgebase() {
		$(".error-msg").html("");
		var selectedKnowledgebase = "";

		$.each($("input[name='knowledgebase']:checked"), function() {
			selectedKnowledgebase += $(this).val() + ",";
		});

		if (selectedKnowledgebase == "") {
			$(".error-msg").html("Please select Knowledgebase");
			return;
		}
		$.ajax({
			url : knowledgebaseContextPath + "/activateKnowledgebase",
			type : "POST",
			data : {
				knowledgebases : selectedKnowledgebase,
			},
			success : function(status) {
				$("#enableKnowledgebaseModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateKnowledgebase(el) {
		knowledgebaseModel.name = $('#field_name').val();
		knowledgebaseModel.alias = $('#field_alias').val();
		knowledgebaseModel.description = $('#field_description').val();
		knowledgebaseModel.productGroupPid = $('#field_product_group').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(knowledgebaseModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showKnowledgebase(id) {
		$.ajax({
			url : knowledgebaseContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text((data.description == null ? "" : data.description));
				$('#lbl_productGroup').text((data.productGroupName == null ? "" : data.productGroupName));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editKnowledgebase(id) {
		$.ajax({
			url : knowledgebaseContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				$('#field_product_group').val(data.productGroupPid);

				// set pid
				knowledgebaseModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteKnowledgebase(actionurl, id) {
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

	Knowledgebase.setActive=function(name,pid,active){
		knowledgebaseModel.pid=pid;
		knowledgebaseModel.activated=active;
		knowledgebaseModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:knowledgebaseContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(knowledgebaseModel),
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
		window.location = knowledgebaseContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = knowledgebaseContextPath;
	}

	Knowledgebase.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showKnowledgebase(id);
				break;
			case 1:
				editKnowledgebase(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', knowledgebaseContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	Knowledgebase.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		knowledgebaseModel.pid = null; // reset knowledgebase model;
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