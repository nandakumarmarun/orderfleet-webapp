// Create a Activity object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Activity) {
	this.Activity = {};
}

(function() {
	'use strict';

	var activityContextPath = location.protocol + '//' + location.host
			+ '/web/activities';
	var createEditForm = $("#activityForm");
	var deleteForm = $("#deleteForm");
	var activityModel = {
		pid : null,
		name : null,
		alias : null,
		description : null,
		hasDefaultAccount : null,
		hasSecondarySales : null,
		completePlans : null,
		targetDisplayOnDayplan : null,
		companyPid : null,
		contactManagement : null,
		geoFencing : null,
		hasTelephonicOrder : null,
		emailTocomplaint : null,
		locationRadius : null
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		},
		companyPid : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		}
	};

	$(document)
			.ready(
					function() {
						
						$('.selectpicker').selectpicker();

						// add the rule here
						$.validator.addMethod("valueNotEquals", function(value,
								element, arg) {
							return arg != value;
						}, "");

						createEditForm.validate({
							rules : validationRules,
							messages : validationMessages,
							submitHandler : function(form) {
								createUpdateActivity(form);
							}
						});

						deleteForm.submit(function(e) {
							// prevent Default functionality
							e.preventDefault();
							// pass the action-url of the form
							deleteActivity(e.currentTarget.action);
						});

						$('#btnSaveAccountTypes').on('click', function() {
							saveAssignedAccountTypes();
						});

						$('#btnSaveDocuments').on('click', function() {
							saveAssignedDocuments();
						});

						// table search
						$('#btnSearch').click(function() {
							searchTable($("#search").val());
						});

						// table search
						$('#btnMainSearch').click(
								function() {
									searchMainTable($('#searchText').val(),
											$('#activityTbody'));
								});

						// select all checkbox in table tblPriceLevels
						$('input:checkbox.allcheckbox')
								.click(
										function() {
											$(this)
													.closest('table')
													.find(
															'tbody tr td input[type="checkbox"]:visible')
													.prop(
															'checked',
															$(this).prop(
																	'checked'));
										});

						$('#btnActivateActivity').on('click', function() {
							activateAssignedActivity();
						});

						$('#slt_status').on('change', function() {
							findByfilfter();
						});

						$('#field_mainCompany').on('change', function() {
							findByCompanyfilfter();
						});

					});

	function activateAssignedActivity() {
		$(".error-msg").html("");
		var selectedActivity = "";

		$.each($("input[name='activity']:checked"), function() {
			selectedActivity += $(this).val() + ",";
		});

		if (selectedActivity == "") {
			$(".error-msg").html("Please select Activity");
			return;
		}
		$.ajax({
			url : activityContextPath + "/activateActivity",
			type : "POST",
			data : {
				activitys : selectedActivity,
			},
			success : function(status) {
				$("#enableActivityModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function searchTable(inputVal) {
		var table = $('#tbodyAccountTypes');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
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

	function searchMainTable(inputVal, table) {
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
									return false;
								}
							}
						}
					}
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

	function createUpdateActivity(el) {
		activityModel.name = $('#field_name').val();
		activityModel.contactManagement = $('#contactManagement').val();
		activityModel.alias = $('#field_alias').val();
		activityModel.description = $('#field_description').val();
		activityModel.hasDefaultAccount = $('#hasDefaultAccount').prop(
				"checked");
		activityModel.hasSecondarySales = $('#hasSecondarySales').prop(
		"checked");
		activityModel.geoFencing = $('#geoFencing').prop(
		"checked");
		activityModel.hasTelephonicOrder = $('#hasTelephonicOrder').prop(
		"checked");
		activityModel.emailTocomplaint = $('#emailTocomplaint').prop(
		"checked");
		activityModel.completePlans = $('#completePlans').prop("checked");
		activityModel.targetDisplayOnDayplan = $('#targetDisplayOnDayplan')
				.prop("checked");
		activityModel.locationRadius =$('#field_locationRadius').val();
		activityModel.companyPid = $('#field_company').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action') + "/save",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(activityModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showActivity(id) {
		$
				.ajax({
					url : activityContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#lbl_name').text(data.name);
						$('#lbl_alias').text(
								(data.alias == null ? "" : data.alias));
						$('#lbl_description').text(
								(data.description == null ? ""
										: data.description));

						$('#tblAccountTypes').html("");
						var accountTypes = "";
						$.each(data.activityAccountTypes,
								function(index, accountType) {
									accountTypes += ("<tr><td>"
											+ accountType.name + "</td></tr>");
								});
						if (accountTypes != "") {
							var table = "<thead><tr><th>Account Types</th></tr></thead><tbody>"
									+ accountTypes + "</tbody>";
							$('#tblAccountTypes').append(table);
						}
						$('#tblDocuments').html("");
						var documents = "";
						$
								.each(
										data.documents,
										function(index, document) {
											documents += ("<tr><td>"
													+ document.name + "</td></tr>");
										});
						if (documents != "") {
							var table = "<thead><tr><th>Documents</th></tr></thead><tbody>"
									+ documents + "</tbody>";
							$('#tblDocuments').append(table);
						}

					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function editActivity(id) {
		$
				.ajax({
					url : activityContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#field_name').val(data.name);
						$('#field_alias').val(
								(data.alias == null ? "" : data.alias));
						$('#field_description').val(
								(data.description == null ? ""
										: data.description));
						$('#contactManagement').val(
								data.contactManagement == null ? "" : data.contactManagement)
						$("#hasDefaultAccount").prop("checked",
								data.hasDefaultAccount);
						$("#hasSecondarySales").prop("checked",
								data.hasSecondarySales);
						$("#geoFencing").prop("checked",
								data.geoFencing);
						$("#hasTelephonicOrder").prop("checked",
								data.hasTelephonicOrder);
						$("#emailTocomplaint").prop("checked",
								data.emailTocomplaint);
						$("#completePlans").prop("checked", data.completePlans);
						$("#targetDisplayOnDayplan").prop("checked",
								data.targetDisplayOnDayplan);
						$("#field_company").val(data.companyPid);
						$('#field_locationRadius').val(
							      (data.locationRadius == null ? ""
							            : data.locationRadius));

						$("#field_company").prop('disabled', true);

						// set pid
						activityModel.pid = data.pid;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteActivity(actionurl, id) {
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

	function loadAccountTypes(pid) {

		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("");

		// clear all check box
		$("#divAccountTypes input:checkbox").attr('checked', false);
		$.ajax({
			url : activityContextPath + "/" + pid,
			type : "GET",
			success : function(data) {
				activityModel.pid = data.pid;
				if (data.activityAccountTypes) {
					$.each(data.activityAccountTypes, function(index,
							accountType) {
						$(
								"#divAccountTypes input:checkbox[value="
										+ accountType.pid + "]").prop(
								"checked", true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function saveAssignedAccountTypes() {

		$(".error-msg").html("");
		var selectedAccountTypes = "";

		$.each($("input[name='accountType']:checked"), function() {
			selectedAccountTypes += $(this).val() + ",";
		});

		if (selectedAccountTypes == "") {
			$(".error-msg").html("Please select Account Types");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : activityContextPath + "/assignAccountTypes",
			type : "POST",
			data : {
				pid : activityModel.pid,
				assignedAccountTypes : selectedAccountTypes,
			},
			success : function(status) {
				$("#assignAccountTypesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function loadDocuments(pid) {

		// clear all
		$(".error-msg").html("");
		$("#divDocuments input:checkbox").attr('checked', false);
		$(".sortOrder").val(0);

		activityModel.pid = pid;
		$.ajax({
			url : activityContextPath + "/documents/" + pid,
			type : "GET",
			success : function(activityDocuments) {
				if (activityDocuments) {
					$.each(activityDocuments, function(index, document) {
						$(
								"#divDocuments input:checkbox[value="
										+ document.documentPid + "]").prop(
								"checked", true);
						$("#required" + document.documentPid).prop("checked",
								document.required);
						$("#sortOrder" + document.documentPid).val(
								document.sortOrder);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function saveAssignedDocuments() {

		$(".error-msg").html("");
		var selectedDocuments = [];

		$.each($("input[name='document']:checked"), function() {
			var documentPid = $(this).val();
			var required = $("#required" + documentPid).prop("checked");
			var sortOrder = $("#sortOrder" + documentPid).val();
			selectedDocuments.push({
				documentPid : documentPid,
				required : required,
				sortOrder : sortOrder
			});
		});
		if (selectedDocuments.length == 0) {
			$(".error-msg").html("Please select Documents");
			return;
		}
		$(".error-msg").html("Please wait.....");
		$
				.ajax({
					url : activityContextPath + "/assignDocuments/"
							+ activityModel.pid,
					type : "POST",
					contentType : "application/json; charset=utf-8",
					data : JSON.stringify(selectedDocuments),
					success : function(status) {
						$("#assignDocumentsModal").modal("hide");
						onSaveSuccess(status);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	Activity.setActive = function(name, pid, active) {
		activityModel.pid = pid;
		activityModel.activated = active;
		activityModel.name = name;

		var r = confirm("Are you confirm?");
		if (r == true) {
			$.ajax({
				url : activityContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset:utf-8",
				data : JSON.stringify(activityModel),
				success : function(status) {
					onSaveSuccess(status);
					console.log(status);
					// var row = $(this).closest('tr');
					// row.find("td").eq(2).html(spanActivated(status.name,status.pid,status.activated));
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		} else {
			onSaveSuccess();
		}

	}

	function statusExecutive(id, obj) {
		if (confirm("Are you Sure you want to change this status ?")) {
		} else {
			return false;
		}
		$.ajax({
			url : contextPath + "/company/statusExecutive",
			type : "POST",
			data : {
				id : id
			},
			success : function(status) {
				if (status.activated) {
					var row = $(obj).closest('tr');
					row.find("td").eq(5).find('button[title="Deactivate"]')
							.show("slow", function() {
							});
					row.find("td").eq(5).find('button[title="Activate"]').hide(
							"slow", function() {
							});

					row.find("td").eq(4).text("Active");

				} else {

					var row = $(obj).closest('tr');
					row.find("td").eq(5).find('button[title="Activate"]').show(
							"slow", function() {
							});
					row.find("td").eq(5).find('button[title="Deactivate"]')
							.hide("slow", function() {
							});
					row.find("td").eq(4).text("Inactive");

				}

			}
		});

	}

	function findByfilfter() {
		var active, deactivate;
		var statusBox = $("#slt_status").val();
		if (statusBox == "All") {
			active = true;
			deactivate = true;
		} else if (statusBox == "Active") {
			active = true;
			deactivate = false;
		} else if (statusBox == "Deactive") {
			deactivate = true;
			active = false;
		} else if (statusBox == "MultipleActivate") {
			Activity.showModalPopup($('#enableActivityModal'));
		}
		$.ajax({
			url : activityContextPath + "/get-by-status-filter",
			type : "GET",
			data : {
				active : active,
				deactivate : deactivate
			},
			success : function(activitys) {
				loadActivities(activitys);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function findByCompanyfilfter() {
		var companyPid = $("#field_mainCompany").val();

		if (companyPid == "-1") {
			return;
		}

		$.ajax({
			url : activityContextPath + "/get-by-company-filter/" + companyPid,
			type : "GET",
			success : function(activitys) {
				loadActivities(activitys);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function loadActivities(activities) {

		$('#activityTbody').html(
				"<tr><td colspan='5' align='center'>Please wait...</td></tr>");

		if (!activities.length > 0) {
			$('#activityTbody')
					.html(
							"<tr><td colspan='5' align='center'>No data available</td></tr>");
			return;
		}

		$('#activityTbody').html("");
		$
				.each(
						activities,
						function(index, activity) {

							$('#activityTbody')
									.append(
											"<tr><td title='click to view' class='sa'><span style='color: #2C7BD0; cursor: pointer;' onclick='Activity.showModalPopup($(\"#viewModal\"),\""
													+ activity.pid
													+ "\",0);'>"
													+ activity.name
													+ "</span></td><td>"
													+ (activity.alias == null ? ""
															: activity.alias)
													+ "</td><td>"
													+ (activity.description == null ? ""
															: activity.description)
													+ "</td><td>"
													+ spanActivated(
															activity.name,
															activity.pid,
															activity.activated)
													+ "</td><td><i type='button' class='btn btn-blue entypo-pencil' title='Edit Activity' onclick='Activity.showModalPopup($(\"#myModal\"),\""
													+ activity.pid
													+ "\",1);'></i></td></tr>");

						});
	}

	function spanActivated(name1, pid1, activated) {

		var spanActivated = "";
		var name = "'" + name1 + "'";
		var pid = "'" + pid1 + "'";

		if (activated) {
			spanActivated = '<span class="label label-success" onclick="Activity.setActive('
					+ name
					+ ','
					+ pid
					+ ','
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="Activity.setActive('
					+ name
					+ ','
					+ pid
					+ ','
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		var contextPath = location.protocol + '//' + location.host
				+ '/web/site-activities';
		window.location = contextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		var contextPath = location.protocol + '//' + location.host
				+ '/web/site-activities';
		window.location = contextPath;
	}

	Activity.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showActivity(id);
				break;
			case 1:
				editActivity(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', activityContextPath + "/" + id);
				break;
			case 3:
				loadAccountTypes(id);
				break;
			case 4:
				loadDocuments(id);
				break;

			}
		}
		$("#field_company").prop('disabled', false);
		el.modal('show');
	}

	Activity.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		activityModel.pid = null; // reset activity model;
		$('#hasDefaultAccount').prop('checked', false);
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