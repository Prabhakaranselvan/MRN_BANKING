<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="transaction-container">
    <h2 class="form-header">FUND TRANSFER</h2>

    <form id="transferForm" class="transfer-form">
        <div class="form-group">
            <label for="fromAccount">From Account</label>
            <select id="fromAccount" name="accountNo" required>
                <option value="">Select Account</option>
            </select>
        </div>

        <div class="form-group">
            <label for="peerAccNo">To Account No</label>
            <input type="number" id="peerAccNo" name="peerAccNo" required placeholder="Enter Receiver's Account Number">
        </div>

        <div class="form-group">
            <label for="amount">Amount</label>
            <input type="number" id="amount" name="amount" required min="1" step="0.01">
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <input type="text" id="description" name="description" placeholder="Eg: Rent, Shopping">
        </div>

        <div class="form-group full-width">
            <button type="submit" class="btn-submit">➤ Transfer</button>
        </div>
    </form>
</div>

<script src="${pageContext.request.contextPath}/js/dashboard-transaction.js"></script>
