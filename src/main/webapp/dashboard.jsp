<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%
    HttpSession session = request.getSession(false); // Do not create a new session
    if (session == null || session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    
    String userName = (String) session.getAttribute("name");
    String userEmail = (String) session.getAttribute("email");
    Long userId = (Long) session.getAttribute("userId");
    Short userRole = (Short) session.getAttribute("userCategory");
    Long branchId =(Long) session.getAttribute("branchId");
    
    request.setAttribute("userId", userId);
    request.setAttribute("userName", userName);
    request.setAttribute("userRole", userRole);
    request.setAttribute("userEmail", userEmail);
    request.setAttribute("showProfile", true);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>

    <%@ include file="/includes/head-resources.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-profile.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-addUser.css" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
</head>

<body  data-user-id="<%= userId %>" data-user-role="<%= userRole %>" data-branch-id="<%= branchId %>" data-user-name="<%= userName %>">

    <%@ include file="/includes/header.jsp" %>

<!-- Sidebar -->
	<aside class="sidebar" id="sidebar">
    	<%-- <div class="brand">
        	<img src="${pageContext.request.contextPath}/images/Logo_BG_Removed.png" alt="Company Logo" class="logo-icon" />
            <span class="brand-name">MRN Bank</span>
        </div> --%>
        <nav class="nav-links">
            <%
            	if (userRole == 0 || userRole == 2 || userRole == 3) {
			    String targetPage = (userRole == 0) ? "dashboard-main.jsp" : "dashboard-admin.jsp";
			%>
			<a href="#" onclick="loadContent('<%= targetPage %>'); return false;">
			    <span class="material-icons">dashboard</span>
			    <span class="link-text">Dashboard</span>
			</a>
           	<%
            	}
           		if (userRole == 1 || userRole == 2 || userRole == 3) {
			%>
		    <a href="#" onclick="loadContent('dashboard-clients.jsp'); return false;">
		        <span class="material-icons">group</span>
		        <span class="link-text">Clients</span>
		    </a>
			<%
			    } 
           		if (userRole == 2 || userRole == 3) {
   			%>
   			    <a href="#" onclick="loadContent('dashboard-employee.jsp'); return false;" class="employee-link">
   			        <span class="material-icons">engineering</span>
   			        <span class="link-text">Employees</span>
   			    </a>
   			    <a href="#" onclick="loadContent('dashboard-request.jsp'); return false;" class="dashboard-btn">
				  <span class="material-icons">assignment</span>
				  <span class="link-text">Account Requests</span>
				</a>
   			<%
   			    }
          		if (userRole == 0) {
			%>
            <a href="#" onclick="loadContent('dashboard-client-accounts.jsp'); return false;">
			    <span class="material-icons">account_balance</span>
			    <span class="link-text">My Accounts</span>
			</a>
			<%
			    } else {
    		%>
    		    <a href="#" onclick="loadContent('dashboard-accounts.jsp'); return false;">
				    <span class="material-icons">account_balance</span>
				    <span class="link-text">Accounts</span>
				</a>
   			<%
			    }
			%>
			
			<a href="#" onclick="loadContent('dashboard-statement.jsp'); return false;">
			    <span class="material-icons">receipt_long</span>
			    <span class="link-text">Account Statement</span>
			</a>
			<a href="#" onclick="loadContent('dashboard-transaction.jsp'); return false;">
			    <span class="material-icons">sync_alt</span>
			    <span class="link-text">Money Transfer</span>
			</a>
			<a href="#" onclick="loadContent('dashboard-branch.jsp'); return false;">
			  <span class="material-icons">business</span>
			  <span class="link-text">Branch Directory</span>
			</a>
        </nav>

        <button class="toggle-btn" onclick="toggleSidebar()">
        	<span class="material-icons" id="toggle-icon">menu</span>
		</button>
    </aside>
    
    <div class="dashboard-wrapper">
        <!-- Main Content -->
        <main class="dashboard-content"  id="main-content"></main>
     
        <%@ include file="/includes/footer.jsp" %>
      	<%@ include file="/includes/dialog-box.jsp"%>
    </div> 
 
    <div id="modal-root"></div>
    
	<script>
	    window.appContext = "${pageContext.request.contextPath}";
	    
	 // Prevent navigating back to login/signup pages after login
	    if (window.history && window.history.pushState) {
	    window.history.pushState(null, "", location.href);
	    window.onpopstate = function () {
	        window.history.pushState(null, "", location.href);
	    };
	}
	
	    
	    function closeDropdown() {
	        const dropdown = document.querySelector(".zoho-style-dropdown");
	        dropdown?.classList.remove("show");
	    }
	
	    const userRole = parseInt(document.body.getAttribute("data-user-role"));
	    document.addEventListener("DOMContentLoaded", function () {
	    	if (userRole === 0) {
	   			 loadContent("dashboard-main.jsp");
	   		}
	    	else if (userRole === 1) {
	   			loadContent("dashboard-accounts.jsp")
	   		}
	    	else {
	    		loadContent("dashboard-admin.jsp");
				}
	    	
	
	        // Profile dropdown toggle
	        const profileBtn = document.querySelector(".profile-icon-btn");
	        const dropdown = document.querySelector(".zoho-style-dropdown");
	        const closeBtn = document.querySelector(".close-btn");
	
	        function toggleDropdown() {
	            dropdown.classList.toggle("show");
	        }
	
	        function closeDropdown() {
	            dropdown.classList.remove("show");
	        }
	
	        // Toggle on button click
	        profileBtn?.addEventListener("click", (e) => {
	            e.stopPropagation();
	            toggleDropdown();
	        });
	
	        // Close on outside click
	        document.addEventListener("click", (e) => {
	            if (!dropdown.contains(e.target) && !profileBtn.contains(e.target)) {
	                closeDropdown();
	            }
	        });
	
	        // Close on ✕ button
	        closeBtn?.addEventListener("click", (e) => {
	            e.stopPropagation();
	            closeDropdown();
	        });
	    });
	</script>
	
	<!-- Scripts -->
	<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels"></script>
</body>
</html>
