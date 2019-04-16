// Create a CopyCompany object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	
	var copyCompanyDataModel = {
			fromCompanyId : null,
			toCompanyId : null,
			fromSchema : null,
			toSchema : null,
			tblNames : null
	};

	$(document).ready(function() {
		//numeric only
		$('#field_to_company').keyup(function () { 
		    this.value = this.value.replace(/[^0-9\.]/g,'');
		});
		$('#btnCMD').click(function() {
			if(validateInputData()){
				copyMasterData();
			}
		});
		//select all
		$('#cbMasterDataAll').click(function() {
			var checked = $(this).prop('checked');
			$('#divCheckboxInline').find(':checkbox').each(function() {
		        $(this).prop('checked',checked);
		    });
		});
	});
	
	function validateInputData() {
		var fromSchema = $('#field_from_schema').val();
		var toSchema = $('#field_to_schema').val();
		var fromCompany = $('#dbCompanyFrom').val();
		var toCompany = $('#field_to_company').val(); 
		if(!fromSchema.trim().length > 0) {
			alert("Enter From schema name");
			return false;
		}
		if(!toSchema.trim().length > 0) {
			alert("Enter To schema name");
			return false;
		}
		if(fromCompany == "-1") {
			alert("Select From company");
			return false;
		}
		if(!toCompany.trim().length > 0) {
			alert("Eneter To company ID.");
			return false;
		}
		copyCompanyDataModel.fromCompanyId = fromCompany;
		copyCompanyDataModel.toCompanyId = toCompany;
		copyCompanyDataModel.fromSchema = fromSchema;
		copyCompanyDataModel.toSchema = toSchema;
		copyCompanyDataModel.tblNames = $("#divCheckboxInline input:checkbox:checked").map(function() {
			return $(this).val();
		}).get();
		return true;
	}
	
	function copyMasterData() {
		$('.alert').hide();
		$.ajax({
			url : contextPath + "/web/siteadmin/copy-compnay-data",
			method : 'POST',
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(copyCompanyDataModel),
			success : function(data) {
				console.log("=== Success ===");
			},
			error : function(xhr, error) {
				addErrorAlert("<b>Copying Failed - </b>" + xhr.responseText);
			}
		});
	}
	
	function addErrorAlert(message) {
		$(".alert > p").html(message);
		$('.alert').show();
	}
	
})();