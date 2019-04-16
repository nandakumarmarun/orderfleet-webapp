// Create a DynamicReport object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.DynamicReport) {
	this.DynamicReport = {};
}

(function() {
	'use strict';
	var contextPath = location.protocol + '//' + location.host;
	var selectedColIndex;
	$(document).ready(function() {
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
			$('#tblDynamicReport tbody tr').show();
		});
		$('#dbColumnName').on('change', function() {
			var selectedVal = $(this).val().trim();
			$('#tblDynamicReport thead').find('th').each(function() {
				var thVal = $(this).text().trim();
			    if(thVal == selectedVal) {
			    	filterBySelectedHeader($(this));
			    	return false; //break;
			    }
			});
		});
		
	});

	function filterDynamicReport(elm) {
		filterBySelectedHeader($(elm).closest('th'));
	}

	DynamicReport.sortDynamicReport = function(elm) {
		sortBySelectedHeader($(elm).closest('th'));
	}

	function filterBySelectedHeader(thElm) {
		selectedColIndex = $(thElm).index();
		var index = selectedColIndex + 1;
		$("#mdlTitle").html($(thElm).text());
		var items = [];
		var options = [];

		//Iterate all td's in second column
		$('#tblDynamicReport tbody tr td:nth-child(' + index + ')').each(
				function() {
					if($(this).text().trim() != "" && $(this).text().trim() != "."){
						//add item to array
						items.push($(this).text());
					}
				});
		//restrict array to unique items
		var uniqueItems = $.unique(items);

		//iterate unique array and build array of select options
		$.each(uniqueItems, function(i, item) {
			options.push('<option value="' + item + '">' + item + '</option>');
		})

		//finally empty the select and append the items from the array
		$('#sbDynamicReport').empty().append(options.join());

		$('#sbDynamicReport').multiselect('rebuild');

		$("#mdlDynamicReport").modal("show");
	}

	function sortBySelectedHeader(thElm) {
		var $tbody = $('#tblDynamicReport tbody');
		var dir = $(thElm).data("initialsortorder");
		$(thElm).data("initialsortorder", dir == "asc" ? "desc" : "asc");
		$tbody.find('tr').sort(function(a, b) {
			var tda = $(a).find('td:eq(' + $(thElm).index() + ')').text(); // can replace 1 with the column you want to sort on
			var tdb = $(b).find('td:eq(' + $(thElm).index() + ')').text(); // this will sort on the second column
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
		$('#tblDynamicReport').find('tbody').find('tr')
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

})();