if (!this.AssignProduct) {
	this.AssignProduct = {};
}
(function() {
	'use strict';

	var assignProductContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document).ready(
			function() {
				$('input:checkbox.allcheckbox').click(
						function() {
							if (!$('.search-results-panes').find(
									'input:checkbox:visible').is('checked')) {
								$('.search-results-panes').find(
										'input:checkbox:visible').prop(
										'checked', $(this).prop('checked'));
							} else {
								$('.search-results-panes').find(
										'input:checkbox:visible').removeAttr(
										'checked');
							}
						});
			});

	var categoryPids = [];
	var groupPids = [];
	AssignProduct.assignGroupAndCategory = function() {
		if ($("#dbCompany").val() == "no") {
			alert("Please Select Company");
			return;
		}
		if ($("#dbUser").val() == "no") {
			alert("Please Select User");
			return;
		}
		categoryPids = [];
		groupPids = [];
		var categoryCount = [];
		var groupCount = [];
		$("#pCategory").find('input[type="checkbox"]:checked').each(function() {
			categoryPids.push($(this).val());
		});
		$("#pGroup").find('input[type="checkbox"]:checked').each(function() {
			groupPids.push($(this).val());
		});
		$("#pCategory").find('input[type="checkbox"]').each(function() {
			categoryCount.push($(this).val());
		});
		$("#pGroup").find('input[type="checkbox"]').each(function() {
			groupCount.push($(this).val());
		});
		if (categoryCount.length == categoryPids.length) {
			$("#selectCategory").html("All");
		} else {
			$("#selectCategory").html("Customised");
		}
		if (groupCount.length == groupPids.length) {
			$("#selectGroup").html("All");
		} else {
			$("#selectGroup").html("Customised");
		}
		var selectedUserPid = "";

		$("#dbUser option:selected").each(function(i) {
			selectedUserPid += $(this).text() + ",";
		});
		$("#companyName").html($("#dbCompany option:selected").text());
		$("#userName").html(selectedUserPid);
		$("#selectedValueModal").modal('show');
	}

	AssignProduct.loadUser = function() {
		if ($("#dbCompany").val() == "no") {
			alert("Please Select Company");
			return;
		}
		$("#dbUser").html('<option>Loading...</option>');
		$("#productGroupAdd").html("");
		$("#productCategoryAdd").html("");
		$
				.ajax({
					url : assignProductContextPath + "/loadUser",
					method : 'GET',
					data : {
						companyPid : $("#dbCompany").val(),
					},
					success : function(assignProductSetUpDTOs) {
						$('input[name = checkAll]').prop('checked', true);
						$("#dbUser").html(
								'<option value="no">Select User</option>');
						if (assignProductSetUpDTOs.userDTOs != null
								&& assignProductSetUpDTOs.userDTOs.length > 0) {
							$.each(assignProductSetUpDTOs.userDTOs,
									function(index, user) {
										$("#dbUser").append(
												'<option value="' + user.pid
														+ '">' + user.firstName
														+ '</option>');
									});
						}
						if (assignProductSetUpDTOs.productGroupDTOs != null
								&& assignProductSetUpDTOs.productGroupDTOs.length > 0) {
							$
									.each(
											assignProductSetUpDTOs.productGroupDTOs,
											function(index, group) {
												$("#productGroupAdd")
														.append(
																'<div class="col-md-4"><div class="checkbox"><label> <input name="'
																		+ group.pid
																		+ '" value="'
																		+ group.pid
																		+ '" type="checkbox"> '
																		+ group.name
																		+ ''
																		+ '</label></div></div>');
												$(
														'input[name = '
																+ group.pid
																+ ']').prop(
														'checked', true);
											});
						}
						if (assignProductSetUpDTOs.productCategoryDTOs != null
								&& assignProductSetUpDTOs.productCategoryDTOs.length > 0) {
							$
									.each(
											assignProductSetUpDTOs.productCategoryDTOs,
											function(index, catagory) {
												$("#productCategoryAdd")
														.append(
																'<div class="col-md-4"><div class="checkbox"><label> <input name="'
																		+ catagory.pid
																		+ '" value="'
																		+ catagory.pid
																		+ '" type="checkbox"> '
																		+ catagory.name
																		+ ''
																		+ '</label></div></div>');
												$(
														'input[name = '
																+ catagory.pid
																+ ']').prop(
														'checked', true);
											});
						}

					},

				});
	}

	AssignProduct.assignProduct = function() {

		if ($("#dbCompany").val() == "no") {
			alert("Please Select Company");
			return;
		}
		if ($("#dbUser").val() == "no") {
			alert("Please Select User");
			return;
		}
		var selectedUserPid = "";

		$("#dbUser option:selected").each(function(i) {
			selectedUserPid += $(this).val() + ",";
		});
		$.ajax({
			url : assignProductContextPath + "/assign-product",
			method : 'GET',
			data : {
				companyPid : $("#dbCompany").val(),
				selectedUserPid : selectedUserPid,
				categoryPids : categoryPids.join(","),
				groupPids : groupPids.join(",")
			},
			success : function(data) {
				window.location = assignProductContextPath;
			},

		});
	}
})();