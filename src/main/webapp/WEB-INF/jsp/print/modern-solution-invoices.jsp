<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<title>Tata Wiron Fencing Solutions</title>
<style type="text/css">
* {
	margin: 0 auto;
	padding: 0;
}
#tableHead{
border: 1px solid;
 border-top-width: 0;
 border-left-width: 0;
 height: 50px;
 color: white;
}

#dat{
border: 1px solid;
border-collapse: collapse;
}

</style>
<script type="text/javascript">
	function printPage() {
		window.print();
	}
</script>
<spring:url value="/resources/assets/js/moment.js" var="momentJs"></spring:url>
<script type="text/javascript" src="${momentJs}"></script>
</head>
<body>
	<br />
	<div align="center">
		<a href="/web/other-voucher-transaction"
			style="font-weight: bold; font-size: large;">Back</a>
		&nbsp;&nbsp;&nbsp; <a href="#" onclick="printPage();"
			style="font-weight: bold; font-size: large;">Print</a>
	</div>
	<br />
	<div style="width: 960px; border: 2px solid;">
		<section style="width: 100%; float: left;">
			<div
				style="width: 10%; float: left;  height: 120px; ">
				<img
					src="data:image/png;base64,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"
					style="margin-left: 10%;width: 135%;  height: inherit;" alt="logo" />
			</div>
			<div
				style="width: 722px; margin-top: 1%;float: left; text-align: center;  height: 120px; ">
				<h2 style="margin: 0; padding: 0;">
					<b>MODERN DISTROPOLIS LIMITED</b>
				</h2>
				<p style="margin: 0; padding: 0;">
					<b>AP IX /78B. MODERN INDUSTRIAL COMPLEX</b>
				</p>
				<p style="margin: 0; padding: 0;">
					<b>CHEPPUR, ANAKKAYAM PO, MALAPPURAM DT</b>
				</p>
				<p style="margin: 0; padding: 0;">
					<b>KERALA - 676509</b>
				</p>
				<p style="margin: 0; padding: 0;">
				<b>www.moderndistropolis.com</b></p>
			</div>
			<div
				style="width: 14%; float: left; margin-top: 1%; height: 120px; ">
				<p>Tel: 0483 2782354</p>
				<p style="margin-left: 65px;">3252354</p>
			</div>
		</section>
		<section style="width: 100%; float: left;"><div
				style="width: 960px; float: left; text-align: center; height: 80px; ">
				<h1 style="font-size: 300%;margin-top: 1%;"><b>TATA</b> <i>Wiron</i> <b>FENCING SOLUTION</b></h1></div></section>
		<section style="width: 100%; float: left;">
		<div style="width: 35%; float: left; border: 1px solid; height: 130px;margin-left: 2%"><label style="margin-left: 3%;margin-top: 2%"><b>To</b></label></div>
		
		<!-- <div style="width: 382px; float: left; text-align: center; border: 1px solid; height: 130px;">
		<p style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;"><b>UFN :</b></p>
		<p  style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;"><b>Date :</b></p>
		</div> -->
		<table  id="dat" style="width: 30%;margin-left: 68%"><tr><th id="dat" style="width: 3%">No.</th><td id="dat" style="width: 8%">sdfs</td></tr>
		<tr><th id="dat" style="width: 3%">Date</th><td id="dat" style="width: 8%">sdf</td></tr>
		</table>
		<table  id="dat" style="width: 40%;margin-left: 58%;margin-top: 5%;"><tr><th id="dat" style="width: 3%">UFN</th><td id="dat" style="width: 8%">sdfs</td></tr>
		<tr><th id="dat" style="width: 3%">Date</th><td id="dat" style="width: 8%">sdf</td></tr>
		</table>
		</section>
		 
		<table style="border: 2px solid; width: 96%;margin-top: 37%; margin-left: 2%;border-collapse:collapse; "><thead style="background-color: black;"><tr>
		<th id="tableHead">Sl No.</th><th id="tableHead">Description</th><th id="tableHead">Amount</th>
		</tr></thead><tbody id="body"><tr><td></td><td></td><td></td></tr></tbody></table>
		
		
		<div><label style="margin-left: 80%">Signature</label></div>

		
		</div>

	<spring:url value="/resources/assets/js/jquery-1.11.3.min.js"
		var="jQuery"></spring:url>
	<script type="text/javascript" src="${jQuery}"></script>

</body>
</html>