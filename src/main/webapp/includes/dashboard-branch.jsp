<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<%
	Short userRole = (Short) ses.getAttribute("userCategory");
	Boolean isGM = userRole != null && userRole == 3;
%>

<div class="branch-container">
  <div class="branch-header">
    <h2 class="form-header">Branch Directory</h2>

    <% if (isGM) { %>
      <button id="openBranchModal" class="add-branch-btn" title="Add New Branch">
        <i class="fas fa-plus-circle"></i>
      </button>
    <% } %>
  </div>

  <div id="branchCardsContainer" class="branch-cards-grid">
    <!-- Cards populated via JS -->
  </div>

  <div id="branchInfo" class="branch-info-box">
    <p>Please select a branch to view details.</p>
  </div>
</div>

<% if (isGM) { %>
<!-- Branch Modal (Create or Edit) -->
<div id="branchModal" class="modal-overlay">
  <div class="modal-content">
    <div class="modal-header">
      <h3 id="branchModalTitle">New Branch</h3>
      <button id="closeBranchModal" class="modal-close">&times;</button>
    </div>
    <form id="branchForm">
      <input type="hidden" id="branchId" />

      <label for="branchName">Branch Name</label>
      <input type="text" id="branchName" required />

      <label for="branchLocation">Location</label>
      <input type="text" id="branchLocation" required />

      <label for="contactNo">Contact Number</label>
      <input type="tel" id="contactNo" pattern="\\d{10}" required />

      <div class="modal-actions">
        <button type="submit" class="btn-submit">Save</button>
      </div>
    </form>
  </div>
</div>
<% } %>

