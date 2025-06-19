<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="account-container">
    <h2 class="form-header">My Accounts</h2>

    <table id="account-summary-table" class="account-table">
        <thead>
            <tr>
                <th>Account No</th>
                <th>Branch ID</th>
                <th>Account Type</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <!-- Populated via JS -->
        </tbody>
    </table>

    <div class="account-details">
        <label for="accountSelect" class="select-label">Select Account for Details:</label>
        <select id="accountSelect" class="select-dropdown">
            <option value="">-- Select Account --</option>
        </select>

        <div id="accountInfo" class="account-info-box">
            <p>Please select an account to view details.</p>
        </div>
    </div>
</div>



<script src="${pageContext.request.contextPath}/js/dashboard-accounts.js"></script>
