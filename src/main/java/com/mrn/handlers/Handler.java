package com.mrn.handlers;

import java.util.Map;

import com.mrn.enums.HttpMethod;
import com.mrn.exception.InvalidException;;

public abstract class Handler {

	// Final method to handle request based on HttpMethod
	public final Map<String, Object> handle(HttpMethod httpMethod, Object pojoInstance) throws InvalidException 
	{
		switch (httpMethod) 
		{
		case GET:
			return handleGet(pojoInstance);
			
		case POST:
			return handlePost(pojoInstance);
			
		case PUT:
			return handlePut(pojoInstance);
			
		default:
			throw new InvalidException("Unsupported HTTP method: " + httpMethod);
		}
	}

	// Abstract methods: must be implemented by subclasses
	protected abstract Map<String, Object> handleGet(Object pojoInstance) throws InvalidException;

	protected abstract Map<String, Object> handlePost(Object pojoInstance) throws InvalidException;

	protected abstract Map<String, Object> handlePut(Object pojoInstance) throws InvalidException;

}
