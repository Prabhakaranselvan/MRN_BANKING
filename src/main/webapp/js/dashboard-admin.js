
function initAdminScript() {
  const userName = document.body.dataset.userName;
  document.querySelector(".header-left h2").textContent = `Welcome, ${userName}`;  
  renderAllCharts();
  loadPendingRequestActions();
  setupChartTabSwitcher();
  fetchTransactions();
}

// ================== CHART TAB SWITCHING ===================
function setupChartTabSwitcher() {
  const tabs = document.querySelectorAll(".chart-tab");
  const charts = document.querySelectorAll(".chart-slide");

  tabs.forEach((tab, index) => {
    tab.addEventListener("click", () => {
      tabs.forEach(t => t.classList.remove("active"));
      charts.forEach(c => c.classList.remove("active"));
      tab.classList.add("active");
      charts[index].classList.add("active");
    });
  });
}

// ================== CHARTS ===================
function renderAllCharts() {
  fetch("/MRN_BANKING/MRNBank/report", {
    method: "GET",
    headers: { "Content-Type": "application/json", "Method": "GET" }
  })
    .then(res => res.json())
    .then(data => {
      const stats = data.Stats || [];

      const typeData = stats.filter(s => s.category === "Accounts By Type");
	  const branchData = stats.filter(s => s.category === "Accounts Per Branch");
	  const totalAccounts = stats.find(s => s.category === "Total Accounts");
	  const accountCount = totalAccounts ? totalAccounts.count : 0;
	  
	  const totalBranches = branchData.length;
	        const estimatedClients = 12;
	        const avgAccountsPerBranch = totalBranches > 0 ? Math.round(accountCount / totalBranches) : 0;

			// Fill summary panel
			      document.getElementById("totalAccounts").textContent = accountCount;
			      document.getElementById("totalBranches").textContent = totalBranches;
			      document.getElementById("estimatedClients").textContent = estimatedClients;
			      document.getElementById("avgPerBranch").textContent = avgAccountsPerBranch;
				  
      renderClientAccountChart(totalAccounts); // Optional
      renderPieChart("accountTypeChart", typeData.map(s => s.subcategory), typeData.map(s => s.count), "Accounts by Type");
      renderPieChart("branchAccountChart", branchData.map(s => "Branch " + s.subcategory), branchData.map(s => s.count), "Accounts per Branch");
    })
    .catch(err => {
      console.error("Failed to load report data:", err);
    });
}

function renderClientAccountChart(accountCount) {
  const canvas = document.getElementById("clientAccountChart");
  if (!canvas) return;

  const ctx = canvas.getContext("2d");
  const clientCount = Math.round(accountCount * 0.7);

  new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: ["Clients (Est)", "Accounts"],
      datasets: [{
        data: [clientCount, accountCount],
        backgroundColor: ["#3498db", "#2ecc71"],
        borderWidth: 1
      }]
    },
    options: {
      plugins: {
        legend: { position: "bottom" },
        datalabels: {
          color: "#fff",
          formatter: (value, ctx) => {
            const total = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
            return ((value / total) * 100).toFixed(1) + "%";
          }
        }
      }
    },
    plugins: [ChartDataLabels]
  });
}

function renderPieChart(canvasId, labels, values, title) {
  const canvas = document.getElementById(canvasId);
  if (!canvas) return;

  const ctx = canvas.getContext("2d");

  new Chart(ctx, {
    type: "doughnut",
    data: {
      labels,
      datasets: [{
        data: values,
        backgroundColor: ["#3498db", "#2ecc71", "#e67e22", "#9b59b6", "#e74c3c"],
        borderWidth: 1
      }]
    },
    options: {
      plugins: {
        legend: { position: "bottom" },
        title: {
          display: true,
          text: title,
          padding: { top: 10, bottom: 20 }
        },
        datalabels: {
          color: "#fff",
          formatter: (value, ctx) => {
            const total = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
            return ((value / total) * 100).toFixed(1) + "%";
          }
        }
      }
    },
    plugins: [ChartDataLabels]
  });
}

// ================== PENDING REQUESTS ===================
function loadPendingRequestActions() {
  const container = document.getElementById("pendingRequestsPreview");
  if (!container) return;

  fetch("/MRN_BANKING/MRNBank/accountrequest?page=1&limit=10&status=0", {
    method: "GET",
    headers: { "Content-Type": "application/json", "Method": "GET" }
  })
    .then(res => res.json())
    .then(data => {
      const requests = data.AccountRequests || [];

      if (requests.length === 0) {
        container.innerHTML = `<p class="no-request">No pending requests.</p>`;
        return;
      }

	  const items = requests.map(function(req) {
	    return (
	      '<div class="admin-request-item">' +
	        '<div class="admin-request-id">#' + req.requestId + '</div>' +
	        '<div class="admin-request-details">' +
	          '<span><strong>Client:</strong> ' + req.clientId + '</span>' +
	          '<span><strong>Type:</strong> ' + formatAccountType(req.accountType) + '</span>' +
	          '<span><strong>Date:</strong> ' + new Date(req.requestedTime * 1000).toLocaleString() + '</span>' +
	        '</div>' +
	        '<div class="admin-request-actions">' +
	          '<button class="approve-btn" title="Approve" onclick="approveRequestFromAdmin(' + req.requestId + ', 1)">' +
	            '<span class="material-icons">check_circle</span>' +
	          '</button>' +
	          '<button class="reject-btn" title="Reject" onclick="approveRequestFromAdmin(' + req.requestId + ', 2)">' +
	            '<span class="material-icons">cancel</span>' +
	          '</button>' +
	        '</div>' +
	      '</div>'
	    );
	  }).join("");


      container.innerHTML = items;
    })
    .catch(err => {
      console.error("Failed to load preview requests:", err);
      container.innerHTML = `<p class="error">Unable to load account requests.</p>`;
    });
}

function approveRequestFromAdmin(requestId, status) {
  fetch("/MRN_BANKING/MRNBank/accountapproval", {
    method: "POST",
    headers: { "Content-Type": "application/json", "Method": "POST" },
    body: JSON.stringify({ requestId, status })
  })
    .then(res => res.json())
    .then(data => {
      handleResponse(data);
      loadPendingRequestActions(); // Refresh list
    })
    .catch(err => {
      console.error("Approval error:", err);
    });
}

function formatAccountType(type) {
  switch (type) {
    case 1: return "Savings";
    case 2: return "Current";
    case 3: return "Fixed Deposit";
    default: return "Unknown";
  }
}

// === Render Modern Transactions List ===
function renderTransactions(transactions) {
  const txnListContainer = document.getElementById('modernTxnList');
  const typeMap = ['Deposit', 'Withdrawal', 'Credit', 'Debit'];

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

// === Fetch Transactions for GM/Manager View ===
function fetchTransactions(page = 1, limit = 10, branchId = null) {
  const params = new URLSearchParams({ page, limit });
  if (branchId !== null) {
    params.append('branchId', branchId);
  }

  fetch(`http://localhost:8080/MRN_BANKING/MRNBank/accountstatement?${params.toString()}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Method': 'GET'  // Custom header to signal internal handling if needed
    }
  })
    .then(res => res.json())
    .then(data => renderTransactions(data.Transactions || []))
    .catch(err => {
      console.error('Failed to load transactions:', err);
    });
}

