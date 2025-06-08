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

public class AuthorizationFilter implements Filter 
{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String path = req.getPathInfo();
		String query = req.getQueryString();
		if (query != null) {
		    path += "?" + query;  // Append query parameters to the path
		}
		String exclude = ".*/(login|signup|logout)$";
		if (path != null && path.matches(exclude)) 
		{
			chain.doFilter(request, response); // Allow login signup and logout requests
			return;
		}
		
		String method = req.getMethod();
		String headerMethod = req.getHeader("Method");
		short userRole = (short) req.getSession().getAttribute("userCategory");

		// Authorization check using YamlLoader
		boolean authorized = YamlLoader.isAllowed(path, headerMethod, method, userRole);

		if (!authorized) 
		{
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.getWriter().write("{\"error\":\"Forbidden - Access denied for your role - " + userRole + "\"}");
			return;
		}
		
		// Authorized, proceed with the request
		chain.doFilter(request, response);
	}
}
