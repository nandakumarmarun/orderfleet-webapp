if (!this.SalesReport) {
	this.SalesReport = {};
}

(function() {
	'use strict';

	var salesReportContextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		var employeePid = getParameterByName('user-key-pid');
		getEmployees(employeePid); // common js
		
		$("#dbDocumentType").change(function() {
			loadAllDocumentByDocumentType();
		});

		$('#downloadXls').on('click', function() {
			var tblSale = $("#tblSalesReport tbody");
			if (tblSale.children().length == 0) {
				alert("no values available");
				return;
			}
			downloadXls();
		});
		
		// report name
		$('#dbReportName').change( function() {
			SalesReport.filter();
		});
		// load default
		SalesReport.filter();
	});

	// set the date selection limit to 3 months
			$("#txtToDate").datepicker({
    								 dateFormat: 'mm-dd-yy',
                                                minDate: 0,
                                    onSelect: function(selectedDate) {
                                    var toDate = new Date(selectedDate);
                                    var fromDate = new Date(toDate);
                                    fromDate.setMonth(toDate.getMonth() - 3);
                                    $("#txtFromDate").datepicker("option", "minDate", fromDate);
                                    $("#txtFromDate").datepicker("option", "maxDate",toDate);

                                                                                            }
    							});
    							$("#txtFromDate").datepicker({
    								dateFormat : "mm-dd-yy",
    								onSelect: function(selectedDate) {

                                                    var fromDate = new Date(selectedDate);
                                                    console.log("fromDate :"+fromDate)
                                                    var toDate = new Date(fromDate);
                                                    toDate.setMonth(toDate.getMonth() + 3);
                                                $("#txtToDate").datepicker("option", "minDate", fromDate);
                                                    $("#txtToDate").datepicker("option", "maxDate",toDate);

                                                }
    							});
	function loadReports(rName) {
		$('#title').text(rName);
		$('#tHeadSalesReport').html("");
		$("#lblTotal").text("0");
		if(rName == "Sales Report"){
			getSalesReport();
		} else if(rName == "Not Ordered Products"){
			getSalesNotOrderedReport();
		} else if(rName == "User Wise Product Group Summary"){
			getUserWiseProductGroupSummary();
		}
	}

	function downloadXls() {
// var clonedTable = $("#tblSalesReport").clone();
// clonedTable.find('[style*="display: none"]').remove();

		var excelName = $("#dbReportName option:selected").text();
		
		
		 var table2excel = new Table2Excel();
		     table2excel.export(document.getElementById('tblSalesReport'),excelName);

// clonedTable.table2excel({
// filename : excelName, // do not include extension
// });
	}

	function getParameterByName(name, url) {
		if (!url)
			url = window.location.href;
		name = name.replace(/[\[\]]/g, "\\$&");
		var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"), results = regex
				.exec(url);
		if (!results)
			return null;
		if (!results[2])
			return '';
		return decodeURIComponent(results[2].replace(/\+/g, " "));
	}

	SalesReport.filter = function() {
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
		loadReports($('#dbReportName').val());
		
	}
	
	function getSalesReport() {
		$('#tBodySalesReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
	    console.log($("#dbDocument").val())
	    console.log("FromDate :"+$("#txtFromDate").val())
		$
				.ajax({
					url : salesReportContextPath + "/web/sales-report/filter",
					type : 'GET',
					data : {
						pGroupPid : $('#dbProductGroup').val(),
						employeePid : $('#dbEmployee').val(),
						accountPid : $("#dbAccount").val(),
						voucherType : $("#dbDocumentType").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDates : $("#txtFromDate").val(),
						toDates : $("#txtToDate").val(),
						inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					
					success : function(sales) {
						$('#tHeadSalesReport').html('<tr><th style="min-width: 80px;">Employee</th><th>Account Profile</th><th>Item</th><th>Quantity</th><th>Unit Qty</th><th>Total Qty</th><th>Date</th><th>Visit Remarks</th></tr>');
						$('#tBodySalesReport').html("");
						if (sales.length == 0) {
							$('#tBodySalesReport')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var totalSales = 0;
						$.each(sales,function(index, sale){
							var unitQty = 1;
							if(sale.productUnitQty){
								unitQty = sale.productUnitQty;
							}
							var qty = Math.round((sale.quantity * unitQty) * 100) / 100;
							totalSales += qty;
							$('#tBodySalesReport')
							.append("<tr data-id='"
									+ sale.detailId
									+ "' data-parent=\"\"><td>"
									+ sale.employeeName
									+ "</td><td>" + sale.accountName + "</td>" 
									+ "<td>" + sale.productName + "</td>" 
									+ "<td>" + sale.quantity + "</td>" 
									+ "<td>" + unitQty + "</td>" 
									+ "<td style='width:10px;'>" + qty + "</td>" 
									+ "<td >" + convertDateTimeFromServer(sale.createdDate) + "</td>"
									+ "<td >" + sale.visitRemarks + "</td>"
									+ "</tr>");
						});
						$("#lblTotal").text(totalSales.toFixed(2));
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	

	function getSalesNotOrderedReport() {
		$('#tBodySalesReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : salesReportContextPath + "/web/sales-report/filter/not-ordered",
			type : 'GET',
			data : {
				pGroupPid : $('#dbProductGroup').val(),
				employeePid : $('#dbEmployee').val(),
				accountPid : $("#dbAccount").val(),
				filterBy : $("#dbDateSearch").val(),
				voucherType : $("#dbDocumentType").val(),
				fromDates : $("#txtFromDate").val(),
				toDates : $("#txtToDate").val(),
				inclSubordinate : $('#inclSubOrdinates').is(":checked")
			},
			success : function(products) {
				$('#tHeadSalesReport').html('<tr><th>Item</th></tr>');
				$('#tBodySalesReport').html("");
				if (products.length == 0) {
					$('#tBodySalesReport')
						.html(
							"<tr><td colspan='9' align='center'>No data available</td></tr>");
					return;
				}
				$.each(products,function(index, product){
					$('#tBodySalesReport')
					.append("<tr><td>" + product + "</td></tr>");
				});
			},
			error : function(xhr, error) {
				console.log("Error sales not ordered report : " + error)
			}
			
		});
	}
	
	function getUserWiseProductGroupSummary() {
		$('#tBodySalesReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		var groupNames = "";
		if($('#dbProductGroup').val() == "no"){
			groupNames = $.map($('#dbProductGroup option') ,function(option) {
				if(option.value != "no"){
					 return option.text;
				}
			});
		} else {
			groupNames = $("#dbProductGroup option:selected").text();
		}
		$
				.ajax({
					url : salesReportContextPath + "/web/sales-report/filter/userwise-group-summary",
					type : 'GET',
					data : {
						pGroupNames : groupNames.toString(),
						employeePid : $('#dbEmployee').val(),
						accountPid : $("#dbAccount").val(),
						voucherType : $("#dbDocumentType").val(),
						filterBy : $("#dbDateSearch").val(),
						fromDates : $("#txtFromDate").val(),
						toDates : $("#txtToDate").val(),
						voucherType : $("#dbDocumentType").val(),
                        inclSubordinate : $('#inclSubOrdinates').is(":checked")
					},
					success : function(sales) {
						console.log(sales);
						if (sales == null) {
							$('#tBodySalesReport')
								.html(
									"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						} else {
							$('#tBodySalesReport').html("");
						}
						var headerRow = '<tr><th style="min-width: 80px;">Employee</th>';
						$.each(sales.reportHeaders, function(indx, name){
							headerRow += '<th>'+ name +'</th>'
						});
						headerRow += '<th>Total</th></tr>';
						$('#tHeadSalesReport').html(headerRow);
						
						if(sales.reportValues.length == 0){
							$('#tBodySalesReport')
							.html(
								"<tr><td colspan='9' align='center'>No data available</td></tr>");
							return;
						}
						var totalSales = 0;
						$.each(sales.reportValues, function(indx, values){
							var rowHtml = "<tr>";
							var rowTot = 0;
							$.each(values, function(indx, value){
								if(!isNaN(value)){
									value = Math.round(value * 100) / 100;
									rowTot += value;
								}
								rowHtml += "<td>"+ value +"</td>"
							});
							rowTot = Math.round(rowTot * 100) / 100;
							rowHtml += "<td>" + rowTot + "</td>"
							rowHtml += "</tr>";
							totalSales += rowTot;
							$('#tBodySalesReport').append(rowHtml);
						});
						$("#lblTotal").text(totalSales.toFixed(2));
					},
					error : function(xhr, error) {
						console.log("Error sales report : " + error)
					}
				});
	}
	
	SalesReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
        			$(".custom_date1").addClass('show');
        			$(".custom_date2").addClass('show');
        			$(".custom_date1").removeClass('hide');
        			$(".custom_date2").removeClass('hide');
        			$('#divDatePickers').css('display', 'initial');
        		}else {
        			$(".custom_date1").addClass('hide');
        			$(".custom_date1").removeClass('show');
        			$(".custom_date2").addClass('hide');
        			$(".custom_date2").removeClass('show');
        			$('#divDatePickers').css('display', 'none');
        		}

	}

	function clearAndHideErrorBox() {
		$(".alert > p").html("");
		$('.alert').hide();
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}
	function loadAllDocumentByDocumentType() {
		if ($('#dbDocumentType').val() == "no") {
			$("#dbDocument").html("<option>All</option>");
			alert("Please Select Document Type");
			return;
		}
		var documentType = $('#dbDocumentType').val();
		$("#dbDocument").html("<option>Documents loading...</option>")
		$.ajax({
			url : salesReportContextPath + "/web/sales-report/load-document",
			type : 'GET',
			data : {
				voucherType : documentType,
			},
			success : function(documents) {
				$("#dbDocument").html("<option value='no'>All</option>")
				$.each(documents, function(key, document) {
					$("#dbDocument").append(
							"<option value='" + document.pid + "'>"
									+ document.name + "</option>");
				});
			}
		});
	}

})();