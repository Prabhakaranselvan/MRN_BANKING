<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="dashboard-main">
  <!-- Welcome, Balance, and Transfer -->
  <div class="dashboard-header">
    <div class="header-left">
      <h2>Welcome back, <c:out value="${userName}"/></h2>
      <div class="total-balance">
        <span class="material-icons balance-icon">account_balance_wallet</span>
        <span id="totalBalance">₹0.00</span>
      </div>
    </div>
    <div class="header-right">
      <button class="transfer-button" onclick="loadContent('dashboard-transaction.jsp')">
        <span class="material-icons">send</span>
        Transfer Money
      </button>
    </div>
  </div>

  <!-- Grid Layout: Accounts & Chart -->
  <div class="dashboard-grid">
    <!-- Accounts Overview -->
    <div class="section-box">
      <div class="section-header">
        <h4>Accounts</h4>
        <span class="material-icons clickable" onclick="loadContent('dashboard-client-accounts.jsp')">open_in_new</span>
      </div>
      <div id="accountCards" class="card-container"></div>
    </div>

    <!-- Balance Chart -->
    <div class="section-box">
      <div class="section-header">
        <h4>Balance Distribution</h4>
      </div>
      <canvas id="balancePieChart" height="200"></canvas>
    </div>
  </div>

  <!-- Transactions -->
  <div class="section-box">
    <div class="section-header">
      <h4>Latest Transactions</h4>
      <span class="material-icons clickable" onclick="loadContent('dashboard-statement.jsp')">open_in_new</span>
    </div>
    <div id="modernTxnList" class="txn-list">
      <p>Loading...</p>
    </div>
  </div>
</div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels"></script>
