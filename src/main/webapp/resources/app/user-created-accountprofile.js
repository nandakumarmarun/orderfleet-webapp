if (!this.UserCreatedAccountProfile) {
	this.UserCreatedAccountProfile = {};
}

(function() {
	'use strict';

	var userCreatedAccountProfileContextPath = location.protocol + '//' + location.host
	+ location.pathname;

	$(document).ready(function() {
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		UserCreatedAccountProfile.filter();

		$('input:checkbox.allcheckbox').click(
			function() {
				$(this).closest('table').find(
					'tbody tr td input[type="checkbox"]:visible').prop(
					'checked', $(this).prop('checked'));
			});
		$('#btnDownload')
			.on(
				'click',
				function() {
					var tblUserCreatedAccountProfile = $("#tblUserCreatedAccountProfile tbody");
					if (tblUserCreatedAccountProfile
							.children().length == 0) {
						alert("no values available");
						return;
					}
					if (tblUserCreatedAccountProfile[0].textContent == "No data available") {
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
		$("#tblUserCreatedAccountProfile th:last-child, #tblUserCreatedAccountProfile td:last-child").hide();
		var excelName = "userCreatedAccountProfile";
		var table2excel = new Table2Excel();
		    table2excel.export(document.getElementById('tblUserCreatedAccountProfile'),excelName);
		$("#tblUserCreatedAccountProfile th:last-child, #tblUserCreatedAccountProfile td:last-child").show();
	}

	function searchTable(inputVal) {
		var table = $('#tBodyUserCreatedAccountProfile');
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
	
	function createtableData(accountProfiles){
	
		$('#tBodyUserCreatedAccountProfile')
		.html(
			"<tr><td colspan='13' align='center'>please wait...</td></tr>");
		if (accountProfiles.length == 0) {
			$('#tBodyUserCreatedAccountProfile')
				.html(
					"<tr><td colspan='13' align='center'>No data available</td></tr>");
			return;
		}
		$('#tBodyUserCreatedAccountProfile').html("");
		$
			.each(
				accountProfiles,
				function(index, accountProfile) {
					$('#tBodyUserCreatedAccountProfile')
						.append(
							"<tr><td>"
							+ accountProfile.userName
							+ "</td><td>"
							+ accountProfile.name
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
							+ "</td></tr>");
				});
	}

	UserCreatedAccountProfile.filterByAccountType = function() {
		var accountTypePids = [];
		var value = "";
		console.log(2);
		$("#paccountType").find('input[type="checkbox"]:checked').each(function() {
			accountTypePids.push($(this).val());
		});
		$('#tBodyUserCreatedAccountProfile').html(
			"<tr><td colspan='13' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : userCreatedAccountProfileContextPath + "/filterByAccountType",
				method : 'GET',
				data : {
					accountTypePids : accountTypePids.join(","),
					includeInActive : value,
				},

				success : function(accountProfiles) {
					createtableData(accountProfiles);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
	}
	
	
 UserCreatedAccountProfile.filter = function() {
	 
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
		$('#tBodyUserCreatedAccountProfile').html(
			"<tr><td colspan='13' align='center'>Please wait...</td></tr>");
		$
			.ajax({
				url : userCreatedAccountProfileContextPath + "/filter",
				type : 'GET',
				data : {
					employeePid : $("#dbEmployee").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val(),
					active : $('#includeInactive').prop('checked')
				},
				success : function(accountProfiles) {
					createtableData(accountProfiles);
				}
			});
 }

	UserCreatedAccountProfile.showDatePicker = function() {
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