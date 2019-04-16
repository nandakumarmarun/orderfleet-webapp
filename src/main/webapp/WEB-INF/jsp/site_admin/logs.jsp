<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Logs</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Logs</h2>
			<p>There are ${fn:length(loggers)} loggers.</p>
			<span>Filter</span> <input id="ofKeyupSearch" type="text" class="form-control" />

			<table
				class="table table-condensed table-striped table-bordered table-responsive ofKeyupTable">
				<thead>
					<tr title="click to order">
						<th><span>Name</span></th>
						<th><span>Level</span></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${loggers}" var="logger" varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td><small>${logger.name}</small></td>
							<td>
								<button
									class="btn btn-default btn-xs ${(logger.level=='TRACE') ? 'btn-danger' : 'btn-default'}">TRACE</button>
								<button
									class="btn btn-default btn-xs ${(logger.level=='DEBUG') ? 'btn-warning' : 'btn-default'}">DEBUG</button>
								<button
									class="btn btn-default btn-xs ${(logger.level=='INFO') ? 'btn-info' : 'btn-default'}">INFO</button>
								<button
									class="btn btn-default btn-xs ${(logger.level=='WARN') ? 'btn-success' : 'btn-default'}">WARN</button>
								<button
									class="btn btn-default btn-xs ${(logger.level=='ERROR') ? 'btn-primary' : 'btn-default'}">ERROR</button>
							</td>

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
		
	</script>
</body>
</html>