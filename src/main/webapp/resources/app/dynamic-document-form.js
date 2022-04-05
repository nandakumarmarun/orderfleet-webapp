// Create a DynamicDocumentForm object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DynamicDocumentForm) {
	this.DynamicDocumentForm = {};
}

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	var selectedColIndex;
	$(document).ready(function() {
		$("#txtToDate").datepicker({ dateFormat: "dd-mm-yy" });
		$("#txtFromDate").datepicker({ dateFormat: "dd-mm-yy" });
		// call from menu item
		var titleName = getParameterByName('title');
		if (titleName != null && titleName != "") {
			$('#title').html(titleName);
		}
		
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
			if ($("#dbEmployee").val() == "no") {
				alert("Please select Employee.");
				return;
			}
			if ($("#dbForm").val() == "no") {
				alert("Please select Form.");
				return;
			}
			filterDynamicForm();
		});

		$('#btnDownload').on('click', function() {
			var tblDynamicFormHead = $("#tblDynamicForm thead");
			var tblDynamicFormBody = $("#tblDynamicForm tbody");
			//hideTableHeadAndBodyForExcel(tblDynamicFormHead,tblDynamicFormBody);
			if (tblDynamicFormBody.children().length == 0) {
				alert("no values available");
				return;
			}
			downloadXls();
			//showTableHeadAndBodyForUi(tblDynamicFormHead,tblDynamicFormBody);
			
			
		});
	});
	
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

	DynamicDocumentForm.onChangeUser = function(){
		if ($("#dbEmployee").val() == "no") {
			return;
		}
		$("#dbDocument").html('<option>Loading...</option>');
		$.ajax({
			url : contextPath + "/web/dynamic-document-forms/document",
			type : 'GET',
			data : {
				employeePid : $("#dbEmployee").val()
			},
			success : function(documents) {
				$("#dbDocument").html('<option value="no">Select Document</option>');
				if (documents != null && documents.length > 0) {
					$.each(documents, function(index, document) {
						$("#dbDocument").append(
								'<option value="' + document.pid + '">' + document.name
										+ '</option>');
						$("#dbDocument").val(document.pid);
					});
				}
				DynamicDocumentForm.onChangeDocument();
			}
		});
	}
	
	DynamicDocumentForm.onChangeDocument = function() {
		if ($("#dbDocument").val() == "no") {
			return;
		}
		$("#dbForm").html('<option>Loading...</option>');
		$.ajax({
			url : contextPath + "/web/dynamic-document-forms",
			type : 'GET',
			data : {
				documentPid : $("#dbDocument").val()
			},
			success : function(forms) {
				$("#dbForm").html('<option value="no">Select Form</option>');
				if (forms != null && forms.length > 0) {
					$.each(forms, function(index, form) {
						$("#dbForm").append(
								'<option value="' + form.pid + '">' + form.name
										+ '</option>');
						$("#dbForm").val(form.pid);
					});
				}

			}
		});
	}

	DynamicDocumentForm.showDatePicker = function() {
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

	function downloadXls() {
		var excelName = $("#dbDocument option:selected").text();
		var userName = new Array();
		
			 $("#tblBody").find('tr').each (function() {
				 userName.push($(this).find('td').eq(5).text());
				 $(this).find('td').eq(5).text($(this).find('td').eq(5).text().toString().replace(" ", ""));
				}); 
         
		 var table2excel = new Table2Excel();
	     table2excel.export(document.getElementById('tblDynamicForm'),excelName);
		     var i = 0;
		     $("#tblBody").find('tr').each (function() {
				 $(this).find('td').eq(5).text(userName[i]);
				 i++;
				}); 
	}
	
	function hideTableHeadAndBodyForExcel(tblDynamicFormHead,tblDynamicFormBody){
		tblDynamicFormHead.find('tr').each(function (i, el) {
	        var $ths = $(this).find('th');
	        $ths.eq(4).hide();
	        $ths.eq(5).hide();        
	    });
		
		tblDynamicFormBody.find('tr').each(function (i, el) {
	        var $tds = $(this).find('td');
	        $tds.eq(4).hide();
	        $tds.eq(5).hide();         
	    });
	}
	
	function showTableHeadAndBodyForUi(tblDynamicFormHead,tblDynamicFormBody){
		tblDynamicFormHead.find('tr').each(function (i, el) {
	        var $ths = $(this).find('th');
	        $ths.eq(4).show();
	        $ths.eq(5).show();
	              
	    });
		
		tblDynamicFormBody.find('tr').each(function (i, el) {
	        var $tds = $(this).find('td');
	        $tds.eq(4).show();
	        $tds.eq(5).show();	              
	    });
	}
	

	function filterDynamicForm() {
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}
		$("#tblBody").html("<h3>Loading...</h3>");
		var isHeaderChecked = true;
		if ($('#includeHeader').is(":checked")) {
			isHeaderChecked = true;
		} else {
			isHeaderChecked = false;
		}
		
		var isAccountChecked = true;
		if ($('#includeAccount').is(":checked")) {
			isAccountChecked = true;
		} else {
			isAccountChecked = false;
		}
		$.ajax({
			url : contextPath + "/web/dynamic-document-forms/filter",
			type : 'GET',
			data : {
				employeePid : $("#dbEmployee").val(),
				documentPid : $("#dbDocument").val(),
				formPid : $("#dbForm").val(),
				filterBy : $("#dbDateSearch").val(),
				fromDate : $("#txtFromDate").val(),
				toDate : $("#txtToDate").val(),
				includeHeader : isHeaderChecked,
				includeAccount:isAccountChecked
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
		var id=1;
		if (elementNameToShow != null && elementNameToShow.length > 0) {
			$
					.each(
							elementNameToShow,
							function(index, elementName) {
								$("#tblHead")
										.append(
												'<th data-initialsortorder="asc"><div style="float: right;">'
														+elementName.toString().replace(" ", "")+''
														+ '<span onclick="DynamicDocumentForm.filterDynamicReport(this);" class="glyphicon glyphicon-filter"></span><span onclick="DynamicDocumentForm.sortDynamicReport(this);" class="glyphicon glyphicon-sort"></span></div></th>')
							});
		}
	}

	function createTableBody(elementValues, length) {
		if (elementValues != null && elementValues.length > 0) {
			$.each(elementValues, function(index, formDetail) {
				var cssClass = index % 2 === 0 ? "odd" : "even";
				var row = $('<tr class="' + cssClass + '">');
				$("#tblBody").append(row);
				for (var i = 0; i < length; i++) {
					console.log(formDetail[0].replace(/[0-9]/g, ''));
					var value;
				
					if(i == 0){
						// remove numbers in AccountProfileName
						value = formDetail[i] == undefined
						|| formDetail[i] == null ? "" : formDetail[i].replace(/[^A-Za-z]+/g, '');
					}else{
						value = formDetail[i] == undefined
						|| formDetail[i] == null ? "" : formDetail[i];
					}
					
					
					row.append('<td>' + value + '</td>');
				}
				$("#tblBody").append('</tr>');
			});
		}
	}

	DynamicDocumentForm.filterDynamicReport = function(elm) {
		filterBySelectedHeader($(elm).closest('th'));
	}

	DynamicDocumentForm.sortDynamicReport = function(elm) {
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

})();