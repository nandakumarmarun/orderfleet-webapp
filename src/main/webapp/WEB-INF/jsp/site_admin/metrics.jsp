<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Metrics</title>
</head>
<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<h2>Application Metrics</h2>
			<p>
				<button type="button" class="btn btn-primary" onclick="refresh();">
					<span class="glyphicon glyphicon-refresh"></span>&nbsp;<span>Refresh</span>
				</button>
			</p>
			<h3>JVM Metrics</h3>
			<div class="row">
				<div class="col-md-4">
					<b>Memory</b>
					<p id="totalMemory">
						<span>Total Memory</span>
					</p>
					<div class="progress">
						<div id="totalMemoryBar"
							class="progress-bar progress-bar-success progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
					<p id="heapMemory">
						<span>Heap Memory</span>
					</p>
					<div class="progress">
						<div id="heapMemoryBar"
							class="progress-bar progress-bar-success progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
					<p id="nonHeapMemory">
						<span>Non-Heap Memory</span>
					</p>
					<div class="progress">
						<div id="nonHeapMemoryBar"
							class="progress-bar progress-bar-success progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
				</div>
				<div class="col-md-4">
					<b>Threads</b> <span id="threads"></span> <a class="hand"
						onclick="refreshThreadDumpData();" data-toggle="modal"
						data-target="#threadDump"><i class=""></i></a>
					<p id="runnable">
						<span>Runnable</span>
					</p>
					<div class="progress">
						<div id="runnableBar"
							class="progress-bar progress-bar-success progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
					<p id="timedWaiting">
						<span>Timed Waiting</span>

					</p>
					<div class="progress">
						<div id="timedWaitingBar"
							class="progress-bar progress-bar-warning progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
					<p id="waiting">
						<span>Waiting</span>

					</p>
					<div class="progress">
						<div id="waitingBar"
							class="progress-bar progress-bar-warning progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
					<p id="blocked">
						<span>Blocked</span>
					</p>
					<div class="progress">
						<div id="blockedBar"
							class="progress-bar progress-bar-warning progress-bar-striped active"
							role="progressbar" aria-valuenow="" aria-valuemin="0"
							aria-valuemax="" style="width: 60%">
							<span></span>
						</div>
					</div>
				</div>
				<div class="col-md-4">
					<b>Garbage collections</b>
					<div class="row">
						<div class="col-md-9">Mark Sweep count</div>
						<div class="col-md-3 text-right" id="markSweepCount">{{vm.metrics.gauges['jvm.garbage.PS-MarkSweep.count'].value}}</div>
					</div>
					<div class="row">
						<div class="col-md-9">Mark Sweep time</div>
						<div class="col-md-3 text-right" id="markSweepTime"></div>
					</div>
					<div class="row">
						<div class="col-md-9">Scavenge count</div>
						<div class="col-md-3 text-right" id="scavengeCount"></div>
					</div>
					<div class="row">
						<div class="col-md-9">Scavenge time</div>
						<div class="col-md-3 text-right" id="scavengeTime"></div>
					</div>
				</div>
			</div>

			<h3>HTTP requests (events per second)</h3>
			<p id="httpRequest">
				<span>Active requests</span> <b></b> - <span>Total requests</span> <b></b>
			</p>
			<div class="table-responsive">
				<table class="table table-striped" id="tblHttpRequest">
					<thead>
						<tr>
							<th>Code</th>
							<th>Count</th>
							<th class="text-right">Mean</th>
							<th class="text-right"><span>Average</span> (1 min)</th>
							<th class="text-right"><span>Average</span> (5 min)</th>
							<th class="text-right"><span>Average</span> (15 min)</th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
			</div>

			<h3>Services statistics (time in millisecond)</h3>
			<div class="table-responsive">
				<table id="tblServicesStatistics" class="table table-striped">
					<thead>
						<tr>
							<th>Service name</th>
							<th class="text-right">Count</th>
							<th class="text-right">Mean</th>
							<th class="text-right">Min</th>
							<th class="text-right">p50</th>
							<th class="text-right">p75</th>
							<th class="text-right">p95</th>
							<th class="text-right">p99</th>
							<th class="text-right">Max</th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
			</div>

			<h3>DataSource statistics (time in millisecond)</h3>
			<div class="table-responsive">
				<table id="tblDSStatistics" class="table table-striped">
					<thead>
						<tr>
							<th><span>Usage</span>&nbsp;&nbsp;<span id="dsUsage"></span></th>
							<th class="text-right">Count</th>
							<th class="text-right">Mean</th>
							<th class="text-right">Min</th>
							<th class="text-right">p50</th>
							<th class="text-right">p75</th>
							<th class="text-right">p95</th>
							<th class="text-right">p99</th>
							<th class="text-right">Max</th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
			</div>

			<hr />
			<!-- Footer -->
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<script type="text/javascript">
		var loc = location.protocol + '//' + location.host;
		var vm = this;

		vm.cachesStats = {};
        vm.metrics = {};
        vm.refresh = refresh;
        vm.refreshThreadDumpData = refreshThreadDumpData;
        vm.servicesStats = {};
        vm.updatingMetrics = true;

		$(document).ready(function() {
			getMetrics();
			//getThreadDump();
		});
		
		function refresh () {
			window.location = loc + "/web/management/metrics";
		}
		
		function refreshThreadDumpData () {
			console.log("thread dump view button clicked");
		}

		function getMetrics() {
			var url = loc + "/orderfleet/metrics";
			$.ajax({
				url : url,
				method : 'GET',
				success : function(response) {
					vm.metrics = response;
					
					updateJVMMetrixUI();
					updateHTTPRequestsUI();
					updateServicesStatisticsUI();
					updateDataSourceStatisticsUI();
				},
				error : function(xhr, error) {
					console.log(error);
				}
			});
		}

		function getThreadDump() {
			var url = loc + "/management/dump";
			$.ajax({
				url : url,
				method : 'GET',
				success : function(response) {
					console.log("========================dump data=======================");
					console.log(response);
				},
				error : function(xhr, error) {
					console.log(error);
				}
			});
		}
		
		function updateJVMMetrixUI() {
			//total memory
			$("#totalMemory").append("(" + Number(vm.metrics.gauges['jvm.memory.total.used'].value / 1000000).toFixed(0) + "M / " + Number(vm.metrics.gauges['jvm.memory.total.max'].value / 1000000).toFixed(0) + "M)");
			$("#totalMemoryBar").attr('aria-valuenow', vm.metrics.gauges['jvm.memory.total.used'].value);
			$("#totalMemoryBar").attr('aria-valuemax', vm.metrics.gauges['jvm.memory.total.max'].value);
			$("#totalMemoryBar span").text(Number(vm.metrics.gauges['jvm.memory.total.used'].value * 100 / vm.metrics.gauges['jvm.memory.total.max'].value).toFixed(0) + "%");
			//heap memory
			$("#heapMemory").append("(" + Number(vm.metrics.gauges['jvm.memory.heap.used'].value / 1000000).toFixed(0) + "M / " + Number(vm.metrics.gauges['jvm.memory.heap.max'].value / 1000000).toFixed(0) + "M)");
			$("#heapMemoryBar").attr('aria-valuenow', vm.metrics.gauges['jvm.memory.heap.used'].value);
			$("#heapMemoryBar").attr('aria-valuemax', vm.metrics.gauges['jvm.memory.heap.max'].value);
			$("#heapMemoryBar span").text(Number(vm.metrics.gauges['jvm.memory.heap.used'].value * 100 / vm.metrics.gauges['jvm.memory.heap.max'].value).toFixed(0) + "%");
			//non-heap memory
			$("#nonHeapMemory").append("(" + Number(vm.metrics.gauges['jvm.memory.non-heap.used'].value / 1000000).toFixed(0) + "M / " + Number(vm.metrics.gauges['jvm.memory.non-heap.committed'].value / 1000000).toFixed(0) + "M)");
			$("#nonHeapMemoryBar").attr('aria-valuenow', vm.metrics.gauges['jvm.memory.non-heap.used'].value);
			$("#nonHeapMemoryBar").attr('aria-valuemax', vm.metrics.gauges['jvm.memory.non-heap.committed'].value);
			$("#nonHeapMemoryBar span").text(Number(vm.metrics.gauges['jvm.memory.non-heap.used'].value * 100 / vm.metrics.gauges['jvm.memory.non-heap.committed'].value).toFixed(0) + "%");
			
			//Threads and runnable
			$("#threads").text("(Total: "+ vm.metrics.gauges['jvm.threads.count'].value + ")");
			$("#runnable").append(vm.metrics.gauges['jvm.threads.runnable.count'].value);
			$("#runnableBar").attr('aria-valuenow', vm.metrics.gauges['jvm.threads.runnable.count'].value);
			$("#runnableBar").attr('aria-valuemax', vm.metrics.gauges['jvm.threads.count'].value);
			$("#runnableBar span").text(Number(vm.metrics.gauges['jvm.threads.runnable.count'].value * 100 / vm.metrics.gauges['jvm.threads.count'].value).toFixed(0) + "%");
			//Timed Waiting
			$("#timedWaiting").append(vm.metrics.gauges['jvm.threads.timed_waiting.count'].value);
			$("#timedWaitingBar").attr('aria-valuenow', vm.metrics.gauges['jvm.threads.timed_waiting.count'].value);
			$("#timedWaitingBar").attr('aria-valuemax', vm.metrics.gauges['jvm.threads.count'].value);
			$("#timedWaitingBar span").text(Number(vm.metrics.gauges['jvm.threads.timed_waiting.count'].value * 100 / vm.metrics.gauges['jvm.threads.count'].value).toFixed(0) + "%");
			//waiting
			$("#waiting").append(vm.metrics.gauges['jvm.threads.waiting.count'].value);
			$("#waitingBar").attr('aria-valuenow', vm.metrics.gauges['jvm.threads.waiting.count'].value);
			$("#waitingBar").attr('aria-valuemax', vm.metrics.gauges['jvm.threads.count'].value);
			$("#waitingBar span").text(Number(vm.metrics.gauges['jvm.threads.waiting.count'].value * 100 / vm.metrics.gauges['jvm.threads.count'].value).toFixed(0) + "%");
			//blocked
			$("#blocked").append(vm.metrics.gauges['jvm.threads.blocked.count'].value);
			$("#blockedBar").attr('aria-valuenow', vm.metrics.gauges['jvm.threads.blocked.count'].value);
			$("#blockedBar").attr('aria-valuemax', vm.metrics.gauges['jvm.threads.count'].value);
			$("#blockedBar span").text(Number(vm.metrics.gauges['jvm.threads.blocked.count'].value * 100 / vm.metrics.gauges['jvm.threads.count'].value).toFixed(0) + "%");
			
			//Garbage collections
			$("#markSweepCount").text(vm.metrics.gauges['jvm.garbage.PS-MarkSweep.count'].value);
			$("#markSweepTime").text(vm.metrics.gauges['jvm.garbage.PS-MarkSweep.time'].value +"ms");
			$("#scavengeCount").text(vm.metrics.gauges['jvm.garbage.PS-Scavenge.count'].value);
			$("#scavengeTime").text(vm.metrics.gauges['jvm.garbage.PS-Scavenge.time'].value + "ms");
		}
		
		//Http Request
		function updateHTTPRequestsUI() {
			$('#httpRequest b').eq(0).text(Number(vm.metrics.counters['com.codahale.metrics.servlet.InstrumentedFilter.activeRequests'].count).toFixed(0));
			$('#httpRequest b').eq(1).text(Number(vm.metrics.timers['com.codahale.metrics.servlet.InstrumentedFilter.requests'].count).toFixed(2));
			$('#tblHttpRequest').append('<tr>'
						+ '<td>OK</td>'
						+ '<td>'+ vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].count +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].mean_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m1_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m5_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.ok'].m15_rate).toFixed(2) +'</td>'
						+'</tr>').append('<tr>'
						+ '<td>Not Found</td>'
						+ '<td>'+ vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].count +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].mean_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m1_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m5_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.notFound'].m15_rate).toFixed(2) +'</td>'
						+'</tr>').append('<tr>'
						+ '<td>Server error</td>'
						+ '<td>'+ vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].count +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].mean_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m1_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m5_rate).toFixed(2) +'</td>'
						+ '<td class="text-right">'+ Number(vm.metrics.meters['com.codahale.metrics.servlet.InstrumentedFilter.responseCodes.serverError'].m15_rate).toFixed(2) +'</td>'
						+'</tr>');
		}
		
		//Services statistics (time in millisecond)
		function updateServicesStatisticsUI(){
			vm.servicesStats = {};
            vm.cachesStats = {};
			$.each(vm.metrics.timers, function(key, value) {
				if (key.indexOf('web.rest') !== -1 || key.indexOf('service') !== -1) {
                    vm.servicesStats[key] = value;
                }
				if (key.indexOf('net.sf.ehcache.Cache') !== -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf('.');
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf('.');
                    vm.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                }
			});
			
			$.each(vm.servicesStats, function(key, value) {
				$('#tblServicesStatistics').append('<tr>'
						+ '<td>'+ key +'</td>'
						+ '<td class="text-right">'+ value.count +'</td>'
						+ '<td class="text-right">'+ Number(value.mean * 1000).toFixed(0) +'</td>'
						+ '<td class="text-right">'+ Number(value.min * 1000).toFixed(0) +'</td>'
						+ '<td class="text-right">'+ Number(value.p50 * 1000).toFixed(0) +'</td>'
						+ '<td class="text-right">'+ Number(value.p75 * 1000).toFixed(0) +'</td>'
						+ '<td class="text-right">'+ Number(value.p95 * 1000).toFixed(0) +'</td>'
						+ '<td class="text-right">'+ Number(value.p99 * 1000).toFixed(0) +'</td>'
						+ '<td class="text-right">'+ Number(value.max * 1000).toFixed(0) +'</td>'
						+'</tr>');
			});
		}
		
		//DataSource statistics (time in millisecond)
		function updateDataSourceStatisticsUI(){
			$('#dsUsage').text(vm.metrics.gauges['HikariPool-1.pool.ActiveConnections'].value + "/" + vm.metrics.gauges['HikariPool-1.pool.TotalConnections'].value);
			$('#tblDSStatistics').append('<tr>'
					+ '<td class="text-right">'+ Number(vm.metrics.gauges['HikariPool-1.pool.ActiveConnections'].value * 100 / vm.metrics.gauges['HikariPool-1.pool.TotalConnections'].value).toFixed(0) +'%</td>'
					+ '<td class="text-right">'+ vm.metrics.histograms['HikariPool-1.pool.Usage'].count +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].mean).toFixed(2) +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].min).toFixed(2) +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].p50).toFixed(2) +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].p75).toFixed(2) +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].p95).toFixed(2) +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].p99).toFixed(2) +'</td>'
					+ '<td class="text-right">'+ Number(vm.metrics.histograms['HikariPool-1.pool.Usage'].max).toFixed(2) +'</td>'
					+'</tr>');
		}
		
	</script>
</body>
</html>