function handleLogout() {
    fetch("/MRN_BANKING/MRNBank/logout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
			"Method": "POST"
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
	let spinnerTimeout = setTimeout(() => {
	    container.innerHTML = `
	      <div class="loading-spinner">
	        <span class="material-icons">hourglass_top</span> Loading...
	      </div>
	    `;
	}, 150); // show spinner only if load takes longer than 150ms
	
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
	                    }, 1000);

	                    throw new Error("Unauthorized - session expired");
	                });
	            }

	            if (!response.ok) {
	                throw new Error("Failed to load content. HTTP " + response.status);
	            }

	            return response.text(); // For normal successful HTML pages
        })
        .then(html => {
			clearTimeout(spinnerTimeout); // ✅ Cancel spinner if content arrived fast
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

			if (page === "dashboard-accounts.jsp") {
				loadCssOnce("accountsCss", "/css/dashboard-accounts.css");
			    loadScriptOnce("accountsScriptLoaded", "/js/dashboard-accounts.js", "initAccountsScript");
			} 
			else if (page === "dashboard-client-accounts.jsp") {
				loadCssOnce("clientAccountsCss", "/css/dashboard-client-accounts.css");
			    loadScriptOnce("clientAccountsScriptLoaded", "/js/dashboard-client-accounts.js", "initClientAccountsScript");
			} 
			else if (page === "dashboard-statement.jsp") {
				loadCssOnce("statementCss", "/css/dashboard-statement.css");
			    loadScriptOnce("statementScriptLoaded", "/js/dashboard-statement.js", "initStatementScript");
			} 
			else if (page === "dashboard-transaction.jsp") {
				loadCssOnce("transactionCss", "/css/dashboard-transaction.css");
			    loadScriptOnce("transactionScriptLoaded", "/js/dashboard-transaction.js", "initTransactionScript");
			} 
			else if (page === "dashboard-clients.jsp") {
				loadCssOnce("clientsCss", "/css/dashboard-clients.css");
			    loadScriptOnce("clientsScriptLoaded", "/js/dashboard-clients.js", "initClientsScript");
			} 
			else if (page === "dashboard-main.jsp") {
				loadCssOnce("mainCss", "/css/dashboard-main.css");
                loadScriptOnce("mainScriptLoaded", "/js/dashboard-main.js", "initMainScript");
			} 
			else if (page === "dashboard-employee.jsp") {
		        loadCssOnce("employeeCss", "/css/dashboard-employee.css");
		        loadScriptOnce("employeeScriptLoaded", "/js/dashboard-employee.js", "initEmployeesScript");
	      	}
			else if (page === "dashboard-branch.jsp") {
		        loadCssOnce("branchCss", "/css/dashboard-branch.css");
		        loadScriptOnce("branchScriptLoaded", "/js/dashboard-branch.js", "initBranchScript");
	      	}
			else if (page === "dashboard-request.jsp") {
		        loadCssOnce("requestCss", "/css/dashboard-request.css");
		        loadScriptOnce("requestScriptLoaded", "/js/dashboard-request.js", "initRequestScript");
	      	}
			else if (page === "dashboard-admin.jsp") {
		        loadCssOnce("adminCss", "/css/dashboard-admin.css");
		        loadScriptOnce("adminScriptLoaded", "/js/dashboard-admin.js", "initAdminScript");
	      	}
        })
        .catch(error => {
			clearTimeout(spinnerTimeout);
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
        "accountsCss", "clientAccountsCss", "statementCss", 
        "transactionCss", "clientsCss", "employeeCss", "branchCss", "requestCss", "adminCss"
    ];
    dynamicCssIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.remove();
    });
}


function openProfileModal(targetId = null, targetRole = null) {
	const modalRoot = document.getElementById("modal-root");


	   document.body.setAttribute("data-target-id", targetId != null ? targetId : '');
	   document.body.setAttribute("data-target-role", targetRole != null ? targetRole : '');

	    // Build URL conditionally
	    const baseUrl = `${window.appContext}/includes/dashboard-profile.jsp`;
	    const fetchUrl = (targetId != null && targetRole != null)
	        ? `${baseUrl}?targetId=${targetId}&targetRole=${targetRole}`
	        : baseUrl;

	    fetch(fetchUrl)
        .then(response => {
            if (response.status === 401) {
                return response.json().then(data => {
                    console.warn("Session expired. Redirecting to login.");
                    if (typeof handleResponse === "function") handleResponse(data);
                    setTimeout(() => {
                        window.location.href = `${window.appContext}/login.jsp`;
                    }, 1000);
                    throw new Error("Unauthorized - session expired");
                });
            }

            if (!response.ok) throw new Error("Failed to load profile modal. HTTP " + response.status);
            return response.text();
        })
        .then(html => {
            modalRoot.innerHTML = html;
            document.body.style.overflow = "hidden";
            loadScriptOnce("profileScriptLoaded", `/js/dashboard-profile.js`, "initProfileScript");
        })
        .catch(err => {
            console.error("Failed to load profile modal:", err);
            modalRoot.innerHTML = `
              <div class="modal-overlay">
                <div class="modal-box">Failed to load profile.</div>
              </div>
            `;
        });
}

function closeProfileModal() {
    const modalRoot = document.getElementById("modal-root");
    modalRoot.innerHTML = "";
}

function openAddUserModal(targetRole = null) {
	const modalRoot = document.getElementById("modal-root");


	   document.body.setAttribute("data-target-role", targetRole != null ? targetRole : '');

	    // Build URL conditionally
	    const baseUrl = `${window.appContext}/includes/dashboard-addUser.jsp`;
	    const fetchUrl = (targetRole != null)
	        ? `${baseUrl}?targetRole=${targetRole}`
	        : baseUrl;

	    fetch(fetchUrl)
        .then(response => {
            if (response.status === 401) {
                return response.json().then(data => {
                    console.warn("Session expired. Redirecting to login.");
                    if (typeof handleResponse === "function") handleResponse(data);
                    setTimeout(() => {
                        window.location.href = `${window.appContext}/login.jsp`;
                    }, 1000);
                    throw new Error("Unauthorized - session expired");
                });
            }

            if (!response.ok) throw new Error("Failed to load Add User modal. HTTP " + response.status);
            return response.text();
        })
        .then(html => {
            modalRoot.innerHTML = html;
            document.body.style.overflow = "hidden";
            loadScriptOnce("addUserScriptLoaded", "/js/dashboard-addUser.js", "initAddUserScript");
        })
        .catch(err => {
            console.error("Failed to load add user modal:", err);
            modalRoot.innerHTML = `
              <div class="user-modal-overlay">
                <div class="user-modal-box">Failed to load add User.</div>
              </div>
            `;
        });
}


function closeAddUserModal() {
    const modalRoot = document.getElementById("modal-root");
    modalRoot.innerHTML = "";
}


