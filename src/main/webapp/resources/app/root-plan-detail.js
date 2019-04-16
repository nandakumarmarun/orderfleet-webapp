if (!this.RootPlanDetail) {
	this.RootPlanDetail = {};
}

(function() {
	'use strict';

	var rootPlanDetailContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#rootPlanDetailForm");
	var deleteForm = $("#deleteForm");
	var rootPlanDetailModel = {
			pid : null,
			rootPlanOrder:null,
			taskListPid:null,
			rootPlanHeaderPid:null,
	};

	// Specify the validation rules
	var validationRules = {
		
	};

	// Specify the validation error messages
	var validationMessages = {
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateRootPlanDetail(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteRootPlanDetail(e.currentTarget.action);
		});
		
		$('#btnActivateRootPlanDetail').on('click', function() {
			activateAssignedRootPlanDetail();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	
	function createUpdateRootPlanDetail(el) {
		var selectedTaskLists = [];
	var rootPlanHeaderPid=	$("#field_rootPlanHeader").val();
		$.each($("input[name='rootPlanTaskList']:checked"), function() {
			var taskListPid=$(this).val();
			var rootPlanOrder=$("#rootPlanOrder"+$(this).val()+"").val();
			
			selectedTaskLists.push({
				taskListPid : taskListPid,
				rootPlanOrder : rootPlanOrder,
				rootPlanHeaderPid:rootPlanHeaderPid,
			});
		});
		console.log(selectedTaskLists);
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
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
	
	 RootPlanDetail.loadTasklistByHeader=function(){
		var headerPid=	$("#rootPlanHeader").val();
		$.ajax({
			url:rootPlanDetailContextPath+"/"+headerPid,
			type:'GET',
			async:false,
			success: function(rootPlanDetails) {
				console.log(rootPlanDetails);
				$('#detailWithOrder').html("");
				if (rootPlanDetails.length == 0) {
					$('#detailWithOrder')
						.html(
							"<tr><td colspan='2' align='center'>No data available</td></tr>");
					return;
				}
				$
				.each(rootPlanDetails,
					function(index, rootPlanDetail) {
							$('#detailWithOrder')
							.append(
									"<tr><td>"
											+ rootPlanDetail.taskListName
											+ "</td><td>"
											+ rootPlanDetail.rootPlanOrder
											+ "</td></tr>");
					});
//				$
//				.each(
//						userScheduleTaskList.userSchedules,
//					function(index, userSchedule) {
//							$("input:checkbox[name=userScheduleName][value="+ userSchedule.taskListPid + "]").prop("checked",true);
//						    $('#scheduleOrder'+userSchedule.taskListPid).val(userSchedule.scheduleOrder);
//						});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function showRootPlanDetail(id) {
		$.ajax({
			url : rootPlanDetailContextPath + "/" + id,
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

	function editRootPlanDetail(id) {
		$.ajax({
			url : rootPlanDetailContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#txtFromDate').val(data.fromDate);
				$('#txtToDate').val(data.toDate);
				// set pid
				rootPlanDetailModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteRootPlanDetail(actionurl, id) {
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

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = rootPlanDetailContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = rootPlanDetailContextPath;
	}

	function loadTaskList(el){
		$.ajax({
			url:rootPlanDetailContextPath+"/loadTaskList",
			type:'GET',
			async:false,
			success: function(taskLists) {
				$('#tblTaskList').html("");
				if (taskLists.length == 0) {
					$('#tblTaskList')
						.html(
							"<tr><td colspan='3' align='center'>No data available</td></tr>");
					return;
				}
				$
				.each(taskLists,
					function(index, taskList) {
							$('#tblTaskList')
							.append(
									"<tr><td>"
											+ "<input name='rootPlanTaskList' value='"+taskList.pid+"' type='checkbox'/>"
											+ "</td><td>"
											+ taskList.name
											+ "</td><td>"
											+ "<input type='number'  id='rootPlanOrder"+taskList.pid+"' />"
											+ "</td></tr>");
					});
//				$
//				.each(
//						userScheduleTaskList.userSchedules,
//					function(index, userSchedule) {
//							$("input:checkbox[name=userScheduleName][value="+ userSchedule.taskListPid + "]").prop("checked",true);
//						    $('#scheduleOrder'+userSchedule.taskListPid).val(userSchedule.scheduleOrder);
//						});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	RootPlanDetail.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showRootPlanDetail(id);
				break;
			case 1:
				editRootPlanDetail(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', rootPlanDetailContextPath + "/" + id);
				break;
			case 3:
				loadTaskList(el);
				break;
			}
		}
		el.modal('show');
	}

	RootPlanDetail.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		rootPlanDetailModel.pid = null; // reset rootPlanDetail model;
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