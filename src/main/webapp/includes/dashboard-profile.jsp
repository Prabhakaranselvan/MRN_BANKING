<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<div class="profile-modal-overlay" id="profile-modal">
  <div class="profile-modal-box">
    <button class="profile-close-btn" onclick="closeProfileModal()">✖</button>
    <form id="MRN-Form" class="profile-mrn-form">
    <h2 class="profile-form-header">USER PROFILE</h2>
        <div class="profile-double-column">
            <div class="profile-part">
                <label class="profile-form-label" for="name">Name</label>
                <input class="profile-form-input" type="text" id="name" name="name"
					pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" disabled required maxlength="70"
					title="Name should contain only letters, spaces, hyphens, and apostrophes (1–70 characters).">
            </div>
            
            <div class="profile-part"  id="dob-wrapper">
				<%
				LocalDate today = LocalDate.now();
				LocalDate maxBirthDate = today.minusYears(18);     // Latest allowed DOB (must be at least 18 years old)
				LocalDate minBirthDate = today.minusYears(150);    // Earliest allowed DOB (max age 150 years)

				DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
				String maxBirthDateStr = maxBirthDate.format(dobFormatter); // e.g., 07/13/2007
				String minBirthDateStr = minBirthDate.format(dobFormatter); // e.g., 07/13/1875
				%>
                <label class="profile-form-label" for="dob">Date of Birth</label>
                <input class="profile-form-input" type="date" id="dob" name="dob" min="<%=minBirthDate%>" max="<%=maxBirthDate%>"  
                title="You must be between 18 and 150 years old. Allowed DOB: <%= minBirthDateStr %> to <%= maxBirthDateStr %>" 
                data-min-birthdate-str="<%=minBirthDateStr%>" data-max-birthdate-str="<%=maxBirthDateStr%>"	disabled required>
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
                	pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" maxlength="250" 
                	title="Please enter a valid email (e.g., user@example.com) with 6–250 characters."disabled required>
            </div>
            <div class="profile-part">
                <label class="profile-form-label" for="phone">Phone Number</label>
                <input class="profile-form-input" type="text" id="phone" name="phoneNo" maxlength="10" pattern="\d{10}"
					title="Phone number must be exactly 10 digits." disabled required>
            </div>
        </div>
		<div id="client-only-fields">
        <div class="profile-double-column">
            <div class="profile-part">
                <label class="profile-form-label" for="aadhar">Aadhar</label>
                <input class="profile-form-input" type="text" id="aadhar" name="aadhar" maxlength="12" 
                pattern="\d{12}" title="Aadhar number must be exactly 12 digits."  disabled required >
            </div>
            <div class="profile-part">
                <label class="profile-form-label" for="pan">PAN</label>
                <input class="profile-form-input" type="text" id="pan" name="pan" maxlength="10" pattern="[A-Z]{5}\d{4}[A-Z]"
					oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')" disabled required
					title="PAN must be in format: 5 uppercase letters, 4 digits, and 1 uppercase letter (e.g., ABCDE1234F).">
            </div>
        </div>

        <label class="profile-form-label" for="address">Address</label>
        <input class="profile-form-input" type="text" id="address" name="address" minlength="5" maxlength="60" 
					pattern="[A-Za-z0-9\\\.\/ ,\-']+" disabled required
					title="Address should be 5–100 characters long with only letters, digits, and symbols like , . ' / \ -" >
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
					<span class="material-symbols-outlined password-toggle-icon" title="Show Password">visibility</span>
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
