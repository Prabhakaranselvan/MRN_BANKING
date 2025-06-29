package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.dao.AccountRequestDAO;
import com.mrn.enums.RequestStatus;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class AccountRequestHandler
{
	AccountRequestDAO requestDAO = new AccountRequestDAO();

	// GET|GET /account-requests(?:\\?.*)?
	// Roles: 2, 3
	public Map<String, Object> handleGet(Map<String, String> queryParams, Map<String, Object> session)
	        throws InvalidException
	{
	    return TransactionExecutor.execute(() ->
	    {
	        Long sessionBranchId = (Long) session.get("branchId");
	        Short sessionRole = (Short) session.get("userCategory");

	        // Validate and parse query parameters
	        Utility.checkError(Validator.checkAccountRequestFilterParams(queryParams));

	        // Safely parse after validation
	        Short filterStatus = queryParams.containsKey("status") ? Short.parseShort(queryParams.get("status")) : null;
	        Long filterBranchId = queryParams.containsKey("branchId") ? Long.parseLong(queryParams.get("branchId")) : null;
	        int pageNo = Integer.parseInt(queryParams.getOrDefault("page", "1"));
	        int limit = Integer.parseInt(queryParams.getOrDefault("limit", "10"));
	        int offset = (pageNo - 1) * limit;

	        Long effectiveBranchId = (sessionRole == 3) ? filterBranchId : sessionBranchId;

	        List<AccountRequest> requests = requestDAO.getAllAccountRequestsFiltered(filterStatus, effectiveBranchId, limit, offset);
	        return Utility.createResponse("Account Requests Fetched Successfully", "AccountRequests", requests);
	    });
	}

	// POST|POST /accountrequest
	// 0
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			AccountRequest accountRequest = (AccountRequest) pojoInstance;
			Utility.checkError(Validator.checkAccountRequest(accountRequest));
			Long userId = (Long) session.get("userId");
			accountRequest.setClientId(userId);
			accountRequest.setStatus(RequestStatus.PENDING.getValue());
			accountRequest.setModifiedBy(userId);

			requestDAO.addAccountRequest(accountRequest);
			return Utility.createResponse("Account creation request submitted successfully");
		});
	}
}
