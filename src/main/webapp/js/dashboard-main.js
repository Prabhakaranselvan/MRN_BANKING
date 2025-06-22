(function () {
  const userId = +document.body.dataset.userId;

  const totalBalEl = document.getElementById('totalBalance');
  const cardsContainer = document.getElementById('accountCards');
  const txnListContainer = document.getElementById('modernTxnList');
  const pieCtx = document.getElementById('balancePieChart');

  const typeMap = ['Deposit', 'Withdrawal', 'Credit', 'Debit'];
  const colorPalette = ['#3498db', '#2ecc71', '#f1c40f', '#e74c3c', '#9b59b6', '#34495e'];
  let doughnutChart;

  // === Render Account Cards ===
  function renderAccounts(accounts) {
    cardsContainer.innerHTML = '';
    let totalBalance = 0;
    const labels = [];
    const balances = [];
    const colors = [];

    accounts.forEach((account, i) => {
      const { accountNo, balance, accountType, status } = account;
      totalBalance += balance;
      labels.push(`Acct ${accountNo}`);
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
    });

    totalBalEl.textContent = `₹${totalBalance.toFixed(2)}`;
    renderDoughnutChart(labels, balances, colors);
  }

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
        plugins: {
          datalabels: {
            formatter: (value, ctx) => {
              const total = ctx.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
              return `${((value / total) * 100).toFixed(1)}%`;
            },
            color: '#fff',
            font: { weight: 'bold' }
          },
          legend: {
            position: 'bottom',
            labels: { usePointStyle: true }
          }
        }
      }
    });
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
              <span class="txn-title">${label}</span>
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

  function fetchTransactions() {
    fetch('/MRN_BANKING/MRNBank/accountstatement', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Method': 'GET'
      },
      body: JSON.stringify({
        clientId: userId,
        limit: 5,
        page: 1
      })
    })
    .then(res => res.json())
    .then(data => renderTransactions(data.Transactions || []))
    .catch(err => {
      console.error('Failed to load transactions:', err);
      txnListContainer.innerHTML = `<p>Error loading transactions.</p>`;
    });
  }

  // === INIT ===
  fetchAccounts();
  fetchTransactions();
})();
