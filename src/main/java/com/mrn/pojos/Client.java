package com.mrn.pojos;

public class Client {
	private Long clientId;
	private String dob;
	private String aadhar;
	private String pan;
	private String address;

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getdob() {
		return dob;
	}

	public void setdob(String dob) {
		this.dob = dob;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() 
	{
		return "Client {" + "clientId=" + clientId + ", dob=" + dob + ", aadhar=" + aadhar + ", pan=" + pan + ", address=" + address + "}";
	}
}
