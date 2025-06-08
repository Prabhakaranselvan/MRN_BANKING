package com.mrn.accesscontrol;

import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Employee;

public class ManagerAccessPolicy implements AccessPolicy
{
	@Override
	public void validateGetAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionUserId = ctx.getSessionUserId();
		UserCategory sessionRole = ctx.getSessionRole();
		long sessionBranchId = ctx.getSessionBranchId();

		if (resource instanceof Employee) // GET|POST /employee
		{
			Employee emp = (Employee) resource;
			UserCategory targetRole = UserCategory.fromValue(emp.getUserCategory());
			if (sessionRole == targetRole)
			{
				if (emp.getUserId() != sessionUserId)
				{
					throw new InvalidException("Managers can only view their own profile");
				}
			}
			else
			{
				if (emp.getBranchId() != sessionBranchId)
				{
					throw new InvalidException("You can only access employees within your branch");
				}
			}
		}
		else
		{
			throw new InvalidException("Unauthorized access.");
		}
	}

	@Override
	public void validatePostAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionBranchId = ctx.getSessionBranchId();

		if (resource instanceof Employee) // POST|POST /employee
		{
			Employee emp = (Employee) resource;
			UserCategory targetRole = UserCategory.fromValue(emp.getUserCategory());
			if (targetRole != UserCategory.EMPLOYEE)
			{
				throw new InvalidException("You can only add employees.");
			}
			if (emp.getBranchId() != sessionBranchId)
			{
				throw new InvalidException("You can only add employees to your own branch.");
			}
		}
		else
		{
			throw new InvalidException("Unauthorized access.");
		}
	}

	@Override
	public void validatePutAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionUserId = ctx.getSessionUserId();
		UserCategory sessionRole = ctx.getSessionRole();
		long sessionBranchId = ctx.getSessionBranchId();

		if (resource instanceof Employee) // POST|POST /employee
		{
			Employee emp = (Employee) resource;
			UserCategory targetRole = UserCategory.fromValue(emp.getUserCategory());
			if (sessionRole == targetRole)
			{
				if (emp.getUserId() != sessionUserId)
				{
					throw new InvalidException("You can only edit your own profile");
				}
			}
			else
			{
				if (targetRole != UserCategory.EMPLOYEE)
				{
					throw new InvalidException("you can only edit employees.");
				}
				if (emp.getBranchId() != sessionBranchId)
				{
					throw new InvalidException("You can only update employees within your branch");
				}
			}
		}
		else
		{
			throw new InvalidException("Unauthorized access.");
		}

	}
}
