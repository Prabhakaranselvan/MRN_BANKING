package com.mrn.pojos;

public class User
{
	private Long userId;
	private Short userCategory;
	private String name;
	private String gender;
	private String email;
	private String phoneNo;
	private String password;
	private Short status;
	private Long createdTime;
	private Long modifiedTime;
	private Long modifiedBy;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Short getUserCategory()
	{
		return userCategory;
	}

	public void setUserCategory(Short userCategory)
	{
		this.userCategory = userCategory;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhoneNo()
	{
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Short getStatus()
	{
		return status;
	}

	public void setStatus(Short status)
	{
		this.status = status;
	}

	public Long getCreatedTime()
	{
		return createdTime;
	}

	public void setCreatedTime(Long createdTime)
	{
		this.createdTime = createdTime;
	}

	public Long getModifiedTime()
	{
		return modifiedTime;
	}

	public void setModifiedTime(Long modifiedTime)
	{
		this.modifiedTime = modifiedTime;
	}

	public Long getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

}
