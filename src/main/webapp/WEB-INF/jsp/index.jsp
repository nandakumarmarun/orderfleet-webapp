<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:url value="/resources/assets/images/index/favicon.ico"
	var="favicon"></spring:url>
<link rel="icon" href="${favicon}">

<title>SalesNrich</title>

<!-- Bootstrap -->
<spring:url value="/resources/assets/css/index/bootstrap.min.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/index/font-awesome.min.css"
	var="fontCss"></spring:url>
<link href="${fontCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/index/animate.css"
	var="animateCss"></spring:url>
<link href="${animateCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/index/styleWheelNav.css"
	var="styleWheelNavCss"></spring:url>
<link href="${styleWheelNavCss}" rel="stylesheet">


<!-- styles using for SalesNrich -->
<spring:url value="/resources/assets/css/index/style.css" var="styleCss"></spring:url>
<link href="${styleCss}" rel="stylesheet">

<spring:url value="/resources/assets/css/index/jquery.fancybox.css"
	var="fancyboxCss"></spring:url>
<link href="${fancyboxCss}" rel="stylesheet">

<style type="text/css">
#trial-demo-form {
	z-index: 1029;
	position: relative;
}

@media ( max-width : 1024px) {
	.show-icon {
		position: relative;
		top: -813px;
	}
	.free-demo-form {
		margin-top: 200px;
	}
	.home .form aside {
		padding: 18% 0 15%;
	}
}

@media ( max-width : 400px) {
	#trial-demo-form .show-icon {
		top: -999px;
		position: relative;
	}
	.free-demo-form {
		margin-top: 92px;
	}
	.home .form {
		padding: 33% 0 0 0;
		position: relative;
		z-index: 9999;
	}
}
</style>

</head>
<body id="home">

	<!-- preloader -->
	<div id="preloader"></div>

	<!-- Navigation -->
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header page-scroll">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand page-scroll" href="index"> <img
					class="logowhite" src="/resources/assets/images/index/logo.png"
					width="250" class="img-responsive"> <img
					class="hidden logo-og"
					src="/resources/assets/images/index/logo-w.png" width="150"
					class="img-responsive">
				</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse navbar-ex1-collapse">
				<ul class="nav navbar-nav navbar-right">
					<!-- Hidden li included to remove active class from about link when scrolled up past about section -->
					<li class="hidden"><a class="page-scroll" href="#page-top"></a>
					</li>
					<li class="navDropMenu"><a class="page-scroll">Solution
							For <i class="fa fa-caret-right"></i>
					</a></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">Enterprises <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li class="SalesNrich"><i class="fa fa-cog"
								style="background: #32475c;"></i> <a href="#salesnrich"
								style="background: #425a72;">FMCG</a></li>
							<li class="Pharma"><i class="fa fa-cog"
								style="background: #ae6cc4;"></i> <a href="#pharma"
								style="background: #b979d0;">Pharma</a></li>
							<li class="Building"><i class="fa fa-cog"
								style="background: #43ace3;"></i> <a href="#building"
								style="background: #46b3ec;">Building</a></li>
							<li class="foodNbeverage"><i class="fa fa-cog"
								style="background: #23caae;"></i> <a href="#foodBeverage"
								style="background: #26d5b8;">Food / Beverage</a></li>
							<li class="institution"><i class="fa fa-cog"
								style="background: #39d886;"></i> <a href="#Institution"
								style="background: #3bdd8a;">Institution</a></li>
							<li><i class="fa fa-cog" style="background: #ec932b;"></i> <a
								style="background: #f69c2e;">More</a></li>
						</ul></li>
					<li><a class="page-scroll" href="benefits">Benefits /
							Services</a></li>
					<li><a class="page-scroll" href="contact">Contact</a></li>
					<li><a class="page-scroll" href="#">+91 8893 499 106</a></li>	
					<li><a class="page-scroll" href="login">LOGIN</a></li>
				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container -->
	</nav>

	<!-- main content -->

	<div id="background-wrap">
		<div class="x1">
			<div class="cloud"></div>
		</div>

		<div class="x2">
			<div class="cloud"></div>
		</div>

		<div class="x3">
			<div class="cloud"></div>
		</div>

		<!--  <div class="x4">
            <div class="cloud"></div>
        </div>

        <div class="x5">
            <div class="cloud"></div>
        </div> -->
	</div>

	<section class="banner web-view">
		<div class="home left-content col-xs-12 col-md-11">
			<div class="col-md-12 c-main">
				<article
					class="col-md-5 col-md-offset-2 col-sm-12 container text-center">
					<h3>
						Enterprise mobility <br> solution for
					</h3>
					<h1>Productivity</h1>
					<h1>Acceleration,</h1>
					<h1>Faster Delivery,</h1>
					<h2>and More Sales</h2>
				</article>
				<figure class="col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-0">
					<div class="phone-slider">
						<div class="carousel slide" data-ride="carousel">
							<!-- Indicators -->
							<ol class="carousel-indicators">
								<li data-target="#carousel-example-generic" data-slide-to="0"
									class="active"></li>
								<li data-target="#carousel-example-generic" data-slide-to="1"></li>
								<li data-target="#carousel-example-generic" data-slide-to="2"></li>
								<li data-target="#carousel-example-generic" data-slide-to="3"></li>
							</ol>

							<!-- Wrapper for slides -->
							<div class="carousel-inner" role="listbox">
								<div class="item active">
									<img src="/resources/assets/images/index/phone-slider-1.png"
										alt="...">
								</div>
								<div class="item">
									<img src="/resources/assets/images/index/phone-slider-2.png"
										alt="...">
								</div>
								<div class="item">
									<img src="/resources/assets/images/index/phone-slider-3.png"
										alt="...">
								</div>
								<div class="item">
									<img src="/resources/assets/images/index/phone-slider-4.png"
										alt="...">
								</div>
							</div>
						</div>
					</div>
				</figure>

				<!-- button for hide and show of free-demo-form -->
				<!-- <figure class="col-md-1 col-sm-12 free-demo-form-btn">
                    <img src="/resources/assets/images/index/buttonFree.png" class="img-responsive show-icon" class="img-responsive">
                    <img src="/resources/assets/images/index/buttonFreeClose.png" class="img-responsive hide-icon" class="img-responsive">
                </figure> -->
			</div>
		</div>

		<!-- button for hide and show of free-demo-form -->
		<div id="trial-demo-form">
			<figure class="col-md-1 col-sm-12 free-demo-form-btn">
				<img src="/resources/assets/images/index/buttonFree.png"
					class="img-responsive show-icon" class="img-responsive">
				<img src="/resources/assets/images/index/buttonFreeClose.png"
					class="img-responsive hide-icon hidden-xs hidden-sm"
					class="img-responsive">
			</figure>
			<div class="home free-demo-form col-xs-12 col-md-3">
				<div class="col-md-12 form">
					<button
						class="btn pull-right btn-info hidden-md hidden-lg formcloseBtn">
						<i class="fa fa-close"></i>
					</button>
					<aside class="col-md-12 container text-center">
						<h3>Request a Demo</h3>
						<br>
						<form>
							<div class="form-group">
								<input type="text" class="form-control" placeholder="Name">
							</div>
							<div class="form-group">
								<input type="email" class="form-control" placeholder="Email">
							</div>
							<div class="form-group">
								<input type="phone" class="form-control" placeholder="Phone">
							</div>
							<div class="form-group">
								<button class="btn btn-success">Submit</button>
							</div>
						</form>
					</aside>
				</div>
			</div>
		</div>
	</section>

	<div class="clearfix"></div>

	<!-- wheel navigation starts here -->
	<section id="wheel-navigation"
		class="hidden-xs hidden-sm mouseUnSelect"
		style="padding: 5% 35px; background: white;" unselectable="on"
		onselectstart="return false;" onmousedown="return false;">
		<div class="container">
			<ul>
				<li class="active SalesNrich"><a>FMCG</a></li>
				<li class="Pharma"><a>Pharma Rx</a></li>
				<li class="Building"><a>Building Meterials</a></li>
				<li class="foodNbeverage"><a>Food / Beverages</a></li>
				<li class="institution"><a>Institution Sales</a></li>
				<li class=""><a>More</a></li>
			</ul>
		</div>
	</section>

	<div class="clearfix"></div>

	<!-- salesnrich -->
	<section id="salesnrich"
		class="hidden-xs hidden-sm mouseUnSelect wheel-navbar"
		unselectable="on" onselectstart="return false;"
		onmousedown="return false;">
		<nav class="main-sec">
			<aside class="a">
				<b>Field activities info</b>
				<div>
					<img class="default-img-a"
						src="/resources/assets/images/index/Field activities info.png">
					<img class="active-img-a"
						src="/resources/assets/images/index/Field activities info_B.png">
				</div>
			</aside>
			<aside class="b">
				<b>Receipts</b>
				<div>
					<img class="default-img-b"
						src="/resources/assets/images/index/Receipts.png"> <img
						class="active-img-b"
						src="/resources/assets/images/index/Receipts_B.png">
				</div>
			</aside>
			<aside class="c">
				<b>Performance management</b>
				<div>
					<img class="default-img-c"
						src="/resources/assets/images/index/Performance management.png">
					<img class="active-img-c"
						src="/resources/assets/images/index/Performance management_B.png">
				</div>
			</aside>
			<aside class="d">
				<b>Sales Orders</b>
				<div>
					<img class="default-img-d"
						src="/resources/assets/images/index/Sales Orders.png"> <img
						class="active-img-d"
						src="/resources/assets/images/index/Sales Orders_B.png">
				</div>
			</aside>
			<aside class="e">
				<b>Business Process Automation</b>
				<div>
					<img class="default-img-e"
						src="/resources/assets/images/index/Business ProcessAutomation.png">
					<img class="active-img-e"
						src="/resources/assets/images/index/Business ProcessAutomation_B.png">
				</div>
			</aside>
			<aside class="f active">
				<b>Administrative Tools</b>
				<div>
					<img src="/resources/assets/images/index/Administrative Tools.png">
				</div>
			</aside>
			<aside class="g">
				<b>Delivery module</b>
				<div>
					<img class="default-img-g"
						src="/resources/assets/images/index/Delivery module.png"> <img
						class="active-img-g"
						src="/resources/assets/images/index/Delivery module_B.png">
				</div>
			</aside>
		</nav>
		<article>

			<!-- f -->
			<figure class="f-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/admin screen.png"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/admin screen.png"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Administrative Tools</h2> -->
					<p>Helping the managers to track & guide their team well. And
						management tools (Dashboards, charts, and reports) that help make
						insightful decisions.</p>
				</figcaption>
			</figure>

			<!-- d content -->
			<figure class="d-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/sales order.png"
					title="Sales Orders"> <img
					src="/resources/assets/images/index/sales order.png" width="250"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Sales Orders</h2> -->
					<p>Sales reps can take order with the help of required
						information such as stock availability, price level to be applied,
						offers, tax rates, etc.</p>
				</figcaption>
			</figure>

			<!-- a content -->
			<figure class="a-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/Field-activities-info.png"
					title="Field activities info"> <img
					src="/resources/assets/images/index/Field-activities-info.png"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Field activities info</h2> -->
					<p>Activities along with notes such as sales order note,
						receipt note, etc. reach back office instantly from the field
						along with location information</p>
				</figcaption>
			</figure>

			<!-- b content -->
			<figure class="b-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/receipt2.jpg"
					title="Field activities info"> <img
					src="/resources/assets/images/index/receipt2.jpg" width="900"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Receipts</h2> -->
					<p>The users will have information related to pending invoices,
						to which they can allocate the payments.</p>
				</figcaption>
			</figure>

			<!-- e content -->
			<figure class="e-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/BusinessProcessAutomation.png"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/BusinessProcessAutomation.png"
					width="250" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Business process automation</h2> -->
					<p>As the notes\docs reach back-office further actions can be
						automated, for example as the sales order reaches back office
						Picking & Packing task is assigned to the warehouse staffs.</p>
				</figcaption>
			</figure>

			<!-- g content -->
			<figure class="g-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/delivery.png"
					title="Delivery module"> <img
					src="/resources/assets/images/index/delivery.png" width="250"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Delivery module</h2> -->
					<p>SalesNrich allows route wise delivery planning and
						execution.</p>
				</figcaption>
			</figure>

			<!-- c content -->
			<figure class="c-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/PerformanceManagement.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/PerformanceManagement.jpg"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Performance management</h2> -->
					<p>Performance management features such as tasks, schedules,
						targets, and incentive settings & calculations</p>
				</figcaption>
			</figure>

		</article>
	</section>

	<!-- pharma -->
	<section id="pharma"
		class="hidden-xs hidden-sm mouseUnSelect wheel-navbar"
		unselectable="on" onselectstart="return false;"
		onmousedown="return false;">
		<nav class="main-sec">
			<aside class="a">
				<b>Return On Investment</b>
				<div>
					<img class="default-img-a"
						src="/resources/assets/images/index/Field activities info.png">
					<img class="active-img-a"
						src="/resources/assets/images/index/Field activities info_B.png">
				</div>
			</aside>
			<aside class="b">
				<b>Sales Orders</b>
				<div>
					<img class="default-img-b"
						src="/resources/assets/images/index/Sales Orders.png"> <img
						class="active-img-b"
						src="/resources/assets/images/index/Sales Orders_B.png">
				</div>
			</aside>
			<aside class="c">
				<b>Performance Management</b>
				<div>
					<img class="default-img-c"
						src="/resources/assets/images/index/Receipts.png"> <img
						class="active-img-c"
						src="/resources/assets/images/index/Receipts_B.png">
				</div>
			</aside>
			<aside class="d">
				<b>List of activities</b>
				<div>
					<img class="default-img-d"
						src="/resources/assets/images/index/Performance management.png">
					<img class="active-img-d"
						src="/resources/assets/images/index/Performance management_B.png">
				</div>
			</aside>
			<aside class="e">
				<b>Expense Voucher</b>
				<div>
					<img class="default-img-e"
						src="/resources/assets/images/index/Business ProcessAutomation.png">
					<img class="active-img-e"
						src="/resources/assets/images/index/Business ProcessAutomation_B.png">
				</div>
			</aside>
			<aside class="f active">
				<b>Administrative Tools</b>
				<div>
					<img src="/resources/assets/images/index/Administrative Tools.png">
				</div>
			</aside>
			<aside class="g">
				<b>Delivery module</b>
				<div>
					<img class="default-img-g"
						src="/resources/assets/images/index/Delivery module.png"> <img
						class="active-img-g"
						src="/resources/assets/images/index/Delivery module_B.png">
				</div>
			</aside>
		</nav>
		<article>

			<!-- a -->
			<figure class="f-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/admin screen pharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/admin screen pharma.jpg"
					class="img-responsive animated zoomInUp pc" style="margin: 0 auto;">
				</a>
				<figcaption>
					<!-- <h2>Administrative Tools</h2><h2>Administrative Tools</h2> -->
					<p>Helping the managers to track & guide their team well. And
						management tools (Dashboards, charts, and reports) that help make
						insightful decisions.</p>
				</figcaption>
			</figure>

			<!-- b content -->
			<figure class="g-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/delivery.png"
					title="Delivery module"> <img
					src="/resources/assets/images/index/delivery.png" width="250"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Delivery module</h2> -->
					<p>SalesNrich allows route wise delivery planning and
						execution.</p>
				</figcaption>
			</figure>

			<!-- c content -->
			<figure class="b-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/salesOrderPharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/salesOrderPharma.jpg"
					width="260" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Sales Orders</h2> -->
					<!-- <p>Sales reps can take order with the help of required information such as stock availability, price level to be applied, offers, tax rates, etc.</p> -->
					<p>SalesNrich supports both primary and secondary sales.</p>
				</figcaption>
			</figure>

			<!-- d content -->
			<figure class="d-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/ListOfActivitiesPharma.png"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/ListOfActivitiesPharma.png"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>List of activities</h2> -->
					<p>Performance management features such as tasks, schedules,
						targets, and incentive settings & calculations.</p>
				</figcaption>
			</figure>

			<!-- e content -->
			<figure class="a-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/ReturnOnInvestment.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/ReturnOnInvestment.jpg"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Return On Investment</h2> -->
					<p>Activities along with notes such as sales order note,
						receipt note, etc. reach back office instantly from the field
						along with location information</p>
				</figcaption>
			</figure>

			<!-- f content -->
			<figure class="e-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/ExpenseVoucherPharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/ExpenseVoucherPharma.jpg"
					width="260" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Expense Voucher</h2> -->
					<p>The users can make notes related to his\her expenses and
						send it across to back-office.</p>
				</figcaption>
			</figure>

			<!-- g content -->
			<figure class="c-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/PerformanceManagementPharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/PerformanceManagementPharma.jpg"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Performance Management </h2> -->
					<p>Employee wise performance analysis on sales and marketing
						are available.</p>
				</figcaption>
			</figure>

		</article>
	</section>

	<!-- building meterial -->
	<section id="building"
		class="hidden-xs hidden-sm mouseUnSelect wheel-navbar"
		unselectable="on" onselectstart="return false;"
		onmousedown="return false;">
		<nav class="main-sec">
			<aside class="a">
				<b>Performance Management</b>
				<div>
					<img class="default-img-a"
						src="/resources/assets/images/index/Receipts.png"> <img
						class="active-img-a"
						src="/resources/assets/images/index/Receipts_B.png">
				</div>
			</aside>
			<aside class="b">
				<b>Quotation management</b>
				<div>
					<img class="default-img-b"
						src="/resources/assets/images/index/Field activities info.png">
					<img class="active-img-b"
						src="/resources/assets/images/index/Field activities info_B.png">
				</div>
			</aside>
			<aside class="c">
				<b>User Activities</b>
				<div>
					<img class="default-img-c"
						src="/resources/assets/images/index/Business ProcessAutomation.png">
					<img class="active-img-c"
						src="/resources/assets/images/index/Business ProcessAutomation_B.png">
				</div>
			</aside>
			<aside class="d">
				<b>Field-activities-info</b>
				<div>
					<img class="default-img-d"
						src="/resources/assets/images/index/Sales Orders.png"> <img
						class="active-img-d"
						src="/resources/assets/images/index/Sales Orders_B.png">
				</div>
			</aside>
			<aside class="e">
				<b>Delivery module</b>
				<div>
					<img class="default-img-e"
						src="/resources/assets/images/index/Delivery module.png"> <img
						class="active-img-e"
						src="/resources/assets/images/index/Delivery module_B.png">
				</div>
			</aside>
			<aside class="f active">
				<b>Administrative Tools</b>
				<div>
					<img src="/resources/assets/images/index/Administrative Tools.png">
				</div>
			</aside>
			<aside class="g">
				<b>Competitor Activities</b>
				<div>
					<img class="default-img-g"
						src="/resources/assets/images/index/Performance management.png">
					<img class="active-img-g"
						src="/resources/assets/images/index/Performance management_B.png">
				</div>
			</aside>
		</nav>
		<article>
			<!-- a -->
			<figure class="f-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/adminScreenBuliding.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/adminScreenBuliding.jpg"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Administrative Tools</h2><h2>Administrative Tools</h2> -->
					<p>Helping the managers to track &amp; guide their team well.
						And management tools (Dashboards, charts, and reports) that help
						make insightful decisions.</p>
				</figcaption>
			</figure>

			<!-- b content -->
			<figure class="e-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/delivery.png"
					title="Delivery module"> <img
					src="/resources/assets/images/index/delivery.png" width="250"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Delivery module</h2> -->
					<p>SalesNrich allows route wise delivery planning and
						execution.</p>
				</figcaption>
			</figure>

			<!-- c content -->
			<figure class="d-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/Field-activities-info.png"
					title="Field activities info"> <img
					src="/resources/assets/images/index/Field-activities-info.png"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Field-activities-info</h2> -->
					<p>Activities along with notes such as sales order note,
						receipt note, etc. reach back office instantly from the field
						along with location information.</p>
				</figcaption>
			</figure>

			<!-- d content -->
			<figure class="g-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/CompetitorActivities.jpg"
					title="Competitor Activities"> <img
					src="/resources/assets/images/index/CompetitorActivities.jpg"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Competitor Activities</h2> -->
					<p>Provision for sales executives to mark the rates and other
						offers by competitors</p>
				</figcaption>
			</figure>

			<!-- e content -->
			<figure class="b-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/Quotation.jpg"
					title="Quotation management"> <img
					src="/resources/assets/images/index/Quotation.jpg" width="200"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Quotation management</h2> -->
					<p>Request for quotations can be converted as quotations in
						printable formats and can be forwarded as emails. Versions of
						quotations can be maintained.</p>
				</figcaption>
			</figure>

			<!-- f content -->
			<figure class="c-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/User Activities.jpg"
					title="User Activities"> <img
					src="/resources/assets/images/index/User Activities.jpg"
					width="260" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>User Activities</h2> -->
					<p>The sales executives\users may perform different activities
						related to sales & marketing and can create notes\docs to the
						same. The activates include visiting different types of customers,
						sales partners, and other stake holders such as architects,
						institutions, dealers, retailers, etc.</p>
				</figcaption>
			</figure>

			<!-- g content -->
			<figure class="a-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/PerformanceManagementBuilding.png"
					title="Performance Management"> <img
					src="/resources/assets/images/index/PerformanceManagementBuilding.png"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Performance Management </h2> -->
					<p>Performance management features such as tasks, schedules,
						targets, and incentive settings & calculations.</p>
				</figcaption>
			</figure>
		</article>
	</section>

	<!-- foodBeverage -->
	<section id="foodBeverage"
		class="hidden-xs hidden-sm mouseUnSelect wheel-navbar"
		unselectable="on" onselectstart="return false;"
		onmousedown="return false;">
		<nav class="main-sec">
			<aside class="a">
				<b>Performance Management</b>
				<div>
					<img class="default-img-a"
						src="/resources/assets/images/index/Receipts.png"> <img
						class="active-img-a"
						src="/resources/assets/images/index/Receipts_B.png">
				</div>
			</aside>
			<aside class="b">
				<b>Return On Investment</b>
				<div>
					<img class="default-img-b"
						src="/resources/assets/images/index/Field activities info.png">
					<img class="active-img-b"
						src="/resources/assets/images/index/Field activities info_B.png">
				</div>
			</aside>
			<aside class="c">
				<b>Expense Voucher</b>
				<div>
					<img class="default-img-c"
						src="/resources/assets/images/index/Business ProcessAutomation.png">
					<img class="active-img-c"
						src="/resources/assets/images/index/Business ProcessAutomation_B.png">
				</div>
			</aside>
			<aside class="d">
				<b>Sales Orders</b>
				<div>
					<img class="default-img-d"
						src="/resources/assets/images/index/Sales Orders.png"> <img
						class="active-img-d"
						src="/resources/assets/images/index/Sales Orders_B.png">
				</div>
			</aside>
			<aside class="e">
				<b>Delivery module</b>
				<div>
					<img class="default-img-e"
						src="/resources/assets/images/index/Delivery module.png"> <img
						class="active-img-e"
						src="/resources/assets/images/index/Delivery module_B.png">
				</div>
			</aside>
			<aside class="f active">
				<b>Administrative Tools</b>
				<div>
					<img src="/resources/assets/images/index/Administrative Tools.png">
				</div>
			</aside>
			<aside class="g">
				<b>List of activities</b>
				<div>
					<img class="default-img-g"
						src="/resources/assets/images/index/Performance management.png">
					<img class="active-img-g"
						src="/resources/assets/images/index/Performance management_B.png">
				</div>
			</aside>
		</nav>
		<article>

			<!-- a -->
			<figure class="f-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/admin screen pharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/admin screen pharma.jpg"
					class="img-responsive animated zoomInUp pc" style="margin: 0 auto;">
				</a>
				<figcaption>
					<!-- <h2>Administrative Tools</h2><h2>Administrative Tools</h2> -->
					<p>Helping the managers to track & guide their team well. And
						management tools (Dashboards, charts, and reports) that help make
						insightful decisions.</p>
				</figcaption>
			</figure>

			<!-- b content -->
			<figure class="e-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/delivery.png"
					title="Delivery module"> <img
					src="/resources/assets/images/index/delivery.png" width="250"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Delivery module</h2> -->
					<p>SalesNrich allows route wise delivery planning and
						execution.</p>
				</figcaption>
			</figure>

			<!-- c content -->
			<figure class="d-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/salesOrderPharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/salesOrderPharma.jpg"
					width="260" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Sales Orders</h2> -->
					<!-- <p>Sales reps can take order with the help of required information such as stock availability, price level to be applied, offers, tax rates, etc.</p> -->
					<p>SalesNrich supports both primary and secondary sales.</p>
				</figcaption>
			</figure>

			<!-- d content -->
			<figure class="g-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/ListOfActivitiesPharma.png"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/ListOfActivitiesPharma.png"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>List of activities</h2> -->
					<p>Performance management features such as tasks, schedules,
						targets, and incentive settings & calculations.</p>
				</figcaption>
			</figure>

			<!-- e content -->
			<figure class="b-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/ReturnOnInvestment.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/ReturnOnInvestment.jpg"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Return On Investment</h2> -->
					<p>Activities along with notes such as sales order note,
						receipt note, etc. reach back office instantly from the field
						along with location information</p>
				</figcaption>
			</figure>

			<!-- f content -->
			<figure class="c-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/ExpenseVoucherPharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/ExpenseVoucherPharma.jpg"
					width="260" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Expense Voucher</h2> -->
					<p>The users can make notes related to his\her expenses and
						send it across to back-office.</p>
				</figcaption>
			</figure>

			<!-- g content -->
			<figure class="a-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/PerformanceManagementPharma.jpg"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/PerformanceManagementPharma.jpg"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Performance Management </h2> -->
					<p>Employee wise performance analysis on sales and marketing
						are available.</p>
				</figcaption>
			</figure>

		</article>
	</section>

	<!-- Institution -->
	<section id="Institution"
		class="hidden-xs hidden-sm mouseUnSelect wheel-navbar"
		unselectable="on" onselectstart="return false;"
		onmousedown="return false;">
		<nav class="main-sec">
			<aside class="a">
				<b>Field-activities-info</b>
				<div>
					<img class="default-img-a"
						src="/resources/assets/images/index/Sales Orders.png"> <img
						class="active-img-a"
						src="/resources/assets/images/index/Sales Orders_B.png">
				</div>
			</aside>
			<aside class="b">
				<b>Quotation management</b>
				<div>
					<img class="default-img-b"
						src="/resources/assets/images/index/Field activities info.png">
					<img class="active-img-b"
						src="/resources/assets/images/index/Field activities info_B.png">
				</div>
			</aside>
			<aside class="c">
				<b>Performance Management</b>
				<div>
					<img class="default-img-c"
						src="/resources/assets/images/index/Receipts.png"> <img
						class="active-img-c"
						src="/resources/assets/images/index/Receipts_B.png">
				</div>
			</aside>
			<aside class="d">
				<b>User Activities</b>
				<div>
					<img class="default-img-d"
						src="/resources/assets/images/index/Business ProcessAutomation.png">
					<img class="active-img-d"
						src="/resources/assets/images/index/Business ProcessAutomation_B.png">
				</div>
			</aside>
			<aside class="e">
				<b>Competitor Activities</b>
				<div>
					<img class="default-img-e"
						src="/resources/assets/images/index/Performance management.png">
					<img class="active-img-e"
						src="/resources/assets/images/index/Performance management_B.png">
				</div>
			</aside>
			<aside class="f active">
				<b>Administrative Tools</b>
				<div>
					<img src="/resources/assets/images/index/Administrative Tools.png">
				</div>
			</aside>
			<aside class="g">
				<b>Tasks & Schedules</b>
				<div>
					<img class="default-img-g"
						src="/resources/assets/images/index/TasksandSchedules.png">
					<img class="active-img-g"
						src="/resources/assets/images/index/TasksandSchedules_B.png">
				</div>
			</aside>
		</nav>
		<article>
			<!-- a -->
			<figure class="f-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/adminScreenInstitusion.png"
					title="Administrative Tools"> <img
					src="/resources/assets/images/index/adminScreenInstitusion.png"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Administrative Tools</h2><h2>Administrative Tools</h2> -->
					<p>Helping the managers to track &amp; guide their team well.
						And management tools (Dashboards, charts, and reports) that help
						make insightful decisions.</p>
				</figcaption>
			</figure>

			<!-- b content -->
			<figure class="g-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/Tasks & Schedules.png"
					title="Tasks & Schedules"> <img
					src="/resources/assets/images/index/Tasks & Schedules.png"
					width="250" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Delivery module</h2> -->
					<p>SalesNrich allows route wise delivery planning and
						execution.</p>
				</figcaption>
			</figure>

			<!-- c content -->
			<figure class="a-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/Field-activities-info.png"
					title="Field activities info"> <img
					src="/resources/assets/images/index/Field-activities-info.png"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Field-activities-info</h2> -->
					<p>Activities along with notes such as sales order note,
						receipt note, etc. reach back office instantly from the field
						along with location information.</p>
				</figcaption>
			</figure>

			<!-- d content -->
			<figure class="e-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/CompetitorTracking.jpg"
					title="Competitor Activities"> <img
					src="/resources/assets/images/index/CompetitorTracking.jpg"
					class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Competitor Activities</h2> -->
					<p>Provision for sales executives to mark the rates and other
						offers by competitors</p>
				</figcaption>
			</figure>

			<!-- e content -->
			<figure class="b-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/Quotation.jpg"
					title="Quotation management"> <img
					src="/resources/assets/images/index/Quotation.jpg" width="200"
					class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>Quotation management</h2> -->
					<p>Request for quotations can be converted as quotations in
						printable formats and can be forwarded as emails. Versions of
						quotations can be maintained.</p>
				</figcaption>
			</figure>

			<!-- f content -->
			<figure class="d-content mobile-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/InstituionUserActivities.jpg"
					title="User Activities"> <img
					src="/resources/assets/images/index/InstituionUserActivities.jpg"
					width="260" class="img-responsive animated zoomInUp phone">
				</a>
				<figcaption>
					<!-- <h2>User Activities</h2> -->
					<p>The sales executives\users may perform different activities
						related to sales & marketing and can create notes\docs to the
						same. The activates include visiting different types of customers,
						sales partners, and other stake holders such as architects,
						institutions, dealers, retailers, etc.</p>
				</figcaption>
			</figure>

			<!-- g content -->
			<figure class="c-content web-screen">
				<a class="fancybox-effects-a"
					href="/resources/assets/images/index/PerformanceManagementInsti.jpg"
					title="Performance Management"> <img
					src="/resources/assets/images/index/PerformanceManagementInsti.jpg"
					width="900" class="img-responsive animated zoomInUp pc">
				</a>
				<figcaption>
					<!-- <h2>Performance Management </h2> -->
					<p>Performance management features such as tasks, schedules,
						targets, and incentive settings & calculations.</p>
				</figcaption>
			</figure>
		</article>
	</section>
	<!-- wheel navigation starts here -->


	<!-- wheel-navigation responsive section -->



	<!-- wheel-navigation responsive section -->


	<div class="clearfix"></div>

	<section id="points">
		<div class="container">
			<!-- <div class="col-md-6 col-xs-12 bullet-points">
            <ul>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Mobile apps for field staffs to handle quatations, sales orders, receipts, delivery, etc.</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Monitoring/Tracking tools for managers</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Contact and lead management modules</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Reports & Tools that help make insightful decisions</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Extended support to sales partners and customersusing mobile apps to keep them remain upto date with products & offers info, and can place/receiveorders anytime.</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Seamless integration with any back-office softwarepackage such as SAP, Tally, etc.</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Built on highly scalable cloud architecture using cutting edge open source technologies.</b></li>
                <i class="fa fa-circle" style="float: left;padding-right: 8px;margin-top: 4px;margin-left: -19px;color: #f59b11;"></i><li><b>Flexible licensing models which allows to start withlow budgets</b></li>
            </ul>
        </div>
        <div class="col-md-6">
            <figure>
                <img src="/resources/assets/images/index/phone_on_hand.png" >
            </figure>
        </div> -->

			<div class="col-md-4">
				<figure class="text-center">
					<img src="/resources/assets/images/index/points_img_1.png"
						width="150">
					<br>
					<figcaption>
						<h3>
							<b>Guided Selling</b>
						</h3>
						<br>
						<p>SalesNrich allows the managers\business owners to guide
							their sales force well with the help of features to organize and
							schedule each member’s sales trips and related activities (e.g.
							customer visits), with required information about the customers,
							products, stock, and offers. The directions to frontline sales
							persons can be given instantly, or well in advance. The route,
							whom all to meet (with set order), customer wise performance
							history, purchase history, credit limits, receivables, and every
							bit of information that would help make the sales process easier
							is made available to each team member. The managers\business
							owners may track & analyze the activities live, and verify its
							adherence to make sure things move as per the plans, with help of
							dashboards, reports, charts, alerts, and notifications.</p>
					</figcaption>
				</figure>
			</div>

			<div class="col-md-4">
				<figure class="text-center">
					<img src="/resources/assets/images/index/points_img_2.png"
						width="150">
					<br>
					<figcaption>
						<h3>
							<b>Business process automation</b>
						</h3>
						<br>
						<p>Sales improve when the supporting team at back-office, ware
							house, production floor, etc. work in a synchronized manner,
							which would result in faster delivery and better pre\post sales
							support. SalesNrich helps enterprises to automate every process,
							right from lead to sales, and from picking & packing to delivery.
							Approval works flows (e.g. approval on additional discount
							request) can also be defined and set with notifications and
							reminders to make sure that decisions are taken faster.</p>
					</figcaption>
				</figure>
			</div>

			<div class="col-md-4">
				<figure class="text-center">
					<img src="/resources/assets/images/index/points_img_3.png"
						width="150">
					<br>
					<figcaption>
						<h3>
							<b>Performance Management</b>
						</h3>
						<br>
						<p>SalesNrich helps the enterprises to improve sales team’s
							performance with tools & facilities to set targets and help the
							team members achieve the same by keeping them well aware on their
							performance status. Flexible target and incentive fixing feature
							allows setting of targets based on customer wise sales, product
							wise sales, or simple ones based on total sales volume or amount.
							Different reports and tools are made available to
							managers\business owners to track & analyze their team’s
							performance on a real time basis, and interfere, guide, or
							communicate with the team members to help them perform better.</p>
					</figcaption>
				</figure>
			</div>

		</div>
	</section>

	<div class="clearfix"></div>

	<section id="clients">
		<div class="container">
			<h2>Our Clients</h2>
			<p>Our Happiest Clients is here</p>
			<div class="container">
				<div class="row">
					<div class="span12">
						<div class="well">
							<div id="myCarousel" class="carousel fdi-Carousel slide">
								<!-- Carousel items -->
								<div class="carousel fdi-Carousel slide" id="eventCarousel"
									data-interval="0">
									<div class="carousel-inner onebyone-carosel">
										<div class="item active">
											<div class="col-md-4">
												<a href="#"><img
													src="/resources/assets/images/index/client-1.png"
													class="img-responsive center-block"></a>
											</div>
										</div>
										<div class="item">
											<div class="col-md-4">
												<a href="#"><img
													src="/resources/assets/images/index/client-2.png"
													class="img-responsive center-block"></a>
											</div>
										</div>
										<div class="item">
											<div class="col-md-4">
												<a href="#"><img
													src="/resources/assets/images/index/client-3.png"
													class="img-responsive center-block"></a>
											</div>
										</div>
										<div class="item">
											<div class="col-md-4">
												<a href="#"><img
													src="/resources/assets/images/index/client-1.png"
													class="img-responsive center-block"></a>
											</div>
										</div>
										<div class="item">
											<div class="col-md-4">
												<a href="#"><img
													src="/resources/assets/images/index/client-2.png"
													class="img-responsive center-block"></a>
											</div>
										</div>
										<div class="item">
											<div class="col-md-4">
												<a href="#"><img
													src="/resources/assets/images/index/client-3.png"
													class="img-responsive center-block"></a>
											</div>
										</div>
									</div>
									<a class="left carousel-control" href="#eventCarousel"
										data-slide="prev" style="background: transparent;"></a> <a
										class="right carousel-control" href="#eventCarousel"
										data-slide="next" style="background: transparent;"></a>
								</div>
								<!--/carousel-inner-->
							</div>
							<!--/myCarousel-->
						</div>
						<!--/well-->
					</div>
				</div>
			</div>
		</div>
	</section>

	<div class="clearfix"></div>

	<section id="GetStarted">
		<div class="container">
			<h2>Get Started</h2>
			<p>Please get in touch with us for a demo or for more details</p>
			<div class="container" style="margin-top: 40px;">
				<a href="#" class="col-md-4 col-md-offset-2 col-xs-12">REQUEST A
					DEMO</a> <a href="#" class="col-md-4 col-xs-12">FAQ</a>
			</div>
		</div>
	</section>

	<div class="clearfix"></div>

	<section id="phone">
		<div class="container">
			<a href="#" class="col-md-4 col-md-offset-2 col-xs-12 col-sm-6">
				<i class="fa fa-phone"></i> +91 8893 499 106 / 0487 32 99 108
			</a> <a href="#" class="col-md-4 col-xs-12 col-sm-6"> <i
				class="fa fa-envelope"></i> sales@aitrich.com
			</a>
		</div>
	</section>

	<div class="clearfix"></div>

	<footer>
		<div class="container">
			<figure class="col-md-4">
				<img src="/resources/assets/images/index/aitrich_logo.png"
					class="img-responsive">
			</figure>
			<form class="col-md-7 col-xs-12 pull-right text-center">
				<input type="text" placeholder="email" required
					oninvalid="this.setCustomValidity('Please Enter Your Email ID')">
				<button>Sign Up</button>
			</form>
			<div class="col-md-12">
				<div class="col-md-8 col-xs-12 f-nav">
					<ul class="list-group">
						<li>&copy;SalesNrich</li>
						<li><a href="benefits">Benefits / Service</a></li>
						<li><a href="contact">Contact</a></li>
					</ul>
				</div>
				<div class="col-md-4 col-xs-12 social-icons">
					<ul>
						<li><a href="#"><i class="fa fa-facebook"></i></a></li>
						<li><a href="#"><i class="fa fa-twitter"></i></a></li>
						<li><a href="#"><i class="fa fa-google-plus"></i></a></li>
						<li><a href="#"><i class="fa fa-youtube"></i></a></li>
					</ul>
				</div>
			</div>
		</div>
	</footer>

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<spring:url value="/resources/assets/js/index/bootstrap.min.js"
		var="bootstrapJs" />
	<script src="${bootstrapJs}"></script>

	<!-- Scrolling Nav JavaScript -->
	<spring:url value="/resources/assets/js/jquery.easing.min.js"
		var="jqueryEasingJs" />
	<script src="${jqueryEasingJs}"></script>
	<spring:url value="/resources/assets/js/index/scrolling-nav.js"
		var="scrollingJs" />
	<script src="${scrollingJs}"></script>

	<spring:url value="/resources/assets/js/index/jquery.fancybox.pack.js"
		var="fancyboxJs" />
	<script src="${fancyboxJs}"></script>

	<script type="text/javascript">
		$('.fancybox').fancybox();
		$(".fancybox-effects-a").fancybox({
			helpers : {
				title : {
					type : 'outside'
				},
				overlay : {
					speedOut : 0
				}
			}
		});
		$('.fancybox-thumbs').fancybox({
			prevEffect : 'none',
			nextEffect : 'none',

			closeBtn : false,
			arrows : false,
			nextClick : true,

			helpers : {
				thumbs : {
					width : 50,
					height : 50
				}
			}
		});
		$('.carousel').carousel({
			interval : 3000
		});
	</script>

	<!-- custom scripts -->
	<spring:url value="/resources/assets/js/index/custom.js" var="customJs" />
	<script src="${customJs}"></script>
	<spring:url value="/resources/assets/js/index/wheelNav.js"
		var="wheelNavJs" />
	<script src="${wheelNavJs}"></script>

</body>
</html>