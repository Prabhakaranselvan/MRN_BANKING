package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.AccountRequestDAO;
import com.mrn.dao.AccountsDAO;
import com.mrn.enums.RequestStatus;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountApprovalHandler 
{
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
	{
		try 
		{
			AccountRequest requestReview = (AccountRequest) pojoInstance;

			long requestId = requestReview.getRequestId();
			AccountRequestDAO requestDAO = new AccountRequestDAO();
			Accounts accountRequest = requestDAO.getRequestAsAccount(requestId);

			// Permission check: Only MANAGER can approve requests from their own branch
			short userCategory = (short) attributeMap.get("userCategory");
			UserCategory category = UserCategory.fromValue(userCategory);
			if (category == UserCategory.MANAGER) {
				long approverBranchId = (long) attributeMap.get("branchId");
				if (approverBranchId != accountRequest.getBranchId()) 
				{
					throw new InvalidException("Managers can only approve requests from their own branch.");
				}
			}

			RequestStatus currentStatus = RequestStatus.fromValue(accountRequest.getStatus());
			if (currentStatus != RequestStatus.PENDING) {
				throw new InvalidException("Request ID " + requestId + " has already been processed.");
			}

			long approverId = (long) attributeMap.get("userId");
			short reviewStatusValue = requestReview.getStatus();
			RequestStatus reviewStatus = RequestStatus.fromValue(reviewStatusValue);

			boolean updated = requestDAO.updateRequestStatus(requestId, reviewStatusValue, approverId);
			if (!updated) {
				throw new InvalidException("Failed to update the request status.");
			}

			Map<String, Object> responseMap = new HashMap<>();
			if (reviewStatus == RequestStatus.APPROVED) {
				accountRequest.setStatus((short) Status.ACTIVE.getValue());
				accountRequest.setModifiedBy(approverId);

				AccountsDAO accountsDAO = new AccountsDAO();
				boolean accountCreated = accountsDAO.addAccount(accountRequest);

				if (accountCreated) {
					ConnectionManager.commit();
					responseMap.put("message", "Account approved and created successfully.");
				} else {
					ConnectionManager.rollback();
					throw new InvalidException("Account creation failed after approval.");
				}
			} else {
				ConnectionManager.commit();
				responseMap.put("message", "Account request rejected.");
			}

			return responseMap;

		} catch (InvalidException e) {
			ConnectionManager.rollback();
			throw e;
		} catch (Exception e) {
			ConnectionManager.rollback();
			throw new InvalidException("Unexpected error during account approval", e);
		} finally {
			ConnectionManager.close();
		}
	}
}
