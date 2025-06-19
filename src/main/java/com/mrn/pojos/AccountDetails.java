package com.mrn.pojos;

public class AccountDetails {
    private Accounts account;
    private String branchName;
    private String ifscCode;
    // maybe also: private String clientName; private String employeeName; etc.
	public Accounts getAccount()
	{
		return account;
	}
	public void setAccount(Accounts account)
	{
		this.account = account;
	}
	public String getBranchName()
	{
		return branchName;
	}
	public void setBranchName(String branchName)
	{
		this.branchName = branchName;
	}
	public String getIfscCode()
	{
		return ifscCode;
	}
	public void setIfscCode(String ifscCode)
	{
		this.ifscCode = ifscCode;
	}
}

