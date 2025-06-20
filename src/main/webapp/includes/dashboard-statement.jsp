<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>
<%@ page import="java.time.LocalDate"%>

<div class="statement-container">
    <h2 class="form-header">ACCOUNT STATEMENT</h2>

    <form id="statementForm" class="statement-form">
        <div class="form-group">
            <label for="accountSelect">Select Account</label>
            <select id="accountSelect" class="dropdown-select">
                <option value="">All Accounts</option>
            </select>
        </div>

	  	<%
		    LocalDate today = LocalDate.now();
		    LocalDate sixMonthsAgo = today.minusMonths(6);
		%>
		
		<div class="form-group">
		    <label for="fromDate">From Date</label>
		    <input type="date" id="fromDate" name="fromDate"
		           min="<%= sixMonthsAgo %>" max="<%= today %>">
		</div>
		
		<div class="form-group">
		    <label for="toDate">To Date</label>
		    <input type="date" id="toDate" name="toDate"
		           max="<%= today %>">
		</div>

        <div class="form-group full-width">
            <button type="submit" class="btn-submit">Fetch Statement</button>
        </div>
    </form>

    <div id="statementResult" class="statement-result"></div>
</div>

<!-- Link to your logic -->
<script src="${pageContext.request.contextPath}/js/dashboard-statement.js"></script>
