function initTransactionScript() {
    const userId = document.body.getAttribute("data-user-id");
    const form = document.getElementById("transferForm");
    const fromAccount = document.getElementById("fromAccount");
    const resultBox = document.getElementById("transferResult");

    // Load user's accounts into the fromAccount dropdown
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
                fromAccount.appendChild(option);
            });
        }
    })
    .catch(err => {
        console.error("Error loading accounts:", err);
    });

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const body = {
            accountNo: parseInt(form.accountNo.value),
            peerAccNo: parseInt(form.peerAccNo.value),
            amount: parseFloat(form.amount.value),
            txnType: 3, // DEBIT (transfer)
            description: form.description.value || ""
        };

        fetch("/MRN_BANKING/MRNBank/transaction", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
				"Method": "POST"
            },
            body: JSON.stringify(body)
        })
        .then(res => res.json())
        .then(data => {
            console.log("Transaction response:", data);
            resultBox.textContent = data.message || "Transfer complete.";
            resultBox.className = "transfer-result success";
            form.reset();
        })
        .catch(err => {
            console.error("Transaction error:", err);
            resultBox.textContent = "Transfer failed. Please try again.";
            resultBox.className = "transfer-result error";
        });
    });
}

document.addEventListener("DOMContentLoaded", initTransactionScript);
