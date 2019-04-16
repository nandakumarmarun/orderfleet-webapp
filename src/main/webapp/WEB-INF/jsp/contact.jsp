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

<title>SalesNrich | Contact</title>

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

<!-- styles using for SalesNrich -->
<spring:url value="/resources/assets/css/index/style.css" var="styleCss"></spring:url>
<link href="${styleCss}" rel="stylesheet">

</head>
<body id="contact">

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
								style="background: #32475c;"></i> <a href="index#salesnrich"
								style="background: #425a72;">FMCG</a></li>
							<li class="Pharma"><i class="fa fa-cog"
								style="background: #ae6cc4;"></i> <a href="index#pharma"
								style="background: #b979d0;">Pharma</a></li>
							<li class="Building"><i class="fa fa-cog"
								style="background: #43ace3;"></i> <a href="index#building"
								style="background: #46b3ec;">Building</a></li>
							<li class="foodNbeverage"><i class="fa fa-cog"
								style="background: #23caae;"></i> <a href="index#foodBeverage"
								style="background: #26d5b8;">Food / Beverage</a></li>
							<li class="institution"><i class="fa fa-cog"
								style="background: #39d886;"></i> <a href="index#Institution"
								style="background: #3bdd8a;">Institution</a></li>
							<li><i class="fa fa-cog" style="background: #ec932b;"></i> <a
								style="background: #f69c2e;">More</a></li>
						</ul></li>

					<li><a class="page-scroll" href="benefits">Benefits /
							Services</a></li>
					<li class="active"><a class="page-scroll" href="contact">Contact</a>
					</li>
					<li><a class="page-scroll" href="#">+91 8893 499 106</a></li>
					<li>
						<!-- <a class="page-scroll"  id="reqDemo" href="#animatedModal">Trial Demo</a> -->
						<a class="page-scroll" href="login">LOGIN</a>
					</li>
				</ul>

			</div>
			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container -->
	</nav>

	<!-- main content -->

	<section id="contact-main">
		<article class="container">
			<div class="col-md-12 first">
				<!-- support -->
				<div class="col-xs-12 col-sm-6 col-md-4 col-lg-4 support">
					<h3>Support</h3>
					<img src="/resources/assets/images/index/support.png">
					<h4>
						<a href="tel:+91 8893 499 106">+91 8893 499 106</a>
					</h4>
					<p>
						<a href="mailto: sales@aitrich.com"> sales@aitrich.com</a>
					</p>
				</div>
				<!-- Billing -->
				<div class="col-xs-12 col-sm-6 col-md-4 col-lg-4 billing">
					<h3>billing</h3>
					<img src="/resources/assets/images/index/billing.png">
					<h4>
						<a href="tel:0487 32 99 108">0487 32 99 108</a>
					</h4>
					<p>
						<a href="mailto: sales@aitrich.com"> sales@aitrich.com</a>
					</p>
				</div>
				<!-- Sales -->
				<div
					class="col-xs-12 col-sm-6 col-sm-offset-3 col-md-offset-0 col-md-4 col-lg-4 sales">
					<h3>sales</h3>
					<img src="/resources/assets/images/index/chat.png">
					<h4>
						<a href="tel:+91 9387 007 657">+91 9387 007 657</a>
					</h4>
					<p>
						<a href="mailto: sales@aitrich.com"> sales@aitrich.com</a>
					</p>
				</div>
			</div>
			<div class="clearfix"></div>
			<div class="col-md-12 contact-help-form">
				<h2>How can we help you?</h2>
				<p>We are happy to hear from you. Send us your valuable message!</p>
				<div class="col-md-7 col-xs-12 col-sm-12 contact-form">
					<form id="contact-demo-form">
						<div class="col-lg-12 col-xs-12 col-sm-12">
							<div class="form-group ">
								<input type="text" class="form-control" placeholder="Name">
							</div>
						</div>
						<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 col-sm-12">
							<div class="form-group">
								<input type="email" class="form-control" placeholder="Email">
							</div>
						</div>
						<div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 col-sm-12">
							<div class="form-group">
								<input type="text" class="form-control" placeholder="Telephone">
							</div>
						</div>
						<div class="col-lg-12 col-xs-12 col-sm-12">
							<div class="form-group">
								<input type="text" class="form-control" placeholder="Subject">
							</div>
						</div>
						<div class="col-lg-12 col-xs-12 col-sm-12">
							<div class="form-group">
								<textarea placeholder="comments" style="height: 115px;" rows="5"
									class="form-control"></textarea>
							</div>
						</div>
						<div class="clearfix"></div>
						<div class="col-sm-12">
							<button type="button" class="contact-btn" id="contact-btn">Send</button>
						</div>
					</form>
					<div class="clearfix"></div>
					<div class="col-lg-6 col-md-12 col-sm-12 contact-address">
						<h3>SalesNrich | INDIA</h3>
						<p>
							Aitrich Tecnologies Pvt Ltd.&nbsp;<br> Visitors Building<br>
							MG Road, Thissur<br> Kerala, India - 680001
						</p>
					</div>
					<div class="col-lg-6 col-md-12 col-sm-12 quick-contact">
						<h3></h3>
						<p>
							Email: <a href="mailto:info@trackbizz.com">sales@aitrich.com</a>
							Phone: +91 8893 499 106<br> Office: 0487 32 99 108
						</p>
					</div>
				</div>
			</div>
		</article>
	</section>

	<div class="clearfix"></div>

	<footer>
		<div class="container">
			<figure class="col-md-4">
				<img src="/resources/assets/images/index/aitrich_logo.png"
					class="img-responsive">
			</figure>
			<form class="col-md-7 col-xs-12 pull-right text-center">
				<input type="text" placeholder="email" required>
				<button class="">sign</button>
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

	<!--animatedModal DEMO-->
	<div id="animatedModal" class="demo-model"
		style="background-color: rgb(254, 254, 254) !important;">
		<!--THIS IS IMPORTANT! to close the modal, the class name has to match the name given on the ID -->
		<!-- <div id="btn-close-modal" class="close-animatedModal"> 
            CLOSE MODAL
        </div> -->
		<div class="container">
			<div id="btn-close-modal" class="close-animatedModal close-modal">
			</div>
		</div>

		<div class="modal-content">
			<h3>Request A Demo</h3>
			<form class="modal-form" id="request-demo-form"
				data-toggle="validator">
				<div class="col-lg-12">
					<input type="text" class="form-control" placeholder="Name" required>
				</div>
				<div class="col-lg-6">
					<input type="text" class="form-control" placeholder="Company"
						required>
				</div>
				<div class="col-lg-6">
					<input type="text" class="form-control" placeholder="Location"
						required>
				</div>
				<div class="col-lg-6">
					<input type="text" class="form-control" placeholder="Industry"
						required>
				</div>
				<div class="col-lg-6">
					<input type="email" class="form-control" placeholder="Email"
						required>
				</div>
				<div class="col-lg-6">
					<input type="text" class="form-control" placeholder="Phone Number"
						required>
				</div>
				<div class="col-lg-6">
					<input type="text" class="form-control" placeholder="Mobile No"
						required>
				</div>
				<div class="col-lg-12">
					<textarea class="form-control" rows="5"
						placeholder="Any special description or requirement"></textarea>
				</div>
				<button type="submit" class="contact-btn">Send</button>
			</form>
		</div>

	</div>


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

	<spring:url value="/resources/assets/js/index/animatedModal.js"
		var="animatedModalJs" />
	<script src="${animatedModalJs}"></script>

	<!-- animate model -->
	<script type="text/javascript">
		$("#reqDemo").animatedModal();
	</script>

	<!-- custom scripts -->
	<spring:url value="/resources/assets/js/index/custom.js" var="customJs" />
	<script src="${customJs}"></script>

</body>
</html>