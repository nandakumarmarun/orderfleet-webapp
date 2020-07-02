if (!this.EditedAccountProfile) {
	this.EditedAccountProfile = {};
}

(function() {
	'use strict';

	var editedAccountProfileContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	

	$(document).ready(function() {
		
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		EditedAccountProfile.filter();

		$('#btnDownload')
			.on(
				'click',
				function() {
					var tblEditedAccountProfile = $("#tblEditedAccountProfile tbody");
					if (tblEditedAccountProfile
							.children().length == 0) {
						alert("no values available");
						return;
					}
					if (tblEditedAccountProfile[0].textContent == "No data available") {
						alert("no values available");
						return;
					}

					downloadXls();
					
				});
		
		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

	});

	function downloadXls() {
		// Avoid last column in each row
		$("#tblEditedAccountProfile th:last-child, #tblEditedAccountProfile td:last-child").hide();
		var excelName = "editedAccountProfile";
		var table2excel = new Table2Excel();
		    table2excel.export(document.getElementById('tblEditedAccountProfile'),excelName);
		$("#tblEditedAccountProfile th:last-child, #tblEditedAccountProfile td:last-child").show();
	}

	function searchTable(inputVal) {
		var table = $('#tBodyEditedAccountProfile');
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


	function editAccountProfile(id) {
		$.ajax({
			url : editedAccountProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').text(data.name);
				$('#field_alias').text(data.alias);
				$('#field_accountType').text(data.accountTypeName);
				$('#field_priceLevel').text(data.defaultPriceLevelName);
				$('#field_city').text(data.city);
				$('#field_location').text(data.location);
				$('#field_pin').text(data.pin);
				$('#field_phone1').text(data.phone1);
				$('#field_phone2').text(data.phone2);
				$('#field_email1').text(data.email1);
				$('#field_email2').text(data.email2);
				$('#field_whatsAppNo').text(data.whatsAppNo);
				$('#field_address').text(data.address);
				$('#field_description').text(data.description);
				$('#field_creditDays').text(data.creditDays);
				$('#field_creditLimit').text(data.creditLimit);
				$('#field_contactPerson').text(data.contactPerson);
				$('#field_defaultDiscountPercentage').text(data.defaultDiscountPercentage);
				$('#field_closingBalance').text(data.closingBalance);
				// set pid
				accountProfileModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	
	function createtableData(accountProfiles){
	
		
		if (accountProfiles.length == 0) {
			$('#tBodyEditedAccountProfile')
				.html(
					"<tr><td colspan='13' align='center'>No data available</td></tr>");
			return;
		}
		$('#tBodyEditedAccountProfile').html("");
		$
			.each(
				accountProfiles,
				function(index, accountProfile) {
					$('#tBodyEditedAccountProfile')
						.append(
							"<tr><td>"
							+ accountProfile.name
							+ "</td><td>"
							+ accountProfile.userName
							+ "</td><td>"
							+ convertDateTimeFromServer(accountProfile.lastModifiedDate)
							+"</td><td>"
							+ accountProfile.dataSourceType
							+ "</td><td>"
							+ accountProfile.alias
							+ "</td><td>"
							+ accountProfile.address
							+ "</td><td>"
							+ accountProfile.accountTypeName
							+ "</td><td>"
							+ accountProfile.phone1
							+ "</td><td>"
							+ accountProfile.email1
							+ "</td><td>"
							+ accountProfile.city
							+ "</td><td>"
							+ accountProfile.location
							+ "</td><td>"
							+ convertDateTimeFromServer(accountProfile.createdDate)
							+ "</td><td>"
							+ (accountProfile.tinNo==null ? "" : accountProfile.tinNo)
							+ "</td><td>"
							+ accountProfile.activated
							+ "</td><td>"
							+"<button class='btn btn-blue' onclick='EditedAccountProfile.showModalPopup($(\"#myModal\"),\""
													+ accountProfile.pid
													+ "\",1);'>View Details</button>"
							+ "</td></tr>");
					
					
				});
	}
	
	
	EditedAccountProfile.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				showAccountProfile(id);
				break;
			case 1:
				editAccountProfile(id);
				break;
			case 2:
				deleteForm.attr('action', accountProfileContextPath + "/" + id);
				break;
			case 3:
				showAccountProfileLocation(id);
				break;
			}
		}
		el.modal('show');
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = editedAccountProfileContextPath;
	}

	
	EditedAccountProfile.filter = function() {
	 
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$('#tBodyEditedAccountProfile').html(
			"<tr><td colspan='13' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : editedAccountProfileContextPath + "/filter",
				type : 'GET',
				data : {
					employeePid : $("#dbEmployee").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val(),
				},
				success : function(accountProfiles) {
					createtableData(accountProfiles);
				}
			});
 }

	EditedAccountProfile.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}

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
	
	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
})();