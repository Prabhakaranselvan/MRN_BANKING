// dashboard-profile.js

function initProfileScript() {
    const form = document.getElementById("profileForm");
    const editBtn = document.getElementById("edit-btn");
    const saveBtn = document.getElementById("save-btn");
    const passwordField = document.querySelector(".password-confirm");
    const userId = document.body.getAttribute("data-user-id");

    let isEditMode = false;

    if (!form || !editBtn || !saveBtn || !passwordField) {
        console.warn("Missing required DOM elements.");
        return;
    }

    // Enable edit mode
    editBtn.addEventListener("click", () => {
        isEditMode = true;

        // Enable only selected fields
        form.email.disabled = false;
        form.phoneNo.disabled = false;
        form.address.disabled = false;

        // Show save button and password field
        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
        passwordField.style.display = "flex";
    });

    // Handle form submission
    form.addEventListener("submit", function (e) {
        e.preventDefault();

        if (!isEditMode) {
            console.warn("Edit mode not active. Submission blocked.");
            return;
        }

        const formData = new FormData(form);
        const jsonBody = {
            userId: userId,
            email: formData.get("email"),
            phoneNo: formData.get("phoneNo"),
            address: formData.get("address"),
            password: formData.get("password") // Include password for confirmation
        };

        fetch("/MRN_BANKING/MRNBank/client", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "PUT"
            },
            body: JSON.stringify(jsonBody)
        })
            .then(res => res.json())
            .then(data => {
                console.log("Update profile response:", data);
                handleResponse(data);
                isEditMode = false;

                // Reload just the profile section
                loadContent("dashboard-profile.jsp");
            })
            .catch(err => {
                console.error("Error updating profile:", err);
                handleResponse({ error: "Failed to update profile. Try again." });
            });
    });

    fetchProfileData(form, userId);
}


function fetchProfileData(form, userId) {
    fetch("/MRN_BANKING/MRNBank/client", {
        method: "POST", // Or GET, depending on your actual servlet config
        headers: {
            "Content-Type": "application/json",
			"Method": "GET"
        },
        body: JSON.stringify({ userId })
    })
	.then(response =>
	     response.json().then(data => {
	         if (!response.ok) {
	             // Still handle error (HTTP status) by throwing parsed JSON
	             throw data;
	         }
	         return data;
	     })
	 )
	 .then(data => {
	     console.log("Fetch profile response:", data);

	     if (data.clients) {
	         const c = data.clients;
	         form.name.value = c.name;
	         form.dob.value = c.dob;
	         [...form.gender].forEach(r => r.checked = r.value === c.gender);
	         form.email.value = c.email;
	         form.phoneNo.value = c.phoneNo;
	         form.aadhar.value = c.aadhar;
	         form.pan.value = c.pan;
	         form.address.value = c.address;
	     }
	 })
    .catch(err => {
        console.error("Network or JSON error in fetchProfileData:", err);
        handleResponse(err);
    });
}

