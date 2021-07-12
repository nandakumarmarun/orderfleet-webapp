<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>

<spring:url
	value="/resources/assets/plugin/orgchart/jquery.orgchart.css"
	var="orgchartCss"></spring:url>
<link href="${orgchartCss}" rel="stylesheet">

<style type="text/css">
#chart-container {
	width: 100%;
	height: 100%;
}
</style>

<title>SalesNrich | Locations Hierarchy Update Parent</title>
</head>
<body class="page-body" data-url="">

	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Locations Hierarchy</h2>
			<div class="row col-xs-12" id="divRootLocation">
				<div class="pull-left">
					<button type="button" class="btn btn-success"
						onclick="rootLocationButtonClicked();">Create Root
						Location</button>
				</div>
			</div>
			<div class="clearfix"></div>
			<div id="edit-panel" class="view-state">
				<span id="chart-state-panel" class="radio-panel"> <input
					type="radio" name="chart-state" id="rd-view" value="view" checked>
					&nbsp;<label for="rd-view">View</label> &nbsp; &nbsp;<input
					type="radio" name="chart-state" id="rd-edit" value="edit">
					&nbsp;<label for="rd-edit">Edit</label>
				</span>
				<div id="divEdit" class="col-md-12 col-sm-12 clearfix"
					style="display: none; padding: 1%; background: #3B5998;">
					<div class="col-sm-10">
						<label class="selected-node-group">Selected Node:</label> <input
							readonly="readonly" type="text" id="selected-node"
							class="selected-node-group"> &nbsp; <label>New
							Node:</label> <select id="field_location" name="location">
							<option value="-1">Select Location</option>
							<c:forEach items="${locations}" var="location">
								<option value="${location.id}">${location.name}</option>
							</c:forEach>
						</select> &nbsp;&nbsp; <span id="node-type-panel" class="radio-panel">
							<!-- <input type="radio" name="node-type" id="rd-child"
							value="children"> &nbsp; <label for="rd-child">Child</label>&nbsp;
							<input type="radio" name="node-type" id="rd-sibling"
							value="siblings"> &nbsp;<label for="rd-sibling">Sibling</label> -->
						</span> &nbsp;&nbsp;
						<!-- <button type="button" id="btn-add-nodes" class="btn btn-info">Add</button>
						<button type="button" id="btn-delete-nodes" class="btn btn-danger">Delete</button> -->
						<button type="button" id="btn-reset" class="btn">Reset</button>
					</div>

					<!-- <div class="col-sm-2" class="pull-right">
						<button type="button" id="btn-update" class="btn btn-success">Update
							Hierarchy</button>
					</div> -->

					<div class="col-sm-2" class="pull-right">
						<button type="button" id="btn-update-parent"
							class="btn btn-success">Update Location</button>
					</div>

				</div>
			</div>
			<div id="chart-container"></div>
			<!-- Create root location - Model Container-->
			<div class="modal fade container" id="myModal">
				<!-- model Dialog -->
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title" id="myModalLabel">Create Root
								Location</h4>
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
									<label class="control-label" for="field_name">Location</label>
									<select id="dropdownLocation"></select>
								</div>
							</div>
						</div>
						<div class="modal-footer">
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Cancel</button>
								<button onclick="this.disabled=true;saveRootLocation();"
									class="btn btn-primary">Save</button>
							</div>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.Model Container-->
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url
		value="/resources/assets/plugin/orgchart/jquery.orgchart.js"
		var="orgchartJS"></spring:url>
	<spring:url
		value="/resources/assets/plugin/orgchart/flat-to-nested-js.js"
		var="flatToNestedJS"></spring:url>

	<script type="text/javascript" src="${orgchartJS}"></script>
	<script type="text/javascript" src="${flatToNestedJS}"></script>

	<spring:url value="/resources/app/location-hierarchy.js"
		var="locationHierarchyJS"></spring:url>
	<script type="text/javascript" src="${locationHierarchyJS}"></script>

	<script type="text/javascript">
		function rootLocationButtonClicked() {
			resetForm();
			$('#myModal').modal('show');
			loadLocations();
		}

		function loadLocations() {
			$.ajax({
				method : 'GET',
				url : '${pageContext.request.contextPath}/web/locations-json',
				contentType : "application/json; charset=utf-8",
				success : function(data) {
					$("#dropdownLocation").find('option').remove();
					for (i = 0; i < data.length; i++) {
						$("#dropdownLocation").append(
								'<option value="'+ data[i].id +'">'
										+ data[i].name + '</option>');
					}
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

		function saveRootLocation() {
			$.ajax({
				method : 'POST',
				url : '${pageContext.request.contextPath}/web/root-location',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify($('#dropdownLocation option:selected')
						.val()),
				success : function(data) {
					//reloading page to see the updated data
					window.location = location.protocol + '//' + location.host
							+ '/web/location-hierarchy';
				},
				error : function(xhr, error) {
					onError(xhr, error);
				}
			});
		}

		$('#btn-update-parent').on(
				'click',
				function() {

					/* var $node = $('#selected-node').data('node');
					 
					 var hierarchy = $('#chart-container').orgchart('getHierarchy');
					 var flatData = convertToFlat(hierarchy);
					 updateHierarchy(flatData);*/

					var selectedNode = $('#selected-node').val();
					var parentId = $('#field_location').val();
					console.log("selectedNode =" + selectedNode + "parentId="
							+ parentId);
					if (selectedNode == null && parentId == null && selectedNode == "" && parentId == "") {
						alert("please select nodes");
					}

					$
							.ajax({
								url : location.protocol + '//' + location.host+ "/web/location-hierarchies-parent-update",
								type : "GET",
								data : {
									selectedNode : selectedNode,
									parentId : parentId
								},
								success : function(status) {
									window.location = location.protocol + '//'
											+ location.host
											+ '/web/location-hierarchy-update-parent';
								},
								error : function(xhr, error) {
									onError(xhr, error);
								},
							});

				});

		function onError(httpResponse, exception) {
			var i;
			switch (httpResponse.status) {
			// connection refused, server not reachable
			case 0:
				addErrorAlert('Server not reachable',
						'error.server.not.reachable');
				break;
			case 400:
				var errorHeader = httpResponse
						.getResponseHeader('X-orderfleetwebApp-error');
				var entityKey = httpResponse
						.getResponseHeader('X-orderfleetwebApp-params');
				if (errorHeader) {
					var entityName = entityKey;
					addErrorAlert(errorHeader, errorHeader, {
						entityName : entityName
					});
				} else if (httpResponse.responseText) {
					var data = JSON.parse(httpResponse.responseText);
					if (data && data.fieldErrors) {
						for (i = 0; i < data.fieldErrors.length; i++) {
							var fieldError = data.fieldErrors[i];
							var convertedField = fieldError.field.replace(
									/\[\d*\]/g, '[]');
							var fieldName = convertedField.charAt(0)
									.toUpperCase()
									+ convertedField.slice(1);
							addErrorAlert('Field ' + fieldName
									+ ' cannot be empty', 'error.'
									+ fieldError.message, {
								fieldName : fieldName
							});
						}
					} else if (data && data.message) {
						addErrorAlert(data.message, data.message, data);
					} else {
						addErrorAlert(data);
					}
				} else {
					addErrorAlert(exception);
				}
				break;
			default:
				if (httpResponse.responseText) {
					var data = JSON.parse(httpResponse.responseText);
					if (data && data.description) {
						addErrorAlert(data.description);
					} else if (data && data.message) {
						addErrorAlert(data.message);
					} else {
						addErrorAlert(data);
					}
				} else {
					addErrorAlert(exception);
				}
			}
		}

		function addErrorAlert(message, key, data) {
			$(".alert > p").html(message);
			$('.alert').show();
		}

		function resetForm() {
			$('.alert').hide();
		}
	</script>
</body>
</html>