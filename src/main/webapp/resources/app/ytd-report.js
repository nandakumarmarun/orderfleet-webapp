// Create a YTDReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.YTDReport) {
	this.YTDReport = {};
}

(function() {
	'use strict';

	var ytdReportContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(function() {
		getReport();
	});

	function getReport() {
		$("#tblBody")
				.html(
						'<tr><td colspan="6" style="color:black;font-weight: bold;">Please wait...</td></tr>');
		$("#totalTC").html("0.0");
		$("#totalPC").html("0.0");
		$("#totalEfficiency").html("0.0");
		$("#totalVol").html("0.0");
		$("#totalAmount").html("0.0");
		var tc = 0.0;
		var pc = 0.0;
		var volume = 0.0;
		var amount = 0.0;
		$.ajax({
			url : ytdReportContextPath + "/getReport",
			type : 'GET',
			//data : JSON.stringify(territories),
			contentType : "application/json",
			success : function(months) {
				$("#tblBody").html("");
				$.each(months, function(indexMonth, month) {

					tc += month.tc;
					pc += month.pc;
					volume += month.volume;
					amount += month.amount;

					// generate month
					$("#tblBody").append(
							'<tr style="background: beige;" data-id="'
									+ month.month
									+ '" data-parent=""><td class="janvary">'
									+ month.month + '</td><td>' + month.tc
									+ '</td><td>' + month.pc + '</td><td>'
									+ month.efficiency.toFixed(2) + '</td><td>'
									+ month.volume.toFixed(2) + '</td><td>'
									+ month.amount.toFixed(2) + '</td></tr>');
					$.each(month.weeks, function(indexWeek, week) {

						// generate week
						var weekId = week.week.replace(/ /g, '') + month.month;
						$("#tblBody").append(
								'<tr style="background: rgba(255, 228, 196, 0.43);" data-id="'
										+ weekId + '" data-parent="'
										+ month.month
										+ '"><td class="janvary">' + week.week
										+ '</td><td>' + week.tc + '</td><td>'
										+ week.pc + '</td><td>'
										+ week.efficiency.toFixed(2)
										+ '</td><td>' + week.volume.toFixed(2)
										+ '</td><td>' + week.amount.toFixed(2)
										+ '</td></tr>');

						// generate day
						$.each(week.days, function(indexDay, day) {
							var dayId = weekId + "" + indexDay;
							$("#tblBody").append(
									'<tr onclick="showDayDeatls()" data-id="'
											+ dayId + '" data-parent="'
											+ weekId + '"><td class="janvary">'
											+ day.day
											+ '<input type="hidden" value="'
											+ day.day + '"/></td><td>' + day.tc
											+ '</td><td>' + day.pc
											+ '</td><td>'
											+ day.efficiency.toFixed(2)
											+ '</td><td>'
											+ day.volume.toFixed(2)
											+ '</td><td>'
											+ day.amount.toFixed(2)
											+ '</td></tr>');
						});
					});
				});
				var efficiency = pc * 100 / tc;
				$("#totalTC").html(tc);
				$("#totalPC").html(pc);
				$("#totalEfficiency").html(efficiency.toFixed(2));
				$("#totalVol").html(volume.toFixed(2));
				$("#totalAmount").html(amount.toFixed(2));

				$('.collaptable').aCollapTable({
					startCollapsed : true,
					addColumn : false,
					plusButton : '<span><i class="entypo-down-open-mini"></i></span>',
					minusButton : '<span><i class="entypo-up-open-mini"></i></span>'
				});
			}
		});
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
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