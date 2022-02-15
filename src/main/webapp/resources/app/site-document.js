// Create a Document object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Document) {
	this.Document = {};
}

(function() {
	'use strict';

	var documentContextPath = location.protocol + '//' + location.host
			+ '/web/documents';
	var createEditForm = $("#documentForm");
	var deleteForm = $("#deleteForm");
	var documentModel = {
		pid : null,
		name : null,
		documentPrefix : null,
		alias : null,
		documentType : null,
		activityAccount : null,
		mode : null,
		description : null,
		termsAndConditions : null,
		discountPercentage : null,
		save : true,
		editable : true,
		batchEnabled : false,
		promptStockLocation : false,
		photoMandatory : false,
		isTakeImageFromGallery : false,
		companyPid : null,
		companyName : null,
		stockFlow : null,
		qrCodeEnabled : false,
		orderNoEnabled : false,
		voucherNumberGenerationType : 'TYPE_1',
		addNewCustomer : false,
		termsAndConditionsColumn : false,
		hasTelephonicOrder : false,
		rateWithTax : false,
		discountScaleBar : false,
		headerImage : null,
		headerImageContentType : null,
		footerImage : null,
		footerImageContentType : null
		

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		documentPrefix : {
			required : true,
			maxlength : 10
		},
		alias : {
			maxlength : 55
		},
		documentType : {
			valueNotEquals : "-1"
		},
		activityAccount : {
			valueNotEquals : "-1"
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
		documentPrefix : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 10 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		},
		documentType : {
			valueNotEquals : "This field is required."
		},
		activityAccount : {
			valueNotEquals : "This field is required."
		}
	};

	$(document).ready(
			function() {

				$('.selectpicker').selectpicker();
				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");

				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateDocument(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteDocument(e.currentTarget.action);
				});

				$('#btnSaveAccountTypes').on('click', function() {
					saveAssignedAccountTypes();
				});

				$('#btnSavePriceLevels').on('click', function() {
					saveAssignedPriceLevels();
				});

				$('input[type=radio][name=rdStockCalculation]').change(
						function() {
							$("#txtClosingLogical").attr('checked', false);
							if (this.value == 'Opening') {
								$('#divLogical').hide();
							} else if (this.value == 'Closing') {
								$('#divLogical').show();
							}
						});

				$('#btnStockCalculation').on('click', function() {
					saveStockCalculation();
				});

				// table search
				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				// select all checkbox in table tblPriceLevels
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});

				$('#field_mainCompany').on('change', function() {
					findByCompanyfilfter();
				});
			});

	function loadDocumentTables(documents) {

		$('#documentTbody').html(
				"<tr><td colspan='5' align='center'>Please wait...</td></tr>");

		if (!documents.length > 0) {
			$('#documentTbody')
					.html(
							"<tr><td colspan='5' align='center'>No data available</td></tr>");
			return;
		}

		$('#documentTbody').html("");
		$
				.each(
						documents,
						function(index, document) {

							$('#documentTbody')
									.append(
											"<tr><td title='click to view' class='sa'><span style='color: #2C7BD0; cursor: pointer;' onclick='Document.showModalPopup($(\"#viewModal\"),\""
													+ document.pid
													+ "\",0);'>"
													+ document.name
													+ "</span></td><td>"
													+ (document.alias == null ? ""
															: document.alias)
													+ "</td><td>"
													+ (document.description == null ? ""
															: document.description)
													+ "</td><td>"
													+ document.documentType
													+ "</td><td>"
													+ document.activityAccount
													+ "</td><td><i type='button' class='btn btn-blue entypo-pencil' title='Edit Document' onclick='Document.showModalPopup($(\"#myModal\"),\""
													+ document.pid
													+ "\",1);'></i><i type='button' class='btn btn-danger entypo-trash' title='Delete Document' onclick='Document.showModalPopup($(\"#deleteModal\"),\""
													+ document.pid
													+ "\",2);'></i></td></tr>");

						});
	}

	$('#headerImage').on(
			'change',
			function() {
				var file = $(this)[0].files[0]; // only one file exist
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);

				fileReader.onload = function(e) {
					$('#previewHeaderImage').attr('src', fileReader.result);
					var base64Data = e.target.result.substr(e.target.result
							.indexOf('base64,')
							+ 'base64,'.length);
					documentModel.headerImage = base64Data;
					documentModel.headerImageContentType = file.type;
				};

			});

	$('#footerImage').on(
			'change',
			function() {
				var file = $(this)[0].files[0]; // only one file exist
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);

				fileReader.onload = function(e) {
					$('#previewFooterImage').attr('src', fileReader.result);
					var base64Data = e.target.result.substr(e.target.result
							.indexOf('base64,')
							+ 'base64,'.length);
					documentModel.footerImage = base64Data;
					documentModel.footerImageContentType = file.type;

				};

			});

	function findByCompanyfilfter() {
		var companyPid = $("#field_mainCompany").val();

		if (companyPid == "-1") {
			return;
		}

		$.ajax({
			url : documentContextPath + "/get-by-company-filter/" + companyPid,
			type : "GET",
			success : function(data) {
				loadDocumentTables(data);

			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function searchTable(inputVal) {
		var table = $('#tblPriceLevels');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
					var regExp = new RegExp(inputVal, 'i');
					if (regExp.test($(td).text())) {
						found = true;
						return false;
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function createUpdateDocument(el) {
		$('#previewHeaderImage').attr('src', 'data:image/png;base64,' + null);
		$('#previewFooterImage').attr('src', 'data:image/png;base64,' + null);
		var documentType = $('#field_documentType').val();
		documentModel.name = $('#field_name').val();
		documentModel.documentPrefix = $('#field_documentPrefix').val();
		documentModel.alias = $('#field_alias').val();
		documentModel.description = $('#field_description').val();
		documentModel.termsAndConditions = $('#field_terms_and_conditions')
				.val();
		documentModel.discountPercentage = $('#field_discount_Percentage')
		.val();
		documentModel.documentType = $('#field_documentType').val();
		documentModel.mode = $('#field_paymentMode').val();
		documentModel.stockFlow = $('#field_stockFlow').val();
		documentModel.activityAccount = $('#field_activityAccount').val();
		documentModel.save = $('#field_save').prop('checked');
		documentModel.editable = $('#field_editable').prop('checked');
		documentModel.batchEnabled = $('#field_batchEnabled').prop('checked');
		documentModel.promptStockLocation = $('#field_promptStockLocation')
				.prop('checked');
		documentModel.companyPid = $('#field_company').val();
		if (documentType == "ACCOUNTING_VOUCHER") {
			documentModel.singleVoucherMode = $('#field_singleVoucherMode')
					.prop('checked');
		}
		documentModel.photoMandatory = $('#field_photoMandatory').prop(
				'checked');
		documentModel.isTakeImageFromGallery = $(
				'#field_isTakeImageFromGallery').prop('checked');
		documentModel.qrCodeEnabled = $('#field_qrCodeEnabled').prop('checked');
		documentModel.addNewCustomer = $('#field_addNewCustomer').prop(
				'checked');
		documentModel.termsAndConditionsColumn = $(
				'#field_termsAndConditionColumn').prop('checked');
		documentModel.hasTelephonicOrder = $('#field_hasTelephonicOrder').prop(
				'checked');
		documentModel.rateWithTax = $('#field_rateWithTax').prop('checked');
		documentModel.discountScaleBar = $('#field_discountScaleBar').prop('checked');

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action') + "/save",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(documentModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showDocument(id) {
		$.ajax({
			url : documentContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_documentPrefix').text(data.documentPrefix);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text(
						(data.description == null ? "" : data.description));
				$('#lbl_terms_and_conditions').text(
						(data.termsAndConditions == null ? ""
								: data.termsAndConditions));
				$('#lbl_discount_Percentage').text(
						(data.discountPercentage == null ? ""
								: data.discountPercentage));
				$('#lbl_documentType').text(data.documentType);
				$('#lbl_paymentMode').text(data.mode);
				$('#lbl_activityAccount').text(data.activityAccount);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDocument(id) {
		$('#previewHeaderImage').attr('src', 'data:image/png;base64,' + null);
		$('#previewFooterImage').attr('src', 'data:image/png;base64,' + null);
		$
				.ajax({
					url : documentContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						console.log(data);
						$('#field_name').val(data.name);
						$('#field_documentPrefix').val(data.documentPrefix);
						$('#field_alias').val(
								(data.alias == null ? "" : data.alias));
						$('#field_description').val(
								(data.description == null ? ""
										: data.description));
						$('#field_terms_and_conditions').val(
								(data.termsAndConditions == null ? ""
										: data.termsAndConditions));
						$('#field_discount_Percentage').val(
								(data.discountPercentage == null ? ""
										: data.discountPercentage));
						$('#field_documentType').val(data.documentType);
						$('#field_paymentMode').val(data.mode);
						$('#field_stockFlow').val(data.stockFlow);
						Document.onChangeDocumentType();
						if (data.activityAccount != null)
							$('#field_activityAccount').val(
									data.activityAccount);
						$('#field_save').prop("checked", data.save);
						$('#field_editable').prop("checked", data.editable);
						$('#field_batchEnabled').prop("checked",
								data.batchEnabled);
						$('#field_promptStockLocation').prop("checked",
								data.promptStockLocation);
						$('#field_singleVoucherMode').prop("checked",
								data.singleVoucherMode);
						$('#field_photoMandatory').prop("checked",
								data.photoMandatory);
						$('#field_isTakeImageFromGallery').prop("checked",
								data.isTakeImageFromGallery);
						$("#field_company").val(data.companyPid);

						$("#field_company").prop('disabled', true);
						$("#field_qrCodeEnabled").prop("checked",
								data.qrCodeEnabled);
						$("#field_addNewCustomer").prop("checked",
								data.addNewCustomer);
						$("#field_termsAndConditionColumn").prop("checked",
								data.termsAndConditionsColumn);
						$("#field_hasTelephonicOrder").prop("checked",
								data.hasTelephonicOrder);
						$("#field_rateWithTax").prop("checked",
								data.rateWithTax);
						$("#field_discountScaleBar").prop("checked",
								data.discountScaleBar);
						if (data.headerImage != null) {
							$('#previewHeaderImage')
									.attr(
											'src',
											'data:image/png;base64,'
													+ data.headerImage);
						}

						if (data.footerImage != null) {
							$('#previewFooterImage')
									.attr(
											'src',
											'data:image/png;base64,'
													+ data.footerImage);
						}

						documentModel.pid = data.pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteDocument(actionurl, id) {
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

	Document.onChangeDocumentType = function() {
		var documentType = $('#field_documentType').val();
		$('#field_activityAccount').html("");
		if (documentType == "INVENTORY_VOUCHER") {
			$('#field_activityAccount')
					.append(
							'<option value="-1">Select Activity Account</option><option value="Receiver">Receiver</option><option value="Supplier">Supplier</option>');
			document.getElementById('single').style.display = "none";
		} else if (documentType == "ACCOUNTING_VOUCHER") {
			document.getElementById('single').style.display = "block";
			$('#field_activityAccount')
					.append(
							'<option value="-1">Select Activity Account</option><option value="By">By</option><option value="To">To</option>');
		} else if (documentType == "DYNAMIC_DOCUMENT") {
			$('#field_activityAccount').append(
					'<option value="0">Select Activity Account</option>');
			document.getElementById('single').style.display = "none";
		} else if (documentType == "-1") {
			document.getElementById('single').style.display = "none";
		}
	}
	Document.assignPriceLevels = function(el, pid, type) {
		documentModel.pid = pid;

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#divPriceLevels input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : documentContextPath + "/price-levels",
			type : "GET",
			data : {
				documentPid : documentModel.pid
			},
			success : function(assignedPriceLevels) {
				if (assignedPriceLevels) {
					$.each(assignedPriceLevels, function(index, priceLevel) {
						$(
								"#divPriceLevels input:checkbox[value="
										+ priceLevel.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		el.modal('show');

	}

	function saveAssignedPriceLevels() {

		$(".error-msg").html("");
		var selectedPriceLevels = "";

		$.each($("input[name='priceLevel']:checked"), function() {
			selectedPriceLevels += $(this).val() + ",";
		});

		if (selectedPriceLevels == "") {
			$(".error-msg").html("Please select Price Level");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : documentContextPath + "/assign-price-levels",
			type : "POST",
			data : {
				documentPid : documentModel.pid,
				assignedPriceLevels : selectedPriceLevels
			},
			success : function(status) {
				$("#priceLevelsModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	Document.stockCalculation = function(el, pid) {

		documentModel.pid = pid;

		// clear all check box
		$("#txtClosingLogical").attr('checked', false);
		$("#divRadio input:radio").attr("checked", false);
		$('#divLogical').hide();

		$(".error-msg").html("");

		$.ajax({
			url : documentContextPath + "/stock-calculation/" + pid,
			type : "GET",
			data : {
				documentPid : documentModel.pid
			},
			success : function(documentStockCalculation) {
				if (documentStockCalculation.documentPid) {
					if (documentStockCalculation.closingActual) {
						$("input[value='Closing']").prop('checked', true);
						$('#divLogical').show();
					} else {
						console.log("Opening..........");
						$("input[value='Opening']").prop('checked', true);
					}
					$("#txtClosingLogical").prop('checked',
							documentStockCalculation.closingLogical);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

		el.modal('show');
	}

	function saveStockCalculation() {

		$(".error-msg").html("");
		var value = $("input[name='rdStockCalculation']:checked").val()
		if (value == null) {
			$(".error-msg").html("Please select");
			return;
		}
		var stockCalculation = {
			documentPid : documentModel.pid,
			opening : null,
			closingActual : null,
			closingLogical : true
		};
		if (value == 'Opening') {
			stockCalculation.opening = true;
			stockCalculation.closingActual = false;
			stockCalculation.closingLogical = false;
		} else if (value == 'Closing') {
			stockCalculation.closingActual = true;
			stockCalculation.closingLogical = $('#txtClosingLogical').is(
					":checked");
			stockCalculation.opening = false;
		}

		console.log(stockCalculation);

		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : documentContextPath + "/stock-calculation",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(stockCalculation),
			success : function(status) {
				$("#stockCalculationModal").modal("hide");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	Document.assignAccountTypes = function(el, pid, type) {

		documentModel.pid = pid;
		$('#accountTypeColumn').text(type);
		$('#hdnAccountTypeColumn').val(type);

		// clear all check box
		$("#divAccountTypes input:checkbox").attr('checked', false);
		$(".error-msg").html("");

		$.ajax({
			url : documentContextPath + "/accountTypes",
			type : "GET",
			data : {
				documentPid : documentModel.pid,
				accountTypeColumn : type
			},
			success : function(assignedAccountTypes) {
				if (assignedAccountTypes) {
					$.each(assignedAccountTypes, function(index, accountType) {
						$(
								"#divAccountTypes input:checkbox[value="
										+ accountType.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

		el.modal('show');
	}

	function saveAssignedAccountTypes() {

		$(".error-msg").html("");
		var selectedAccountTypes = "";

		$.each($("input[name='accountType']:checked"), function() {
			selectedAccountTypes += $(this).val() + ",";
		});

		if (selectedAccountTypes == "") {
			$(".error-msg").html("Please select Account Types");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : documentContextPath + "/assignAccountTypes",
			type : "POST",
			data : {
				documentPid : documentModel.pid,
				assignedAccountTypes : selectedAccountTypes,
				accountTypeColumn : $('#hdnAccountTypeColumn').val()
			},
			success : function(status) {
				$("#accountTypesModal").modal("hide");
				// onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		var contextPath = location.protocol + '//' + location.host
				+ '/web/site-documents';
		window.location = contextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		var contextPath = location.protocol + '//' + location.host
				+ '/web/site-documents';
		window.location = contextPath;
	}

	Document.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDocument(id);
				break;
			case 1:
				editDocument(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', documentContextPath + "/" + id);
				break;
			}
		}
		$("#field_company").prop('disabled', false);
		el.modal('show');
	}

	Document.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		documentModel.pid = null; // reset document model;
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