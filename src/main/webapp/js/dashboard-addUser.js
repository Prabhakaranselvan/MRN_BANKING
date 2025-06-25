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
	const userBranchId = parseInt(document.body.dataset.branchId);
	    
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
	
	// Prevent numbers in the name field
    const nameInput = document.querySelector("input[name='name']");
    nameInput.addEventListener("input", function () {
        this.value = this.value.replace(/[0-9]/g, ''); // Remove any digits
    });

    // Toggle password visibility
    if (showPasswordCheckbox) {
        showPasswordCheckbox.addEventListener("change", function () {
            const type = this.checked ? "text" : "password";
            password.type = type;
            confirmPassword.type = type;
        });
    }
	
	// Determine if branch selection should be fixed
	const isManagerAddingEmployee = userRole === 2 && targetRole === 1;

	if (isManagerAddingEmployee) {
		document.querySelectorAll("select.branch-input").forEach(select => {
	      select.style.pointerEvents = "none";
	      select.style.backgroundColor = "#f4f4f4";
	      select.style.color = "#777";
	    });
	  setFixedBranch(userBranchId);
	} else {
	  loadBranches();
	}

	function loadBranches() {
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
	    })
		    .catch(err => console.error("[loadBranches] Error:", err));
		}

	function setFixedBranch(branchId) {
	 
	  fetch("/MRN_BANKING/MRNBank/branch", {
	    method: "POST",
	    headers: {
	      "Content-Type": "application/json",
	      Method: "GET"
	    },
	    body: JSON.stringify({ branchId })
	  })
	    .then(res => res.json())
	    .then(data => {
	      if (!data.branch) {
	        console.warn("[setFixedBranch] No branch data returned", data);
	        return;
	      }

	      const label = `${data.branch.branchName} (${data.branch.branchLocation})`;
	      const value = String(data.branch.branchId);
	      const option = new Option(label, value);

	      branchSelect.innerHTML = "";
	      branchSelect.appendChild(option.cloneNode(true));
	      branchSelect.value = value;
		  
		  document.querySelectorAll("select.branch-input").forEach((select, i) => {
            select.innerHTML = "";
            const opt = option.cloneNode(true);
            opt.selected = true;
            select.appendChild(opt);
            select.value = value;
            console.log(`[setFixedBranch] Set branch-input[${i}] value to: ${select.value}`);
          });
	    })
	    .catch(err => console.error("[setFixedBranch] Error:", err));
	}

	// Role-based visibility
    userCategorySelect.addEventListener("change", () => {
        const value = parseInt(userCategorySelect.value);
        document.querySelectorAll(".client-only").forEach(el => el.style.display = value === 0 ? "flex" : "none");
        document.querySelectorAll(".employee-only").forEach(el => el.style.display = value > 0 ? "flex" : "none");
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
