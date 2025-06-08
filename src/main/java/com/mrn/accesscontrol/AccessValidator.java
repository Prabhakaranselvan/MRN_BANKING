package com.mrn.accesscontrol;

import java.util.Map;

import com.mrn.exception.InvalidException;

public class AccessValidator
{

	public static void validateGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		AccessContext context = new AccessContext(session, pojoInstance);
		AccessPolicy policy = AccessPolicyRegistry.getPolicy(context.getSessionRole());
		policy.validateGetAccess(context);
	}

	public static void validatePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		AccessContext context = new AccessContext(session, pojoInstance);
		AccessPolicy policy = AccessPolicyRegistry.getPolicy(context.getSessionRole());
		policy.validatePostAccess(context);
	}
}