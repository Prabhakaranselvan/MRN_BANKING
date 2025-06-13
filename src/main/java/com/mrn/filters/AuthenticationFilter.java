package com.mrn.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mrn.utilshub.YamlLoader;

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
		String[] parts = (path != null) ? path.split("/") : new String[0];
		
		String module = (parts.length >= 2) ? parts[1] : null;

		if (module == null || module.isEmpty()) 
		{
		    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		    res.getWriter().write("{\"error\": \"Invalid URL path\"}");
		    return;
		}
		
		String endpoint = "/" + module;
		List<String> validEndpoints = YamlLoader.loadEndpoints();

		if (!validEndpoints.contains(endpoint)) 
		{
		    res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    res.getWriter().write("{\"error\": \"Invalid endpoint: " + endpoint +"\"}");
		    return;
		}
		
		HttpSession session = req.getSession(false);
		String exclude = ".*/(login|signup)$";
		System.out.println(session);
		System.out.println(path);
		System.out.println(path.matches(exclude));
		System.out.println(session == null && path.matches(exclude));
		if (session == null && path.matches(exclude)) 
		{
			chain.doFilter(request, response); // Allow login & signup requests
			return;
		}

		// Block other requests without session
		System.out.println(session.getAttribute("userId") == null);
		if (session == null || session.getAttribute("userId") == null) 
		{
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().write("{\"error\": \"User not logged in or session expired\"}");
			return;
		}

		chain.doFilter(request, response); // continue if valid session
	}
}
