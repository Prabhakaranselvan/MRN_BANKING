package com.mrn.filters;

import java.io.IOException;
import java.util.regex.Pattern;

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
	private static final Pattern EXCLUDED_PATHS = Pattern.compile(".*/(login|signup|logout)$");
	
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
		if (path != null && EXCLUDED_PATHS.matcher(path).matches()) {
		    chain.doFilter(request, response); // Allow login signup and logout requests
		    return;
		}
		
		String method = req.getMethod();
		String headerMethod = req.getHeader("Method");
		if (headerMethod == null || headerMethod.isBlank()) 
		{
		    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    res.getWriter().write("{\"error\": \"Missing required header: Method\"}");
		    return;
		}

		short userRole = (short) req.getSession().getAttribute("userCategory");

		// Authorization check using YamlLoader
		boolean authorized = YamlLoader.isAllowed(path, headerMethod, method, userRole);

		if (!authorized) 
		{
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.getWriter().write("{\"error\":\"Access denied: Your role (" + userRole + ") is not permitted to access " + path + "\"}");
			return;
		}
		
		// Authorized, proceed with the request
		chain.doFilter(request, response);
	}
}
