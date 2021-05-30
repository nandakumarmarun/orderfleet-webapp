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
		
			TerritoryWiseSales.view()
			
	
		
	});
	
	function downloadXls() {
		// Avoid last column in each row
		
		var excelName = "Multiple_Location_AccountProfile";
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
	
	$(".loader").addClass('show');
		
		$.ajax({
			url : territoryWiseAccountContextPath + "/loadNoOfLevels",
			type : "GET",
			beforeSend : function() {
				// Show image container
				$("#loader").modal('show');

			},
			success : function(loadNoOfLevels) {
				TerritoryWiseSales.getLocationhierarchyAccountProfile(loadNoOfLevels);
				$("#loader").modal('hide');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
		
}
	
	
	
	TerritoryWiseSales.getLocationhierarchyAccountProfile= function(loadNoOfLevels) {
		
		$('.myprogress').css('width', '0');
        $('.msg').text('');
		
		$('#tBodyLocationHierarchyAccountProfile').html("<tr><td colspan='5' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : territoryWiseAccountContextPath + "/view",
					type : 'GET',
//					xhr: function () {
//                        var xhr = new window.XMLHttpRequest();
//                        xhr.upload.addEventListener("progress", function (evt) {
//                            if (evt.lengthComputable) {
//                                var percentComplete = evt.loaded / evt.total;
//                                percentComplete = parseInt(percentComplete * 100);
//                                $('.myprogress').text(percentComplete + '%');
//                                $('.myprogress').css('width', percentComplete + '%');
//                            }
//                        }, false);
//                        return xhr;
//                    },
					success : function(locationHierarchyAccounts) {
						
						
						$('#tHeadLocationHierarchyAccountProfile').html("<tr><th style='min-width: 80px;'>Account Profile</th><th>Location</th>");
						$('#tBodyLocationHierarchyAccountProfile').html("");
						
						if (locationHierarchyAccounts.length == 0) {
							$('#tBodyLocationHierarchyAccountProfile')
								.html(
									"<tr><td colspan='2' align='center'>No data available</td></tr>");
							return;
						}
					
						$.each(locationHierarchyAccounts,function(index, lha){
							
							var levelTr="";
							
							$.each(lha.locations,function(index, loc){
								
								levelTr += "<tr><td></td><td>"+loc+"</td></tr>";
								
							});
							
							
							$('#tBodyLocationHierarchyAccountProfile')
							.append("<tr><td>"
									+ lha.accountProfileName
									+ "</td><td>"+levelTr+"</td>" 
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