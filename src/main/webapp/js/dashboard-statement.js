// dashboard-statement.js

function initStatementScript() {
    const form = document.getElementById("statementForm");
    const resultBox = document.getElementById("statementResult");
    const userId = document.body.getAttribute("data-user-id");

    if (!form || !resultBox) return;

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const formData = new FormData(form);
        const accountNo = formData.get("accountNo").trim();
        const fromDate = formData.get("fromDate");
        const toDate = formData.get("toDate");

        let body = {};
        if (accountNo) body.accountNo = accountNo;
        else body.clientId = parseInt(userId);

        if (fromDate) body.fromDate = fromDate;
        if (toDate) body.toDate = toDate;

        fetch("/MRN_BANKING/MRNBank/accountstatement", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Method": "GET"
            },
            body: JSON.stringify(body)
        })
            .then(res => res.json())
            .then(data => displayStatement(data))
            .catch(err => {
                console.error("Error fetching statement:", err);
                resultBox.innerHTML = `<p style="color:red;">Failed to load statement.</p>`;
            });
    });
}

function displayStatement(data) {
    const container = document.getElementById("statementResult");

    if (!data || !data.transactions || data.transactions.length === 0) {
        container.innerHTML = "<p>No transactions found.</p>";
        return;
    }

    const rows = data.transactions.map(t => `
        <tr>
            <td>${t.date}</td>
            <td>${t.accountNo}</td>
            <td>${t.type}</td>
            <td>${t.amount}</td>
            <td>${t.description}</td>
            <td>${t.balance}</td>
        </tr>
    `).join("");

    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Account No</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Description</th>
                    <th>Balance</th>
                </tr>
            </thead>
            <tbody>
                ${rows}
            </tbody>
        </table>
    `;
}

document.addEventListener("DOMContentLoaded", initStatementScript);
