<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<head>
<title>SalesNrich | aitrich Tehnologies</title>

<!-- Meta -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="author" content="">
<meta name="description" content="SalesNrich is a Best crm software system in Thrissur .It's helps to Van Sales,Order Taking,Team Tracking,Sales executive Tracking,tally integration,And It also help to Manage Contacts and Leads.
">

<meta name="keywords" content="Van Sales,Order Taking,Team Tracking,Sales Excutive Tracking,Sales Excutive Management,tally integration,order management system,sap integration,Manage Contacts and Leads,Lead Management,order taking mobile app,Mobile based sale,stock management,van sales management">

<meta name="robots" content="follow,index">
<meta name="p:domain_verify" content="d58647af00127e8383edd5fb3c2f99ef"/>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- Favicon -->
<spring:url value="/resources/assets/images/index/favicon.ico"
	var="favicon"></spring:url>
<link rel="icon" href="${favicon}">



<!-- Bootstrap CSS Global Compulsory -->
<spring:url
	value="/resources/web-assets/plugins/bootstrap/css/bootstrap.min.css"
	var="bootstrapCss"></spring:url>
<link href="${bootstrapCss}" rel="stylesheet">

<!-- CSS Theme -->
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
	value="/resources/web-assets/plugins/fancybox/source/jquery.fancybox.css"
	var="fancybox"></spring:url>
<link href="${fancybox}" rel="stylesheet">

<spring:url
	value="/resources/web-assets/plugins/owl-carousel/owl-carousel/owl.carousel.css"
	var="carousel"></spring:url>
<link href="${carousel}" rel="stylesheet">

<spring:url
	value="/resources/web-assets/plugins/revolution-slider/rs-plugin/css/settings.css"
	var="settings"></spring:url>
<link href="${settings}" rel="stylesheet" media="screen">

<!-- CSS Customization -->
<spring:url value="/resources/web-assets/css/custom.css" var="custom"></spring:url>
<link href="${custom}" rel="stylesheet">


<!--[if lt IE 9]><link rel="stylesheet" href="web-assets/plugins/revolution-slider/rs-plugin/css/settings-ie8.css" type="text/css" media="screen"><![endif]-->

<jsp:include page="../fragments/google-analytics.jsp"></jsp:include>
</head>

<body>
	<div class="wrapper page-option-v1">
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
						<li><a>+91 8893 499 106</a></li>
						<li class="topbar-devider"></li>
						<li><a>Help</a></li>
						<li class="topbar-devider"></li>
						<li><a href="../login">Login</a></li>
						<li class="topbar-devider"></li>
						<li><a href="../register">Register</a></li>


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
					<ul class="nav navbar-nav ">
						<!--<li class="solutions"> 
                    	Solutions For
                       <span class="glyphicon glyphicon-play"></span>
                    </li>-->
						<!-- Pages -->
						<li class="active"><a href="index">Home</a></li>
						<li class="dropdown"><a href="javascript:void(0);"
							class="dropdown-toggle" data-toggle="dropdown"> Products </a>
							<ul class="dropdown-menu">
								<!--  <li class="active"><a href="index">Option 1: Default Page</a></li>-->
								<li class=""><a href="marketpro">Market Pro</a></li>
								<li class=""><a>BMet</a></li>
								<li class=""><a href="pharmaplus">Pharme Plus</a></li>
								<li class=""><a href="orderpro">Order Pro</a></li>
								<li class=""><a href="vansales">Van Sales</a></li>
								<li class=""><a>Sales360</a></li>

								<!-- End About Pages -->
							</ul></li>
						<!-- End Pages -->

						<!-- Blog -->
						<!-- <li class="dropdown"><a  class="dropdown-toggle"
							data-toggle="dropdown"> Services </a>
							<ul class="dropdown-menu">
								<li class="dropdown-submenu">
                                <a href="javascript:void(0);">Blog Large Image</a>
                            </li>
							</ul></li> -->
						<!-- End Blog -->





						<!-- Pages -->
						<!-- <li><a >About Us</a></li> -->
						<li><a href="contactUs">Contact Us</a></li>
						<!-- End Pages -->


					</ul>

				</div>
				<!--/end container-->

			</div>
			<!--/navbar-collapse-->

		</div>
		<!--=== End Header ===-->




		<!--=== Slider ===-->
		<div class="tp-banner-container">
			<div class="tp-banner">
				<ul>
					<!-- SLIDE -->
					<li class="revolution-mch-1" data-transition="fade"
						data-slotamount="5" data-masterspeed="1000" data-title="Slide 1">
						<!-- MAIN IMAGE --> <img
						src="/resources/web-assets/img/sliders/sales.jpg" alt="darkblurbg"
						data-bgfit="cover" data-bgposition="left top"
						data-bgrepeat="no-repeat">

						<div class="tp-caption revolution-ch1 sft start" data-x="center"
							data-hoffset="0" data-y="100" data-speed="1500" data-start="500"
							data-easing="Back.easeInOut" data-endeasing="Power1.easeIn"
							data-endspeed="300">
							<!-- Introducing  Template-->
						</div> <!-- LAYER 
                    <div class="tp-caption revolution-ch2 sft"
                        data-x="center"
                        data-hoffset="0"
                        data-y="190"
                        data-speed="1400"
                        data-start="2000"
                        data-easing="Power4.easeOut"
                        data-endspeed="300"
                        data-endeasing="Power1.easeIn"
                        data-captionhidden="off"
                        style="z-index: 6">
                        We are creative technology company providing <br/>
                        key digital services on web and mobile.
                    </div>--> <!-- LAYER -->
						<div class="tp-caption sft" data-x="center" data-hoffset="0"
							data-y="310" data-speed="1600" data-start="2800"
							data-easing="Power4.easeOut" data-endspeed="300"
							data-endeasing="Power1.easeIn" data-captionhidden="off"
							style="z-index: 6; margin-top: 50px;">
							<!--<a  class="btn-u " style="margin-right:25px;">Watch Video</a>
                        <a  class="btn-u">Learn More </a>-->
						</div>
					</li>
					<!-- END SLIDE -->

				</ul>
				<div class="tp-bannertimer tp-bottom"></div>
			</div>
		</div>
		<!--=== End Slider ===-->

		<!--=== Purchase Block ===-->
		<div class="purchase margin-bottom-30" style="background-color: #FFF;">
			<div class="container ">
				<div class="row ">
					<div class="col-md-9 animated fadeInLeft">
						<h1>A platform for your team to work in a much organized
							manner</h1>

						<p>SalesNrich enables mobility to field staffs and automates
							various business processes related to sales, marketing, and
							supply chain. It helps you sell more, deliver faster, and gives
							you more control over field activities.</p>
					</div>
					<div class="col-md-3 btn-buy animated fadeInRight">
						<a class="btn-u btn-u-lg"><i class="fa fa-book"></i> Learn
							More</a>
					</div>
				</div>
			</div>
		</div>
		<!--/row-->
		<!-- End Purchase Block -->


		<!--=== Service Block ===-->
		<div class="container">

			<div class="title-box-bg">
				<div class="title-box-text-bg">We have the tools to help your
					team work well and find success</div>
				<p>SalesNrich has multiple software products designed to
					optimize performance and enhance specific functionalities related
					to sales, marketing, and service.</p>
			</div>


			<!-- Promo Box -->
			<div class="row">
				<div class="col-md-4">
					<div class="promo-box">
						<i class="fa fa-map-marker color-blue"></i> <strong>Market
							Pro</strong>
						<p>SalesNrich MarketPro helps to scale your business with
							monitored marketing activities. Hasten your sales and marketing
							activities in an effective and efficient manner. See the span
							growth of your firm in your target market...</p>
						<br> <a class="text-uppercase btn-u btn-u-sm"
							href="marketpro">Read More</a>
					</div>
				</div>

				<div class="col-md-4">
					<div class="promo-box">
						<i class="fa fa-building color-blue"></i> <strong>BMet</strong>
						<p>SalesNrich BMet accelerates productivity of the
							sales\marketing team members by providing relevant information
							with appropriate tools & facilities which keeps them well engaged
							in the core business. It also allows ...</p>
						<br> <a class="text-uppercase btn-u btn-u-sm">Read More</a>
					</div>
				</div>

				<div class="col-md-4">
					<div class="promo-box">
						<i class="fa fa-user-plus color-blue"></i> <strong>Pharma
							Plus</strong>
						<p>SalesNrich PharmaPlus is specially designed to enrich sales
							& marketing related activities of your pharmaceutical business.
							It helps you manage daily Call Reports (DCR), Doctors
							appointments and visits, Primary as well as Secondary...</p>
						<br> <a class="text-uppercase btn-u btn-u-sm"
							href="pharmaplus">Read More</a>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-4">
					<div class="promo-box">
						<i class="fa fa-mobile color-blue"></i> <strong>Order Pro</strong>
						<p>SalesNrich OrderPro is your one stop solution to sales
							force automation and order processing, made very simple. With
							sales order being received at the back end within seconds of
							entering it at the customers' end you save leaps in time and ...</p>
						<br> <a class="text-uppercase btn-u btn-u-sm" href="orderpro">Read
							More</a>
					</div>
				</div>

				<div class="col-md-4">
					<div class="promo-box">
						<i class="fa fa-bus color-blue"></i> <strong>Van Sales</strong>
						<p>Say good bye to the time consuming manual data
							consolidation and move on to a simple zero error -prone approach
							with SalesNrich VanSales. VanSales allows you real time
							visibility into billing & van stock at any given time...</p>
						<br> <a class="text-uppercase btn-u btn-u-sm" href="vansales">Read
							More</a>
					</div>
				</div>

				<div class="col-md-4">
					<div class="promo-box">
						<i class="fa fa-rocket color-blue"></i> <strong>Sales-360</strong>
						<p>SalesNrich Sales-360 accelerates productivity of the
							sales\marketing team members by providing relevant information
							with appropriate tools & facilities which keeps them well engaged
							in the core business. It also allows ...</p>
						<br> <a class="text-uppercase btn-u btn-u-sm">Read More</a>
					</div>
				</div>
			</div>

			<!-- End Promo Box -->



			<div class="margin-bottom-60"></div>

			<!--Start Blocks----->
			<!--- End Blcoks----->

		</div>
		<!--/container-->
		<!--=== End Service Block ===-->





		<!--=== Service Block ===-->
		<!--/container-->
		<!--=== End Service Block ===-->

		<!--=== Parallax Backgound ===-->
		<!--=== End Parallax Backgound ===-->

		<!--=== Recent Posts ===-->
		<!--/container-->
		<!--=== End Recent Posts ===-->


		<!-- Flat Background Block -->

		<!-- End Flat Background Block -->



		<!--=== Container Part ===-->
		<div class="bg-color-blue">
			<div class="container content-sm">
				<div class="row">
					<div class="title-box margin-bottom-20">
						<div class="title-box-text">SalesNrich stands apart with
							following business essential features</div>
					</div>
					<div class="col-md-6 md-margin-bottom-50">
						<img class="img-responsive"
							src="/resources/web-assets/img/mockup/home.png" alt="">
					</div>
					<div class="col-md-6">
						<!--<div class="headline-left margin-bottom-30">
                    <h2 class="headline-brd">Features</h2>
                </div>-->
						<div class="margin-bottom-20"></div>
						<ul class="list-unstyled lists-sales margin-bottom-30">
							<li><i class="fa fa-check"></i> Guided Selling</li>
							<li><i class="fa fa-check"></i> Performance Management</li>
							<li><i class="fa fa-check"></i> Survey Management</li>
							<li><i class="fa fa-check"></i> Feedback Management</li>
							<li><i class="fa fa-check"></i> Business Process Automation</li>
							<li><i class="fa fa-check"></i> Delivery Management</li>
							<li><i class="fa fa-check"></i> Seamless Integration with
								partner system</li>
						</ul>
						<a class="btn-u  btn-brd-hover btn-u-dark margin-left-10">Learn
							More</a>

					</div>
					<!--/end row-->
				</div>
			</div>
		</div>
		<!--=== End Container Part ===-->

		<!--=== Container Part ===-->
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
		<!--=== Container Part ===-->

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
							<p>SalesNrich BMet accelerates productivity of the
								sales\marketing team members by providing relevant information</p>
							<p>with appropriate tools & facilities which keeps them well
								engaged in the core business. It also allows...</p>
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
									<li><a>Custom Report Designer</a> <small>May 8,
											2017</small></li>
									<li><a>Intelligent Analytics</a> <small>June 23,
											2016</small></li>
									<li><a>Predictive Customer Support</a> <small>September
											15, 2015</small></li>
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
								<li><a href="contactUs">Contact Us</a><i
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
								Aitrich Technologies Pvt Ltd. <br /> 19/60. IInd Floor Visitors
								Building<br /> MG Road,Thrissur-4, Kerala <br /> Phone: +91
								9387007657, <br /> Mobile: +91 9846056008 <br /> Email: <a
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
		value="/resources/web-assets/plugins/fancybox/source/jquery.fancybox.pack.js"
		var="jqueryFancyboxPackJS" />
	<script src="${jqueryFancyboxPackJS}"></script>

	<spring:url
		value="/resources/web-assets/plugins/owl-carousel/owl-carousel/owl.carousel.js"
		var="owlCarouselJS" />
	<script src="${owlCarouselJS}"></script>

	<spring:url
		value="/resources/web-assets/plugins/revolution-slider/rs-plugin/js/jquery.themepunch.tools.min.js"
		var="themepunchToolsMinJS" />
	<script src="${themepunchToolsMinJS}"></script>

	<spring:url
		value="/resources/web-assets/plugins/revolution-slider/rs-plugin/js/jquery.themepunch.revolution.min.js"
		var="themepunchRevolutionMinJS" />
	<script src="${themepunchRevolutionMinJS}"></script>

	<!-- JS Customization -->

	<spring:url value="/resources/web-assets/js/custom.js" var="customJS" />
	<script src="${customJS}"></script>

	<!-- JS Page Level -->

	<spring:url value="/resources/web-assets/js/app.js" var="appJS" />
	<script src="${appJS}"></script>

	<spring:url value="/resources/web-assets/js/plugins/fancy-box.js"
		var="fancyBoxJS" />
	<script src="${fancyBoxJS}"></script>

	<spring:url value="/resources/web-assets/js/plugins/owl-carousel.js"
		var="owlCarouselJS" />
	<script src="${owlCarouselJS}"></script>

	<spring:url
		value="/resources/web-assets/js/plugins/revolution-slider.js"
		var="revolutionSliderJS" />
	<script src="${revolutionSliderJS}"></script>

	<spring:url value="/resources/web-assets/js/plugins/progress-bar.js"
		var="progressBarJS" />
	<script src="${progressBarJS}"></script>


	<script type="text/javascript">
		jQuery(document).ready(function() {
			App.init();
			App.initParallaxBg();
			FancyBox.initFancybox();
			OwlCarousel.initOwlCarousel();
			RevolutionSlider.initRSfullWidth();
			ProgressBar.initProgressBarVertical();
		});
	</script>
</body>
</html>