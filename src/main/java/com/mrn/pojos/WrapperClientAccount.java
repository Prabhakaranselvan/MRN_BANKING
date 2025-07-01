package com.mrn.pojos;

public class WrapperClientAccount
{
	private Client client;
	private Accounts account;
	private AccountRequest accountRequest;

	public Client getClient()
	{
		return client;
	}

	public void setClient(Client client)
	{
		this.client = client;
	}

	public Accounts getAccount()
	{
		return account;
	}

	public void setAccount(Accounts account)
	{
		this.account = account;
	}

	public AccountRequest getAccountRequest()
	{
		return accountRequest;
	}

	public void setAccountRequest(AccountRequest accountRequest)
	{
		this.accountRequest = accountRequest;
	}

}
