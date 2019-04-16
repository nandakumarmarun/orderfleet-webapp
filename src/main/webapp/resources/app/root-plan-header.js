if (!this.RootPlanHeader) {
	this.RootPlanHeader = {};
}

(function() {
	'use strict';

	var rootPlanHeaderContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#rootPlanHeaderForm");
	var deleteForm = $("#deleteForm");
	var rootPlanHeaderModel = {
		pid : null,
		name : null,
		formDate : null,
		toDate : null,
		fromDateString : null,
		toDateString : null,
		userPid : null

	};
	var rootPlanHeaderAndDetailModel = {
		rootPlanDetailDTOs : [],
		rootPlanHeaderDTO : null,
	};
	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		userPid : {
			valueNotEquals : "-1"
		},

	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		userPid : {
			valueNotEquals : "This field is required."
		}

	};

	$(document).ready(
			function() {
				// add the rule here
				$.validator.addMethod("valueNotEquals", function(value,
						element, arg) {
					return arg != value;
				}, "");
				createEditForm.validate({
					rules : validationRules,
					messages : validationMessages,
					submitHandler : function(form) {
						createUpdateRootPlanHeader(form);
					}
				});

				deleteForm.submit(function(e) {
					// prevent Default functionality
					e.preventDefault();
					// pass the action-url of the form
					deleteRootPlanHeader(e.currentTarget.action);
				});
				$("#txtToDate").datepicker({
					dateFormat : "dd-mm-yy"
				});
				$("#txtFromDate").datepicker({
					dateFormat : "dd-mm-yy"
				});
				$("#txtNewToDate").datepicker({
					dateFormat : "dd-mm-yy"
				});
				$("#txtNewFromDate").datepicker({
					dateFormat : "dd-mm-yy"
				});
				$('#assignTaskList').on('click', function() {
					assignedRootPlanDetail();
				});
				$('#copyTaskList').on('click', function() {
					copyRootPlanDetail();
				});
				$('#assignUser').on('click', function() {
					assignedUser();
				});
				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});
			});

	function copyRootPlanDetail() {
		var res = nameForCopy.split("(");
		rootPlanHeaderModel.name = res[0] + '(' + $("#txtNewFromDate").val()
				+ ' to ' + $("#txtNewToDate").val() + ')';
		rootPlanHeaderModel.fromDateString = $("#txtNewFromDate").val();
		rootPlanHeaderModel.toDateString = $("#txtNewToDate").val();

		var rootPlanDetailDTOs = [];
		$.each($("input[name='rootPlanTaskList1']:checked"),
				function() {
					var taskListPid = $(this).val();
					var rootPlanOrder = $(
							"#rootPlanOrder1" + $(this).val() + "").val();
					if (rootPlanOrder == "" || !$.isNumeric(rootPlanOrder)) {
						invalidOrder = true;
						return false;
					}
					rootPlanDetailDTOs.push({
						pid : $(this).closest("tr").prop("id"),
						taskListPid : taskListPid,
						rootPlanOrder : rootPlanOrder,
					});
				});
		rootPlanHeaderAndDetailModel.rootPlanHeaderDTO = rootPlanHeaderModel;
		rootPlanHeaderAndDetailModel.rootPlanDetailDTOs = rootPlanDetailDTOs;
		$.ajax({
			method : 'POST',
			url : rootPlanHeaderContextPath + "/copyTasklist",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(rootPlanHeaderAndDetailModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	RootPlanHeader.validateRootPlanOrder = function() {

		var selectedTaskLists = [];
		$.each($("input[name='rootPlanTaskList']:checked"), function() {
			var taskListPid = $(this).val();
			var rootPlanOrder = $("#rootPlanOrder" + $(this).val() + "").val();

			selectedTaskLists.push({
				pid : $(this).closest("tr").prop("id"),
				taskListPid : taskListPid,
				rootPlanOrder : rootPlanOrder,
				rootPlanHeaderPid : headerIdForAssign,
			});
		});

		var checkDuplicate = 0;
		var duplicate = false;
		var tasklistsDuplicate = [];

		for (var i = 0; i < selectedTaskLists.length; i++) {
			tasklistsDuplicate.push(selectedTaskLists[i].rootPlanOrder);
		}
		var tasklists = tasklistsDuplicate.sort();
		for (var i = 0; i < tasklists.length - 1; i++) {
			if (tasklists[i + 1] != "") {
				if (tasklists[i + 1] == tasklists[i]) {
					checkDuplicate = tasklists[i];
					duplicate = true;
					break;
				}
			}
		}

		if (duplicate) {
			alert("Route Plan Order " + checkDuplicate + " is Already Exist");
			return;
		}
	}

	RootPlanHeader.filter = function() {

		$('#tBodyRootPlanHeader').html(
				"<tr><td colspan='7' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : rootPlanHeaderContextPath + "/filter",
					type : 'GET',
					data : {
						userPid : $("#dbUser").val(),
						active : $("#activated").val()
					},
					success : function(rootPlanHeaders) {
						$('#tBodyRootPlanHeader').html("");
						if (rootPlanHeaders.length == 0) {
							$('#tBodyRootPlanHeader')
									.html(
											"<tr><td colspan='7' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										rootPlanHeaders,
										function(index, rootPlanHeader) {
											$('#tBodyRootPlanHeader')
													.append(
															"<tr><td>"
																	+ rootPlanHeader.userName
																	+ "</td><td>"
																	+ rootPlanHeader.name
																	+ "</td>"
																	+ "<td>"
																	+ convertDateFromServer(rootPlanHeader.fromDate)
																	+ "</td><td>"
																	+ convertDateFromServer(rootPlanHeader.toDate)
																	+ "</td>"
																	+ "<td><button type='button' class='btn btn-blue' onclick='RootPlanHeader.showModalPopup($(\"#viewModal\"),\""
																	+ rootPlanHeader.pid
																	+ "\",0);'>View</button>"
																	+ "	<button type='button' class='btn btn-blue' onclick='RootPlanHeader.showModalPopup($(\"#myModal\"),\""
																	+ rootPlanHeader.pid
																	+ "\",1);'>Edit</button>"
																	+ "	<button type='button' class='btn btn-danger' onclick='RootPlanHeader.showModalPopup($(\"#deleteModal\"),\""
																	+ rootPlanHeader.pid
																	+ "\",2);'>Delete</button>"
																	+ "	<button type='button' class='btn btn-info' onclick='RootPlanHeader.showModalPopup($(\"#assignModal\"),\""
																	+ rootPlanHeader.pid
																	+ "\",3);'>Assign TaskList</button></td><td>"
																	+ spanActivated(
																			rootPlanHeader.userPid,
																			rootPlanHeader.pid,
																			rootPlanHeader.activated)
																	+ "</td>"
																	+ "<td><button type='button' class='btn' onclick='RootPlanHeader.showModalPopup($(\"#copyRoutePlanModal\"),\""
																	+ rootPlanHeader.pid
																	+ "\",5);'>Copy Route Plan</button></td>"
																	+ "</tr>");
										});
					},
				});
	}

	function spanActivated(userPid, pid, activated) {

		var spanActivated = "";
		var userPid1 = "'" + userPid + "'";
		var pid1 = "'" + pid + "'";
		if (activated) {
			spanActivated = '<span class="label label-success" onclick="RootPlanHeader.setActive('
					+ userPid1
					+ ', '
					+ pid1
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="RootPlanHeader.setActive('
					+ userPid1
					+ ', '
					+ pid1
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}

	RootPlanHeader.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}

	var headerIdForAssign;
	function assignedRootPlanDetail() {
		var selectedTaskLists = [];
		var invalidOrder = false;
		$.each($("input[name='rootPlanTaskList']:checked"), function() {
			var taskListPid = $(this).val();
			var rootPlanOrder = $("#rootPlanOrder" + $(this).val() + "").val();
			if (rootPlanOrder == "" || !$.isNumeric(rootPlanOrder)) {
				invalidOrder = true;
				return false;
			}
			selectedTaskLists.push({
				pid : $(this).closest("tr").prop("id"),
				taskListPid : taskListPid,
				rootPlanOrder : rootPlanOrder,
				rootPlanHeaderPid : headerIdForAssign,
			});
		});

		var checkDuplicate = 0;
		var duplicate = false;
		var tasklistsDuplicate = [];

		for (var i = 0; i < selectedTaskLists.length; i++) {
			tasklistsDuplicate.push(selectedTaskLists[i].rootPlanOrder);
		}
		var tasklists = tasklistsDuplicate.sort();
		for (var i = 0; i < tasklists.length - 1; i++) {
			if (tasklists[i + 1] != "") {
				if (tasklists[i + 1] == tasklists[i]) {
					checkDuplicate = tasklists[i];
					duplicate = true;
					break;
				}
			}
		}

		if (duplicate) {
			alert("Route Plan Order " + checkDuplicate + " is Already Exist");
			return;
		}
		if (invalidOrder) {
			alert("Invalid plan order");
			return;
		}

		if (selectedTaskLists == "") {
			$(".error-msg").html("Please select TaskList");
			return;
		}
		$.ajax({
			method : 'POST',
			url : rootPlanHeaderContextPath + "/assignTasklist",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(selectedTaskLists),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function createUpdateRootPlanHeader(el) {
		rootPlanHeaderModel.name = $('#field_name').val();
		rootPlanHeaderModel.fromDateString = $("#txtFromDate").val();
		rootPlanHeaderModel.toDateString = $("#txtToDate").val();
		rootPlanHeaderModel.userPid = $("#field_user").val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(rootPlanHeaderModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showRootPlanHeader(id) {
		$.ajax({
			url : rootPlanHeaderContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_fromDate').text(data.fromDate);
				$('#lbl_toDate').text(data.toDate);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	RootPlanHeader.setActive = function(userPid, pid, active) {
		if (confirm("Are you confirm?")) {
			$
					.ajax({
						url : rootPlanHeaderContextPath + "/changeStatus",
						method : 'GET',
						data : {
							userPid : userPid,
							rootPlanHeaderPid : pid,
							activated : active,
						},
						success : function(data) {
							if (data) {
								alert("Root Plan Header is already activated for this user");
							} else {
								onSaveSuccess(data);
							}

						},
						error : function(xhr, error) {
							onError(xhr, error);
						}
					});
		}
	}

	function editRootPlanHeader(id) {
		$.ajax({
			url : rootPlanHeaderContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_user').val(data.userPid);
				$('#field_name').val(data.name);
				$('#txtFromDate').val(convertDateFromServer(data.fromDate));
				$('#txtToDate').val(convertDateFromServer(data.toDate));
				// set pid
				rootPlanHeaderModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteRootPlanHeader(actionurl, id) {
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

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('DD-MM-YYYY');
		} else {
			return "";
		}
	}
	var nameForCopy;
	var approvalStatusDisable = 0;
	function copyRoutePlan(id) {
		$.ajax({
			url : rootPlanHeaderContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				nameForCopy = data.name;
				rootPlanHeaderModel.userPid = data.userPid;
				var newDate = new Date(data.toDate).getTime() + 1 * 24 * 60
						* 60 * 1000;
				var dayCount = days_between(new Date(data.fromDate), new Date(
						data.toDate));
				var lastDate = new Date(newDate).getTime() + dayCount * 24 * 60
						* 60 * 1000;
				$('#txtNewFromDate').val(
						convertDateFromServer(new Date(newDate)));
				$('#txtNewToDate').val(
						convertDateFromServer(new Date(lastDate)));
				approvalStatusDisable = 1;
				loadTask(id);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function days_between(date1, date2) {

		// The number of milliseconds in one day
		var ONE_DAY = 1000 * 60 * 60 * 24;

		// Convert both dates to milliseconds
		var date1_ms = date1.getTime();
		var date2_ms = date2.getTime();

		// Calculate the difference in milliseconds
		var difference_ms = Math.abs(date1_ms - date2_ms);

		// Convert back to days and return
		return Math.round(difference_ms / ONE_DAY);

	}

	function loadTask(id) {
		$('input[name="rootPlanOrder1"]').val("");
		$('input[name="rootPlanOrder1"]').prop("disabled", false);
		$("input:checkbox[name=rootPlanTaskList1]").prop("checked", false);
		headerIdForAssign = id;
		$
				.ajax({
					url : rootPlanHeaderContextPath + "/loadTaskList/" + id,
					type : 'GET',
					async : false,
					success : function(rootPlanDetailDTOs) {

						$
								.each(
										rootPlanDetailDTOs,
										function(index, rootPlanDetailDTO) {
											$(
													"input:checkbox[name=rootPlanTaskList1][value="
															+ rootPlanDetailDTO.taskListPid
															+ "]").prop(
													"checked", true);
											$(
													'#rootPlanOrder1'
															+ rootPlanDetailDTO.taskListPid)
													.val(
															rootPlanDetailDTO.rootPlanOrder);

											// rearrange
											var $this = $("input:checkbox[name=rootPlanTaskList1][value="
													+ rootPlanDetailDTO.taskListPid
													+ "]");
											var row = $this.closest('tr');
											if ($this.prop('checked')) { // move
												// to
												// top
												row
														.insertBefore(
																row
																		.parent()
																		.find(
																				'tr:first-child'))
														.find('label')
														.html('move to bottom');
											} else { // move to bottom
												row
														.insertAfter(
																row
																		.parent()
																		.find(
																				'tr:last-child'))
														.find('label').html(
																'move to top');
											}
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function loadTaskList(id) {
		$(" #tblAssignTaskList td:nth-last-child(2)").text("");
		$('input[name="rootPlanOrder"]').val("");
		$('input[name="rootPlanOrder"]').prop("disabled", false);
		$("input:checkbox[name=rootPlanTaskList]").prop("checked", false);
		headerIdForAssign = id;
		$
				.ajax({
					url : rootPlanHeaderContextPath + "/loadTaskList/" + id,
					type : 'GET',
					async : false,
					success : function(rootPlanDetailDTOs) {

						$
								.each(
										rootPlanDetailDTOs,
										function(index, rootPlanDetailDTO) {
											$(
													"input:checkbox[name=rootPlanTaskList][value="
															+ rootPlanDetailDTO.taskListPid
															+ "]").prop(
													"checked", true);
											$(
													'#rootPlanOrder'
															+ rootPlanDetailDTO.taskListPid)
													.val(
															rootPlanDetailDTO.rootPlanOrder);
											$(
													'#status'
															+ rootPlanDetailDTO.taskListPid)
													.text(
															rootPlanDetailDTO.approvalStatus);
											if (rootPlanDetailDTO.approvalStatus == 'APPROVED') {
												$(
														'#rootPlanOrder'
																+ rootPlanDetailDTO.taskListPid)
														.prop("disabled", true);
												$(
														"input:checkbox[name=rootPlanTaskList][value="
																+ rootPlanDetailDTO.taskListPid
																+ "]").prop(
														"disabled", true);
											}
											if (rootPlanDetailDTO.approvalStatus == 'DOWNLOADED') {
												$(
														'#rootPlanOrder'
																+ rootPlanDetailDTO.taskListPid)
														.prop("disabled", true);
												$(
														"input:checkbox[name=rootPlanTaskList][value="
																+ rootPlanDetailDTO.taskListPid
																+ "]").prop(
														"disabled", true);
											}
											// set plan id
											$(
													'#status'
															+ rootPlanDetailDTO.taskListPid)
													.closest("tr")
													.prop(
															"id",
															rootPlanDetailDTO.pid);
											// rearrange
											var $this = $("input:checkbox[name=rootPlanTaskList][value="
													+ rootPlanDetailDTO.taskListPid
													+ "]");
											var row = $this.closest('tr');
											if ($this.prop('checked')) { // move
												// to
												// top
												row
														.insertBefore(
																row
																		.parent()
																		.find(
																				'tr:first-child'))
														.find('label')
														.html('move to bottom');
											} else { // move to bottom
												row
														.insertAfter(
																row
																		.parent()
																		.find(
																				'tr:last-child'))
														.find('label').html(
																'move to top');
											}
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = rootPlanHeaderContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = rootPlanHeaderContextPath;
	}

	RootPlanHeader.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showRootPlanHeader(id);
				break;
			case 1:
				editRootPlanHeader(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', rootPlanHeaderContextPath + "/" + id);
				break;
			case 3:
				approvalStatusDisable = 0;
				loadTaskList(id);
				break;
			case 4:
				loadUsers(id);
				break;
			case 5:
				copyRoutePlan(id);
				break;
			}
		}
		el.modal('show');
	}

	RootPlanHeader.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		rootPlanHeaderModel.pid = null; // reset rootPlanHeader model;
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