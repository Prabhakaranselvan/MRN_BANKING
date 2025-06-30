<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>
<%@ page import="java.time.LocalDate"%>
<%
LocalDate today = LocalDate.now();
LocalDate minEligibleDate = today.minusYears(18);
%>
<div class="user-modal-overlay" id="profile-modal">
  <div class="user-modal-box">
    <button class="user-close-btn" onclick="closeProfileModal()">✖</button>
	<form id="MRN-Form" class="user-mrn-form">
		<h2 class="user-form-header">USER REGISTRATION</h2>

		<div class="user-part">
			<label class="user-form-label" for="userCategory">Register As <span
				class="user-required">*</span></label> <select class="user-form-input"
				name="userCategory" id="userCategory" required>
				<option value="">Select Role</option>
				<option value="0">Client</option>
				<option value="1">Employee</option>
				<option value="2">Manager</option>
			</select>
		</div>

		<div class="user-double-column">
			<div class="user-part">
				<label class="user-form-label" for="name">Name <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					name="name" placeholder="Name"
					pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" maxlength="30"
					title="Name should contain only letters, spaces, hyphens or apostrophes."
					required>
			</div>
			<div class="user-part client-only">
				<label class="user-form-label" for="dob">Date of Birth <span
					class="user-required">*</span></label> <input class="user-form-input" type="date"
					id="dob" name="dob" max="<%=minEligibleDate%>"
					title="You must be at least 18 years old.">
			</div>
		</div>

		<div class="user-gender">
			<label class="user-form-label">Gender <span class="user-required">*</span></label>
			<div class="user-options">
				<input class="user-radio-input" type="radio" name="gender" value="Male"
					required title="Select your gender."> <label
					class="user-radio-label">Male</label>
			</div>
			<div class="user-options">
				<input class="user-radio-input" type="radio" name="gender" value="Female"
					required title="Select your gender."> <label
					class="user-radio-label">Female</label>
			</div>
			<div class="user-options">
				<input class="user-radio-input" type="radio" name="gender" value="Other"
					required title="Select your gender."> <label
					class="user-radio-label">Other</label>
			</div>
		</div>

		<div class="user-double-column">
			<div class="user-part">
				<label class="user-form-label" for="email">Email <span
					class="user-required">*</span></label> <input class="user-form-input" type="email"
					id="email" name="email" required
					pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"
					title="Enter a valid email address (e.g., user@example.com).">
			</div>
			<div class="user-part">
				<label class="user-form-label" for="phone">Phone Number <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					id="phone" name="phoneNo" maxlength="10" pattern="\d{10}"
					title="Phone number must be 10 digits" required>
			</div>
		</div>

		<div class="user-double-column client-only">
			<div class="user-part">
				<label class="user-form-label" for="aadhar">Aadhar Number <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					id="aadhar" name="aadhar" maxlength="12" pattern="\d{12}"
					title="Aadhar number must be exactly 12 digits.">
			</div>
			<div class="user-part">
				<label class="user-form-label" for="pan">PAN Number <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]"
					oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')"
					title="PAN format: 5 uppercase letters, 4 digits, and 1 uppercase letter (e.g., ABCDE1234F).">
			</div>
		</div>

		<div class="user-address client-only">
			<label class="user-form-label" for="address">Address <span
				class="user-required">*</span></label> <input class="user-form-input" type="text"
				name="address" placeholder="Address" maxlength="60"
				title="Enter your full address (max 60 characters).">
		</div>

		<div class="user-part employee-only">
			<label class="user-form-label" for="branchId">Branch <span
				class="user-required">*</span></label> <select class="user-branch-input"
				name="branchId" id="branchId">
				<option value="">Select Branch</option>
			</select>
		</div>

		<div class="user-double-column">
			<div class="user-part">
				<label class="user-form-label" for="password">Set Password <span
					class="user-required">*</span></label> <input class="user-form-input" type="password"
					id="password" name="password" maxlength="20"
					pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
					title="Password must be 8-20 characters, include uppercase, lowercase, number, and a special character.">
			</div>
			<div class="user-part">
				<label class="user-form-label" for="confirm-password">Confirm
					Password <span class="user-required">*</span>
				</label> <input class="user-form-input" type="password" id="confirm-password"
					name="confirm_password" maxlength="20"
					pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
					title="Must match the password above.">
			</div>
		</div>

		<div class="user-show-password">
			<input type="checkbox" id="show-password"> <label
				for="show-password">Show Password</label>
		</div>
		<span id="password-error" class="user-error-message"></span>

		<div class="user-buttons">
			<button class="user-form-button" type="submit">Submit</button>
			<button class="user-form-button" type="reset">Reset</button>
		</div>
	</form>
</div>
</div>