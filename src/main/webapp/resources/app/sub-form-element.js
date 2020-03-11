// Create a FormElement object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.SubFormElement) {
	this.SubFormElement = {};
}
(function() {
	'use strict';
	var subFormElementContextPath = location.protocol + '//' + location.host
			+ location.pathname;

	$(document)
			.ready(
					function() {

						$('#btnApply').on('click', function() {

							loadAllFormElementValues();

						});

						$("#dbDocuments").change(function() {
							loadForms();
						});
						
						$("#dbFormElementTypes").change(function() {
							loadFormElements();
						});

						$("#dbForms").change(function() {
							loadFormElements();
						});

						// table search
						$('#btnSearch').click(function() {
							searchTable($("#search").val());
						});

						$('#btnSaveFormElements').on('click', function() {
							saveAssignedFormElements();
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

	function searchTable(inputVal) {

		var table = $('#tBodyFormElement');
		var filterBy = $("input[name='filter']:checked").val();
		table.find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 0) {
						if (filterBy != "all") {
							var val = $(td).find('input').prop('checked');
							if (filterBy == "selected") {
								if (!val) {
									return false;
								}
							} else if (filterBy == "unselected") {
								if (val) {
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

	var valueId = 0;
	function loadForms() {
		var docPid = $('#dbDocuments').val();
		$.ajax({
			url : subFormElementContextPath + "/load-forms",
			type : 'GET',
			data : {
				documentPid : docPid
			},
			success : function(documentForms){
				console.log("Form size by document :"+documentForms.length);
				if(documentForms.length == 0){
					alert("No forms assigned to this document");
					$("#tBodyFormElement").html("");
					$("#tblFormElementValues").html("");
					return;
				}
				$("#dbForms").html("<option value='-1'>--Select--</option>");
				$.each(documentForms,function(key,docForm){
					$('#dbForms').append("<option value="+docForm.formPid+">"+docForm.formName+"</option>");
				});
			}
			
		});
	}
	function loadFormElements() {
		$(".error-msg").html("");

		if ($('#dbForms').val() == "-1") {
			$("#dbFormElements").html("<option>--Select--</option>");
			alert("Please Select Forms");
			$("#tblFormElementValues").html("");
			return;
		}
		var formElementType = $('#dbFormElementTypes').val();
		var form = $('#dbForms').val();
		$("#tBodyFormElement").append("");
		$("#tBodyFormElement").html("");
		$("#dbFormElements").html("<option>Form Elements loading...</option>")
		$.ajax({
			url : subFormElementContextPath + "/load-form-elements",
			type : 'GET',
			data : {
				formElementTypeId : formElementType,
				formPid : form
			},
			success : function(formElements) {
				$("#dbFormElements").html(
						"<option value='-1'>--Select--</option>");
				$.each(formElements, function(key, formElement) {
					$("#dbFormElements").append(
							"<option value='" + formElement.pid + "'>"
									+ formElement.name + "</option>");
				});
			}
		});
	}
	
	function loadFormElementsInModal() {
		$(".error-msg").html("");

		if ($('#dbForms').val() == "-1") {
			$("#dbFormElements").html("<option>--Select--</option>");
			alert("Please Select Forms");
			$("#tBodyFormElement").html("");
			return;
		}
		var formElementType = $('#dbFormElementTypes').val();
		var form = $('#dbForms').val();
		$("#tBodyFormElement").append("");
		$("#tBodyFormElement").html("");
		/*$("#dbFormElements").html("<option>Form Elements loading...</option>")*/
		$.ajax({
			url : subFormElementContextPath + "/load-form-elements-by-form",
			type : 'GET',
			data : {
				formPid : form
			},
			success : function(formElements) {
				/*$("#dbFormElements").html(
						"<option value='-1'>--Select--</option>");*/
				$.each(formElements, function(key, formElement) {

					$("#tBodyFormElement").append(
							"<tr><td><input name='formElement' type='checkbox' value='"
									+ formElement.pid
									+ "' style='display: block;' /></td><td>"
									+ formElement.name + "</td><td>"
									+ formElement.formElementTypeName
									+ "</td></tr>");
				});
			}
		});
	}

	function loadAllFormElementValues() {

		
		if ($('#dbFormElements').val() == "-1") {
			alert("Please Select Form Elements");
			$("#tblFormElementValues").html("");
			return;
		}
		$("#spFormElements").hide();
		$("#tblFormElementValues").html("<tr><td>Loading...</td></tr>")
		$
				.ajax({
					url : subFormElementContextPath
							+ "/load-all-form-element-values",
					type : 'GET',
					data : {
						formElementPid : $('#dbFormElements').val()
					},
					success : function(formElementValues) {

						$("#tblFormElementValues").html("")

						$
								.each(
										formElementValues,
										function(key, formElementValue) {
											$("#tblFormElementValues")
													.append(
															"<tr><td>"
																	+ formElementValue.name
																	+ "</td><td><button type='button' class='btn btn-info' onclick='SubFormElement.assignSubFormElements(\""
																	+ formElementValue.id
																	+ "\");'>Assign Sub Form Elements</button></td></tr>");
										});

						$
					}
				});
	}

	SubFormElement.assignSubFormElements = function(id) {
		loadFormElementsInModal();
		setTimeout(function (){
			
			
			console.log("selected");
			valueId = id;
			var documentPid = $("#dbDocuments").val();
			var formPid = $('#dbForms').val();
			var formElementPid = $('#dbFormElements').val();
			$("input[name='filter'][value='all']").prop("checked", true);
			$("#search").val("");
			searchTable("");

			// clear all check box
			$("#divFormElements input:checkbox").attr('checked', false);

			$(".error-msg").html("");

			$.ajax({
				url : subFormElementContextPath + "/formElements",
				type : "GET",
				data : {
					formElementValueId : valueId,
					documentPid : documentPid,
					formPid : formPid,
					formElementPid : formElementPid
				},
				success : function(assignedFormElements) {
					if (assignedFormElements) {
						$.each(assignedFormElements, function(index, formElement) {
							$(
									"#divFormElements input:checkbox[value="
											+ formElement.pid + "]").prop(
									"checked", true);
						});
					}
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});

			$("#subFormElementModal").modal('show');
			
			
			
		}, 200);
		
	}

	function saveAssignedFormElements() {
		$(".error-msg").html("");
		$("#spFormElements").show();
		var selectedFormElements = "";
		var documentPid = $("#dbDocuments").val();
		var formPid = $('#dbForms').val();
		var formElementPid = $('#dbFormElements').val();
			
		$.each($("input[name='formElement']:checked"), function() {
			selectedFormElements += $(this).val() + ",";
		});

		console.log(selectedFormElements);

		$(".error-msg").html("Please wait.....");
		$.ajax({
			url : subFormElementContextPath + "/assign-form-elements",
			type : "POST",
			data : {
				formElementValueId : valueId,
				assignedFormElements : selectedFormElements,
				documentPid : documentPid,
				formPid : formPid,
				formElementPid : formElementPid
				
			},
			success : function(status) {
				$("#subFormElementModal").modal('hide');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

})();