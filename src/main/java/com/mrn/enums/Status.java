package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum Status 
{
	INACTIVE(0),
	ACTIVE(1),
    CLOSED(2);

    private final int value;
    
//Constructor to assign integer value to enum constant.
    Status(int value) 
    {
        this.value = value;
    }

//Returns the integer value associated with the status.
    public int getValue() 
    {
        return value;
    }

//Converts an integer value to the corresponding UserStatus enum.
    public static Status fromValue(int value) throws InvalidException 
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
}

