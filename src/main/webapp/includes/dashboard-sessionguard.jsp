<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%
    HttpSession ses = request.getSession(false);
    if (ses == null || ses.getAttribute("userId") == null) 
    {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Session expired. Please log in again.\"}");
        return;
    }
%>