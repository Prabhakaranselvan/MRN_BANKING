package com.mrn.handlers;

import java.util.Map;

import com.mrn.exception.InvalidException;;

public abstract class Handler 
{
	// Abstract methods: must be implemented by subclasses
	protected abstract Map<String, Object> handleGet(Object pojoInstance) throws InvalidException;

	protected abstract Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException;

	protected abstract Map<String, Object> handlePut(Object pojoInstance) throws InvalidException;

}
