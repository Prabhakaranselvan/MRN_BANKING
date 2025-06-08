package com.mrn.accesscontrol;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.pojos.Client;
import com.mrn.pojos.Employee;

public class ManagerAccessPolicy implements AccessPolicy
{

	@Override
	public void validateGetAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long branchId = ctx.getSessionBranchId();

		if (resource instanceof Accounts)
		{
			Accounts acc = (Accounts) resource;
			if (acc.getBranchId() != branchId)
			{
				throw new InvalidException("Manager can only view accounts from their branch.");
			}
		}
		else if (resource instanceof Employee)
		{
			Employee emp = (Employee) resource;
			if (emp.getBranchId() != branchId)
			{
				throw new InvalidException("Manager can only access employees in their branch.");
			}
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
//		Object resource = ctx.getTargetResource();
//		long sessionUserId = ctx.getSessionUserId();
//
//		if (resource instanceof Client)
//		{
//			Client client = (Client) resource;
//			if (client.getUserId() != sessionUserId)
//			{
//				throw new InvalidException("Clients can only edit their own profile");
//			}
//		}
//		else
//		{
//			throw new InvalidException("Unauthorized access.");
//		}
	}
}
