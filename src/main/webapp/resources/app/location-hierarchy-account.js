if (!this.TerritoryWiseSales) {
	this.TerritoryWiseSales = {};
}

(function() {
	'use strict';

	var territoryWiseAccountContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		
		
		
		
		
		$('#btnDownload').on('click', function() {
			var tblLocationHierarchyAccountProfile = $("#tblLocationHierarchyAccountProfile tbody");
			
			if (tblLocationHierarchyAccountProfile
					.children().length == 0) {
				alert("no values available");
				return;
			}
			if (tblLocationHierarchyAccountProfile[0].textContent == "No data available") {
				alert("no values available");
				return;
			}
			downloadXls();
			
		});
		
		$('#btnSave').on('click', function() {

			TerritoryWiseSales.save()
			
		});
		
		$('#btnView').on('click', function() {

			TerritoryWiseSales.view()
			
		});
		
	});
	
	function downloadXls() {
		// Avoid last column in each row
		
		var excelName = "AccountProfile_Teritories";
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblLocationHierarchyAccountProfile'),excelName);
		 
	}
	
	
TerritoryWiseSales.save= function(){
	
	$(".loader").addClass('show');
		
		$.ajax({
			url : territoryWiseAccountContextPath + "/save",
			type : "GET",
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			success : function(loadNoOfLevels) {
				$("#loader").modal('hide');
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
}

TerritoryWiseSales.view= function(){
	
		
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadNoOfLevels",
			type : "GET",
			success : function(loadNoOfLevels) {
				TerritoryWiseSales.getLocationhierarchyAccountProfile(loadNoOfLevels);		
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
}
	
	
	
	TerritoryWiseSales.getLocationhierarchyAccountProfile= function(loadNoOfLevels) {
		
		
		$('#tBodyLocationHierarchyAccountProfile').html("<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : territoryWiseAccountContextPath + "/view",
					type : 'GET',
					success : function(locationHierarchyAccounts) {
						
						var levelTh="";
						
						var i;
						for(i=loadNoOfLevels.length;i>0;i--){
							levelTh += "<th>Level_"+i+"</th>";
						}
						
						var colspan= loadNoOfLevels.length;
						$('#tHeadLocationHierarchyAccountProfile').html("<tr><th style='min-width: 80px;'>Account Profile</th>"+levelTh+"");
						$('#tBodyLocationHierarchyAccountProfile').html("");
						
						if (locationHierarchyAccounts.length == 0) {
							$('#tBodyLocationHierarchyAccountProfile')
								.html(
									"<tr><td colspan='"+colspan+"' align='center'>No data available</td></tr>");
							return;
						}
					
						$.each(locationHierarchyAccounts,function(index, lha){
							
							var levelTd="";
							var levelArray="";
							for(i=0;i<loadNoOfLevels.length;i++){
								
								if(lha.locations!=null){
								
								levelArray=lha.locations.split("~");
								
								// var location=levelArray[i].replace(" ", "_");
								
								levelTd += "<td>"+levelArray[i].replace(/\s+/g,'_')+"</td>";
								}else{
									levelTd += "<td>-</td>";
								}
								
							}
							
							
							$('#tBodyLocationHierarchyAccountProfile')
							.append("<tr><td>"
									+ lha.accountProfileName
									+ "</td>" + levelTd + "</td>" 
									+ "</tr>");
						});
						
					},
					error : function(xhr, error) {
						console.log("Error Report : " + error)
					}
				});
	}
	
	function onChangeFunctions(){
		
	
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadNoOfLevels",
			type : "GET",
			success : function(loadNoOfLevels) {
				console.log(loadNoOfLevels);
				
				
				$
				.each(
						loadNoOfLevels,
						function(index, level) {
							
							 $( "#dbLevel"+level).change(function() {
							    changeLevel(level, $( "#dbLevel"+level).val(),loadNoOfLevels.length);
							 });
							
						});
				
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
	}
	
	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}
	
	function changeLevel(level, territoryId,levelLength){
		// alert(level+"----"+territoryId)
		
		let parentIds = $('#dbLevel'+level).val();
		if ("-1" == parentIds) {
			parentIds = $('#dbLevel'+level+' option').map(function() {
				return $(this).val();
			}).get().join(',');
		}
		
		$(".loader").addClass('show');
		
		
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadChildLevels",
			type : "GET",
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			data : {
				level : level,
				territoryIds : parentIds
			},
			success : function(locationLevels) {
				console.log(locationLevels);
				
				$
				.each(
						locationLevels,
						function(index, locationLevelLevel) {
							
							$("#dbLevel"+locationLevelLevel.level).html("<option value='-1'>All</option>")
							$.each(locationLevelLevel.locationDtos, function(key, loc) {
								$("#dbLevel"+locationLevelLevel.level).append(
										"<option value='" + loc.id + "'>"
												+ loc.name + "</option>");
							});
							
							
							
							if(levelLength==locationLevelLevel.level){
								$("#dbAccount").html("<option value='-1'>All Account</option>")
								$.each(locationLevelLevel.accountProfileDTOs, function(key, acc) {
									$("#dbAccount").append(
											"<option value='" + acc.pid + "'>"
													+ acc.name + "</option>");
								});
								
							}
							
							
						});

				$("#loader").modal('hide');	
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function loadAllLevels(){
		
		
		$(".loader").addClass('show');
		
		
			$.ajax({
				url : territoryWiseAccountContextPath + "/loadAllLevels",
				type : "GET",
				beforeSend : function() {
					// Show image container
					$("#loader").modal('show');

				},
				success : function(locationLevels) {
					console.log(locationLevels);
					
					$
					.each(
							locationLevels,
							function(index, locationLevelLevel) {
								
								$("#dbLevel"+locationLevelLevel.level).html("<option value='-1'>All</option>")
								$.each(locationLevelLevel.locationDtos, function(key, loc) {
									$("#dbLevel"+locationLevelLevel.level).append(
											"<option value='" + loc.id + "'>"
													+ loc.name + "</option>");
								});
								
								
								
								if(locationLevels.length==locationLevelLevel.level){
									$("#dbAccount").html("<option value='-1'>All Account</option>")
									$.each(locationLevelLevel.accountProfileDTOs, function(key, acc) {
										$("#dbAccount").append(
												"<option value='" + acc.pid + "'>"
														+ acc.name + "</option>");
									});
									
								}
								
								
							});

					$("#loader").modal('hide');	
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
			
		 
	}

	TerritoryWiseSales.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

// function downloadXls() {
// // Avoid last column in each row
// $("#tblTerritoryWiseSales th:first-child, #tblTerritoryWiseSales
// td:first-child").hide();
//		
// var excelName = "territoryWiseAccounts";
// var table2excel = new Table2Excel();
// table2excel.export(document.getElementById('tblTerritoryWiseSales'),excelName);
// $("#tblTerritoryWiseSales th:first-child, #tblTerritoryWiseSales
// td:first-child").show();
// }
//	

	

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