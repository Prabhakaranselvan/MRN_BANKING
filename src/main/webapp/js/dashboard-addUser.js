function initAddUserScript() {
	const form = document.getElementById("MRN-Form");
    const userCategorySelect = document.getElementById("userCategory");
    const branchSelect = document.getElementById("branchSelect");
    const dobField = document.getElementById("dob");

	const userRole = parseInt(document.body.dataset.userRole);       // Logged-in user's role
    const targetRole = parseInt(document.body.dataset.targetRole);
	const userBranchId = parseInt(document.body.dataset.branchId);

    // Enforce numeric-only input
    const numberInputs = document.querySelectorAll("#phone, #aadhar");
    numberInputs.forEach(input => {
        input.addEventListener("input", function () {
            this.value = this.value.replace(/\D/g, '');
        });
    });
	
	const balanceInput = document.getElementById("balance");
	balanceInput.addEventListener("input", () => {
	    let value = balanceInput.value.replace(/[^0-9.]/g, '').replace(/^(\d*\.\d{0,2}).*$/, '$1');
	    let floatVal = parseFloat(value);
	    if (!isNaN(floatVal)) {
	      if (floatVal > 100000) value = '100000';
	      else if (floatVal < 1) value = '1';
	    }
	    balanceInput.value = value;
	  });
	
	// Prevent numbers in the name field
    const nameInput = document.querySelector("input[name='name']");
    nameInput.addEventListener("input", function () {
        this.value = this.value.replace(/[0-9]/g, ''); // Remove any digits
    });
	
	// Custom DOB validation: Show clear age-related error
	   const dob = document.getElementById("dob");
	   dob.addEventListener("invalid", function () {
	       const validity = dob.validity;
		   const minDateStr = dob.dataset.minBirthdateStr;
		   const maxDateStr = dob.dataset.maxBirthdateStr;

	       if (validity.rangeOverflow) {
	    	    // Too young: DOB after max (e.g., after 07/13/2007)
	    	    dob.setCustomValidity(`You must be at least 18 years old.\nAllowed DOB: on or before ${maxDateStr}`);
	    	    dob.reportValidity();
	    	} 
	    	else if (validity.rangeUnderflow) {
	    	    // Too old: DOB before min (e.g., before 07/13/1874)
	    	    dob.setCustomValidity(`You must be no older than 150 years.\nAllowed DOB: on or after ${minDateStr}`);
	    	    dob.reportValidity();
	    	}
	       else {
	           dob.setCustomValidity(""); // For other errors, use default
	       }
	   });

	   dob.addEventListener("input", function () {
	       dob.setCustomValidity(""); // Always clear previous message on input
	   });
	
	const isGM = userRole === 3;
	// Determine if branch selection should be fixed
    const isManagerAddingEmployee = userRole === 2 && targetRole === 1;
    const isManagerOrEmployeeAddingClient = (userRole === 1 || userRole === 2) && targetRole === 0;

	if (isGM) {
	        loadAllBranches(); // GM can assign any branch
	    } else if (isManagerAddingEmployee || isManagerOrEmployeeAddingClient) {
			branchSelect.style.pointerEvents = "none";
			branchSelect.style.backgroundColor = "#f4f4f4";
			branchSelect.style.color = "#777";

	        setFixedBranch(userBranchId); // Force-assign current user's branch
	    }

	function loadAllBranches() {
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
	        branchSelect.innerHTML = '<option value="">-- Select --</option>';
	        data.Branches.forEach(branch => {
            const option = document.createElement("option");
            option.value = branch.branchId;
            option.textContent = `${branch.branchName} (${branch.branchLocation})`;
            branchSelect.appendChild(option);
	        });
	    })
		    .catch(err => console.error("[loadAllBranches] Error:", err));
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
		const opt = option.cloneNode(true);
        opt.selected = true;
        branchSelect.appendChild(opt);
        branchSelect.value = value;
  	  
         console.log(`[setFixedBranch] value : ${branchSelect.value}`);
      })
	    .catch(err => console.error("[setFixedBranch] Error:", err));
	}

	// Role-based visibility
	userCategorySelect.addEventListener("change", () => {
	    const value = parseInt(userCategorySelect.value);

	    document.querySelectorAll(".client-only").forEach(el => {
	        const inputs = el.querySelectorAll("input, select");
	        if (value === 0) {
	            el.style.display = "flex";
	            inputs.forEach(input => input.disabled = false);  // ✅ enable
	        } else {
	            el.style.display = "none";
	            inputs.forEach(input => input.disabled = true);   // ✅ disable to skip validation
	        }
	    });
	});



	userCategorySelect.value = targetRole;
	userCategorySelect.dispatchEvent(new Event("change"));
    // Restrict visible roles based on logged-in role
    const allowedRoles = userRole === 1 ? [0] : userRole === 2 ? [0, 1] : [0, 1, 2];
    Array.from(userCategorySelect.options).forEach(opt => {
        if (opt.value && !allowedRoles.includes(parseInt(opt.value))) {
            opt.disabled = true;
        }
    });


	const password = document.getElementById("password");
	const confirmPassword = document.getElementById("confirm-password");
    const errorMessage = document.getElementById("password-error");
	let errorTimer = null;
		        
     // Function to show mismatch error
    function showPasswordMismatchError() {
        errorMessage.textContent = "Passwords do not match!";
        errorMessage.style.color = "red";

        if (errorTimer) clearTimeout(errorTimer);
        errorTimer = setTimeout(() => {
            errorMessage.textContent = "";
        }, 3000);
    }
	
	// Toggle password visibility
	const showPasswordCheckbox = document.getElementById("show-password");
	if (showPasswordCheckbox) {
	    showPasswordCheckbox.addEventListener("change", function () {
	        const type = this.checked ? "text" : "password";
	        password.type = type;
	        confirmPassword.type = type;
	    });
	}
			
	// Form submission
	form.addEventListener("submit", function (e) {
	    e.preventDefault();

		if (password.value !== confirmPassword.value) {
            showPasswordMismatchError();
            return;
        }

        errorMessage.textContent = ""; // Clear if matched

		const formData = new FormData(form);
	    const role = parseInt(formData.get("userCategory"));
	    const body = {};
		const user = {
	        userCategory: role,
	        name: formData.get("name"),
	        gender: formData.get("gender"),
	        email: formData.get("email"),
	        phoneNo: formData.get("phoneNo"),
	        password: formData.get("password")
	    };
		if (role === 0) {
		       // Client-specific
		       user.dob = formData.get("dob");
		       user.aadhar = formData.get("aadhar");
		       user.pan = formData.get("pan");
		       user.address = formData.get("address");

		       const account = {
		           branchId: parseInt(formData.get("branchId")),
		           accountType: parseInt(formData.get("accountTypeSelect")),
				   balance: parseInt(formData.get("balance"))
		       };

		       body.client = user;
		       body.account = account;
		   } else {
		       // Employee/Manager
		       user.branchId = parseInt(formData.get("branchId"));
		       body.userCategory = role;
		       Object.assign(body, user);
		   }

		   const endpoint = role === 0
		          ? "/MRN_BANKING/MRNBank/client"
		          : "/MRN_BANKING/MRNBank/employee";

	    fetch(endpoint, {
	        method: "POST",
	        headers: {
	            "Content-Type": "application/json",
	            "Method": "POST"
	        },
	        body: JSON.stringify(body)
	    })
	    .then(res => res.json())
	    .then(data => {
	        handleResponse(data);
			if (data.message) {
	            form.reset();
	            closeAddUserModal(); // ✅ close modal on success
	        };
	    })
	    .catch(err => {
	        console.error("Registration failed:", err);
	        handleResponse({ error: "Failed to submit form." });
	    });
	});

    // Optional: Show early mismatch if both fields are filled and focus leaves confirm
    confirmPassword.addEventListener("blur", () => {
        if (password.value && confirmPassword.value && password.value !== confirmPassword.value) {
            showPasswordMismatchError();
        }
    });

}
