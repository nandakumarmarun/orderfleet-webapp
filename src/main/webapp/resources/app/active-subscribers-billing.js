if (!this.ActiveSubscriberBilling) {
	this.ActiveSubscriberBilling = {};
}

(function() {
	'use strict';

	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(function() {
		
		$("#txtFromDate").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		$("#txtToDate").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		
		$('#selectAll').on('click', function() {
			selectAllBilling(this);
		});
		
		// load default
		ActiveSubscriberBilling.filter();
	});
	
	function selectAllBilling(checkbox) {
		$('.check-one').prop('checked', checkbox.checked);
	}
	
	$('#btnDownloadxls').on('click', function() {
		downloadXls();
	});

	function downloadXls()
	{
		var billingSettingPids = "";
		$("input[type='checkbox']:checked").each(function() {
			var billingSettingPid = $(this).val();
			if (billingSettingPid != "on") {
				billingSettingPids += billingSettingPid + ",";
			}
		});

		if (billingSettingPids == "") {
			alert("please select billing setting");
		} else {
			window.location.href = contextPath
					+ "/web/subscribers-billing/download-xls?billingSettingPids="
					+ billingSettingPids;
			console.log("sucess.......");
		}
		
	}

	ActiveSubscriberBilling.details = function(Pid,companyPid) {
	     $('#tbodyDetails').html("");
	     $('#lbl_active_user').html("");
	     $("#lbl_CompanyName").text("");
        $
        		        .ajax({
        					url : contextPath + "/web/subscribers-billing/details",
        					type : 'GET',
        					data : {
        						companyPid : companyPid,
        						billingSettingPid : Pid,
        					},
        					success : function(billing) {
        						showDetails(billing)
        					},
        					error: function(xhr, error) {
        	                    console.log("Error sales report : " + error)
        	                }
        				});
	}

	function showDetails(billingdetails){
	                       $("#lbl_active_user").text(billingdetails.countOfActiveUser);
	                         $("#lbl_CompanyName").text(billingdetails.companyName);
                        $.each(
                            billingdetails.billingDetail,
                            function(index, details) {
                                $('#tbodyDetails')
                                    .append(
                                        "<tr><td>" +
                                        details.slabName +
                                        "</td><td>" +
                                        details.slabRate +
                                        "</td></tr>");
                            }

                        );
	}

	ActiveSubscriberBilling.filter = function() {
		
		if ($('#dbDateSearch').val() == "CUSTOM") {
			if ($("#txtFromDate").val() == "" || $("#txtToDate").val() == "") {
				return;
			}
		}

	
		$('#tBodyActiveBillingReport').html("<tr><td colspan='9' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : contextPath + "/web/subscribers-billing/filter",
					type : 'GET',
					data : {
						billingPeriod : $('#field_billingPeriod').val(),
						fromDate : $("#txtFromDate").val(),
						toDate : $("#txtToDate").val(),
						
					},
					success : function(billing) {
						loadTableData(billing)
					},
					error: function(xhr, error) {
	                    console.log("Error sales report : " + error)
	                }
				});
	}

		
	function loadTableData(billing)
	{

  $('#tBodyActiveBillingReport').html("");
        if (billing.length == 0) {

            $('#tBodyActiveBillingReport')
                .html(
                    "<tr><td colspan='9' align='center'>No data available</td></tr>");
            return;
        }

        $
            .each(
                billing,
                function(index, billingDTO) {


                    $('#tBodyActiveBillingReport')
                        .append(
                            "<tr style='" +
                            "'data-id='" +
                            billingDTO.pid +
                            "' data-parent=\"\"><td><input type='checkbox' class='check-one' value='"
																	+ billingDTO.pid
																	+ "' />"
																	+ "</td><td class='tableexport-string target'>" +
                            billingDTO.companyName +
                            "</td><td class='tableexport-string target'>" +
                            billingDTO.fromDate +
                            "</td><td>" +
                            billingDTO.toDate +
                            "</td><td>" +
                            billingDTO.noOfMonth +
                            "</td><td>" +
                            "<button type='button' class='btn btn-blue' onclick='ActiveSubscriberBilling.showModalPopup($(\"#viewModal\"),\""
                            																			+ billingDTO.pid
                            																			+ "\",\""
                            																			+ billingDTO.companyPid
                            																			+ "\",);'>View Details</button>" +
                            "</td></tr>");

//                    $('#tBodyActiveBillingReport')
//                        .append(
//                            "<tr  data-parent='" +
//                            billingDTO.pid +
//                            "'><th colspan='3'>" +
//                            "Slab" +
//                            "</th><th colspan='3'>" +
//                            "Amount" +
//                            "</th></tr>");

//                    $
//                        .each(
//                            billingDTO.billingDetail,
//                            function(index, details) {
//                                $('#tBodyActiveBillingReport')
//                                    .append(
//                                        "<tr  data-id='" +
//                                        details.pid +
//                                        "1' data-parent='" +
//                                        details.headerPid +
//                                        "' ><td colspan='3'>" +
//                                        details.slabName +
//                                        "</td><td colspan='3'>" +
//                                        details.slabRate +
//                                        "</td></tr>");
//                            }
//
//                        );

                });

//        $('.collaptable')
//            .aCollapTable({
//                startCollapsed: true,
//                addColumn: false,
//                plusButton: '<span><i class="entypo-down-open-mini"></i></span>',
//                minusButton: '<span><i class="entypo-up-open-mini"></i></span>'
//            });
    
	}
	
	ActiveSubscriberBilling.showDatePicker = function() {
		$("#txtFromDate").val("");
		$("#txtToDate").val("");
		if ($('#dbDateSearch').val() == "CUSTOM") {
			$(".custom_date1").addClass('show');
			$(".custom_date2").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").removeClass('hide');
			$('#divDatePickers').css('display', 'initial');
		} else if ($('#dbDateSearch').val() == "SINGLE") {
			$(".custom_date1").addClass('show');
			$(".custom_date1").removeClass('hide');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$("#txtFromDate").datepicker({
				dateFormat : "dd-mm-yy"
			});
			$("#txtFromDate").datepicker('show');
			$('#divDatePickers').css('display', 'initial');
		} else {
			$(".custom_date1").addClass('hide');
			$(".custom_date1").removeClass('show');
			$(".custom_date2").addClass('hide');
			$(".custom_date2").removeClass('show');
			$('#divDatePickers').css('display', 'none');
		}

	}

	ActiveSubscriberBilling.showModalPopup = function(el, pid,companyPid,action) {
    		$(".error-msg").html("");
    		console.log(pid)
    		console.log(companyPid)
            ActiveSubscriberBilling.details(pid,companyPid)
    		el.modal('show');
    	}

	function convertDateTimeFromServer(date) {
		if (date) {
			return moment(date).format('MMM DD YYYY');
		} else {
			return "";
		}
	}

})();