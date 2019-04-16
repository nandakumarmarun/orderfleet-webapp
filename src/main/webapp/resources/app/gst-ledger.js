// Create a GstLedgerConfig object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.GstLedger) {
	this.GstLedger = {};
}

(function() {
	'use strict';
	
	var ContextPath = location.protocol + '//' + location.host + location.pathname;
	var deleteForm = $("#deleteForm");
	var gstLedgerModel = {
		id : 0,
		name : null,
		taxType : null,
		taxRate : 0,
		accountType : null,
		activated : null
	};
	
	$(document).ready(
			function() {
				
				/*createEditForm.validate({
					submitHandler : function(form) {
						createUpdateGstLedgerConfig(form);
					}
				});*/
				
				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteGstLedger(e.currentTarget.action);
				});
				
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
				
				$('#dbCompany').change(
						function() {
							getAllGstLedgers();
						});
				
			}
	);
	
	function getAllGstLedgers(){
		
		if ($("#dbCompany").val() == -1) {
			alert("Please select company");
			return;
		}
		var companyPid = $('#dbCompany').val();
		$.ajax({
			url : ContextPath + "/" + companyPid,
			type : "GET",
			success : function(data) {
				addTableBodyvalues(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	GstLedger.changeStatus = function (){
		if ($("#dbCompany").val() == -1) {
			alert("Please select company");
			return;
		}
		var gstLedgers = new Array();
		$.each($("input[name='ledger']:checked"), function() {
			gstLedgerModel = {};
			gstLedgerModel.id = parseInt($(this).val());
			gstLedgerModel.activated = true;
			gstLedgers.push(gstLedgerModel);
		});
		$.each($("input[name='ledger']:not(:checked)"), function() {
			gstLedgerModel = {};
			gstLedgerModel.id = parseInt($(this).val());
			gstLedgerModel.activated = false;
			gstLedgers.push(gstLedgerModel);
		});
	var datas = JSON.stringify(gstLedgers);
	$.ajax({
		url : location.protocol + '//' + location.host + "/web/gst-ledger/activation",
		type : "POST",
		contentType : "application/json; charset=utf-8",
		data : datas,
		success : function(status) {
			$('input:checkbox.allcheckbox').removeAttr('checked');
			getAllGstLedgers();
		},
		error : function(xhr, error) {
			onError(xhr, error);
		},
	});
	}
	
	GstLedger.deleteGstLedgerConfig = function (id) {
		$.ajax({
			url : location.protocol + '//' + location.host + "/web/gst-ledger/" + id,
			method : 'DELETE',
			success : function(data) {
				//onDeleteSuccess(data);
				getAllGstLedgers();
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	
	 function addTableBodyvalues(gstLedgers){
		 
		 $('#tBodyGstLedgers').html("");
			if (gstLedgers.length == 0) {
				$('#tBodyGstLedgers').html("<tr><td colspan='7' align='center'>No data available</td></tr>");
				return;
			}
		$('#tBodyGstLedgers').html("");
		var content = "<tr>";
		$.each(gstLedgers, function(index, gstLedger) {
			
			if(gstLedger.activated == true){
				content += "<td><input type='checkbox' name='ledger' value='" +gstLedger.id+ "' checked/></td>";
			} else {
				content += "<td><input type='checkbox' name='ledger' value='" +gstLedger.id+ "'/></td>";
			}
			
			content += "<td>" + gstLedger.name + "</td>";
			content += "<td>" + gstLedger.taxType + "</td>";
			
			if(gstLedger.activated == true){
				content += "<td><span class='label label-success'>Activated</span></td>";
			} else {
				content += "<td><span class='label label-danger'>Deactivated</span></td>";
			}
			
			content += "<td>" + gstLedger.accountType + "</td>";
			content += "<td>" + gstLedger.taxRate + "</td>";
			
			content += "<td><button type='button' class='btn btn-danger' onclick='" 
						+ "GstLedger.deleteGstLedgerConfig(" + gstLedger.id + ");' title='Delete'>Delete"
						+ "</button></td></tr>";
		});
		content += "</tr>";
		$('#tBodyGstLedgers').html(content);
	 }
	
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = ContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		//window.location = ContextPath;
		window.location = ContextPath;
	}

	GstLedger.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showVehicle(id);
				break;
			case 1:
				editGstLedger(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', ContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	GstLedger.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		gstLedgerModel.pid = null; // reset vehicle model;
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