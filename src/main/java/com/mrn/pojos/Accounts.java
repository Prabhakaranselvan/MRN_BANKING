package com.mrn.pojos;

import java.math.BigDecimal;

public class Accounts
{
	private Long accountNo;
	private Long branchId;
	private Long clientId;
	private Short accountType;
	private Short status;
	private BigDecimal balance;
	private Long createdTime;
	private Long modifiedTime;
	private Long modifiedBy;

	private String password;

	public Long getAccountNo()
	{
		return accountNo;
	}

	public void setAccountNo(Long accountNo)
	{
		this.accountNo = accountNo;
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

	public BigDecimal getBalance()
	{
		return balance;
	}

	public void setBalance(BigDecimal balance)
	{
		this.balance = balance;
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

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}



}
