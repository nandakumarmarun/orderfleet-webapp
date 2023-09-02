// Create a Company object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.BillingSetting) {
	this.BillingSetting = {};
}

(function() {
	'use strict';

	var billingSettingPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#companyForm");
	var editForm = $("#editForm");
	var deleteForm = $("#deleteForm");
	var companyModel = {
		pid : null,
		companyPid : null,
		companyName : null,
		noOfMonths : null,
		billingPeriod : null,
		lastBillDate : null,
		dueBillDate : null,
	};

	$(document).ready(function() {

		$("#txtFromDate").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		$("#txtToDate").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		$("#txtFromDates").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		$("#txtToDates").datepicker({
			dateFormat : 'yy-mm-dd'
		});

		createEditForm.validate({
			// rules : validationRules,
			// messages : validationMessages,
			submitHandler : function(form) {
				createCompany(form);
				
			}
		});
		editForm.validate({
			// rules : validationRules,
			// messages : validationMessages,
			submitHandler : function(form) {
				editCompanydetail(form);
			}
		});

	});

	deleteForm.submit(function(e) {
		// prevent Default functionality
		e.preventDefault();
		// pass the action-url of the form
		deleteCompany(e.currentTarget.action);
	});
	$('#btnSearchCompany').click(function() {
		searchTableCompany($("#searchCompany").val());
	});

	$('#btnDownload').on('click', function() {
		var tblCompany = $("#tblCompany tbody");
		if (tblCompany.children().length == 0) {
			alert("no values available");
			return;
		}
		if (tblCompany[0].textContent == "No data available") {
			alert("no values available");
			return;
		}

		downloadXls();
	});

	function downloadXls() {
		console.log("Ready to download")
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tBodyCompany").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('tr').each(function() {
                $(this).find('td:eq(6)').remove();
            });

		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "Billing-Setting";

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename : excelName, // do not include extension
		// fileext : ".xls",
		// exclude_img : true,
		// exclude_links : true,
		// exclude_inputs : true
		});
	}
	function searchTableCompany(inputVal) {
		var table = $('#tBodyCompany');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}

	function createCompany(el) {

		companyModel.companyPid = $("#dbCompany").val();
		companyModel.billingPeriod = $('#field_billingPeriod').val();
		companyModel.noOfMonths = $('#field_noOfMonth').val();
		companyModel.lastBillDate = $("#txtFromDate").val();
       	companyModel.dueBillDate = $("#txtToDate").val();

		$.ajax({
			method : $(el).attr('method'),
			url : billingSettingPath,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(companyModel),
			success : function(data) {
				onSaveSuccess(data)
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editCompany(id) {
		console.log("Enteres")

		$.ajax({
			url : billingSettingPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log("succesfuly get :"+data.companyName)
			
				$('#dbCompanys').val(data.companyName);
				$('#field_billingPeriods').val(data.billingPeriod);
				$('#field_noOfMonths').val(data.noOfMonths);
				$('#txtFromDates').val(data.lastBillDate);
				$('#txtToDates').val(data.dueBillDate);
				
				// set pid
				companyModel.pid = data.pid;
				companyModel.companyPid =data.companyPid;
			console.log(companyModel.companyPid)
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editCompanydetail(el) {
         console.log("Enter to edit create")
         
		companyModel.companyName = $("#dbCompanys").val();
		companyModel.billingPeriod = $('#field_billingPeriods').val();
		companyModel.noOfMonths = $('#field_noOfMonths').val();
		companyModel.lastBillDate = $("#txtFromDates").val();
       	companyModel.dueBillDate = $("#txtToDates").val();

		$.ajax({
			method : $(el).attr('method'),
			url : billingSettingPath,
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(companyModel),
			success : function(data) {
				onSaveSuccess(data)
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = billingSettingPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = billingSettingPath;
	}

	BillingSetting.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showCompany(id);
				break;
			case 1:
				editCompany(id);
				console.log("enter to edit save")
				editForm.attr('method', 'PUT');
				break;

			}
		}
		el.modal('show');
	}

	BillingSetting.changeBillDate= function(pid)
	{
		if(confirm("Are you confirm to change Date")== true)
		{
		$.ajax({
			url : billingSettingPath + "/changeDueBillDate" ,
			method : 'POST',
			data : {
				companyPid : pid
			},
			success : function(data) {
				onSaveSuccess(data)
			}
			});
		}
		
	}
	BillingSetting.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		companyModel.pid = null; // reset company model;

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