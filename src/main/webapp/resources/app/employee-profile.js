// Create a EmployeeProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.EmployeeProfile) {
	this.EmployeeProfile = {};
}

(function() {
	'use strict';

	var employeeProfileContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#employeeProfileForm");
	var deleteForm = $("#deleteForm");
	var employeeProfileModel = {
		pid : null,
		name : null,
		alias : null,
		address : null,
		orgEmpId:null,
		phone : null,
		email : null,
		designationPid : null,
		departmentPid : null,
		profileImage : null,
		profileImageContentType : null
		
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		designationPid : {
			valueNotEquals : "-1"
		},
		departmentPid : {
			valueNotEquals : "-1"
		},
		phone : {
			maxlength : 20
		},
		email : {
			maxlength : 100,
			email : true
		},
		address : {
			maxlength : 250
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		name : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		phone : {
			maxlength : "This field cannot be longer than 20 characters."
		},
		email : {
			maxlength : "This field cannot be longer than 100 characters."
		},
		address : {
			maxlength : "This field cannot be longer than 250 characters."
		}
	};

	$(document).ready(function() {

		// add the rule here
		$.validator.addMethod("valueNotEquals", function(value, element, arg) {
			return arg != value;
		}, "");

		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateEmployeeProfile(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteEmployeeProfile(e.currentTarget.action);
		});
		
		$('#btnActivateEmployeeProfile').on('click', function() {
			activateAssignedEmployeeProfile();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
		$('#btnDownload')
		.on(
				'click',
				function() {
					var tblEmployeeProfile = $("#tblEmployeeProfile tbody");
					if (tblEmployeeProfile
							.children().length == 0) {
						alert("no values available");
						return;
					}
					if (tblEmployeeProfile[0].textContent == "No data available") {
						alert("no values available");
						return;
					}

					downloadXls();
					$("#tblEmployeeProfile th:last-child, #tblEmployeeProfile td:last-child").show();
				});
	});
	

	function downloadXls() {
		//Avoid last column in each row
		$("#tblEmployeeProfile th:last-child, #tblEmployeeProfile td:last-child").hide();
		// When the stripped button is clicked, clone the existing source
		var clonedTable = $("#tblEmployeeProfile").clone();
		// Strip your empty characters from the cloned table (hidden didn't seem
		// to work since the cloned table isn't visible)
		clonedTable.find('[style*="display: none"]').remove();

		var excelName = "employeeProfiles";

		clonedTable.table2excel({
			// exclude CSS class
			// exclude : ".odd .even",
			// name : "Dynamic Document Form",
			filename : excelName, // do not include extension
		// fileext : ".xls",
		// exclude_img : true,
		// exclude_links : true,
		// exclude_inputs : true
		});
	}

	function activateAssignedEmployeeProfile() {
		$(".error-msg").html("");
		var selectedEmployeeProfile = "";

		$.each($("input[name='employeeprofile']:checked"), function() {
			selectedEmployeeProfile += $(this).val() + ",";
		});

		if (selectedEmployeeProfile == "") {
			$(".error-msg").html("Please select Employee Profile");
			return;
		}
		$.ajax({
			url : employeeProfileContextPath + "/activateEmployeeProfile",
			type : "POST",
			data : {
				employeeprofiles : selectedEmployeeProfile,
			},
			success : function(status) {
				$("#enableEmployeeProfileModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateEmployeeProfile(el) {
		employeeProfileModel.name = $('#field_name').val();
		employeeProfileModel.alias = $('#field_alias').val();
		employeeProfileModel.address = $('#field_address').val();
		employeeProfileModel.orgEmpId = $('#field_org_emp_id').val();
		employeeProfileModel.designationPid = $('#field_designation').val();
		employeeProfileModel.departmentPid = $('#field_department').val();
		employeeProfileModel.phone = $('#field_phone').val();
		employeeProfileModel.email = $('#field_email').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(employeeProfileModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	$('#profileImage').on(
			'change',
			function() {
				var file = $(this)[0].files[0]; // only one file exist
				var fileReader = new FileReader();
				fileReader.readAsDataURL(file);

				fileReader.onload = function(e) {
					$('#previewImage').attr('src', fileReader.result);
					var base64Data = e.target.result.substr(e.target.result
							.indexOf('base64,')
							+ 'base64,'.length);
					employeeProfileModel.profileImage = base64Data;
					employeeProfileModel.profileimageContentType = file.type;
				};

			});

	function showEmployeeProfile(id) {
		$('#profileImageView').attr('src', '');
		$.ajax({
			url : employeeProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_designation').text(data.designationName);
				$('#lbl_department').text(data.departmentName);
				$('#lbl_phone').text((data.phone == null ? "" : data.phone));
				$('#lbl_email').text((data.email == null ? "" : data.email));
				$('#lbl_address').text((data.address == null ? "" : data.address));
				$('#lbl_org_emp_id').text((data.orgEmpId == null ? "" : data.orgEmpId));
				
				if (data.profileImage != null) {
					$('#profileImageView').attr('src',
							'data:image/png;base64,' + data.profileImage);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editEmployeeProfile(id) {
		$('#previewImage').attr('src', '');
		$.ajax({
			url : employeeProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
                 console.log(data);
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_designation').val(data.designationPid);
				$('#field_department').val(data.departmentPid);
				$('#field_phone').val((data.phone == null ? "" : data.phone));
				$('#field_email').val((data.email == null ? "" : data.email));
				$('#field_address').val((data.address == null ? "" : data.address));
				$('#field_org_emp_id').val((data.orgEmpId == null ? "" : data.orgEmpId));

				
				if (data.profileImage != null) {
					$('#previewImage').attr('src',
							'data:image/png;base64,' + data.profileImage);
				}

				// set pid
				employeeProfileModel.pid = data.pid;
				// set image byte array
				employeeProfileModel.profileImage = data.profileImage;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteEmployeeProfile(actionurl, id) {
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

	EmployeeProfile.setActive=function(name,address,desPId,depPid,pid,active){
		employeeProfileModel.pid=pid;
		employeeProfileModel.activated=active;
		employeeProfileModel.name=name;
		employeeProfileModel.address=address;
		employeeProfileModel.departmentPid=depPid;
		employeeProfileModel.designationPid=desPId;
		console.log(employeeProfileModel);
		if(confirm("Are you confirm?")){
			$.ajax({
				url:employeeProfileContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(employeeProfileModel),
				success:function(data){
					onSaveSuccess(data);
				},
				error:function(xhr,error){
					onError(xhr, error);
				}
			});
		}
	}
	
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = employeeProfileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = employeeProfileContextPath;
	}

	EmployeeProfile.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showEmployeeProfile(id);
				break;
			case 1:
				editEmployeeProfile(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm
						.attr('action', employeeProfileContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	EmployeeProfile.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		employeeProfileModel.pid = null; // reset employeeProfile model;
		employeeProfileModel.profileImage = null;
		$('#previewImage').attr('src', '');
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