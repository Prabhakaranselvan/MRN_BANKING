package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum Status
{
	INACTIVE((short) 0),
	ACTIVE((short) 1),
	CLOSED((short) 2);

	private final short value;

	// Constructor to assign short value to enum constant
	Status(short value)
	{
		this.value = value;
	}

	// Returns the short value associated with the status
	public short getValue()
	{
		return value;
	}

	// Method 1: Accepts primitive short
	public static Status fromValue(short value) throws InvalidException
	{
		for (Status status : values())
		{
			if (status.value == value)
			{
				return status;
			}
		}
		throw new InvalidException("Unknown Status value: " + value);
	}

	// Method 2: Accepts boxed Short, handles null safely
	public static Status fromValue(Short value) throws InvalidException
	{
		if (value == null)
		{
			throw new InvalidException("Status is required and cannot be left blank.");
		}
		return fromValue(value.shortValue());
	}
}
