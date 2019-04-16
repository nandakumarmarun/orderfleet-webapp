// Create a DynamicDocumentReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DynamicDocumentReport) {
	this.DynamicDocumentReport = {};
}

(function() {
	'use strict';
	var dynamicDocumentReportContextPath = location.protocol + '//'
			+ location.host + location.pathname;
	var selectedColIndex;
	$(document).ready(function() {

		$("#txtToDate").datepicker({
			dateFormat : "dd-mm-yy"
		});
		$("#txtFromDate").datepicker({
			dateFormat : "dd-mm-yy"
		});

		$('#sbDynamicReport').multiselect({
			enableCaseInsensitiveFiltering : true,
			includeSelectAllOption : true,
			maxHeight : 400
		});

		$('#btnApplyFilter').on('click', function() {
			var selectedValues = $('#sbDynamicReport').val();
			$("#mdlDynamicReport").modal("hide");
			searchTable(selectedValues);
		});

		$('#btnClearFilter').on('click', function() {
			$("#mdlDynamicReport").modal("hide");
			$('#tblDynamicForm tbody tr').show();
		});

		$('#btnApply').on('click', function() {
			var name = $("#modelAttr").val();
			getDynamicDocumentReport(name);
		});
		
		$('#btnDownload').on('click', function() {
			var tblDynamicForm = $("#tblDynamicForm tbody");
			if (tblDynamicForm.children().length == 0) {
				alert("no values available");
				return;
			}
			var name = $("#modelAttr").val();
			downloadXls(name);
		});

	});

	function getDynamicDocumentReport(name) {

		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$("#tblBody").html("<h3>Loading...</h3>");

		var isAccountChecked = true;
		if ($('#includeAccount').is(":checked")) {
			isAccountChecked = true;
		} else {
			isAccountChecked = false;
		}

		$.ajax({
			url : location.protocol + '//' + location.host
					+ "/web/dynamic-document-report/filter",
			type : 'GET',
			data : {
				userPid : $("#dbUser").val(),
				name : name,
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val(),
				includeAccount : isAccountChecked
			},
			success : function(dynamicForm) {
				$("#tblHead").html("");
				$("#tblBody").html("");
				createTableHeader(dynamicForm.elementNameToShow);
				createTableBody(dynamicForm.elementValues,
						dynamicForm.elementNameToShow.length);
			}
		});
	}

	function createTableHeader(elementNameToShow) {
		if (elementNameToShow != null && elementNameToShow.length > 0) {
			$
					.each(
							elementNameToShow,
							function(index, elementName) {
								$("#tblHead")
										.append(
												'<th data-initialsortorder="asc"><div >'
														+ elementName
														+ '<span onclick="DynamicDocumentReport.filterDynamicReport(this);" class="glyphicon glyphicon-filter"></span><span onclick="DynamicDocumentReport.sortDynamicReport(this);" class="glyphicon glyphicon-sort"></span></div></th>')
							});
		}
	}

	function createTableBody(elementValues, length) {
		
		
		if (elementValues != null && elementValues.length > 0) {
			$.each(elementValues,
					function(index, formDetail) {
						var cssClass = index % 2 === 0 ? "odd" : "even";
						var tData = "";
						var color = "";
						for (var i = 0; i < length; i++) {
							
							
							var value =formDetail[i] == undefined || formDetail[i].value == undefined
									|| formDetail[i].value == null ? ""
									: formDetail[i].value;
							var tDcolor = formDetail[i] == undefined || formDetail[i].colour == undefined
									|| formDetail[i].colour == null ? ""
									: formDetail[i].colour;
							if (color == "") {
								color = tDcolor
							}
							tData += ('<td>' + value + '</td>');
						}
						var theme = "";
						if (color == "red") {
							theme = "background-color: #ff5757;color: white;";
						} else if (color == "green") {
							theme = "background-color: #27b327;color: white;";
						} else if (color == "yellow") {
							theme = "background-color: yellow;";
						}
						var row = '<tr class="' + cssClass + '" style="'
								+ theme + '">';
						$("#tblBody").append(row + tData + '</tr>');
					});
		}
	}

	DynamicDocumentReport.filterDynamicReport = function(elm) {
		filterBySelectedHeader($(elm).closest('th'));
	}

	DynamicDocumentReport.sortDynamicReport = function(elm) {
		sortBySelectedHeader($(elm).closest('th'));
	}

	function filterBySelectedHeader(thElm) {
		selectedColIndex = $(thElm).index();
		var index = selectedColIndex + 1;
		$("#mdlTitle").html($(thElm).text());
		var items = [];
		var options = [];

		// Iterate all td's in second column
		$('#tblDynamicForm tbody tr td:nth-child(' + index + ')').each(
				function() {
					// add item to array
					if ($(this).text() != "") {
						items.push($(this).text());
					}
				});
		// restrict array to unique items
		var uniqueItems = $.unique(items);

		// iterate unique array and build array of select options
		$.each(uniqueItems, function(i, item) {
			options.push('<option value="' + item + '">' + item + '</option>');
		})

		// finally empty the select and append the items from the array
		$('#sbDynamicReport').empty().append(options.join());

		$('#sbDynamicReport').multiselect('rebuild');

		$("#mdlDynamicReport").modal("show");
	}
	function downloadXls(excelName) {
		 var table2excel = new Table2Excel();
	     table2excel.export(document.getElementById('tblDynamicForm'),excelName);
	}
	function sortBySelectedHeader(thElm) {
		var $tbody = $('#tblDynamicForm tbody');
		var dir = $(thElm).data("initialsortorder");
		$(thElm).data("initialsortorder", dir == "asc" ? "desc" : "asc");
		$tbody.find('tr').sort(function(a, b) {
			var tda = $(a).find('td:eq(' + $(thElm).index() + ')').text();
			var tdb = $(b).find('td:eq(' + $(thElm).index() + ')').text();
			if (dir == "asc") {
				// if a < b return 1
				return tda > tdb ? 1
				// else if a > b return -1
				: tda < tdb ? -1
				// else they are equal - return 0
				: 0;
			} else if (dir == "desc") {
				// if a < b return 1
				return tda < tdb ? 1
				// else if a > b return -1
				: tda > tdb ? -1
				// else they are equal - return 0
				: 0;
			}
		}).appendTo($tbody);
	}

	function searchTable(inputVal) {
		$('#tblDynamicForm').find('tbody').find('tr')
				.each(
						function(index, row) {
							var colText = $(row).find('td')
									.eq(selectedColIndex).text();
							if (inputVal.indexOf(colText) > -1) {
								$(row).show();
							} else {
								$(row).hide();
							}
						});
	}

	function addErrorAlert(message, key, data) {
		$(".alert > p").html(message);
		$('.alert').show();
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = location.protocol + '//' + location.host
				+ location.pathname;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = dynamicDocumentReportContextPath;
	}

	DynamicDocumentReport.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() != "CUSTOM") {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		} else {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}
	}

	// DynamicDocumentReport.showListModalPopup = function(el, id, docPid,
	// action) {
	// resetForm();
	// if (id) {
	// switch (action) {
	// case 0:
	// loadDocumentSettingsColumns(id, docPid);
	// break;
	// case 1:
	// loadDocumentSettingsRowColours(id, docPid);
	// break;
	// }
	// }
	// el.modal('show');
	// }

	DynamicDocumentReport.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		dynamicDocumentReportModel.pid = null; // reset bank model;
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
