function initStatementScript() {
    const form = document.getElementById("statementForm");
    const resultBox = document.getElementById("statementResult");
    const accountSelect = document.getElementById("accountSelect");
    const userId = document.body.getAttribute("data-user-id");
    const userRole = parseInt(document.body.getAttribute("data-user-role"));

    const prevBtn = document.getElementById("prevPage");
    const nextBtn = document.getElementById("nextPage");
    const paginationWrapper = document.getElementById("paginationWrapper");
    paginationWrapper.style.display = "none";

    let currentPage = 1;
    const limit = 5;

    if (userRole === 1 || userRole === 2 || userRole === 3) {
        const wrapper = document.createElement("div");
        wrapper.className = "form-group";
        wrapper.style.display = "flex";
        wrapper.style.gap = "10px";

        const accInput = document.createElement("input");
        accInput.type = "text";
        accInput.id = "accountInput";
        accInput.name = "accountNo";
        accInput.placeholder = "Account No (11 digits)";
        accInput.maxLength = 11;
        accInput.className = "form-control";
        accInput.style.flex = "1";

        // Only digits and max 11
        accInput.addEventListener("input", () => {
            accInput.value = accInput.value.replace(/\D/g, "").slice(0, 11);
        });
        accInput.addEventListener("keydown", (e) => {
            if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault();
        });

        wrapper.appendChild(accInput);

        if (userRole === 3) {
            const clientInput = document.createElement("input");
            clientInput.type = "text";
            clientInput.id = "clientInput";
            clientInput.name = "clientId";
            clientInput.placeholder = "Client ID";
            clientInput.className = "form-control";
            clientInput.style.flex = "1";

            clientInput.addEventListener("keydown", (e) => {
                if (["e", "E", "+", "-"].includes(e.key)) e.preventDefault();
            });

            wrapper.appendChild(clientInput);
        }

        accountSelect.replaceWith(wrapper);
    } else {
        // Client role (dropdown only)
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
            const accInput = document.getElementById("accountInput");
            const accVal = accInput.value.trim();
            if (!accVal) {
                handleResponse({ error: "Please enter an account number." });
                return;
            }
            requestBody.accountNo = accVal;

        } else if (userRole === 3) {
            // GM: can use either, not both
            const accInput = document.getElementById("accountInput");
            const clientInput = document.getElementById("clientInput");
            const accVal = accInput.value.trim();
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
            if (!selectedAcc) {
                handleResponse({ error: "Please select an account number." });
                return;
            }
            requestBody.accountNo = selectedAcc;
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
            

            if (data.Transactions && data.Transactions.length > 0) {
				handleResponse(data);
                displayStatement(data.Transactions);
                paginationWrapper.style.display = "flex";
                prevBtn.disabled = currentPage === 1;
                nextBtn.disabled = data.Transactions.length < limit;
            } else {
                handleResponse(data);
                resultBox.innerHTML = "";
                paginationWrapper.style.display = "none";
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
        <tr>
            <td>${txn.txnRefNo}</td>
            <td>${new Date(txn.txnTime * 1000).toLocaleString()}</td>
            <td>${txnTypeMap[txn.txnType]}</td>
            <td>${txn.peerAccNo || "-"}</td>
            <td>${txn.amount}</td>
            <td>${txn.closingBalance}</td>
            <td class="description">${txn.description || "-"}</td>
        </tr>
    `).join("");

    container.innerHTML = `
        <table class="statement-table">
            <thead>
                <tr>
                    <th>Txn Ref</th>
                    <th>Time</th>
                    <th>Type</th>
                    <th>Peer Acc No</th>
                    <th>Amount</th>
                    <th>Closing Balance</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                ${rows}
            </tbody>
        </table>
    `;
}

