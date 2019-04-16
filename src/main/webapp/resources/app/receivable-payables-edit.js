// Create a ReceivablePayableEdit object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ReceivablePayableEdit) {
	this.ReceivablePayableEdit = {};
}

(function() {
	'use strict';

	var receivablePayableContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#receivablePayablesForm");
	var deleteForm = $("#deleteForm");
	var receivablePayableModel = {
		
		accountPid : null,
		receivablePayableType : null,
		referenceDocumentNumber : null,
		referenceDocumentDate : null,
		referenceDocumentType : null,
		referenceDocumentAmount : null,
		referenceDocumentBalanceAmount : null,
		remarks : null,
		billOverDue : null,
		lastModifiedDate : null,
		closingBalance : null,
		pid : null,
		accountName : null,
		accountType : null,
		accountAddress : null
	};
	
	var validationRules = {
			accountPid : {
				valueNotEquals : "-1"
			},
			receivablePayableType : {
				required : true
			},
			referenceDocumentNumber : {
				required : true
			},
			referenceDocumentDate : {
				required : true
			},
			referenceDocumentAmount : {
				required : true
			},
			referenceDocumentBalanceAmount : {
				required : true
			}
		};

	
	var validationMessages = {
			
			receivablePayableType : {
				required : "This field is required."
			},
			referenceDocumentNumber : {
				required : "This field is required."
			},
			referenceDocumentDate : {
				required : "This field is required."
			},
			referenceDocumentAmount : {
				required : "This field is required."
			},
			referenceDocumentBalanceAmount : {
				required : "This field is required."
			}
		};

	$(document).ready(function() {
		// load data
		ReceivablePayableEdit.loadData();
		$("#voucherDate").datepicker({
			dateFormat : "yy-mm-dd",
			required : true,
		});
		
		$('#field_balanceAmount').focus(function(){
			if($('#field_balanceAmount').val() == 0){
				$('#field_balanceAmount').val($('#field_voucherAmount').val());
			}
		});
		
		$('#myFormUpdate').hide();
		
		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				ReceivablePayableEdit.saveData(form);
			}
		});
		
		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			ReceivablePayableEdit.deleteData(e.currentTarget.action);
		});
	});

	ReceivablePayableEdit.loadData = function() {

		$('#tBodyReceivablePayable').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : receivablePayableContextPath + "/load",
					type : 'GET',
					data : {
						accountPid : $('#dbAccount').val()
					},
					success : function(receivablePayableMap) {
						$('#tBodyReceivablePayable').html("");
						if (jQuery.isEmptyObject(receivablePayableMap)) {
							$('#tBodyReceivablePayable')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										receivablePayableMap,
										function(accountPid, receivablePayables) {
											var receivablePayable1 = receivablePayables[0];
											$('#tBodyReceivablePayable')
													.append(
															"<tr data-toggle='collapse' data-target='#"
																	+ accountPid
																	+ "'><td class='entypo-down-open-mini'>"
																	+ receivablePayable1.accountName
																	+ "</td><td>"
																	+ receivablePayable1.accountType
																	+ "</td><td>"
																	+ receivablePayable1.accountAddress
																	+ "</td><td id='bAmount"
																	+ accountPid
																	+ "' align='right' style='font-weight: bold;'>"
																	+ +"</td></tr>");
											$('#tBodyReceivablePayable')
													.append(
															"<tr class='collapse' id='"
																	+ accountPid
																	+ "'><td colspan='4'><table class='table table-striped table-bordered'><tr><th>Voucher Number</th><th>Voucher Date</th><th>Voucher Amount</th><th>Blanace Amount</th><th>Action</th></tr><tbody id='tblReceivable"
																	+ accountPid
																	+ "'><tr style='background: antiquewhite;font-size: small; font-weight: bold;'><td colspan='2'>Receivables</td><td colspan='2' id='rbAmount"
																	+ accountPid
																	+ "' align='right'></td><td></td></tr></tbody>"
																	+ "<tbody id='tblPayable"
																	+ accountPid
																	+ "'><tr style='background: rgba(180, 232, 168, 0.56);font-size: small; font-weight: bold;'><td colspan='2'>Payable</td><td colspan='2' id='pbAmount"
																	+ accountPid
																	+ "' align='right'></td><td></td></tr></tbody></table></td></tr>");
											var receivablesBlanaceAmount = 0;
											var payableBlanaceAmount = 0;
											$
													.each(
															receivablePayables,
															function(index,
																	receivablePayable) {
																if (receivablePayable.receivablePayableType == "Receivable") {
																	receivablesBlanaceAmount += receivablePayable.referenceDocumentBalanceAmount;
																	$(
																			'#tblReceivable'
																					+ accountPid)
																			.append(
																					"<tr><td>"
																							+ receivablePayable.referenceDocumentNumber
																							+ "</td><td>"
																							+ receivablePayable.referenceDocumentDate
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentAmount
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentBalanceAmount
																							+ "</td><td><i class='btn btn-blue entypo-pencil' style='margin-right:5px' title='Edit' onclick=\"ReceivablePayableEdit.showModalPopup($('#myModal'),'"
																							+ receivablePayable.pid+"',1);\"></i><i class='btn btn-danger entypo-trash' title='Delete' onclick=\"ReceivablePayableEdit.showModalPopup($('#deleteModal'),'"
																							+ receivablePayable.pid+"',2);\"></i></td></tr>");
																} else if (receivablePayable.receivablePayableType == "Payable") {
																	payableBlanaceAmount += receivablePayable.referenceDocumentBalanceAmount;
																	$(
																			'#tblPayable'
																					+ accountPid)
																			.append(
																					"<tr><td>"
																							+ receivablePayable.referenceDocumentNumber
																							+ "</td><td>"
																							+ receivablePayable.referenceDocumentDate
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentAmount
																							+ "</td><td align='right'>"
																							+ receivablePayable.referenceDocumentBalanceAmount
																							+ "</td><td><i class='btn btn-blue entypo-pencil' style='margin-right:5px' title='Edit' onclick=\"ReceivablePayableEdit.showModalPopup($('#myModal'),'"
																							+ receivablePayable.pid+"',1);\"></i><i class='btn btn-danger entypo-trash' title='Delete' onclick=\"ReceivablePayableEdit.showModalPopup($('#deleteModal'),'"
																							+ receivablePayable.pid+"',2);\"></i></td></tr>");
																}
															});
											$('#rbAmount' + accountPid).text(
													receivablesBlanaceAmount);
											$('#pbAmount' + accountPid).text(
													payableBlanaceAmount);
											var blanaceAmount = receivablesBlanaceAmount
													- payableBlanaceAmount;
											$('#bAmount' + accountPid).text(
													blanaceAmount);
										});
					}
				});
	}
	
	ReceivablePayableEdit.saveData = function(form) {
		receivablePayableModel.accountPid = $('#field_account').val();
		receivablePayableModel.accountName = $('#field_account option:selected').html();
		receivablePayableModel.receivablePayableType = $('#field_receivablePayableType').val();
		receivablePayableModel.referenceDocumentNumber = $('#field_voucherNumber').val();
		receivablePayableModel.referenceDocumentDate = $('#voucherDate').val();
		receivablePayableModel.referenceDocumentAmount = $('#field_voucherAmount').val();
		receivablePayableModel.referenceDocumentBalanceAmount = $('#field_balanceAmount').val();
		$
		.ajax({
			url : $(form).attr('action'),
			method : $(form).attr('method'),
			contentType : 'application/json',
			data :JSON.stringify(receivablePayableModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
			});
		
	}
	
	

	
	ReceivablePayableEdit.editData = function(id) {
		$
		.ajax({
			url : receivablePayableContextPath + "/" +id,
			type : 'GET',
			success : function(data) {
				$('#field_account').val(data.accountPid);
				$('#field_receivablePayableType').val(data.receivablePayableType);
				$('#field_voucherNumber').val(data.referenceDocumentNumber);
				$('#voucherDate').val(data.referenceDocumentDate);
				$('#field_voucherAmount').val(data.referenceDocumentAmount);
				$('#field_balanceAmount').val(data.referenceDocumentBalanceAmount);
				receivablePayableModel.pid = id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
	});
	}
	
	ReceivablePayableEdit.deleteData = function(actionurl, id) {
		$
		.ajax({
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
	
	ReceivablePayableEdit.dueDateUpdate = function() {
		$
		.ajax({
			url : receivablePayableContextPath + "/dueUpdate",
			type : 'GET',
			success : function(data) {
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
	});
	}
	
	
	ReceivablePayableEdit.showModalPopup = function(el,id,action) {
		resetForm();
		if(id){
			switch(action){
			case 1:
				ReceivablePayableEdit.editData(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action',receivablePayableContextPath + "/" + id)
				break;
			}
		}
		el.modal('show');
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = receivablePayableContextPath;
	}
	
	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = receivablePayableContextPath;
	}
	
	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		receivablePayableModel.pid = null; // reset accountProfile model;
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