function initTransactionScript() {
    const userId = document.body.getAttribute("data-user-id");
    const form = document.getElementById("transferForm");
    const fromAccount = document.getElementById("fromAccount");
    const resultBox = document.getElementById("transferResult");
	
	// Input field restrictions
    const peerAccNoInput = document.getElementById("peerAccNo");
    const amountInput = document.getElementById("amount");
    const descriptionInput = document.getElementById("description");

    // Allow only digits in receiver account number
    peerAccNoInput.addEventListener("input", function () {
        this.value = this.value.replace(/\D/g, ''); // Remove non-digits
    });

	// Allow only valid amount (digits and max 2 decimal places), and max 100000
	    amountInput.addEventListener("input", function () {
	        let value = this.value
	            .replace(/[^0-9.]/g, '')           // Remove non-digit/non-dot
	            .replace(/^(\d*\.\d{0,2}).*$/, '$1'); // Limit to 2 decimals

	        const parts = value.split('.');
	        if (parts.length > 2) {
	            value = parts[0] + '.' + parts[1]; // Remove extra dots
	        }

	        const floatVal = parseFloat(value);
	        if (!isNaN(floatVal) && floatVal > 100000) {
	            value = '100000';
	        }

	        this.value = value;
	    });

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
            handleResponse(data);
            form.reset();
        })
        .catch(err => {
            console.error("Transaction error:", err);
            resultBox.textContent = "Transfer failed. Please try again.";
            resultBox.className = "transfer-result error";
        });
    });
}
