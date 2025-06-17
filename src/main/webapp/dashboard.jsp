<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>

    <%@ include file="/includes/head-resources.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>

<body>
    <%
        request.setAttribute("showProfile", true);
        request.setAttribute("showNotifications", true);
        request.setAttribute("showHome", true);
    %>

    <%@ include file="/includes/header.jsp" %>

    <div class="dashboard-wrapper">
        <!-- Sidebar -->
        <aside class="sidebar" id="sidebar">
            <div class="brand">
            	<img src="${pageContext.request.contextPath}/images/Logo_BG_Removed.png" alt="Company Logo" class="logo-icon" />
                <span class="brand-name">MRN Bank</span>
            </div>
            <nav class="nav-links">
                <a href="#"><span class="material-icons">dashboard</span><span class="link-text">Dashboard</span></a>
                <a href="#"><span class="material-icons">person</span><span class="link-text">My Profile</span></a>
                <a href="#"><span class="material-icons">account_balance</span><span class="link-text">My Accounts</span></a>
                <a href="#"><span class="material-icons">receipt_long</span><span class="link-text">Account Statement</span></a>
                <a href="#"><span class="material-icons">sync_alt</span><span class="link-text">Money Transfer</span></a>
                <a href="#"><span class="material-icons">help</span><span class="link-text">Help</span></a>
                <a href="#"><span class="material-icons">settings</span><span class="link-text">Settings</span></a>
                <a href="#"><span class="material-icons">lock</span><span class="link-text">Password</span></a>
                <a href="${pageContext.request.contextPath}/MRNBank/logout" class="logout">
                    <span class="material-icons">logout</span><span class="link-text">Sign Out</span>
                </a>
            </nav>

            <button class="toggle-btn" onclick="toggleSidebar()">
			    <span class="material-icons" id="toggle-icon">menu</span>
			</button>

        </aside>

        <!-- Main Content -->
        <main class="dashboard-content">
            <h2>Welcome to Your Dashboard</h2>
            <p>Select an option from the sidebar to continue.</p>
        </main>
    </div>
    

	<%@ include file="/includes/dialog-box.jsp"%>
    <%@ include file="/includes/footer.jsp" %>

   <script>
    function toggleSidebar() {
        const sidebar = document.getElementById("sidebar");
        const icon = document.getElementById("toggle-icon");
        
        sidebar.classList.toggle("expanded");
        
        // Change icon based on sidebar state
        if (sidebar.classList.contains("expanded")) {
            icon.textContent = "close";
        } else {
            icon.textContent = "menu";
        }
    }
</script>

</body>
</html>
