// Create a AccountProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.AccountProfile) {
	this.AccountProfile = {};
}

(function() {
	'use strict';

	var accountProfileContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#accountProfileForm");
	var deleteForm = $("#deleteForm");
	var locationRadius = $("#locationRadiusForm");

	var accountProfileModel = {
		pid : null,
		name : null,
		alias : null,
		accountTypePid : null,
		defaultPriceLevelPid : null,
		city : null,
		location : null,
		pin : null,
		phone1 : null,
		phone2 : null,
		email1 : null,
		email2 : null,
		whatsAppNo : null,
		address : null,
		description : null,
		creditDays : null,
		creditLimit : null,
		leadToCashStage : null,
		contactPerson : null,
		defaultDiscountPercentage : null,
		closingBalance : 0,
		tinNo : null,
		locationRadius : 0,
		customerId : null,
		countryId : null,
		stateId : null,
		districtId : null

	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
		},
		accountTypePid : {
			valueNotEquals : "-1"
		},
		city : {
			required : true,
			maxlength : 100
		},
		location : {
			maxlength : 100
		},
		pin : {
			maxlength : 15
		},
		phone1 : {
			maxlength : 20
		},
		phone2 : {
			maxlength : 20
		},
		email1 : {
			maxlength : 100
		},
		email2 : {
			maxlength : 100
		},
		address : {
			required : true,
			maxlength : 250
		},
		countryId : {
			valueNotEquals : "-1"
		},
		stateId : {
			valueNotEquals : "-1"
		},
		districtId : {
			valueNotEquals : "-1"
		},

		description : {
			maxlength : 250
		},
		locationRadius : {
			maxlength : 250
		}

	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		alias : {
			maxlength : "This field cannot be longer than 55 characters."
		},
		city : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 100 characters."
		},
		location : {
			maxlength : "This field cannot be longer than 100 characters."
		},
		pin : {
			maxlength : "This field cannot be longer than 15 characters."
		},
		phone1 : {
			maxlength : "This field cannot be longer than 20 characters."
		},
		phone2 : {
			maxlength : "This field cannot be longer than 20 characters."
		},
		email1 : {
			maxlength : "This field cannot be longer than 100 characters."
		},
		email2 : {
			maxlength : "This field cannot be longer than 100 characters."
		},
		address : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 250 characters."
		},

		description : {
			maxlength : "This field cannot be longer than 250 characters."
		},
		locationRadius : {
			maxlength : "This field cannot be longer than 250 characters."
		}

	};

	$(document)
			.ready(
					function() {

						$('.selectpicker').selectpicker();

						// table search
						$('#btnSearch').click(function() {
							searchTable($("#search").val());
						});

						$('#setLocationRadius').click(function() {
							$(".error-msg").html("");
							$("#locationRadiusModal").modal('show');
						});

						$('#saveLocationRadius').click(function() {
							saveLocationRadius();
						});

						$("#dbCountry").change(function() {
							loadStates();
						});

						$("#dbState").change(function() {
							loadDistricts();
						});

						$("#dbCountrycreate").change(function() {
							loadStatescreate();
						});

						$("#dbStatecreate").change(function() {
							loadDistrictscreate();
						});

						// add the rule here
						$.validator.addMethod("valueNotEquals", function(value,
								element, arg) {
							return arg != value;
						}, "");

						createEditForm.validate({
							rules : validationRules,
							messages : validationMessages,
							submitHandler : function(form) {
								createUpdateAccountProfile(form);
							}
						});

						deleteForm.submit(function(e) {
							// prevent Default functionality
							e.preventDefault();
							// pass the action-url of the form
							deleteAccountProfile(e.currentTarget.action);
						});
						var importstatusparameter = "";
						var accounttypeparameter = "";
						if (location.search == "") {
							AccountProfile
									.loadAccountProfiles(accounttypeparameter,
											importstatusparameter);
						} else {
							var importstatusparameter = location.search
									.split('=');
							var accounttypeparameter = importstatusparameter[1]
									.split('&')[0];
							AccountProfile.loadAccountProfiles(
									accounttypeparameter,
									importstatusparameter[2]);
						}
						$('#btnActivateAccountProfile').on('click', function() {
							activateAssignedAccountProfile();
						});

						$('input:checkbox.allcheckbox')
								.click(
										function() {
											$(this)
													.closest('table')
													.find(
															'tbody tr td input[type="checkbox"]')
													.prop(
															'checked',
															$(this).prop(
																	'checked'));
										});

						$('#btnDownload')
								.on(
										'click',
										function() {
											var tblAccountProfile = $("#tblAccountProfile tbody");

											if (tblAccountProfile.children().length == 0) {
												alert("no values available");
												return;
											}
											if (tblAccountProfile[0].textContent == "No data available") {
												alert("no values available");
												return;
											}

											var data = [];
											var i = 0;
											$('#tblAccountProfile')
													.find('.userName')
													.each(
															function() {
																data[i] = $(
																		this)
																		.html();
																$(this)
																		.html(
																				data[i]
																						.replace(
																								" ",
																								""));
																i++;
															});

											downloadXls();

											i = 0;
											$('#tblAccountProfile').find(
													'.userName').each(
													function() {
														$(this).html(data[i]);
														i++;
													});

										});

						$('#slt_status').on('change', function() {
							findByfilfter();
						});

						$('#slt_status').val("Active");
					});

	function loadStates() {

		alert('hai');

		var countryId = $("#dbCountry").val();
		$.ajax({
			url : accountProfileContextPath + "/loadStates",
			method : 'GET',
			data : {
				countryId : countryId
			},
			success : function(states) {
				$("#dbState").html("<option value='-1'>All</option>")
				$.each(states, function(index, state) {

					$("#dbState").append(
							"<option value='" + state.id + "'>" + state.name
									+ "</option>");
				});
			},
			error : function(xhr, error) {

				onError(xhr, error);
			}
		});

	}

	function loadDistricts() {

		var stateId = $("#dbState").val();
		$.ajax({
			url : accountProfileContextPath + "/loadDistricts",
			method : 'GET',
			data : {
				stateId : stateId
			},
			success : function(districts) {
				$("#dbDistrict").html("<option value='-1'>All</option>")
				$.each(districts, function(index, district) {

					$("#dbDistrict").append(
							"<option value='" + district.id + "'>"
									+ district.name + "</option>");
				});
			},
			error : function(xhr, error) {

				onError(xhr, error);
			}
		});

	}

	function loadStatescreate() {

		var countryId = $("#dbCountrycreate").val();
		$.ajax({
			url : accountProfileContextPath + "/loadStates",
			method : 'GET',
			data : {
				countryId : countryId
			},
			success : function(states) {
				$("#dbStatecreate").html(
						"<option value='-1'>Select State</option>")
				$.each(states, function(index, state) {

					$("#dbStatecreate").append(
							"<option value='" + state.id + "'>" + state.name
									+ "</option>");
				});
			},
			error : function(xhr, error) {

				onError(xhr, error);
			}
		});

	}

	function loadDistrictscreate() {

		var stateId = $("#dbStatecreate").val();
		$.ajax({
			url : accountProfileContextPath + "/loadDistricts",
			method : 'GET',
			data : {
				stateId : stateId
			},
			success : function(districts) {
				$("#dbDistrictcreate").html(
						"<option value='-1'>Select District</option>")
				$.each(districts, function(index, district) {

					$("#dbDistrictcreate").append(
							"<option value='" + district.id + "'>"
									+ district.name + "</option>");
				});
			},
			error : function(xhr, error) {

				onError(xhr, error);
			}
		});

	}

	function saveLocationRadius() {

		$(".error-msg").html("Please Wait!..Saving..");

		var locationRadius = $('#field_locationradius').val();

		$.ajax({
			url : accountProfileContextPath + "/saveLocationRadius",
			method : 'GET',
			data : {
				locationRadius : locationRadius
			},
			success : function(accountProfiles) {
				$("#locationRadiusModal").modal('hide');
				$(".error-msg").html("");
			},
			error : function(xhr, error) {
				$(".error-msg").html("Saving Failed.Error");
				onError(xhr, error);
			}
		});

	}

	function downloadXls() {
		var status = $('#slt_status').val();
		console.log(status);
		window.location.href = accountProfileContextPath
				+ "/download-profile-xls?status=" + status;
		/*
		 * // Avoid last column in each row $("#tblAccountProfile th:last-child,
		 * #tblAccountProfile td:last-child").hide();
		 * 
		 * var excelName = "accountProfile"; var table2excel = new
		 * Table2Excel();
		 * table2excel.export(document.getElementById('tblAccountProfile'),excelName);
		 * $("#tblAccountProfile th:last-child, #tblAccountProfile
		 * td:last-child").show();
		 */
	}

	function activateAssignedAccountProfile() {
		$(".error-msg").html("");
		var selectedAccountProfile = "";

		$.each($("input[name='accountprofile']:checked"), function() {
			selectedAccountProfile += $(this).val() + ",";
		});

		if (selectedAccountProfile == "") {
			$(".error-msg").html("Please select Account Profile");
			return;
		}
		$.ajax({
			url : accountProfileContextPath + "/activateAccountProfile",
			type : "POST",
			data : {
				accountprofiles : selectedAccountProfile,
			},
			success : function(status) {
				$("#enableAccountProfileModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	// Value of Imported is "Imported" get imported status is true
	AccountProfile.loadAccountProfiles = function(accountType, importStatus) {
		var accounttypepid = "";
		var imports = "";
		if (accountType != "" && importStatus != "") {
			if (accountType != "No" && importStatus != "No") {
				accounttypepid = accountType;
				if (importStatus == "Imported") {
					imports = "true";
				} else {
					imports = "false";
				}
			}
			if (accountType == "No" && importStatus != "No") {
				if (importStatus == "Imported") {
					imports = "true";
				} else {
					imports = "false";
				}
			}
			if (accountType != "No" && importStatus == "No") {
				accounttypepid = accountType;
			}
		}

		$('#tBodyAccountProfile').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : accountProfileContextPath + "/filterByAccountType",
			method : 'GET',
			data : {
				accountTypePids : accounttypepid,
				importedStatus : imports
			},
			success : function(accountProfiles) {
				addTableBodyvalues(accountProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	AccountProfile.filterByAccountType = function() {
		var accountTypePids = [];
		var importedStatus = "";
		$("#paccountType").find('input[type="checkbox"]:checked').each(
				function() {
					accountTypePids.push($(this).val());
				});
		var imports = $("input[name='import']:checked").val();
		console.log(imports);

		$('#tBodyAccountProfile').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$.ajax({
			url : accountProfileContextPath + "/filterByAccountType",
			method : 'GET',
			data : {
				accountTypePids : accountTypePids.join(","),
				importedStatus : imports,
			},

			success : function(accountProfiles) {
				addTableBodyvalues(accountProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function findByfilfter() {
		var active, deactivate;
		var statusBox = $("#slt_status").val();
		if (statusBox == "All") {
			active = true;
			deactivate = true;
		} else if (statusBox == "Active") {
			active = true;
			deactivate = false;
		} else if (statusBox == "Deactive") {
			deactivate = true;
			active = false;
		} else if (statusBox == "MultipleActivate") {
			AccountProfile.showModalPopup($('#enableAccountProfileModal'));
		}
		$.ajax({
			url : accountProfileContextPath + "/get-by-status-filter",
			type : "GET",
			data : {
				active : active,
				deactivate : deactivate
			},
			success : function(accountProfiles) {
				addTableBodyvalues(accountProfiles);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}

	function addTableBodyvalues(accountProfiles) {

		$('#tBodyAccountProfile').html("");
		if (accountProfiles.length == 0) {
			$('#tBodyAccountProfile')
					.html(
							"<tr><td colspan='9' align='center'>No data available</td></tr>");
			return;
		}
		$('#tBodyAccountProfile').html("");
		/*
		 * &nbsp;<button type='button' class='btn btn-danger'
		 * onclick='AccountProfile.showModalPopup($(\"#deleteModal\"),\"" +
		 * accountProfile.pid + "\",2);'>Delete</button>
		 */
		$
				.each(
						accountProfiles,
						function(index, accountProfile) {
							var gstRegistrationType = accountProfile.gstRegistrationType;
							if (gstRegistrationType == null) {
								gstRegistrationType = "";
							}
							$('#tBodyAccountProfile')
									.append(
											"<tr><td title='click to view' class='sa'><span style='color: #2C7BD0; cursor: pointer;' onclick='AccountProfile.showModalPopup($(\"#viewModal\"),\""
													+ accountProfile.pid
													+ "\",0);'>"
													+ accountProfile.name
													+ "</span></td><td>"
													+ accountProfile.accountTypeName
													+ "</td><td>"
													+ accountProfile.closingBalance
															.toFixed(2)
													+ "</td><td>"
													+ accountProfile.address
													+ "</td><td>"
													+ (accountProfile.phone1 == null ? ""
															: accountProfile.phone1)
													+ "</td><td>"
													+ (accountProfile.email1 == null ? ""
															: accountProfile.email1)
													+ "</td><td>"
													+ (accountProfile.whatsAppNo == null ? ""
															: accountProfile.whatsAppNo)
													+ "</td><td>"
													+ spanVerify(
															accountProfile.name,
															accountProfile.city,
															accountProfile.address,
															accountProfile.accountStatus,
															accountProfile.pid)
													+ "</td><td>"
													+ (accountProfile.tinNo == null ? ""
															: accountProfile.tinNo)
													+ "</td><td>"
													+ gstRegistrationType
													+ "</td><td>"
													+ convertDateTimeFromServer(accountProfile.createdDate)
													+ "</td><td>"
													+ convertDateTimeFromServer(accountProfile.lastModifiedDate)
													+ "</td><td class='userName'>"
													+ accountProfile.userName
													+ "</td><td>"
													+ (accountProfile.leadToCashStage == null ? ""
															: accountProfile.leadToCashStage)
													+ "</td><td>"
													+ spanActivated(
															accountProfile.name,
															accountProfile.city,
															accountProfile.address,
															accountProfile.activated,
															accountProfile.pid)
													+ "</td><td><i class='btn btn-blue entypo-pencil' title='Edit AccountProfile' onclick='AccountProfile.showModalPopup($(\"#myModal\"),\""
													+ accountProfile.pid
													+ "\",1);'></i>&nbsp;<a class='btn btn-info entypo-location' title='Assigned Locations' onclick='AccountProfile.showModalPopup($(\"#locationModal\"),\""
													+ accountProfile.pid
													+ "\",3);'></a></td></tr>");
						});
	}

	function spanVerify(name2, city2, address2, accountStatus2,
			accountProfilePid2) {
		var spanVerify = "";
		var name = "'" + name2 + "'";
		var city = "'" + city2 + "'";
		var address = "'" + address2 + "'";
		var accountProfile = "'" + accountProfilePid2 + "'";
		var accountStatus = "'" + accountStatus2 + "'";

		if (accountStatus == "'Verified'") {
			spanVerify = '<span class="label label-success" onclick="AccountProfile.setVerify('
					+ name
					+ ', '
					+ city
					+ ', '
					+ address
					+ ', '
					+ accountProfile
					+ ', '
					+ accountStatus
					+ ')" style="cursor: pointer;">Verified</span>';
		} else {
			spanVerify = '<span class="label label-danger" onclick="AccountProfile.setVerify('
					+ name
					+ ', '
					+ city
					+ ', '
					+ address
					+ ', '
					+ accountProfile
					+ ', '
					+ accountStatus
					+ ')" style="cursor: pointer;">Unverified</span>';
		}
		return spanVerify;

	}

	function spanActivated(name1, city1, address1, activated, accountProfilePid) {

		var spanActivated = "";
		var name = "'" + name1 + "'";
		var city = "'city1'";
		var address = "'address1'";
		var accountProfile = "'" + accountProfilePid + "'";
		if (activated) {
			spanActivated = '<span class="label label-success" onclick="AccountProfile.setActive('
					+ name
					+ ', '
					+ city
					+ ', '
					+ address
					+ ', '
					+ accountProfile
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="AccountProfile.setActive('
					+ name
					+ ', '
					+ city
					+ ', '
					+ address
					+ ', '
					+ accountProfile
					+ ','
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}

	AccountProfile.setActive = function(name, city, address, accountProfilePid,
			isActivated) {

		accountProfileModel.pid = accountProfilePid;
		accountProfileModel.activated = isActivated;
		accountProfileModel.name = name;
		accountProfileModel.city = city;
		accountProfileModel.address = address;

		if (confirm("Are you sure?")) {
			// update status;changeStatus

			$.ajax({
				url : accountProfileContextPath + "/changeStatus",
				method : 'POST',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(accountProfileModel),
				success : function(data) {

					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}
	AccountProfile.setVerify = function(name, city, address, accountProfilePid,
			verify) {

		accountProfileModel.pid = accountProfilePid;
		accountProfileModel.accountStatus = verify;
		accountProfileModel.name = name;
		accountProfileModel.city = city;
		accountProfileModel.address = address;

		if (confirm("Are you sure?")) {
			// update verify status

			$.ajax({
				url : accountProfileContextPath + "/verifyStatus",
				method : 'POST',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(accountProfileModel),
				success : function(data) {

					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

	}

	function createUpdateAccountProfile(el) {

		$(".error-msg").html("");
		if ($('#dbCountrycreate').val() == '-1') {
			$(".error-msg").html("Please select Country");
			return false;
		}
		if ($('#dbStatecreate').val() == '-1') {
			$(".error-msg").html("Please select State");
			return false;
		}
		if ($('#dbDistrictcreate').val() == '-1') {
			$(".error-msg").html("Please select District");
			return false;
		}
		accountProfileModel.name = $('#field_name').val();
		accountProfileModel.alias = $('#field_alias').val();
		accountProfileModel.accountTypePid = $('#field_accountType').val();
		accountProfileModel.defaultPriceLevelPid = $('#field_priceLevel').val();
		accountProfileModel.countryId = $('#dbCountrycreate').val();
		accountProfileModel.stateId = $('#dbStatecreate').val();
		accountProfileModel.districtId = $('#dbDistrictcreate').val();

		accountProfileModel.city = $('#field_city').val();
		accountProfileModel.location = $('#field_location').val();
		accountProfileModel.pin = $('#field_pin').val();
		accountProfileModel.phone1 = $('#field_phone1').val();
		accountProfileModel.phone2 = $('#field_phone2').val();
		accountProfileModel.email1 = $('#field_email1').val();
		accountProfileModel.email2 = $('#field_email2').val();
		accountProfileModel.whatsAppNo = $('#field_whatsAppNo').val();
		accountProfileModel.address = $('#field_address').val();
		accountProfileModel.description = $('#field_description').val();
		accountProfileModel.creditDays = $('#field_creditDays').val();
		accountProfileModel.creditLimit = $('#field_creditLimit').val();
		accountProfileModel.contactPerson = $('#field_contactPerson').val();
		accountProfileModel.defaultDiscountPercentage = $(
				'#field_defaultDiscountPercentage').val();
		accountProfileModel.closingBalance = $('#field_closingBalance').val();
		accountProfileModel.tinNo = $('#field_tinNo').val();
		accountProfileModel.customerId = $('#field_customerId').val();
		accountProfileModel.locationRadius = $('#fld_location_radius').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountProfileModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function locationRadius(el) {
		accountProfileModel.locationRadius = $('#field_locationradius').val();
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),

			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(accountProfileModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});

	}
	/*
	 * function locationRadius(id) { $.ajax({ url : accountProfileContextPath +
	 * "/accountProfiles/locationRadius" + id, method : 'GET', success :
	 * function(data) { $('#field_locationradius').text(data.locationRadius); },
	 * error : function(xhr, error) { onError(xhr, error); } }); }
	 */

	function showAccountProfile(id) {
		$(".error-msg").html("");
		$
				.ajax({
					url : accountProfileContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#lbl_name').text(data.name);
						$('#lbl_alias').text(
								(data.alias == null ? "" : data.alias));
						$('#lbl_accountType').text(data.accountTypeName);
						$('#lbl_priceLevel').text(data.defaultPriceLevelName);
						$('#lbl_city').text(data.city);
						$('#lbl_location').text(
								(data.location == null ? "" : data.location));
						$('#lbl_pin').text((data.pin == null ? "" : data.pin));
						$('#lbl_phone1').text(
								(data.phone1 == null ? "" : data.phone1));
						$('#lbl_phone2').text(
								(data.phone2 == null ? "" : data.phone2));
						$('#lbl_email1').text(
								(data.email1 == null ? "" : data.email1));
						$('#lbl_email2').text(
								(data.email2 == null ? "" : data.email2));
						$('#lbl_whatsAppNo')
								.text(
										(data.whatsAppNo == null ? ""
												: data.whatsAppNo));
						$('#lbl_address').text(data.address);
						$('#lbl_description').text(
								(data.description == null ? ""
										: data.description));
						$('#lbl_creditDays')
								.text(
										(data.creditDays == null ? ""
												: data.creditDays));
						$('#lbl_creditLimit').text(
								(data.creditLimit == null ? ""
										: data.creditLimit));
						$('#lbl_contactPerson').text(
								(data.contactPerson == null ? ""
										: data.contactPerson));
						$('#lbl_locationRadius').text(
								(data.locationRadius == null ? ""
										: data.locationRadius));

						$('#lbl_defaultDiscountPercentage').text(
								(data.defaultDiscountPercentage == null ? 0
										: data.defaultDiscountPercentage));
						$('#lbl_closingBalance').text(
								(data.closingBalance == null ? 0
										: data.closingBalance));
						$('#lbl_tinNo').text(
								(data.tinNo == null ? "" : data.tinNo));
						$('#lbl_customerId')
								.text(
										(data.customerId == null ? ""
												: data.customerId));
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function editAccountProfile(id) {
		$.ajax({
			url : accountProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val(data.alias);
				$('#field_accountType').val(data.accountTypePid);
				if (data.defaultPriceLevelPid)
					$('#field_priceLevel').val(data.defaultPriceLevelPid);
				$('#dbCountrycreate').val(data.countryId);
				$('#dbStatecreate').val(data.stateId);
				$('#dbDistrictcreate').val(data.districtId);

				$('#field_city').val(data.city);
				$('#field_location').val(data.location);
				$('#field_pin').val(data.pin);
				$('#field_phone1').val(data.phone1);
				$('#field_phone2').val(data.phone2);
				$('#field_email1').val(data.email1);
				$('#field_email2').val(data.email2);
				$('#field_whatsAppNo').val(data.whatsAppNo);
				$('#field_address').val(data.address);
				$('#field_description').val(data.description);
				$('#field_creditDays').val(data.creditDays);
				$('#field_creditLimit').val(data.creditLimit);
				$('#field_contactPerson').val(data.contactPerson);
				$('#field_defaultDiscountPercentage').val(
						data.defaultDiscountPercentage);
				$('#field_closingBalance').val(data.closingBalance);
				$('#field_tinNo').val(data.tinNo);
				$('#field_customerId').val(data.customerId);
				$('#fld_location_radius').val(data.locationRadius);

				// set pid
				accountProfileModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteAccountProfile(actionurl, id) {
		$.ajax({
			url : actionurl,
			method : 'DELETE',
			success : function(data) {
				onDeleteSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = accountProfileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = accountProfileContextPath;
	}

	AccountProfile.showModalPopup = function(el, id, action) {
		$(".error-msg").html("");
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showAccountProfile(id);
				break;
			case 1:
				editAccountProfile(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', accountProfileContextPath + "/" + id);
				break;
			case 3:
				showAccountProfileLocation(id);
				break;
			case 4:
				locationRadius(id);
				locationRadius.attr('method', 'PUT');
				saveLocationRadius();

			}
		}
		el.modal('show');
	}

	function showAccountProfileLocation(pid) {
		var assignedLocation = "";
		$('#tbllocation').html("");
		$.ajax({
			url : accountProfileContextPath + "/location/" + pid,
			method : 'GET',
			success : function(response) {
				console.log(response);
				$.each(response, function(index, location) {
					assignedLocation += location.locationName + "<br>";
				});
				$('#tbllocation').append(
						'<tr><td>' + assignedLocation + '</td></tr>');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function searchTable(inputVal) {
		var table = $('#tBodyAccountProfile');
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

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY, h:mm:ss a');
		} else {
			return "";
		}
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		accountProfileModel.pid = null; // reset accountProfile model;
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