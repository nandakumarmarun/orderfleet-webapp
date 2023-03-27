// Create a User object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.Slab) {
	this.Slab = {};
}

(/**
	 *
	 */
	function () {

		var userContextPath = location.protocol + '//' + location.host
			+ location.pathname;

		var createEditForm = $("#userForm");
		var deleteForm = $("#deleteForm");

		var slabModel = {
			pid: null,
			companyPid: null,
			companyName: null,
			minimumUser: null,
			maximumUser: null,
			slabRate: null,
		};

		// Specify the validation rules
		var validationRules = {
			minUser: {
				required: true,
				maxlength: 55
			},
			maxUser: {
				required: true,
				maxlength: 55
			},
			slabRate: {
				required: true,
				maxlength: 55
			},
			companyPid: {
				valueNotEquals: "-1"
			}
		};

		// Specify the validation error messages
		var validationMessages = {
			minUser: {
				required: "This field is required.",
				maxlength: "This field cannot be longer than 55 characters."
			},
			maxUser: {
				required: "This field is required.",
				maxlength: "This field cannot be longer than 55 characters."
			},
			slabRate: {
				required: "This field is required.",
				maxlength: "This field cannot be longer than 55 characters."
			},
		};

		$(document).ready(function () {
			Slab.filterUsers()

			$('.selectpicker').selectpicker();
			// add the rule here

			$.validator.addMethod("valueNotEquals", function (value, element, arg) {
				return arg != value;
			}, "");

			createEditForm.validate({
				rules: validationRules,
				messages: validationMessages,
				submitHandler: function (form) {
					createUpdateUser(form);
				}
			});



			deleteForm.submit(function (e) {
				// prevent Default functionality
				e.preventDefault();
				// pass the action-url of the form
				deleteUser(e.currentTarget.action);
			});

			$("#btnSearch").click(function () {
				console.log("searching .........")
				search();
			});

		});

		function createUpdateUser(el) {
			slabModel.minimumUser = $('#field_min_user').val();
			slabModel.maximumUser = $('#field_max_user').val();
			slabModel.slabRate = $('#field_slab_rate').val();
			slabModel.companyPid = $('#field_company').val();
			$.ajax({
				method: $(el).attr('method'),
				url: userContextPath + "/save-slab",
				contentType: "application/json; charset=utf-8",
				data: JSON.stringify(slabModel),
				success: function (data) {
					onSaveSuccess(data);
				},
				error: function (xhr, error) {
					onError(xhr, error);
				}
			});
		}

		// function showUser(id) {
		// 	$.ajax({
		// 		url: userContextPath + "/" + id,
		// 		method: 'GET',
		// 		success: function (data) {
		// 			$('#lbl_login').text(data.login);
		// 			$('#lbl_firstName').text(data.firstName);
		// 			$('#lbl_lastName').text(data.lastName);
		// 			$('#lbl_company').text(data.companyName);
		// 			$('#lbl_email').text(data.email);
		// 			$('#lbl_mobile').text(data.mobile);
		// 		},
		// 		error: function (xhr, error) {
		// 			onError(xhr, error);
		// 		}
		// 	});
		// }

		// function editUser(id) {
		// 	$.ajax({
		// 		url: userContextPath + "/" + id,
		// 		method: 'GET',
		// 		success: function (data) {
		// 			$('#field_min_user').val(data.login);
		// 			$('#field_max_user').val(data.password);
		// 			$('#field_slab_rate').val(data.firstName);
		// 			slabModel.pid = data.pid;
		// 		},
		// 		error: function (xhr, error) {
		// 			onError(xhr, error);
		// 		}
		// 	});
		// }

		function search() {
			var companyName = "";
			$.ajax({
				url: userContextPath + "/search",
				method: 'GET',
				data: {
					companyName: $('#search').val(),
				},
				success: function (data) {
					loadTableData(data)
				},
				error: function (xhr, error) {
					onError(xhr, error);
				}
			});
		}

		Slab.deleteUser = function (pid) {
			$.ajax({
				url: userContextPath + "/delete-slab" + "/" + pid,
				method: 'DELETE',
				success: function (status) {
					onDeleteSuccess(status);
				},
				error: function (xhr, error) {
					onError(xhr, error);
				}
			});
		}

		Slab.filterUsers = function () {

			$('#tBodyUser').html(
				"<tr><td colspan='10' align='center'>Please wait...</td></tr>");
			$
				.ajax({
					url: userContextPath + "/filter",
					method: 'GET',
					// data : {
					// 	companyPid : $('#dbCompany').val()
					// },
					success: function (users) {
						loadTableData(users)
					},
					error: function (xhr, error) {
						onError(xhr, error);
					}
				});
		}


		function loadTableData(users){
			$('#tBodyUser').html("");
			if (users.length == 0) {
				$('#tBodyUser')
					.html(
						"<tr><td colspan='10' align='center'>No data available</td></tr>");
				return;
			}
			$
				.each(
					users,
					function (index, user) {
						$('#tBodyUser')
							.append(
								"<tr  data-id='"
								+ user.companyPid
								+ "'data-parent=\"\"><td class='tableexport-string target'  colspan='4'>"
								+ user.legalName
								+ "</td></tr>");

						$('#tBodyUser')
							.append(
								"<tr data-parent='"
								+ user.companyPid
								+ "'><th>"
								+ "MinimumUser"
								+ "</th><th>"
								+ "MaximumUser"
								+ "</th><th>"
								+ "SlabRate"
								+ "</th><th>"
								+ "Actions"
								+ "</th></tr>");

						var editButton =


							$.each(user.slabDTos, function (index, slab) {

								$('#tBodyUser')
									.append(
										"<tr data-id='"
										+ slab.pid
										+ "1' data-parent='"
										+ slab.companyPid
										+ "' ><td>"
										+ slab.minimumUser
										+ "</td><td>"
										+ slab.maximumUser
										+ "</td><td>"
										+ slab.slabRate
										+ "</td><td class='action'>"
										+ "<button type='button' class='btn btn-red btndlt' onclick='Slab.deleteUser(\""
										+ slab.pid
										+ "\");'>Delete</button>"
										+ "</td></tr>");

							});

					});


			$('.collaptable')
				.aCollapTable(
					{
						startCollapsed: true,
						addColumn: false,
						plusButton: '<span><i class="entypo-down-open-mini"></i></span>',
						minusButton: '<span><i class="entypo-up-open-mini"></i></span>'
					});

		}

		function onSaveSuccess(result) {
			// reloading page to see the updated data
			window.location = userContextPath;
		}

		function onDeleteSuccess(result) {
			// reloading page to see the updated data
			window.location = userContextPath;
		}

		Slab.showModalPopup = function (el, id, action) {
			resetForm();
			if (id) {
				switch (action) {
					case 0:
						showUser(id);
						break;
					case 1:
						editUser(id);
						createEditForm.attr('method', 'PUT');
						break;
				}
			}
			el.modal('show');
		}

		Slab.closeModalPopup = function (el) {
			el.modal('hide');
		}

		function resetForm() {
			$('.alert').hide();
			createEditForm.trigger("reset"); // clear form fields
			createEditForm.validate().resetForm(); // clear validation messages
			createEditForm.attr('method', 'POST'); // set default method
			slabModel.pid = null; // reset user model;
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
							entityName: entityName
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
									fieldName: fieldName
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

		function convertDateTimeFromServer(date) {
			if (date) {
				return moment(date).format('MMMM Do YYYY, h:mm:ss a');
			} else {
				return null;
			}

		}
	})();