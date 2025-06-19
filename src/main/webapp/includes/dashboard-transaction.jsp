<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="transaction-container">
    <h2 class="form-header">↔ Fund Transfer</h2>

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

    <div id="transferResult" class="transfer-result"></div>
</div>

<style>
.transaction-container {
    max-width: 800px;
    margin: 40px auto;
    padding: 30px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    font-family: 'Segoe UI', sans-serif;
}
.form-header {
    font-size: 26px;
    text-align: center;
    color: #2c3e50;
    margin-bottom: 25px;
}
.transfer-form {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    justify-content: space-between;
}
.form-group {
    flex: 1;
    min-width: 240px;
    display: flex;
    flex-direction: column;
}
.form-group.full-width {
    flex: 1 1 100%;
    display: flex;
    justify-content: center;
}
label {
    font-weight: 500;
    margin-bottom: 6px;
    color: #34495e;
}
input, select {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 15px;
    background-color: #fdfdfd;
}
input:focus, select:focus {
    border-color: #3498db;
    outline: none;
}
.btn-submit {
    padding: 12px 24px;
    background-color: #27ae60;
    color: white;
    font-size: 16px;
    font-weight: 500;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}
.btn-submit:hover {
    background-color: #1e8449;
}
.transfer-result {
    margin-top: 20px;
    text-align: center;
    font-size: 16px;
    font-weight: 500;
}
.transfer-result.success { color: #2ecc71; }
.transfer-result.error { color: #e74c3c; }
</style>

<script src="${pageContext.request.contextPath}/js/dashboard-transaction.js"></script>
