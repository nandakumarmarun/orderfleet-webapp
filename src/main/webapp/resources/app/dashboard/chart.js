function loadDashboardSummaryChart(date) {
	$.ajax({
		url : appContextPath + "/web/dashboard/summary/chart",
		type : "GET",
		data : {
			date : formatDate(date, 'YYYY-MM-DD'),
		},
		success : function(chartData) {
			$("#chartContainer").html("");
			let dayTileHeight = $('#divDaySummary').parent().height();
			$('#divDaySummary').parent().height()
			if (chartData.chartType == 'FUNNEL') {
				loadDashboardSummaryFunnelChart(chartData.funnelChartDtos, dayTileHeight);
			} else {
				loadDashboardSummaryBarChart(chartData.barChartDtos, dayTileHeight);
			}
			$("#chartLabel").html(chartData.chartLabel);
		},
		error : function(xhr, error) {
			console.log("<< Error in loading chart >>");
		}
	});
}

function loadDashboardSummaryBarChart(barChartData, dayTileHeight) {
	let barChart = new britecharts.bar();
	let tooltip = new britecharts.miniTooltip();
	tooltip.numberFormat(',d');
	let barContainer = d3.select('#chartContainer');
	let containerWidth = barContainer.node() ? barContainer.node()
			.getBoundingClientRect().width : false;
	barChart.width(containerWidth).height(dayTileHeight).isAnimated(true).enableLabels(
			true).labelsNumberFormat(',d').on('customMouseOver', tooltip.show)
			.on('customMouseMove', tooltip.update).on('customMouseOut',
					tooltip.hide);
	barContainer.datum(barChartData).call(barChart);
	let tooltipContainer = d3
			.select('#chartContainer .bar-chart .metadata-group');
	tooltipContainer.datum([]).call(tooltip);
}

function loadDashboardSummaryFunnelChart(funnelChartData, dayTileHeight) {
	const options = {
		block : {
			dynamicHeight : false,
			minHeight : 20,
			fill : {
				type : 'gradient'
			}
		},
		label: {
			format: function(label, value) {
				return label + "\n" + value + "/-";
			},
			fontSize: '12px'
	    },
		chart : {
			height : dayTileHeight,
			bottomPinch : 1,
			animate : 200,
			curve : {
				enabled : true
			}
		},
		tooltip : {
			enabled : true,
			format: '{l}\nRs: {f}'
		},
	};
	const chart = new D3Funnel('#chartContainer');
	chart.draw(funnelChartData, options);
}
