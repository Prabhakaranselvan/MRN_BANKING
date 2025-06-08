package com.mrn.accesscontrol;

import java.util.EnumMap;
import java.util.Map;

import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;

public class AccessPolicyRegistry
{
	private static final Map<UserCategory, AccessPolicy> policies = new EnumMap<>(UserCategory.class);

	static
	{
		policies.put(UserCategory.CLIENT, new ClientAccessPolicy());
		policies.put(UserCategory.EMPLOYEE, new EmployeeAccessPolicy());
		policies.put(UserCategory.MANAGER, new ManagerAccessPolicy());
	}

	public static AccessPolicy getPolicy(UserCategory role) throws InvalidException
	{
		AccessPolicy policy = policies.get(role);
		if (policy == null)
		{
			throw new InvalidException("No access policy defined for role: " + role);
		}
		return policy;
	}
}
