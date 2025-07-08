function initProfileScript() {
    const form = document.getElementById("MRN-Form");
    const editBtn = document.getElementById("edit-btn");
    const saveBtn = document.getElementById("save-btn");
	const cancelBtn = document.getElementById("cancel-btn");

    const passwordField = document.querySelector(".profile-password-confirm");
	
	const targetId = document.body.getAttribute("data-target-id");
	const targetRole = parseInt(document.body.getAttribute("data-target-role"), 10);

    const userId = targetId || document.body.getAttribute("data-user-id");
    const userRole = parseInt(document.body.getAttribute("data-user-role"));
	
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
	
	const isSelfEdit = !targetId && userRole!==3;
	const isEditingClient = targetRole === 0 || userRole === 0;
	
	let isPrivilegedEditing = false;

	if (!isSelfEdit) {
		if (userRole ===3)
			{
				isPrivilegedEditing = true;
			}
			else
			{
				isPrivilegedEditing = targetRole < userRole;
			}
	}
	console.log(targetRole);
	console.log(userRole);
	console.log(isSelfEdit);
	console.log(isEditingClient);
	console.log(isPrivilegedEditing);


    const clientOnlyFields = document.getElementById("client-only-fields");
	const dobWrapper = document.getElementById("dob-wrapper");
    // Show/Hide DOB, Aadhar, PAN, Address only for clients
    if (clientOnlyFields) {
        if (!isEditingClient) {
            clientOnlyFields.style.display = "none";
        }
    }
	if (dobWrapper) {
	    if (!isEditingClient) {
	        dobWrapper.style.display = "none";
	    }
	}

    let isEditMode = false;

    if (!form || !editBtn || !saveBtn || !passwordField) {
        console.warn("Missing required DOM elements.");
        return;
    }

    // Show edit button if it's a self edit or privileged edit
    if (isSelfEdit || isPrivilegedEditing) {
        editBtn.style.display = "inline-block";
		

    }

    editBtn.addEventListener("click", () => {
        isEditMode = true;

        // Enable editable fields
		if (isPrivilegedEditing) {
		    if (targetRole === 0) {
		        // Editing a client — enable all fields
		        Array.from(form.elements).forEach(input => input.disabled = false);
		    } else {
		        // Editing an employee/manager — only enable specific fields
				form.name.disabled = false;
		        form.email.disabled = false;
		        form.phoneNo.disabled = false;
		        [...form.gender].forEach(radio => radio.disabled = false);
		        form.status.disabled = false;
		    }
		} else if (userRole === 0) { // Client editing self
            form.email.disabled = false;
            form.phoneNo.disabled = false;
            form.address.disabled = false;
        } else if ([1, 2].includes(userRole)) { // Employee/Manager editing self
            form.email.disabled = false;
            form.phoneNo.disabled = false;
        }

        editBtn.style.display = "none";
        saveBtn.style.display = "inline-block";
		cancelBtn.style.display = "inline-block";
        passwordField.style.display = "flex";
    });
	

	cancelBtn.addEventListener("click", () => {
	    isEditMode = false;

		if (isPrivilegedEditing) {
			openProfileModal(targetId, targetRole);
        } else {
			openProfileModal();
        }
	});

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
            password: formData.get("password")
        };

        if (isPrivilegedEditing) {
            Object.assign(jsonBody, {
                name: formData.get("name"),
                dob: formData.get("dob"),
                gender: formData.get("gender"),
                aadhar: formData.get("aadhar"),
                pan: formData.get("pan"),
                address: formData.get("address"),
                userCategory: formData.get("userCategory"),
                status: formData.get("status")
            });
        }
		
        const endpoint = isEditingClient ? "/MRN_BANKING/MRNBank/client" : "/MRN_BANKING/MRNBank/employee";

        fetch(endpoint, {
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

			if (isPrivilegedEditing) {
				openProfileModal(targetId, targetRole);
	        } else {
				openProfileModal();
	        }
        })
        .catch(err => {
            console.error("Error updating profile:", err);
            handleResponse({ error: "Failed to update profile. Try again." });
        });
    });

    fetchProfileData(form, userId, isEditingClient);
}

function fetchProfileData(form, userId, isEditingClient) {
    const endpoint = isEditingClient ? "/MRN_BANKING/MRNBank/client" : "/MRN_BANKING/MRNBank/employee";

    fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Method": "GET"
        },
        body: JSON.stringify({ userId })
    })
    .then(response => response.json().then(data => {
        if (!response.ok) throw data;
        return data;
    }))
    .then(data => {
        console.log("Fetch profile response:", data);
        const user = data.clients || data.Employee;

        if (user) {
            form.name.value = user.name;
            form.dob.value = user.dob || "";
            [...form.gender].forEach(r => r.checked = r.value === user.gender);
            form.email.value = user.email;
            form.phoneNo.value = user.phoneNo;
            form.aadhar.value = user.aadhar || "";
            form.pan.value = user.pan || "";
            form.address.value = user.address || "";
            form.userCategory.value = user.userCategory;
            form.status.value = user.status;
        }
    })
    .catch(err => {
        console.error("Network or JSON error in fetchProfileData:", err);
        handleResponse(err);
    });
}
