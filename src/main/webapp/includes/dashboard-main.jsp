<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="dashboard-main">
  <!-- Welcome, Balance, and Transfer -->
  <div class="dashboard-header">
    <div class="header-left">
      <h2></h2>
      <div class="total-balance">
        <span class="material-icons balance-icon">account_balance_wallet</span>
        <span id="totalBalance">₹0.00</span>
      </div>
    </div>
    <div class="header-right">
      <button class="transfer-button" onclick="loadContent('dashboard-transaction.jsp')">
        <span class="material-icons">send</span>
        Transfer Money
      </button>
    </div>
  </div>

  <!-- Grid Layout: Accounts & Chart -->
  <div class="dashboard-grid">
    <!-- Accounts Overview -->
    <div class="section-box">
      <div class="section-header">
        <h4>Accounts</h4>
        <div class="section-header-inner">
         <select id="accountSelectDropdown" class="account-select-dropdown">
	      <option value="">-- All Accounts --</option>
	    </select>
        <span class="material-icons clickable" onclick="loadContent('dashboard-client-accounts.jsp')" 
        	title ="Accounts">open_in_new</span>
        </div>
      </div>
      <div id="accountCards" class="card-container"></div>
    </div>

    <!-- Balance Chart -->
    <div class="section-box">
      <div class="section-header">
        <h4 id="chartTitle">Balance Distribution</h4>
      </div>
      <canvas id="balancePieChart" height="200"></canvas>
    </div>
  </div>

  <!-- Transactions -->
  <div class="section-box">
    <div class="section-header">
      <h4>Latest Transactions</h4>
      <span class="material-icons clickable" onclick="loadContent('dashboard-statement.jsp')" 
      	title ="Account Statement">open_in_new</span>
    </div>
    <div id="modernTxnList" class="txn-list">
      <p>Loading...</p>
    </div>
  </div>
</div>
