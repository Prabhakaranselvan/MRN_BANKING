package com.mrn.accesscontrol;

import java.util.Map;

import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;

public class AccessValidator
{

	public static void validateGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		AccessContext context = new AccessContext(session, pojoInstance);
		UserCategory role = context.getSessionRole();
		if (role == UserCategory.GENERAL_MANAGER) return;
		AccessPolicy policy = AccessPolicyRegistry.getPolicy(role);
		policy.validateGetAccess(context);
	}

	public static void validatePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		AccessContext context = new AccessContext(session, pojoInstance);
		UserCategory role = context.getSessionRole();
		if (role == UserCategory.GENERAL_MANAGER) return;
		AccessPolicy policy = AccessPolicyRegistry.getPolicy(role);
		policy.validatePostAccess(context);
	}
	
	public static void validatePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		AccessContext context = new AccessContext(session, pojoInstance);
		UserCategory role = context.getSessionRole();
		if (role == UserCategory.GENERAL_MANAGER) return;
		AccessPolicy policy = AccessPolicyRegistry.getPolicy(role);
		policy.validatePutAccess(context);
	}
}