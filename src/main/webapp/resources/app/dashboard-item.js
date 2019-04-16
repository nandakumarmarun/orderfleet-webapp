// Create a DashboardItem object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DashboardItem) {
	this.DashboardItem = {};
}

(function() {
	'use strict';

	var dashboardItemContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#dashboardItemForm");
	var deleteForm = $("#deleteForm");
	var dashboardItemModel = {
		pid : null,
		name : null,
		sortOrder : 0,
		taskPlanType : null,
		dashboardItemType : null,
		dashboardItemConfigType : null,
		documentType : null,
		activities : [],
		productGroups : [],
		salesTargetGroupPid: null,
		salesTargetBlockPid: null,
		documents : []
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		}
	};

	$(document).ready(function() {

		// table search
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		$('#btnSaveUsers').click(function() {
			saveAssignedUsers();
		});
		
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateDashboardItem(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteDashboardItem(e.currentTarget.action);
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});

	});
	
	function loadUsers(pid) {
		dashboardItemModel.pid = pid;
		// clear all check box
		$("#divUsers input:checkbox").attr('checked', false);
		$.ajax({
			url : dashboardItemContextPath + "/users/" + pid,
			type : "GET",
			success : function(users) {
					$.each(users, function(index, user) {
						
						$(
								"#divUsers input:checkbox[value="
										+ user.pid + "]").prop("checked",
								true);
					});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function saveAssignedUsers() {

		$(".error-msg").html("");
		var selectedUsers = "";

		$.each($("input[name='user']:checked"), function() {
			selectedUsers += $(this).val() + ",";
		});

		if (selectedUsers == "") {
			$(".error-msg").html("Please select Users");
			return;
		}
		$.ajax({
			url : dashboardItemContextPath + "/assign-users",
			type : "POST",
			data : {
				pid : dashboardItemModel.pid,
				assignedUsers : selectedUsers,
			},
			success : function(status) {
				$("#assignUsersModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	DashboardItem.onChangeItemType = function() {
		var dashboardItemType = $('#field_dashboardItemType').val();
		$('#divActivities').hide();
		$('#divPGroups').hide();
		$('#divDocumentType').hide();
		$('#divDocuments').hide();
		$('#divTargetBlock').hide();
		if (dashboardItemType == 'ACTIVITY') {
			$('#divActivities').show();
		} else if (dashboardItemType == 'DOCUMENT') { 
			$('#divDocumentType').show();
		} else if (dashboardItemType == 'TARGET') { 
			$('#field_documentType').val("INVENTORY_VOUCHER");
			$('#divTargetBlock').show();
		} else {
			$('#field_documentType').val("INVENTORY_VOUCHER");
			$('#divPGroups').show();
			$('#divDocuments').show();
		} 
	}

	DashboardItem.onChangeDocumentType = function() {
		var dashboardItemType = $('#field_documentType').val();

		$('#field_documents').html("");
		$.ajax({
			async : false,
			url : dashboardItemContextPath + "/documents/" + dashboardItemType,
			method : 'GET',
			success : function(documnets) {
				$.each(documnets, function(index, documnet) {
					$('#field_documents').append(
							'<option value="' + documnet.pid + '">'
									+ documnet.name + '</option>');
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		$('#divDocuments').show();
	}
	
	DashboardItem.onChangeTargetGroup = function() {
		var targetGroupPid = $('#field_targetGroup').val();
		$('#field_targets').html("");
		$.ajax({
			async : false,
			url : dashboardItemContextPath + "/targetGroup/" + targetGroupPid,
			method : 'GET',
			success : function(targetBlocks) {
				console.log(targetBlocks);
				$.each(targetBlocks, function(index, targetBlock) {
					$('#field_targets').append(
							'<option value="' + targetBlock.pid + '">'
									+ targetBlock.name + '</option>');
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function createUpdateDashboardItem(el) {
		dashboardItemModel.name = $('#field_name').val();
		dashboardItemModel.sortOrder = $('#field_sortOrder').val()
		dashboardItemModel.taskPlanType = $('#field_taskPlanType').val();
		dashboardItemModel.dashboardItemType = $('#field_dashboardItemType')
				.val();
		dashboardItemModel.documentType = $('#field_documentType').val();
		dashboardItemModel.dashboardItemConfigType = $(
				'#field_dashboardItemConfig').val();

		var documents = $('#field_documents').val();
		var activities = $('#field_activities').val();
		var productGroups = $('#field_pGroups').val();
		var targetGroup = $('#field_targetGroup').val();
		var targetBlock = $('#field_targets').val();
		if (dashboardItemModel.dashboardItemType == 'ACTIVITY') {
			dashboardItemModel.documentType = null;
			if (activities == null) {
				addErrorAlert("Please select activities");
				return;
			}
			$.each(activities, function(index, pid) {
				dashboardItemModel.activities.push({
					pid : pid
				});
			});
		}
		if (dashboardItemModel.dashboardItemType == 'DOCUMENT') {
			if (documents == null) {
				addErrorAlert("Please select documents");
				return;
			}
			$.each(documents, function(index, pid) {
				dashboardItemModel.documents.push({
					pid : pid
				});
			});
		}
		if (dashboardItemModel.dashboardItemType == 'PRODUCT') {
			if (productGroups == null) {
				addErrorAlert("Please select product group");
				return;
			}
			if (documents == null) {
				addErrorAlert("Please select documents");
				return;
			}
			
			$.each(documents, function(index, pid) {
				dashboardItemModel.documents.push({
					pid : pid
				});
			});
			
			$.each(productGroups, function(index, pid) {
				dashboardItemModel.productGroups.push({
					pid : pid
				});
			});
		}
		if (dashboardItemModel.dashboardItemType == 'TARGET') {
			if (targetGroup == "-1" || targetBlock == "-1") {
				addErrorAlert("Please select target");
				return;
			}
			dashboardItemModel.salesTargetGroupPid = targetGroup;
			dashboardItemModel.salesTargetBlockPid = targetBlock;
		}
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(dashboardItemModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editDashboardItem(id) {
		$.ajax({
			url : dashboardItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_sortOrder').val((data.sortOrder));
				$('#field_taskPlanType').val(data.taskPlanType);
				$('#field_dashboardItemType').val(data.dashboardItemType);
				$('#field_dashboardItemConfig').val(
						data.dashboardItemConfigType);

				$('#divDocumentType').hide();
				$('#divDocuments').hide();
				$('#divActivities').hide();
				$('#divPGroups').hide();
				$('#divTargetBlock').hide();
				
				if (data.dashboardItemType == 'ACTIVITY') {
					$('#divActivities').show();
					var actArray = [];
					$.each(data.activities, function(index, activity) {
						actArray.push(activity.pid);
					});
					$('#field_activities').val(actArray);

				} else if (data.dashboardItemType == 'DOCUMENT') {
					$('#field_documentType').val(data.documentType);
					// load documents
					DashboardItem.onChangeDocumentType();
					$('#divDocumentType').show();

					var docArray = [];
					$.each(data.documents, function(index, document) {
						docArray.push(document.pid);
					});
					$('#field_documents').val(docArray);
				} else if (data.dashboardItemType == 'TARGET') {
					$('#divTargetBlock').show();
					$('#field_documentType').val("INVENTORY_VOUCHER");
					$('#field_targetGroup').val(data.salesTargetGroupPid);
					DashboardItem.onChangeTargetGroup();
					$('#field_targets').val(data.salesTargetBlockPid);
				} else {
					$('#divDocuments').show();
					$('#divPGroups').show();
					$('#field_documentType').val("INVENTORY_VOUCHER");
					
					var pgArray = [];
					$.each(data.productGroups, function(index, productGroup) {
						pgArray.push(productGroup.pid);
					});
					$('#field_pGroups').val(pgArray);
					
					docArray = [];
					$.each(data.documents, function(index, document) {
						docArray.push(document.pid);
					});
					$('#field_documents').val(docArray);
				}
				// set pid
				dashboardItemModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	
	function showDashboardItem(id) {
		$.ajax({
			url : dashboardItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_sortOrder').text(data.sortOrder);
				$('#lbl_itemType').text(data.dashboardItemType);
				$('#lbl_taskPlanType').text(data.taskPlanType);
				$('#lbl_dashboardItemConfigType').text(
						data.dashboardItemConfigType);

				$('#tblActivities').html("");
				$('#tblDocuments').html("");
				if (data.dashboardItemType == 'ACTIVITY') {
					$.each(data.activities, function(index, activity) {
						$('#tblActivities').append(
								"<tr><td>" + activity.name + "</td></tr>");
					});
				} else {
					$.each(data.documents, function(index, document) {
						$('#tblDocuments').append(
								"<tr><td>" + document.name + "</td></tr>");
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteDashboardItem(actionurl, id) {
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
		window.location = dashboardItemContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dashboardItemContextPath;
	}

	DashboardItem.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showDashboardItem(id);
				break;
			case 1:
				editDashboardItem(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', dashboardItemContextPath + "/" + id);
				break;
			case 3:
				loadUsers(id);
				break;
			}
		}
		el.modal('show');
	}

	function searchTable(inputVal) {
		var table = $('#tBodyDashboardItem');
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

	function resetForm() {
		$('#divDocumentType').hide();
		$('#divDocuments').hide();
		$('#divActivities').show();

		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		dashboardItemModel.pid = null; // reset dashboardItem model;
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