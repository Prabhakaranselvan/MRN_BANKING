
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"%>
<%@ page import="java.time.LocalDate"%>

<%
LocalDate today = LocalDate.now();
LocalDate minEligibleDate = today.minusYears(18);
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Signup</title>

<%@ include file="/includes/head-resources.jsp"%>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/signup.css">
</head>

<body>
	<%@ include file="/includes/header.jsp"%>
	<div class="content">
		<div class="left-half">
			<img src="images/grow.jpg" alt="Rise with Us" class="grow-image">
			<h3 class="quote-msg">Stay, Grow, Conquer!</h3>
			<h2 class="welcome-msg">Welcome Aboard!</h2>
		</div>

		<div class="right-half">
			<form id="signupForm" class="signup-form">
				<h2 class="form-header">USER REGISTRATION</h2>
				
				<input type="hidden" name="userCategory" value="0" data-type="int">

				<div class="double-column">
					<div class="part">
						<label class="form-label" for="name">Name <span class="required">*</span></label>
						<input class="form-input" type="text" name="name" placeholder="Name" maxlength="30"
							pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*"	required autofocus
							title="Name should contain only letters, spaces, hyphens or apostrophes."> 
					</div>
					<div class="part">
						<label class="form-label" for="dob">Date of Birth <span class="required">*</span></label> 
						<input class="form-input" type="date" id="dob" name="dob" max="<%=minEligibleDate%>"
						 	title="You must be at least 18 years old." required>
					</div>
				</div>

				<div class="gender">
					<label class="form-label">Gender <span class="required">*</span></label>
					<div class="options">
						<input class="radio-input" type="radio" name="gender" value="Male" required title="Select your gender."> 
						<label class="radio-label">Male</label>
					</div>
					<div class="options">
						<input class="radio-input" type="radio" name="gender" value="Female" required title="Select your gender.">
						 <label class="radio-label">Female</label>
					</div>
					<div class="options">
						<input class="radio-input" type="radio" name="gender" value="Other" required title="Select your gender."> 
						<label class="radio-label">Other</label>
					</div>
				</div>

				<div class="double-column">
					<div class="part-with-icon">
						<label class="form-label" for="email">Email <span class="required">*</span></label>
						<input class="form-input" type="email" id="email" name="email"
							pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" 
							title="Enter a valid email address (e.g., user@example.com)." required> 
						<span class="material-symbols-outlined">mail</span>
					</div>
					<div class="part-with-icon">
						<label class="form-label" for="phone">Phone Number <span class="required">*</span></label> 
						<input class="form-input" type="text" id="phone" name="phoneNo" maxlength="10" pattern="\d{10}"
							title="Phone number must be 10 digits" required> 
						<span class="material-symbols-outlined">call</span>
					</div>
				</div>

				<div class="double-column">
					<div class="part">
						<label class="form-label" for="aadhar">Aadhar Number <span class="required">*</span></label> 
						<input class="form-input" type="text" id="aadhar" name="aadhar" maxlength="12" pattern="\d{12}" 
							title="Aadhar number must be exactly 12 digits." required>
					</div>
					<div class="part">
						<label class="form-label" for="pan">PAN Number <span class="required">*</span></label> 
						<input class="form-input" type="text" id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]"
							oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')" required
							title="PAN format: 5 uppercase letters, 4 digits, and 1 uppercase letter (e.g., ABCDE1234F).">
					</div>
				</div>

				<label class="form-label">Address <span class="required">*</span></label>
				<input class="form-input" type="text" name="address" placeholder="Address" maxlength="60" required
					title="Enter your full address (max 60 characters)."> 
				
				<div class="double-column">
					<div class="part">
						<label class="form-label" for="password">Set Password <span	class="required">*</span></label> 
						<input class="form-input" type="password" id="password" name="password" maxlength="20"
							pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
							title="Password must be 8-20 characters, include uppercase, lowercase, number, and a special character.">
					</div>
					<div class="part">
						<label class="form-label" for="confirm-password">Confirm Password <span class="required">*</span></label> 
						<input class="form-input" type="password" id="confirm-password"	name="confirm_password" maxlength="20"
							pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
							 title="Must match the password above.">
					</div>
				</div>

				<div class="show-password">
					<input type="checkbox" id="show-password"> <label
						for="show-password">Show Password</label>
				</div>
				<span id="password-error" class="error-message"></span>

				<div class="buttons">
					<button class="form-button" type="submit">Submit</button>
					<button class="form-button" type="reset">Reset</button>
				</div>
				
				<div class="group">
					<p class="signup-text">Already an User?</p>  
					<a href="${pageContext.request.contextPath}/login.jsp">Login</a>
				</div>
			</form>
		</div>
	</div>

	<%@ include file="/includes/footer.jsp"%>
	<%@ include file="/includes/dialog-box.jsp"%>

	<script>
	    document.addEventListener("DOMContentLoaded", () => 
	    {
	        const form =  document.getElementById("signupForm");
	        const password = document.getElementById("password");
	        const confirmPassword = document.getElementById("confirm-password");
	        const showPasswordCheckbox = document.getElementById("show-password");
	        const errorMessage = document.getElementById("password-error");
	
	        // Enforce numeric-only input
	        const numberInputs = document.querySelectorAll("#phone, #aadhar");
	        numberInputs.forEach(input => {
	            input.addEventListener("input", function () {
	                this.value = this.value.replace(/\D/g, ''); // Remove non-digits
	            });
	        });
	        
	        // Prevent numbers in the name field
	        const nameInput = document.querySelector("input[name='name']");
	        nameInput.addEventListener("input", function () {
	            this.value = this.value.replace(/[0-9]/g, ''); // Remove any digits
	        });


	        // Toggle password visibility
	        showPasswordCheckbox.addEventListener("change", function () {
	            const type = this.checked ? "text" : "password";
	            password.type = type;
	            confirmPassword.type = type;
	        });
	
	        // Handle form submission
	        form.addEventListener("submit", async function (event) {
	            event.preventDefault();
	
	            if (password.value !== confirmPassword.value) {
	                errorMessage.textContent = "Passwords do not match!";
	                errorMessage.style.color = "red";
	                return;
	            } else {
	                errorMessage.textContent = "";
	            }
	
	            const formData = new FormData(form);
	            const jsonBody = {};
	
	            for (const [key, value] of formData.entries()) {
	                if (key === "userCategory") {
	                    jsonBody[key] = parseInt(value);
	                } else {
	                    jsonBody[key] = value;
	                }
	            }
	
	            try {
	                const response = await fetch(`${pageContext.request.contextPath}/MRNBank/signup`, {
	                    method: "POST",
	                    headers: {
	                        "Content-Type": "application/json",
                        	"Method": "POST"
	                    },
	                    body: JSON.stringify(jsonBody)
	                });
	
	                const data = await response.json();
	                handleResponse(data, `${pageContext.request.contextPath}/login.jsp`);
	            } 
	            catch (error) {
	                handleResponse({ error: "An error occurred while submitting the form." });
	            }
	        });
	    });
	</script>


</body>
</html>
