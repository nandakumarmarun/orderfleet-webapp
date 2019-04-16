// Create a DocumentPrint object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DocumentPrint) {
	this.DocumentPrint = {};
}

(function() {
	'use strict';

	var documentPrintContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var documentPrintModel = {
		userPid : null
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveActivity').on('click', function() {
			saveAssignedActivities();
		});

		$('#field_activity').on('change', function() {
			getdocuments();
		});

	});

	function saveAssignedActivities() {

		var activityPid = $('#field_activity').val();
		var userPid = documentPrintModel.userPid;
		var printEnableDocuments = "";
		$.each($("input[name='chk_documents']:checked"), function() {
			printEnableDocuments += $(this).val() + ",";
		});
		
		if(activityPid=="-1"){
			alert("please select activity");
			return false;
		}
		

		$.ajax({
			url : documentPrintContextPath,
			type : "POST",
			data : {
				userPid : userPid,
				activityPid : activityPid,
				printEnableDocuments : printEnableDocuments
			},
			success : function(status) {
				$("#enableDivisionModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function getdocuments() {
		var activityPid = $('#field_activity').val();
		// clear all check box
		$
				.ajax({
					url : documentPrintContextPath + "/getDocuments/"
							+ activityPid,
					type : "GET",
					data : {
						userPid : documentPrintModel.userPid,
					},
					success : function(documents) {
						var documentValues = "";
						$.each(documents.allDocuments, function(key, allDoc) {
							var chkbox="<input type='checkbox' name='chk_documents' value='"+ allDoc.pid + "' id='"+ allDoc.pid+ "'/>";
							$.each(documents.trueDocuments,function(index, trueDoc) {
								if(allDoc.pid==trueDoc.pid){
									chkbox= "<input type='checkbox' name='chk_documents' value='"+ trueDoc.pid + "' id='"+ trueDoc.pid+ "'checked/>";
								}});
							documentValues += "<tr><td>"+ allDoc.name+ "</td><td>"+ chkbox+ "</td></tr>";
						});
						$('#documents').html(documentValues);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function loadDocumentPrint(userPid) {
		documentPrintModel.userPid = userPid;
		$('#field_activity').html('');
		$.ajax({
			url : documentPrintContextPath + "/getActivities/" + userPid,
			type : "GET",
			success : function(activities) {
				if (activities) {
					var activityhtml = "";
					$.each(activities, function(index, activity) {
						activityhtml += '<option value=' + activity.pid + '>'
								+ activity.name + '</option>';
					});
					$('#field_activity').append(
							'<option value="-1">select activity</option>'
									+ activityhtml + '');
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = documentPrintContextPath;
	}

	DocumentPrint.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadDocumentPrint(id);
				break;
			}
		}
		el.modal('show');
	}

	DocumentPrint.closeModalPopup = function(el) {
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