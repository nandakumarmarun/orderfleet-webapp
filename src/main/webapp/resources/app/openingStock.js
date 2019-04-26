//.........................JS PAGE.............................

//Create a OpeningStock object only if one does not already exist. We create the
//methods in a closure to avoid creating global variables.

if (!this.OpeningStock) {
	this.OpeningStock = {};
}

(function() {
	'use strict';

	var openingStockContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#openingStockForm");
	var deleteForm = $("#deleteForm");
	var openingStockModel = {
		pid : null,
		productProfilePid : null,
		batchNumber : null,
		stockLocationPid : null,
		quantity : null
		

	};

	// Specify the validation rules
	var validationRules = {

		productProfilePid : {
			valueNotEquals : "-1"
		},
		batchNumber : {
			required : true,
			maxlength : 255
		},
		stockLocationPid : {
			valueNotEquals : "-1"
		},
		quantity : {
			maxlength : 100
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		batchNumber : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},

		quantity : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 100 characters."
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
				createUpdateOpeningStock(form);
			}
		});
		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteOpeningStock(e.currentTarget.action);
		});
		
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		$('#btnActivateOpeningStock').on('click', function() {
			activateAssignedOpeningStock();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
		
	});
	
	$('#btnDownload')
	.on(
			'click',
			function() {
				var tblOpeningStocks = $("#tblOpeningStocks tbody");
				if (tblOpeningStocks
						.children().length == 0) {
					alert("no values available");
					return;
				}
				if (tblOpeningStocks[0].textContent == "No data available") {
					alert("no values available");
					return;
				}

				downloadXls();
				$("#tblOpeningStocks th:last-child, #tblOpeningStocks td:last-child").show();
			});
	
	function searchTable(inputVal) {
		var table = $('#tbodyOpeningStocks');
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
	
	function downloadXls() {
		
		window.location.href = openingStockContextPath
		+ "/download-openingStock-xls";
		
		
//		// Avoid last column in each row
//		$("#tblProductProfile th:last-child, #tblProductProfile td:last-child").hide();
//		var excelName = "productProfile";
//		 var table2excel = new Table2Excel();
//		     table2excel.export(document.getElementById('tblProductProfile'),excelName);
//		     $("#tblProductProfile th:last-child, #tblProductProfile td:last-child").show();
	}
	
	OpeningStock.checkboxChange = function(e) {
		if (e.checked) {
			$('#tbodyOpeningStocks').find(
					'tr:visible td input[type="checkbox"]').each(function() {
				$(this).prop('checked', true);
			});
		} else {
			$('#tbodyOpeningStocks').find(
					'tr:visible td input[type="checkbox"]').each(function() {
				$(this).prop('checked', false);
			});
		}

	}

	function activateAssignedOpeningStock() {
		$(".error-msg").html("");
		var selectedOpeningStock = "";

		$.each($("input[name='openingstock']:checked"), function() {
			selectedOpeningStock += $(this).val() + ",";
		});

		if (selectedOpeningStock == "") {
			$(".error-msg").html("Please select OpeningStock");
			return;
		}
		$.ajax({
			url : openingStockContextPath + "/activateOpeningStock",
			type : "POST",
			data : {
				openingstocks : selectedOpeningStock,
			},
			success : function(status) {
				$("#enableOpeningStockModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateOpeningStock(el) {
		openingStockModel.batchNumber = $('#field_batchNumber').val();
		openingStockModel.stockLocationPid = $('#field_stockLocation').val();
		openingStockModel.productProfilePid = $('#field_productProfile').val();
		openingStockModel.quantity = $('#field_quantity').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(openingStockModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showOpeningStock(id) {
		$.ajax({
			url : openingStockContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				console.log(data);
				$('#lbl_batchNumber').text(data.batchNumber);
				$('#lbl_stockLocation').text(data.stockLocationName);
				$('#lbl_productProfile').text(data.productProfileName);
				$('#lbl_quantity').text(data.quantity);
				var localDate = convertUTCDateToDate(data.openingStockDate);
				$('#lbl_openingStockDate').text(localDate);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editOpeningStock(id) {
		$.ajax({
			url : openingStockContextPath + "/" + id,
			method : 'GET',
			success : function(data) {

				$('#field_batchNumber').val(data.batchNumber);
				$('#field_stockLocation').val(data.stockLocationPid);
				$('#field_productProfile').val(data.productProfilePid);
				$('#field_quantity').val(data.quantity);

				// set pid
				openingStockModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteOpeningStock(actionurl, id) {
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
	OpeningStock.setActive=function(pid,activated){
		openingStockModel.pid=pid;
		openingStockModel.activated=activated;
		
		
		if(confirm("Are you confirm?")){
		$.ajax({
			url:openingStockContextPath+"/changeStatus",
			method:'POST',
			contentType : "application/json; charset=utf-8",
				data:JSON.stringify(openingStockModel),
				success:function(data){
					onSaveSuccess(data)
				},
		
				error:function(xhr,error){
					onError(xhr, error)
				}
		});
	}}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = openingStockContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = openingStockContextPath;
	}

	OpeningStock.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showOpeningStock(id);
				break;
			case 1:
				editOpeningStock(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', openingStockContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	OpeningStock.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		openingStockModel.pid = null; // reset openingStock model;
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

function toTimeZone(time, zone) {
	var format = 'YYYY/MM/DD HH:mm:ss ZZ';
	return moment(time, format).tz(zone).format(format);
}
function convertUTCDateToDate(utcDate) {
	var date = new Date(utcDate)
	var dd = date.getDate();
	var mm = date.getMonth() + 1;
	var yyyy = date.getFullYear();
	if (dd < 10) {
		dd = '0' + dd
	}
	if (mm < 10) {
		mm = '0' + mm
	}
	;
	return utcDate = dd + '-' + mm + '-' + yyyy
}
