// Create a Overdue object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Overdue) {
	this.Overdue = {};
}

(function() {
	'use strict';

	var overdueContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		loadData();
	});

	function loadData() {
		if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
			return;
		}
		$('#tHeadOverdue').html("");
		$('#tBodyOverdue').html("<tr><td align='center'>Please wait...</td></tr>");
		$.ajax({
					url : overdueContextPath + "/filter",
					type : 'GET',
					success : function(overdues) {
						$('#tBodyOverdue').html("");
						if (overdues.length == 0) {
							$('#tBodyOverdue').html("<tr><td align='center'>No data available</td></tr>");
							return;
						}
						
						// create table header
						createHeader(overdues[0].overdueReportPeriods);
//						 var percentage = ((achieved/scheduled)*100).toFixed(2);
						$.each(overdues, function(index, overdue) {
							var tr = "<tr data-id='"+ overdue.pid + "' data-parent=''><td>" + overdue.name + "</td>"
							tr += createPeriodsTDs(overdue.overdueReportPeriods);
							tr += "</tr>";
							$('#tBodyOverdue').append(tr);
							
							createAccountHierarchy(overdue.pid , overdue.overdueReportAccounts);
							createLocationHierarchy(overdue.pid , overdue.overdueReportLocations);
						});
						
						$('.collaptable')
						.aCollapTable(
								{
									startCollapsed : true,
									addColumn : false,
									plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
									minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
								});
					}
				});
		
	}
	
	function createHeader(overdueReportPeriods) {
		$('#tHeadOverdue').html("<tr><th style='font-weight: bold;color: black;'>Location/Account</th></tr>");
		$.each(overdueReportPeriods, function(key, period) {
			$('#tHeadOverdue tr').append("<th style='font-weight: bold;color: black;'>" + period.name + "</th>");
		});
	}

	function createLocationHierarchy(parentId, overdueReportLocations) {
		if(overdueReportLocations != null && overdueReportLocations.length > 0){
			$.each(overdueReportLocations, function(key, overdueLocation) {
				var tr = "<tr style='background: azure;' data-id='"+ overdueLocation.pid + "' data-parent='"+parentId+"'><td>" + overdueLocation.name + "</td>";
				tr += createPeriodsTDs(overdueLocation.overdueReportPeriods);
				tr += "</tr>";
				$('#tBodyOverdue').append(tr);
				
				createLocationHierarchy(overdueLocation.pid, overdueLocation.overdueReportLocations);
				createAccountHierarchy(overdueLocation.pid, overdueLocation.overdueReportAccounts);
			});
		}
	}

	function createAccountHierarchy(parentId, overdueReportAccounts) {
		if(overdueReportAccounts != null && overdueReportAccounts.length > 0){
			$.each(overdueReportAccounts, function(key, overdueAccount) {
				var tr = "<tr style='background: rgba(255, 235, 205, 0.62);' data-id='"+ overdueAccount.pid + "' data-parent='"+parentId+"'><td>" + overdueAccount.name + "</td>";
				tr += createPeriodsTDs(overdueAccount.overdueReportPeriods);
				tr += "</tr>";
				$('#tBodyOverdue').append(tr);
				
				createBill(overdueAccount.pid, overdueAccount.overdueReportBills);
			});
		}
	}
	
	function createBill(parentId, overdueReportBills) {
		if(overdueReportBills != null && overdueReportBills.length > 0){
			var tbale = "<tr data-parent='"+parentId+"' ><td colspan='3'><table class='table  table-striped table-bordered'><thead><tr><th>Voucher Number</th><th>Voucher Date</th><th>Amount</th></tr></thead><tbody>";
			$.each(overdueReportBills, function(key, bill) {
				var percentage = (bill.amount ).toFixed(2);
				tbale += "<tr><td>" + bill.billNumber + "</td><td >" + bill.date + "</td><td >" + percentage + "</td></tr>";
			});
			tbale += "</tbody></table></td></tr>";
			$('#tBodyOverdue').append(tbale);
		}
	}

	function createPeriodsTDs(overdueReportPeriods) {
		var tds = "";
		$.each(overdueReportPeriods, function(key, period) {
			var percentage = (period.amount).toFixed(2);
			tds += "<td>" + percentage + "</td>";
		});
		return tds;
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