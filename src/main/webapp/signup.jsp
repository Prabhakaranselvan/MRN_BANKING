
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
			<form class="json-form" data-endpoint="${pageContext.request.contextPath}/MRNBank/signup" data-method="POST">
				<h2 class="form-header">USER REGISTRATION</h2>
				
				<input type="hidden" name="userCategory" value="0" data-type="int">

				<!-- Name Section -->
				<!-- <label class="form-label" for="title">Name <span class="required">*</span></label>
				<div class="name-container">
					<select class="form-input" id="title" name="title" required>
						<option value="Mr">Mr</option>
						<option value="Mrs">Mrs</option>
						<option value="Ms">Ms</option>
						<option value="Dr">Dr</option>
					</select>
					<input class="form-input" type="text" name="name"	placeholder="Name" 
						pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*"	required autofocus> 
					<input class="form-input" type="text" name="last_name" placeholder="Last Name" 
						pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*">
				</div> -->

				<div class="double-column">
					<div class="part">
						<label class="form-label" for="title">Name <span class="required">*</span></label>
						<input class="form-input" type="text" name="name"	placeholder="Name" 
							pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*"	required autofocus> 
					</div>
					<div class="part">
						<label class="form-label" for="dob">Date of Birth <span class="required">*</span></label> 
						<input class="form-input" type="date" id="dob" name="dob" max="<%=minEligibleDate%>" required>
					</div>
					<!-- <div class="part">
						<label class="form-label" for="account-type">Account Type <span class="required">*</span></label>
						 <select class="form-input" name="account_type" required>
							<option value="Savings">Savings</option>
							<option value="Current">Current</option>
							<option value="Fixed Deposit">Fixed Deposit</option>
						</select>
					</div> -->
				</div>

				<div class="gender">
					<label class="form-label">Gender <span class="required">*</span></label>
					<div class="options">
						<input class="radio-input" type="radio" name="gender" value="Male" required> 
						<label class="radio-label">Male</label>
					</div>
					<div class="options">
						<input class="radio-input" type="radio" name="gender" value="Female" required>
						 <label class="radio-label">Female</label>
					</div>
					<div class="options">
						<input class="radio-input" type="radio" name="gender" value="Other" required> 
						<label class="radio-label">Other</label>
					</div>
				</div>

				<div class="double-column">
					<div class="part-with-icon">
						<label class="form-label" for="email">Email <span class="required">*</span></label>
						<input class="form-input" type="text" id="email" name="email"
							pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" required> 
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
						<input class="form-input" type="text" id="aadhar" name="aadhar" maxlength="12" pattern="\d{12}" required>
					</div>
					<div class="part">
						<label class="form-label" for="pan">PAN Number <span class="required">*</span></label> 
						<input class="form-input" type="text" id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]"
							oninput="this.value = this.value.toUpperCase();" required>
					</div>
				</div>

				<label class="form-label">Address <span class="required">*</span></label>
				<input class="form-input" type="text" name="address" placeholder="Address" required> 
				<!-- <input class="form-input" type="text" name="address_line2" placeholder="Address Line 2">
				<div class="row-container">
					<input class="form-input" type="text" name="district" placeholder="District" required> 
					<input class="form-input" type="text" name="state" placeholder="State" required>
				</div>
				<div class="row-container">
					<input class="form-input" type="text" name="country" placeholder="Country" required> 
					<input class="form-input" type="text" name="pincode" maxlength="6" pattern="\d{6}" required>
				</div> -->

				<div class="double-column">
					<div class="part">
						<label class="form-label" for="password">Set Password <span	class="required">*</span></label> 
						<input class="form-input" type="password" id="password" name="password" maxlength="20"
							pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required>
					</div>
					<div class="part">
						<label class="form-label" for="confirm-password">Confirm Password <span class="required">*</span></label> 
						<input class="form-input" type="password" id="confirm-password"	name="confirm_password" maxlength="20"
							pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required>
					</div>
				</div>

				<div class="show-password">
					<input type="checkbox" id="show-password"> <label
						for="show-password">Show Password</label>
				</div>
				<span id="password-error" class="error-message"></span>

				<script>
					document.addEventListener("DOMContentLoaded", function () {
					    const password = document.getElementById("password");
					    const confirmPassword = document.getElementById("confirm-password");
					    const showPasswordCheckbox = document.getElementById("show-password");
					    const errorMessage = document.getElementById("password-error");
					    const form = document.querySelector("form");
					
					    showPasswordCheckbox.addEventListener("change", function () {
					        const type = this.checked ? "text" : "password";
					        password.type = type;
					        confirmPassword.type = type;
					    });
					
					    form.addEventListener("submit", function (event) {
					        if (password.value !== confirmPassword.value) {
					            event.preventDefault(); // stops submission
					            errorMessage.textContent = "Passwords do not match!";
					            errorMessage.style.color = "red";
					        } else {
					            errorMessage.textContent = "";
					        }
					    });
					});
				</script>


				<div class="buttons">
					<button class="form-button" type="submit">Submit</button>
					<button class="form-button" type="reset">Reset</button>
				</div>
			</form>
		</div>
	</div>

	<%@ include file="/includes/dialog-box.jsp"%>
	<%@ include file="/includes/footer.jsp"%>

	<!-- Inline or External Script -->
	<script src="${pageContext.request.contextPath}/scripts/json-form-handler.js"></script>
</body>
</html>
