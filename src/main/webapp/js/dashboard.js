function handleFetchResponse(response) {
  if (response.status === 401) {
    return response.json().then(data => {
      handleResponse(data, `${window.appContext}/login.jsp`);
      return Promise.reject("handled"); // Stop further .then() chains
    });
  }

  if (!response.ok) {
    return response.json().then(data => {
      handleResponse(data);
      return Promise.reject("handled");
    });
  }

  return response.json();
}

function handlePageFetchResponse(response) {
  if (response.status === 401) {
    return response.json().then(data => {
      handleResponse(data, `${window.appContext}/login.jsp`);
      return Promise.reject("handled");
    });
  }

  if (!response.ok) {
    // Try JSON first, if it fails, fall back to plain error
    return response.text().then(errorText => {
      try {
        const data = JSON.parse(errorText);
        handleResponse(data);
      } catch(e) {
        handleResponse({ error: "Failed to load page content." });
      }
      return Promise.reject("handled");
    });
  }

  return response.text(); // ✅ HTML page content
}


function handleLogout() {
  document.getElementById("logoutConfirmModal").classList.remove("hidden");
}

function closeLogoutModal() {
  document.getElementById("logoutConfirmModal").classList.add("hidden");
}

function confirmLogout() {
  fetch(`${window.appContext}/MRNBank/logout`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
	  "Method": "POST"
    }
  })
  .then(handleFetchResponse)
  .then(data => {
      window.location.href = `${window.appContext}/logout.jsp`;
  })
  .catch((err) => {
    if (err !== "handled") {  // Avoid duplicate error toast if already shown by handleFetchResponse
      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
    }
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
	
    fetch("includes/" + page)
	.then(handlePageFetchResponse)
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
	.catch((err) => {
	  if (err !== "handled") {
	    handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	  }
	});
}

function loadScriptOnce(scriptId, scriptPath, initFunctionName) {
    if (!window[scriptId]) {
        const script = document.createElement("script");
        script.src = `${window.appContext}${scriptPath}`;
        script.onload = () => {
            window[scriptId] = true;
            window[initFunctionName]();
        };
        document.body.appendChild(script);
    } else {
        window[initFunctionName]();
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
        "accountsCss", "clientAccountsCss", "statementCss", "mainCss",
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
    .then(handlePageFetchResponse)
    .then(html => {
        modalRoot.innerHTML = html;
        document.body.style.overflow = "hidden";
        loadScriptOnce("profileScriptLoaded", `/js/dashboard-profile.js`, "initProfileScript");
    })
	.catch((err) => {
	  if (err !== "handled") {
	    handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	  }
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
    .then(handlePageFetchResponse)
    .then(html => {
        modalRoot.innerHTML = html;
        document.body.style.overflow = "hidden";
        loadScriptOnce("addUserScriptLoaded", "/js/dashboard-addUser.js", "initAddUserScript");
    })
	.catch((err) => {
	  if (err !== "handled") {
	    handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	  }
	});
}

function closeAddUserModal() {
    const modalRoot = document.getElementById("modal-root");
    modalRoot.innerHTML = "";
}


