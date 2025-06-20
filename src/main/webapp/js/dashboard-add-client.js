function initAddClientScript() {
    const form = document.getElementById("signupForm");
    const password = document.getElementById("password");
    const confirmPassword = document.getElementById("confirm-password");
    const showPasswordCheckbox = document.getElementById("show-password");
    const errorMessage = document.getElementById("password-error");

    if (!form || !password || !confirmPassword) {
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

    // Handle form submission
    form.addEventListener("submit", function (event) {
        event.preventDefault();

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
            jsonBody[key] = (key === "userCategory") ? parseInt(value) : value;
        }

        fetch("/MRN_BANKING/MRNBank/client", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "POST"
            },
            body: JSON.stringify(jsonBody)
        })
        .then(response => response.json())
        .then(data => {
            console.log("Add Client response:", data);
            handleResponse(data);
			
			if (data.message) {
	           form.reset(); // ✅ Reset form only if successful
	       }
        })
        .catch(error => {
            console.error("Error adding client:", error);
            handleResponse({ error: "An error occurred while submitting the form." });
        });
    });
}
