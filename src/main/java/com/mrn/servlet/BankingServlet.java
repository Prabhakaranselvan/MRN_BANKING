package com.mrn.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.mrn.enums.HttpMethod;
import com.mrn.exception.InvalidException;
import com.mrn.utilshub.ModuleResolver;

public class BankingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{	
		try 
		{
			String path = request.getPathInfo();
			String[] parts = (path != null) ? path.split("/") : new String[0];
			if (parts.length < 2 || parts[1] == null || parts[1].isEmpty()) 
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writeJsonError(response, "Invalid module in URL path");
				return;
			}

			String module = parts[1];	

			if ("logout".equalsIgnoreCase(module)) 
			{
				handleLogout(request, response);
				return;
			}
			
			String jsonString;
			try (BufferedReader reader = request.getReader()) 
			{
				jsonString = reader.lines().collect(Collectors.joining());
			}

			Gson gson = new Gson();
			Class<?> pojoClass = ModuleResolver.getPojoClass(module);
			Object pojoInstance = gson.fromJson(jsonString, pojoClass);

			Class<?> handlerClass =  ModuleResolver.getHandlerClass(module);
			Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();
			
			String method = ModuleResolver.getMethodName(request.getMethod());
			Map<String, Object> attributeMap = getSessionAttributes(request);
			Method handleMethod = handlerClass.getMethod(method, Object.class , Map.class);
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (Map<String, Object>) handleMethod.invoke(handlerInstance, pojoInstance, attributeMap);

			if ("login".equalsIgnoreCase(module)) 
			{
			    createSession(request, response, resultMap);
			}

			sendResponse(response, resultMap);
		} 
		catch (InvocationTargetException e) 
		{
			Throwable cause = e.getCause();
			if (cause instanceof InvalidException) 
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writeJsonError(response, cause.getMessage());
			} 
			else 
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writeJsonError(response, "Unhandled error in method execution");
			}
			e.printStackTrace();
		} 
		catch (Exception e) 
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeJsonError(response, "Unexpected server error");
			e.printStackTrace();
		}
	}
	
	private void sendResponse(HttpServletResponse response, Map<String, Object> resultMap) throws IOException 
	{
	    Gson gson = new Gson();
	    
	    boolean success = (boolean) resultMap.getOrDefault("success", false);
	    response.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
	    
	    String json = gson.toJson(resultMap);
	    response.getWriter().write(json);
	}
	
	private void writeJsonError(HttpServletResponse response, String errorMessage) throws IOException 
	{
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
	
	private void createSession(HttpServletRequest request, HttpServletResponse response, Map<String, Object> resultMap) 
	{
        HttpSession session = request.getSession(true); // creates session if not exists
        
        // Define keys you want to set as session attributes
        List<String> sessionKeys = new ArrayList<>(Arrays.asList("userId", "userCategory", "branchId"));

        // Loop through and set attributes
        for (String key : sessionKeys) 
        {
            Object value = resultMap.get(key);
            if (value != null) 
            {
                session.setAttribute(key, value);
            }
        }
        
        session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout
	}
	
	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		HttpSession session = request.getSession(false);
		if (session != null) 
		{
			session.invalidate();
		}

		// Invalidate the JSESSIONID cookie
		Cookie cookie = new Cookie("JSESSIONID", "");
		cookie.setMaxAge(0); // Expire immediately
		cookie.setPath(request.getContextPath()); // Must match the cookie path
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		response.addCookie(cookie);

		// Send JSON response
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("{\"message\": \"Logged out successfully\"}");
	}
	
	 public static Map<String, Object> getSessionAttributes(HttpServletRequest request) 
	 {
	        HttpSession session = request.getSession(false); // don't create if it doesn't exist
	        Map<String, Object> attributeMap = new HashMap<>();

	        if (session != null) 
	        {
	            Enumeration<String> attributeNames = session.getAttributeNames();
	            while (attributeNames.hasMoreElements()) 
	            {
	                String name = attributeNames.nextElement();
	                Object value = session.getAttribute(name);
	                attributeMap.put(name, value);
	            }
	        }

	        return attributeMap;
	    }

}
