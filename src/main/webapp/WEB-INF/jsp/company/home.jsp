<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
			<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

				<%@ taglib tagdir="/WEB-INF/tags/util" prefix="util" %>

					<html lang="en">

					<head>
						<jsp:include page="../fragments/m_head.jsp"></jsp:include>
						<title>SalesNrich | Home </title>

						<style type="text/css">
							.error {
								color: red;
							}

							@media (min-width : 1360px) and (max-width: 1370px) {
								.width83 {
									width: 83%
								}
							}

							.banner {
								padding-left: 10px;
								padding-right: 10px;
								width: 100%;
							}

							.banner-img {
								width: 100%;
							}

							.purchase {
								display: flex;
								align-items: center;
								width: 50%;
								background-color: #e34848;
								justify-content: center;
								padding: 41px;
								height: 376px;
							}

							.pauchase-banner {
								display: flex;
								align-items: center;
								justify-content: center;
							}

							.purchase-h1 {
								margin-top: 17px;
								margin-bottom: 21.5px;
							}

							.mockup-banner {
								display: flex;
								align-items: center;
								justify-content: center;
							}

							.mockup-img {
								width: 50%;
								height: 50%;
							}

							@media (max-width: 800px) {
								.pauchase-banner {
									flex-direction: column;
								}

								.purchase {
									display: flex;
									align-items: center;
									width: auto;
									background-color: #e34848;
									justify-content: center;
									padding: 41px;
									height: auto;
								}

								.banner-img {
									width: 100%;
									height: auto;
								}

								.banner {
									width: 100%;
								}
							}
						</style>
					</head>

					<body class="page-body" data-url="">

						<div class="page-container">
							<jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
							<div class="main-content">
								<jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
								<div>
									<br>
									<br>
								</div>
								<div class="banner">
									<img class="banner-img" alt="image not found "
										src="/resources/web-assets/img/sliders/sales.jpg">
								</div>


								<div class="pauchase-banner">
									<div class="purchase margin-bottom-30" style="background-color: #FFF;">
										<div>
											<h1 class="purchase-h1">A platform for your team to work in a much organized
												manner</h1>

											<p>SalesNrich enables mobility to field staffs and automates
												various business processes related to sales, marketing, and
												supply chain. It helps you sell more, deliver faster, and gives
												you more control over field activities.</p>
										</div>
									</div>
									<div class="mockup-banner">
										<img class='mockup-img' src="\resources\web-assets\img\mockup\im.png" alt="">
									</div>
								</div>
								<hr>

							</div>
							<jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

							<spring:url value="/resources/app/home.js" var="homeJs"></spring:url>
							<script type="text/javascript" src="${homeJs}"></script>
					</body>

					</html>