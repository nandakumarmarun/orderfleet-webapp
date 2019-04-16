// Create a UserFavouriteDocument object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.UserFavouriteDocument) {
	this.UserFavouriteDocument = {};
}

(function() {
	'use strict';

	var userFavouriteDocumentContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var userFavouriteDocumentModel = {
		userPid : null
	};

	$(document).ready(function() {

		$('#btnSaveDocument').on('click', function() {
			saveAssignedFavouriteDocuments();
		});
	});

	function loadUserFavoriteDocument(userPid) {
		userFavouriteDocumentModel.userPid = userPid;
		$("#tblDocuments").html('');
		$
				.ajax({
					url : userFavouriteDocumentContextPath + "/activities/"
							+ userPid,
					type : "GET",
					async : false,
					success : function(activityDocuments) {
						if (activityDocuments) {
							$
									.each(
											activityDocuments,
											function(index, activityDocument) {
												$
														.each(
																activityDocument.documents,
																function(
																		index1,
																		document) {
																	$(
																			"#tblDocuments")
																			.append(
																					"<tr><td><input class='"
																							+ activityDocument.pid
																							+ "' name='document' type='checkbox' value='"
																							+ document.pid
																							+ "' /></td><td>"
																							+ activityDocument.name
																							+ " - "
																							+ document.name
																							+ "</td><td><input type='number' class='sortOrder'"
																							+"id='sortOrder"+activityDocument.pid+"-"+ document.pid+"' min='0' max='500'"
																							+"maxlength='3' value='0' /></td></tr>");
																});
											});
						}
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});

		$("#documentsCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : userFavouriteDocumentContextPath + "/" + userPid,
			type : "GET",
			success : function(userFavouriteDocuments) {
				if (userFavouriteDocuments) {
					$.each(userFavouriteDocuments, function(index, userFavouriteDocument) {
						    $("#documentsCheckboxes input:checkbox[value="+ userFavouriteDocument.documentPid + "][class="+ userFavouriteDocument.activityPid + "]").prop("checked",true);
						    $('#sortOrder'+userFavouriteDocument.activityPid+"-"+ userFavouriteDocument.documentPid).val(userFavouriteDocument.sortOrder);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	}

	function saveAssignedFavouriteDocuments() {
		$(".error-msg").html("");
		var selectedDocuments = [];

		$.each($("input[name='document']:checked"), function() {
			var documentPid = $(this).val();
			var activityPid= $(this).attr('class');
		var	sortOrder=$('#sortOrder'+activityPid+"-"+ documentPid).val();
			selectedDocuments.push({
				userPid : userFavouriteDocumentModel.userPid,
				activityPid : activityPid,
				documentPid : documentPid,
				sortOrder:sortOrder
			})
		});

		if (selectedDocuments.length == 0) {
			$(".error-msg").html("Please select Documents");
			return;
		}
		console.log(selectedDocuments);
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : userFavouriteDocumentContextPath + "/save",
			type : "POST",
			data : JSON.stringify(selectedDocuments),
			contentType : "application/json; charset=utf-8",
			success : function(status) {
				$("#documentsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = userFavouriteDocumentContextPath;
	}

	UserFavouriteDocument.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadUserFavoriteDocument(id);
				break;
			}
		}
		el.modal('show');
	}

	UserFavouriteDocument.closeModalPopup = function(el) {
		el.modal('hide');
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