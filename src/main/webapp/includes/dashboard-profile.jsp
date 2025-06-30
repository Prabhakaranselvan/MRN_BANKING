<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ page import="java.time.LocalDate"%>

<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="profile-modal-overlay" id="profile-modal">
  <div class="profile-modal-box">
    <button class="profile-close-btn" onclick="closeProfileModal()">✖</button>
    <form id="MRN-Form" class="profile-mrn-form">
    <h2 class="profile-form-header">USER PROFILE</h2>
        <div class="profile-double-column">
            <div class="profile-part">
                <label class="profile-form-label" for="name">Name</label>
                <input class="profile-form-input" type="text" id="name" name="name" placeholder="Name" 
					pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" disabled required maxlength="30"
					title="Name should contain only letters, spaces, hyphens or apostrophes.">
            </div>
            
            <div class="profile-part"  id="dob-wrapper">
				<%
				LocalDate today = LocalDate.now();
				LocalDate minEligibleDate = today.minusYears(18);
				%>
                <label class="profile-form-label" for="dob">Date of Birth</label>
                <input class="profile-form-input" type="date" id="dob" name="dob" max="<%=minEligibleDate%>" disabled 
                	title="You must be at least 18 years old." required>
            </div>
        </div>
        
        <input type="hidden" id="userCategory" name="userCategory" value="0" required>

        <div class="profile-gender">
            <label class="profile-form-label">Gender</label>
            <div class="profile-options">
                <input class="profile-radio-input" type="radio" name="gender" value="Male" disabled required title="Select your gender.">
                <label class="profile-radio-label">Male</label>
            </div>
            <div class="profile-options">
                <input class="profile-radio-input" type="radio" name="gender" value="Female" disabled required title="Select your gender.">
                <label class="profile-radio-label">Female</label>
            </div>
            <div class="profile-options">
                <input class="profile-radio-input" type="radio" name="gender" value="Other" disabled required title="Select your gender.">
                <label class="profile-radio-label">Other</label>
            </div>
        </div>

        <div class="profile-double-column">
            <div class="profile-part">
                <label class="profile-form-label" for="email">Email</label>
                <input class="profile-form-input" type="email" id="email" name="email" 
                	pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" disabled required
                	title="Enter a valid email address (e.g., user@example.com).">
            </div>
            <div class="profile-part">
                <label class="profile-form-label" for="phone">Phone Number</label>
                <input class="profile-form-input" type="text" id="phone" name="phoneNo" maxlength="10" pattern="\d{10}"
					title="Phone number must be 10 digits" disabled required>
            </div>
        </div>
		<div id="client-only-fields">
        <div class="profile-double-column">
            <div class="profile-part">
                <label class="profile-form-label" for="aadhar">Aadhar</label>
                <input class="profile-form-input" type="text" id="aadhar" name="aadhar" maxlength="12" pattern="\d{12}" disabled required
                title="Aadhar number must be exactly 12 digits.">
            </div>
            <div class="profile-part">
                <label class="profile-form-label" for="pan">PAN</label>
                <input class="profile-form-input" type="text" id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]"
					oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')" disabled required
					title="PAN format: 5 uppercase letters, 4 digits, and 1 uppercase letter (e.g., ABCDE1234F).">
            </div>
        </div>

        <label class="profile-form-label" for="address">Address</label>
        <input class="profile-form-input" type="text" id="address" name="address" placeholder="Address" maxlength="60" disabled required
        	title="Enter your full address (max 60 characters).">
        </div>
        <div class="profile-double-column">
        	<div class="profile-part">
			    <label class="profile-form-label" for="status">Status</label>
			    <select class="profile-form-input" id="status" name="status" disabled required>
			        <option value="1" selected>Active</option>
			        <option value="0">Inactive</option>
			        <option value="2">Closed</option>
			    </select>
			</div>

			<div class="profile-part">
				 <div class="profile-password-confirm" style="display: none;">
				    <label class="profile-form-label" for="password">Password<span	class="profile-required">*</span></label>
					<input class="profile-form-input" type="password" id="password" name="password" maxlength="20"
						pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required
						title="Password must be 8-20 characters, include uppercase, lowercase, number, and a special character.">
				</div>	
			</div>
		</div>


        <div class="profile-buttons">
            <button class="profile-form-button" type="button" id="edit-btn">Edit</button>
            <button class="profile-form-button" type="submit" id="save-btn" style="display: none;">Save</button>
            <button class="profile-form-button" type="button" id="cancel-btn" style="display: none;">Cancel</button>
        </div>
    </form>
</div>    

</div>
