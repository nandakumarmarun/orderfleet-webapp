$(document).ready(function() {

	// jquery dataTable
	dtOrders = $('#dtOrders').dataTable({
		"bSort" : false
	});

	// jquery datepicker
	$("#txtFromDate").datepicker();

	$("#txtToDate").datepicker();

});

function filter() {

	dtOrders.fnClearTable();
	if ($('#dbdateserch').val() != "CUSTOM") {
		$('#divDatePickers').css('display', 'none');
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
	} else {
		$('#divDatePickers').css('display', 'initial');
		onDateChange();
		return;
	}

	if ($('#dbitem').val() == "-1") {
		customAlert("Please Select Product"); 
		
		return;
	}
		console.log($('#dbdateserch').val());
		console.log("name print  " + $('#dbitem option:selected').text());
		$.ajax({
			url : contextPath + "/company/productReportWiseFilterOrders",
			type : 'GET',
			data : {
				filterBy : $('#dbdateserch').val(),
				name : $('#dbitem option:selected').text(),
				id : $('#dbitem').val()
			},
			success : function(orders) {
				console.log(orders);
				addRowsToTable(orders);
			}

		});
	} 
function onDateChange() {
	
	if ($('#dbitem').val() == "-1") {
		customAlert("Please Select Product");
		return;
	}
	if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
		return;
	}
	dtOrders.fnClearTable();
	console.log($("#txtFromDate").val());
	console.log($("#txtToDate").val());
	console.log($('#dbitem').val());
	$.ajax({
		url : contextPath + "/company/filterProductReportWise",
		type : 'GET',
		data : {
			id : $("#dbitem").val(),
			fromDate : $("#txtFromDate").val(),
			toDate : $("#txtToDate").val(),
			name : $('#dbitem option:selected').text()
		},

		success : function(orders) {
			console.log(orders);
			addRowsToTable(orders);
		}

	});

}

function addRowsToTable(orders) {
	$
			.each(
					orders,
					function(index, order) {
						console.log(order);
						console.log("order of 9  " + order[9]);
						console.log("order tot dis 10  " + order[10]);
						console.log("order tot amt  " + order[11]);
						dtOrders
								.fnAddData([
										order[1],
										order[2],
										order[3],
										getDateTime(order[4]),
										order[5],
										order[6],
										order[7],
										"<input type='checkbox' id='orderid' class='check-one' value='"
												+ order[9]
												+ "'/>&nbsp;<span style='font-size: smaller; color: green;'>"
												+ order[8] + "</span>",
										"<input class='btn btn-success' type='button' value='View Details' onclick='orderDetails("
												+ order[0] + ")' />" ]);

					});
}

function orderDetails(id) {
	console.log(id);
	$('#orderDetails').html("");
	$.ajax({
		url : contextPath + "/company/getOrder",
		type : 'GET',
		data : {
			orderId : id
		},
		success : function(order) {
			var rows = "";
			$('#exName').html(order.executive.name);
			$('#exPhone').html(order.executive.phone);
			$('#exEmail').html(order.executive.email);

			$('#cusName').html(order.customer.name);
			$('#cusPhone').html(order.customer.mobile1);
			$('#cusEmail').html(order.customer.email1);
			$('#cusPinCode').html(order.customer.pincode);
			$('#cusTerritory').html(order.customer.territory.name);
			$('#cusState').html(order.customer.state);
			$('#cusAddress').html(order.customer.address);

			$('#odDate').html(order.date);
			$('#odTotalNoOfItem').html(order.totalNoOfItem);
			$('#odTotalDiscount').html(order.totalDiscount);
			$('#odTotalAmount').html(order.grandTotal);

			$.each(order.orderItems, function(index, orderItem) {
				rows += "<tr><td>" + orderItem.name + "</td><td>"
						+ orderItem.rate + "</td><td>" + orderItem.quantity
						+ "</td><td>" + orderItem.discount + "</td><td>"
						+ orderItem.freeQuantity + "</td><td>"
						+ orderItem.totalAmount + "</td></tr>";
			});
			$('#orderDetails').html(rows);
			$("#modalOrderDetails").modal("show");
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

function downloadOrders() {
	var selectedOrderItems = "";
	var rows = dtOrders.fnGetNodes();
	for (var i = 0; i < rows.length; i++) {
		if ($(rows[i]).find('td:eq(7) input:checkbox').is(":checked")) {
			var v = $(rows[i]).find('td:eq(7) input').val();
			selectedOrderItems = selectedOrderItems + v + ",";
			$(rows[i]).find("td").eq(7).find('span').text('');
		}
	}
	console.log(selectedOrderItems);
	if (selectedOrderItems == "") {
		customAlert('Please select Product Reports');
		return;
	}
	window.location.href = contextPath
			+ "/company/downloadOrderCSVProduct?selectedOrderItems=" + selectedOrderItems;
}

// check all Orders
function onSelectAllOrders(checkbox) {
	var nodes = dtOrders.fnGetNodes();
	if (checkbox.checked) {
		$('.check-one', nodes).prop('checked', true);
	} else {
		$('.check-one', nodes).prop('checked', false);
	}
}

function customAlert(msg) {
	$("#dialog").text(msg);
	$("#dialog").dialog({
		resizable : false,
		modal : true,
		height : 180,
		width : 350,
		buttons : {
			"OK" : function() {
				$(this).dialog('close');
			}
		}
	});
}