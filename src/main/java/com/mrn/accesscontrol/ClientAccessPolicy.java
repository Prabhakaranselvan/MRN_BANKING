package com.mrn.accesscontrol;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;

public class ClientAccessPolicy implements AccessPolicy
{

	@Override
	public void validateGetAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionUserId = ctx.getSessionUserId();

		if (resource instanceof Client) // GET|POST /client
		{
			Client client = (Client) resource;
			if (client.getUserId() != sessionUserId)
			{
				throw new InvalidException("Clients can only access their own profile");
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
		throw new InvalidException("Clients cannot POST data.");
	}
	
	@Override
	public void validatePutAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		long sessionUserId = ctx.getSessionUserId();

		if (resource instanceof Client) // PUT|POST /client
		{
			Client client = (Client) resource;
			if (client.getUserId() != sessionUserId)
			{
				throw new InvalidException("Clients can only edit their own profile");
			}
		}
		else
		{
			throw new InvalidException("Unauthorized access.");
		}
	}
}
