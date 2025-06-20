function initStatementScript() {
    const form = document.getElementById("statementForm");
    const resultBox = document.getElementById("statementResult");
    const accountSelect = document.getElementById("accountSelect");
    const userId = document.body.getAttribute("data-user-id");
	

    // Load accounts for dropdown
    fetch("/MRN_BANKING/MRNBank/accounts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Method": "GET"
        },
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

    form.addEventListener("submit", e => {
        e.preventDefault();
        const fromDate = form.fromDate.value;
        const toDate = form.toDate.value;
        const selectedAcc = accountSelect.value;

        const requestBody = {};
        if (selectedAcc) requestBody.accountNo = selectedAcc;
        else requestBody.clientId = parseInt(userId);

        if (fromDate) requestBody.fromDate = fromDate;
        if (toDate) requestBody.toDate = toDate;

        fetch("/MRN_BANKING/MRNBank/accountstatement", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "GET"
            },
            body: JSON.stringify(requestBody)
        })
        .then(res => res.json())
        .then(data => {
			handleResponse(data);
			displayStatement(data);
		})
        .catch(err => {
            console.error("Error:", err);
            resultBox.innerHTML = `<p style="color:red;">Failed to load statement.</p>`;
        });
    });
}

function displayStatement(data) {
    const container = document.getElementById("statementResult");
    const transactions = data.Transactions || [];

    if (transactions.length === 0) {
        container.innerHTML = "<p>No transactions found.</p>";
        return;
    }

    const txnTypeMap = ["DEPOSIT", "WITHDRAWAL", "CREDIT", "DEBIT"];
    const txnStatusMap = ["FAILED", "SUCCESS", "PENDING"];

    const rows = transactions.map(txn => `
        <tr>
            <td>${txn.txnRefNo}</td>
            <td>${new Date(txn.txnTime * 1000).toLocaleString()}</td>
            <td>${txnTypeMap[txn.txnType]}</td>
            <td>${txn.peerAccNo}</td>
            <td>${txn.amount}</td>
            <td>${txn.closingBalance}</td>
            <td>${txnStatusMap[txn.txnStatus]}</td>
        </tr>
    `).join("");

    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>Txn Ref</th>
                    <th>Time</th>
                    <th>Type</th>
                    <th>Peer Acc No</th>
                    <th>Amount</th>
                    <th>Closing Balance</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                ${rows}
            </tbody>
        </table>
    `;
}

