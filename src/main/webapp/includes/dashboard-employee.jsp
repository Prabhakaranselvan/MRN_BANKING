<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="container">
  <div class="clients-container">
    <div class="clients-header">
      <h2 class="form-header">EMPLOYEE LIST</h2>
      <div class="clients-header-actions">
        <div id="filterControls" class="filters">
          <select id="roleFilter">
            <option value="">All Roles</option>
            <option value="EMPLOYEE">Employee</option>
            <option value="MANAGER">Manager</option>
            <option value="GENERAL_MANAGER">General Manager</option>
          </select>
          <select id="branchFilter">
            <option value="">All Branches</option>
            <!-- dynamically populate -->
          </select>
        </div>
        <div class="pagination">
        	<button id="addClientBtn" class="add-user-btn" onclick="loadContent('dashboard-addUser.jsp?targetRole=1')">
				<span class="material-icons">person_add</span>
			</button>
          <button id="prevPage" class="page-btn" title="Previous">
            <span class="material-icons">chevron_left</span>
          </button>
          <button id="nextPage" class="page-btn" title="Next">
            <span class="material-icons">chevron_right</span>
          </button>
        </div>
      </div>
    </div>
    <div id="employeesList" class="clients-list">
      <p>Loading employees...</p>
    </div>
  </div>
</div>

