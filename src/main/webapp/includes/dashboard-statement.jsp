<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>
<%@ page import="java.time.LocalDate" %>

<%
LocalDate today = LocalDate.now();
LocalDate sixMonthsAgo = today.minusMonths(6);
%>

<div class="container">
	<div class="statement-container">
			<form id="statementForm" class="statement-form">
				<h2 class="form-header">ACCOUNT STATEMENT</h2>
				<div class="statement-inputs">
					<div class="form-group" id="accountDropdownGroup">
						<label for="accountSelect">Select Account</label> <select
							id="accountSelect" class="dropdown-select">
							<option value="">All Accounts</option>
						</select>
					</div>

					<!-- Dynamic Inputs for Employee/Manager/GM -->
					<div class="form-group" id="accountInputGroup"
						style="display: none; flex-direction: column;">
						<label for="accountInput">Account No</label> <input type="text"
							id="accountInput" name="accountNo"
							placeholder="Account No (11 digits)" maxlength="11"
							class="form-control" />
					</div>

					<div class="form-group" id="clientInputGroup"
						style="display: none; flex-direction: column;">
						<label for="clientInput">Client ID</label> <input type="text"
							id="clientInput" name="clientId" placeholder="Client ID"
							maxlength="6" class="form-control" />
					</div>

					<div class="form-group">
						<label for="fromDate">From Date</label> <input type="date"
							id="fromDate" name="fromDate" min="<%=sixMonthsAgo%>"
							max="<%=today%>">
					</div>

					<div class="form-group">
						<label for="toDate">To Date</label> <input type="date" id="toDate"
							name="toDate" min="<%=sixMonthsAgo%>" max="<%=today%>">
					</div>
				</div>
				<div class="fetch-button">
				<button type="submit" class="btn-submit">Fetch Statement</button>
				</div>
			</form>
		<div id="paginationWrapper" class="pagination-bar">
			<button id="prevPage" class="page-btn" title="Previous">
				<span class="material-icons">chevron_left</span>
			</button>
			<button id="nextPage" class="page-btn" title="Next">
				<span class="material-icons">chevron_right</span>
			</button>
		</div>

		<div id="statementResult" class="statement-result"></div>
	</div>
</div>

