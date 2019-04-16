// Create a CompetitorProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.CompetitorProfile) {
	this.CompetitorProfile = {};
}

(function() {
	'use strict';

	var competitorProfileContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#competitorProfileForm");
	var deleteForm = $("#deleteForm");
	var competitorProfileModel = {
		pid : null,
		name : null,
		alias : null,
		description : null
		
	};

	// Specify the validation rules
	var validationRules = {
		name : {
			required : true,
			maxlength : 255
		},
		alias : {
			maxlength : 55
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
		}
	};

	$(document).ready(function() {
		createEditForm.validate({
			rules : validationRules,
			messages : validationMessages,
			submitHandler : function(form) {
				createUpdateCompetitorProfile(form);
			}
		});

		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteCompetitorProfile(e.currentTarget.action);
		});
		
		$('#btnActivateCompetitorProfile').on('click',function(){
			activateCompetitorProfile();
		});
		
		$('input:checkbox.allcheckbox').click(
				function() {
					$(this).closest('table').find(
							'tbody tr td input[type="checkbox"]').prop(
							'checked', $(this).prop('checked'));
				});
	});

	function activateCompetitorProfile() {
		$('.error-msg').html("");
		var selectedCompetitorProfile="";
		$.each($("input[name='competitorprofile']:checked"),function(){
			selectedCompetitorProfile += $(this).val()+",";
		});
		
		
		if(selectedCompetitorProfile==""){
			$('.error-msg').html("Please select Competitor Profile");
			return;
		}
		
		$.ajax({
			url:competitorProfileContextPath+"/activateCompetitorProfile",
			method:'POST',
			data:{
				competitorprofile:selectedCompetitorProfile,
			},
			success:function(data){
				$('#enableCompetitorProfileModal').modal('hide');
				onSaveSuccess(data);
			},
			error:function(xhr,error){
				onError(xhr, error);
			},
		});
	}
	
	function createUpdateCompetitorProfile(el) {
		competitorProfileModel.name = $('#field_name').val();
		competitorProfileModel.alias = $('#field_alias').val();
		competitorProfileModel.description = $('#field_description').val();

		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(competitorProfileModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showCompetitorProfile(id) {
		$.ajax({
			url : competitorProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text((data.alias == null ? "" : data.alias));
				$('#lbl_description').text((data.description == null ? "" : data.description));
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editCompetitorProfile(id) {
		$.ajax({
			url : competitorProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_name').val(data.name);
				$('#field_alias').val((data.alias == null ? "" : data.alias));
				$('#field_description').val((data.description == null ? "" : data.description));
				// set pid
				competitorProfileModel.pid = data.pid;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteCompetitorProfile(actionurl, id) {
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

	CompetitorProfile.setActive=function(name,pid,active){
		competitorProfileModel.pid=pid;
		competitorProfileModel.activated=active;
		competitorProfileModel.name=name;
		if(confirm("Are you confirm?")){
			$.ajax({
				url:competitorProfileContextPath+"/changeStatus",
				method:'POST',
				contentType:"application/json; charset:utf-8",
				data:JSON.stringify(competitorProfileModel),
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
		window.location = competitorProfileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = competitorProfileContextPath;
	}

	CompetitorProfile.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showCompetitorProfile(id);
				break;
			case 1:
				editCompetitorProfile(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', competitorProfileContextPath + "/"
						+ id);
				break;
			}
		}
		el.modal('show');
	}

	CompetitorProfile.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		competitorProfileModel.pid = null; // reset competitorProfile model;
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