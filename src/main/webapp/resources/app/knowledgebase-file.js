// Create a KnowledgebaseFile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.KnowledgebaseFile) {
	this.KnowledgebaseFile = {};
}

(function() {
	'use strict';

	var knowledgebaseFileContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#knowledgebaseFileForm");
	var deleteForm = $("#deleteForm");
	var knowledgebaseFileModel = {
		pid : null,
		fileName : null,
		knowledgebasePid : null,
		knowledgebaseName : null,
		filePid : null,
		searchTags : null
	};

	// Specify the validation rules
	var validationRules = {
		fileName : {
			required : true,
			maxlength : 200
		},
		knowledgebasePid : {
			valueNotEquals : "-1"
		},
		file : {
			required : {
				depends : function() {
					return $('#hdnPid').val() == "";
				}
			}
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		fileName : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 200 characters."
		},
		knowledgebasePid : {
			valueNotEquals : "This field is required."
		},
		file : {
			required : "This field is required."
		},
	};

	var options = {
		beforeSend : function() {
			$("#progressbox").show();
			// clear everything
			$("#progressbar").width('0%');
			$("#message").empty();
			$("#percent").html("0%");

		},
		uploadProgress : function(event, position, total, percentComplete) {
			$("#progressbar").width(percentComplete + '%');
			$("#percent").html(percentComplete + '%');

			// change message text and % to red after 50%
			if (percentComplete > 50) {
				$("#message")
						.html(
								"<font color='red'>File Upload is in progress...</font>");
			}
		},
		success : function(response) {
			$("#progressbar").width('100%');
			$("#percent").html('100%');
			console.log("response");
			console.log(response);
			$("#message").html(
					"<font color='blue'>File has been uploaded!</font>");
			$("#myModal").modal("hide");
			resetForm();
			onSaveSuccess(response);
		},
		complete : function(response) {
			setTimeout(function() {

			}, 1000);
		},
		error : function(xhr, error) {
			onError(xhr, error);
		},
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages
		});

		createEditForm.ajaxForm(options);

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteKnowledgebaseFile(e.currentTarget.action);
		});

		$("#btnSaveUsers").on("click", function(event) {
			saveAssignedUsers();
		});
		
			$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});

	});

	/** serach tags start */
	$(document)
			.ready(
					function() {

						var availableTags = [];

						// get search tags
						$.ajax({
							url : knowledgebaseFileContextPath + "/searchTags",
							method : 'GET',
							success : function(searchTags) {
								availableTags = searchTags;
							},
							error : function(xhr, error) {
								onError(xhr, error);
							}
						});

						function split(val) {
							return val.split(/,\s*/);
						}
						function extractLast(term) {
							return split(term).pop();
						}

						$("#field_searchTags")
								// don't navigate away from the field on tab
								// when selecting an item
								.on(
										"keydown",
										function(event) {
											if (event.keyCode === $.ui.keyCode.TAB
													&& $(this).autocomplete(
															"instance").menu.active) {
												event.preventDefault();
											}
										})
								.autocomplete(
										{
											minLength : 0,
											source : function(request, response) {
												// delegate back to
												// autocomplete, but extract the
												// last term
												response($.ui.autocomplete
														.filter(
																availableTags,
																extractLast(request.term)));
											},
											focus : function() {
												// prevent value inserted on
												// focus
												return false;
											},
											select : function(event, ui) {
												var terms = split(this.value);
												// remove the current input
												terms.pop();
												// add the selected item
												terms.push(ui.item.value);
												// add placeholder to get the
												// comma-and-space at the end
												terms.push("");
												this.value = terms.join(", ");
												return false;
											}
										});
					});
	/** serach tags end */

	function showKnowledgebaseFile(id) {
		$.ajax({
			url : knowledgebaseFileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_knowledgebase').text(data.knowledgebaseName);
				$('#lbl_fileName').text(data.fileName);
				$('#lbl_searchTags').text(data.searchTags);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editKnowledgebaseFile(pid) {
		$('#lblEditFileName').text("");

		$.ajax({
			url : knowledgebaseFileContextPath + "/" + pid,
			method : 'GET',
			success : function(data) {
				$('#hdnPid').val(data.pid);
				$('#field_fileName').val(data.fileName);
				$('#field_knowledgebase').val(data.knowledgebasePid);
				$('#field_searchTags').val(data.searchTags);
				// set pid
				knowledgebaseFileModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteKnowledgebaseFile(actionurl, id) {
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

	function loadUsers(pid) {
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$.ajax({
			url : knowledgebaseFileContextPath + "/users/" + pid,
			type : "GET",
			success : function(users) {
				knowledgebaseFileModel.pid = pid;
				if (users) {
					$.each(users, function(index, user) {
						$("#divUsers input:checkbox[value=" + user.pid + "]").prop("checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function saveAssignedUsers() {

		$(".error-msg").html("");
		var selectedUsers = "";

		$.each($("input[name='user']:checked"), function() {
			selectedUsers += $(this).val() + ",";
		});

		if (selectedUsers == "") {
			$(".error-msg").html("Please select Users");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : knowledgebaseFileContextPath + "/assignUsers",
			type : "POST",
			data : {
				pid : knowledgebaseFileModel.pid,
				assignedUsers : selectedUsers,
			},
			success : function(status) {
				$("#assignUsersModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = knowledgebaseFileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = knowledgebaseFileContextPath;
	}

	KnowledgebaseFile.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showKnowledgebaseFile(id);
				break;
			case 1:
				editKnowledgebaseFile(id);
				// createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', knowledgebaseFileContextPath + "/"
						+ id);
				break;
			case 3:
				loadUsers(id);
				break;
			}
		}
		el.modal('show');
	}

	KnowledgebaseFile.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		// createEditForm.attr('method', 'POST'); // set default method
		knowledgebaseFileModel.pid = null; // reset knowledgebaseFile model;

		$('#hdnPid').val("");
		$("#progressbar").width('0%');
		$("#message").empty();
		$("#percent").html("0%");
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