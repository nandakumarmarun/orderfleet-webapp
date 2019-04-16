
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime"%>
<html lang="en">
<head>
<title>Tata Wiron Fencing Solutions</title>
<style type="text/css">
* {
	margin: 0 auto;
	padding: 0;
}

.tableHead {
	border: 1px solid;
	border-top-width: 0;
	border-left-width: 0;
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
	<%!private final String[] one = {" ", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine",
			" Ten", " Eleven", " Twelve", " Thirteen", " Fourteen", "Fifteen", " Sixteen", " Seventeen", " Eighteen",
			" Nineteen"};

	private final String[] ten = {" ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", "Seventy", " Eighty",
			" Ninety"};

	public String convertNumberToWords(int num) {

		String words = "";
		words += convertToWord((num / 1000000000), " Hundred");
		words += convertToWord((num / 10000000) % 100, " crore");
		words += convertToWord(((num / 100000) % 100), " lakh");
		words += convertToWord(((num / 1000) % 100), " thousand");
		words += convertToWord(((num / 100) % 10), " hundred");
		words += convertToWord((num % 100), " ");
		words += " only.";

		return words;
	}

	private String convertToWord(int num, String ch) {
		String word = "";
		if (num > 19) {
			word += ten[num / 10] + " " + one[num % 10];
		} else {
			word += one[num];
		}
		if (num > 0)
			word += ch;
		return word;
	}%>
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
				style="width: 10%; float: left; border: 1px solid; height: 120px; border-top-width: 0; border-left-width: 0;">
				<img
					src="data:image/png;base64,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"
					style="margin-left: 10%; height: inherit;" alt="logo" width="80" />
			</div>
			<div
				style="width: 860px; float: left; text-align: center; border: 1px solid; height: 120px; border-right-width: 0; border-top-width: 0; border-left-width: 0;">
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
				<p style="margin: 0; padding: 0;">
					<b>(SERVICE TAX NO:AAJFM8191PST001) </b>
				</p>
			</div>
		</section>
		<section style="width: 100%; float: left;">
			<div
				style="width: 960px; float: left; text-align: center; border: 1px solid; height: 80px; border-right-width: 0; border-top-width: 0; border-left-width: 0; background-color: yellow;">
				<h1 style="font-size: -webkit-xxx-large; margin-top: 1%;">
					<b>TATA</b> <i>Wiron</i> <b>Fencing Solutions</b>
				</h1>
			</div>
		</section>
		<section style="width: 100%; float: left;">
			<div
				style="width: 35%; float: left; border: 1px solid; height: 130px; border-top-width: 0; border-left-width: 0;">
				<label style="margin-left: 3%;"><b>To</b></label>
				<div id="customer">
					<p title="name"
						style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;">Name
						: ${account.name}</p>
					<p title="address"
						style="margin-left: 10px; text-align: left; line-height: 20px;">Address
						: ${account.address }</p>
					<p title="email"
						style="margin-left: 10px; text-align: left; line-height: 20px;">E-mail
						: ${account.email1 }</p>
					<p title="phone"
						style="margin-left: 10px; text-align: left; line-height: 20px;">Ph
						: ${account.phone1 }</p>
				</div>
			</div>
			<div
				style="width: 25%; float: left; border: 1px solid; height: 130px; border-top-width: 0; border-left-width: 0;">
				<label style="margin-left: 4%; font-size: 160%"><b>STATEMENT
						OF </b></label> <label style="margin-left: 16%; font-size: 160%"><b>ACCOUNTS
				</b></label>
			</div>

			<div id="executiveDetails"
				style="width: 382px; float: left; text-align: center; border: 1px solid; height: 130px; border-right-width: 0; border-top-width: 0; border-left-width: 0;">
				<p title="UF No"
					style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;">
					<b>UFN :</b> ${dynamicDocument.documentNumberServer}
				</p>
				<p title="date"
					style="margin-left: 10px; text-align: left; line-height: 20px; padding-top: 13px;">
					<b>Date : </b>
					<javatime:format value="${dynamicDocument.documentDate}" style="MS" />
				</p>
			</div>
		</section>
		<table style="border: 1px solid; width: 100%;">
			<tr>
				<td class="tableHead" style="width: 45px;"><b>Sl No.</b></td>
				<td class="tableHead"><b>Description</b></td>
				<td class="tableHead"><b>Amount</b></td>
				<td class="tableHead"><b>Remarks</b></td>
			</tr>
			<tbody id="tblBody">
				<c:set var="slNo" value="0" />
				<c:forEach items="${dynamicDocument.filledForms}" var="filledForm">
					<c:if test="${filledForm.formName=='Bills'}">
						<c:set var="counter" value="0" />
						<c:set var="total" value="0" />
						<c:forEach items="${filledForm.filledFormDetails}"
							var="filledFormDetails">
							<c:set var="counter" value="${counter + 1}" />
							<c:if
								test="${filledFormDetails.formElementName=='Bill Description' }">
								<c:set var="description" value="${filledFormDetails.value}"></c:set>
							</c:if>
							<c:if test="${filledFormDetails.formElementName=='Bill Amount' }">
								<c:set var="amount" value="${filledFormDetails.value}"></c:set>
								<c:if test="${amount.matches('[0-9]+')}">
									<c:set var="total" value="${total=total+amount }" />
								</c:if>
							</c:if>
							<c:if
								test="${filledFormDetails.formElementName=='Bill Remarks' }">
								<c:set var="remark" value="${filledFormDetails.value}"></c:set>
							</c:if>
							<c:if test="${(counter == 3)}">
								<c:set var="counter" value="0" />
								<c:set var="slNo" value="${slNo + 1}" />
								<tr class="tableHead">
									<td class="tableHead">${slNo}</td>
									<td class="tableHead">${description}</td>
									<td class="tableHead">${amount}</td>
									<td class="tableHead">${remark}</td>
								</tr>
							</c:if>
						</c:forEach>
						<tr class="tableHead">
							<td class="tableHead" style="border-bottom-width: 0;"></td>
							<td class="tableHead"><b>Total :</b></td>
							<td class="tableHead"><b id="totAmt">${total}</b></td>
							<td class="tableHead"></td>
						</tr>
						<tr class="tableHead">
							<td class="tableHead" style="border-bottom-width: 0;"></td>
							<td class="tableHead" colspan="2"><b>Amount :</b>
								<%
									String totalInWord = convertNumberToWords(((Long) pageContext.getAttribute("total")).intValue());
											out.println(totalInWord);
								%>
							</td>
							<td class="tableHead"></td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<script type="text/javascript">
		(function() {
			document.getElementById("divPrint").style["display"] = "block";
		})();
	</script>
</body>
</html>
