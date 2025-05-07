package com.mrn.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mrn.utilshub.YamlLoader;

public class AuthorizationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String path = req.getPathInfo();
		if (path != null && path.endsWith("/login")) 
		{
			chain.doFilter(request, response); // Allow login requests
			return;
		}
		String method = req.getMethod();
		String userRole = (String) req.getSession().getAttribute("userCategory");
		String actionTag = (String) req.getAttribute("actionTag");

		if (userRole == null) {
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing user role.");
			return;
		}

		// Authorization check using YamlLoader
		boolean authorized = YamlLoader.isAllowed(path, method, userRole, actionTag);

		if (!authorized) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.getWriter().write("{\"error\":\"Forbidden - Access denied for your role - " + userRole + "\"}");
			return;
		}
		
		// Authorized, proceed with the request
		chain.doFilter(request, response);
	}
}
