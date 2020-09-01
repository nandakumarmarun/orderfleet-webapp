<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<head>
<title>Order Pro | SalesNrich</title>

<!-- Meta -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="author" content="">
<meta name="description" content="SalesNrich OrderPro  is your one stop solution to sales force automation and order processing, made very simple. With sales order being received at the back end within seconds of entering it at the customers' end you save leaps in time and money.">

<meta name="keywords" content="Van Sales,Order Taking,Team Tracking,Sales Excutive Tracking,Sales Excutive Management,tally integration,order management system,sap integration,Manage Contacts and Leads,Lead Management,order taking mobile app,Mobile based sale,stock management,van sales management,salesforce automation">

<meta name="robots" content="follow,index">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- Favicon -->
<spring:url value="/resources/assets/images/index/favicon.ico"
	var="favicon"></spring:url>
<link rel="icon" href="${favicon}">

<!-- Web Fonts 
    <link rel='stylesheet' type='text/css' href='//fonts.googleapis.com/css?family=Open+Sans:400,300,600&amp;subset=cyrillic,latin'>
-->
<!-- CSS Global Compulsory -->
<spring:url
	value="/resources/web-assets/plugins/bootstrap/css/bootstrap.min.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">
<spring:url value="/resources/web-assets/css/style.css" var="styleCss"></spring:url>
<link href="${styleCss}" rel="stylesheet">

<!-- CSS Header and Footer -->
<spring:url value="/resources/web-assets/css/headers/header-default.css"
	var="headerdefault"></spring:url>
<link href="${headerdefault}" rel="stylesheet">
<spring:url value="/resources/web-assets/css/footers/footer-v1.css"
	var="footerv1"></spring:url>
<link href="${footerv1}" rel="stylesheet">

<!-- CSS Implementing Plugins -->
<spring:url value="/resources/web-assets/plugins/animate.css"
	var="animate"></spring:url>
<link href="${animate}" rel="stylesheet">
<spring:url
	value="/resources/web-assets/plugins/line-icons/line-icons.css"
	var="lineicons"></spring:url>
<link href="${lineicons}" rel="stylesheet">
<spring:url
	value="/resources/web-assets/plugins/font-awesome/css/font-awesome.min.css"
	var="fontawesome"></spring:url>
<link href="${fontawesome}" rel="stylesheet">
<spring:url
	value="/resources/web-assets/plugins/owl-carousel/owl-carousel/owl.carousel.css"
	var="carousel"></spring:url>
<link href="${carousel}" rel="stylesheet">


<spring:url
	value="/resources/web-assets/plugins/sky-forms-pro/skyforms/css/sky-forms.css"
	var="skyFormsCss"></spring:url>
<link href="${skyFormsCss}" rel="stylesheet">

<spring:url
	value="/resources/web-assets/plugins/sky-forms-pro/skyforms/custom/custom-sky-forms.css"
	var="customSkyFormsCss"></spring:url>
<link href="${customSkyFormsCss}" rel="stylesheet">

<!-- CSS Customization -->

<spring:url value="/resources/web-assets/css/custom.css" var="customCss"></spring:url>
<link href="${customCss}" rel="stylesheet">

<jsp:include page="../fragments/google-analytics.jsp"></jsp:include>
</head>

<body>
	<div class="wrapper">
		<!--=== Header ===-->
		<div class="header">
			<div class="container">
				<!-- Logo -->
				<a class="logo" href="index"> <img
					src="/resources/web-assets/img/logo1.png" alt="Logo">
				</a>
				<!-- End Logo -->
				<!-- Topbar -->
				<div class="topbar">
					<ul class="loginbar pull-right">
						<!--
                    <li class="hoverSelector">
                        <i class="fa fa-globe"></i>
                        <a>Languages</a>
                        <ul class="languages hoverSelectorBlock">
                            <li class="active">
                                <a >English <i class="fa fa-check"></i></a>
                            </li>
                            <li><a >Spanish</a></li>
                            <li><a >Russian</a></li>
                            <li><a >German</a></li>
                        </ul>
                    </li>-->
                    	<li><a>+91 9846 056 008</a></li>
						<li class="topbar-devider"></li>
                    	<li><a>+91 9387 007 657</a></li>
						<li class="topbar-devider"></li>
						<li><a>+91 8893 499 106</a></li>
						<li class="topbar-devider"></li>
						<li><a>Help</a></li>
						<li class="topbar-devider"></li>
						<li><a href="../login">Login</a></li>
						<!-- <li class="topbar-devider"></li>
						<li><a href="../register">Register</a></li> -->
					</ul>
				</div>
				<!-- End Topbar -->
				<!-- Toggle get grouped for better mobile display -->
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-responsive-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="fa fa-bars"></span>
				</button>
				<!-- End Toggle -->
			</div>
			<!--/end container-->
			<!--<p style="float:left;margin-left:350px; margin-top:10px;font-size:16px;color:#09F;"> 
                    	Solutions For
                       <span class="glyphicon glyphicon-play"></span>
                        
                    </p>-->
			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse navbar-responsive-collapse">
				<div class="container">
					<ul class="nav navbar-nav">
						<!--<li class="solutions"> 
                    	Solutions For
                       <span class="glyphicon glyphicon-play"></span>
                    </li>-->
						<!-- Pages -->
						<li><a href="index">Home</a></li>
						<li class="dropdown active"><a href="javascript:void(0);"
							class="dropdown-toggle" data-toggle="dropdown"> Products </a>
							<ul class="dropdown-menu">
								<!--  <li class="active"><a href="index">Option 1: Default Page</a></li>-->
								<li class="active"><a href="marketpro">Market Pro</a></li>
								<li class=""><a>BMet</a></li>
								<li class=""><a href="pharmaplus">Pharme Plus</a></li>
								<li class=""><a href="orderpro">Order Pro</a></li>
								<li class=""><a href="vansales">Van Sales</a></li>
								<li class=""><a>Sales360</a></li>
								<!-- End About Pages -->
							</ul></li>
						<!-- End Pages -->
						<!-- Blog -->
						<!-- <li class="dropdown"> <a href="#l" class="dropdown-toggle" data-toggle="dropdown"> Services </a>
              <ul class="dropdown-menu">
                <li class="dropdown-submenu">
                                <a href="javascript:void(0);">Blog Large Image</a>
                            </li>
              </ul>
            </li> -->
						<!-- End Blog -->
						<!-- Pages -->
						<!--  <li ><a  >About Us</a></li> -->
						<li><a href="contactUs">Contact Us</a></li>
						<!-- End Pages -->
					</ul>
				</div>
				<!--/end container-->
			</div>
			<!--/navbar-collapse-->
		</div>
		<!--=== End Header ===-->

		<!--=== Breadcrumbs v3 ===-->
		<div class="breadcrumbs-v3 img-v3 text-center margin-bottom-60">
			<div class="container">
				<p>Cloud and Mobile based Sales Force Automation System</p>
				<h1>for OrderPro</h1>

			</div>
		</div>
		<!--=== End Breadcrumbs v3 ===-->

		<div class="container margin-bottom-40">
			<div class="row">
				<div class="col-md-6">
					<h2 class="title-v2">Order Pro</h2>
					<!-- <p>SalesNrich Market pro helps to scale your business with monitored marketing activities. Hasten your sales and marketing activities in an effective and efficient manner. See the span growth of your firm in your target market live and on a daily basis. Avoid the middleman errs by auto guiding the activities of your representatives on the go.</p>
                
                 <p>Approach individual customers with the complete know-how on previous encounters. Having a SaleNrich will allow you to intentionally pursue new business systematically for target accounts. Whichever domain you may be market pro is your solution to exponential productivity with minimum effort. Make bespoke strategies that would engrave your firm in the market share.</p><br>-->
					<p>
						<strong>SalesNrich OrderPro</strong> is your one stop solution to
						sales force automation and order processing, made very simple.
						With sales order being received at the back end within seconds of
						entering it at the customers' end you save leaps in time and
						money. Be delivery ready the very same day of receiving the order.
						Next to zero data entry in turn cultivates into high efficiency
						and productivity. Monitor the entire process from order to
						shipment to delivery on the go. Your customer satisfaction
						guaranteed with quick order fulfilment and real time response to
						their grievances..
					</p>


				</div>
				<div class="col-md-6">
					<img class="img-responsive"
						src="/resources/web-assets/img/mockup/marketpro.png" alt="">
				</div>
			</div>
		</div>

		<!--=== Featured Blog ===-->
		<div class="bg-color-blue">
			<div class="container content-sm no-bottom-space">

				<div class="title-box margin-bottom-40">
					<div class="title-box-text">We have the tools to help your
						team work well and find success</div>
					<p>SalesNrich has multiple software products designed to
						optimize performance and enhance specific functionalities related
						to sales, marketing, and service.</p>
				</div>

				<!-- <a  class="btn-u btn-u-dark btn-brd btn-brd-hover">Learn More About Unify</a>-->

				<div class="row margin-bottom-40">
					<div class="col-md-4">
						<div class="content-boxes-v5 margin-bottom-30">
							<i class="rounded-x icon-layers"></i>
							<div class="overflow-h">
								<h3 class="no-top-space">Product Management</h3>
								<p>Proin et augue vel nisi rhoncus tincidunt. Cras venenatis</p>
							</div>
						</div>
						<div class="content-boxes-v5 md-margin-bottom-30">
							<i class="rounded-x icon-settings"></i>
							<div class="overflow-h">
								<h3 class="no-top-space">Employee Management</h3>
								<p>Proin et augue vel nisi rhoncus tincidunt. Cras venenatis</p>
							</div>
						</div>
					</div>
					<div class="col-md-4">
						<div class="content-boxes-v5 margin-bottom-30">
							<i class="rounded-x icon-earphones-alt "></i>
							<div class="overflow-h">
								<h3 class="no-top-space">Account Management</h3>
								<p>Proin et augue vel nisi rhoncus tincidunt. Cras venenatis</p>
							</div>
						</div>
						<div class="content-boxes-v5 md-margin-bottom-30">
							<i class="rounded-x icon-user "></i>
							<div class="overflow-h">
								<h3 class="no-top-space">Trittory Management</h3>
								<p>Proin et augue vel nisi rhoncus tincidunt. Cras venenatis</p>
							</div>
						</div>
					</div>
					<div class="col-md-4">
						<div class="content-boxes-v5 margin-bottom-30">
							<i class="rounded-x icon-wrench"></i>
							<div class="overflow-h">
								<h3 class="no-top-space">Marketing Management</h3>
								<p>Proin et augue vel nisi rhoncus tincidunt. Cras venenatis</p>
							</div>
						</div>
					</div>
				</div>

			</div>



		</div>
		<!--=== End Featured Blog ===-->


		<!--=== Featured Blog ===-->
		<div class="bg-color-dark-blue">
			<div class="container content-sm no-bottom-space">

				<div class="title-box margin-bottom-40">
					<div class="title-box-text">Make a request for Market Pro
						Demo</div>
				</div>

				<!-- <a  class="btn-u btn-u-dark btn-brd btn-brd-hover">Learn More About Unify</a>-->
			</div>

			<div class="container">

				<div class="mb-margin-bottom-30">
					<!-- Order Form -->
					<form action="assets/php/demo-order.php" method="post"
						enctype="multipart/form-data" id="sky-form1" class="sky-form">

						<fieldset>
							<div class="row">
								<div class="margin-bottom-40"></div>
								<section class="col col-6">
									<label class="input"> <i class="icon-append fa fa-user"></i>
										<input type="text" name="name" placeholder="Name">
									</label>
								</section>
								<section class="col col-6">
									<label class="input"> <i
										class="icon-append fa fa-briefcase"></i> <input type="text"
										name="company" placeholder="Company">
									</label>
								</section>
							</div>

							<div class="row">
								<section class="col col-6">
									<label class="input"> <i
										class="icon-append fa fa-envelope"></i> <input type="email"
										name="email" placeholder="E-mail">
									</label>
								</section>
								<section class="col col-6">
									<label class="input"> <i
										class="icon-append fa fa-phone"></i> <input type="tel"
										name="phone" placeholder="Phone">
									</label>
								</section>
							</div>
						</fieldset>

						<fieldset>
							<div class="row">

								<section class="col col-6">
									<label class="select"> <select name="budget">
											<option value="0" selected disabled>Employees</option>
											<option value="less than 5000$">1 - 50</option>
											<option value="5000$ - 10000$">50 - 100$</option>
											<option value="10000$ - 20000$">100 - 200</option>
											<option value="more than 20000$">more than 200</option>
									</select> <i></i>
									</label>
								</section>
								<section class="col col-6">
									<label class="select"> <select name="interested">
											<option value="none" selected disabled>Country</option>
											<option value="design">Inida</option>
											<option value="development">UAE</option>
											<option value="illustration">Saudi Arabia</option>
											<option value="branding">Oman</option>
											<option value="video">United States</option>
									</select> <i></i>
									</label>
								</section>
							</div>

							<div class="row">
								<!-- <section class="col col-6">
                                    <label class="select">
                                        <select name="interested">
                                            <option value="none" selected disabled>Product Interest </option>
                                            <option value="design">Market Pro</option>
                                            <option value="development">BMet</option>
                                            <option value="illustration">Pharma Plus</option>
                                            <option value="branding">Order Pro</option>
                                            <option value="video">Van Sales</option>
                                            <option value="video">Sales360</option>
                                        </select>
                                        <i></i>
                                    </label>
                          </section>
                            <section class="col col-6">
                                <label class="input">
                                    <i class="icon-append fa fa-calendar"></i>
                                    <input type="text" name="start" id="start" placeholder="Expected start date">
                                </label>
                            </section>
                            <section class="col col-6">
                                <label class="input">
                                    <i class="icon-append fa fa-calendar"></i>
                                    <input type="text" name="finish" id="finish" placeholder="Expected finish date">
                                </label>
                            </section>-->
							</div>

							<!--<section>
                            <label for="file" class="input input-file">
                                <div class="button"><input type="file" name="file" multiple onchange="this.parentNode.nextSibling.value = this.value">Browse</div><input type="text" placeholder="Include some file" readonly>
                            </label>
                        </section>-->

							<section>
								<label class="textarea"> <i
									class="icon-append fa fa-comment"></i> <textarea rows="5"
										name="comment" placeholder="Tell us about your project"></textarea>
								</label>
							</section>
						</fieldset>
						<footer>
							<button type="button" class="btn-u">Send request</button>
							<div class="progress"></div>
						</footer>

						<div class="message">
							<i class="rounded-x fa fa-check"></i>
							<p>
								Thanks for your order!<br>We'll contact you very soon.
							</p>
						</div>

					</form>
					<div class="container margin-bottom-60"></div>
					<!-- End Order Form -->
				</div>
			</div>

		</div>
		<!--=== End Featured Blog ===-->

		<!--=== Parallax Counter v4 ===-->
		<div class="testimonials-v3 bg-image-v2">
			<div class="container">
				<div class="row">
					<div class="col-md-8 col-md-offset-2">
						<div class="headline-center-v2 headline-center-v2-dark">
							<h2>What Our Clients Say About Us</h2>
							<span class="bordered-icon"><i class="fa fa-th-large"></i></span>
						</div>
						<ul class="list-unstyled owl-ts-v1">
							<li class="item"><img class="rounded-x img-bordered"
								src="/resources/web-assets/img/team/img3-sm.jpg" alt="">
								<div class="testimonials-v3-title">
									<p>Shakkira</p>
									<span>Administrator, Modern Distropolis</span>
								</div>
								<p>
									I just wanted to tell you how much I like to use - <strong>it's
										so sleek and elegant!</strong> <br> The customisation options you
									implemented are countless. Good job, and keep going!
								</p></li>

						</ul>
					</div>
				</div>
			</div>
		</div>
		<!--=== End Parallax Counter v4 ===-->
		<!--=== Image Block ===-->
		<div class="content-md">
			<div class="container">
				<!-- Service Box -->
				<div class="row text-center">
					<div class="col-md-4 flat-service">
						<span
							class="rounded-x  glyphicon glyphicon-earphone color-dark-blue"
							style="font-size: 50px;"></span>
						<h1 class="title-v1-md margin-bottom-10">Call us</h1>
						<h3>Get help choosing a solution.</h3>
						<p>CALL +91 -938700-7657</p>
					</div>
					<div class="col-md-4 flat-service">
						<span
							class="rounded-x  glyphicon glyphicon-comment color-dark-blue"
							style="font-size: 50px;"></span>
						<h1 class="title-v1-md margin-bottom-10">Chat with us</h1>
						<h3>Find out how to get started.</h3>
						<p>
							<a>Start Chat Section</a>
						</p>
					</div>
					<div class="col-md-4 flat-service">

						<span
							class="rounded-x  glyphicon glyphicon-thumbs-up color-dark-blue"
							style="font-size: 50px;"></span>
						<h1 class="title-v1-md margin-bottom-10">Request we contact
							you</h1>
						<h3>Have a SalesNrich expert contact you.</h3>
						<p>
							<a>Send Request</a>
						</p>
					</div>
				</div>
				<!-- End Service Box -->
			</div>
			<!--/container -->

			<!-- Parallax Section -->
			<!-- End Parallax Section -->
			<!--/end container-->

			<!-- Flat Background Block -->
			<!-- End Flat Background Block -->
			<!--/end container-->

			<!-- Flat Testimonials -->
			<!-- End Flat Testimonials -->
			<!--/end container-->
		</div>
		<!--=== End Image Block ===-->

		<!--=== Service Block ===-->
		<!--=== End Service Block ===-->

		<!--=== Owl Clients v1 ===-->
		<!--=== End Owl Clients v1 ===-->

		<!--=== Footer Version 1 ===-->
		<div class="footer-v1">
			<!--/footer-->
			<div class="footer">
				<div class="container">
					<div class="row">
						<!-- About -->
						<div class="col-md-3 md-margin-bottom-40">
							<a href="index"><img id="logo-footer" class="footer-logo"
								src="/resources/web-assets/img/logo1.png" alt=""></a>
							<p>SalesNrich OrderPro is your one stop solution to sales
								force automation and order processing,</p>
							<p>made very simple. With sales order being received at the
								back end within seconds of entering it at the customers' end you
								save...</p>
						</div>
						<!--/col-md-3-->
						<!-- End About -->
						<!-- Latest -->
						<div class="col-md-3 md-margin-bottom-40">
							<div class="posts">
								<div class="headline">
									<h2>Latest Features</h2>
								</div>
								<ul class="list-unstyled latest-list">
									<li><a>Custom Report Designer</a> <small>May 8, 2017</small></li>
									<li><a>Intelligent Analytics</a> <small>June 23, 2016</small></li>
									<li><a>Predictive Customer Support</a> <small>September 15,
											2015</small></li>
								</ul>
							</div>
						</div>
						<!--/col-md-3-->
						<!-- End Latest -->
						<!-- Link List -->
						<div class="col-md-3 md-margin-bottom-40">
							<div class="headline">
								<h2>Useful Links</h2>
							</div>
							<ul class="list-unstyled link-list">
								<li><a>About us</a><i class="fa fa-angle-right"></i></li>
								<li><a>Products</a><i class="fa fa-angle-right"></i></li>
								<li><a>Services</a><i class="fa fa-angle-right"></i></li>
								<li><a>FAQ</a><i class="fa fa-angle-right"></i></li>
								<li><a href="contactUs">Contact us</a><i
									class="fa fa-angle-right"></i></li>
							</ul>
						</div>
						<!--/col-md-3-->
						<!-- End Link List -->
						<!-- Address -->
						<div class="col-md-3 map-img md-margin-bottom-40">
							<div class="headline">
								<h2>Contact Us</h2>
							</div>
							<address class="md-margin-bottom-40">
								Aitrich Technologies Pvt Ltd. <br /> 9/60. IInd Floor Visitors
								Building<br /> MG Road,Thrissur-4, Kerala <br /> Phone: 91
								9387007657, <br /> Mobile: 91 9846056008 <br /> Email: <a
									href="mailto:sales@salesnrich.com" class="">sales@salenrich.com</a>
							</address>
						</div>
						<!--/col-md-3-->
						<!-- End Address -->
					</div>
				</div>
			</div>
			<div class="copyright">
				<div class="container">
					<div class="row">
						<div class="col-md-6">
							<p>
								2011 &copy; All Rights Reserved. <a>Privacy Policy</a> | <a>Terms
									of Service</a>
							</p>
						</div>
						<!-- Social Links -->
						<div class="col-md-6">
							<ul class="footer-socials list-inline">
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Facebook">
										<i class="fa fa-facebook"></i>
								</a></li>
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Skype">
										<i class="fa fa-skype"></i>
								</a></li>
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Google Plus">
										<i class="fa fa-google-plus"></i>
								</a></li>
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Linkedin">
										<i class="fa fa-linkedin"></i>
								</a></li>
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Pinterest">
										<i class="fa fa-pinterest"></i>
								</a></li>
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Twitter">
										<i class="fa fa-twitter"></i>
								</a></li>
								<li><a class="tooltips" data-toggle="tooltip"
									data-placement="top" title="" data-original-title="Dribbble">
										<i class="fa fa-dribbble"></i>
								</a></li>
							</ul>
						</div>
						<!-- End Social Links -->
					</div>
				</div>
			</div>
			<!--/copyright-->
		</div>
		<!--=== End Footer Version 1 ===-->
	</div>
	<!--/wrapper-->

	<!-- JS Global Compulsory -->
	<spring:url value="/resources/web-assets/plugins/jquery/jquery.min.js"
		var="jqueryMin" />
	<script src="${jqueryMin}"></script>
	<spring:url
		value="/resources/web-assets/plugins/jquery/jquery-migrate.min.js"
		var="migrateMinJS" />
	<script src="${migrateMinJS}"></script>
	<spring:url
		value="/resources/web-assets/plugins/bootstrap/js/bootstrap.min.js"
		var="bootstrapMinJS" />
	<script src="${bootstrapMinJS}"></script>
	<!-- JS Implementing Plugins -->
	<spring:url value="/resources/web-assets/plugins/back-to-top.js"
		var="backToTopJS" />
	<script src="${backToTopJS}"></script>
	<spring:url value="/resources/web-assets/plugins/smoothScroll.js"
		var="smoothScrollJS" />
	<script src="${smoothScrollJS}"></script>
	<spring:url value="/resources/web-assets/plugins/jquery.parallax.js"
		var="jqueryParallaxJS" />
	<script src="${jqueryParallaxJS}"></script>
	<spring:url
		value="/resources/web-assets/plugins/counter/waypoints.min.js"
		var="waypointsJS" />
	<script src="${waypointsJS}"></script>

	<spring:url
		value="/resources/web-assets/plugins/counter/jquery.counterup.min.js"
		var="counterupMinJS" />
	<script src="${counterupMinJS}"></script>
	<spring:url
		value="/resources/web-assets/plugins/owl-carousel/owl-carousel/owl.carousel.js"
		var="owlCarouselJS" />
	<script src="${owlCarouselJS}"></script>
	<!-- JS Customization -->
	<spring:url value="/resources/web-assets/js/custom.js" var="customJS" />
	<script src="${customJS}"></script>
	<!-- JS Page Level -->
	<spring:url value="/resources/web-assets/js/app.js" var="appJS" />
	<script src="${appJS}"></script>
	<spring:url value="/resources/web-assets/js/plugins/owl-carousel.js"
		var="owlCarouselJS" />
	<script src="${owlCarouselJS}"></script>
	<script type="text/javascript">
		jQuery(document).ready(function() {
			App.init();
			App.initCounter();
			App.initParallaxBg();
			OwlCarousel.initOwlCarousel();
		});
	</script>
	<!--[if lt IE 9]>
    <script src="web-assets/plugins/respond.js"></script>
    <script src="web-assets/plugins/html5shiv.js"></script>
    <script src="web-assets/plugins/placeholder-IE-fixes.js"></script>
<![endif]-->

	<!-- Global site tag (gtag.js) - Google Analytics -->
	<script async
		src="https://www.googletagmanager.com/gtag/js?id=UA-110550224-1"></script>
	<script>
		window.dataLayer = window.dataLayer || [];
		function gtag() {
			dataLayer.push(arguments);
		}
		gtag('js', new Date());

		gtag('config', 'UA-110550224-1');
	</script>
</body>
</html>