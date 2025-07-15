function initTransactionScript() {
  const userId = document.body.getAttribute("data-user-id");
  const userRole = parseInt(document.body.getAttribute("data-user-role"));
  const form = document.getElementById("transferForm");

  const txnType = document.getElementById("txnType");
  const fromAccount = document.getElementById("fromAccount");
  const amountInput = document.getElementById("amount");
  const passwordInput = document.getElementById("txnPassword");
  const togglePasswordBtn = document.getElementById("togglePassword");
  const fromAccountGroup = document.getElementById("fromAccountGroup");

  const peerGroupInside = document.getElementById("peerGroupInside");
  const peerGroupOutside = document.getElementById("peerGroupOutside");
  const peerAccNoInside = document.getElementById("peerAccNoInside");
  const peerAccNoOutside = document.getElementById("peerAccNoOutside");
  const peerBankName = document.getElementById("peerBankName");
  const peerIFSCCode = document.getElementById("peerIFSCCode");
  const peerName = document.getElementById("peerName");
  const extraFields = document.getElementById("extraInfoFields");

  if ([1, 2, 3].includes(userRole)) {
    txnType.innerHTML = `
      <option value="0">Deposit</option>
      <option value="1">Withdraw</option>
      <option value="3">Transfer - Within Bank</option>
      <option value="4">Transfer - Outside Bank</option>
    `;
  }

  txnType.addEventListener("change", () => {
    const val = txnType.value;
    peerGroupInside.style.display = val === "3" ? "flex" : "none";
    peerGroupOutside.style.display = val === "4" ? "flex" : "none";
    extraFields.style.display = val === "4" ? "flex" : "none";
    peerAccNoInside.required = val === "3";
    peerAccNoOutside.required = val === "4";
  });

  if (peerAccNoInside) {
    peerAccNoInside.addEventListener("input", () => {
      peerAccNoInside.value = peerAccNoInside.value.replace(/\D/g, '').slice(0, 11);
    });
  }

  if (peerAccNoOutside) {
    peerAccNoOutside.addEventListener("input", () => {
      let cleaned = peerAccNoOutside.value.replace(/\D/g, '').replace(/^0+/, '');
      peerAccNoOutside.value = cleaned.slice(0, 15);
    });
  }

  amountInput.addEventListener("input", () => {
    let value = amountInput.value.replace(/[^0-9.]/g, '').replace(/^(\d*\.\d{0,2}).*$/, '$1');
    let floatVal = parseFloat(value);
    if (!isNaN(floatVal)) {
      if (floatVal > 100000) value = '100000';
      else if (floatVal < 1) value = '1';
    }
    amountInput.value = value;
  });

  togglePasswordBtn.addEventListener("click", () => {
	const isHidden = passwordInput.type === "password";
    passwordInput.type = isHidden ? "text" : "password";
    togglePasswordBtn.textContent = isHidden ? "visibility_off" : "visibility";
    togglePasswordBtn.title = isHidden ? "Hide Password" : "Show Password";
  });

  if (userRole === 0) {
    const dropdown = document.createElement("select");
    dropdown.name = "accountNo";
    dropdown.id = "fromAccount";
    dropdown.required = true;
    dropdown.innerHTML = `<option value="">Select Account</option>`;
    fromAccountGroup.innerHTML = `<label for="fromAccount">From Account</label>`;
    fromAccountGroup.appendChild(dropdown);

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
    fromAccount.addEventListener("input", () => {
      fromAccount.value = fromAccount.value.replace(/\D/g, '').slice(0, 11);
    });
  }

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    const txn = parseInt(txnType.value);
    const actualTxn = (txn === 3 || txn === 4) ? 3 : txn;
    const password = passwordInput.value.trim();

    if (!password) return handleResponse({ error: "Please enter your password." });

    const accountNo = parseInt(document.getElementById("fromAccount").value);
    if (isNaN(accountNo)) return handleResponse({ error: "Invalid From Account number." });

    const body = {
      accountNo: accountNo,
      amount: parseFloat(amountInput.value),
      txnType: actualTxn,
      description: form.description.value || "",
      Password: password
    };

    if (txn === 3) {
      const peer = parseInt(peerAccNoInside.value);
      if (isNaN(peer)) return handleResponse({ error: "Please enter a valid inside bank recipient account number." });
      body.peerAccNo = peer;
	  body.internalTransfer = true;
    }

    if (txn === 4) {
      const peer = parseInt(peerAccNoOutside.value);
      if (isNaN(peer)) return handleResponse({ error: "Please enter a valid outside bank recipient account number." });

      const extra = {
        peerBankName: peerBankName.value.trim(),
        peerIFSCCode: peerIFSCCode.value.trim(),
        peerName: peerName.value.trim()
      };

      if (!extra.peerBankName || !extra.peerIFSCCode || !extra.peerName) {
        return handleResponse({ error: "Please fill all fields for outside bank transfer." });
      }

      body.peerAccNo = peer;
      body.extraInfo = JSON.stringify(extra);
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
          txnType.dispatchEvent(new Event("change"));
        }
      })
      .catch(err => {
        console.error("Transaction error:", err);
        handleResponse({ error: "Transaction failed. Please try again." });
      });
  });

  txnType.dispatchEvent(new Event("change"));
}
