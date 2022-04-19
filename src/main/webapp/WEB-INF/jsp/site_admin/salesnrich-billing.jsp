<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<!-- convert to pdf -->
<spring:url value="/resources/assets/js/jspdf.debug.js" var="jsPDF"></spring:url>
<script type="text/javascript" src="${jsPDF}"></script>
<spring:url value="/resources/assets/js/html2canvas.js"
	var="html2canvas"></spring:url>
<script type="text/javascript" src="${html2canvas}"></script>

<style type="text/css">
text-center {
	text-align: center;
}

.clickable {
	cursor: pointer;
}

.invoice-title h2, .invoice-title h3 {
	display: inline-block;
}

.table>tbody>tr>.no-line {
	border-top: none;
}

.table>thead>tr>.no-line {
	border-bottom: none;
}

.table>tbody>tr>.thick-line {
	border-top: 2px solid;
}
</style>

<title>SalesNrich | Billing</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<h2>Billing</h2>
			<div class="clearfix"></div>
			<hr />
			<div id="aaa">
				<div class="row">
					<form>
						<div class="row">
						 <div class="col-sm-3">
						Company<select id="sbCompany" name="companyCode"
							class="form-control selectpicker" data-live-search="true">
							<option value="no">Select Company</option>
							<c:forEach items="${companies}" var="company">
								<option value="${company.pid}">${company.legalName}</option>
							</c:forEach>
						</select>
					</div> 
							 
							<div class="col-sm-2">
								<br /> <select class="form-control" id="dbDateSearch"
									onchange="showDatePicker()">
									<option value="CURRENT MONTH">CURRENT MONTH</option>
									<option value="PREVIOUS MONTH">PREVIOUS MONTH</option>
									<option value="CUSTOM">CUSTOM</option>
								</select>
							</div>
							
								<div class="col-sm-2 hide custom_date1">
								<br />
								<div class="input-group">
									<input type="text" class="form-control" id="txtFromDate"
										placeholder="Select From Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-2 hide custom_date2">
								<br />
								<div class="input-group">
									<input type="text" class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
							<div class="col-sm-3">
								<br />
								<button type="button" class="btn btn-info entypo-search"
									style="font-size: 18px" id="btnApply" title="Apply"></button>
							</div>
							
							
						</div>
					</form>
				</div>

				<div class="row">
					<form>
						<div class="row">
							<div class="col-sm-3">
								<label>Invoice Number</label> <input type="text"
									placeholder="invoice number"
									onkeypress="return isNumber(event);" value="${invoiceNumber}"
									id="txtInvoiceNumber" class="form-control "
									style="text-align: center;" />
							</div>
							<div class="col-sm-3">
								<label>Invoice Date</label>
								<div class="input-group">
									<input type="text" class="form-control" id="txtInvoiceDate"
										placeholder="Select Invoice Date"
										style="background-color: #fff;" readonly="readonly" />
									<div class="input-group-addon">
										<a href="#"><i class="entypo-calendar"></i></a>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>

				<div class="row clickable" data-toggle="collapse"
					data-target="#divTxnDetails">
					<div class="alert alert-success">
						<div id="divLoading" class="row"
							style="display: none; text-align: center;">
							<strong>Loading...</strong>
						</div>
						<div id="divCount" class="row">
							<div class="col-sm-4">
								<strong>Checked : <span id="sltUserCount">0</span></strong>
							</div>
							<div class="col-sm-4">
								<strong>Active : <span id="actUserCount">0</span></strong>
							</div>
							<div class="col-sm-4">
								<strong>Total : <span id="totUserCount">0</span></strong>
							</div>
						</div>
					</div>
				</div>

				<div class="row collapse" id="divTxnDetails">
					<table class="table table-bordered table-hover">
						<thead>
							<tr>
								<th class="col-md-1"><small></small></th>
								<th class="col-md-5"><small>User</small></th>
								<th class="col-md-3"><small>Attendance Count</small></th>
								<th class="col-md-3"><small>Execution Count</small></th>
							</tr>
						</thead>
						<tbody></tbody>
					</table>
				</div>

				<div class="row">
					<form>
						<div class="row">
							<div class="col-sm-4">
								<label for="txtParticular">Particulars</label>
								<textarea id="txtParticular" class="form-control" rows="2"></textarea>
							</div>
							<div class="col-sm-2">
								<label for="txtQty">Quantity</label> <input type="text"
									onkeypress="return isNumber(event);"
									onkeyup="calculateTotal();" id="txtQty" class="form-control"
									style="text-align: center;" value="1" />
							</div>
							<div class="col-sm-2">
								<label for="txtPrice">Price</label> <input type="text"
									onkeypress="return isNumber(event);"
									onkeyup="calculateTotal();" id="txtPrice" value="0"
									class="form-control" style="text-align: center;" />
							</div>
							<div class="col-sm-2">
								<label for="txtTotal">Total</label> <input type="text"
									id="txtTotal" value="0" class="form-control"
									style="text-align: center;" readonly="readonly" />
							</div>
							<div class="col-sm-2">
								<br />
								<button id="btnAddRow" type="button" class="btn btn-info">Add
									Row</button>
							</div>
						</div>
					</form>
					<table id="tblBillDetails" class="table table-bordered" border="1">
						<thead>
							<tr>
								<th style="width: 50%; text-align: left;">Particulars</th>
								<th style="width: 8%; text-align: left;">Quantity</th>
								<th style="width: 10%; text-align: left;">Price</th>
								<th style="width: 22%; text-align: center;" class="">Total</th>
								<th style="width: 10%"></th>
							</tr>
						</thead>
						<tbody id= tableBillDetails></tbody>
						<tfoot>
							<tr>
								<td></td>
								<td></td>
								<td class="hidden-xs" style="text-align: left;">Sub Total</td>
								<td class="hidden-xs " style="text-align: center;"><strong
									id="subTotal"></strong></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td class="hidden-xs" style="text-align: left;">GST@18%</td>
								<td class="hidden-xs" style="text-align: center;"><strong
									id="gstAmt"></strong></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
								<td class="hidden-xs" style="text-align: center;"><strong>TOTAL</strong></td>
								<td class="hidden-xs" style="text-align: center;"><strong
									id="totalAmt"></strong></td>
								<td></td>
							</tr>
						</tfoot>
					</table>

					<div class="col-sm-12">
						<div style="position: absolute; right: 0px;">
							<button id="myFormSubmit" class="btn btn-primary">Save</button>
							&nbsp;
							<button id="printInvoice"
								class="btn btn-primary btn-icon icon-left hidden-print">
								Print Invoice <i class="entypo-doc-text"></i>
							</button>

							<a id="getCompanyInvoConfig"
								class="btn btn-primary entypo-doc-text">print</a>
						</div>
					</div>
				</div>
			</div>


			<div class="container" id="invoiceDiv" hidden="true">
				<div class="row">
					<div class="row" style="height: 200px">
						<div class="col-xs-12" id="img_topBar">
							<div class="col-xs-6">
								<address>
									<br> <br> <font size="2"> 19/60,Visitors
										Bulding, II nd Floor,<br> M.G Road,Thrissur-4<br> <strong>
											Ph : 0487 32 99 100</strong><br> e-mail : admin@aitrich.com<br>
										www.aitrich.com
									</font>
								</address>
							</div>
							<div class="row"
								style="position: absolute; right: 0px; top: 0px;">
								<img src="/resources/assets/images/aitrich.png"
									style="display: block; margin: auto; width: 200px;">
							</div>
						</div>
					</div>

					<div class="col-xs-12">
						<div class="col-xs-6" style="position: absolute; right: 350px;">
							<h2>
								<b style="font-size: 20px; text-decoration: underline;">Invoice</b>
							</h2>
						</div>
					</div>
					<br /> <br /> <br /> <br />
					<div class="col-xs-12">
						<div style="position: absolute; right: 0px;">
							<address>
								<strong>Invoice ID :</strong> ${invoiceNumber} <br> <strong>Invoice
									Date :</strong> <strong id="invoiceDate"></strong> <br> <br>
							</address>
						</div>
					</div>
					<br /> <br /> <br /> <br />
					<div class="col-xs-12">
						<div class="col-xs-6 ">
							<address>
								<strong>From,</strong><br> Aitrich Technologies Pvt Ltd.<br>
								M.G Road, Thrissur<br> GSTIN - 32AAICA5588N2Z4
							</address>
						</div>
					</div>
					<br />
					<div class="col-xs-12">
						<div class="col-xs-6 ">
							<address>
								<strong>To:</strong><br /> <a id="toAddress"></a>
							</address>
						</div>
					</div>
				</div>
				<br /> <br />
				<div class="row">
					<div class="col-md-12">
						<div class="panel panel-default">
							<div class="panel-body">
								<div class="table-responsive">
									<table id="tblPrintBillDetails" class="table table-bordered"
										style="border-collapse: collapse; border: 1px solid black;">
									</table>
									<div class="col-xs-12" id="img_seal">
										<div class="col-xs-6 "></div>
										<div class="col-xs-6">
											<img src="/resources/assets/images/Seal.png"
												style="display: block; margin: auto; width: 160px;">
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
	<script type="text/javascript" src="${momentJs}"></script>

	<script type="text/javascript">
	
	var salesNrichBillingContextPath = location.protocol + '//' + location.host
	+ location.pathname;
	
	var salesnrichInvoiceDetailModel = {
			companyPid : null,
			companyName : null,
			particulars : null,
			quantity : null,
			price : null,
			total : null
		};
	
	var salesnrichInvoiceHeaderModel = {
			invoiceNumber : null,
			invoiceDate : null,
			billingFrom : null,
			billingTo : null,
			activeUserCount : null,
			checkedUserCount : null,
			totalUserCount : null,
			gstPercentage : null,
			gstAmount : null,
			subTotal : null,
			totalAmount : null,
			companyPid : null,
			companyName : null
	};
	
	function isNumber(evt) {
	    evt = (evt) ? evt : window.event;
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
	        return false;
	    }
	    return true;
	}
	
	function calculateTotal(){
		var qty = $("#txtQty").val();
		var price = $("#txtPrice").val();
		$("#txtTotal").val(qty * price);
	}
	
		$(document).ready(
				function() {
				//$("#invoiceDiv").hide();
					
				$("#printInvoice").click(function() {
					getCompanyInvoConfigAndPrint();
					$("#img_topBar").hide();
					$("#img_seal").hide();
					printDiv();
					$("#img_topBar").show();
					$("#img_seal").show();
				});
				
					$('#getCompanyInvoConfig').on('click', function() {
						getCompanyInvoConfigAndPrint();
						printDiv();
					});
					
					$('#btnApply').on('click', function() {
						loadTransactionDetails();
					});
					$('#btnSearch').click(function() {
						searchTable($("#search").val(), $('#tableBillDetails'));
					});
					salesNrichDatePickers();

					//updateSelectedUserCountOnDiv
					$(document).on("change", ".chkBox" , function() {
						var count = $('.chkBox:checked').size();
						$("#sltUserCount").html(count);
						$("#txtQty").val(count);
				    });
					
					$("#btnAddRow").click(
						function() {
							var particular = $("#txtParticular").val();
							var qty = $("#txtQty").val();
							var price = $("#txtPrice").val();
							var tot = $("#txtTotal").val();
							if(particular == "" || tot == 0){
								alert("Please fill the details correctly");
								return;
							}
							var markup = "<tr><td>" + particular
									+ "</td><td>" + qty + "</td><td>"
									+ price + "</td><td class='tot' style='text-align: center; height: 50px;'>" + tot
									+ "</td><td><button class='btn btn-danger btn-sm'><i class='glyphicon glyphicon-trash'></i></button></td></tr>";
							$("#tblBillDetails tbody").append(markup);
							
							clearElements();
							calculateTotal();
					});
					// Find and remove selected table rows
					$(document).on("click", ".btn-danger" , function() {
						$(this).closest('tr').remove();
						calculateTotal();
				    });
					
					function clearElements(){
						$("#txtParticular").val('');
						$("#txtQty").val('1');
						$("#txtPrice").val('0');
						$("#txtTotal").val('0');
					}
					
					function calculateTotal() {
						var subTotal = 0;
						$('#tblBillDetails .tot').each(function() {     
							subTotal += parseInt($(this).text());                    
			        	});
						$('#subTotal').html(' &#x20B9;' + subTotal);
						var gstAmt = (18 * subTotal)/100;
						$('#gstAmt').html(' &#x20B9;' + gstAmt);
						var totAmt = subTotal + gstAmt;
						$('#totalAmt').html(' &#x20B9;' + totAmt);
					}
					
					function loadTransactionDetails() {
						var companyPid = $('#sbCompany').val();
						if (companyPid == "-1") {
							alert("Please select company");
							return;
						}
						$("#divLoading").css("display","block");
						$("#divCount").css("display","none");
						
						$.ajax({
							url : salesNrichBillingContextPath + "/" + companyPid,
							type : "GET",
							data : {
								filterBy : $("#dbDateSearch").val(),
								fromDate : moment($("#txtFromDate").val(), 'DD-MM-YYYY')
										.format('YYYY-MM-DD'),
								toDate : moment($("#txtToDate").val(), 'DD-MM-YYYY')
										.format('YYYY-MM-DD')
							},
							success : function(data) {
								$("#divLoading").css("display","none");
								$("#divCount").css("display","block");
								if(data){
									var html = "";
									var activeUser = 0;
									$.each(data, function(index, billingUser){
										var checkbox = "";
										if (billingUser.executionCount > 0 || billingUser.attendanceCount > 0) {
											checkbox = "<input class='chkBox' type='checkbox' value='" + billingUser.login + "' checked>";
											activeUser += 1;
										} else {
											checkbox = "<input class='chkBox' type='checkbox' value='" + billingUser.login + "'";
										}
										var attCount = 0;
										var execCount = 0;
										if(billingUser.attendanceCount) {
											attCount = billingUser.attendanceCount;
										}
										if(billingUser.executionCount){
											execCount = billingUser.executionCount;
										}
										html += "<tr><td>"
												+ checkbox
												+ "</td>"
												+ "<td>"
												+ billingUser.login
												+ "</td>"
												+ "<td>"
												+ attCount
												+ "</td>"
												+ "<td>"
												+ execCount
												+ "</td></tr>";
									});
									$("#divTxnDetails table tbody").html(html);
									$("#totUserCount").html(data.length);
									$("#actUserCount").html(activeUser);
									var count = $('.chkBox:checked').size();
									$("#sltUserCount").html(count);
									$("#txtQty").val(count);
								}
							},
							error : function(xhr, error) {
								$("#divLoading").css("display","none");
								$("#divCount").css("display","block");
								console.log("Error in loading data.");
							}
						});
					}
					
					$("#myFormSubmit").click( function() {
						saveSalesNrichInvoiceHeader();
					});
				});
		
		function getCompanyInvoConfigAndPrint() {
			var companyPid = $('#sbCompany').val();
			if (companyPid == "-1") {
				alert("Please select company");
				return;
			}
			
			$.ajax({
				url : salesNrichBillingContextPath + "/get-company-invoice-config/" + companyPid,
				type : "GET",
				async:false,
				success : function(data) {
					if(data){
					$("#toAddress").html(data.address);
					
					var today = moment().format('DD-MM-YY');
					$("#invoiceDate").html(today);
					}
					$("#tblPrintBillDetails").html($("#tblBillDetails").html());
					
					$('#tblPrintBillDetails').find("tr td:nth-child(5)").each(function(){
					    $(this).remove()
					});
					$('#tblPrintBillDetails th:nth-child(5),#table td:nth-child(5)').remove();
				},
				error : function(xhr, error) {
					$("#divLoading").css("display","none");
					$("#divCount").css("display","block");
					console.log("Error in loading data.");
				}
			});
		}
		
		
		
		
		function printDiv() 
		{
		 var divToPrint=document.getElementById('invoiceDiv');
		  var newWin=window.open('','Print-Window');
		  newWin.document.open();
		  newWin.document.write('<html><body onload="window.print()">'+divToPrint.innerHTML+'</body></html>');
		  newWin.document.close();
		  setTimeout(function(){newWin.close();},5); 
		}
		
	function saveSalesNrichInvoiceHeader(){
		
		var salesnrichInvoiceDetailDtos=[];
		
		var companyPid = $('#sbCompany').val();
		if (companyPid == "-1") {
			alert("Please select company");
			return;
		}
		
		//details
		var rows = $('#tblBillDetails tbody >tr');
	    var columns;
	    for (var i = 0; i < rows.length; i++) {
	        columns = $(rows[i]).find('td');
	        
	        salesnrichInvoiceDetailModel.companyPid = companyPid;
			salesnrichInvoiceDetailModel.particulars = $(columns[0]).html();
			salesnrichInvoiceDetailModel.quantity = $(columns[1]).html();
			salesnrichInvoiceDetailModel.price = $(columns[2]).html();
			salesnrichInvoiceDetailModel.total = $(columns[3]).html();
	        
			salesnrichInvoiceDetailDtos.push(salesnrichInvoiceDetailModel);
			salesnrichInvoiceDetailModel={};
	    }
	   
	    
	    var gstAmt = $('#gstAmt').html().replace( /^\D+/g, '');
	    var subTotal = $('#subTotal').html().replace( /^\D+/g, '');
	    var totAmt = $('#totalAmt').html().replace( /^\D+/g, '');
	    
	    salesnrichInvoiceHeaderModel.invoiceNumber = $('#txtInvoiceNumber').val();
	    salesnrichInvoiceHeaderModel.invoiceDate = moment($("#txtInvoiceDate").val(),'DD-MM-YYYY').format('YYYY-MM-DD');
	    salesnrichInvoiceHeaderModel.billingFrom = moment($("#txtFromDate").val(),'DD-MM-YYYY').format('YYYY-MM-DD');
	    salesnrichInvoiceHeaderModel.billingTo = moment($("#txtToDate").val(),'DD-MM-YYYY').format('YYYY-MM-DD');
	    salesnrichInvoiceHeaderModel.activeUserCount = $('#actUserCount').html();
	    salesnrichInvoiceHeaderModel.checkedUserCount = $("#sltUserCount").html();
	    salesnrichInvoiceHeaderModel.totalUserCount = $('#totUserCount').html();
	    salesnrichInvoiceHeaderModel.gstPercentage = parseFloat('18.0');
	    salesnrichInvoiceHeaderModel.gstAmount = parseFloat(gstAmt);
	    salesnrichInvoiceHeaderModel.subTotal = parseFloat(subTotal);
	    salesnrichInvoiceHeaderModel.totalAmount = parseFloat(totAmt);
	    salesnrichInvoiceHeaderModel.companyPid = companyPid;
	    salesnrichInvoiceHeaderModel. salesnrichInvoiceDetailDTOs=salesnrichInvoiceDetailDtos;
	    
	    $.ajax({
			method : 'POST',
			url : salesNrichBillingContextPath+"/save",
			contentType : "application/json; charset=utf-8",
			data : JSON.stringify(salesnrichInvoiceHeaderModel),
			success : function(data) {
				onSaveSuccess(data);
			},
			error : function(xhr, error) {
				console.log("Error====" + error);
			}
		});
		
	}
	
	showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		}  else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}
		salesNrichDatePickers();
	}
		
		function salesNrichDatePickers(){
			var date = new Date();
			var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
			var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
			
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			}).datepicker("setDate", firstDay);
			
			$("#txtToDate").datepicker({
				dateFormat : "dd-mm-yy"
			}).datepicker("setDate", lastDay);
			
			$("#txtInvoiceDate").datepicker({
				dateFormat : "dd-mm-yy"
			}).datepicker("setDate", date);
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

			
		function onSaveSuccess(result) {
			// reloading page to see the updated data
			window.location = salesNrichBillingContextPath;
		}
	</script>
</body>
</html>