<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<title>SalesNrich | 403</title>
</head>
<body>
	<!-- Page Content -->
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<h3 class="page-head-line">Access is denied</h3>
				<hr />
			</div>
		</div>
		<div class="row">
			<div class="col-lg-12 text-center">
				<div class="panel panel-default">
					<div class="panel-body">
						<c:choose>
							<c:when test="${empty username}">
								<h2 style="color: red;">You do not have permission to
									access this page!</h2>
							</c:when>
							<c:otherwise>
								<h2>Username : ${username}</h2>
								<h3 style="color: red;">You do not have permission to
									access this page!</h3>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
		<!-- /.row -->
	</div>
	<!-- /.container -->
</body>
</html>
