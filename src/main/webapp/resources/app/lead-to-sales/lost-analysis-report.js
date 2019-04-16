if (!this.LostAnalysisReport) {
	this.LostAnalysisReport = {};
}

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		
		let dateValue = getParameterByName('date');
		if(dateValue != null){
			$('#dbDateSearch').val(dateValue);
		}
		
		
		$('#dbRca').multiselect({
			includeSelectAllOption : true,
		});
		
		

		//load default
		LostAnalysisReport.filter();
	});
	

	
	LostAnalysisReport.filter = function() {
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
		$('#tBodyLostAnalysisReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		var rcaPids = $('#dbRca').val();
		var accPids = $('#dbAccount').val();
		if(rcaPids == null){
			rcaPids = $('#dbRca option').map(function() {
		        return $(this).val();
		    }).get().join(',');
		} else {
			rcaPids = rcaPids.join();
		}
		var values = [];
		if(accPids == '-1'){
			var options = $('#dbAccount option');

				values = $.map(options ,function(option) {
								
							    return option.value;
							});
				values.splice(0, 1);
				accPids =values.join(',');
		}else{
			values[0] = accPids;
			accPids = values.join(',');
			
		}

		$
				.ajax({
					url : contextPath + "/web/lost-analysis-report/filter",
					type : 'GET',
					data : {
						rcaPid : rcaPids,
						accountPid : accPids,
						filterBy : $("#dbDateSearch").val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
					},
					success : function(stages) {
						$('#lblCount').html("0");
						if (stages == null || stages == "") {
							$('#tBodyLostAnalysisReport')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tBodyLostAnalysisReport').html("");
						}
						if(stages.length == 0){
							$('#tBodyLostAnalysisReport')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						$('#lblCount').html(stages.length);
						
//						var textvalues = [];
//						$('#dbRca :selected').each(function(i, selected) {
//						    textvalues[i] = $(selected).text();
//						});

						$.each(stages, function(indx, stage){
							var rowHtml = "<tr>";
							var attachments = "";
							if(!jQuery.isEmptyObject(stage.files)){
								$.each(stage.files, function(key, value) {
									var url = contextPath + "/web/stage-report/stage-header-files/" + key;
									attachments += '<a href="'+ url +'" style="color: blue;">'+ value +'</a><br />';
								});
							}
							var btnDetails = "";
							if(stage.dynamicDocumentHeaderPid){
								btnDetails = "<a href='' style='color: blue;' onclick='StageReport.getDynamicDocumentDetails(\""+ stage.dynamicDocumentHeaderPid +"\", event);'>"+ (stage.activityName == null ? "" : stage.activityName) +"</a><br />";
							}
							var rca_reason = '';
							
							$.each(stage.rcas, function(key, value){
//								if(textvalues.length == 0){
									rca_reason += value +"<br>";
//								}else{
//									if(jQuery.inArray(value, textvalues) !== -1){
//										rca_reason += value +"<br>";
//									}
//								}
							})
							rowHtml += "<td>"+ stage.accountProfileName +"</td>"
									+ "<td>"+ rca_reason +"</td>"
									+ "<td>"+ stage.employeeProfileName +"</td>"
									+ "<td>    </td>"
									+ "<td>"+ convertDateTimeFromServer(stage.createdDate) +"</td>"
									
							rowHtml += "</tr>";
							$('#tBodyLostAnalysisReport').append(rowHtml);
						});
						
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	
	LostAnalysisReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}




	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD, YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

})();