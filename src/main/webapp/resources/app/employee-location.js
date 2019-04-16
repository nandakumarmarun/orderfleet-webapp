// Create a EmployeeProfileLocation object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.EmployeeProfileLocation) {
	this.EmployeeProfileLocation = {};
}

(function() {
	'use strict';

	var employeeProfileLocationContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var employeeProfileLocationModel = {
		employeeProfilePid : null
	};
	
	var locationTreeData;

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");
		
		$('#btnSearch').click(function() {
			searchTable($("#search").val());
		});

		$('#btnSaveLocation').on('click', function() {
			saveAssignedLocations();
		});
		
		//on-load fetch location hierarchy
		getLocationHierarchy();
		
		$('#cbSelectAll').change(function() {
			if($(this).is(":checked")) {
				$('#tree-container').jstree("select_all");
			} else {
				$('#tree-container').jstree("deselect_all");
			}
		});
	});
	
	function searchTable(inputVal) {
		var table = $('#tbodyemployeeProfileLocations');
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
	
	function getLocationHierarchy() {
		var locationHierarchyUrl = location.protocol + '//' + location.host + '/web/location-hierarchies';
		  $.ajax({
	          url:  locationHierarchyUrl,
	          method: 'GET',
	          success: function(responseData){
	        	buildLocationTree(responseData);
	          },
	          error: function(xhr, error){
	          	console.log("error : " + error);
	          }
	      });
	}
	
	function buildLocationTree(responseData){
		var i, len, model, element;
		i = 0;
		locationTreeData = [];
		for (i, len = responseData.length; i < len; i++) {
			model = {};
			element = responseData[i];
			model.id = element["id"];
			if(element["parentId"] === null) {
				model.parent = "#"
			} else {
				model.parent = element["parentId"];
			}
			model.text = element["locationName"];
			locationTreeData.push(model);
		}
		$('#tree-container').jstree({
			'core' : {
				'data' : locationTreeData
			},
			"checkbox" : {
				"three_state": false
			},
			"plugins" : ["checkbox"]
		}).on('loaded.jstree', function (e, data) {
			$(this).jstree("open_all");
		});
		
	  }

	function loadEmployeeProfileLocation(employeeProfilePid) {
		employeeProfileLocationModel.employeeProfilePid = employeeProfilePid;
		//un select all
		$('#tree-container').jstree("deselect_all");
		$.ajax({
			url : employeeProfileLocationContextPath + "/" + employeeProfilePid,
			type : "GET",
			success : function(locations) {
				var locationIds = [];
				if (locations) {
					$.each(locations, function(index, location) {
						locationIds.push(location.id);
					});
					$('#tree-container').jstree('select_node', locationIds); //node ids that you want to check
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function saveAssignedLocations() {
		var checked_ids = $('#tree-container').jstree('get_selected');
		$(".error-msg").html("");
		
		if (checked_ids == "") {
			$(".error-msg").html("Please select Locations");
			return;
		}
		// $(".error-msg").html("Please wait.....");
		$.ajax({
			url : employeeProfileLocationContextPath + "/save",
			type : "POST",
			data : {
				employeeProfilePid : employeeProfileLocationModel.employeeProfilePid,
				assignedLocations : "" + checked_ids,
			},
			success : function(status) {
				$("#locationsModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = employeeProfileLocationContextPath;
	}

	EmployeeProfileLocation.showModalPopup = function(el, id, action) {
		if (id) {
			switch (action) {
			case 0:
				loadEmployeeProfileLocation(id);
				break;
			}
		}
		el.modal('show');
	}

	EmployeeProfileLocation.closeModalPopup = function(el) {
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