<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="statement-container">
    <h2 class="form-header">Account Statement</h2>

    <form id="statementForm" class="statement-form">
        <div class="form-row">
            <label for="accountNo">Account No</label>
            <input type="text" id="accountNo" name="accountNo" placeholder="Optional">
        </div>

        <div class="form-row">
            <label for="fromDate">From Date</label>
            <input type="date" id="fromDate" name="fromDate">

            <label for="toDate">To Date</label>
            <input type="date" id="toDate" name="toDate">
        </div>

        <button type="submit" class="btn-submit">Fetch Statement</button>
    </form>

    <div id="statementResult" class="statement-result">
        <!-- Transactions will be inserted here -->
    </div>
</div>

<style>
    .statement-container {
        max-width: 960px;
        margin: 30px auto;
        padding: 20px;
        font-family: 'Segoe UI', sans-serif;
        background: #fff;
    }
    .form-header {
        font-size: 24px;
        margin-bottom: 20px;
        color: #2c3e50;
    }
    .statement-form {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        margin-bottom: 30px;
    }
    .form-row {
        display: flex;
        flex-direction: column;
        flex: 1;
    }
    label {
        font-weight: 500;
        margin-bottom: 5px;
    }
    input[type="text"],
    input[type="date"] {
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }
    .btn-submit {
        padding: 10px 20px;
        background-color: #3498db;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        align-self: flex-end;
    }
    .btn-submit:hover {
        background-color: #2980b9;
    }
    .statement-result {
        margin-top: 30px;
    }
    .statement-result table {
        width: 100%;
        border-collapse: collapse;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
    }
    .statement-result th, .statement-result td {
        padding: 12px;
        border: 1px solid #ddd;
        text-align: center;
    }
    .statement-result th {
        background-color: #f3f6f9;
        font-weight: 600;
    }
</style>

<script src="${pageContext.request.contextPath}/js/dashboard-statement.js"></script>
