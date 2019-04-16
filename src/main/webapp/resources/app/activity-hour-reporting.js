// Create a ActivityHourReporting object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

if (!this.ActivityHourReporting) {
	this.ActivityHourReporting = {};
}

(function() {
	'use strict';

	var activityHourReportingContextPath = location.protocol + '//'
			+ location.host + location.pathname;

	$(document).ready(function() {

	});

	ActivityHourReporting.loadData = function() {
		
		$("#divProductGroupName").html("");
		$("#divProductCategory").html("");
		
		var productGroupPid = $('#dbProductGroup').val();
		if (productGroupPid == "-1") {
			return;
		}
		
		var productGroupName = $('#dbProductGroup option:selected').text();
		$("#divProductGroupName").html(productGroupName);
		$("#divProductCategory").html("<label style='color:green;' >Please wait...</label>");
		
		showTable(productGroupPid);
	}

	

	function showTable(productGroupPid) {

		$.ajax({
					url : activityHourReportingContextPath + "/load-data",
					type : 'GET',
					data : {
						priceTrendProductGroupPid : productGroupPid
					},
					success : function(productGroup) {
						var table = "";
						var month = "";
						$("#divProductCategory").html("");
					
						$.each(productGroup.productCategories, function(index, productCategory) {
											var i = 0;
											var j = 1;

											var k = 0;
											var l = 1;

											var tbodyRow = "";
											var theadRow = "";
											var monthRow = "";

											var array = [];

											table += "<div class='table-responsive'><table class='table table-striped table-bordered' style='border'><thead ><tr><th id='tHead"
													+ productCategory.pid
													+ ""
													+ i
													+ "' colspan='2' style='background-color: rgba(255, 175, 189, 0.21);color:black;font-weight:bolder;font-size: small;'>"
													+ productCategory.name
													+ "</th></tr><tr style='background-color:rgb(255, 255, 154);color:black;font-weight:bolder'><td></td><td id='tdRow"
													+ productCategory.pid
													+ ""
													+ i
													+ "'></td></tr><tr></thead><tbody id='tableBody"
													+ productCategory.pid
													+ "'></tbody></table></div>"
											$("#divProductCategory").append(table);

											$.each(productCategory.compitators,function(index, compitator) {

													var rcp = "";
													var dealerPrice = ""

													array.push(compitator.pid);

													   console.log("prices...............................................");
													var prices = compitator.months[0].weeks[0].prices.slice();
													$.each(compitator.months,function(index,month) {
															$.each(month.weeks,function(index,week) {
																$.each(week.prices,function(index,price1) {
																     $.each(prices,function(index,price) {
																          if(price1.priceLabel == price.priceLabel){
																        	  price.values += "<td>"+ price1.price  + "</td>";
																          }
																      });
																});
															});
													});
													
													tbodyRow += "<tr ><td rowspan='"+prices.length+"' class='col-md-2' style='background-color:rgb(210, 252, 205);color:black;'>"
																		+ compitator.name
																		+ "</td>";
													                 $.each(prices,function(index,price) {	
													                	 if(index == 0){
													                		 tbodyRow +=   "<td id='tdRcp"
																					+ productGroupPid
																					+ ""
																					+ productCategory.pid
																					+ ""
																					+ compitator.pid
																					+ "'>"+price.priceLabel+"</td>"
																					+ price.values
																					+ "</tr>";
													                	 } else{
													                		 tbodyRow +=  "<tr ><td id='tdDealerPrice"
																					+ productGroupPid
																					+ ""
																					+ productCategory.pid
																					+ ""
																					+ compitator.pid
																					+ "'>"+price.priceLabel+"</td>"
																					+ price.values
																					+ "</tr>";
													                	 }
													                   
													             	});

															});
											$("#tableBody"+ productCategory.pid+ "").html(tbodyRow);

											$.each(productGroup.months,function(index,month) {

													$("#tHead"+ productCategory.pid + "" + i + "").after(
																				"<th id='tHead"
																						+ productCategory.pid
																						+ ""
																						+ j
																						+ "' colspan='"
																						+ month.noOfWeeks
																						+ "' style='background-color:rgba(255, 255, 143, 0.34);color:black;font-weight:bolder; data-id="
																						+ month.name
																						+ "; data-parent='>"
																						+ month.name
																						+ "</th>");

													if (month.weeks != 0) {
														$.each(month.weeks,function(index,week) {
															$("#tdRow"+ productCategory.pid+ ""+ k + "").after(
																						"<td style='background-color:#ebebeb;color:black;font-weight:bolder;font-size:11px;data-id="
																							+ week.week
																							+ "; data-parent="
																							+ month.name
																							+ ";' id='tdRow"
																							+ productCategory.pid
																							+ ""
																							+ l
																							+ "'>"
																							+ week.week
																							+ "</td>");
																						k++;
																						l++;
														 });
																}
																i++;
																j++;
															});

											table = "";
										});

					},
					beforeSend : function(xhr) {
						xhr.setRequestHeader("Content-Type","application/json");
					},
					error : function(jqXhr, textStatus, errorThrown) {
						alert(textStatus);
						console.log("Error in load routes first time.......");
					}
				});
	}

	function show_form() {
		$("#modalProductGroup").modal("show");
		$('.dd').nestable({});
	}
	var categoryid = 0;
	function categoryUl() {
		$(".error-msg").html("");
		if ($('#txtProductGroup').val() == "") {
			$(".error-msg").html("Please enter Product Group");
			return;
		}

		categoryid++;

		var rows = "";
		rows = "<li class='dd-item'><div class='dd-content'><input type='text' id='txtCategory"
				+ categoryid
				+ "' class='form-control col-md-10' placeholder='Product Catagory'> <input type='submit' id='categoryBtn' onclick='itemUl("
				+ categoryid
				+ ")' class='btn btn-facebook' value='Add Item'></div><ul id='ulItem"
				+ categoryid + "' class='dd-list'></ul></li>";
		$("#ulCategory").append(rows);
		$('.dd').nestable({});

	}

	var itemid = 0;
	function itemUl(id) {
		$(".error-msg").html("");
		if ($("#txtCategory" + id + "").val() == "") {
			$(".error-msg").html("Please enter category");
			return;
		}

		itemid++;

		var rows = "";
		rows = "<li class='dd-item'><div class='dd-content'><input type='text' id='txtItem"
				+ id
				+ "-"
				+ itemid
				+ "' class='form-control col-md-10' placeholder='Item'> <input type='submit' id='compitatorBtn' onclick='compitatorUl("
				+ itemid
				+ ")' class='btn btn-facebook' value='Add Compitators'></div><ul id='ulCompitators"
				+ itemid + "' class='dd-list'></ul></li>";

		$("#ulItem" + id + "").append(rows);
		$('.dd').nestable({});

	}

	var compitatorid = 0;
	function compitatorUl(id) {
		$(".error-msg").html("");
		if ($("#txtItem" + id + "").val() == "") {
			$(".error-msg").html("Please enter item");
			return;
		}

		compitatorid++;

		var rows = "";
		rows = "<li class='dd-item'><div class='dd-content'><input type='text' id='txtCompitator"
				+ id
				+ "-"
				+ compitatorid
				+ "' class='form-control' placeholder='Compitator'> <br><br></li>";

		$("#ulCompitators" + id + "").append(rows);

		$('.dd').nestable({});

	}

	var productGroup = {
		id : null,
		name : null,
		productCategories : []
	};

	function saveProductGroup() {
		productGroup.name = $("#txtProductGroup").val();
		for (var i = 1; i <= categoryid; i++) {
			var categories = {
				id : null,
				name : $("#txtCategory" + i + "").val(),
				productItems : []
			};

			for (var j = 1; j <= itemid; j++) {

				var items = {
					id : null,
					name : $("#txtItem" + i + "-" + j + "").val(),
					productCompitators : []
				};

				for (var k = 1; k <= compitatorid; k++) {

					var compitators = {
						id : null,
						name : $("#txtCompitator" + j + "-" + k + "").val()
					};
					items.productCompitators.push(compitators);
				}
				categories.productItems.push(items);
			}
			productGroup.productCategories.push(categories);
		}
		console.log(JSON.stringify(productGroup));

		$.ajax({
			url : contextPath + "/company/saveProductGroup",
			type : "POST",
			contentType : 'application/json; charset=utf-8',
			dataType : 'json',
			data : JSON.stringify(productGroup),

			success : function(status) {

				$("#modalProductGroup").modal("hide");

			},
			error : function(jqXhr, textStatus, errorThrown) {
				alert(textStatus);
			}
		});
		productGroup.productCategories = []
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