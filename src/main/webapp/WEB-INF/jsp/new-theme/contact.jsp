<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<head>
<title>Contact Us | SalesNrich</title>

<!-- Meta -->
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- Favicon -->
<spring:url value="/resources/assets/images/index/favicon.ico"
	var="favicon"></spring:url>
<link rel="icon" href="${favicon}">

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
	var="skyFormsCSS"></spring:url>
<link href="${skyFormsCSS}" rel="stylesheet">

<spring:url
	value="/resources/web-assets/plugins/sky-forms-pro/skyforms/custom/custom-sky-forms.css"
	var="customSkyFormsCSS"></spring:url>
<link href="${customSkyFormsCSS}" rel="stylesheet">


<!-- CSS Page Style -->
<spring:url value="/resources/web-assets/css/pages/page_contact.css"
	var="pageContactCSS"></spring:url>
<link href="${pageContactCSS}" rel="stylesheet">

<!-- CSS Customization -->
<spring:url value="/resources/web-assets/css/custom.css" var="customCSS"></spring:url>
<link href="${customCSS}" rel="stylesheet">

<script
	src="https://maps.googleapis.com/maps/api/js?v=3.exp&key=AIzaSyA9DK5nQritQuP980HXN7RzaKklErXYc-M&signed_in=true&libraries=places">
	
</script>
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
                    	<!-- <li><a>+91 9846 056 008</a></li>
						<li class="topbar-devider"></li> -->
                    	<li><a>+91 9387 007 657</a></li>
						<li class="topbar-devider"></li>
						<!-- <li><a>+91 8893 499 106</a></li>
						<li class="topbar-devider"></li> -->
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
						<!-- 		<li class="dropdown"><a href="#l" class="dropdown-toggle"
							data-toggle="dropdown"> Services </a>
							<ul class="dropdown-menu">
								<li class="dropdown-submenu">
                                <a href="javascript:void(0);">Blog Large Image</a>
                            </li>
							</ul></li> -->

						<!-- End Blog -->
						<!-- Pages -->
						<!-- <li><a >About Us</a></li> -->
						<li class="active"><a href="contactUs">Contact Us</a></li>
						<!-- End Pages -->
					</ul>
				</div>
				<!--/end container-->
			</div>
			<!--/navbar-collapse-->
		</div>
		<!--=== End Header ===-->

		<!--=== Breadcrumbs ===-->

		<div class="breadcrumbs">
			<div class="container">
				<h1 class="pull-left">Our Contacts</h1>
				<!--
            <ul class="pull-right breadcrumb">
                <li><a href="index">Home</a></li>
                <li><a href="">Pages</a></li>
                <li class="active">Contacts</li>
            </ul>
            -->
			</div>
		</div>
		<!--/breadcrumbs-->

		<!--=== End Breadcrumbs ===-->

		<!-- Google Map -->
		<div id="map" class="map"></div>
		<!---/map-->
		<!-- End Google Map -->

		<!--=== Content Part ===-->
		<div class="container content">
			<div class="row margin-bottom-30">
				<div class="col-md-9 mb-margin-bottom-30">
					<div class="headline">
						<h2>Contact Form</h2>
					</div>
					<!-- Order Form -->
					<form action="assets/php/demo-order.php" method="post"
						enctype="multipart/form-data" id="sky-form1" class="sky-form">
						<header>Tell us a little about yourself, and we'll be in
							touch right away.</header>

						<fieldset>
							<div class="row">
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
								<section class="col col-6">
									<label class="select"> <select name="interested">
											<option value="none" selected disabled>Product
												Interest</option>
											<option value="design">Market Pro</option>
											<option value="development">BMet</option>
											<option value="illustration">Pharma Plus</option>
											<option value="branding">Order Pro</option>
											<option value="video">Van Sales</option>
											<option value="video">Sales360</option>
									</select> <i></i>
									</label>
								</section>
								<!-- <section class="col col-6">
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
					<!-- End Order Form -->
				</div>
				<!--/col-md-9-->

				<div class="col-md-3">
					<!-- Contacts -->
					<div class="headline">
						<h2>Contacts</h2>
					</div>
					<ul class="list-unstyled who margin-bottom-30">
						<li><a><i class="fa fa-home"></i>Aitrich Technologies P
								Ltd</a></li>
						<li><a><i class="fa fa-building"></i>19/60. 2nd Floor
								Visitors Bld. </a></li>
						<li><a><i class="fa fa-road"></i>MG Road , Thrissur-4</a></li>
						<li><a><i class="fa fa-home"></i>Kerala, India</a></li>

						<li><a><i class="fa fa-envelope"></i>info@aitrich.com</a></li>
						<li><a><i class="fa fa-phone"></i>+91 9387 007 657</a></li>
						<li><a><i class="fa fa-globe"></i>http://www.salesnrich.com</a></li>
					</ul>

					<!-- Business Hours -->
					<div class="headline">
						<h2>Business Hours</h2>
					</div>

					<ul class="list-unstyled margin-bottom-30">
						<li><strong>Monday-Saturday:</strong> 9am to 7pm</li>
						<li><strong>Sunday:</strong> Closed</li>
					</ul>

					<!-- Why we are? -->
					<div class="headline">
						<h2>Why we are?</h2>
					</div>

					<ul class="list-unstyled">
						<li><i class="fa fa-check color-green"></i> Having helped
							good number of customers to find success we have gained sound
							experience and expertise in providing solutions beyond just
							typical CRM software packages.</li>
						<li><i class="fa fa-check color-green"></i> We had always
							felt good as our customers advanced through our system to adopt
							most modern business methods.</li>

					</ul>
				</div>
				<!--/col-md-3-->
			</div>
			<!--/row-->

			<!-- Owl Clients v1 -->
			<div class="headline">
				<h2>Our Clients</h2>
			</div>
			<!--=== Owl Carousel v6 ===-->
			<div class="container">
				<ul
					class="list-inline owl-carousel owl-carousel-v6 owl-slider-v6 margin-bottom-50">

					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-1.png" alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-2.png" alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-3.png" alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-4.png"  alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-5.jpg"  alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-6.png"  alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-7.jpg"  alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-8.png"  alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-9.png"  alt="">
					</li>
					<li class="owl-carousel-item"><img
						src="/resources/web-assets/img/clients4/client-10.png"  alt="">
					</li>

				</ul>
			</div>
			<!--/container-->
			<!--<div class="owl-clients-v1">
            <div class="item">
                <img src="web-assets/img/clients4/1.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/2.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/3.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/4.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/5.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/6.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/7.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/8.png" alt="">
            </div>
            <div class="item">
                <img src="web-assets/img/clients4/9.png" alt="">
            </div>
        </div>-->
			<!-- End Owl Clients v1 -->
		</div>
		<!--/container-->
		<!--=== End Content Part ===-->

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
								9387007657, <br /> Mobile: 91 8893 499 106 <br /> Email: <a
									href="mailto:info@aitrich.com" class="">info@aitrich.com</a>
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
	<!--/wrapepr-->

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

	<%-- 	<spring:url value="http://maps.google.com/maps/api/js?sensor=true" var="mapJS" />
	<script src="${mapJS}"></script>

	<spring:url value="/resources/web-assets/plugins/gmap/gmap.js" var="gmapJS" />
	<script src="${gmapJS}"></script> --%>

	<spring:url
		value="/resources/web-assets/plugins/owl-carousel/owl-carousel/owl.carousel.js"
		var="owlCarouselJS" />
	<script src="${owlCarouselJS}"></script>

	<spring:url
		value="/resources/web-assets/plugins/sky-forms-pro/skyforms/js/jquery.form.min.js"
		var="jqueryFormNinJS" />
	<script src="${jqueryFormNinJS}"></script>

	<spring:url
		value="/resources/web-assets/plugins/sky-forms-pro/skyforms/js/jquery.validate.min.js"
		var="jqueryValidateMinJS" />
	<script src="${jqueryValidateMinJS}"></script>

	<spring:url value="/resources/web-assets/js/custom.js" var="customJS" />
	<script src="${customJS}"></script>

	<!-- JS Page Level -->
	<spring:url value="/resources/web-assets/js/app.js" var="appJS" />
	<script src="${appJS}"></script>

	<spring:url value="/resources/web-assets/js/forms/contact.js"
		var="contactJS" />
	<script src="${contactJS}"></script>

	<spring:url value="/resources/web-assets/js/pages/page_contacts.js"
		var="pageContactJS" />
	<script src="${pageContactJS}"></script>

	<spring:url value="/resources/web-assets/js/plugins/owl-carousel.js"
		var="owlCarouselJS" />
	<script src="${owlCarouselJS}"></script>

	<script type="text/javascript">
		/* 	jQuery(document).ready(function() {
				App.init();
				ContactPage.initMap();
				ContactForm.initContactForm();
				OwlCarousel.initOwlCarousel();
			}); */

		var map;
		function initialize() {
			var mapOptions = {
				zoom : 12,
				center : {
					lat : 10.522672,
					lng : 76.205370
				}
			};
			map = new google.maps.Map(document.getElementById('map'),
					mapOptions);
			var marker = new google.maps.Marker({
				position : {
					lat : 10.522672,
					lng : 76.205370
				},
				map : map
			});
			var infowindow = new google.maps.InfoWindow(
					{
						content : '<p>19/60, Visitors Building, II nd Floor M.G Road,, II nd Floor M.G Road, Visitors Building, MG Road, Thrissur, Kerala 680004</p>'
					});
			google.maps.event.addListener(marker, 'click', function() {
				infowindow.open(map, marker);
			});
		}

		google.maps.event.addDomListener(window, 'load', initialize);
	</script>
	<script type="text/javascript">
		$(".owl-carousel").owlCarousel({
      autoPlay: 3000,
      items : 5, // THIS IS IMPORTANT
      // responsive : {
      // 		280:{items : 2 },
      // 		320:{items : 2 },
      // 		360:{items : 2 },
      //       480 : { items : 2 }, // from zero to 480 screen width 4 items
      //       768 : { items : 6 }, // from 480 screen widthto 768 6 items
      //       1024 : { items : 4  // from 768 screen width to 1024 8 items
      //       }
      //   },
  });
	</script>

	<!--[if lt IE 9]>
    <script src="web-assets/plugins/respond.js"></script>
    <script src="web-assets/plugins/html5shiv.js"></script>
    <script src="web-assets/plugins/placeholder-IE-fixes.js"></script>
    <script src="web-assets/plugins/sky-forms-pro/skyforms/js/sky-forms-ie8.js"></script>
<![endif]-->
	<!--[if lt IE 10]>
    <script src="web-assets/plugins/sky-forms-pro/skyforms/js/jquery.placeholder.min.js"></script>
<![endif]-->

</body>
</html>