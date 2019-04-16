if (!this.CompitatorPriceTrend) {
	this.CompitatorPriceTrend = {};
}

(function() {
	'use strict';

	var compitatorPriceTrendContextPath = location.protocol + '//' + location.host
	+ location.pathname;

	$(document)
		.ready(
			function() {
				$("#txtToDate").datepicker({
					dateFormat : "dd-mm-yy"
				});
				$("#txtFromDate").datepicker({
					dateFormat : "dd-mm-yy"
				});

				$('#txtFromMonth').MonthPicker({
					MonthFormat : 'M, yy',
					ShowIcon : false
				});
				$('#txtToMonth').MonthPicker({
					MonthFormat : 'M, yy',
					ShowIcon : false
				});

				$('input[type=month]').MonthPicker().css('backgroundColor',
					'lightyellow');

				$('#btn').click(
					function() {
						searchTable($("#search").val(),
							$('#tbodyCompetitorPriceTrend'));
					});
				// filter by
				$('#btnSearch').click(
					function() {
						searchTable($("#search").val(),
							$('#tbodyCompetitorPriceTrend'));
					});

			});



	CompitatorPriceTrend.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOMMONTH") {
			$(".custom_date1").addClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").removeClass('show');
			$(".custom_month1").addClass('show');
			$(".custom_month2").addClass('show');
			$(".custom_month1").removeClass('hide');
			$(".custom_month2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "CUSTOMDATE") {
			$(".custom_month1").addClass('hide');
			$(".custom_month2").addClass('hide');
			$(".custom_month1").removeClass('show');
			$(".custom_month2").removeClass('show');
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$(".custom_month1").addClass('hide');
			$(".custom_month2").addClass('hide');
			$(".custom_month1").removeClass('show');
			$(".custom_month2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}
	}

	function searchTable(inputVal, table) {
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


	function convertLocalDateToServer(date) {
		if (date) {
			return moment(date).format('DD-MM-YYYY');
		} else {
			return "";
		}
	}

	CompitatorPriceTrend.filter = function() {
		$("#btnApply").attr("disabled", "disabled");
		var fromDate = $('#txtFromMonth').MonthPicker('GetSelectedMonthYear');
		var toDate = $('#txtToMonth').MonthPicker('GetSelectedMonthYear');
		if ($('#dbProductGroup').val() == "-1") {
			alert("Please Select Product Group");
			$("#btnApply").removeAttr("disabled");
			return;
		}
		if ($('#dbUser').val() == "-1") {
			alert("Please Select User")
			$("#btnApply").removeAttr("disabled");
			return;
		}
		if ($('#dbDateSearch').val() == "DATE") {
			alert("Please Select Date")
			$("#btnApply").removeAttr("disabled");
			return;
		}
		if ($('#dbDateSearch').val() == "CUSTOMDATE") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				$("#btnApply").removeAttr("disabled");
				return;
			}
			fromDate = $("#txtFromDate").val();
			toDate = $("#txtToDate").val();
		}

		if ($('#dbDateSearch').val() == "CUSTOMMONTH") {

			if (fromDate === null || toDate === null) {
				$("#btnApply").removeAttr("disabled");
				return;
			}
			var [fromMonth, fromYear] = fromDate.split('/');
			var [toMonth, toYear] = toDate.split('/');
			fromDate = new Date(fromYear, fromMonth - 1, 1);
			toDate = new Date(toYear, toMonth, 0);
			fromDate = convertLocalDateToServer(fromDate);
			toDate = convertLocalDateToServer(toDate);
		}


		$
			.ajax({
				url : compitatorPriceTrendContextPath + "/filter",
				type : 'GET',
				data : {
					userPid : $("#dbUser").val(),
					productGroupPid : $("#dbProductGroup").val(),
					filterBy : $("#dbDateSearch").val(),
					fromDate : fromDate,
					toDate : toDate
				},
				success : function(compitatorPriceTrends) {
					$('#cptReport').html("");
					$.each(compitatorPriceTrends, function(key, compitatorPriceTrend) {
						if (compitatorPriceTrend.length > 0) {
							$('#cptReport').append('<div class="row col-xs-12" style="font-size: x-large;; text-align: center; font-weight: bolder; color: black;">' + key + '</div>');
							createReportTable(compitatorPriceTrend);
						}
					});
					$("#btnApply").removeAttr("disabled");
					return;
				},
				error : function(xhr, error) {
					$("#btnApply").removeAttr("disabled");
					onError(xhr, error);
				}
			});
	}

	function createReportTable(compitatorPriceTrend) {
		var competitors = compitatorPriceTrend[0].labels;
		var tblElm = '<table class="table table-striped table-bordered"><thead><th style="background-color:#de9aa6;color:black;font-weight:bolder;font-size:12px;">Competitor</th><th style="background-color:#de9aa6;color:black;font-weight:bolder;font-size:12px;">Price Level</th>';
		$.each(compitatorPriceTrend[0].barChartDatas, function(indx, cData) {
			tblElm += '<th style="background-color:#de9aa6;color:black;font-weight:bolder;font-size:12px;">' + convertReportDateFromServer(cData[0]) + '</th>';
		});
		tblElm += '<thead><tbody>';
		$.each(competitors, function(indx, cName) {
			$.each(compitatorPriceTrend, function(i, cData) {
				tblElm += '<tr>';
				if (i == 0) {
					tblElm += '<td rowspan="' + compitatorPriceTrend.length + '" class="col-md-2" style="background-color: rgb(210, 252, 205); color: black;">' + cName + '</td>';
				}
				tblElm += '<td>' + cData.titleText + '</td>';
				$.each(cData.barChartDatas, function(j, bData) {
					tblElm += '<td>' + cData.barChartDatas[j][indx + 1] + '</td>';
				});
				tblElm += '</tr>';
			});
		});
		tblElm += '</tbody></table>';
		$('#cptReport').append(tblElm);
	}

	function convertReportDateFromServer(selectedDate) {
		if (selectedDate) {
			return moment(selectedDate, 'YYYY-MM-DD').format('DD-MM-YYYY');
		} else {
			return "";
		}
	}

	function convertDateTimeFromServer(createdDate) {
		if (createdDate) {
			return moment(createdDate).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function convertDateFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
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