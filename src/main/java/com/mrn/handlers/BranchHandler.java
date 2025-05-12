package com.mrn.handlers;

import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.BranchDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.ConnectionManager;

public class BranchHandler extends Handler{

	@Override
	protected Map<String, Object> handleGet(Object pojoInstance) throws InvalidException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException 
	{
		try 
		{
			Map<String, Object> responseMap = new HashMap<>();
			Branch branch = (Branch) pojoInstance;
			BranchDAO branchDAO = new BranchDAO();

			boolean success = branchDAO.addBranch(branch);
			
            if (success) 
            {
                ConnectionManager.commit();
                responseMap.put("success", true);
                responseMap.put("message", "Branch Added Successfully");
            } 
            else 
            {
                ConnectionManager.rollback();
                responseMap.put("success", false);
                responseMap.put("message", "Branch Addition Failed");
            }
			return responseMap;
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


	@Override
	protected Map<String, Object> handlePut(Object pojoInstance) throws InvalidException {
		// TODO Auto-generated method stub
		return null;
	}

}
