function initClientAccountsScript() {
    const userId = document.body.getAttribute("data-user-id");
    const cardsContainer = document.getElementById("accountCardsContainer");
    const accountSelect = document.getElementById("accountSelect");
    const accountInfo = document.getElementById("accountInfo");

    if (!userId) {
        console.warn("No userId found.");
        return;
    }

    // Load accounts
    fetch("/MRN_BANKING/MRNBank/accounts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Method": "GET"
        },
        body: JSON.stringify({ clientId: userId })
    })
    .then(res => res.json())
    .then(data => {
        const accounts = data.Accounts || [];
        accounts.forEach(account => {
			// Card
			const card = document.createElement("div");
			card.className = "account-card";
			card.dataset.accountNo = account.accountNo;
			card.style.cursor = "pointer";
			card.innerHTML = `
			  <h4>Account No: ${account.accountNo}</h4>
			  <div class="account-type">Type: ${getAccountTypeName(account.accountType)}</div>
			  <div class="status ${account.status === 1 ? '' : 'inactive'}">${account.status === 1 ? 'Active' : 'Inactive'}</div>
			  <div class="account-balance">₹${account.balance.toFixed(2)}</div>
			`;
			card.addEventListener("click", () => {
			  accountSelect.value = account.accountNo;
			  accountSelect.dispatchEvent(new Event("change"));
			});
			cardsContainer.appendChild(card);

            // Dropdown
            const option = document.createElement("option");
            option.value = account.accountNo;
            option.textContent = account.accountNo;
            accountSelect.appendChild(option);
        });
    })
    .catch(err => {
        console.error("Failed to fetch accounts:", err);
        handleResponse({ error: "Unable to fetch account list." });
    });

    accountSelect.addEventListener("change", () => {
        const selectedAccountNo = accountSelect.value;
        accountInfo.innerHTML = "";

        if (!selectedAccountNo) {
		    accountInfo.innerHTML = `<p>Please select an account to view details.</p>`;
		    return;
		  }

        fetch("/MRN_BANKING/MRNBank/accounts", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "GET"
            },
            body: JSON.stringify({ accountNo: parseInt(selectedAccountNo) })
        })
        .then(res => res.json())
        .then(data => {
            const acc = data.Account.account;
            const branchName = data.Account.branchName;
            const ifscCode = data.Account.ifscCode;

			accountInfo.innerHTML = `
			    <h3>Account Details</h3>
			    <div class="detail-row"><span>Account No:</span><strong>${acc.accountNo}</strong></div>
			    <div class="detail-row"><span>Branch:</span><strong>${branchName}</strong></div>
			    <div class="detail-row"><span>IFSC Code:</span><strong>${ifscCode}</strong></div>
			    <div class="detail-row"><span>Type:</span><strong>${getAccountTypeName(acc.accountType)}</strong></div>
			    <div class="detail-row"><span>Status:</span><strong>${acc.status === 1 ? "Active" : "Inactive"}</strong></div>
			    <div class="detail-row"><span>Balance:</span><strong>₹${acc.balance.toFixed(2)}</strong></div>
			    <div class="detail-row"><span>Created Time:</span><strong>${new Date(acc.createdTime * 1000).toLocaleString()}</strong></div>
			`;
        })
        .catch(err => {
            console.error("Failed to fetch account details:", err);
            handleResponse({ error: "Unable to fetch account details." });
        });
    });

    initAccountRequestFeature();
}

function getAccountTypeName(typeId) {
    switch (typeId) {
        case 1: return "Savings";
        case 2: return "Current";
        case 3: return "Fixed Deposit";
        default: return "Unknown";
    }
}

function initAccountRequestFeature() {
    const openModalBtn = document.getElementById("openAccountRequestModal");
    const modal = document.getElementById("accountRequestModal");
    const closeModalBtn = document.getElementById("closeModal");
    const branchSelect = document.getElementById("branchSelect");
    const requestForm = document.getElementById("accountRequestForm");

    openModalBtn.addEventListener("click", () => {
        modal.style.display = "flex";
        fetchBranches();
    });

    closeModalBtn.addEventListener("click", () => {
        modal.style.display = "none";
        requestForm.reset();
    });

    function fetchBranches() {
        fetch("/MRN_BANKING/MRNBank/branch", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Method": "GET"
            }
        })
        .then(res => res.json())
        .then(data => {
            branchSelect.innerHTML = '<option value="">-- Select Branch --</option>';
            data.Branches.forEach(branch => {
                const option = document.createElement("option");
                option.value = branch.branchId;
                option.textContent = `${branch.branchName} (${branch.branchLocation})`;
                branchSelect.appendChild(option);
            });
        })
        .catch(err => {
            console.error("Failed to load branches:", err);
        });
    }

    requestForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const branchId = parseInt(branchSelect.value);
        const accountType = parseInt(document.getElementById("accountTypeSelect").value);

        if (!branchId || !accountType) {
             handleResponse({error:"Please select both branch and account type."});
            return;
        }

        fetch("/MRN_BANKING/MRNBank/accountrequest", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "POST"
            },
            body: JSON.stringify({ branchId, accountType })
        })
        .then(res => res.json())
        .then(data => {
             handleResponse(data);
            modal.style.display = "none";
            requestForm.reset();
        })
        .catch(err => {
            console.error("Request failed:", err);
        });
    });
}
