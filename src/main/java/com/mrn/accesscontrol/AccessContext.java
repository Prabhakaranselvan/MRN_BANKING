package com.mrn.accesscontrol;

import java.util.Map;

import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;

public class AccessContext
{
	private final Map<String, Object> session;
	private final Object targetResource; // Could be Employee, Account, etc.

	public AccessContext(Map<String, Object> session, Object targetResource)
	{
		this.session = session;
		this.targetResource = targetResource;
	}

	public Map<String, Object> getSession()
	{
		return session;
	}

	public Object getTargetResource()
	{
		return targetResource;
	}

	public long getSessionUserId()
	{
		return (long) session.get("userId");
	}

	public UserCategory getSessionRole() throws InvalidException
	{
		return UserCategory.fromValue((short) session.get("userCategory"));
	}

	public long getSessionBranchId()
	{
		return session.containsKey("branchId") ? (long) session.get("branchId") : -1;
	}
}
