// Create a ProductProfile object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ProductProfile) {
	this.ProductProfile = {};
}

(function() {
	'use strict';

	var productProfileContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	var createEditForm = $("#productProfileForm");
	var deleteForm = $("#deleteForm");
	var propid="";
	var productProfileModel = {
		pid : null,
		name : null,
		alias : null,
		productCategoryPid : null,
		divisionPid : null,
		sku : null,
		unitQty : null,
		taxRate : null,
		price : null,
		mrp : null,
		size : 0,
		colorImage : null,
		colorImageContentType : null,
		description : null,
		hsnCode : null
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
		productCategoryPid : {
			valueNotEquals : "-1"
		},
		divisionPid : {
			valueNotEquals : "-1"
		},
		sku : {
			maxlength : 20
		},
		unitQty : {
			maxlength : 20
		},
		price : {
			required : true,
			maxlength : 10
		},
		mrp : {
			required : true,
			maxlength : 10
		},
		taxRate : {
			required : true,
			maxlength : 4
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
		sku : {
			maxlength : "This field cannot be longer than 20 characters."
		},
		unitQty : {
			maxlength : "This field cannot be longer than 20 characters."
		},
		price : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 10 characters."
		},
		mrp : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 10 characters."
		},
		taxRate : {
			required : "This field is required.",
			maxlength : "This field cannot be longer than 4 characters."
		}
	};

	$(document)
			.ready(
					function() {
						// filter by
						$('#btnSearch').click(
								function() {
									searchTable($("#search").val(),
											$('#tBodyProductProfile'));
								});

						// validate color image
						$('#field_colorImage')
								.bind(
										'change',
										function() {
											console
													.log("field_colorImage................");
											var size = this.files[0].size;
											// in bytes
											if (size > 5120) {
												$('#field_colorImage').val('')
												addErrorAlert('color image size exeeded max limit of 5 KB');
												return;
											}
											$('.alert').hide();

											// image preview and set bytes and
											// contentType
											var file = $(this)[0].files[0];
											var fileReader = new FileReader();
											fileReader.readAsDataURL(file);

											fileReader.onload = function(e) {
												$('#previewColorImage').attr(
														'src',
														fileReader.result);
												var base64Data = e.target.result
														.substr(e.target.result
																.indexOf('base64,')
																+ 'base64,'.length);
												productProfileModel.colorImage = base64Data;
												productProfileModel.colorImageContentType = file.type;
											};

										});

						$('#imgProduct')
								.bind(
										'change',
										function() {
											// image preview and set bytes and
											// contentType
											var file = $(this)[0].files[0];
											var fileReader = new FileReader();
											fileReader.readAsDataURL(file);

											fileReader.onload = function(e) {
												$('#previewProductImage').attr(
														'src',
														fileReader.result);
												var base64Data = e.target.result
														.substr(e.target.result
																.indexOf('base64,')
																+ 'base64,'.length);
												productProfileModel.colorImage = base64Data;
												productProfileModel.colorImageContentType = file.type;
											};
										});
										
										// validate image
						$('#field_image').bind(
								'change',
								function() {
										
									var val = $(this).val().toLowerCase();
										var regex = new RegExp("(.*?)\.(docx|doc|pdf|xml|bmp|ppt|xls)$");
										 if((regex.test(val))) {
										$(this).val('');
										alert('Please select image only...!');
										return;
										}  
									
									var size = this.files[0].size;
									var file = $(this)[0].files[0];
									var fileReader = new FileReader();
									fileReader.readAsDataURL(file);

									fileReader.onload = function(e) {
										$('#previewImage').attr('src', fileReader.result);
										var base64Data = e.target.result
												.substr(e.target.result
														.indexOf('base64,')
														+ 'base64,'.length);
										
									};
									changeSpan();

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
								createUpdateProductProfile(form);
							}
						});

						deleteForm.submit(function(e) {
							// prevent Default functionality
							e.preventDefault();
							// pass the action-url of the form
							deleteProductProfile(e.currentTarget.action);
						});

						// load products
						loadAllProductProfiles();

						$('#btnSaveSize').click(function() {
							saveSize();
						});

						$('#btnActivateProductProfiles').on('click',
								function() {
									activateAssignedProductProfiles();
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
									var tblProductProfile = $("#tblProductProfile tbody");
									if (tblProductProfile
											.children().length == 0) {
										alert("no values available");
										return;
									}
									if (tblProductProfile[0].textContent == "No data available") {
										alert("no values available");
										return;
									}

									downloadXls();
									$("#tblProductProfile th:last-child, #tblProductProfile td:last-child").show();
								});
						
						$('#btnSaveTax').on('click', function() {
							saveAssignedTax();
						});
						$('#btnSaveFilledFormImage').click(function() {
							uploadFilledFormImage();
						});
						$('#btnRemoveImage').click(function() {
						var result = confirm("are you sure?");
							if (result) {
   							 removeImage();
								}
							
						});
						$('#btnRefresh').click(function() {
							clearImageShownDiv();
						});
						
						$('#slt_status').on('change', function() {
							findByfilfter();
						});
					});
	
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
			ProductProfile.showModalPopup($('#enableProductProfilesModal'));
			return;
		}
		$.ajax({
			url : productProfileContextPath + "/get-by-status-filter",
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
	
	function saveAssignedTax() {

		var selectedTaxMasters = "";
		var prodPrfilePid=productProfileModel.pid;
		
		$.each($("input[name='taxMaster']:checked"), function() {
			selectedTaxMasters += $(this).val() + ",";
		});

		if (selectedTaxMasters == "") {
			$(".error-msg").html("Please select tax Master");
			return;
		}
		$(".error-msg").html("Please wait...");

		if(selectedTaxMasters != ""){
			
			$.ajax({
				url : productProfileContextPath + "/assign-tax-master/"+prodPrfilePid,
				type : "POST",
				data : {
					selectedTax : selectedTaxMasters
				},
				success : function(status) {
					$("#assignTaxModal").modal("hide");
					onSaveSuccess(status);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				},
			});
		}
	
	}

	function downloadXls() {
		
		var status = $('#slt_status').val();
		console.log(status);
		window.location.href = productProfileContextPath
		+ "/download-profile-xls?status="+ status;
		
		
//		// Avoid last column in each row
//		$("#tblProductProfile th:last-child, #tblProductProfile td:last-child").hide();
//		var excelName = "productProfile";
//		 var table2excel = new Table2Excel();
//		     table2excel.export(document.getElementById('tblProductProfile'),excelName);
//		     $("#tblProductProfile th:last-child, #tblProductProfile td:last-child").show();
	}
	
	ProductProfile.checkboxChange = function(e) {
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

	function activateAssignedProductProfiles() {
		$(".error-msg").html("");
		var selectedProductProfiles = "";

		$.each($("input[name='productprofile']:checked"), function() {
			selectedProductProfiles += $(this).val() + ",";
		});

		if (selectedProductProfiles == "") {
			$(".error-msg").html("Please select Product Profiles");
			return;
		}
		$.ajax({
			url : productProfileContextPath + "/activateProductProfiles",
			type : "POST",
			data : {
				productprofiles : selectedProductProfiles,
			},
			success : function(status) {
				$("#enableProductProfilesModal").modal("hide");
				onSaveSuccess(status);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			},
		});
	}

	// pop up product table search by name
	ProductProfile.productNameSearch = function(e) {
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

	// pop up product table search by size
	ProductProfile.productSizeSearch = function(e) {
		var inputVal = e.value;
		$('#tBodyFieldProducts').find('tr').each(function(index, row) {
			var allCells = $(row).find('td');
			if (allCells.length > 0) {
				var found = false;
				allCells.each(function(index, td) {
					if (index == 2) {
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

	function saveSize() {
		var size = $('#txtSize').val();
		var selectedProducts = "";
		$(".error-msg").html("");
		$('#field_products').find('input[type="checkbox"]:checked').each(
				function() {
					selectedProducts += $(this).val() + ",";
				});
		if (selectedProducts == "") {
			$(".error-msg").html("Please select Account Types");
			return;
		}
		$(".error-msg").html("Please wait.....");
		// remove last character and split to array
		var ar = (selectedProducts.substring(0, selectedProducts.length - 1))
				.split(",");
		var productSize = {
			size : size,
			products : ar,
		}
		$.ajax({
			method : 'POST',
			url : productProfileContextPath + "/set-size",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(productSize),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function createUpdateProductProfile(el) {
		productProfileModel.name = $('#field_name').val();
		productProfileModel.alias = $('#field_alias').val();
		productProfileModel.productCategoryPid = $('#field_category').val();
		productProfileModel.divisionPid = $('#field_division').val();
		productProfileModel.sku = $('#field_sku').val();
		productProfileModel.unitQty = $('#field_unitQty').val();
		productProfileModel.price = $('#field_price').val();
		productProfileModel.mrp = $('#field_mrp').val();
		productProfileModel.description = $('#field_description').val();
		productProfileModel.taxRate = $('#field_taxRate').val();
		productProfileModel.size = $('#field_size').val();
		productProfileModel.hsnCode = $('#field_hsnCode').val();
		console.log(productProfileModel);
		$.ajax({
			method : $(el).attr('method'),
			url : $(el).attr('action'),
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(productProfileModel),
			success : function(data) {
				if($(el).attr('method') == 'PUT'){
					$("#myModal").modal("hide");
					ProductProfile.filterByCategoryAndGroup();
				} else {
					onSaveSuccess(data);
				}
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function showProductProfile(id) {
		$.ajax({
			url : productProfileContextPath + "/" + id,
			method : 'GET',
			success : function(data) {
				$('#lbl_name').text(data.name);
				$('#lbl_alias').text(data.alias);
				$('#lbl_category').text(data.productCategoryName);
				$('#lbl_division').text(data.divisionName);
				$('#lbl_unitQty').text(data.unitQty);
				$('#lbl_sku').text(data.sku);
				$('#lbl_price').text(data.price);
				$('#lbl_mrp').text(data.mrp);
				$('#lbl_taxRate').text(data.taxRate);
				$('#lbl_size').text(data.size);
				$('#lbl_hsnCode').text(data.hsnCode);
				$('#lbl_description').text(data.description);
				$('#lbl_colorImage').html(
						'<img src="data:image/png;base64,' + data.colorImage
								+ '"/>');
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}

	function editProductProfile(id) {
		$
				.ajax({
					url : productProfileContextPath + "/" + id,
					method : 'GET',
					success : function(data) {
						$('#field_name').val(data.name);
						$('#field_alias').val(data.alias);
						$('#field_category').val(data.productCategoryPid);
						$('#field_division').val(data.divisionPid);
						$('#field_sku').val(data.sku);
						$('#field_unitQty').val(data.unitQty);
						$('#field_price').val(data.price);
						$('#field_mrp').val(data.mrp);
						$('#field_description').val(data.description);
						$('#field_taxRate').val(data.taxRate);
						$('#field_size').val(data.size);
						$('#field_hsnCode').val(data.hsnCode);
						// set pid
						productProfileModel.pid = data.pid;

						// set image byte array
						productProfileModel.colorImage = data.colorImage;
						productProfileModel.colorImageContentType = data.colorImageContentType;
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function deleteProductProfile(actionurl, id) {
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

	function uploadImage(pid) {
		getFilledFormImages(pid);
		propid=pid;	
	}

	function showProductProfileImages(pid) {
		$('#divProductProfileImages')
				.html(
						'<img src="/resources/assets/images/ajax-loader.gif" style="display: block; margin: auto;">');
		$
				.ajax({
					url : productProfileContextPath + "/images/" + pid,
					method : 'GET',
					success : function(imageFiles) {
						$('#divProductProfileImages').html("");
						$
								.each(
										imageFiles,
										function(index, image) {
											var table = '<table class="table  table-striped table-bordered" id="tbl'
													+ image.filePid + '">';
											table += '<tr><th>'
													+ image.fileName
													+ '&nbsp;&nbsp;<button style="float: right;" type="button" class="btn btn-danger" onclick="ProductProfile.deleteImage(\''
													+ pid
													+ '\',\''
													+ image.filePid
													+ '\');">Delete</button></th></tr>';
											table += '<tr><td><img src="data:image/png;base64,'
													+ image.content
													+ '"/></td></tr>';
											table += '</table>';
											$('#divProductProfileImages')
													.append(table);
										});
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	ProductProfile.deleteImage = function(pid, filePid) {
		$.ajax({
			url : productProfileContextPath + "/delete-image/" + pid + "/"
					+ filePid,
			method : 'DELETE',
			success : function(response) {
				if (response) {
					$("#tbl" + filePid).remove();
				}
			}
		});
	}

	ProductProfile.filterByCategoryAndGroup = function() {
		var categoryPids = [];
		var groupPids = [];
		$("#pCategory").find('input[type="checkbox"]:checked').each(function() {
			categoryPids.push($(this).val());
		});
		$("#pGroup").find('input[type="checkbox"]:checked').each(function() {
			groupPids.push($(this).val());
		});
		$('#tBodyProductProfile').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : productProfileContextPath + "/filterByCategoryGroup",
					method : 'GET',
					data : {
						categoryPids : categoryPids.join(","),
						groupPids : groupPids.join(",")
					},

					success : function(productProfiles) {

						addTableBodyvalues(productProfiles);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});
	}

	function spanActivated(name, price, activated, productPid) {
		var spanActivated = "";
		var product = "'" + productPid + "'";
		var name = "'" + name + "'";
		var price = "'" + price + "'";
		if (activated) {
			spanActivated = '<span class="label label-success" onclick="ProductProfile.setActive('
					+ name
					+ ', '
					+ price
					+ ', '
					+ product
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Activated</span>';
		} else {
			spanActivated = '<span class="label label-danger" onclick="ProductProfile.setActive('
					+ name
					+ ', '
					+ price
					+ ', '
					+ product
					+ ', '
					+ !activated
					+ ')" style="cursor: pointer;">Deactivated</span>';
		}
		return spanActivated;
	}

	ProductProfile.setActive = function(name, price, pid, activated) {
		productProfileModel.pid = pid;
		productProfileModel.activated = activated;
		productProfileModel.name = name;
		productProfileModel.price = price;
		if (confirm("Are you ready to confirm?")) {
			$.ajax({
				url : productProfileContextPath + "/change",
				method : 'POST',
				contentType : "application/json ; charset:utf-8",
				data : JSON.stringify(productProfileModel),
				success : function(data) {
					onSaveSuccess(data);
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}
	}
	function loadAllProductProfiles() {
		$('#tBodyProductProfile').html(
				"<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : productProfileContextPath + "/filterByCategoryGroup",
					method : 'GET',
					data : {
						categoryPids : "",
						groupPids : ""
					},
					success : function(productProfiles) {
						addTableBodyvalues(productProfiles);
					},
					error : function(xhr, error) {
						onError(xhr, error);
					}
				});

	}
	
	
	function addTableBodyvalues(productProfiles){
		$('#tBodyProductProfile').html("");
		// select box for set size
		$('#field_products').html("");
		if (productProfiles.length == 0) {
			$('#tBodyProductProfile')
					.html(
							"<tr><td colspan='9' align='center'>No data available</td></tr>");
			return;
		}
		$('#field_products')
				.append(
						'<thead><tr><th>Select <br /><input type="checkbox" onchange="ProductProfile.checkboxChange(this);"  /></th><th>Name<input type="text" onkeyup="ProductProfile.productNameSearch(this);" id="productNameSearch" placeholder="search" class="form-control" style="width: 200px;"></th><th>Size<input type="text" onkeyup="ProductProfile.productSizeSearch(this);" placeholder="search" class="form-control" style="width: 100px;"></th></tr></thead><tbody id="tBodyFieldProducts">');
		$
				.each(
						productProfiles,
						function(index, productProfile) {
							$('#tBodyProductProfile')
									.append(
											"<tr><td title='click to view' class='sa'><span style='color: #2C7BD0; cursor: pointer;' onclick='ProductProfile.showModalPopup($(\"#viewModal\"),\""
													+ productProfile.pid
													+ "\",0);'>"
													+ productProfile.name
													+ "</td><td>"
													+ productProfile.productCategoryName
													+ "</td><td>"
													+ productProfile.divisionName
													+ "</td><td>"
													+  (productProfile.unitQty == null ? "" : productProfile.unitQty)
													+ "</td><td>"
													+ productProfile.sku
													+ "</td><td>"
													+ productProfile.price
													+ "</td><td>"
													+  (productProfile.alias == null ? "" : productProfile.alias)
													+ "</td> <td>"
													+ spanActivated(
															productProfile.name,
															productProfile.price,
															productProfile.activated,
															productProfile.pid)
													+ "</td><td><i class='btn btn-blue entypo-pencil' title='Edit Product Profile' onclick='ProductProfile.showModalPopup($(\"#myModal\"),\""
													+ productProfile.pid
													+ "\",1);'></i>&nbsp;<i class='btn btn-danger entypo-trash' title='Delete' onclick='ProductProfile.showModalPopup($(\"#deleteModal\"),\""
													+ productProfile.pid
													+ "\",2);'></i>&nbsp;<i class='btn btn-orange entypo-upload' title='View/Update Images' onclick='ProductProfile.showModalPopup($(\"#oploadImage\"),\""
													+ productProfile.pid
													+ "\",3);'></i>&nbsp;<div class='btn-group'> <span class=' btn btn-info dropdown-toggle entypo-dot-3' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false' title='more options'></span> <div class='dropdown-menu dropdown-menu-right' style='background-color: #F0F0F0'> <div><a class='btn btn-default dropdown-item' style='width: 100%; text-align: left;' onclick='ProductProfile.showModalPopup($(\"#productGroupModal\"),\""
													+ productProfile.pid
													+ "\",5);'>Assigned ProductGroups</a></div> <div><a class='btn btn-default dropdown-item' style='width: 100%; text-align: left;' onclick='ProductProfile.showModalPopup($(\"#assignTaxModal\"),\""
													+ productProfile.pid
													+ "\",6);'>Assigned Tax</a></div> </div></td></tr>");
							$('#field_products')
									.append(
											'<tr><td><input type="checkbox" value="'
													+ productProfile.pid
													+ '" /></td>'
													+ '<td>'
													+ productProfile.name
													+ '</td>'
													+ '<td>'
													+ productProfile.size
													+ '</td>'
													+ '</tr></tbody>');
						});	
	}
	

	function showProductProfileProductGroup(pid) {
		$('#tblProductGroups').html("");
		$.ajax({
			url : productProfileContextPath + "/productGroups/" + pid,
			method : 'GET',
			success : function(response) {
				$.each(response, function(index, productGroup) {
					$('#tblProductGroups').append('<tr><td>'+ productGroup.name +'</td><td>'+ productGroup.alias +'</td><td>'+ productGroup.description +'</td></tr>');
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function uploadFilledFormImage() {
	
	var ImgFilePid= $('#previewImage').attr("alt");
// var formPid=$("#dbForm").val();
// 		
// if(formPid==null || formPid=="no"){
// alert ("please select Form");
// return;
// }
	var image=$('#field_image')[0].files[0];
	if(image == null && ImgFilePid ==""){
		alert ("please select image");
		return;
	}
	
	if(image == null && ImgFilePid !=""){
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		changeSpan();
		return;
	}
	
	if(image != null && ImgFilePid !=""){
		uploadEditedImageFile(image,ImgFilePid);
	}
	else{
		uploadNewImageFile(image);
	}
	}
	
	function uploadEditedImageFile(image,ImgFilePid){
		$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var uploadImage = new FormData();
		uploadImage.append("file", image);
		uploadImage.append('productPid', propid);
		uploadImage.append('ImgFilePid', ImgFilePid);
		
			$.ajax({
				type : 'POST',
				url : productProfileContextPath + "/upload-edit-image",
				data :uploadImage,
				cache : false,
				contentType : false,
				processData : false,
				success : function(data) {
					$("#divImgeUpload").html("");
					$.each(data, function(key, filePid) {
						$("#"+filePid).remove();
						$("#div"+filePid).html("");
						var imgesrc=$("#previewImage").attr('src');
						$("#showImages").append('<div style="width: 250px;" id="div'+filePid+'"; class="col-md-4 zoom"><img title='+image.name+' onclick="ProductProfile.editImage(\''+ filePid +'\');" style="width: 200px; cursor: pointer;" id="'+filePid+'"; class="img-thumbnail" src="' + imgesrc + '" /></div><br/>');
						$('#previewImage').attr('src','');
						$('#previewImage').attr('alt','');
						$("#div"+ImgFilePid).attr('class','');
						$("#div"+ImgFilePid).remove(); 
						$('#field_image').val('');
						changeSpan();
						});
				},
				error : function(xhr, error) {
					$("#divImgeUpload").html("");
					onError(xhr, error);
				}
			});
		
	}
	
function uploadNewImageFile(image){
	$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var uploadImage = new FormData();
		uploadImage.append("file", image);
		uploadImage.append('productPid', propid);
		$.ajax({
			type : 'POST',
			url : productProfileContextPath + "/upload-image",
			data :uploadImage,
			cache : false,
			contentType : false,
			processData : false,
			success : function(data) {
				$("#divImgeUpload").html("");
				$.each(data, function(key, filePid) {
				var imgesrc=$("#previewImage").attr('src');
				$("#showImages").append('<div style="width: 250px;" id="div'+filePid+'"; class="col-md-4 zoom"><img title='+image.name+' onclick="ProductProfile.editImage(\''+ filePid +'\');" style="width: 200px; cursor: pointer;" id="'+filePid+'"; class="img-thumbnail" src="' + imgesrc + '"/></div><br/>');
				$('#previewImage').attr('src','');
				$('#previewImage').attr('alt','');
				$('#field_image').val('');
				changeSpan();
				});
			},
			error : function(xhr, error) {
				$("#divImgeUpload").html("");
				onError(xhr, error);
			}
		});
		
	}
	
	function getFilledFormImages(pid){
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		changeSpan();
		$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var formPid=$("#dbForm").val();
		$.ajax({
			url : productProfileContextPath + "/images/" + pid,
			type : 'GET',
			success : function(forms) {
				$("#showImages").html('');
				if(forms!=null){
					$.each(forms, function(key, form) {
						$("#showImages").append('<div style="width: 250px;" id="div'+form.filePid+'"; class="col-md-4 zoom"><img title='+form.fileName+' onclick="ProductProfile.editImage(\''+ form.filePid +'\');" style="width: 200px; cursor: pointer;" id="'+form.filePid+'"; class="img-thumbnail" src="data:image/png;base64,' + form.content + '"/></div><br/>');
					});
				}
				$("#divImgeUpload").html("");
			},
			error : function(xhr, error) {
				$("#divImgeUpload").html("");
				onError(xhr, error);
			}
		});
	}
	
	ProductProfile.editImage=function(filePid){
		var imgesrc=$("#"+filePid).attr('src');
		$('#previewImage').attr('src',imgesrc);
		$('#previewImage').attr('alt',filePid);
		changeSpan();
	}
	
	function removeImage(){
		$("#divImgeUpload").html('<img src="/resources/assets/images/content-ajax-loader.gif" style="display: block; margin: auto;">');
		var imagePid =$("#previewImage").attr("alt");
		var image=$('#field_image')[0].files[0];
		var productPid=propid;
		if(imagePid=="" && image==null){
				alert("please select image");
				$("#divImgeUpload").html("");
				return;
		}
		if(imagePid=="" && image!=null){
			$('#previewImage').attr('src','');
			$('#previewImage').attr('alt','');
			$("#divImgeUpload").html("");
			$('#field_image').val('');
			changeSpan();
			return;
		}
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		$("#"+imagePid).remove(); 
		$("#div"+imagePid).attr('class','');
		$("#div"+imagePid).remove(); 
		$("#divImgeUpload").html("");
		
		changeSpan();
			$.ajax({
			url : productProfileContextPath + "/delete-image/" + productPid + "/"
					+ imagePid,
			method : 'DELETE',
			success : function(response) {
				if (response) {
					$("#divImgeUpload").html("");
				}
			},
				error : function(xhr, error) {
					$("#divImgeUpload").html("");
					onError(xhr, error);
				}
		});
	}
	
	function clearImageShownDiv(){
		
		$('#previewImage').attr('src','');
		$('#previewImage').attr('alt','');
		$("#divImgeUpload").html("");
		$("#btnSltImg").text("New Image");
		$("#btnRefresh").fadeOut("slow");
		$('#field_image').val('');
	}

	function changeSpan(){
		if($("#previewImage").attr('src')!=''){
			$("#btnSltImg").text("Change Image");
			$("#btnRefresh").fadeIn("slow");
		}else{
			$("#btnSltImg").text("New Image");
			$("#btnRefresh").fadeOut("slow");
		}
	}
	function onSaveSuccess(result) {
		// reloading page to see the updated data
		window.location = productProfileContextPath;
	}

	function onDeleteSuccess(result) {
		// reloading page to see the updated data
		window.location = productProfileContextPath;
	}
	
	
	ProductProfile.showModalPopup = function(el, id, action) {
		resetForm();
		if (id) {
			switch (action) {
			case 0:
				showProductProfile(id);
				break;
			case 1:
				editProductProfile(id);
				createEditForm.attr('method', 'PUT');
				break;
			case 2:
				deleteForm.attr('action', productProfileContextPath + "/" + id);
				break;
			case 3:
				uploadImage(id);
				break;
			case 4:
				showProductProfileImages(id);
				break;
			case 5:
				showProductProfileProductGroup(id);
				break;
			case 6:
				loadAssignTax(id);
				productProfileModel.pid=id;
				break;
			}
		}
		el.modal('show');
	}

	function loadAssignTax(id){
		$("#taxcheck input[type='checkbox']").prop("checked",false);
		$.ajax({
			url : productProfileContextPath + "/assignTax/" + id,
			method : 'GET',
			success : function(response) {
				$.each(response, function(index, taxMaster) {
						$("#taxcheck input[type='checkbox'][value="+ taxMaster.pid + "]").prop("checked",true);
				});
			},
			error : function(xhr, error) {
				onError(xhr, error);
			}
		});
	}
	
	function searchTable(inputVal, table) {
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

	function resetForm() {
		$('.alert').hide();
		createEditForm.trigger("reset"); // clear form fields
		createEditForm.validate().resetForm(); // clear validation messages
		createEditForm.attr('method', 'POST'); // set default method
		productProfileModel.pid = null; // reset productProfile model;

		// color image
		productProfileModel.colorImage = null;
		productProfileModel.colorImageContentType = null;
		$('#previewColorImage').attr('src', '');

		$('#txtSize').val(0);
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