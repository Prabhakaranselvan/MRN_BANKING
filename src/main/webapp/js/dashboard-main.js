function initMainScript() {
  const userId = +document.body.dataset.userId;

  const totalBalEl = document.getElementById('totalBalance');
  const cardsContainer = document.getElementById('accountCards');
  const txnListContainer = document.getElementById('modernTxnList');
  const pieCtx = document.getElementById('balancePieChart');
  const userName = document.body.dataset.userName;
  document.querySelector(".header-left h2").textContent = `Welcome, ${userName}`;

  const typeMap = ['Deposit', 'Withdrawal', 'Credit', 'Debit'];
  const colorPalette = ['#3498db', '#2ecc71', '#f1c40f', '#e74c3c', '#9b59b6', '#34495e'];
  let doughnutChart;
  
  const chartTitle = document.getElementById('chartTitle');
  const accountSelect = document.getElementById('accountSelectDropdown');
    const accountInfo = document.createElement('div');
    accountInfo.id = "accountDetailPanel";
	accountInfo.className = "account-info-box";
	accountInfo.style.display = 'none';

  // === Render Account Cards ===
  function renderAccounts(accounts) {
    cardsContainer.innerHTML = '';
    let totalBalance = 0;
    const labels = [];
    const balances = [];
    const colors = [];
	
	accountSelect.innerHTML = `<option value="">-- All Accounts --</option>`;

    accounts.forEach((account, i) => {
      const { accountNo, balance, accountType, status } = account;
      totalBalance += balance;
      labels.push(`${accountNo}`);
      balances.push(balance);
      colors.push(colorPalette[i % colorPalette.length]);

      const card = document.createElement('div');
      card.className = 'account-card';
      card.innerHTML = `
        <h5>Account: ${accountNo}</h5>
        <p>Type: ${['Savings', 'Current', 'FD'][accountType - 1]}</p>
        <p>Status: ${status === 1 ? 'Active' : 'Inactive'}</p>
        <div class="account-balance">₹${balance.toFixed(2)}</div>
      `;
      cardsContainer.appendChild(card);
	  
	  // Add to dropdown
	       const opt = document.createElement("option");
	       opt.value = accountNo;
	       opt.textContent = `${accountNo} - ${getAccountTypeName(accountType)}`;
	       accountSelect.appendChild(opt);
    });

    totalBalEl.textContent = `₹${totalBalance.toFixed(2)}`;
	
	// Insert dropdown and info panel
	    cardsContainer.prepend(accountInfo);
		
    renderDoughnutChart(labels, balances, colors);
  }
  
  // === Account Dropdown Change Listener ===
    accountSelect.addEventListener("change", () => {
      const selectedAccountNo = accountSelect.value;
      accountInfo.innerHTML = "";
	  
	  // Hide or show account cards based on selection
	    const allCards = document.querySelectorAll('.account-card');
	    if (selectedAccountNo) {
			chartTitle.textContent = "Credit vs Debit (Last 30 Days)";
			accountInfo.style.display = '';
	      allCards.forEach(card => card.style.display = 'none');
	    } else {
			chartTitle.textContent = "Balance Distribution";
			accountInfo.style.display = 'none';
	      allCards.forEach(card => card.style.display = '');
	    }


      if (!selectedAccountNo) {
		fetchAccounts(); 
        fetchTransactions(); // Show all if none selected
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
        fetchTransactions(parseInt(selectedAccountNo)); // fetch transactions for this account
		 fetchCreditDebitChart(parseInt(selectedAccountNo));
      })
      .catch(err => {
        console.error("Failed to fetch account details:", err);
        accountInfo.innerHTML = `<p>Error loading account details.</p>`;
      });
    });

  // === Render Doughnut Chart ===
  function renderDoughnutChart(labels, data, backgroundColors) {
    if (doughnutChart) doughnutChart.destroy();

    Chart.register(ChartDataLabels);
    doughnutChart = new Chart(pieCtx, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data,
          backgroundColor: backgroundColors,
          borderWidth: 1
        }]
      },
	  options: {
	    cutout: '60%',
	    animation: {
	      duration: 500, // smooth entrance
	    },
	    hover: {
	      mode: 'nearest',
	      animationDuration: 200 // smooth transition on hover
	    },
	    plugins: {
	      datalabels: {
	        formatter: (value, ctx) => {
	          const total = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
	          return `${((value / total) * 100).toFixed(1)}%`;
	        },
	        color: '#fff',
	        font: { weight: 'bold' }
	      },
	      tooltip: {
	        enabled: true,
	        backgroundColor: '#2c3e50',
	        titleFont: { size: 13 },
	        bodyFont: { size: 13 },
	        padding: 10,
	        displayColors: false,
	        animation: {
	          duration: 300
	        },
	        callbacks: {
	          label: function(context) {
	            const value = context.parsed;
	            const total = context.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
	            const percentage = ((value / total) * 100).toFixed(1);
	            return ` ₹${value.toFixed(2)} (${percentage}%)`;
	          }
	        }
	      },
	      legend: {
	        position: 'bottom',
	        labels: {
	          usePointStyle: true,
	          font: {
	            weight: 'bold'
	          },
	          color: '#000'
	        }
	      }
	    }
	  }

    });
  }
  
  function renderCreditDebitChart(credit, debit) {
     if (doughnutChart) doughnutChart.destroy();

     doughnutChart = new Chart(pieCtx, {
       type: 'doughnut',
       data: {
         labels: ['Credit', 'Debit'],
         datasets: [{
           data: [credit, debit],
           backgroundColor: colorPalette
         }]
       },
       options: {
         cutout: '60%',
         plugins: {
           datalabels: {
             formatter: (val, ctx) => {
               const total = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
               return `${((val / total) * 100).toFixed(1)}%`;
             },
             color: '#fff'
           },
           legend: { position: 'bottom' }
         }
       }
     });
   }
   
   function fetchCreditDebitChart(accountNo) {
       const today = new Date();
       const toDate = today.toISOString().split("T")[0];
       const fromDate = new Date(today.setDate(today.getDate() - 29)).toISOString().split("T")[0];

       fetch('/MRN_BANKING/MRNBank/accountstatement', {
         method: 'POST',
         headers: {
           'Content-Type': 'application/json',
           'Method': 'GET'
         },
         body: JSON.stringify({
           accountNo,
           fromDate,
           toDate,
           limit: 100
         })
       })
       .then(res => res.json())
       .then(data => {
         const txns = data.Transactions || [];
         let credit = 0, debit = 0;

         txns.forEach(txn => {
           if (txn.txnType === 0 || txn.txnType === 2) credit += txn.amount;
           else if (txn.txnType === 1 || txn.txnType === 3) debit += txn.amount;
         });

         renderCreditDebitChart(credit, debit);
       })
       .catch(err => console.error('Failed to load credit/debit stats:', err));
     }

  // === Render Modern Transactions List ===
  function renderTransactions(transactions) {
    if (!transactions.length) {
      txnListContainer.innerHTML = '<p>No recent transactions.</p>';
      return;
    }

    txnListContainer.innerHTML = transactions.map(txn => {
      const dateStr = new Date(txn.txnTime * 1000).toLocaleString();
      const isCredit = txn.txnType === 0 || txn.txnType === 2;
      const iconClass = isCredit ? 'green' : 'red';
      const amountClass = isCredit ? 'credit' : 'debit';
      const label = typeMap[txn.txnType] || 'Unknown';

      return `
        <div class="txn-item">
          <div class="txn-info">
            <div class="txn-icon ${iconClass}">
              <span class="material-icons">sync_alt</span>
            </div>
            <div class="txn-labels">
              <span class="txn-title">${label} (Acc: ${txn.accountNo})</span>
              <span class="txn-date">${dateStr}</span>
            </div>
          </div>
          <div class="txn-amount ${amountClass}">₹${txn.amount.toFixed(2)}</div>
        </div>
      `;
    }).join('');
  }

  // === Fetch Functions ===
  function fetchAccounts() {
    fetch('/MRN_BANKING/MRNBank/accounts', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Method': 'GET'
      },
      body: JSON.stringify({ clientId: userId })
    })
    .then(res => res.json())
    .then(data => renderAccounts(data.Accounts || []))
    .catch(err => console.error('Failed to load accounts:', err));
  }

  // === Fetch Transactions by Account or All ===
    function fetchTransactions(accountNo = null) {
      const payload = accountNo ? { accountNo, limit: 5, page: 1 } : { clientId: userId, limit: 5, page: 1 };

      fetch('/MRN_BANKING/MRNBank/accountstatement', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Method': 'GET'
        },
        body: JSON.stringify(payload)
      })
      .then(res => res.json())
      .then(data => renderTransactions(data.Transactions || []))
      .catch(err => {
        console.error('Failed to load transactions:', err);
        txnListContainer.innerHTML = `<p>Error loading transactions.</p>`;
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

  // === INIT ===
  fetchAccounts();
  fetchTransactions();
}
