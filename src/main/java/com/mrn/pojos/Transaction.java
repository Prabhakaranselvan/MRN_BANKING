package com.mrn.pojos;

import java.math.BigDecimal;

public class Transaction {
	private long txnId;
	private long clientId;
	private long accountNo;
	private long peerAccNo;
	private BigDecimal amount;
	private short txnType;
	private long txnTime;
	private short txnStatus;
	private long txnRefNo;
	private BigDecimal closingBalance;
	private String description;
	private long doneBy;

	public long getTxnId() {
		return txnId;
	}

	public void setTxnId(long txnId) {
		this.txnId = txnId;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public long getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}

	public long getPeerAccNo() {
		return peerAccNo;
	}

	public void setPeerAccNo(long peerAccNo) {
		this.peerAccNo = peerAccNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public short getTxnType() {
		return txnType;
	}

	public void setTxnType(short txnType) {
		this.txnType = txnType;
	}

	public long getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(long txnTime) {
		this.txnTime = txnTime;
	}

	public short getTxnStatus() {
		return txnStatus;
	}

	public void setTxnStatus(short txnStatus) {
		this.txnStatus = txnStatus;
	}

	public long getTxnRefNo() {
		return txnRefNo;
	}

	public void setTxnRefNo(long txnRefNo) {
		this.txnRefNo = txnRefNo;
	}

	public BigDecimal getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(BigDecimal closingBalance) {
		this.closingBalance = closingBalance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDoneBy() {
		return doneBy;
	}

	public void setDoneBy(long doneBy) {
		this.doneBy = doneBy;
	}

}
