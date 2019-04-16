$(document).ready(function() {

	// jquery dataTable
	dtVisits = $('#dtVisits').dataTable({
		"bSort" : false,
		"aLengthMenu" : [ [ 10, 25, 50, -1 ], [ 10, 25, 50, "All" ] ]
	});

	// Initalize Select Dropdown after DataTables is created
	dtVisits.closest('.dataTables_wrapper').find('select').select2({
		minimumResultsForSearch : -1

	});

	// jquery datepicker
	$("#txtFromDate").datepicker();

	$("#txtToDate").datepicker();

});

function filter() {
	dtVisits.fnClearTable();
	if ($('#dbdateserch').val() != "CUSTOM") {
		$(".custom_date").addClass('hide');
		$(".custom_date").removeClass('show');
		$('#divDatePickers').css('display', 'none');
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
	} else {
		$(".custom_date").addClass('show');
		$(".custom_date").removeClass('hide');
		$('#divDatePickers').css('display', 'initial');
		onDateChange();
		return;
	}
	var totalQuantity = 0;
	var totalItem = 0;
	var totalAmount = 0;
	if ($('#dbCustomer').val() == "-1") {
		customAlert("Please Select Customer");
		return;
	}
	console.log($('#dbCustomer').val());

	console.log($('#dbdateserch').val());
	console.log($('#dbCustomer').val());
	$.ajax({
		url : contextPath + "/company/customerWiseFilterVisits",
		type : 'GET',
		data : {
			filterBy : $('#dbdateserch').val(),
			id : $('#dbCustomer').val()
		},
		success : function(visits) {
			console.log(visits);
			addRowsToTable(visits);
			$.each(visits, function(index, visit) {
				totalQuantity += visit.totalQuantity;
				totalItem += visit.totalItem;
				totalAmount += visit.totalReceiptAmount;
			});
			$('#totalAmount').text(totalAmount);
			$('#totalItem').text(totalItem);
			$('#totalQuantity').text(totalQuantity);
		}

	});
}

function onDateChange() {
	if ($('#dbCustomer').val() == "-1") {
		customAlert("Please Select Customer");
		return;
	}
	if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {

	}
	var totalQuantity = 0;
	var totalItem = 0;
	var totalAmount = 0;
	dtVisits.fnClearTable();
	console.log($("#txtFromDate").val());
	console.log($("#txtToDate").val());
	console.log($('#dbCustomer').val());

	$.ajax({
		url : contextPath + "/company/filterCustomWiseVisits",
		type : 'GET',
		data : {
			id : $('#dbCustomer').val(),
			fromDate : $("#txtFromDate").val(),
			toDate : $("#txtToDate").val()
		},

		success : function(visits) {
			console.log(visits);
			addRowsToTable(visits);
			$.each(visits, function(index, visit) {
				totalQuantity += visit.totalQuantity;
				totalItem += visit.totalItem;
				totalAmount += visit.totalReceiptAmount;
			});
			$('#totalAmount').text(totalAmount);
			$('#totalItem').text(totalItem);
			$('#totalQuantity').text(totalQuantity);
		}

	});

}

function addRowsToTable(visits) {
	$
			.each(
					visits,
					function(index, visit) {
						console.log(visit);
						console.log(visit[1]);

						var date = "";
						var mode = "";
						var invoiceNo = "";
						$.each(visit.receiptDetails, function(index1, rep) {

							console.log(rep.mode)

							mode += "<tr><td>" + rep.mode
									+ "<br><br></td></tr>";

							if (rep.chequeDate != null) {
								if (rep.mode == 'CH') {
									date += "<tr><td>"
											+ getDateTime1(rep.chequeDate)
											+ "<br><br></td></tr>";
								} else if (rep.mode == 'CA') {
									date += "<tr><td><br><br></td></tr>";
								}

							} else {
								date += "<tr><td><br><br></td></tr>";
							}

							invoiceNo += "<tr><td>" + rep.invoiceNo
									+ "<br><br></td></tr>";

							console.log("hai")
						});
						if (date != null) {
							dtVisits
									.fnAddData([
											visit.customer,
											getDateTime(visit.visitTime),
											visit.executive,
											visit.place,
											visit.totalReceiptAmount,
											mode,
											date,
											invoiceNo,
											visit.totalItem,
											visit.totalQuantity,
											"<input class='btn btn-success' type='button' value='Visit Details' onclick='visitDetails("
													+ visit.id + ")' />" ]);
						} else {
							dtVisits
									.fnAddData([
											visit.customer,
											getDateTime(visit.visitTime),
											visit.executive,
											visit.place,
											visit.totalReceiptAmount,
											mode,
											"",
											invoiceNo,
											visit.totalItem,
											visit.totalQuantity,
											"<input class='btn btn-success' type='button' value='Visit Details' onclick='visitDetails("
													+ visit.id + ")' />" ]);
						}
					});
}

function visitDetails(id) {
	console.log(id);
	$('#visitDetails').html("");
	$.ajax({
		url : contextPath + "/company/getVisits",
		type : 'GET',
		data : {
			id : id
		},
		success : function(visit) {

			var rows = "";
			$('#vdExecutiveName').html(visit.executive);
			$('#vdCustomerName').html(visit.customer);
			$('#vdVisitNo').html(visit.visitNo);
			$('#vdRemarkText').html(visit.remarkText);

			$('#odNo').html(visit.order.orderNo);
			$('#odTotalNoOfItem').html(visit.order.totalNoOfItem);
			$('#odRemarkText').html(visit.order.remarkText);

			var rows1 = "";

			// if(visit.order.orderItems!= null )
			// {

			$.each(visit.order.orderItems, function(index, orderItem) {

				rows1 += "<tr><td>" + orderItem.itemName + "</td><td>"
						+ orderItem.quantity + "</td></tr>";

			});
			// }

			$('#orderDetails').html(rows1);

			$('#rdNo').html(visit.receiptMaster.id);
			$('#rdRemarkText').html(visit.receiptMaster.remarkText);

			var rows2 = "";

			// if(visit.receiptMaster.receiptDetails !=null){
			$.each(visit.receiptMaster.receiptDetails, function(index,
					receiptDetail) {

				if (receiptDetail.mode == "CH") {
					rows2 += "<tr><td>" + receiptDetail.mode + "</td><td>"
							+ receiptDetail.invoiceNo + "</td><td>"
							+ receiptDetail.amount + "</td><td>"
							+ receiptDetail.bank.name + "</td><td>"
							+ receiptDetail.chequeNo + "</td><td>"
							+ getDateTime1(receiptDetail.chequeDate)
							+ "</td></tr>";
				} else {
					if (receiptDetail.chequeNo != null
							|| receiptDetail.mode == "CA") {
						rows2 += "<tr><td>" + receiptDetail.mode + "</td><td>"
								+ receiptDetail.invoiceNo + "</td><td>"
								+ receiptDetail.amount + "</td><td>"
								+ "</td><td>" + "</td><td>" + "</td></tr>";
					}
				}

			});
			// }

			$('#receiptDetails').html(rows2);

			$('#visitDetails').html(rows);
			$("#modalVisitDetails").modal("show");
		},
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		error : function(jqXhr, textStatus, errorThrown) {
			alert(textStatus);
		}
	});
}

function getDateTime(date1) {
	var date = new Date(date1);
	/*
	 * var monthNames = [ "January", "February", "March", "April", "May",
	 * "June", "July", "August", "September", "October", "November", "December" ];
	 */
	var day = date.getDate();
	var monthIndex = date.getMonth() + 1;
	var year = date.getFullYear();
	// var dateString = day + ", " + monthNames[monthIndex] + ", " + year;
	var dateString = day + "/" + monthIndex + "/" + year;

	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var timeString = "" + ((hours > 12) ? hours - 12 : hours);
	timeString += ((minutes < 10) ? ":0" : ":") + minutes;
	// timeString += ((seconds < 10) ? ":0" : ":") + seconds;
	timeString += (hours >= 12) ? " PM" : " AM";

	return dateString + " " + timeString;
}
function getDateTime1(date2) {
	var date = new Date(date2);
	/*
	 * var monthNames = [ "January", "February", "March", "April", "May",
	 * "June", "July", "August", "September", "October", "November", "December" ];
	 */
	var day = date.getDate();
	var monthIndex = date.getMonth() + 1;
	var year = date.getFullYear();
	// var dateString = day + ", " + monthNames[monthIndex] + ", " + year;
	var dateString = day + "/" + monthIndex + "/" + year;

	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var timeString = "" + ((hours > 12) ? hours - 12 : hours);
	timeString += ((minutes < 10) ? ":0" : ":") + minutes;
	// timeString += ((seconds < 10) ? ":0" : ":") + seconds;
	timeString += (hours >= 12) ? " PM" : " AM";

	return dateString;
}
