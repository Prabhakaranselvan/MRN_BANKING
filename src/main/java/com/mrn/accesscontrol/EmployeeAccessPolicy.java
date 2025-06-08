package com.mrn.accesscontrol;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.pojos.Employee;

public class EmployeeAccessPolicy implements AccessPolicy
{

	@Override
	public void validateGetAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionUserId = ctx.getSessionUserId();
		long sessionBranchId = ctx.getSessionBranchId();

		if (resource instanceof Employee) // GET|POST /employee
		{
			Employee emp = (Employee) resource;
			if (emp.getUserId() != sessionUserId)
			{
				throw new InvalidException("Employees can only view their own profile");
			}
		}
		else if (resource instanceof Accounts)
		{
			Accounts acc = (Accounts) resource;
			if (acc.getBranchId() != sessionBranchId)
			{
				throw new InvalidException("Access denied to this account");
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
//		long sessionUserId = ctx.getSessionUserId();
		long sessionBranchId = ctx.getSessionBranchId();
		
		if (resource instanceof Accounts)
		{
			Accounts acc = (Accounts) resource;
			if (acc.getBranchId() != sessionBranchId)
			{
				throw new InvalidException("You can add account to only your branch");
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
//		long sessionUserId = ctx.getSessionUserId();
		long sessionBranchId = ctx.getSessionBranchId();
		
		if (resource instanceof Accounts)
		{
			Accounts acc = (Accounts) resource;
			if (acc.getBranchId() != sessionBranchId)
			{
				throw new InvalidException("You can only update accounts within your branch");
			}
		}
		else
		{
			throw new InvalidException("Unauthorized access.");
		}

	}
}