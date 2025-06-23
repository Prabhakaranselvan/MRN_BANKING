function initAddUserScript() {
	const form = document.getElementById("MRN-Form");
    const userCategorySelect = document.getElementById("userCategory");
    const branchSelect = document.getElementById("branchId");
    const dobField = document.getElementById("dob");
    const showPasswordCheckbox = document.getElementById("show-password");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirm-password");
    const errorMessage = document.getElementById("password-error");

	const userRole = parseInt(document.body.dataset.userRole);       // Logged-in user's role
    const targetRole = parseInt(document.body.dataset.targetRole);
	    
    if (!form || !password || !confirmPassword || !userCategorySelect) {
        console.warn("Add Client form elements not found.");
        return;
    }

    // Enforce numeric-only input
    const numberInputs = document.querySelectorAll("#phone, #aadhar");
    numberInputs.forEach(input => {
        input.addEventListener("input", function () {
            this.value = this.value.replace(/\D/g, '');
        });
    });

    // Toggle password visibility
    if (showPasswordCheckbox) {
        showPasswordCheckbox.addEventListener("change", function () {
            const type = this.checked ? "text" : "password";
            password.type = type;
            confirmPassword.type = type;
        });
    }
	
	// Fetch branches for dropdown
    fetch("/MRN_BANKING/MRNBank/branch", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Method": "GET"
        }
    })
    .then(res => res.json())
    .then(data => {
        branchSelect.innerHTML = '<option value="">Select Branch</option>';
        data.Branches.forEach(branch => {
            const option = document.createElement("option");
            option.value = branch.branchId;
            option.textContent = `${branch.branchName} (${branch.branchLocation})`;
            branchSelect.appendChild(option);
        });
    });

	// Role-based visibility
    userCategorySelect.addEventListener("change", () => {
        const value = parseInt(userCategorySelect.value);
        document.querySelectorAll(".client-only").forEach(el => el.style.display = value === 0 ? "block" : "none");
        document.querySelectorAll(".employee-only").forEach(el => el.style.display = value > 0 ? "block" : "none");
    });

	userCategorySelect.value = targetRole;
    // Restrict visible roles based on logged-in role
    const allowedRoles = userRole === 1 ? [0] : userRole === 2 ? [0, 1] : [0, 1, 2];
    Array.from(userCategorySelect.options).forEach(opt => {
        if (opt.value && !allowedRoles.includes(parseInt(opt.value))) {
            opt.disabled = true;
        }
    });

	// Form submission
	form.addEventListener("submit", function (e) {
	    e.preventDefault();

	    if (password.value !== confirmPassword.value) {
	        errorMessage.textContent = "Passwords do not match!";
	        errorMessage.style.color = "red";
	        return;
	    } else {
	        errorMessage.textContent = "";
	    }

	    const formData = new FormData(form);
	    const jsonBody = {};
	    for (const [key, value] of formData.entries()) {
	        if (key === "userCategory") {
	            jsonBody[key] = parseInt(value);
	        } else {
	            jsonBody[key] = value;
	        }
	    }

	    const endpoint = jsonBody.userCategory === 0 ? "/MRN_BANKING/MRNBank/client" : "/MRN_BANKING/MRNBank/employee";

	    fetch(endpoint, {
	        method: "POST",
	        headers: {
	            "Content-Type": "application/json",
	            "Method": "POST"
	        },
	        body: JSON.stringify(jsonBody)
	    })
	    .then(res => res.json())
	    .then(data => {
	        handleResponse(data);
	        if (data.message) form.reset();
	    })
	    .catch(err => {
	        console.error("Registration failed:", err);
	        handleResponse({ error: "Failed to submit form." });
	    });
	});

	// Initial trigger
	userCategorySelect.dispatchEvent(new Event("change"));
}
