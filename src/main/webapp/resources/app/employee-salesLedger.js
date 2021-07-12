// Create a EmployeeProfileSalesLedger object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.EmployeeProfileSalesLedger) {
	this.EmployeeProfileSalesLedger = {};
}

(function() {
	'use strict';

	var employeeProfileSalesLedgerContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var employeeProfileSalesLedgerModel = {
		employeeProfilePid : null
	};

	var salesLedgerTreeData;

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		$('#btnSaveSalesLedger').on('click', function() {
			saveAssignedSalesLedgers();
		});

		// table search
		$('#btnSearch').click(
				function() {
					searchTable($("#search").val(), $('#tblSalesLedger'),
							'filter');
				});
		// select all checkbox in table tblProducts
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]:visible').prop(
							'checked', $(this).prop('checked'));
				});

	});
	
	function searchTable(inputVal, table, filter) {
		var filterBy = $("input[name='" + filter + "']:checked").val();
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

//	function searchTable(inputVal) {
//		var table = $('#tbodyemployeeProfileSalesLedgers');
//		table.find('tr').each(function(index, row) {
//			var allCells = $(row).find('td');
//			if (allCells.length > 0) {
//				var found = false;
//				allCells.each(function(index, td) {
//					if (index != 7) {
//						var regExp = new RegExp(inputVal, 'i');
//						if (regExp.test($(td).text())) {
//							found = true;
//							return false;
//						}
//					}
//				});
//				if (found == true)
//					$(row).show();
//				else
//					$(row).hide();
//			}
//		});
//	}

	EmployeeProfileSalesLedger.filter = function() {

		$('#tbodyemployeeProfileSalesLedgers').html(
				"<tr><td colspan='2' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : employeeProfileSalesLedgerContextPath + "/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val()
					},
					success : function(employeeProfiles) {

						if (employeeProfiles.length == 0) {
							$('#tbodyemployeeProfileSalesLedgers')
									.html(
											"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tbodyemployeeProfileSalesLedgers').html("");
							$
									.each(
											employeeProfiles,
											function(index, employeeProfile) {

												$(
														'#tbodyemployeeProfileSalesLedgers')
														.append(
																"<tr><td>"
																		+ employeeProfile.name
																		+ "</td><td><button type='button' class='btn btn-info' onclick='EmployeeProfileSalesLedger.showModalPopup($(\"#salesLedgersModal\"),\""
																		+ employeeProfile.pid
																		+ "\",0);'>Assign Sales Ledgers</button></td></tr>");

											});

						}
					}

				});
	}

//	function getSalesLedgerHierarchy() {
//		var salesLedgerHierarchyUrl = salesLedger.protocol + '//'
//				+ salesLedger.host + '/web/salesLedger-hierarchies';
//		$.ajax({
//			url : salesLedgerHierarchyUrl,
//			method : 'GET',
//			success : function(responseData) {
//				buildSalesLedgerTree(responseData);
//			},
//			error : function(xhr, error) {
//				console.log("error : " + error);
//			}
//		});
//	}

	// function buildSalesLedgerTree(responseData) {
	// var i, len, model, element;
	// i = 0;
	// salesLedgerTreeData = [];
	// for (i, len = responseData.length; i < len; i++) {
	// model = {};
	// element = responseData[i];
	// model.id = element["id"];
	// if (element["parentId"] === null) {
	// model.parent = "#"
	// } else {
	// model.parent = element["parentId"];
	// }
	// model.text = element["salesLedgerName"];
	// salesLedgerTreeData.push(model);
	// }
	// $('#tree-container').jstree({
	// 'core' : {
	// 'data' : salesLedgerTreeData
	// },
	// "checkbox" : {
	// "three_state" : false
	// },
	// "plugins" : [ "checkbox" ]
	// }).on('loaded.jstree', function(e, data) {
	// $(this).jstree("open_all");
	// });
	//
	// }

	function loadEmployeeProfileSalesLedger(employeeProfilePid) {
		employeeProfileSalesLedgerModel.employeeProfilePid = employeeProfilePid;
		
		$("input[name='filter'][value='all']").prop("checked", true);
		$("#search").val("");
		searchTable("", $('#tblSalesLedger'), 'filter');

		// clear all check box
		$("#salesLedgerCheckboxes input:checkbox").attr('checked', false);
		$.ajax({
			url : employeeProfileSalesLedgerContextPath + "/"
					+ employeeProfilePid,
			type : "GET",
			success : function(data) {
//				productGroupModel.pid = pid;
				if (data) {
					$.each(data, function(index, salesLedger) {
						$(
								"#salesLedgerCheckboxes input:checkbox[value="
										+ salesLedger.pid + "]").prop("checked",
								true);
					});
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
		
		
		
		
		console.log(employeeProfileSalesLedgerModel.employeeProfilePid);
		// un select all
		$('#tree-container').jstree("deselect_all");
		$.ajax({
			url : employeeProfileSalesLedgerContextPath + "/"
					+ employeeProfilePid,
			type : "GET",
			success : function(salesLedgers) {
				var salesLedgerIds = [];
				if (salesLedgers) {
					$.each(salesLedgers, function(index, salesLedger) {
						salesLedgerIds.push(salesLedger.id);
					});
					$('#tree-container').jstree('select_node', salesLedgerIds); // node
					// ids
					// that
					// you
					// want
					// to
					// check
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedSalesLedgers() {
		$(".error-msg").html("");
		var selectedSalesLedger = "";

		$.each($("input[name='salesLedger']:checked"), function() {
			selectedSalesLedger += $(this).val() + ",";
		});

		if (selectedSalesLedger == "") {
			$(".error-msg").html("Please select sales ledger");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$
				.ajax({
					url : employeeProfileSalesLedgerContextPath + "/save",
					type : "POST",
					data : {
						employeeProfilePid : employeeProfileSalesLedgerModel.employeeProfilePid,
						assignedSalesLedgers : "" + selectedSalesLedger,
					},
					success : function(status) {
						$("#salesLedgersModal").modal("hide");
						onSaveSuccess(status);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					},
				});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.salesLedger = employeeProfileSalesLedgerContextPath;
	}

	EmployeeProfileSalesLedger.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadEmployeeProfileSalesLedger(id);
				break;
			}
		}
		el.modal('show');
	}

	EmployeeProfileSalesLedger.closeModalPopup = function(el) {
		el.modal('hide');
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