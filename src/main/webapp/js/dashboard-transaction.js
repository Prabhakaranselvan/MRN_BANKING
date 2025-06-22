function initTransactionScript() {
	const userId = document.body.getAttribute("data-user-id");
	const userRole = parseInt(document.body.getAttribute("data-user-role"));
	const form = document.getElementById("transferForm");

	const txnType = document.getElementById("txnType");
	const fromAccount = document.getElementById("fromAccount");
	const peerGroup = document.getElementById("peerGroup");
	const peerAccNo = document.getElementById("peerAccNo");
	const amountInput = document.getElementById("amount");
	const passwordInput = document.getElementById("txnPassword");
	const togglePasswordBtn = document.getElementById("togglePassword");
	const fromAccountGroup = document.getElementById("fromAccountGroup");

	// Transaction type options for privileged users
	if ([1, 2, 3].includes(userRole)) {
		txnType.innerHTML = `
			<option value="0">Deposit</option>
			<option value="1">Withdraw</option>
			<option value="3">Transfer</option>
		`;
	}

	// Peer account visibility for Transfer only
	txnType.addEventListener("change", () => {
		const isTransfer = txnType.value === "3";
		peerGroup.classList.toggle("show", isTransfer);
		peerAccNo.required = isTransfer;
	});

	// Peer Account: only numbers, max 15 digits
	peerAccNo.addEventListener("input", () => {
		peerAccNo.value = peerAccNo.value.replace(/\D/g, '').slice(0, 15);
	});

	// Amount: restrict to 2 decimals, max 100000
	amountInput.addEventListener("input", () => {
		let value = amountInput.value
			.replace(/[^0-9.]/g, '')
			.replace(/^(\d*\.\d{0,2}).*$/, '$1');

		const floatVal = parseFloat(value);
		if (!isNaN(floatVal) && floatVal > 100000) value = '100000';
		amountInput.value = value;
	});

	// Password visibility toggle
	togglePasswordBtn.addEventListener("click", () => {
		const isVisible = passwordInput.type === "text";
		passwordInput.type = isVisible ? "password" : "text";
		togglePasswordBtn.textContent = isVisible ? "visibility" : "visibility_off";
	});

	// Account input behavior: client gets dropdown, others type manually
	if (userRole === 0) {
		// Convert to dropdown (already a text input)
		const dropdown = document.createElement("select");
		dropdown.name = "accountNo";
		dropdown.id = "fromAccount";
		dropdown.required = true;
		dropdown.innerHTML = `<option value="">Select Account</option>`;
		fromAccountGroup.innerHTML = `
			<label for="fromAccount">From Account</label>
		`;
		fromAccountGroup.appendChild(dropdown);

		// Fetch and load client's own accounts
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
					const opt = document.createElement("option");
					opt.value = acc.accountNo;
					opt.textContent = acc.accountNo;
					dropdown.appendChild(opt);
				});
			}
		})
		.catch(err => console.error("Error loading client accounts:", err));
	} else {
		// For employee/manager/gm: only digits, max 11
		fromAccount.addEventListener("input", () => {
			fromAccount.value = fromAccount.value.replace(/\D/g, '').slice(0, 11);
		});
	}

	// Form submit
	form.addEventListener("submit", function (e) {
		e.preventDefault();

		const txn = parseInt(txnType.value);
		const password = passwordInput.value.trim();

		if (!password) {
			handleResponse({ error: "Please enter your password." });
			return;
		}

		const accountNo = parseInt(document.getElementById("fromAccount").value);
		if (isNaN(accountNo)) {
			handleResponse({ error: "Invalid From Account number." });
			return;
		}

		const body = {
			accountNo: accountNo,
			amount: parseFloat(amountInput.value),
			txnType: txn,
			description: form.description.value || "",
			Password: password
		};

		if (txn === 3) {
			const peer = parseInt(peerAccNo.value);
			if (isNaN(peer)) {
				handleResponse({ error: "Please enter a valid recipient account number." });
				return;
			}
			body.peerAccNo = peer;
		}

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
			handleResponse(data);
			if (!data.error) {
				form.reset();
				txnType.dispatchEvent(new Event("change")); // Reset peer visibility
			}
		})
		.catch(err => {
			console.error("Transaction error:", err);
			handleResponse({ error: "Transaction failed. Please try again." });
		});
	});

	// Trigger change event to set peer field visibility
	txnType.dispatchEvent(new Event("change"));
}
