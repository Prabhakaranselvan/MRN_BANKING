<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%
    HttpSession session = request.getSession(false); // Do not create a new session

    // Redirect to login if session is missing
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>

    <%@ include file="/includes/head-resources.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-profile.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-accounts.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-statement.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-transaction.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>

<body  data-user-id="<%= session.getAttribute("userId") %>">
    <%
        request.setAttribute("showLogout", true);
        request.setAttribute("showNotifications", true);
        request.setAttribute("showHome", true);
    %>

    <%@ include file="/includes/header.jsp" %>

<!-- Sidebar -->
	<aside class="sidebar" id="sidebar">
    	<div class="brand">
        	<img src="${pageContext.request.contextPath}/images/Logo_BG_Removed.png" alt="Company Logo" class="logo-icon" />
            <span class="brand-name">MRN Bank</span>
        </div>
        <nav class="nav-links">
            <a href="#" onclick="loadContent('dashboard-main.jsp'); return false;">
            	<span class="material-icons">dashboard</span>
            	<span class="link-text">Dashboard</span>
           	</a>
            <a href="#" onclick="loadContent('dashboard-profile.jsp'); return false;">
            	<span class="material-icons">person</span>
            	<span class="link-text">My Profile</span>
           	</a>
            <a href="#" onclick="loadContent('dashboard-accounts.jsp'); return false;">
			    <span class="material-icons">account_balance</span>
			    <span class="link-text">My Accounts</span>
			</a>
			<a href="#" onclick="loadContent('dashboard-statement.jsp'); return false;">
			    <span class="material-icons">receipt_long</span>
			    <span class="link-text">Account Statement</span>
			</a>
			<a href="#" onclick="loadContent('dashboard-transaction.jsp'); return false;">
			    <span class="material-icons">sync_alt</span>
			    <span class="link-text">Money Transfer</span>
			</a>
            <a href="#"><span class="material-icons">help</span><span class="link-text">Help</span></a>
            <a href="#"><span class="material-icons">settings</span><span class="link-text">Settings</span></a>
            <a href="#"><span class="material-icons">lock</span><span class="link-text">Password</span></a>
        </nav>

        <button class="toggle-btn" onclick="toggleSidebar()">
		    <span class="material-icons" id="toggle-icon">menu</span>
		</button>

    </aside>
    
    <div class="dashboard-wrapper">
        <!-- Main Content -->
        <main class="dashboard-content"  id="main-content">
           <%@ include file="/includes/dashboard-main.jsp" %>
        </main>
        
        <%@ include file="/includes/footer.jsp" %>
      	<%@ include file="/includes/dialog-box.jsp"%>
      	
    </div> 
    <script>
	    window.appContext = "${pageContext.request.contextPath}";
	    document.addEventListener("DOMContentLoaded", function () {
	        // Mark Dashboard link as active on initial load
	        const dashboardLink = [...document.querySelectorAll('.nav-links a')].find(a =>
	            a.getAttribute('onclick')?.includes('dashboard-main.jsp')
	        );
	        if (dashboardLink) {
	            dashboardLink.classList.add('active');
	        }
	    });

	</script> 
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
   
    

</body>
</html>
