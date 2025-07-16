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
      <input type="text" id="branchName" pattern="^[A-Za-z0-9 '\-]+$" minlength="3" maxlength="50" required 
      	title="Branch Name must be 3–50 characters long and may include letters, digits, spaces, apostrophes, hyphens only"/>

      <label for="branchLocation">Location</label>
      <input type="text" id="branchLocation" pattern="^[A-Za-z '\-]+$" minlength="3" maxlength="50" required 
      	title="Branch Location must be 3–50 characters long and may include letters, spaces, apostrophes, hyphenN only"/>

      <label for="contactNo">Contact Number</label>
      <input type="text" id="contactNo" maxlength="10" pattern="\d{10}" title="Phone number must be 10 digits" 
      	  oninput="this.value = this.value.replace(/\D/g, '')" required />

      <div class="modal-actions">
        <button type="submit" class="btn-submit">Save</button>
      </div>
    </form>
  </div>
</div>
<% } %>

