<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>
<%@ page import="java.time.LocalDate" %>
<%
    LocalDate today = LocalDate.now();
    LocalDate minEligibleDate = today.minusYears(18);
%>
<div class="form-container">
  <form id="MRN-Form" class="mrn-form">
    <h2 class="form-header">USER REGISTRATION</h2>

    <div class="part">
      <label class="form-label" for="userCategory">Register As <span class="required">*</span></label>
      <select class="form-input" name="userCategory" id="userCategory" required>
        <option value="">Select Role</option>
        <option value="0">Client</option>
        <option value="1">Employee</option>
        <option value="2">Manager</option>
      </select>
    </div>

    <div class="double-column">
      <div class="part">
        <label class="form-label" for="name">Name <span class="required">*</span></label>
        <input class="form-input" type="text" name="name" placeholder="Name" pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" maxlength="30" 
        	title="Name should contain only letters, spaces, hyphens or apostrophes." required>
      </div>
      <div class="part client-only">
        <label class="form-label" for="dob">Date of Birth <span class="required">*</span></label>
        <input class="form-input" type="date" id="dob" name="dob" max="<%=minEligibleDate%>"
        	title="You must be at least 18 years old.">
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
      <div class="part">
        <label class="form-label" for="email">Email <span class="required">*</span></label>
        <input class="form-input" type="email" id="email" name="email" required
        	pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" 
			title="Enter a valid email address (e.g., user@example.com).">
      </div>
      <div class="part">
        <label class="form-label" for="phone">Phone Number <span class="required">*</span></label>
        <input class="form-input" type="text" id="phone" name="phoneNo" maxlength="10" pattern="\d{10}" 
        	title="Phone number must be 10 digits" required>
      </div>
    </div>

    <div class="double-column client-only">
      <div class="part">
        <label class="form-label" for="aadhar">Aadhar Number <span class="required">*</span></label>
        <input class="form-input" type="text" id="aadhar" name="aadhar" maxlength="12" pattern="\d{12}"
        	title="Aadhar number must be exactly 12 digits.">
      </div>
      <div class="part">
        <label class="form-label" for="pan">PAN Number <span class="required">*</span></label>
        <input class="form-input" type="text" id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]" 
        	oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')"
        	title="PAN format: 5 uppercase letters, 4 digits, and 1 uppercase letter (e.g., ABCDE1234F).">
      </div>
    </div>

    <div class="address client-only">
      <label class="form-label" for="address">Address <span class="required">*</span></label>
      <input class="form-input" type="text" name="address" placeholder="Address" maxlength="60"
      	title="Enter your full address (max 60 characters).">
    </div>

    <div class="part employee-only">
      <label class="form-label" for="branchId">Branch <span class="required">*</span></label>
      <select class="branch-input" name="branchId" id="branchId">
        <option value="">Select Branch</option>
      </select>
    </div>

    <div class="double-column">
      <div class="part">
        <label class="form-label" for="password">Set Password <span class="required">*</span></label>
        <input class="form-input" type="password" id="password" name="password" maxlength="20" 
        	pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
        	title="Password must be 8-20 characters, include uppercase, lowercase, number, and a special character.">
      </div>
      <div class="part">
        <label class="form-label" for="confirm-password">Confirm Password <span class="required">*</span></label>
        <input class="form-input" type="password" id="confirm-password" name="confirm_password" maxlength="20" 
        	pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
        	title="Must match the password above.">
      </div>
    </div>

    <div class="show-password">
      <input type="checkbox" id="show-password"> <label for="show-password">Show Password</label>
    </div>
    <span id="password-error" class="error-message"></span>

    <div class="buttons">
      <button class="form-button" type="submit">Submit</button>
      <button class="form-button" type="reset">Reset</button>
    </div>
  </form>
</div>