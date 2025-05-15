package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.AccountRequestDAO;
import com.mrn.enums.RequestStatus;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountRequest;
import com.mrn.utilshub.ConnectionManager;

public class AccountRequestHandler 
{
    public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
    {
        try 
        {
            AccountRequest accountRequest = (AccountRequest) pojoInstance;

            long userId = (long) attributeMap.get("userId");
            accountRequest.setClientId(userId);
            accountRequest.setStatus((short) RequestStatus.PENDING.getValue());
            accountRequest.setModifiedBy(userId);

            AccountRequestDAO requestDAO = new AccountRequestDAO();
            boolean success = requestDAO.addAccountRequest(accountRequest);

            if (success) 
            {
                ConnectionManager.commit();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Account creation request submitted successfully");
                return responseMap;
            } 
            else 
            {
                ConnectionManager.rollback();
                throw new InvalidException("Failed to submit account creation request");
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
            throw new InvalidException("Unexpected error during account request", e);
        } 
        finally 
        {
            ConnectionManager.close();
        }
    }
}

