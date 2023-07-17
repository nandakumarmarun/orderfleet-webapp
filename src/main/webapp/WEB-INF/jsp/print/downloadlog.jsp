<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>
<title>downloadlog</title>
<style type="text/css">
body {
	background-color: #f5f5f5;
}
</style>
</head>
<body>
	<div id="main-content" class="container">
		<div class="row">
			<div class="col-md-12">
				<form action="/web/get-log/logs" method="get">
                    <label for="date">Select Date:</label>
                    <input type="date" id="date" name="date" required>
                    <button type="submit">Download Text File</button>
                </form>
			</div>
		</div>
	</div>
</body>
</html>
