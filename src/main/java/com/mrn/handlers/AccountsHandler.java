package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.accesscontrol.AccessValidator;
import com.mrn.dao.AccountsDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountDetails;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class AccountsHandler
{

	private final AccountsDAO accountsDAO = new AccountsDAO();
	private final UserDAO userDAO = new UserDAO();

	// GET|GET /accounts(?:\\?.*)?
	// 1,2,3
	public Map<String, Object> handleGet(Map<String, String> queryParams, Map<String, Object> session)
	        throws InvalidException
	{
	    return TransactionExecutor.execute(() ->
	    {
	        Long sessionBranchId = (Long) session.get("branchId");
	        Short sessionRole = (Short) session.get("userCategory");

	        Utility.checkError(Validator.checkAccountsFilterParams(queryParams));

	        // Safe parsing after validation
	        Short filterType = queryParams.containsKey("type") ? Short.parseShort(queryParams.get("type")) : null;
	        Long filterBranchId = queryParams.containsKey("branchId") ? Long.parseLong(queryParams.get("branchId")) : null;
	        int pageNo = Integer.parseInt(queryParams.getOrDefault("page", "1"));
	        int limit = Integer.parseInt(queryParams.getOrDefault("limit", "10"));
	        int offset = (pageNo - 1) * limit;

	        // Apply branch access restriction
	        Long effectiveBranchId = (sessionRole == 3) ? filterBranchId : sessionBranchId;

	        List<Accounts> accounts = accountsDAO.getAllAccountsFiltered(filterType, effectiveBranchId, limit, offset);
	        return Utility.createResponse("Accounts List Fetched Successfully", "Accounts", accounts);
	    });
	}


	// GET|POST /accounts
	// 0,1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Accounts account = (Accounts) pojoInstance;

			Long accountNo = account.getAccountNo(); // May be null
			Long clientId = account.getClientId(); // May be null

			// Enforce: Exactly one of accountNo or clientId must be provided
			if ((accountNo == null && clientId == null) || (accountNo != null && clientId != null))
			{
				throw new InvalidException("Provide exactly one: either Account Number or Client ID, but not both.");
			}

			// If clientId is provided
			if (clientId != null)
			{
				Utility.checkError(Validator.checkClientId(clientId));
				AccessValidator.validateGet(account, session);
				List<Accounts> accounts = accountsDAO.getAccountsByClientId(clientId);
				return Utility.createResponse("Accounts fetched successfully", "Accounts", accounts);
			}

			// If accountNo is provided
			Utility.checkError(Validator.checkAccountNo(accountNo));
			AccountDetails accDetails = accountsDAO.getAccountByAccountNo(accountNo);
			AccessValidator.validateGet(accDetails, session);
			return Utility.createResponse("Account details fetched successfully", "Account", accDetails);
		});
	}

	// POST|POST /accounts
	// 1,2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Accounts account = (Accounts) pojoInstance;
			Utility.checkError(Validator.checkAccountCreation(account));
			AccessValidator.validatePost(account, session);

			account.setStatus(Status.ACTIVE.getValue());
			account.setModifiedBy((Long) session.get("userId"));

			accountsDAO.addAccount(account);
			return Utility.createResponse("Account Created Successfully");
		});
	}

	// PUT|POST /accounts
	// 1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Accounts updatedAccount = (Accounts) pojoInstance;
			Long sessionUserId = (Long) session.get("userId");
			Utility.checkError(Validator.checkAccountUpdate(updatedAccount));
			Utility.validatePassword(updatedAccount.getPassword(), userDAO.getPasswordByUserId(sessionUserId));

			long targetAccountNo = updatedAccount.getAccountNo();
			long actualBranchId = accountsDAO.getBranchIdFromAccount(targetAccountNo);
			updatedAccount.setBranchId(actualBranchId);
			updatedAccount.setModifiedBy(sessionUserId);

			AccessValidator.validatePut(updatedAccount, session);
			accountsDAO.updateAccount(updatedAccount);
			return Utility.createResponse("Account updated successfully");
		});
	}
}
