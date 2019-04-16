<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Vehicle Fare</title>
<style type="text/css">
	tbody tr{
		text-align: center;
	}
</style>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Vehicle Fare</h2>
			<div class="row col-xs-12">
				<div class="pull-right">
					<button type="button" class="btn btn-success"
						onclick="DistanceFare.showModalPopup($('#myModal'));">Create
						new Vehicle Fare</button>
				</div>
			</div>
			<br> <br>
			<div class="clearfix"></div>
			<hr />
			<table class="table  table-striped table-bordered">
				<thead>
					<tr>
						<th>Vehicle</th>
						<th>Fare</th>
						<th>Actions</th>
					</tr>
				</thead>
				
				<tbody id="tbody_distanceFare">
					<c:if test="${empty distaceFare}">
					<tr><td colspan=3>No Data Available</td></tr>
					</c:if>
					<c:forEach items="${distaceFare}" var="distaceFare"
						varStatus="loopStatus">
						<tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}">
							<td>${distaceFare.vehicleType}</td>
							<td>${distaceFare.fare == null ? "" : distaceFare.fare}</td>
							<td>
								<button type="button" class="btn btn-blue"
									onclick="DistanceFare.showModalPopup($('#myModal'),'${distaceFare.pid}',1);">Edit</button>
								<button type="button" class="btn btn-danger"
									onclick="DistanceFare.showModalPopup($('#deleteModal'),'${distaceFare.pid}',2);">Delete</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
			
			<spring:url value="/web/distanceFare" var="urlDistanceFare"></spring:url>

			<form id="distanceFareForm" role="form" method="post"
				action="${urlDistanceFare}">
				<!-- Model Container-->
				<div class="modal fade container" id="myModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="myModalLabel">Create or edit a
									Vehicle Fare</h4>
							</div>
							<div class="modal-body">
								<div class="alert alert-danger alert-dismissible" role="alert"
									style="display: none;">
									<button type="button" class="close"
										onclick="$('.alert').hide();" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<p></p>
								</div>

								<div class="modal-body" style="overflow: auto;">
									<div class="form-group">
										<label class="control-label" for="field_vehicleType">Vehicle Type</label> <input
											autofocus="autofocus" type="text" class="form-control"
											name="vehicleType" id="field_vehicleType" maxlength="255"
											placeholder="Vehicle Type" />
									</div>
									<div class="form-group">
										<label class="control-label" for="field_fare">Fare</label> <input
											type="text" class="form-control" name="fare"
											id="field_fare" maxlength="55" placeholder="Fare/Km" />
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button id="myFormSubmit" class="btn btn-primary">Save</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>
			
			<form id="deleteForm" name="deleteForm" action="${urlDistanceFare}">
				<!-- Model Container-->
				<div class="modal fade container" id="deleteModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title">Confirm delete operation</h4>
							</div>
							<div class="modal-body">

								<div class="modal-body" style="overflow: auto;">
									<!-- error message -->
									<div class="alert alert-danger alert-dismissible" role="alert"
										style="display: none;">
										<button type="button" class="close"
											onclick="$('.alert').hide();" aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<p></p>
									</div>
									<p>Are you sure you want to delete this Vehicle Fare?</p>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal">Cancel</button>
									<button class="btn btn-danger">Delete</button>
								</div>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.Model Container-->
			</form>
			
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	
	<spring:url value="/resources/app/distanceFare.js" var="distanceFareJs"></spring:url>
	<script type="text/javascript" src="${distanceFareJs}"></script>
	
</body>
</html>