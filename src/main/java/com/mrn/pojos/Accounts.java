package com.mrn.pojos;

import java.math.BigDecimal;

public class Accounts
{
	private long accountNo;
	private long branchId;
	private long clientId;
	private short accountType;
	private short status;
	private BigDecimal balance;
	private long createdTime;
	private long modifiedTime;
	private long modifiedBy;

	private String password;

	public long getAccountNo()
	{
		return accountNo;
	}

	public void setAccountNo(long accountNo)
	{
		this.accountNo = accountNo;
	}

	public long getBranchId()
	{
		return branchId;
	}

	public void setBranchId(long branchId)
	{
		this.branchId = branchId;
	}

	public long getClientId()
	{
		return clientId;
	}

	public void setClientId(long clientId)
	{
		this.clientId = clientId;
	}

	public short getAccountType()
	{
		return accountType;
	}

	public void setAccountType(short accountType)
	{
		this.accountType = accountType;
	}

	public short getStatus()
	{
		return status;
	}

	public void setStatus(short status)
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

	public long getCreatedTime()
	{
		return createdTime;
	}

	public void setCreatedTime(long createdTime)
	{
		this.createdTime = createdTime;
	}

	public long getModifiedTime()
	{
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime)
	{
		this.modifiedTime = modifiedTime;
	}

	public long getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy)
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
