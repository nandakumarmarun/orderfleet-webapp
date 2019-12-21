if (!this.MobileMasterUpdate) {
	this.MobileMasterUpdate = {};
}

(function() {
	'use strict';

	$(document).ready(function() {

		// loadAllDevices();

		$('#dbCompany').on('change', function() {
			var companyid = $('#dbCompany').val();
			loadUserByCompanyId(companyid);
		});

		$('#btnSearch').click(function() {
			searchTable($("#search").val(), $('#tableMobileMasterUpdate'));
		});

		$('.selectpicker').selectpicker();
	});

	var mobileMasterUpdateContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	function loadUserByCompanyId(companyid) {
		$("#dbUser").html("<option>Users loading...</option>")
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/load-company-users/" + companyid,
			type : 'GET',
			success : function(users) {
				$("#dbUser").html("<option value='no'>Select User</option>")
				$.each(users, function(key, user) {
					$("#dbUser").append(
							"<option value='" + user.pid + "'>" + user.login
									+ "</option>");
				});
			}
		});
	}

	MobileMasterUpdate.getMobileMasterUpdate = function() {
		var companyPid = $('#dbCompany').val();
		var userPid = $('#dbUser').val();
		if (companyPid != "no" && userPid != 'no') {
			$('#tableMobileMasterUpdate')
					.html(
							"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
			$
					.ajax({
						url : mobileMasterUpdateContextPath
								+ "/getUsersMobileMasterUpdate",
						type : "GET",
						data : {
							companyPid : companyPid,
							userPid : userPid
						},
						success : function(mobileMasterDetails) {
							$('#tableMobileMasterUpdate').html("");
							if (mobileMasterDetails.length == 0) {
								$('#tableMobileMasterUpdate')
										.html(
												"<tr><td colspan='8' align='center'>No data available</td></tr>");
								return;
							}

							$("#lblUsername").text(
									mobileMasterDetails[0].userName);
							$("#lblEmployeeName").text(
									mobileMasterDetails[0].employeeName);
							$("#lblUserBuildVersion").text(
									mobileMasterDetails[0].userBuildVersion);
							$("#lblTotalUpdateTime").text(
									mobileMasterDetails[0].totalTime);

							$
									.each(
											mobileMasterDetails,
											function(index, mobileMasterDetail) {
												var date1 = convertDateTimeFromServer(mobileMasterDetail.createdDate);

												$('#tableMobileMasterUpdate')
														.append(
																"<tr><td>"
																		+ mobileMasterDetail.masterItem
																		+ "</td><td>"
																		+ date1
																		+ "</td><td>"
																		+ mobileMasterDetail.count
																		+ "</td><td>"
																		+ mobileMasterDetail.operationTime
																		+ "</td></tr>");
											});
						},
						error : function(xhr, error) {
							onError(xhr, error);
						},
					});
		} else {
			var data = null;
			if (companyPid == "no") {
				alert("Please Select Company");
				return;
			}
			if (userPid == "no") {
				alert("Please Select User");
				return;
			}
		}
	}

	/*
	 * function loadAllDevices() {
	 * 
	 * $('#tableCompanyUserDevice').html( "<tr><td colspan='8' align='center'>Please
	 * wait...</td></tr>"); $ .ajax({ url : mobileMasterUpdateContextPath +
	 * "/listAllDevice", method : 'GET', success : function(companyUserDevices) {
	 * $('#tableCompanyUserDevice').html(""); if (companyUserDevices.length ==
	 * 0) { $('#tableCompanyUserDevice') .html( "<tr><td colspan='8' align='center'>No
	 * data available</td></tr>"); return; } $ .each( companyUserDevices,
	 * function(index, companyUserDevice) { var date1 =
	 * convertDateTimeFromServer(companyUserDevice.createdDate); var date2 =
	 * convertDateTimeFromServer(companyUserDevice.lastModifiedDate);
	 * $('#tableCompanyUserDevice') .append( "<tr><td>" +
	 * companyUserDevice.userLoginName + "</td><td>" +
	 * activated(companyUserDevice.activated) + "</td><td>" + date1 + "</td><td>" +
	 * date2 + "</td><td>" + companyUserDevice.deviceKey + "</td><td>" +
	 * companyUserDevice.lastAcessedDeviceKey + "</td><td>" +
	 * companyUserDevice.lastAcessedLogin + "</td><td>" + spanReleasedDevice(
	 * companyUserDevice.pid, companyUserDevice.activated) + "</td></tr>");
	 * }); }, error : function(xhr, error) { onError(xhr, error); } }); }
	 */

	function searchTable(inputVal, table) {
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = mobileMasterUpdateContextPath;
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

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
})();