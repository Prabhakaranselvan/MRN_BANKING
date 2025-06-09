package com.mrn.accesscontrol;

import com.mrn.enums.TxnType;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountStatement;
import com.mrn.pojos.Accounts;
import com.mrn.pojos.Client;
import com.mrn.pojos.Transaction;

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
		else if (resource instanceof Accounts)
		{
			Accounts acc = (Accounts) resource;
			if (acc.getClientId() != sessionUserId)
			{
				throw new InvalidException("Clients can only access their own Accounts");
			}
		}
		else if (resource instanceof AccountStatement)
		{
			AccountStatement acc = (AccountStatement) resource;
			if (acc.getClientId() != sessionUserId)
			{
				throw new InvalidException("Clients can only access their own transactions.");
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
		long sessionUserId = ctx.getSessionUserId();
		
		if (resource instanceof Transaction) // POST|POST /transaction
		{
			Transaction txn = (Transaction) resource;
			TxnType txnType = TxnType.fromValue(txn.getTxnType());
			if (txnType != TxnType.DEBIT || txn.getClientId() != sessionUserId)
			{
				throw new InvalidException("Clients can only transfer from their own accounts.");
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
