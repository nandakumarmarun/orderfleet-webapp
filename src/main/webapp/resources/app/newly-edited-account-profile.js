if (!this.NewlyEditedAccountProfile) {
	this.NewlyEditedAccountProfile = {};
}

(function() {
	'use strict';

	var newlyEditedAccountProfileContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	

	$(document).ready(function() {
		
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});
		
		NewlyEditedAccountProfile.filter();

		$('#btnDownload').on('click',function() {
			var tblAccountProfile = $("#tblEditedAccountProfile tbody");

			if (tblAccountProfile
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblAccountProfile[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			
			var data = [];
			var i = 0;
			$('#tblEditedAccountProfile').find('.userName').each(function(){
				 data[i] = $(this).html();
				$(this).html(data[i].replace(" ",""));
				i++;
			});
			
			downloadXls();
			
			i = 0;
			$('#tblEditedAccountProfile').find('.userName').each(function(){
				$(this).html(data[i]);
				i++;
			});
			
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
					
					var tdNewlyEditedAlias=""; var tdNewlyEditedTinNo="";var tdNewlyEditedDescription="";var tdNewlyEditedAddress="";var tdNewlyEditedCity="";
					var tdNewlyEditedPin="";var tdNewlyEditedPhone1="";var tdNewlyEditedEmail1="";var tdNewlyEditedEmail2="";var tdNewlyEditedContactPerson="";
					
					if(accountProfile.newlyEditedTinNo != "-"){
						tdNewlyEditedAlias="yellow";
					}
					
					if(accountProfile.newlyEditedDescription != "-"){
						tdNewlyEditedDescription="yellow";
					}
					
					if(accountProfile.newlyEditedDescription != "-"){
						tdNewlyEditedDescription="yellow";
					}
					
					if(accountProfile.newlyEditedAlias != "-"){
						tdNewlyEditedAlias="yellow";
					}
					
					if(accountProfile.newlyEditedAddress != "-"){
						tdNewlyEditedAddress="yellow";
					}
					
					if(accountProfile.newlyEditedPin != "-"){
						tdNewlyEditedPin="yellow";
					}
					
					if(accountProfile.newlyEditedCity != "-"){
						tdNewlyEditedCity="yellow";
					}
					
					if(accountProfile.newlyEditedPhone1 != "-"){
						tdNewlyEditedPhone1="yellow";
					}
					
					if(accountProfile.newlyEditedEmail1 != "-"){
						tdNewlyEditedEmail1="yellow";
					}
					
					if(accountProfile.newlyEditedEmail2 != "-"){
						tdNewlyEditedEmail2="yellow";
					}
					
					if(accountProfile.newlyEditedContactPerson != "-"){
						tdNewlyEditedContactPerson="yellow";
					}
	
					$('#tBodyEditedAccountProfile')
						.append(
							"<tr><td style='background-color:'>"
							+ accountProfile.name
							+ "</td><td style='background-color:'>"
							+ accountProfile.employeeName
							+ "</td><td style='background-color:'>"
							+ convertDateTimeFromServer(accountProfile.newlyEditedCreatedDate)
							+"</td><td  style='background-color:'>"
							+ accountProfile.address
							+ "</td><td style='background-color:"+tdNewlyEditedAddress+"'>"
							+ accountProfile.newlyEditedAddress
							+ "</td><td style='background-color:'>"
							+ accountProfile.city
							+ "</td><td style='background-color:"+tdNewlyEditedCity+"'>"
							+ accountProfile.newlyEditedCity
							+ "</td><td style='background-color:'>"
							+ accountProfile.pin
							+ "</td><td style='background-color:"+tdNewlyEditedPin+"'>"
							+ accountProfile.newlyEditedPin
							+ "</td><td style='background-color:'>"
							+ accountProfile.phone1
							+ "</td><td style='background-color:"+tdNewlyEditedPhone1+"'>"
							+ accountProfile.newlyEditedPhone1
							+ "</td><td style='background-color:'>"
							+ accountProfile.email1
							+ "</td><td style='background-color:"+tdNewlyEditedEmail1+"'>"
							+ accountProfile.newlyEditedEmail1
							+ "</td><td style='background-color:'>"
							+ accountProfile.email2
							+ "</td><td style='background-color:"+tdNewlyEditedEmail2+"'>"
							+ accountProfile.newlyEditedEmail2
							+ "</td><td style='background-color:'>"
							+ accountProfile.alias
							+ "</td><td style='background-color:"+tdNewlyEditedAlias+"'>"
							+ accountProfile.newlyEditedAlias
							+ "</td><td style='background-color:'>"
							+ accountProfile.tinNo
							+ "</td><td style='background-color:"+tdNewlyEditedTinNo+"'>"
							+ accountProfile.newlyEditedTinNo
							+ "</td><td style='background-color:'>"
							+ accountProfile.description
							+ "</td><td style='background-color:"+tdNewlyEditedDescription+"'>"
							+ accountProfile.newlyEditedDescription
							+ "</td><td style='background-color:'>"
							+ accountProfile.contactPerson
							+ "</td><td style='background-color:"+tdNewlyEditedContactPerson+"'>"
							+ accountProfile.newlyEditedContactPerson
							+ "</td><td style='background-color:'>"
							+ accountProfile.accountStatus
							+ "</td><td style='background-color:'>"
							+"<button class='btn btn-success' onclick='NewlyEditedAccountProfile.verifyAccount(\""
															+ accountProfile.newlyEditedPid
															+ "\",\""
															+ accountProfile.accountStatus
															+ "\");'>Verify</button>"
							+ "</td></tr>");
					
					
				});
	}
	
	
	NewlyEditedAccountProfile.showModalPopup = function(el, id, action) {
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
		window.location = newlyEditedAccountProfileContextPath;
	}

	
	NewlyEditedAccountProfile.filter = function() {
	 
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
				url : newlyEditedAccountProfileContextPath + "/filter",
				type : 'GET',
				data : {
					employeePid : $("#dbEmployee").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : $("#txtFromDate").val(),
					toDate : $("#txtToDate").val(),
					accountStatus : $("#dbStatus").val()
				},
				success : function(accountProfiles) {
					createtableData(accountProfiles);
				}
			});
 }
	

	NewlyEditedAccountProfile.verifyAccount = function(accountProfilePid,accountStatus) {
		
		console.log(accountStatus +"------"+ accountProfilePid);

		if (accountStatus == "Unverified") {

			if (confirm("Are you sure?")) {

				$.ajax({
					url : newlyEditedAccountProfileContextPath
							+ "/updateAccountProfileStatus",
					method : 'GET',
					data : {
						pid : accountProfilePid
					},
					beforeSend : function() {
						// Show image container
						$("#loader").modal('show');

					},
					success : function(data) {

						$("#loader").modal('hide');
						NewlyEditedAccountProfile.filter();

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

			}
		} else {
			alert("Account Profile Is Already Verified");
			return;
		}

	}


	NewlyEditedAccountProfile.showDatePicker = function() {
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