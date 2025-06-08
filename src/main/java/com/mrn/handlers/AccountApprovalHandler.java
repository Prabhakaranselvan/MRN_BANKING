package com.mrn.handlers;
import java.util.Map;

import com.mrn.accesscontrol.AccessValidator;
import com.mrn.dao.AccountRequestDAO;
import com.mrn.dao.AccountsDAO;
import com.mrn.enums.RequestStatus;
import com.mrn.enums.Status;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;

public class AccountApprovalHandler
{
	private final AccountRequestDAO requestDAO = new AccountRequestDAO();
	private final AccountsDAO accountsDAO = new AccountsDAO();

	// POST|POST /accountapproval
	// 2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			AccountRequest requestReview = (AccountRequest) pojoInstance;

			long requestId = requestReview.getRequestId();
			Accounts accountRequest = requestDAO.getRequestAsAccount(requestId);
			requestReview.setBranchId(accountRequest.getBranchId());
			AccessValidator.validatePost(requestReview, session);

			RequestStatus currentStatus = RequestStatus.fromValue(accountRequest.getStatus());
			if (currentStatus != RequestStatus.PENDING)
			{
				throw new InvalidException("Request ID " + requestId + " has already been processed.");
			}

			long approverId = (long) session.get("userId");
			short reviewStatusValue = requestReview.getStatus();
			RequestStatus reviewStatus = RequestStatus.fromValue(reviewStatusValue);

			requestDAO.updateRequestStatus(requestId, reviewStatusValue, approverId);

			if (reviewStatus == RequestStatus.APPROVED)
			{
				accountRequest.setStatus((short) Status.ACTIVE.getValue());
				accountRequest.setModifiedBy(approverId);
				accountsDAO.addAccount(accountRequest);
				return Utility.createResponse("Account approved and created successfully.");
			}
			else
			{
				return Utility.createResponse("Account request rejected.");
			}
		});
	}
}
