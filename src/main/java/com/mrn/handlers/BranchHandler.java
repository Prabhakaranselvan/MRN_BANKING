package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.BranchDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.ConnectionManager;

public class BranchHandler
{
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
	{
		try 
		{
			Branch branch = (Branch) pojoInstance;
			
			long modifierId = (long) attributeMap.get("userId");
			branch.setModifiedBy(modifierId);
			
			BranchDAO branchDAO = new BranchDAO();
			long nextId = branchDAO.getNextBranchId();
			String ifsc = String.format("MRNB0%06d", nextId); // Format like MRNB000001
			branch.setIfscCode(ifsc);

			boolean success = branchDAO.addBranch(branch);
			
            if (success) 
            {
                ConnectionManager.commit();
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("message", "Branch Added Successfully");
                return responseMap;
            } 
            else 
            {
                ConnectionManager.rollback();
                throw new InvalidException("Branch Addition Failed");
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
			throw new InvalidException("Branch Addition failed due to an unexpected error.", e);
		} 
		finally 
		{
			ConnectionManager.close();
		}
	}
}
