package com.mrn.pojos;

public class AccountRequest
{
	private Long requestId;
	private Long branchId;
	private Long clientId;
	private Short accountType;
	private Short status;
	private Long requestedTime;
	private Long modifiedTime;
	private Long modifiedBy;

	public Long getRequestId()
	{
		return requestId;
	}

	public void setRequestId(Long requestId)
	{
		this.requestId = requestId;
	}

	public Long getBranchId()
	{
		return branchId;
	}

	public void setBranchId(Long branchId)
	{
		this.branchId = branchId;
	}

	public Long getClientId()
	{
		return clientId;
	}

	public void setClientId(Long clientId)
	{
		this.clientId = clientId;
	}

	public Short getAccountType()
	{
		return accountType;
	}

	public void setAccountType(Short accountType)
	{
		this.accountType = accountType;
	}

	public Short getStatus()
	{
		return status;
	}

	public void setStatus(Short status)
	{
		this.status = status;
	}

	public Long getRequestedTime()
	{
		return requestedTime;
	}

	public void setRequestedTime(Long requestedTime)
	{
		this.requestedTime = requestedTime;
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
