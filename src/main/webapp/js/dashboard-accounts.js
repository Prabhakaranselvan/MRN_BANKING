function initAccountsScript() {
  const container = document.getElementById("accountsList");
  const prevBtn = document.getElementById("prevPage");
  const nextBtn = document.getElementById("nextPage");
  const paginationWrapper = document.getElementById("paginationWrapper");
  paginationWrapper.style.display = "none";
  const addBtn = document.getElementById("addAccountBtn");

  const typeFilter = document.getElementById("accountTypeFilter");
  const branchFilter = document.getElementById("branchFilter");

  const addModal = document.getElementById("addAccountModal");
  const updateModal = document.getElementById("updateAccountModal");
  const closeAddModal = document.getElementById("closeAddModal");
  const closeUpdateModal = document.getElementById("closeUpdateModal");
  const addForm = document.getElementById("addAccountForm");
  const updateForm = document.getElementById("updateAccountForm");

  const userRole = parseInt(document.body.dataset.userRole);
  const userBranchId = parseInt(document.body.dataset.branchId);
  const isGM = userRole === 3;

  let currentPage = 1;
  const limit = 10;

  if (isGM) {
    loadBranches();
  } else {
    branchFilter.disabled = true;
    document.querySelectorAll("select.branch-input").forEach(select => {
      select.style.pointerEvents = "none";
      select.style.backgroundColor = "#f4f4f4";
      select.style.color = "#777";
    });
    setFixedBranch(userBranchId);
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
      .then(handleFetchResponse)
	  .then(data => {
        const label = `${data.branch.branchName} (${data.branch.branchLocation})`;
        const value = String(data.branch.branchId);
        const option = new Option(label, value);

        branchFilter.innerHTML = "";
        branchFilter.appendChild(option.cloneNode(true));
        branchFilter.value = value;

        document.querySelectorAll("select.branch-input").forEach((select, i) => {
          select.innerHTML = "";
          const opt = option.cloneNode(true);
          opt.selected = true;
          select.appendChild(opt);
          select.value = value;
        });
      })
	  .catch((err) => {
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
	  });
  }

  function loadBranches() {
    fetch("/MRN_BANKING/MRNBank/branch", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Method: "GET"
      }
    })
      .then(handleFetchResponse)
      .then(data => {
        branchFilter.innerHTML = '<option value="">All Branches</option>';
        document.querySelectorAll("select.branch-input").forEach(select => {
          select.innerHTML = '<option value="">-- Select Branch --</option>';
        });

        data.Branches.forEach(branch => {
          const label = `${branch.branchName} (${branch.branchLocation})`;
          const opt = new Option(label, branch.branchId);

          branchFilter.appendChild(opt.cloneNode(true));
          document.querySelectorAll("select.branch-input").forEach(select => {
            select.appendChild(opt.cloneNode(true));
          });
        });
      })
	  .catch(err => {
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
	  });
  }

  function loadAccounts(page) {
    const params = new URLSearchParams({ page, limit });
    if (typeFilter.value) params.append("type", typeFilter.value);
    if (branchFilter.value) params.append("branchId", branchFilter.value);

    fetch(`/MRN_BANKING/MRNBank/accounts?${params}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Method: "GET"
      }
    })
      .then(handleFetchResponse)
      .then(data => {
		if (data.Accounts && data.Accounts.length > 0) {
          renderAccounts(data.Accounts);
		  paginationWrapper.style.display = "flex";
          prevBtn.disabled = currentPage === 1;
          nextBtn.disabled = data.Accounts.length < limit;
        } else {
          container.innerHTML = "<p>No accounts found.</p>";
		  if (currentPage === 1) {
	            paginationWrapper.style.display = "none";
	        } else {
	            paginationWrapper.style.display = "flex";
	            prevBtn.disabled = false;
	            nextBtn.disabled = true;
	        }
        }
      })
      .catch(err => {
		if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
      });
  }

  function renderAccounts(accounts) {
    const rows = accounts.map(a => {
      const statusText = getStatusText(a.status);
      const statusClass = getStatusClass(a.status);

      const actions = a.status !== 2
        ? `<button onclick='editAccount(${JSON.stringify(a)})' class="action-btn" title="Edit Account">
             <span class="material-icons">edit_square</span>
           </button>`
        : `<button class="lock-btn" title="Locked">
   	          <span class="material-icons" style="color: #bbb;">lock</span>
   	        </button >`;

      return `
        <div class="account-row">
          <div class="account-no">${a.accountNo}</div>
          <div class="client-id">${a.clientId}</div>
          <div class="account-type">${getAccountTypeText(a.accountType)}</div>
          <div class="acc-balance">₹${a.balance}</div>
          <div class="account-status ${statusClass}">${statusText}</div>
          <div class="account-actions">${actions}</div>
        </div>
      `;
    }).join("");

    container.innerHTML = `
      <div class="account-table">
        <div class="account-header">
          <div class="account-no">ACCOUNT NO</div>
          <div class="client-id">CLIENT ID</div>
          <div class="account-type">TYPE</div>
          <div class="acc-balance">BALANCE</div>
          <div class="account-status">STATUS</div>
          <div class="account-actions">ACTION</div>
        </div>
        ${rows}
      </div>
    `;
  }


  function getAccountTypeText(type) {
    const t = Number(type);
    return t === 1 ? "SAVINGS" : t === 2 ? "CURRENT" : "FIXED_DEPOSIT";
  }

  function getStatusText(status) {
    return status === 1 ? "Active" : status === 0 ? "Inactive" : "Closed";
  }

  function getStatusClass(status) {
    return status === 1 ? "active" : status === 0 ? "inactive" : "closed";
  }

  window.editAccount = function (account) {
    updateForm.accountNo.value = account.accountNo;
    updateForm.accountType.value = account.accountType;
    updateForm.status.value = account.status;
    updateModal.style.display = "flex";
  };

  addBtn.addEventListener("click", () => {
    addForm.reset();
    addModal.style.display = "flex";
  });

  closeAddModal.addEventListener("click", () => addModal.style.display = "none");
  closeUpdateModal.addEventListener("click", () => updateModal.style.display = "none");

  addForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = new FormData(addForm);
    const json = Object.fromEntries(formData.entries());
	
	const balanceVal = formData.get("balance").trim();
   	const balanceNum = parseFloat(balanceVal);
  	if (isNaN(balanceNum) || balanceNum < 1 || balanceNum > 100000) {
  	  return handleResponse({ error: "Please enter a valid Opening Balance between ₹1 and ₹1,00,000 (one lakh), with up to 2 decimal places" });
  	}

    json.balance = balanceNum;
    json.clientId = parseInt(json.clientId);
    json.accountType = parseInt(json.accountType);
    json.branchId = parseInt(json.branchId);

    fetch("/MRN_BANKING/MRNBank/accounts", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Method: "POST"
      },
      body: JSON.stringify(json)
    })
      .then(handleFetchResponse)
      .then(data => {
        handleResponse(data);
        addModal.style.display = "none";
        loadAccounts(currentPage);
      })
	  .catch(err => {
  		if (err !== "handled") {
  	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
  	    }
      });
  });

  updateForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const formData = new FormData(updateForm);
    const json = Object.fromEntries(formData.entries());
    json.accountNo = parseInt(json.accountNo);
    json.accountType = parseInt(json.accountType);
    json.status = parseInt(json.status);

    fetch("/MRN_BANKING/MRNBank/accounts", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Method: "PUT"
      },
      body: JSON.stringify(json)
    })
    .then(handleFetchResponse)
    .then(data => {
      handleResponse(data);
      updateModal.style.display = "none";
   	  updateForm.reset(); // This clears all inputs including password
      loadAccounts(currentPage);
    })
    .catch(err => {
	  if (err !== "handled") {
        handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
      }
    });
  });

  typeFilter.addEventListener("change", () => {
    currentPage = 1;
    loadAccounts(currentPage);
  });

  branchFilter.addEventListener("change", () => {
    currentPage = 1;
    loadAccounts(currentPage);
  });


  prevBtn.addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      loadAccounts(currentPage);
    }
  });

  nextBtn.addEventListener("click", () => {
    currentPage++;
    loadAccounts(currentPage);
  });

  loadAccounts(currentPage);
  
  // ===== PASSWORD TOGGLE LOGIC =====
   const passwordInput = document.getElementById("updatePassword");
   const togglePassword = document.getElementById("togglePassword");

   if (passwordInput && togglePassword) {
     togglePassword.addEventListener("click", () => {
       const isHidden = passwordInput.type === "password";
       passwordInput.type = isHidden ? "text" : "password";
       togglePassword.textContent = isHidden ? "visibility_off" : "visibility";
       togglePassword.title = isHidden ? "Hide Password" : "Show Password";
     });
   }
}
