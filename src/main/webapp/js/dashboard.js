function handleLogout() {
    fetch("/MRN_BANKING/MRNBank/logout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                window.location.href = "/MRN_BANKING/logout.jsp";
            }
        })
        .catch(error => {
            console.error("Logout failed", error);
            alert("Logout failed. Please try again.");
        });
}

function toggleSidebar() {
    const sidebar = document.getElementById("sidebar");
    const icon = document.getElementById("toggle-icon");
    sidebar.classList.toggle("expanded");
    icon.textContent = sidebar.classList.contains("expanded") ? "close" : "menu";
}

function loadContent(page) {
    const container = document.getElementById("main-content");
    container.innerHTML = "<p>Loading...</p>";

    fetch("includes/" + page)
	.then(response => {
	            if (response.status === 401) {
	                // This means your JSP sent the JSON error (session expired)
	                return response.json().then(data => {
	                    console.warn("Session expired. Redirecting to login.");

	                    if (typeof handleResponse === "function") {
	                        handleResponse(data); // Show toast or modal
	                    }

	                    setTimeout(() => {
	                        window.location.href = `${window.appContext}/login.jsp`;
	                    }, 1500);

	                    throw new Error("Unauthorized - session expired");
	                });
	            }

	            if (!response.ok) {
	                throw new Error("Failed to load content. HTTP " + response.status);
	            }

	            return response.text(); // For normal successful HTML pages
        })
        .then(html => {
            container.innerHTML = html;

			if (page === "dashboard-profile.jsp") {
			    loadScriptOnce("profileScriptLoaded", "/js/dashboard-profile.js", "initProfileScript");
			} else if (page === "dashboard-accounts.jsp") {
			    loadScriptOnce("accountsScriptLoaded", "/js/dashboard-accounts.js", "initAccountScript");
			} else if (page === "dashboard-statement.jsp") {
						    loadScriptOnce("statementScriptLoaded", "/js/dashboard-statement.js", "initStatementScript");
						}   
        })
        .catch(error => {
            console.error("Error loading content:", error);
            container.innerHTML = "<p>Failed to load content.</p>";
        });
}

function loadScriptOnce(scriptId, scriptPath, initFunctionName) {
    if (!window[scriptId]) {
        const script = document.createElement("script");
        script.src = `${window.appContext}${scriptPath}`;
        script.onload = () => {
            window[scriptId] = true;
            if (typeof window[initFunctionName] === "function") {
                window[initFunctionName]();
                console.log(`${scriptId} loaded and initialized.`);
            } else {
                console.warn(`${scriptId} loaded, but ${initFunctionName} not found.`);
            }
        };
        document.body.appendChild(script);
    } else {
        if (typeof window[initFunctionName] === "function") {
            window[initFunctionName]();
            console.log(`${initFunctionName} re-initialized.`);
        } else {
            console.warn(`${initFunctionName} already expected, but not found.`);
        }
    }
}

