package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum AccountType
{
	SAVINGS((short) 1),
	CURRENT((short) 2),
	FIXED_DEPOSIT((short) 3);

	private final short value;

	// Constructor to assign short value to enum constant
	AccountType(short value)
	{
		this.value = value;
	}

	// Returns the short value associated with the account type
	public short getValue()
	{
		return value;
	}

	// Method 1: Accepts primitive short
	public static AccountType fromValue(short value) throws InvalidException
	{
		for (AccountType type : values())
		{
			if (type.value == value)
			{
				return type;
			}
		}
		throw new InvalidException("Unknown Account Type value: " + value);
	}

	// Method 2: Accepts boxed Short, handles null safely
	public static AccountType fromValue(Short value) throws InvalidException
	{
		if (value == null)
		{
			throw new InvalidException("Account Type is required and cannot be left blank.");
		}
		return fromValue(value.shortValue());
	}
}
