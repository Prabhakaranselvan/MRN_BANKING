<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="container">
  <div class="clients-container">
    <div class="clients-header">
      <h2 class="form-header">EMPLOYEE</h2>
      <div class="clients-header-actions">
        <div id="filterControls" class="filters">
          <select id="roleFilter">
            <option value="">All Roles</option>
            <option value="1">Employee</option>
            <option value="2">Manager</option>
            <option value="3">General Manager</option>
          </select>
          <select id="branchFilter">
            <option value="">All Branches</option>
            <!-- dynamically populate -->
          </select>
        </div>
        <div class="pagination">
        	<button id="addClientBtn" class="add-user-btn" onclick="openAddUserModal(1)">
				<span class="material-icons" title="Add Employee">person_add</span>
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
    <div id="employeesList" class="clients-list">
    </div>
  </div>
</div>

