<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

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
					name="name" pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" maxlength="70"
					title="Name should contain only letters, spaces, hyphens, and apostrophes (1–70 characters)." required>
			</div>
			<%
				LocalDate today = LocalDate.now();
				LocalDate maxBirthDate = today.minusYears(18);     // Latest allowed DOB (Atleast 18 years old)
				LocalDate minBirthDate = today.minusYears(150);    // Earliest allowed DOB (max age 150 years)
				
				DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
				String maxBirthDateStr = maxBirthDate.format(dobFormatter); // e.g., 07/13/2007
				String minBirthDateStr = minBirthDate.format(dobFormatter); // e.g., 07/13/1875
			%>
			<div class="user-part client-only">
				<label class="user-form-label" for="dob">Date of Birth <span
					class="user-required">*</span></label> <input class="user-form-input" type="date"
					id="dob" name="dob" min="<%=minBirthDate%>"	max="<%=maxBirthDate%>"  
					title="You must be between 18 and 150 years old. Allowed DOB: <%= minBirthDateStr %> to <%= maxBirthDateStr %>" 
					 data-min-birthdate-str="<%=minBirthDateStr%>" data-max-birthdate-str="<%=maxBirthDateStr%>" required>
					
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
					pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" maxlength="250"
							title="Please enter a valid email (e.g., user@example.com) with 6–250 characters.">
			</div>
			<div class="user-part">
				<label class="user-form-label" for="phone">Phone Number <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					id="phone" name="phoneNo" maxlength="10" pattern="\d{10}"
					title="Phone number must be exactly 10 digits." required>
			</div>
		</div>
		
		<div class="user-double-column">
			<div class="user-part">
				<label class="user-form-label" for="branchSelect">Branch <span class="required">*</span></label> 
				 <select  class="user-form-input" id="branchSelect" name="branchId" required></select>
			</div>
			<div class="user-part client-only">
				<label class="user-form-label" for="accountTypeSelect">Account Type<span class="required">*</span></label> 
				<select  class="user-form-input" id="accountTypeSelect" name="accountTypeSelect" required>
			        <option value="">-- Select --</option>
			        <option value="1">Savings</option>
			        <option value="2">Current</option>
			        <option value="3">Fixed Deposit</option>
			      </select>
			</div>
		</div>
		
		<div class="user-part client-only">
			<label class="user-form-label" for="balance">Opening Balance <span class="user-required">*</span></label>
			<input class="user-form-input" type="text" id="balance" name="balance" maxlength="8" 
			title="Amount must be between ₹1 and ₹1,00,000 (one lakh), with up to 2 decimal places"
       				placeholder="₹1–₹1,00,000 (max 2 decimal places)" required>
		</div>

		<div class="user-double-column client-only">
			<div class="user-part">
				<label class="user-form-label" for="aadhar">Aadhar Number <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					id="aadhar" name="aadhar" maxlength="12" pattern="\d{12}" required
					title="Aadhar number must be exactly 12 digits.">
			</div>
			<div class="user-part">
				<label class="user-form-label" for="pan">PAN Number <span
					class="user-required">*</span></label> <input class="user-form-input" type="text"
					id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]" required
					oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')"
					title="PAN must be in format: 5 uppercase letters, 4 digits, and 1 uppercase letter (e.g., ABCDE1234F).">
			</div>
		</div>

		<div class="user-address client-only">
			<label class="user-form-label" for="address">Address <span class="user-required">*</span></label> 
			<input class="user-form-input" type="text" name="address" minlength="5" maxlength="60" 
					pattern="[A-Za-z0-9\\\.\/ ,\-']+" required
					title="Address should be 5–100 characters long with only letters, digits, and symbols like , . ' / \ -" >
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
					title="Confirm your password. It must match the password you entered already.">
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