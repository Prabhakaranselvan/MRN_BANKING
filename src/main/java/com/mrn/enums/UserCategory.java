package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum UserCategory
{
	CLIENT((short) 0), 
	EMPLOYEE((short) 1), 
	MANAGER((short) 2), 
	GENERAL_MANAGER((short) 3);

	private final short value;

	// Constructor to assign short value to enum constant
	UserCategory(short value)
	{
		this.value = value;
	}

	// Returns the short value associated with the user category
	public short getValue()
	{
		return value;
	}

	// Method 1: Accepts primitive short
	public static UserCategory fromValue(short value) throws InvalidException
	{
		for (UserCategory category : values())
		{
			if (category.value == value)
			{
				return category;
			}
		}
		throw new InvalidException("Unknown UserCategory value: " + value);
	}

	// Method 2: Accepts boxed Short, handles null safely
	public static UserCategory fromValue(Short value) throws InvalidException
	{
		if (value == null)
		{
			throw new InvalidException("UserCategory is required and cannot be left blank.");
		}
		return fromValue(value.shortValue());
	}
}
