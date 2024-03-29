// Create a DashboardUser object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DashboardUser) {
	this.DashboardUser = {};
}

(function() {
	'use strict';

	var dashboardUserContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		$("#btnSaveUsers").click(function() {
			saveAssignedUsers();
		});
	});

	var dashboardUserModel = {
		userPid : null,
		sortOrder : null

	};

	DashboardUser.assignUsers = function() {
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$
				.ajax({
					url : dashboardUserContextPath + "/edit",
					type : "GET",
					success : function(dashboardUsers) {
						$.each(dashboardUsers, function(index, dashboardUser) {
							$(
									"#divUsers input:checkbox[value="
											+ dashboardUser.userPid + "]")
									.prop("checked", true);
							$('#field_sortOrder_' + dashboardUser.userPid).val(
									dashboardUser.sortOrder);

						});

						var sortorders = document
								.getElementsByClassName("sort_order");

						$
								.each(
										dashboardUsers,
										function(index, dashboardUser) {

											$(
													'#field_sortOrder_'
															+ dashboardUser.userPid)
													.on(
															'change',
															function() {
																var sortFeildId = this.id;
																var sortFeildValue = this.value;
																$
																		.each(
																				sortorders,
																				function(
																						index,
																						sortOrder) {
																					if (sortOrder.id != sortFeildId) {
																						if (sortOrder.value != 0
																								&& sortFeildValue == sortOrder.value) {

																							$(
																									'#field_sortOrder_'
																											+ dashboardUser.userPid)
																									.val(
																											dashboardUser.sortOrder);

																							alert("Sort Order Value : "
																									+ sortOrder.value
																									+ " Already Exists");

																						}
																					}
																				});
															});
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
		$("#assignUsersModal").modal("show");
	}

	function saveAssignedUsers() {

		$(".error-msg").html("");
		var selectedUsers = [];

		var selectUserWithSortOrder = [];

		var sortedNumberList = [];

		$.each($("input[name='user']:checked"), function() {
			selectedUsers.push($(this).val());
		});
		if (selectedUsers.length == 0) {
			$(".error-msg").html("Please select users");
			return;
		}

		$.each(selectedUsers, function(index, userPid) {
			dashboardUserModel = {};
			dashboardUserModel.userPid = userPid
			dashboardUserModel.sortOrder = $('#field_sortOrder_' + userPid)
					.val();
			sortedNumberList.push($('#field_sortOrder_' + userPid).val());
			selectUserWithSortOrder.push(dashboardUserModel);
		});

		var uniqueSortOrderListArray = [];

		var duplicatedSortOrderListArray = [];

		var i = 0;

		// Loop through array values
		for (i = 0; i < sortedNumberList.length; i++) {

			if (uniqueSortOrderListArray.indexOf(sortedNumberList[i]) === -1) {
				uniqueSortOrderListArray.push(sortedNumberList[i]);
			} else {
				duplicatedSortOrderListArray.push(sortedNumberList[i]);
			}
		}

		var duplicatestatus = false;

		$.each(uniqueSortOrderListArray, function(index, sotedValue) {
			$.each(duplicatedSortOrderListArray,
					function(index, duplicateValue) {
						if (duplicateValue != 0) {
							if (sotedValue == duplicateValue) {
								duplicatestatus = true;
								var result = confirm("Sort Order Value : "
										+ duplicateValue + " Already Exists");
								return;
							}
						}
					});
			return;
		});

		if (duplicatestatus) {
			return;
		} else {

			$(".error-msg").html("Please wait.....");
			$.ajax({
				url : dashboardUserContextPath,
				type : "POST",
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(selectUserWithSortOrder),
				success : function(status) {
					$("#assignUsersModal").modal("hide");
					onSaveSuccess(status);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});

		}
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = dashboardUserContextPath;
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