<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	    function handleLogout() {
	    	fetch("${pageContext.request.contextPath}/MRNBank/logout", {
	    		method: "POST",
	    		headers: {
	    			"Content-Type": "application/json",
	    			"Method": "POST"
	    		}
	    	})
	    		.then(response => {
	    			// Since we're redirecting from the backend, we just follow it
	    			if (response.redirected) {
	    				window.location.href = response.url;
	    			} else {
	    				// fallback if not redirected
	    				window.location.href = "${pageContext.request.contextPath}/logout.jsp";
	    			}
	    		})
	    		.catch(error => {
	    			console.error("Logout failed", error);
	    			alert("Logout failed. Please try again.");
	    		});
	    }
	
	    function toggleSidebar() {
	                const sidebar = document.getElementById("sidebar");
	                const icon = document.getElementById("toggle-icon");
	                sidebar.classList.toggle("expanded");
	                icon.textContent = sidebar.classList.contains("expanded") ? "close" : "menu";
	            }
	    		
	
	    function loadContent(page) {
	        const container = document.getElementById("main-content");
	        container.innerHTML = "<p>Loading...</p>";
	        
	        fetch(`includes/` + page)
	            .then(response => {
	                if (!response.ok) throw new Error("Network error");
	                return response.text();
	            })
	            .then(html => {
	                container.innerHTML = html;
	
	                // Trigger any needed JS based on page
	                if (page === "dashboard-profile.jsp") {
	                    fetchProfileData();
	                }
	            })
	            .catch(error => {
	                console.error("Error loading content:", error);
	                container.innerHTML = "<p>Failed to load content.</p>";
	            });
	    }
	
	     function fetchProfileData() {
	        const userId = <%= session.getAttribute("userId") %>;
	        console.log("UserID: " + userId);
	
	        fetch("http://localhost:8080/MRN_BANKING/MRNBank/client", {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json",
	                "Method": "GET" // Custom header
	            },
	            body: JSON.stringify({ userId })
	        })
	        .then(response => {
	            if (!response.ok) throw new Error("Failed to fetch profile");
	            return response.json();
	        })
	        .then(data => {
	            const client = data.clients;
	            const container = document.getElementById("profile-details");
	
	            container.innerHTML =
	                "<p><strong>Name:</strong> " + client.name + "</p>" +
	                "<p><strong>Email:</strong> " + client.email + "</p>" +
	                "<p><strong>Phone:</strong> " + client.phoneNo + "</p>" +
	                "<p><strong>DOB:</strong> " + client.dob + "</p>" +
	                "<p><strong>Gender:</strong> " + client.gender + "</p>" +
	                "<p><strong>Aadhar:</strong> " + client.aadhar + "</p>" +
	                "<p><strong>PAN:</strong> " + client.pan + "</p>" +
	                "<p><strong>Address:</strong> " + client.address + "</p>" +
	                "<p><strong>Status:</strong> " + (client.status == 1 ? 'Active' : 'Inactive') + "</p>" +
	                "<p><strong>User ID:</strong> " + client.userId + "</p>";
	
	        })
	        .catch(error => {
	            document.getElementById("profile-details").innerHTML = "<p>Error loading profile.</p>";
	            console.error(error);
	        });
	    } 
    </script>

</body>
</html>
