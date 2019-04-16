(function() {
	'use strict';
	$(document).ready(function() {
		$('#dbDateFilter').on('change', function() {
			showDatePicker(this.value);
		});
		$('#btnApply').on('click', function() {
			loadFunnelChart();
		});
		
		loadFunnelChart();
	});

	function loadFunnelChart() {
		$("#chartContainer").html('<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">');
		$.ajax({
			url : appContextPath + "/web/funnel-chart/load",
			type : "GET",
			data : {
				filterBy : $("#dbDateFilter").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val()
			},
			success : function(chartData) {
				$("#chartContainer").html("");
				if (chartData.chartType == 'FUNNEL') {
					if(chartData.funnelChartDtos.length == 0){
						$('#chartContainer').html("<h4 align='center'>No data available</h4>");
					} else {
						createFunnelChart(chartData.funnelChartDtos);
					}
				}
			},
			error : function(xhr, error) {
				$("#chartContainer").html("");
				console.log("<< Error in loading chart >>");
			}
		});
	}
	
	function createFunnelChart(funnelChartData) {
		const options = {
			block : {
				dynamicHeight : true,
				minHeight : 20,
				fill : {
					type : 'gradient'
				}
			},
			label: {
		        format: '{l}\nRs: {f}',
		    },
			chart : {
				bottomPinch : 1,
				animate : 200,
				height : 600,
				curve : {
					enabled : true
				}
			},
			tooltip : {
				enabled : true,
				format: '{l}\nRs: {f}'
			},
			events: {
				click: {
					block(data) {
						let dateValue = $('#dbDateFilter').val();
						let stageValue = data.label.raw.split("\n")[0];
						window.location = appContextPath + "/web/stage-report?date="+ dateValue +"&stage="+ stageValue;
					}
				}
			}
		};
		const chart = new D3Funnel('#chartContainer');
		chart.draw(funnelChartData, options);
	}
	
	function showDatePicker(dateValue) {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if (dateValue != "CUSTOM") {
			$('#divDatePickers').css('display', 'none');
		} else {
			$('#divDatePickers').css('display', 'initial');
		}
	}

})();