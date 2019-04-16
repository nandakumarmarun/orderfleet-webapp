<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util"%>

<html lang="en">
<head>
<jsp:include page="../fragments/m_head.jsp"></jsp:include>
<title>SalesNrich | Dashboard</title>
</head>

<script type="text/javascript">
	jQuery(document).ready(
			function($) {

				$(".monthly-sales").sparkline(
						[ 1, 5, 6, 7, 10, 12, 16, 11, 9, 8.9, 8.7, 7, 8, 7, 6,
								5.6, 5, 7, 5, 4, 5, 6, 7, 8, 6, 7, 6, 3, 2 ], {
							type : 'bar',
							barColor : '#ff4e50',
							height : '55px',
							width : '100%',
							barWidth : 8,
							barSpacing : 1
						});

			});

	function getRandomInt(min, max) {
		return Math.floor(Math.random() * (max - min + 1)) + min;
	}
</script>



<!-- styles for the content i changed -->
<style type="text/css">
.dashboard-first-tile th, .dashboard-first-tile td {
	color: white;
	padding: 3px;
}

.dashboard-first-tile {
	height: 240px;
}

.tile-block .tile-header {
	padding: 12px;
}

.tile-block .tile-content, .tile-block.tile-aqua .tile-footer,
	.tile-block .tile-content, .tile-block.tile-green .tile-footer,
	.tile-block .tile-content, .tile-block.tile-red .tile-footer,
	.tile-block.tile-yellow .tile-footer {
	padding-top: 10px;
	padding-bottom: 10px;
}

.table_ {
	float: left;
}

.table_ .head .first p, .table_ .head .first, .table_ .head .second,
	.table_ .head .second p {
	float: left;
}

.table_ .head .first p {
	width: 100%;
}

.table_ .head .second div {
	width: 100%;
	float: left;
}

.table_ .head .second div p {
	float: left;
	width: 50%;
}

.table_ .head .first {
	width: 30%;
}

.table_ .head .second {
	width: 70%;
}

.table_ .body .first {
	width: 100%;
	float: left;
}

.table_ .body .first p {
	float: left;
	width: 32%;
	text-align: center;
}

.table_ .body .second {
	width: 100%;
	float: left;
}

.table_ .body .second p {
	float: left;
	width: 32%;
	text-align: center;
}

.table_ .body .third {
	width: 100%;
	float: left;
}

.table_ .body .third p {
	float: left;
	width: 32%;
	text-align: center;
}

.tile-footer {
	float: left;
	width: 100%;
	margin-top: 3%;
}

/*diffrent colors*/
.tile-aqua .table_ .head {
	background: #0696b9;
	float: left;
	padding: 10px 5px 0px 5px;
	width: 100%;
}

.tile-aqua .table_ .body {
	background: #15a3c5;
	float: left;
	padding: 5px;
}

.tile-green .table_ .head {
	background: #048047;
	float: left;
	padding: 10px 5px 0px 5px;
	width: 100%;
}

.tile-green .table_ .body {
	background: #0b9053;
	float: left;
	padding: 5px;
}

.tile-red .table_ .head {
	background: #ed4533;
	float: left;
	padding: 10px 5px 0px 5px;
	width: 100%;
}

.tile-red .table_ .body {
	background: #ea5342;
	float: left;
	padding: 5px;
}

.tile-yellow {
	background: #ff0;
}

.tile-yellow span, .tile-yellow p, .tile-yellow a {
	color: black !important;
}

.tile-yellow .table_ .head {
	background: #d9d900;
	float: left;
	padding: 10px 5px 0px 5px;
	width: 100%;
}

.tile-yellow .table_ .body {
	background: #e2e200;
	float: left;
	padding: 5px;
}

.profile_picture {
	float: left;
	margin-top: -28px;
}

.date_ {
	margin-top: 5px;
}

.m-t-1 {
	margin-top: 1%;
}

@media ( max-width : 768px) {
	.table_ {
		width: 100%;
	}
	.body {
		width: 100%;
	}
}

/*updated_ 05-09-2016 monday*/
.tile-footer {
	padding-bottom: 0px !important;
}

.tile-footer .address {
	width: 80%;
	float: left;
	overflow-y: hidden;
	height: 16px;
}

.tile-footer .icon {
	font-size: 24px;
	float: right;
	margin-top: -2%;
	margin-right: 3%;
}

.tile-footer .time {
	width: 16%;
	float: left;
	padding-left: 4%;
}

@media ( min-width : 1556px) {
	.height_tiles {
		height: 270px;
	}
}

@media ( min-width : 1200px) and (max-width: 1366px) {
	.col-lg-mq {
		width: 33.33333333%;
	}
	.tile-footer .address {
		width: 85%;
		float: left;
	}
	.tile-footer .icon {
		font-size: 29px;
		float: right;
		margin-top: -5%;
	}
	.tile-footer .time {
		width: 71%;
		float: left;
	}
}
</style>

<body class="page-body" data-url="">
	<div class="page-container">
		<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
		<div class="main-content">
			<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
			<hr />
			<div class="row">
				<div class="form-group col-md-3 col-md-offset-2 pull-right date_">
					<div class="col-sm-12">
						<div class="input-group">
							<input type="date" class="form-control datepicker"
								data-format="D, dd MM yyyy">

							<div class="input-group-addon">
								<a href="#"><i class="entypo-calendar"></i></a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row" style="color: white;">
				<div class="col-sm-6 col-md-5 col-lg-4 col-xs-12">
					<div class="tile-block tile-aqua">
						<div class="tile-header">
							<i class="entypo-chart-line"></i> <a href="#" class="text-center">
								19-Aug-2016 </a>
						</div>
						<div class="tile-content">
							<div class="table_">
								<div class="head">
									<div class="first">
										<p class="text-center">Description</p>
									</div>
									<div class="second">
										<div>
											<p class="text-center">Count</p>
											<p class="text-center">Amount</p>
										</div>
									</div>
								</div>
								<div class="clearfix"></div>
								<div class="body">
									<div class="first">
										<p>PJP Visit</p>
										<p>60000</p>
										<p>12000</p>
									</div>
									<div class="second">
										<p>Sales Orders</p>
										<p>54551</p>
										<p>65465</p>
									</div>
									<div class="third">
										<p>Reciepts</p>
										<p>545254</p>
										<p>484555</p>
									</div>
								</div>
							</div>
						</div>
						<div class="tile-footer"></div>
					</div>
				</div>
				<div class="col-sm-6 col-md-5 col-lg-4 col-xs-12">
					<div class="tile-block tile-green">
						<div class="tile-header">
							<i class="entypo-chart-line"></i> <a href="#" class="text-center">
								28-Jul-16 To 4-Aug-16 </a>
						</div>
						<div class="tile-content">
							<div class="table_">
								<div class="head">
									<div class="first">
										<p class="text-center">Description</p>
									</div>
									<div class="second">
										<div>
											<p class="text-center">Count</p>
											<p class="text-center">Amount</p>
										</div>
									</div>
								</div>
								<div class="clearfix"></div>
								<div class="body">
									<div class="first">
										<p>PJP Visit</p>
										<p>60000</p>
										<p>12000</p>
									</div>
									<div class="second">
										<p>Sales Orders</p>
										<p>54551</p>
										<p>65465</p>
									</div>
									<div class="third">
										<p>Reciepts</p>
										<p>545254</p>
										<p>484555</p>
									</div>
								</div>
							</div>
						</div>
						<div class="tile-footer"></div>
					</div>
				</div>
				<div class="col-md-3 col-sm-6 col-xs-12">
					<div class="tile-stats tile-white stat-tile">
						<h3>32 Sales</h3>
						<p>Avg. Sales per day</p>
						<span class="monthly-sales"></span>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12">
					<div class="panel panel-primary" data-collapsed="0">
						<div class="panel-heading"
							style="background-color: rgb(231, 231, 231);">
							<div class="panel-title">
								<strong>Live streaming - Salesman wise performance</strong>
								&nbsp;&nbsp;|&nbsp;&nbsp; <strong><b>Attendance -
										8/10</b></strong>
							</div>
							<div class="panel-options">
								<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
								<button type="button" id="exce_toggle"
									class="btn btn-red btn-xs tooltip-info" data-toggle="tooltip"
									data-placement="top" data-original-title="Click For More">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</button>

								<a href="#" data-rel="close"><i class="entypo-cancel"></i></a>
							</div>
						</div>
						<div class="panel-body">
							<div class="row">
								<div
									class="col-sm-6 col-md-5 col-lg-3 col-xs-12 m-t-1 col-lg-mq">
									<div class="tile-block tile-yellow height_tiles">
										<div class="tile-header">
											<i class="entypo-thumbs-up" style="color: black;"></i> <img
												src="/resources/assets/images/de.jpg" width="30"
												class="img-responsive img-circle profile_picture"> <a
												href="#"> Raju <span>Thrissur To Kunnamkulam</span>
											</a>
										</div>
										<div class="tile-content">
											<div class="table_">
												<div class="head">
													<div class="first">
														<p class="text-center">Description</p>
													</div>
													<div class="second">
														<div>
															<p class="text-center">Count</p>
															<p class="text-center">Amount</p>
														</div>
													</div>
												</div>
												<div class="clearfix"></div>
												<div class="body">
													<div class="first">
														<p>PJP Visit</p>
														<p>60000</p>
														<p>12000</p>
													</div>
													<div class="second">
														<p>Sales Orders</p>
														<p>54551</p>
														<p>65465</p>
													</div>
													<div class="third">
														<p>Reciepts</p>
														<p>545254</p>
														<p>484555</p>
													</div>
												</div>
											</div>
										</div>
										<div class="tile-footer">
											<p class="address popover-default" data-toggle="popover"
												data-trigger="hover" data-placement="top"
												data-content=" anatha Stores, MG Road, Thrissur"
												data-original-title="Address">Janatha Stores, MG Road,
												Thrissur</p>
											<a href="livetracking.html" class="icon"> <i
												class="entypo-location"></i>
											</a>
											<p class="address">Janatha Stores, MG Road, Thrissur</p>
											<p class="time">05:25</p>
										</div>
									</div>
								</div>
								<div
									class="col-sm-6 col-md-5 col-lg-3 col-xs-12 m-t-1 col-lg-mq">
									<div class="tile-block tile-red height_tiles">
										<div class="tile-header">
											<i class="entypo-thumbs-down"></i> <img
												src="/resources/assets/images/de.jpg" width="30"
												class="img-responsive img-circle profile_picture"> <a
												href="#"> Shanu <span>Not Assigned</span>
											</a>
										</div>
										<div class="tile-content">
											<div class="table_">
												<div class="head">
													<div class="first">
														<p class="text-center">Description</p>
													</div>
													<div class="second">
														<div>
															<p class="text-center">Count</p>
															<p class="text-center">Amount</p>
														</div>
													</div>
												</div>
												<div class="clearfix"></div>
												<div class="body">
													<div class="first">
														<p>PJP Visit</p>
														<p>0000</p>
														<p>0000</p>
													</div>
													<div class="second">
														<p>Sales Orders</p>
														<p>0000</p>
														<p>0000</p>
													</div>
													<div class="third">
														<p>Reciepts</p>
														<p>0000</p>
														<p>0000</p>
													</div>
												</div>
											</div>
										</div>
										<div class="tile-footer">
											<p class="address popover-default" data-toggle="popover"
												data-trigger="hover" data-placement="top"
												data-content=" anatha Stores, MG Road, Thrissur"
												data-original-title="Address">Janatha Stores, MG Road,
												Ekm</p>
											<a href="livetracking.html" class="icon"> <i
												class="entypo-location"></i>
											</a>
											<p class="address">Janatha Stores, MG Road, Thrissur</p>
											<p class="time">05:25</p>
										</div>
									</div>
								</div>
								<div
									class="col-sm-6 col-md-5 col-lg-3 col-xs-12 m-t-1 col-lg-mq">
									<div class="tile-block tile-green height_tiles">
										<div class="tile-header">
											<i class="entypo-thumbs-up"></i> <img
												src="/resources/assets/images/de.jpg" width="30"
												class="img-responsive img-circle profile_picture"> <a
												href="#"> Ayyoob <span>Thrissur To Kuttippuram</span>
											</a>
										</div>
										<div class="tile-content">
											<div class="table_">
												<div class="head">
													<div class="first">
														<p class="text-center">Description</p>
													</div>
													<div class="second">
														<div>
															<p class="text-center">Count</p>
															<p class="text-center">Amount</p>
														</div>
													</div>
												</div>
												<div class="clearfix"></div>
												<div class="body">
													<div class="first">
														<p>PJP Visit</p>
														<p>60000</p>
														<p>12000</p>
													</div>
													<div class="second">
														<p>Sales Orders</p>
														<p>54551</p>
														<p>65465</p>
													</div>
													<div class="third">
														<p>Reciepts</p>
														<p>545254</p>
														<p>484555</p>
													</div>
												</div>
											</div>
										</div>
										<div class="tile-footer">
											<p class="address popover-default" data-toggle="popover"
												data-trigger="hover" data-placement="top"
												data-content=" anatha Stores, MG Road, Thrissur"
												data-original-title="Address">Janatha Stores, MG Road,
												Thrissur</p>
											<a href="livetracking.html" class="icon"> <i
												class="entypo-location"></i>
											</a>
											<p class="address">Janatha Stores, MG Road, Thrissur</p>
											<p class="time">05:25</p>
										</div>

									</div>
								</div>
								<div
									class="col-sm-6 col-md-5 col-lg-3 col-xs-12 m-t-1 col-lg-mq">
									<div class="tile-block tile-red height_tiles">
										<div class="tile-header">
											<i class="entypo-thumbs-up"></i> <img
												src="/resources/assets/images/de.jpg" width="30"
												class="img-responsive img-circle profile_picture"> <a
												href="#"> Vinay <span>Thrissur To EKLM</span>
											</a>
										</div>
										<div class="tile-content">
											<div class="table_">
												<div class="head">
													<div class="first">
														<p class="text-center">Description</p>
													</div>
													<div class="second">
														<div>
															<p class="text-center">Count</p>
															<p class="text-center">Amount</p>
														</div>
													</div>
												</div>
												<div class="clearfix"></div>
												<div class="body">
													<div class="first">
														<p>PJP Visit</p>
														<p>60000</p>
														<p>12000</p>
													</div>
													<div class="second">
														<p>Sales Orders</p>
														<p>54551</p>
														<p>65465</p>
													</div>
													<div class="third">
														<p>Reciepts</p>
														<p>545254</p>
														<p>484555</p>
													</div>
												</div>
											</div>
										</div>
										<div class="tile-footer">
											<p class="address popover-default" data-toggle="popover"
												data-trigger="hover" data-placement="top"
												data-content=" anatha Stores, MG Road, Thrissur"
												data-original-title="Address">Janatha Stores, MG Road,
												Ekm</p>
											<a href="livetracking.html" class="icon"> <i
												class="entypo-location"></i>
											</a>
											<p class="address">Janatha Stores, MG Road, Thrissur</p>
											<p class="time">05:25</p>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<jsp:include page="../fragments/m_footer.jsp"></jsp:include>
		</div>
	</div>
	<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

	<spring:url value="/resources/app/dashboard.js" var="dashboardJs"></spring:url>
	<script type="text/javascript" src="${dashboardJs}"></script>
</body>
</html>