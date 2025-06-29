package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum RequestStatus
{
	PENDING((short) 0),
	APPROVED((short) 1),
	REJECTED((short) 2);

	private final short value;

	// Constructor to assign short value to enum constant
	RequestStatus(short value)
	{
		this.value = value;
	}

	// Returns the short value associated with the request status
	public short getValue()
	{
		return value;
	}

	// Method 1: Accepts primitive short
	public static RequestStatus fromValue(short value) throws InvalidException
	{
		for (RequestStatus status : values())
		{
			if (status.value == value)
			{
				return status;
			}
		}
		throw new InvalidException("Unknown RequestStatus value: " + value);
	}

	// Method 2: Accepts boxed Short, handles null safely
	public static RequestStatus fromValue(Short value) throws InvalidException
	{
		if (value == null)
		{
			throw new InvalidException("RequestStatus is required and cannot be left blank.");
		}
		return fromValue(value.shortValue());
	}
}
