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
			
			// Remove 'active' class from all sidebar links
			document.querySelectorAll('.nav-links a').forEach(function(link) {
			    link.classList.remove('active');
			});

			// Find the clicked link by checking if its onclick attribute contains the page name
			var clickedLink = Array.from(document.querySelectorAll('.nav-links a')).find(function(a) {
			    var onclickAttr = a.getAttribute('onclick');
			    return onclickAttr && onclickAttr.indexOf(page) !== -1;
			});

			// Add 'active' class to the matching link
			if (clickedLink) {
			    clickedLink.classList.add('active');
			}

			if (page === "dashboard-profile.jsp") {
			    loadScriptOnce("profileScriptLoaded", "/js/dashboard-profile.js", "initProfileScript");
			} else if (page === "dashboard-accounts.jsp") {
			    loadScriptOnce("accountsScriptLoaded", "/js/dashboard-accounts.js", "initAccountScript");
			} else if (page === "dashboard-statement.jsp") {
			    loadScriptOnce("statementScriptLoaded", "/js/dashboard-statement.js", "initStatementScript");
			} else if (page === "dashboard-transaction.jsp") {
			    loadScriptOnce("transactionScriptLoaded", "/js/dashboard-transaction.js", "initTransactionScript");
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

