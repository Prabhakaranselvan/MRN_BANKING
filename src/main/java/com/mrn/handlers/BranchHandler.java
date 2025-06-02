package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.dao.BranchDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class BranchHandler
{
	BranchDAO branchDAO = new BranchDAO();

	// GET|GET /branch
	// 0,1,2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		try
		{
			List<Branch> branches = branchDAO.getAllBranches();

			ConnectionManager.commit();
			return Utility.createResponse("Branches List Fetched Successfully", "Branches", branches);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch branch details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// GET|POST /branch
    // 0,1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Branch inputBranch = (Branch) pojoInstance;
			Branch branch = branchDAO.getBranchById(inputBranch.getBranchId());
			
			ConnectionManager.commit();
			return Utility.createResponse("Branch Details Fetched Successfully", "branch", branch);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch branch detail", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// POST|POST /branch
	// 3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> attributeMap) throws InvalidException
	{
		try
		{
			Branch branch = (Branch) pojoInstance;
			
			Utility.checkError(Validator.checkBranch(branch));

			long modifierId = (long) attributeMap.get("userId");
			branch.setModifiedBy(modifierId);

			long nextId = branchDAO.getNextBranchId();
			String ifsc = String.format("MRNB0%06d", nextId); // Format like MRNB000001
			branch.setIfscCode(ifsc);

			boolean success = branchDAO.addBranch(branch);

			if (success)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Branch Added Successfully");
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

	// PUT|POST /branch
	// 3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Branch updatedBranch = (Branch) pojoInstance;
			Utility.checkError(Validator.checkBranch(updatedBranch));
			
			long sessionUserId = (long) session.get("userId");
			updatedBranch.setModifiedBy(sessionUserId);

			boolean updated = branchDAO.updateBranchDetails(updatedBranch);

			if (updated)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Branch updated Successfully");
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Branch update failed");
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
			throw new InvalidException("Branch update failed", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

}
