<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<h2>Money Transfer</h2>
<p>Transfer funds securely between accounts.</p>

<form action="transferMoney" method="post">
    <label for="fromAccount">From Account:</label><br>
    <select name="fromAccount" id="fromAccount" required>
        <option value="1234567890">1234567890 (Savings)</option>
        <option value="9876543210">9876543210 (Current)</option>
    </select><br><br>

    <label for="toAccount">To Account Number:</label><br>
    <input type="text" name="toAccount" id="toAccount" required /><br><br>

    <label for="amount">Amount (INR):</label><br>
    <input type="number" name="amount" id="amount" step="0.01" required /><br><br>

    <label for="remarks">Remarks:</label><br>
    <input type="text" name="remarks" id="remarks" /><br><br>

    <button type="submit">Transfer</button>
</form>
