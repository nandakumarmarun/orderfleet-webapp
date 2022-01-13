<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<title>SalesNrich |Summary of company</title>
<style type="text/css">
.error {
	color: red;
}

@media ( min-width : 1360px) and (max-width: 1370px) {
	.width83 {
		width: 83%
	}
}
</style>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<h2>Summary of company</h2>
			<div class="clearfix"></div>
			<hr />

			
			<table class="table">
				<thead>
					<tr>
						<th>Active Company Name</th>
						<th>Active Company Count</th>
						<th>DeactiveCompany Name</th>
						<th>DeactiveCompany Count</th>
					
					</tr>
				</thead>
				
				 <tr>
            <td>
                <table>
                    <c:forEach var="activename" items="${companyActiveName}">
                        <tr>
                            <td>${activename.companyName}</td>
                        </tr>
                    </c:forEach>
                </table>
            </td>
           <td>${companyActive} </td>
            <td>
                <table>
                    <c:forEach var="deactivename" items="${companyDeactiveName}">
                        <tr>
                            <td>${deactivename.companyName}</td>
                        </tr>
                    </c:forEach>
                </table>

            </td>
			<td>${companyDeactive}</td>	
        </tr>


				
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<spring:url value="/web/company-count" var="urlCompanyCount"></spring:url>


		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	
</body>
</html>