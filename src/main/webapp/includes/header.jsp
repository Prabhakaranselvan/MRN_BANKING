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

