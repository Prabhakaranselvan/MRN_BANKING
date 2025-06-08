package com.mrn.pojos;

public class Branch
{
	private Long branchId;
	private String branchName;
	private String branchLocation;
	private String contactNo;
	private String ifscCode;
	private Long createdTime;
	private Long modifiedTime;
	private Long modifiedBy;

	public Long getBranchId()
	{
		return branchId;
	}

	public void setBranchId(Long branchId)
	{
		this.branchId = branchId;
	}

	public String getBranchName()
	{
		return branchName;
	}

	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
	}

	public String getBranchLocation()
	{
		return branchLocation;
	}

	public void setBranchLocation(String branchLocation)
	{
		this.branchLocation = branchLocation;
	}

	public String getContactNo()
	{
		return contactNo;
	}

	public void setContactNo(String contactNo)
	{
		this.contactNo = contactNo;
	}

	public String getIfscCode()
	{
		return ifscCode;
	}

	public void setIfscCode(String ifscCode)
	{
		this.ifscCode = ifscCode;
	}

	public Long getCreatedTime()
	{
		return createdTime;
	}

	public void setCreatedTime(Long createdTime)
	{
		this.createdTime = createdTime;
	}

	public Long getModifiedTime()
	{
		return modifiedTime;
	}

	public void setModifiedTime(Long modifiedTime)
	{
		this.modifiedTime = modifiedTime;
	}

	public Long getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

}
