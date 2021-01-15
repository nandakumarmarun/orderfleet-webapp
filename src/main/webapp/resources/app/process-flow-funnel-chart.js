(function() {
	'use strict';
	$(document).ready(function() {		
		loadFunnelChart();
	});

	function loadFunnelChart() {
		$("#chartContainer").html('<img src="/resources/assets/images/menu-ajax-loader.gif" style="display: block; margin: auto;">');
		$.ajax({
			url : appContextPath + "/web/process-flow-funnel-chart/load",
			type : "GET",
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
				format: '{f} : {v}',
		        fill: '#000'
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
				format: '{f} : {v}'
			},
			events: {
				click: {
					block(data) {
						//let stageValue = data.label.raw.split("\n")[0]; 
						let stageValue = data.label.raw;
						window.location = appContextPath + "/web/process-flow-stage-all?deliveryStage="+ stageValue;
					}
				}
			}
		};
		console.log(funnelChartData);
		const chart = new D3Funnel('#chartContainer');
		chart.draw(funnelChartData, options);
	}
})();