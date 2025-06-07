package com.mrn.pojos;

public class AccountStatement
{
	private long clientId;
	private long accountNo; // Can be 0 to indicate all accounts
	private String fromDate; // Format: yyyy-MM-dd
	private String toDate; // Format: yyyy-MM-dd

	// Getters and Setters
	public long getClientId()
	{
		return clientId;
	}

	public void setClientId(long clientId)
	{
		this.clientId = clientId;
	}

	public long getAccountNo()
	{
		return accountNo;
	}

	public void setAccountNo(long accountNo)
	{
		this.accountNo = accountNo;
	}

	public String getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getToDate()
	{
		return toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}
}
