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
	private static final Pattern EXCLUDED_PATHS = Pattern.compile("^/(login|signup|logout)$");
	
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
		
		String method = req.getMethod();
		String headerMethod = req.getHeader("Method");
		if (headerMethod == null || headerMethod.isBlank()) 
		{
			writeJsonError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing required header: Method");
		    return;
		}
		
		if (path != null && EXCLUDED_PATHS.matcher(path).matches()) {
			System.out.println("[AuthzFilter] " + path + " is excluded . Proceeding To Servlet.\n");
		    chain.doFilter(request, response); // Allow login signup and logout requests
		    return;
		}
		

		short userRole = (short) req.getSession().getAttribute("userCategory");

		// Authorization check using YamlLoader
		System.out.println("[AuthzFilter] Checking access for path: " + path 
                + ", headerMethod: " + headerMethod 
                + ", method: " + method 
                + ", role: " + userRole);
		boolean authorized = YamlLoader.isAllowed(path, headerMethod, method, userRole);

		if (!authorized) 
		{
			writeJsonError(res, HttpServletResponse.SC_FORBIDDEN, "Access denied: Your role (" + userRole + ") is not permitted to access " + path);
			return;
		}
		
		// Authorized, proceed with the request
		System.out.println("[AuthzFilter] Proceeding to Servlet.\n");
		chain.doFilter(request, response);
	}
	
	private void writeJsonError(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
