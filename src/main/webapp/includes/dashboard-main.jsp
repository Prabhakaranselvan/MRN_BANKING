<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
 <h2>Welcome to Your Dashboard</h2>
            <p>Select an option from the sidebar to continue.</p>
		    <p>USER ID: <%= session.getAttribute("userId") %></p>
		    <%
			    short userCategory = (short) session.getAttribute("userCategory");
			    String userCategoryName = "";
			
			    switch (userCategory) {
			        case 0:
			            userCategoryName = "Client";
			            break;
			        case 1:
			            userCategoryName = "Employee";
			            break;
			        case 2:
			            userCategoryName = "Manager";
			            break;
			        case 3:
			            userCategoryName = "General Manager";
			            break;
			        default:
			            userCategoryName = "Unknown";
			    }
			%>
			<p>USER CATEGORY: <%= userCategoryName %></p>