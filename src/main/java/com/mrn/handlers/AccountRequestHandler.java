package com.mrn.handlers;

import java.util.Map;

import com.mrn.dao.AccountRequestDAO;
import com.mrn.enums.RequestStatus;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;

public class AccountRequestHandler 
{
	 AccountRequestDAO requestDAO = new AccountRequestDAO();
	 
	// POST|POST /accountrequest
	// 0
    public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException 
    {
    	return TransactionExecutor.execute(() ->
		{
            AccountRequest accountRequest = (AccountRequest) pojoInstance;
            long userId = (long) session.get("userId");
            
            accountRequest.setClientId(userId);
            accountRequest.setStatus((short) RequestStatus.PENDING.getValue());
            accountRequest.setModifiedBy(userId);
           
            requestDAO.addAccountRequest(accountRequest);
            return Utility.createResponse("Account creation request submitted successfully");
        });
    }
}

