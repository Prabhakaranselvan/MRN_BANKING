package com.mrn.accesscontrol;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Employee;

public class EmployeeAccessPolicy implements AccessPolicy
{

	@Override
	public void validateGetAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionUserId = ctx.getSessionUserId();		
//		long branchId = ctx.getSessionBranchId();

		if (resource instanceof Employee) // GET|POST /employee
		{
			Employee emp = (Employee) resource;
			if (emp.getUserId() != sessionUserId)
			{
				throw new InvalidException("Employees can only view their own profile");
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
		// Optional: based on your access control policy
	}
	
	@Override
	public void validatePutAccess(AccessContext ctx) throws InvalidException
	{
		
	}
}