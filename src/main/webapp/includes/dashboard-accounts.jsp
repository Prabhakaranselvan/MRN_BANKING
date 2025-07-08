<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="container">
  <div class="accounts-container">
    <div class="accounts-header">
      <h2 class="form-header">ACCOUNTS</h2>
      <div class="accounts-header-actions">
        <div id="filterControls" class="filters">
          <select id="accountTypeFilter">
            <option value="">All Types</option>
            <option value="1">Savings</option>
            <option value="2">Current</option>
            <option value="3">Fixed Deposit</option>
          </select>
          <select id="branchFilter">
            <option value="">All Branches</option>
            <!-- dynamically populated -->
          </select>
        </div>
        <div class="pagination">
          <button id="addAccountBtn" class="add-account-btn">
            <span class="material-icons">add</span>
          </button>
        <div  id="paginationWrapper" class="pagination-bar">
	          <button id="prevPage" class="page-btn" title="Previous">
	            <span class="material-icons">chevron_left</span>
	          </button>
	          <button id="nextPage" class="page-btn" title="Next">
	            <span class="material-icons">chevron_right</span>
	          </button>
        </div>
        </div>
      </div>
    </div>
    <div id="accountsList" class="accounts-list">
    </div>
  </div>
</div>

<!-- Add Account Modal -->
<div id="addAccountModal" class="modal-overlay">
  <div class="modal-content">
    <div class="modal-header">
      <h3>Add Account</h3>
      <button id="closeAddModal" class="modal-close">&times;</button>
    </div>
    <form id="addAccountForm">
      <label for="addBranch">Branch</label>
      <select id="addBranch" class="branch-input" name="branchId" required></select>

      <label for="addClientId">Client ID</label>
      <input type="number" id="addClientId" name="clientId" required min="1" step="1" oninput="this.value=this.value.replace(/[^0-9]/g,'')" />

      <label for="addAccountType">Account Type</label>
      <select id="addAccountType" name="accountType" required>
        <option value="">-- Select --</option>
        <option value="1">Savings</option>
        <option value="2">Current</option>
        <option value="3">Fixed Deposit</option>
      </select>

      <label for="addBalance">Opening Balance</label>
      <input type="number" id="addBalance" name="balance" required min="0" step="0.01" oninput="this.value=this.value.replace(/[^0-9.]/g,'')" />

      <div class="modal-actions">
        <button type="submit" class="btn-submit">Create</button>
      </div>
    </form>
  </div>
</div>

<!-- Update Account Modal -->
<div id="updateAccountModal" class="modal-overlay">
  <div class="modal-content">
    <div class="modal-header">
      <h3>Update Account</h3>
      <button id="closeUpdateModal" class="modal-close">&times;</button>
    </div>
    <form id="updateAccountForm">
      <input type="hidden" name="accountNo" />

      <label for="updateAccountType">Account Type</label>
      <select id="updateAccountType" name="accountType" required>
        <option value="1">Savings</option>
        <option value="2">Current</option>
        <option value="3">Fixed Deposit</option>
      </select>

      <label for="updateStatus">Status</label>
      <select id="updateStatus" name="status" required>
        <option value="1">Active</option>
        <option value="0">Inactive</option>
        <option value="2">Closed</option>
      </select>
      
      <label for="updatePassword">Password</label>
			<div class="password-field">
			  <input type="password" id="updatePassword" name="password" required />
			  <span id="togglePassword" class="material-icons toggle-icon" title="Show Password">visibility</span>
			</div>

      <div class="modal-actions">
        <button type="submit" class="btn-submit">Update</button>
      </div>
    </form>
  </div>
</div>
