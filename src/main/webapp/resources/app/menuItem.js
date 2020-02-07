// Create a MenuItem object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.MenuItem) {
	this.MenuItem = {};
}

(function() {
	'use strict';

	var menuItemContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#menuItemForm");
	var deleteForm = $("#deleteForm");
	var menuItemModel = {
		id : null,
		label : null,
		link : null,
		description : null,
		parentId : null,
		parentLabel : null,
		iconClass : "",
		menuItemLabelView : null
	};

	// Specify the validation rules
	var validationRules = {
		label : {
			required : true,
			maxlength : 255
		},
		link : {
			maxlength : 150
		},
		parentId : {
			valueNotEquals : "-1"
		}
	};

	// Specify the validation error messages
	var validationMessages = {
		label : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 255 characters."
		},
		link : {
			maxlength : "This field cannot be longer than 150 characters."
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
				createUpdateMenuItem(form);
			}

		});
		deleteForm.submit(function(e) {
			// prevent Default functionality
			e.preventDefault();
			// pass the action-url of the form
			deleteMenuItem(e.currentTarget.action);
		});

		$(document).ready(function() {
			$(".dynamic_dropdown dt a span").addClass("flagvisibility");
			$(".dynamic_dropdown dt a").click(function() {
				$(".dynamic_dropdown dd ul").toggle();
			});
			$(".dynamic_dropdown dd ul li a").click(function() {
				var text = $(this).html();
				$("#formDT").html("");
				$("#formDT").html(text);
				$(".dynamic_dropdown dd ul").hide();
			});
			$(document).bind('click', function(e) {
				var $clicked = $(e.target);
				if (!$clicked.parents().hasClass("dynamic_dropdown"))
					$(".dynamic_dropdown dd ul").hide();
			});
		});
	});



	function createUpdateMenuItem(el) {
		menuItemModel.label = $('#field_label').val();
		menuItemModel.link = $('#field_link').val();
		menuItemModel.description = $('#field_description').val();
		menuItemModel.parentId = $('#field_parent').val();
		menuItemModel.iconClass = $('#formDT').text();
		menuItemModel.menuItemLabelView=$('#field_menuItemLabelView').val();
		console.log(menuItemModel.iconClass);
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(menuItemModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showMenuItem(id) {
		$.ajax({
			url : menuItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_label').text(data.label);
				$('#lbl_parent').text(data.parentLabel);
				$('#lbl_link').text(data.link);
				$('#lbl_description').text(data.description);
				$('#lbl_icon').html("<i class='" + data.iconClass + "'></i>");
				$('#lbl_menuItemLabelView').text(data.menuItemLabelView!=null ? data.menuItemLabelView:"-");
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editMenuItem(id) {
		$.ajax({
			url : menuItemContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#field_label').val(data.label);
				if (data.parentId == null) {
					$('#field_parent').val(-1);
				} else {
					$('#field_parent').val(data.parentId);
				}
				$('#field_link').val(data.link);
				$('#field_description').val(data.description);
				$('#field_menuItemLabelView').val(data.menuItemLabelView);
				if(data.iconClass==""){
					$("#formDT").html("<span class='entypo-users'>entypo-users</span>")
				}else{
				$("#formDT").html("<span class="+data.iconClass+">"+data.iconClass+"</span>");
				}
				// set id
				menuItemModel.id = data.id;
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function deleteMenuItem(actionurl, id) {
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
		window.location = menuItemContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = menuItemContextPath;
	}

	MenuItem.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showMenuItem(id);
				break;
			case 1:
				editMenuItem(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', menuItemContextPath + "/" + id);
				break;
			}
		}
		el.modal('show');
	}

	MenuItem.closeModalPopup = function(el) {
		el.modal('hide');
	}

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		menuItemModel.id = null; // reset menuItem model;
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