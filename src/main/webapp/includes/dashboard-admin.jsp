<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>

<div class="dashboard-main">

	<!-- Header -->
	<div class="dashboard-header">
		<div class="header-left">
			<h2></h2>
			<p class="subtitle">Manage requests and monitor operations
				effectively</p>
		</div>
		<div class="header-right">
			<button class="transfer-button"
				onclick="loadContent('dashboard-transaction.jsp')">
				<span class="material-icons">send</span> Transfer Money
			</button>
		</div>
	</div>

	<!-- Grid Sections -->
	<div class="dashboard-grid">

		<!-- Pending Requests Section -->
		<div class="section-box">
			<div class="section-header">
				<h4>Pending Account Requests</h4>
				<span class="material-icons clickable"
					onclick="loadContent('dashboard-request.jsp')">open_in_new</span>
			</div>
			<div id="pendingRequestsPreview" class="request-preview">
				<p>Loading...</p>
			</div>
		</div>

		<!-- Unified Chart Section -->
		<div class="section-box chart-slider">
			<div class="section-header">
				<h4>Account Summary Charts</h4>
				<div class="chart-tabs">
					<button class="chart-tab active">By	Type</button>
					<button class="chart-tab">By Branch</button>
				</div>
				<span class="material-icons clickable"
					onclick="loadContent('dashboard-accounts.jsp')">open_in_new</span>
			</div>
			<div class="inner-section">
				<div class="chart-slider-container">
					<canvas id="accountTypeChart" class="chart-slide active"
						height="260"></canvas>
					<canvas id="branchAccountChart" class="chart-slide" height="260"></canvas>
				</div>
				<div class="chart-summary-panel">
					<h5>Summary</h5>
					<ul>
						<li><strong>Total Accounts:</strong> <span id="totalAccounts">-</span></li>
						<li><strong>Total Branches:</strong> <span id="totalBranches">-</span></li>
						<li><strong>Estimated Clients:</strong> <span
							id="estimatedClients">-</span></li>
						<li><strong>Avg. Accounts / Branch:</strong> <span
							id="avgPerBranch">-</span></li>
					</ul>
				</div>
			</div>
		</div>

	</div>
	<div class="section-box transactions">
		<div class="section-header">
			<h4>Latest Transactions</h4>
			<span class="material-icons clickable"
				onclick="loadContent('dashboard-statement.jsp')">open_in_new</span>
		</div>
		<div id="modernTxnList" class="txn-list">
		</div>
	</div>

</div>

<!-- External Chart Scripts -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script
	src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2.2.0"></script>
