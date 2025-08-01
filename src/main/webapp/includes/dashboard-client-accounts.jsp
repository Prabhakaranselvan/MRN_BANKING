<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="account-container">
  <!-- Header Row -->
  <div class="account-header">
    <h2 class="form-header">My Accounts</h2>
    <button id="openAccountRequestModal" class="add-account-btn" title="Request New Account">
      <i class="fas fa-plus-circle"></i>
    </button>
  </div>

  <!-- Account Cards -->
  <div id="accountCardsContainer" class="account-cards-grid">
    <!-- Cards populated via JS -->
  </div>

  <!-- Account Selection + Info -->
  <div class="account-details">
    <label for="accountSelect" class="select-label" id="selectLabel">Select Account for Details:</label>
    <select id="accountSelect" class="select-dropdown">
      <option value="">-- Select Account --</option>
    </select>

    <div id="accountInfo" class="account-info-box">
      <p>Please select an account to view details.</p>
    </div>
  </div>
</div>

<!-- Account Request Modal -->
<div id="accountRequestModal" class="modal-overlay">
  <div class="modal-content">
    <div class="modal-header">
      <h3>Request New Account</h3>
      <button id="closeModal" class="modal-close">&times;</button>
    </div>
    <form id="accountRequestForm">
      <label for="branchSelect">Select Branch</label>
      <select id="branchSelect" required></select>

      <label for="accountTypeSelect">Select Account Type</label>
      <select id="accountTypeSelect" required>
        <option value="">-- Select Type --</option>
        <option value="1">Savings</option>
        <option value="2">Current</option>
        <option value="3">Fixed Deposit</option>
      </select>

      <div class="modal-actions">
        <button type="submit" class="btn-submit">Submit</button>
      </div>
    </form>
  </div>
</div>

