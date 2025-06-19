<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>
<%
    Long userId = (Long) ses.getAttribute("userId");
    Short userCategory = (Short) ses.getAttribute("userCategory");

    String userCategoryName = "Unknown";
    if (userCategory != null) {
        switch (userCategory) {
            case 0: userCategoryName = "Client"; break;
            case 1: userCategoryName = "Employee"; break;
            case 2: userCategoryName = "Manager"; break;
            case 3: userCategoryName = "General Manager"; break;
        }
    }
%>
<h2>Welcome to Your Dashboard</h2>
<p>Select an option from the sidebar to continue.</p>

<p>USER ID: <%= userId %></p>
<p>USER CATEGORY: <%= userCategoryName %></p>
