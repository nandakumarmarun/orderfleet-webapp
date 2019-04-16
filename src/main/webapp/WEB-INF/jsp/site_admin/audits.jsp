<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Audits</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Audits</h2>
			<div class="row">
				<div class="col-md-5">
					<h4>Filter by date</h4>
					<p class="input-group">
						<span class="input-group-addon">from</span>
						<input type="date" class="input-sm form-control" id="fromDate" name="start" value="${defaultFromDate}"
						 	required /> <span class="input-group-addon">to</span> <input type="date"
							class="input-sm form-control" id="toDate" name="end" value="${defaultToDate}" required />
					</p>
					 <button class="btn btn-info " onclick="loadAudits();">Load</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<div class="row">
				<div class="col-md-12 col-sm-12 clearfix">
					<input type="text" id="ofKeyupSearch" placeholder="Search..." class="form-control" style="float: right; width: 200px;">
				</div>
			</div>
			<table
				class="table table-condensed table-striped table-bordered table-responsive ofKeyupTable">
				<thead>
					<tr>
						<th>Date</th>
						<th>User</th>
						<th>State</th>
						<th>Extra data</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${audits}" var="audit"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td><fmt:formatDate value="${audit.timestamp}" type="both"
									dateStyle="medium" timeStyle="medium" /></td>
							<td>${audit.principal}</td>
							<td>${audit.type}</td>
							<td><span>${audit.data.message}</span> <span><span>Remote
										Address</span> ${audit.data.remoteAddress}</span></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<script type="text/javascript">
		function loadAudits(){
			var auditContextPath = location.protocol + '//' + location.host + location.pathname;
			location.href = auditContextPath + '?fromDate=' + $('#fromDate').val() + '&toDate=' +$('#toDate').val()
		}
	</script>
</body>
</html>