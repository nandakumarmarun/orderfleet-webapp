if (!this.ProductProfileGroup) {
	this.ProductProfileGroup = {};
}

(function() {
	'use strict';

	var productProfileGroupContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(
			function() {

				$('#btnSearch').click(function() {
					searchTable($("#search").val());
				});

				$('input:checkbox.allcheckbox').click(
						function() {
							$(this).closest('table').find(
									'tbody tr td input[type="checkbox"]').prop(
									'checked', $(this).prop('checked'));
						});

				loadAllProductProfiles();

			});

	function searchTable(inputVal) {
		var table = $('#tBodyProductProfileGroup');
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index != 7) {
						var regExp = new RegExp(inputVal, 'i');
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

	ProductProfileGroup.productNameSearch = function(e) {
		var inputVal = e.value;
		$('#tBodyFieldProducts').find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 1) {
						var regExp = new RegExp(inputVal, 'i');
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

	ProductProfileGroup.checkboxChange = function(e) {
		if (e.checked) {
			$('#tBodyFieldProducts').find(
					'tr:visible td input[type="checkbox"]').each(function() {
				$(this).prop('checked', true);
			});
		} else {
			$('#tBodyFieldProducts').find(
					'tr:visible td input[type="checkbox"]').each(function() {
				$(this).prop('checked', false);
			});
		}

	}

	ProductProfileGroup.filterByCategoryAndGroup = function() {
		var categoryPids = [];
		var groupPids = [];
		$("#pCategory").find('input[type="checkbox"]:checked').each(function() {
			categoryPids.push($(this).val());
		});
		$("#pGroup").find('input[type="checkbox"]:checked').each(function() {
			groupPids.push($(this).val());
		});
		$('#tBodyProductProfileGroup').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : productProfileGroupContextPath + "/filterByCategoryGroup",
			method : 'GET',
			data : {
				categoryPids : categoryPids.join(","),
				groupPids : groupPids.join(",")
			},
			success : function(productProfileGroupDTOs) {
				displayData(productProfileGroupDTOs);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function loadAllProductProfiles() {
		$('#tBodyProductProfileGroup').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : productProfileGroupContextPath + "/filterByCategoryGroup",
			method : 'GET',
			data : {
				categoryPids : "",
				groupPids : ""
			},
			success : function(productProfileGroupDTOs) {
				displayData(productProfileGroupDTOs);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function displayData(productProfileGroupDTOs) {

		$('#tBodyProductProfileGroup').html("");
		// select box for set size
		$('#field_products').html("");
		if (productProfileGroupDTOs.length == 0) {
			$('#tBodyProductProfileGroup')
					.html(
							"<tr><td colspan='9' align='center'>No data available</td></tr>");
			return;
		}
		$('#field_products')
				.append(
						'<thead><tr><th>Select <br /><input type="checkbox" onchange="ProductProfileGroup.checkboxChange(this);"  /></th><th>Name<input type="text" onkeyup="ProductProfileGroup.productNameSearch(this);" id="productNameSearch" placeholder="search" class="form-control" style="width: 200px;"></th></tr></thead><tbody id="tBodyFieldProducts">');
		$
				.each(
						productProfileGroupDTOs,
						function(index, productProfileGroup) {
							var productG = "";
							if (productProfileGroup.productGroupDTOs != null) {
							$
									.each(
											productProfileGroup.productGroupDTOs,
											function(index, productGroup) {
												if (productProfileGroup.productGroupDTOs.length > 1) {
													productG += productGroup.name
															+ "<br>";
												} else {
													productG += productGroup.name;
												}
											});}

							else {
								productG = "";
								console.log(productG);
							}
							$('#tBodyProductProfileGroup')
									.append(
											"<tr><td>"
													+ productProfileGroup.productProfileDTO.name
													+ "</td><td>"
													+ productProfileGroup.productProfileDTO.productCategoryName
													+ "</td><td>"
													+ productProfileGroup.productProfileDTO.price
													+ "</td><td>" + productG
													+ "</td></tr>");
						});
	}

})();