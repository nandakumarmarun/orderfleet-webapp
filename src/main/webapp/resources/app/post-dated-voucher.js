if (!this.PostDatedVoucher) {
	this.PostDatedVoucher = {};
}

(function() {
	'use strict';

	var postDatedVoucherContextPath = location.protocol + '//' + location.host
			+ location.pathname;
	$(document).ready(function() {
		PostDatedVoucher.loadData();
	});

	PostDatedVoucher.loadData = function() {
		$('#tBodyPostDatedVoucher').html(
				"<tr><td colspan='4' align='center'>Please wait...</td></tr>");
		$
				.ajax({
					url : postDatedVoucherContextPath + "/load",
					type : 'GET',
					data : {
						accountPid : $('#dbAccount').val()
					},
					success : function(postDatedVoucherMap) {
						$('#tBodyPostDatedVoucher').html("");
						if (jQuery.isEmptyObject(postDatedVoucherMap)) {
							$('#tBodyPostDatedVoucher')
									.html(
											"<tr><td colspan='4' align='center'>No data available</td></tr>");
							return;
						}
						$
								.each(
										postDatedVoucherMap,
										function(accountProfilePid,
												postDatedVouchers) {
											var pdc = postDatedVouchers[0];
											console.log(accountProfilePid);
											$('#tBodyPostDatedVoucher')
													.append(
															"<tr data-toggle='collapse' data-target='#"
																	+ accountProfilePid
																	+ "'>"
																	+ "<td class='entypo-down-open-mini'>"
																	+ pdc.accountProfileName
																	+ "</td><td align='left' style='font-weight: bold;'  id='pdAmount"
																	+ pdc.accountProfilePid
																	+ "'>"
																	+ pdc.referenceDocumentAmount
																	+ "</td></tr>");
											$('#tBodyPostDatedVoucher')
													.append(
															"<tr class='collapse' id='"
																	+ accountProfilePid
																	+ "'><td colspan='5'>"
																	+ "<table class='table table-striped table-bordered' id='pdc-"
																	+ pdc.accountProfilePid
																	+ "'>"
																	+ "<tr style='background: rgba(180, 232, 168, 0.56);'><th>Document Number</th><th> Date</th><th> Amount</th><th>Remark</th></tr>");
											var totalPdcAmt = 0;
											$
													.each(
															postDatedVouchers,function(index,postDated) {

																totalPdcAmt += postDated.referenceDocumentAmount;

																$('#pdc-'+ pdc.accountProfilePid)
																		.append(
																				"<tr><td>"
																						+ postDated.referenceDocumentNumber
																						+ "</td><td>"
																						+ postDated.referenceDocumentDate
																						+ "</td><td>"
																						+ postDated.referenceDocumentAmount
																						+ "</td><td>"
																						+ postDated.remark
																						+ "</td></tr>"
																						+ "</table></td></tr>");

															});
											$('#pdAmount'+ pdc.accountProfilePid).text(totalPdcAmt);
										});

					}
				});
	}

})();