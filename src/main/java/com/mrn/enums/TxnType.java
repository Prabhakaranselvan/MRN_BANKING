package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum TxnType
{
	DEPOSIT((short) 0),
	WITHDRAWAL((short) 1),
	CREDIT((short) 2),
	DEBIT((short) 3);

	private final short value;

	// Constructor to assign short value to enum constant
	TxnType(short value)
	{
		this.value = value;
	}

	// Returns the short value associated with the transaction type
	public short getValue()
	{
		return value;
	}

	// Method 1: Accepts primitive short
	public static TxnType fromValue(short value) throws InvalidException
	{
		for (TxnType type : values())
		{
			if (type.value == value)
			{
				return type;
			}
		}
		throw new InvalidException("Unknown TxnType value: " + value);
	}

	// Method 2: Accepts boxed Short, handles null safely
	public static TxnType fromValue(Short value) throws InvalidException
	{
		if (value == null)
		{
			throw new InvalidException("Transaction type is required and cannot be left blank.");
		}
		return fromValue(value.shortValue());
	}
}
