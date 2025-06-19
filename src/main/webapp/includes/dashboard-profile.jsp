<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class=profile-container>
    <form id="profileForm" class="profile-form">
    <h2 class="form-header">USER PROFILE</h2>
        <div class="double-column">
            <div class="part">
                <label class="form-label" for="name">Name</label>
                <input class="form-input" type="text" id="name" name="name" disabled required>
            </div>
            
            <div class="part">
                <label class="form-label" for="dob">Date of Birth</label>
                <input class="form-input" type="date" id="dob" name="dob" disabled required>
            </div>
        </div>

        <div class="gender">
            <label class="form-label">Gender</label>
            <div class="options">
                <input class="radio-input" type="radio" name="gender" value="Male" disabled>
                <label class="radio-label">Male</label>
            </div>
            <div class="options">
                <input class="radio-input" type="radio" name="gender" value="Female" disabled>
                <label class="radio-label">Female</label>
            </div>
            <div class="options">
                <input class="radio-input" type="radio" name="gender" value="Other" disabled>
                <label class="radio-label">Other</label>
            </div>
        </div>

        <div class="double-column">
            <div class="part">
                <label class="form-label" for="email">Email</label>
                <input class="form-input" type="email" id="email" name="email" 
                	pattern="[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}" disabled required>
            </div>
            <div class="part">
                <label class="form-label" for="phone">Phone Number</label>
                <input class="form-input" type="text" id="phone" name="phoneNo" maxlength="10" pattern="\d{10}"
					title="Phone number must be 10 digits" disabled required>
            </div>
        </div>

        <div class="double-column">
            <div class="part">
                <label class="form-label" for="aadhar">Aadhar</label>
                <input class="form-input" type="text" id="aadhar" name="aadhar" disabled required>
            </div>
            <div class="part">
                <label class="form-label" for="pan">PAN</label>
                <input class="form-input" type="text" id="pan" name="pan" disabled required>
            </div>
        </div>

        <label class="form-label" for="address">Address</label>
        <input class="form-input" type="text" id="address" name="address" disabled required>
        
        <div class="double-column">
			<div class="part">
				 <div class="password-confirm" style="display: none;">
				    <label class="form-label" for="password">Confirm With Password<span	class="required">*</span></label>
					<input class="form-input" type="password" id="password" name="password" maxlength="20"
						pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required>
				</div>	
			</div>
		</div>


        <div class="buttons">
            <button class="form-button" type="button" id="edit-btn">Edit</button>
            <button class="form-button" type="submit" id="save-btn" style="display: none;">Save</button>
        </div>
    </form>
</div>    
    
     <script src="${pageContext.request.contextPath}/js/dashboard-profile.js"></script>
