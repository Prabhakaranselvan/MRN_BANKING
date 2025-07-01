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
			                <div class="user-role">User Role: 
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
			            <a href="#" onclick="openProfileModal(); closeDropdown(); return false;" class="profile-link">My Profile</a>
			            <a href="#" class="signout-link" onclick="handleLogout()">Sign Out</a>
			        </div>
			    </div>
			</div>

		</c:if>
        
    </div>
</header>

<div id="logoutConfirmModal" class="logout-modal hidden">
  <div class="logout-modal-content">
    <div class="logout-modal-icon">
      <span class="material-icons logout-icon">logout</span>
    </div>
    <h3 class="logout-modal-title">Sign Out?</h3>
    <p class="logout-modal-message">Are you sure you want to sign out of <strong>MRN Bank</strong>?</p>
    <div class="logout-modal-actions">
      <button class="logout-btn logout-cancel-btn" onclick="closeLogoutModal()">Cancel</button>
      <button class="logout-btn logout-confirm-btn" onclick="confirmLogout()">Sign Out</button>
    </div>
  </div>
</div>


<style>
.logout-modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  font-family: 'Segoe UI', sans-serif;
}

.logout-modal.hidden {
  display: none;
}

.logout-modal-content {
  background: #fff;
  padding: 30px;
  border-radius: 16px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.15);
  text-align: center;
  position: relative;
}

.logout-modal-icon {
  color: #f44336;
  margin-bottom: 10px;
}

.logout-icon {
  font-weight: bold;
  font-size: 40px !important;
}


.logout-modal-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a237e;
  margin: 10px 0;
}

.logout-modal-message {
  font-size: 15px;
  color: #555;
  margin-bottom: 25px;
}

.logout-modal-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.logout-btn {
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.logout-cancel-btn:hover {
  background-color: #e0e0e0;
}

.logout-confirm-btn {
  background-color: #d32f2f;
  color: white;
}

.logout-confirm-btn:hover {
  background-color: #b71c1c;
}


</style>

