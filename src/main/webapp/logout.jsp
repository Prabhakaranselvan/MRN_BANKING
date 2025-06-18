<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Logged Out - MRN Bank</title>
<%@ include file="/includes/head-resources.jsp"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/logout.css" />
</head>
<body>
	<div class="inner-body">
		<div class="logout-container">
			<img
				src="${pageContext.request.contextPath}/images/Logo_BG_Removed.png"
				alt="MRN Bank Logo" class="logo" />
			<h1>You have been securely logged out</h1>
			<p>
				Thank you for banking with <strong>MRN Bank</strong>. We look
				forward to serve you again.
			</p>
			<a href="${pageContext.request.contextPath}/login.jsp" class="button">Login
				Again</a>
		</div>
	</div>
	<%@ include file="/includes/footer.jsp"%>
</body>
</html>
