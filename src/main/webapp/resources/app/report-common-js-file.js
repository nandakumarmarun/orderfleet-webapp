var dashboardEmployees = [];
var otherEmployees = [];
var allEmployeesMap = new Object();

function getEmployees(fixEmpPid) {
	var sltEmpVal = $("#dbEmployee").val();
	$("#dbEmployee").children().remove();
	$.ajax({
		async: false,
		url : location.protocol + '//' + location.host
				+ "/web/get-hierarchical-employees",
		method : 'GET',
		success : function(data) {
			allEmployeesMap = data;
			$.each(data, function(key, value) {
				if (key == "DASHBOARD") {
					if (sltEmpVal == "Dashboard Employee") {
						appendDataToSelectBox(value, "Dashboard Employee",
								"All Dashboard Employee");
					} else {
						appendDataToSelectBox(value, "no",
								"Select Dashboard Employee");
					}
					dashboardEmployees = value;
				}
				if (key == "OTHERS") {
					otherEmployees = value;
				}
			});
			if (fixEmpPid != null && fixEmpPid != "") {
				$("#dbEmployee").val(fixEmpPid);
			}
		},
		error : function(xhr, error) {
			onError(xhr, error);
		}
	});
}

function appendMapDataToSelectBox(data, key, title) {
	$("#dbEmployee").children().remove();
	$("#dbEmployee").append(
			'<option value="' + key + '">' + title + '</option>');
	$.each(data,
			function(key, value) {
				$("#dbEmployee").append(
						'<optgroup style="color: #1f30ff;" label="---' + key
								+ '---">');
				$.each(value, function(index, employee) {
					$("#dbEmployee").append(
							'<option value="' + employee.pid + '">'
									+ employee.name + '</option>');
				});
				if (key == "DASHBOARD EMPLOYEES") {
					dashboardEmployees = value
				} else if (key == "OTHER EMPLOYEES") {
					otherEmployees = value
				}
			});
}

function appendDataToSelectBox(value, key, title) {
	$("#dbEmployee").children().remove();
	$("#dbEmployee").append(
			'<option value="' + key + '">' + title + '</option>');
	$.each(value, function(index, employee) {
		$("#dbEmployee").append(
				'<option value="' + employee.pid + '">' + employee.name
						+ '</option>');
	});
}

function GetOtherEmployees(obj, key, title) {
	appendDataToSelectBox(otherEmployees, key, title);
}

function GetDashboardEmployees(obj, key, title) {
	appendDataToSelectBox(dashboardEmployees, key, title);
}

function GetAllEmployees(obj, key, title) {
	appendMapDataToSelectBox(allEmployeesMap, key, title);
}
