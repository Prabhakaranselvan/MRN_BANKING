package com.mrn.pojos;

public class Employee extends User
{
	private Long branchId;

	public Long getBranchId()
	{
		return branchId;
	}

	public void setBranchId(Long branchId)
	{
		this.branchId = branchId;
	}

}
