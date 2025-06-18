<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="profile-container">
    <h2>User Profile</h2>
    <form id="profileForm" class="signup-form">
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
                <input class="form-input" type="email" id="email" name="email" disabled required>
            </div>
            <div class="part">
                <label class="form-label" for="phone">Phone Number</label>
                <input class="form-input" type="text" id="phone" name="phoneNo" disabled required>
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

        <div class="buttons">
            <button class="form-button" type="button" id="edit-btn">Edit</button>
            <button class="form-button" type="submit" id="save-btn" style="display: none;">Save</button>
        </div>
    </form>
</div>

<%-- <script>
    const form = document.getElementById("profileForm");
    const editBtn = document.getElementById("edit-btn");
    const saveBtn = document.getElementById("save-btn");

    // Fetch and populate profile data
    function fetchProfileData() {
        const userId = <%= session.getAttribute("userId") %>;

        fetch("http://localhost:8080/MRN_BANKING/MRNBank/client", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "GET"
            },
            body: JSON.stringify({ userId })
        })
        .then(response => response.json())
        .then(data => {
            const c = data.clients;
            form.name.value = c.name;
            form.dob.value = c.dob;
            form.gender.value = c.gender;
            form.email.value = c.email;
            form.phoneNo.value = c.phoneNo;
            form.aadhar.value = c.aadhar;
            form.pan.value = c.pan;
            form.address.value = c.address;
            [...form.gender].forEach(r => r.checked = r.value === c.gender);
        })
        .catch(err => console.error("Error loading profile:", err));
    }

    // Enable edit mode
    editBtn.addEventListener("click", () => {
        [...form.elements].forEach(el => el.disabled = false);
        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
    });

    // Handle form submission (save)
    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const formData = new FormData(form);
        const jsonBody = {};
        for (const [key, value] of formData.entries()) {
            jsonBody[key] = value;
        }

        jsonBody.userId = <%= session.getAttribute("userId") %>;

        fetch("http://localhost:8080/MRN_BANKING/MRNBank/client", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "PUT"
            },
            body: JSON.stringify(jsonBody)
        })
        .then(res => res.json())
        .then(data => {
            alert("Profile updated successfully!");
            window.location.reload();
        })
        .catch(err => {
            console.error("Error updating profile:", err);
            alert("Failed to update profile.");
        });
    });

    // Load on page
    document.addEventListener("DOMContentLoaded", fetchProfileData);
</script>
 --%>
