<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>

<div class="container">
	<div class="account-requests-container">
		<div class="account-requests-header">
			<h2 class="form-header">ACCOUNT REQUESTS</h2>
			<div class="account-requests-header-actions">
				<div id="filterControls" class="filters">
					<select id="statusFilter">
						<option value="">All Status</option>
						<option value="0">Pending</option>
						<option value="1">Approved</option>
						<option value="2">Rejected</option>
					</select> <select id="branchFilter">
						<option value="">All Branches</option>
						<!-- dynamically populate -->
					</select>
				</div>
				<div class="pagination">
					<button id="prevPage" class="page-btn" title="Previous">
						<span class="material-icons">chevron_left</span>
					</button>
					<button id="nextPage" class="page-btn" title="Next">
						<span class="material-icons">chevron_right</span>
					</button>
				</div>
			</div>
		</div>
		<div id="accountRequestList" class="account-request-list"></div>
	</div>
</div>
