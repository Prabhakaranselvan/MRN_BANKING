package com.mrn.pojos;

public class Client extends User
{
	private String dob;
	private String aadhar;
	private String pan;
	private String address;

	public String getDob()
	{
		return dob;
	}

	public void setDob(String dob)
	{
		this.dob = dob;
	}

	public String getAadhar()
	{
		return aadhar;
	}

	public void setAadhar(String aadhar)
	{
		this.aadhar = aadhar;
	}

	public String getPan()
	{
		return pan;
	}

	public void setPan(String pan)
	{
		this.pan = pan;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}
}
