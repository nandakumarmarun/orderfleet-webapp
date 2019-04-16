<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Authentication Audits</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Authentication Audits</h2>
			<div class="row">
				<div class="col-md-5">
					<h4>Filter by date</h4>
					<p class="input-group">
						<span class="input-group-addon">from</span>
						<input type="date" class="input-sm form-control" id="fromDate" name="start" value="${defaultFromDate}"
						 	required /> <span class="input-group-addon">to</span> <input type="date"
							class="input-sm form-control" id="toDate" name="end" value="${defaultToDate}" required />
					</p>
					 <button class="btn btn-info " onclick="loadAuthenticationAudits();">Load</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<hr />
			<table
				class="table table-condensed table-striped table-bordered table-responsive">
				<thead>
					<tr>
						<th>Date</th>
						<th>User</th>
						<th>Password</th>
						<th>IP Address</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${authenticationAudits.content}" var="audit"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td><javatime:format value="${audit.auditEventDate}" style="MS" /></td>
							<td>${audit.login}</td>
							<td>${audit.password}</td>
							<td><span><span>Remote Address : </span> ${audit.ipAddress}</span></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="row-fluid">
				<util:pagination thispage="${authenticationAudits}"></util:pagination>
			</div>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<script type="text/javascript">
		function loadAuthenticationAudits(){
			var auditContextPath = location.protocol + '//' + location.host + location.pathname;
			location.href = auditContextPath + '?fromDate=' + $('#fromDate').val() + '&toDate=' +$('#toDate').val()
		}
	</script>
</body>
</html>