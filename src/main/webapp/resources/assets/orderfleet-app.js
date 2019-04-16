//  This file creates a global Orderfleet object containing the following methods: 
//  searchTable

var Orderfleet = Orderfleet || {};

(function($, window) {
	"use strict";
	
	$(document).ready(function() {
		Orderfleet.contextPath = location.protocol + '//' + location.host;
		// filter pop up search text box
		$('#ofTxtSearch').keyup(function() {
			var searchContianer = $(".search-results-panes").children(".search-results-pane:visible");
			searchCheckbox($(this).val(), searchContianer)
		});
		$('#ofKeyupSearch').keyup(function() {
			var table = $(".ofKeyupTable");
			var columnsCount = table.find('tbody > tr:first').find('td').length;
			searchHTMLTable($(this).val(), table.find('tbody'), columnsCount);
		});
	});
	
	Orderfleet.searchTable = function(searchString, table) {
		var columnsCount;
		var tbody;
		if(table === undefined){
			columnsCount = $(".of-tbl-search").find('tbody > tr:first').find('td').length;
			tbody = $(".of-tbl-search").find('tbody');
		} else {
			columnsCount = table.find('tbody > tr:first').find('td').length;
			tbody = table.find('tbody');
		}
		searchHTMLTable(searchString, tbody, columnsCount);
	}

	function searchCheckbox(searchString, container) {
		container.find('.col-md-4 div').each(function(index, element) {
			var text = $(element).find("label").text();
			if(text.length > 0){
				var regExp = new RegExp(searchString, 'i');
				if (regExp.test(text)) {
					$(element).show();
				} else {
					$(element).hide();
				}
			}
		});
	}
	
	function searchHTMLTable(searchString, table, columnsCount) {
		// Loop through all table rows, and hide those who don't match the search query
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index < columnsCount) {
						var regExp = new RegExp(searchString, 'i');
						if (regExp.test($(td).text())) {
							found = true;
							return false;
						}
					}
				});
				if (found == true)
					$(row).show();
				else
					$(row).hide();
			}
		});
	}
	
	Orderfleet.getParameterByName = function(name, url) {
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
})(jQuery, window);
