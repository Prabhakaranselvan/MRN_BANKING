package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.dao.BranchDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Branch;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class BranchHandler
{
	BranchDAO branchDAO = new BranchDAO();

	// GET|GET /branch
	// 0,1,2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			List<Branch> branches = branchDAO.getAllBranches();
			return Utility.createResponse("Branches List Fetched Successfully", "Branches", branches);
		});
	}

	// GET|POST /branch
    // 0,1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Branch inputBranch = (Branch) pojoInstance;
			Branch branch = branchDAO.getBranchById(inputBranch.getBranchId());
			return Utility.createResponse("Branch Details Fetched Successfully", "branch", branch);
		});
	}

	// POST|POST /branch
	// 3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Branch branch = (Branch) pojoInstance;
			Utility.checkError(Validator.checkBranch(branch));
			branch.setModifiedBy( (long) session.get("userId"));

			long nextId = branchDAO.getNextBranchId();
			String ifsc = String.format("MRNB0%06d", nextId); // Format like MRNB000001
			branch.setIfscCode(ifsc);

			branchDAO.addBranch(branch);
			return Utility.createResponse("Branch Added Successfully");
		});
	}

	// PUT|POST /branch
	// 3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Branch updatedBranch = (Branch) pojoInstance;
			Utility.checkError(Validator.checkBranch(updatedBranch));
			
			long sessionUserId = (long) session.get("userId");
			updatedBranch.setModifiedBy(sessionUserId);

			branchDAO.updateBranchDetails(updatedBranch);
			return Utility.createResponse("Branch updated Successfully");
		});
	}

}
