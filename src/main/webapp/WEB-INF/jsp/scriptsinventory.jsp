<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<title>SalesNrich</title>
</head>
<body id="benefits">
	<script type="text/javascript">
		function calculateTotal(inElements) {
			var totalRate, discount, amount, tax, total, outElements;
			outElements = {};
			totalRate = inElements.selling_rate * inElements.quantity;
			if (inElements.discount_percentage === undefined
					|| inElements.discount_percentage === null) {
				discount = 0;
			} else {
				discount = (totalRate * inElements.discount_percentage) / 100;
			}
			amount = totalRate - discount;
			if (inElements.tax_percentage === undefined
					|| inElements.tax_percentage === null) {
				tax = 0;
			} else {
				tax = (amount * inElements.tax_percentage) / 100;
			}
			total = (amount + tax)/1000;
			outElements.total = total;
			return outElements;
		}
	</script>
</body>
</html>