if (!this.FeatureData) {
	this.FeatureData = {};
}
(function() {
	'use strict';

	var featureDataContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var companyPid;
	var userCount;
	$(document).ready(function() {

		companyPid = getParameterByName('companyPid');
		userCount = getParameterByName('userCount');
		console.log(userCount);
		setTimeout(FeatureData.saveDefaultData(companyPid, userCount), 5000);
		$('#loadDashboard').on('click', function() {
			FeatureData.loadDashboard();
		});
	});

	FeatureData.loadDashboard = function() {
		window.location = location.protocol + '//' + location.host
				+ "/web/dashboard";
	}

	FeatureData.saveDefaultData = function(companyPid, userCount) {
		console.log(companyPid);
		console.log(userCount);
		createUsers(companyPid, userCount);
	}

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

	function createUsers(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/create-users",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				console.log(data);
				if (data == true) {
					$("#tdUsersrefresh").css("display", "none");
					$("#tdUsersok").css("display", "block");
					$("#progressBarForFeatures").css("width", "9%");
					$("#progressBarForFeatures").html("9%");
				}
				setTimeout(defaultSetUp(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var defaultSetUp = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/default-set-up",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdDefaultSetUprefresh").css("display", "none");
					$("#tdDefaultSetUpok").css("display", "block");
					$("#progressBarForFeatures").css("width", "18%");
					$("#progressBarForFeatures").html("18%");
				}
				setTimeout(createEmployees(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var createEmployees = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/create-employees",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdEmployeerefresh").css("display", "none");
					$("#tdEmployeeok").css("display", "block");
					$("#progressBarForFeatures").css("width", "27%");
					$("#progressBarForFeatures").html("27%");
				}
				setTimeout(employeeHierarchy(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var employeeHierarchy = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/employee-hierarchy-setup",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdEmployeeHierarchyrefresh").css("display", "none");
					$("#tdEmployeeHierarchyok").css("display", "block");
					$("#progressBarForFeatures").css("width", "36%");
					$("#progressBarForFeatures").html("36%");
				}
				setTimeout(documentSetUp(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var documentSetUp = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/setup-document-company-data",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdDocumentSetUprefresh").css("display", "none");
					$("#tdDocumentSetUpok").css("display", "block");
					$("#progressBarForFeatures").css("width", "45%");
					$("#progressBarForFeatures").html("45%");
				}
				setTimeout(activitySetUp(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var activitySetUp = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/setup-activity-company-data",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdActivitySetUprefresh").css("display", "none");
					$("#tdActivitySetUpok").css("display", "block");
					$("#progressBarForFeatures").css("width", "54%");
					$("#progressBarForFeatures").html("54%");
				}
				setTimeout(employeeToLocationAssigned(companyPid, userCount),
						1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var employeeToLocationAssigned = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/assign-employee-to-locations",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdEmployeeToLocationAssignedrefresh").css("display",
							"none");
					$("#tdEmployeeToLocationAssignedok")
							.css("display", "block");
					$("#progressBarForFeatures").css("width", "63%");
					$("#progressBarForFeatures").html("63%");
				}
				setTimeout(defaultUserAssigned(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var defaultUserAssigned = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/assign-to-users",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdDefaultAssignedrefresh").css("display", "none");
					$("#tdDefaultAssignedok").css("display", "block");
					$("#progressBarForFeatures").css("width", "72%");
					$("#progressBarForFeatures").html("72%");
				}
				setTimeout(dashboard(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	var dashboard = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/dashboard-features",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdDashboardrefresh").css("display", "none");
					$("#tdDashboardok").css("display", "block");
					$("#progressBarForFeatures").css("width", "81%");
					$("#progressBarForFeatures").html("81%");
				}
				setTimeout(documentAssigned(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var documentAssigned = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/assign-document-company-data",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdDocumentAssignedrefresh").css("display", "none");
					$("#tdDocumentAssignedok").css("display", "block");
					$("#progressBarForFeatures").css("width", "90%");
					$("#progressBarForFeatures").html("90%");
				}
				setTimeout(menuItem(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var menuItem = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/assign-menuItem-to-users",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdMenuItemrefresh").css("display", "none");
					$("#tdMenuItemok").css("display", "block");
					$("#progressBarForFeatures").css("width", "95%");
					$("#progressBarForFeatures").html("95%");
				}
				setTimeout(mobileMenuItem(companyPid, userCount), 1000);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	var mobileMenuItem = function(companyPid, userCount) {
		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/assign-mobilemenuItem-to-users",
			method : 'POST',
			async : false,
			data : {
				userCount : userCount,
				companyPid : companyPid,
			},
			success : function(data) {
				if (data == true) {
					$("#tdMobileMenuItemrefresh").css("display", "none");
					$("#tdMobileMenuItemok").css("display", "block");
					$("#progressBarForFeatures").css("width", "100%");
					$("#progressBarForFeatures").html("100%");
					$("#loadDashboard").removeAttr('disabled');
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
})();