package com.mrn.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter 
{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

		String path = req.getPathInfo();
		String exclude = ".*/(login|signup|logout)$";
		if (path != null && path.matches(exclude)) 
		{
			chain.doFilter(request, response); // Allow login requests
			return;
		}

		HttpSession session = req.getSession(false);
		// Block other requests without session
		if (session == null || session.getAttribute("userId") == null) 
		{
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().write("{\"error\": \"User not logged in or session expired\"}");
			return;
		}

		chain.doFilter(request, response); // continue if valid session
	}
}
