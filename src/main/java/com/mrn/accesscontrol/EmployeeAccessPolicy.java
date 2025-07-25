package com.mrn.accesscontrol;

import com.mrn.dao.AccountsDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountStatement;
import com.mrn.pojos.Accounts;
import com.mrn.pojos.Employee;
import com.mrn.pojos.Transaction;

public class EmployeeAccessPolicy implements AccessPolicy
{
	private final AccountsDAO accountsDAO = new AccountsDAO();

	@Override
	public void validateGetAccess(AccessContext ctx) throws InvalidException
	{
		Object resource = ctx.getTargetResource();
		Long sessionUserId = ctx.getSessionUserId();
		Long sessionBranchId = ctx.getSessionBranchId();

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
		else if (resource instanceof AccountStatement) // GET|POST /accountstatement
		{
			AccountStatement accStatement = (AccountStatement) resource;
			if (accountsDAO.getBranchIdFromAccount(accStatement.getAccountNo()) != sessionBranchId)
			{
				throw new InvalidException("You can only access on accounts from your branch.");
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
		Long sessionBranchId = ctx.getSessionBranchId();
		
		if (resource instanceof Accounts)
		{
			Accounts acc = (Accounts) resource;
			if (acc.getBranchId() != sessionBranchId)
			{
				throw new InvalidException("You can add account to only your branch");
			}
		}
		else if (resource instanceof Transaction) // POST|POST /transaction
		{
			Transaction txn = (Transaction) resource;
			if (accountsDAO.getBranchIdFromAccount(txn.getAccountNo()) != sessionBranchId)
			{
				throw new InvalidException("You can only operate on accounts from your branch.");
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
		Long sessionUserId = ctx.getSessionUserId();
		Long sessionBranchId = ctx.getSessionBranchId();
		
		if (resource instanceof Employee) // PUT|POST /client
		{
			Employee emp = (Employee) resource;
			if (emp.getUserId() != sessionUserId)
			{
				throw new InvalidException("Employees can only edit their own profile");
			}
		} else if (resource instanceof Accounts)
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