<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%
    HttpSession session = request.getSession(false); // Do not create a new session
    if (session != null && session.getAttribute("userId") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
        return;
    }
%>
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
	<%
		request.setAttribute("showSignUp", true);
		request.setAttribute("showHome", true);
	%>
	<%@ include file="/includes/header.jsp"%>

	<div class="inner-body">
		<div class="out-box">
			<div class="box">
				<div class="login">
					<form id="loginForm" class="login-form">

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
	
	<%@ include file="/includes/footer.jsp"%>
	<%@ include file="/includes/dialog-box.jsp"%>

	<script>
		document.addEventListener("DOMContentLoaded", () => {
			const box = document.querySelector(".box");
			let hasExpanded = false;
	
			box.addEventListener("mouseenter", () => {
				if (!hasExpanded) {
					box.classList.add("expanded");
					hasExpanded = true;
				}
			});
		});
	 
	    document.getElementById("loginForm").addEventListener("submit", async function (event) {
	        event.preventDefault(); // Prevent default form submission
	
	        const email = document.getElementById("email").value;
	        const password = document.getElementById("password").value;
	
	        try {
	            const response = await fetch("${pageContext.request.contextPath}/MRNBank/login", {
	                method: "POST",
	                headers: {
	                    "Content-Type": "application/json",
                    	"Method": "POST"
	                },
	                body: JSON.stringify({ email, password })
	            });
	
	            const data = await response.json();
	            handleResponse(data, "${pageContext.request.contextPath}/dashboard.jsp"); // Display response in dialog
	        } 
	        catch (error) {
	            handleResponse({ error: "An error occurred while connecting to the server." });
	        }
	    });
	</script>
	
</body>
</html>
