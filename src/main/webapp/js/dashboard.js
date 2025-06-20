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
	
	const [basePage, queryString] = page.split("?");
	    const queryParams = new URLSearchParams(queryString);

	    // Optional: store query params in body so script can access it
	    document.body.setAttribute("data-client-id", queryParams.get("clientId") || "");

	    fetch("includes/" + basePage + (queryString ? "?" + queryString : ""))
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
			
			// Remove previous dynamic styles
            removeDynamicCss();
			
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

			if (page.startsWith("dashboard-profile.jsp")) {
				loadCssOnce("profileCss", "/css/dashboard-profile.css");
			    loadScriptOnce("profileScriptLoaded", "/js/dashboard-profile.js", "initProfileScript");
			} else if (page === "dashboard-accounts.jsp") {
				loadCssOnce("accountsCss", "/css/dashboard-accounts.css");
			    loadScriptOnce("accountsScriptLoaded", "/js/dashboard-accounts.js", "initAccountScript");
			} else if (page === "dashboard-statement.jsp") {
				loadCssOnce("statementCss", "/css/dashboard-statement.css");
			    loadScriptOnce("statementScriptLoaded", "/js/dashboard-statement.js", "initStatementScript");
			} else if (page === "dashboard-transaction.jsp") {
				loadCssOnce("transactionCss", "/css/dashboard-transaction.css");
			    loadScriptOnce("transactionScriptLoaded", "/js/dashboard-transaction.js", "initTransactionScript");
			} else if (page === "dashboard-clients.jsp") {
				loadCssOnce("clientsCss", "/css/dashboard-clients.css");
			    loadScriptOnce("clientsScriptLoaded", "/js/dashboard-clients.js", "initClientsScript");
			} else if (page === "dashboard-add-client.jsp") {
				loadCssOnce("addClientCss", "/css/dashboard-add-client.css");
	                loadScriptOnce("addClientScriptLoaded", "/js/dashboard-add-client.js", "initAddClientScript");
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

function loadCssOnce(cssId, cssPath) {
    if (!document.getElementById(cssId)) {
        const link = document.createElement("link");
        link.id = cssId;
        link.rel = "stylesheet";
        link.href = `${window.appContext}${cssPath}`;
        document.head.appendChild(link);
    }
}

function removeDynamicCss() {
    const dynamicCssIds = [
        "profileCss", "accountsCss", "statementCss",
        "transactionCss", "clientsCss", "addClientCss"
    ];
    dynamicCssIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.remove();
    });
}

