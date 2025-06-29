package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum TxnStatus
{
	FAILED((short) 0),
	SUCCESS((short) 1),
	PENDING((short) 2);

	private final short value;

	// Constructor to assign short value to enum constant
	TxnStatus(short value)
	{
		this.value = value;
	}

	// Returns the short value associated with the transaction status
	public short getValue()
	{
		return value;
	}

	// Method 1: Accepts primitive short
	public static TxnStatus fromValue(short value) throws InvalidException
	{
		for (TxnStatus status : values())
		{
			if (status.value == value)
			{
				return status;
			}
		}
		throw new InvalidException("Unknown TxnStatus value: " + value);
	}

	// Method 2: Accepts boxed Short, handles null safely
	public static TxnStatus fromValue(Short value) throws InvalidException
	{
		if (value == null)
		{
			throw new InvalidException("Transaction status is required and cannot be left blank.");
		}
		return fromValue(value.shortValue());
	}
}
