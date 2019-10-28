if (!this.UserDeviceKey) {
	this.UserDeviceKey = {};
}
var Users = new Array();
var list = new Array();
(function() {
	'use strict';

	$(document).ready(function() {
		
		$('.selectpicker').selectpicker();
		
		UserDeviceKey.getUserDeviceKey();

		$('#dbCompany').on('change', function() {
			var companyid = $('#dbCompany').val();
			loadUserByCompanyId(companyid);
		});

		$('#btnSearch').click(function() {
			searchTable($("#search").val(), $('#tableUserDeviceKey'));
		});
	});

	var userDeviceKeyContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	function loadUserByCompanyId(companyid) {
		list = [];
		$("#dbUser").html("<option>Users loading...</option>")
		$.ajax({
			url : location.protocol + '//' + location.host + "/web/load-users/"
					+ companyid,
			type : 'GET',
			success : function(users) {
				$("#dbUser").html("<option value='no'>All Users</option>")
				$.each(users, function(key, user) {
					$("#dbUser").append(
							"<option value='" + user.pid + "'>" + user.login
									+ "</option>");
				});
			}
		});
	}

	UserDeviceKey.getUserDeviceKey = function() {
		var companyPid = $('#dbCompany').val();
		var userPid = $('#dbUser').val();
		$('#tableUserDeviceKey').html(
				"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : userDeviceKeyContextPath + "/filter",
					type : "GET",
					data : {
						companyPid : companyPid,
						userPid : userPid
					},
					success : function(users) {
						list = [];
						$('#tableUserDeviceKey').html("");
						if (users.length == 0) {
							$('#tableUserDeviceKey')
									.html(
											"<tr><td colspan='8' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										users,
										function(index, user) {
											list.push(user.pid);
											$('#tableUserDeviceKey')
													.append(
															"<tr><td>"
																	+ user.login
																	+ "</td><td>"
																	+ '<input class="col-xs-3 form-control form-control-lg" style="font-size:20px;"  type="text" id='
																	+ user.pid
																	+ ' value='
																	+ (user.deviceKey == null
																			|| user.deviceKey == "null" ? "deviceKey"
																			: user.deviceKey)
																	+ ' /></td>'
																	+ '<tr>'
																	+ '<input style="display:none;" type="text" value='
																	+ user.pid
																	+ ' />'
																	+ "</tr>");
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});

	}

	UserDeviceKey.saveUserDeviceKey = function() {
		var i = 0;
		for (i = 0; i < list.length; i++) {
			var upid = list[i];
			var deviceKey = $("#" + upid).val();
			Users.push({
				pid : upid,
				deviceKey : deviceKey
			});
		}
		$.ajax({
			url : userDeviceKeyContextPath + "/update",
			contentType : "application/json; charset=utf-8",
			type : 'POST',
			data : JSON.stringify(Users),

			success : function(result) {
				onSaveSuccess(result);
			}
		});

	}

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
		window.location = userDeviceKeyContextPath;
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