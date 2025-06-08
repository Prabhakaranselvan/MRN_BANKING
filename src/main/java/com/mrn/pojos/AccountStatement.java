package com.mrn.pojos;

public class AccountStatement
{
	private Long clientId;
	private Long accountNo; // Can be 0 to indicate all accounts
	private String fromDate; // Format: yyyy-MM-dd
	private String toDate; // Format: yyyy-MM-dd

	public Long getClientId()
	{
		return clientId;
	}

	public void setClientId(Long clientId)
	{
		this.clientId = clientId;
	}

	public Long getAccountNo()
	{
		return accountNo;
	}

	public void setAccountNo(Long accountNo)
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
