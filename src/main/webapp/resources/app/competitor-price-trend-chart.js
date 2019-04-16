// Create a Bank object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CompetitorPriceTrendChart) {
	this.CompetitorPriceTrendChart = {};
}

(function() {
	'use strict';
	var contextPath = location.protocol + '//' + location.host
			+ "/web/competitor-price-trend/chart";
	var chartColors = [ 'rgb(255, 99, 132)', 'rgb(54, 162, 235)',
			'rgb(75, 192, 192)', 'rgb(255, 159, 64)', 'rgb(255, 205, 86)',
			'rgb(153, 102, 255)', 'rgb(201, 203, 207)' ];
	var cPriceTrendChartModel = {
		userPid : null,
		filterBy : null,
		fromDate : null,
		toDate : null,
		priceLevelNames : [],
		ptProductGroupPid : null
	};
	$(document).ready(function() {
		/*$('#dbProductGroups').multiselect({
			enableCaseInsensitiveFiltering : true,
			includeSelectAllOption : true,
			maxHeight : 400
		});*/

		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy",
		});

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy",
		});

		setDefaultDateInDatepicker();
		hideDatePicker();

		$("#dbDate").change(function() {
			if (this.value == "CUSTOM") {
				showDatePicker();
			} else {
				hideDatePicker();
			}
		});
		$("#btnApply").click(function() {
			$(this).attr("disabled", "disabled");
			loadBarChart();
		});
	});

	function loadBarChart() {
		if ($('#dbProductGroups').val() == "-1") {
			$("#btnApply").removeAttr("disabled");
			alert("Please Select Product Group");
			return;
		}
		if ($('#dbUser').val() == "-1") {
			$("#btnApply").removeAttr("disabled");
			alert("Please select user");
			return;
		}
		cPriceTrendChartModel.userPid = $("#dbUser").val();
		cPriceTrendChartModel.filterBy = $("#dbDate").val();
		cPriceTrendChartModel.fromDate = convertDateToServer($("#txtFromDate")
				.val());
		cPriceTrendChartModel.toDate = convertDateToServer($("#txtToDate")
				.val());
		cPriceTrendChartModel.priceLevelNames = [];
		if ($('#dbPrices').val() != "-1") {
			cPriceTrendChartModel.priceLevelNames.push($('#dbPrices').val());
		} else {
			$('#dbPrices option').each(function() {
				if ($(this).val() != "-1") {
					cPriceTrendChartModel.priceLevelNames.push($(this).val());
				}
			});
		}
		cPriceTrendChartModel.ptProductGroupPid = $("#dbProductGroups").val()
		/*
		 cPriceTrendChartModel.ptProductGroupPids = [];
		 var selectedValues = $('#dbProductGroups').val();
		if (selectedValues == null) {
			$('#dbProductGroups option').each(function() {
				cPriceTrendChartModel.ptProductGroupPids.push($(this).val());
			})
		} else {
			cPriceTrendChartModel.ptProductGroupPids = selectedValues;
		}*/
		$.ajax({
			url : contextPath + "/filter",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(cPriceTrendChartModel),
			success : function(pricetrends) {
				$('#divCharts').html("");
				$("#btnApply").removeAttr("disabled");
				console.log(pricetrends);
				$.each(pricetrends, function(index, pricetrend) {
					if (pricetrend.barChartDatas.length > 0) {
						createBarChart(index, pricetrend);
					}
				});
			},
			error : function(xhr, error) {
				$("#btnApply").removeAttr("disabled");
				console.log("Error loading graph..........");
			}
		});
	}

	function createBarChart(index, chartData) {
		$('#divCharts').append('<canvas id="chart_' + index + '"></canvas>');
		var color = Chart.helpers.color;
		var barChartData = {
			labels : [],
			datasets : []
		};
		$.each(chartData.barChartDatas, function(indx, cData) {
			barChartData.labels.push(convertDateFromServer(cData[0]));
		});
		$.each(chartData.labels, function(i, label) {
			var obj = {};
			var barColor = chartData.colors[i] == null ? chartColors[i] : chartData.colors[i];
			obj.label = label;
			obj.backgroundColor = color(barColor).alpha(0.5).rgbString();
			obj.borderColor = barColor;
			obj.borderWidth = 1;
			obj.data = [];
			$.each(chartData.barChartDatas, function(j, cData) {
				obj.data.push(cData[i + 1]);
			});
			barChartData.datasets.push(obj);
		});
		var ctx = document.getElementById('chart_' + index);
		new Chart(ctx, {
			type : 'bar',
			data : barChartData,
			options : {
				responsive : true,
				legend : {
					position : 'top',
				},
				title : {
					display : true,
					text : chartData.titleText
				}
			}
		});
	}

	function setDefaultDateInDatepicker() {
		var currentDate = convertDateFromServer(new Date().toISOString()
				.substring(0, 10));
		$("#txtFromDate").val(currentDate);
		$("#txtToDate").val(currentDate);
	}

	function hideDatePicker() {
		$("#txtFromDate").parent().parent().hide();
		$("#txtToDate").parent().parent().hide();
	}

	function showDatePicker() {
		$("#txtFromDate").parent().parent().show();
		$("#txtToDate").parent().parent().show();
	}

	function filterProductByProductGroup() {
		var option = "";
		var productGroupPids = "";
		$("#dbProducts").html("");
		$.each($("input[name='productGroup']:checked"), function() {
			productGroupPids += $(this).val() + ",";
		});
		$
				.ajax({
					url : contextPath + "/product-groups",
					type : "GET",
					data : {
						productGroupPids : productGroupPids
					},
					success : function(products) {
						console.log(products);
						console.log("Success");
						//assign to product dropdown.
						option = '<option value="-1" selected="selected">All Products</option>'
						$.each(products, function(index, product) {
							option += '<option value="' + product.pid + '">'
									+ product.name + '</option>';
						});
						$("#dbProducts").append(option);
					},
					error : function(xhr, error) {
						console
								.log("Error loading product by product group..........");
					}
				});
	}

	function convertDateFromServer(selectedDate) {
		if (selectedDate) {
			return moment(selectedDate, 'YYYY-MM-DD').format('DD-MM-YYYY');
		} else {
			return "";
		}
	}

	function convertDateToServer(selectedDate) {
		if (selectedDate) {
			return moment(selectedDate, 'DD-MM-YYYY').format('YYYY-MM-DD');
		} else {
			return "";
		}
	}

})();