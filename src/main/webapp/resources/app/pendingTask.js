// Create a InvoiceWiseReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.PendingTask) {
	this.PendingTask = {};
}

(function() {
	'use strict';

	var pendingTaskContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	var invoiceWiseReportPid = null;
	var taskPid = null;
	var userPid = null;
	var date = null;

	$(document).ready(function() {

		PendingTask.filter();

	});

	PendingTask.filter = function() {

		console.log("entered here.....");
		$('#tBodyInvoiceWiseReport').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : pendingTaskContextPath + "/filter",
					type : 'GET',

					success : function(invoiceWiseReports) {

						$('#tBodyInvoiceWiseReport').html("");

						if (invoiceWiseReports.length == 0) {
							$('#tBodyInvoiceWiseReport')
									.html(
											"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}

						$
								.each(
										invoiceWiseReports,
										function(index, invoiceWiseReport) {
											$('#tBodyInvoiceWiseReport')
													.append(
															"<tr style='"

																	+ "' data-id='"
																	+ invoiceWiseReport.pid
																	+ "' data-parent=\"\"><td class='tableexport-string target'>"
																	+ invoiceWiseReport.empName
																	+ "</td><td>"
																	+ invoiceWiseReport.userLogin
																	+ "</td><td class=tableexport-string target'>"
																	+ invoiceWiseReport.territory
																	+ "</td><td>"
																	+ invoiceWiseReport.unattendedTask
																	+ "</td></tr>");
											var series=0;
											$
													.each(
															invoiceWiseReport.taskList,

															function(index,
																	executionDetail) {
																series++;
																console.log("number:"+series)
																$(
																		'#tBodyInvoiceWiseReport')
																		.append(
																				"<tr class='tableexport-ignore' style='background: rgba(225, 225, 225, 0.66);'"

																						+ "' data-id='"
																						+ executionDetail.pid
																						+ "1' data-parent='"
																						+ invoiceWiseReport.pid
																						+ "'>" 
																						+"<td><div class='col-md-2'>" 
																								+series
																								+"</div></td>"
																					    +"<td class='tableexport-string target' colspan=4 ><div class='col-md-2'><label style='font-size:15px;'>AccountType: </label> "
																				    	+executionDetail.accountType
																						+ "</div><div class='col-md-2'><label style='font-size:15px;'>AccountProfile :</label>"
																						+ executionDetail.accountProfile
																						+ "</div><div class='col-md-2'><label style='font-size:15px;'>Activity:</label>"
																						+ executionDetail.activity
																						+ "</div><div class='col-md-2'><label style='font-size:15px;'>Assigned Date:</label><br>"
																						+ formatDate(
																								executionDetail.date,
																								'MMM DD YYYY')
																						+ "</div>"
																						+ "<div class='col-md-4' ><button type='button' class='btn btn-blue'onclick = 'PendingTask.assignTask(\""
																						+ executionDetail.taskPid
																						+ "\",\""
																						+ executionDetail.userPid
																						+ "\");'>Assign</button>"
																						+ "&nbsp;&nbsp;<button type='button' class='btn btn-blue' onclick = 'PendingTask.DeactivateTask(\""
																						+ executionDetail.pid
																						+ "\",\""
																						+ !executionDetail.activated
																						+ "\");'>Deactivate</button>"
																						+ "</div></td>></tr>");
															});

											//											
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

	PendingTask.assignTask = function(taskPid, userPid) {
		date = $("#txtFromDate").val();

		console.log("Reached here" + taskPid, userPid, date);
		if(date)
		{
		$.ajax({
			url : pendingTaskContextPath + "/forward-task-plan",
			method : 'POST',
			data : {
				userPid : userPid,
				taskPid : taskPid,
				date : date
			},
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			success : function(response) {

				
				var r = confirm("Tasks successfully reScheduled to :"+date);
              if (r == true) {
            	  onSaveSuccess(response);
                }
           
			},
			error : function(xhr, error) {
				console.log("error loading user planned tasks.");
				$('.alert').addClass('alert-danger');
				$(".alert > p").html(error.responseText);
				$('.alert').show();

			}
		});
			}
		else{
			alert("select a date to schedule")
		}
	}
	PendingTask.DeactivateTask = function(pid, activated) {
		pid = pid;
		activated = activated;

		if (confirm("Are you ready to confirm?")) {
			$.ajax({
				url : pendingTaskContextPath + "/" + pid + "/" + activated,
				method : 'POST',
				contentType : "application/json ; charset:utf-8",

				success : function(data) {
					
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					console.log("error on deactivating.");
				}
			});
		}
	}
	PendingTask.closeModalPopup = function(el) {
		el.modal('hide');
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = pendingTaskContextPath;
	}

})();