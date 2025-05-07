package com.mrn.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");		
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
			System.out.println("Module: " + module);

			
			String jsonString;
			try (BufferedReader reader = request.getReader()) 
			{
				jsonString = reader.lines().collect(Collectors.joining());
			}

			Gson gson = new Gson();
			Class<?> pojoClass = ModuleResolver.getPojoClass(module);
			Object pojoInstance = gson.fromJson(jsonString, pojoClass);
			System.out.println("pojo " + pojoClass);


			Class<?> handlerClass =  ModuleResolver.getHandlerClass(module);
			Object handlerInstance = handlerClass.getDeclaredConstructor().newInstance();
			
			HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
			Method handleMethod = handlerClass.getMethod("handle", HttpMethod.class, Object.class);
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = (Map<String, Object>) handleMethod.invoke(handlerInstance, httpMethod, pojoInstance);

			if ("login".equalsIgnoreCase(module)) 
			{
			    createSessionAndCookie(request, response, resultMap);
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
	
	public static void sendResponse(HttpServletResponse response, Map<String, Object> resultMap) throws IOException 
	{
	    Gson gson = new Gson();
	    
	    boolean success = (boolean) resultMap.getOrDefault("success", false);
	    response.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
	    
	    String json = gson.toJson(resultMap);
	    response.getWriter().write(json);
	}
	
	public static void writeJsonError(HttpServletResponse response, String errorMessage) throws IOException {
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
	
	private void createSessionAndCookie(HttpServletRequest request, HttpServletResponse response, Map<String, Object> resultMap) 
	{
	    boolean success = (boolean) resultMap.getOrDefault("success", false);

	    if (success) 
	    {
	        HttpSession session = request.getSession(true); // creates session if not exists
	        session.setAttribute("userId", resultMap.get("userId"));
	        session.setAttribute("userCategory", resultMap.get("userCategory"));
	        session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout

//	        // Create a JSESSIONID cookie with appropriate properties
//	        Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
//	        sessionCookie.setMaxAge(30 * 60);           // 30 minutes
//	        sessionCookie.setHttpOnly(true);            // prevent access via JS
//	        //sessionCookie.setPath("/");                 // available across the app
//	        sessionCookie.setSecure(true);              // send only over HTTPS
//	        sessionCookie.setComment("Session tracking cookie");
//
//	        response.addCookie(sessionCookie);          // attach to response
	    }
	}



}
