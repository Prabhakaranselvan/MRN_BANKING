package com.mrn.pojos;

import java.math.BigDecimal;

public class Transaction
{
	private Long txnId;
	private Long clientId;
	private Long accountNo;
	private Long peerAccNo;
	private BigDecimal amount;
	private Short txnType;
	private Long txnTime;
	private Short txnStatus;
	private Long txnRefNo;
	private BigDecimal closingBalance;
	private String description;
	private Long doneBy;
	
	private String Password;

	public Long getTxnId()
	{
		return txnId;
	}

	public void setTxnId(Long txnId)
	{
		this.txnId = txnId;
	}

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

	public Long getPeerAccNo()
	{
		return peerAccNo;
	}

	public void setPeerAccNo(Long peerAccNo)
	{
		this.peerAccNo = peerAccNo;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public Short getTxnType()
	{
		return txnType;
	}

	public void setTxnType(Short txnType)
	{
		this.txnType = txnType;
	}

	public Long getTxnTime()
	{
		return txnTime;
	}

	public void setTxnTime(Long txnTime)
	{
		this.txnTime = txnTime;
	}

	public Short getTxnStatus()
	{
		return txnStatus;
	}

	public void setTxnStatus(Short txnStatus)
	{
		this.txnStatus = txnStatus;
	}

	public Long getTxnRefNo()
	{
		return txnRefNo;
	}

	public void setTxnRefNo(Long txnRefNo)
	{
		this.txnRefNo = txnRefNo;
	}

	public BigDecimal getClosingBalance()
	{
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance)
	{
		this.closingBalance = closingBalance;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Long getDoneBy()
	{
		return doneBy;
	}

	public void setDoneBy(Long doneBy)
	{
		this.doneBy = doneBy;
	}

	public String getPassword()
	{
		return Password;
	}

	public void setPassword(String password)
	{
		Password = password;
	}
	
	

}
