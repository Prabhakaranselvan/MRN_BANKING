function initStatementScript() {
    const form = document.getElementById("statementForm");
    const resultBox = document.getElementById("statementResult");
    const accountSelect = document.getElementById("accountSelect");
	
	const accountInput = document.getElementById("accountInput");
	const clientInput = document.getElementById("clientInput");

	const accountDropdownGroup = document.getElementById("accountDropdownGroup");
	const accountInputGroup = document.getElementById("accountInputGroup");
	const clientInputGroup = document.getElementById("clientInputGroup");

    const userId = document.body.getAttribute("data-user-id");
    const userRole = parseInt(document.body.getAttribute("data-user-role"));

    const prevBtn = document.getElementById("prevPage");
    const nextBtn = document.getElementById("nextPage");
    const paginationWrapper = document.getElementById("paginationWrapper");
    paginationWrapper.style.display = "none";

    let currentPage = 1;
    const limit = 10;

    if (userRole === 1 || userRole === 2 || userRole === 3) {
		accountDropdownGroup.style.display = "none";
		accountInputGroup.style.display = "flex";
		accountInput.style.display = "block";

        // Only digits and max 11
		accountInput.addEventListener("input", () => {
           accountInput.value = accountInput.value.replace(/\D/g, "").slice(0, 11);
       });
       accountInput.addEventListener("keydown", (e) => {
           if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault();
       });

        if (userRole === 3) {
			clientInputGroup.style.display = "flex";
			clientInput.style.display = "block";
			
			clientInput.addEventListener("input", () => {
	           clientInput.value = clientInput.value.replace(/\D/g, "").slice(0, 6);
	       });
            clientInput.addEventListener("keydown", (e) => {
                if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault();
            });

        }
    } else {
		// Client role – use dropdown
		accountDropdownGroup.style.display = "";
		accountInputGroup.style.display = "none";
		clientInputGroup.style.display = "none";
				
        fetch("/MRN_BANKING/MRNBank/accounts", {
            method: "POST",
            headers: { "Content-Type": "application/json"			,
							"Method": "GET" },
            body: JSON.stringify({ clientId: parseInt(userId) })
        })
        .then(res => res.json())
        .then(data => {
            if (Array.isArray(data.Accounts)) {
                data.Accounts.forEach(acc => {
                    const option = document.createElement("option");
                    option.value = acc.accountNo;
                    option.textContent = acc.accountNo;
                    accountSelect.appendChild(option);
                });
            }
        });
    }

    form.addEventListener("submit", e => {
        e.preventDefault();
		    const fromDate = form.fromDate.value;
		    const toDate = form.toDate.value;
			
			// If only one date is filled
			    if ((fromDate && !toDate) || (!fromDate && toDate)) {
			        handleResponse({ error: "Please provide both From Date and To Date." });
			        return;
			    }

		    // Validate: To Date must be greater than or equal to From Date
		    if (fromDate && toDate && new Date(toDate) < new Date(fromDate)) {
				handleResponse({ error: "To Date must be greater than or equal to From Date." });
		        return;
		    }
        currentPage = 1;
        fetchStatement();
    });

    prevBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            fetchStatement();
        }
    });

    nextBtn.addEventListener("click", () => {
        currentPage++;
        fetchStatement();
    });

    function fetchStatement() {
        const fromDate = form.fromDate.value;
        const toDate = form.toDate.value;

        const requestBody = {
            page: currentPage,
            limit: limit
        };

        // Roles 1 & 2: must use only account number
        if (userRole === 1 || userRole === 2) {
            const accVal = accountInput.value.trim();
            if (!accVal) {
                handleResponse({ error: "Please enter an account number." });
                return;
            }
            requestBody.accountNo = accVal;

        } else if (userRole === 3) {
            // GM: can use either, not both
			const accVal = accountInput.value.trim();
           const clientVal = clientInput.value.trim();

            if (accVal && clientVal) {
                handleResponse({ error: "Please enter either Account No or Client ID, not both." });
                return;
            } else if (accVal) {
                requestBody.accountNo = accVal;
            } else if (clientVal) {
				requestBody.clientId = clientVal;
            } else {
                handleResponse({ error: "Please enter either Account No or Client ID." });
                return;
            }

        } else {
            // Client (dropdown)
            const selectedAcc = accountSelect.value;
			if (selectedAcc) {
                requestBody.accountNo = selectedAcc;
            } else {
                requestBody.clientId = userId;
            }
        }

        if (fromDate) requestBody.fromDate = fromDate;
        if (toDate) requestBody.toDate = toDate;

        fetch("/MRN_BANKING/MRNBank/accountstatement", {
            method: "POST",
            headers: { "Content-Type": "application/json",
				"Method": "GET"
			 },
            body: JSON.stringify(requestBody)
        })
        .then(res => res.json())
		.then(data => {
		    handleResponse(data);
		    if (data.Transactions && data.Transactions.length > 0) {
		        displayStatement(data.Transactions);
		        paginationWrapper.style.display = "flex";
		        prevBtn.disabled = currentPage === 1;
		        nextBtn.disabled = data.Transactions.length < limit;
		    } else {
		        resultBox.innerHTML = "No Records Found";
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
            console.error("Error:", err);
            handleResponse({ error: "Failed to load statement. Please try again." });
        });
    }
}

function displayStatement(transactions) {
  const container = document.getElementById("statementResult");

  const txnTypeMap = ["DEPOSIT", "WITHDRAWAL", "CREDIT", "DEBIT"];

  const rows = transactions.map(txn => `
    <div class="statement-row">
      <div class="statement-ref">#${txn.txnRefNo}</div>
      <div class="statement-time">${new Date(txn.txnTime * 1000).toLocaleString()}</div>
      <div class="statement-type">${txnTypeMap[txn.txnType]}</div>
      <div class="statement-peer">${txn.peerAccNo || "-"}</div>
      <div class="statement-amount">${txn.amount}</div>
      <div class="statement-balance">${txn.closingBalance}</div>
      <div class="statement-desc">${txn.description || "-"}</div>
    </div>
  `).join("");

  container.innerHTML = `
    <div class="statement-table">
      <div class="statement-header">
        <div class="statement-ref">Txn Ref</div>
        <div class="statement-time">Time</div>
        <div class="statement-type">Type</div>
        <div class="statement-peer">Peer Acc No</div>
        <div class="statement-amount">Amount</div>
        <div class="statement-balance">Closing Balance</div>
        <div class="statement-desc">Description</div>
      </div>
      ${rows}
    </div>
  `;
}


