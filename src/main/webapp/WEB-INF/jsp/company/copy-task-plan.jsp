<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<!-- jQuery UI css-->
<spring:url value="/resources/assets/css/jquery-ui.css"
	var="jqueryUiCss"></spring:url>
<link href="${jqueryUiCss}" rel="stylesheet">
<style type="text/css">
.plans-daterange {
	position: relative;
}

.plans-daterange i {
	position: absolute;
	bottom: 10px;
	right: 24px;
	top: auto;
	cursor: pointer;
}
</style>
<title>SalesNrich | Copy Task Plan</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Copy Task Plan</h2>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-sm-4">
					<select id="sbUsers" class="form-control">
						<option value="-1">Select User</option>
						<c:forEach items="${users}" var="user">
							<option value="${user.pid}">${user.firstName}
								(${user.login})</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-6">
					<button id="viewPlannedDate" type="button" class="btn btn-info"
						data-toggle="popover" data-trigger="focus" title="Planned date"
						data-content="">View Future Plans</button>
				</div>
			</div>
			<br />
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<form role="form" class="form-horizontal form-groups-bordered">
						<div class="form-group">
							<div class="col-sm-3">
								<div class="input-group input-append date" id="datePicker">
									<input type="text" class="form-control" id="txtFromDate"
										placeholder="Select From Date" style="background-color: #fff;"
										readonly="readonly" /> <span class="input-group-addon add-on"><span
										class="glyphicon glyphicon-calendar"
										onclick="showDatePicker()"></span></span>
								</div>
							</div>
							<div class="col-sm-3">
								<div class="input-group input-append date" id="datePicker">
									<input type="text" class="form-control" id="txtToDate"
										placeholder="Select To Date" style="background-color: #fff;"
										readonly="readonly" /> <span class="input-group-addon add-on"><span
										class="glyphicon glyphicon-calendar"
										onclick="showDatePicker()"></span></span>
								</div>
							</div>
							<div class="col-sm-1">
								<button type="button" class="btn btn-info" id="btnApply">Apply</button>
							</div>
						</div>
						<div class="col-md-5">
							<br /> <br />
							<!-- error message -->
							<div class="alert alert-dismissible" role="alert"
								style="display: none;">
								<button type="button" class="close"
									onclick="$('.alert').hide();" aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<p></p>
							</div>
						</div>
					</form>

				</div>
			</div>
			<br />
			<div>
				<table id="tblPlanning" class="table table-condensed"
					style="border-collapse: collapse;">
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th>Plan Date</th>
							<th>Copy to date</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>

					</tbody>
				</table>
			</div>
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
</body>
<script type="text/javascript">
	var contextPath = location.protocol + '//' + location.host;
	$(document).ready(
			function() {
				//date range picker
				/* $("#txtToDate").datepicker();
					$("#txtFromDate").datepicker(); */
				$("#txtToDate").datepicker({
					dateFormat : 'yy-mm-dd',

				}).datepicker("setDate", new Date());
				$("#txtFromDate").datepicker({
					dateFormat : 'yy-mm-dd',

				}).datepicker("setDate", new Date());

				$('.plans-daterange i').click(function() {
					$(this).parent().find('input').click();
				});

				$('#btnApply').click(
						function() {
							if ($('#sbUsers').val() != "-1") {
								if ($("#txtFromDate").val() == ""
										|| $("#txtToDate").val() == "") {
									return;
								}

								loadPlannedUserTasksBetween($("#txtFromDate")
										.val(), $("#txtToDate").val())
							} else {
								alert("Please select a user.");
							}
						});
				$('#sbUsers').change(function() {
					loadPlannedUserTasks($(this).val());
					//reset plan date
					$('#tblPlanning tbody').html("");
				});
			});

	function showDatePicker() {
		$("#fromDate").datepicker();
	}
	
	function copyUserTasks(el) {
		$('.alert').hide();
		$(el).attr('disabled','disabled');
		var targetId = $(el).closest('tr').find('td:first-child').attr(
				'data-target').slice(1);
		var selectedDate = $(el).closest('tr').find(
				'td input[name="copytoDate"]').val();
		var copyToDate = moment(selectedDate, 'MM/DD/YYYY', true).format(
				"YYYY-MM-DD");
		var userPid = $('#sbUsers').val();
		if (userPid == "-1") {
			alert("Please select user");
			$(el).removeAttr('disabled');
			return;
		}
		if (copyToDate === "" || copyToDate == "Invalid date") {
			alert("Invalid copy to date");
			$(el).removeAttr('disabled');
			return;
		}
		var planDate = $(el).closest('tr').find('td:nth-child(2)').attr('title');
		if(planDate == copyToDate) {
			alert("Plan date and copy date are equal");
			$(el).removeAttr('disabled');
		}
		var selectedTasks = [];
		$('#tblPlanning').find('div[id="' + targetId + '"]').find(
				'table tbody tr').each(function() {
			if ($(this).find('input[type="checkbox"]').prop('checked')) {
				selectedTasks.push($(this).attr('id'));
			}
		});
		$.ajax({
			url : contextPath + '/web/copy-task-plan',
			method : 'POST',
			data : $.param({
				userPid : userPid,
				copytoDate : copyToDate,
				taskPlanPids : selectedTasks.join(',')
			}),
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			success : function(response) {
				$('.alert').addClass('alert-success');
				$(".alert > p").html("Tasks successfully copied");
				$('.alert').show();
				$(el).removeAttr('disabled');
			},
			error : function(xhr, error) {
				console.log("error loading user planned tasks.");
				$('.alert').addClass('alert-danger');
				$(".alert > p").html(error.responseText);
				$('.alert').show();
				$(el).removeAttr('disabled');
			}
		});
	}
	
	function deleteUserTasks(el){
		$(el).attr('disabled','disabled');
		var userPid = $('#sbUsers').val();
		if (userPid == "-1") {
			alert("Please select user");
			$(el).removeAttr('disabled');
			return;
		}
		var planDate = $(el).closest('tr').find('td:nth-child(2)').attr('title');
		var formattedDate = $(el).closest('tr').find('td:nth-child(2)').html();
		if(confirm("Task plan in '"+ formattedDate +"' will be deleted and cannot be reverted. Please confirm?")){
			$.ajax({
				url : contextPath + '/web/delete-task-plan',
				method : 'POST',
				data : $.param({
					userPid : userPid,
					plannedDate : planDate
				}),
				contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
				success : function(response) {
					$('.alert').addClass('alert-success');
					$(".alert > p").html("Tasks successfully deleted");
					$('.alert').show();
					$(el).removeAttr('disabled');
				},
				error : function(xhr, error) {
					console.log("Error deleting task plan.");
					$('.alert').addClass('alert-danger');
					$(".alert > p").html(error.responseText);
					$('.alert').show();
					$(el).removeAttr('disabled');
				}
			});
		}else {
			$(el).removeAttr('disabled');
		}
		
	}

	function planningAllCheckBoxChanged(el) {
		$(el).closest('table').find('tbody tr td input[type="checkbox"]').prop(
				'checked', $(el).prop('checked'));
	}

	function loadPlannedUserTasksBetween(startDate, endDate) {
		var userPid = $('#sbUsers').val();
		$('#tblPlanning tbody').html("Loading....");
		$
				.ajax({
					url : contextPath + '/web/planned-user-tasks?userPid='
							+ userPid + '&startDate=' + startDate + '&endDate='
							+ endDate,
					method : 'GET',
					ContentType : 'json',
					success : function(response) {
						var html = "";
						var index = 0;
						$
								.each(
										response,
										function(key, value) {
											index += 1;
											html += '<tr>'
													+ '<td data-toggle="collapse" data-target="#'+ index +'" class="accordion-toggle"><button class="btn btn-default btn-xs"><span class="glyphicon glyphicon-chevron-down"></span></button></td>'
													+ '<td title=\"'+ moment(key).format('YYYY-MM-DD') + '\">'
													+ moment(key).format(
															'DD MMM YYYY')
													+ '</td>'
													+ '<td><input type="text" name="copytoDate" /></td>'
													+ '<td><button type="button" class="btn btn-blue" onclick="copyUserTasks(this);">Copy</button>'
													+ '&nbsp;&nbsp;<button type="button" class="btn btn-default" onclick="deleteUserTasks(this);">Delete</button></td>'
													+ '</tr>';
											if (value.length > 0) {
												html += '<tr>'
														+ '<td colspan="12" class="hiddenRow">'
														+ '<div class="accordian-body collapse" id="'+ index +'">'
														+ '<table class="table table-striped">'
														+ '<thead><tr><th><label><input type="checkbox" checked="checked" onchange="planningAllCheckBoxChanged(this);" class="allcheckbox" value="">All</label></th><th>Activity</th><th>Account</th><th>Remarks</th></tr></thead>'
														+ '<tbody>';
												$
														.each(
																value,
																function(index,
																		value) {
																	html += '<tr id="'+ value.pid +'">'
																			+ '<td><input checked="checked" type="checkbox" /></td>'
																			+ '<td>'
																			+ value.activityName
																			+ '</td>'
																			+ '<td>'
																			+ value.accountProfileName
																			+ '</td>'
																			+ '<td>'
																			+ value.remarks
																			+ '</td>'
																			+ '</tr>';
																});
												html += '</tbody></table></div></td></tr>';
											}
										});
						$('#tblPlanning tbody').html(html);
						//initilaize daterengepicker
						$('input[name="copytoDate"]').daterangepicker({
							singleDatePicker : true,
							minDate : new Date()
						});
						$('input[name="copytoDate"]').val('');
					},
					error : function() {
						console.log("error loading user planned tasks.");
					}
				});
	}

	function loadPlannedUserTasks(userPid) {
		$.ajax({
			url : contextPath + '/web/planned-user-tasks?userPid=' + userPid,
			method : 'GET',
			ContentType : 'json',
			success : function(response) {
				var planedDate = "";
				$.each(response, function(index, value) {
					planedDate += moment(value).format('DD MMM YYYY') + " , ";
				});
				$('#viewPlannedDate').attr('data-content', planedDate);
			},
			error : function() {
				console.log("error loading user planned tasks.");
			}
		});
	}
</script>
<%-- <spring:url value="/resources/assets/js/jquery-ui.js" var="jqueryUI"></spring:url>
<script type="text/javascript" src="${jqueryUI}"></script>
 --%>
<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
<script type="text/javascript" src="${momentJs}"></script>


</html>