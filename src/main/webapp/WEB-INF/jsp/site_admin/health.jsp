<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Health</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Health Checks</h2>
			<p>
				<button type="button" class="btn btn-primary"
					onclick="checkHealth();">
					<span class="glyphicon glyphicon-refresh"></span>&nbsp;<span>Refresh</span>
				</button>
			</p>
			<div class="clearfix"></div>
			<hr />
			<table id="healthCheck"
				class="table table-condensed table-striped table-bordered table-responsive">
				<thead>
					<tr>
						<th class="col-md-7">Service Name</th>
						<th class="col-md-2 text-center">Status</th>
						<th class="col-md-2 text-center">Details</th>
					</tr>
				</thead>
				<tbody>

				</tbody>
			</table>
			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>

			<form name="viewForm" role="form">
				<!-- Model Container-->
				<div class="modal fade container" id="healthDetailsModal">
					<!-- model Dialog -->
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-label="Close">
									<span aria-hidden="true">&times;</span>
								</button>
								<h4 class="modal-title" id="showHealthLabel">
									
								</h4>
							</div>
							<div class="modal-body">
								<div class="modal-body pad" style="overflow: auto;">
									<div>
										<h4>Properties</h4>
										<table id="tblProperties" class="table table-striped">
											<thead>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<div class="modal-footer">
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

		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>
	<script type="text/javascript">
		var loc = location.protocol + '//' + location.host;
		var vm = this;

		vm.getLabelClass = getLabelClass;
		vm.showHealthDetails = showHealthDetails;
		vm.baseName = getBaseName;
		vm.subSystemName = getSubSystemName;

		$(document).ready(function() {
			checkHealth();
		});

		function getLabelClass(statusState) {
			if (statusState === 'UP') {
				return 'label-success';
			} else {
				return 'label-danger';
			}
		}

		function checkHealth() {
			//clear table
			$('#healthCheck').html('');
			var url = loc + "/management/health";
			$.ajax({
				url : url,
				method : 'GET',
				success : function(response) {
					console.log(response);
					vm.healthData = transformHealthData(response);
					showHealthData();
				},
				error : function(xhr, error) {
					console.log(error);
				}
			});
		}

		function showHealthDetails(health) {
			$('#showHealthLabel').append('<span class="text-capitalize">' + vm.baseName(health.name) + '</span>'
					+ vm.subSystemName(health.name));
			$.each(health.details, function(key, value) {
				$('#tblProperties').append('<tr>'
						+ '<td class="col-md-6 text-left">'+ key +'</td>'
						+ '<td class="col-md-6 text-left">'+ value +'</td>'
						+'</tr>');
			});
			
			$('#healthDetailsModal').modal('show');
		}

		function showHealthData() {
			vm.healthData
					.forEach(function(health) {
						$('#healthCheck')
								.append(
										'<tr>'
												+ '<td><span class="text-capitalize">'
												+ vm.baseName(health.name)
												+ '</span>'
												+ vm.subSystemName(health.name)
												+ '</td>'
												+ '<td class="text-center"><span class="label '
												+ vm
														.getLabelClass(health.status)
												+ '">'
												+ health.status
												+ '</span></td>'
												+ '<td class="text-center"><button type="button" class="btn btn-blue" onclick="showHealthDetails();">View</button></td>'
												+'</tr>');
					});
		}

		function transformHealthData(data) {
			var response = [];
			flattenHealthData(response, null, data);
			return response;
		}

		function getBaseName(name) {
			if (name) {
				var split = name.split('.');
				return split[0];
			}
		}

		function getSubSystemName(name) {
			if (name) {
				var split = name.split('.');
				split.splice(0, 1);
				var remainder = split.join('.');
				return remainder ? ' - ' + remainder : '';
			}
		}

		function flattenHealthData(result, path, data) {
			$.each(data, function(key, value) {
				if (value !== null && typeof value === 'object') {
					if (isHealthObject(value)) {
						addHealthObject(result, true, value, getModuleName(
								path, key));
					}
				}
			});
			return result;
		}

		function addHealthObject(result, isLeaf, healthObject, name) {
			var healthData = {
				'name' : name
			};
			var details = {};
			var hasDetails = false;

			$.each(healthObject, function(key, value) {
				if (key === 'status' || key === 'error') {
					healthData[key] = value;
				} else {
					if (value !== null && typeof value === 'object') {
						if (!isHealthObject(value)) {
							details[key] = value;
							hasDetails = true;
						}
					}
				}
			});

			// Add the of the details
			if (hasDetails) {
				$.extend(healthData, {
					'details' : details
				});
			}

			// Only add nodes if they provide additional information
			if (isLeaf || hasDetails || healthData.error) {
				result.push(healthData);
			}

			//console.log(healthData);
			return healthData;
		}

		function getModuleName(path, name) {
			var result;
			if (path && name) {
				result = path + separator + name;
			} else if (path) {
				result = path;
			} else if (name) {
				result = name;
			} else {
				result = '';
			}
			return result;
		}

		function isHealthObject(healthObject) {
			var result = false;
			if (healthObject) {
				$.each(healthObject, function(key, value) {
					if (key === 'status') {
						result = true;
					}
				});
			}
			return result;
		}
	</script>
</body>
</html>