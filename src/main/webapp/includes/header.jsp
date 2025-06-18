<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<header class="banner">
    <div class="logo-container">
        <img src="${pageContext.request.contextPath}/images/Logo_BG_Removed.png" alt="Company Logo" class="logo" />
        <img src="${pageContext.request.contextPath}/images/Name_BG_Removed_Crop.png" alt="Company Name" class="bank-name" />
    </div>

    <div class="banner-buttons">
        <!-- Conditionally show Sign Up -->
        <c:if test="${showSignUp eq true}">
            <a href="${pageContext.request.contextPath}/signup.jsp" class="banner-text-button">Sign Up</a>
        </c:if>

        <!-- Conditionally show Login -->
        <c:if test="${showLogin eq true}">
            <a href="${pageContext.request.contextPath}/login.jsp" class="banner-text-button">Login</a>
        </c:if>

        <!-- Conditionally show Notifications -->
        <c:if test="${showNotifications eq true}">
            <button class="banner-button" title="Notifications">
                <span class="material-symbols-outlined">notifications</span>
            </button>
        </c:if>
        
        <!-- Conditionally show Profile -->
        <c:if test="${showProfile eq true}">
            <button class="banner-button" title="Profile">
                <span class="material-symbols-outlined">identity_platform</span>
            </button>
        </c:if>
        
        <!-- Conditionally show Home -->
        <c:if test="${showHome eq true}">
            <a href="#" class="banner-button" title="Home">
                <span class="material-symbols-outlined">home_app_logo</span>
            </a>
        </c:if>
        
        <c:if test="${showLogout eq true}">
	        <button type="button" class="banner-button" onclick="handleLogout()">
			    <span class="material-icons">logout</span><span class="link-text">Sign Out</span>
			</button>
		</c:if>
        
    </div>
</header>