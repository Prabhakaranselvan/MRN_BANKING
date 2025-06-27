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
import javax.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter 
{
	 private static final Pattern EXCLUDED_PATHS = Pattern.compile("^/(login|signup)$");
	 
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        System.out.println("[AuthFilter] Incoming request path: " + path);
        if (path == null || path.trim().isEmpty()) {
            writeJsonError(res, HttpServletResponse.SC_BAD_REQUEST, "Missing request path.");
            return;
        }
        
        String[] parts = path.split("/");
		String module = (parts.length >= 2) ? parts[1] : null;
		if (module == null || module.isEmpty()) {
            writeJsonError(res, HttpServletResponse.SC_BAD_REQUEST, "Invalid URL path.");
            return;
        }
		
		HttpSession session = req.getSession(false);
		System.out.println("[AuthFilter] Session exists: " + (session != null));
		
		if (EXCLUDED_PATHS.matcher(path).matches()) {
            if (session == null) {
            	System.out.println("[AuthFilter] No session found. Allowing " + path);
                chain.doFilter(request, response); // Allow login/signup
            } else {
                writeJsonError(res, HttpServletResponse.SC_BAD_REQUEST, "Clear (or) logout the existing session to proceed.");
            }
            return;
        }

		// Block other requests without session
		if (session == null || session.getAttribute("userId") == null) {
            writeJsonError(res, HttpServletResponse.SC_UNAUTHORIZED, "User not logged in (or) session expired.");
            return;
        }

		System.out.println("[AuthFilter] Valid session found. Proceeding to Authorization.");
		chain.doFilter(request, response); // continue if valid session
	}
	
	private void writeJsonError(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
