if (!this.UserDiscontinuedStatus) {
	this.UserDiscontinuedStatus = {};
}

(function() {
	'use strict';

	$(document).ready(function() {

		$('#dbCompany').on('change', function() {
			var companyid = $('#dbCompany').val();
			UserDiscontinuedStatus.getUserDiscontinuedStatus();
		});

		$('#btnSearch').click(function() {
			searchTable($("#search").val(), $('#tableUserDiscontinuedStatus'));
		});

	});

	var companyDiscontinuedStatusContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	UserDiscontinuedStatus.release = function(udPid, status, obj) {
		if (confirm("Are you sure!")) {
			$.ajax({
				url : companyDiscontinuedStatusContextPath + "/" + udPid,
				type : 'PUT',
				data : {
					status : status,
				},
				success : function(result) {
					var row = $(obj).closest('tr');
					row.find("td").eq(1)
							.html(
									spanReleasedDevice(result.pid,
											result.discontinued));
					$(obj).hide("slow", function() {
					});

				}
			});
		}
	}

	UserDiscontinuedStatus.getUserDiscontinuedStatus = function() {

		var companyPid = $('#dbCompany').val();
		if (companyPid == "no") {
			var data = null;
			alert("Please Select Company");
			return;
		}

		$('#tableUserDiscontinuedStatus').html(
				"<tr><td colspan='8' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : companyDiscontinuedStatusContextPath + "/getCompanyUsers",
			type : "GET",
			data : {
				companyPid : companyPid,
			},
			success : function(users) {
				addValuesToTable(users)
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});

	}

	function addValuesToTable(users) {
		$('#tableUserDiscontinuedStatus').html("");
		if (users.length == 0) {
			$('#tableUserDiscontinuedStatus')
					.html(
							"<tr><td colspan='8' align='center'>No data available</td></tr>");
			return;
		}
		$.each(users, function(index, user) {
			$('#tableUserDiscontinuedStatus').append(
					"<tr><td>" + user.login + "</td><td>"
							+ spanReleasedDevice(user.pid, user.discontinued)
							+ "</td></tr>");
		});
	}

	function spanReleasedDevice(userPid, discontinued) {

		var classfield = "";
		var title = "";
		var upid = "'" + userPid + "'";
		var status = "'" + !discontinued + "'";

		if (!discontinued) {
			classfield = "btn btn-success";
			title = "Continued"
		} else {
			classfield = "btn btn-danger";
			title = "Discontinued"
		}

		var spanReleased = '<span id="' + userPid + '" class="' + classfield
				+ '"onclick="UserDiscontinuedStatus.release(' + upid + ','
				+ status + ',this)" style="cursor: pointer;">' + title
				+ '</span>';
		return spanReleased;
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
		window.location = companyDiscontinuedStatusContextPath;
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