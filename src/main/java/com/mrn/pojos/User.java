package com.mrn.pojos;

public class User {
	private long userId;
	private String userCategory;
	private String name;
	private String gender;
	private String email;
	private String phoneNo;
	private String password;
	private String status;
	private long createdTime;
	private long modifiedTime;
	private long modifiedBy;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Override
	public String toString() {
		return "User {" + "userId=" + userId + ", userCategory=" + userCategory + ", name=" + name + ", gender=" + gender + ", email=" + email + ", phoneNo=" + phoneNo 
				+ ", password=" + password + ", status=" + status + ", createdTime=" + createdTime+ ", modifiedTime=" + modifiedTime + ", modifiedBy=" + modifiedBy + "}";
	}

}
