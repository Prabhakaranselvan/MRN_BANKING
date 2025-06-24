<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<header class="banner">
    <div class="logo-container">
        <img src="${pageContext.request.contextPath}/images/Logo_BG_Removed.png" alt="Company Logo" class="logo" />
        <img src="${pageContext.request.contextPath}/images/Name_BG_Removed_Crop.png" alt="Company Name" class="bank-name" />
    </div>

    <div class="banner-buttons">
    
       <c:if test="${showProfile eq true}">
		    <div class="profile-dropdown">
			    <button class="profile-icon-btn" title="Profile">
			        <span class="material-symbols-outlined">account_circle</span>
			    </button>
			    <div class="zoho-style-dropdown">
			        <div class="profile-header">
			            <div class="profile-image">
			                <span class="material-symbols-outlined">person</span>
			            </div>
			            <div class="profile-info">
			                <div class="user-name">${userName}</div>
			                <div class="user-email">${userEmail}</div>
			                <div class="user-id">User Id: ${userId} <span class="material-symbols-outlined help-icon" title="Unique internal ID">help</span></div>
			                <div class="user-org">User Role: 
				                <strong><c:choose>
								            <c:when test="${userRole == 0}">Client</c:when>
								            <c:when test="${userRole == 1}">Employee</c:when>
								            <c:when test="${userRole == 2}">Manager</c:when>
								            <c:when test="${userRole == 3}">General Manager</c:when>
								            <c:otherwise>Unknown</c:otherwise>
							        	</c:choose>
					        	</strong>
				        	</div>
			            </div>
			            <button class="close-btn" onclick="closeDropdown()">✕</button>
			        </div>
			
			        <hr class="divider" />
			
			        <div class="profile-actions">
			            <a href="#" onclick="loadContent('dashboard-profile.jsp'); closeDropdown(); return false;" class="account-link">My Profile</a>
			            <a href="#" class="signout-link" onclick="handleLogout()">Sign Out</a>
			        </div>
			    </div>
			</div>

		</c:if>

        <!-- Conditionally show Home -->
        <c:if test="${showHome eq true}">
            <a href="#" class="banner-button" title="Home">
                <span class="material-symbols-outlined">home_app_logo</span>
            </a>
        </c:if>
        
         <!-- Conditionally show Login -->
        <c:if test="${showLogin eq true}">
            <a href="${pageContext.request.contextPath}/login.jsp" class="banner-text-button">Login</a>
        </c:if>
        
    </div>
</header>

<style>
/* --- Dropdown Container --- */
.zoho-style-dropdown {
    display: none;
    position: absolute;
    top: 55px;
    right: 10px;
    width: 360px;
    background-color: #fff;
    color: #1c1c1c;
    border-radius: 12px;
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.15);
    font-family: 'Segoe UI', sans-serif;
    z-index: 1000;
    padding: 16px;
    box-sizing: border-box;
    opacity: 0;
    transform: translateY(-10px);
    transition: opacity 0.2s ease, transform 0.2s ease;
}

/* --- Header Section --- */
.profile-header {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    position: relative;
}

.profile-image {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    background-color: #f1f1f1;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    color: #666;
}

.profile-info {
    flex: 1;
    font-size: 14px;
    line-height: 1.4;
}

.user-name {
    font-weight: 600;
    font-size: 15px;
    color: #202124;
}

.user-email {
    color: #5f6368;
    margin: 2px 0;
    font-size: 13px;
}

.user-id,
.user-org {
    color: #5f6368;
    font-size: 13px;
    margin-top: 2px;
}

.user-id {
	display: flex;
	gap: 4px;
} 


.user-org strong {
    color: #202124;
}

.help-icon {
    font-size: 15px;
    vertical-align: middle;
    cursor: pointer;
    color: #aaa;
}

/* --- Close Button --- */
.close-btn {
    position: absolute;
    top: 0;
    right: 0;
    background: none;
    border: none;
    font-size: 18px;
    color: #888;
    cursor: pointer;
}

.close-btn:hover {
    color: #000;
}

/* --- Divider --- */
.divider {
    margin: 16px 0 12px;
    border: none;
    border-top: 1px solid #e0e0e0;
}

/* --- Footer Links --- */
.profile-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
}

.account-link {
    color: #1a73e8;
    text-decoration: none;
    font-weight: 500;
}

.account-link:hover {
    text-decoration: underline;
}

.signout-link {
    color: #d93025;
    text-decoration: none;
    font-weight: 500;
}

.signout-link:hover {
    text-decoration: underline;
}

/* --- Trigger Button --- */
.profile-icon-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 0;
}

.profile-icon-btn .material-symbols-outlined {
    font-size: 40px;
    color: white;
    background: black;
    border-radius: 50%;
}

/* --- Show dropdown (with fade) --- */
.zoho-style-dropdown.show {
    display: block;
    opacity: 1;
    transform: translateY(0);
}
</style>
