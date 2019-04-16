// Create a RevokeRootPlan object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.RevokeRootPlan) {
	this.RevokeRootPlan = {};
}
(function() {
	'use strict';

	var revokeRootPlanContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		// table search
		$('#search').keyup(function() {
			searchTable($(this).val());
		});
	});

	RevokeRootPlan.filter = function() {

		$('#tBodyRevokeRootPlan').html(
				"<tr><td colspan='5' align='center'>Please wait...</td></tr>");

		$
				.ajax({
					url : revokeRootPlanContextPath + "/filter",
					type : 'GET',
					data : {
						userPid : $("#dbUser").val()
					},
					success : function(downloadedRootPlans) {

						console.log(downloadedRootPlans);

						$('#tBodyRevokeRootPlan').html("");

						if (downloadedRootPlans.length == 0) {
							$('#tBodyRevokeRootPlan')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										downloadedRootPlans,
										function(index, downloadedRootPlan) {

											$('#tBodyRevokeRootPlan')
													.append(
															"<tr><td>"
																	+ downloadedRootPlan.rootPlanHeaderName
																	+ "</td><td>"
																	+ downloadedRootPlan.taskListName
																	+ "</td><td><button class='btn btn-blue' onclick='RevokeRootPlan.showModalPopup($(\"#taskModal\"),\""
																	+ downloadedRootPlan.taskListPid
																	+ "\",0);'>Assign Task</button></td><td>"
																	+ "<button class='btn btn-green' onclick='RevokeRootPlan.showModalPopup($(\"\"),\""
																	+ downloadedRootPlan.pid
																	+ "\",1);'>Revoke</button></td></tr>");

										});
					}
				});
	}

	RevokeRootPlan.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadAssignTask(id);
				el.modal('show');
				break;
			case 1:
				revokeRoutePlan(id);
				break;
			}
		}
		
	}

	function revokeRoutePlan(pid) {
		if (confirm("Are you confirm?")) {
			$.ajax({
				url : revokeRootPlanContextPath + "/revoke",
				type : 'POST',
				data : {
					detailPid : pid
				},
				success : function(tasks) {
					window.location = revokeRootPlanContextPath;
				},
			});
		}
	}

	function loadAssignTask(pid) {
		$
				.ajax({
					url : revokeRootPlanContextPath + "/loadAssignTask",
					type : 'GET',
					data : {
						taskListPid : pid
					},
					success : function(tasks) {
						console.log(tasks);

						$('#tblAssigntask').html("");

						if (tasks.length == 0) {
							$('#tblAssigntask')
									.html(
											"<tr><td colspan='3' align='center'>No data available</td></tr>");
							return;
						}
						$.each(tasks, function(index, task) {

							$('#tblAssigntask').append(
									"<tr><td>" + task.activityName
											+ "</td><td>"
											+ task.accountTypeName
											+ "</td><td>"
											+ task.accountProfileName
											+ "</td></tr>");

						});
					},
				});
	}

	function searchTable(inputVal) {
		var table = $('#tBodyRevokeRootPlan');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
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

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
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
})();