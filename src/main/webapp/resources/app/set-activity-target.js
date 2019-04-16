// Create a SetActivityTarget object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SetActivityTarget) {
	this.SetActivityTarget = {};
}

(function() {
	'use strict';

	var setActivityTargetContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(
			function() {
				$('#txtMonth').MonthPicker({
					ShowIcon : false,
					OnAfterChooseMonth : function(selectedDate) {
						SetActivityTarget.loadUserActivities();
					}
				});
				$('input[type=month]').MonthPicker().css('backgroundColor',
						'lightyellow');
			});

	SetActivityTarget.loadUserActivities = function() {

		if ($('#dbUser').val() == "-1" || $('#txtMonth').val() == "") {
			$('#tblSetTarget').html("");
			return;
		}
		$('#tblSetTarget').html("<tr><td colspan='3'>Please wait...</td></tr>");
		$.ajax({
			url : setActivityTargetContextPath + "/monthly-activity-targets",
			type : 'GET',
			data : {
				userPid : $('#dbUser').val(),
				monthAndYear : $('#txtMonth').val()
			},
			success : function(activityTargets) {
				addRowsToTable(activityTargets);
			}
		});
	}

	var trTarget;
	SetActivityTarget.editActivityTarget = function(activityPid,
			userActivityTragetPid, obj) {
		$('.error-msg').html('');
		trTarget = obj;

		$('#hdnActivityPid').val(activityPid);
		$('#hdnUserActivityTargetPid').val(userActivityTragetPid);
		var target = $(obj).closest("tr").find("td").eq(1).text();
		$('#txtTarget').val(target)
		$("#modalSetTarget").modal("show");
	}

	SetActivityTarget.saveTarget = function() {
		$("#saveTarget").prop('disabled', true);
		$(".error-msg").html("");
		if ($('#txtTarget').val() == "") {
			$(".error-msg").html('Please enter the target');
			$("#saveTarget").prop('disabled', false)
			return;
		}

		var monthlyTarget = {
			userPid : $('#dbUser').val(),
			activityPid : $('#hdnActivityPid').val(),
			userActivityTragetPid : $('#hdnUserActivityTargetPid').val(),
			target : $('#txtTarget').val(),
			monthAndYear : $('#txtMonth').val()
		}

		$(".error-msg").html("Please wait.....");
		$.ajax({
			url :setActivityTargetContextPath + "/monthly-activity-targets",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(monthlyTarget),
			success : function(result) {
				$("#modalSetTarget").modal("hide");
				
				// change target td
				$(trTarget).closest("tr").find("td").eq(1).text(result.target);
				
				// change button userActivityTragetPid
				$(trTarget).closest("tr").find("td").eq(2).html("<input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
					+ "onclick='SetActivityTarget.editActivityTarget(\""
					+ result.activityPid
					+ "\",\""
					+ result.userActivityTragetPid
					+ "\",this);'/>");
				$("#saveTarget").prop('disabled', false)
			},
			error : function(textStatus, errorThrown) {
				$("#saveTarget").prop('disabled', false);
			},
		});
	}

	function addRowsToTable(activityTargets) {

		if (activityTargets) {
			$('#tblSetTarget').html("");
			$
					.each(
							activityTargets,
							function(index, activityTarget) {
								$('#tblSetTarget')
										.append(
												"<tr><td>"
														+ activityTarget.activityName
														+ "</td><td>"
														+ activityTarget.target
														+ "</td><td><input class='btn btn-blue' style='width:100px'  type='button' value='Edit' "
														+ "onclick='SetActivityTarget.editActivityTarget(\""
														+ activityTarget.activityPid
														+ "\",\""
														+ activityTarget.userActivityTragetPid
														+ "\",this);'/></td></tr>");
							});
		} else {
			$('#tblSetTarget').html(
					"<tr><td colspan='3'>Activity not assigned</td></tr>")
		}

	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = activityUserTargetContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = activityUserTargetContextPath;
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		activityUserTargetModel.pid = null; // reset activityUserTarget model;
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