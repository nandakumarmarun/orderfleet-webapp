if (!this.CustomerJourney) {
	this.CustomerJourney = {};
}

(function() {
	'use strict';
	$(document).ready(function() {
		$('#btnApply').on('click', function() {
			doFilter();;
		});
		
		$('#btnDownload').on('click',function() {
			var tblCustomerJourney = $("#tblCustomerJourney tbody");
			if (tblCustomerJourney
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblCustomerJourney[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
			
		});
	});
	
	function downloadXls() {
		// Avoid last column in each row
		//$("#tblCustomerJourney th:last-child, #tblCustomerJourney td:last-child").hide();
		var excelName = "customerJourney";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblCustomerJourney'),excelName);
		 //$("#tblCustomerJourney th:last-child, #tblCustomerJourney td:last-child").show();
	}
	
	function doFilter() {
		loadReports();
	}
	
	function loadReports() {
		$('#tBodyCustomerJourney').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		let accountPids = $('#dbAccount').val();
		if("-1" == accountPids){
			accountPids = $('#dbAccount option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		}
		$
				.ajax({
					url : appContextPath + "/web/customer-journey/filter",
					type : 'GET',
					data : {
						accountPid : accountPids
					},
					success : function(stages) {
						if (stages == null || stages == "") {
							$('#tBodyCustomerJourney')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tBodyCustomerJourney').html("");
						}
						if(stages.length == 0){
							$('#tBodyCustomerJourney')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$('#lblCount').html(stages.length);
						$.each(stages, function(indx, stage){
							let rowHtml = "<tr>";
							let attachments = "";
							if(!jQuery.isEmptyObject(stage.files)){
								$.each(stage.files, function(key, value) {
									let url = appContextPath + "/web/customer-journey/stage-header-files/" + key;
									attachments += '<a href="'+ url +'" style="color: blue;">'+ value +'</a><br />';
								});
							}
							let btnDetails = "";
							if(stage.dynamicDocumentHeaderPid){
								btnDetails = "<a href='' style='color: blue;' onclick='CustomerJourney.getDynamicDocumentDetails(\""+ stage.dynamicDocumentHeaderPid +"\", event);'>"+ (stage.activityName == null ? "" : stage.activityName) +"</a><br />";
							}
							rowHtml += "<td>"+ formatDate(stage.createdDate, 'MMM DD, YYYY, h:mm:ss a') +"</td>"
									+ "<td>"+ stage.accountProfileName +"</td>"
									+ "<td>"+ stage.stageName +"</td>"
									+ "<td>"+ stage.employeeProfileName +"</td>"
									+ "<td>"+ (stage.remarks == null ? "" : stage.remarks) +"</td>"
									+ "<td>"+ attachments +" <br />"+ btnDetails +"</td>"
									+ "<td>"+ (stage.value == null ? "" : stage.value) +"</td>"
							rowHtml += "</tr>";
							$('#tBodyCustomerJourney').append(rowHtml);
						});
						
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	
	CustomerJourney.getDynamicDocumentDetails = function(docHeaderPid, e) {
		e.preventDefault();
		$
		.ajax({
			url : appContextPath + "/web/dynamic-documents/" + docHeaderPid,
			method : 'GET',
			success : function(data) {
				$('#lbl_documentNumber').text((data.documentNumberLocal == null ? "" : data.documentNumberLocal));
				$('#lbl_user').text((data.userName == null ? "" : data.userName));
				$('#lbl_activity').text((data.activityName == null ? "" : data.activityName));
				$('#lbl_account').text((data.accountName == null ? "" : data.accountName));
				$('#lbl_accountph').text((data.accountPhone == null ? "" : data.accountPhone));
				$('#lbl_document').text((data.documentName == null ? "" : data.documentName));
				$('#lbl_documentDate').text(formatDate(data.createdDate, 'MMM DD, YYYY, h:mm:ss a'));
				$('#divDynamicDocumentDetails').html("");
				$
					.each(
						data.filledForms,
						function(index, filledForm) {
							var table = '<table class="table  table-striped table-bordered"><tr><td colspan="2" style="font-weight: bold;">'
								+ filledForm.formName
								+ '</td></tr>';
							$
								.each(
									filledForm.filledFormDetails,
									function(index,
										formDetail) {
										table += "<tr><td>"
											+ formDetail.formElementName
											+ "</td><td>"
											+ (formDetail.value == null ? "" : formDetail.value)
											+ "</td>";
									});
							table += '</table>';
							$('#divDynamicDocumentDetails')
								.append(table);
						});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
		$('#viewDetailsModal').modal('show');
	}

	CustomerJourney.sortTable = function(n) {
		var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
		table = document.getElementById("tblCustomerJourney");
		switching = true;
		// Set the sorting direction to ascending:
		dir = "asc";
		/* Make a loop that will continue until
		no switching has been done: */
		while (switching) {
			// Start by saying: no switching is done:
			switching = false;
			rows = table.getElementsByTagName("TR");
			/* Loop through all table rows (except the
			first, which contains table headers): */
			for (i = 1; i < (rows.length - 1); i++) {
				// Start by saying there should be no switching:
				shouldSwitch = false;
				/* Get the two elements you want to compare,
				one from current row and one from the next: */
				x = rows[i].getElementsByTagName("TD")[n];
				y = rows[i + 1].getElementsByTagName("TD")[n];
				/* Check if the two rows should switch place,
				based on the direction, asc or desc: */
				if (dir == "asc") {
					if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
						// If so, mark as a switch and break the loop:
						shouldSwitch = true;
						break;
					}
				} else if (dir == "desc") {
					if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
						// If so, mark as a switch and break the loop:
						shouldSwitch = true;
						break;
					}
				}
			}
			if (shouldSwitch) {
				/* If a switch has been marked, make the switch
				and mark that a switch has been done: */
				rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
				switching = true;
				// Each time a switch is done, increase this count by 1:
				switchcount++;
			} else {
				/* If no switching has been done AND the direction is "asc",
				set the direction to "desc" and run the while loop again. */
				if (switchcount == 0 && dir == "asc") {
					dir = "desc";
					switching = true;
				}
			}
		}
	}
})();