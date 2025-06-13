<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Login</title>

<%@ include file="/includes/head-resources.jsp"%>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/login.css">
</head>

<body>
	<%@ include file="/includes/header.jsp"%>

	<div class="inner-body">
		<div class="out-box">
			<div class="box">
				<div class="login">
					<form class="json-form" data-endpoint="${pageContext.request.contextPath}/MRNBank/login" data-method="POST">

						<h2>
							<i class="fa-solid fa-right-to-bracket"></i> Login
						</h2>
						<input type="text" id="email" name="email" placeholder="Email"
							pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" required /> 
						<input type="password" id="password" name="password" maxlength="20"
							pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" placeholder="Password" required /> 
						<input type="submit" value="Sign in" />
						<div class="group">
							<a href="#">Forgot Password</a> <a href="./signup.jsp">Sign up</a>
						</div>
					</form>
				</div>
			</div>
		</div>
		<img src="${pageContext.request.contextPath}/images/Login_Image.png" alt="Login_Image" class="login-image" />
	</div>

	<%@ include file="/includes/dialog-box.jsp"%>
	<%@ include file="/includes/footer.jsp"%>

	<!-- Inline or External Script -->
	<script	src="${pageContext.request.contextPath}/scripts/json-form-handler.js"></script>
</body>
</html>
