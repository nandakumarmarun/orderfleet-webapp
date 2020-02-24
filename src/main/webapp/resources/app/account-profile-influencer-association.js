// Create a InventoryVoucher object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountProfileInfluencerAssociation) {
	this.AccountProfileInfluencerAssociation = {};
}

(function() {
	'use strict';

	var accountProfileInfluencerAssociationContextPath = location.protocol
			+ '//' + location.host + location.pathname;

	$(document)
			.ready(
					function() {

						$('.selectpicker').selectpicker();

						console
								.log(accountProfileInfluencerAssociationContextPath);
						$('#btnApply').on('click', function() {
							// InventoryVoucher.filter();
							loadAllAssociatedAccountProfiles();

						});

						$('#btnSaveAssociatedAccountProfile').on('click',
								function() {
									// InventoryVoucher.filter();
									saveAssociatedAccountProfiles();

								});

						$("#dbAccountTypes").change(function() {
							loadAccountProfiles();
							loadAssociatedAccountTypes();
						});

						$('#btnSearch_accountProfile').click(
								function() {
									searchAccountProfileTable($(
											"#search_accountProfiles").val());
								});

						$('input:checkbox.allcheckbox')
								.click(
										function() {
											$(this)
													.closest('table')
													.find(
															'tbody tr td input[type="checkbox"]:visible')
													.prop(
															'checked',
															$(this).prop(
																	'checked'));
										});

					});

	function searchAccountProfileTable(inputVal) {

		console.log(inputVal);
		var table = $('#tblAllAssociateAccountProfiles');
		var filterBy = $("input[name='filter_accountProfile']:checked").val();
		table.find('tr').each(
				function(index, row) {
					var allCells = $(row).find('td');
					if (allCells.length > 0) {
						var found = false;
						allCells.each(function(index, td) {
							if (index == 0) {
								if (filterBy != "all") {
									var val = $(td).find('input').prop(
											'checked');
									var deselected = $(td).closest('tr').find(
											'td:eq(2)').text();
									if (filterBy == "selected") {
										if (!val) {
											return false;
										}
									} else if (filterBy == "unselected") {
										if (val) {
											return false;
										}
									} else if (filterBy == "deactivated") {
										if (deselected == "true") {
											return false;
										}
									}
								}
							}
							var regExp = new RegExp(inputVal, 'i');
							if (regExp.test($(td).text())) {
								found = true;
								return false;
							}
						});
						if (found == true)
							$(row).show();
						else
							$(row).hide();
					}
				});
	}

	function loadAccountProfiles() {
		if ($('#dbAccountTypes').val() == "-1") {
			$("#dbAccount").html("<option>--Select--</option>");
			alert("Please Select Influencer Account Type");
			return;
		}
		var accountType = $('#dbAccountTypes').val();
		$("#dbAccount").html("<option>Account Profiles loading...</option>")
		$.ajax({
			url : accountProfileInfluencerAssociationContextPath
					+ "/load-account-profiles",
			type : 'GET',
			data : {
				accountTypePid : accountType,
			},
			success : function(accountProfiles) {
				$("#dbAccount").html("<option value='-1'>--Select--</option>")
				$.each(accountProfiles, function(key, accountProfile) {
					$("#dbAccount").append(
							"<option value='" + accountProfile.pid + "'>"
									+ accountProfile.name + "</option>");
				});
			}
		});
	}

	function loadAssociatedAccountTypes() {
		if ($('#dbAccountTypes').val() == "-1") {
			$("#dbAssociatedAccountTypes").html("<option>All</option>");
			alert("Please Select Influencer Account Type");
			return;
		}
		var accountType = $('#dbAccountTypes').val();
		$("#dbAssociatedAccountTypes").html(
				"<option>Account Types loading...</option>")
		$.ajax({
			url : accountProfileInfluencerAssociationContextPath
					+ "/load-associated-account-types",
			type : 'GET',
			data : {
				accountTypePid : accountType,
			},
			success : function(accountTypes) {
				$("#dbAssociatedAccountTypes").html(
						"<option value='no'>All</option>")
				$.each(accountTypes, function(key, associatedAccountType) {
					$("#dbAssociatedAccountTypes").append(
							"<option value='" + associatedAccountType.pid
									+ "'>" + associatedAccountType.name
									+ "</option>");
				});
			}
		});
	}

	function loadAllAssociatedAccountProfiles() {

		if ($('#dbAccountTypes').val() == "-1") {
			$("#dbAccount").html("<option>--Select--</option>");
			$("#dbAssociatedAccountTypes").html("<option>All</option>");
			alert("Please Select Influencer Account Type");
			return;
		}

		if ($('#dbAccount').val() == "-1") {
			$("#dbAccount").html("<option>--Select--</option>");
			$("#dbAssociatedAccountTypes").html("<option>All</option>");
			alert("Please Select Influencer Account");
			loadAccountProfiles();
			loadAssociatedAccountTypes();
			return;
		}

		$("#associatedAccountProfileCheckboxes input:checkbox").attr('checked',
				false);
		$("#tblAllAssociateAccountProfiles").html(
				"<tr><td>Loading...</td></tr>")
		$
				.ajax({
					url : accountProfileInfluencerAssociationContextPath
							+ "/load-all-associated-account-profiles",
					type : 'GET',
					data : {
						accountPid : $('#dbAccount').val(),
						associatedAccountTypePid : $(
								"#dbAssociatedAccountTypes").val(),
						loadAssociatedAccountsOnly : $(
								'#loadAssociatedAccountsOnly').is(":checked"),
						influencerAccountTypePid : $('#dbAccountTypes').val()
					},
					success : function(response) {

						$("#tblAllAssociateAccountProfiles").html("")

						$("input[name='filter_accountProfile'][value='all']")
								.prop("checked", true);

						$
								.each(
										response.accountProfileDtos,
										function(key, accountProfile) {
											$("#tblAllAssociateAccountProfiles")
													.append(
															"<tr><td><input name='associatedAccountProfile' type='checkbox' value='"
																	+ accountProfile.pid
																	+ "' style='display: block;' /></td><td>"
																	+ accountProfile.name
																	+ "</td></tr>");
										});

						$.each(response.associatedAccountProfilePids, function(
								index, pid) {

							$(
									"#associatedAccountProfileCheckboxes input:checkbox[value="
											+ pid + "]").prop("checked", true);
						});

					}
				});

		$('#savingStatus').html("");
	}

	function saveAssociatedAccountProfiles() {

		$('#savingStatus').html("Saving..");

		var selectedAccountProfiles = "";

		$.each($("input[name='associatedAccountProfile']:checked"), function() {
			selectedAccountProfiles += $(this).val() + "~"
					+ $('#drop-' + $(this).val()).val() + ",";

		});

		if (selectedAccountProfiles == "") {
			$(".error-msg").html("Please select Account Profiles");
			return;
		}

		console.log($('#dbAccount').val() + "------------"
				+ selectedAccountProfiles);

		accountPid: $('#dbAccount').val(), $.ajax({
			url : accountProfileInfluencerAssociationContextPath
					+ "/assignAssociatedAccountProfiles",
			type : "POST",
			data : {
				pid : $('#dbAccount').val(),
				assignedAccountProfiles : selectedAccountProfiles

			},
			success : function(status) {
				$('#savingStatus').html("Saving Success");

			
				loadAllAssociatedAccountProfiles()
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

})();