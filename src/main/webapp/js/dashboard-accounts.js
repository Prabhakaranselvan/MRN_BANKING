function initAccountScript() {
    const userId = document.body.getAttribute("data-user-id");
    const tableBody = document.querySelector("#account-summary-table tbody");
    const accountSelect = document.getElementById("accountSelect");
    const accountInfo = document.getElementById("accountInfo");

    if (!userId) {
        console.warn("No userId found.");
        return;
    }

    // Fetch all accounts for client
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
        console.log("Account list:", data);
        const accounts = data.Accounts || [];

        accounts.forEach(account => {
            // Add to table
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${account.accountNo}</td>
                <td>${account.branchId}</td>
                <td>${getAccountTypeName(account.accountType)}</td>
                <td>${account.status === 1 ? "Active" : "Inactive"}</td>
            `;
            tableBody.appendChild(row);

            // Add to dropdown
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

    // Handle account detail fetch
    accountSelect.addEventListener("change", () => {
        const selectedAccountNo = accountSelect.value;
        accountInfo.innerHTML = "";

        if (!selectedAccountNo) return;

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
            console.log("Account detail:", data);
            const acc = data.Account.account;
            const branchName = data.Account.branchName;
            const ifscCode = data.Account.ifscCode;

            accountInfo.innerHTML = `
                <h3>Account Details</h3>
                <p><strong>Account No:</strong> ${acc.accountNo}</p>
                <p><strong>Branch:</strong> ${branchName}</p>
                <p><strong>IFSC Code:</strong> ${ifscCode}</p>
                <p><strong>Type:</strong> ${getAccountTypeName(acc.accountType)}</p>
                <p><strong>Status:</strong> ${acc.status === 1 ? "Active" : "Inactive"}</p>
                <p><strong>Balance:</strong> ₹${acc.balance.toFixed(2)}</p>
                <p><strong>Created Time:</strong> ${new Date(acc.createdTime * 1000).toLocaleString()}</p>
            `;
        })
        .catch(err => {
            console.error("Failed to fetch account details:", err);
            handleResponse({ error: "Unable to fetch account details." });
        });
    });
}

function getAccountTypeName(typeId) {
    switch (typeId) {
        case 1: return "Savings";
        case 2: return "Current";
        case 3: return "Fixed Deposit";
        default: return "Unknown";
    }
}
