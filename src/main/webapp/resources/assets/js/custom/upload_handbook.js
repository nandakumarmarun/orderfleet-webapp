$(document)
		.ready(
				function() {

					var options = {
						beforeSend : function() {
							$("#progressbox").show();
							// clear everything
							$("#progressbar").width('0%');
							$("#message").empty();
							$("#percent").html("0%");

						},
						uploadProgress : function(event, position, total,
								percentComplete) {
							$("#progressbar").width(percentComplete + '%');
							$("#percent").html(percentComplete + '%');
							// change message text and % to red after 50%
							var territory = $("#dbTerritory option:selected")
									.text();

							if (percentComplete > 50) {
								$("#message")
										.html(
												"<font color='red'>File Upload is in progress...</font>");
							}
						},
						success : function(response) {
							$("#progressbar").width('100%');
							$("#percent").html('100%');

							$("#message").html(
									"<font color='blue'>"
											+ response.description + "</font>");

							var territory = $("#dbTerritory option:selected")
									.text();
							var handbook = response.body;
							updateTableData(handbook, territory);

							$('#frmHandBook')[0].reset();
						},
						complete : function(response) {
							setTimeout(function() {
								$("#modalHandBook").modal("hide");
								clear();
							}, 4000);

						},
						error : function() {
							console.log("..........................complete");
							$("#message")
									.html(
											"<font color='red'> ERROR: unable to upload files</font>");
						}
					};
					$("#frmHandBook").ajaxForm(options);
				});

function editHandBook(id, obj) {
	clear();
	handBookRow = $(obj).closest('tr');
	$.ajax({
		url : contextPath + "/company/editHandBook",
		type : "GET",
		data : {
			id : id
		},
		success : function(handbook) {
			$('#hdnId').val(handbook.id);
			$('#dbTerritory').val(handbook.territory.id);
			$('#dbTerritory').prop('readonly', true);
			$("#modalHandBook").modal("show");
		}
	});
}

function clear() {
	$('.uploadHandBookStatus').html('');
	$('#dbTerritory').val(-1)
	$('#hdnId').val('');

	$("#progressbar").width('0%');
	$("#message").empty();
	$("#percent").html("0%");
}

function updateTableData(handbook, territory) {
	var pdfName = handbook.url;

	if ($('#hdnId').val() == "") {
		dtHandBooks
				.fnAddData([
						handbook.name,
						territory,
						"<button type='button' class='btn btn-blue' style='width: 80px;' onclick='editHandBook("
								+ handbook.id
								+ ",this)' >Edit</button>&nbsp;<button type='button' class='btn btn-danger' style='width: 80px;' onclick='deleteHandBook("
								+ handbook.id
								+ ",this)'>Delete</button>&nbsp;<a class='btn btn-fb' style='width: 130px;' href='"
								+ contextPath
								+ "/ws/getPdf/"
								+ handbook.territory.id
								+ "/"
								+ pdfName
								+ ".pdf' target='_blank'>View Hand Book</a>" ]);
	} else {
		var actions = "<button type='button' class='btn btn-blue' style='width: 80px;' onclick='editHandBook("
				+ handbook.id
				+ ",this)' >Edit</button>&nbsp;<button type='button' class='btn btn-danger' style='width: 80px;' onclick='deleteHandBook("
				+ handbook.id
				+ ",this)'>Delete</button>&nbsp;<a class='btn btn-fb' style='width: 130px;' href='"
				+ contextPath
				+ "/ws/getPdf/"
				+ handbook.territory.id
				+ "/"
				+ pdfName + ".pdf' target='_blank'>View Hand Book</a>"
		handBookRow.find("td").eq(0).text(handbook.name);
		handBookRow.find("td").eq(1).text(territory);
		handBookRow.find("td").eq(2).html(actions);
	}
}

function validationEvent() {
	if ($('#dbTerritory').val() == -1) {
		$('.uploadHandBookStatus').text('Please select territory.');
		return false;
	}
	if ($('#dbTerritory').val() == "") {
		$('.uploadHandBookStatus').text('Please select territory.');
		return false;
	}

	if ($('#txtFileHandBook').val() == '') {
		$('.uploadHandBookStatus').text('Please choose a file.');
		return false;
	}
	var formObject = $("#frmHandBook").serializeArray();
	var id = formObject[0].value;
	var territoryId = formObject[1].value;
	var exists = "";
	$("#message").val("Success");
	$.ajax({ // make an AJAX request
		type : "POST",
		url : contextPath + "/company/validateHandBook",
		data : {
			id : id,
			territoryId : territoryId
		},
		async:false,
		success : function(response) {
			if (response.status != "Exist") {
				exists = "success";
			} else {
				exists = "";
			}
		}
	});
	console.log(exists+"---------");
	if (exists == "") {
		$('.uploadHandBookStatus').text(
				'Handbook already exists in this territory!');
		return false;
	}
	$('.uploadHandBookStatus').text('');
	return true;
}