package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.AccountsDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Accounts;
import com.mrn.utilshub.ConnectionManager;

public class AccountsHandler 
{
    public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
    {
        try 
        {
        	Accounts account = (Accounts) pojoInstance;
        	
        	short categoryValue = (short) attributeMap.get("userCategory");
            UserCategory modifierCategory = UserCategory.fromValue(categoryValue);
            
            if (modifierCategory == UserCategory.EMPLOYEE || modifierCategory == UserCategory.MANAGER)
            {
                // Extract modifier and target user's brannchId
            	long modifierBranchId = (long) attributeMap.get("branchId");
                long targetBranchId = account.getBranchId();
                if (modifierBranchId != targetBranchId)
                {
                	throw new InvalidException("You can add account to only your branch");
                }
            }

            short active = (short) Status.ACTIVE.getValue();
            account.setStatus(active);
            long modifierId = (long) attributeMap.get("userId");
            account.setModifiedBy(modifierId);

            AccountsDAO accountDAO = new AccountsDAO();

            boolean success = accountDAO.addAccount(account);

            if (success) 
            {
                ConnectionManager.commit();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Account Created Successfully");
                return responseMap;
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
}

