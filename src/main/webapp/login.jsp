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
<title>Sign In</title>

<%@ include file="/includes/head-resources.jsp"%>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/login.css">
</head>

<body>
	<%@ include file="/includes/header.jsp"%>

	<div class="inner-body">
		<img src="${pageContext.request.contextPath}/images/Login_Image.png" alt="Login_Image" class="login-image" />
		<div class="out-box">
			<div class="box">
				<div class="login">
					<form id="loginForm" class="login-form">

						<h2>
							<i class="fa-solid fa-right-to-bracket"></i> Sign in
						</h2>
						<input type="text" id="email" name="email" placeholder="Email"
						pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"  maxlength="250"
						title="Please enter a valid email (e.g., user@example.com) with 6–250 characters." required /> 
						<div class="password-wrapper">
						    <input type="password" id="password" name="password" maxlength="20"
						        pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" placeholder="Password" required
						        title="Password must be 8–20 characters with uppercase, lowercase, number, and special character." />
						    <span id="toggle-password" class="toggle-icon material-icons">visibility</span>
						</div>

						<input type="submit" value="Sign in" />
						<div class="group">
							<p class="signup-text">Not an User?</p>  
							<a href="${pageContext.request.contextPath}/signup.jsp">Sign up</a>
						</div>
					</form>
				</div>
			</div>
		</div>
		
	</div>
	
	<%@ include file="/includes/footer.jsp"%>
	<%@ include file="/includes/dialog-box.jsp"%>

	<script>		
		document.getElementById("toggle-password").addEventListener("click", function () {
		    const passwordInput = document.getElementById("password");
		    const type = passwordInput.getAttribute("type");
		    
		    if (type === "password") {
		        passwordInput.setAttribute("type", "text");
		        this.textContent = "visibility_off";
		    } else {
		        passwordInput.setAttribute("type", "password");
		        this.textContent = "visibility";
		    }
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
