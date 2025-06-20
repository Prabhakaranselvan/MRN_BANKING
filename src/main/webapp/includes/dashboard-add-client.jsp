<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>
<%@ page import="java.time.LocalDate"%>

<%
LocalDate today = LocalDate.now();
LocalDate minEligibleDate = today.minusYears(18);
%>

<div class="container">
	<form id="signupForm" class="signup-form">
				<h2 class="form-header">USER REGISTRATION</h2>
				
				<input type="hidden" name="userCategory" value="0" data-type="int">

				<div class="double-column">
					<div class="part">
						<label class="form-label" for="name">Name <span class="required">*</span></label>
						<input class="form-input" type="text" name="name"	placeholder="Name" 
							pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*"	required autofocus> 
					</div>
					<div class="part">
						<label class="form-label" for="dob">Date of Birth <span class="required">*</span></label> 
						<input class="form-input" type="date" id="dob" name="dob" max="<%=minEligibleDate%>" required>
					</div>
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
						<input class="form-input" type="email" id="email" name="email"
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

				<div class="buttons">
					<button class="form-button" type="submit">Submit</button>
					<button class="form-button" type="reset">Reset</button>
				</div>
			</form>
</div>

<script src="${pageContext.request.contextPath}/js/dashboard-clients.js"></script>