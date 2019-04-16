if (!this.StageReportAccountGroups) {
	this.StageReportAccountGroups = {};
}

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		$('#rca_div').hide();
		let employeePid = getParameterByName('user-key-pid');
		// getEmployees(employeePid); // common js
		
		let stageName = getParameterByName('stage');
		if(stageName != null){
			$("#dbStage option:contains(" + stageName + ")").attr('selected', 'selected');
		}
		let dateValue = getParameterByName('date');
		if(dateValue != null){
			$('#dbDateSearch').val(dateValue);
		}
		
		$('#dbStage').multiselect({
			includeSelectAllOption : true,
		});
		$('#dbRca').multiselect({
			includeSelectAllOption : true,
		});
		
		$('#dbRca').multiselect({
			includeSelectAllOption : true,
		});
		
		$('#dbLocation').on('change', function() {
			loadAccountsByLocation(this.value);
		});
		
		$('#dbStageGroup').on('change', function() {
			loadStagesByStageGroup(this.value);
		});
		
		$('#dbNewStage').on('change', function() {
			var sel_stageTypeName = $(this).find('option:selected').attr("name");
			if(sel_stageTypeName === 'CLOSED_LOST'){
				$('#rca_div').show();
			}else{
				$('#rca_div').hide();
			}
			
			var stagePid = $(this).find('option:selected').val();
			console.log(stagePid);
			
			$
			.ajax({
				url : contextPath + "/web/stage-file-upload/find",
				type : 'GET',
				data : {
					pid : stagePid
				},
				success : function(stageFile) {
					console.log(stageFile);
					var result = stageFile.fileUploadName.split(";");
					var content = "";
					console.log(result);
					$.each(result, function(index, fileUploadName){
						content += '<label>'+fileUploadName+'</label>'+
								  '<input type="file" name="multipartFiles['+index+']" />';
					});
					$('#upFiles').html(content);
				},
				error : function(xhr, error) {
					console.log("Error finding stage file upload : " + error)
				}
			});

		});
		
		// save new stage
		$('#btnSaveNewStage').on('click', function() {
			saveChangedStageDetails();
		});
		
		$('#btnDownload').on('click',function() {
			var tblStageReportAccountGroups = $("#tblStageReportAccountGroups tbody");
			if (tblStageReportAccountGroups
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblStageReportAccountGroups[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
			
		});
		// load default
		StageReportAccountGroups.filter();
	});
	
	

	function downloadXls() {
		// Avoid last column in each row
		$("#tblStageReportAccountGroups th:last-child, #tblStageReportAccountGroups td:last-child").hide();
		var excelName = "stageReport";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblStageReportAccountGroups'),excelName);
		 $("#tblStageReportAccountGroups th:last-child, #tblStageReportAccountGroups td:last-child").show();
	}
	
	
	function loadAccountsByLocation(locPid){
		$
		.ajax({
			url : contextPath + "/web/stage-report-account-groups/accountProfile",
			type : 'GET',
			data : {
				locationPid : locPid
			},
			success : function(accounts) {
				$('#dbAccount').html('');
				$('#dbAccount').append('<option value="-1">All Contacts</option>');
				if (accounts != null && accounts.length > 0) {
					$.each(accounts, function(indx, account){
						$('#dbAccount').append('<option value='+ account.pid +'>'+ account.name +'</option>');
					});
				}
			},
			error : function(xhr, error) {
				console.log("Error loading account profile : " + error)
			}
		});
	}
	
	function loadStagesByStageGroup(sgPid){
		$
		.ajax({
			url : contextPath + "/web/stage-report-account-groups/stages",
			type : 'GET',
			data : {
				stageGroupPid : sgPid
			},
			success : function(stages) {
				if (stages != null && stages.length > 0) {
					$('#dbStage').empty(); 
					$.each(stages, function(indx, stage){
						$('#dbStage').append('<option value='+ stage.pid +'>'+ stage.name +'</option>');
					});
					$('#dbStage').multiselect('rebuild');
				}
			},
			error : function(xhr, error) {
				console.log("Error loading account profile : " + error)
			}
		});
	}
	
	StageReportAccountGroups.filter = function() {
		if ($('#dbDateSearch').val() == "SINGLE") {
			if ($("#txtFromDate").val() == "") {
				return;
			}
		}
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		loadReports();
	}
	
	function loadReports() {
		$('#tBodyStageReportAccountGroups').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		var stagePids = $('#dbStage').val();
		var locPids = $('#dbLocation').val();
		var accGroupPid=$('#dbAccountGroups').val();
		if(stagePids == null){
			stagePids = $('#dbStage option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		} else {
			stagePids = stagePids.join();
		}
		if("-1" == locPids){
			locPids = $('#dbLocation option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		}
		$
				.ajax({
					url : contextPath + "/web/stage-report-account-groups/filter",
					type : 'GET',
					data : {
						employeePid : $('#dbEmployee').val(),
						stagePid : stagePids,
						locationPid : locPids,
						accountGroupPid : $('#dbAccountGroups').val(),
						accountPid : $('#dbAccount').val(),
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
					},
					success : function(stages) {
						$('#lblCount').html("0");
						if (stages == null || stages == "") {
							$('#tBodyStageReportAccountGroups')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tBodyStageReportAccountGroups').html("");
						}
						if(stages.length == 0){
							$('#tBodyStageReportAccountGroups')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$('#lblCount').html(stages.length);
						$.each(stages, function(indx, stage){
							var rowHtml = "<tr>";
							var attachments = "";
							if(!jQuery.isEmptyObject(stage.files)){
								$.each(stage.files, function(key, value) {
									var url = contextPath + "/web/stage-report-account-groups/stage-header-files/" + key;
									attachments += '<a href="'+ url +'" style="color: blue;">'+ value +'</a><br />';
								});
							}
							var btnDetails = "";
							if(stage.dynamicDocumentHeaderPid){
								btnDetails = "<a href='' style='color: blue;' onclick='StageReportAccountGroups.getDynamicDocumentDetails(\""+ stage.dynamicDocumentHeaderPid +"\", event);'>"+ (stage.activityName == null ? "" : stage.activityName) +"</a><br />";
							}
							rowHtml += "<td>"+ convertDateTimeFromServer(stage.createdDate) +"</td>"
									+ "<td>"+ stage.accountProfileName +"</td>"
									+ "<td>"+ stage.stageName +"</td>"
									+ "<td>"+ stage.employeeProfileName +"</td>"
									+ "<td>"+ (stage.remarks == null ? "" : stage.remarks) +"</td>"
									+ "<td>"+ attachments +" <br />"+ btnDetails +"</td>"
									+ "<td>"+ (stage.value == null ? "" : stage.value) +"</td>"
									+ "<td>"+ (stage.quotationNo == null ? "" : stage.quotationNo) +"</td>"
									+ "<td><button type='button' class='btn btn-blue' onclick='StageReportAccountGroups.showAddNewStageModal("+ stage.id + ",\""+ stage.stageName+"\")'>Change</button></td>"
							rowHtml += "</tr>";
							$('#tBodyStageReportAccountGroups').append(rowHtml);
						});
						
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	
	StageReportAccountGroups.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}
	
	StageReportAccountGroups.getDynamicDocumentDetails = function(docHeaderPid, e) {
		e.preventDefault();
		$
		.ajax({
			url : contextPath + "/web/dynamic-documents/" + docHeaderPid,
			method : 'GET',
			success : function(data) {
				$('#lbl_documentNumber').text((data.documentNumberLocal == null ? "" : data.documentNumberLocal));
				$('#lbl_user').text((data.userName == null ? "" : data.userName));
				$('#lbl_activity').text((data.activityName == null ? "" : data.activityName));
				$('#lbl_account').text((data.accountName == null ? "" : data.accountName));
				$('#lbl_accountph').text((data.accountPhone == null ? "" : data.accountPhone));
				$('#lbl_document').text((data.documentName == null ? "" : data.documentName));
				$('#lbl_documentDate').text(
					convertDateTimeFromServer(data.createdDate));
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
	
	StageReportAccountGroups.showAddNewStageModal = function(stageHeaderId, stageName) {
		$('#hiddenSavedStageHeaderPid').val(stageHeaderId);
		$('#currentStage').html(stageName);
		$('#formSaveNewStage')[0].reset();
		$('#addNewStageModal').modal('show');
	}
	
	function saveChangedStageDetails() {
		if($('#dbNewStage').val() == "-1"){
			alert("Please select a stage");
			return;
		}
		$("input:file").each(function() {
			if($(this).val() == "") {
				$(this).remove();
			}
		});
		$('#formSaveNewStage').ajaxSubmit({
			beforeSend : function() {
				$("#progressbox").show();
				// clear everything
				$("#progressbar").width('0%');
				$("#message").empty();
				$("#percent").html("0%");
			},
			uploadProgress : function(event, position, total, percentComplete) {
				$("#progressbar").width(percentComplete + '%');
				$("#percent").html(percentComplete + '%');
				// change message text and % to red after 50%
				if (percentComplete > 50) {
					$("#message").html("<font color='green'>File Upload is in progress...</font>");
				}
			},
			success : function(response) {
				$("#progressbar").width('100%');
				$("#percent").html('100%');
				$("#message").html("<font color='blue'>File has been uploaded!</font>");
				$('#formSaveNewStage')[0].reset();
				window.location = contextPath + "/web/stage-report-account-groups";
			},
			complete : function(response) {
			},
			error : function(jqxhr) {
				$("#message").html("<font color='red'>"+ jqxhr.responseText +"</font>");
			}
		});
	}
	

	StageReportAccountGroups.sortTable = function(n) {
		var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
		table = document.getElementById("tblStageReportAccountGroups");
		switching = true;
		// Set the sorting direction to ascending:
		dir = "asc";
		/*
		 * Make a loop that will continue until no switching has been done:
		 */
		while (switching) {
			// Start by saying: no switching is done:
			switching = false;
			rows = table.getElementsByTagName("TR");
			/*
			 * Loop through all table rows (except the first, which contains
			 * table headers):
			 */
			for (i = 1; i < (rows.length - 1); i++) {
				// Start by saying there should be no switching:
				shouldSwitch = false;
				/*
				 * Get the two elements you want to compare, one from current
				 * row and one from the next:
				 */
				x = rows[i].getElementsByTagName("TD")[n];
				y = rows[i + 1].getElementsByTagName("TD")[n];
				/*
				 * Check if the two rows should switch place, based on the
				 * direction, asc or desc:
				 */
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
				/*
				 * If a switch has been marked, make the switch and mark that a
				 * switch has been done:
				 */
				rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
				switching = true;
				// Each time a switch is done, increase this count by 1:
				switchcount++;
			} else {
				/*
				 * If no switching has been done AND the direction is "asc", set
				 * the direction to "desc" and run the while loop again.
				 */
				if (switchcount == 0 && dir == "asc") {
					dir = "desc";
					switching = true;
				}
			}
		}
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD, YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}
	
	StageReportAccountGroups.selectAccountGroups = function(){
		$
		.ajax({
			url : contextPath + "/web/stage-report-account-groups/getAccountGroupsAccountProfiles",
			type : 'GET',
			data : {
				accountGroupPid : $('#dbAccountGroups').val(),
			},
			success : function(accountProfiles) {
				if (accountProfiles == null || accountProfiles == "") {
					$('#dbAccount')
						.html(
							"<option value='0'>No Contacts</option>");
					return;
				} else {
					console.log(accountProfiles);
					$('#dbAccount').html(" ");
				}
				if(accountProfiles.length == 0){
					$('#dbAccount')
					.html(
						"<option value='0'>No Contacts</option>");
					return;
				}
				
				var optionHtml = "<option value='-1'>All Contacts</option>";
				$.each(accountProfiles, function(indx, account){
					optionHtml += "<option value='"+account.pid+"'>"+account.name+"</option>";
				});
				$('#dbAccount').append(optionHtml);	
			},
			error : function(xhr, error) {
				console.log("Error sales report : " + error)
			}
		});
	}

})();