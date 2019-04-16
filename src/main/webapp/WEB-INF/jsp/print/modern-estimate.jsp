<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<html lang="en">
<head>
<title>Solution Tracker</title>
<style type="text/css">
* {
	margin: 0 auto;
	padding: 0;
}

#tableHead {
	border: 1px solid;
	height: 50px
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
	<div id="divPrint" align="center" style="display: none">
		<a href="/web/other-voucher-transaction"
			style="font-weight: bold; font-size: large;">Back</a>
		&nbsp;&nbsp;&nbsp; <a href="#" onclick="printPage();"
			style="font-weight: bold; font-size: large;">Print</a>
	</div>
	<br />
	<div style="width: 960px; border: 2px solid;">
		<section style="width: 100%; float: left;">
			<div
				style="width: 10%; float: left; border: 1px solid; height: 80px; border-top-width: 0; border-left-width: 0;">
				<img
					src="data:image/png;base64,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"
					alt="logo" width="80" />
			</div>
			<div
				style="width: 860px; float: left; text-align: center; border: 1px solid; height: 80px; border-right-width: 0; border-top-width: 0; border-left-width: 0;">
				<h2 style="margin: 0; padding: 0;">
					<b>MODERN DISTROPOLIS LIMITED</b>
				</h2>
				<p style="margin: 0; padding: 0;">
					<b>Modern Industrial Complex, Door No. AP IX /78A IX/78C,</b>
				</p>
				<p style="margin: 0; padding: 0;">
					<b>P.O. Anakkayam-676 509, Malappuram District, Kerala State</b>
				</p>
				<p style="margin: 0; padding: 0;">
					<b>Tel : +91-483-2782354, 3252354,3252365, 3252364
						+91-9349935511.</b>
				</p>
			</div>
		</section>

		<section style="width: 100%; float: left;">
			<div
				style="width: 48%; float: left; border: 1px solid; margin: 10px 8px; text-align: center;">
				<p
					style="text-align: center; color: black; text-decoration: underline;">Details
					of Tata Wiron Fencing Associate.</p>
				<p
					style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;">Name
					: ${dynamicDocument.employeeName}</p>
				<p style="margin-left: 10px; text-align: left; line-height: 20px;">UF
					No : ${dynamicDocument.documentNumberServer}</p>
				<p style="margin-left: 10px; text-align: left; line-height: 20px;">Mobile
					No : ${dynamicDocument.emplyeePhone}</p>
				<p style="margin-left: 10px; text-align: left; line-height: 20px;">
					Date :
					<javatime:format value="${dynamicDocument.documentDate}" style="MS" />
				</p>
			</div>
			<div
				style="width: 48%; float: left; border: 1px solid; margin: 10px 8px; text-align: center;">
				<p
					style="text-align: center; color: black; text-decoration: underline;">Details
					of Customer.</p>
				<p
					style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;">Name
					: ${account.name}</p>
				<p style="margin-left: 10px; text-align: left; line-height: 20px;">Address
					: ${account.address }</p>
				<p style="margin-left: 10px; text-align: left; line-height: 20px;">E-mail
					: ${account.email1 }</p>
				<p style="margin-left: 10px; text-align: left; line-height: 20px;">Ph
					: ${account.phone1 }</p>
			</div>
		</section>
		<c:forEach items="${dynamicDocument.filledForms}" var="filledForm">
			<c:if test="${filledForm.formName=='D Fence'}">
				<c:forEach items="${filledForm.filledFormDetails}"
					var="filledFormDetails">
					<c:if test="${filledFormDetails.formElementName=='Area (cents)' }">
						<c:set var="Areacents" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Area (meters)' }">
						<c:set var="Areameters" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Item Gage' }">
						<c:set var="ItemGage" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Item Gage (millimeter)' }">
						<c:set var="ItemGagemillimeter" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Height of fence (ft)' }">
						<c:set var="Heightoffenceft" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Height of fence (meters)' }">
						<c:set var="Heightoffencemeters"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Mesh Size (inches)' }">
						<c:set var="MeshSizeinches" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Mesh Size (millimeter)' }">
						<c:set var="MeshSizemillimeter" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Size of horizontal bars (inches)' }">
						<c:set var="Sizeofhorizontalbarsinches"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Size of horizontal bars (millimeter)' }">
						<c:set var="Sizeofhorizontalbarsmillimeter"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Type of Post' }">
						<c:set var="TypeofPost" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Post Height (ft)' }">
						<c:set var="PostHeightft" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Post Height (meters)' }">
						<c:set var="PostHeightmeters" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='No. of horizontal lines' }">
						<c:set var="Noofhorizontallines"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Distance between posts (meter)' }">
						<c:set var="Distancebetweenpostsmeter"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Rate/Running meter' }">
						<c:set var="RateRunningmeter" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Total Amount' }">
						<c:set var="TotalAmount" value="${filledFormDetails.value}"></c:set>
					</c:if>
				</c:forEach>
				<table id="dfence"
					style="border: 1px solid; width: 98%; margin-left: 1%">
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Area</b></td>
						<td id="tableHead">&nbsp;${Areacents}</td>
						<td id="tableHead">&nbsp;${Areameters}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Item Gage</b></td>
						<td id="tableHead">&nbsp;${ItemGage}</td>
						<td id="tableHead">&nbsp;${ItemGagemillimeter}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Height of
								fence</b></td>
						<td id="tableHead">&nbsp;${Heightoffenceft}</td>
						<td id="tableHead">&nbsp;${Heightoffencemeters}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Mesh Size</b></td>
						<td id="tableHead">&nbsp;${MeshSizeinches}</td>
						<td id="tableHead">&nbsp;${MeshSizemillimeter}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Size of
								horizontal bars</b></td>
						<td id="tableHead">&nbsp;${Sizeofhorizontalbarsinches}</td>
						<td id="tableHead">&nbsp;${Sizeofhorizontalbarsmillimeter}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Type of
								Post</b></td>
						<td id="tableHead">&nbsp;${TypeofPost}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Post
								Height</b></td>
						<td id="tableHead">&nbsp;${PostHeightft}</td>
						<td id="tableHead">&nbsp;${PostHeightmeters}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">No of
								horizontal lines</b></td>
						<td id="tableHead">&nbsp;${Noofhorizontallines}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Distance
								between posts</b></td>
						<td id="tableHead">&nbsp;${Distancebetweenpostsmeter}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Rate/
								Running Meter</b></td>
						<td id="tableHead">&nbsp;${RateRunningmeter}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Total
								Amount</b></td>
						<td id="tableHead">&nbsp;${TotalAmount}</td>
						<td id="tableHead"></td>
					</tr>
				</table>
			</c:if>
			<c:if test="${filledForm.formName=='Barbed Wire'}">
				<c:forEach items="${filledForm.filledFormDetails}"
					var="filledFormDetails">
					<c:if test="${filledFormDetails.formElementName=='Area (cents)' }">
						<c:set var="Areacents" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Area (meters)' }">
						<c:set var="Areameters" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='No. of horizontal lines' }">
						<c:set var="Noofhorizontallines"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='No. of cross/diagonal lines' }">
						<c:set var="Noofcrossdiagonallines"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Size Gage' }">
						<c:set var="SizeGage" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Size Gage (millimeter)' }">
						<c:set var="SizeGagemillimeter" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Type of Post' }">
						<c:set var="TypeofPost" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Post Height (ft)' }">
						<c:set var="PostHeightft" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Post Height (meters)' }">
						<c:set var="PostHeightmeters" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Distance between posts (meter)' }">
						<c:set var="Distancebetweenpostsmeter"
							value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if
						test="${filledFormDetails.formElementName=='Rate/Running meter' }">
						<c:set var="RateRunningmeter" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Total Amount' }">
						<c:set var="TotalAmount" value="${filledFormDetails.value}"></c:set>
					</c:if>
				</c:forEach>
				<table id="barbedWire"
					style="border: 1px solid; width: 98%; margin-left: 1%">
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Area</b></td>
						<td id="tableHead">&nbsp;${Areacents}</td>
						<td id="tableHead">&nbsp;${Areameters}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">No of
								horizontal lines</b></td>
						<td id="tableHead">&nbsp;${Noofhorizontallines}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">No. of
								cross/ diagonal lines</b></td>
						<td id="tableHead">&nbsp;${Noofcrossdiagonallines }</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Size Gage</b></td>
						<td id="tableHead">&nbsp;${SizeGage}</td>
						<td id="tableHead">&nbsp;${SizeGagemillimeter}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Type of
								Post</b></td>
						<td id="tableHead">&nbsp;${TypeofPost}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Post
								Height</b></td>
						<td id="tableHead">&nbsp;${PostHeightft}</td>
						<td id="tableHead">&nbsp;${PostHeightmeters}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Distance
								between posts</b></td>
						<td id="tableHead">&nbsp;${Distancebetweenpostsmeter}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Rate/
								Running Meter</b></td>
						<td id="tableHead">&nbsp;${RateRunningmeter}</td>
						<td id="tableHead"></td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Total
								Amount</b></td>
						<td id="tableHead">&nbsp;${TotalAmount}</td>
						<td id="tableHead"></td>
					</tr>
				</table>
			</c:if>
			<c:if test="${filledForm.formName=='Roofing Solution'}">
				<c:forEach items="${filledForm.filledFormDetails}"
					var="filledFormDetails">
					<c:if test="${filledFormDetails.formElementName=='Area' }">
						<c:set var="Area" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Slopping' }">
						<c:set var="Slopping" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Structure' }">
						<c:set var="Structure" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Roofing Sheet' }">
						<c:set var="RoofingSheet" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Rate per sqft' }">
						<c:set var="Ratepersqft" value="${filledFormDetails.value}"></c:set>
					</c:if>
					<c:if test="${filledFormDetails.formElementName=='Total Amount' }">
						<c:set var="TotalAmount" value="${filledFormDetails.value}"></c:set>
					</c:if>
				</c:forEach>
				<table id="roofingSln"
					style="border: 1px solid; width: 98%; margin-left: 1%">
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Area</b></td>
						<td id="tableHead">&nbsp;${Area}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Slopping</b></td>
						<td id="tableHead">&nbsp;${Slopping}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Structure</b></td>
						<td id="tableHead">&nbsp;${Structure}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Roofing
								sheet</b></td>
						<td id="tableHead">&nbsp;${RoofingSheet}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Rate per
								sqft</b></td>
						<td id="tableHead">&nbsp;${Ratepersqft}</td>
					</tr>
					<tr>
						<td id="tableHead"><b style="margin-left: 1%;">Total
								Amount</b></td>
						<td id="tableHead">&nbsp;${TotalAmount}</td>
					</tr>
				</table>
			</c:if>
		</c:forEach>
		<div>
			<ul>
				<li
					style="margin-top: 20px; margin-bottom: 10px; margin-left: 30px;">All
					materials are using Tata steel products</li>
			</ul>
			<h2 style="text-decoration: underline; margin-left: 10px;">
				<b>Terms and conditions</b>
			</h2>
			<ul>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">The
					chain link will have a 5 year warranty, which is subject to
					theconditions/instructions of MDL.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">The
					boundary is to be marked / shown by the customer/Party at the time
					offencing</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">If
					there arise any boundary issue, the sole responsibility to solve
					the same will bewith the customer himself.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">If
					the fencing works halts in midway because of the reasons that are
					beyond theresponsibility of Tata Wiron or Modern Distropolis
					Limited, the customer must make the full payment which is due from
					the customer to Modern Distropolis Limited soon.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">The
					customer reserves the right to check the quality of material used
					for fencing.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Once
					the work order got confirmed for FS, the same will be executed most
					satisfactorily within 7 days.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">The
					total amount quoted is just an assumption based on the estimated
					length. The correct amount may vary as per the correct length after
					taking the length by our team in your presence.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">On
					acceptance of this quotation, 50% of the above quoted amount of the
					contract is to be paid to us as advance by way of cash / DD /
					Cheque / RTGS in our favor</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">30%
					of the above quoted amount is to be paid soon after the fencing
					materialsreaches your site.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">30%
					of the above quoted amount is to be paid soon after the fencing
					materialsreaches your site.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">The
					balance amount is to be paid to us soon after the completion of
					work.Our Bank details are as follows.</li>
			</ul>
			<div style="width: 30%; float: left; margin-left: 20%;">
				<p>Name of Beneficiary</p>
				<p>Bank</p>
				<p>Branch</p>
				<p>A/C No.</p>
				<p>IFSC CODE</p>
			</div>
			<div style="width: 50%; float: left;">
				<p>: Modern Distropolis Limited.</p>
				<p>: Bank of Baroda</p>
				<p>: Manjeri</p>
				<p>: 25350200000123</p>
				<p>: BARB0MANJER</p>
			</div>
			<div style="clear: both"></div>
			<ul>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Separate
					Bill will be issued for material (by MDL/Dealer), labour charge(by
					labour) and leading charge (by MDL).</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">3%
					leading charge and service tax against leading charge are included
					in the amount quoted above.</li>
				<li
					style="list-style-type: decimal; line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Validity
					of this quotation is 10 days only.</li>
			</ul>
		</div>

		<p
			style="padding: 4% 0; font-weight: 900; font-size: 19px; margin-left: 10px;">We
			trust our offer will meet with your approval and awaiting your
			valuableconfirmation letter to proceed further.</p>

		<br /> <br />

		<p style="margin-left: 49px; font-weight: bold;">Thanking you</p>
		<p style="margin-left: 49px; font-weight: bold;">yours faithfully</p>

		<br /> <br /> <br />

		<p style="margin-left: 49px; font-weight: bold;">SUDHIN</p>
		<br />
		<p style="margin-left: 49px; font-weight: bold;">Executive
			–Fencing Solution</p>

		<br /> <br />

		<h2 style="text-decoration: underline; margin-left: 10px;">
			<b>Terms and conditions for warrantee.</b>
		</h2>
		<ul>
			<li
				style="line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Fire,
				chemicals, acids, kerosene etc. should not come into contact with
				thefencing.</li>
			<li
				style="line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Hedges
				and creepers must be kept away from fence.</li>
			<li
				style="line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Painting
				or any alteration should not be made on the fencing without the
				prior permission of Modern Distropolis Limited.</li>
			<li
				style="line-height: 43px; font-size: 18px; list-style-position: inside; margin-left: 10px;">Force
				Majoure excuses like Natural calamities will be applicable to this
				warrantee. We will not give any warrantee to those things that are
				beyond our control (i.e., for Act of God).</li>
		</ul>
	</div>
	<script type="text/javascript">
	(function() {
		document.getElementById("divPrint").style["display"] = "block";
	})();
	</script>
</body>
</html>