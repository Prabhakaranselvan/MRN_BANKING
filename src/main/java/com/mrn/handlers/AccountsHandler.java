package com.mrn.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mrn.dao.AccountsDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class AccountsHandler
{

	AccountsDAO accountsDAO = new AccountsDAO();
	UserDAO userDAO = new UserDAO();

	// GET|GET /accounts
	// 1,2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		try
		{
			UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));
			List<Accounts> accounts = new ArrayList<>();

			if (sessionRole == UserCategory.EMPLOYEE || sessionRole == UserCategory.MANAGER)
			{
				long sessionBranchId = (long) session.get("branchId");
				accounts.addAll(accountsDAO.getAccountsByBranchId(sessionBranchId));
			}
			else if (sessionRole == UserCategory.GENERAL_MANAGER)
			{
				accounts.addAll(accountsDAO.getAllAccounts());
			}
			ConnectionManager.commit();
			return Utility.createResponse("Accounts List Fetched Successfully", "Accounts", accounts);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch account details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Accounts account = (Accounts) pojoInstance;

			long accountNo = account.getAccountNo(); // May be null
			long clientId = account.getClientId();
			long sessionUserId = (long) session.get("userId");
			UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));

			List<Accounts> accounts = new ArrayList<>();

			if (accountNo == 0)
			{
				accounts.addAll(accountsDAO.getAccountsByClientId(clientId));
			}
			else
			{
				Accounts dbAccount = accountsDAO.getAccountByAccountNo(accountNo);
				if (sessionRole == UserCategory.CLIENT) // Clients can only access their own accounts
				{
					if (dbAccount.getClientId() != sessionUserId)
					{
						throw new InvalidException("Access denied to this account");
					}
				}
				else if (sessionRole == UserCategory.EMPLOYEE || sessionRole == UserCategory.MANAGER)
				{
					long sessionBranchId = (long) session.get("branchId");
					if (dbAccount.getBranchId() != sessionBranchId)
					{
						throw new InvalidException("Access denied to this account");
					}
				}
				accounts.add(dbAccount);
			}
			ConnectionManager.commit();
			return Utility.createResponse("Account details fetched successfully", "Accounts", accounts);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch account details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// POST|POST /accounts
	// 1,2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Accounts account = (Accounts) pojoInstance;

			UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));

			if (sessionRole == UserCategory.EMPLOYEE || sessionRole == UserCategory.MANAGER)
			{
				// Extract performer and target user's brannchId
				long sessionBranchId = (long) session.get("branchId");
				long targetBranchId = account.getBranchId();
				if (sessionBranchId != targetBranchId)
				{
					throw new InvalidException("You can add account to only your branch");
				}
			}

			short active = (short) Status.ACTIVE.getValue();
			account.setStatus(active);
			long modifierId = (long) session.get("userId");
			account.setModifiedBy(modifierId);

			boolean success = accountsDAO.addAccount(account);

			if (success)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Account Created Successfully");
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Account Creation Failed");
			}
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Account Creation failed due to an unexpected error.", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Accounts updatedAccount = (Accounts) pojoInstance;
			long sessionUserId = (long) session.get("userId");
			short sessionRole = (short) session.get("userCategory");
			
			Utility.checkError(Validator.checkAccountUpdate(updatedAccount));
			Utility.validatePassword(updatedAccount.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
			
			updatedAccount.setModifiedBy(sessionUserId);
			updatedAccount.setModifiedTime(System.currentTimeMillis());

			long targetAccountNo = updatedAccount.getAccountNo();
			long actualBranchId = accountsDAO.getBranchIdFromAccount(targetAccountNo);

			if (sessionRole == UserCategory.EMPLOYEE.ordinal() || sessionRole == UserCategory.MANAGER.ordinal())
			{
				long sessionBranchId = (long) session.get("branchId");

				if (sessionBranchId != actualBranchId)
				{
					throw new InvalidException("You can only update accounts within your branch");
				}
			}
				
			boolean updated = accountsDAO.updateAccount(updatedAccount);

			if (updated)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Account updated successfully");
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Account update failed");
			}
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to update account", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

}
